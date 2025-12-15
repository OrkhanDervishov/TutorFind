package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.BookingRequestRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.dto.BookingResponse;
import com.team13.TutorFind.dto.CreateBookingRequest;
import com.team13.TutorFind.entity.BookingRequest;
import com.team13.TutorFind.entity.Subject;
import com.team13.TutorFind.Database.Services.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    
    private final BookingRequestRepo bookingRequestRepo;
    private final UserRepo userRepo;
    private final SubjectRepo subjectRepo;
    private final TutorDetailsRepo tutorDetailsRepo;
    private final NotificationService notificationService;
    
    public BookingService(BookingRequestRepo bookingRequestRepo,
                         UserRepo userRepo,
                         SubjectRepo subjectRepo,
                         TutorDetailsRepo tutorDetailsRepo,
                         NotificationService notificationService) {
        this.bookingRequestRepo = bookingRequestRepo;
        this.userRepo = userRepo;
        this.subjectRepo = subjectRepo;
        this.tutorDetailsRepo = tutorDetailsRepo;
        this.notificationService = notificationService;
    }
    
    // Create booking request
    public BookingResponse createBooking(Long learnerId, CreateBookingRequest request) {
        BookingRequest booking = new BookingRequest();
        booking.setLearnerId(learnerId);
        booking.setTutorId(request.getTutorId());
        booking.setSubjectId(request.getSubjectId());
        booking.setMode(request.getMode());
        booking.setSlotText(request.getSlot());
        booking.setLearnerNote(request.getNote());
        booking.setProposedPrice(request.getProposedPrice());
        booking.setStatus("PENDING");
        
        // Parse slot into day and time (e.g., "Monday 10:00-11:00")
        if (request.getSlot() != null && request.getSlot().contains(" ")) {
            String[] parts = request.getSlot().split(" ", 2);
            booking.setSlotDay(parts[0]);
            if (parts.length > 1) {
                booking.setSlotTime(parts[1]);
            }
        }
        
        BookingRequest saved = bookingRequestRepo.save(booking);

        // Notify tutor
        tutorDetailsRepo.findById(request.getTutorId()).ifPresent(tutor -> {
            Long tutorUserId = tutor.getUserId();
            if (tutorUserId != null) {
                notificationService.create(
                        tutorUserId,
                        "booking_created",
                        "{\"bookingId\":" + saved.getId() + "}"
                );
            }
        });

        return convertToDTO(saved);
    }
    
    // Get bookings sent by learner
    public List<BookingResponse> getSentBookings(Long learnerId, String status) {
        List<BookingRequest> bookings;
        
        if (status != null && !status.isEmpty()) {
            bookings = bookingRequestRepo.findByLearnerIdAndStatusOrderByCreatedAtDesc(
                learnerId, status.toUpperCase()
            );
        } else {
            bookings = bookingRequestRepo.findByLearnerIdOrderByCreatedAtDesc(learnerId);
        }
        
        return convertToDTOList(bookings);
    }
    
    // Get bookings received by tutor
    public List<BookingResponse> getReceivedBookings(Long tutorId, String status) {
        List<BookingRequest> bookings;
        
        if (status != null && !status.isEmpty()) {
            bookings = bookingRequestRepo.findByTutorIdAndStatusOrderByCreatedAtDesc(
                tutorId, status.toUpperCase()
            );
        } else {
            bookings = bookingRequestRepo.findByTutorIdOrderByCreatedAtDesc(tutorId);
        }
        
        return convertToDTOList(bookings);
    }
    
    // Accept booking
    public BookingResponse acceptBooking(Long bookingId, Long tutorId, String response) {
        BookingRequest booking = bookingRequestRepo.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Verify tutor owns this booking
        if (!booking.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        booking.setStatus("ACCEPTED");
        booking.setTutorResponse(response);
        booking.setRespondedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        BookingRequest saved = bookingRequestRepo.save(booking);

        // Notify learner
        notificationService.create(
                booking.getLearnerId(),
                "booking_accepted",
                "{\"bookingId\":" + booking.getId() + "}"
        );

        return convertToDTO(saved);
    }
    
    // Decline booking
    public BookingResponse declineBooking(Long bookingId, Long tutorId, String response) {
        BookingRequest booking = bookingRequestRepo.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        // Verify tutor owns this booking
        if (!booking.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        booking.setStatus("DECLINED");
        booking.setTutorResponse(response);
        booking.setRespondedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        BookingRequest saved = bookingRequestRepo.save(booking);

        // Notify learner
        notificationService.create(
                booking.getLearnerId(),
                "booking_declined",
                "{\"bookingId\":" + booking.getId() + "}"
        );

        return convertToDTO(saved);
    }
    
    // Get single booking
    public BookingResponse getBooking(Long bookingId) {
        BookingRequest booking = bookingRequestRepo.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        return convertToDTO(booking);
    }
    
    // Convert to DTO
    private BookingResponse convertToDTO(BookingRequest booking) {
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setLearnerId(booking.getLearnerId());
        dto.setTutorId(booking.getTutorId());
        dto.setMode(booking.getMode());
        dto.setSlot(booking.getSlotText());
        dto.setLearnerNote(booking.getLearnerNote());
        dto.setTutorResponse(booking.getTutorResponse());
        dto.setProposedPrice(booking.getProposedPrice());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setRespondedAt(booking.getRespondedAt());
        dto.setSubjectId(booking.getSubjectId());
        
        // Get learner info
        User learner = userRepo.findById(booking.getLearnerId()).orElse(null);
        if (learner != null) {
            dto.setLearnerName(learner.getFirstName() + " " + learner.getLastName());
            dto.setLearnerPhone(learner.getPhoneNumber());
        }
        
        // Get tutor info
        tutorDetailsRepo.findById(booking.getTutorId()).ifPresent(tutorProfile -> {
            Long tutorUserId = tutorProfile.getUserId();
            if (tutorUserId != null) {
                userRepo.findById(tutorUserId).ifPresent(tutorUser -> {
                    dto.setTutorName(tutorUser.getFirstName() + " " + tutorUser.getLastName());
                });
            }
        });
        
        // Get subject info
        if (booking.getSubjectId() != null) {
            Subject subject = subjectRepo.findById(booking.getSubjectId()).orElse(null);
            if (subject != null) {
                dto.setSubject(subject.getName());
            }
        }
        
        return dto;
    }
    
    // Convert list to DTOs
    private List<BookingResponse> convertToDTOList(List<BookingRequest> bookings) {
        List<BookingResponse> dtos = new ArrayList<>();
        for (BookingRequest booking : bookings) {
            dtos.add(convertToDTO(booking));
        }
        return dtos;
    }
}
