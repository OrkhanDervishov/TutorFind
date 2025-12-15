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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.Database.Services.AdminService;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.entity.Review;
import com.team13.TutorFind.security.JwtUtil;
import com.team13.TutorFind.Database.Services.FlagService;
import com.team13.TutorFind.entity.Flag;
import com.team13.TutorFind.Database.Repos.CityRepo;
import com.team13.TutorFind.Database.Repos.DistrictRepo;
import com.team13.TutorFind.Database.Repos.SubjectRepo;
import com.team13.TutorFind.entity.City;
import com.team13.TutorFind.entity.District;
import com.team13.TutorFind.entity.Subject;
import org.springframework.data.domain.Page;

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

    @Autowired
    private FlagService flagService;

    @Autowired
    private CityRepo cityRepo;

    @Autowired
    private DistrictRepo districtRepo;

    @Autowired
    private SubjectRepo subjectRepo;
    
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

    // ============================================
    // FLAG MODERATION ENDPOINTS
    // ============================================

    @GetMapping("/flags")
    public ResponseEntity<?> getFlags(@RequestHeader("Authorization") String authHeader,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) String contentType,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        try {
            verifyAdmin(authHeader);
            Page<Flag> flags = flagService.listFlags(status, contentType, page, size);
            return ResponseEntity.ok(flags);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/flags/{flagId}/status")
    public ResponseEntity<?> updateFlagStatus(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable Long flagId,
                                              @RequestBody Map<String, String> body) {
        try {
            verifyAdmin(authHeader);
            String status = body != null ? body.get("status") : null;
            if (status == null) throw new RuntimeException("status is required");
            Flag flag = flagService.updateStatus(flagId, status);
            return ResponseEntity.ok(flag);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ============================================
    // CATALOG MANAGEMENT (cities/districts/subjects)
    // ============================================

    @PostMapping("/cities")
    public ResponseEntity<?> createCity(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody Map<String, String> body) {
        try {
            verifyAdmin(authHeader);
            String name = body.get("name");
            String country = body.getOrDefault("country", "Azerbaijan");
            City city = new City();
            city.setName(name);
            city.setCountry(country);
            cityRepo.save(city);
            return ResponseEntity.ok(city);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/districts")
    public ResponseEntity<?> createDistrict(@RequestHeader("Authorization") String authHeader,
                                            @RequestBody Map<String, Object> body) {
        try {
            verifyAdmin(authHeader);
            Number cityId = (Number) body.get("cityId");
            String name = (String) body.get("name");
            if (cityId == null || name == null) throw new RuntimeException("cityId and name required");
            District d = new District();
            d.setCity(new com.team13.TutorFind.entity.City() {{
                setId(cityId.longValue());
            }});
            d.setName(name);
            districtRepo.save(d);
            return ResponseEntity.ok(d);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/subjects")
    public ResponseEntity<?> createSubject(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody Map<String, String> body) {
        try {
            verifyAdmin(authHeader);
            String name = body.get("name");
            String category = body.get("category");
            if (name == null) throw new RuntimeException("name required");
            Subject s = new Subject();
            s.setName(name);
            s.setCategory(category);
            subjectRepo.save(s);
            return ResponseEntity.ok(s);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
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
