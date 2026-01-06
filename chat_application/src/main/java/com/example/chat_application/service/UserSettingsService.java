package com.example.chat_application.service;

import com.example.chat_application.model.User;
import com.example.chat_application.model.UserSettings;
import com.example.chat_application.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    /**
     * Always returns settings (creates default if not exists)
     */
    public UserSettings getOrCreateSettings(User user) {
        return userSettingsRepository.findById(user.getId())
                .orElseGet(() -> {
                    UserSettings settings = new UserSettings();
                    settings.setUser(user);
                    return userSettingsRepository.save(settings);
                });
    }

    public UserSettings getSettings(User user) {
        return getOrCreateSettings(user);
    }

    @Transactional
    public void updateSettings(User user, UserSettings updated) {
        UserSettings existing = getOrCreateSettings(user);

        existing.setAudioEnabled(updated.isAudioEnabled());
        existing.setVideoEnabled(updated.isVideoEnabled());
        existing.setAllowUnknownCalls(updated.isAllowUnknownCalls());
        existing.setAllowGroupCalls(updated.isAllowGroupCalls());
        existing.setConnectionPolicy(updated.getConnectionPolicy());
    }

    /**
     * 🔐 IMPORTANT (future WebRTC)
     * Receiver settings ALWAYS override sender intention
     */
    public boolean isAudioAllowed(User receiver) {
        return getOrCreateSettings(receiver).isAudioEnabled();
    }

    public boolean isVideoAllowed(User receiver) {
        return getOrCreateSettings(receiver).isVideoEnabled();
    }
}
