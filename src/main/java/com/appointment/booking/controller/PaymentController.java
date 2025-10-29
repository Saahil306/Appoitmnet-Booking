package com.appointment.booking.controller;

import com.appointment.booking.entity.Payment;
import com.appointment.booking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(
                request.getAppointmentId(), 
                request.getAmount()
            );
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
   
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody ProcessPaymentRequest request) {
        try {
            Payment payment = paymentService.processPayment(
                request.getPaymentId(),
                request.getPaymentMethod(),
                request.getCardLastFour()
            );
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerPayments(@PathVariable Long customerId) {
        try {
            List<Payment> payments = paymentService.getCustomerPayments(customerId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderPayments(@PathVariable Long providerId) {
        try {
            List<Payment> payments = paymentService.getProviderPayments(providerId);
            Double earnings = paymentService.getProviderEarnings(providerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("payments", payments);
            response.put("totalEarnings", earnings);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getPaymentStatistics() {
        try {
            Map<String, Object> stats = paymentService.getPaymentStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
   
    public static class CreatePaymentRequest {
        private Long appointmentId;
        private Double amount;
        
        public Long getAppointmentId() { return appointmentId; }
        public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
    
    public static class ProcessPaymentRequest {
        private Long paymentId;
        private String paymentMethod;
        private String cardLastFour;
        
        public Long getPaymentId() { return paymentId; }
        public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getCardLastFour() { return cardLastFour; }
        public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
    }
}