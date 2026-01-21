package com.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import com.example.models.Project;
import com.example.models.Task;
import com.example.services.ConcurrencyService;
import com.example.services.ProjectService;
import com.example.services.ReportService;
import com.example.services.TaskService;
import com.example.utils.ConsoleMenu;
import com.example.utils.FileUtils;
import com.example.utils.Seed;
import com.example.utils.SessionManager;

public class Main {

    public static void main(String[] args) {
        Path dataFile = Paths.get(System.getProperty("user.dir"))
            .resolve(Paths.get("data", "projects_data.json"))
            .normalize();
        System.out.printf("Data file resolved to: %s (exists=%s)%n", dataFile.toAbsolutePath(), Files.exists(dataFile));

        FileUtils.LoadResult loaded = FileUtils.load(dataFile);
        int loadedProjectCount = loaded.projects == null ? 0 : loaded.projects.length;
        int loadedTaskCount = loaded.tasks == null ? 0 : loaded.tasks.length;
        System.out.printf("Loaded from file -> projects: %d, tasks: %d%n", loadedProjectCount, loadedTaskCount);
        if (loadedProjectCount > 0) {
            System.out.println("Project IDs from file: " + Arrays.stream(loaded.projects)
                .filter(Objects::nonNull)
                .map(Project::getProjectId)
                .toList());
        }
        if (loadedTaskCount > 0) {
            System.out.println("Task IDs from file: " + Arrays.stream(loaded.tasks)
                .filter(Objects::nonNull)
                .map(Task::getTaskId)
                .toList());
        }

        Project[] cleanedProjects = Arrays.stream(loaded.projects == null ? new Project[0] : loaded.projects)
                .filter(Objects::nonNull)
                .filter(p -> p.getProjectId() != null && p.getProjectId().startsWith("PRJ"))
                .toArray(Project[]::new);
        Arrays.stream(loaded.projects == null ? new Project[0] : loaded.projects)
                .filter(Objects::nonNull)
                .filter(p -> p.getProjectId() == null || !p.getProjectId().startsWith("PRJ"))
                .forEach(bad -> System.err.printf("âš  Skipping non-project entry during load: id=%s, name=%s%n",
                        bad.getProjectId(), bad.getProjectName()));

        final ProjectService projectService;
        final TaskService taskService;

        if (cleanedProjects.length > 0) {
            projectService = new ProjectService(cleanedProjects);
            taskService = new TaskService(loaded.tasks, projectService);
            System.out.printf("Loaded %d project(s) and %d task(s) from file.%n",
                    projectService.getProjectCount(), taskService.getTaskCount());
        } else {
            projectService = new ProjectService(Seed.seedProjects());
            taskService = new TaskService(Seed.seedTasks(), projectService);
            System.out.println("No persisted data found. Seeded initial projects and tasks.");
        }
        for (Project p : projectService.getAllProjects()) {
            for (Task t : p.getTasks()) {
                if (taskService.findTaskById(t.getTaskId()) == null) {
                    taskService.addTask(t);
                }
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
