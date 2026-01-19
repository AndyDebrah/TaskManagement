package com.example.exceptions;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(String projectId) {
        super("Error: Project with ID " + projectId + " not found!");
    }
}
