package com.team13.TutorFind.Authorize;

import com.team13.TutorFind.Database.Services.UserService;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {

    private final UserService userService;

    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHashed(request.getPassword()); // hash in real app
        user.setRole(UserRoles.valueOf(request.getRole()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAge(request.getAge());
        user.setPhoneNumber(request.getPhoneNumber());

        userService.saveUser(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        User user = userService.getUserByEmail(request.getEmail());
        if(user == null || !user.getPasswordHashed().equals(request.getPassword())) {
            return "Invalid credentials";
        }
        return "Login successful";
    }
}
