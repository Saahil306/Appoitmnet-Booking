package com.appointment.booking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.booking.entity.Rating;
import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.entity.User;
import com.appointment.booking.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PutMapping("/providers/{providerId}/approve")
    public ResponseEntity<?> approveServiceProvider(@PathVariable Long providerId) {
        try {
            ServiceProvider provider = adminService.approveServiceProvider(providerId);
            return ResponseEntity.ok(provider);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
   
    
    @DeleteMapping("/providers/{providerId}/reject")
    public ResponseEntity<?> rejectServiceProvider(@PathVariable Long providerId) {
        try {
            adminService.rejectServiceProvider(providerId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Service provider rejected successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/providers/pending")
    public ResponseEntity<?> getPendingProviders() {
        try {
            List<ServiceProvider> providers = adminService.getPendingProviders();
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active) {
        
        try {
            List<User> users = adminService.searchUsers(search, role, active);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats() {
        try {
            AdminService.SystemStats stats = adminService.getSystemStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // NEW RATINGS ENDPOINTS
    @GetMapping("/ratings")
    public ResponseEntity<?> getAllRatings() {
        try {
            List<Rating> ratings = adminService.getAllRatings();
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/ratings/search")
    public ResponseEntity<?> searchRatings(@RequestParam(required = false) String search) {
        try {
            List<Rating> ratings = adminService.searchRatings(search);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/ratings/stats")
    public ResponseEntity<?> getRatingStatistics() {
        try {
            Map<String, Object> stats = adminService.getRatingStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}