package com.example.chat_application.dto;

import com.example.chat_application.model.UserSettings.ConnectionPolicy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSettingsResponse {

    private boolean audioEnabled;
    private boolean videoEnabled;
    private boolean allowUnknownCalls;
    private boolean allowGroupCalls;

    private ConnectionPolicy connectionPolicy;
}
