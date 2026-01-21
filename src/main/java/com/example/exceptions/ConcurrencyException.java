package com.example.exceptions;

/**
 * Thrown when concurrent operations fail or are interrupted.
 * Used in ConcurrencyService for thread-related failures.
 */
public class ConcurrencyException extends RuntimeException {

    public ConcurrencyException(String message) {
        super(message);
    }

    public ConcurrencyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory methods for common concurrency failures
     */
    public static ConcurrencyException forExecutorShutdownTimeout(int timeoutSeconds) {
        return new ConcurrencyException(
            "Error: Executor service failed to shutdown within " + timeoutSeconds + " seconds"
        );
    }

    public static ConcurrencyException forInterruptedOperation(String operationName) {
        return new ConcurrencyException(
            "Error: " + operationName + " was interrupted"
        );
    }

    public static ConcurrencyException forTaskUpdateFailure(String taskId, String reason) {
        return new ConcurrencyException(
            "Error: Failed to update task '" + taskId + "' concurrently. Reason: " + reason
        );
    }

    public static ConcurrencyException forDeadlock(String details) {
        return new ConcurrencyException(
            "Error: Potential deadlock detected. Details: " + details
        );
    }
}