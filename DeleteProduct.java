import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * DeleteProductForm.java
 * Developed by Arvind Kumar S
 * Java Swing-based Product Deletion Panel for Admin Dashboard
 */

public class DeleteProductForm extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton, clearSearchButton, closeButton;
    private JTextField searchField;

    public DeleteProductForm() {
        setTitle("🗑️ Delete Products - Admin Panel");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setupLayout();
        addEventListeners();
        loadProducts();
        applyTheme();
    }

    private void initComponents() {
        String[] columns = {"ID", "Name", "Category", "Price", "Quantity", "Description", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setRowHeight(28);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        productTable.getTableHeader().setReorderingAllowed(false);

        productTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        productTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        searchField = new JTextField(20);
        searchField.setToolTipText("Search products by name or category");

        refreshButton = new JButton("🔄 Refresh");
        clearSearchButton = new JButton("❌ Clear");
        closeButton = new JButton("Close");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.add(new JLabel("🔎 Search:"));
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);
        searchPanel.add(clearSearchButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(closeButton);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        refreshButton.addActionListener(e -> loadProducts());

        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            loadProducts();
        });

        closeButton.addActionListener(e -> dispose());

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterProducts();
            }
        });

        searchField.addActionListener(e -> filterProducts());
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY name")) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.format("₹%.2f", rs.getDouble("price")),
                    rs.getInt("quantity"),
                    rs.getString("description"),
                    "Delete"
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            showError("Failed to load products.", ex);
        }
    }

    private void filterProducts() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadProducts();
            return;
        }

        tableModel.setRowCount(0);
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM products WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ? ORDER BY name";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchText + "%");
            ps.setString(2, "%" + searchText + "%");

            ResultSet rs = ps.executeQuery();
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No matching products found.",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    String.format("₹%.2f", rs.getDouble("price")),
                    rs.getInt("quantity"),
                    rs.getString("description"),
                    "Delete"
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            showError("Search failed.", ex);
        }
    }

    public void deleteProduct(int productId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this product?\nThis cannot be undone.",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM products WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, productId);

                int result = ps.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Product deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "Product not found or already deleted.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                showError("Failed to delete product.", ex);
            }
        }
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message,
                "Database Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); // Log only
    }

    private void applyTheme() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeleteProductForm().setVisible(true));
    }
}

// Renderer for Delete Button
class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
        setForeground(Color.RED);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "");
        return this;
    }
}

// Editor for Delete Button
class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private int productId;
    private boolean isPushed;
    private final DeleteProductForm parentForm;

    public ButtonEditor(JCheckBox checkBox, DeleteProductForm parentForm) {
        super(checkBox);
        this.parentForm = parentForm;
        button = new JButton();
        button.setOpaque(true);
        button.setForeground(Color.RED);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        productId = (int) table.getValueAt(row, 0);
        button.setText(value != null ? value.toString() : "");
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            parentForm.deleteProduct(productId);
        }
        isPushed = false;
        return "Delete";
    }
}
