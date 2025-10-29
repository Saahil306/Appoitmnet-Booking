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
    
   
    public List<Availability> setAvailability(Long providerId, List<Availability> availabilities) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        
        
        List<Availability> existingAvailabilities = availabilityRepository.findByServiceProvider(provider);
        System.out.println("Found " + existingAvailabilities.size() + " existing availabilities to delete");
        
       
        if (!existingAvailabilities.isEmpty()) {
            availabilityRepository.deleteAll(existingAvailabilities);
            availabilityRepository.flush(); // Force immediate delete
            System.out.println("Deleted existing availabilities");
        }
        
       
        for (Availability availability : availabilities) {
            availability.setServiceProvider(provider);
            availability.setId(null); // Ensure new entities are created
        }
        
        
        List<Availability> savedAvailabilities = availabilityRepository.saveAll(availabilities);
        System.out.println("Saved " + savedAvailabilities.size() + " new availabilities");
        
        return savedAvailabilities;
    }
    
    
    public List<Availability> getProviderAvailability(Long providerId) {
        ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
        return availabilityRepository.findByServiceProvider(provider);
    }
    
    
    public boolean isProviderAvailable(Long providerId, LocalDateTime requestedDateTime, int duration) {
        try {
            ServiceProvider provider = (ServiceProvider) userService.getUserById(providerId);
            
            
            DayOfWeek dayOfWeek = requestedDateTime.getDayOfWeek();
            
            
            List<Availability> dayAvailabilities = availabilityRepository.findByServiceProviderAndDayOfWeek(provider, dayOfWeek);
            
            System.out.println("Checking availability for " + dayOfWeek + ": " + dayAvailabilities.size() + " slots found");
            
            if (dayAvailabilities.isEmpty()) {
                System.out.println("Provider is not available on " + dayOfWeek);
                return false;
            }

            
            LocalTime requestedTime = requestedDateTime.toLocalTime();
            LocalTime requestedEndTime = requestedTime.plusMinutes(duration);

            
            for (Availability availability : dayAvailabilities) {
                if (!availability.isAvailable()) {
                    continue; 
                }
                
                LocalTime slotStart = availability.getStartTime();
                LocalTime slotEnd = availability.getEndTime();

                System.out.println("Checking slot: " + slotStart + " - " + slotEnd + " for request: " + requestedTime + " - " + requestedEndTime);

                
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