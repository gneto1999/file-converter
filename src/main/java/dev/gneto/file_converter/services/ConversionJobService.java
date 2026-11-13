package dev.gneto.file_converter.services;

import dev.gneto.file_converter.config.RabbitMqConfig;
import dev.gneto.file_converter.dto.ConversionMessage;
import dev.gneto.file_converter.dto.FileDownload;
import dev.gneto.file_converter.dto.StoredFile;
import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.enums.ConversionStatus;
import dev.gneto.file_converter.enums.FileFormat;
import dev.gneto.file_converter.enums.UserRole;
import dev.gneto.file_converter.exception.ConversionException;
import dev.gneto.file_converter.exception.ConversionJobNotFoundException;
import dev.gneto.file_converter.mapper.ConversionJobMapper;
import dev.gneto.file_converter.model.ConversionJob;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.registry.ConversionFormatRegistry;
import dev.gneto.file_converter.repository.ConversionJobRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ConversionJobService {

    private final FileStorageService fileStorageService;
    private final ConversionJobRepository repository;
    private final ConversionJobMapper mapper;
    private final RabbitTemplate rabbitTemplate;
    private final ConversionFormatRegistry formatRegistry;

    private static final Logger logger = LoggerFactory.getLogger(ConversionJobService.class);

    public ConversionJobService(
            FileStorageService fileStorageService,
            ConversionJobRepository repository,
            ConversionJobMapper mapper,
            RabbitTemplate rabbitTemplate,
            ConversionFormatRegistry formatRegistry
    ) {
        this.fileStorageService = fileStorageService;
        this.repository = repository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
        this.formatRegistry = formatRegistry;
    }

    @Transactional
    public ConversionResponse createConversion(User authUser, MultipartFile file, String outputExtension) {
        FileFormat outputFormat = FileFormat.fromExtension(outputExtension)
                .orElseThrow(() -> new ConversionException("Conversion not supported: " + outputExtension));

        StoredFile storedFile = fileStorageService.store(file);

        ConversionJob conversionJob = new ConversionJob(
                storedFile.originalFileName(),
                storedFile.fileFormat(),
                outputFormat,
                storedFile.path().toString(),
                authUser
        );

        ConversionJob jobSaved = repository.save(conversionJob);

        logger.info("Sending message on queue.");

        ConversionMessage conversionMessage = new ConversionMessage(jobSaved.getId());

        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, conversionMessage);

        logger.info("Message sent on queue.");

        return mapper.conversionJobToConversionResponse(jobSaved);
    }

    public ConversionResponse conversionStatus(User authUser, UUID id) {
        var job = repository.findByPublicId(id)
                .orElseThrow(() -> new ConversionJobNotFoundException("Conversion job not found with ID: " + id));

        validateAccess(authUser, job);

        return mapper.conversionJobToConversionResponse(job);
    }

    public FileDownload downloadConversion(User authUser, UUID id) {
        ConversionJob job = repository.findByPublicId(id)
                .orElseThrow(() -> new ConversionJobNotFoundException("Conversion job not found with ID: " + id));

        validateAccess(authUser, job);

        if (job.getStatus() != ConversionStatus.DONE) {
            throw new ConversionException("It is not possible to download the file because the conversion has not yet been completed.");
        }

        Resource resource = fileStorageService.loadAsResource(job.getOutputPath());

        return new FileDownload(generateOutputFileName(job), resource);
    }

    public void deleteConversion(User authUser, UUID id) {
        ConversionJob job = repository.findByPublicId(id)
                .orElseThrow(() -> new ConversionJobNotFoundException("Conversion job not found with ID: " + id));

        validateAccess(authUser, job);

        fileStorageService.delete(job.getOutputPath());
    }

    public Page<ConversionResponse> getCompletedConversions(User authUser, Pageable pageable) {
        return repository.findAllByStatus(ConversionStatus.DONE, pageable)
                .map(mapper::conversionJobToConversionResponse);
    }

    public Map<String, List<String>> getSupportedConversions() {
        return formatRegistry.getSupportedConversions();
    }

    private void validateAccess(User authUser, ConversionJob job) {
        boolean isAdmin = authUser.getRole() == UserRole.ADMIN;

        boolean isOwner = authUser.getId()
                .equals(job.getUser().getId());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException(
                    "You do not have permission to access this resource."
            );
        }
    }

    private String generateOutputFileName(ConversionJob job) {
        String originalName = extractFileNameWithoutExtension(job.getOriginalFileName());

        return originalName + "." + job.getOutputFormat().getExtension();
    }

    private String extractFileNameWithoutExtension(String fileName) {
        int indice = fileName.lastIndexOf('.');

        return indice > 0 ? fileName.substring(0, indice) : fileName;
    }
}
