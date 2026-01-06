package com.example.chat_application.controller;

import com.example.chat_application.dto.UpdateProfileRequest;
import com.example.chat_application.dto.UserProfileResponse;
import com.example.chat_application.dto.PublicKeyRequest;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.service.UserProfileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    /* ===============================
       👤 GET PROFILE (FIXED)
       =============================== */
    @GetMapping
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal String username
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse response = new UserProfileResponse();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setProfileImageUrl(user.getProfileImageUrl());
        response.setBio(user.getBio());
        response.setProfileCompleted(user.isProfileCompleted());

        // 🔐 CRITICAL FOR E2EE HISTORY
        response.setPublicKey(user.getPublicKey());

        return response;
    }

    /* ===============================
       ✏️ COMPLETE PROFILE
       =============================== */
    @PutMapping
    public void completeProfile(
            @AuthenticationPrincipal String username,
            @RequestBody UpdateProfileRequest request
    ) {
        userProfileService.completeProfile(
                username,
                request.getDisplayName(),
                request.getPhoneNumber(),
                request.getProfileImageUrl(),
                request.getBio()
        );
    }

    /* ===============================
       ✅ PROFILE COMPLETION CHECK
       =============================== */
    @GetMapping("/completed")
    public Map<String, Boolean> isProfileCompleted(
            @AuthenticationPrincipal String username
    ) {
        return Map.of(
                "completed",
                userProfileService.isProfileCompleted(username)
        );
    }

    /* ===============================
       🔐 SAVE PUBLIC KEY (PUT ONLY)
       =============================== */
    @PutMapping("/public-key")
    public void savePublicKey(@RequestBody PublicKeyRequest request) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPublicKey(request.getPublicKey());
        userRepository.save(user);
    }

    /* ===============================
       🔑 GET PUBLIC KEY BY USER ID
       =============================== */
    @Transactional(readOnly = true)
    @GetMapping("/users/{id}/public-key")
    public ResponseEntity<Map<String, String>> getUserPublicKey(
            @PathVariable Long id
    ) {
        return userRepository.findById(id)
                .filter(u -> u.getPublicKey() != null)
                .map(u -> ResponseEntity.ok(
                        Map.of("publicKey", u.getPublicKey())
                ))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("error", "Public key not found"))
                );
    }
}
