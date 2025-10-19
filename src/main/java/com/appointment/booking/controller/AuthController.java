package com.appointment.booking.controller;

import com.appointment.booking.entity.*;
import com.appointment.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {
        try {
            // Explicitly set role before saving
            customer.setRole(UserRole.CUSTOMER);
            Customer savedCustomer = userService.registerCustomer(customer);
            return ResponseEntity.ok(savedCustomer);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register/provider")
    public ResponseEntity<?> registerServiceProvider(@RequestBody ServiceProvider serviceProvider) {
        try {
            // Explicitly set role before saving
            serviceProvider.setRole(UserRole.SERVICE_PROVIDER);
            ServiceProvider savedProvider = userService.registerServiceProvider(serviceProvider);
            return ResponseEntity.ok(savedProvider);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            // Explicitly set role before saving
            admin.setRole(UserRole.ADMIN);
            Admin savedAdmin = userService.registerAdmin(admin);
            return ResponseEntity.ok(savedAdmin);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Login Request DTO
    public static class LoginRequest {
        private String email;
        private String password;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}