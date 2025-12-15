package com.team13.TutorFind.Authorize;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role; // TUTOR or LEARNER
    private String firstName;
    private String lastName;
    private Integer age;
    private String phoneNumber;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String role,
                           String firstName, String lastName, Integer age, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
