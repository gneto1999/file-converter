package dev.gneto.file_converter.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateUserByAdminRequest(
        JsonNullable<@NotBlank String> firstName,
        JsonNullable<@NotBlank String> lastName,
        JsonNullable<@NotBlank String> email
) { }
