package main.java.com.example.services;

import main.java.com.example.models.HardwareProject;

public class HardwareProjectManager {

    private final HardwareProject project;  // final because manager always manages this project

    public HardwareProjectManager(HardwareProject project) {
        this.project = project;
    }

    /**
     * Calculates the completion percentage of the hardware project.
     * Components count for 80% and prototype completion counts for 20%.
     */
    public double calculateCompletionPercentage() {
        int totalComponents = project.getTotalComponents();
        if (totalComponents == 0) return 0.0;

        int assembledComponents = project.getAssembledComponents();
        boolean prototypeCompleted = project.isPrototypeCompleted();

        double componentProgress = (assembledComponents * 80.0) / totalComponents;
        double prototypeProgress = prototypeCompleted ? 20.0 : 0.0;

        return componentProgress + prototypeProgress;
    }

    /**
     * Returns the type of the project.
     */
    public String getProjectType() {
        return "Hardware Development";
    }

    /**
     * Returns a detailed description of the project.
     */
    public String getProjectDetails() {
        return String.format(
                "Hardware Details: Type=%s | Components=%d/%d assembled | Prototype=%s",
                project.getHardwareType(),
                project.getAssembledComponents(),
                project.getTotalComponents(),
                project.isPrototypeCompleted() ? "Completed" : "In Progress"
        );
    }

    /**
     * Assemble one component of the hardware project.
     * Updates the project's assembled components and status if complete.
     */
    public boolean assembleComponent() {
        int assembledComponents = project.getAssembledComponents();
        int totalComponents = project.getTotalComponents();
        boolean prototypeCompleted = project.isPrototypeCompleted();

        if (assembledComponents < totalComponents) {
            project.setAssembledComponents(assembledComponents + 1);

            // If all components are assembled and prototype is done, mark project completed
            if (project.getAssembledComponents() == totalComponents && prototypeCompleted) {
                project.setStatus("Completed");
            }
            return true;
        }
        return false;
    }

    /**
     * Marks the prototype as completed and updates project status if all components are done.
     */
    public void completePrototype() {
        project.setPrototypeCompleted(true);

        // If all components are assembled, mark project completed
        if (project.getAssembledComponents() == project.getTotalComponents()) {
            project.setStatus("Completed");
        }
    }
}

