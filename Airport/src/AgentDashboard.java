import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AgentDashboard extends JPanel {
    private final Agent agent;
    private final BookingSystem bookingSystem;
    private JTable customersTable;
    private JTable flightsTable;

    public AgentDashboard(Agent agent, BookingSystem bookingSystem) {
        this.agent = agent;
        this.bookingSystem = bookingSystem;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Customers Tab
        JPanel customersPanel = createCustomersPanel();
        tabbedPane.addTab("Customers", customersPanel);

        // Flights Tab
        JPanel flightsPanel = createFlightsPanel();
        tabbedPane.addTab("Flights", flightsPanel);

        // Reports Tab
        JPanel reportsPanel = createReportsPanel();
        tabbedPane.addTab("Reports", reportsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"ID", "Name", "Email", "Contact", "Bookings"};
        customersTable = new JTable(new DefaultTableModel(columnNames, 0));
        refreshCustomersTable();

        JScrollPane scrollPane = new JScrollPane(customersTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton createBookingButton = new JButton("Create Booking");
        styleButton(createBookingButton);
        createBookingButton.addActionListener(e -> createBookingForCustomer());

        buttonPanel.add(createBookingButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"Flight #", "Airline", "Origin", "Destination", "Departure", "Arrival", "Seats"};
        flightsTable = new JTable(new DefaultTableModel(columnNames, 0));
        refreshFlightsTable();

        JScrollPane scrollPane = new JScrollPane(flightsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton updateButton = new JButton("Update Flight");
        styleButton(updateButton);
        updateButton.addActionListener(e -> updateFlight());

        buttonPanel.add(updateButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(reportArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton salesReportButton = new JButton("Sales Report");
        styleButton(salesReportButton);
        salesReportButton.addActionListener(e -> {
            reportArea.setText(generateSalesReport());
        });

        JButton flightsReportButton = new JButton("Flights Report");
        styleButton(flightsReportButton);
        flightsReportButton.addActionListener(e -> {
            reportArea.setText(generateFlightsReport());
        });

        buttonPanel.add(salesReportButton);
        buttonPanel.add(flightsReportButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshCustomersTable() {
        DefaultTableModel model = (DefaultTableModel) customersTable.getModel();
        model.setRowCount(0);

        for (User user : bookingSystem.getUsers()) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                model.addRow(new Object[]{
                        customer.getUserId(),
                        customer.getName(),
                        customer.getEmail(),
                        customer.getContactInfo(),
                        customer.viewBookings().size()
                });
            }
        }
    }

    private void refreshFlightsTable() {
        DefaultTableModel model = (DefaultTableModel) flightsTable.getModel();
        model.setRowCount(0);

        for (Flight flight : bookingSystem.getFlights()) {
            model.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getAirline(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    flight.getAvailableSeats()
            });
        }
    }

    private String generateSalesReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SALES REPORT ===\n\n");

        double totalSales = bookingSystem.getBookings().stream()
                .filter(b -> b.getStatus().equals("Confirmed"))
                .mapToDouble(Booking::calculateTotalPrice)
                .sum();

        long totalBookings = bookingSystem.getBookings().stream()
                .filter(b -> b.getStatus().equals("Confirmed"))
                .count();

        report.append(String.format("Total Bookings: %d\n", totalBookings));
        report.append(String.format("Total Sales: $%.2f\n", totalSales));
        report.append(String.format("Your Commission (%.0f%%): $%.2f\n",
                agent.getCommission() * 100, totalSales * agent.getCommission()));

        return report.toString();
    }

    private String generateFlightsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== FLIGHTS REPORT ===\n\n");

        for (Flight flight : bookingSystem.getFlights()) {
            long bookings = bookingSystem.getBookings().stream()
                    .filter(b -> b.getFlight().equals(flight))
                    .count();

            report.append(String.format("%s (%s)\n", flight.getFlightNumber(), flight.getAirline()));
            report.append(String.format("Route: %s to %s\n", flight.getOrigin(), flight.getDestination()));
            report.append(String.format("Date: %s | Seats: %d/%d\n",
                    flight.getDepartureDate(),
                    flight.getAvailableSeats(),
                    flight.getAvailableSeats() + bookings));
            report.append("----------------------------\n");
        }

        return report.toString();
    }

    private void createBookingForCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer",
                    "No Customer Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) customersTable.getValueAt(selectedRow, 0);
        Customer selectedCustomer = (Customer) bookingSystem.getUsers().stream()
                .filter(u -> u.getUserId() == customerId)
                .findFirst()
                .orElse(null);

        if (selectedCustomer != null) {
            new AgentBookingDialog(this, agent, selectedCustomer, bookingSystem).setVisible(true);
        }
    }

    private void updateFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight",
                    "No Flight Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String flightNumber = (String) flightsTable.getValueAt(selectedRow, 0);
        Flight selectedFlight = bookingSystem.getFlights().stream()
                .filter(f -> f.getFlightNumber().equals(flightNumber))
                .findFirst()
                .orElse(null);

        if (selectedFlight != null) {
            new FlightUpdateDialog(this, selectedFlight, bookingSystem).setVisible(true);
            refreshFlightsTable();
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}