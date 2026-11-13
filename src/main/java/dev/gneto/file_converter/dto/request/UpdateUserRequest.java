package dev.gneto.file_converter.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateUserRequest(
        JsonNullable<@NotBlank  String> firstName,
        JsonNullable<@NotBlank String> lastName
) { }
