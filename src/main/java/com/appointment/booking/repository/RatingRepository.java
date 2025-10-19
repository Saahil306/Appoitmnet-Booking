package com.appointment.booking.repository;

import com.appointment.booking.entity.Rating;
import com.appointment.booking.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByServiceProvider(ServiceProvider serviceProvider);
    List<Rating> findByServiceProviderOrderByCreatedAtDesc(ServiceProvider serviceProvider);
    List<Rating> findByAppointmentId(Long appointmentId);
    boolean existsByAppointmentId(Long appointmentId);
    
    // ADD THESE METHODS FOR ADMIN
    List<Rating> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT AVG(r.rating) FROM Rating r")
    Double findOverallAverageRating();
    
    @Query("SELECT COUNT(r) FROM Rating r")
    Long getTotalRatingCount();
    
    @Query("SELECT r FROM Rating r WHERE r.customer.firstName LIKE %:search% OR r.customer.lastName LIKE %:search% OR r.serviceProvider.firstName LIKE %:search% OR r.serviceProvider.lastName LIKE %:search% OR r.comment LIKE %:search%")
    List<Rating> searchRatings(@Param("search") String search);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.serviceProvider = :serviceProvider")
    Double findAverageRatingByServiceProvider(@Param("serviceProvider") ServiceProvider serviceProvider);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.serviceProvider = :serviceProvider")
    Long countByServiceProvider(@Param("serviceProvider") ServiceProvider serviceProvider);
}