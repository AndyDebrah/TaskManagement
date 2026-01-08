package main.java.com.example.models;


/** Hardware development project implementation. */
   public class HardwareProject extends Project {
    private final String hardwareType;
    private final int totalComponents;
    private final int assembledComponents;
    private final boolean prototypeCompleted;

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


        public int getTotalComponents() { return totalComponents; }

        public int getAssembledComponents() { return assembledComponents; }
        public void setAssembledComponents(int assembledComponents) {
        }


       public boolean isPrototypeCompleted() { return prototypeCompleted; }
       public void setPrototypeCompleted(boolean prototypeCompleted) {
       }


    @Override
    public double calculateCompletionPercentage() {
        return 0;
    }

    @Override
    public String getProjectType() {
        return "";
    }

    @Override
    public String getProjectDetails() {
        return "";
    }

}
