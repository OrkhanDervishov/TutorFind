package com.team13.TutorFind.Database.Repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.entity.Enrollment;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
    
    // Find all enrollments for a class
    List<Enrollment> findByClassId(Long classId);
    
    // Find all enrollments for a learner
    List<Enrollment> findByLearnerId(Long learnerId);
    
    // Find specific enrollment
    Optional<Enrollment> findByClassIdAndLearnerId(Long classId, Long learnerId);
    
    // Count enrollments for a class
    Long countByClassIdAndStatus(Long classId, String status);
    
    // Find learner's enrollments by status
    List<Enrollment> findByLearnerIdAndStatus(Long learnerId, String status);
    
    // Check if already enrolled
    boolean existsByClassIdAndLearnerId(Long classId, Long learnerId);

    Long countByStatus(String status);
}