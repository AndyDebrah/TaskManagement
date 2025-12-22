package utils;

import java.util.Scanner;

import models.User;
import services.ProjectService;
import services.ReportService;
import services.TaskService;

/**
 * Console menu system for user interaction.
 * Presents text-based menus and simple helper utilities.
 */
public class ConsoleMenu {
    private final Scanner scanner;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final  SessionManager sessionManager;

    public ConsoleMenu(ProjectService projectService, TaskService taskService,
                        Scanner scanner, SessionManager sessionManager) {
        this.scanner = scanner;
        this.projectService = projectService;
        this.taskService = taskService;
        this.sessionManager = sessionManager;
    }

    public void displayMainMenu() {
        System.out.println();
        System.out.println("================= PROJECT MANAGEMENT SYSTEM - MAIN MENU =================");
        System.out.println("1.  Project Management");
        System.out.println("2.  Task Management");
        System.out.println("3.  User Management");
        System.out.println("4.  Reports & Analytics");
        System.out.println("5.  System Statistics");
        System.out.println("6. Switch User");
        System.out.println("0.  Exit");
        System.out.println("=======================================================================");
    }

    public void displayProjectMenu() {
        System.out.println();
        System.out.println("-------------------- PROJECT MANAGEMENT --------------------");
        System.out.println("1.  Create New Project");
        System.out.println("2.  View All Projects");
        System.out.println("3.  Search Project by ID");
        System.out.println("4.  Update Project");
        System.out.println("5.  Delete Project");
        System.out.println("6.  Filter Projects by Status");
        System.out.println("7.  Filter Projects by Type");
        System.out.println("0.  Back to Main Menu");
        System.out.println("------------------------------------------------------------");
    }

    public void displayTaskMenu() {
        System.out.println();
        System.out.println("----------------------- TASK MANAGEMENT -----------------------");
        System.out.println("1.  Create New Task");
        System.out.println("2.  View All Tasks");
        System.out.println("3.  Search Task by ID");
        System.out.println("4.  Update Task Status");
        System.out.println("5.  Delete Task");
        System.out.println("6.  View Tasks by Project");
        System.out.println("7.  View Tasks by User");
        System.out.println("8.  View Tasks by Priority");
        System.out.println("0.  Back to Main Menu");
        System.out.println("--------------------------------------------------------------");
    }

    public void displayReportMenu() {
        System.out.println();
        System.out.println("---------------------- REPORTS & ANALYTICS ---------------------");
        System.out.println("1.  System Status Report");
        System.out.println("2.  Project Detail Report");
        System.out.println("3.  User Workload Report");
        System.out.println("4.  Project Completion Summary");
        System.out.println("0.  Back to Main Menu");
        System.out.println("---------------------------------------------------------------");
    }

    public void displaySystemStats() {
        System.out.println();
        System.out.println("------------------------ SYSTEM STATISTICS ---------------------");
        System.out.printf("Total Projects : %d%n", projectService.getProjectCount());
        System.out.printf("Total Tasks    : %d%n", taskService.getTaskCount());
        System.out.printf("Avg Completion : %.2f%%%n", projectService.getAverageCompletion());
        System.out.println("---------------------------------------------------------------");
    }

    public void displayWelcomeBanner() {
        System.out.println();
        System.out.println("================ Welcome to Project Management System ================");
        System.out.println("A small console application to manage projects and tasks.");
        System.out.println("======================================================================");
    }

    public void displayExitMessage() {
        System.out.println();
        System.out.println("======================================================================");
        System.out.println("Thank you for using Project Management System. Goodbye!");
        System.out.println("======================================================================\n");
    }

    public void pause() {
        System.out.println();
        System.out.println("Press Enter to continue...");
        // use the shared scanner provided by Main
        scanner.nextLine();
    }

    public void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }


}