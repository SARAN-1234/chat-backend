package com.example.chat_application.websocket;

import com.example.chat_application.dto.ChatMessageDTO;
import com.example.chat_application.dto.WebSocketMessageRequest;
import com.example.chat_application.model.Message;
import com.example.chat_application.model.MessageType;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.MessageService;
import com.example.chat_application.service.ProfileGuardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatWebSocketController {

    private static final Logger log =
            LoggerFactory.getLogger(ChatWebSocketController.class);

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
     * Client  -> /app/chat.send
     * Server  -> /topic/chat/{roomId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload WebSocketMessageRequest msg,
            StompHeaderAccessor accessor
    ) {

        /* ===============================
           🔐 AUTH FROM WS SESSION
           =============================== */
        Map<String, Object> session = accessor.getSessionAttributes();

        if (session == null ||
                !session.containsKey(WebSocketAuthInterceptor.WS_USER_ID)) {

            log.warn("❌ WS SEND blocked: missing WS_USER_ID");
            return; // DO NOT THROW → avoids STOMP ERROR
        }

        Long senderId;
        try {
            senderId = Long.valueOf(
                    session.get(WebSocketAuthInterceptor.WS_USER_ID).toString()
            );
        } catch (Exception e) {
            log.warn("❌ Invalid WS_USER_ID in session");
            return;
        }

        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) {
            log.warn("❌ Sender not found: {}", senderId);
            return;
        }

        /* ===============================
           🔐 PROFILE CHECK
           =============================== */
        profileGuardService.checkProfileCompleted(sender);

        /* ===============================
           🔎 PAYLOAD VALIDATION
           =============================== */
        if (msg == null ||
                msg.getCipherText() == null ||
                msg.getIv() == null) {

            log.warn("❌ Invalid encrypted payload");
            return;
        }

        log.info(
                "📨 WS SEND received | roomId={} | receiverId={}",
                msg.getChatRoomId(),
                msg.getReceiverId()
        );

        /* ===============================
           🔥 CREATE OR FIND CHAT ROOM
           =============================== */
        Message savedMessage;
        try {
            savedMessage = messageService.sendMessage(
                    msg.getChatRoomId(),                 // may be null / precomputed
                    msg.getReceiverId(),                 // required for first private msg
                    sender,
                    msg.getCipherText(),
                    msg.getIv(),
                    msg.getEncryptedAesKeyForSender(),
                    msg.getEncryptedAesKeyForReceiver(),
                    msg.getType() != null
                            ? msg.getType()
                            : MessageType.TEXT
            );
        } catch (Exception e) {
            log.error("❌ MessageService failed", e);
            return; // DO NOT PROPAGATE TO STOMP
        }

        /* ===============================
           📡 BUILD RESPONSE DTO
           =============================== */
        ChatMessageDTO response = new ChatMessageDTO();
        response.setId(savedMessage.getId());
        response.setChatRoomId(
                savedMessage.getChatRoom().getRoomId()
        );
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

        /* ===============================
           📣 BROADCAST TO ROOM
           =============================== */
        String topic =
                "/topic/chat/" +
                        savedMessage.getChatRoom().getRoomId();

        messagingTemplate.convertAndSend(topic, response);

        log.info(
                "✅ Message broadcasted | roomId={} | messageId={}",
                savedMessage.getChatRoom().getRoomId(),
                savedMessage.getId()
        );
    }
}
