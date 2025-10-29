package com.appointment.booking.entity;

import java.time.LocalDateTime;

import com.appointment.booking.entity.Appointment;
import com.appointment.booking.entity.Customer;
import com.appointment.booking.entity.ServiceProvider;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ServiceProvider serviceProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private Integer rating; // 1 to 5
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
    public Rating() {}

    public Rating(Customer customer, ServiceProvider serviceProvider, Appointment appointment, Integer rating, String comment) {
        this.customer = customer;
        this.serviceProvider = serviceProvider;
        this.appointment = appointment;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { 
        if (rating < 1) rating = 1;
        if (rating > 5) rating = 5;
        this.rating = rating; 
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}