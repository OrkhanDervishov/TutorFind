package com.team13.TutorFind.Database.Services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team13.TutorFind.Database.Repos.BookingRequestRepo;
import com.team13.TutorFind.Database.Repos.ClassRepo;
import com.team13.TutorFind.Database.Repos.EnrollmentRepo;
import com.team13.TutorFind.Database.Repos.ReviewRepo;
import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.entity.Review;

@Service
public class AdminService {
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private TutorDetailsRepo tutorDetailsRepo;
    
    @Autowired
    private ReviewRepo reviewRepo;
    
    @Autowired
    private BookingRequestRepo bookingRequestRepo;
    
    @Autowired
    private ClassRepo classRepo;
    
    @Autowired
    private EnrollmentRepo enrollmentRepo;
    
    // ============================================
    // TUTOR VERIFICATION
    // ============================================
    
    // Get all unverified tutors
    public List<TutorDetails> getUnverifiedTutors() {
        return tutorDetailsRepo.findByIsVerified(false);
    }
    
    // Verify a tutor
    @Transactional
    public void verifyTutor(Long tutorId) {
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        tutor.setIsVerified(true);
        tutorDetailsRepo.save(tutor);
    }
    
    // Unverify a tutor
    @Transactional
    public void unverifyTutor(Long tutorId) {
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        tutor.setIsVerified(false);
        tutorDetailsRepo.save(tutor);
    }
    
    // ============================================
    // REVIEW MODERATION
    // ============================================
    
    // Get all pending reviews
    public List<Review> getPendingReviews() {
        return reviewRepo.findByStatus("PENDING");
    }
    
    // Approve a review
    @Transactional
    public void approveReview(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setStatus("APPROVED");
        reviewRepo.save(review);
        recalculateTutorRating(review.getTutorId());
        // Trigger rating recalculation (will happen automatically via trigger)
    }
    private void recalculateTutorRating(Long tutorId) {
        // Get all APPROVED reviews for this tutor
        List<Review> approvedReviews = reviewRepo.findByTutorIdAndStatus(tutorId, "APPROVED");
        
        if (approvedReviews.isEmpty()) {
            return; // No approved reviews yet
        }
        
        // Calculate average rating
        double avgRating = approvedReviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
        
        // Update tutor profile
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor not found"));
        
        tutor.setRatingAvg((float) avgRating);
        tutor.setRatingCount(approvedReviews.size());
        
        tutorDetailsRepo.save(tutor);
    }
    
    // Reject a review
    // Reject a review
    @Transactional
    public void rejectReview(Long reviewId, String reason) {
        Review review = reviewRepo.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        String previousStatus = review.getStatus();
        review.setStatus("REJECTED");
        reviewRepo.save(review);
        
        // ✅ RECALCULATE if review was previously APPROVED
        if ("APPROVED".equals(previousStatus)) {
            recalculateTutorRating(review.getTutorId());
        }
    }
    
    // ============================================
    // USER MANAGEMENT
    // ============================================
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    // Get users by role
    public List<User> getUsersByRole(UserRoles role) {
        return userRepo.findByRole(role);
    }
    
    // Deactivate user
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Don't allow deactivating admins
        if (user.getRole() == UserRoles.ADMIN) {
            throw new RuntimeException("Cannot deactivate admin users");
        }
        
        // If tutor, deactivate their profile too
        if (user.getRole() == UserRoles.TUTOR) {
            tutorDetailsRepo.findByUserId(userId).ifPresent(tutor -> {
                tutor.setIsActive(false);
                tutorDetailsRepo.save(tutor);
            });
        }
    }
    
    // Activate user
    @Transactional
    public void activateUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // If tutor, activate their profile too
        if (user.getRole() == UserRoles.TUTOR) {
            tutorDetailsRepo.findByUserId(userId).ifPresent(tutor -> {
                tutor.setIsActive(true);
                tutorDetailsRepo.save(tutor);
            });
        }
    }
    
    // ============================================
    // PLATFORM STATISTICS
    // ============================================
    
    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User counts
        Long totalUsers = userRepo.count();
        Long totalTutors = (long) userRepo.findByRole(UserRoles.TUTOR).size();
        Long totalLearners = (long) userRepo.findByRole(UserRoles.LEARNER).size();
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalTutors", totalTutors);
        stats.put("totalLearners", totalLearners);
        
        // Tutor stats
        Long verifiedTutors = tutorDetailsRepo.countByIsVerified(true);
        Long unverifiedTutors = tutorDetailsRepo.countByIsVerified(false);
        
        stats.put("verifiedTutors", verifiedTutors);
        stats.put("unverifiedTutors", unverifiedTutors);
        
        // Review stats
        Long totalReviews = reviewRepo.count();
        Long pendingReviews = (long) reviewRepo.findByStatus("PENDING").size();
        Long approvedReviews = (long) reviewRepo.findByStatus("APPROVED").size();
        Long rejectedReviews = (long) reviewRepo.findByStatus("REJECTED").size();
        
        stats.put("totalReviews", totalReviews);
        stats.put("pendingReviews", pendingReviews);
        stats.put("approvedReviews", approvedReviews);
        stats.put("rejectedReviews", rejectedReviews);
        
        // Booking stats
        Long totalBookings = bookingRequestRepo.count();
        Long pendingBookings = (long) bookingRequestRepo.findByStatus("PENDING").size();
        Long acceptedBookings = (long) bookingRequestRepo.findByStatus("ACCEPTED").size();
        
        stats.put("totalBookings", totalBookings);
        stats.put("pendingBookings", pendingBookings);
        stats.put("acceptedBookings", acceptedBookings);
        
        // Class stats
        Long totalClasses = classRepo.count();
        Long openClasses = (long) classRepo.findByStatus("OPEN").size();
        Long fullClasses = (long) classRepo.findByStatus("FULL").size();
        
        stats.put("totalClasses", totalClasses);
        stats.put("openClasses", openClasses);
        stats.put("fullClasses", fullClasses);
        
        // Enrollment stats
        Long totalEnrollments = enrollmentRepo.count();
        Long activeEnrollments = enrollmentRepo.countByStatus("ACTIVE");
        
        stats.put("totalEnrollments", totalEnrollments);
        stats.put("activeEnrollments", activeEnrollments);
        
        return stats;
    }
}