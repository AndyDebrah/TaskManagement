package services;

import models.*;

/**
 * Service class for managing project operations
 * Demonstrates separation of concerns and business logic layer
 *
 * This class handles all CRUD operations for projects
 * using in-memory arrays (easily migrated to collections later)
 */
public class ProjectServices {
    // In-memory storage using arrays
    private Project[] projects;
    private int projectCount;
    private static final int MAX_PROJECTS = 100;

    /**
     * Constructor initializes the project array
     */
    public ProjectServices() {
        this.projects = new Project[MAX_PROJECTS];
        this.projectCount = 0;
    }

    /**
     * Create and add a new project
     * Demonstrates polymorphism - accepts any Project subtype
     *
     * @param project The project to add
     * @return true if successfully added
     */
    public boolean addProject(Project project) {
        // Validation: Check if array is full
        if (projectCount >= MAX_PROJECTS) {
            System.out.println("Error: Maximum project limit reached!");
            return false;
        }

        // Validation: Check for duplicate project ID
        if (findProjectById(project.getProjectId()) != null) {
            System.out.println("Error: Project ID already exists!");
            return false;
        }

        // Add project to array
        projects[projectCount] = project;
        projectCount++;

        System.out.println("✓ Project added successfully!");
        return true;
    }

    /**
     * Find a project by ID
     *
     * @param projectId The ID to search for
     * @return Project if found, null otherwise
     */
    public Project findProjectById(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                return projects[i];
            }
        }
        return null;
    }

    /**
     * Update an existing project
     *
     * @param projectId The ID of project to update
     * @param updatedProject The new project data
     * @return true if successfully updated
     */
    public boolean updateProject(String projectId, Project updatedProject) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                projects[i] = updatedProject;
                System.out.println("✓ Project updated successfully!");
                return true;
            }
        }
        System.out.println("Error: Project not found!");
        return false;
    }

    /**
     * Delete a project by ID
     * Shifts remaining elements to fill the gap
     *
     * @param projectId The ID of project to delete
     * @return true if successfully deleted
     */
    public boolean deleteProject(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                // Shift elements left to fill the gap
                for (int j = i; j < projectCount - 1; j++) {
                    projects[j] = projects[j + 1];
                }
                projects[projectCount - 1] = null; // Clear last element
                projectCount--;
                System.out.println("✓ Project deleted successfully!");
                return true;
            }
        }
        System.out.println("Error: Project not found!");
        return false;
    }

    /**
     * Get all projects
     * Returns a copy to prevent external modification
     *
     * @return Array of all projects
     */
    public Project[] getAllProjects() {
        Project[] result = new Project[projectCount];
        for (int i = 0; i < projectCount; i++) {
            result[i] = projects[i];
        }
        return result;
    }

    /**
     * Get projects by status
     *
     * @param status The status to filter by
     * @return Array of matching projects
     */
    public Project[] getProjectsByStatus(String status) {
        // First, count matching projects
        int count = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }

        // Create result array and populate
        Project[] result = new Project[count];
        int index = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getStatus().equalsIgnoreCase(status)) {
                result[index++] = projects[i];
            }
        }

        return result;
    }

    /**
     * Get projects by type
     *
     * @param type The project type to filter by
     * @return Array of matching projects
     */
    public Project[] getProjectsByType(String type) {
        int count = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectType().equalsIgnoreCase(type)) {
                count++;
            }
        }

        Project[] result = new Project[count];
        int index = 0;
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectType().equalsIgnoreCase(type)) {
                result[index++] = projects[i];
            }
        }

        return result;
    }

    /**
     * Display all projects in formatted output
     */
    public void displayAllProjects() {
        if (projectCount == 0) {
            System.out.println("\n📋 No projects available.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                     PROJECT CATALOG");
        System.out.println("=".repeat(70));

        for (int i = 0; i < projectCount; i++) {
            System.out.printf("\n[%d] ", i + 1);
            projects[i].displayProjectInfo();
        }

        System.out.println("\nTotal Projects: " + projectCount);
    }

    /**
     * Get current project count
     */
    public int getProjectCount() {
        return projectCount;
    }

    /**
     * Calculate average completion across all projects
     */
    public double getAverageCompletion() {
        if (projectCount == 0) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < projectCount; i++) {
            total += projects[i].calculateCompletionPercentage();
        }

        return total / projectCount;
    }
}