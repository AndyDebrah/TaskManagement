package com.example.utils;
import com.example.models.User;

public class SessionManager {

    private User currentUser;

    public void login(User user){
        this.currentUser = user;
    }

    public User getCurrentUser(){
        return this.currentUser;
    }

    public void logout(){
        this.currentUser = null;
    }

    public boolean isLoggedIn(){
        return this.currentUser != null;
    }

    public void requirePermission(String permission) {
        if (this.currentUser == null) {
            throw new SecurityException("No active session");
        }
        if (!this.currentUser.hasPermission(permission)) {
            throw new SecurityException("Insufficient permissions");
        }
    }

}
