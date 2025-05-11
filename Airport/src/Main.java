import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BookingSystem bookingSystem = new BookingSystem();

            // Initialize sample data if needed
            if (!new java.io.File("users.txt").exists()) {
                initializeSampleData(bookingSystem);
                bookingSystem.saveSystemData();
            } else {
                bookingSystem.loadSystemData();
            }

            // Start with role selection screen
            new RoleSelectionScreen(bookingSystem).setVisible(true);
        });
    }

    private static void initializeSampleData(BookingSystem bookingSystem) {
        // Create default admin account
        bookingSystem.getUsers().add(new Administrator(
                1001, "admin", "admin123", "System Admin",
                "admin@flightbookingsystem.com", "555-0100",
                1001, 3));

        // Create sample agent
        bookingSystem.getUsers().add(new Agent(
                2001, "agent", "agent123", "Booking Agent",
                "agent@flightbookingsystem.com", "555-0200",
                2001, "Sales", 0.05));

        // Create sample customer
        bookingSystem.getUsers().add(new Customer(
                3001, "customer", "customer123", "John Traveler",
                "john@example.com", "555-0300",
                3001, "123 Main St", "Window seat preferred"));
    }
}