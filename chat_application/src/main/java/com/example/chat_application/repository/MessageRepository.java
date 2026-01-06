package com.example.chat_application.repository;

import com.example.chat_application.model.Message;
import com.example.chat_application.model.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatRoomOrderByTimestampAscIdAsc(
            ChatRoom chatRoom,
            Pageable pageable
    );
}
