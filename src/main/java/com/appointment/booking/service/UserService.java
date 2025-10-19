package com.appointment.booking.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.booking.entity.Admin;
import com.appointment.booking.entity.Customer;
import com.appointment.booking.entity.ServiceProvider;
import com.appointment.booking.entity.User;
import com.appointment.booking.repository.AdminRepository;
import com.appointment.booking.repository.CustomerRepository;
import com.appointment.booking.repository.ServiceProviderRepository;
import com.appointment.booking.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    // User Registration
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }
    
    // Customer Registration
    public Customer registerCustomer(Customer customer) {
        if (userRepository.existsByEmail(customer.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return customerRepository.save(customer);
    }
    
    // Service Provider Registration
    public ServiceProvider registerServiceProvider(ServiceProvider serviceProvider) {
        if (userRepository.existsByEmail(serviceProvider.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return serviceProviderRepository.save(serviceProvider);
    }
    
    // Admin Registration
    public Admin registerAdmin(Admin admin) {
        if (userRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return adminRepository.save(admin);
    }
    
    // User Login
    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password) && user.get().isActive()) {
            return user.get();
        }
        throw new RuntimeException("Invalid credentials or inactive account");
    }
    
    // Get user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // Update user profile
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        return userRepository.save(user);
    }
    
    // Get all service providers
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.findByApprovedTrue();
    }
    
    // Get service providers by type
    public List<ServiceProvider> getServiceProvidersByType(String serviceType) {
        return serviceProviderRepository.findByServiceTypeAndApprovedTrue(serviceType);
    }
    
    // Deactivate user
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }
    
    // Activate user
    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        userRepository.save(user);
    }
    
    // Advanced search for service providers
    public List<ServiceProvider> searchServiceProviders(String searchTerm, String serviceType, Boolean availableToday) {
        List<ServiceProvider> providers = serviceProviderRepository.findByApprovedTrue();
        
        // Apply filters
        return providers.stream()
                .filter(provider -> matchesSearchTerm(provider, searchTerm))
                .filter(provider -> matchesServiceType(provider, serviceType))
                .collect(Collectors.toList());
    }

    private boolean matchesSearchTerm(ServiceProvider provider, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return provider.getFirstName().toLowerCase().contains(lowerSearchTerm) ||
               provider.getLastName().toLowerCase().contains(lowerSearchTerm) ||
               provider.getServiceType().toLowerCase().contains(lowerSearchTerm) ||
               (provider.getBio() != null && provider.getBio().toLowerCase().contains(lowerSearchTerm));
    }

    private boolean matchesServiceType(ServiceProvider provider, String serviceType) {
        if (serviceType == null || serviceType.trim().isEmpty() || "ALL".equalsIgnoreCase(serviceType)) {
            return true;
        }
        return provider.getServiceType().equalsIgnoreCase(serviceType);
    }

    // Get distinct service types for filter dropdown
    public List<String> getDistinctServiceTypes() {
        return serviceProviderRepository.findByApprovedTrue()
                .stream()
                .map(ServiceProvider::getServiceType)
                .distinct()
                .collect(Collectors.toList());
    }

}