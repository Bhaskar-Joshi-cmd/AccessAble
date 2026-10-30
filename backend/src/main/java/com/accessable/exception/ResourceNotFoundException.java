package com.accessable.exception;

/**
 * Thrown when a requested resource (building, facility or report) does not exist.
 * The global exception handler maps this to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}