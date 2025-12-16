package com.team13.TutorFind.dto;

/**
 * Request DTO for creating feedback
 * Used by tutors to give feedback to learners
 */
public class CreateFeedbackRequest {
    
    private Long learnerId;
    private Long bookingId;           // Optional
    private Long subjectId;           // Optional
    private String sessionDate;       // Format: "YYYY-MM-DD"
    private String feedbackText;      // Required - main feedback
    private String strengths;         // Optional
    private String areasForImprovement;  // Optional
    
    public CreateFeedbackRequest() {}
    
    // Getters and Setters
    public Long getLearnerId() { return learnerId; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }
    
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }
    
    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
    
    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }
    
    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { 
        this.areasForImprovement = areasForImprovement; 
    }
}