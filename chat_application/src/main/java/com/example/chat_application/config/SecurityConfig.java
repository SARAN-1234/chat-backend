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

                // ✅ Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // 🔓 PUBLIC endpoints
                        .requestMatchers(
                                "/auth/**",
                                "/ws/**",
                                "/ws/info/**",
                                "/error",
                                "/users/search"   // ✅ ONLY search is public
                        ).permitAll()

                        // 🔒 AUTHENTICATED endpoints
                        .requestMatchers(
                                "/users/public-key",
                                "/users/presence",
                                "/profile/**",
                                "/profile/users/*/public-key",// ✅ FIX
                                "/chatroom/**",
                                "/message/**",
                                "/email/**",
                                "/ai/**"
                        ).authenticated()

                        // 🔒 Everything else
                        .anyRequest().authenticated()
                )


                // ✅ JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
