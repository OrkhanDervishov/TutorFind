package com.team13.TutorFind.Database.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;  // ✅ ADD THIS
import com.team13.TutorFind.User.UserRoles;
import com.team13.TutorFind.dto.UpdateProfileRequest;  // ✅ ADD THIS

@Service
public class TutorService {
    private UserRepo userRepo;
    private final TutorDetailsRepo tutorDetailsRepo;

    public TutorService(UserRepo userRepo, TutorDetailsRepo tutorDetailsRepo) {
        this.userRepo = userRepo;
        this.tutorDetailsRepo = tutorDetailsRepo;
    }

    public void saveTutor(User user, float rating, String bio) {
        user.setRole(UserRoles.TUTOR);
        userRepo.save(user);

        TutorDetails details = new TutorDetails();
        details.setRatingAvg(rating);
        details.setBio(bio);
        tutorDetailsRepo.save(details);
    }

    public TutorDetails getTutorDetailsByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if(user == null) return null;
        return tutorDetailsRepo.findById(user.getId()).orElse(null);
    }
    
    // ✅ ADD THIS NEW METHOD
    /**
     * Update tutor profile information
     * Allows tutors to edit their headline, bio, qualifications, experience, and rate
     */
    @Transactional
    public TutorDetails updateTutorProfile(Long tutorId, UpdateProfileRequest request) {
        TutorDetails tutor = tutorDetailsRepo.findById(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor profile not found"));
        
        // Update only provided fields (partial update)
        if (request.getHeadline() != null) {
            tutor.setHeadline(request.getHeadline());
        }
        
        if (request.getBio() != null) {
            tutor.setBio(request.getBio());
        }
        
        if (request.getQualifications() != null) {
            tutor.setQualifications(request.getQualifications());
        }
        
        if (request.getExperienceYears() != null) {
            tutor.setExperienceYears(request.getExperienceYears());
        }
        
        if (request.getMonthlyRate() != null) {
            tutor.setMonthlyRate(request.getMonthlyRate());
        }
        
        // Update timestamp
        tutor.setUpdatedAt(java.time.LocalDateTime.now());
        
        return tutorDetailsRepo.save(tutor);
    }
}