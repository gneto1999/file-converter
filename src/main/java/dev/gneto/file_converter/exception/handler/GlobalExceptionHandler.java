package dev.gneto.file_converter.exception.handler;

import dev.gneto.file_converter.exception.*;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<ExceptionResponse> buildExceptionResponse(Exception ex, HttpServletRequest request, HttpStatus status) {
        ExceptionResponse body = new ExceptionResponse(
                Instant.now(),
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler({Exception.class, FileStorageException.class})
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildExceptionResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            AuthenticationException.class,
            InvalidTokenException.class,
            MissingJwtTokenException.class,
            JwtException.class
    })
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(Exception ex, HttpServletRequest request) {
        return buildExceptionResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UserNotFoundException.class, ConversionJobNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return buildExceptionResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConversionException.class, UserAlreadyRegisteredException.class})
    public ResponseEntity<ExceptionResponse> handleConflictException(Exception ex, HttpServletRequest request) {
        return buildExceptionResponse(ex, request, HttpStatus.CONFLICT);
    }

}
