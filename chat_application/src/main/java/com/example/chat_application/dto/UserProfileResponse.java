package com.example.chat_application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {

    private String username;
    private String email;

    private String displayName;
    private String phoneNumber;
    private String profileImageUrl;
    private String bio;

    private boolean profileCompleted;

    // 🔐 REQUIRED FOR E2EE HISTORY
    private String publicKey;
}
