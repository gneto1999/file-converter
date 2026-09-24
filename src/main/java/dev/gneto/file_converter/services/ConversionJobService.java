package dev.gneto.file_converter.services;

import dev.gneto.file_converter.config.FileStorageConfig;
import dev.gneto.file_converter.dto.StoredFile;
import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.enums.FileFormat;
import dev.gneto.file_converter.exception.ConversionJobNotFoundException;
import dev.gneto.file_converter.exception.FileStorageException;
import dev.gneto.file_converter.mapper.ConversionJobMapper;
import dev.gneto.file_converter.model.ConversionJob;
import dev.gneto.file_converter.repository.ConversionJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

@Service
public class ConversionJobService {

    private final Path inputDir;
    private final Path outputDir;

    private final ConversionJobRepository repository;

    private final ConversionJobMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(ConversionJobService.class);

    public ConversionJobService(
            FileStorageConfig fileStorageConfig,
            ConversionJobRepository repository,
            ConversionJobMapper mapper
    ) {
        String inputDirName = fileStorageConfig.getInputDir();
        String outputDirName = fileStorageConfig.getOutputDir();

        if(inputDirName == null || inputDirName.isBlank() && outputDirName == null || outputDirName.isBlank()){
            throw new FileStorageException("File upload location cannot be empty.");
        }

        this.inputDir = Paths.get(inputDirName).toAbsolutePath().normalize();
        this.outputDir = Paths.get(outputDirName).toAbsolutePath().normalize();

        this.repository = repository;

        this.mapper = mapper;
    }

    public ConversionResponse createConversion(MultipartFile file, String outputExtension) {
        StoredFile storedFile = store(file);
        FileFormat outputFormat = FileFormat.fromExtension(outputExtension)
                .orElseThrow(() -> new FileStorageException("Unsupported file format: " + outputExtension + "."));

        ConversionJob conversionJob = new ConversionJob(
                storedFile.originalFileName(),
                storedFile.fileFormat(),
                outputFormat,
                storedFile.path().toString()
        );

        ConversionJob jobSaved = repository.save(conversionJob);

        // Falta publicar mensagem no rabbitmq

        return mapper.conversionJobToConversionResponse(jobSaved);
    }

    private StoredFile store(MultipartFile file) {
        try {
            String originalFilename = validateFile(file);

            FileFormat fileFormat = extractFileFormat(originalFilename);

            UUID conversionId = UUID.randomUUID();

            String storedFileName = conversionId + "." + fileFormat.getExtension();

            Path destinationFile = inputDir.resolve(storedFileName).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(inputDir)) {
                throw new FileStorageException("Cannot store file outside input directory.");
            }

            logger.info("Saving file...");

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile);
            }

            logger.info("File stored.");

            return new StoredFile(conversionId, originalFilename, storedFileName, fileFormat, destinationFile);
        } catch (IOException e) {
            throw new FileStorageException("Could not store uploaded file.", e);
        }
    }

    private FileFormat extractFileFormat(String filename) {
        int index = filename.lastIndexOf('.');

        if (index == -1 || index == filename.length() - 1) {
            throw new FileStorageException("Unable to identify the file format.");
        }

        String extension = filename.substring(index + 1).toLowerCase(Locale.ROOT);

        return FileFormat.fromExtension(extension)
                .orElseThrow(() -> new FileStorageException("Unsupported file format: " + extension + "."));
    }

    private String validateFile(MultipartFile file) {
        logger.info("Validating the file.");

        if (file == null) {
            throw new FileStorageException("Uploaded file cannot be null.");
        }

        if (file.isEmpty()) {
            throw new FileStorageException("Uploaded file cannot be empty.");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new FileStorageException("File name can not be empty.");
        }

        return originalFilename;
    }

    public ConversionResponse conversionStatus(UUID id) {
        var conversionJob = repository.findByPublicId(id)
                .orElseThrow(() -> new ConversionJobNotFoundException(""));

        return mapper.conversionJobToConversionResponse(conversionJob);
    }

    public void initStorage() {
        try {
            logger.info("Creating Directories.");

            Files.createDirectories(this.inputDir);
            Files.createDirectories(this.outputDir);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory.", e);
        }
    }
}
