package Component;

import Model.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class Customers extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG        = new Color(0xFAF7F2);
    private static final Color CARD      = Color.WHITE;
    private static final Color INK       = new Color(0x1A1A2E);
    private static final Color INK_LIGHT = new Color(0x6B6B80);
    private static final Color ACCENT    = new Color(0xE8923A);
    private static final Color BORDER    = new Color(0xE2DDD6);
    private static final Color ROW_ALT   = new Color(0xFAF7F2);
    private static final Color HDR_BG    = new Color(0x1A1A2E);

    private DefaultTableModel tableModel;
    private JTable table;
    private static final String[] COLUMNS = {"Name", "Contact", "Address"};

    public Customers() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // Top bar
        JPanel topBar = new JPanel(null);
        topBar.setBackground(CARD);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        topBar.setPreferredSize(new Dimension(810, 64));

        JLabel title = new JLabel("Customers");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(INK); title.setBounds(28, 16, 300, 32); topBar.add(title);
        add(topBar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.setRowHeight(46);
        table.setBackground(CARD);
        table.setOpaque(true);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);
        table.setSelectionBackground(new Color(0xFFF3E8));

        // Custom renderer for alternating rows + padding
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(row % 2 == 0 ? CARD : ROW_ALT);
                setForeground(col == 0 ? INK : INK_LIGHT);
                setFont(col == 0 ? new Font("Tahoma", Font.BOLD, 13) : new Font("Tahoma", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 16, 0, 8));
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 12));
        header.setBackground(HDR_BG);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        header.setBorder(null);
        header.setPreferredSize(new Dimension(0, 42));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(360);

        refreshCustomerTable();

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG); sp.getViewport().setBackground(BG);
        sp.setBorder(new EmptyBorder(24, 28, 24, 28));
        add(sp, BorderLayout.CENTER);
    }

    public void registerUserOnLogin(String username) {
        if (username == null || username.trim().isEmpty() || username.equals("Walk-In Customer")) return;
        String checkQuery = "SELECT COUNT(*) FROM customers WHERE username = ?";
        String insertQuery = "INSERT INTO customers (username, contact, address, product_name, quantity) VALUES (?, 'Not Provided', 'Not Provided', 'Registration Only', 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            checkPs.setString(1, username.trim());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
                        insertPs.setString(1, username.trim()); insertPs.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) { System.err.println("Error auto-registering user session: " + ex.getMessage()); }
        refreshCustomerTable();
    }

    public void updateCustomerProfile(String username, String contact, String address) {
        if (username == null || username.trim().isEmpty()) return;
        String query = "UPDATE customers SET contact = ?, address = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, contact.trim()); ps.setString(2, address.trim());
            ps.setString(3, username.trim()); ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Failed to update profile values: " + ex.getMessage()); ex.printStackTrace();
        }
        refreshCustomerTable();
    }

    public void refreshCustomerTable() {
        tableModel.setRowCount(0);
        String query = "SELECT username, MAX(contact) as contact, MAX(address) as address FROM customers GROUP BY username ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("username"), rs.getString("contact"), rs.getString("address")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Failed to pull active customer entities: " + ex.getMessage()); ex.printStackTrace();
        }
    }

    public void addCustomerRecord(String displayedUser, String name, int quantity) {}
}
