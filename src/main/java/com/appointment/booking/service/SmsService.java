package com.appointment.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}") 
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

   
    public SmsService() {
        
    }

    @PostConstruct
    public void init() {
        try {
            Twilio.init(accountSid, authToken);
            System.out.println("Twilio initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize Twilio: " + e.getMessage());
        }
    }

    
    public void sendSms(String toPhoneNumber, String messageBody) {
        try {
            
            String formattedTo = formatPhoneNumber(toPhoneNumber);
            
            Message message = Message.creator(
                new PhoneNumber(formattedTo),
                new PhoneNumber(fromPhoneNumber), 
                messageBody
            ).create();

            System.out.println("SMS sent successfully! SID: " + message.getSid());
            System.out.println("To: " + formattedTo);
            System.out.println("Message: " + messageBody);
            
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            
        }
    }

    
    public void sendAppointmentConfirmationSms(String customerPhone, String customerName, 
                                              String providerName, String appointmentDateTime) {
        String message = String.format(
            "Hi %s, your appointment with %s is confirmed for %s. Thank you for choosing our service!",
            customerName, providerName, appointmentDateTime
        );
        sendSms(customerPhone, message);
    }

    public void sendAppointmentReminderSms(String customerPhone, String customerName,
                                          String providerName, String appointmentDateTime) {
        String message = String.format(
            "Reminder: Your appointment with %s is tomorrow at %s. Please be on time!",
            providerName, appointmentDateTime
        );
        sendSms(customerPhone, message);
    }

    public void sendAppointmentCancellationSms(String customerPhone, String customerName,
                                              String providerName, String appointmentDateTime) {
        String message = String.format(
            "Hi %s, your appointment with %s on %s has been cancelled as requested.",
            customerName, providerName, appointmentDateTime
        );
        sendSms(customerPhone, message);
    }

    public void sendAppointmentRescheduleSms(String customerPhone, String customerName,
                                            String providerName, String newDateTime) {
        String message = String.format(
            "Hi %s, your appointment with %s has been rescheduled to %s.",
            customerName, providerName, newDateTime
        );
        sendSms(customerPhone, message);
    }

    
    public void sendNewAppointmentSmsToProvider(String providerPhone, String providerName,
                                               String customerName, String appointmentDateTime) {
        String message = String.format(
            "Hi %s, you have a new appointment with %s on %s. Please check your dashboard.",
            providerName, customerName, appointmentDateTime
        );
        sendSms(providerPhone, message);
    }

   
    private String formatPhoneNumber(String phoneNumber) {
       
        String digitsOnly = phoneNumber.replaceAll("[^0-9]", "");
        
        
        if (!phoneNumber.startsWith("+")) {
            
            if (digitsOnly.length() == 10) {
                return "+91" + digitsOnly;
            }
        }
        
        return phoneNumber;
    }

   
    public void sendTestSms(String toPhoneNumber) {
        String testMessage = "Test SMS from Appointment Booking System. If you received this, SMS is working!";
        sendSms(toPhoneNumber, testMessage);
    }
    
 
    public void sendAppointmentCompletionSms(String customerPhone, String customerName,
                                            String providerName) {
        String message = String.format(
            "Hi %s, thank you for your appointment with %s. We hope you had a great experience! Please consider leaving a review.",
            customerName, providerName
        );
        sendSms(customerPhone, message);
    }
}