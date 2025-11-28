package models;

/**
 * Administrator user with full system permissions
 * Demonstrates inheritance and elevated privileges
 *
 * Admin users can perform all operations including
 * project/task/user management
 */
public class AdminUser extends User {
    // Additional fields for admin tracking
    private int projectsCreated;
    private int usersManaged;

    /**
     * Constructor for Admin User
     */
    public AdminUser(String userId, String userName, String email, String password) {
        super(userId, userName, email, password);
        this.role = "Administrator"; // Set admin role
        this.projectsCreated = 0;
        this.usersManaged = 0;
    }

    public int getProjectsCreated() {
        return projectsCreated;
    }

    public void incrementProjectsCreated() {
        this.projectsCreated++;
    }

    public int getUsersManaged() {
        return usersManaged;
    }

    public void setUsersManaged(int usersManaged) {
        this.usersManaged = usersManaged;
    }

    /**
     * Implementation of abstract method
     * Admins have full permissions
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
                "CREATE_PROJECTS",
                "UPDATE_PROJECTS",
                "DELETE_PROJECTS",
                "VIEW_PROJECTS",
                "CREATE_TASKS",
                "UPDATE_TASKS",
                "DELETE_TASKS",
                "VIEW_TASKS",
                "CREATE_USERS",
                "UPDATE_USERS",
                "DELETE_USERS",
                "VIEW_USERS",
                "GENERATE_REPORTS",
                "VIEW_REPORTS",
                "SYSTEM_ADMIN"
        };
    }

    /**
     * Admin has permission for all actions
     * Demonstrates polymorphism with different implementation
     */
    @Override
    public boolean hasPermission(String action) {
        // Admins have all permissions
        return true;
    }

    /**
     * Override to add admin-specific information
     */
    @Override
    public void displayUserInfo() {
        super.displayUserInfo();
        System.out.println("Administrator Statistics:");
        System.out.printf("  Projects Created : %d%n", projectsCreated);
        System.out.printf("  Users Managed    : %d%n", usersManaged);
        System.out.println("  Access Level     : FULL SYSTEM ACCESS");
        System.out.println();
    }
}