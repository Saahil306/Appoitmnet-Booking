package com.appointment.booking.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore  
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    @JsonIgnore  
    private ServiceProvider serviceProvider;
    
    private LocalDateTime appointmentDateTime;
    private LocalDateTime endDateTime;
    private int duration; 
    
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    
    private String serviceType;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    
    public Appointment() {}
    
    public Appointment(Customer customer, ServiceProvider serviceProvider, LocalDateTime appointmentDateTime, int duration, String serviceType) {
        this.customer = customer;
        this.serviceProvider = serviceProvider;
        this.appointmentDateTime = appointmentDateTime;
        this.duration = duration;
        this.serviceType = serviceType;
        this.status = AppointmentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.endDateTime = appointmentDateTime.plusMinutes(duration);
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public ServiceProvider getServiceProvider() { return serviceProvider; }
    public void setServiceProvider(ServiceProvider serviceProvider) { this.serviceProvider = serviceProvider; }
    
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { 
        this.appointmentDateTime = appointmentDateTime;
        this.endDateTime = appointmentDateTime.plusMinutes(duration);
    }
    
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { 
        this.duration = duration;
        if (appointmentDateTime != null) {
            this.endDateTime = appointmentDateTime.plusMinutes(duration);
        }
    }
    
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
