package com.example.chat_application.dto;

import com.example.chat_application.model.MessageType;

public class WebSocketMessageRequest {

    private Long chatRoomId;

    // 🔐 E2EE fields
    private String cipherText;
    private String iv;
    private String encryptedAesKeyForSender;
    private String encryptedAesKeyForReceiver;

    // 🔥 ADD THIS
    private MessageType type;

    /* ===============================
       GETTERS & SETTERS
       =============================== */

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

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

    // 🔥 NEW
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
