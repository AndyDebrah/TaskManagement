package main.java.com.example;

import main.java.com.example.services.ProjectService;
import main.java.com.example.services.ReportService;
import main.java.com.example.services.TaskService;
import main.java.com.example.utils.ConsoleMenu;
import main.java.com.example.utils.Seed;
import main.java.com.example.utils.SessionManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final ProjectService projectService = new ProjectService(Seed.seedProjects());
        final TaskService taskService = new TaskService(Seed.seedTasks(), projectService);
        final ReportService reportService = new ReportService(projectService, taskService);

        final SessionManager sessionManager = new SessionManager();

        final ConsoleMenu menu;
        final Scanner scanner;

        scanner = new Scanner(System.in);
        menu = new ConsoleMenu(projectService, taskService, scanner);
        ConsoleApp app = new ConsoleApp(menu, scanner, projectService, taskService, reportService, sessionManager);
        app.runApplication();


        scanner.close();
        menu.displayExitMessage();
    }
}
