package com.team13.TutorFind.Database;

import com.team13.TutorFind.User.TestUser;
import org.springframework.stereotype.Service;

@Service
public class TestUserService {

    private TestUserRepo repo;

    public TestUserService(TestUserRepo repo){
        this.repo = repo;
    }

    public void saveUser(TestUser user){
        repo.save(user);
    }
}
