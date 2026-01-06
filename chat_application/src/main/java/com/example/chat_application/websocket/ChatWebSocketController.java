package com.example.chat_application.websocket;

import com.example.chat_application.dto.ChatMessageDTO;
import com.example.chat_application.dto.WebSocketMessageRequest;
import com.example.chat_application.model.Message;
import com.example.chat_application.model.MessageType;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.MessageService;
import com.example.chat_application.service.ProfileGuardService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ProfileGuardService profileGuardService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(
            MessageService messageService,
            UserRepository userRepository,
            ProfileGuardService profileGuardService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.profileGuardService = profileGuardService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Client → /app/chat.send
     * Server → /topic/chat/{roomId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload WebSocketMessageRequest msg,
            StompHeaderAccessor accessor
    ) {

        /* ===============================
           AUTH FROM WS SESSION
           =============================== */
        Object userIdObj = accessor.getSessionAttributes()
                .get(WebSocketAuthInterceptor.WS_USER_ID);

        if (userIdObj == null) {
            throw new RuntimeException("Unauthenticated WebSocket session");
        }

        Long userId = Long.valueOf(userIdObj.toString());

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        profileGuardService.checkProfileCompleted(sender);

        /* ===============================
           PERSIST ENCRYPTED MESSAGE
           =============================== */
        Message savedMessage = messageService.sendMessage(
                msg.getChatRoomId(),
                sender,
                msg.getCipherText(),
                msg.getIv(),
                msg.getEncryptedAesKeyForSender(),
                msg.getEncryptedAesKeyForReceiver(),
                msg.getType() != null ? msg.getType() : MessageType.TEXT
        );

        /* ===============================
           BROADCAST PERSISTED MESSAGE
           =============================== */
        ChatMessageDTO response = new ChatMessageDTO();

        response.setId(savedMessage.getId());
        response.setChatRoomId(savedMessage.getChatRoom().getId());

        // 🔐 encrypted payload (from DB entity)
        response.setCipherText(savedMessage.getCipherText());
        response.setIv(savedMessage.getIv());
        response.setEncryptedAesKeyForSender(
                savedMessage.getEncryptedAesKeyForSender()
        );
        response.setEncryptedAesKeyForReceiver(
                savedMessage.getEncryptedAesKeyForReceiver()
        );

        response.setType(savedMessage.getType());
        response.setStatus(savedMessage.getStatus());
        response.setSenderId(sender.getId());
        response.setSenderUsername(sender.getUsername());
        response.setTimestamp(savedMessage.getTimestamp());

        messagingTemplate.convertAndSend(
                "/topic/chat/" + savedMessage.getChatRoom().getId(),
                response
        );
    }
}
