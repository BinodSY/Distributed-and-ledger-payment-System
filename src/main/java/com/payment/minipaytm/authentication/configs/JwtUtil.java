package com.payment.minipaytm.authentication.configs;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component // Added missing annotation so Spring can manage this bean
public class JwtUtil {

    @Value("${jwt.secret}") 
    private String secret;

    @Value("${jwt.expireMs}") 
    private long expireMs; 

    private SecretKey getSignKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}


    public String generateToken(String email,UUID userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMs);

        return Jwts.builder()
                .subject(email)
                .claim(email,userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSignKey()) 
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token validation error: " + e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey()) // Updated for JJWT 0.12.x
                .build()
                .parseSignedClaims(token) // Updated for JJWT 0.12.x
                .getPayload();           // Updated for JJWT 0.12.x
    }
}