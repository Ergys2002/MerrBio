package com.app.merrbioapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * Represents an individual message within a conversation
 */
@Entity
@DynamicUpdate
@SQLDelete(sql = "UPDATE message SET deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "last_notification_sent")
    private LocalDateTime lastNotificationSent;
}