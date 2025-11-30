package com.team13.TutorFind.User;


public abstract class User {
    Long id;
    String passwordHashed;
    String username;
    String email;
    String rating;

    // Changable
    String firstName;
    String lastName;
    Integer age;
    String phoneNumber;
    String profilePictureUrl;

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
    public String getRating() {
        return rating;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
