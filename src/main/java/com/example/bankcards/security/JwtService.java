package com.example.bankcards.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String b64Secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        byte[] decoded = Decoders.BASE64.decode(b64Secret);
        this.key = Keys.hmacShaKeyFor(decoded);
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMinutes * 60 * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
