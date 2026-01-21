
package com.example.exceptions;

/**
 * Thrown when task data violates business rules or constraints.
 * Used for validation failures specific to task operations.
 */
public class InvalidTaskDataException extends RuntimeException {

    public InvalidTaskDataException(String message) {
        super(message);
    }

    public InvalidTaskDataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory method for common validation failures
     */
    public static InvalidTaskDataException forMissingField(String fieldName) {
        return new InvalidTaskDataException("Error: Task " + fieldName + " cannot be empty!");
    }

    public static InvalidTaskDataException forInvalidStatus(String status) {
        return new InvalidTaskDataException("Error: Invalid task status '" + status + 
            "'. Allowed: Pending, In Progress, Completed");
    }

    public static InvalidTaskDataException forInvalidPriority(String priority) {
        return new InvalidTaskDataException("Error: Invalid priority '" + priority + 
            "'. Allowed: High, Medium, Low");
    }

    public static InvalidTaskDataException forInvalidDate(String date) {
        return new InvalidTaskDataException("Error: Invalid due date format '" + date + 
            "'. Expected format: YYYY-MM-DD");
    }
}