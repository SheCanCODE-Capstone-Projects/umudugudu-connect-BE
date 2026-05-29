package com.umudugudu.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiry-ms}")
    private long refreshExpiryMs;

    private SecretKey getKey() {
        // Decoders.BASE64 is safer than getBytes() — secret must be Base64-encoded in your .env
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateAccessToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No role assigned to user"))
                .getAuthority();
        return buildToken(userDetails.getUsername(), role, jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No role assigned to user"))
                .getAuthority();
        return buildToken(userDetails.getUsername(), role, refreshExpiryMs);
    }

    private String buildToken(String subject, String role, long expiry) {
        return Jwts.builder()
                .subject(subject)                                               // ✅ replaces setSubject()
                .claim("role", role)
                .issuedAt(new Date())                                           // ✅ replaces setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + expiry))      // ✅ replaces setExpiration()
                .signWith(getKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()                    // ✅ replaces parserBuilder()
                .verifyWith(getKey())                          // ✅ replaces setSigningKey()
                .build()
                .parseSignedClaims(token)                      // ✅ replaces parseClaimsJws()
                .getPayload();                                 // ✅ replaces getBody()
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !extractClaim(token, Claims::getExpiration).before(new Date());
    }
}