package main.java.com.example.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String taskId) {
        super("Error: Task with ID " + taskId + " not found!");

    }
}
