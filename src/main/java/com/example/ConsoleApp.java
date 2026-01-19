package com.example;

import com.example.interfaces.TaskFilter;
import com.example.interfaces.TaskFilters;
import com.example.models.*;
import com.example.services.*;
import com.example.utils.ConsoleMenu;
import com.example.utils.SessionManager;
import com.example.utils.ValidationUtils;
import com.example.models.HardwareProject;
import com.example.services.ConcurrencyService;
import org.w3c.dom.ls.LSOutput;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import com.example.models.Project;
import com.example.models.Task;
import java.util.Scanner;

public class ConsoleApp {
    private final ConsoleMenu menu;
    private final Scanner scanner;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ReportService reportService;
    private final SessionManager sessionManager;
    private final ConcurrencyService concurrencyService;

    public ConsoleApp(ConsoleMenu menu, Scanner scanner, ProjectService projectService, TaskService taskService, ReportService reportService, SessionManager sessionManager, ConcurrencyService concurrencyService) {
        this.sessionManager = sessionManager;
        this.reportService = reportService;
        this.menu = menu;
        this.scanner = scanner;
        this.projectService = projectService;
        this.taskService = taskService;
        this.concurrencyService = concurrencyService;
    }

    private void simulateLogin() {
        System.out.println("\nAUTHENTICATION");
        System.out.println("Login as:");
        System.out.println("1. Administrator");
        System.out.println("2. Regular User");


        int choice = ValidationUtils.getValidatedChoice(scanner, "Select user type (1-2): ", 1, 2);

        User user;
        if (choice == 1) {
            user = new AdminUser("ADM001", "Admin User", "admin@projectmgmt.com", "admin123");
            System.out.println("Logged in as Administrator");
        } else {
            user = new RegularUser("USR001", "John Developer", "john@projectmgmt.com", "user123");
            System.out.println("Logged in as Regular User");
        }

        sessionManager.login(user);
        sessionManager.getCurrentUser().displayUserInfo();
        menu.pause();
    }
    StreamService streamService = new StreamService();
    HardwareProject project = new HardwareProject(
            "Smart Sensor", "IoT Hardware Project",
            "2026-01-01", "2026-03-31",
            5000.0, 5,
            "Sensor", 10
    );




// taskfilter demo
    private void runStreamDemo() {
        TaskFilter filter = t -> t != null
                && "Completed".equalsIgnoreCase(t.getStatus())
                && "USR1".equalsIgnoreCase(t.getAssignedTo());

        List<Task> filteredTasks = streamService.filterTasks(project, filter);
        filteredTasks.forEach(t -> System.out.println("Filtered Task: " + t.getTaskName()));
    }


    private void switchUser() {
        System.out.println("\nSWITCH USER");
        User current = sessionManager.getCurrentUser();

        System.out.println("Currently logged in as:" + current.getRole());
        System.out.println("1. Switch to Administrator");
        System.out.println("2. Switch to Regular User");
        System.out.println("0. Cancel");

        int choice = ValidationUtils.getValidatedChoice(scanner, "Select user type (0-2): ", 0, 2);

        if (choice == 0) {
            System.out.println("User switch cancelled.");
            menu.pause();
            return;
        }

        User newUser;
        if (choice == 1) {
            newUser = new AdminUser("ADM001", "Admin User", "admin@system.com", "admin-pass");
            System.out.println("You are now logged in as ADMIN.");
        } else {
            newUser = new RegularUser("USR001", "Regular User", "user@system.com", "userpass");
            System.out.println("You are now logged in as REGULAR USER.");
        }

        sessionManager.login(newUser);
        sessionManager.getCurrentUser().displayUserInfo();
        menu.pause();
    }

