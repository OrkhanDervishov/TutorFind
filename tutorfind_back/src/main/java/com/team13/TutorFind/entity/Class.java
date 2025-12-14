package com.team13.TutorFind.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "classes")
public class Class {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tutor_id", nullable = false)
    private Long tutorId;
    
    @Column(name = "subject_id")
    private Long subjectId;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "class_type", length = 50)
    private String classType; // INDIVIDUAL, SMALL_GROUP, LARGE_GROUP
    
    @Column(name = "max_students")
    private Integer maxStudents = 1;
    
    @Column(name = "current_students")
    private Integer currentStudents = 0;
    
    @Column(name = "price_per_session", precision = 10, scale = 2)
    private BigDecimal pricePerSession;
    
    @Column(name = "total_sessions")
    private Integer totalSessions;
    
    @Column(name = "schedule_day", length = 20)
    private String scheduleDay;
    
    @Column(name = "schedule_time", length = 20)
    private String scheduleTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(length = 50)
    private String status = "OPEN"; // OPEN, FULL, IN_PROGRESS, COMPLETED, CANCELLED
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Class() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTutorId() {
        return tutorId;
    }
    
    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }
    
    public Long getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getClassType() {
        return classType;
    }
    
    public void setClassType(String classType) {
        this.classType = classType;
    }
    
    public Integer getMaxStudents() {
        return maxStudents;
    }
    
    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }
    
    public Integer getCurrentStudents() {
        return currentStudents;
    }
    
    public void setCurrentStudents(Integer currentStudents) {
        this.currentStudents = currentStudents;
    }
    
    public BigDecimal getPricePerSession() {
        return pricePerSession;
    }
    
    public void setPricePerSession(BigDecimal pricePerSession) {
        this.pricePerSession = pricePerSession;
    }
    
    public Integer getTotalSessions() {
        return totalSessions;
    }
    
    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }
    
    public String getScheduleDay() {
        return scheduleDay;
    }
    
    public void setScheduleDay(String scheduleDay) {
        this.scheduleDay = scheduleDay;
    }
    
    public String getScheduleTime() {
        return scheduleTime;
    }
    
    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}