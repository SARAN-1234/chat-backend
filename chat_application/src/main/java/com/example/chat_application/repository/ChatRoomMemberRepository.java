package com.example.chat_application.repository;

import com.example.chat_application.dto.GroupMemberDTO;
import com.example.chat_application.model.ChatRoomMember;
import com.example.chat_application.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    /**
     * Find all members of a chat room
     */
    List<ChatRoomMember> findByChatRoomId(Long chatRoomId);

    /**
     * Find all group memberships for a user
     */
    List<ChatRoomMember> findByUserId(Long userId);

    /**
     * Check if a user is already a member of a group
     */
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    /**
     * ✅ Check if user is ADMIN in group (CRITICAL)
     */
    boolean existsByChatRoomIdAndUserIdAndRole(
            Long chatRoomId,
            Long userId,
            Role role
    );

    /**
     * Remove a user from a group
     */
    void deleteByChatRoomIdAndUserId(Long chatRoomId, Long userId);


    @Query("""
    SELECT new com.example.chat_application.dto.GroupMemberDTO(
        u.id,
        u.username,
        m.role
    )
    FROM ChatRoomMember m, User u
    WHERE u.id = m.userId
      AND m.chatRoomId = :chatRoomId
""")
    List<GroupMemberDTO> findMembersByChatRoomId(@Param("chatRoomId") Long chatRoomId);




}
