package dev.gneto.file_converter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConversionJobNotFoundException extends RuntimeException {

    public ConversionJobNotFoundException(String message) {
        super(message);
    }

    public ConversionJobNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
