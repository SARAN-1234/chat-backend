package com.example.chat_application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class
GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotCompletedException.class)
    public ResponseEntity<Map<String, Object>> handleProfileNotCompleted(
            ProfileNotCompletedException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 403,
                        "error", "FORBIDDEN",
                        "message", ex.getMessage()
                ));
    }
}
