package services;

import models.*;

/**
 * Service class for generating various reports
 * Demonstrates data analysis and reporting capabilities
 */
public class ReportService {
    private ProjectServices projectService;
    private TaskService taskService;

    public ReportService(ProjectServices projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    /**
     * Generate comprehensive system status report
     */
    public void generateStatusReport() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                   SYSTEM STATUS REPORT");
        System.out.println("═".repeat(70));

        // Project statistics
        Project[] allProjects = projectService.getAllProjects();
        System.out.println("\n📊 PROJECT STATISTICS:");
        System.out.println("─".repeat(70));
        System.out.printf("  Total Projects        : %d%n", allProjects.length);
        System.out.printf("  Average Completion    : %.2f%%%n", projectService.getAverageCompletion());

        // Count by status
        Project[] activeProjects = projectService.getProjectsByStatus("Active");
        Project[] completedProjects = projectService.getProjectsByStatus("Completed");
        Project[] onHoldProjects = projectService.getProjectsByStatus("On Hold");

        System.out.printf("  Active Projects       : %d%n", activeProjects.length);
        System.out.printf("  Completed Projects    : %d%n", completedProjects.length);
        System.out.printf("  On Hold Projects      : %d%n", onHoldProjects.length);

        // Task statistics
        Task[] allTasks = taskService.getAllTasks();
        System.out.println("\n📝 TASK STATISTICS:");
        System.out.println("─".repeat(70));
        System.out.printf("  Total Tasks           : %d%n", allTasks.length);

        // Count by status
        Task[] pendingTasks = taskService.getTasksByStatus("Pending");
        Task[] inProgressTasks = taskService.getTasksByStatus("In Progress");
        Task[] completedTasks = taskService.getTasksByStatus("Completed");

        System.out.printf("  Pending Tasks         : %d%n", pendingTasks.length);
        System.out.printf("  In Progress Tasks     : %d%n", inProgressTasks.length);
        System.out.printf("  Completed Tasks       : %d%n", completedTasks.length);

        if (allTasks.length > 0) {
            double taskCompletionRate = (completedTasks.length * 100.0) / allTasks.length;
            System.out.printf("  Task Completion Rate  : %.2f%%%n", taskCompletionRate);
        }

        // Priority breakdown
        Task[] highPriority = taskService.getTasksByPriority("High");
        Task[] mediumPriority = taskService.getTasksByPriority("Medium");
        Task[] lowPriority = taskService.getTasksByPriority("Low");

        System.out.println("\n🎯 TASK PRIORITY BREAKDOWN:");
        System.out.println("─".repeat(70));
        System.out.printf("  High Priority         : %d%n", highPriority.length);
        System.out.printf("  Medium Priority       : %d%n", mediumPriority.length);
        System.out.printf("  Low Priority          : %d%n", lowPriority.length);

        System.out.println("═".repeat(70));
    }

    /**
     * Generate project-specific detailed report
     */
    public void generateProjectReport(String projectId) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            System.out.println("Error: Project not found!");
            return;
        }

        System.out.println("\n" + "═".repeat(70));
        System.out.println("                  PROJECT DETAILED REPORT");
        System.out.println("═".repeat(70));

        project.displayProjectInfo();

        // Get tasks for this project
        Task[] projectTasks = taskService.getTasksByProjectId(projectId);
        System.out.println("\n📋 ASSOCIATED TASKS:");
        System.out.println("─".repeat(70));
        System.out.printf("  Total Tasks           : %d%n", projectTasks.length);

        if (projectTasks.length > 0) {
            int completed = 0;
            int inProgress = 0;
            int pending = 0;

            for (Task task : projectTasks) {
                if (task.getStatus().equals("Completed")) {
                    completed++;
                } else if (task.getStatus().equals("In Progress")) {
                    inProgress++;
                } else {
                    pending++;
                }
            }

            System.out.printf("  Completed Tasks       : %d%n", completed);
            System.out.printf("  In Progress Tasks     : %d%n", inProgress);
            System.out.printf("  Pending Tasks         : %d%n", pending);
            System.out.printf("  Task Completion       : %.2f%%%n",
                    taskService.calculateProjectTaskCompletion(projectId));

            // Display individual tasks
            System.out.println("\n  Task Details:");
            for (int i = 0; i < projectTasks.length; i++) {
                Task task = projectTasks[i];
                System.out.printf("    [%d] %s - %s (%s)%n",
                        i + 1, task.getTaskName(), task.getStatus(), task.getPriority());
            }
        }

        System.out.println("═".repeat(70));
    }

    /**
     * Generate user workload report
     */
    public void generateUserWorkloadReport(String userId) {
        Task[] userTasks = taskService.getTasksByUserId(userId);

        System.out.println("\n" + "═".repeat(70));
        System.out.println("                  USER WORKLOAD REPORT");
        System.out.println("═".repeat(70));
        System.out.printf("\nUser ID: %s%n", userId);
        System.out.printf("Total Assigned Tasks: %d%n", userTasks.length);

        if (userTasks.length > 0) {
            int completed = 0;
            int inProgress = 0;
            int pending = 0;
            int high = 0;
            int medium = 0;
            int low = 0;

            for (Task task : userTasks) {
                // Count by status
                switch (task.getStatus()) {
                    case "Completed":
                        completed++;
                        break;
                    case "In Progress":
                        inProgress++;
                        break;
                    default:
                        pending++;
                }

                // Count by priority
                switch (task.getPriority()) {
                    case "High":
                        high++;
                        break;
                    case "Medium":
                        medium++;
                        break;
                    default:
                        low++;
                }
            }

            System.out.println("\nStatus Breakdown:");
            System.out.printf("  Completed    : %d%n", completed);
            System.out.printf("  In Progress  : %d%n", inProgress);
            System.out.printf("  Pending      : %d%n", pending);

            System.out.println("\nPriority Breakdown:");
            System.out.printf("  High         : %d%n", high);
            System.out.printf("  Medium       : %d%n", medium);
            System.out.printf("  Low          : %d%n", low);

            double completionRate = (completed * 100.0) / userTasks.length;
            System.out.printf("\nCompletion Rate: %.2f%%%n", completionRate);
        }

        System.out.println("═".repeat(70));
    }
}