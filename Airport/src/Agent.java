import java.util.*;
import java.time.*;

class Agent extends User {
    private int agentId;
    private String department;
    private double commission;

    public Agent(int userId, String username, String password, String name, String email,
                 String contactInfo, int agentId, String department, double commission) {
        super(userId, username, password, name, email, contactInfo);
        this.agentId = agentId;
        this.department = department;
        this.commission = commission;
    }

    public void manageFlights(Flight flight, String action, BookingSystem system) {
        switch (action.toLowerCase()) {
            case "update":
                System.out.print("Enter new departure time: ");
                String depTime = new Scanner(System.in).nextLine();
                System.out.print("Enter new arrival time: ");
                String arrTime = new Scanner(System.in).nextLine();
                flight.updateSchedule(depTime, arrTime);
                system.saveSystemData();
                break;

            case "cancel":
                system.getFlights().remove(flight);
                System.out.println("Flight " + flight.getFlightNumber() + " cancelled");
                system.saveSystemData();
                break;

            default:
                System.out.println("Invalid action");
        }
    }

    public Booking createBookingForCustomer(Customer customer, Flight flight, List<Passenger> passengers, String seatSelection) {
        Booking booking = customer.createBooking(flight, passengers, seatSelection);
        if (booking != null) {
            System.out.println("Booking created for customer " + customer.getName());
            System.out.println("Commission earned: $" + (booking.calculateTotalPrice() * commission));
        }
        return booking;
    }

    public boolean modifyBooking(Booking booking, String newSeatSelection) {
        booking.setSeatSelections(newSeatSelection);
        System.out.println("Booking " + booking.getBookingId() + " modified");
        return true;
    }

    public void generateReports(String reportType, BookingSystem system) {
        switch (reportType.toLowerCase()) {
            case "sales":
                double totalSales = system.getBookings().stream()
                        .filter(b -> b.getStatus().equals("Confirmed"))
                        .mapToDouble(Booking::calculateTotalPrice)
                        .sum();
                System.out.println("Total Sales: $" + totalSales);
                System.out.println("Your Commission: $" + (totalSales * commission));
                break;

            case "flights":
                system.getFlights().forEach(f -> System.out.println(
                        f.getFlightNumber() + " - " + f.getAirline() +
                                " | " + f.getOrigin() + " to " + f.getDestination() +
                                " | Seats: " + f.getAvailableSeats()
                ));
                break;

            default:
                System.out.println("Invalid report type");
        }
    }

    @Override
    public String getRole() {
        return "agent";
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n=== AGENT DASHBOARD ===");
        System.out.println("Welcome, Agent " + getName());
        System.out.println("Department: " + department);
        System.out.println("Commission Rate: " + (commission * 100) + "%");
        System.out.println("1. Manage Flights");
        System.out.println("2. Create Booking for Customer");
        System.out.println("3. Modify Booking");
        System.out.println("4. Generate Reports");
        System.out.println("5. Update Profile");
        System.out.println("6. Logout");
    }

    @Override
    public void handleBookingOperation(Scanner scanner, BookingSystem system) {
        System.out.println("\n=== AGENT BOOKING OPERATIONS ===");
        System.out.println("1. Create Booking for Customer");
        System.out.println("2. Modify Booking");
        System.out.println("3. Generate Report");
        System.out.print("Select option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter customer username: ");
                String username = scanner.nextLine();

                User user = system.getUsers().stream()
                        .filter(u -> u.getUsername().equals(username))
                        .findFirst()
                        .orElse(null);

                if (user instanceof Customer) {
                    Customer customer = (Customer)user;

                    System.out.print("Enter flight number: ");
                    String flightNumber = scanner.nextLine();
                    Flight flight = system.getFlights().stream()
                            .filter(f -> f.getFlightNumber().equals(flightNumber))
                            .findFirst()
                            .orElse(null);

                    if (flight != null) {
                        System.out.print("Number of passengers: ");
                        int count = scanner.nextInt();
                        scanner.nextLine();

                        List<Passenger> passengers = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            System.out.println("Passenger " + (i+1) + " details:");
                            System.out.print("Name: ");
                            String name = scanner.nextLine();
                            System.out.print("Passport: ");
                            String passport = scanner.nextLine();

                            passengers.add(new Passenger(
                                    (int)System.currentTimeMillis() % 1000000,
                                    name,
                                    passport,
                                    "2000-01-01",
                                    ""
                            ));
                        }

                        System.out.print("Seat class: ");
                        String seatClass = scanner.nextLine();

                        createBookingForCustomer(customer, flight, passengers, seatClass);
                    } else {
                        System.out.println("Flight not found");
                    }
                } else {
                    System.out.println("Customer not found");
                }
                break;

            case 2:
                System.out.print("Enter booking ID to modify: ");
                int bookingId = scanner.nextInt();
                scanner.nextLine();

                Booking booking = system.getBookings().stream()
                        .filter(b -> b.getBookingId() == bookingId)
                        .findFirst()
                        .orElse(null);

                if (booking != null) {
                    System.out.print("Enter new seat class: ");
                    String newSeatClass = scanner.nextLine();
                    modifyBooking(booking, newSeatClass);
                } else {
                    System.out.println("Booking not found");
                }
                break;

            case 3:
                System.out.print("Enter report type (sales/flights): ");
                String reportType = scanner.nextLine();
                generateReports(reportType, system);
                break;

            default:
                System.out.println("Invalid choice");
        }
    }

    public double getCommission() {
        return commission;
    }
}