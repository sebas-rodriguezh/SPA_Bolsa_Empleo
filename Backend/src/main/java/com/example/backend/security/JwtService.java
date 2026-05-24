package com.example.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(String correo, String rol)
    {
        return Jwts.builder()
                .subject(correo + "::" + rol)
                .claim("rol", rol) // EMPRESA, OFERENTE o ADMIN
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        String subject = extractAllClaims(token).getSubject();
        return subject.split("::")[0];
    }

    public String extractRolFromSubject(String token) {
        String subject = extractAllClaims(token).getSubject();
        String[] parts = subject.split("::");
        return parts.length > 1 ? parts[1] : null;
    }

    public String extractRol(String token) {
        return (String) extractAllClaims(token).get("rol");
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String subject = extractAllClaims(token).getSubject();
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String extraerCorreo(String nombre) {
        return nombre.contains("::") ? nombre.split("::")[0] : nombre;
    }
}