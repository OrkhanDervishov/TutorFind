package com.team13.TutorFind.Database.Repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.entity.Review;

/**
 * Repository for Review entity
 * Provides methods to query reviews by tutor, learner, etc.
 */
@Repository
public interface ReviewRepo extends JpaRepository<Review, Long> {
    
    /**
     * Find all reviews for a specific tutor, ordered by newest first
     */
    List<Review> findByTutorIdOrderByCreatedAtDesc(Long tutorId);
    
    /**
     * Find all reviews written by a specific learner
     */
    List<Review> findByLearnerId(Long learnerId);
    
    /**
     * Check if a learner has already reviewed a tutor
     */
    Optional<Review> findByTutorIdAndLearnerId(Long tutorId, Long learnerId);
    
    /**
     * Calculate average rating for a tutor
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.tutorId = :tutorId")
    Double calculateAverageRating(@Param("tutorId") Long tutorId);
    
    /**
     * Count total reviews for a tutor
     */
    Long countByTutorId(Long tutorId);
    List<Review> findByStatus(String status);
    // Find reviews by tutor and status
    List<Review> findByTutorIdAndStatus(Long tutorId, String status);
}