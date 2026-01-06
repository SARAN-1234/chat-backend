package com.example.chat_application.controller;

import com.example.chat_application.dto.EmailRequestDTO;
import com.example.chat_application.dto.EmailResponseDTO;
import com.example.chat_application.service.EmailAiService;
import com.example.chat_application.service.EmailPromptBuilder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/ai/email")
public class AiEmailController {

    private final EmailAiService emailAiService;

    public AiEmailController(EmailAiService emailAiService) {
        this.emailAiService = emailAiService;
    }

    @PostMapping("/generate")
    public EmailResponseDTO generate(@RequestBody EmailRequestDTO req) {

        String aiText = emailAiService.generateEmail(
                req.getRecipient(),
                req.getPurpose(),
                req.getSenderName()
        );

        return new EmailResponseDTO("AI Generated Email", aiText);
    }

}
