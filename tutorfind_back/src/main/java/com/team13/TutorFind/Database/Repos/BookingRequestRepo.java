package com.team13.TutorFind.Database.Repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.entity.BookingRequest;

@Repository
public interface BookingRequestRepo extends JpaRepository<BookingRequest, Long> {
    
    // Find bookings sent by a learner
    List<BookingRequest> findByLearnerIdOrderByCreatedAtDesc(Long learnerId);
    
    // Find bookings received by a tutor
    List<BookingRequest> findByTutorIdOrderByCreatedAtDesc(Long tutorId);
    
    // Find by status
    List<BookingRequest> findByLearnerIdAndStatusOrderByCreatedAtDesc(Long learnerId, String status);
    List<BookingRequest> findByTutorIdAndStatusOrderByCreatedAtDesc(Long tutorId, String status);
    List<BookingRequest> findByStatus(String status);
}