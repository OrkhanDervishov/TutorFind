package com.team13.TutorFind.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Services.ClassService;
import com.team13.TutorFind.Database.Services.EnrollmentService;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.dto.ClassResponse;
import com.team13.TutorFind.dto.CreateClassRequest;
import com.team13.TutorFind.dto.EnrollmentResponse;
import com.team13.TutorFind.security.JwtUtil;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private TutorDetailsRepo tutorDetailsRepo;
    
    // Helper method to get tutor ID from JWT token
    private Long getTutorIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No authorization token provided");
        }
        
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        
        TutorDetails tutor = tutorDetailsRepo.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
        
        return tutor.getId();
    }
    
    // Helper method to get learner ID from JWT token
    private Long getLearnerIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No authorization token provided");
        }
        
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
    
    // ============================================
    // CLASS MANAGEMENT ENDPOINTS
    // ============================================
    
    // Create class (tutor only)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createClass(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateClassRequest request) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            ClassResponse classResponse = classService.createClass(tutorId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("classId", classResponse.getId());
            response.put("message", "Class created successfully");
            response.put("class", classResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get all classes (public)
    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAllClasses(
            @RequestParam(required = false) String status) {
        try {
            List<ClassResponse> classes;
            if ("OPEN".equals(status)) {
                classes = classService.getOpenClasses();
            } else {
                classes = classService.getAllClasses();
            }
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get class by ID (public)
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(@PathVariable Long id) {
        try {
            ClassResponse classResponse = classService.getClassById(id);
            return ResponseEntity.ok(classResponse);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get tutor's classes
    @GetMapping("/my-classes")
    public ResponseEntity<List<ClassResponse>> getMyClasses(
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            List<ClassResponse> classes = classService.getTutorClasses(tutorId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Update class (tutor only)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClass(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateClassRequest request) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            ClassResponse updated = classService.updateClass(id, tutorId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Class updated successfully");
            response.put("class", updated);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Delete class (tutor only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteClass(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            classService.deleteClass(id, tutorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Class deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // ============================================
    // ENROLLMENT ENDPOINTS
    // ============================================
    
    // Enroll in class (learner only)
    @PostMapping("/{id}/enroll")
    public ResponseEntity<Map<String, Object>> enrollInClass(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long learnerId = getLearnerIdFromToken(authHeader);
            EnrollmentResponse enrollment = enrollmentService.enrollInClass(id, learnerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("enrollmentId", enrollment.getId());
            response.put("message", "Successfully enrolled in class");
            response.put("enrollment", enrollment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Drop from class (learner only)
    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<Map<String, Object>> dropFromClass(
            @PathVariable Long enrollmentId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long learnerId = getLearnerIdFromToken(authHeader);
            enrollmentService.dropFromClass(enrollmentId, learnerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Successfully dropped from class");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get class roster (tutor only)
    @GetMapping("/{id}/roster")
    public ResponseEntity<List<EnrollmentResponse>> getClassRoster(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            List<EnrollmentResponse> roster = enrollmentService.getClassRoster(id, tutorId);
            return ResponseEntity.ok(roster);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get my enrollments (learner only)
    @GetMapping("/enrollments/my")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status) {
        try {
            Long learnerId = getLearnerIdFromToken(authHeader);
            List<EnrollmentResponse> enrollments;
            
            if ("ACTIVE".equals(status)) {
                enrollments = enrollmentService.getLearnerActiveEnrollments(learnerId);
            } else {
                enrollments = enrollmentService.getLearnerEnrollments(learnerId);
            }
            
            return ResponseEntity.ok(enrollments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}