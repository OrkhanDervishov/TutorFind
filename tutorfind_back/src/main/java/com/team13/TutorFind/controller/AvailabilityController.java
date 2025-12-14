package com.team13.TutorFind.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.Database.Services.AvailabilityService;
import com.team13.TutorFind.Database.Services.TutorService;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.dto.AddAvailabilityRequest;
import com.team13.TutorFind.dto.AddDistrictRequest;
import com.team13.TutorFind.dto.AddSubjectRequest;
import com.team13.TutorFind.dto.UpdateProfileRequest;
import com.team13.TutorFind.entity.AvailabilitySlot;
import com.team13.TutorFind.security.JwtUtil;
@RestController
@RequestMapping("/api/tutors")
@CrossOrigin(origins = "*")
public class AvailabilityController {
    
    private final AvailabilityService availabilityService;
    private final JwtUtil jwtUtil;
    private final TutorDetailsRepo tutorDetailsRepo;
   private final TutorService tutorService;  // Add field

private final UserRepo userRepo;  // Add this field

public AvailabilityController(AvailabilityService availabilityService,
                             TutorService tutorService,
                             JwtUtil jwtUtil,
                             TutorDetailsRepo tutorDetailsRepo,
                             UserRepo userRepo) {  // Add parameter
    this.availabilityService = availabilityService;
    this.tutorService = tutorService;
    this.jwtUtil = jwtUtil;
    this.tutorDetailsRepo = tutorDetailsRepo;
    this.userRepo = userRepo;  // Add initialization
}
    
    /**
     * Helper method to get tutor profile ID from JWT token
     */
    private Long getTutorIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        // Find tutor profile by user ID
        TutorDetails tutorProfile = tutorDetailsRepo.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("You don't have a tutor profile. Only tutors can manage availability."));
        
        return tutorProfile.getId();
    }
    
    // ==================== AVAILABILITY SLOTS ====================
    
    @PostMapping("/availability")
    public ResponseEntity<?> addAvailabilitySlot(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddAvailabilityRequest request) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            
            Map<String, Object> result = availabilityService.addAvailabilitySlot(
                tutorId,
                request.getDayOfWeek(),
                request.getStartTime(),
                request.getEndTime()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/availability/{slotId}")
    public ResponseEntity<?> removeAvailabilitySlot(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long slotId) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            
            Map<String, Object> result = availabilityService.removeAvailabilitySlot(tutorId, slotId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

 @GetMapping("/availability")
public ResponseEntity<?> getMyAvailability(@RequestHeader("Authorization") String authHeader) {
    try {
        // ✅ ADD THIS CHECK
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != UserRoles.TUTOR) {
            throw new RuntimeException("Unauthorized: Only tutors can view availability");
        }
        
        Long tutorId = getTutorIdFromToken(authHeader);
        List<AvailabilitySlot> slots = availabilityService.getTutorAvailabilitySlots(tutorId);
        
        return ResponseEntity.ok(slots);
        
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}

@PutMapping("/profile")
public ResponseEntity<?> updateProfile(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody UpdateProfileRequest request) {
    try {
        // Extract user ID from token
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        // ✅ ADD THIS CHECK: Verify user is a TUTOR
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != UserRoles.TUTOR) {
            throw new RuntimeException("Unauthorized: Only tutors can update profile");
        }
        
        Long tutorId = getTutorIdFromToken(authHeader);
        TutorDetails updatedProfile = tutorService.updateTutorProfile(tutorId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile updated successfully");
        response.put("profile", updatedProfile);
        
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
    
    // ==================== SUBJECTS ====================
    
    @PostMapping("/subjects")
    public ResponseEntity<?> addSubject(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddSubjectRequest request) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            
            Map<String, Object> result = availabilityService.addSubject(
                tutorId,
                request.getSubjectId()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/subjects/{subjectId}")
    public ResponseEntity<?> removeSubject(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long subjectId) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            
            Map<String, Object> result = availabilityService.removeSubject(tutorId, subjectId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // ==================== DISTRICTS ====================
    
    @PostMapping("/districts")
    public ResponseEntity<?> addDistrict(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddDistrictRequest request) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            
            Map<String, Object> result = availabilityService.addDistrict(
                tutorId,
                request.getDistrictId()
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/districts/{districtId}")
    public ResponseEntity<?> removeDistrict(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long districtId) {
        try {
            Long tutorId = getTutorIdFromToken(authHeader);
            
            Map<String, Object> result = availabilityService.removeDistrict(tutorId, districtId);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}