package com.team13.TutorFind.Authorize;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Services.UserService;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.dto.LoginResponse;
import com.team13.TutorFind.security.JwtUtil;

@RestController
@CrossOrigin
public class AuthorizationController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TutorDetailsRepo tutorDetailsRepo;

    public AuthorizationController(UserService userService, JwtUtil jwtUtil, TutorDetailsRepo tutorDetailsRepo) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tutorDetailsRepo = tutorDetailsRepo;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            // ✅ SECURITY: Block admin registration via API
            if ("ADMIN".equalsIgnoreCase(request.getRole())) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Admin registration is not allowed via API");
            }
            
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPasswordHashed(request.getPassword()); // Will be hashed in UserService
            
            // ✅ Only allow TUTOR or LEARNER roles
            String role = request.getRole().toUpperCase();
            if (!"TUTOR".equals(role) && !"LEARNER".equals(role)) {
                role = "LEARNER"; // Default to learner
            }
            user.setRole(UserRoles.valueOf(role));
            
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setAge(request.getAge());
            user.setPhoneNumber(request.getPhoneNumber());

            userService.saveUser(user);
            
            // Get the saved user to access the generated ID
            User savedUser = userService.getUserByEmail(request.getEmail());
            
            // AUTO-CREATE TUTOR PROFILE IF ROLE IS TUTOR
            if (savedUser.getRole() == UserRoles.TUTOR) {
                TutorDetails tutorProfile = new TutorDetails();
                tutorProfile.setUserId(savedUser.getId());
                tutorProfile.setIsActive(true);
                tutorProfile.setIsVerified(false);
                tutorProfile.setRatingAvg(0.0f);
                tutorProfile.setRatingCount(0);
                
                tutorDetailsRepo.save(tutorProfile);
            }
            
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.getUserByEmail(request.getEmail());
            
            if (user == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
            }
            
            // Verify password
            if (!userService.verifyPassword(request.getPassword(), user.getPasswordHashed())) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
            }

            // Block deactivated accounts
            if (user.getIsActive() != null && !user.getIsActive()) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Account is deactivated. Please contact support.");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().toString()
            );
            
            // Create user info
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().toString()
            );
            
            // Create response
            LoginResponse response = new LoginResponse(token, userInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Login failed: " + e.getMessage());
        }
    }
}
