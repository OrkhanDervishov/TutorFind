package com.team13.TutorFind.User;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String passwordHashed;
    private String username;
    private String email;
    private UserRoles role;

    // Changable
    private String firstName;
    private String lastName;
    private Integer age;
    private String phoneNumber;
    private byte[] profilePicture;

    User(){

    }

    public String getUsername() {
        return username;
    }
    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    // This probably will be deleted
    public String getPasswordHashed() {
        return passwordHashed;
    }
    public UserRoles getRole() {return role;}

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        if(firstName.length() < 2 || firstName.length() > 32){
            return;
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        if(lastName.length() < 2 || lastName.length() > 32){
            return;
        }
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        if(age < 18 || age > 100){
            return;
        }
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        // Will be improved
        if(phoneNumber.length() > 10){
            return;
        }
        this.phoneNumber = phoneNumber;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}
