package com.appointment.booking.controller;

import com.appointment.booking.entity.Availability;
import com.appointment.booking.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/availability")
@CrossOrigin(origins = "*")
public class AvailabilityController {
    
    @Autowired
    private AvailabilityService availabilityService;
    
    @PostMapping("/provider/{providerId}")
    public ResponseEntity<?> setAvailability(
            @PathVariable Long providerId, 
            @RequestBody List<Availability> availabilities) {
        try {
            List<Availability> savedAvailabilities = availabilityService.setAvailability(providerId, availabilities);
            return ResponseEntity.ok(savedAvailabilities);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderAvailability(@PathVariable Long providerId) {
        try {
            List<Availability> availabilities = availabilityService.getProviderAvailability(providerId);
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}