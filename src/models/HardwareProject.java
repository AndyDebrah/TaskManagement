package models;

/** Hardware development project implementation. */
public class HardwareProject extends Project {
    private String hardwareType;
    private int totalComponents;
    private int assembledComponents;
    private boolean prototypeCompleted;

    public HardwareProject(String projectName, String description,
                           String startDate, String endDate, double budget, int teamSize,
                           String hardwareType, int totalComponents) {
        super( projectName, description, startDate, endDate, budget, teamSize);
        this.hardwareType = hardwareType;
        this.totalComponents = totalComponents;
        this.assembledComponents = 0;
        this.prototypeCompleted = false;
    }

    public String getHardwareType() { return hardwareType; }
    public void setHardwareType(String hardwareType) { this.hardwareType = hardwareType; }
    public int getTotalComponents() { return totalComponents; }
    public void setTotalComponents(int totalComponents) { this.totalComponents = totalComponents; }
    public int getAssembledComponents() { return assembledComponents; }
    public void setAssembledComponents(int assembledComponents) { this.assembledComponents = assembledComponents; }
    public boolean isPrototypeCompleted() { return prototypeCompleted; }
    public void setPrototypeCompleted(boolean prototypeCompleted) { this.prototypeCompleted = prototypeCompleted; }

    @Override
    public double calculateCompletionPercentage() {
        if (totalComponents == 0) return 0.0;
        double componentProgress = (assembledComponents * 80.0) / totalComponents;
        double prototypeProgress = prototypeCompleted ? 20.0 : 0.0;
        return componentProgress + prototypeProgress;
    }

    @Override
    public String getProjectType() { return "Hardware Development"; }

    @Override
    public String getProjectDetails() {
        return String.format("Hardware Details: Type=%s | Components=%d/%d assembled | Prototype=%s",
                hardwareType, assembledComponents, totalComponents, prototypeCompleted ? "Completed" : "In Progress");
    }

    public boolean assembleComponent() {
        if (assembledComponents < totalComponents) {
            assembledComponents++;
            if (assembledComponents == totalComponents && prototypeCompleted) setStatus("Completed");
            return true;
        }
        return false;
    }

    public void completePrototype() {
        this.prototypeCompleted = true;
        if (assembledComponents == totalComponents) setStatus("Completed");
    }
}