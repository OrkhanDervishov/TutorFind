package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.FeedbackService;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.dto.CreateFeedbackRequest;
import com.team13.TutorFind.dto.FeedbackResponse;
import com.team13.TutorFind.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Feedback operations
 * 
 * Endpoints:
 * - POST /api/feedback - Create feedback (tutors only)
 * - GET /api/feedback/received - Get feedback I received (learners)
 * - GET /api/feedback/given - Get feedback I gave (tutors)
 * - GET /api/feedback/{id} - Get single feedback (with privacy check)
 */
@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    private final JwtUtil jwtUtil;
    private final TutorDetailsRepo tutorDetailsRepo;
    
    public FeedbackController(FeedbackService feedbackService,
                             JwtUtil jwtUtil,
                             TutorDetailsRepo tutorDetailsRepo) {
        this.feedbackService = feedbackService;
        this.jwtUtil = jwtUtil;
        this.tutorDetailsRepo = tutorDetailsRepo;
    }
    
    /**
     * Create new feedback (tutors only)
     * 
     * POST /api/feedback
     * Headers: Authorization: Bearer <token>
     * Body: { learnerId, sessionDate, feedbackText, strengths, areasForImprovement }
     * 
     * @return Success response with feedback ID
     */
    @PostMapping
    public ResponseEntity<?> createFeedback(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateFeedbackRequest request) {
        try {
            // Extract user ID from JWT token
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);
            
            // Get tutor profile ID
            TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Only tutors can create feedback"));
            
            Long tutorId = tutorProfile.getId();
            
            // Create feedback
            Map<String, Object> result = feedbackService.createFeedback(tutorId, request);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get feedback I received (learners)
     * 
     * GET /api/feedback/received
     * Headers: Authorization: Bearer <token>
     * 
     * @return List of feedback received
     */
    @GetMapping("/received")
    public ResponseEntity<?> getFeedbackReceived(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract learner ID from JWT token
            String token = authHeader.replace("Bearer ", "");
            Long learnerId = jwtUtil.extractUserId(token);
            
            List<FeedbackResponse> feedback = feedbackService.getFeedbackReceived(learnerId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get feedback I gave (tutors)
     * 
     * GET /api/feedback/given
     * Headers: Authorization: Bearer <token>
     * 
     * @return List of feedback given
     */
    @GetMapping("/given")
    public ResponseEntity<?> getFeedbackGiven(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract user ID from JWT token
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);
            
            // Get tutor profile ID
            TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Only tutors can view given feedback"));
            
            Long tutorId = tutorProfile.getId();
            
            List<FeedbackResponse> feedback = feedbackService.getFeedbackGiven(tutorId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get single feedback by ID (with privacy check)
     * 
     * GET /api/feedback/{id}
     * Headers: Authorization: Bearer <token>
     * 
     * @param id Feedback ID
     * @return Feedback details if authorized
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            // Extract user ID from JWT token
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.extractUserId(token);
            
            FeedbackResponse feedback = feedbackService.getFeedbackById(id, userId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}