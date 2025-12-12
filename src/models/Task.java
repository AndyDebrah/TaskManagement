package models;

import interfaces.Completable;

/** Task model representing individual tasks within projects. */
public class Task implements Completable {
    private static int taskCounter = 1;
    private String projectId;
    private String taskName;
    private String description;
    private String assignedTo;
    private String priority;
    private String status;
    private String dueDate;
    private String taskId;
    private String generateTaskId() {
        return String.format("TSK%04d", taskCounter++);
    }


    public Task(String projectId, String taskName, String description,
                String assignedTo, String priority, String dueDate) {
        this.taskId = generateTaskId();
        this.projectId = projectId;
        this.taskName = taskName;
        this.description = description;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.status = "Pending";
        this.dueDate = dueDate;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public boolean isCompleted() { return "Completed".equalsIgnoreCase(status); }
    public void startTask() { if ("Pending".equalsIgnoreCase(status)) this.status = "In Progress"; }
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

    public int getPriorityWeight() {
        switch (priority.toLowerCase()) {
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("Task[ID=%s, Name=%s, Status=%s, Priority=%s]", taskId, taskName, status, priority);
    }
}