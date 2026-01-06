package com.example.chat_application.controller;

import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message/read")
public class MessageReadController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageReadController(MessageService messageService,
                                 UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public String markAsRead(@RequestParam Long messageId,
                             Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException("Unauthenticated");
        }

        // ✅ JWT principal = username (String)
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        messageService.markMessageAsRead(messageId, user);

        return "Message marked as READ";
    }
}
