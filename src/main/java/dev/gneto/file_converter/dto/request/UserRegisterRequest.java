package dev.gneto.file_converter.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record UserRegisterRequest(
        @NotEmpty String email,
        @NotEmpty String firstName,
        @NotEmpty String lastName,
        @NotEmpty String password
) { }
