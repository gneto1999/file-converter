package dev.gneto.file_converter.dto;

import org.springframework.core.io.Resource;

public record FileDownload(String originalFilename, Resource resource) {
}
