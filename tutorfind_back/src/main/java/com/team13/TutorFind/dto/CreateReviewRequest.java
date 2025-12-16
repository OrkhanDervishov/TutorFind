package com.team13.TutorFind.dto;

/**
 * Request DTO for creating a new review
 */
public class CreateReviewRequest {
    
    private Long tutorId;
    private Integer rating;      // 1-5
    private String comment;
    private Long bookingId;      // Optional
    
    public CreateReviewRequest() {}
    
    // Getters and Setters
    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
}