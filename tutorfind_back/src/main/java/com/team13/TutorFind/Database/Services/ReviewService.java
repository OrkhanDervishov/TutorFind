package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.ReviewRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.entity.Review;
import com.team13.TutorFind.dto.CreateReviewRequest;
import com.team13.TutorFind.dto.ReviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing reviews
 * Handles review creation, retrieval, and tutor rating updates
 */
@Service
public class ReviewService {
    
    private final ReviewRepo reviewRepo;
    private final TutorDetailsRepo tutorDetailsRepo;
    private final UserRepo userRepo;
    
    public ReviewService(ReviewRepo reviewRepo, 
                        TutorDetailsRepo tutorDetailsRepo,
                        UserRepo userRepo) {
        this.reviewRepo = reviewRepo;
        this.tutorDetailsRepo = tutorDetailsRepo;
        this.userRepo = userRepo;
    }
    
    /**
     * Create a new review
     * 
     * @param learnerId The ID of the user writing the review
     * @param request Review data (tutorId, rating, comment)
     * @return Success response with review ID
     */
    @Transactional
    public Map<String, Object> createReview(Long learnerId, CreateReviewRequest request) {
        // Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        // Verify tutor exists
        TutorDetails tutor = tutorDetailsRepo.findById(request.getTutorId())
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        // Check if learner already reviewed this tutor
        if (reviewRepo.findByTutorIdAndLearnerId(request.getTutorId(), learnerId).isPresent()) {
            throw new RuntimeException("You have already reviewed this tutor");
        }
        
        // Create review
        Review review = new Review();
        review.setTutorId(request.getTutorId());
        review.setLearnerId(learnerId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setBookingId(request.getBookingId());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        
        Review saved = reviewRepo.save(review);
        
        // Update tutor's rating
        updateTutorRating(request.getTutorId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Review submitted successfully");
        response.put("reviewId", saved.getId());
        return response;
    }
    
    /**
     * Get all reviews for a tutor
     * 
     * @param tutorId The tutor's ID
     * @return List of reviews with learner names
     */
    public List<ReviewResponse> getReviewsForTutor(Long tutorId) {
        List<Review> reviews = reviewRepo.findByTutorIdOrderByCreatedAtDesc(tutorId);
        List<ReviewResponse> responses = new ArrayList<>();
        
        for (Review review : reviews) {
            ReviewResponse response = new ReviewResponse();
            response.setId(review.getId());
            response.setTutorId(review.getTutorId());
            response.setLearnerId(review.getLearnerId());
            response.setRating(review.getRating());
            response.setComment(review.getComment());
            response.setCreatedAt(review.getCreatedAt());
            
            // Get learner name
            User learner = userRepo.findById(review.getLearnerId()).orElse(null);
            if (learner != null) {
                response.setLearnerName(learner.getFirstName() + " " + learner.getLastName());
            } else {
                response.setLearnerName("Anonymous");
            }
            
            responses.add(response);
        }
        
        return responses;
    }
    
    /**
     * Get all reviews written by a learner
     * 
     * @param learnerId The learner's ID
     * @return List of reviews with tutor names
     */
    public List<ReviewResponse> getMyReviews(Long learnerId) {
        List<Review> reviews = reviewRepo.findByLearnerId(learnerId);
        List<ReviewResponse> responses = new ArrayList<>();
        
        for (Review review : reviews) {
            ReviewResponse response = new ReviewResponse();
            response.setId(review.getId());
            response.setTutorId(review.getTutorId());
            response.setLearnerId(review.getLearnerId());
            response.setRating(review.getRating());
            response.setComment(review.getComment());
            response.setCreatedAt(review.getCreatedAt());
            
            // Get learner name (current user)
            User learner = userRepo.findById(review.getLearnerId()).orElse(null);
            if (learner != null) {
                response.setLearnerName(learner.getFirstName() + " " + learner.getLastName());
            }
            
            responses.add(response);
        }
        
        return responses;
    }
    
    /**
     * Update tutor's average rating and review count
     * Called automatically after a new review is created
     * 
     * @param tutorId The tutor's ID
     */
    @Transactional
    public void updateTutorRating(Long tutorId) {
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        // Calculate new average rating
        Double avgRating = reviewRepo.calculateAverageRating(tutorId);
        Long reviewCount = reviewRepo.countByTutorId(tutorId);
        
        // Update tutor profile
        tutor.setRatingAvg(avgRating != null ? avgRating.floatValue() : 0.0f);
        tutor.setRatingCount(reviewCount != null ? reviewCount.intValue() : 0);
        
        tutorDetailsRepo.save(tutor);
    }
}