package com.bookreader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class AdminPanel extends JFrame {
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField titleField, filePathField, coverPathField, searchField;
    private JButton addBookButton, deleteBookButton, browsePdfButton, browseCoverButton, searchButton, logoutButton;

    public AdminPanel() {
        setTitle("Admin Panel - Manage Books");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header panel with title + logout + search (stacked vertically)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(40, 116, 166));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Title + Logout panel (top of header)
        JPanel titleLogoutPanel = new JPanel(new BorderLayout());
        titleLogoutPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin Panel - Manage Books", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLogoutPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(120, 40));
        titleLogoutPanel.add(logoutButton, BorderLayout.EAST);

        headerPanel.add(titleLogoutPanel);

        // Search panel centered
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search by Title:");
        searchLabel.setForeground(Color.WHITE);  // Make label visible on dark background
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField(25);
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(searchPanel);

        add(headerPanel, BorderLayout.NORTH);

        // Table in center
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "File Path", "Cover Path"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        booksTable = new JTable(tableModel);
        booksTable.setRowHeight(28);
        booksTable.setSelectionBackground(new Color(46, 204, 113));
        booksTable.setSelectionForeground(Color.WHITE);
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Books List"));
        add(tableScrollPane, BorderLayout.CENTER);

        // Form panel on right
        JPanel formPanel = new JPanel();
        formPanel.setPreferredSize(new Dimension(350, 0));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Books"));

        // Helper method replacement for vertical space
        Runnable addVerticalSpace = () -> formPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabelForm = new JLabel("Book Title:");
        titleLabelForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(titleLabelForm);
        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formPanel.add(titleField);
        addVerticalSpace.run();

        JLabel filePathLabel = new JLabel("PDF Path:");
        filePathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(filePathLabel);
        filePathField = new JTextField();
        filePathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formPanel.add(filePathField);
        addVerticalSpace.run();

        browsePdfButton = new JButton("Browse PDF");
        browsePdfButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        browsePdfButton.setBackground(new Color(52, 152, 219));
        browsePdfButton.setForeground(Color.WHITE);
        browsePdfButton.setFocusPainted(false);
        browsePdfButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(browsePdfButton);
        addVerticalSpace.run();

        JLabel coverPathLabel = new JLabel("Cover Image Path:");
        coverPathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(coverPathLabel);
        coverPathField = new JTextField();
        coverPathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formPanel.add(coverPathField);
        addVerticalSpace.run();

        browseCoverButton = new JButton("Browse Cover");
        browseCoverButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        browseCoverButton.setBackground(new Color(52, 152, 219));
        browseCoverButton.setForeground(Color.WHITE);
        browseCoverButton.setFocusPainted(false);
        browseCoverButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(browseCoverButton);
        addVerticalSpace.run();

        // Buttons panel (Add/Delete)
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        addBookButton = new JButton("Add Book");
        addBookButton.setBackground(new Color(39, 174, 96));
        addBookButton.setForeground(Color.WHITE);
        addBookButton.setFocusPainted(false);
        deleteBookButton = new JButton("Delete Selected Book");
        deleteBookButton.setBackground(new Color(192, 57, 43));
        deleteBookButton.setForeground(Color.WHITE);
        deleteBookButton.setFocusPainted(false);
        buttonsPanel.add(addBookButton);
        buttonsPanel.add(deleteBookButton);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        formPanel.add(buttonsPanel);

        add(formPanel, BorderLayout.EAST);

        // Button Listeners
        browsePdfButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        browseCoverButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                coverPathField.setText(selectedFile.getAbsolutePath());
            }
        });

        addBookButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String filePath = filePathField.getText().trim();
            String coverPath = coverPathField.getText().trim();

            if (title.isEmpty() || filePath.isEmpty() || coverPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            addBookToDatabase(title, filePath, coverPath);
            loadBooks();
            titleField.setText("");
            filePathField.setText("");
            coverPathField.setText("");
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        });

        deleteBookButton.addActionListener(e -> {
            int selectedRow = booksTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete selected book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteBookFromDatabase(bookId);
                loadBooks();
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
            }
        });

        searchButton.addActionListener(e -> searchBooks());

        logoutButton.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        loadBooks();
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM books";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String filePath = rs.getString("file_path");
                String coverPath = rs.getString("cover_path");
                tableModel.addRow(new Object[]{id, title, filePath, coverPath});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBookToDatabase(String title, String filePath, String coverPath) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO books (title, file_path, cover_path) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, filePath);
            stmt.setString(3, coverPath);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBookFromDatabase(int bookId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, title, file_path, cover_path FROM books WHERE title LIKE ? OR author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String filePath = rs.getString("file_path");
                String coverPath = rs.getString("cover_path");
                tableModel.addRow(new Object[]{id, title, filePath, coverPath});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPanel().setVisible(true));
    }
}
