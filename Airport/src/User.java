import java.util.*;
import java.time.*;

// Abstract User class
abstract class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String contactInfo;

    public User(int userId, String username, String password, String name, String email, String contactInfo) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
    }


    public void logout() {
        System.out.println("User " + username + " logged out successfully.");
    }

    public void updateProfile(String name, String email, String contactInfo) {
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
        System.out.println("Profile updated successfully.");
    }
    public boolean login(String enteredUsername, String enteredPassword) {
        System.out.println("Checking: " + username + " vs " + enteredUsername + " and " + password + " vs " + enteredPassword);
        return this.username.equals(enteredUsername) && this.password.equals(enteredPassword);
    }


    public abstract String getRole();
    public abstract void displayDashboard();
    public abstract void handleBookingOperation(Scanner scanner, BookingSystem system);

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactInfo() { return contactInfo; }
    protected String getPassword() { return password; }
}