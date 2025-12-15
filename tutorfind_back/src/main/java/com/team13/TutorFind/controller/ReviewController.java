package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.ReviewService;
import com.team13.TutorFind.dto.CreateReviewRequest;
import com.team13.TutorFind.dto.ReviewResponse;
import com.team13.TutorFind.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Review operations
 * 
 * Endpoints:
 * - POST /api/reviews - Create review (requires authentication)
 * - GET /api/tutors/{tutorId}/reviews - Get tutor's reviews (public)
 * - GET /api/reviews/my-reviews - Get my reviews (requires authentication)
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;
    
    public ReviewController(ReviewService reviewService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Create a new review
     * 
     * POST /api/reviews
     * Headers: Authorization: Bearer <token>
     * Body: { tutorId, rating, comment, bookingId (optional) }
     * 
     * @return Success response with review ID
     */
    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateReviewRequest request) {
        try {
            // Extract learner ID from JWT token
            String token = authHeader.replace("Bearer ", "");
            Long learnerId = jwtUtil.extractUserId(token);
            
            // Create review
            Map<String, Object> result = reviewService.createReview(learnerId, request);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get all reviews for a specific tutor
     * 
     * GET /api/tutors/{tutorId}/reviews
     * Public endpoint - no authentication required
     * 
     * @param tutorId The tutor's ID
     * @return List of reviews with learner names
     */
    @GetMapping("/tutors/{tutorId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getTutorReviews(@PathVariable Long tutorId) {
        List<ReviewResponse> reviews = reviewService.getReviewsForTutor(tutorId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Get all reviews written by the authenticated user
     * 
     * GET /api/reviews/my-reviews
     * Headers: Authorization: Bearer <token>
     * 
     * @return List of reviews I wrote
     */
    @GetMapping("/reviews/my-reviews")
    public ResponseEntity<?> getMyReviews(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract learner ID from JWT token
            String token = authHeader.replace("Bearer ", "");
            Long learnerId = jwtUtil.extractUserId(token);
            
            List<ReviewResponse> reviews = reviewService.getMyReviews(learnerId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}