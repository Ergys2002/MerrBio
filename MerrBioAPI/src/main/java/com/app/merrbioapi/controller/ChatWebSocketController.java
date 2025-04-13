package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.chat.ChatMessageDto;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.repository.UserRepository;
import com.app.merrbioapi.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/websocket-api-docs") // This path is just for documentation, not actual routing
@RequiredArgsConstructor
@Tag(name = "Chat WebSocket", description = "WebSocket endpoints for real-time chat")
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserRepository userRepository;


    @Operation(
            summary = "Send a message",
            description = "Send a message to a conversation using WebSocket. " +
                    "Connect to /ws endpoint with JWT token and send message to /app/chat.sendMessage. " +
                    "Payload should include conversationId (String) and content (String)."
    )
    @MessageMapping("/chat.sendMessage")
    @SendToUser("/queue/reply")
    public ChatMessageDto sendMessage(
            @Payload Map<String, Object> payload,
            Authentication authentication) {
        
        if (authentication == null) {
            throw new IllegalStateException("Not authenticated");
        }
        
        UUID userId = getUserIdFromAuthentication(authentication);
        UUID conversationId = UUID.fromString((String) payload.get("conversationId"));
        String content = (String) payload.get("content");
        
        return chatService.sendMessage(userId, conversationId, content);
    }

    /**
     * Handle marking messages as read
     */
    @Operation(
            summary = "Mark messages as read",
            description = "Mark all messages in a conversation as read using WebSocket. " +
                    "Connect to /ws endpoint with JWT token and send message to /app/chat.markRead. " +
                    "Payload should include conversationId (String)."
    )
    @MessageMapping("/chat.markRead")
    public void markMessagesRead(
            @Payload Map<String, Object> payload,
            Authentication authentication) {
        
        if (authentication == null) {
            throw new IllegalStateException("Not authenticated");
        }
        
        UUID userId = getUserIdFromAuthentication(authentication);
        UUID conversationId = UUID.fromString((String) payload.get("conversationId"));
        
        chatService.markConversationAsRead(userId, conversationId);
    }
    
    /**
     * Helper method to get user ID from authentication
     */
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        return user.getId();
    }
}