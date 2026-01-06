package com.example.chat_application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailAiService {

    private final WebClient webClient;

    public EmailAiService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateEmail(String recipient, String purpose, String senderName) {

        String prompt = """
        Write a professional email.

        Recipient: %s
        Purpose: %s
        Sender: %s
        """.formatted(recipient, purpose, senderName);

        Map<String, Object> body = Map.of(
                "model", "phi3",
                "prompt", prompt,
                "stream", false
        );

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("response").toString())
                .block();
    }
}
