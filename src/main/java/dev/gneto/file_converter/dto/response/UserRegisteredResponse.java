package dev.gneto.file_converter.dto.response;

import dev.gneto.file_converter.enums.UserRole;

import java.util.UUID;

public record UserRegisteredResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) { }
