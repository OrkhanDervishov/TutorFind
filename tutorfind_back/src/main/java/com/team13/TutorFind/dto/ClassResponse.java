package com.team13.TutorFind.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClassResponse {
    
    private Long id;
    private Long tutorId;
    private String tutorName;
    private Long subjectId;
    private String subjectName;
    private String name;
    private String description;
    private String classType;
    private Integer maxStudents;
    private Integer currentStudents;
    private Integer availableSeats;
    private BigDecimal pricePerSession;
    private Integer totalSessions;
    private String scheduleDay;
    private String scheduleTime;
    private Integer durationMinutes;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    
    // Constructors
    public ClassResponse() {}
    
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
    
    public String getTutorName() {
        return tutorName;
    }
    
    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
    
    public Long getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
    
    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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
    
    public Integer getAvailableSeats() {
        return availableSeats;
    }
    
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
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
}