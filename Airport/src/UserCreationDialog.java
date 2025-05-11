import javax.swing.*;
import java.awt.*;

public class UserCreationDialog extends JDialog {
    private final Administrator admin;
    private final BookingSystem bookingSystem;
    private JComboBox<String> roleComboBox;

    public UserCreationDialog(JFrame parent, Administrator admin, BookingSystem bookingSystem) {
        super(parent, "Create New User", true);
        this.admin = admin;
        this.bookingSystem = bookingSystem;

        setSize(400, 350);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();
        roleComboBox = new JComboBox<>(new String[]{"customer", "agent", "admin"});

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createBtn = new JButton("Create User");
        styleButton(createBtn);
        createBtn.addActionListener(e -> createUser(
                usernameField.getText(),
                new String(passwordField.getPassword()),
                nameField.getText(),
                emailField.getText(),
                contactField.getText(),
                (String) roleComboBox.getSelectedItem()
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

    public UserCreationDialog(AdminDashboard adminDashboard, Administrator admin, BookingSystem bookingSystem) {

        this.admin = admin;
        this.bookingSystem = bookingSystem;
        this.roleComboBox = roleComboBox;
    }

    private void createUser(String username, String password, String name,
                            String email, String contact, String role) {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all required fields",
                    "Incomplete Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exists = bookingSystem.getUsers().stream()
                .anyMatch(u -> u.getUsername().equals(username));

        if (exists) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = admin.createUser(role, username, password, name, email, contact, bookingSystem);
        if (newUser != null) {
            JOptionPane.showMessageDialog(this,
                    "User created successfully!\nID: " + newUser.getUserId(),
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