package com.example.chat_application.dto;

import java.time.LocalDateTime;

public class UserPresenceDTO {

    private Long userId;
    private String status;
    private LocalDateTime lastSeen;

    public UserPresenceDTO(Long userId, String status, LocalDateTime lastSeen) {
        this.userId = userId;
        this.status = status;
        this.lastSeen = lastSeen;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
}
