package com.example.models;

/** Regular user with standard permissions. */
public class RegularUser extends User {
    private final int tasksAssigned;
    private final int tasksCompleted;

    public RegularUser(String userId, String userName, String email, String password) {
        super(userId, userName, email, password);
        this.role = "Regular User";
        this.tasksAssigned = 0;
        this.tasksCompleted = 0;
    }

//    public int getTasksAssigned() { return tasksAssigned; }
//    public void setTasksAssigned(int tasksAssigned) { this.tasksAssigned = tasksAssigned; }
//    public int getTasksCompleted() { return tasksCompleted; }
//    public void setTasksCompleted(int tasksCompleted) { this.tasksCompleted = tasksCompleted; }
//
//    public void assignTask() { tasksAssigned++; }
//    public void completeTask() { tasksCompleted++; }

    public double getProductivityRate() {
        if (tasksAssigned == 0) return 0.0;
        return (tasksCompleted * 100.0) / tasksAssigned;
    }

    @Override
    public String[] getPermissions() {
        return new String[]{"VIEW_PROJECTS", "VIEW_TASKS", "UPDATE_OWN_TASKS", "VIEW_REPORTS"};
    }

    @Override
    public boolean hasPermission(String action) {
        for (String permission : getPermissions()) {
            if (permission.equalsIgnoreCase(action)) return true;
        }
        return false;
    }

    @Override
    public void displayUserInfo() {
        super.displayUserInfo();
        System.out.println("Regular User Statistics:");
        System.out.printf("  Tasks Assigned  : %d%n", tasksAssigned);
        System.out.printf("  Tasks Completed : %d%n", tasksCompleted);
        System.out.printf("  Productivity    : %.2f%%%n%n", getProductivityRate());
    }
}