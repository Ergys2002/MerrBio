package com.app.merrbioapi.service;

import com.app.merrbioapi.exception.AccessDeniedException;
import com.app.merrbioapi.exception.EntityNotFoundException;
import com.app.merrbioapi.model.dto.chat.ChatMessageDto;
import com.app.merrbioapi.model.dto.chat.ConversationRequestDto;
import com.app.merrbioapi.model.dto.chat.ConversationResponseDto;
import com.app.merrbioapi.model.entity.Conversation;
import com.app.merrbioapi.model.entity.Message;
import com.app.merrbioapi.model.entity.Product;
import com.app.merrbioapi.model.entity.User;
import com.app.merrbioapi.model.entity.UserInfo;
import com.app.merrbioapi.repository.ConversationRepository;
import com.app.merrbioapi.repository.MessageRepository;
import com.app.merrbioapi.repository.ProductRepository;
import com.app.merrbioapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ConversationResponseDto startConversation(UUID currentUserId, ConversationRequestDto request) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new EntityNotFoundException("Recipient not found"));

        // Check if conversation already exists between these users
        Optional<Conversation> existingConversation = conversationRepository.findConversationBetweenUsers(currentUser, recipient);

        Conversation conversation;
        if (existingConversation.isPresent()) {
            conversation = existingConversation.get();
            // If conversation was inactive, reactivate it
            if (!conversation.getIsActive()) {
                conversation.setIsActive(true);
                conversation = conversationRepository.save(conversation);
            }
        } else {
            // Create new conversation
            Product relatedProduct = null;
            if (request.getProductId() != null) {
                relatedProduct = productRepository.findById(request.getProductId())
                        .orElse(null); // If product not found, we still create the conversation without it
            }

            conversation = Conversation.builder()
                    .initiator(currentUser)
                    .recipient(recipient)
                    .relatedProduct(relatedProduct)
                    .title(generateConversationTitle(currentUser, recipient, relatedProduct))
                    .isActive(true)
                    .messages(new ArrayList<>())
                    .build();

            conversation = conversationRepository.save(conversation);
        }

        // Send initial message if provided
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            Message initialMessage = Message.builder()
                    .conversation(conversation)
                    .sender(currentUser)
                    .content(request.getInitialMessage())
                    .isRead(false)
                    .build();

            messageRepository.save(initialMessage);

            // Send real-time notification via WebSocket
            notifyRecipient(initialMessage);
        }

        return mapToConversationResponseDto(conversation);
    }

    @Transactional
    public ChatMessageDto sendMessage(UUID currentUserId, UUID conversationId, String content) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        // Check if current user is part of the conversation
        if (!conversation.getInitiator().equals(currentUser) && !conversation.getRecipient().equals(currentUser)) {
            throw new AccessDeniedException("You don't have permission to send messages in this conversation");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content(content)
                .isRead(false)
                .build();

        messageRepository.save(message);

        // Send real-time notification via WebSocket
        notifyRecipient(message);

        return mapToChatMessageDto(message);
    }

    private void notifyRecipient(Message message) {
        User recipient = message.getConversation().getInitiator().equals(message.getSender()) 
                ? message.getConversation().getRecipient() 
                : message.getConversation().getInitiator();

        ChatMessageDto chatMessageDto = mapToChatMessageDto(message);

        // Send via WebSocket to the specific user
        messagingTemplate.convertAndSendToUser(
                recipient.getId().toString(),
                "/queue/messages",
                chatMessageDto
        );
    }

    private String getSenderName(User user) {
        if (user.getUserInfo() != null) {
            UserInfo userInfo = user.getUserInfo();
            return userInfo.getFirstName() + " " + userInfo.getLastName();
        }
        return user.getEmail();
    }

    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getUserConversations(UUID userId) {
        List<Conversation> conversations = conversationRepository.findActiveConversationsByUserId(userId);
        return conversations.stream()
                .map(this::mapToConversationResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationResponseDto getConversation(UUID userId, UUID conversationId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        // Check if user is part of the conversation
        if (!conversation.getInitiator().equals(currentUser) && !conversation.getRecipient().equals(currentUser)) {
            throw new AccessDeniedException("You don't have permission to view this conversation");
        }

        return mapToConversationResponseDto(conversation);
    }

    @Transactional
    public void markConversationAsRead(UUID userId, UUID conversationId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        // Check if user is part of the conversation
        if (!conversation.getInitiator().equals(currentUser) && !conversation.getRecipient().equals(currentUser)) {
            throw new AccessDeniedException("You don't have permission to access this conversation");
        }

        // Mark all messages from other user as read
        messageRepository.markMessagesAsRead(conversationId, userId);
    }

    private String generateConversationTitle(User initiator, User recipient, Product product) {
        String initiatorName = getSenderName(initiator);
        String recipientName = getSenderName(recipient);

        if (product != null) {
            return "Conversation about " + product.getName();
        } else {
            return "Conversation between " + initiatorName + " and " + recipientName;
        }
    }

    private ChatMessageDto mapToChatMessageDto(Message message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(getSenderName(message.getSender()))
                .content(message.getContent())
                .conversationId(message.getConversation().getId())
                .timestamp(message.getCreatedAt())
                .build();
    }

    private ConversationResponseDto mapToConversationResponseDto(Conversation conversation) {
        List<ChatMessageDto> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .map(this::mapToChatMessageDto)
                .collect(Collectors.toList());

        // Get last message timestamp if available
        Instant lastMessageTime = conversation.getCreatedAt();
        if (!messages.isEmpty()) {
            lastMessageTime = messages.get(messages.size() - 1).getTimestamp();
        }

        return ConversationResponseDto.builder()
                .id(conversation.getId())
                .initiatorId(conversation.getInitiator().getId())
                .initiatorName(getSenderName(conversation.getInitiator()))
                .recipientId(conversation.getRecipient().getId())
                .recipientName(getSenderName(conversation.getRecipient()))
                .title(conversation.getTitle())
                .productId(conversation.getRelatedProduct() != null ? conversation.getRelatedProduct().getId() : null)
                .productName(conversation.getRelatedProduct() != null ? conversation.getRelatedProduct().getName() : null)
                .messages(messages)
                .lastMessageTime(lastMessageTime)
                .active(conversation.getIsActive())
                .build();
    }
}