package com.appointment.booking.repository;

import com.appointment.booking.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findByApprovedTrue();
    List<ServiceProvider> findByApprovedFalse();  // YEH NAYA METHOD
    List<ServiceProvider> findByServiceTypeAndApprovedTrue(String serviceType);
}