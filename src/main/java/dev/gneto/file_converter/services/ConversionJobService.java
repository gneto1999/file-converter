package dev.gneto.file_converter.services;

import dev.gneto.file_converter.config.RabbitMqConfig;
import dev.gneto.file_converter.dto.ConversionMessage;
import dev.gneto.file_converter.dto.FileDownload;
import dev.gneto.file_converter.dto.StoredFile;
import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.enums.ConversionStatus;
import dev.gneto.file_converter.enums.FileFormat;
import dev.gneto.file_converter.exception.ConversionException;
import dev.gneto.file_converter.exception.ConversionJobNotFoundException;
import dev.gneto.file_converter.mapper.ConversionJobMapper;
import dev.gneto.file_converter.model.ConversionJob;
import dev.gneto.file_converter.repository.ConversionJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ConversionJobService {

    private final FileStorageService fileStorageService;
    private final ConversionJobRepository repository;
    private final ConversionJobMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ConversionJobService.class);

    public ConversionJobService(
            FileStorageService fileStorageService,
            ConversionJobRepository repository,
            ConversionJobMapper mapper,
            RabbitTemplate rabbitTemplate
    ) {
        this.fileStorageService = fileStorageService;
        this.repository = repository;
        this.mapper = mapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    public ConversionResponse createConversion(MultipartFile file, String outputExtension) {
        StoredFile storedFile = fileStorageService.store(file);
        FileFormat outputFormat = FileFormat.fromExtension(outputExtension)
                .orElseThrow(() -> new ConversionException("Conversion not supported: " + outputExtension));

        ConversionJob conversionJob = new ConversionJob(
                storedFile.originalFileName(),
                storedFile.fileFormat(),
                outputFormat,
                storedFile.path().toString()
        );

        ConversionJob jobSaved = repository.save(conversionJob);

        logger.info("Sending message on queue.");
        ConversionMessage conversionMessage = new ConversionMessage(conversionJob.getId());
        logger.info("Message sent on queue.");

        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, conversionMessage);

        return mapper.conversionJobToConversionResponse(jobSaved);
    }

    public ConversionResponse conversionStatus(UUID id) {
        var conversionJob = repository.findByPublicId(id)
                .orElseThrow(() -> new ConversionJobNotFoundException("Conversion job not found with ID: " + id));

        return mapper.conversionJobToConversionResponse(conversionJob);
    }

    public FileDownload downloadConversion(UUID id) {
        ConversionJob job = repository.findByPublicId(id)
                .orElseThrow(() -> new ConversionJobNotFoundException("Conversion job not found with ID: " + id));

        if (job.getStatus() != ConversionStatus.DONE) {
            throw new ConversionException("It is not possible to download the file because the conversion has not yet been completed.");
        }

        Resource resource = fileStorageService.loadAsResource(job.getOutputPath());

        return new FileDownload(job.getOriginalFileName(), resource);
    }
}
