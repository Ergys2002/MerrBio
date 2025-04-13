package com.app.merrbioapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * This controller exists purely to document WebSocket interactions for Swagger UI.
 * It doesn't provide actual REST endpoints but serves as documentation for WebSocket usage.
 */
@RestController
@RequestMapping("/chat-socket-docs")
@Tag(name = "WebSocket Documentation", description = "Documentation for WebSocket chat API usage")
public class ChatSocketDocController {

    @Operation(
            summary = "WebSocket Connection Guide",
            description = "Instructions for connecting to the WebSocket chat API. This is not an actual API endpoint " +
                    "but documentation for how to use the WebSocket API." +
                    "\n\n" +
                    "1. Connect to `/api/v1/ws?token=YOUR_JWT_TOKEN` using SockJS or a WebSocket client\n" +
                    "2. Use STOMP protocol over the WebSocket connection\n" +
                    "3. Subscribe to `/user/queue/messages` to receive messages addressed to you\n" +
                    "4. Send messages to `/app/chat.sendMessage` with appropriate payload\n" +
                    "5. Mark messages as read by sending to `/app/chat.markRead`",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "WebSocket Connection Information",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "Connection Info",
                                                    value = "{\n" +
                                                            "  \"wsEndpoint\": \"/api/v1/ws\",\n" +
                                                            "  \"subscriptionEndpoints\": {\n" +
                                                            "    \"personalMessages\": \"/user/queue/messages\"\n" +
                                                            "  },\n" +
                                                            "  \"sendEndpoints\": {\n" +
                                                            "    \"sendMessage\": \"/app/chat.sendMessage\",\n" +
                                                            "    \"markRead\": \"/app/chat.markRead\"\n" +
                                                            "  }\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping("/connection-info")
    public Map<String, Object> getWebSocketConnectionInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("wsEndpoint", "/api/v1/ws");
        
        Map<String, String> subscriptionEndpoints = new HashMap<>();
        subscriptionEndpoints.put("personalMessages", "/user/queue/messages");
        info.put("subscriptionEndpoints", subscriptionEndpoints);
        
        Map<String, String> sendEndpoints = new HashMap<>();
        sendEndpoints.put("sendMessage", "/app/chat.sendMessage");
        sendEndpoints.put("markRead", "/app/chat.markRead");
        info.put("sendEndpoints", sendEndpoints);
        
        return info;
    }
    
    @Operation(
            summary = "Send Message Payload Example",
            description = "Example of the payload format for sending messages via WebSocket. " +
                    "Send this payload to `/app/chat.sendMessage` endpoint.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Example message payload",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "Send Message Payload",
                                                    value = "{\n" +
                                                            "  \"conversationId\": \"123e4567-e89b-12d3-a456-426614174000\",\n" +
                                                            "  \"content\": \"Hello, this is a test message!\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping("/send-message-example")
    public Map<String, Object> getMessagePayloadExample() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("conversationId", "123e4567-e89b-12d3-a456-426614174000");
        payload.put("content", "Hello, this is a test message!");
        return payload;
    }
    
    @Operation(
            summary = "Mark Read Payload Example",
            description = "Example of the payload format for marking messages as read via WebSocket. " +
                    "Send this payload to `/app/chat.markRead` endpoint.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Example mark read payload",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "Mark Read Payload",
                                                    value = "{\n" +
                                                            "  \"conversationId\": \"123e4567-e89b-12d3-a456-426614174000\"\n" +
                                                            "}"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping("/mark-read-example")
    public Map<String, Object> getMarkReadPayloadExample() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("conversationId", "123e4567-e89b-12d3-a456-426614174000");
        return payload;
    }
}