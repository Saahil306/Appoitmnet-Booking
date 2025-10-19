package com.appointment.booking.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.entity.User;
import com.appointment.booking.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/providers")
    public ResponseEntity<?> getAllServiceProviders() {
        try {
            List<?> providers = userService.getAllServiceProviders();
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/providers/{serviceType}")
    public ResponseEntity<?> getServiceProvidersByType(@PathVariable String serviceType) {
        try {
            List<?> providers = userService.getServiceProvidersByType(serviceType);
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User activated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    @GetMapping("/providers/search")
    public ResponseEntity<?> searchServiceProviders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Boolean availableToday) {
        
        try {
            List<ServiceProvider> providers = userService.searchServiceProviders(search, serviceType, availableToday);
            return ResponseEntity.ok(providers);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/providers/service-types")
    public ResponseEntity<?> getServiceTypes() {
        try {
            List<String> serviceTypes = userService.getDistinctServiceTypes();
            return ResponseEntity.ok(serviceTypes);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}