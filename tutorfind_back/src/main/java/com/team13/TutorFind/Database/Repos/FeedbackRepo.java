package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Feedback entity
 * Provides methods to query feedback by tutor, learner, etc.
 */
@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    
    /**
     * Find all feedback given by a specific tutor, ordered by newest first
     */
    List<Feedback> findByTutorIdOrderBySessionDateDesc(Long tutorId);
    
    /**
     * Find all feedback received by a specific learner, ordered by newest first
     */
    List<Feedback> findByLearnerIdOrderBySessionDateDesc(Long learnerId);
    
    /**
     * Find feedback for a specific booking
     */
    List<Feedback> findByBookingId(Long bookingId);
}