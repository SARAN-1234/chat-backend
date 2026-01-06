package com.example.chat_application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    private String displayName;
    private String phoneNumber;
    private String profileImageUrl;
    private String bio;
}
