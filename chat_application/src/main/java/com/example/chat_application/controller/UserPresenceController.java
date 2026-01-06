package com.example.chat_application.controller;

import com.example.chat_application.dto.UserPresenceDTO;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserPresenceController {

    private final UserRepository userRepository;

    public UserPresenceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ✅ Initial presence snapshot (READ-ONLY)
     */
    @GetMapping("/presence")
    public List<UserPresenceDTO> getAllUserPresence() {
        return userRepository.findAll().stream()
                .map(user -> new UserPresenceDTO(
                        user.getId(),
                        user.getStatus() != null
                                ? user.getStatus().name()
                                : "OFFLINE",          // ✅ DEFAULT
                        user.getLastSeen()
                ))
                .collect(Collectors.toList());
    }

}
