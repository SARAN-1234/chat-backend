package com.example.chat_application.repository;

import com.example.chat_application.model.ChatRequest;
import com.example.chat_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ChatRequestRepository extends JpaRepository<ChatRequest, Long> {

    Optional<ChatRequest> findBySenderAndReceiver(User sender, User receiver);

    List<ChatRequest> findByReceiverAndStatus(
            User receiver, ChatRequest.RequestStatus status
    );
}