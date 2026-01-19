package com.example.models;

/** Administrator user with full system permissions. */
public class AdminUser extends User {
    private final int projectsCreated;
    private final int usersManaged;

    public AdminUser(String userId, String userName, String email, String password) {
        super(userId, userName, email, password);
        this.role = "Administrator";
        this.projectsCreated = 0;
        this.usersManaged = 0;
    }

//    public int getProjectsCreated() { return projectsCreated; }
//    public void incrementProjectsCreated() { this.projectsCreated++; }
//    public int getUsersManaged() { return usersManaged; }
//    public void setUsersManaged(int usersManaged) { this.usersManaged = usersManaged; }

    @Override
    public String[] getPermissions() {
        return new String[]{
                "CREATE_PROJECTS","UPDATE_PROJECTS","DELETE_PROJECTS","VIEW_PROJECTS",
                "CREATE_TASKS","UPDATE_TASKS","DELETE_TASKS","VIEW_TASKS",
                "CREATE_USERS","UPDATE_USERS","DELETE_USERS","VIEW_USERS",
                "GENERATE_REPORTS","VIEW_REPORTS","SYSTEM_ADMIN"
        };
    }

    @Override
    public boolean hasPermission(String action) {
        return true; // Admins have all permissions
    }

    @Override
    public void displayUserInfo() {
        super.displayUserInfo();
        System.out.println("Administrator Statistics:");
        System.out.printf("  Projects Created : %d%n", projectsCreated);
        System.out.printf("  Users Managed    : %d%n", usersManaged);
        System.out.println("  Access Level     : FULL SYSTEM ACCESS\n");
    }
}
