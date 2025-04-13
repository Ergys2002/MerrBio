package com.app.merrbioapi.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private String content;
    private UUID conversationId;
    private Instant timestamp;
}