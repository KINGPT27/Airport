import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboard extends JPanel {
    private final BookingSystem bookingSystem;
    private final JTable flightTable;
    private final DefaultTableModel tableModel;

    public AdminDashboard(Administrator admin, BookingSystem system) {
        this.bookingSystem = system;
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Flights Tab ---
        JPanel flightsPanel = new JPanel(new BorderLayout());
        String[] flightColumns = {"Flight #", "Airline", "Origin", "Destination", "Departure", "Arrival", "Seats", "Date", "Price"};
        tableModel = new DefaultTableModel(flightColumns, 0);
        flightTable = new JTable(tableModel);
        loadFlightsToTable();
        JScrollPane flightScrollPane = new JScrollPane(flightTable);
        flightsPanel.add(flightScrollPane, BorderLayout.CENTER);

        JPanel flightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addRowButton = new JButton("Add Row");
        addRowButton.addActionListener(e -> tableModel.addRow(new Object[flightColumns.length]));
        JButton saveButton = new JButton("Save Flights");
        saveButton.addActionListener(this::saveFlights);
        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });
        flightButtonPanel.add(addRowButton);
        flightButtonPanel.add(removeButton);
        flightButtonPanel.add(saveButton);
        flightsPanel.add(flightButtonPanel, BorderLayout.SOUTH);

        // --- Users Tab ---
        JPanel usersPanel = new JPanel(new BorderLayout());
        String[] userColumns = {"ID", "Username", "Role", "Name", "Email"};
        DefaultTableModel userModel = new DefaultTableModel(userColumns, 0);
        JTable userTable = new JTable(userModel);
        if (bookingSystem.getUsers().isEmpty()) {
            bookingSystem.getUsers().add(new Administrator(100, "admin", "admin123", "Admin User", "admin@example.com", "000-000", 100, 1));
            bookingSystem.getUsers().add(new Agent(200, "agent", "agent123", "Agent Smith", "agent@example.com", "111-111", 200, "Sales", 0.05));
            bookingSystem.getUsers().add(new Customer(300, "cust", "cust123", "Jane Doe", "jane@example.com", "222-222", 300, "123 Street", "None"));
        }
        JScrollPane userScrollPane = new JScrollPane(userTable);
        usersPanel.add(userScrollPane, BorderLayout.CENTER);

        for (User user : bookingSystem.getUsers()) {
            userModel.addRow(new Object[]{
                    user.getUserId(), user.getUsername(), user.getRole(),
                    user.getName(), user.getEmail()
            });
        }

        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> userModel.addRow(new Object[userColumns.length]));

        JButton removeUserButton = new JButton("Remove Selected");
        removeUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                userModel.removeRow(selectedRow);
            }
        });

        JButton saveUsersButton = new JButton("Save Users");
        saveUsersButton.addActionListener(e -> {
            bookingSystem.getUsers().clear();
            for (int i = 0; i < userModel.getRowCount(); i++) {
                try {
                    int id = Integer.parseInt(userModel.getValueAt(i, 0).toString());
                    String username = (String) userModel.getValueAt(i, 1);
                    String role = (String) userModel.getValueAt(i, 2);
                    String name = (String) userModel.getValueAt(i, 3);
                    String email = (String) userModel.getValueAt(i, 4);

                    User user;
                    switch (role.toLowerCase()) {
                        case "customer":
                            user = new Customer(id, username, "pass", name, email, "N/A", id, "", "");
                            break;
                        case "agent":
                            user = new Agent(id, username, "pass", name, email, "N/A", id, "Dept", 0.0);
                            break;
                        case "admin":
                            user = new Administrator(id, username, "pass", name, email, "N/A", id, 1);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown role: " + role);
                    }
                    bookingSystem.getUsers().add(user);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input in user row " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            bookingSystem.saveSystemData();
            log("Users saved by admin at " + LocalDateTime.now());
            JOptionPane.showMessageDialog(this, "Users saved successfully.");
        });

        userButtonPanel.add(addUserButton);
        userButtonPanel.add(removeUserButton);
        userButtonPanel.add(saveUsersButton);
        usersPanel.add(userButtonPanel, BorderLayout.SOUTH);

        // --- System Tab ---
        JPanel systemPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
            new RoleSelectionScreen(bookingSystem).setVisible(true);
        });
        systemPanel.add(logoutButton, BorderLayout.NORTH);

        JTextArea logArea = new JTextArea(15, 60);
        logArea.setEditable(false);
        try {
            List<String> logs = Files.readAllLines(Paths.get("logs.txt"));
            for (String line : logs) {
                logArea.append(line + "\n");
            }
        } catch (IOException ex) {
            logArea.setText("No logs available or failed to load logs.");
        }
        JScrollPane logScroll = new JScrollPane(logArea);
        systemPanel.add(logScroll, BorderLayout.CENTER);

        tabbedPane.addTab("Users", usersPanel);
        tabbedPane.addTab("Flights", flightsPanel);
        tabbedPane.addTab("System", systemPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadFlightsToTable() {
        tableModel.setRowCount(0);
        if (bookingSystem.getFlights().isEmpty()) {
            bookingSystem.getFlights().add(new Flight("F1001", "Delta Airlines", "NYC", "LAX", "08:00", "11:30", 150, "2025-06-01", 299.99));
            bookingSystem.getFlights().add(new Flight("F1002", "Emirates", "DXB", "LHR", "22:15", "02:30", 180, "2025-06-02", 599.99));
            bookingSystem.getFlights().add(new Flight("F1003", "Qatar Airways", "DOH", "JFK", "01:00", "08:00", 200, "2025-06-03", 649.50));
            bookingSystem.getFlights().add(new Flight("F1004", "Lufthansa", "FRA", "SIN", "13:45", "07:00", 220, "2025-06-04", 720.00));
            bookingSystem.getFlights().add(new Flight("F1005", "Air France", "CDG", "NRT", "10:00", "05:50", 175, "2025-06-05", 580.25));
        }
        for (Flight f : bookingSystem.getFlights()) {
            tableModel.addRow(new Object[]{
                    f.getFlightNumber(), f.getAirline(), f.getOrigin(),
                    f.getDestination(), f.getDepartureTime(), f.getArrivalTime(),
                    f.getAvailableSeats(), f.getDepartureDate(), f.getBasePrice()
            });
        }
    }

    private void saveFlights(ActionEvent e) {
        bookingSystem.getFlights().clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                String number = (String) tableModel.getValueAt(i, 0);
                String airline = (String) tableModel.getValueAt(i, 1);
                String origin = (String) tableModel.getValueAt(i, 2);
                String destination = (String) tableModel.getValueAt(i, 3);
                String dep = (String) tableModel.getValueAt(i, 4);
                String arr = (String) tableModel.getValueAt(i, 5);
                int seats = Integer.parseInt(tableModel.getValueAt(i, 6).toString());
                String date = (String) tableModel.getValueAt(i, 7);
                double price = Double.parseDouble(tableModel.getValueAt(i, 8).toString());

                Flight flight = new Flight(number, airline, origin, destination, dep, arr, seats, date, price);
                bookingSystem.getFlights().add(flight);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input at row " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        bookingSystem.saveSystemData();
        log("Flights saved by admin at " + LocalDateTime.now());
        JOptionPane.showMessageDialog(this, "Flights saved successfully.");
    }

    private void log(String message) {
        try (PrintWriter out = new PrintWriter(new java.io.FileWriter("logs.txt", true))) {
            out.println(LocalDateTime.now() + " - " + message);
        } catch (IOException e) {
            System.err.println("Failed to write to logs.txt: " + e.getMessage());
        }
    }
}
