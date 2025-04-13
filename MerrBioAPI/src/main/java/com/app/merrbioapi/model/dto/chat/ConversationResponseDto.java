package com.app.merrbioapi.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseDto {
    private UUID id;
    private UUID initiatorId;
    private String initiatorName;
    private UUID recipientId;
    private String recipientName;
    private String title;
    private UUID productId;
    private String productName;
    private Instant lastMessageTime;
    private List<ChatMessageDto> messages;
    private boolean active;
}