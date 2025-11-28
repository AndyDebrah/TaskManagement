package models;

/**
 * Concrete implementation of Project for Hardware projects
 * Demonstrates how different subclasses can have different attributes
 * and calculation logic (Polymorphism)
 */
public class HardwareProject extends Project {
    // Hardware-specific attributes
    private String hardwareType; // "Electronics", "Mechanical", "Embedded", etc.
    private int totalComponents;
    private int assembledComponents;
    private boolean prototypeCompleted;

    /**
     * Constructor for Hardware Project
     */
    public HardwareProject(String projectId, String projectName, String description,
                           String startDate, String endDate, double budget, int teamSize,
                           String hardwareType, int totalComponents) {
        super(projectId, projectName, description, startDate, endDate, budget, teamSize);
        this.hardwareType = hardwareType;
        this.totalComponents = totalComponents;
        this.assembledComponents = 0;
        this.prototypeCompleted = false;
    }

    // Getters and Setters
    public String getHardwareType() {
        return hardwareType;
    }

    public void setHardwareType(String hardwareType) {
        this.hardwareType = hardwareType;
    }

    public int getTotalComponents() {
        return totalComponents;
    }

    public void setTotalComponents(int totalComponents) {
        this.totalComponents = totalComponents;
    }

    public int getAssembledComponents() {
        return assembledComponents;
    }

    public void setAssembledComponents(int assembledComponents) {
        this.assembledComponents = assembledComponents;
    }

    public boolean isPrototypeCompleted() {
        return prototypeCompleted;
    }

    public void setPrototypeCompleted(boolean prototypeCompleted) {
        this.prototypeCompleted = prototypeCompleted;
    }

    /**
     * Different calculation logic for hardware projects
     * Takes into account both component assembly and prototype status
     * Demonstrates polymorphism - same method name, different implementation
     */
    @Override
    public double calculateCompletionPercentage() {
        if (totalComponents == 0) {
            return 0.0;
        }
        // Component assembly accounts for 80% of completion
        double componentProgress = (assembledComponents * 80.0) / totalComponents;
        // Prototype completion adds 20%
        double prototypeProgress = prototypeCompleted ? 20.0 : 0.0;

        return componentProgress + prototypeProgress;
    }

    @Override
    public String getProjectType() {
        return "Hardware Development";
    }

    @Override
    public String getProjectDetails() {
        return String.format("Hardware Details: Type=%s | Components=%d/%d assembled | Prototype=%s",
                hardwareType, assembledComponents, totalComponents, prototypeCompleted ? "Completed" : "In Progress");
    }

    /**
     * Method to assemble a component
     */
    public boolean assembleComponent() {
        if (assembledComponents < totalComponents) {
            assembledComponents++;
            // Auto-complete if all components assembled and prototype done
            if (assembledComponents == totalComponents && prototypeCompleted) {
                setStatus("Completed");
            }
            return true;
        }
        return false;
    }

    /**
     * Method to mark prototype as completed
     */
    public void completePrototype() {
        this.prototypeCompleted = true;
        // Auto-complete if all components also assembled
        if (assembledComponents == totalComponents) {
            setStatus("Completed");
        }
    }

    @Override
    public void displayProjectInfo() {
        super.displayProjectInfo();
        System.out.println();
    }
}