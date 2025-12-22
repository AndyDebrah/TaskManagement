package utils;
import models.User;

public class SessionManager {

    private static User currentUser;

    public  void login(User user){
        currentUser = user;
    }
    public static User getCurrentUser(){
        return currentUser;
    }

    public  void logout(){
        currentUser = null;
    }

    public  boolean isLoggedIn(){
        return currentUser != null;
    }
    // TODO: check it out
    public static void requirePermission(String permission) {
        if (currentUser == null) {
            throw new SecurityException("No active session");
        }
        if (!currentUser.hasPermission(permission)) {
            System.out.println("Error: You do not have permission to perform this action.");
            throw new SecurityException("Insufficient permissions");
        }
    }

}
