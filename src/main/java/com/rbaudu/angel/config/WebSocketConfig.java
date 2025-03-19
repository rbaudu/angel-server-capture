package com.rbaudu.angel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration des WebSockets pour la communication en temps réel.
 * Cette classe configure les points de terminaison WebSocket et le broker de messages.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure le broker de messages.
     * 
     * @param registry le registre du broker de messages
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configure les préfixes pour les destinations qui mappent les méthodes annotées @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
        
        // Configure les préfixes pour les destinations auxquelles les clients peuvent s'abonner
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Configure le préfixe pour les destinations utilisateur
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Enregistre les points de terminaison STOMP.
     * 
     * @param registry le registre des points de terminaison
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Enregistre le point de terminaison "/ws" avec la politique SockJS activée
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
