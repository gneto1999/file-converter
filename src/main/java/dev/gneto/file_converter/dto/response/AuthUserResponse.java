package dev.gneto.file_converter.dto.response;

import java.time.Instant;

public record AuthUserResponse(String accessToken, String refreshToken, String subject, Instant issueAt, Instant expiresAt) {

}
