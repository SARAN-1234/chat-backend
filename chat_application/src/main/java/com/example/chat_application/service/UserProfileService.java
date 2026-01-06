package com.example.chat_application.service;

import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    @Transactional
    public void completeProfile(
            String username,
            String displayName,
            String phoneNumber,
            String profileImageUrl,
            String bio
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 PRESERVE EXISTING PUBLIC KEY
        String existingPublicKey = user.getPublicKey();

        user.setDisplayName(displayName);
        user.setPhoneNumber(phoneNumber);
        user.setProfileImageUrl(profileImageUrl);
        user.setBio(bio);
        user.setProfileCompleted(true);

        // 🔥 RESTORE IT BEFORE SAVE
        user.setPublicKey(existingPublicKey);

        userRepository.save(user);
    }

    public boolean isProfileCompleted(String username) {
        return userRepository.findByUsername(username)
                .map(User::isProfileCompleted)
                .orElse(false);
    }
}
