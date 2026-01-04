package com.inventory.management.exception;

/**
 * Exception thrown when business validation fails.
 * This is used for validation logic that goes beyond simple field validation.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
