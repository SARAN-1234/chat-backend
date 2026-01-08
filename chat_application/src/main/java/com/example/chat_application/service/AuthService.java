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

    public AuthService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /* =========================
       ✅ LOGIN (DEBUG ENABLED)
       ========================= */
    public LoginResponse login(String username, String password) {

        System.out.println("🟡 LOGIN ATTEMPT");
        System.out.println("➡️ Raw username input : [" + username + "]");
        System.out.println("➡️ Raw password input : [" + password + "]");

        // 🔴 Normalize input
        String normalizedUsername = username.trim().toLowerCase();
        String normalizedPassword = password.trim();

        System.out.println("✅ Normalized username : [" + normalizedUsername + "]");

        User user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> {
                    System.out.println(
                            "❌ USER NOT FOUND IN DB for username: " + normalizedUsername
                    );
                    return new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid username or password"
                    );
                });

        System.out.println("✅ USER FOUND IN DB");
        System.out.println("➡️ DB username : " + user.getUsername());
        System.out.println("➡️ DB password : " + user.getPassword());

        boolean passwordMatch =
                passwordEncoder.matches(normalizedPassword, user.getPassword());

        System.out.println("🔐 Password match result: " + passwordMatch);

        if (!passwordMatch) {
            System.out.println("❌ PASSWORD MISMATCH");
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password"
            );
        }

        String token = jwtUtil.generateToken(user.getUsername());
        System.out.println("✅ JWT GENERATED SUCCESSFULLY");

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.isProfileCompleted()
        );
    }


    /* =========================
       ✅ SIGNUP (DEBUG ENABLED)
       ========================= */
    public void signup(String username, String email, String password) {

        System.out.println("🟢 SIGNUP ATTEMPT");
        System.out.println("➡️ Raw username : [" + username + "]");
        System.out.println("➡️ Raw email    : [" + email + "]");

        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();
        password = password.trim();

        System.out.println("✅ Normalized username : [" + username + "]");
        System.out.println("✅ Normalized email    : [" + email + "]");

        if (userRepository.existsByUsername(username)) {
            System.out.println("❌ USERNAME ALREADY EXISTS");
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(email)) {
            System.out.println("❌ EMAIL ALREADY EXISTS");
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(User.Status.OFFLINE);
        user.setProfileCompleted(false);

        userRepository.save(user);

        System.out.println("✅ USER SAVED SUCCESSFULLY");
    }
}
