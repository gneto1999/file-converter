package dev.gneto.file_converter.dto;

import java.time.Instant;

public record JwtToken(
        String token,
        String subject,
        Instant issueAt,
        Instant expiresAt
) { }