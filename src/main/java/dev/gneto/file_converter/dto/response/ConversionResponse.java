package dev.gneto.file_converter.dto.response;

import dev.gneto.file_converter.enums.ConversionStatus;
import dev.gneto.file_converter.enums.FileFormat;

import java.time.Instant;
import java.util.UUID;

public record ConversionResponse(
        UUID id,
        ConversionStatus status,
        String filename,
        Instant createdAt,
        FileFormat inputFormat,
        FileFormat outputFormat
) { }
