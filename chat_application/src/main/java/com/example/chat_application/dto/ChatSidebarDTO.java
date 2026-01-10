package com.example.chat_application.dto;

import java.time.LocalDateTime;

/**
 * Sidebar DTO MUST use PUBLIC roomId (String)
 * NEVER database IDs.
 */
public class ChatSidebarDTO {

    // 🔥 PUBLIC STRING roomId (e.g. private-1-2)
    private String roomId;

    private Long otherUserId;
    private String otherUsername;
    private String otherUserEmail;
    private String otherUserPublicKey;
    private LocalDateTime lastMessageTime;
    private Long lastMessageSenderId;

    public ChatSidebarDTO(
            String roomId,
            Long otherUserId,
            String otherUsername,
            String otherUserEmail,
            String otherUserPublicKey,
            LocalDateTime lastMessageTime,
            Long lastMessageSenderId
    ) {
        this.roomId = roomId;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
        this.otherUserEmail = otherUserEmail;
        this.otherUserPublicKey = otherUserPublicKey;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    /* ===============================
       GETTERS
       =============================== */

    public String getRoomId() {
        return roomId;
    }

    public Long getOtherUserId() {
        return otherUserId;
    }

    public String getOtherUsername() {
        return otherUsername;
    }

    public String getOtherUserEmail() {
        return otherUserEmail;
    }

    public String getOtherUserPublicKey() {
        return otherUserPublicKey;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public Long getLastMessageSenderId() {
        return lastMessageSenderId;
    }
}
