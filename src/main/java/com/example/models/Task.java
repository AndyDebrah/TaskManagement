
package main.java.com.example.models;

import main.java.com.example.interfaces.Completable;

/** Task model representing individual tasks within projects. */
public class Task implements Completable {
    private static int taskCounter = 4;
    private final String projectId;
    private final String taskName;
    private final String description;
    private final String assignedTo;
    private final String priority;
    private String status;
    private final String dueDate;
    private final String taskId;

    private static String generateTaskId() {
        return String.format("TSK%04d", taskCounter++);
    }

    public Task(String projectId, String taskName, String description,
                String assignedTo, String priority, String dueDate) {
        this(generateTaskId(), projectId, taskName, description, assignedTo, priority, dueDate);
    }

    public Task(String taskId, String projectId, String taskName, String description,
                String assignedTo, String priority, String dueDate) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.taskName = taskName;
        this.description = description;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.status = "Pending";
        this.dueDate = dueDate;
    }

    public String getTaskId() { return taskId; }
    public String getProjectId() { return projectId; }
    public String getTaskName() { return taskName; }
    public String getDescription() { return description; }
    public String getAssignedTo() { return assignedTo; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDueDate() { return dueDate; }

    public boolean isCompleted() { return "Completed".equalsIgnoreCase(status); }
    public void completeTask() { this.status = "Completed"; }

    @Override
    public boolean markAsCompleted() {
        if (isCompleted()) return false;
        completeTask();
        return true;
    }

    @Override
    public double getCompletionPercentage() { return isCompleted() ? 100.0 : 0.0; }

    @Override
    public String getCompletionStatus() { return status; }

    public void displayTaskInfo() {
        System.out.printf("Task ID    : %s%n", taskId);
        System.out.printf("Name       : %s%n", taskName);
        System.out.printf("Project ID : %s%n", projectId);
        System.out.printf("Assigned To: %s%n", assignedTo);
        System.out.printf("Priority   : %s%n", priority);
        System.out.printf("Status     : %s%n", status);
        System.out.printf("Due Date   : %s%n", dueDate);
        System.out.printf("Description : %s%n", description);
    }

    @Override
    public String toString() {
        return String.format("Task[ID=%s, Name=%s, Status=%s, Priority=%s]", taskId, taskName, status, priority);
    }

    // Phase 1: equality by stable identifier, needed for List/Map behavior
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task other = (Task) o;
        return taskId.equals(other.taskId);
    }

    @Override
    public int hashCode() { return taskId.hashCode(); }
}
