package com.example.chat_application.service;

import com.example.chat_application.dto.ChatSidebarDTO;
import com.example.chat_application.model.ChatRoom;
import com.example.chat_application.model.ChatRoomType;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.ChatRoomRepository;
import com.example.chat_application.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    /* =====================================================
       ONE-TO-ONE ROOM (DETERMINISTIC)
       ===================================================== */
    @Transactional
    public ChatRoom getOrCreateOneToOneRoom(
            Long currentUserId,
            Long otherUserId
    ) {

        List<ChatRoom> rooms =
                chatRoomRepository.findOneToOneChats(
                        currentUserId,
                        otherUserId
                );

        if (!rooms.isEmpty()) {
            return rooms.get(0);
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String roomId = generatePrivateRoomId(
                currentUserId,
                otherUserId
        );

        ChatRoom newRoom = new ChatRoom();
        newRoom.setRoomId(roomId);
        newRoom.setType(ChatRoomType.PRIVATE);
        newRoom.setParticipants(Set.of(currentUser, otherUser));

        return chatRoomRepository.save(newRoom);
    }

    /* =====================================================
       🔒 CHAT SIDEBAR (E2EE SAFE)
       ===================================================== */
    @Transactional(readOnly = true)
    public List<ChatSidebarDTO> getUserChatSidebar(User user) {

        List<Object[]> rows =
                chatRoomRepository.findChatRoomsWithLastMessage(user);

        return rows.stream()
                .map(r -> new ChatSidebarDTO(
                        (String) r[0],           // ✅ roomId (STRING)
                        (Long) r[1],             // otherUserId
                        (String) r[2],           // otherUsername
                        (String) r[3],           // otherUserPublicKey
                        (String) r[4],           // otherUserEmail
                        (LocalDateTime) r[5],    // lastMessageTime
                        (Long) r[6]              // lastMessageSenderId
                ))
                .toList();
    }

    /* =====================================================
       🔑 PRIVATE ROOM ID (SINGLE SOURCE OF TRUTH)
       ===================================================== */
    private String generatePrivateRoomId(Long u1, Long u2) {
        long min = Math.min(u1, u2);
        long max = Math.max(u1, u2);
        return "private-" + min + "-" + max;
    }
}
