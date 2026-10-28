package dev.gneto.file_converter.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record AuthUserRequest(@NotEmpty String email, @NotEmpty String password) {
}
