import javax.swing.*;
import java.awt.*;

public class DashboardScreen extends JFrame {
    private final User currentUser;
    private final FlightBookingApp app;

    public DashboardScreen(FlightBookingApp app, User user) {
        this.app = app;
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Dashboard - " + currentUser.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.add(new JLabel("Welcome, " + currentUser.getUsername()));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content - Dynamic based on role
        JPanel contentPanel = createRoleSpecificPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> app.logout());
        mainPanel.add(logoutBtn, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createRoleSpecificPanel() {
        JPanel panel = new JPanel();

        switch (currentUser.getRole().toLowerCase()) {
            case "administrator":
                panel.add(new JLabel("ADMIN DASHBOARD"));
                // Add admin-specific components
                break;

            case "agent":
                panel.add(new JLabel("AGENT DASHBOARD"));
                // Add agent-specific components
                break;

            case "customer":
                panel.add(new JLabel("CUSTOMER DASHBOARD"));
                // Add customer-specific components
                break;
        }

        return panel;
    }
}