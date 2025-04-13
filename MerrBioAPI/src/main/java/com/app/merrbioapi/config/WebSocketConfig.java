package com.app.merrbioapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker for destinations prefixed with /topic or /queue
        config.enableSimpleBroker("/topic", "/queue");
        
        // Messages from client to application will be prefixed with /app
        config.setApplicationDestinationPrefixes("/app");
        
        // Point-to-point messaging with /user prefix
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes) {
                        String query = request.getURI().getQuery();
                        String token = null;
                        
                        if (query != null) {
                            Map<String, String> queryParams = UriComponentsBuilder
                                .fromUriString(request.getURI().toString())
                                .build()
                                .getQueryParams()
                                .toSingleValueMap();
                            
                            token = queryParams.get("token");
                        }
                        
                        if (token != null && !token.isEmpty()) {
                            try {
                                // Use correct method for extracting username
                                String username = jwtService.extractUsername(token);
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                // Use correct method for validating token
                                if (jwtService.isAccessTokenValid(token, userDetails)) {
                                    Authentication auth = new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                    attributes.put("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                                    return true;
                                }
                            } catch (Exception e) {
                                System.err.println("Error processing token: " + e.getMessage());
                            }
                        }
                        return true; // Allow the handshake to proceed
                    }

                    @Override
                    public void afterHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Exception exception) {
                        // Nothing to do after handshake
                    }
                })
                .withSockJS();
        
        // Add a raw WebSocket endpoint without SockJS for testing
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}