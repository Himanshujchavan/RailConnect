package com.railconnect.common.exception;

import com.railconnect.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses the same ErrorResponse shape as CustomAuthenticationEntryPoint / CustomAccessDeniedHandler
 * (the two error paths that fire inside the Spring Security filter chain, before a request
 * ever reaches this handler) so every error response — regardless of where it originates —
 * looks the same to API consumers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception,
                                                                      HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                exception.getStatus().value(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
        return ResponseEntity.status(exception.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                       HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ValidationException",
                "Validation failed",
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * ResourceNotFoundException carries its own @ResponseStatus, but that annotation is only
     * honored by Spring's fallback ResponseStatusExceptionResolver - which never runs here
     * because the catch-all Exception handler below already claims every unmatched exception
     * type first. Without this explicit handler, every "not found" case would incorrectly
     * surface as a 500.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception,
                                                                  HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "ResourceNotFoundException",
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception,
                                                                     HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ConstraintViolationException",
                exception.getMessage()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getClass().getSimpleName(),
                "Unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
