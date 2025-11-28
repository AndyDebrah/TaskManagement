package models;

/**
 * Abstract base class for all user types
 * Demonstrates abstraction and role-based user management
 *
 * Different user types have different permissions and capabilities
 */
public abstract class User {
    // Protected fields - accessible by subclasses
    protected String userId;
    protected String userName;
    protected String email;
    protected String role; // "Admin", "Regular", etc.
    protected String password;

    /**
     * Constructor for User
     */
    public User(String userId, String userName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Abstract method to get user permissions
     * Different user types have different permission levels
     */
    public abstract String[] getPermissions();

    /**
     * Abstract method to check if user can perform an action
     * @param action The action to check permission for
     * @return true if user has permission
     */
    public abstract boolean hasPermission(String action);

    /**
     * Method to verify password
     * In a real system, this would use hashing
     */
    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * Display user information
     */
    public void displayUserInfo() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.printf("║ User ID    : %-45s ║%n", userId);
        System.out.printf("║ Name       : %-45s ║%n", userName);
        System.out.printf("║ Email      : %-45s ║%n", email);
        System.out.printf("║ Role       : %-45s ║%n", role);
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, Role=%s]", userId, userName, role);
    }
}