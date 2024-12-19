package SOKOBAN.model;

import java.io.Serializable;
import java.util.Scanner;

public class User implements Serializable {
    private final String username;
    private final boolean isGuest;
    private final String password;

    public User(String username, boolean isGuest, String password) {
        this.username = username;
        this.isGuest = isGuest;
        this.password = password;
        //this.password = password;

    }
   /* public User(String username) {
        this.username = username;
        this.isGuest = false;
        String p =
    }*/



    public boolean PasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public String getSaveFileName() {
        return isGuest ? null : "resources/saves/" + username + ".dat";
    }

    @Override
    public String toString() {
        return isGuest ? "Guest" : "User: " + username;
    }
}
