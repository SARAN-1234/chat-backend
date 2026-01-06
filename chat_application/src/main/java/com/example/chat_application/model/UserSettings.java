package com.example.chat_application.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean audioEnabled = true;

    @Column(nullable = false)
    private boolean videoEnabled = true;

    @Column(nullable = false)
    private boolean allowUnknownCalls = false;

    @Column(nullable = false)
    private boolean allowGroupCalls = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionPolicy connectionPolicy = ConnectionPolicy.EVERYONE;

    public enum ConnectionPolicy {
        EVERYONE,
        CONTACTS_ONLY,
        NOBODY
    }
}
