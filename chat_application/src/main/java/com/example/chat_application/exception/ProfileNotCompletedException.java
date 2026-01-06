package com.example.chat_application.exception;

public class ProfileNotCompletedException extends RuntimeException {

    public ProfileNotCompletedException(String s) {
        super("Profile setup required before accessing chat");
    }
}
