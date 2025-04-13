package com.app.merrbioapi.model.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRequestDto {
    private UUID recipientId;
    private String initialMessage;
    private UUID productId; // Optional, in case conversation is about a specific product
}