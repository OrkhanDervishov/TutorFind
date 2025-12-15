package com.team13.TutorFind.User;


// This class is for GET user requests
// As sometimes we don't want to send all data of the user
// So there is only selected data that can be sent
public class UserDTO {
    private String username;
    private UserRoles role;

    // Changable
    private String firstName;
    private String lastName;
    private Integer age;
    private byte[] profilePicture;

    public UserDTO(User user){
        this.username = user.getUsername();
        this.role = user.getRole();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.profilePicture = user.getProfilePicture();
    }
}
