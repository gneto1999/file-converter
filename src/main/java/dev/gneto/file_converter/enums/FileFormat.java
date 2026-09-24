package dev.gneto.file_converter.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FileFormat {
    PDF("pdf"),
    DOCX("docx");

    private final String extension;

    FileFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<FileFormat> fromExtension(String extension) {
        return Arrays.stream(values())
                .filter(format -> format.extension.equalsIgnoreCase(extension))
                .findFirst();
    }
}
