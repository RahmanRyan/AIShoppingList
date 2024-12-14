package org.example.scraper.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.example.scraper.exception.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public String verifyToken(String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new AuthenticationException("Invalid token format");
            }

            String tokenValue = token.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenValue);
            return decodedToken.getUid();
        } catch (FirebaseAuthException e) {
            throw new AuthenticationException("Invalid token", e);
        }
    }
}