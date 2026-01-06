package com.example.chat_application.dto;

import java.util.Map;

public class GroupListDTO {

    private Long id;
    private String roomId;
    private String name;
    private Long memberCount;

    // 🔥 REQUIRED FOR GROUP E2EE
    // userId -> encryptedGroupKey (Base64)
    private Map<Long, String> encryptedGroupKeys;

    public GroupListDTO(
            Long id,
            String roomId,
            String name,
            Long memberCount
    ) {
        this.id = id;
        this.roomId = roomId;
        this.name = name;
        this.memberCount = memberCount;
    }

    /* ===============================
       GETTERS
       =============================== */

    public Long getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public Map<Long, String> getEncryptedGroupKeys() {
        return encryptedGroupKeys;
    }

    /* ===============================
       SETTERS
       =============================== */

    public void setEncryptedGroupKeys(
            Map<Long, String> encryptedGroupKeys
    ) {
        this.encryptedGroupKeys = encryptedGroupKeys;
    }
}
