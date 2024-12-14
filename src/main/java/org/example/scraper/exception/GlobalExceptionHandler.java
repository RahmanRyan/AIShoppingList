package org.example.scraper.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(401)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(FirebaseAuthException.class)
    public ResponseEntity<?> handleFirebaseAuthException(FirebaseAuthException e) {
        return ResponseEntity.status(401)
                .body(Map.of("error", "Authentication failed: " + e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.status(500)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}