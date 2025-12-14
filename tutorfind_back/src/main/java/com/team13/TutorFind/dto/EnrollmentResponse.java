package com.team13.TutorFind.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EnrollmentResponse {
    
    private Long id;
    private Long classId;
    private String className;
    private Long learnerId;
    private String learnerName;
    private LocalDateTime enrollmentDate;
    private String status;
    private Integer sessionsAttended;
    private String paymentStatus;
    private BigDecimal amountPaid;
    
    // Class details
    private String scheduleDay;
    private String scheduleTime;
    private Integer durationMinutes;
    private String tutorName;
    private String subjectName;
    
    // Constructors
    public EnrollmentResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClassId() {
        return classId;
    }
    
    public void setClassId(Long classId) {
        this.classId = classId;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public Long getLearnerId() {
        return learnerId;
    }
    
    public void setLearnerId(Long learnerId) {
        this.learnerId = learnerId;
    }
    
    public String getLearnerName() {
        return learnerName;
    }
    
    public void setLearnerName(String learnerName) {
        this.learnerName = learnerName;
    }
    
    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getSessionsAttended() {
        return sessionsAttended;
    }
    
    public void setSessionsAttended(Integer sessionsAttended) {
        this.sessionsAttended = sessionsAttended;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public BigDecimal getAmountPaid() {
        return amountPaid;
    }
    
    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
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
    
    public String getTutorName() {
        return tutorName;
    }
    
    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }
    
    public String getSubjectName() {
        return subjectName;
    }
    
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}