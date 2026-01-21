
package com.example.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.exceptions.InvalidInputException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.exceptions.TaskNotFoundException;
import com.example.models.Project;
import com.example.models.Task;
import com.example.utils.ValidationUtils;



/** Service class for managing task operations. */
public class TaskService {
    private final Map<String, Task> tasksById = new ConcurrentHashMap<>();
    private static final int MAX_TASKS = 500;
    private static int taskCounter = 4;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private ProjectService projectService;

    @SuppressWarnings("unused")
    private static String generateTaskId() {
        return String.format("TSK%04d", taskCounter++);
    }

    public TaskService() {
        this.projectService = null;
    }

    public TaskService(Task[] tasks) {
        this();
        if (tasks != null) {
            for (Task task : tasks) {
                if (task != null) {
                    if (tasksById.size() >= MAX_TASKS) {
                        throw new InvalidInputException("Error: Maximum task limit reached!");
                    }
                    tasksById.put(task.getTaskId(), task);
                }
            }
        }
    }

    public TaskService(Task[] tasks, ProjectService projectService) {
        this(tasks);
        this.projectService = projectService;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new InvalidInputException("Task must not be null.");
        }

        ValidationUtils.requireValidTaskId(task.getTaskId());
        validateTaskData(task);

        if (projectService == null) {
            throw new ProjectNotFoundException(task.getProjectId());
        }
        Project project = projectService.findProjectById(task.getProjectId());
        if (project == null) {
            throw new ProjectNotFoundException(task.getProjectId());
        }

        rwLock.writeLock().lock();
        try {
            if (tasksById.size() >= MAX_TASKS) {
                throw new InvalidInputException("Error: Maximum task limit reached!");
            }
            if (tasksById.putIfAbsent(task.getTaskId(), task) != null) {
                throw new InvalidInputException("Error: Task ID already exists!");
            }
        } finally {
            rwLock.writeLock().unlock();
        }
        project.addTask(task);
    }

    public Task findTaskById(String taskId) {
        return tasksById.get(taskId);
    }

    public void updateTask(String taskId, Task updatedTask) {
        Task existing = tasksById.get(taskId);
        if (existing == null) {
            throw new TaskNotFoundException(taskId);
        }

        ValidationUtils.requireNonEmpty(updatedTask.getTaskId(), "Task ID");
        ValidationUtils.requireNonEmpty(updatedTask.getProjectId(), "Project ID");
        ValidationUtils.requireNonEmpty(updatedTask.getTaskName(), "Task Name");
        ValidationUtils.requireNonEmpty(updatedTask.getDescription(), "Description");
        ValidationUtils.requireNonEmpty(updatedTask.getAssignedTo(), "Assigned User");
        ValidationUtils.requireValidPriority(updatedTask.getPriority());
        ValidationUtils.requireNonEmpty(updatedTask.getDueDate(), "Due Date");
        ValidationUtils.requireValidStatus(updatedTask.getStatus());

        if (existing.getProjectId().equals(updatedTask.getProjectId())) {
            existing.setStatus(updatedTask.getStatus());
            tasksById.put(taskId, existing);
            return;
        }

        if (projectService == null) {
            throw new ProjectNotFoundException(updatedTask.getProjectId());
        }

        Project oldProject = projectService.findProjectById(existing.getProjectId());
        Project newProject = projectService.findProjectById(updatedTask.getProjectId());

        if (newProject == null) {
            existing.setStatus(updatedTask.getStatus());
            tasksById.put(taskId, existing);

            System.out.printf(
                    "⚠ Warning: Cannot reassign task %s → project %s does not exist. Status updated only.%n",
                    taskId, updatedTask.getProjectId()
            );

            return;
        }

        Project first = oldProject != null && oldProject.getProjectId().compareTo(newProject.getProjectId()) <= 0
                ? oldProject : newProject;
        Project second = (first == oldProject) ? newProject : oldProject;

        if (first != null) {
            synchronized (first) {
                if (second != null && second != first) {
                    synchronized (second) {
                        if (oldProject != null) oldProject.removeTask(taskId);
                        newProject.addTask(existing);
                    }
                } else {
                    if (oldProject != null) oldProject.removeTask(taskId);
                    newProject.addTask(existing);
                }
            }
        } else {
            newProject.addTask(existing);
        }

        existing.setStatus(updatedTask.getStatus());
        tasksById.put(taskId, existing);

    }






    public void deleteTask(String taskId) {
        Task removed = tasksById.remove(taskId);
        if (removed == null) {
            throw new TaskNotFoundException(taskId);
        }
        if (projectService == null) {
            throw new ProjectNotFoundException(removed.getProjectId());
        }
        Project project = projectService.findProjectById(removed.getProjectId());
        if (project != null) {
            project.removeTask(taskId);
        } else {
            throw new ProjectNotFoundException(removed.getProjectId());
        }
    }

    public Task[] getAllTasks() {
        return tasksById.values().toArray(new Task[0]);
    }

    public Task[] getTasksByProjectId(String projectId) {
        return tasksById.values().stream()
                .filter(t -> t.getProjectId().equals(projectId))
                .toArray(Task[]::new);
    }

    public Task[] getTasksByUserId(String userId) {
        return tasksById.values().stream()
                .filter(t -> t.getAssignedTo().equals(userId))
                .toArray(Task[]::new);
    }

    public Task[] getTasksByStatus(String status) {
        return tasksById.values().stream()
                .filter(t -> t.getStatus().equalsIgnoreCase(status))
                .toArray(Task[]::new);
    }

    public Task[] getTasksByPriority(String priority) {
        return tasksById.values().stream()
                .filter(t -> t.getPriority().equalsIgnoreCase(priority))
                .toArray(Task[]::new);
    }

    public int getTaskCount() {
        return tasksById.size();
    }

    public double calculateProjectTaskCompletion(String projectId) {
        long total = tasksById.values().stream()
                .filter(t -> t.getProjectId().equals(projectId))
                .count();
        if (total == 0) return 0.0;

        long completed = tasksById.values().stream()
                .filter(t -> t.getProjectId().equals(projectId))
                .filter(Task::isCompleted)
                .count();

        return (completed * 100.0) / total;
    }

    private void validateTaskData(Task task) {
        if (task == null) {
            throw new InvalidInputException("Task must not be null.");
        }
        ValidationUtils.requireNonEmpty(task.getTaskId(), "Task ID");
        ValidationUtils.requireValidProjectId(task.getProjectId());
        ValidationUtils.requireNonEmpty(task.getTaskName(), "Task Name");
        ValidationUtils.requireNonEmpty(task.getDescription(), "Description");
        ValidationUtils.requireNonEmpty(task.getAssignedTo(), "Assigned User");
        ValidationUtils.requireValidPriority(task.getPriority());
        ValidationUtils.requireNonEmpty(task.getDueDate(), "Due Date");
        ValidationUtils.requireValidStatus(task.getStatus());




    }


}

