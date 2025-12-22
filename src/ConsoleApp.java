import models.*;
import services.ProjectService;
import services.ReportService;
import services.TaskService;
import utils.ConsoleMenu;
import utils.SessionManager;
import utils.ValidationUtils;

import java.util.Scanner;

public class ConsoleApp {
    private  final ConsoleMenu menu;
    private  final Scanner scanner;
    private  final ProjectService projectService;
    private  final  TaskService taskService;
    private final ReportService reportService;
    private  final SessionManager sessionManager;

     public  ConsoleApp(ConsoleMenu menu, Scanner scanner, ProjectService projectService, TaskService taskService, ReportService reportService, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.reportService = reportService;
        this.menu = menu;
        this.scanner = scanner;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    private  void simulateLogin() {
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
        SessionManager.getCurrentUser().displayUserInfo();
        menu.pause();
    }

    private  void switchUser() {
        System.out.println("\nSWITCH USER");
        User current = SessionManager.getCurrentUser();

        System.out.println("Currently logged in as:" + current.getRole());
        System.out.println("1. Switch to Administrator");
        System.out.println("2. Switch to Regular User");
        System.out.println("0. Cancel");

        int choice = ValidationUtils.getValidatedChoice(scanner, "Select user type (0-2): ", 0, 2);

        if (choice == 0) {
            System.out.println( "User switch cancelled." );
            menu.pause();
            return;
        }
        User newUser;


        if (choice == 1) {
            newUser = new AdminUser("ADM001", "Admin User", "admin@system.com", "adminpass");
            System.out.println("You are now logged in as ADMIN.");
        } else {
            newUser = new RegularUser("USR001", "Regular User", "user@system.com", "userpass");
            System.out.println("You are now logged in as REGULAR USER.");
        }

        // Update your session user
        sessionManager.login(newUser);

        // Display the new user information
        newUser.displayUserInfo();
        menu.pause();
    }

    public   void runApplication() {
        menu.displayWelcomeBanner();
        simulateLogin();
        boolean running = true;
        while (running) {
            menu.displayMainMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 6);
            try{
                switch (choice) {
                    case 1 -> handleProjectManagement();
                    case 2 -> handleTaskManagement();
                    case 3 -> handleUserManagement();
                    case 4 -> handleReports();
                    case 5 -> { menu.displaySystemStats(); menu.pause(); }
                    case 6 -> switchUser();
                    case 0 -> running = false;
                }
            }catch (RuntimeException e){
                showError(e);
            }
        }
    }

