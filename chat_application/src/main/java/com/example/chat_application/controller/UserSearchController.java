package com.example.chat_application.controller;

import com.example.chat_application.dto.UserResponseDTO;
import com.example.chat_application.service.UserSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserSearchController {

    private final UserSearchService userSearchService;

    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    /**
     * 🔍 Search users by username or email
     * connection_policy:
     *   NULL      → PUBLIC
     *   EVERYONE  → PUBLIC
     *   OTHERS    → HIDDEN
     */
    @GetMapping("/search")
    public List<UserResponseDTO> searchUsers(
            @RequestParam("q") String query
    ) {
        return userSearchService.searchUsers(query);
    }
}
