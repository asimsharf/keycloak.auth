package com.sudagoarth.keycloak.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

import java.time.Instant;

@Component
public class JwtTokenProvider {

    private final JwtDecoder jwtDecoder;

    public JwtTokenProvider(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public Jwt parseToken(String token) {
        return jwtDecoder.decode(token);
    }

    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        if (userPrincipal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        return Jwt.withTokenValue("dummyToken") // Replace with actual logic
                .issuedAt(Instant.now()) // Using Instant instead of Date
                .expiresAt(Instant.now().plusSeconds(3600))  // Token expiry in 1 hour
                .build().getTokenValue();
    }
}
