package com.team13.TutorFind.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // Default: 24 hours
    private Long expiration;
    
    // Generate token
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        
        return createToken(claims, email);
    }
    
    // Create token with claims
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Validate token
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Extract email from token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    // Extract userId from token
    public Long extractUserId(String token) {
        Claims claims = extractClaims(token);
        return claims.get("userId", Long.class);
    }
    
    // Extract role from token
    public String extractRole(String token) {
        Claims claims = extractClaims(token);
        return claims.get("role", String.class);
    }
    
    // Extract all claims
    private Claims extractClaims(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}