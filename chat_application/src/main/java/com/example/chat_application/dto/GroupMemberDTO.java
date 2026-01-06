package com.example.chat_application.dto;

import com.example.chat_application.model.Role;

public class GroupMemberDTO {

    private Long userId;
    private String username;
    private Role role;

    // ✅ MATCHES JPQL EXACTLY
    public GroupMemberDTO(Long userId, String username, Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }
}
