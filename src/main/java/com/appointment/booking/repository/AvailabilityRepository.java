package com.appointment.booking.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.appointment.booking.entity.Availability;
import com.appointment.booking.entity.ServiceProvider;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByServiceProvider(ServiceProvider serviceProvider);
    List<Availability> findByServiceProviderAndDayOfWeek(ServiceProvider serviceProvider, DayOfWeek dayOfWeek);
    void deleteByServiceProvider(ServiceProvider serviceProvider);
    
    @Modifying
    @Query(value = "DELETE FROM availabilities WHERE provider_id = :providerId", nativeQuery = true)
    void deleteByServiceProviderId(@Param("providerId") Long providerId);
}