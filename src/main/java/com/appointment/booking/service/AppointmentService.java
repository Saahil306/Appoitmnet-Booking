package com.appointment.booking.service;

import com.appointment.booking.dto.AppointmentResponseDTO;
import com.appointment.booking.entity.*;
import com.appointment.booking.repository.AppointmentRepository;
import com.appointment.booking.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    // Book appointment - WITH IMPROVED AVAILABILITY CHECK
    public AppointmentResponseDTO bookAppointment(Long customerId, Long providerId, LocalDateTime appointmentDateTime, int duration, String serviceType) {
        Customer customer = (Customer) userService.getUserById(customerId);
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        
        if (!provider.isApproved()) {
            throw new RuntimeException("Service provider is not approved");
        }

        // ✅ IMPROVED: Use AvailabilityService for availability check
        if (!availabilityService.isProviderAvailable(providerId, appointmentDateTime, duration)) {
            // Get available slots for better error message
            List<String> availableSlots = availabilityService.getAvailableTimeSlots(providerId, appointmentDateTime);
            String errorMsg = "Time slot not available. Please choose a different time. ";
            
            if (!availableSlots.isEmpty()) {
                errorMsg += "Available slots for this day: " + String.join(", ", availableSlots);
            } else {
                DayOfWeek dayOfWeek = appointmentDateTime.getDayOfWeek();
                errorMsg += "No availability set for " + dayOfWeek + ".";
            }
            
            throw new RuntimeException(errorMsg);
        }
        
        // Check for conflicting appointments
        List<Appointment> conflicts = appointmentRepository.findByServiceProviderAndAppointmentDateTimeBetween(
            provider, appointmentDateTime, appointmentDateTime.plusMinutes(duration));
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot not available - Another appointment exists at this time");
        }
        
        Appointment appointment = new Appointment(customer, provider, appointmentDateTime, duration, serviceType);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send notification
        sendAppointmentNotification(customer, provider, savedAppointment, "APPOINTMENT_BOOKED");
        
        String formattedDateTime = formatDateTimeForEmail(appointmentDateTime);
        
        // ✅ SEND CONFIRMATION EMAIL
        try {
            emailService.sendAppointmentConfirmation(
                customer.getEmail(),
                customer.getFirstName() + " " + customer.getLastName(),
                provider.getFirstName() + " " + provider.getLastName(),
                formattedDateTime,
                serviceType,
                duration
            );
            
            // Also send email to provider
            emailService.sendNewAppointmentNotification(
                provider.getEmail(),
                provider.getFirstName() + " " + provider.getLastName(),
                customer.getFirstName() + " " + customer.getLastName(),
                formattedDateTime,
                serviceType
            );
            
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
        
        // ✅ SEND CONFIRMATION SMS
        try {
            // Customer ko SMS
            smsService.sendAppointmentConfirmationSms(
                customer.getPhone(),
                customer.getFirstName() + " " + customer.getLastName(),
                provider.getFirstName() + " " + provider.getLastName(),
                formattedDateTime
            );
            
            // Provider ko SMS
            smsService.sendNewAppointmentSmsToProvider(
                provider.getPhone(),
                provider.getFirstName() + " " + provider.getLastName(),
                customer.getFirstName() + " " + customer.getLastName(),
                formattedDateTime
            );
            
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
        
        return convertToDTO(savedAppointment);
    }

    // Get customer appointments - DTO return karo
    public List<AppointmentResponseDTO> getCustomerAppointments(Long customerId) {
        Customer customer = (Customer) userService.getUserById(customerId);
        List<Appointment> appointments = appointmentRepository.findByCustomerOrderByAppointmentDateTimeDesc(customer);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Get provider appointments - DTO return karo
    public List<AppointmentResponseDTO> getProviderAppointments(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        List<Appointment> appointments = appointmentRepository.findByServiceProviderOrderByAppointmentDateTimeDesc(provider);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Convert Appointment to DTO
    private AppointmentResponseDTO convertToDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
            appointment.getId(),
            appointment.getCustomer().getId(),
            appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
            appointment.getServiceProvider().getId(),
            appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
            appointment.getServiceType(),
            appointment.getAppointmentDateTime(),
            appointment.getEndDateTime(),
            appointment.getDuration(),
            appointment.getStatus(),
            appointment.getNotes(),
            appointment.getCreatedAt(),
            appointment.getUpdatedAt()
        );
    }
    
    // Reschedule appointment - WITH IMPROVED AVAILABILITY CHECK
    public AppointmentResponseDTO rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        // ✅ IMPROVED: Use AvailabilityService for availability check
        if (!availabilityService.isProviderAvailable(appointment.getServiceProvider().getId(), newDateTime, appointment.getDuration())) {
            List<String> availableSlots = availabilityService.getAvailableTimeSlots(appointment.getServiceProvider().getId(), newDateTime);
            String errorMsg = "Time slot not available for reschedule. Please choose a different time.";
            
            if (!availableSlots.isEmpty()) {
                errorMsg += " Available slots: " + String.join(", ", availableSlots);
            }
            
            throw new RuntimeException(errorMsg);
        }

        // Check for conflicts (excluding current appointment)
        List<Appointment> conflicts = appointmentRepository.findByServiceProviderAndAppointmentDateTimeBetween(
            appointment.getServiceProvider(), newDateTime, newDateTime.plusMinutes(appointment.getDuration()));
        
        conflicts = conflicts.stream()
                .filter(conflict -> !conflict.getId().equals(appointmentId))
                .collect(Collectors.toList());
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot not available - Another appointment exists at this time");
        }
        
        LocalDateTime oldDateTime = appointment.getAppointmentDateTime();
        appointment.setAppointmentDateTime(newDateTime);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Send notification
        sendAppointmentNotification(appointment.getCustomer(), appointment.getServiceProvider(), 
                                  updatedAppointment, "APPOINTMENT_RESCHEDULED");
        
        String oldFormattedDateTime = formatDateTimeForEmail(oldDateTime);
        String newFormattedDateTime = formatDateTimeForEmail(newDateTime);
        
        // ✅ SEND RESCHEDULE EMAIL
        try {
            emailService.sendAppointmentReschedule(
                appointment.getCustomer().getEmail(),
                appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                oldFormattedDateTime,
                newFormattedDateTime,
                appointment.getServiceType()
            );
            
        } catch (Exception e) {
            System.err.println("Failed to send reschedule email: " + e.getMessage());
        }
        
        // ✅ SEND RESCHEDULE SMS
        try {
            smsService.sendAppointmentRescheduleSms(
                appointment.getCustomer().getPhone(),
                appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                newFormattedDateTime
            );
            
        } catch (Exception e) {
            System.err.println("Failed to send reschedule SMS: " + e.getMessage());
        }
        
        return convertToDTO(updatedAppointment);
    }
    
    // Cancel appointment
    public AppointmentResponseDTO cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        
        // Send notification
        sendAppointmentNotification(appointment.getCustomer(), appointment.getServiceProvider(), 
                                  cancelledAppointment, "APPOINTMENT_CANCELLED");
        
        String formattedDateTime = formatDateTimeForEmail(appointment.getAppointmentDateTime());
        
        // ✅ SEND CANCELLATION EMAIL
        try {
            emailService.sendAppointmentCancellation(
                appointment.getCustomer().getEmail(),
                appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                formattedDateTime,
                appointment.getServiceType()
            );
            
            // Also notify provider
            emailService.sendAppointmentCancellationToProvider(
                appointment.getServiceProvider().getEmail(),
                appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                formattedDateTime,
                appointment.getServiceType()
            );
            
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
        
        // ✅ SEND CANCELLATION SMS
        try {
            smsService.sendAppointmentCancellationSms(
                appointment.getCustomer().getPhone(),
                appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                formattedDateTime
            );
            
        } catch (Exception e) {
            System.err.println("Failed to send cancellation SMS: " + e.getMessage());
        }
        
        return convertToDTO(cancelledAppointment);
    }
    
    // Update appointment status
    public AppointmentResponseDTO updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // Send notification
        sendAppointmentNotification(appointment.getCustomer(), appointment.getServiceProvider(), 
                                  updatedAppointment, "APPOINTMENT_STATUS_UPDATED");
        
        String formattedDateTime = formatDateTimeForEmail(appointment.getAppointmentDateTime());
        
        // ✅ SEND STATUS UPDATE EMAIL
        try {
            if (status == AppointmentStatus.CONFIRMED && oldStatus == AppointmentStatus.PENDING) {
                emailService.sendAppointmentConfirmation(
                    appointment.getCustomer().getEmail(),
                    appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                    appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                    formattedDateTime,
                    appointment.getServiceType(),
                    appointment.getDuration()
                );
            } else if (status == AppointmentStatus.COMPLETED) {
                emailService.sendAppointmentCompletion(
                    appointment.getCustomer().getEmail(),
                    appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                    appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName(),
                    formattedDateTime,
                    appointment.getServiceType()
                );
                
                // ✅ SEND COMPLETION SMS
                try {
                    smsService.sendAppointmentCompletionSms(
                        appointment.getCustomer().getPhone(),
                        appointment.getCustomer().getFirstName() + " " + appointment.getCustomer().getLastName(),
                        appointment.getServiceProvider().getFirstName() + " " + appointment.getServiceProvider().getLastName()
                    );
                } catch (Exception smsEx) {
                    System.err.println("Failed to send completion SMS: " + smsEx.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Failed to send status update email: " + e.getMessage());
        }
        
        return convertToDTO(updatedAppointment);
    }
    
    // Get appointment by ID
    public AppointmentResponseDTO getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return convertToDTO(appointment);
    }
    
    // Get appointments by status
    public List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByStatus(status);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // PRIVATE METHOD TO SEND NOTIFICATIONS
    private void sendAppointmentNotification(Customer customer, ServiceProvider provider, 
                                           Appointment appointment, String type) {
        String message = "";
        String title = "";
        
        switch (type) {
            case "APPOINTMENT_BOOKED":
                title = "New Appointment Booked";
                message = String.format("Appointment booked with %s on %s", 
                    customer.getFirstName(), formatDateTimeForEmail(appointment.getAppointmentDateTime()));
                break;
            case "APPOINTMENT_RESCHEDULED":
                title = "Appointment Rescheduled";
                message = String.format("Appointment rescheduled to %s", 
                    formatDateTimeForEmail(appointment.getAppointmentDateTime()));
                break;
            case "APPOINTMENT_CANCELLED":
                title = "Appointment Cancelled";
                message = "Your appointment has been cancelled";
                break;
            case "APPOINTMENT_STATUS_UPDATED":
                title = "Appointment Status Updated";
                message = String.format("Appointment status changed to %s", 
                    appointment.getStatus());
                break;
        }
        
        // Notify customer
        Notification customerNotification = new Notification(customer, title, message, 
            NotificationType.APPOINTMENT_CONFIRMATION);
        notificationRepository.save(customerNotification);
        
        // Notify provider
        Notification providerNotification = new Notification(provider, title, message, 
            NotificationType.APPOINTMENT_CONFIRMATION);
        notificationRepository.save(providerNotification);
    }

    // ✅ METHOD: Format datetime for email/SMS
    private String formatDateTimeForEmail(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");
        return dateTime.format(formatter);
    }
    
    public Appointment getAppointmentEntityById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    // ✅ NEW METHOD: Check availability for specific provider and time
    public String checkAvailability(Long providerId, LocalDateTime dateTime, int duration) {
        try {
            boolean isAvailable = availabilityService.isProviderAvailable(providerId, dateTime, duration);
            List<String> availableSlots = availabilityService.getAvailableTimeSlots(providerId, dateTime);
            
            if (isAvailable) {
                return "Time slot is available. Available slots: " + String.join(", ", availableSlots);
            } else {
                return "Time slot not available. Available slots for this day: " + String.join(", ", availableSlots);
            }
        } catch (Exception e) {
            return "Error checking availability: " + e.getMessage();
        }
    }
}