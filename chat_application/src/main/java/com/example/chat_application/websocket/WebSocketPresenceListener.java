package com.example.chat_application.websocket;

import com.example.chat_application.dto.UserPresenceDTO;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketPresenceListener {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Map<String, AtomicInteger> ACTIVE_SESSIONS =
            new ConcurrentHashMap<>();

    public WebSocketPresenceListener(UserRepository userRepository,
                                     SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /* =========================
       🔵 CONNECT (AUTH READY)
       ========================= */
    @EventListener
    public void handleWebSocketConnected(
            org.springframework.web.socket.messaging.SessionConnectedEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() == null) return;

        String username = accessor.getUser().getName();

        AtomicInteger counter =
                ACTIVE_SESSIONS.computeIfAbsent(username, u -> new AtomicInteger(0));

        int sessions = counter.incrementAndGet();

        // ✅ FIRST ACTIVE SESSION → ONLINE
        if (sessions == 1) {
            userRepository.findByUsername(username).ifPresent(user -> {
                user.setStatus(User.Status.ONLINE);
                userRepository.save(user);

                messagingTemplate.convertAndSend(
                        "/topic/presence",
                        new UserPresenceDTO(
                                user.getId(),
                                "ONLINE",
                                null
                        )
                );
            });
        }
    }

    /* =========================
       🔴 DISCONNECT
       ========================= */
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() == null) return;

        String username = accessor.getUser().getName();

        AtomicInteger count = ACTIVE_SESSIONS.get(username);
        if (count == null) return;

        int remaining = count.decrementAndGet();

        if (remaining <= 0) {
            ACTIVE_SESSIONS.remove(username);

            userRepository.findByUsername(username).ifPresent(user -> {
                user.setStatus(User.Status.OFFLINE);
                user.setLastSeen(LocalDateTime.now());
                userRepository.save(user);

                messagingTemplate.convertAndSend(
                        "/topic/presence",
                        new UserPresenceDTO(
                                user.getId(),
                                "OFFLINE",
                                user.getLastSeen()
                        )
                );
            });
        }
    }
}
