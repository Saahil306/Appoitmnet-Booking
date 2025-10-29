package com.appointment.booking.service;

import com.appointment.booking.entity.Notification;
import com.appointment.booking.entity.User;
import com.appointment.booking.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;
    
    
    public List<Notification> getUserNotifications(Long userId) {
        User user = userService.getUserById(userId);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
   
    public long getUnreadNotificationCount(Long userId) {
        User user = userService.getUserById(userId);
        return notificationRepository.countByUserAndReadFalse(user);
    }
    
   
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
    
    
    public void markAllAsRead(Long userId) {
        User user = userService.getUserById(userId);
        List<Notification> notifications = notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
        
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        
        notificationRepository.saveAll(notifications);
    }
    
    
    public Notification createNotification(Long userId, String title, String message, String type) {
        User user = userService.getUserById(userId);
        Notification notification = new Notification(user, title, message, 
            com.appointment.booking.entity.NotificationType.valueOf(type));
        
        return notificationRepository.save(notification);
    }
    

   
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}