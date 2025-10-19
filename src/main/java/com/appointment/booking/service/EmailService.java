package com.appointment.booking.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Send simple email
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true for HTML
            
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    // Send templated email
    public void sendTemplatedEmail(String to, String subject, String templateName, 
                                   Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(templateName, context);
            
            sendSimpleEmail(to, subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Failed to send templated email: " + e.getMessage());
        }
    }

    // Helper method to create maps for Java 8
    private Map<String, Object> createVariables(Object... keyValuePairs) {
        Map<String, Object> variables = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            variables.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return variables;
    }

    // Appointment specific emails - FIXED FOR JAVA 8
    public void sendAppointmentConfirmation(String customerEmail, String customerName, 
                                           String providerName, String appointmentDateTime,
                                           String serviceType, int duration) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("providerName", providerName);
        variables.put("appointmentDateTime", appointmentDateTime);
        variables.put("serviceType", serviceType);
        variables.put("duration", duration);
        
        sendTemplatedEmail(customerEmail, 
            "Appointment Confirmation - Booking System",
            "appointment-confirmation", 
            variables);
    }

    public void sendAppointmentReminder(String customerEmail, String customerName,
                                       String providerName, String appointmentDateTime) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("providerName", providerName);
        variables.put("appointmentDateTime", appointmentDateTime);
        
        sendTemplatedEmail(customerEmail,
            "Appointment Reminder - Booking System",
            "appointment-reminder",
            variables);
    }

    public void sendAppointmentCancellation(String customerEmail, String customerName,
                                           String providerName, String appointmentDateTime,
                                           String serviceType) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("providerName", providerName);
        variables.put("appointmentDateTime", appointmentDateTime);
        variables.put("serviceType", serviceType);
        
        sendTemplatedEmail(customerEmail,
            "Appointment Cancelled - Booking System",
            "appointment-cancellation",
            variables);
    }

    // Provider approval email
    public void sendProviderApprovalEmail(String providerEmail, String providerName) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("providerName", providerName);
        
        sendTemplatedEmail(providerEmail,
            "Provider Account Approved - Booking System",
            "provider-approval",
            variables);
    }
    
    // New appointment notification to provider
    public void sendNewAppointmentNotification(String providerEmail, String providerName, 
                                             String customerName, String appointmentDateTime, String serviceType) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("providerName", providerName);
        variables.put("customerName", customerName);
        variables.put("appointmentDateTime", appointmentDateTime);
        variables.put("serviceType", serviceType);
        
        sendTemplatedEmail(providerEmail,
            "New Appointment Booking - Booking System",
            "new-appointment-provider",
            variables);
    }

    public void sendAppointmentReschedule(String customerEmail, String customerName,
                                         String providerName, String oldDateTime, 
                                         String newDateTime, String serviceType) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("providerName", providerName);
        variables.put("oldDateTime", oldDateTime);
        variables.put("newDateTime", newDateTime);
        variables.put("serviceType", serviceType);
        
        sendTemplatedEmail(customerEmail,
            "Appointment Rescheduled - Booking System",
            "appointment-reschedule",
            variables);
    }

    public void sendAppointmentCancellationToProvider(String providerEmail, String providerName,
                                                     String customerName, String appointmentDateTime, String serviceType) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("providerName", providerName);
        variables.put("customerName", customerName);
        variables.put("appointmentDateTime", appointmentDateTime);
        variables.put("serviceType", serviceType);
        
        sendTemplatedEmail(providerEmail,
            "Appointment Cancelled - Booking System",
            "appointment-cancellation-provider",
            variables);
    }

    public void sendAppointmentCompletion(String customerEmail, String customerName,
                                         String providerName, String appointmentDateTime, String serviceType) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("providerName", providerName);
        variables.put("appointmentDateTime", appointmentDateTime);
        variables.put("serviceType", serviceType);
        
        sendTemplatedEmail(customerEmail,
            "Appointment Completed - Thank You!",
            "appointment-completion",
            variables);
    }
}