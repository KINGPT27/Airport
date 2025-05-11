import javax.swing.*;

public class FlightBookingApp {
    private BookingSystem bookingSystem;
    private User currentUser;
    private String currentUserRole;  // Track role separately
    private JFrame currentScreen;

    public FlightBookingApp() {
        this.bookingSystem = new BookingSystem();
        loadInitialData();
    }

    private void loadInitialData() {
        try {
            bookingSystem.loadSystemData();
        } catch (Exception e) {
            bookingSystem.initializeSampleData();
        }
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            showLoginScreen(null); // Start with no previous role
        });
    }

    // Modified to accept previous role
    public void showLoginScreen(String previousRole) {
        if (currentScreen != null) {
            currentScreen.dispose();
        }

        currentScreen = new LoginScreen(this.getBookingSystem(), previousRole);
        currentScreen.setVisible(true);
    }

    // Modified to store role
    public void showDashboard(User user, String role) {
        this.currentUser = user;
        this.currentUserRole = role;

        if (currentScreen != null) {
            currentScreen.dispose();
        }

        currentScreen = new DashboardScreen(this, user);
        currentScreen.setVisible(true);
    }

    public void logout() {
        if (currentUser != null) {
            String roleToPassBack = currentUserRole; // Save role before clearing
            currentUser = null;
            currentUserRole = null;

            if (currentScreen != null) {
                currentScreen.dispose();
            }

            showLoginScreen(roleToPassBack); // Pass role back to login
        }
    }

    public static void main(String[] args) {
        new FlightBookingApp().start();
    }

    // Getters
    public BookingSystem getBookingSystem() { return bookingSystem; }
    public User getCurrentUser() { return currentUser; }
    public String getCurrentUserRole() { return currentUserRole; }
}