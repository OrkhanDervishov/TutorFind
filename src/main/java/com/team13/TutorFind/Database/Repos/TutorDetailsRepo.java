package com.team13.TutorFind.Database.Repos;

import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorDetailsRepo extends JpaRepository<TutorDetails, Long> {

//    TutorDetails findByUser(User user);
}
