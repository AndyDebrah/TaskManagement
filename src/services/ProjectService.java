package services;


import models.Project;
import exceptions.InvalidProjectDataException;
import exceptions.ProjectNotFoundException;
import utils.ValidationUtils;






/** Service class for managing project operations (in-memory). */
public class ProjectService {
    private Project[] projects;
    private int projectCount;
    private static final int MAX_PROJECTS = 100;

    public ProjectService() {
        this.projects = new Project[MAX_PROJECTS];
        this.projectCount = 0;
    }

    public ProjectService(Project[] seededProjects) {
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
            throw new InvalidProjectDataException("Error: Maximum project limit reached!");
        }
        if (findProjectById(project.getProjectId()) != null) {
            throw new InvalidProjectDataException("Error: Project ID already exists!");

        }
        validateProjectData(project);
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
                validateProjectData(updatedProject);
                projects[i] = updatedProject;
                System.out.println("Project updated successfully.");
                return true;
            }
        }
       throw new ProjectNotFoundException(projectId);
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
       throw new ProjectNotFoundException(projectId);
    }

    private void validateProjectData(Project project){
        if (project == null){
            throw new InvalidProjectDataException("Error: Project data cannot be null!");
        }
        ValidationUtils.requireNonEmpty(project.getProjectId(), "Project ID");
        ValidationUtils.requireNonEmpty(project.getProjectName(), "Project Name");
        ValidationUtils.requireNonEmpty(project.getStatus(), "Project Status");
        ValidationUtils.requireNonEmpty(project.getProjectId(), "Description");
        if(project.getTeamSize() <=1){
            throw new InvalidProjectDataException("Error: Team size must be positive!");

        }
        if (project.getBudget() <1){
            throw new InvalidProjectDataException("Error: Budget cannot be negative!");
        }



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