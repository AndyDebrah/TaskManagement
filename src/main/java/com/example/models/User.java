package main.java.com.example.models;

/** Abstract base class for user types. */
public abstract class User {
    protected String userId;
    protected String userName;
    protected String email;
    protected String role;
    protected String password;

    public User(String userId, String userName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public abstract String[] getPermissions();
    public abstract boolean hasPermission(String action);

    public boolean verifyPassword(String inputPassword) { return this.password.equals(inputPassword); }

    public void displayUserInfo() {
        System.out.printf("User ID: %s%n", userId);
        System.out.printf("Name   : %s%n", userName);
        System.out.printf("Email  : %s%n", email);
        System.out.printf("Role   : %s%n", role);
    }

    @Override
    public String toString() {
        return String.format("User[ID=%s, Name=%s, Role=%s]", userId, userName, role);
    }
}