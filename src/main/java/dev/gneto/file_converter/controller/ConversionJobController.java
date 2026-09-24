package dev.gneto.file_converter.controller;

import dev.gneto.file_converter.dto.response.ConversionResponse;
import dev.gneto.file_converter.enums.FileFormat;
import dev.gneto.file_converter.services.ConversionJobService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/conversion")
public class ConversionJobController {

    private final ConversionJobService service;

    public ConversionJobController(ConversionJobService service) {
        this.service = service;
    }

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
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
    public ResponseEntity<ConversionResponse> conversionStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(service.conversionStatus(id));
    }
}
