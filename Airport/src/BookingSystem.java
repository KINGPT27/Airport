import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookingSystem {
    private final List<Flight> flights;
    private final List<Booking> bookings;
    private final List<User> users;

    public BookingSystem() {
        this.flights = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    // --------- Flights ----------
    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public Flight getFlightByNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) return null;

        for (Flight flight : flights) {
            if (flight.getFlightNumber().equalsIgnoreCase(flightNumber.trim())) {
                return flight;
            }
        }
        return null;
    }

    public List<Flight> getAvailableFlights() {
        List<Flight> available = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getAvailableSeats() > 0) available.add(f);
        }
        return available;
    }
    public void initializeSampleData() {
        // Add default users
        users.clear();
        users.add(new Administrator(100, "admin", "admin123", "Admin User", "admin@example.com", "000-000", 100, 1));
        users.add(new Agent(200, "agent", "agent123", "Agent Smith", "agent@example.com", "111-111", 200, "Sales", 0.05));
        users.add(new Customer(300, "cust", "cust123", "Jane Doe", "jane@example.com", "222-222", 300, "123 Street", "None"));

        // Add default flights
        flights.clear();
        flights.add(new Flight("F1001", "Delta Airlines", "NYC", "LAX", "08:00", "11:30", 150, "2025-06-01", 299.99));
        flights.add(new Flight("F1002", "Emirates", "DXB", "LHR", "22:15", "02:30", 180, "2025-06-02", 599.99));
        flights.add(new Flight("F1003", "Qatar Airways", "DOH", "JFK", "01:00", "08:00", 200, "2025-06-03", 649.50));
        flights.add(new Flight("F1004", "Lufthansa", "FRA", "SIN", "13:45", "07:00", 220, "2025-06-04", 720.00));
        flights.add(new Flight("F1005", "Air France", "CDG", "NRT", "10:00", "05:50", 175, "2025-06-05", 580.25));

        saveSystemData(); // persist the sample data
    }


    // --------- Bookings ----------
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    // --------- Users ----------
    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // --------- Search ----------
    public List<Flight> searchFlights(String origin, String destination, String date) {
        List<Flight> results = new ArrayList<>();
        for (Flight flight : flights) {
            boolean matches = true;

            if (origin != null && !origin.isEmpty()) {
                matches &= flight.getOrigin().equalsIgnoreCase(origin);
            }
            if (destination != null && !destination.isEmpty()) {
                matches &= flight.getDestination().equalsIgnoreCase(destination);
            }
            if (date != null && !date.isEmpty()) {
                matches &= flight.getDepartureDate().equals(date);
            }

            if (matches && flight.getAvailableSeats() > 0) {
                results.add(flight);
            }
        }
        return results;
    }

    // --------- File I/O ----------
    public void saveSystemData() {
        saveUsersToFile();
        saveFlightsToFile();
    }

    public void loadSystemData() {
        loadUsersFromFile();
        loadFlightsFromFile();
    }

    private void saveUsersToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter("users.txt"))) {
            for (User u : users) {
                out.println(u.getUserId() + "," + u.getUsername() + "," + u.getPassword() + "," +
                        u.getRole() + "," + u.getName() + "," + u.getEmail());
            }
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    private void loadUsersFromFile() {
        users.clear();
        File file = new File("users.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                String username = parts[1];
                String password = parts[2];
                String role = parts[3];
                String name = parts[4];
                String email = parts[5];

                switch (role.toLowerCase()) {
                    case "admin":
                        users.add(new Administrator(id, username, password, name, email, "N/A", id, 1));
                        break;
                    case "agent":
                        users.add(new Agent(id, username, password, name, email, "N/A", id, "Sales", 0.05));
                        break;
                    case "customer":
                        users.add(new Customer(id, username, password, name, email, "N/A", id, "Default Address", ""));
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    private void saveFlightsToFile() {
        try (PrintWriter out = new PrintWriter(new FileWriter("flights.txt"))) {
            for (Flight f : flights) {
                out.println(f.getFlightNumber() + "," + f.getAirline() + "," + f.getOrigin() + "," +
                        f.getDestination() + "," + f.getDepartureTime() + "," + f.getArrivalTime() + "," +
                        f.getAvailableSeats() + "," + f.getDepartureDate() + "," + f.getBasePrice());
            }
        } catch (IOException e) {
            System.err.println("Failed to save flights: " + e.getMessage());
        }
    }

    private void loadFlightsFromFile() {
        flights.clear();
        File file = new File("flights.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                String number = parts[0];
                String airline = parts[1];
                String origin = parts[2];
                String destination = parts[3];
                String depTime = parts[4];
                String arrTime = parts[5];
                int seats = Integer.parseInt(parts[6]);
                String date = parts[7];
                double price = Double.parseDouble(parts[8]);

                flights.add(new Flight(number, airline, origin, destination, depTime, arrTime, seats, date, price));
            }
        } catch (IOException e) {
            System.err.println("Failed to load flights: " + e.getMessage());
        }
    }
}
