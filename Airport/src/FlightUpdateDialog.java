import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FlightUpdateDialog extends JDialog {
    private final Flight flight;
    private final BookingSystem bookingSystem;
    private final JTextField departureField;
    private final JTextField arrivalField;

    public FlightUpdateDialog(JFrame parent, Flight flight, BookingSystem bookingSystem) {
        super(parent, "Update Flight " + flight.getFlightNumber(), true);
        this.flight = flight;
        this.bookingSystem = bookingSystem;

        setSize(400, 250);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Flight info
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Flight Information"));
        infoPanel.add(new JLabel("Flight Number:"));
        infoPanel.add(new JLabel(flight.getFlightNumber()));
        infoPanel.add(new JLabel("Airline:"));
        infoPanel.add(new JLabel(flight.getAirline()));
        infoPanel.add(new JLabel("Route:"));
        infoPanel.add(new JLabel(flight.getOrigin() + " to " + flight.getDestination()));

        // Schedule update
        JPanel schedulePanel = new JPanel(new GridLayout(0, 2, 10, 5));
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Update Schedule"));
        schedulePanel.add(new JLabel("Current Departure:"));
        schedulePanel.add(new JLabel(flight.getDepartureTime()));
        schedulePanel.add(new JLabel("New Departure:"));
        departureField = new JTextField(flight.getDepartureTime());
        schedulePanel.add(departureField);
        schedulePanel.add(new JLabel("Current Arrival:"));
        schedulePanel.add(new JLabel(flight.getArrivalTime()));
        schedulePanel.add(new JLabel("New Arrival:"));
        arrivalField = new JTextField(flight.getArrivalTime());
        schedulePanel.add(arrivalField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateBtn = new JButton("Update Flight");
        styleButton(updateBtn);
        updateBtn.addActionListener(this::updateFlight);

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(updateBtn);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(schedulePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public FlightUpdateDialog(AgentDashboard agentDashboard, Flight selectedFlight, BookingSystem bookingSystem) {

        arrivalField = null;
        departureField = null;
        this.bookingSystem = null;
        flight = null;
    }

    private void updateFlight(ActionEvent e) {
        String newDeparture = departureField.getText();
        String newArrival = arrivalField.getText();

        if (newDeparture.isEmpty() || newArrival.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both departure and arrival times",
                    "Incomplete Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        flight.updateSchedule(newDeparture, newArrival);
        bookingSystem.saveSystemData();

        JOptionPane.showMessageDialog(this,
                "Flight schedule updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}