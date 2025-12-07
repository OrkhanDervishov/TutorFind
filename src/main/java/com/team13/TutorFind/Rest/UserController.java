package com.team13.TutorFind.Rest;

import com.team13.TutorFind.Database.Services.TutorService;
import com.team13.TutorFind.Database.Services.UserService;
import com.team13.TutorFind.User.TutorDetails;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserDTO;
import com.team13.TutorFind.User.UserRoles;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private UserService userService;
    private TutorService tutorService;

    public UserController(UserService userService, TutorService tutorService){
        this.userService = userService;
        this.tutorService = tutorService;
    }

    @PostMapping("/addUser")
    public void createUser(@RequestBody User user){
        if(user.getRole() == UserRoles.TUTOR){
            tutorService.saveTutor(user, 0.0f, "");
            return;
        }
        userService.saveUser(user);
    }

    @RequestMapping("/user{id}")
    public ResponseEntity<UserDTO> sendUser(@PathVariable Long id){
        User user = userService.getUserById(id);
        if(user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserDTO(user));
    }
}
