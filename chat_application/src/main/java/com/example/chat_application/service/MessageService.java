package com.example.chat_application.service;

import com.example.chat_application.model.*;
import com.example.chat_application.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageReadRepository messageReadRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;

    public MessageService(
            MessageRepository messageRepository,
            ChatRoomRepository chatRoomRepository,
            MessageReadRepository messageReadRepository,
            ChatRoomMemberRepository chatRoomMemberRepository,
            UserRepository userRepository
    ) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.messageReadRepository = messageReadRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.userRepository = userRepository;
    }

    /* =====================================================
       🔐 SEND MESSAGE (PRIVATE + GROUP – FINAL FIXED)
       ===================================================== */
    @Transactional
    public Message sendMessage(
            String roomId,                  // MAY BE PRESENT EVEN IF ROOM DOES NOT EXIST
            Long receiverId,                // REQUIRED FOR FIRST PRIVATE MESSAGE
            User sender,
            String cipherText,
            String iv,
            String encryptedAesKeyForSender,
            String encryptedAesKeyForReceiver,
            MessageType type
    ) {

        /* ===============================
           🔍 BASIC VALIDATION
           =============================== */
        if (cipherText == null || iv == null) {
            throw new IllegalArgumentException("Encrypted payload missing");
        }

        ChatRoom room = null;

        /* =====================================================
           🔥 CRITICAL FIX
           -----------------------------------------------------
           roomId != null DOES NOT mean room already exists
           Frontend may send precomputed roomId on first message
           ===================================================== */
        if (roomId != null && !roomId.isBlank()) {
            room = chatRoomRepository.findByRoomId(roomId).orElse(null);
        }

        /* ===============================
           🏗️ AUTO-CREATE PRIVATE ROOM
           =============================== */
        if (room == null) {

            if (receiverId == null) {
                throw new RuntimeException(
                        "ChatRoom not found and receiverId missing for first private message"
                );
            }

            room = createPrivateRoom(sender, receiverId);
        }

        /* ===============================
           🔐 AUTHORIZATION CHECK
           =============================== */
        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(sender)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        room.getId(),
                        sender.getId()
                );

        if (!isAllowed) {
            throw new RuntimeException("You are not allowed to send messages in this chat");
        }

        /* ===============================
           💾 SAVE MESSAGE
           =============================== */
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
       🏗️ CREATE PRIVATE CHAT ROOM (IDEMPOTENT)
       ===================================================== */
    private ChatRoom createPrivateRoom(User sender, Long receiverId) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        /* ===============================
           🔁 REUSE EXISTING PRIVATE CHAT
           =============================== */
        List<ChatRoom> existing =
                chatRoomRepository.findOneToOneChats(sender.getId(), receiverId);

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        String roomId = generatePrivateRoomId(sender.getId(), receiverId);

        Set<User> participants = new HashSet<>();
        participants.add(sender);
        participants.add(receiver);

        ChatRoom room = ChatRoom.builder()
                .roomId(roomId)
                .type(ChatRoomType.PRIVATE)
                .participants(participants)
                .build();

        return chatRoomRepository.save(room);
    }

    /* =====================================================
       🆔 PRIVATE ROOM ID (DETERMINISTIC)
       ===================================================== */
    private String generatePrivateRoomId(Long u1, Long u2) {
        long min = Math.min(u1, u2);
        long max = Math.max(u1, u2);
        return "private-" + min + "-" + max;
    }

    /* =====================================================
       📥 FETCH MESSAGES
       ===================================================== */
  /* =====================================================
   📥 FETCH MESSAGES (RACE-SAFE)
   ===================================================== */
    @Transactional(readOnly = true)
    public List<Message> getMessages(
            String roomId,
            User user,
            int page,
            int size
    ) {

        ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElse(null);

        // 🔥 FIRST MESSAGE RACE CONDITION FIX
        if (room == null) {
            return List.of(); // DO NOT THROW
        }

        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(user)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        room.getId(),
                        user.getId()
                );

        if (!isAllowed) {
            return List.of(); // DO NOT THROW
        }

        return messageRepository.findByChatRoomOrderByTimestampAscIdAsc(
                room,
                PageRequest.of(Math.max(page, 0), Math.min(size, 50))
        );
    }


    /* =====================================================
       👁️ MARK MESSAGE AS READ
       ===================================================== */
    @Transactional
    public void markMessageAsRead(Long messageId, User reader) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.getSender().getId().equals(reader.getId())) return;

        ChatRoom room = message.getChatRoom();

        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(reader)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        room.getId(),
                        reader.getId()
                );

        if (!isAllowed) return;

        if (messageReadRepository.existsByMessageAndUser(message, reader)) return;

        messageReadRepository.save(
                MessageRead.builder()
                        .message(message)
                        .user(reader)
                        .readAt(LocalDateTime.now())
                        .build()
        );

        if (message.getStatus() != MessageStatus.READ) {
            message.setStatus(MessageStatus.READ);
            messageRepository.save(message);
        }
    }
}
