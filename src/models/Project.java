package models;

/** Common abstract project class. */
public abstract class Project {
    private String projectId;
    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private double budget;
    private int teamSize;

    private Task[] tasks;
    private int taskCount;
    private static final int MAX_TASKS_PER_PROJECT = 200;

    public Project(String projectId, String projectName, String description,
                   String startDate, String endDate, double budget, int teamSize) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "Active";
        this.budget = budget;
        this.teamSize = teamSize;
        this.tasks = new Task[MAX_TASKS_PER_PROJECT];
        this.taskCount = 0;
    }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public abstract double calculateCompletionPercentage();
    public abstract String getProjectType();
    public abstract String getProjectDetails();

    public void displayProjectInfo() {
        System.out.printf("Project ID   : %s%n", projectId);
        System.out.printf("Name         : %s%n", projectName);
        System.out.printf("Type         : %s%n", getProjectType());
        System.out.printf("Description  : %s%n", description);
        System.out.printf("Start Date   : %s%n", startDate);
        System.out.printf("End Date     : %s%n", endDate);
        System.out.printf("Team Size    : %d%n", teamSize);
        System.out.printf("Budget       : $%.2f%n", budget);
        System.out.printf("Status       : %s%n", status);
        System.out.printf("Completion   : %.2f%%%n", calculateCompletionPercentage());
        System.out.println(getProjectDetails());
    }

    @Override
    public String toString() {
        return String.format("Project[ID=%s, Name=%s, Type=%s, Status=%s, Completion=%.2f%%]",
                projectId, projectName, getProjectType(), status, calculateCompletionPercentage());
    }

    public boolean addTask(Task task) {
        if (taskCount >= MAX_TASKS_PER_PROJECT) return false;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(task.getTaskId())) return false;
        }
        tasks[taskCount++] = task;
        return true;
    }

    public boolean removeTask(String taskId) {
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getTaskId().equals(taskId)) {
                for (int j = i; j < taskCount - 1; j++) tasks[j] = tasks[j + 1];
                tasks[taskCount - 1] = null;
                taskCount--;
                return true;
            }
        }
        return false;
    }

    public Task[] getTasks() {
        Task[] result = new Task[taskCount];
        for (int i = 0; i < taskCount; i++) result[i] = tasks[i];
        return result;
    }

    public int getTaskCount() { return taskCount; }
}