
package main.java.com.example.services;

import main.java.com.example.models.Project;
import main.java.com.example.models.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Concurrency demo service for Phase 4.
 * - Simulates multi-user task updates concurrently.
 * - Uses synchronized methods for thread safety.
 * - Shows progress until all threads complete.
 */
public class ConcurrencyService {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final Random random = new Random();

    public ConcurrencyService(ProjectService projectService, TaskService taskService) {
        this.projectService = Objects.requireNonNull(projectService);
        this.taskService = Objects.requireNonNull(taskService);
    }

    /**
     * Workflow 4: Simulate Concurrent Updates using ExecutorService (threads).
     * Each worker picks tasks and randomly toggles status or marks completion.
     */
    public void simulateConcurrentUpdatesWithExecutor(int workers, int opsPerWorker) {
        List<Task> allTasks = List.of(taskService.getAllTasks());
        if (allTasks.isEmpty()) {
            System.out.println("No tasks available to update.");
            return;
        }

        System.out.printf("Starting concurrent updates: %d worker(s), %d op(s) each%n", workers, opsPerWorker);

        ExecutorService pool = Executors.newFixedThreadPool(Math.max(1, workers));
        CountDownLatch latch = new CountDownLatch(workers);
        Instant start = Instant.now();

        for (int w = 0; w < workers; w++) {
            pool.submit(() -> {
                try {
                    for (int i = 0; i < opsPerWorker; i++) {
                        Task task = allTasks.get(random.nextInt(allTasks.size()));
                       try {
                           simulateRandomTaskUpdate(task);
                       } catch (RuntimeException ex){

                           // Log and continue; do not kill the worker
                           System.out.printf("Skip task %s due to: %s%n", task.getTaskId(), ex.getMessage());

                       }
                        // Simulate variable work
                        sleepQuietly(10 + random.nextInt(40));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Progress monitor
        monitorProgress(latch, start);

        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }

        System.out.println("All workers completed safely.");
        summarizeProgress();
    }

    /**
     * Alternate approach: use parallel streams to update all tasks in parallel.
     * Functional style; thread safety must still be guaranteed by services/models.
     */
    public void simulateParallelStreamUpdates() {
        List<Task> tasks = List.of(taskService.getAllTasks());
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to update.");
            return;
        }
        Instant start = Instant.now();
        System.out.println("Starting parallel stream updates over tasks...");

        tasks.parallelStream().forEach(this::simulateRandomTaskUpdate);

        Duration elapsed = Duration.between(start, Instant.now());
        System.out.printf("Parallel updates completed in %d ms.%n", elapsed.toMillis());
        summarizeProgress();
    }

    // -------------------- Helpers --------------------

    private void simulateRandomTaskUpdate(Task task) {
        // Randomly choose an operation: mark completed or toggle status
        int choice = random.nextInt(3);
        switch (choice) {
            case 0 -> {
                // Mark completed
                task.setStatus("Completed");
                // Reflect in central store (update under TaskService to keep project association consistent)
                taskService.updateTask(task.getTaskId(), task);
            }
            case 1 -> {
                // Toggle to In Progress
                task.setStatus("In Progress");
                taskService.updateTask(task.getTaskId(), task);
            }
            default -> {
                // Toggle to Pending
                task.setStatus("Pending");
                taskService.updateTask(task.getTaskId(), task);
            }
        }
    }

    private void monitorProgress(CountDownLatch latch, Instant start) {
        int lastRemaining = Integer.MAX_VALUE;
        while (latch.getCount() > 0) {
            long remaining = latch.getCount();
            if (remaining != lastRemaining) {
                long total = remaining; // We don't know total; report remaining only
                long doneWorkers = Math.max(0, lastRemaining == Integer.MAX_VALUE ? 0 : (lastRemaining - remaining));
                System.out.printf("Progress: %d worker(s) remaining...%n", remaining);
                lastRemaining = (int) remaining;
            }
            sleepQuietly(200);
        }
        Duration elapsed = Duration.between(start, Instant.now());
        System.out.printf("Concurrent updates finished in %d ms.%n", elapsed.toMillis());
    }

    private void sleepQuietly(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private void summarizeProgress() {
        // Print per-project completion summary (uses streams)
        List<Project> projects = List.of(projectService.getAllProjects());
        if (projects.isEmpty()) return;

        System.out.println("---- Completion Summary ----");
        projects.stream()
                .sorted(Comparator.comparing(Project::getProjectId))
                .forEach(p -> System.out.printf("%s (%s) -> %.2f%%%n",
                        p.getProjectId(), p.getProjectName(), p.calculateCompletionPercentage()));

        System.out.println("----------------------------");
    }
}
