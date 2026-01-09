package com.example.chat_application.service;

import com.example.chat_application.model.*;
import com.example.chat_application.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
       🔐 SEND MESSAGE (AUTO-CREATE PRIVATE CHAT – FINAL)
       ===================================================== */
    @Transactional
    public Message sendMessage(
            String roomId,          // MAY BE NULL FOR FIRST MESSAGE
            Long receiverId,        // REQUIRED ONLY FOR FIRST MESSAGE
            User sender,
            String cipherText,
            String iv,
            String encryptedAesKeyForSender,
            String encryptedAesKeyForReceiver,
            MessageType type
    ) {

        if (cipherText == null || iv == null
                || encryptedAesKeyForSender == null
                || encryptedAesKeyForReceiver == null) {
            throw new IllegalArgumentException("Encrypted payload missing");
        }

        ChatRoom room;

        /* ===============================
           🔥 FIRST PRIVATE MESSAGE
           =============================== */
        if (roomId == null || roomId.isBlank()) {

            if (receiverId == null) {
                throw new RuntimeException("receiverId required for first private message");
            }

            room = createPrivateRoom(sender, receiverId);

        }
        /* ===============================
           🔁 EXISTING CHAT
           =============================== */
        else {
            room = chatRoomRepository.findByRoomId(roomId)
                    .orElseThrow(() -> new RuntimeException("ChatRoom not found"));
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
       🏗️ CREATE PRIVATE CHAT ROOM
       ===================================================== */
    private ChatRoom createPrivateRoom(User sender, Long receiverId) {

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // 🔁 Reuse if already exists
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

    private String generatePrivateRoomId(Long u1, Long u2) {
        long min = Math.min(u1, u2);
        long max = Math.max(u1, u2);
        return "private-" + min + "-" + max;
    }

    /* =====================================================
       📥 FETCH MESSAGES
       ===================================================== */
    @Transactional(readOnly = true)
    public List<Message> getMessages(
            String roomId,
            User user,
            int page,
            int size
    ) {

        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        boolean isAllowed =
                room.getType() == ChatRoomType.PRIVATE
                        ? room.getParticipants().contains(user)
                        : chatRoomMemberRepository.existsByChatRoomIdAndUserId(
                        room.getId(),
                        user.getId()
                );

        if (!isAllowed) {
            throw new RuntimeException("You are not allowed to view this chat");
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
                        .readAt(java.time.LocalDateTime.now())
                        .build()
        );

        if (message.getStatus() != MessageStatus.READ) {
            message.setStatus(MessageStatus.READ);
            messageRepository.save(message);
        }
    }
}
