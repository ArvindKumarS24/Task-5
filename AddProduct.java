import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * AddProductForm.java
 * Developed by Arvind Kumar S
 * Allows admins to add new products into inventory.db
 */

public class AddProductForm extends JFrame {
    private JTextField nameField, categoryField, priceField, quantityField;
    private JTextArea descriptionArea;
    private JButton saveButton, clearButton, cancelButton;

    public AddProductForm() {
        setTitle("🆕 Add New Product");
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setupLayout();
        addEventListeners();
        applyLookAndFeel();
    }

    private void initComponents() {
        nameField = new JTextField(20);
        categoryField = new JTextField(20);
        priceField = new JTextField(20);
        quantityField = new JTextField(20);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        saveButton = new JButton("💾 Save Product");
        clearButton = new JButton("🧹 Clear");
        cancelButton = new JButton("❌ Cancel");

        nameField.setToolTipText("Enter product name");
        categoryField.setToolTipText("Enter category (e.g., Electronics)");
        priceField.setToolTipText("Enter price in ₹ (e.g., 399.99)");
        quantityField.setToolTipText("Enter quantity in stock");
        descriptionArea.setToolTipText("Enter short product description");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Price (₹):"), gbc);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        saveButton.addActionListener(e -> saveProduct());
        clearButton.addActionListener(e -> clearForm());
        cancelButton.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(saveButton);
    }

    private void saveProduct() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String quantityText = quantityField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty()) {
            showError("Product name is required.", nameField);
            return;
        }
        if (category.isEmpty()) {
            showError("Category is required.", categoryField);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Enter a valid positive price.", priceField);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showError("Enter a valid positive quantity.", quantityField);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO products (name, category, price, quantity, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setInt(4, quantity);
            ps.setString(5, description);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error:\n" + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        quantityField.setText("");
        descriptionArea.setText("");
        nameField.requestFocus();
    }

    private void showError(String message, Component focusField) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
        focusField.requestFocus();
    }

    private void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddProductForm().setVisible(true));
    }
}