    public void runApplication() {
        menu.displayWelcomeBanner();
        simulateLogin();
        boolean running = true;
        while (running) {
            menu.displayMainMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 7);
            try {
                switch (choice) {
                    case 1 -> handleProjectManagement();
                    case 2 -> handleTaskManagement();
                    case 3 -> handleUserManagement();
                    case 4 -> handleReports();
                    case 5 -> {
                        menu.displaySystemStats();
                        menu.pause();
                    }
                    case 6 -> switchUser();
                    case 7 -> handleStreamAnalytics();
                    case 0 -> running = false;
                }
            } catch (RuntimeException e) {
                showError(e);
            }
        }
    }

    private void handleProjectManagement() {
        boolean inProjectMenu = true;
        while (inProjectMenu) {
            menu.displayProjectMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 7);
            try {
                switch (choice) {
                    case 1 -> createNewProject();
                    case 2 -> {
                        Project[] all = projectService.getAllProjects();
                        if (all.length == 0) {
                            System.out.println("No projects available.");
                        } else {
                            System.out.println("PROJECT CATALOG");
                            for (int i = 0; i < all.length; i++) {
                                System.out.printf("\n[%d] ", i + 1);
                                all[i].displayProjectInfo();
                            }
                            System.out.println("Total Projects: " + all.length);
                        }
                        menu.pause();
                    }
                    case 3 -> searchProject();
                    case 4 -> updateProject();
                    case 5 -> deleteProject();
                    case 6 -> filterProjectsByStatus();
                    case 7 -> filterProjectsByType();
                    case 0 -> inProjectMenu = false;
                }
            } catch (RuntimeException e) {
                showError(e);
            }
        }
    }

    private void createNewProject() {
        try {
            sessionManager.requirePermission("CREATE_PROJECTS");
            System.out.println("\nCREATE NEW PROJECT");
            System.out.println("Select Project Type: 1) Software  2) Hardware");
            int type = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-2): ", 1, 2);
            if (type == 1) {
                SoftwareProject project = readSoftwareProjectFromInput();
                projectService.addProject(project);
                System.out.println("Project added successfully.");
            } else {
                HardwareProject project = readHardwareProjectFromInput();
                projectService.addProject(project);
                System.out.println("Project added successfully.");
            }
        } catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
        menu.pause();
    }

    private SoftwareProject readSoftwareProjectFromInput() {
        String name = ValidationUtils.getValidatedTextField(scanner,
                "Enter Project Name: ", "Project Name");
        String description = ValidationUtils.getValidatedTextField(scanner,
                "Enter Description: ", "Description");
        String startDate = ValidationUtils.getValidatedDate(scanner, "Enter Start Date (YYYY-MM-DD): ");
        String endDate = ValidationUtils.getValidatedDate(scanner, "Enter End Date (YYYY-MM-DD): ");
        String techStack = ValidationUtils.getValidatedString(scanner, "Enter Technology Stack: ");
        String methodology = ValidationUtils.getValidatedString(scanner, "Enter Methodology (Agile/Waterfall): ");
        int totalFeatures = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Total Features: ");
        int teamSize = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Team Size: ");
        int budgetInt = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Budget (whole number): ");


        return new SoftwareProject(name, description, startDate, endDate,
                budgetInt, teamSize, techStack, methodology, totalFeatures);
    }

    private HardwareProject readHardwareProjectFromInput() {
        String name = ValidationUtils.getValidatedTextField(scanner,
                "Enter Project Name: ", "Project Name");
        String description = ValidationUtils.getValidatedTextField(scanner,
                "Enter Description: ", "Description");
        String startDate = ValidationUtils.getValidatedDate(scanner, "Enter Start Date (YYYY-MM-DD): ");
        String endDate = ValidationUtils.getValidatedDate(scanner, "Enter End Date (YYYY-MM-DD): ");
        String hardwareType = ValidationUtils.getValidatedString(scanner, "Enter Hardware Type: ");
        int totalComponents = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Total Components: ");
        int teamSize = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Team Size: ");
        int budgetInt = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Budget (whole number): ");


        return new HardwareProject(name, description, startDate, endDate,
                budgetInt, teamSize, hardwareType, totalComponents);
    }

    private void searchProject() {
        Project project = findProjectOrNotify();
        if (project != null) {
            System.out.println("Project Found:");
            project.displayProjectInfo();

        }

    }

    private void updateProject() {
        try {
            sessionManager.requirePermission("UPDATE_PROJECTS");
            String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID to update: ");
            Project project = projectService.findProjectById(projectId);
            if (project == null) {
                System.out.println("Project not found.");
                menu.pause();
                return;
            }

            System.out.println("Current Project Details:");
            project.displayProjectInfo();

            System.out.println("What would you like to update? 1.Name  2.Description  3.Status  4.End Date");
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-4): ", 1, 4);


            switch (choice) {
                case 1 -> project.setProjectName(ValidationUtils.getValidatedTextField(scanner, "Enter new name: ", "Project Name"));
                case 2 -> project.setDescription(ValidationUtils.getValidatedTextField(scanner, "Enter new description: ", "Description"));
                case 3 -> project.setStatus(ValidationUtils.getValidatedString(scanner, "Enter new status: "));
                case 4 -> project.setEndDate(ValidationUtils.getValidatedDate(scanner, "Enter new end date (YYYY-MM-DD): "));
            }

            try {
                projectService.updateProject(projectId, project);
                System.out.println("Project updated successfully.");
                menu.pause();
            } catch (RuntimeException e) {
                showError(e);
            }
        } catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private void deleteProject() {
        try {
            sessionManager.requirePermission("DELETE_PROJECTS");
            String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID to delete: ");
            Project project = projectService.findProjectById(projectId);
            if (project != null) {
                project.displayProjectInfo();
                System.out.print("Are you sure you want to delete this project? (yes/no): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("yes")) {
                    projectService.deleteProject(projectId);
                    System.out.println("Project deleted successfully.");
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
            menu.pause();
        } catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private void filterProjectsByStatus() {
        System.out.println("Filter by status: 1.Active 2.Completed 3.On Hold");
        int choice = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-3): ", 1, 3);
        String status = switch (choice) {
            case 1 -> "Active";
            case 2 -> "Completed";
            default -> "On Hold";
        };

        Project[] filtered = projectService.getProjectsByStatus(status);
        System.out.println("Projects with status: " + status);

        if (filtered.length == 0) {
            System.out.println("No projects found with this status.");
        } else {
            for (int i = 0; i < filtered.length; i++) {
                System.out.printf("\n[%d] ", i + 1);
                filtered[i].displayProjectInfo();
            }
        }
        menu.pause();
    }

    private void filterProjectsByType() {
        System.out.println("Filter by type: 1.Software Development 2.Hardware Development");
        int choice = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-2): ", 1, 2);
        String type = (choice == 1) ? "Software Development" : "Hardware Development";

        Project[] filtered = projectService.getProjectsByType(type);
        System.out.println("Projects of type: " + type);

        if (filtered.length == 0) {
            System.out.println("No projects found of this type.");
        } else {
            for (int i = 0; i < filtered.length; i++) {
                System.out.printf("\n[%d] ", i + 1);
                filtered[i].displayProjectInfo();
            }
        }
        menu.pause();
    }

    private void handleTaskManagement() {
        boolean inTaskMenu = true;
        while (inTaskMenu) {
            menu.displayTaskMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 9);
            try {
                switch (choice) {
                    case 1 -> createNewTask();
                    case 2 -> {
                        Task[] all = taskService.getAllTasks();
                        if (all.length == 0) {
                            System.out.println("No tasks available.");
                        } else {
                            System.out.println("TASK LIST");
                            for (int i = 0; i < all.length; i++) {
                                System.out.printf("\n[%d] ", i + 1);
                                all[i].displayTaskInfo();
                            }
                            System.out.println("Total Tasks: " + all.length);
                        }
                        menu.pause();
                    }
                    case 3 -> searchTask();
                    case 4 -> updateTaskStatus();
                    case 5 -> deleteTask();
                    case 6 -> viewTasksByProject();
                    case 7 -> viewTasksByUser();
                    case 8 -> viewTasksByPriority();
                    case 9 -> simulateConcurrentUpdatesFlow();
                    case 0 -> inTaskMenu = false;
                }
            } catch (RuntimeException e) {
                showError(e);
            }
        }
    }


    private void simulateConcurrentUpdatesFlow() {
        int[] params = menu.promptConcurrencyParams();
        int workers = params[0];
        int opsPerWorker = params[1];

        System.out.println();
        System.out.println("=== Simulating Concurrent Updates (ExecutorService) ===");
        concurrencyService.simulateConcurrentUpdatesWithExecutor(workers, opsPerWorker);

        System.out.println();
        System.out.println("=== Simulating Parallel Stream Updates ===");
        concurrencyService.simulateParallelStreamUpdates();

        System.out.println("âœ” Simulation complete.");

        menu.pause();

    }

    // --- STREAM ANALYTICS HANDLER (paste inside ConsoleApp class) ---
    private void handleStreamAnalytics() {
        boolean inMenu = true;
        while (inMenu) {
            menu.displayStreamAnalyticsMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Choose: ", 0, 8);

            try {
                switch (choice) {
                    case 1 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        Project p = projectService.findProjectById(projectId);

                        List<Task> completed = streamService.listCompletedTasks(p);
                        if (completed.isEmpty()) System.out.println("No completed tasks.");
                        completed.forEach(t -> System.out.printf("%s - %s%n", t.getTaskId(), t.getTaskName()));
                        menu.pause();
                    }
                    case 2 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        Project p = projectService.findProjectById(projectId);

                        List<String> assignees = streamService.distinctAssignees(p);
                        System.out.println("Assignees (sorted): " + assignees);
                        menu.pause();
                    }
                    case 3 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        Project p = projectService.findProjectById(projectId);

                        streamService.countTasksByStatus(p)
                                .forEach((status, count) -> System.out.printf("%-12s : %d%n", status, count));
                        menu.pause();
                    }
                    case 4 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        Project p = projectService.findProjectById(projectId);

                        streamService.groupTasksByPriority(p)
                                .forEach((prio, list) -> System.out.printf("%-8s -> %d%n", prio, list.size()));
                        menu.pause();
                    }
                    case 5 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        int n = ValidationUtils.getValidatedPositiveInteger(scanner, "Top N: ");

                        Project p = projectService.findProjectById(projectId);
                        List<Task> topN = streamService.topNTasksByName(p, n);
                        if (topN.isEmpty()) System.out.println("No tasks found.");
                        topN.forEach(t -> System.out.println(t.getTaskName()));
                        menu.pause();
                    }
                    case 6 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        Project p = projectService.findProjectById(projectId);

                        // Minimal TaskFilter (as per your requirement doc)
                        System.out.print("Filter criteria (leave blank to skip):");
                        String status = scanner.nextLine().trim();
                        if(!status.isBlank()) {
                            ValidationUtils.requireValidStatus(status);
                        }
                        System.out.println("Assignee (leave blank to skip): ");
                        String assignee = scanner.nextLine().trim();
                        if(!assignee.isBlank()) {
                            ValidationUtils.requireNonEmpty(assignee, "Assignee");
                        }


                        TaskFilter filter = t ->
                                (status.isBlank() || status.equalsIgnoreCase(t.getStatus())) &&
                                        (assignee.isBlank() || assignee.equalsIgnoreCase(t.getAssignedTo()));

                        List<Task> results = streamService.filterTasks(p, filter);
                        if (results.isEmpty()) System.out.println("No tasks matched your filters.");
                        results.forEach(t -> System.out.printf("%s - %s (%s)%n",
                                t.getTaskId(), t.getTaskName(), t.getStatus()));
                        menu.pause();
                    }
                    case 7 -> {
                        double avg = streamService.averageProjectCompletion(projectService);
                        System.out.printf("Average completion across all projects: %.2f%%%n", avg);
                        menu.pause();
                    }
                    case 8 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "Project ID: ");
                        ValidationUtils.requireValidProjectId(projectId);
                        Project p = projectService.findProjectById(projectId);

                        List<Task> par = streamService.listCompletedTasksParallel(p);
                        System.out.printf("Completed (parallel): %d%n", par.size());
                        menu.pause();
                    }
                    case 0 -> inMenu = false;
                    default -> System.out.println("Unknown option.");
                }
            } catch (RuntimeException ex) {
                System.err.println("Error: " + ex.getMessage());
                menu.pause();
            }
        }
    }



    private void createNewTask() {
        try {
            sessionManager.requirePermission("CREATE_TASKS");
            System.out.println("\nCREATE NEW TASK");
            Task task = readTaskFromInput();
            if (task == null) {
                return;
            }
            taskService.addTask(task);
            System.out.println("Task added successfully.");
            menu.pause();
        } catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private Task readTaskFromInput() {
        String projectId = ValidationUtils.getValidatedString(scanner, "Enter Project ID for the Task: ");

        if (projectService.findProjectById(projectId) == null) {
            System.out.println("Project not found. Task creation cancelled.");
            menu.pause();
            return null;
        }
        String taskName = ValidationUtils.getValidatedString(scanner, "Enter Task Name: ");
        String description = ValidationUtils.getValidatedString(scanner, "Enter Description: ");
        String assignedTo = ValidationUtils.getValidatedString(scanner, "Assign to User ID: ");
        System.out.println("Priority: High, Medium, Low");
        String priority = ValidationUtils.getValidatedString(scanner, "Enter Priority: ");
        String dueDate = ValidationUtils.getValidatedDate(scanner, "Enter Due Date (YYYY-MM-DD): ");

        return new Task(projectId, taskName, description, assignedTo, priority, dueDate);
    }

    private void searchTask() {
        Task task = findTaskOrNotify("\nEnter Task ID to search: ");
        if (task != null) {
            System.out.println("Task Found:");
            task.displayTaskInfo();

        }
        menu.pause();
    }

    private void updateTaskStatus() {

        Task task = findTaskOrNotify("\nEnter Task ID to update: ");

        if (task == null) {

            return;
        }

        System.out.println("Current Task:");
        task.displayTaskInfo();

        System.out.println("New Status: 1.Pending 2.In Progress 3.Completed");
        int choice = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-3): ", 1, 3);

        switch (choice) {
            case 1 -> task.setStatus("Pending");
            case 2 -> task.setStatus("In Progress");
            case 3 -> task.setStatus("Completed");
        }


        try {
            taskService.updateTask(task.getTaskId(), task);
            System.out.println("Task updated successfully.");
            menu.pause();
        } catch (RuntimeException e) {
            showError(e);
        }
    }

    private void deleteTask() {
        try {
            sessionManager.requirePermission("DELETE_TASKS");
            String taskId = ValidationUtils.getValidatedString(scanner, "\nEnter Task ID to delete: ");
            Task task = taskService.findTaskById(taskId);
            if (task != null) {
                task.displayTaskInfo();
                System.out.print("Are you sure you want to delete this task? (yes/no): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("yes")) {
                    try {
                        taskService.deleteTask(taskId);
                        System.out.println("Task deleted successfully.");

                    } catch (RuntimeException e) {
                        showError(e);
                    }

                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
            menu.pause();
        } catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private void viewTasksByProject() {
        String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID: ");
        Task[] tasks = taskService.getTasksByProjectId(projectId);
        System.out.println("Tasks for Project: " + projectId);
        if (tasks.length == 0) {
            System.out.println("No tasks found for this project.");
        } else {
            for (int i = 0; i < tasks.length; i++) {
                System.out.printf("\n[%d] ", i + 1);
                tasks[i].displayTaskInfo();
            }
        }
        menu.pause();
    }

    private void viewTasksByUser() {
        String userId = ValidationUtils.getValidatedString(scanner, "\nEnter User ID: ");
        Task[] tasks = taskService.getTasksByUserId(userId);
        System.out.println("Tasks assigned to User: " + userId);
        if (tasks.length == 0) {
            System.out.println("No tasks found for this user.");
        } else {
            for (int i = 0; i < tasks.length; i++) {
                System.out.printf("\n[%d] ", i + 1);
                tasks[i].displayTaskInfo();
            }
        }
        menu.pause();
    }

    private void viewTasksByPriority() {
        System.out.println("Filter by priority: 1.High 2.Medium 3.Low");
        int choice = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-3): ", 1, 3);
        String priority = switch (choice) {
            case 1 -> "High";
            case 2 -> "Medium";
            default -> "Low";
        };

        Task[] tasks = taskService.getTasksByPriority(priority);
        System.out.println("Tasks with priority: " + priority);
        if (tasks.length == 0) {
            System.out.println("No tasks found with this priority.");
        } else {
            for (int i = 0; i < tasks.length; i++) {
                System.out.printf("\n[%d] ", i + 1);
                tasks[i].displayTaskInfo();
            }
        }
        menu.pause();
    }

    private void handleUserManagement() {
        System.out.println("USER MANAGEMENT");
        System.out.println("Current User Information:");
        sessionManager.getCurrentUser().displayUserInfo();
        System.out.println("User Permissions:");
        for (String permission : sessionManager.getCurrentUser().getPermissions()) {
            System.out.println(" - " + permission);
        }
        menu.pause();
    }

    private void handleReports() {
        boolean inReportMenu = true;
        while (inReportMenu) {
            menu.displayReportMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 4);
            try {
                switch (choice) {

                    case 1 -> {
                        String report = reportService.generateStatusReport();
                        System.out.println(report);
                        menu.pause();
                    }
                    case 2 -> {
                        String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID: ");
                        String report = reportService.generateProjectReport(projectId);
                        System.out.println(report);
                        menu.pause();
                    }
                    case 3 -> {
                        String userId = ValidationUtils.getValidatedString(scanner, "\nEnter User ID: ");
                        String report = reportService.generateUserWorkloadReport(userId);
                        System.out.println(report);
                        menu.pause();
                    }
                    case 4 -> {
                        generateCompletionSummary();
                        menu.pause();
                    }
                    case 0 -> inReportMenu = false;
                }
            } catch (RuntimeException e) {
                showError(e);
            }
        }
    }

    private void generateCompletionSummary() {
        Project[] projects = projectService.getAllProjects();
        System.out.println("PROJECT COMPLETION SUMMARY");
        if (projects.length == 0) {
            System.out.println("No projects available.");
            return;
        }

        System.out.printf("%-12s %-30s %-15s %10s%n", "Project ID", "Project Name", "Type", "Completion");
        for (Project project : projects) {
            String name = project.getProjectName();
            String type = project.getProjectType();
            System.out.printf("%-12s %-30s %-15s %9.2f%%%n",
                    project.getProjectId(),
                    name.substring(0, Math.min(name.length(), 28)),
                    type.substring(0, Math.min(type.length(), 13)),
                    project.calculateCompletionPercentage());
        }

        System.out.printf("Average Completion: %.2f%%%n", projectService.getAverageCompletion());
    }

    private Project findProjectOrNotify() {
        String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID to search: ");
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            System.out.println("Project not found.");
        }
        return project;
    }

    private Task findTaskOrNotify(String prompt) {
        String taskId = ValidationUtils.getValidatedString(scanner, prompt);
        Task task = taskService.findTaskById(taskId);
        if (task == null) {
            System.out.println("Task not found.");
        }
        return task;
    }


    private void showError(RuntimeException e) {
        System.out.println("Error: " + e.getMessage());
        menu.pause();
    }
}
