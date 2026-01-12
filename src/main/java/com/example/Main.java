
package main.java.com.example;

import main.java.com.example.services.ConcurrencyService;
import main.java.com.example.services.ProjectService;
import main.java.com.example.services.ReportService;
import main.java.com.example.services.TaskService;
import main.java.com.example.utils.ConsoleMenu;
import main.java.com.example.utils.Seed;
import main.java.com.example.utils.SessionManager;
import main.java.com.example.utils.FileUtils;
import main.java.com.example.services.ConcurrencyService;



import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class Main {

    static void main() {
        Path dataFile = Paths.get("data", "projects_data.json");

        // Phase 3: Load from file (fallback to Seed)
        FileUtils.LoadResult loaded = FileUtils.load(dataFile);
        final ProjectService projectService;
        final TaskService taskService;

        if (loaded.projects.length > 0) {
            projectService = new ProjectService(loaded.projects);
            taskService = new TaskService(loaded.tasks, projectService);
            System.out.printf("Loaded %d project(s) and %d task(s) from file.%n",
                    projectService.getProjectCount(), taskService.getTaskCount());
        } else {
            projectService = new ProjectService(Seed.seedProjects());
            taskService = new TaskService(Seed.seedTasks(), projectService);
            System.out.println("No persisted data found. Seeded initial projects and tasks.");
        }

        final ReportService reportService = new ReportService(projectService, taskService);
        final SessionManager sessionManager = new SessionManager();

        final Scanner scanner = new Scanner(System.in);
        final ConsoleMenu menu = new ConsoleMenu(projectService, taskService, scanner);

        final ConcurrencyService concurrencyService = new ConcurrencyService(projectService, taskService);

        ConsoleApp app = new ConsoleApp(menu, scanner, projectService, taskService, reportService, sessionManager, concurrencyService);
        app.runApplication();


        // Phase 3: Save on exit
        try {
            FileUtils.save(projectService, dataFile);
            System.out.printf("✅ Saved %d project(s) to %s%n", projectService.getProjectCount(), dataFile.toString());
        } catch (Exception e) {
            System.out.println("❌ Failed to save data: " + e.getMessage());
        }

        scanner.close();
        menu.displayExitMessage();
    }
}
