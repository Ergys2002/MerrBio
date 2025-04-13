package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Conversation;
import com.app.merrbioapi.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt ASC")
    List<Message> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") UUID conversationId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.conversation.id = :conversationId AND m.sender.id != :userId")
    void markMessagesAsRead(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.isRead = false AND m.sender.id != :userId")
    long countUnreadMessages(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
    
    /**
     * Find unread messages that are older than the given time and haven't had a notification sent recently
     */
    @Query("SELECT m FROM Message m WHERE m.isRead = false AND m.createdAt < :cutoffTime " +
           "AND (m.lastNotificationSent IS NULL OR m.lastNotificationSent < :cutoffTime)")
    List<Message> findUnreadMessagesOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Update the last notification sent time for a message
     */
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.lastNotificationSent = :sentTime WHERE m.id = :messageId")
    void updateNotificationSentTime(@Param("messageId") UUID messageId, @Param("sentTime") LocalDateTime sentTime);
}