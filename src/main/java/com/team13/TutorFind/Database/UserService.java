package com.team13.TutorFind.Database;

import com.team13.TutorFind.User.TestUser;
import com.team13.TutorFind.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
