package com.example.store.controller;

import com.example.store.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = resolveStatus(exception.getStatusCode().value());
        String message = exception.getReason() != null ? exception.getReason() : status.getReasonPhrase();
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format(
                "Invalid value '%s' for parameter '%s'", exception.getValue(), exception.getName());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(buildErrorResponse(status, "Malformed JSON request body", request.getRequestURI()));
    }

    private ApiErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        return new ApiErrorResponse(
                OffsetDateTime.now(ZoneOffset.UTC).toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path);
    }

    private HttpStatus resolveStatus(int statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("Unsupported HTTP status code: " + statusCode);
        }
        return status;
    }
}
