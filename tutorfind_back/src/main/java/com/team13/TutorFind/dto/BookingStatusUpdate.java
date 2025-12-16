package com.team13.TutorFind.dto;

public class BookingStatusUpdate {
    private String status; // ACCEPTED, DECLINED
    private String response; // Tutor's response message
    
    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}