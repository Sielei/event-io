package com.hs.eventio.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Component
class JwtUtil {

    private static final String SECRET = "pYXQiOjEMsnVz5AYXJvbXMNjc2OTgyNTUzfQ";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateJWTToken(AuthDTO.FindUserResponse userDto){
        return Jwts.builder()
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .issuer("Lunar HR")
                .claim("userId", userDto.id())
                .claim("username", userDto.email())
//                .setExpiration(Date.from(ZonedDateTime.now().plusHours(1).toInstant()))
                .expiration(Date.from(ZonedDateTime.now().plusDays(30).toInstant()))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateJWTToken(String token){
        return getUsername(token) != null && isExpired(token);
    }

    private boolean isExpired(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis()));
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        return claims.get("username").toString();
    }
    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }
    public UUID getUserIdFromJWTToken(String token){
        return UUID.fromString(getClaims(token).get("userId").toString());
    }
}
