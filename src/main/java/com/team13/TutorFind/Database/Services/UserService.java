package com.team13.TutorFind.Database.Services;

import com.team13.TutorFind.Database.Repos.UserRepo;
import com.team13.TutorFind.User.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepo repo;

    public UserService(UserRepo repo){this.repo = repo;}

    public void saveUser(User user){
        repo.save(user);
    }

    public User getUserById(Long id){
        return repo.findById(id).orElse(null);
    }

    public User getUserByEmail(String email){
        return repo.findByEmail(email);
    }
}
