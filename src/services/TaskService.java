package services;

import models.Task;

/**
 * Service class for managing task operations
 * Handles all task-related business logic
 */
public class TaskService {
    private Task[] tasks;
    private int taskCount;
    private static final int MAX_TASKS = 500;

    // Optional reference to ProjectServices to keep per-project task lists in sync
    private ProjectServices projectService;

    public TaskService() {
        this.tasks = new Task[MAX_TASKS];
        this.taskCount = 0;
        this.projectService = null;
    }

    public TaskService(ProjectServices projectService) {
        this();
        this.projectService = projectService;
    }

    /**
     * Add a new task
     */
    public boolean addTask(Task task) {
        if (taskCount >= MAX_TASKS) {
            System.out.println("Error: Maximum task limit reached!");
            return false;
        }

        if (findTaskById(task.getTaskId()) != null) {
            System.out.println("Error: Task ID already exists!");
            return false;
        }

        tasks[taskCount] = task;
        taskCount++;

        // If project service is available, attempt to add the task to the project
        if (projectService != null) {
            models.Project project = projectService.findProjectById(task.getProjectId());
            if (project != null) {
                project.addTask(task);
            }
        }

        System.out.println("✓ Task added successfully!");
        return true;
    }

    /**
     * Find task by ID
     */
    public Task findTaskById(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                return tasks[i];
            }
        }
        return null;
    }

    /**
     * Update existing task
     */
    public boolean updateTask(String taskId, Task updatedTask) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                tasks[i] = updatedTask;
                System.out.println("✓ Task updated successfully!");
                return true;
            }
        }
        System.out.println("Error: Task not found!");
        return false;
    }

    /**
     * Delete a task
     */
    public boolean deleteTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                String projectId = tasks[i].getProjectId();

                for (int j = i; j < taskCount - 1; j++) {
                    tasks[j] = tasks[j + 1];
                }
                tasks[taskCount - 1] = null;
                taskCount--;

                // If project service is available, remove from project task list
                if (projectService != null) {
                    models.Project project = projectService.findProjectById(projectId);
                    if (project != null) {
                        project.removeTask(taskId);
                    }
                }

                System.out.println("✓ Task deleted successfully!");
                return true;
            }
        }
        System.out.println("Error: Task not found!");
        return false;
    }

    /**
     * Get all tasks
     */
    public Task[] getAllTasks() {
        Task[] result = new Task[taskCount];
        for (int i = 0; i < taskCount; i++) {
            result[i] = tasks[i];
        }
        return result;
    }

    /**
     * Get tasks by project ID
     */
    public Task[] getTasksByProjectId(String projectId) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getProjectId().equals(projectId)) {
                count++;
            }
        }

        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getProjectId().equals(projectId)) {
                result[index++] = tasks[i];
            }
        }
        return result;
    }

    /**
     * Get tasks assigned to a user
     */
    public Task[] getTasksByUserId(String userId) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getAssignedTo().equals(userId)) {
                count++;
            }
        }

        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getAssignedTo().equals(userId)) {
                result[index++] = tasks[i];
            }
        }
        return result;
    }

    /**
     * Get tasks by status
     */
    public Task[] getTasksByStatus(String status) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }

        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus().equalsIgnoreCase(status)) {
                result[index++] = tasks[i];
            }
        }
        return result;
    }

    /**
     * Get tasks by priority
     */
    public Task[] getTasksByPriority(String priority) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getPriority().equalsIgnoreCase(priority)) {
                count++;
            }
        }

        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getPriority().equalsIgnoreCase(priority)) {
                result[index++] = tasks[i];
            }
        }
        return result;
    }

    /**
     * Display all tasks
     */
    public void displayAllTasks() {
        if (taskCount == 0) {
            System.out.println("\n📋 No tasks available.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                     TASK LIST");
        System.out.println("=".repeat(70));

        for (int i = 0; i < taskCount; i++) {
            System.out.printf("\n[%d] ", i + 1);
            tasks[i].displayTaskInfo();
        }

        System.out.println("\nTotal Tasks: " + taskCount);
    }

    /**
     * Get task count
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Calculate completion rate for a project
     */
    public double calculateProjectTaskCompletion(String projectId) {
        Task[] projectTasks = getTasksByProjectId(projectId);
        if (projectTasks.length == 0) {
            return 0.0;
        }

        int completedCount = 0;
        for (Task task : projectTasks) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }

        return (completedCount * 100.0) / projectTasks.length;
    }
}