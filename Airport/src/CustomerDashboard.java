import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CustomerDashboard extends JPanel {
    private final Customer customer;
    private final BookingSystem bookingSystem;
    private JTable flightsTable;
    private JTable bookingsTable;

    public CustomerDashboard(Customer customer, BookingSystem bookingSystem) {
        this.customer = customer;
        this.bookingSystem = bookingSystem;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 250));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel searchPanel = createSearchPanel();
        tabbedPane.addTab("Search Flights", searchPanel);

        JPanel bookingsPanel = createBookingsPanel();
        tabbedPane.addTab("My Bookings", bookingsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel searchForm = new JPanel(new GridLayout(0, 2, 10, 10));
        searchForm.setBorder(BorderFactory.createTitledBorder("Flight Search"));

        JLabel originLabel = new JLabel("From:");
        JTextField originField = new JTextField();

        JLabel destLabel = new JLabel("To:");
        JTextField destField = new JTextField();

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField();

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All Flights");
        styleButton(searchButton);
        styleButton(showAllButton);

        searchForm.add(originLabel);
        searchForm.add(originField);
        searchForm.add(destLabel);
        searchForm.add(destField);
        searchForm.add(dateLabel);
        searchForm.add(dateField);
        searchForm.add(showAllButton);
        searchForm.add(searchButton);

        String[] columnNames = {"Flight #", "Airline", "Departure", "Arrival", "Date", "Price", "Seats"};
        flightsTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(flightsTable);

        JButton bookButton = new JButton("Book Selected Flight");
        styleButton(bookButton);
        bookButton.addActionListener(e -> bookSelectedFlight());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(bookButton);

        panel.add(searchForm, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshFlightsTable();

        searchButton.addActionListener(e -> {
            String origin = originField.getText().trim();
            String destination = destField.getText().trim();
            String dateStr = dateField.getText().trim();

            try {
                DefaultTableModel model = (DefaultTableModel) flightsTable.getModel();
                model.setRowCount(0);

                List<Flight> flights;
                if (dateStr.isEmpty()) {
                    flights = bookingSystem.searchFlights(origin, destination, null);
                } else {
                    LocalDate date = LocalDate.parse(dateStr);
                    flights = bookingSystem.searchFlights(origin, destination, date.toString());
                }

                for (Flight flight : flights) {
                    model.addRow(new Object[]{
                            flight.getFlightNumber(),
                            flight.getAirline(),
                            flight.getDepartureTime(),
                            flight.getArrivalTime(),
                            flight.getDepartureDate(),
                            flight.calculatePrice("economy"),
                            flight.getAvailableSeats()
                    });
                }

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this,
                            "No flights found matching your criteria",
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Please use YYYY-MM-DD or leave blank",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        showAllButton.addActionListener(e -> refreshFlightsTable());

        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"Booking ID", "Flight #", "Date", "Status", "Passengers", "Total Price"};
        bookingsTable = new JTable(new DefaultTableModel(columnNames, 0));
        refreshBookingsTable();

        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel Booking");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> cancelBooking());

        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshBookingsTable());

        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshFlightsTable() {
        DefaultTableModel model = (DefaultTableModel) flightsTable.getModel();
        model.setRowCount(0);

        List<Flight> allFlights = bookingSystem.getFlights();  // ✅ Show all flights, not just available
        for (Flight flight : allFlights) {
            model.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getAirline(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    flight.getDepartureDate(),
                    flight.calculatePrice("economy"),
                    flight.getAvailableSeats()
            });
        }
    }

    private void refreshBookingsTable() {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);

        for (Booking booking : customer.viewBookings()) {
            model.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getFlight().getFlightNumber(),
                    booking.getFlight().getDepartureDate(),
                    booking.getStatus(),
                    booking.getPassengers().size(),
                    booking.calculateTotalPrice()
            });
        }
    }

    private void bookSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight to book",
                    "No Flight Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String flightNumber = (String) flightsTable.getValueAt(selectedRow, 0);
        Flight selectedFlight = bookingSystem.getFlightByNumber(flightNumber);

        if (selectedFlight != null) {
            if (selectedFlight.getAvailableSeats() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "This flight is fully booked",
                        "No Seats Available",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BookingDialog bookingDialog = new BookingDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    customer,
                    selectedFlight,
                    bookingSystem
            );
            bookingDialog.setVisible(true);
            refreshFlightsTable();
            refreshBookingsTable();
        }
    }

    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking to cancel",
                    "No Booking Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
        boolean success = customer.cancelBooking(bookingId);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Booking cancelled successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshBookingsTable();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to cancel booking",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
