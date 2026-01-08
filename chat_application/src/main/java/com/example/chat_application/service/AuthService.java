package com.example.chat_application.service;

import com.example.chat_application.dto.LoginResponse;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /* =========================
       ✅ LOGIN (FINAL FIXED)
       ========================= */
    public LoginResponse login(String username, String password) {

        // 🔴 CRITICAL FIX — NORMALIZE INPUT
        username = username.trim().toLowerCase();
        password = password.trim();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.isProfileCompleted()
        );
    }

    /* =========================
       ✅ SIGNUP (FINAL FIXED)
       ========================= */
    public void signup(String username, String email, String password) {

        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();
        password = password.trim();

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(User.Status.OFFLINE);
        user.setProfileCompleted(false);

        userRepository.save(user);
    }
}
