package com.example.chat_application.util;

import com.example.chat_application.dto.ChatRoomResponseDTO;
import com.example.chat_application.dto.UserResponseDTO;
import com.example.chat_application.model.ChatRoom;
import com.example.chat_application.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class ChatRoomMapper {

    public static ChatRoomResponseDTO toDto(ChatRoom room) {

        Set<UserResponseDTO> users =
                room.getParticipants()
                        .stream()
                        .map(ChatRoomMapper::toUserDto)
                        .collect(Collectors.toSet());

        return new ChatRoomResponseDTO(
                room.getId(),
                room.getRoomId(),
                room.getType(),
                users
        );
    }

    private static UserResponseDTO toUserDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername()
        );
    }
}
