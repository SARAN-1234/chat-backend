package com.example.chat_application.dto;

import java.time.LocalDateTime;

public class ChatSidebarDTO {

    private Long chatRoomId;
    private Long otherUserId;
    private String otherUsername;
    private String otherUserEmail;
    private String otherUserPublicKey;
    private LocalDateTime lastMessageTime;
    private Long lastMessageSenderId;

    public ChatSidebarDTO(
            Long chatRoomId,
            Long otherUserId,
            String otherUsername,
            String otherUserEmail,
            String otherUserPublicKey,
            LocalDateTime lastMessageTime,
            Long lastMessageSenderId
    ) {
        this.chatRoomId = chatRoomId;
        this.otherUserId = otherUserId;
        this.otherUsername = otherUsername;
        this.otherUserEmail = otherUserEmail;
        this.otherUserPublicKey = otherUserPublicKey;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getOtherUserPublicKey() {
        return otherUserPublicKey;
    }

    public Long getChatRoomId() { return chatRoomId; }
    public Long getOtherUserId() { return otherUserId; }
    public String getOtherUsername() { return otherUsername; }
    public String getOtherUserEmail() { return otherUserEmail; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public Long getLastMessageSenderId() { return lastMessageSenderId; }
}
