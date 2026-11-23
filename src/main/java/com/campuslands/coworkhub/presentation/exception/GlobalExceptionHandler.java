package com.campuslands.coworkhub.presentation.exception;

import com.campuslands.coworkhub.business.exception.ConflictException;
import com.campuslands.coworkhub.business.exception.InvalidDataException;
import com.campuslands.coworkhub.business.exception.NotFoundException;
import com.campuslands.coworkhub.presentation.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "El cuerpo de la petición no es válido o tiene un formato incorrecto");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Falta el parámetro obligatorio '" + ex.getParameterName() + "'");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN",
                "No tienes permiso para realizar esta acción");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        if (ex instanceof org.springframework.web.ErrorResponse standard) {
            HttpStatus status = HttpStatus.valueOf(standard.getStatusCode().value());
            return build(status, status.name(), ex.getMessage());
        }
        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error inesperado");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {
        ErrorResponse body = new ErrorResponse(
                message, code, Instant.now(clock).truncatedTo(ChronoUnit.SECONDS));
        return ResponseEntity.status(status).body(body);
    }
}