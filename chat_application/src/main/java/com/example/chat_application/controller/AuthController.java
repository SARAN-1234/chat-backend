package com.example.chat_application.controller;

import com.example.chat_application.dto.LoginResponse;
import com.example.chat_application.service.AuthService;
import com.example.chat_application.dto.LoginRequest;
import com.example.chat_application.dto.SignupRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(
                request.getUsername(),
                request.getPassword()
        );
    }
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {

        authService.signup(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        return "User registered successfully";
    }
}
