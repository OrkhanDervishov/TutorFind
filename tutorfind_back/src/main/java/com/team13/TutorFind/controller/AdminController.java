package com.team13.TutorFind.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.Database.Services.AdminService;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.entity.Review;
import com.team13.TutorFind.security.JwtUtil;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserRepo userRepo;
    
    // ============================================
    // AUTHORIZATION HELPER
    // ============================================
    
    private void verifyAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("No authorization token provided");
        }
        
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);
        
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ✅ IMPORTANT: Check role is ADMIN (using enum, not string!)
        if (user.getRole() != UserRoles.ADMIN) {
            throw new RuntimeException("Unauthorized: Admin access only");
        }
    }
    
    // ============================================
    // TUTOR VERIFICATION ENDPOINTS
    // ============================================
    
    // Get unverified tutors
    @GetMapping("/tutors/unverified")
    public ResponseEntity<?> getUnverifiedTutors(@RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            List<TutorDetails> tutors = adminService.getUnverifiedTutors();
            return ResponseEntity.ok(tutors);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Verify a tutor
    @PutMapping("/tutors/{tutorId}/verify")
    public ResponseEntity<Map<String, Object>> verifyTutor(
            @PathVariable Long tutorId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            adminService.verifyTutor(tutorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tutor verified successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Unverify a tutor
    @PutMapping("/tutors/{tutorId}/unverify")
    public ResponseEntity<Map<String, Object>> unverifyTutor(
            @PathVariable Long tutorId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            adminService.unverifyTutor(tutorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tutor unverified successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // ============================================
    // REVIEW MODERATION ENDPOINTS
    // ============================================
    
    // Get pending reviews
    @GetMapping("/reviews/pending")
    public ResponseEntity<?> getPendingReviews(@RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            List<Review> reviews = adminService.getPendingReviews();
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Approve a review
    @PutMapping("/reviews/{reviewId}/approve")
    public ResponseEntity<Map<String, Object>> approveReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            adminService.approveReview(reviewId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review approved successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Reject a review
    @PutMapping("/reviews/{reviewId}/reject")
    public ResponseEntity<Map<String, Object>> rejectReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            verifyAdmin(authHeader);
            String reason = body != null ? body.get("reason") : null;
            adminService.rejectReview(reviewId, reason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Review rejected successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // ============================================
    // USER MANAGEMENT ENDPOINTS
    // ============================================
    
    // Get all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Deactivate user
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            adminService.deactivateUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deactivated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Activate user
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<Map<String, Object>> activateUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            adminService.activateUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User activated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // ============================================
    // PLATFORM STATISTICS
    // ============================================
    
    // Get dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<?> getPlatformStats(@RequestHeader("Authorization") String authHeader) {
        try {
            verifyAdmin(authHeader);
            Map<String, Object> stats = adminService.getPlatformStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}