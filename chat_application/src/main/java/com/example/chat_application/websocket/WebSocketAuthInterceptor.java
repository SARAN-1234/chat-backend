package com.example.chat_application.websocket;

import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.security.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    public static final String WS_USER_ID = "WS_USER_ID";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(
            JwtUtil jwtUtil,
            UserRepository userRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // ✅ MUST use this — never wrap()
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();
        if (command == null) {
            return message;
        }

        /* ===============================
           🔐 AUTH ONLY ON CONNECT
           =============================== */
        if (command == StompCommand.CONNECT) {

            String authHeader =
                    accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null; // ❌ reject connection
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return null; // ❌ reject connection
            }

            String username = jwtUtil.extractUsername(token);
            Long userId =
                    userRepository.findUserIdByUsername(username);

            if (userId == null) {
                return null; // ❌ reject connection
            }

            // ✅ Attach user to STOMP session
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );

            accessor.setUser(authentication);

            // 🔥 THIS IS THE MOST IMPORTANT LINE
            Map<String, Object> session =
                    accessor.getSessionAttributes();

            if (session != null) {
                session.put(WS_USER_ID, userId);
            }
        }

        // ❌ DO NOT AUTH ON SEND / SUBSCRIBE
        return message;
    }
}
