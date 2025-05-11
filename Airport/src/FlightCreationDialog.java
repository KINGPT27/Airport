import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class FlightCreationDialog extends JDialog {
    private final BookingSystem bookingSystem;

    public FlightCreationDialog(JFrame parent, BookingSystem bookingSystem) {
        super(parent, "Create New Flight", true);
        this.bookingSystem = bookingSystem;

        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Flight Details"));

        JTextField flightNumberField = new JTextField();
        JTextField airlineField = new JTextField();
        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        JTextField departureTimeField = new JTextField();
        JTextField arrivalTimeField = new JTextField();
        JTextField seatsField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().plusDays(7).toString());
        JTextField economyPriceField = new JTextField("300");
        JTextField businessPriceField = new JTextField("700");
        JTextField firstPriceField = new JTextField("1200");

        formPanel.add(new JLabel("Flight Number:"));
        formPanel.add(flightNumberField);
        formPanel.add(new JLabel("Airline:"));
        formPanel.add(airlineField);
        formPanel.add(new JLabel("Origin:"));
        formPanel.add(originField);
        formPanel.add(new JLabel("Destination:"));
        formPanel.add(destinationField);
        formPanel.add(new JLabel("Departure Time:"));
        formPanel.add(departureTimeField);
        formPanel.add(new JLabel("Arrival Time:"));
        formPanel.add(arrivalTimeField);
        formPanel.add(new JLabel("Available Seats:"));
        formPanel.add(seatsField);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Economy Price:"));
        formPanel.add(economyPriceField);
        formPanel.add(new JLabel("Business Price:"));
        formPanel.add(businessPriceField);
        formPanel.add(new JLabel("First Class Price:"));
        formPanel.add(firstPriceField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createBtn = new JButton("Create Flight");
        styleButton(createBtn);
        createBtn.addActionListener(e -> createFlight(
                flightNumberField.getText(),
                airlineField.getText(),
                originField.getText(),
                destinationField.getText(),
                departureTimeField.getText(),
                arrivalTimeField.getText(),
                seatsField.getText(),
                dateField.getText(),
                economyPriceField.getText(),
                businessPriceField.getText(),
                firstPriceField.getText()
        ));

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public FlightCreationDialog(AdminDashboard adminDashboard, BookingSystem bookingSystem) {

        this.bookingSystem = null;
    }

    private void createFlight(String flightNumber, String airline, String origin,
                              String destination, String departureTime, String arrivalTime,
                              String seatsStr, String dateStr, String economyPriceStr,
                              String businessPriceStr, String firstPriceStr) {
        try {
            int seats = Integer.parseInt(seatsStr);
            LocalDate date = LocalDate.parse(dateStr);
            double economyPrice = Double.parseDouble(economyPriceStr);
            double businessPrice = Double.parseDouble(businessPriceStr);
            double firstPrice = Double.parseDouble(firstPriceStr);

            if (flightNumber.isEmpty() || airline.isEmpty() || origin.isEmpty() ||
                    destination.isEmpty() || departureTime.isEmpty() || arrivalTime.isEmpty()) {
                throw new IllegalArgumentException("Please fill all required fields");
            }

            Flight newFlight = new Flight(
                    flightNumber, airline, origin, destination,
                    departureTime, arrivalTime, seats,
                    new double[]{economyPrice, businessPrice, firstPrice},
                    date
            );

            bookingSystem.getFlights().add(newFlight);
            bookingSystem.saveSystemData();

            JOptionPane.showMessageDialog(this,
                    "Flight created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
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