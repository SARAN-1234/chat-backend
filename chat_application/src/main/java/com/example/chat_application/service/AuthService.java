package com.example.chat_application.service;

import com.example.chat_application.dto.LoginResponse;
import com.example.chat_application.model.User;
import com.example.chat_application.repository.UserRepository;
import com.example.chat_application.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    /**
     * ✅ LOGIN (JWT + USER INFO + PROFILE STATE)
     * ❗ Presence is NOT handled here
     */
    public LoginResponse login(String username, String password) {

        // 1️⃣ Fetch user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // ❌ REMOVED ONLINE/OFFLINE LOGIC (handled by WebSocket)
        // user.setStatus(User.Status.ONLINE);
        // user.setLastSeen(LocalDateTime.now());
        // userRepository.save(user);

        // 3️⃣ Generate JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // 4️⃣ Return login response
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.isProfileCompleted()
        );
    }

    /**
     * ✅ SIGNUP
     */
    public void signup(String username, String email, String password) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(User.Status.OFFLINE); // default
        user.setProfileCompleted(false);

        userRepository.save(user);
    }
}
