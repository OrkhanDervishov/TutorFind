package com.team13.TutorFind.Rest;

import com.team13.TutorFind.Database.UserService;
import com.team13.TutorFind.User.TestUser;
import com.team13.TutorFind.User.User;
import com.team13.TutorFind.User.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private UserService service;

    public UserController(UserService service){this.service = service;}

    @PostMapping("/addUser")
    public void createUser(@RequestBody User user){
        service.saveUser(user);
    }

    @RequestMapping("/user{id}")
    public ResponseEntity<UserDTO> sendUser(@PathVariable Long id){
        User user = service.getUserById(id);
        if(user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserDTO(user));
    }
}
