import models.*;
import services.*;
import utils.*;
import java.util.Scanner;

/**
 * Main application entry point
 * Demonstrates complete application architecture and flow
 *
 * This is a console-based Project/Task Management System built with Java 21
 * showcasing OOP principles: Encapsulation, Inheritance, Polymorphism,
 * Abstract Classes, and Interfaces
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class Main {
    // Service layer instances
    private static ProjectServices projectService;
    private static TaskService taskService;
    private static ReportService reportService;

    // UI components
    private static ConsoleMenu menu;
    private static Scanner scanner;

    // Current logged-in user (simulated authentication)
    private static User currentUser;

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        // Initialize services
        initializeServices();

        // Initialize UI
        scanner = new Scanner(System.in);
        menu = new ConsoleMenu(projectService, taskService, reportService);

        // Display welcome banner
        menu.displayWelcomeBanner();

        // Load sample data for demonstration
        loadSampleData();

        // Simulate user login (in real system, would have authentication)
        simulateLogin();

        // Main application loop
        runApplication();

        // Cleanup
        scanner.close();
        menu.displayExitMessage();
    }

    /**
     * Initialize all service layer components
     */
    private static void initializeServices() {
        projectService = new ProjectServices();
        taskService = new TaskService(projectService);
        reportService = new ReportService(projectService, taskService);
    }

    /**
     * Load sample data for demonstration purposes
     * In a real system, this would load from database
     */
    private static void loadSampleData() {
        System.out.println("\n📦 Loading sample data...");

        // Create sample software projects
        SoftwareProject swProject1 = new SoftwareProject(
                "PROJ001",
                "E-Commerce Platform",
                "Online shopping platform with payment integration",
                "2025-01-01",
                "2025-06-30",
                100000.00, // budget
                10,       // teamSize
                "Java, Spring Boot, React, PostgreSQL",
                "Agile",
                10
        );
        swProject1.setCompletedFeatures(6); // 60% complete
        projectService.addProject(swProject1);

        SoftwareProject swProject2 = new SoftwareProject(
                "PROJ002",
                "Mobile Banking App",
                "Secure mobile banking application",
                "2025-02-01",
                "2025-08-31",
                150000.00, // budget
                12,        // teamSize
                "Flutter, Firebase, Node.js",
                "Scrum",
                15
        );
        swProject2.setCompletedFeatures(3); // 20% complete
        projectService.addProject(swProject2);

        // Create sample hardware project
        HardwareProject hwProject1 = new HardwareProject(
                "PROJ003",
                "IoT Smart Home Hub",
                "Central hub for smart home devices",
                "2025-03-01",
                "2025-12-31",
                200000.00, // budget
                8,         // teamSize
                "Embedded Systems",
                20
        );
        hwProject1.setAssembledComponents(15); // 75% assembly
        hwProject1.setPrototypeCompleted(true); // +20% = 95% total
        projectService.addProject(hwProject1);

        // Create sample tasks
        Task task1 = new Task(
                "TASK001",
                "PROJ001",
                "Implement User Authentication",
                "Create secure login and registration system",
                "USR001",
                "High",
                "2025-02-15"
        );
        task1.setStatus("Completed");
        taskService.addTask(task1);

        Task task2 = new Task(
                "TASK002",
                "PROJ001",
                "Design Product Catalog",
                "Create responsive product listing interface",
                "USR002",
                "High",
                "2025-03-01"
        );
        task2.setStatus("In Progress");
        taskService.addTask(task2);

        Task task3 = new Task(
                "TASK003",
                "PROJ001",
                "Integrate Payment Gateway",
                "Add Stripe payment processing",
                "USR001",
                "High",
                "2025-03-15"
        );
        taskService.addTask(task3);

        Task task4 = new Task(
                "TASK004",
                "PROJ002",
                "Setup Firebase Backend",
                "Configure Firebase authentication and database",
                "USR003",
                "Medium",
                "2025-03-10"
        );
        task4.setStatus("Completed");
        taskService.addTask(task4);

        Task task5 = new Task(
                "TASK005",
                "PROJ003",
                "PCB Design Review",
                "Review and finalize circuit board design",
                "USR002",
                "High",
                "2025-04-01"
        );
        taskService.addTask(task5);

        System.out.println("✓ Sample data loaded successfully!");
        System.out.println("  - 3 Projects created");
        System.out.println("  - 5 Tasks created");
    }

    /**
     * Simulate user login
     * In production, would have proper authentication
     */
    private static void simulateLogin() {
        System.out.println("\n" + "─".repeat(70));
        System.out.println("🔐 AUTHENTICATION");
        System.out.println("─".repeat(70));
        System.out.println("Login as:");
        System.out.println("1. Administrator");
        System.out.println("2. Regular User");

        int choice = ValidationUtils.getValidatedChoice(scanner,
                "Select user type (1-2): ", 1, 2);

        if (choice == 1) {
            currentUser = new AdminUser("ADM001", "Admin User",
                    "admin@projectmgmt.com", "admin123");
            System.out.println("✓ Logged in as Administrator");
        } else {
            currentUser = new RegularUser("USR001", "John Developer",
                    "john@projectmgmt.com", "user123");
            System.out.println("✓ Logged in as Regular User");
        }

        menu.setCurrentUser(currentUser);
        currentUser.displayUserInfo();
        menu.pause();
    }

    /**
     * Main application loop
     */
    private static void runApplication() {
        boolean running = true;

        while (running) {
            menu.displayMainMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner,
                    "Enter your choice: ", 0, 5);

            switch (choice) {
                case 1:
                    handleProjectManagement();
                    break;
                case 2:
                    handleTaskManagement();
                    break;
                case 3:
                    handleUserManagement();
                    break;
                case 4:
                    handleReports();
                    break;
                case 5:
                    menu.displaySystemStats();
                    menu.pause();
                    break;
                case 0:
                    running = false;
                    break;
            }
        }
    }

    /**
     * Handle project management operations
     */
    private static void handleProjectManagement() {
        boolean inProjectMenu = true;

        while (inProjectMenu) {
            menu.displayProjectMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner,
                    "Enter your choice: ", 0, 7);

            switch (choice) {
                case 1:
                    createNewProject();
                    break;
                case 2:
                    projectService.displayAllProjects();
                    menu.pause();
                    break;
                case 3:
                    searchProject();
                    break;
                case 4:
                    updateProject();
                    break;
                case 5:
                    deleteProject();
                    break;
                case 6:
                    filterProjectsByStatus();
                    break;
                case 7:
                    filterProjectsByType();
                    break;
                case 0:
                    inProjectMenu = false;
                    break;
            }
        }
    }

    /**
     * Create a new project
     */
    private static void createNewProject() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                    CREATE NEW PROJECT");
        System.out.println("═".repeat(70));

        System.out.println("Select Project Type:");
        System.out.println("1. Software Project");
        System.out.println("2. Hardware Project");

        int type = ValidationUtils.getValidatedChoice(scanner,
                "Enter choice (1-2): ", 1, 2);

        String projectId = ValidationUtils.getValidatedString(scanner,
                "Enter Project ID: ");
        String name = ValidationUtils.getValidatedString(scanner,
                "Enter Project Name: ");
        String description = ValidationUtils.getValidatedString(scanner,
                "Enter Description: ");
        String startDate = ValidationUtils.getValidatedDate(scanner,
                "Enter Start Date (YYYY-MM-DD): ");
        String endDate = ValidationUtils.getValidatedDate(scanner,
                "Enter End Date (YYYY-MM-DD): ");

        if (type == 1) {
            String techStack = ValidationUtils.getValidatedString(scanner,
                    "Enter Technology Stack: ");
            String methodology = ValidationUtils.getValidatedString(scanner,
                    "Enter Methodology (Agile/Waterfall): ");
            int totalFeatures = ValidationUtils.getValidatedPositiveInteger(scanner,
                    "Enter Total Features: ");
            int teamSize = ValidationUtils.getValidatedPositiveInteger(scanner,
                    "Enter Team Size: ");
            int budgetInt = ValidationUtils.getValidatedPositiveInteger(scanner,
                    "Enter Budget (whole number): ");

            SoftwareProject project = new SoftwareProject(projectId, name,
                    description, startDate, endDate, (double)budgetInt, teamSize, techStack, methodology,
                    totalFeatures);
            projectService.addProject(project);
        } else {
            String hardwareType = ValidationUtils.getValidatedString(scanner,
                    "Enter Hardware Type: ");
            int totalComponents = ValidationUtils.getValidatedPositiveInteger(scanner,
                    "Enter Total Components: ");
            int teamSize = ValidationUtils.getValidatedPositiveInteger(scanner,
                    "Enter Team Size: ");
            int budgetInt = ValidationUtils.getValidatedPositiveInteger(scanner,
                    "Enter Budget (whole number): ");

            HardwareProject project = new HardwareProject(projectId, name,
                    description, startDate, endDate, (double)budgetInt, teamSize, hardwareType,
                    totalComponents);
            projectService.addProject(project);
        }

        menu.pause();
    }

    /**
     * Search for a project
     */
    private static void searchProject() {
        String projectId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Project ID to search: ");

        Project project = projectService.findProjectById(projectId);
        if (project != null) {
            System.out.println("\n✓ Project Found:");
            project.displayProjectInfo();
        } else {
            System.out.println("\n❌ Project not found!");
        }

        menu.pause();
    }

    /**
     * Update a project
     */
    private static void updateProject() {
        String projectId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Project ID to update: ");

        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            System.out.println("❌ Project not found!");
            menu.pause();
            return;
        }

        System.out.println("\nCurrent Project Details:");
        project.displayProjectInfo();

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Project Name");
        System.out.println("2. Description");
        System.out.println("3. Status");
        System.out.println("4. End Date");

        int choice = ValidationUtils.getValidatedChoice(scanner,
                "Enter choice (1-4): ", 1, 4);

        switch (choice) {
            case 1:
                String newName = ValidationUtils.getValidatedString(scanner,
                        "Enter new name: ");
                project.setProjectName(newName);
                break;
            case 2:
                String newDesc = ValidationUtils.getValidatedString(scanner,
                        "Enter new description: ");
                project.setDescription(newDesc);
                break;
            case 3:
                System.out.println("Status options: Active, Completed, On Hold");
                String newStatus = ValidationUtils.getValidatedString(scanner,
                        "Enter new status: ");
                project.setStatus(newStatus);
                break;
            case 4:
                String newDate = ValidationUtils.getValidatedDate(scanner,
                        "Enter new end date (YYYY-MM-DD): ");
                project.setEndDate(newDate);
                break;
        }

        projectService.updateProject(projectId, project);
        menu.pause();
    }

    /**
     * Delete a project
     */
    private static void deleteProject() {
        String projectId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Project ID to delete: ");

        Project project = projectService.findProjectById(projectId);
        if (project != null) {
            project.displayProjectInfo();
            System.out.print("\nAre you sure you want to delete this project? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                projectService.deleteProject(projectId);
            } else {
                System.out.println("Deletion cancelled.");
            }
        }

        menu.pause();
    }

    /**
     * Filter projects by status
     */
    private static void filterProjectsByStatus() {
        System.out.println("\nFilter by status:");
        System.out.println("1. Active");
        System.out.println("2. Completed");
        System.out.println("3. On Hold");

        int choice = ValidationUtils.getValidatedChoice(scanner,
                "Enter choice (1-3): ", 1, 3);

        String status = "";
        switch (choice) {
            case 1: status = "Active"; break;
            case 2: status = "Completed"; break;
            case 3: status = "On Hold"; break;
        }

        Project[] filtered = projectService.getProjectsByStatus(status);
        System.out.println("\n" + "═".repeat(70));
        System.out.println("Projects with status: " + status);
        System.out.println("═".repeat(70));

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

    /**
     * Filter projects by type
     */
    private static void filterProjectsByType() {
        System.out.println("\nFilter by type:");
        System.out.println("1. Software Development");
        System.out.println("2. Hardware Development");

        int choice = ValidationUtils.getValidatedChoice(scanner,
                "Enter choice (1-2): ", 1, 2);

        String type = choice == 1 ? "Software Development" : "Hardware Development";

        Project[] filtered = projectService.getProjectsByType(type);
        System.out.println("\n" + "═".repeat(70));
        System.out.println("Projects of type: " + type);
        System.out.println("═".repeat(70));

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

    /**
     * Handle task management operations
     */
    private static void handleTaskManagement() {
        boolean inTaskMenu = true;

        while (inTaskMenu) {
            menu.displayTaskMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner,
                    "Enter your choice: ", 0, 8);

            switch (choice) {
                case 1:
                    createNewTask();
                    break;
                case 2:
                    taskService.displayAllTasks();
                    menu.pause();
                    break;
                case 3:
                    searchTask();
                    break;
                case 4:
                    updateTaskStatus();
                    break;
                case 5:
                    deleteTask();
                    break;
                case 6:
                    viewTasksByProject();
                    break;
                case 7:
                    viewTasksByUser();
                    break;
                case 8:
                    viewTasksByPriority();
                    break;
                case 0:
                    inTaskMenu = false;
                    break;
            }
        }
    }

    /**
     * Create a new task
     */
    private static void createNewTask() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                    CREATE NEW TASK");
        System.out.println("═".repeat(70));

        String taskId = ValidationUtils.getValidatedString(scanner,
                "Enter Task ID: ");
        String projectId = ValidationUtils.getValidatedString(scanner,
                "Enter Project ID: ");

        // Verify project exists
        if (projectService.findProjectById(projectId) == null) {
            System.out.println("❌ Project not found! Task creation cancelled.");
            menu.pause();
            return;
        }

        String taskName = ValidationUtils.getValidatedString(scanner,
                "Enter Task Name: ");
        String description = ValidationUtils.getValidatedString(scanner,
                "Enter Description: ");
        String assignedTo = ValidationUtils.getValidatedString(scanner,
                "Assign to User ID: ");

        System.out.println("Priority: High, Medium, Low");
        String priority = ValidationUtils.getValidatedString(scanner,
                "Enter Priority: ");

        String dueDate = ValidationUtils.getValidatedDate(scanner,
                "Enter Due Date (YYYY-MM-DD): ");

        Task task = new Task(taskId, projectId, taskName, description,
                assignedTo, priority, dueDate);
        taskService.addTask(task);

        menu.pause();
    }

    /**
     * Search for a task
     */
    private static void searchTask() {
        String taskId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Task ID to search: ");

        Task task = taskService.findTaskById(taskId);
        if (task != null) {
            System.out.println("\n✓ Task Found:");
            task.displayTaskInfo();
        } else {
            System.out.println("\n❌ Task not found!");
        }

        menu.pause();
    }

    /**
     * Update task status
     */
    private static void updateTaskStatus() {
        String taskId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Task ID to update: ");

        Task task = taskService.findTaskById(taskId);
        if (task == null) {
            System.out.println("❌ Task not found!");
            menu.pause();
            return;
        }

        System.out.println("\nCurrent Task:");
        task.displayTaskInfo();

        System.out.println("\nNew Status:");
        System.out.println("1. Pending");
        System.out.println("2. In Progress");
        System.out.println("3. Completed");

        int choice = ValidationUtils.getValidatedChoice(scanner,
                "Enter choice (1-3): ", 1, 3);

        switch (choice) {
            case 1: task.setStatus("Pending"); break;
            case 2: task.setStatus("In Progress"); break;
            case 3: task.setStatus("Completed"); break;
        }

        taskService.updateTask(taskId, task);
        menu.pause();
    }

    /**
     * Delete a task
     */
    private static void deleteTask() {
        String taskId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Task ID to delete: ");

        Task task = taskService.findTaskById(taskId);
        if (task != null) {
            task.displayTaskInfo();
            System.out.print("\nAre you sure you want to delete this task? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                taskService.deleteTask(taskId);
            } else {
                System.out.println("Deletion cancelled.");
            }
        }

        menu.pause();
    }

    /**
     * View tasks by project
     */
    private static void viewTasksByProject() {
        String projectId = ValidationUtils.getValidatedString(scanner,
                "\nEnter Project ID: ");

        Task[] tasks = taskService.getTasksByProjectId(projectId);

        System.out.println("\n" + "═".repeat(70));
        System.out.println("Tasks for Project: " + projectId);
        System.out.println("═".repeat(70));

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

    /**
     * View tasks by user
     */
    private static void viewTasksByUser() {
        String userId = ValidationUtils.getValidatedString(scanner,
                "\nEnter User ID: ");

        Task[] tasks = taskService.getTasksByUserId(userId);

        System.out.println("\n" + "═".repeat(70));
        System.out.println("Tasks assigned to User: " + userId);
        System.out.println("═".repeat(70));

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

    /**
     * View tasks by priority
     */
    private static void viewTasksByPriority() {
        System.out.println("\nFilter by priority:");
        System.out.println("1. High");
        System.out.println("2. Medium");
        System.out.println("3. Low");

        int choice = ValidationUtils.getValidatedChoice(scanner,
                "Enter choice (1-3): ", 1, 3);

        String priority = "";
        switch (choice) {
            case 1: priority = "High"; break;
            case 2: priority = "Medium"; break;
            case 3: priority = "Low"; break;
        }

        Task[] tasks = taskService.getTasksByPriority(priority);

        System.out.println("\n" + "═".repeat(70));
        System.out.println("Tasks with priority: " + priority);
        System.out.println("═".repeat(70));

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

    /**
     * Handle user management operations
     */
    private static void handleUserManagement() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                    USER MANAGEMENT");
        System.out.println("═".repeat(70));
        System.out.println("Current User Information:");
        currentUser.displayUserInfo();

        System.out.println("\nUser Permissions:");
        String[] permissions = currentUser.getPermissions();
        for (String permission : permissions) {
            System.out.println("  ✓ " + permission);
        }

        menu.pause();
    }

    /**
     * Handle reports generation
     */
    private static void handleReports() {
        boolean inReportMenu = true;

        while (inReportMenu) {
            menu.displayReportMenu();
            int choice = ValidationUtils.getValidatedChoice(scanner,
                    "Enter your choice: ", 0, 4);

            switch (choice) {
                case 1:
                    reportService.generateStatusReport();
                    menu.pause();
                    break;
                case 2:
                    String projectId = ValidationUtils.getValidatedString(scanner,
                            "\nEnter Project ID: ");
                    reportService.generateProjectReport(projectId);
                    menu.pause();
                    break;
                case 3:
                    String userId = ValidationUtils.getValidatedString(scanner,
                            "\nEnter User ID: ");
                    reportService.generateUserWorkloadReport(userId);
                    menu.pause();
                    break;
                case 4:
                    generateCompletionSummary();
                    menu.pause();
                    break;
                case 0:
                    inReportMenu = false;
                    break;
            }
        }
    }

    /**
     * Generate project completion summary
     */
    private static void generateCompletionSummary() {
        Project[] projects = projectService.getAllProjects();

        System.out.println("\n" + "═".repeat(70));
        System.out.println("              PROJECT COMPLETION SUMMARY");
        System.out.println("═".repeat(70));

        if (projects.length == 0) {
            System.out.println("No projects available.");
            return;
        }

        System.out.printf("%-12s %-30s %-15s %10s%n",
                "Project ID", "Project Name", "Type", "Completion");
        System.out.println("─".repeat(70));

        for (Project project : projects) {
            System.out.printf("%-12s %-30s %-15s %9.2f%%%n",
                    project.getProjectId(),
                    project.getProjectName().substring(0,
                            Math.min(project.getProjectName().length(), 28)),
                    project.getProjectType().substring(0, 13),
                    project.calculateCompletionPercentage());
        }

        System.out.println("═".repeat(70));
        System.out.printf("Average Completion: %.2f%%%n",
                projectService.getAverageCompletion());
    }
}
