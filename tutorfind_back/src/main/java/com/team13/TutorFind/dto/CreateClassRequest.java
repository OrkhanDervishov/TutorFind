package com.team13.TutorFind.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateClassRequest {
    
    private Long subjectId;
    private String name;
    private String description;
    private String classType; // INDIVIDUAL, SMALL_GROUP, LARGE_GROUP
    private Integer maxStudents;
    private BigDecimal pricePerSession;
    private Integer totalSessions;
    private Integer durationMinutes;
    private Long availabilitySlotId;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Constructors
    public CreateClassRequest() {}
    
    // Getters and Setters
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
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public Long getAvailabilitySlotId() {
        return availabilitySlotId;
    }
    
    public void setAvailabilitySlotId(Long availabilitySlotId) {
        this.availabilitySlotId = availabilitySlotId;
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
}
