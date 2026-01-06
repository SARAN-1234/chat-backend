package com.example.chat_application.dto;

import com.example.chat_application.model.MessageStatus;
import com.example.chat_application.model.MessageType;

import java.time.LocalDateTime;

public class MessageResponseDTO {

    /* ===============================
       CORE IDS
       =============================== */
    private Long id;
    private Long chatRoomId;

    /* ===============================
       🔐 E2EE PAYLOAD (VERBATIM)
       =============================== */
    private String cipherText;
    private String iv;
    private String encryptedAesKeyForSender;
    private String encryptedAesKeyForReceiver;

    /* ===============================
       METADATA
       =============================== */
    private MessageType type;
    private MessageStatus status;
    private LocalDateTime timestamp;
    private UserResponseDTO sender;

    /* ===============================
       CONSTRUCTORS
       =============================== */
    public MessageResponseDTO() {}

    /* ===============================
       GETTERS
       =============================== */
    public Long getId() { return id; }
    public Long getChatRoomId() { return chatRoomId; }

    public String getCipherText() { return cipherText; }
    public String getIv() { return iv; }

    public String getEncryptedAesKeyForSender() {
        return encryptedAesKeyForSender;
    }

    public String getEncryptedAesKeyForReceiver() {
        return encryptedAesKeyForReceiver;
    }

    public MessageType getType() { return type; }
    public MessageStatus getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public UserResponseDTO getSender() { return sender; }

    /* ===============================
       SETTERS (REQUIRED FOR MAPPER)
       =============================== */
    public void setId(Long id) { this.id = id; }
    public void setChatRoomId(Long chatRoomId) { this.chatRoomId = chatRoomId; }

    public void setCipherText(String cipherText) { this.cipherText = cipherText; }
    public void setIv(String iv) { this.iv = iv; }

    public void setEncryptedAesKeyForSender(String encryptedAesKeyForSender) {
        this.encryptedAesKeyForSender = encryptedAesKeyForSender;
    }

    public void setEncryptedAesKeyForReceiver(String encryptedAesKeyForReceiver) {
        this.encryptedAesKeyForReceiver = encryptedAesKeyForReceiver;
    }

    public void setType(MessageType type) {
        this.type = type != null ? type : MessageType.TEXT;
    }

    public void setStatus(MessageStatus status) {
        this.status = status != null ? status : MessageStatus.SENT;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setSender(UserResponseDTO sender) {
        this.sender = sender;
    }
}
