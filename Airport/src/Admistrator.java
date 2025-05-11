import java.util.*;
import java.time.*;
import java.nio.file.*;

class Administrator extends User {
    private int adminId;
    private int securityLevel;

    public Administrator(int userId, String username, String password, String name, String email,
                         String contactInfo, int adminId, int securityLevel) {
        super(userId, username, password, name, email, contactInfo);
        this.adminId = adminId;
        this.securityLevel = securityLevel;
    }

    public User createUser(String role, String username, String password, String name, String email, String contactInfo, BookingSystem system) {
        if (securityLevel < 2) {
            System.out.println("Insufficient privileges");
            return null;
        }

        int userId = (int)System.currentTimeMillis() % 1000000;
        User newUser = null;

        switch (role.toLowerCase()) {
            case "customer":
                newUser = new Customer(
                        userId, username, password, name, email, contactInfo,
                        userId, "Default Address", "None"
                );
                break;

            case "agent":
                newUser = new Agent(
                        userId, username, password, name, email, contactInfo,
                        userId, "Sales", 0.05
                );
                break;

            case "admin":
                if (securityLevel < 3) {
                    System.out.println("Only level 3 admins can create other admins");
                    return null;
                }
                newUser = new Administrator(
                        userId, username, password, name, email, contactInfo,
                        userId, 1
                );
                break;

            default:
                System.out.println("Invalid role");
                return null;
        }

        system.getUsers().add(newUser);
        system.saveSystemData();
        System.out.println("User created successfully. ID: " + userId);
        return newUser;
    }

    public void modifySystemSettings(String setting, String value) {
        if (securityLevel >= 2) {
            System.out.println("Setting '" + setting + "' updated to '" + value + "'");
        } else {
            System.out.println("Insufficient privileges");
        }
    }

    /*public void viewSystemLogs() {
        try {
            System.out.println("\n=== SYSTEM LOGS ===");
            Files.lines(Paths.get("system.log"))
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Error reading logs: " + e.getMessage());
        }
    }*/

    public void manageUserAccess(int userId, boolean grantAccess, BookingSystem system) {
        if (securityLevel >= 2) {
            system.getUsers().stream()
                    .filter(u -> u.getUserId() == userId)
                    .findFirst()
                    .ifPresentOrElse(
                            user -> System.out.println("User " + userId + " access " + (grantAccess ? "granted" : "revoked")),
                            () -> System.out.println("User not found")
                    );
        } else {
            System.out.println("Insufficient privileges");
        }
    }

    @Override
    public String getRole() {
        return "admin";
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n=== ADMIN DASHBOARD ===");
        System.out.println("Welcome, Admin " + getName());
        System.out.println("Security Level: " + securityLevel);
        System.out.println("1. Create User");
        System.out.println("2. Modify System Settings");
        System.out.println("3. View System Logs");
        System.out.println("4. Manage User Access");
        System.out.println("5. Update Profile");
        System.out.println("6. Logout");
    }

    @Override
    public void handleBookingOperation(Scanner scanner, BookingSystem system) {
        System.out.println("\n=== ADMIN BOOKING OPERATIONS ===");
        System.out.println("1. View All Bookings");
        System.out.println("2. Generate Booking Report");
        System.out.print("Select option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                system.getBookings().forEach(b -> System.out.println(
                        "ID: " + b.getBookingId() +
                                " | Customer: " + b.getCustomer().getName() +
                                " | Flight: " + b.getFlight().getFlightNumber() +
                                " | Status: " + b.getStatus()
                ));
                break;

            case 2:
                System.out.println("\n=== BOOKING REPORT ===");
                long totalBookings = system.getBookings().size();
                long confirmedBookings = system.getBookings().stream()
                        .filter(b -> b.getStatus().equals("Confirmed"))
                        .count();
                double totalRevenue = system.getBookings().stream()
                        .filter(b -> b.getStatus().equals("Confirmed"))
                        .mapToDouble(Booking::calculateTotalPrice)
                        .sum();

                System.out.println("Total Bookings: " + totalBookings);
                System.out.println("Confirmed Bookings: " + confirmedBookings);
                System.out.println("Total Revenue: $" + totalRevenue);
                break;

            default:
                System.out.println("Invalid choice");
        }
    }
}