package com.appointment.booking.dto;

import com.appointment.booking.entity.AppointmentStatus;
import java.time.LocalDateTime;

public class AppointmentResponseDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long providerId;
    private String providerName;
    private String serviceType;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime endDateTime;
    private int duration;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AppointmentResponseDTO() {}

    public AppointmentResponseDTO(Long id, Long customerId, String customerName, Long providerId, 
                                 String providerName, String serviceType, LocalDateTime appointmentDateTime,
                                 LocalDateTime endDateTime, int duration, AppointmentStatus status, 
                                 String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.providerId = providerId;
        this.providerName = providerName;
        this.serviceType = serviceType;
        this.appointmentDateTime = appointmentDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}