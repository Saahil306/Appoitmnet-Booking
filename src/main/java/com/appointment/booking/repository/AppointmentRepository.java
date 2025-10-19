package com.appointment.booking.repository;

import com.appointment.booking.entity.Appointment;
import com.appointment.booking.entity.AppointmentStatus;
import com.appointment.booking.entity.Customer;
import com.appointment.booking.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByCustomerOrderByAppointmentDateTimeDesc(Customer customer);
    List<Appointment> findByServiceProviderOrderByAppointmentDateTimeDesc(ServiceProvider serviceProvider);
    List<Appointment> findByCustomerAndStatus(Customer customer, AppointmentStatus status);
    List<Appointment> findByServiceProviderAndStatus(ServiceProvider serviceProvider, AppointmentStatus status);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByServiceProviderAndAppointmentDateTimeBetween(ServiceProvider provider, LocalDateTime start, LocalDateTime end);
}