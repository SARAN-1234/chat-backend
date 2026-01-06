package com.example.chat_application.model;

import jakarta.persistence.*;

@Entity
@Table(name = "group_keys")
public class GroupKey {

    /* ===============================
       PRIMARY KEY
       =============================== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===============================
       FOREIGN KEYS (LOGICAL)
       =============================== */
    @Column(nullable = false)
    private Long chatRoomId;

    @Column(nullable = false)
    private Long userId;

    /* ===============================
       ENCRYPTED GROUP AES KEY
       =============================== */
    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String encryptedGroupKey;

    /* ===============================
       GETTERS & SETTERS
       =============================== */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEncryptedGroupKey() {
        return encryptedGroupKey;
    }

    public void setEncryptedGroupKey(String encryptedGroupKey) {
        this.encryptedGroupKey = encryptedGroupKey;
    }
}
