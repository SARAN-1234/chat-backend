package com.example.chat_application.service;

import com.example.chat_application.dto.GroupListDTO;
import com.example.chat_application.dto.GroupMemberDTO;
import com.example.chat_application.model.*;
import com.example.chat_application.repository.*;
import com.example.chat_application.util.CryptoUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final GroupKeyRepository groupKeyRepository;
    private final UserRepository userRepository;

    public GroupChatService(
            ChatRoomRepository chatRoomRepository,
            ChatRoomMemberRepository chatRoomMemberRepository,
            GroupKeyRepository groupKeyRepository,
            UserRepository userRepository
    ) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomMemberRepository = chatRoomMemberRepository;
        this.groupKeyRepository = groupKeyRepository;
        this.userRepository = userRepository;
    }

    /* =====================================================
       ➕ CREATE GROUP (WITH E2EE KEY)
       ===================================================== */
    @Transactional
    public ChatRoom createGroup(
            Long creatorId,
            String name,
            List<Long> memberIds
    ) {

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(ChatRoomType.GROUP);
        chatRoom.setRoomId(UUID.randomUUID().toString());
        chatRoom.setName(name);

        chatRoom = chatRoomRepository.save(chatRoom);

        /* ===============================
           👥 COLLECT MEMBERS
           =============================== */
        Set<Long> members = new HashSet<>();
        members.add(creatorId);
        if (memberIds != null) {
            members.addAll(memberIds);
        }

        /* ===============================
           🔐 GENERATE GROUP AES KEY
           =============================== */
        SecretKey groupAESKey = CryptoUtils.generateAESKey();

        /* ===============================
           🔑 ENCRYPT GROUP KEY PER MEMBER
           =============================== */
        for (Long userId : members) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new RuntimeException("User not found: " + userId)
                    );

            String publicKeyBase64 = user.getPublicKey();

            // 🔥 CRITICAL SAFETY CHECK
            if (publicKeyBase64 == null || publicKeyBase64.isBlank()) {
                throw new RuntimeException(
                        "User " + user.getUsername() +
                                " (ID " + userId + ") has no public key. " +
                                "User must complete key setup before joining group."
                );
            }

            PublicKey publicKey =
                    CryptoUtils.importPublicKey(publicKeyBase64);

            String encryptedGroupKey =
                    CryptoUtils.encryptAESKeyWithRSA(groupAESKey, publicKey);

            // 🔥 STORE GROUP KEY
            GroupKey key = new GroupKey();
            key.setChatRoomId(chatRoom.getId());
            key.setUserId(userId);
            key.setEncryptedGroupKey(encryptedGroupKey);
            groupKeyRepository.save(key);

            Role role = userId.equals(creatorId)
                    ? Role.ADMIN
                    : Role.MEMBER;

            chatRoomMemberRepository.save(
                    new ChatRoomMember(chatRoom.getId(), userId, role)
            );
        }

        return chatRoom;
    }

    /* =====================================================
       ➕ ADD MEMBER (ADMIN ONLY)
       ===================================================== */
    @Transactional
    public void addMember(Long chatRoomId, Long adminUserId, Long newUserId) {

        boolean isAdmin =
                chatRoomMemberRepository
                        .existsByChatRoomIdAndUserIdAndRole(
                                chatRoomId,
                                adminUserId,
                                Role.ADMIN
                        );

        if (!isAdmin) {
            throw new RuntimeException("Only admin can add participants");
        }

        if (chatRoomMemberRepository
                .existsByChatRoomIdAndUserId(chatRoomId, newUserId)) {
            throw new RuntimeException("User already in group");
        }

        chatRoomMemberRepository.save(
                new ChatRoomMember(chatRoomId, newUserId, Role.MEMBER)
        );

        // ⚠️ IMPORTANT (NEXT STEP)
        // Group key SHOULD be rotated here
    }

    /* =====================================================
       👋 LEAVE GROUP
       ===================================================== */
    @Transactional
    public void leaveGroup(Long chatRoomId, Long userId) {

        if (!chatRoomMemberRepository
                .existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new RuntimeException("Not a group member");
        }

        chatRoomMemberRepository
                .deleteByChatRoomIdAndUserId(chatRoomId, userId);

        groupKeyRepository
                .findByChatRoomIdAndUserId(chatRoomId, userId)
                .ifPresent(groupKeyRepository::delete);
    }

    /* =====================================================
       👥 GET GROUP MEMBERS
       ===================================================== */
    public List<GroupMemberDTO> getGroupMembers(
            Long chatRoomId,
            Long requesterId
    ) {

        if (!chatRoomMemberRepository
                .existsByChatRoomIdAndUserId(chatRoomId, requesterId)) {
            throw new RuntimeException("Access denied");
        }

        return chatRoomMemberRepository
                .findMembersByChatRoomId(chatRoomId);
    }

    /* =====================================================
       📋 GET MY GROUPS (DTO + ENCRYPTED KEYS)
       ===================================================== */
    public List<GroupListDTO> getGroupsForUserDTO(Long userId) {

        List<GroupListDTO> groups =
                chatRoomRepository.findGroupListForUser(userId);

        for (GroupListDTO dto : groups) {

            Map<Long, String> encryptedKeys =
                    groupKeyRepository
                            .findByChatRoomId(dto.getId())
                            .stream()
                            .collect(Collectors.toMap(
                                    GroupKey::getUserId,
                                    GroupKey::getEncryptedGroupKey
                            ));

            dto.setEncryptedGroupKeys(encryptedKeys);
        }

        return groups;
    }
}
