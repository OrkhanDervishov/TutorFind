package com.team13.TutorFind.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_requests")
public class BookingRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "learner_id", nullable = false)
    private Long learnerId;
    
    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;
    
    @Column(name = "subject_id")
    private Long subjectId;
    
    @Column(name = "session_type", length = 50)
    private String sessionType; // individual, group
    
    @Column(length = 50)
    private String mode; // online, in-person
    
    @Column(name = "slot_day", length = 20)
    private String slotDay; // Monday, Tuesday, etc.
    
    @Column(name = "slot_time", length = 20)
    private String slotTime; // "10:00-11:00"
    
    @Column(name = "slot_text")
    private String slotText; // "Monday 10:00-11:00"
    
    @Column(name = "preferred_schedule", columnDefinition = "TEXT")
    private String preferredSchedule;
    
    @Column(name = "learner_note", columnDefinition = "TEXT")
    private String learnerNote;
    
    @Column(name = "tutor_response", columnDefinition = "TEXT")
    private String tutorResponse;
    
    @Column(name = "proposed_price", precision = 10, scale = 2)
    private BigDecimal proposedPrice;
    
    @Column(name = "sessions_count")
    private Integer sessionsCount = 1;
    
    @Column(length = 50)
    private String status = "PENDING"; // PENDING, ACCEPTED, DECLINED, CANCELLED, COMPLETED
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    // Constructors
    public BookingRequest() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getLearnerId() { return learnerId; }
    public void setLearnerId(Long learnerId) { this.learnerId = learnerId; }
    
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    
    public String getSlotDay() { return slotDay; }
    public void setSlotDay(String slotDay) { this.slotDay = slotDay; }
    
    public String getSlotTime() { return slotTime; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }
    
    public String getSlotText() { return slotText; }
    public void setSlotText(String slotText) { this.slotText = slotText; }
    
    public String getPreferredSchedule() { return preferredSchedule; }
    public void setPreferredSchedule(String preferredSchedule) { 
        this.preferredSchedule = preferredSchedule; 
    }
    
    public String getLearnerNote() { return learnerNote; }
    public void setLearnerNote(String learnerNote) { this.learnerNote = learnerNote; }
    
    public String getTutorResponse() { return tutorResponse; }
    public void setTutorResponse(String tutorResponse) { this.tutorResponse = tutorResponse; }
    
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(BigDecimal proposedPrice) { 
        this.proposedPrice = proposedPrice; 
    }
    
    public Integer getSessionsCount() { return sessionsCount; }
    public void setSessionsCount(Integer sessionsCount) { 
        this.sessionsCount = sessionsCount; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { 
        this.respondedAt = respondedAt; 
    }
}