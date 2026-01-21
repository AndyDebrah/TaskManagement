
package com.example.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/** Common abstract project class. */
public abstract class Project {
    private static int projectCounter = 4;
    private final String projectId;
    private String projectName;
    private String description;
    private final String startDate;
    private String endDate;
    private String status;
    private final double budget;
    private final int teamSize;

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
        if (projectId == null || projectId.trim().isEmpty()) {
            projectId = String.format("PRJ%04d", projectCounter++);
        }
        try {
            java.lang.reflect.Field f = Project.class.getDeclaredField("projectId");
            f.setAccessible(true);
            f.set(this, projectId);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set projectId for persisted load", e);
        }
        this.setProjectName(projectName);
        this.setDescription(description);
        try {
            java.lang.reflect.Field fStart = Project.class.getDeclaredField("startDate");
            fStart.setAccessible(true);
            fStart.set(this, startDate);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set startDate for persisted load", e);
        }
        this.setEndDate(endDate);
        this.setStatus((status == null || status.isBlank()) ? "Active" : status);

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

    public synchronized Optional<Task> findTaskById(String taskId) {
        return tasks.stream().filter(t -> t.getTaskId().equals(taskId)).findFirst();
    }

    public Stream<Task> streamTasks() {
        return tasks.stream();
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project other = (Project) o;
        return projectId.equals(other.projectId);
    }

    @Override
    public int hashCode() { return projectId.hashCode(); }
}
