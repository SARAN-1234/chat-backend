package com.example.chat_application.service;

import com.example.chat_application.exception.ProfileNotCompletedException;
import com.example.chat_application.model.User;
import org.springframework.stereotype.Service;

@Service
public class ProfileGuardService {

    public void checkProfileCompleted(User user) {

        // 🔐 Authentication safety
        if (user == null) {
            throw new IllegalStateException("Unauthenticated user");
        }

        // 🚧 Business rule: profile must be completed
        if (!user.isProfileCompleted()) {
            throw new ProfileNotCompletedException("Please complete your profile before using chat features");
        }
    }
}
