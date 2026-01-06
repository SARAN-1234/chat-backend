package com.example.chat_application.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages") // ⚠️ ensure DB table name matches exactly
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===============================
       RELATIONS
       =============================== */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    /* ===============================
       🔐 E2EE PAYLOAD (DO NOT DECRYPT)
       =============================== */

    @Column(name = "cipher_text", columnDefinition = "TEXT", nullable = false)
    private String cipherText;

    @Column(length = 64, nullable = false)
    private String iv;

    @Column(name = "encrypted_aes_key_sender", columnDefinition = "TEXT", nullable = false)
    private String encryptedAesKeyForSender;

    @Column(name = "encrypted_aes_key_receiver", columnDefinition = "TEXT", nullable = false)
    private String encryptedAesKeyForReceiver;

    /* ===============================
       METADATA
       =============================== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    /* ===============================
       LIFECYCLE
       =============================== */

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();

        // 🔥 CRITICAL: prevent NULL enum crashes
        if (this.type == null) {
            this.type = MessageType.TEXT;
        }

        // Safe default (adjust if needed)
        if (this.status == null) {
            this.status = MessageStatus.SENT;
        }
    }
}
