package com.railconnect.security;

import com.railconnect.common.constants.SecurityConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey jwtSecretKey;

    public JwtService(@Value("${railconnect.jwt.secret}") String secret) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ---- Access token ----

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateToken(userPrincipal.getUsername());
    }

    public String generateToken(String username) {
        return buildToken(username, SecurityConstants.JWT_EXPIRATION_MS);
    }

    // ---- Refresh token ----
    // Note: this issues a self-contained JWT with a longer expiry, used as the *signature*
    // check for a refresh token. The actual revocation/lookup source of truth is the
    // RefreshToken row in the database (see RefreshTokenService) — this token's JWT
    // signature alone does not mean it's still valid, only that it hasn't been tampered with.

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateRefreshToken(userPrincipal.getUsername());
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, SecurityConstants.REFRESH_TOKEN_EXPIRATION_MS);
    }

    private String buildToken(String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtSecretKey)
                .compact();
    }

    // ---- Reading claims ----

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ---- Validation ----

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token structure.");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token.");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token type.");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
