package com.example.chat_application.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // 🔹 Broadcast (chat, presence)
        registry.enableSimpleBroker("/topic", "/queue");

        // 🔹 Client → Server
        registry.setApplicationDestinationPrefixes("/app");

        // 🔹 /user/queue/*
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                // 🔥 REQUIRED ON RENDER / CLOUD
                .setSessionCookieNeeded(false)
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
