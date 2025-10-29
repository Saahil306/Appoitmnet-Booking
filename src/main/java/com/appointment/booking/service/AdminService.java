package com.appointment.booking.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.booking.entity.Rating;
import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.entity.User;
import com.appointment.booking.repository.AppointmentRepository;
import com.appointment.booking.repository.RatingRepository;
import com.appointment.booking.repository.ServiceProviderRepository;
import com.appointment.booking.repository.UserRepository;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private RatingRepository ratingRepository;

    
    public ServiceProvider approveServiceProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));
        
        provider.setApproved(true);
        return serviceProviderRepository.save(provider);
    }
    
    
    public void rejectServiceProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found"));
        
        serviceProviderRepository.delete(provider);
    }
    
    
    public List<ServiceProvider> getPendingProviders() {
        return serviceProviderRepository.findByApprovedFalse();
    }
    
  
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    
    public SystemStats getSystemStats() {
        long totalUsers = userRepository.count();
        long totalAppointments = appointmentRepository.count();
        long totalProviders = serviceProviderRepository.count();
        long pendingProviders = serviceProviderRepository.findByApprovedFalse().size();
        
        
        Long totalRatings = ratingRepository.getTotalRatingCount();
        Double averageRating = ratingRepository.findOverallAverageRating();
        
        return new SystemStats(totalUsers, totalAppointments, totalProviders, pendingProviders, totalRatings, averageRating);
    }

  
    public List<Rating> getAllRatings() {
        return ratingRepository.findAllByOrderByCreatedAtDesc();
    }

    
    public List<Rating> searchRatings(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ratingRepository.findAllByOrderByCreatedAtDesc();
        }
        return ratingRepository.searchRatings(searchTerm.trim());
    }

    
    public Map<String, Object> getRatingStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalRatings = ratingRepository.getTotalRatingCount();
        Double averageRating = ratingRepository.findOverallAverageRating();
        
        stats.put("totalRatings", totalRatings != null ? totalRatings : 0);
        stats.put("averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
        
        
        List<Rating> allRatings = ratingRepository.findAll();
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            long count = allRatings.stream().filter(r -> r.getRating() == rating).count();
            ratingDistribution.put(rating, count);
        }
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }

    
    public List<User> searchUsers(String searchTerm, String role, Boolean active) {
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
                .filter(user -> matchesSearchTerm(user, searchTerm))
                .filter(user -> matchesRole(user, role))
                .filter(user -> matchesActiveStatus(user, active))
                .collect(Collectors.toList()); // ✅ FIXED: toList() -> collect(Collectors.toList())
    }

    private boolean matchesSearchTerm(User user, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return user.getFirstName().toLowerCase().contains(lowerSearchTerm) ||
               user.getLastName().toLowerCase().contains(lowerSearchTerm) ||
               user.getEmail().toLowerCase().contains(lowerSearchTerm);
    }

    private boolean matchesRole(User user, String role) {
        if (role == null || role.trim().isEmpty() || "ALL".equalsIgnoreCase(role)) {
            return true;
        }
        return user.getRole().toString().equalsIgnoreCase(role);
    }

    private boolean matchesActiveStatus(User user, Boolean active) {
        if (active == null) {
            return true;
        }
        return user.isActive() == active;
    }

    
    public static class SystemStats {
        public long totalUsers;
        public long totalAppointments;
        public long totalProviders;
        public long pendingProviders;
        public long totalRatings;
        public double averageRating;
        
        public SystemStats(long totalUsers, long totalAppointments, long totalProviders, long pendingProviders, long totalRatings, double averageRating) {
            this.totalUsers = totalUsers;
            this.totalAppointments = totalAppointments;
            this.totalProviders = totalProviders;
            this.pendingProviders = pendingProviders;
            this.totalRatings = totalRatings;
            this.averageRating = averageRating;
        }
    }
}