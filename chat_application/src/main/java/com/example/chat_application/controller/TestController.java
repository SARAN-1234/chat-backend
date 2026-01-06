package com.example.chat_application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "JWT is working. You are authenticated!";
    }
}
