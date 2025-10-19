package com.appointment.booking.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointment.booking.dto.AppointmentResponseDTO;
import com.appointment.booking.entity.Appointment;
import com.appointment.booking.entity.AppointmentStatus;
import com.appointment.booking.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
 // All methods ke return types Appointment se AppointmentResponseDTO change karo
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookAppointmentRequest request) {
        try {
            AppointmentResponseDTO appointment = appointmentService.bookAppointment(
                request.getCustomerId(), 
                request.getProviderId(), 
                request.getAppointmentDateTime(), 
                request.getDuration(), 
                request.getServiceType()
            );
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerAppointments(@PathVariable Long customerId) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getCustomerAppointments(customerId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderAppointments(@PathVariable Long providerId) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getProviderAppointments(providerId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long appointmentId, 
            @RequestBody RescheduleRequest request) {
        try {
        	AppointmentResponseDTO appointment = appointmentService.rescheduleAppointment(
                appointmentId, 
                request.getNewDateTime()
            );
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId) {
        try {
        	AppointmentResponseDTO appointment = appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long appointmentId, 
            @RequestBody UpdateStatusRequest request) {
        try {
        	AppointmentResponseDTO appointment = appointmentService.updateAppointmentStatus(
                appointmentId, 
                request.getStatus()
            );
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId) {
        try {
        	AppointmentResponseDTO appointment = appointmentService.getAppointmentById(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatus(status);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Request DTOs
    public static class BookAppointmentRequest {
        private Long customerId;
        private Long providerId;
        private LocalDateTime appointmentDateTime;
        private int duration;
        private String serviceType;
        
        // Getters and Setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public Long getProviderId() { return providerId; }
        public void setProviderId(Long providerId) { this.providerId = providerId; }
        public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
        public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    }
    
    public static class RescheduleRequest {
        private LocalDateTime newDateTime;
        
        public LocalDateTime getNewDateTime() { return newDateTime; }
        public void setNewDateTime(LocalDateTime newDateTime) { this.newDateTime = newDateTime; }
    }
    
    public static class UpdateStatusRequest {
        private AppointmentStatus status;
        
        public AppointmentStatus getStatus() { return status; }
        public void setStatus(AppointmentStatus status) { this.status = status; }
    }
}