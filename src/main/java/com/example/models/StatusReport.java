package main.java.com.example.models;

import main.java.com.example.services.ProjectService;
import main.java.com.example.services.TaskService;

/**
 * Utility class for generating status reports
 * Demonstrates separation of reporting logic
 */
public class StatusReport {
    private final String reportId;
    private final String reportDate;
    private final ProjectService projectService;
    private final TaskService taskService;

    public StatusReport(String reportId, String reportDate,
                        ProjectService projectService, TaskService taskService) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    /**
     * Generate and display formatted status report
     */
    public void generate() {
        System.out.println("STATUS REPORT - " + reportDate);
        System.out.println("Report ID: " + reportId);

        System.out.printf("Projects: %d | Tasks: %d%n",
                projectService.getProjectCount(), taskService.getTaskCount());
        System.out.printf("Avg Project Completion: %.2f%%%n", projectService.getAverageCompletion());
    }
}