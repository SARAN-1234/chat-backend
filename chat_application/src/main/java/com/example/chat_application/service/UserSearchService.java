package com.example.chat_application.service;

import com.example.chat_application.dto.UserResponseDTO;
import com.example.chat_application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserSearchService {

    private final UserRepository userRepository;

    public UserSearchService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> searchUsers(String keyword) {

        return userRepository.searchPublicUsers(keyword)
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername()
                ))
                .toList();
    }
}
