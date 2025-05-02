package com.example.backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {


 
    private static final  long expirationTime = 86400000 ; // Loaded in milliseconds

    
    private static final String SECRET_KEY = "E0gBPl63QkWxOgoxP9XKKeRXboltmdbrq2DmtMKWfUWp2TwHjjot3sV8p397a4gi";
    private static final SecretKey JWT_SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    /**
     * Generates a JWT token with the user's email.
     * @param email User's email.
     * @return Signed JWT token.
     */
    
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
        .subject(email) 
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(JWT_SECRET_KEY, Jwts.SIG.HS256) 
        .compact();

    }

    /**
     * Extracts the email (subject) from the JWT token.
     * @param token JWT token.
     * @return Email address.
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validates the token and checks if the email matches and the token isn't expired.
     * @param token JWT token.
     * @param email User's email to verify.
     * @return true if valid, false otherwise.
     */
    public boolean validateToken(String token, String email) {
        try {
            return email.equals(extractEmail(token)) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; 
        }
    }

    /**
     * Extracts claims from the token.
     * @param token JWT token.
     * @return Claims object.
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
        .verifyWith(JWT_SECRET_KEY) 
        .build()
        .parseSignedClaims(token)
        .getPayload();

    }

    /**
     * Checks if the token is expired.
     * @param token JWT token.
     * @return true if expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}