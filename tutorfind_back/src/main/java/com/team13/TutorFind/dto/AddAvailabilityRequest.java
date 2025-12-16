package com.team13.TutorFind.dto;

public class AddAvailabilityRequest {
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    
    public AddAvailabilityRequest() {}
    
    // Getters and Setters
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}