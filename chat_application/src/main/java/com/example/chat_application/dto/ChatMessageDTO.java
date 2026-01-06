package com.example.chat_application.dto;

import com.example.chat_application.model.MessageStatus;
import com.example.chat_application.model.MessageType;

import java.time.LocalDateTime;

public class ChatMessageDTO {

    /* ===============================
       IDENTIFIERS
       =============================== */
    private Long id;
    private Long chatRoomId;

    /* ===============================
       🔐 ENCRYPTED PAYLOAD (VERBATIM)
       =============================== */
    private String cipherText;
    private String iv;
    private String encryptedAesKeyForSender;
    private String encryptedAesKeyForReceiver;

    /* ===============================
       SENDER INFO
       =============================== */
    private Long senderId;
    private String senderUsername;

    /* ===============================
       METADATA
       =============================== */
    private MessageType type;
    private MessageStatus status;
    private LocalDateTime timestamp;

    public ChatMessageDTO() {}

    /* ===== IDs ===== */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    /* ===== ENCRYPTED FIELDS ===== */
    public String getCipherText() { return cipherText; }
    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getIv() { return iv; }
    public void setIv(String iv) { this.iv = iv; }

    public String getEncryptedAesKeyForSender() {
        return encryptedAesKeyForSender;
    }
    public void setEncryptedAesKeyForSender(String encryptedAesKeyForSender) {
        this.encryptedAesKeyForSender = encryptedAesKeyForSender;
    }

    public String getEncryptedAesKeyForReceiver() {
        return encryptedAesKeyForReceiver;
    }
    public void setEncryptedAesKeyForReceiver(String encryptedAesKeyForReceiver) {
        this.encryptedAesKeyForReceiver = encryptedAesKeyForReceiver;
    }

    /* ===== SENDER ===== */
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    /* ===== METADATA ===== */
    public MessageType getType() { return type; }
    public void setType(MessageType type) {
        this.type = type != null ? type : MessageType.TEXT;
    }

    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) {
        this.status = status != null ? status : MessageStatus.SENT;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
