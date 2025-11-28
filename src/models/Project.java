package models;

/**
 * Abstract base class for all project types
 * Demonstrates OOP principles: Abstraction and Encapsulation
 *
 * This class provides common functionality for all project types
 * and enforces a contract for calculating completion percentage
 */
public abstract class Project {
    // Encapsulation: Private fields with controlled access
    private String projectId;
    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String status; // "Active", "Completed", "On Hold"

    // New fields required by user stories
    private double budget;
    private int teamSize;

    // Per-project task storage
    private models.Task[] tasks;
    private int taskCount;
    private static final int MAX_TASKS_PER_PROJECT = 200;

    /**
     * Constructor to initialize common project attributes
     * @param projectId Unique identifier for the project
     * @param projectName Name of the project
     * @param description Brief description
     * @param startDate Project start date (format: YYYY-MM-DD)
     * @param endDate Project end date (format: YYYY-MM-DD)
     * @param budget Project budget
     * @param teamSize Project team size
     */
    public Project(String projectId, String projectName, String description,
                   String startDate, String endDate, double budget, int teamSize) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "Active"; // Default status
        this.budget = budget;
        this.teamSize = teamSize;

        // Initialize task storage
        this.tasks = new Task[MAX_TASKS_PER_PROJECT];
        this.taskCount = 0;
    }

    // Getters and Setters for encapsulation
    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    //the getters enabled us to access the private class
    // The setters also enabled us to initialize the value of the projectId class

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    /**
     * Abstract method - forces subclasses to implement their own logic
     * This demonstrates polymorphism as different project types
     * may calculate completion differently
     *
     * @return Completion percentage (0-100)
     */
    public abstract double calculateCompletionPercentage();

    /**
     * Abstract method to get project type
     * Allows identification of concrete project types
     *
     * @return String representing the project type
     */
    public abstract String getProjectType();

    /**
     * Abstract method to get project-specific details
     * Implemented by subclasses to include their own fields
     */
    public abstract String getProjectDetails();

    /**
     * Common method to display project information
     * Can be overridden by subclasses for specific formatting
     */
    public void displayProjectInfo() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.printf("║ Project ID      : %-40s ║%n", projectId);
        System.out.printf("║ Project Name    : %-40s ║%n", projectName);
        System.out.printf("║ Type            : %-40s ║%n", getProjectType());
        System.out.printf("║ Description     : %-40s ║%n", description);
        System.out.printf("║ Start Date      : %-40s ║%n", startDate);
        System.out.printf("║ End Date        : %-40s ║%n", endDate);
        System.out.printf("║ Team Size       : %-40d ║%n", teamSize);
        System.out.printf("║ Budget          : $%-39.2f ║%n", budget);
        System.out.printf("║ Status          : %-40s ║%n", status);
        System.out.printf("║ Completion      : %-38.2f%% ║%n", calculateCompletionPercentage());
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        // Print additional subclass-specific details
        System.out.println(getProjectDetails());
    }

    /**
     * Override toString for easy debugging and logging
     */
    @Override
    public String toString() {
        return String.format("Project[ID=%s, Name=%s, Type=%s, Status=%s, Completion=%.2f%%]",
                projectId, projectName, getProjectType(), status, calculateCompletionPercentage());
    }

    /**
     * Add a task to this project
     * @param task Task to add
     * @return true if added successfully
     */
    public boolean addTask(Task task) {
        if (taskCount >= MAX_TASKS_PER_PROJECT) {
            return false;
        }
        // Prevent duplicates
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(task.getTaskId())) {
                return false;
            }
        }
        tasks[taskCount++] = task;
        return true;
    }

    /**
     * Remove a task by ID from this project
     * @param taskId ID of the task to remove
     * @return true if removed
     */
    public boolean removeTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                for (int j = i; j < taskCount - 1; j++) {
                    tasks[j] = tasks[j + 1];
                }
                tasks[taskCount - 1] = null;
                taskCount--;
                return true;
            }
        }
        return false;
    }

    /**
     * Get tasks belonging to this project
     * @return array of tasks (copy)
     */
    public Task[] getTasks() {
        Task[] result = new Task[taskCount];
        for (int i = 0; i < taskCount; i++) {
            result[i] = tasks[i];
        }
        return result;
    }

    /**
     * Get number of tasks in this project
     */
    public int getTaskCount() {
        return taskCount;
    }
}