package com.common.exceptions;

import com.common.dto.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionDTO> handleException(AppException ex) {
        log.warn("App Exception: {} - Status: {}", ex.getMessage()/*, ex.getStackTrace()*/);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getMessage().contains("not found") || ex.getMessage().contains("doesn't have")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("already") || ex.getMessage().contains("not owner")) {
            status = HttpStatus.CONFLICT;
        } else if (ex.getMessage().toLowerCase().contains("unauthorized") || ex.getMessage().contains("401")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        ExceptionDTO appEx = new ExceptionDTO(status.value(), ex.getMessage());
        return new ResponseEntity<>(appEx, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionDTO> handleAccessDeniedException(AccessDeniedException ex) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(new ExceptionDTO(status.value(),
                "Access denied. You don't have permission to access this resource."), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ExceptionDTO(status.value(), errorMessage), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception ex) {
        log.error("CRITICAL UNEXPECTED ERROR: {}", ex.getMessage(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ExceptionDTO(status.value(),
                "An internal server error occurred."), status);
    }
}
