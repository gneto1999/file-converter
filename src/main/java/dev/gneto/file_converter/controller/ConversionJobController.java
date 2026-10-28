package dev.gneto.file_converter.controller;

import dev.gneto.file_converter.controller.docs.ConversionJobControllerDocs;
import dev.gneto.file_converter.dto.FileDownload;
import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.services.ConversionJobService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/conversions")
public class ConversionJobController implements ConversionJobControllerDocs {

    private final ConversionJobService service;

    private static final Logger logger = LoggerFactory.getLogger(ConversionJobController.class);

    public ConversionJobController(ConversionJobService service) {
        this.service = service;
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ConversionResponse> createConversion(
            @RequestParam("file") MultipartFile file,
            @RequestParam("outputExtension") String outputExtension
    ) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(service.createConversion(file, outputExtension));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ConversionResponse> conversionStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(service.conversionStatus(id));
    }

    @GetMapping(
            value = "/{id}/download",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Override
    public ResponseEntity<Resource> downloadConversion(@PathVariable UUID id, HttpServletRequest request) {
        FileDownload downloadFile = service.downloadConversion(id);
        Resource resource = downloadFile.resource();
        String originalFilename = downloadFile.originalFilename();

        String contentType = null;

        try {
            contentType = request.getServletContext()
                    .getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.error("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }
}
