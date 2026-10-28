package dev.gneto.file_converter.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record ExceptionResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING) Instant timestamp,
        Integer statusCode,
        String message,
        String path
) { }
