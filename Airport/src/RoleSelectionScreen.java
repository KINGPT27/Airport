import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RoleSelectionScreen extends JFrame {
    private final BookingSystem bookingSystem;

    public RoleSelectionScreen(BookingSystem bookingSystem) {
        this.bookingSystem = bookingSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Booking System - Select Role");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Header
        JLabel headerLabel = new JLabel("Select Your Role");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setBackground(new Color(240, 245, 250));

        // Admin Button
        JButton adminButton = new JButton("Login as Administrator");
        styleButton(adminButton, new Color(70, 130, 180));  // Steel blue
        adminButton.addActionListener(e -> openLoginScreen("admin"));

        // Agent Button
        JButton agentButton = new JButton("Login as Agent");
        styleButton(agentButton, new Color(65, 105, 225));  // Royal blue
        agentButton.addActionListener(e -> openLoginScreen("agent"));

        // Customer Button
        JButton customerButton = new JButton("Login as Customer");
        styleButton(customerButton, new Color(30, 144, 255));  // Dodger blue
        customerButton.addActionListener(e -> openLoginScreen("customer"));

        buttonPanel.add(adminButton);
        buttonPanel.add(agentButton);
        buttonPanel.add(customerButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    private void openLoginScreen(String role) {
        dispose();  // Close role selection screen
        new LoginScreen(bookingSystem, role).setVisible(true);
    }
}