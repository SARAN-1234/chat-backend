package com.example.chat_application.dto;

public class GroupChatResponseDTO {

    private Long id;
    private String roomId;
    private String type;

    public GroupChatResponseDTO(Long id, String roomId, String type) {
        this.id = id;
        this.roomId = roomId;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getType() {
        return type;
    }
}
