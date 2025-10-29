package com.appointment.booking.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_providers")
@PrimaryKeyJoinColumn(name = "user_id")
public class ServiceProvider extends User {
    private String serviceType;
    private String qualification;
    private String experience;
    private String bio;
    private double hourlyRate;
    private boolean approved;
    
    @OneToMany(mappedBy = "serviceProvider")
    @JsonIgnore  // ADD THIS LINE
    private List<Appointment> appointments = new ArrayList<>();
    
    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL)
    @JsonIgnore  // ADD THIS LINE
    private List<Availability> availabilities = new ArrayList<>();
    
    public ServiceProvider() {}
    
    public ServiceProvider(String email, String password, String firstName, String lastName, String phone, String serviceType) {
        super(email, password, firstName, lastName, phone, UserRole.SERVICE_PROVIDER);
        this.serviceType = serviceType;
        this.approved = false;
    }
    
   
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
    
    public List<Availability> getAvailabilities() { return availabilities; }
    public void setAvailabilities(List<Availability> availabilities) { this.availabilities = availabilities; }
}
