import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

public class LoginScreen extends JFrame {
    private final BookingSystem bookingSystem;
    private final String selectedRole;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen(BookingSystem bookingSystem, String role) {
        this.bookingSystem = bookingSystem;
        this.selectedRole = role;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Flight Booking System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Flight Booking System");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0, 102, 204));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::handleLogin);
        buttonPanel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> showRegistrationDialog());
        buttonPanel.add(registerButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new RoleSelectionScreen(bookingSystem).setVisible(true);
        });
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Optional<User> user = bookingSystem.getUsers().stream()
                .filter(u -> u.getRole().equals(selectedRole))
                .filter(u -> u.login(username, password))
                .findFirst();

        if (user.isPresent()) {
            User loggedInUser = user.get();
            dispose();

            JFrame dashboardFrame = new JFrame("Dashboard");
            dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dashboardFrame.setSize(900, 600);
            dashboardFrame.setLocationRelativeTo(null);

            switch (loggedInUser.getRole()) {
                case "customer":
                    dashboardFrame.setContentPane(new CustomerDashboard((Customer) loggedInUser, bookingSystem));
                    break;
                case "agent":
                    dashboardFrame.setContentPane(new AgentDashboard((Agent) loggedInUser, bookingSystem));
                    break;
                case "admin":
                    dashboardFrame.setContentPane(new AdminDashboard((Administrator) loggedInUser, bookingSystem));
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown user role.");
                    return;
            }

            dashboardFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register as " + selectedRole, true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();

        panel.add(new JLabel("Username:")); panel.add(usernameField);
        panel.add(new JLabel("Password:")); panel.add(passwordField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Contact:")); panel.add(contactField);

        JButton submit = new JButton("Register");
        submit.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String contact = contactField.getText();

            int userId = (int)(System.currentTimeMillis() % 1000000);
            User newUser;

            switch (selectedRole) {
                case "customer":
                    newUser = new Customer(userId, username, password, name, email, contact, userId, "Default Address", "None");
                    break;
                case "agent":
                    newUser = new Agent(userId, username, password, name, email, contact, userId, "Sales", 0.05);
                    break;
                case "admin":
                    newUser = new Administrator(userId, username, password, name, email, contact, userId, 1);
                    break;
                default:
                    JOptionPane.showMessageDialog(dialog, "Invalid role");
                    return;
            }

            bookingSystem.getUsers().add(newUser);
            bookingSystem.saveSystemData();

            JOptionPane.showMessageDialog(dialog, "Registration successful! You can now login.");
            dialog.dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submit);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
