package com.team13.TutorFind.Rest;

import com.team13.TutorFind.Database.Services.TestUserService;
import com.team13.TutorFind.User.TestUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestUserController {

    TestUserService service;

    public TestUserController(TestUserService service){
        this.service = service;
    }

    @PostMapping("/addTestUser")
    public void createTestUser(@RequestBody TestUser user){
        service.saveUser(user);
    }
}
