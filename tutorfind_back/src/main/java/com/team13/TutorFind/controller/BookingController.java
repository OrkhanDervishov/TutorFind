package com.team13.TutorFind.controller;

import com.team13.TutorFind.Database.Services.BookingService;
import com.team13.TutorFind.dto.BookingResponse;
import com.team13.TutorFind.dto.BookingStatusUpdate;
import com.team13.TutorFind.dto.CreateBookingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/bookings")
public class BookingController {
    
    private final BookingService bookingService;
    
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    // Create booking request (learner)
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateBookingRequest request
    ) {
        try {
            // TODO: Extract userId from JWT token
            // For now, using hardcoded learnerId (you'll add JWT extraction later)
            Long learnerId = 2L; // Replace with actual user from token
            
            BookingResponse booking = bookingService.createBooking(learnerId, request);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create booking: " + e.getMessage());
        }
    }
    
    // Get bookings sent by learner
    @GetMapping("/sent")
    public ResponseEntity<?> getSentBookings(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status
    ) {
        try {
            // TODO: Extract userId from JWT token
            Long learnerId = 2L; // Replace with actual user from token
            
            List<BookingResponse> bookings = bookingService.getSentBookings(learnerId, status);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get bookings: " + e.getMessage());
        }
    }
    
    // Get bookings received by tutor
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedBookings(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status
    ) {
        try {
            // TODO: Extract userId from JWT token
            Long tutorId = 3L; // Replace with actual user from token
            
            List<BookingResponse> bookings = bookingService.getReceivedBookings(tutorId, status);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get bookings: " + e.getMessage());
        }
    }
    
    // Accept booking (tutor)
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) BookingStatusUpdate update
    ) {
        try {
            // TODO: Extract userId from JWT token
            Long tutorId = 3L; // Replace with actual user from token
            
            String response = update != null ? update.getResponse() : null;
            BookingResponse booking = bookingService.acceptBooking(id, tutorId, response);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to accept booking: " + e.getMessage());
        }
    }
    
    // Decline booking (tutor)
    @PutMapping("/{id}/decline")
    public ResponseEntity<?> declineBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) BookingStatusUpdate update
    ) {
        try {
            // TODO: Extract userId from JWT token
            Long tutorId = 3L; // Replace with actual user from token
            
            String response = update != null ? update.getResponse() : null;
            BookingResponse booking = bookingService.declineBooking(id, tutorId, response);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to decline booking: " + e.getMessage());
        }
    }
    
    // Get single booking
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        try {
            BookingResponse booking = bookingService.getBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Booking not found");
        }
    }
}