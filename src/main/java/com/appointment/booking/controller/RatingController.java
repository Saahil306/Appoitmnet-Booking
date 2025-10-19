package com.appointment.booking.controller;

import com.appointment.booking.entity.Rating;
import com.appointment.booking.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {
    
    @Autowired
    private RatingService ratingService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitRating(@RequestBody SubmitRatingRequest request) {
        try {
            Rating rating = ratingService.submitRating(
                request.getCustomerId(), 
                request.getProviderId(), 
                request.getAppointmentId(), 
                request.getRating(), 
                request.getComment()
            );
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderRatings(@PathVariable Long providerId) {
        try {
            List<Rating> ratings = ratingService.getProviderRatings(providerId);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/provider/{providerId}/stats")
    public ResponseEntity<?> getProviderRatingStats(@PathVariable Long providerId) {
        try {
            Double averageRating = ratingService.getProviderAverageRating(providerId);
            Long ratingCount = ratingService.getProviderRatingCount(providerId);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("averageRating", averageRating);
            stats.put("ratingCount", ratingCount);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getRatingByAppointment(@PathVariable Long appointmentId) {
        try {
            Rating rating = ratingService.getRatingByAppointment(appointmentId);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Request DTO
    public static class SubmitRatingRequest {
        private Long customerId;
        private Long providerId;
        private Long appointmentId;
        private Integer rating;
        private String comment;

        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public Long getProviderId() { return providerId; }
        public void setProviderId(Long providerId) { this.providerId = providerId; }

        public Long getAppointmentId() { return appointmentId; }
        public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}