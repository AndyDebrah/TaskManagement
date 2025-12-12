package services;

import models.Project;


/** Service class for managing project operations (in-memory). */
public class ProjectServices {
    private Project[] projects;
    private int projectCount;
    private static final int MAX_PROJECTS = 100;

    public ProjectServices() {
        this.projects = new Project[MAX_PROJECTS];
        this.projectCount = 0;
    }

    public  ProjectServices(Project[] seededProjects) {
        this.projects = new Project[MAX_PROJECTS];
        this.projectCount = 0;
        for (Project project : seededProjects) {
            if (project != null) {
                this.projects[projectCount++] = project;
            }
        }
    }

    public boolean addProject(Project project) {
        if (projectCount >= MAX_PROJECTS) {
            System.out.println("Error: Maximum project limit reached!");
            return false;
        }
        if (findProjectById(project.getProjectId()) != null) {
            System.out.println("Error: Project ID already exists!");
            return false;
        }
        projects[projectCount++] = project;
        System.out.println("Project added successfully.");
        return true;
    }

    public Project findProjectById(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) return projects[i];
        }
        return null;
    }

    public boolean updateProject(String projectId, Project updatedProject) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                projects[i] = updatedProject;
                System.out.println("Project updated successfully.");
                return true;
            }
        }
        System.out.println("Error: Project not found!");
        return false;
    }

    public boolean deleteProject(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                for (int j = i; j < projectCount - 1; j++) projects[j] = projects[j + 1];
                projects[projectCount - 1] = null;
                projectCount--;
                System.out.println("Project deleted successfully.");
                return true;
            }
        }
        System.out.println("Error: Project not found!");
        return false;
    }

    public Project[] getAllProjects() {
        Project[] result = new Project[projectCount];
        for (int i = 0; i < projectCount; i++) result[i] = projects[i];
        return result;
    }

    public Project[] getProjectsByStatus(String status) {
        int count = 0;
        for (int i = 0; i < projectCount; i++) if (projects[i].getStatus().equalsIgnoreCase(status)) count++;
        Project[] result = new Project[count];
        int index = 0;
        for (int i = 0; i < projectCount; i++) if (projects[i].getStatus().equalsIgnoreCase(status)) result[index++] = projects[i];
        return result;
    }

    public Project[] getProjectsByType(String type) {
        int count = 0;
        for (int i = 0; i < projectCount; i++) if (projects[i].getProjectType().equalsIgnoreCase(type)) count++;
        Project[] result = new Project[count];
        int index = 0;
        for (int i = 0; i < projectCount; i++) if (projects[i].getProjectType().equalsIgnoreCase(type)) result[index++] = projects[i];
        return result;
    }

    public void displayAllProjects() {
        if (projectCount == 0) {
            System.out.println("No projects available.");
            return;
        }
        System.out.println("PROJECT CATALOG");
        for (int i = 0; i < projectCount; i++) {
            System.out.printf("\n[%d] ", i + 1);
            projects[i].displayProjectInfo();
        }
        System.out.println("Total Projects: " + projectCount);
    }

    public int getProjectCount() { return projectCount; }

    public double getAverageCompletion() {
        if (projectCount == 0) return 0.0;
        double total = 0.0;
        for (int i = 0; i < projectCount; i++) total += projects[i].calculateCompletionPercentage();
        return total / projectCount;
    }
}