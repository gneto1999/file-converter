package dev.gneto.file_converter.dto.response;

import dev.gneto.file_converter.enums.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserRole role
) { }
