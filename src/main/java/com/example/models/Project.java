
package com.example.models;

import java.util.*;
import java.util.stream.Stream;

/** Common abstract project class. */
public abstract class Project {
    private static int projectCounter = 5;
    private final String projectId;
    private String projectName;
    private String description;
    private final String startDate;
    private String endDate;
    private String status;
    private final double budget;
    private final int teamSize;

    // Phase 1: replace array storage with List
    private final List<Task> tasks = new ArrayList<>();

    private String generateProjectId() {
        return String.format("PRJ%04d", projectCounter++);
    }

    public Project(String projectName, String description,
                   String startDate, String endDate, double budget, int teamSize) {
        this.projectId = generateProjectId();
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "Active";
        this.budget = budget;
        this.teamSize = teamSize;

    }



    // ADD this constructor alongside the existing one:
    protected Project(String projectId, String projectName, String description,
                      String startDate, String endDate, double budget, int teamSize, String status) {
        this.projectId  = (projectId == null || projectId.trim().isEmpty()) ? generateProjectId() : projectId;
        this.startDate = startDate;
        this.budget = budget;
        this.teamSize = teamSize;
        this.projectName = projectName;
        this.description = description;
        this.endDate = endDate;
        this.status = status;
        // Explicit ID for persistence loading
        if (projectId == null || projectId.trim().isEmpty()) {
            // fallback to generated if missing
            // (you could also throw if you prefer strictness)
            projectId = String.format("PRJ%04d", projectCounter++);
        }
        // initialize fields
        // (copy same assignment order as your existing constructor)
        try {
            java.lang.reflect.Field f = Project.class.getDeclaredField("projectId");
            f.setAccessible(true);
            f.set(this, projectId);
        } catch (ReflectiveOperationException e) {
            // If reflection is not desired, you can refactor the original class to set projectId directly.
            throw new IllegalStateException("Unable to set projectId for persisted load", e);
        }
        this.setProjectName(projectName);
        this.setDescription(description);
        // startDate is final: we need to assign via reflection similarly
        try {
            java.lang.reflect.Field fStart = Project.class.getDeclaredField("startDate");
            fStart.setAccessible(true);
            fStart.set(this, startDate);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set startDate for persisted load", e);
        }
        this.setEndDate(endDate);
        this.setStatus((status == null || status.isBlank()) ? "Active" : status);

        // budget/teamSize are final: set via reflection
        try {
            java.lang.reflect.Field fBudget = Project.class.getDeclaredField("budget");
            fBudget.setAccessible(true);
            fBudget.set(this, budget);
            java.lang.reflect.Field fTeam = Project.class.getDeclaredField("teamSize");
            fTeam.setAccessible(true);
            fTeam.set(this, teamSize);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set budget/teamSize for persisted load", e);
        }
    }


    public String getProjectId() { return projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStartDate() { return startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getBudget() { return budget; }
    public int getTeamSize() { return teamSize; }

    public abstract double calculateCompletionPercentage();
    public abstract String getProjectType();
    public abstract String getProjectDetails();

    public void displayProjectInfo() {
        System.out.printf("Project ID   : %s%n", projectId);
        System.out.printf("Name         : %s%n", projectName);
        System.out.printf("Type         : %s%n", getProjectType());
        System.out.printf("Description  : %s%n", description);
        System.out.printf("Start Date   : %s%n", startDate);
        System.out.printf("End Date     : %s%n", endDate);
        System.out.printf("Team Size    : %d%n", teamSize);
        System.out.printf("Budget       : $%.2f%n", budget);
        System.out.printf("Status       : %s%n", status);
        System.out.printf("Completion   : %.2f%%%n", calculateCompletionPercentage());
        System.out.println(getProjectDetails());
    }

    @Override
    public String toString() {
        return String.format("Project[ID=%s, Name=%s, Type=%s, Status=%s, Completion=%.2f%%]",
                projectId, projectName, getProjectType(), status, calculateCompletionPercentage());
    }

    // -------- Phase 1: Collections + Streams ----------

    /** Add task if ID is unique within this project. */
    public synchronized void  addTask(Task task) {
        Objects.requireNonNull(task, "task");
        boolean exists = tasks.stream().anyMatch(t -> t.getTaskId().equals(task.getTaskId()));
        if (!exists) tasks.add(task);
    }

    /** Keep original signature; now backed by removeIf (O(n)). */
    public synchronized void removeTask(String taskId) {
        tasks.removeIf(t -> t.getTaskId().equals(taskId));
    }

    /** New: find by ID using Streams. */
    public synchronized Optional<Task> findTaskById(String taskId) {
        return tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst();
    }

    /** New: expose task stream for functional operations. */
    public Stream<Task> streamTasks() {
        return tasks.stream();
    }

    /** Backwards-compatible: still returns an array for callers that expect it. */
    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    /** Equality by immutable identifier for correct behavior in collections. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project other = (Project) o;
        return projectId.equals(other.projectId);
    }

    public double calculateCompletionFromTasks() {
        Task[] tasks = getTasks();
        if (tasks.length == 0) return 0;

        long completedCount = Arrays.stream(tasks)
                .filter(Task::isCompleted)
                .count();

        return (completedCount * 100.0) / tasks.length;
    }


    @Override
    public int hashCode() { return projectId.hashCode(); }
}
