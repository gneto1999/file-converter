package dev.gneto.file_converter.security.jwt;

import dev.gneto.file_converter.dto.JwtToken;
import dev.gneto.file_converter.enums.JwtTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${security.jwt.token.issuer}")
    private String issuer;

    @Value("${security.jwt.token.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.token.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final SecretKey key;

    public JwtUtil(@Value("${security.jwt.token.secret-key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public JwtToken generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), JwtTokenType.ACCESS_TOKEN, accessTokenExpiration);
    }

    public JwtToken generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername(), JwtTokenType.REFRESH_TOKEN, refreshTokenExpiration);
    }

    private JwtToken generateToken(String username, JwtTokenType tokenType, long expiration) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(expiration);

        String token = createToken(username, issuedAt, expiresAt, tokenType);

        return new JwtToken(token, username, issuedAt, expiresAt);
    }

    private String createToken(String username, Instant issuedAt, Instant expiresAt, JwtTokenType tokenType) {
        return Jwts.builder()
                .claim("type", tokenType)
                .subject(username).issuedAt(Date.from(issuedAt))
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .issuer(issuer)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(Date.from(Instant.now()));
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
