package com.example.chat_application.util;

import com.example.chat_application.dto.MessageResponseDTO;
import com.example.chat_application.dto.UserResponseDTO;
import com.example.chat_application.model.Message;
import com.example.chat_application.model.MessageType;
import com.example.chat_application.model.MessageStatus;
import com.example.chat_application.model.User;

public class MessageMapper {

    private MessageMapper() {
        // utility class
    }

    public static MessageResponseDTO toDto(Message message) {

        if (message == null) {
            return null;
        }

        // 🔥 SAFETY: skip legacy messages at mapping level
        if (message.getType() == MessageType.LEGACY) {
            return null;
        }

        User sender = message.getSender();

        MessageResponseDTO dto = new MessageResponseDTO();

        /* ===============================
           CORE IDS
           =============================== */
        dto.setId(message.getId());
        dto.setChatRoomId(
                message.getChatRoom() != null
                        ? message.getChatRoom().getId()
                        : null
        );

        /* ===============================
           🔐 ENCRYPTED PAYLOAD (VERBATIM)
           =============================== */
        dto.setCipherText(message.getCipherText());
        dto.setIv(message.getIv());
        dto.setEncryptedAesKeyForSender(message.getEncryptedAesKeyForSender());
        dto.setEncryptedAesKeyForReceiver(message.getEncryptedAesKeyForReceiver());

        /* ===============================
           METADATA
           =============================== */
        dto.setType(message.getType());
        dto.setTimestamp(message.getTimestamp());

        dto.setStatus(
                message.getStatus() != null
                        ? message.getStatus()
                        : MessageStatus.SENT
        );

        /* ===============================
           SENDER INFO
           =============================== */
        if (sender != null) {
            dto.setSender(
                    new UserResponseDTO(
                            sender.getId(),
                            sender.getUsername()
                    )
            );
        }

        return dto;
    }
}
