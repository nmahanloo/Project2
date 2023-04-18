package com.example.project2;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.example.project2.db.AppDatabase;
/**
 * Author: Nima Mahanloo
 * Title: Shop Master: User Class
 * Date: April 10, 2023
 * It is one of the main classes of my CST 338 Project 2,
 * which is in use for the USER_TABLE in the database.
 */
@Entity(tableName = AppDatabase.USER_TABLE)
public class User {
    @PrimaryKey(autoGenerate = true)
    private int userId;
    private String userName;
    private String password;
    private boolean admin;
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.admin = false;
    }
    @Ignore
    public User(String userName, String password, boolean admin) {
        this.userName = userName;
        this.password = password;
        this.admin = admin;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isAdmin() {
        return admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    @NonNull
    @Override
    public String toString() {
        return ("User ID: " + userId + "\n" +
                "Username: " + userName + "\n" +
                "Password: " + password + "\n" +
                "Administrator: " + admin);
    }
}
