package models;

/**
 * Regular user with standard permissions
 * Demonstrates inheritance and role-based access control
 *
 * Regular users can view and work on assigned tasks but have
 * limited administrative capabilities
 */
public class RegularUser extends User {
    // Additional fields for regular users
    private int tasksAssigned;
    private int tasksCompleted;

    /**
     * Constructor for Regular User
     */
    public RegularUser(String userId, String userName, String email, String password) {
        super(userId, userName, email, password);
        this.role = "Regular User"; // Set specific role
        this.tasksAssigned = 0;
        this.tasksCompleted = 0;
    }

    public int getTasksAssigned() {
        return tasksAssigned;
    }

    public void setTasksAssigned(int tasksAssigned) {
        this.tasksAssigned = tasksAssigned;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    /**
     * Increment tasks assigned counter
     */
    public void assignTask() {
        tasksAssigned++;
    }

    /**
     * Increment tasks completed counter
     */
    public void completeTask() {
        tasksCompleted++;
    }

    /**
     * Calculate user productivity
     */
    public double getProductivityRate() {
        if (tasksAssigned == 0) {
            return 0.0;
        }
        return (tasksCompleted * 100.0) / tasksAssigned;
    }

    /**
     * Implementation of abstract method
     * Regular users have limited permissions
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
                "VIEW_PROJECTS",
                "VIEW_TASKS",
                "UPDATE_OWN_TASKS",
                "VIEW_REPORTS"
        };
    }

    /**
     * Check if regular user has specific permission
     * Demonstrates polymorphism - same method, different implementation
     */
    @Override
    public boolean hasPermission(String action) {
        String[] permissions = getPermissions();
        for (String permission : permissions) {
            if (permission.equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Override to add regular user specific information
     */
    @Override
    public void displayUserInfo() {
        super.displayUserInfo();
        System.out.println("Regular User Statistics:");
        System.out.printf("  Tasks Assigned  : %d%n", tasksAssigned);
        System.out.printf("  Tasks Completed : %d%n", tasksCompleted);
        System.out.printf("  Productivity    : %.2f%%%n", getProductivityRate());
        System.out.println();
    }
}