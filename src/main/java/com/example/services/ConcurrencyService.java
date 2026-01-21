
package com.example.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.example.exceptions.ConcurrencyException;
import com.example.models.Project;
import com.example.models.Task;

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

    private static final class AutoClosingExecutor implements AutoCloseable {
        private final ExecutorService delegate;

        private AutoClosingExecutor(ExecutorService delegate) {
            this.delegate = delegate;
        }

        static AutoClosingExecutor fixedPool(int n) {
            return new AutoClosingExecutor(Executors.newFixedThreadPool(Math.max(1, n)));
        }

        ExecutorService get() {
            return delegate;
        }

        @Override
        public void close() {
            delegate.shutdown();
            try {
                if (!delegate.awaitTermination(30, TimeUnit.SECONDS)) {
                    throw ConcurrencyException.forExecutorShutdownTimeout(30);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw ConcurrencyException.forInterruptedOperation("Executor shutdown");
            }
        }
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

        try (AutoClosingExecutor closer = AutoClosingExecutor.fixedPool(workers)) {
            ExecutorService pool = closer.get();
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
                                System.out.printf("Skip task %s due to: %s%n", task.getTaskId(), ex.getMessage());
                            }
                            sleepQuietly(10 + random.nextInt(40));
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            monitorProgress(latch, start);
        }

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

    private void simulateRandomTaskUpdate(Task task) {
        int choice = random.nextInt(3);
        switch (choice) {
            case 0 -> {
                task.setStatus("Completed");
                taskService.updateTask(task.getTaskId(), task);
            }
            case 1 -> {
                task.setStatus("In Progress");
                taskService.updateTask(task.getTaskId(), task);
            }
            default -> {
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
                long total = remaining;
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
