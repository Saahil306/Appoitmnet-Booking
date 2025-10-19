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

        // Check if appointment belongs to this customer and provider
        if (!appointment.getCustomer().getId().equals(customerId) || 
            !appointment.getServiceProvider().getId().equals(providerId)) {
            throw new RuntimeException("Invalid appointment for rating");
        }

        // Check if appointment is completed
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Can only rate completed appointments");
        }

        // Check if already rated
        if (ratingRepository.existsByAppointmentId(appointmentId)) {
            throw new RuntimeException("Appointment already rated");
        }

        Rating newRating = new Rating(customer, provider, appointment, rating, comment);
        return ratingRepository.save(newRating);
    }

    // Get ratings for a provider
    public List<Rating> getProviderRatings(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        return ratingRepository.findByServiceProviderOrderByCreatedAtDesc(provider);
    }

    // Get average rating for a provider
    public Double getProviderAverageRating(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        Double average = ratingRepository.findAverageRatingByServiceProvider(provider);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0; // Round to 1 decimal
    }

    // Get rating count for a provider
    public Long getProviderRatingCount(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        return ratingRepository.countByServiceProvider(provider);
    }

    // Get rating by appointment
    public Rating getRatingByAppointment(Long appointmentId) {
        List<Rating> ratings = ratingRepository.findByAppointmentId(appointmentId);
        return ratings.isEmpty() ? null : ratings.get(0);
    }

    // Helper method for appointment service
    public Appointment getAppointmentEntityById(Long appointmentId) {
        // This method should be in AppointmentService, adding here for completeness
        // You'll need to add this method to your AppointmentService
        return null; // Placeholder
    }
}