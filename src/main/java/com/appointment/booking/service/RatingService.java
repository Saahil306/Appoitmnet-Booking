package com.appointment.booking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appointment.booking.entity.Appointment;
import com.appointment.booking.entity.AppointmentStatus;
import com.appointment.booking.entity.Customer;
import com.appointment.booking.entity.Rating;
import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.repository.RatingRepository;

@Service
@Transactional
public class RatingService {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AppointmentService appointmentService;

    // Submit a rating
    public Rating submitRating(Long customerId, Long providerId, Long appointmentId, Integer rating, String comment) {
        Customer customer = (Customer) userService.getUserById(customerId);
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        Appointment appointment = appointmentService.getAppointmentEntityById(appointmentId);

       
        if (!appointment.getCustomer().getId().equals(customerId) || 
            !appointment.getServiceProvider().getId().equals(providerId)) {
            throw new RuntimeException("Invalid appointment for rating");
        }

        
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Can only rate completed appointments");
        }

       
        if (ratingRepository.existsByAppointmentId(appointmentId)) {
            throw new RuntimeException("Appointment already rated");
        }

        Rating newRating = new Rating(customer, provider, appointment, rating, comment);
        return ratingRepository.save(newRating);
    }

   
    public List<Rating> getProviderRatings(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        return ratingRepository.findByServiceProviderOrderByCreatedAtDesc(provider);
    }

    
    public Double getProviderAverageRating(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        Double average = ratingRepository.findAverageRatingByServiceProvider(provider);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0; 
    }

    
    public Long getProviderRatingCount(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        return ratingRepository.countByServiceProvider(provider);
    }

    
    public Rating getRatingByAppointment(Long appointmentId) {
        List<Rating> ratings = ratingRepository.findByAppointmentId(appointmentId);
        return ratings.isEmpty() ? null : ratings.get(0);
    }

    
    public Appointment getAppointmentEntityById(Long appointmentId) {
        
        return null; 
    }
}