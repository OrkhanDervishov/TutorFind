package com.team13.TutorFind.Database.Repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.entity.Class;

@Repository
public interface ClassRepo extends JpaRepository<Class, Long> {
    
    // Find all classes by tutor
    List<Class> findByTutorId(Long tutorId);
    
    // Find all classes by subject
    List<Class> findBySubjectId(Long subjectId);
    
    // Find all classes by status
    List<Class> findByStatus(String status);
    
    // Find open classes
    List<Class> findByStatusOrderByCreatedAtDesc(String status);
    
    // Find tutor's classes by status
    List<Class> findByTutorIdAndStatus(Long tutorId, String status);
}