package com.example.chat_application.websocket;

import com.example.chat_application.dto.UserPresenceDTO;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketPresenceListener {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 🔐 userId → active session count
    private static final Map<Long, AtomicInteger> ACTIVE_SESSIONS =
            new ConcurrentHashMap<>();

    public WebSocketPresenceListener(
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /* =========================
       🔵 CONNECT
       ========================= */
    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() == null) return;

        String username = accessor.getUser().getName();

        userRepository.findByUsername(username).ifPresent(user -> {

            AtomicInteger counter =
                    ACTIVE_SESSIONS.computeIfAbsent(
                            user.getId(),
                            u -> new AtomicInteger(0)
                    );

            int sessions = counter.incrementAndGet();

            if (sessions == 1) {
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
            }
        });
    }

    /* =========================
       🔴 DISCONNECT
       ========================= */
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getUser() == null) return;

        String username = accessor.getUser().getName();

        userRepository.findByUsername(username).ifPresent(user -> {

            AtomicInteger counter = ACTIVE_SESSIONS.get(user.getId());
            if (counter == null) return;

            int remaining = counter.decrementAndGet();

            if (remaining <= 0) {
                ACTIVE_SESSIONS.remove(user.getId());

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
            }
        });
    }
}
