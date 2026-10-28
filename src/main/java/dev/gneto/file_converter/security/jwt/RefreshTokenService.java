package dev.gneto.file_converter.security.jwt;

import dev.gneto.file_converter.dto.JwtToken;
import dev.gneto.file_converter.exception.InvalidTokenException;
import dev.gneto.file_converter.model.RefreshToken;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.repository.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public void registerRefreshToken(JwtToken jwtToken, User user) {
        String hashedToken = sha256(jwtToken.token());

        repository.save(new RefreshToken(hashedToken, user, jwtToken.expiresAt()));
    }

    @Transactional(noRollbackFor = InvalidTokenException.class)
    public void rotate(String oldRefreshToken, JwtToken refreshToken, User user) {
        RefreshToken storedToken = repository.findByHashToken(sha256(oldRefreshToken))
                .orElseThrow(() ->  new InvalidTokenException("Invalid refresh token."));

        if (storedToken.isRevoked()) {
            repository.revokeAllByUser(user);

            throw new InvalidTokenException("The refresh token has been revoked.");
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("The refresh token has expired.");
        }

        storedToken.revokeToken();

        String hashedToken = sha256(refreshToken.token());

        repository.save(new RefreshToken(hashedToken, user, refreshToken.expiresAt()));
    }

    @Transactional
    public void revoke(String refreshToken) {
        RefreshToken storedToken = repository.findByHashToken(sha256(refreshToken))
                .orElseThrow(() ->  new InvalidTokenException("Invalid refresh token."));

        storedToken.revokeToken();
    }

    @Transactional
    public void revokeAll(User user) {
        repository.revokeAllByUser(user);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpired() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(30));
        repository.deleteExpiredBefore(cutoff);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não está disponível", ex);
        }
    }
}
