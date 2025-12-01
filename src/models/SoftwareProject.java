package models;

/** Software development project implementation. */
public class SoftwareProject extends Project {
    private String technologyStack;
    private String methodology;
    private int totalFeatures;
    private int completedFeatures;

    public SoftwareProject(String projectId, String projectName, String description,
                           String startDate, String endDate, double budget, int teamSize,
                           String technologyStack, String methodology, int totalFeatures) {
        super(projectId, projectName, description, startDate, endDate, budget, teamSize);
        this.technologyStack = technologyStack;
        this.methodology = methodology;
        this.totalFeatures = totalFeatures;
        this.completedFeatures = 0;
    }

    public String getTechnologyStack() { return technologyStack; }
    public void setTechnologyStack(String technologyStack) { this.technologyStack = technologyStack; }
    public String getMethodology() { return methodology; }
    public void setMethodology(String methodology) { this.methodology = methodology; }
    public int getTotalFeatures() { return totalFeatures; }
    public void setTotalFeatures(int totalFeatures) { this.totalFeatures = totalFeatures; }
    public int getCompletedFeatures() { return completedFeatures; }
    public void setCompletedFeatures(int completedFeatures) { this.completedFeatures = completedFeatures; }

    @Override
    public double calculateCompletionPercentage() {
        if (totalFeatures == 0) return 0.0;
        return (completedFeatures * 100.0) / totalFeatures;
    }

    @Override
    public String getProjectType() { return "Software Development"; }

    @Override
    public String getProjectDetails() {
        return String.format("Software Details: Tech=%s | Method=%s | Features=%d/%d completed",
                technologyStack, methodology, completedFeatures, totalFeatures);
    }

    public boolean completeFeature() {
        if (completedFeatures < totalFeatures) {
            completedFeatures++;
            if (completedFeatures == totalFeatures) setStatus("Completed");
            return true;
        }
        return false;
    }
}