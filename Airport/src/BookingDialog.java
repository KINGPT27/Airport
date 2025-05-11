import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDialog extends JDialog {
    private final Customer customer;
    private final Flight flight;
    private final BookingSystem bookingSystem;
    private final JPanel passengersPanel;
    private final List<JTextField> nameFields = new ArrayList<>();
    private final List<JTextField> passportFields = new ArrayList<>();
    private String selectedSeatClass = "economy";
    private double totalPrice = 0;


    public BookingDialog(JFrame parent, Customer customer, Flight flight, BookingSystem bookingSystem) {
        super(parent, "Book Flight " + flight.getFlightNumber(), true);
        this.customer = customer;
        this.flight = flight;
        this.bookingSystem = bookingSystem;

        setSize(600, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Flight info panel
        JPanel flightPanel = createFlightInfoPanel();

        // Passenger details panel
        passengersPanel = new JPanel();
        passengersPanel.setLayout(new BoxLayout(passengersPanel, BoxLayout.Y_AXIS));
        passengersPanel.setBorder(BorderFactory.createTitledBorder("Passenger Details (1-"+flight.getAvailableSeats()+")"));

        JScrollPane scrollPane = new JScrollPane(passengersPanel);
        scrollPane.setPreferredSize(new Dimension(550, 150));

        // Seat class selection panel
        JPanel seatPanel = createSeatClassPanel();

        // Payment and buttons panel
        JPanel paymentPanel = new JPanel(new BorderLayout());
        JLabel priceLabel = new JLabel("Total Price: $0.00");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addPassengerBtn = new JButton("Add Passenger");
        styleButton(addPassengerBtn);
        addPassengerBtn.addActionListener(e -> addPassengerField(priceLabel));

        JButton bookBtn = new JButton("Confirm Booking");
        styleButton(bookBtn);
        bookBtn.addActionListener(e -> processBooking(priceLabel));

        buttonPanel.add(addPassengerBtn);
        buttonPanel.add(bookBtn);

        paymentPanel.add(priceLabel, BorderLayout.WEST);
        paymentPanel.add(buttonPanel, BorderLayout.EAST);

        mainPanel.add(flightPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(seatPanel, BorderLayout.SOUTH);
        mainPanel.add(paymentPanel, BorderLayout.PAGE_END);

        // Add initial passenger
        addPassengerField(priceLabel);

        add(mainPanel);
    }

    private JPanel createFlightInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Flight Details"));

        panel.add(new JLabel("Flight Number:"));
        panel.add(new JLabel(flight.getFlightNumber()));
        panel.add(new JLabel("Airline:"));
        panel.add(new JLabel(flight.getAirline()));
        panel.add(new JLabel("Route:"));
        panel.add(new JLabel(flight.getOrigin() + " → " + flight.getDestination()));
        panel.add(new JLabel("Date:"));
        panel.add(new JLabel(flight.getDepartureDate().toString()));
        panel.add(new JLabel("Departure:"));
        panel.add(new JLabel(flight.getDepartureTime()));
        panel.add(new JLabel("Arrival:"));
        panel.add(new JLabel(flight.getArrivalTime()));
        panel.add(new JLabel("Available Seats:"));
        panel.add(new JLabel(String.valueOf(flight.getAvailableSeats())));

        return panel;
    }

    private JPanel createSeatClassPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Select Seat Class"));

        ButtonGroup seatGroup = new ButtonGroup();

        JRadioButton economyRadio = new JRadioButton("Economy");
        economyRadio.setSelected(true);
        economyRadio.addActionListener(e -> {
            selectedSeatClass = "economy";
            updateTotalPrice();
        });

        JRadioButton businessRadio = new JRadioButton("Business");
        businessRadio.addActionListener(e -> {
            selectedSeatClass = "business";
            updateTotalPrice();
        });

        JRadioButton firstRadio = new JRadioButton("First Class");
        firstRadio.addActionListener(e -> {
            selectedSeatClass = "first";
            updateTotalPrice();
        });

        JLabel economyPrice = new JLabel("$" + flight.calculatePrice("economy"));
        JLabel businessPrice = new JLabel("$" + flight.calculatePrice("business"));
        JLabel firstPrice = new JLabel("$" + flight.calculatePrice("first"));

        JPanel economyPanel = createSeatClassOptionPanel(economyRadio, economyPrice, new Color(220, 240, 255));
        JPanel businessPanel = createSeatClassOptionPanel(businessRadio, businessPrice, new Color(200, 230, 255));
        JPanel firstPanel = createSeatClassOptionPanel(firstRadio, firstPrice, new Color(180, 220, 255));

        seatGroup.add(economyRadio);
        seatGroup.add(businessRadio);
        seatGroup.add(firstRadio);

        panel.add(economyPanel);
        panel.add(businessPanel);
        panel.add(firstPanel);

        return panel;
    }

    private JPanel createSeatClassOptionPanel(JRadioButton radio, JLabel price, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(bgColor);
        centerPanel.add(radio);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(bgColor);
        southPanel.add(price);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addPassengerField(JLabel priceLabel) {
        if (nameFields.size() >= flight.getAvailableSeats()) {
            JOptionPane.showMessageDialog(this,
                    "Cannot add more passengers than available seats",
                    "Maximum Passengers Reached",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel("Passenger " + (nameFields.size() + 1) + " Name:");
        JTextField nameField = new JTextField();
        nameFields.add(nameField);

        JLabel passportLabel = new JLabel("Passport Number:");
        JTextField passportField = new JTextField();
        passportFields.add(passportField);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(passportLabel);
        panel.add(passportField);

        passengersPanel.add(panel);
        revalidate();
        repaint();

        updateTotalPrice();
        priceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
    }

    private void updateTotalPrice() {
        totalPrice = nameFields.size() * flight.calculatePrice(selectedSeatClass);
    }

    private void processBooking(JLabel priceLabel) {
        // Validate passenger info
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < nameFields.size(); i++) {
            String name = nameFields.get(i).getText();
            String passport = passportFields.get(i).getText();

            if (name.isEmpty() || passport.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all passenger details",
                        "Incomplete Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            passengers.add(new Passenger(
                    (int)System.currentTimeMillis() % 1000000 + i,
                    name,
                    passport,
                    LocalDate.now().minusYears(20).toString(), // Default DOB (20 years ago)
                    "" // No special requests
            ));
        }

        // Check seat availability
        if (!flight.checkAvailability(passengers.size())) {
            JOptionPane.showMessageDialog(this,
                    "Not enough seats available for this flight",
                    "Seats Unavailable",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Process payment
        String[] options = {"Credit Card", "Debit Card", "PayPal", "Cancel"};
        int paymentChoice = JOptionPane.showOptionDialog(this,
                "Total to pay: $" + totalPrice + "\nSelect payment method:",
                "Payment",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (paymentChoice == 3 || paymentChoice == -1) { // Cancel or closed
            return;
        }

        String paymentMethod = options[paymentChoice];

        // Create booking
        int bookingId = (int)System.currentTimeMillis() % 1000000;
        Booking booking = new Booking(
                bookingId,
                customer,
                flight,
                passengers,
                selectedSeatClass,
                "Confirmed",
                "Paid"
        );

        // Reserve seats
        flight.reserveSeat(passengers.size());

        // Add to system
        customer.viewBookings().add(booking);
        bookingSystem.getBookings().add(booking);
        bookingSystem.saveSystemData();

        // Show confirmation
        JOptionPane.showMessageDialog(this,
                "Booking confirmed!\n\n" +
                        "Booking ID: " + bookingId + "\n" +
                        "Flight: " + flight.getFlightNumber() + "\n" +
                        "Passengers: " + passengers.size() + "\n" +
                        "Total Paid: $" + totalPrice,
                "Booking Successful",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }


}
