package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.TutorDetailsRepo;
import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import org.springframework.stereotype.Service;

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
        details.setRating(rating);
        details.setBio(bio);
        tutorDetailsRepo.save(details);
    }

    public TutorDetails getTutorDetailsByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if(user == null) return null;
        return tutorDetailsRepo.findById(user.getId()).orElse(null);
    }
}
