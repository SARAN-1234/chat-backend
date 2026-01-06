package com.example.chat_application.websocket;

import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.security.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return message;
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return message;
            }

            String username = jwtUtil.extractUsername(token);

            Long userId = userRepository.findUserIdByUsername(username);
            if (userId == null) return message;

            UsernamePasswordAuthenticationToken principal =
                    new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList()
                    );

            accessor.setUser(principal);
            SecurityContextHolder.getContext().setAuthentication(principal);

            Map<String, Object> session = accessor.getSessionAttributes();
            if (session != null) {
                session.put(WS_USER_ID, userId);
            }
        }

        return message;
    }

}
