package com.app.merrbioapi.controller;

import com.app.merrbioapi.model.dto.chat.ConversationRequestDto;
import com.app.merrbioapi.model.dto.chat.ConversationResponseDto;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.repository.UserRepository;
import com.app.merrbioapi.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @Operation(summary = "Start a new conversation", description = "Create a new conversation with another user")
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponseDto> startConversation(
            @RequestBody ConversationRequestDto request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UUID userId = getUserIdFromAuthentication(authentication);
        ConversationResponseDto conversation = chatService.startConversation(userId, request);
        return ResponseEntity.ok(conversation);
    }

    @Operation(summary = "Get all user conversations", description = "Get all conversations for the authenticated user")
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDto>> getUserConversations(
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    @Operation(summary = "Get conversation details", description = "Get a specific conversation by ID")
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationResponseDto> getConversation(
            @PathVariable UUID conversationId,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(chatService.getConversation(userId, conversationId));
    }

    @Operation(summary = "Mark conversation as read", description = "Mark all messages in a conversation as read")
    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markConversationAsRead(
            @PathVariable UUID conversationId,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        chatService.markConversationAsRead(userId, conversationId);
        return ResponseEntity.ok().build();
    }

    private UUID getUserIdFromAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        return user.getId();
    }
}