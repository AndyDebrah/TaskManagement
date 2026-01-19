package com.example.services;

import com.example.models.Project;
import com.example.exceptions.InvalidProjectDataException;
import com.example.exceptions.ProjectNotFoundException;
import com.example.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Service class for managing project operations (in-memory). */
public class ProjectService {

  // Phase 1: replace array storage with HashMap catalog
  private final Map<String, Project> projects = new HashMap<>();
  private static final int MAX_PROJECTS = 100;

  public ProjectService(Project[] seededProjects) {
    if (seededProjects != null) {
      for (Project project : seededProjects) {
        if (project == null)
          continue;

        if (projects.size() >= MAX_PROJECTS) {
          throw new InvalidProjectDataException("Error: Maximum project limit reached!");
        }

        // ðŸš§ Safety guard: only allow real project IDs (PRJâ€¦)
        String id = project.getProjectId();
        if (id == null || !id.startsWith("PRJ")) {
          throw new InvalidProjectDataException(
              "Error: Invalid project ID (must start with 'PRJ'): " + id);
        }

        if (projects.containsKey(id)) {
          throw new InvalidProjectDataException("Error: Project ID already exists! (" + id + ")");
        }

        // Run same validations as interactive add
        ValidationUtils.requireValidProjectId(id);
        validateProjectData(project);

        projects.put(id, project);
      }
    }
  }

  public synchronized void addProject(Project project) {
    if (projects.size() >= MAX_PROJECTS) {
      throw new InvalidProjectDataException("Error: Maximum project limit reached!");
    }
    if (project == null) {
      throw new InvalidProjectDataException("Error: Project data cannot be null!");
    }

    // ðŸš§ Safety guard: only allow real project IDs (PRJâ€¦)
    if (project.getProjectId() == null || !project.getProjectId().startsWith("PRJ")) {
      throw new InvalidProjectDataException(
          "Error: Invalid project ID (must start with 'PRJ'): " + project.getProjectId());
    }

    if (projects.containsKey(project.getProjectId())) {
      throw new InvalidProjectDataException("Error: Project ID already exists!");
    }

    ValidationUtils.requireValidProjectId(project.getProjectId());
    validateProjectData(project);

    projects.put(project.getProjectId(), project);
  }

  public Project findProjectById(String projectId) {
    return projects.get(projectId);
  }

  public synchronized void updateProject(String projectId, Project updatedProject) {
    if (!projects.containsKey(projectId)) {
      throw new ProjectNotFoundException(projectId);
    }

    // Keep original invariants
    ValidationUtils.requireValidProjectId(updatedProject.getProjectId());
    validateProjectData(updatedProject);

    // NOTE: if callers ever change the ID, you could guard against that here:
    // if (!projectId.equals(updatedProject.getProjectId())) { ... } // optional
    // rule

    projects.put(projectId, updatedProject);
  }

  public synchronized void deleteProject(String projectId) {
    if (projects.remove(projectId) == null) {
      throw new ProjectNotFoundException(projectId);
    }
  }

  private void validateProjectData(Project project) {
    if (project == null) {
      throw new InvalidProjectDataException("Error: Project data cannot be null!");
    }
    ValidationUtils.requireNonEmpty(project.getProjectId(), "Project ID");
    ValidationUtils.requireNonEmpty(project.getProjectName(), "Project Name");
    ValidationUtils.requireNonEmpty(project.getStatus(), "Project Status");
    ValidationUtils.requireNonEmpty(project.getDescription(), "Description");

    if (project.getTeamSize() <= 0) {
      throw new InvalidProjectDataException("Error: Team size must be positive!");
    }
    if (project.getBudget() <= 0) {
      throw new InvalidProjectDataException("Error: Budget cannot be negative!");
    }
  }

  // -------- Backwards-compatible return types (arrays) --------

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

  public int getProjectCount() {
    return projects.size();
  }

  public double getAverageCompletion() {
    return projects.values().stream()
        .mapToDouble(Project::calculateCompletionPercentage)
        .average()
        .orElse(0.0);
  }

  /**
   * Deprecated: presentation should be handled by the UI layer. Use
   * getAllProjects() instead.
   */
  @Deprecated
  public void displayAllProjects() {
    throw new UnsupportedOperationException("Use getAllProjects() and present data in the UI layer.");
  }
}
