package services;

import models.Project;
import models.Task;

/** Service class for generating reports. */
public class ReportService {
    private final ProjectService projectService;
    private final TaskService taskService;

    public ReportService(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    public void generateStatusReport() {
        System.out.println("SYSTEM STATUS REPORT");

        Project[] allProjects = projectService.getAllProjects();
        System.out.println("PROJECT STATISTICS");
        System.out.printf("Total Projects     : %d%n", allProjects.length);
        System.out.printf("Average Completion : %.2f%%%n", projectService.getAverageCompletion());

        Project[] activeProjects = projectService.getProjectsByStatus("Active");
        Project[] completedProjects = projectService.getProjectsByStatus("Completed");
        Project[] onHoldProjects = projectService.getProjectsByStatus("On Hold");

        System.out.printf("Active Projects    : %d%n", activeProjects.length);
        System.out.printf("Completed Projects : %d%n", completedProjects.length);
        System.out.printf("On Hold Projects   : %d%n", onHoldProjects.length);

        Task[] allTasks = taskService.getAllTasks();
        System.out.println("TASK STATISTICS");
        System.out.printf("Total Tasks        : %d%n", allTasks.length);

        Task[] pendingTasks = taskService.getTasksByStatus("Pending");
        Task[] inProgressTasks = taskService.getTasksByStatus("In Progress");
        Task[] completedTasks = taskService.getTasksByStatus("Completed");

        System.out.printf("Pending Tasks      : %d%n", pendingTasks.length);
        System.out.printf("In Progress Tasks  : %d%n", inProgressTasks.length);
        System.out.printf("Completed Tasks    : %d%n", completedTasks.length);

        if (allTasks.length > 0) {
            double taskCompletionRate = (completedTasks.length * 100.0) / allTasks.length;
            System.out.printf("Task Completion Rate: %.2f%%%n", taskCompletionRate);
        }

        Task[] highPriority = taskService.getTasksByPriority("High");
        Task[] mediumPriority = taskService.getTasksByPriority("Medium");
        Task[] lowPriority = taskService.getTasksByPriority("Low");

        System.out.println("TASK PRIORITY BREAKDOWN");
        System.out.printf("High   : %d%n", highPriority.length);
        System.out.printf("Medium : %d%n", mediumPriority.length);
        System.out.printf("Low    : %d%n", lowPriority.length);
    }

    public void generateProjectReport(String projectId) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            System.out.println("Error: Project not found!");
            return;
        }

        System.out.println("PROJECT DETAILED REPORT");
        project.displayProjectInfo();

        Task[] projectTasks = taskService.getTasksByProjectId(projectId);
        System.out.println("ASSOCIATED TASKS");
        System.out.printf("Total Tasks: %d%n", projectTasks.length);

        if (projectTasks.length > 0) {
            int completed = 0;
            int inProgress = 0;
            int pending = 0;

            for (Task task : projectTasks) {
                if (task.getStatus().equals("Completed")) completed++;
                else if (task.getStatus().equals("In Progress")) inProgress++;
                else pending++;
            }

            System.out.printf("Completed Tasks  : %d%n", completed);
            System.out.printf("In Progress Tasks: %d%n", inProgress);
            System.out.printf("Pending Tasks     : %d%n", pending);
            System.out.printf("Task Completion   : %.2f%%%n", taskService.calculateProjectTaskCompletion(projectId));

            System.out.println("Task Details:");
            for (int i = 0; i < projectTasks.length; i++) {
                Task task = projectTasks[i];
                System.out.printf("  [%d] %s - %s (%s)%n", i + 1, task.getTaskName(), task.getStatus(), task.getPriority());
            }
        }
    }

    public void generateUserWorkloadReport(String userId) {
        Task[] userTasks = taskService.getTasksByUserId(userId);
        System.out.println("USER WORKLOAD REPORT");
        System.out.printf("User ID: %s%n", userId);
        System.out.printf("Total Assigned Tasks: %d%n", userTasks.length);

        if (userTasks.length > 0) {
            int completed = 0;
            int inProgress = 0;
            int pending = 0;
            int high = 0;
            int medium = 0;
            int low = 0;

            for (Task task : userTasks) {
                switch (task.getStatus()) {
                    case "Completed": completed++; break;
                    case "In Progress": inProgress++; break;
                    default: pending++;
                }
                switch (task.getPriority()) {
                    case "High": high++; break;
                    case "Medium": medium++; break;
                    default: low++;
                }
            }

            System.out.println("Status Breakdown:");
            System.out.printf("Completed   : %d%n", completed);
            System.out.printf("In Progress : %d%n", inProgress);
            System.out.printf("Pending     : %d%n", pending);

            System.out.println("Priority Breakdown:");
            System.out.printf("High   : %d%n", high);
            System.out.printf("Medium : %d%n", medium);
            System.out.printf("Low    : %d%n", low);

            double completionRate = (completed * 100.0) / userTasks.length;
            System.out.printf("Completion Rate: %.2f%%%n", completionRate);
        }
    }
}