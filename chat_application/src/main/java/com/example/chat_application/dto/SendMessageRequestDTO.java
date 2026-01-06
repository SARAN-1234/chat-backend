package com.example.chat_application.dto;

public class SendMessageRequestDTO {

    private Long chatRoomId;
    private String cipherText;
    private String iv;

    // 🔐 NEW: dual key E2EE
    private String encryptedAesKeyForSender;
    private String encryptedAesKeyForReceiver;

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
}
