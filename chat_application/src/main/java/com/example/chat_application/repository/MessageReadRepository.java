package com.example.chat_application.repository;

import com.example.chat_application.model.Message;
import com.example.chat_application.model.MessageRead;
import com.example.chat_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {

    boolean existsByMessageAndUser(Message message, User user);
}
