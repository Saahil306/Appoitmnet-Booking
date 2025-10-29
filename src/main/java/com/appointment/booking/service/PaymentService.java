package com.appointment.booking.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appointment.booking.entity.Appointment;
import com.appointment.booking.entity.AppointmentStatus;
import com.appointment.booking.entity.Customer;
import com.appointment.booking.entity.Notification;
import com.appointment.booking.entity.NotificationType;
import com.appointment.booking.entity.Payment;
import com.appointment.booking.entity.PaymentStatus;
import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private NotificationService notificationService;
    
   
    public Payment createPayment(Long appointmentId, Double amount) {
        Appointment appointment = appointmentService.getAppointmentEntityById(appointmentId);
        
       
        Optional<Payment> existingPayment = paymentRepository.findByAppointmentId(appointmentId);
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Payment already exists for this appointment");
        }
        
        Payment payment = new Payment(appointment, amount);
        return paymentRepository.save(payment);
    }
    
    
    public Payment processPayment(Long paymentId, String paymentMethod, String cardLastFour) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
       
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment already processed");
        }
        
       
        String transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        payment.setStatus(PaymentStatus.SUCCESSFUL);
        payment.setTransactionId(transactionId);
        payment.setPaymentMethod(paymentMethod + " (" + cardLastFour + ")");
        payment.setPaymentGateway("SIMULATED_GATEWAY");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        
        
        Appointment appointment = payment.getAppointment();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        
        Payment savedPayment = paymentRepository.save(payment);
        
        
        sendPaymentNotification(appointment.getCustomer(), appointment.getServiceProvider(), savedPayment);
        
        return savedPayment;
    }
    
    
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
    
    
    public List<Payment> getCustomerPayments(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
    
   
    public List<Payment> getProviderPayments(Long providerId) {
        return paymentRepository.findByProviderId(providerId);
    }
    
    
    public Double getProviderEarnings(Long providerId) {
        return paymentRepository.getTotalEarningsByProvider(providerId);
    }
    
    
    public Map<String, Object> getPaymentStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPayments", paymentRepository.count());
        stats.put("successfulPayments", paymentRepository.countByStatus(PaymentStatus.SUCCESSFUL));
        stats.put("pendingPayments", paymentRepository.countByStatus(PaymentStatus.PENDING));
        stats.put("failedPayments", paymentRepository.countByStatus(PaymentStatus.FAILED));
        return stats;
    }
    
    
    private void sendPaymentNotification(Customer customer, ServiceProvider provider, Payment payment) {
       
        String customerMessage = String.format("Payment of ₹%.2f for your appointment with %s was successful. Transaction ID: %s", 
            payment.getAmount(), provider.getFirstName(), payment.getTransactionId());
        
        Notification customerNotification = new Notification(
            customer, 
            "Payment Successful", 
            customerMessage, 
            NotificationType.SYSTEM_ANNOUNCEMENT
        );
        notificationService.createNotification(customerNotification);
        
       
        String providerMessage = String.format("Payment of ₹%.2f received for appointment with %s. Transaction ID: %s", 
            payment.getAmount(), customer.getFirstName(), payment.getTransactionId());
        
        Notification providerNotification = new Notification(
            provider, 
            "Payment Received", 
            providerMessage, 
            NotificationType.SYSTEM_ANNOUNCEMENT
        );
        notificationService.createNotification(providerNotification);
    }
}