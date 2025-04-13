package com.app.merrbioapi.repository;

import com.app.merrbioapi.model.entity.Conversation;
import com.app.merrbioapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    
    List<Conversation> findByInitiatorOrRecipientOrderByCreatedAtDesc(User initiator, User recipient);
    
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.initiator = :user1 AND c.recipient = :user2) OR " +
           "(c.initiator = :user2 AND c.recipient = :user1)")
    Optional<Conversation> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT c FROM Conversation c WHERE " +
           "c.isActive = true AND (c.initiator.id = :userId OR c.recipient.id = :userId) " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findActiveConversationsByUserId(@Param("userId") UUID userId);
}