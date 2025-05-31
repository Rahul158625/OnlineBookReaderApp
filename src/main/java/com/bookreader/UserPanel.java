package com.bookreader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserPanel extends JFrame {

    private ArrayList<Book> booksList = new ArrayList<>();
    private JPanel booksPanel;
    private JTextField searchField;
    private JButton logoutButton;

    public UserPanel() {
        setTitle("User Panel - Read Books");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header panel (vertical stack: title + logout + search)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(40, 116, 166));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Title + Logout panel
        JPanel titleLogoutPanel = new JPanel(new BorderLayout());
        titleLogoutPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Welcome to eBook Reader!", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLogoutPanel.add(titleLabel, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });
        titleLogoutPanel.add(logoutButton, BorderLayout.EAST);

        headerPanel.add(titleLogoutPanel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search by Title:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addCaretListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            filterBooks(query);
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        headerPanel.add(searchPanel);

        add(headerPanel, BorderLayout.NORTH);

        // Books panel (grid of covers)
        booksPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Books"));
        add(scrollPane, BorderLayout.CENTER);

        loadBooks("");

    }

    private void loadBooks(String query) {
        booksPanel.removeAll();
        booksList.clear();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT title, cover_path, file_path, author FROM books";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String title = rs.getString("title");
                    String coverPath = rs.getString("cover_path");
                    String filePath = rs.getString("file_path");
                    String author = rs.getString("author");  // If author exists in DB

                    booksList.add(new Book(title, author, coverPath, filePath));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        filterBooks(query);
    }

    private void filterBooks(String query) {
        booksPanel.removeAll();

        for (Book book : booksList) {
            if (book.getTitle().toLowerCase().contains(query) ||
                    book.getAuthor().toLowerCase().contains(query)) {
                addBookToPanel(book);
            }
        }

        booksPanel.revalidate();
        booksPanel.repaint();
    }

    private void addBookToPanel(Book book) {
        ImageIcon coverIcon = createScaledIcon(book.getCoverPath());
        JButton bookButton = new JButton("<html><center>" + book.getTitle() + "<br/><i>" + book.getAuthor() + "</i></center></html>", coverIcon);
        bookButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        bookButton.setHorizontalTextPosition(SwingConstants.CENTER);
        bookButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookButton.setFocusPainted(false);
        bookButton.setBackground(Color.WHITE);

        bookButton.addActionListener(e -> new PDFViewer(book.getFilePath()).setVisible(true));
        booksPanel.add(bookButton);
    }

    private ImageIcon createScaledIcon(String coverPath) {
        try {
            ImageIcon icon = new ImageIcon(coverPath);
            Image img = icon.getImage().getScaledInstance(150, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserPanel().setVisible(true));
    }

    private static class Book {
        private final String title;
        private final String author;
        private final String coverPath;
        private final String filePath;

        public Book(String title, String author, String coverPath, String filePath) {
            this.title = title;
            this.author = author == null ? "" : author;
            this.coverPath = coverPath;
            this.filePath = filePath;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getCoverPath() { return coverPath; }
        public String getFilePath() { return filePath; }
    }
}
