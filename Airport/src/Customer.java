import java.util.*;
import java.time.*;
import java.util.stream.Collectors;

class Customer extends User {
    private int customerId;
    private String address;
    private List<Booking> bookingHistory;
    private String preferences;

    public Customer(int userId, String username, String password, String name, String email,
                    String contactInfo, int customerId, String address, String preferences) {
        super(userId, username, password, name, email, contactInfo);
        this.customerId = customerId;
        this.address = address;
        this.preferences = preferences;
        this.bookingHistory = new ArrayList<>();
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date, BookingSystem system) {
        // Convert LocalDate to String before calling system method
        return system.searchFlights(origin, destination, date.toString()).stream()
                .filter(f -> f.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    public Booking createBooking(Flight flight, List<Passenger> passengers, String seatSelection) {
        if (flight.reserveSeat(passengers.size())) {
            int bookingId = generateUniqueId();
            Booking newBooking = new Booking(
                    bookingId,
                    this,
                    flight,
                    passengers,
                    seatSelection,
                    "Reserved",
                    "Pending"
            );
            bookingHistory.add(newBooking);
            System.out.println("Booking created successfully. ID: " + bookingId);
            return newBooking;
        }
        System.out.println("Failed to create booking. Not enough seats available.");
        return null;
    }

    public List<Booking> viewBookings() {
        return new ArrayList<>(bookingHistory);
    }

    public boolean cancelBooking(int bookingId) {
        Optional<Booking> booking = bookingHistory.stream()
                .filter(b -> b.getBookingId() == bookingId)
                .findFirst();

        if (booking.isPresent()) {
            booking.get().cancelBooking();
            return true;
        }
        return false;
    }

    @Override
    public String getRole() {
        return "customer";
    }

    @Override
    public void displayDashboard() {
        System.out.println("\n=== CUSTOMER DASHBOARD ===");
        System.out.println("Welcome, " + getName());
        System.out.println("1. Search Flights");
        System.out.println("2. View My Bookings");
        System.out.println("3. Create New Booking");
        System.out.println("4. Cancel Booking");
        System.out.println("5. Update Profile");
        System.out.println("6. Logout");
    }

    @Override
    public void handleBookingOperation(Scanner scanner, BookingSystem system) {
        System.out.println("\n=== BOOKING OPERATIONS ===");
        System.out.println("1. Search Flights");
        System.out.println("2. Create Booking");
        System.out.println("3. View Bookings");
        System.out.println("4. Cancel Booking");
        System.out.print("Select option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter origin: ");
                String origin = scanner.nextLine();
                System.out.print("Enter destination: ");
                String destination = scanner.nextLine();
                System.out.print("Enter date (YYYY-MM-DD): ");
                LocalDate date = LocalDate.parse(scanner.nextLine());

                List<Flight> flights = searchFlights(origin, destination, date, system);
                flights.forEach(f -> System.out.println(
                        f.getFlightNumber() + " - " + f.getAirline() +
                                " | " + f.getDepartureTime() + " to " + f.getArrivalTime() +
                                " | Seats: " + f.getAvailableSeats() +
                                " | Price: $" + f.calculatePrice("economy")
                ));
                break;

            case 2:
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
                        System.out.println("Passenger " + (i + 1) + " details:");
                        System.out.print("Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Passport: ");
                        String passport = scanner.nextLine();
                        System.out.print("Date of Birth (YYYY-MM-DD): ");
                        String dob = scanner.nextLine();

                        passengers.add(new Passenger(
                                generateUniqueId(),
                                name,
                                passport,
                                dob,
                                ""
                        ));
                    }

                    System.out.print("Seat class (economy/business/first): ");
                    String seatClass = scanner.nextLine();

                    Booking booking = createBooking(flight, passengers, seatClass);
                    if (booking != null) {
                        System.out.println("Booking successful! ID: " + booking.getBookingId());
                    }
                } else {
                    System.out.println("Flight not found");
                }
                break;

            case 3:
                viewBookings().forEach(b -> System.out.println(
                        "ID: " + b.getBookingId() +
                                " | Flight: " + b.getFlight().getFlightNumber() +
                                " | Status: " + b.getStatus()
                ));
                break;

            case 4:
                System.out.print("Enter booking ID to cancel: ");
                int bookingId = scanner.nextInt();
                scanner.nextLine();
                if (cancelBooking(bookingId)) {
                    System.out.println("Booking cancelled");
                } else {
                    System.out.println("Booking not found");
                }
                break;

            default:
                System.out.println("Invalid choice");
        }
    }

    private int generateUniqueId() {
        return Math.abs((int) System.currentTimeMillis() % 1000000);
    }

    public int getCustomerId() {
        return customerId;
    }
}
