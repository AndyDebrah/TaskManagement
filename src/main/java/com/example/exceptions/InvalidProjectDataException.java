package com.example.exceptions;

/**
 * Thrown when project data violates business rules
 */

public class InvalidProjectDataException extends RuntimeException {

    public InvalidProjectDataException(String message) {
        super(message);
    }
}
