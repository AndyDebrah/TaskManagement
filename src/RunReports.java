import models.*;
import services.*;

public class RunReports {
    public static void main(String[] args) {
        // Initialize services
        ProjectServices projectService = new ProjectServices();
        TaskService taskService = new TaskService(projectService);
        ReportService reportService = new ReportService(projectService, taskService);

        // Create sample software projects
        SoftwareProject swProject1 = new SoftwareProject(
                "PROJ001",
                "E-Commerce Platform",
                "Online shopping platform with payment integration",
                "2025-01-01",
                "2025-06-30",
                100000.00,
                10,
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
                150000.00,
                12,
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
                200000.00,
                8,
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

        // Generate reports
        System.out.println("\n=== SYSTEM STATUS REPORT ===\n");
        reportService.generateStatusReport();

        System.out.println("\n=== PROJECT DETAILED REPORT (PROJ001) ===\n");
        reportService.generateProjectReport("PROJ001");

        System.out.println("\n=== USER WORKLOAD REPORT (USR001) ===\n");
        reportService.generateUserWorkloadReport("USR001");

        System.out.println("\nDone.");
    }
}

