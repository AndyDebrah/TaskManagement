package models;

/**
 * Concrete implementation of Project for Software Development projects
 * Demonstrates Inheritance and Polymorphism
 *
 * Software projects have specific attributes like technology stack
 * and development methodology
 */
public class SoftwareProject extends Project {
    // Additional fields specific to software projects
    private String technologyStack;
    private String methodology; // Agile, Waterfall, etc.
    private int totalFeatures;
    private int completedFeatures;

    /**
     * Constructor for Software Project
     * Calls parent constructor using super()
     */
    public SoftwareProject(String projectId, String projectName, String description,
                           String startDate, String endDate, double budget, int teamSize,
                           String technologyStack, String methodology, int totalFeatures) {
        // Call parent constructor
        super(projectId, projectName, description, startDate, endDate, budget, teamSize);
        this.technologyStack = technologyStack;
        this.methodology = methodology;
        this.totalFeatures = totalFeatures;
        this.completedFeatures = 0; // Initially no features completed
    }

    // Getters and Setters for software-specific fields
    public String getTechnologyStack() {
        return technologyStack;
    }

    public void setTechnologyStack(String technologyStack) {
        this.technologyStack = technologyStack;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public int getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(int totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    public int getCompletedFeatures() {
        return completedFeatures;
    }

    public void setCompletedFeatures(int completedFeatures) {
        this.completedFeatures = completedFeatures;
    }

    /**
     * Polymorphism: Override abstract method from parent
     * Calculates completion based on features completed
     */
    @Override
    public double calculateCompletionPercentage() {
        if (totalFeatures == 0) {
            return 0.0;
        }
        return (completedFeatures * 100.0) / totalFeatures;
    }

    /**
     * Implementation of abstract method to identify project type
     */
    @Override
    public String getProjectType() {
        return "Software Development";
    }

    @Override
    public String getProjectDetails() {
        return String.format("Software Details: Tech=%s | Method=%s | Features=%d/%d completed",
                technologyStack, methodology, completedFeatures, totalFeatures);
    }

    /**
     * Method to mark a feature as completed
     * Includes validation to prevent exceeding total features
     */
    public boolean completeFeature() {
        if (completedFeatures < totalFeatures) {
            completedFeatures++;
            // Auto-update status if all features completed
            if (completedFeatures == totalFeatures) {
                setStatus("Completed");
            }
            return true;
        }
        return false;
    }

    /**
     * Override parent method to add software-specific information
     * Demonstrates method overriding
     */
    @Override
    public void displayProjectInfo() {
        super.displayProjectInfo(); // Call parent display
        System.out.println();
    }
}