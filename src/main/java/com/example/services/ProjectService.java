package main.java.com.example.services;


import main.java.com.example.models.Project;
import main.java.com.example.exceptions.InvalidProjectDataException;
import main.java.com.example.exceptions.ProjectNotFoundException;
import main.java.com.example.utils.ValidationUtils;






/** Service class for managing project operations (in-memory). */
public class ProjectService {
    private final Project[] projects;
    private int projectCount;
    private static final int MAX_PROJECTS = 100;

    public ProjectService(Project[] seededProjects) {
        this.projects = new Project[MAX_PROJECTS];
        this.projectCount = 0;
        for (Project project : seededProjects) {
            if (project != null) {
                this.projects[projectCount++] = project;
            }
        }
    }

    public void addProject(Project project) {
        if (projectCount >= MAX_PROJECTS) {
            throw new InvalidProjectDataException("Error: Maximum project limit reached!");
        }
        if (findProjectById(project.getProjectId()) != null) {
            throw new InvalidProjectDataException("Error: Project ID already exists!");

        }
        validateProjectData(project);
        projects[projectCount++] = project;
    }

    public Project findProjectById(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) return projects[i];
        }
        return null;
    }


    public void updateProject(String projectId, Project updatedProject) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                validateProjectData(updatedProject);
                projects[i] = updatedProject;

                return;
            }
        }
       throw new ProjectNotFoundException(projectId);
    }

    public void deleteProject(String projectId) {
        for (int i = 0; i < projectCount; i++) {
            if (projects[i].getProjectId().equals(projectId)) {
                for (int j = i; j < projectCount - 1; j++) projects[j] = projects[j + 1];
                projects[projectCount - 1] = null;
                projectCount--;
                return;
            }
        }
       throw new ProjectNotFoundException(projectId);
    }

    private void validateProjectData(Project project){
        if (project == null){
            throw new InvalidProjectDataException("Error: Project data cannot be null!");
        }
        ValidationUtils.requireNonEmpty(project.getProjectId(), "Project ID");
        ValidationUtils.requireNonEmpty(project.getProjectName(), "Project Name");
        ValidationUtils.requireNonEmpty(project.getStatus(), "Project Status");
        ValidationUtils.requireNonEmpty(project.getDescription(), "Description");
        if(project.getTeamSize() <=0){
            throw new InvalidProjectDataException("Error: Team size must be positive!");

        }
        if (project.getBudget() <=0){
            throw new InvalidProjectDataException("Error: Budget cannot be negative!");
        }
    }

    public Project[] getAllProjects() {
        Project[] result = new Project[projectCount];
        System.arraycopy(projects, 0, result, 0, projectCount);
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

    /**
     * Deprecated: presentation should be handled by the UI layer. Use getAllProjects() instead.
     */
    @Deprecated
    public void displayAllProjects() {
        throw new UnsupportedOperationException("Use getAllProjects() and present data in the UI layer.");
    }

    public int getProjectCount() { return projectCount; }

    public double getAverageCompletion() {
        if (projectCount == 0) return 0.0;
        double total = 0.0;
        for (int i = 0; i < projectCount; i++) total += projects[i].calculateCompletionPercentage();
        return total / projectCount;
    }
}