package com.example.chat_application.service;

import com.example.chat_application.model.*;
import com.example.chat_application.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageReadRepository messageReadRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public MessageService(
            MessageRepository messageRepository,
            ChatRoomRepository chatRoomRepository,
            MessageReadRepository messageReadRepository,
            ChatRoomMemberRepository chatRoomMemberRepository
    ) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.messageReadRepository = messageReadRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
    }

    /* =====================================================
       🔐 SEND MESSAGE (DUAL-KEY E2EE – FINAL)
       ===================================================== */
    @Transactional
    public Message sendMessage(
            String roomId,   // 🔥 STRING, NOT Long
            User sender,
            String cipherText,
            String iv,
            String encryptedAesKeyForSender,
            String encryptedAesKeyForReceiver,
            MessageType type
    ) {

        if (cipherText == null ||
                iv == null ||
                encryptedAesKeyForSender == null ||
                encryptedAesKeyForReceiver == null) {
            throw new IllegalArgumentException("Encrypted payload missing");
        }

        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(sender)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        room.getId(),   // ✅ DB id internally
                        sender.getId()
                );

        if (!isAllowed) {
            throw new RuntimeException("You are not allowed to send messages in this chat");
        }

        Message message = Message.builder()
                .chatRoom(room)
                .sender(sender)
                .cipherText(cipherText)
                .iv(iv)
                .encryptedAesKeyForSender(encryptedAesKeyForSender)
                .encryptedAesKeyForReceiver(encryptedAesKeyForReceiver)
                .type(type != null ? type : MessageType.TEXT)
                .status(MessageStatus.SENT)
                .build();

        return messageRepository.save(message);
    }

    /* =====================================================
       📥 FETCH MESSAGES (READ ONLY – E2EE SAFE)
       ===================================================== */
    @Transactional(readOnly = true)
    public List<Message> getMessages(
            Long chatRoomId,
            User user,
            int page,
            int size
    ) {

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(user)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        chatRoomId,
                        user.getId()
                );

        if (!isAllowed) {
            throw new RuntimeException("You are not allowed to view this chat");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        return messageRepository.findByChatRoomOrderByTimestampAscIdAsc(
                room,
                PageRequest.of(safePage, safeSize)
        );
    }

    /* =====================================================
       👁️ MARK MESSAGE AS READ (UNCHANGED)
       ===================================================== */
    @Transactional
    public void markMessageAsRead(Long messageId, User reader) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.getSender().getId().equals(reader.getId())) {
            return;
        }

        ChatRoom room = message.getChatRoom();

        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(reader)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        room.getId(),
                        reader.getId()
                );

        if (!isAllowed) {
            throw new RuntimeException("You are not allowed to read this message");
        }

        if (messageReadRepository.existsByMessageAndUser(message, reader)) {
            return;
        }

        MessageRead read = MessageRead.builder()
                .message(message)
                .user(reader)
                .readAt(java.time.LocalDateTime.now())
                .build();

        messageReadRepository.save(read);

        if (message.getStatus() != MessageStatus.READ) {
            message.setStatus(MessageStatus.READ);
            messageRepository.save(message);
        }
    }
}
