package com.example.chat_application.dto;

public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private boolean profileCompleted;

    public LoginResponse(String token,
                         Long userId,
                         String username,
                         boolean profileCompleted) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.profileCompleted = profileCompleted;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }
}
