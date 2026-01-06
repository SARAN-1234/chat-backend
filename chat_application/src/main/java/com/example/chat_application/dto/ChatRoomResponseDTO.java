package com.example.chat_application.dto;

import com.example.chat_application.model.ChatRoomType;

import java.util.Set;

public class ChatRoomResponseDTO {

    private Long id;
    private String roomId;
    private ChatRoomType type;
    private Set<UserResponseDTO> participants;

    public ChatRoomResponseDTO() {}

    public ChatRoomResponseDTO(Long id,
                               String roomId,
                               ChatRoomType type,
                               Set<UserResponseDTO> participants) {
        this.id = id;
        this.roomId = roomId;
        this.type = type;
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public ChatRoomType getType() {
        return type;
    }

    public Set<UserResponseDTO> getParticipants() {
        return participants;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setType(ChatRoomType type) {
        this.type = type;
    }

    public void setParticipants(Set<UserResponseDTO> participants) {
        this.participants = participants;
    }
}
