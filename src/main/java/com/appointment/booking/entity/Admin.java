package com.appointment.booking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    private String department;
    
    public Admin() {}
    
    public Admin(String email, String password, String firstName, String lastName, String phone) {
        super(email, password, firstName, lastName, phone, UserRole.ADMIN);
    }
    
    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}