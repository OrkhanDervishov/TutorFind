package com.team13.TutorFind.Database.Repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    
    User findByEmail(String email);
    
    Optional<User> findOptionalByEmail(String email);
    
    // ✅ ADD THIS LINE
    List<User> findByRole(UserRoles role);
}