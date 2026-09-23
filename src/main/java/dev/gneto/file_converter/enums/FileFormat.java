package dev.gneto.file_converter.enums;

public enum FileFormat {
    PDF("pdf"),
    DOCX("docx");

    private final String format;

    FileFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
