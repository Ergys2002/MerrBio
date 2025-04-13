package com.app.merrbioapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    
    @Override
    protected boolean sameOriginDisabled() {
        // Disable CSRF for WebSocket connections
        return true;
    }
}