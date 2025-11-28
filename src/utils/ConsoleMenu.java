package utils;

import java.util.Scanner;
import models.*;
import services.*;

/**
 * Console menu system for user interaction
 * Demonstrates user interface design and menu-driven architecture
 *
 * This class handles all menu displays and user navigation
 */
public class ConsoleMenu {
    private Scanner scanner;
    private ProjectServices projectService;
    private TaskService taskService;
    private ReportService reportService;
    private User currentUser;

    public ConsoleMenu(ProjectServices projectService, TaskService taskService,
                       ReportService reportService) {
        this.scanner = new Scanner(System.in);
        this.projectService = projectService;
        this.taskService = taskService;
        this.reportService = reportService;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Display main menu
     */
    public void displayMainMenu() {
        System.out.println("\n" + "╔".repeat(70));
        System.out.println("           PROJECT MANAGEMENT SYSTEM - MAIN MENU");
        System.out.println("╚".repeat(70));
        System.out.println("1.  Project Management");
        System.out.println("2.  Task Management");
        System.out.println("3.  User Management");
        System.out.println("4.  Reports & Analytics");
        System.out.println("5.  System Statistics");
        System.out.println("0.  Exit");
        System.out.println("─".repeat(70));
    }

    /**
     * Display project management submenu
     */
    public void displayProjectMenu() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                    PROJECT MANAGEMENT");
        System.out.println("═".repeat(70));
        System.out.println("1.  Create New Project");
        System.out.println("2.  View All Projects");
        System.out.println("3.  Search Project by ID");
        System.out.println("4.  Update Project");
        System.out.println("5.  Delete Project");
        System.out.println("6.  Filter Projects by Status");
        System.out.println("7.  Filter Projects by Type");
        System.out.println("0.  Back to Main Menu");
        System.out.println("─".repeat(70));
    }

    /**
     * Display task management submenu
     */
    public void displayTaskMenu() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                     TASK MANAGEMENT");
        System.out.println("═".repeat(70));
        System.out.println("1.  Create New Task");
        System.out.println("2.  View All Tasks");
        System.out.println("3.  Search Task by ID");
        System.out.println("4.  Update Task Status");
        System.out.println("5.  Delete Task");
        System.out.println("6.  View Tasks by Project");
        System.out.println("7.  View Tasks by User");
        System.out.println("8.  View Tasks by Priority");
        System.out.println("0.  Back to Main Menu");
        System.out.println("─".repeat(70));
    }

    /**
     * Display report menu
     */
    public void displayReportMenu() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                   REPORTS & ANALYTICS");
        System.out.println("═".repeat(70));
        System.out.println("1.  System Status Report");
        System.out.println("2.  Project Detail Report");
        System.out.println("3.  User Workload Report");
        System.out.println("4.  Project Completion Summary");
        System.out.println("0.  Back to Main Menu");
        System.out.println("─".repeat(70));
    }

    /**
     * Display system statistics
     */
    public void displaySystemStats() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("                    SYSTEM STATISTICS");
        System.out.println("═".repeat(70));
        System.out.printf("Total Projects : %d%n", projectService.getProjectCount());
        System.out.printf("Total Tasks    : %d%n", taskService.getTaskCount());
        System.out.printf("Avg Completion : %.2f%%%n", projectService.getAverageCompletion());
        System.out.println("═".repeat(70));
    }

    /**
     * Display welcome banner
     */
    public void displayWelcomeBanner() {
        System.out.println("\n" + "╔".repeat(70));
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                    ║");
        System.out.println("║            (¯`·.¸¸.·´¯)  PROJECT MANAGEMENT SYSTEM  (¯`·.¸¸.·´¯)   ║");
        System.out.println("║                                                                    ║");
        System.out.println("║
        System.out.println("║                                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        System.out.println("╚".repeat(70));
    }

    /**
     * Display exit message
     */
    public void displayExitMessage() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("           Thank you for using Project Management System!");
        System.out.println("                         Goodbye! 👋");
        System.out.println("═".repeat(70) + "\n");
    }

    /**
     * Pause and wait for user to press Enter
     */
    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Clear screen simulation
     */
    public void clearScreen() {
        // Print multiple newlines to simulate screen clear
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}