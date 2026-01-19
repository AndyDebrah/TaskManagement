package com.example.exceptions;

public class EmptyProjectException extends RuntimeException {

    public EmptyProjectException(String projectId) {
            super("Error: Project with ID " + projectId + " has no tasks!");
        }
    }

