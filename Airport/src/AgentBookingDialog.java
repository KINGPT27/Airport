import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AgentBookingDialog extends JDialog {
    private final Agent agent;
    private final Customer customer;
    private final BookingSystem bookingSystem;
    private final JComboBox<Flight> flightComboBox;
    private final JPanel passengersPanel;
    private final List<JTextField> nameFields = new ArrayList<>();

    public AgentBookingDialog(AgentDashboard parent, Agent agent, Customer customer, BookingSystem bookingSystem) {
        super();
        this.agent = agent;
        this.customer = customer;
        this.bookingSystem = bookingSystem;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Customer info
        JPanel customerPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        customerPanel.add(new JLabel("Customer ID:"));
        customerPanel.add(new JLabel(String.valueOf(customer.getCustomerId())));
        customerPanel.add(new JLabel("Name:"));
        customerPanel.add(new JLabel(customer.getName()));
        customerPanel.add(new JLabel("Email:"));
        customerPanel.add(new JLabel(customer.getEmail()));
        customerPanel.add(new JLabel("Contact:"));
        customerPanel.add(new JLabel(customer.getContactInfo()));

        // Flight selection
        JPanel flightPanel = new JPanel(new BorderLayout());
        flightPanel.setBorder(BorderFactory.createTitledBorder("Select Flight"));
        flightComboBox = new JComboBox<>(bookingSystem.getFlights().toArray(new Flight[0]));
        flightComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Flight) {
                    Flight flight = (Flight) value;
                    setText(flight.getFlightNumber() + " - " + flight.getAirline() +
                            " (" + flight.getOrigin() + " to " + flight.getDestination() + ")");
                }
                return this;
            }
        });
        flightPanel.add(flightComboBox, BorderLayout.CENTER);

        // Passenger details
        passengersPanel = new JPanel();
        passengersPanel.setLayout(new BoxLayout(passengersPanel, BoxLayout.Y_AXIS));
        passengersPanel.setBorder(BorderFactory.createTitledBorder("Passenger Details"));

        JScrollPane scrollPane = new JScrollPane(passengersPanel);
        scrollPane.setPreferredSize(new Dimension(450, 150));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addPassengerBtn = new JButton("Add Passenger");
        styleButton(addPassengerBtn);
        addPassengerBtn.addActionListener(e -> addPassengerField());

        JButton bookBtn = new JButton("Create Booking");
        styleButton(bookBtn);
        bookBtn.addActionListener(this::createBooking);

        buttonPanel.add(addPassengerBtn);
        buttonPanel.add(bookBtn);

        mainPanel.add(customerPanel, BorderLayout.NORTH);
        mainPanel.add(flightPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        // Add initial passenger
        addPassengerField();

        add(mainPanel);
    }

    private void addPassengerField() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JLabel nameLabel = new JLabel("Passenger " + (nameFields.size() + 1) + " Name:");
        JTextField nameField = new JTextField();
        nameFields.add(nameField);

        panel.add(nameLabel);
        panel.add(nameField);

        passengersPanel.add(panel);
        revalidate();
        repaint();
    }

    private void createBooking(ActionEvent e) {
        Flight selectedFlight = (Flight) flightComboBox.getSelectedItem();
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight",
                    "No Flight Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Passenger> passengers = new ArrayList<>();
        for (JTextField nameField : nameFields) {
            String name = nameField.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all passenger names",
                        "Incomplete Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            passengers.add(new Passenger(
                    (int)System.currentTimeMillis() % 1000000 + passengers.size(),
                    name,
                    "PASSPORT" + (passengers.size() + 1), // Default passport
                    "2000-01-01", // Default DOB
                    "" // No special requests
            ));
        }

        String seatClass = "economy"; // Default

        Booking booking = agent.createBookingForCustomer(customer, selectedFlight, passengers, seatClass);
        if (booking != null) {
            bookingSystem.getBookings().add(booking);
            bookingSystem.saveSystemData();

            JOptionPane.showMessageDialog(this,
                    "Booking created successfully!\nCommission: $" + (booking.calculateTotalPrice() * agent.getCommission()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}