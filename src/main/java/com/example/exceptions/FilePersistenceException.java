package com.example.exceptions;

import java.io.IOException;

/**
 * Thrown when file persistence operations fail.
 * Wraps IOException and provides domain-specific error messages.
 * Used in FileUtils for save/load operations.
 */
public class FilePersistenceException extends RuntimeException {

    public FilePersistenceException(String message) {
        super(message);
    }

    public FilePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory methods for common file operation failures
     */
    public static FilePersistenceException forWriteFailure(String filePath, IOException cause) {
        return new FilePersistenceException(
            "Error: Failed to write to file '" + filePath + "': " + cause.getMessage(),
            cause
        );
    }

    public static FilePersistenceException forReadFailure(String filePath, IOException cause) {
        return new FilePersistenceException(
            "Error: Failed to read from file '" + filePath + "': " + cause.getMessage(),
            cause
        );
    }

    public static FilePersistenceException forParseFailure(String filePath, String reason) {
        return new FilePersistenceException(
            "Error: Failed to parse file '" + filePath + "'. Reason: " + reason
        );
    }

    public static FilePersistenceException forDirectoryCreationFailure(String dirPath) {
        return new FilePersistenceException(
            "Error: Failed to create directory '" + dirPath + "'"
        );
    }
}