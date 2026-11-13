package dev.gneto.file_converter.controller;

import dev.gneto.file_converter.controller.docs.ConversionJobControllerDocs;
import dev.gneto.file_converter.dto.FileDownload;
import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.model.User;
import dev.gneto.file_converter.services.ConversionJobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
            @AuthenticationPrincipal User authUser,
            @RequestParam("file") MultipartFile file,
            @RequestParam("outputExtension") String outputExtension
    ) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(service.createConversion(authUser, file, outputExtension));
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<ConversionResponse> conversionStatus(
            @AuthenticationPrincipal User authUser,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok().body(service.conversionStatus(authUser, id));
    }

    @GetMapping(
            value = "/{id}/download",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @Override
    public ResponseEntity<Resource> downloadConversion(
            @AuthenticationPrincipal User authUser,
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        FileDownload downloadFile = service.downloadConversion(authUser, id);
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
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(originalFilename, StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(resource);
    }

    @DeleteMapping(value = "/{id}/delete")
    @Override
    public ResponseEntity<ConversionResponse> deleteConversion(
            @AuthenticationPrincipal User authUser, @PathVariable UUID id
    ) {
        service.deleteConversion(authUser, id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(
            value = "/completed",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Page<ConversionResponse>> getCompletedConversions(
            @AuthenticationPrincipal User authUser,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        int size = Math.min(pageable.getPageSize(), 50);

        Pageable limitedPageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

        return ResponseEntity.ok().body(service.getCompletedConversions(authUser, limitedPageable));
    }

    @GetMapping(
            value = "/supported-formats",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<Map<String, List<String>>> getSupportedFormats() {
        return ResponseEntity.ok().body(service.getSupportedConversions());
    }
}
