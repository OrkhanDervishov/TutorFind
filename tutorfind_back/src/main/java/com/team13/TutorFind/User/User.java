package com.team13.TutorFind.User;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "password_hashed")
    private String passwordHashed;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Enumerated(EnumType.STRING)
    private UserRoles role;

    // Changable
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "age")
    private Integer age;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    public User(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // This probably will be deleted
    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }

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

    public UserRoles getRole() {return role;}
    public void setRole(UserRoles role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
