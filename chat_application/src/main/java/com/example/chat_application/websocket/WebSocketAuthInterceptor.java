package com.example.chat_application.websocket;

import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.security.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    public static final String WS_USER_ID = "WS_USER_ID";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil,
                                    UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        // 🔥 AUTHENTICATE BOTH CONNECT AND SEND
        if (command == StompCommand.CONNECT || command == StompCommand.SEND) {

            String authHeader =
                    accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing Authorization header");
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new IllegalArgumentException("Invalid JWT token");
            }

            String username = jwtUtil.extractUsername(token);

            Long userId =
                    userRepository.findUserIdByUsername(username);

            if (userId == null) {
                throw new IllegalArgumentException("User not found");
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );

            accessor.setUser(authentication);
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            Map<String, Object> session =
                    accessor.getSessionAttributes();

            if (session != null) {
                session.put(WS_USER_ID, userId);
            }
        }

        return message;
    }
}
