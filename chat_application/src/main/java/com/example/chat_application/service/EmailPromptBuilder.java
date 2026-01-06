package com.example.chat_application.service;

public class EmailPromptBuilder {

    public static String buildPrompt(
            String purpose,
            String tone,
            String recipient,
            String sender
    ) {
        return """
        Write a %s email.

        Purpose: %s
        Recipient: %s
        Sender Name: %s

        Requirements:
        - Clear subject line
        - Professional structure
        - Polite closing
        - No markdown
        - Ready to send
        """.formatted(tone, purpose, recipient, sender);
    }
}
