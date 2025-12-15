package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.BookingService;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.dto.BookingResponse;
import com.team13.TutorFind.dto.BookingStatusUpdate;
import com.team13.TutorFind.dto.CreateBookingRequest;
import com.team13.TutorFind.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/bookings")
public class BookingController {
    
    private final BookingService bookingService;
    private final JwtUtil jwtUtil;
    private final TutorDetailsRepo tutorDetailsRepo;
    
    public BookingController(BookingService bookingService, JwtUtil jwtUtil, TutorDetailsRepo tutorDetailsRepo) {
        this.bookingService = bookingService;
        this.jwtUtil = jwtUtil;
        this.tutorDetailsRepo = tutorDetailsRepo;
    }
    
    // Create booking request (learner)
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateBookingRequest request
    ) {
        try {
            Long learnerId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            
            BookingResponse booking = bookingService.createBooking(learnerId, request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create booking: " + e.getMessage());
        }
    }
    
    // Get bookings sent by learner
    @GetMapping("/sent")
    public ResponseEntity<?> getSentBookings(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status
    ) {
        try {
            Long learnerId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            
            List<BookingResponse> bookings = bookingService.getSentBookings(learnerId, status);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get bookings: " + e.getMessage());
        }
    }
    
    // Get bookings received by tutor
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedBookings(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status
    ) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
            Long tutorId = tutorProfile.getId();
            
            List<BookingResponse> bookings = bookingService.getReceivedBookings(tutorId, status);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get bookings: " + e.getMessage());
        }
    }
    
    // Accept booking (tutor)
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) BookingStatusUpdate update
    ) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
            Long tutorId = tutorProfile.getId();
            
            String response = update != null ? update.getResponse() : null;
            BookingResponse booking = bookingService.acceptBooking(id, tutorId, response);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to accept booking: " + e.getMessage());
        }
    }
    
    // Decline booking (tutor)
    @PutMapping("/{id}/decline")
    public ResponseEntity<?> declineBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) BookingStatusUpdate update
    ) {
        try {
            Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
            Long tutorId = tutorProfile.getId();
            
            String response = update != null ? update.getResponse() : null;
            BookingResponse booking = bookingService.declineBooking(id, tutorId, response);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to decline booking: " + e.getMessage());
        }
    }
    
    // Get single booking
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        try {
            BookingResponse booking = bookingService.getBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Booking not found");
        }
    }
}
