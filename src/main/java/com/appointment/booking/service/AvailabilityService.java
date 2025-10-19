package com.appointment.booking.service;

import com.appointment.booking.entity.Availability;
import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AvailabilityService {
    
    @Autowired
    private AvailabilityRepository availabilityRepository;
    
    @Autowired
    private UserService userService;
    
    // Set availability for provider - FIXED VERSION
    public List<Availability> setAvailability(Long providerId, List<Availability> availabilities) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        
        // Get existing availabilities
        List<Availability> existingAvailabilities = availabilityRepository.findByServiceProvider(provider);
        System.out.println("Found " + existingAvailabilities.size() + " existing availabilities to delete");
        
        // Delete existing availabilities
        if (!existingAvailabilities.isEmpty()) {
            availabilityRepository.deleteAll(existingAvailabilities);
            availabilityRepository.flush(); // Force immediate delete
            System.out.println("Deleted existing availabilities");
        }
        
        // Set provider for each availability slot
        for (Availability availability : availabilities) {
            availability.setServiceProvider(provider);
            availability.setId(null); // Ensure new entities are created
        }
        
        // Save new availabilities
        List<Availability> savedAvailabilities = availabilityRepository.saveAll(availabilities);
        System.out.println("Saved " + savedAvailabilities.size() + " new availabilities");
        
        return savedAvailabilities;
    }
    
    // Get provider availability
    public List<Availability> getProviderAvailability(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        return availabilityRepository.findByServiceProvider(provider);
    }
    
    // ✅ IMPROVED: Check if provider is available at specific date/time
    public boolean isProviderAvailable(Long providerId, LocalDateTime requestedDateTime, int duration) {
        try {
            ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
            
            // Get day of week from requested date
            DayOfWeek dayOfWeek = requestedDateTime.getDayOfWeek();
            
            // Get provider's availability for this day
            List<Availability> dayAvailabilities = availabilityRepository.findByServiceProviderAndDayOfWeek(provider, dayOfWeek);
            
            System.out.println("Checking availability for " + dayOfWeek + ": " + dayAvailabilities.size() + " slots found");
            
            if (dayAvailabilities.isEmpty()) {
                System.out.println("Provider is not available on " + dayOfWeek);
                return false;
            }

            // Convert requested time to LocalTime
            LocalTime requestedTime = requestedDateTime.toLocalTime();
            LocalTime requestedEndTime = requestedTime.plusMinutes(duration);

            // Check if requested time falls within any available slot
            for (Availability availability : dayAvailabilities) {
                if (!availability.isAvailable()) {
                    continue; // Skip unavailable slots
                }
                
                LocalTime slotStart = availability.getStartTime();
                LocalTime slotEnd = availability.getEndTime();

                System.out.println("Checking slot: " + slotStart + " - " + slotEnd + " for request: " + requestedTime + " - " + requestedEndTime);

                // Check if requested time is within this availability slot
                boolean isWithinSlot = !requestedTime.isBefore(slotStart) && !requestedEndTime.isAfter(slotEnd);
                
                if (isWithinSlot) {
                    System.out.println("✅ Time slot available!");
                    return true;
                }
            }

            System.out.println("❌ No available time slot found for the requested time");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error checking availability: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ NEW: Get available time slots for a provider on specific date
    public List<String> getAvailableTimeSlots(Long providerId, LocalDateTime date) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        
        List<Availability> dayAvailabilities = availabilityRepository.findByServiceProviderAndDayOfWeek(provider, dayOfWeek);
        
        return dayAvailabilities.stream()
                .filter(Availability::isAvailable)
                .map(avail -> avail.getStartTime() + " - " + avail.getEndTime())
                .collect(java.util.stream.Collectors.toList());
    }
}