package dev.gneto.file_converter.dto.response;

import dev.gneto.file_converter.enums.ConversionStatus;
import dev.gneto.file_converter.enums.FileFormat;

import java.util.UUID;

public record ConversionResponse(UUID id, ConversionStatus status, FileFormat inputFormat, FileFormat outputFormat) { }
