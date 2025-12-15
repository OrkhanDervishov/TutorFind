package com.team13.TutorFind.dto;

import java.math.BigDecimal;

public class UpdateProfileRequest {
    private String headline;
    private String bio;
    private String qualifications;
    private Integer experienceYears;
    private BigDecimal monthlyRate;

    // Default constructor
    public UpdateProfileRequest() {}

    // Full constructor
    public UpdateProfileRequest(String headline, String bio, String qualifications, 
                               Integer experienceYears, BigDecimal monthlyRate) {
        this.headline = headline;
        this.bio = bio;
        this.qualifications = qualifications;
        this.experienceYears = experienceYears;
        this.monthlyRate = monthlyRate;
    }

    // Getters
    public String getHeadline() {
        return headline;
    }

    public String getBio() {
        return bio;
    }

    public String getQualifications() {
        return qualifications;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    // Setters
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }
}