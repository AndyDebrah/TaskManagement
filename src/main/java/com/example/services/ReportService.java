package com.example.services;
import com.example.exceptions.EmptyProjectException;

import com.example.models.Project;
import com.example.models.Task;

/** Service class for generating reports. */
public class ReportService {
    private final ProjectService projectService;
    private final TaskService taskService;

    public ReportService(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    public String generateStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("SYSTEM STATUS REPORT\n\n");

        Project[] allProjects = projectService.getAllProjects();
        sb.append("PROJECT STATISTICS\n");
        sb.append(String.format("Total Projects     : %d%n", allProjects.length));
        sb.append(String.format("Average Completion : %.2f%%%n\n", projectService.getAverageCompletion()));

        Project[] activeProjects = projectService.getProjectsByStatus("Active");
        Project[] completedProjects = projectService.getProjectsByStatus("Completed");
        Project[] onHoldProjects = projectService.getProjectsByStatus("On Hold");

        sb.append(String.format("Active Projects    : %d%n", activeProjects.length));
        sb.append(String.format("Completed Projects : %d%n", completedProjects.length));
        sb.append(String.format("On Hold Projects   : %d%n\n", onHoldProjects.length));

        Task[] allTasks = taskService.getAllTasks();
        sb.append("TASK STATISTICS\n");
        sb.append(String.format("Total Tasks        : %d%n\n", allTasks.length));

        Task[] pendingTasks = taskService.getTasksByStatus("Pending");
        Task[] inProgressTasks = taskService.getTasksByStatus("In Progress");
        Task[] completedTasks = taskService.getTasksByStatus("Completed");

        sb.append(String.format("Pending Tasks      : %d%n", pendingTasks.length));
        sb.append(String.format("In Progress Tasks  : %d%n", inProgressTasks.length));
        sb.append(String.format("Completed Tasks    : %d%n", completedTasks.length));

        if (allTasks.length > 0) {
            double taskCompletionRate = (completedTasks.length * 100.0) / allTasks.length;
            sb.append(String.format("Task Completion Rate: %.2f%%%n\n", taskCompletionRate));
        } else {
            sb.append("\n");
        }

        Task[] highPriority = taskService.getTasksByPriority("High");
        Task[] mediumPriority = taskService.getTasksByPriority("Medium");
        Task[] lowPriority = taskService.getTasksByPriority("Low");

        sb.append("TASK PRIORITY BREAKDOWN\n");
        sb.append(String.format("High   : %d%n", highPriority.length));
        sb.append(String.format("Medium : %d%n", mediumPriority.length));
        sb.append(String.format("Low    : %d%n", lowPriority.length));

        return sb.toString();
    }

    public String generateProjectReport(String projectId) {
        StringBuilder sb = new StringBuilder();
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            return "Error: Project not found!";
        }

        sb.append("PROJECT DETAILED REPORT\n");
        sb.append(String.format(
                "Project ID   : %s%n" +
                        "Name         : %s%n" +
                        "Type         : %s%n" +
                        "Description  : %s%n" +
                        "Start Date   : %s%n" +
                        "End Date     : %s%n" +
                        "Team Size    : %d%n" +
                        "Budget       : $%.2f%n" +
                        "Status       : %s%n" +
                        "Completion   : %.2f%%%n%n",
                project.getProjectId(),
                project.getProjectName(),
                project.getProjectType(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getTeamSize(),
                project.getBudget(),
                project.getStatus(),
                project.calculateCompletionPercentage()
        ));

        Task[] projectTasks = taskService.getTasksByProjectId(projectId);
        sb.append("ASSOCIATED TASKS\n");
        sb.append(String.format("Total Tasks: %d%n\n", projectTasks.length));

        if (projectTasks.length == 0) {
            throw new EmptyProjectException(projectId);
        }
        int completed = 0;
        int inProgress = 0;
        int pending = 0;

        for (Task task : projectTasks) {
            if (task.getStatus().equals("Completed")) completed++;
            else if (task.getStatus().equals("In Progress")) inProgress++;
            else pending++;
        }


        sb.append(String.format("Completed Tasks  : %d%n", completed));
        sb.append(String.format("In Progress Tasks: %d%n", inProgress));
        sb.append(String.format("Pending Tasks     : %d%n", pending));
        sb.append(String.format("Task Completion   : %.2f%%%n\n", taskService.calculateProjectTaskCompletion(projectId)));

        sb.append("Task Details:\n");
        for (int i = 0; i < projectTasks.length; i++) {
            Task task = projectTasks[i];
            sb.append(String.format("  [%d] %s - %s (%s)%n", i + 1, task.getTaskName(), task.getStatus(), task.getPriority()));
        }

        return sb.toString();
    }

    public String generateUserWorkloadReport(String userId) {
        StringBuilder sb = new StringBuilder();
        Task[] userTasks = taskService.getTasksByUserId(userId);
        sb.append("USER WORKLOAD REPORT\n");
        sb.append(String.format("User ID: %s%n", userId));
        sb.append(String.format("Total Assigned Tasks: %d%n\n", userTasks.length));

        if (userTasks.length > 0) {
            int completed = 0;
            int inProgress = 0;
            int pending = 0;
            int high = 0;
            int medium = 0;
            int low = 0;

            for (Task task : userTasks) {
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

            sb.append("Status Breakdown:\n");
            sb.append(String.format("Completed   : %d%n", completed));
            sb.append(String.format("In Progress : %d%n", inProgress));
            sb.append(String.format("Pending     : %d%n\n", pending));

            sb.append("Priority Breakdown:\n");
            sb.append(String.format("High   : %d%n", high));
            sb.append(String.format("Medium : %d%n", medium));
            sb.append(String.format("Low    : %d%n\n", low));

            double completionRate = (completed * 100.0) / userTasks.length;
            sb.append(String.format("Completion Rate: %.2f%%%n", completionRate));
        }

        return sb.toString();
    }
}
