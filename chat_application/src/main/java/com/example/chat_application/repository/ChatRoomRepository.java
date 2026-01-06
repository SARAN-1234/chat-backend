package com.example.chat_application.repository;

import com.example.chat_application.dto.GroupListDTO;
import com.example.chat_application.model.ChatRoom;
import com.example.chat_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /* =====================================================
       BASIC
       ===================================================== */

    Optional<ChatRoom> findByRoomId(String roomId);

    /* =====================================================
       ONE-TO-ONE CHAT (REUSE EXISTING)
       ===================================================== */

    @Query("""
        SELECT cr FROM ChatRoom cr
        WHERE cr.type = com.example.chat_application.model.ChatRoomType.PRIVATE
          AND SIZE(cr.participants) = 2
          AND EXISTS (SELECT u FROM cr.participants u WHERE u.id = :user1Id)
          AND EXISTS (SELECT u FROM cr.participants u WHERE u.id = :user2Id)
        ORDER BY cr.id ASC
    """)
    List<ChatRoom> findOneToOneChats(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );

    /* =====================================================
       ✅ CHAT SIDEBAR (E2EE SAFE)
       NO MESSAGE CONTENT
       INCLUDES RECEIVER PUBLIC KEY
       ===================================================== */

    @Query("""
    SELECT cr.id,
           u.id,
           u.username,
           u.email,
           u.publicKey,
           m.timestamp,
           m.sender.id
    FROM ChatRoom cr
    JOIN cr.participants u
    LEFT JOIN Message m ON m.chatRoom = cr
    WHERE :user MEMBER OF cr.participants
      AND u <> :user
      AND m.timestamp = (
          SELECT MAX(m2.timestamp)
          FROM Message m2
          WHERE m2.chatRoom = cr
      )
""")
    List<Object[]> findChatRoomsWithLastMessage(@Param("user") User user);

    /* =====================================================
       GROUP CHATS FOR USER
       ===================================================== */

    @Query("""
        SELECT DISTINCT cr
        FROM ChatRoom cr
        JOIN cr.participants u
        WHERE cr.type = com.example.chat_application.model.ChatRoomType.GROUP
          AND u.id = :userId
    """)
    List<ChatRoom> findGroupsByUserId(@Param("userId") Long userId);

    /* =====================================================
       GROUP LIST (UNCHANGED)
       ===================================================== */

    @Query("""
        SELECT new com.example.chat_application.dto.GroupListDTO(
            cr.id,
            cr.roomId,
            cr.name,
            COUNT(m.userId)
        )
        FROM ChatRoom cr
        JOIN ChatRoomMember m ON m.chatRoomId = cr.id
        WHERE cr.type = com.example.chat_application.model.ChatRoomType.GROUP
          AND EXISTS (
              SELECT 1
              FROM ChatRoomMember m2
              WHERE m2.chatRoomId = cr.id
                AND m2.userId = :userId
          )
        GROUP BY cr.id, cr.roomId, cr.name
        ORDER BY cr.id DESC
    """)
    List<GroupListDTO> findGroupListForUser(@Param("userId") Long userId);
}
