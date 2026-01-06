package com.example.chat_application.controller;

import com.example.chat_application.dto.MessageResponseDTO;
import com.example.chat_application.model.Message;
import com.example.chat_application.model.MessageType;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.MessageService;
import com.example.chat_application.service.ProfileGuardService;
import com.example.chat_application.util.MessageMapper;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;
    private final ProfileGuardService profileGuardService;
    private final UserRepository userRepository;

    public MessageController(
            MessageService messageService,
            ProfileGuardService profileGuardService,
            UserRepository userRepository
    ) {
        this.messageService = messageService;
        this.profileGuardService = profileGuardService;
        this.userRepository = userRepository;
    }

    /* =====================================================
       📥 FETCH MESSAGES (E2EE – NO DECRYPTION)
       ===================================================== */
    @GetMapping("/chat/{chatRoomId}")
    public List<MessageResponseDTO> getMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Principal principal
    ) {
        if (principal == null) {
            throw new RuntimeException("Unauthenticated");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        profileGuardService.checkProfileCompleted(user);

        return messageService
                .getMessages(chatRoomId, user, page, size)
                .stream()

                // 🔥 HARD SAFETY: exclude legacy at controller level
                .filter(m -> m.getType() != MessageType.LEGACY)

                // Map entity → DTO
                .map(MessageMapper::toDto)

                // 🔥 Remove nulls defensively
                .filter(Objects::nonNull)

                .collect(Collectors.toList());
    }
}
