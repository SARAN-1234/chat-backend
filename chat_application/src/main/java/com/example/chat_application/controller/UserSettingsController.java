package com.example.chat_application.controller;

import com.example.chat_application.dto.UpdateUserSettingsRequest;
import com.example.chat_application.dto.UserSettingsResponse;
import com.example.chat_application.model.User;
import com.example.chat_application.model.UserSettings;
import com.example.chat_application.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    /**
     * ⚙️ Get current user's settings
     * Auto-creates settings if not present
     */
    @GetMapping
    public UserSettingsResponse getSettings(
            @AuthenticationPrincipal User user
    ) {
        UserSettings settings = userSettingsService.getSettings(user);

        UserSettingsResponse response = new UserSettingsResponse();
        response.setAudioEnabled(settings.isAudioEnabled());
        response.setVideoEnabled(settings.isVideoEnabled());
        response.setAllowUnknownCalls(settings.isAllowUnknownCalls());
        response.setAllowGroupCalls(settings.isAllowGroupCalls());
        response.setConnectionPolicy(settings.getConnectionPolicy());

        return response;
    }

    /**
     * ⚙️ Update settings (editable anytime)
     */
    @PutMapping
    public void updateSettings(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateUserSettingsRequest request
    ) {
        UserSettings updated = new UserSettings();
        updated.setAudioEnabled(request.isAudioEnabled());
        updated.setVideoEnabled(request.isVideoEnabled());
        updated.setAllowUnknownCalls(request.isAllowUnknownCalls());
        updated.setAllowGroupCalls(request.isAllowGroupCalls());
        updated.setConnectionPolicy(request.getConnectionPolicy());

        userSettingsService.updateSettings(user, updated);
    }
}
