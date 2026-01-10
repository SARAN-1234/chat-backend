package com.example.chat_application.controller;

import com.example.chat_application.dto.ChatRoomResponseDTO;
import com.example.chat_application.dto.ChatSidebarDTO;
import com.example.chat_application.model.ChatRoom;
import com.example.chat_application.model.ChatRoomType;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.ChatRoomRepository;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.ProfileGuardService;
import com.example.chat_application.util.ChatRoomMapper;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ProfileGuardService profileGuardService;

    public ChatRoomController(
            ChatRoomRepository chatRoomRepository,
            UserRepository userRepository,
            ProfileGuardService profileGuardService
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.profileGuardService = profileGuardService;
    }

    /* =====================================================
       🔐 ONE-TO-ONE CHAT (DETERMINISTIC)
       ===================================================== */
    @PostMapping("/one-to-one")
    public ChatRoomResponseDTO createOneToOneChat(
            @RequestParam Long otherUserId,
            Principal principal
    ) {
        if (principal == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        User currentUser = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileGuardService.checkProfileCompleted(currentUser);

        List<ChatRoom> rooms =
                chatRoomRepository.findOneToOneChats(
                        currentUser.getId(),
                        otherUserId
                );

        ChatRoom room;

        if (!rooms.isEmpty()) {
            room = rooms.get(0);
        } else {
            User otherUser = userRepository.findById(otherUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String roomId = generatePrivateRoomId(
                    currentUser.getId(),
                    otherUserId
            );

            ChatRoom newRoom = new ChatRoom();
            newRoom.setRoomId(roomId);
            newRoom.setType(ChatRoomType.PRIVATE);
            newRoom.setParticipants(Set.of(currentUser, otherUser));

            room = chatRoomRepository.save(newRoom);
        }

        return ChatRoomMapper.toDto(room);
    }

    /* =====================================================
       💬 CHAT SIDEBAR (STRING roomId)
       ===================================================== */
    @GetMapping("/sidebar")
    public List<ChatSidebarDTO> getChatSidebar(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        User user = userRepository
                .findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileGuardService.checkProfileCompleted(user);

        return chatRoomRepository.findChatRoomsWithLastMessage(user)
                .stream()
                .map(row -> new ChatSidebarDTO(
                        (String) row[0],          // ✅ roomId STRING
                        (Long) row[1],            // otherUserId
                        (String) row[2],          // username
                        (String) row[3],          // publicKey
                        (String) row[4],          // email
                        (LocalDateTime) row[5],   // timestamp
                        (Long) row[6]             // senderId
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
