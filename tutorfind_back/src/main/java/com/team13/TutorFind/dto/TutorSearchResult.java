package com.team13.TutorFind.dto;

import java.math.BigDecimal;

public class TutorSearchResult {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String headline;
    private String bio;
    private String city;
    private BigDecimal monthlyRate;
    private Float rating;
    private Integer reviewCount;
    private Integer experienceYears;
    private Boolean isVerified;
    
    // Constructors
    public TutorSearchResult() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public BigDecimal getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(BigDecimal monthlyRate) { this.monthlyRate = monthlyRate; }
    
    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
}