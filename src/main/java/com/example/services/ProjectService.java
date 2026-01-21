
package com.example.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.exceptions.InvalidProjectDataException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.models.Project;
import com.example.utils.ValidationUtils;

/** Service class for managing project operations (in-memory). */
public class ProjectService {
    private final Map<String, Project> projects = new ConcurrentHashMap<>();
    private static final int MAX_PROJECTS = 100;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ProjectService(Project[] seededProjects) {
        if (seededProjects != null) {
            for (Project project : seededProjects) {
                if (project != null) {
                    if (projects.size() >= MAX_PROJECTS) {
                        throw new InvalidProjectDataException("Error: Maximum project limit reached!");
                    }
                    projects.put(project.getProjectId(), project);
                }
            }
        }
    }

    public void addProject(Project project) {
        if (project == null) {
            throw new InvalidProjectDataException("Error: Project data cannot be null!");
        }
        ValidationUtils.requireValidProjectId(project.getProjectId());
        validateProjectData(project);

        rwLock.writeLock().lock();
        try {
            if (projects.size() >= MAX_PROJECTS) {
                throw new InvalidProjectDataException("Error: Maximum project limit reached!");
            }
            if (projects.putIfAbsent(project.getProjectId(), project) != null) {
                throw new InvalidProjectDataException("Error: Project ID already exists!");
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public Project findProjectById(String projectId) {
        return projects.get(projectId);
    }

    public void updateProject(String projectId, Project updatedProject) {
        ValidationUtils.requireValidProjectId(updatedProject.getProjectId());
        validateProjectData(updatedProject);

        projects.compute(projectId, (id, existing) -> {
            if (existing == null) {
                throw new ProjectNotFoundException(id);
            }
            return updatedProject;
        });
    }

    public void deleteProject(String projectId) {
        rwLock.writeLock().lock();
        try {
            if (projects.remove(projectId) == null) {
                throw new ProjectNotFoundException(projectId);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void validateProjectData(Project project){
        if (project == null){
            throw new InvalidProjectDataException("Error: Project data cannot be null!");
        }
        ValidationUtils.requireNonEmpty(project.getProjectId(), "Project ID");
        ValidationUtils.requireNonEmpty(project.getProjectName(), "Project Name");
        ValidationUtils.requireNonEmpty(project.getStatus(), "Project Status");
        ValidationUtils.requireNonEmpty(project.getDescription(), "Description");
        if(project.getTeamSize() <= 0){
            throw new InvalidProjectDataException("Error: Team size must be positive!");
        }
        if (project.getBudget() <= 0){
            throw new InvalidProjectDataException("Error: Budget cannot be negative!");
        }
    }

    public Project[] getAllProjects() {
        List<Project> list = new ArrayList<>(projects.values());
        return list.toArray(new Project[0]);
    }

    public Project[] getProjectsByStatus(String status) {
        return projects.values().stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(status))
                .toArray(Project[]::new);
    }

    public Project[] getProjectsByType(String type) {
        return projects.values().stream()
                .filter(p -> p.getProjectType().equalsIgnoreCase(type))
                .toArray(Project[]::new);
    }

    public int getProjectCount() { return projects.size(); }

    public double getAverageCompletion() {
        return projects.values().stream()
                .mapToDouble(Project::calculateCompletionPercentage)
                .average()
                .orElse(0.0);
    }

    /**
     * Deprecated: presentation should be handled by the UI layer. Use getAllProjects() instead.
     */
    @Deprecated
    public void displayAllProjects() {
        throw new UnsupportedOperationException("Use getAllProjects() and present data in the UI layer.");
    }
}
