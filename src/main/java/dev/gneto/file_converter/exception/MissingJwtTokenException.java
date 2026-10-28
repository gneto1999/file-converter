package dev.gneto.file_converter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class MissingJwtTokenException extends RuntimeException {
    public MissingJwtTokenException(String message) {
        super(message);
    }

    public MissingJwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
