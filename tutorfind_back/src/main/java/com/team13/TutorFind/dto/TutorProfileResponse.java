package com.team13.TutorFind.dto;

import java.math.BigDecimal;
import java.util.List;

public class TutorProfileResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    
    // Profile info
    private String headline;
    private String bio;
    private String qualifications;
    private Integer experienceYears;
    
    // Location
    private String city;
    private List<String> districts;
    
    // Subjects
    private List<SubjectInfo> subjects;
    
    // Pricing
    private BigDecimal monthlyRate;
    
    // Rating
    private Float rating;
    private Integer reviewCount;
    
    // Status
    private Boolean isVerified;
    private Boolean isActive;
    
    // Availability
    private List<AvailabilitySlot> availability;
    
    // Reviews (we'll add later)
    private List<ReviewInfo> reviews;
    
    // Constructors
    public TutorProfileResponse() {}
    
    // Nested classes
    public static class SubjectInfo {
        private Long id;
        private String name;
        private String category;
        
        public SubjectInfo() {}
        
        public SubjectInfo(Long id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
    
    public static class AvailabilitySlot {
        private String day;
        private String startTime;
        private String endTime;
        
        public AvailabilitySlot() {}
        
        public AvailabilitySlot(String day, String startTime, String endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        // Getters and Setters
        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
    
    public static class ReviewInfo {
        private Long id;
        private String learnerName;
        private Integer rating;
        private String comment;
        private String createdAt;
        
        public ReviewInfo() {}
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getLearnerName() { return learnerName; }
        public void setLearnerName(String learnerName) { this.learnerName = learnerName; }
        
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
    
    // Main class Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
    
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { 
        this.experienceYears = experienceYears; 
    }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public List<String> getDistricts() { return districts; }
    public void setDistricts(List<String> districts) { this.districts = districts; }
    
    public List<SubjectInfo> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectInfo> subjects) { this.subjects = subjects; }
    
    public BigDecimal getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(BigDecimal monthlyRate) { this.monthlyRate = monthlyRate; }
    
    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public List<AvailabilitySlot> getAvailability() { return availability; }
    public void setAvailability(List<AvailabilitySlot> availability) { 
        this.availability = availability; 
    }
    
    public List<ReviewInfo> getReviews() { return reviews; }
    public void setReviews(List<ReviewInfo> reviews) { this.reviews = reviews; }
}