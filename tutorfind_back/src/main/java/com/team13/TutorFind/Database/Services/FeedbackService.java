package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.FeedbackRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.entity.Feedback;
import com.team13.TutorFind.entity.Subject;
import com.team13.TutorFind.dto.CreateFeedbackRequest;
import com.team13.TutorFind.dto.FeedbackResponse;
import com.team13.TutorFind.Database.Services.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing private tutor feedback
 * Handles feedback creation, retrieval with privacy controls
 */
@Service
public class FeedbackService {
    
    private final FeedbackRepo feedbackRepo;
    private final TutorDetailsRepo tutorDetailsRepo;
    private final UserRepo userRepo;
    private final SubjectRepo subjectRepo;
    private final NotificationService notificationService;
    
    public FeedbackService(FeedbackRepo feedbackRepo,
                          TutorDetailsRepo tutorDetailsRepo,
                          UserRepo userRepo,
                          SubjectRepo subjectRepo,
                          NotificationService notificationService) {
        this.feedbackRepo = feedbackRepo;
        this.tutorDetailsRepo = tutorDetailsRepo;
        this.userRepo = userRepo;
        this.subjectRepo = subjectRepo;
        this.notificationService = notificationService;
    }
    
    /**
     * Create new feedback (tutor only)
     * 
     * @param tutorId The ID of the tutor giving feedback
     * @param request Feedback data
     * @return Success response with feedback ID
     */
    @Transactional
    public Map<String, Object> createFeedback(Long tutorId, CreateFeedbackRequest request) {
        // Validate feedback text
        if (request.getFeedbackText() == null || request.getFeedbackText().trim().isEmpty()) {
            throw new RuntimeException("Feedback text is required");
        }
        
        // Verify tutor exists
        tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
        
        // Verify learner exists
        userRepo.findById(request.getLearnerId())
            .orElseThrow(() -> new RuntimeException("Learner not found"));
        
        // Create feedback
        Feedback feedback = new Feedback();
        feedback.setTutorId(tutorId);
        feedback.setLearnerId(request.getLearnerId());
        feedback.setBookingId(request.getBookingId());
        feedback.setSubjectId(request.getSubjectId());
        feedback.setFeedbackText(request.getFeedbackText());
        feedback.setStrengths(request.getStrengths());
        feedback.setAreasForImprovement(request.getAreasForImprovement());
        feedback.setCreatedAt(LocalDateTime.now());
        feedback.setUpdatedAt(LocalDateTime.now());
        
        // Parse session date
        if (request.getSessionDate() != null && !request.getSessionDate().isEmpty()) {
            feedback.setSessionDate(LocalDate.parse(request.getSessionDate()));
        }
        
        Feedback saved = feedbackRepo.save(feedback);
        notificationService.create(
                feedback.getLearnerId(),
                "feedback_added",
                "{\"feedbackId\":" + saved.getId() + "}"
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Feedback submitted successfully");
        response.put("feedbackId", saved.getId());
        return response;
    }
    
    /**
     * Get feedback received by a learner
     * 
     * @param learnerId The learner's ID
     * @return List of feedback with tutor names
     */
    public List<FeedbackResponse> getFeedbackReceived(Long learnerId) {
        List<Feedback> feedbackList = feedbackRepo.findByLearnerIdOrderBySessionDateDesc(learnerId);
        return convertToResponseList(feedbackList);
    }
    
    /**
     * Get feedback given by a tutor
     * 
     * @param tutorId The tutor's ID
     * @return List of feedback with learner names
     */
    public List<FeedbackResponse> getFeedbackGiven(Long tutorId) {
        List<Feedback> feedbackList = feedbackRepo.findByTutorIdOrderBySessionDateDesc(tutorId);
        return convertToResponseList(feedbackList);
    }
    
    /**
     * Get single feedback by ID with privacy check
     * 
     * @param feedbackId The feedback ID
     * @param userId The requesting user's ID
     * @return Feedback response if authorized
     */
    public FeedbackResponse getFeedbackById(Long feedbackId, Long userId) {
        Feedback feedback = feedbackRepo.findById(feedbackId)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        // Check if user is authorized (must be the tutor or learner)
        TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId).orElse(null);
        Long tutorId = tutorProfile != null ? tutorProfile.getId() : null;
        
        boolean isAuthorized = feedback.getLearnerId().equals(userId) || 
                              (tutorId != null && feedback.getTutorId().equals(tutorId));
        
        if (!isAuthorized) {
            throw new RuntimeException("Unauthorized: You can only view your own feedback");
        }
        
        return convertToResponse(feedback);
    }
    
    /**
     * Convert feedback list to response DTOs
     */
    private List<FeedbackResponse> convertToResponseList(List<Feedback> feedbackList) {
        List<FeedbackResponse> responses = new ArrayList<>();
        
        for (Feedback feedback : feedbackList) {
            responses.add(convertToResponse(feedback));
        }
        
        return responses;
    }
    
    /**
     * Convert single feedback to response DTO
     */
    private FeedbackResponse convertToResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        
        response.setId(feedback.getId());
        response.setTutorId(feedback.getTutorId());
        response.setLearnerId(feedback.getLearnerId());
        response.setBookingId(feedback.getBookingId());
        response.setSubjectId(feedback.getSubjectId());
        response.setSessionDate(feedback.getSessionDate());
        response.setFeedbackText(feedback.getFeedbackText());
        response.setStrengths(feedback.getStrengths());
        response.setAreasForImprovement(feedback.getAreasForImprovement());
        response.setCreatedAt(feedback.getCreatedAt());
        
        // Get tutor name
        TutorDetails tutor = tutorDetailsRepo.findById(feedback.getTutorId()).orElse(null);
        if (tutor != null) {
            User tutorUser = userRepo.findById(tutor.getUserId()).orElse(null);
            if (tutorUser != null) {
                response.setTutorName(tutorUser.getFirstName() + " " + tutorUser.getLastName());
            }
        }
        
        // Get learner name
        User learner = userRepo.findById(feedback.getLearnerId()).orElse(null);
        if (learner != null) {
            response.setLearnerName(learner.getFirstName() + " " + learner.getLastName());
        }
        
        // Get subject name
        if (feedback.getSubjectId() != null) {
            Subject subject = subjectRepo.findById(feedback.getSubjectId()).orElse(null);
            if (subject != null) {
                response.setSubjectName(subject.getName());
            }
        }
        
        return response;
    }
}
