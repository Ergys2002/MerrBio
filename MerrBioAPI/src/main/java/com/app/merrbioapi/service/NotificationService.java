package com.app.merrbioapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Send a notification to a specific user via WebSockets
     * 
     * @param userId the ID of the user to notify
     * @param title the notification title
     * @param message the notification message
     */
    public void sendNotificationToUser(UUID userId, String title, String message) {
        try {
            NotificationPayload payload = new NotificationPayload(title, message);
            String destination = "/queue/notifications/" + userId;
            
            log.info("Sending notification to user {}: {}", userId, message);
            messagingTemplate.convertAndSend(destination, payload);
        } catch (Exception e) {
            log.error("Failed to send notification to user {}", userId, e);
        }
    }
    
    /**
     * Send a broadcast notification to all connected users
     * 
     * @param title the notification title
     * @param message the notification message
     */
    public void sendBroadcastNotification(String title, String message) {
        try {
            NotificationPayload payload = new NotificationPayload(title, message);
            String destination = "/topic/notifications";
            
            log.info("Sending broadcast notification: {}", message);
            messagingTemplate.convertAndSend(destination, payload);
        } catch (Exception e) {
            log.error("Failed to send broadcast notification", e);
        }
    }
    
    /**
     * Simple payload class for notifications
     */
    private static class NotificationPayload {
        private final String title;
        private final String message;
        private final long timestamp;
        
        public NotificationPayload(String title, String message) {
            this.title = title;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}