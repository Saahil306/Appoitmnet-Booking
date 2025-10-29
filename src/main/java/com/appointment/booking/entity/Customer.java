package com.appointment.booking.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User {
    private String address;
    private String city;
    private String state;
    private String zipCode;
    
    @OneToMany(mappedBy = "customer")
    @JsonIgnore  
    private List<Appointment> appointments = new ArrayList<>();
    
    public Customer() {}
    
    public Customer(String email, String password, String firstName, String lastName, String phone) {
        super(email, password, firstName, lastName, phone, UserRole.CUSTOMER);
    }
    
    
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
}

