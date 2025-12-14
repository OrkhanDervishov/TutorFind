package com.team13.TutorFind.Database.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team13.TutorFind.Database.Repos.ClassRepo;
import com.team13.TutorFind.Database.Repos.EnrollmentRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.dto.EnrollmentResponse;
import com.team13.TutorFind.entity.Class;
import com.team13.TutorFind.entity.Enrollment;

@Service
public class EnrollmentService {
    
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    
    @Autowired
    private ClassRepo classRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private SubjectRepo subjectRepo;
    
    @Autowired
    private TutorDetailsRepo tutorDetailsRepo;
    
    // Enroll in class
    @Transactional
    public EnrollmentResponse enrollInClass(Long classId, Long learnerId) {
        // Check if class exists
        Class classEntity = classRepo.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        // Check if learner exists
        User learner = userRepo.findById(learnerId)
            .orElseThrow(() -> new RuntimeException("Learner not found"));
        
        // Verify learner role
        if (learner.getRole() != UserRoles.LEARNER) {
            throw new RuntimeException("Only learners can enroll in classes");
        }
        
        // Check if class is open
        if (!"OPEN".equals(classEntity.getStatus())) {
            throw new RuntimeException("Class is not open for enrollment");
        }
        
        // Check if already enrolled
        if (enrollmentRepo.existsByClassIdAndLearnerId(classId, learnerId)) {
            throw new RuntimeException("You are already enrolled in this class");
        }
        
        // Check if class is full
        if (classEntity.getCurrentStudents() >= classEntity.getMaxStudents()) {
            throw new RuntimeException("Class is full");
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setClassId(classId);
        enrollment.setLearnerId(learnerId);
        enrollment.setStatus("ACTIVE");
        
        Enrollment saved = enrollmentRepo.save(enrollment);
        
        // Update class current students count
        classEntity.setCurrentStudents(classEntity.getCurrentStudents() + 1);
        
        // Update class status to FULL if needed
        if (classEntity.getCurrentStudents() >= classEntity.getMaxStudents()) {
            classEntity.setStatus("FULL");
        }
        
        classRepo.save(classEntity);
        
        return buildEnrollmentResponse(saved);
    }
    
    // Drop from class
    @Transactional
    public void dropFromClass(Long enrollmentId, Long learnerId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        // Verify ownership
        if (!enrollment.getLearnerId().equals(learnerId)) {
            throw new RuntimeException("Unauthorized: You can only drop from your own enrollments");
        }
        
        // Check if already dropped
        if ("DROPPED".equals(enrollment.getStatus())) {
            throw new RuntimeException("You have already dropped from this class");
        }
        
        // Update enrollment status
        enrollment.setStatus("DROPPED");
        enrollmentRepo.save(enrollment);
        
        // Update class current students count
        Class classEntity = classRepo.findById(enrollment.getClassId())
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        classEntity.setCurrentStudents(Math.max(0, classEntity.getCurrentStudents() - 1));
        
        // Reopen class if it was full
        if ("FULL".equals(classEntity.getStatus())) {
            classEntity.setStatus("OPEN");
        }
        
        classRepo.save(classEntity);
    }
    
    // Get class roster (tutor only)
    public List<EnrollmentResponse> getClassRoster(Long classId, Long tutorId) {
        // Verify class exists and belongs to tutor
        Class classEntity = classRepo.findById(classId)
            .orElseThrow(() -> new RuntimeException("Class not found"));
        
        if (!classEntity.getTutorId().equals(tutorId)) {
            throw new RuntimeException("Unauthorized: You can only view roster for your own classes");
        }
        
        return enrollmentRepo.findByClassId(classId).stream()
            .map(this::buildEnrollmentResponse)
            .collect(Collectors.toList());
    }
    
    // Get learner's enrollments
    public List<EnrollmentResponse> getLearnerEnrollments(Long learnerId) {
        return enrollmentRepo.findByLearnerId(learnerId).stream()
            .map(this::buildEnrollmentResponse)
            .collect(Collectors.toList());
    }
    
    // Get learner's active enrollments
    public List<EnrollmentResponse> getLearnerActiveEnrollments(Long learnerId) {
        return enrollmentRepo.findByLearnerIdAndStatus(learnerId, "ACTIVE").stream()
            .map(this::buildEnrollmentResponse)
            .collect(Collectors.toList());
    }
    
    // Helper method to build response
    private EnrollmentResponse buildEnrollmentResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setClassId(enrollment.getClassId());
        response.setLearnerId(enrollment.getLearnerId());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        response.setStatus(enrollment.getStatus());
        response.setSessionsAttended(enrollment.getSessionsAttended());
        response.setPaymentStatus(enrollment.getPaymentStatus());
        response.setAmountPaid(enrollment.getAmountPaid());
        
        // Get learner name
        userRepo.findById(enrollment.getLearnerId()).ifPresent(user -> {
            response.setLearnerName(user.getFirstName() + " " + user.getLastName());
        });
        
        // Get class details
        classRepo.findById(enrollment.getClassId()).ifPresent(classEntity -> {
            response.setClassName(classEntity.getName());
            response.setScheduleDay(classEntity.getScheduleDay());
            response.setScheduleTime(classEntity.getScheduleTime());
            response.setDurationMinutes(classEntity.getDurationMinutes());
            
            // Get tutor name
            tutorDetailsRepo.findById(classEntity.getTutorId()).ifPresent(tutor -> {
                userRepo.findById(tutor.getUserId()).ifPresent(user -> {
                    response.setTutorName(user.getFirstName() + " " + user.getLastName());
                });
            });
            
            // Get subject name
            if (classEntity.getSubjectId() != null) {
                subjectRepo.findById(classEntity.getSubjectId()).ifPresent(subject -> {
                    response.setSubjectName(subject.getName());
                });
            }
        });
        
        return response;
    }
}