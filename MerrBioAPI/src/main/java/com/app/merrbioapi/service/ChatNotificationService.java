package com.app.merrbioapi.service;

import com.app.merrbioapi.model.entity.Conversation;
import com.app.merrbioapi.model.entity.Message;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for scheduled notifications for unread messages
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatNotificationService {

    private final MessageRepository messageRepository;
    private final EmailService emailService;
    
    /**
     * Checks for unread messages that haven't been replied to for 6 hours
     * and sends email notifications to recipients
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    @Transactional(readOnly = true)
    public void sendUnreadMessageReminders() {
        log.info("Running scheduled check for unread messages");
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(6);
        
        // Find messages that are:
        // 1. Unread
        // 2. Older than 6 hours
        // 3. Have not had a notification sent recently
        List<Message> unreadMessages = messageRepository.findUnreadMessagesOlderThan(cutoffTime);
        
        for (Message message : unreadMessages) {
            User recipient = getRecipientUser(message);
            User sender = message.getSender();
            Conversation conversation = message.getConversation();
            
            // Send email notification
            try {
                sendMessageReminder(recipient, sender, message, conversation);
                
                // Mark that a notification was sent for this message
                messageRepository.updateNotificationSentTime(message.getId(), LocalDateTime.now());
            } catch (Exception e) {
                log.error("Failed to send message reminder for message {}: {}", message.getId(), e.getMessage());
            }
        }
    }
    
    private User getRecipientUser(Message message) {
        Conversation conversation = message.getConversation();
        // If sender is the initiator, then recipient is the other user
        return message.getSender().equals(conversation.getInitiator()) 
                ? conversation.getRecipient() 
                : conversation.getInitiator();
    }
    
    private void sendMessageReminder(User recipient, User sender, Message message, Conversation conversation) {
        String recipientEmail = recipient.getEmail();
        String senderName = getSenderName(sender);
        String messagePreview = message.getContent();
        
        // Trim message preview if too long
        if (messagePreview.length() > 100) {
            messagePreview = messagePreview.substring(0, 97) + "...";
        }
        
        // Get conversation title
        String conversationTitle = conversation.getTitle();
        
        emailService.sendChatNotification(recipientEmail, senderName, messagePreview, conversationTitle);
        log.info("Sent reminder email to {} about message from {}", recipientEmail, senderName);
    }
    
    private String getSenderName(User user) {
        if (user.getUserInfo() != null) {
            return user.getUserInfo().getFirstName() + " " + user.getUserInfo().getLastName();
        }
        return user.getEmail();
    }
}