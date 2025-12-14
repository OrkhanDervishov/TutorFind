package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo repo;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection (Spring will autowire both automatically)
    public UserService(UserRepo repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(User user) {
        // Hash password before saving
        String hashedPassword = passwordEncoder.encode(user.getPasswordHashed());
        user.setPasswordHashed(hashedPassword);
        repo.save(user);
    }

    public User getUserById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return repo.findByEmail(email);
    }
    
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}