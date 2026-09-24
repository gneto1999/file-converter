package dev.gneto.file_converter.dto;

import dev.gneto.file_converter.enums.FileFormat;

import java.nio.file.Path;
import java.util.UUID;

public record StoredFile(
    UUID id,
    String originalFileName,
    String storedFileName,
    FileFormat fileFormat,
    Path path
) { }
