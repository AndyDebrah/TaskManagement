package services;

import models.Task;
import exceptions.TaskNotFoundException;
import exceptions.ProjectNotFoundException;
import exceptions.InvalidInputException;
import utils.ValidationUtils;

/** Service class for managing task operations. */
public class TaskService {
    private final Task[] tasks;
    private int taskCount;
    private static final int MAX_TASKS = 500;

    private ProjectService projectService;

    public TaskService() {
        this.tasks = new Task[MAX_TASKS];
        this.taskCount = 0;
        this.projectService = null;
    }

    public TaskService(Task[] tasks) {
        this();
        for (Task task : tasks) {
            if (task != null) {
                this.tasks[taskCount++] = task;
            }
        }
    }

    public TaskService(Task[] tasks, ProjectService projectService) {
        this(tasks);
        this.projectService = projectService;
    }


    public void addTask(Task task) {
        if (taskCount >= MAX_TASKS) {
            throw new InvalidInputException("Error: Maximum task limit reached!");
        }
        if (findTaskById(task.getTaskId()) != null) {
           throw new InvalidInputException("Error: Task ID already exists!");
        }
        validateTaskData(task);
        if(projectService==null){
            throw new ProjectNotFoundException(task.getProjectId());
        }
        models.Project project = projectService.findProjectById(task.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(task.getProjectId());
        }
        tasks[taskCount++] = task;
        project.addTask(task);
        // UI layer should inform the user; removed duplicate console output from service


    }

    public Task findTaskById(String taskId) {
        for (int i = 0; i < taskCount; i++) if (tasks[i].getTaskId().equals(taskId)) return tasks[i];
        return null;
    }

    public void updateTask(String taskId, Task updatedTask) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                validateTaskData(updatedTask);
                tasks[i] = updatedTask;
                // UI layer will handle user-facing messages; removed console output
                return;
            }
        }
        throw new TaskNotFoundException(taskId);
    }

    public void deleteTask(String taskId) {
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
                else{
                    throw new ProjectNotFoundException(projectId);
                }

                return;
            }
        }
       throw new TaskNotFoundException(taskId);
    }

    public Task[] getAllTasks() {
        Task[] result = new Task[taskCount];
        System.arraycopy(tasks, 0, result, 0, taskCount);
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

    /**
     * Deprecated: presentation should be handled by the UI layer. Use getAllTasks() instead.
     */
    @Deprecated
    public void displayAllTasks() {
        throw new UnsupportedOperationException("Use getAllTasks() and present data in the UI layer.");
    }

    public int getTaskCount() { return taskCount; }

    public double calculateProjectTaskCompletion(String projectId) {
        Task[] projectTasks = getTasksByProjectId(projectId);
        if (projectTasks.length == 0) return 0.0;
        int completedCount = 0;
        for (Task task : projectTasks) if (task.isCompleted()) completedCount++;
        return (completedCount * 100.0) / projectTasks.length;
    }
    private void validateTaskData(Task task) {
        if (task == null) {
            throw new InvalidInputException("Task must not be null.");
        }
        ValidationUtils.requireNonEmpty(task.getTaskId(), "Task ID");
        ValidationUtils.requireNonEmpty(task.getProjectId(), "Project ID");
        ValidationUtils.requireNonEmpty(task.getTaskName(), "Task Name");
        ValidationUtils.requireNonEmpty(task.getDescription(), "Description");
        ValidationUtils.requireNonEmpty(task.getAssignedTo(), "Assigned User");
        ValidationUtils.requireValidPriority(task.getPriority());
        ValidationUtils.requireNonEmpty(task.getDueDate(), "Due Date");
        ValidationUtils.requireValidStatus(task.getStatus());
    }
}

