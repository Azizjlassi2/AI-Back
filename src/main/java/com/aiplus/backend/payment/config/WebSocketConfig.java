package com.aiplus.backend.payment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures WebSocket with STOMP for real-time messaging. - Registers /ws
 * endpoint with SockJS fallback for broader browser support. - Enables simple
 * broker for topics like /topic and /user. - Sets application prefix for
 * client-sent messages and user prefix for private messages.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client connects to this endpoint; SockJS provides fallback for non-WebSocket
        // browsers
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple in-memory broker for public (/topic) and private (/user)
        // destinations
        registry.enableSimpleBroker("/topic", "/user");
        // Prefix for messages sent from client to server (e.g., /app/chat)
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific messages (e.g., /user/{username}/payment-updates)
        registry.setUserDestinationPrefix("/user");
    }
}