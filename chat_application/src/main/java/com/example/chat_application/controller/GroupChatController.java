package com.example.chat_application.controller;

import com.example.chat_application.dto.CreateGroupRequestDTO;
import com.example.chat_application.dto.GroupChatResponseDTO;
import com.example.chat_application.dto.GroupListDTO;
import com.example.chat_application.dto.GroupMemberDTO;
import com.example.chat_application.model.ChatRoom;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.GroupChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/chatroom/group")
public class GroupChatController {

    private final GroupChatService groupChatService;
    private final UserRepository userRepository;

    public GroupChatController(GroupChatService groupChatService,
                               UserRepository userRepository) {
        this.groupChatService = groupChatService;
        this.userRepository = userRepository;
    }

    /* =====================================================
       👥 GET GROUP MEMBERS
       ===================================================== */
    @GetMapping("/{chatRoomId}/members")
    public List<GroupMemberDTO> getGroupMembers(
            @PathVariable Long chatRoomId,
            Authentication authentication) {

        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return groupChatService.getGroupMembers(chatRoomId, userId);
    }

    /* =====================================================
       ➕ CREATE GROUP (WITH NAME ✅)
       ===================================================== */
    @PostMapping("/create")
    public GroupChatResponseDTO createGroup(
            Authentication authentication,
            @RequestBody CreateGroupRequestDTO request) {

        Long creatorId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        ChatRoom chatRoom = groupChatService.createGroup(
                creatorId,
                request.getName(),
                request.getMemberIds()
        );

        return new GroupChatResponseDTO(
                chatRoom.getId(),
                chatRoom.getRoomId(),
                chatRoom.getType().name()
        );
    }

    /* =====================================================
       ➕ ADD MEMBER (ADMIN ONLY)
       ===================================================== */
    @PostMapping("/{chatRoomId}/add/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long chatRoomId,
            @PathVariable Long userId,
            Authentication authentication) {

        Long adminId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        groupChatService.addMember(chatRoomId, adminId, userId);
        return ResponseEntity.ok().build();
    }

    /* =====================================================
       👋 LEAVE GROUP
       ===================================================== */
    @PostMapping("/{chatRoomId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable Long chatRoomId,
            Authentication authentication) {

        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        groupChatService.leaveGroup(chatRoomId, userId);
        return ResponseEntity.ok().build();
    }

    /* =====================================================
       📋 GET MY GROUPS
       ===================================================== */
    @GetMapping("/my")
    public List<GroupListDTO> getMyGroups(Authentication authentication) {

        Long userId = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        return groupChatService.getGroupsForUserDTO(userId);
    }
}
