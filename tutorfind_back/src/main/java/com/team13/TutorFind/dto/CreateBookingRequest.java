package com.team13.TutorFind.dto;

import java.math.BigDecimal;

public class CreateBookingRequest {
    private Long tutorId;
    private Long subjectId;
    private String subject; // Subject name (optional, can use subjectId)
    private String mode; // online, in-person
    private String slot; // "Monday 10:00-11:00"
    private String note;
    private BigDecimal proposedPrice;
    
    // Getters and Setters
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    
    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(BigDecimal proposedPrice) { 
        this.proposedPrice = proposedPrice; 
    }
}