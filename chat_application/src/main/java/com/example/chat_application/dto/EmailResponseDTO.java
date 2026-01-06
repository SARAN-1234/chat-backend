package com.example.chat_application.dto;

public class EmailResponseDTO {
    private String subject;
    private String body;

    public EmailResponseDTO(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() { return subject; }
    public String getBody() { return body; }
}
