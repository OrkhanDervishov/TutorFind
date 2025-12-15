package com.team13.TutorFind.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for review data
 * Includes learner name for display
 */
public class ReviewResponse {
    
    private Long id;
    private Long tutorId;
    private Long learnerId;
    private String learnerName;     // First + Last name
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    
    public ReviewResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }
    
    public Long getLearnerId() { return learnerId; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }
    
    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}