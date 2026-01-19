package com.example;

import com.example.services.ConcurrencyService;
import com.example.services.ProjectService;
import com.example.services.ReportService;
import com.example.services.TaskService;
import com.example.utils.ConsoleMenu;
import com.example.utils.FileUtils;
import com.example.utils.Seed;
import com.example.utils.SessionManager;
import com.example.models.Project;
import com.example.models.Task;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) { // âœ… proper entrypoint
        Path dataFile = Paths.get("data", "projects_data.json");

        // Phase 3: Load from file (fallback to Seed)
        FileUtils.LoadResult loaded = FileUtils.load(dataFile);

        // --- One-time sanitation to avoid TSK* objects in ProjectService ---
        Project[] cleanedProjects = Arrays.stream(loaded.projects == null ? new Project[0] : loaded.projects)
                .filter(Objects::nonNull)
                .filter(p -> p.getProjectId() != null && p.getProjectId().startsWith("PRJ"))
                .toArray(Project[]::new);

        // Log anything that was skipped (helps you spot lingering TSK entries)
        Arrays.stream(loaded.projects == null ? new Project[0] : loaded.projects)
                .filter(Objects::nonNull)
                .filter(p -> p.getProjectId() == null || !p.getProjectId().startsWith("PRJ"))
                .forEach(bad -> System.err.printf("âš  Skipping non-project entry during load: id=%s, name=%s%n",
                        bad.getProjectId(), bad.getProjectName()));

        final ProjectService projectService;
        final TaskService taskService;

        if (cleanedProjects.length > 0) {
            projectService = new ProjectService(cleanedProjects); // âœ… only PRJ* here
            taskService = new TaskService(loaded.tasks, projectService); // seed tasks
            System.out.printf("Loaded %d project(s) and %d task(s) from file.%n",
                    projectService.getProjectCount(), taskService.getTaskCount());
        } else {
            // Fallback to seed when file is empty or fully rejected by guards
            // TODO: replace with seedProjects()
            projectService = new ProjectService(Seed.seedProjects());
            // TODO: replace with seedTasks()
            taskService = new TaskService(Seed.seedTasks(), projectService);
            System.out.println("No persisted data found. Seeded initial projects and tasks.");
        }

        // (Optional) Ensure TaskService registry mirrors what's inside each project
        for (Project p : projectService.getAllProjects()) {
            for (Task t : p.getTasks()) {
                taskService.addTask(t);
            }
        }

        final ReportService reportService = new ReportService(projectService, taskService);
        final SessionManager sessionManager = new SessionManager();

        final Scanner scanner = new Scanner(System.in);
        final ConsoleMenu menu = new ConsoleMenu(projectService, taskService, scanner);
        final ConcurrencyService concurrencyService = new ConcurrencyService(projectService, taskService);

        ConsoleApp app = new ConsoleApp(menu, scanner, projectService, taskService, reportService, sessionManager,
                concurrencyService);
        app.runApplication();

        // Phase 3: Save on exit
        try {
            FileUtils.save(projectService, dataFile);
            System.out.printf("âœ… Saved %d project(s) to %s%n", projectService.getProjectCount(), dataFile);
        } catch (Exception e) {
            System.out.println("âŒ Failed to save data: " + e.getMessage());
        }

        scanner.close();
        menu.displayExitMessage();
    }
}
