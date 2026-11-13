package dev.gneto.file_converter.registry;

import dev.gneto.file_converter.enums.FileFormat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConversionFormatRegistry {

    private static final Map<FileFormat, Set<FileFormat>> SUPPORTED_CONVERSIONS =
            Map.of(
                    FileFormat.PDF, Set.of(
                            FileFormat.DOCX,
                            FileFormat.TEXT
                    ),
                    FileFormat.DOCX, Set.of(
                            FileFormat.PDF,
                            FileFormat.TEXT
                    ),
                    FileFormat.TEXT, Set.of(
                            FileFormat.PDF,
                            FileFormat.DOCX
                    )
            );

    public boolean isSupported(FileFormat input, FileFormat output) {
        return SUPPORTED_CONVERSIONS
                .getOrDefault(input, Set.of())
                .contains(output);
    }

    public Map<String, List<String>> getSupportedConversions() {
        return SUPPORTED_CONVERSIONS.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> entry.getKey().getExtension(),
                        entry -> entry.getValue()
                                .stream()
                                .map(FileFormat::getExtension)
                                .sorted()
                                .toList()
                ));
    }
}