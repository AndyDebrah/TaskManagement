package utils;

import models.*;

public class Seed {

    public static Project[] seedProjects() {
        Project[] projects = new Project[100];

        SoftwareProject swProject1 = new SoftwareProject(
                "PROJ001",
                "E-Commerce Platform",
                "Online shopping platform with payment integration",
                "2025-01-01",
                100000.00,
                10,
                "Java, Spring Boot, React, PostgreSQL",
                "Agile",
                10
        );
        swProject1.setCompletedFeatures(6);
        projects[0] = (swProject1);

        SoftwareProject swProject2 = new SoftwareProject(
                "PROJ002",
                "Mobile Banking App",
                "Secure mobile banking application",
                "2025-02-01",
                150000.00,
                12,
                "Flutter, Firebase, Node.js",
                "Scrum",
                15
        );
        swProject2.setCompletedFeatures(3);
        projects[1] = swProject2;

        HardwareProject hwProject1 = new HardwareProject(
                "PROJ003",
                "IoT Smart Home Hub",
                "Central hub for smart home devices",
                "2025-03-01",
                200000.00,
                8,
                "Embedded Systems",
                20
        );
        hwProject1.setAssembledComponents(15);
        hwProject1.setPrototypeCompleted(true);
        projects[2] = hwProject1;
        return projects;
    }

    public static  Task[] seedTasks(){
        Task[] tasks = new Task[100];

        Task task = new Task("TASK001", "PROJ001", "Implement User Authentication",
                "Create secure login and registration system", "USR001", "High");
        task.setStatus("Completed");
        tasks[0] = task;
        Task task2 = new Task("TASK002", "PROJ001", "Design Product Catalog",
                "Create responsive product listing interface", "USR002", "High");
        task2.setStatus("In Progress");
        tasks[1] = task2;
        Task task3 = new Task("TASK003", "PROJ001", "Integrate Payment Gateway",
                "Add Stripe payment processing", "USR001", "High");
        tasks[2] = task3;
        Task task4 = new Task("TASK004", "PROJ002", "Setup Firebase Backend",
                "Configure Firebase authentication and database", "USR003", "Medium");
        task4.setStatus("Completed");
        tasks[3] = task4;
        Task task5 = new Task("TASK005", "PROJ003", "PCB Design Review",
                "Review and finalize circuit board design", "USR002", "High");
      tasks[4] = task5;

        return tasks;
    }

    private static AdminUser defaultAdmin =
            new AdminUser("ADMIN-001", "System Admin", "admin@app.com", "admin123");

    private static RegularUser defaultRegular =
            new RegularUser("USER-001", "Default User", "user@app.com", "user123");

    public static AdminUser getDefaultAdmin() {
        return defaultAdmin;
    }

    public static RegularUser getDefaultRegular() {
        return defaultRegular;
    }

}