    private  void handleProjectManagement() {
        boolean inProjectMenu = true;
        while (inProjectMenu) {
            menu.displayProjectMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 7);
            try {
                switch (choice) {
                    case 1 -> createNewProject();
                    case 2 -> {
                        projectService.displayAllProjects();
                        menu.pause();
                    }
                    case 3 -> searchProject();
                    case 4 -> updateProject();
                    case 5 -> deleteProject();
                    case 6 -> filterProjectsByStatus();
                    case 7 -> filterProjectsByType();
                    case 0 -> inProjectMenu = false;
                }
            }catch (RuntimeException e){
                showError(e);
            }
        }
    }

    private  void createNewProject() {
        try {
            SessionManager.requirePermission("CREATE_PROJECTS");
            System.out.println("\nCREATE NEW PROJECT");
            System.out.println("Select Project Type: 1) Software  2) Hardware");
            int type = ValidationUtils.getValidatedChoice(scanner, "Enter choice (1-2): ", 1, 2);
            String name = ValidationUtils.getValidatedTextField(scanner,
                    "Enter Project Name: ", "Project Name");
            String description = ValidationUtils.getValidatedTextField(scanner,
                    "Enter Description: ", "Description");
            String startDate = ValidationUtils.getValidatedDate(scanner, "Enter Start Date (YYYY-MM-DD): ");
            String endDate = ValidationUtils.getValidatedDate(scanner, "Enter End Date (YYYY-MM-DD): ");

            if (type == 1) {
                String techStack = ValidationUtils.getValidatedString(scanner, "Enter Technology Stack: ");
                String methodology = ValidationUtils.getValidatedString(scanner, "Enter Methodology (Agile/Waterfall): ");
                int totalFeatures = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Total Features: ");
                int teamSize = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Team Size: ");
                int budgetInt = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Budget (whole number): ");

                SoftwareProject project = new SoftwareProject( name, description, startDate, endDate,
                        budgetInt, teamSize, techStack, methodology, totalFeatures);
                projectService.addProject(project);
            } else {
                String hardwareType = ValidationUtils.getValidatedString(scanner, "Enter Hardware Type: ");
                int totalComponents = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Total Components: ");
                int teamSize = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Team Size: ");
                int budgetInt = ValidationUtils.getValidatedPositiveInteger(scanner, "Enter Budget (whole number): ");

                HardwareProject project = new HardwareProject( name, description, startDate, endDate,
                        budgetInt, teamSize, hardwareType, totalComponents);
                projectService.addProject(project);
            }
        } catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }

        menu.pause();
    }

    private  void searchProject() {
        String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID to search: ");
        Project project = projectService.findProjectById(projectId);
        if (project != null) {
            System.out.println("Project Found:");
            project.displayProjectInfo();
        } else {
            System.out.println("Project not found.");
        }
        menu.pause();
    }

    private  void updateProject() {
        try {
           SessionManager.requirePermission("UPDATE_PROJECTS");
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
                menu.pause();
            } catch (RuntimeException e){
                showError(e);
            }
        }
        catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private  void deleteProject() {
        try {
            SessionManager.requirePermission("DELETE_PROJECTS");
            String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID to delete: ");
            Project project = projectService.findProjectById(projectId);
            if (project != null) {
                project.displayProjectInfo();
                System.out.print("Are you sure you want to delete this project? (yes/no): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("yes")) {
                    projectService.deleteProject(projectId);
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
            menu.pause();
        }
        catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private  void filterProjectsByStatus() {
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

    private  void filterProjectsByType() {
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

    private  void handleTaskManagement() {
        boolean inTaskMenu = true;
        while (inTaskMenu) {
            menu.displayTaskMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 8);
            try {


                switch (choice) {
                    case 1 -> createNewTask();
                    case 2 -> {
                        taskService.displayAllTasks();
                        menu.pause();
                    }
                    case 3 -> searchTask();
                    case 4 -> updateTaskStatus();
                    case 5 -> deleteTask();
                    case 6 -> viewTasksByProject();
                    case 7 -> viewTasksByUser();
                    case 8 -> viewTasksByPriority();
                    case 0 -> inTaskMenu = false;
                }
            }catch (RuntimeException e){
                showError(e);
            }
        }
    }

    private  void createNewTask() {
        try {


            SessionManager.requirePermission("CREATE_TASKS");

            System.out.println("\nCREATE NEW TASK");

            String projectId = ValidationUtils.getValidatedString(scanner, "Enter Project ID: ");

            if (projectService.findProjectById(projectId) == null) {
                System.out.println("Project not found. Task creation cancelled.");
                menu.pause();
                return;
            }

            String taskName = ValidationUtils.getValidatedString(scanner, "Enter Task Name: ");
            String description = ValidationUtils.getValidatedString(scanner, "Enter Description: ");
            String assignedTo = ValidationUtils.getValidatedString(scanner, "Assign to User ID: ");
            System.out.println("Priority: High, Medium, Low");
            String priority = ValidationUtils.getValidatedString(scanner, "Enter Priority: ");
            String dueDate = ValidationUtils.getValidatedDate(scanner, "Enter Due Date (YYYY-MM-DD): ");

            Task task = new Task(projectId, taskName, description, assignedTo, priority, dueDate);
            taskService.addTask(task);
            menu.pause();
        }catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private  void searchTask() {
        String taskId = ValidationUtils.getValidatedString(scanner, "\nEnter Task ID to search: ");
        Task task = taskService.findTaskById(taskId);
        if (task != null) {
            System.out.println("Task Found:");
            task.displayTaskInfo();
        } else {
            System.out.println("Task not found.");
        }
        menu.pause();
    }

    private  void updateTaskStatus() {

        String taskId = ValidationUtils.getValidatedString(scanner, "\nEnter Task ID to update: ");
        Task task = taskService.findTaskById(taskId);
        if (task == null) {
            System.out.println("Task not found.");
            menu.pause();
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
            taskService.updateTask(taskId, task);
            menu.pause();
        } catch (RuntimeException e) {
            showError(e);
        }
    }


    private  void deleteTask() {
        try {


            SessionManager.requirePermission("DELETE_TASKS");
            String taskId = ValidationUtils.getValidatedString(scanner, "\nEnter Task ID to delete: ");
            Task task = taskService.findTaskById(taskId);
            if (task != null) {
                task.displayTaskInfo();
                System.out.print("Are you sure you want to delete this task? (yes/no): ");
                String confirm = scanner.nextLine();
                if (confirm.equalsIgnoreCase("yes")) {
                    try {
                        taskService.deleteTask(taskId);
                    }catch (RuntimeException e){
                        showError(e);
                    }
                } else {
                    System.out.println("Deletion cancelled.");
                }
            }
            menu.pause();
        }
        catch (SecurityException se) {
            System.out.println("Permission Denied: " + se.getMessage());
        }
    }

    private  void viewTasksByProject() {
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

    private  void viewTasksByUser() {
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

    private  void viewTasksByPriority() {
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

    private  void handleUserManagement() {
        System.out.println("USER MANAGEMENT");
        System.out.println("Current User Information:");
        SessionManager.getCurrentUser().displayUserInfo();
        System.out.println("User Permissions:");
        for (String permission : SessionManager.getCurrentUser().getPermissions()) {
            System.out.println(" - " + permission);
        }
        menu.pause();
    }

    private  void handleReports() {
        boolean inReportMenu = true;
        while (inReportMenu) {
            menu.displayReportMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner, "Enter your choice: ", 0, 4);
            try {
                switch (choice) {

                    case 1 -> { reportService.generateStatusReport(); menu.pause(); }
                    case 2 -> { String projectId = ValidationUtils.getValidatedString(scanner, "\nEnter Project ID: "); reportService.generateProjectReport(projectId); menu.pause(); }
                    case 3 -> { String userId = ValidationUtils.getValidatedString(scanner, "\nEnter User ID: "); reportService.generateUserWorkloadReport(userId); menu.pause(); }
                    case 4 -> { generateCompletionSummary(); menu.pause(); }
                    case 0 -> inReportMenu = false;
                }
            } catch (RuntimeException e){
                showError(e);
            }
        }
    }

    private  void generateCompletionSummary() {
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

    private  void showError(RuntimeException e){
        System.out.println("Error: " + e.getMessage());
        menu.pause();
    }
}
