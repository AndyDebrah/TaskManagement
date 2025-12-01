package services;

import models.Task;

/** Service class for managing task operations. */
public class TaskService {
    private Task[] tasks;
    private int taskCount;
    private static final int MAX_TASKS = 500;

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

    public boolean addTask(Task task) {
        if (taskCount >= MAX_TASKS) {
            System.out.println("Error: Maximum task limit reached!");
            return false;
        }
        if (findTaskById(task.getTaskId()) != null) {
            System.out.println("Error: Task ID already exists!");
            return false;
        }
        tasks[taskCount++] = task;
        if (projectService != null) {
            models.Project project = projectService.findProjectById(task.getProjectId());
            if (project != null) project.addTask(task);
        }
        System.out.println("Task added successfully.");
        return true;
    }

    public Task findTaskById(String taskId) {
        for (int i = 0; i < taskCount; i++) if (tasks[i].getTaskId().equals(taskId)) return tasks[i];
        return null;
    }

    public boolean updateTask(String taskId, Task updatedTask) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                tasks[i] = updatedTask;
                System.out.println("Task updated successfully.");
                return true;
            }
        }
        System.out.println("Error: Task not found!");
        return false;
    }

    public boolean deleteTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                String projectId = tasks[i].getProjectId();
                for (int j = i; j < taskCount - 1; j++) tasks[j] = tasks[j + 1];
                tasks[taskCount - 1] = null;
                taskCount--;
                if (projectService != null) {
                    models.Project project = projectService.findProjectById(projectId);
                    if (project != null) project.removeTask(taskId);
                }
                System.out.println("Task deleted successfully.");
                return true;
            }
        }
        System.out.println("Error: Task not found!");
        return false;
    }

    public Task[] getAllTasks() {
        Task[] result = new Task[taskCount];
        for (int i = 0; i < taskCount; i++) result[i] = tasks[i];
        return result;
    }

    public Task[] getTasksByProjectId(String projectId) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getProjectId().equals(projectId)) count++;
        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getProjectId().equals(projectId)) result[index++] = tasks[i];
        return result;
    }

    public Task[] getTasksByUserId(String userId) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getAssignedTo().equals(userId)) count++;
        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getAssignedTo().equals(userId)) result[index++] = tasks[i];
        return result;
    }

    public Task[] getTasksByStatus(String status) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getStatus().equalsIgnoreCase(status)) count++;
        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getStatus().equalsIgnoreCase(status)) result[index++] = tasks[i];
        return result;
    }

    public Task[] getTasksByPriority(String priority) {
        int count = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getPriority().equalsIgnoreCase(priority)) count++;
        Task[] result = new Task[count];
        int index = 0;
        for (int i = 0; i < taskCount; i++) if (tasks[i].getPriority().equalsIgnoreCase(priority)) result[index++] = tasks[i];
        return result;
    }

    public void displayAllTasks() {
        if (taskCount == 0) {
            System.out.println("No tasks available.");
            return;
        }
        System.out.println("TASK LIST");
        for (int i = 0; i < taskCount; i++) {
            System.out.printf("\n[%d] ", i + 1);
            tasks[i].displayTaskInfo();
        }
        System.out.println("Total Tasks: " + taskCount);
    }

    public int getTaskCount() { return taskCount; }

    public double calculateProjectTaskCompletion(String projectId) {
        Task[] projectTasks = getTasksByProjectId(projectId);
        if (projectTasks.length == 0) return 0.0;
        int completedCount = 0;
        for (Task task : projectTasks) if (task.isCompleted()) completedCount++;
        return (completedCount * 100.0) / projectTasks.length;
    }
}