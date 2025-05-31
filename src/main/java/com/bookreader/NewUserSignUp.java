package com.bookreader;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewUserSignUp extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signUpButton, backButton;

    public NewUserSignUp() {
        setTitle("New User Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title label
        JLabel titleLabel = new JLabel("Create a New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Username label and field panel
        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        userPanel.setBackground(Color.WHITE);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(usernameField, BorderLayout.CENTER);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        mainPanel.add(userPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15))); // spacer

        // Password label and field panel
        JPanel passPanel = new JPanel(new BorderLayout(5, 5));
        passPanel.setBackground(Color.WHITE);
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(passwordField, BorderLayout.CENTER);
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        mainPanel.add(passPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30))); // spacer

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(0, 153, 76));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(0, 102, 204));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);

        // Add main panel to frame
        add(mainPanel);

        // Action Listeners
        signUpButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            dispose();
            new LoginScreen().setVisible(true);
        });

        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User registered successfully!");

            dispose();
            new LoginScreen().setVisible(true);

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Username already exists.");
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error occurred.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NewUserSignUp::new);
    }
}
