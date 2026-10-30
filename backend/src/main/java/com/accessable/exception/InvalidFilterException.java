package com.accessable.exception;

/**
 * Thrown when a request query parameter or path value is invalid
 * (for example a non-positive buildingId or an unknown enum value).
 * The global exception handler maps this to HTTP 400 Bad Request.
 */
public class InvalidFilterException extends RuntimeException {

    public InvalidFilterException(String message) {
        super(message);
    }
}