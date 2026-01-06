package com.example.chat_application.controller;

import com.example.chat_application.dto.SendEmailRequest;
import com.example.chat_application.service.EmailSendService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailSendController {

    private final EmailSendService emailSendService;

    public EmailSendController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody SendEmailRequest request) {
        emailSendService.sendEmail(request);
        return "Email sent successfully";
    }
}
