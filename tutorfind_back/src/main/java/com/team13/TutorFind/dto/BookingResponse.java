package com.team13.TutorFind.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingResponse {
    private Long id;
    private Long learnerId;
    private String learnerName;
    private String learnerPhone;
    private Long tutorId;
    private String tutorName;
    private String subject;
    private String mode;
    private String slot;
    private String learnerNote;
    private String tutorResponse;
    private BigDecimal proposedPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    
    // Constructors
    public BookingResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLearnerId() { return learnerId; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }
    
    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }
    
    public String getLearnerPhone() { return learnerPhone; }
    public void setLearnerPhone(String learnerPhone) { this.learnerPhone = learnerPhone; }
    
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }
    
    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    
    public String getLearnerNote() { return learnerNote; }
    public void setLearnerNote(String learnerNote) { this.learnerNote = learnerNote; }
    
    public String getTutorResponse() { return tutorResponse; }
    public void setTutorResponse(String tutorResponse) { this.tutorResponse = tutorResponse; }
    
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(BigDecimal proposedPrice) { 
        this.proposedPrice = proposedPrice; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { 
        this.respondedAt = respondedAt; 
    }
}