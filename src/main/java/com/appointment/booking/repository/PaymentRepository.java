package com.appointment.booking.repository;

import com.appointment.booking.entity.Payment;
import com.appointment.booking.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByAppointmentId(Long appointmentId);
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.customer.id = :customerId")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT p FROM Payment p WHERE p.appointment.serviceProvider.id = :providerId")
    List<Payment> findByProviderId(@Param("providerId") Long providerId);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.appointment.serviceProvider.id = :providerId AND p.status = 'SUCCESSFUL'")
    Double getTotalEarningsByProvider(@Param("providerId") Long providerId);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") PaymentStatus status);
    
    List<Payment> findTop10ByOrderByCreatedAtDesc();
}