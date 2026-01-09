package com.example.chat_application.config;

import com.example.chat_application.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ✅ Disable CSRF (REST + WebSocket)
                .csrf(csrf -> csrf.disable())

                // ✅ Stateless JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Authorization
                .authorizeHttpRequests(auth -> auth

                        // Preflight
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // 🔓 PUBLIC REST
                        .requestMatchers(
                                "/auth/**",
                                "/error",
                                "/users/search"
                        ).permitAll()

                        // 🔓 WEBSOCKET (CRITICAL)
                        .requestMatchers(
                                "/ws/**",
                                "/ws/info/**",
                                "/app/**",      // ✅ STOMP SEND
                                "/topic/**",    // ✅ STOMP SUBSCRIBE
                                "/user/**"      // ✅ PRIVATE QUEUE
                        ).permitAll()

                        // 🔒 AUTHENTICATED REST
                        .requestMatchers(
                                "/users/public-key",
                                "/users/presence",
                                "/profile/**",
                                "/profile/users/*/public-key",
                                "/chatroom/**",
                                "/message/**",
                                "/email/**",
                                "/ai/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

                // ✅ JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
