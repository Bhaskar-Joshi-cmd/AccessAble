package com.accessable.exception;

/**
 * Thrown when a requested state transition cannot be applied
 * (for example reviewing a report that is already PENDING).
 * The global exception handler maps this to HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}