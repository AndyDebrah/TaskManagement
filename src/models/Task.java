package models;

import interfaces.Completable;

/**
 * Task model representing individual tasks within projects
 * Demonstrates encapsulation and data modeling
 *
 * Each task belongs to a project and can be assigned to a user
 */
public class Task implements Completable {
    // Private fields for encapsulation
    private String taskId;
    private String projectId; // Links task to a project
    private String taskName;
    private String description;
    private String assignedTo; // User ID
    private String priority; // "High", "Medium", "Low"
    private String status; // "Pending", "In Progress", "Completed"
    private String dueDate;

    /**
     * Constructor to initialize a task
     */
    public Task(String taskId, String projectId, String taskName, String description,
                String assignedTo, String priority, String dueDate) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.taskName = taskName;
        this.description = description;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.status = "Pending"; // Default status
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Check if task is completed
     */
    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    /**
     * Mark task as in progress
     */
    public void startTask() {
        if ("Pending".equalsIgnoreCase(status)) {
            this.status = "In Progress";
        }
    }

    /**
     * Mark task as completed
     */
    public void completeTask() {
        this.status = "Completed";
    }

    // ---- Completable interface implementations ----
    @Override
    public boolean markAsCompleted() {
        if (isCompleted()) {
            return false;
        }
        completeTask();
        return true;
    }

    @Override
    public double getCompletionPercentage() {
        return isCompleted() ? 100.0 : 0.0;
    }

    @Override
    public String getCompletionStatus() {
        return status;
    }

    /**
     * Display task information in formatted output
     */
    public void displayTaskInfo() {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.printf("│ Task ID      : %-43s │%n", taskId);
        System.out.printf("│ Task Name    : %-43s │%n", taskName);
        System.out.printf("│ Project ID   : %-43s │%n", projectId);
        System.out.printf("│ Assigned To  : %-43s │%n", assignedTo);
        System.out.printf("│ Priority     : %-43s │%n", priority);
        System.out.printf("│ Status       : %-43s │%n", status);
        System.out.printf("│ Due Date     : %-43s │%n", dueDate);
        System.out.printf("│ Description  : %-43s │%n", description);
        System.out.println("└────────────────────────────────────────────────────────────┘");
    }

    /**
     * Get priority weight for sorting purposes
     * High = 3, Medium = 2, Low = 1
     */
    public int getPriorityWeight() {
        switch (priority.toLowerCase()) {
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("Task[ID=%s, Name=%s, Status=%s, Priority=%s]",
                taskId, taskName, status, priority);
    }
}