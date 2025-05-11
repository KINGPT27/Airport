import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.*;

class FileManager {
    private static final String USERS_FILE = "users.txt";
    private static final String FLIGHTS_FILE = "flights.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";
    private static final String PASSENGERS_FILE = "passengers.txt";
    private static final String LOG_FILE = "system.log";

    public void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                String role = user.getRole();
                writer.println(user.getUserId() + "," +
                        user.getUsername() + "," +
                        user.getPassword() + "," +
                        role + "," +
                        user.getName() + "," +
                        user.getEmail() + "," +
                        user.getContactInfo());
            }
            log("Saved users data");
        } catch (IOException e) {
            log("Error saving users: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    int userId = Integer.parseInt(parts[0]);
                    String username = parts[1];
                    String password = parts[2];
                    String role = parts[3];
                    String name = parts[4];
                    String email = parts[5];
                    String contact = parts[6];

                    switch(role) {
                        case "customer":
                            users.add(new Customer(
                                    userId, username, password, name, email, contact,
                                    userId, "Default Address", "None"
                            ));
                            break;
                        case "agent":
                            users.add(new Agent(
                                    userId, username, password, name, email, contact,
                                    userId, "Sales", 0.05
                            ));
                            break;
                        case "admin":
                            users.add(new Administrator(
                                    userId, username, password, name, email, contact,
                                    userId, 1
                            ));
                            break;
                    }
                }
            }
            log("Loaded users data");
        } catch (IOException e) {
            log("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public void saveFlights(List<Flight> flights) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FLIGHTS_FILE))) {
            for (Flight flight : flights) {
                writer.println(String.join(",",
                        flight.getFlightNumber(),
                        flight.getAirline(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureTime(),
                        flight.getArrivalTime(),
                        String.valueOf(flight.getAvailableSeats()),
                        flight.getDepartureDate().toString(),
                        String.valueOf(flight.calculatePrice("economy")) + "," +
                                String.valueOf(flight.calculatePrice("business")) + "," +
                                String.valueOf(flight.calculatePrice("first"))
                ));
            }
            log("Saved flights data");
        } catch (IOException e) {
            log("Error saving flights: " + e.getMessage());
        }
    }

    public List<Flight> loadFlights() {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 11) {
                    flights.add(new Flight(
                                                parts[0], parts[1], parts[2], parts[3],
                                                parts[4], parts[5], Integer.parseInt(parts[6]),
                            new double[]{
                                                        Double.parseDouble(parts[8]),
                                                        Double.parseDouble(parts[9]),
                                                        Double.parseDouble(parts[10])
                                                },
                            LocalDate.parse(parts[7])
                                        ));
                }
            }
            log("Loaded flights data");
        } catch (IOException e) {
            log("Error loading flights: " + e.getMessage());
        }
        return flights;
    }

    public void saveBookings(List<Booking> bookings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                writer.println(String.join(",",
                        String.valueOf(booking.getBookingId()),
                        String.valueOf(booking.getCustomer().getUserId()),
                        booking.getFlight().getFlightNumber(),
                        booking.getSeatSelections(),
                        booking.getStatus(),
                        booking.getPaymentStatus()
                ));
            }
            log("Saved bookings data");
        } catch (IOException e) {
            log("Error saving bookings: " + e.getMessage());
        }
    }

    public List<Booking> loadBookings(List<User> users, List<Flight> flights) {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    int bookingId = Integer.parseInt(parts[0]);
                    int customerId = Integer.parseInt(parts[1]);
                    String flightNumber = parts[2];

                    Customer customer = (Customer) users.stream()
                            .filter(u -> u.getUserId() == customerId && u instanceof Customer)
                            .findFirst()
                            .orElse(null);

                    Flight flight = flights.stream()
                            .filter(f -> f.getFlightNumber().equals(flightNumber))
                            .findFirst()
                            .orElse(null);

                    if (customer != null && flight != null) {
                        bookings.add(new Booking(
                                bookingId, customer, flight,
                                new ArrayList<>(), // Passengers would be loaded separately
                                parts[3], parts[4], parts[5]
                        ));
                    }
                }
            }
            log("Loaded bookings data");
        } catch (IOException e) {
            log("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }

    public void log(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(LocalDateTime.now() + " - " + message);
        } catch (IOException e) {
            System.err.println("Failed to write to log: " + e.getMessage());
        }
    }
}