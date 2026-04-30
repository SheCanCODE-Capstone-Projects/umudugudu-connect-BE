package com.umudugudu.exception;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

// OTP / auth exceptions
// (subclasses of BusinessException — order matters: specific before general)

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(String message, int status, String code, LocalDateTime timestamp) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(ex.getMessage(), 404, "NOT_FOUND", LocalDateTime.now()));
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponse> handleOtpExpired(OtpExpiredException ex) {
        return ResponseEntity.status(400)
            .body(new ErrorResponse(ex.getMessage(), 400, "OTP_EXPIRED", LocalDateTime.now()));
    }

    @ExceptionHandler(OtpInvalidException.class)
    public ResponseEntity<ErrorResponse> handleOtpInvalid(OtpInvalidException ex) {
        return ResponseEntity.status(400)
            .body(new ErrorResponse(ex.getMessage(), 400, "OTP_INVALID", LocalDateTime.now()));
    }

    @ExceptionHandler(MaxAttemptsExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxAttempts(MaxAttemptsExceededException ex) {
        return ResponseEntity.status(429)
            .body(new ErrorResponse(ex.getMessage(), 429, "MAX_ATTEMPTS_EXCEEDED", LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(401)
            .body(new ErrorResponse(ex.getMessage(), 401, "INVALID_REFRESH_TOKEN", LocalDateTime.now()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(400)
            .body(new ErrorResponse(ex.getMessage(), 400, "BUSINESS_ERROR", LocalDateTime.now()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(401)
            .body(new ErrorResponse("Invalid credentials", 401, "UNAUTHORIZED", LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(403)
            .body(new ErrorResponse("Access denied", 403, "FORBIDDEN", LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e ->
            errors.put(((FieldError) e).getField(), e.getDefaultMessage())
        );
        return ResponseEntity.status(400).body(Map.of(
            "message",   "Validation failed",
            "errors",    errors,
            "status",    400,
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ex.printStackTrace(); // logs full stack trace to console
        return ResponseEntity.status(500)
            .body(new ErrorResponse(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                    500, "INTERNAL_ERROR", LocalDateTime.now()));
    }
}
