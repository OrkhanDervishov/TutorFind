package com.team13.TutorFind.dto;

public class LoginResponse {
    private String token;
    private UserInfo user;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    
    // Nested class for user info
    public static class UserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String email, String firstName, String lastName, String role) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}