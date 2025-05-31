package com.bookreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFViewer extends JFrame {
    private PDDocument document;
    private PDFRenderer renderer;
    private int currentPage = 0;
    private double zoomFactor = 1.0;
    private JLabel pdfLabel;
    private JLabel pageNumberLabel;

    private final Color HEADER_BG_COLOR = new Color(40, 116, 166);
    private final Color BUTTON_BG_COLOR = new Color(40, 116, 166);
    private final Font FONT_SEGOE_UI = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_SEGOE_UI_BOLD = new Font("Segoe UI", Font.BOLD, 16);

    public PDFViewer(String filePath) {
        setTitle("PDF Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Page number label with blue background
        pageNumberLabel = new JLabel("Page 1 / 1", SwingConstants.CENTER);
        pageNumberLabel.setOpaque(true);
        pageNumberLabel.setBackground(HEADER_BG_COLOR);
        pageNumberLabel.setForeground(Color.WHITE);
        pageNumberLabel.setFont(FONT_SEGOE_UI_BOLD);
        pageNumberLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        mainPanel.add(pageNumberLabel, BorderLayout.NORTH);

        // PDF display label inside scroll pane
        pdfLabel = new JLabel("", SwingConstants.CENTER);
        pdfLabel.setBackground(Color.WHITE);
        pdfLabel.setOpaque(true);
        JScrollPane scrollPane = new JScrollPane(pdfLabel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Navigation panel at bottom with buttons
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        navigationPanel.setBackground(Color.WHITE);

        JButton prevButton = createNavButton("Previous");
        JButton nextButton = createNavButton("Next");
        JButton zoomInButton = createNavButton("Zoom In");
        JButton zoomOutButton = createNavButton("Zoom Out");

        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                displayPage(currentPage);
            } else {
                JOptionPane.showMessageDialog(this, "Already on the first page!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < document.getNumberOfPages() - 1) {
                currentPage++;
                displayPage(currentPage);
            } else {
                JOptionPane.showMessageDialog(this, "Already on the last page!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        zoomInButton.addActionListener(e -> {
            zoomFactor = Math.min(zoomFactor + 0.1, 3.0); // max zoom 300%
            displayPage(currentPage);
        });

        zoomOutButton.addActionListener(e -> {
            if (zoomFactor > 0.3) { // min zoom 30%
                zoomFactor -= 0.1;
                displayPage(currentPage);
            } else {
                JOptionPane.showMessageDialog(this, "Minimum zoom reached!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        navigationPanel.add(prevButton);
        navigationPanel.add(nextButton);
        navigationPanel.add(zoomInButton);
        navigationPanel.add(zoomOutButton);

        mainPanel.add(navigationPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load PDF document
        try {
            File pdfFile = new File(filePath);
            if (!pdfFile.exists()) {
                throw new IOException("File not found: " + filePath);
            }
            document = PDDocument.load(pdfFile);
            renderer = new PDFRenderer(document);
            displayPage(currentPage);
        } catch (IOException ex) {
            ex.printStackTrace();
            showError("Unable to open PDF. Check the file path or ensure the file is a valid PDF.");
        }
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BG_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_SEGOE_UI_BOLD);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(110, 38));
        return button;
    }

    private void displayPage(int pageIndex) {
        try {
            // Render with DPI adjusted by zoomFactor (base DPI 72 scaled)
            float dpi = (float) (72 * zoomFactor);
            BufferedImage image = renderer.renderImageWithDPI(pageIndex, dpi);
            ImageIcon icon = new ImageIcon(image);
            pdfLabel.setIcon(icon);
            pdfLabel.setText(null);
            pdfLabel.revalidate();

            pageNumberLabel.setText("Page " + (pageIndex + 1) + " / " + document.getNumberOfPages());
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to render page " + (pageIndex + 1));
        }
    }

    private void showError(String message) {
        pdfLabel.setText("<html><div style='color:red; text-align:center;'>" + message + "</div></html>");
        pdfLabel.setIcon(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
