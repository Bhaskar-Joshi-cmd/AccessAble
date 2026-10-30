package com.accessable.exception;

import com.accessable.dto.ApiError;
import com.accessable.exception.ConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;

/**
 * Converts expected exceptions into the standard error response contract
 * defined in 07_API_SPEC.md.
 *
 * All error responses have this shape:
 * {
 *   "timestamp": "2026-10-26T17:00:00+05:30",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "...",
 *   "path": "/api/reports"
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Resource not found (for example unknown facility ID) -> HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(ex, HttpStatus.NOT_FOUND, request);
    }

    /**
     * Invalid filter or query value -> HTTP 400.
     */
    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<ApiError> handleInvalidFilter(InvalidFilterException ex, HttpServletRequest request) {
        return buildError(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Conflict (for example reviewing an already-reviewed report) -> HTTP 409.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildError(ex, HttpStatus.CONFLICT, request);
    }

    /**
     * Path variable could not be converted to the expected type
     * (for example /api/facilities/abc) -> HTTP 400.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest request) {
        String message = "Invalid value for parameter '" + ex.getName() + "': " + ex.getValue();
        InvalidFilterException wrapper = new InvalidFilterException(message);
        return buildError(wrapper, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * ResponseStatusException thrown by controllers -> preserve HTTP status.
     */
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(
            org.springframework.web.server.ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return buildError(new RuntimeException(ex.getReason() != null ? ex.getReason() : ex.getMessage()),
                status, request);
    }

    /**
     * Any other unexpected error -> HTTP 500.
     * No stack traces or internal details are leaked to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        String safeMessage = "An unexpected error occurred.";
        InvalidFilterException wrapper = new InvalidFilterException(safeMessage);
        return buildError(wrapper, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ApiError> buildError(RuntimeException ex, HttpStatus status, HttpServletRequest request) {
        ApiError error = new ApiError(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}