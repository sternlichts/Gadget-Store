package Component;

import Model.DBConnection;
import Model.OrderItem;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Orders extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG        = new Color(0xFAF7F2);
    private static final Color CARD      = Color.WHITE;
    private static final Color INK       = new Color(0x1A1A2E);
    private static final Color INK_LIGHT = new Color(0x6B6B80);
    private static final Color ACCENT    = new Color(0xE8923A);
    private static final Color BORDER    = new Color(0xE2DDD6);
    private static final Color SUCCESS   = new Color(0x2D8A5E);

    private JPanel dynamicCardContainer;
    private int cardYOffset = 16;
    private final ArrayList<OrderItem> orderItemsList = new ArrayList<>();

    public Orders() {
        setLayout(new BorderLayout());
        setBackground(BG);

        // Top bar
        JPanel topBar = new JPanel(null);
        topBar.setBackground(CARD);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        topBar.setPreferredSize(new Dimension(810, 64));

        JLabel title = new JLabel("Order History");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(INK); title.setBounds(28, 16, 300, 32);
        topBar.add(title);
        add(topBar, BorderLayout.NORTH);

        dynamicCardContainer = new JPanel(null);
        dynamicCardContainer.setBackground(BG);
        dynamicCardContainer.setPreferredSize(new Dimension(790, 600));

        JScrollPane scrollPane = new JScrollPane(dynamicCardContainer);
        scrollPane.setBorder(null); scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        loadOrdersFromDB();
    }

    private void loadOrdersFromDB() {
        String sql = "SELECT product_name, variant, price, quantity, image_path, ordered_at FROM orders ORDER BY ordered_at ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getString("product_name"), rs.getString("price"),
                    rs.getString("variant"), rs.getString("image_path"), rs.getInt("quantity")
                );
                Timestamp ts = rs.getTimestamp("ordered_at");
                if (ts != null) {
                    LocalDateTime ldt = ts.toLocalDateTime();
                    item.setOrderedAt(ldt); item.setOrderDate(ldt.toLocalDate());
                }
                orderItemsList.add(item); renderOrderCard(item);
            }
        } catch (SQLException ex) { System.err.println("Failed to load orders from DB: " + ex.getMessage()); }
    }

    public void addDynamicOrderCard(OrderItem order) {
        if (order.getOrderDate() == null) order.setOrderDate(LocalDate.now());
        orderItemsList.add(order); saveOrderToDB(order); renderOrderCard(order);
    }

    private void renderOrderCard(OrderItem order) {
        JPanel card = new JPanel(null);
        card.setBounds(20, cardYOffset, 755, 130);
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JPanel strip = new JPanel();
        strip.setBackground(SUCCESS);
        strip.setBounds(0, 0, 4, 130); card.add(strip);

        JLabel phoneThumbImg = new JLabel();
        phoneThumbImg.setBounds(16, 16, 90, 98);
        phoneThumbImg.setBackground(new Color(0xF7F4EF)); phoneThumbImg.setOpaque(true);
        phoneThumbImg.setHorizontalAlignment(SwingConstants.CENTER);
        phoneThumbImg.setBorder(new LineBorder(BORDER, 1));
        setImage(phoneThumbImg, order.getImagePath()); card.add(phoneThumbImg);

        JLabel prodName = new JLabel(order.getName());
        prodName.setBounds(122, 18, 350, 24);
        prodName.setFont(new Font("Tahoma", Font.BOLD, 15));
        prodName.setForeground(INK); card.add(prodName);

        JLabel prodVariant = new JLabel(order.getVariant());
        prodVariant.setBounds(122, 44, 260, 18);
        prodVariant.setFont(new Font("Tahoma", Font.PLAIN, 12));
        prodVariant.setForeground(INK_LIGHT); card.add(prodVariant);

        String dateText = order.getOrderedAt() != null
            ? order.getOrderedAt().toLocalDate().toString()
            : (order.getOrderDate() != null ? order.getOrderDate().toString() : "");
        JLabel dateLabel = new JLabel("📅  " + dateText);
        dateLabel.setBounds(122, 64, 220, 18);
        dateLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        dateLabel.setForeground(INK_LIGHT); card.add(dateLabel);

        JLabel statusBadge = new JLabel("Delivered");
        statusBadge.setBounds(122, 86, 80, 22);
        statusBadge.setFont(new Font("Tahoma", Font.BOLD, 10));
        statusBadge.setForeground(SUCCESS); statusBadge.setOpaque(true);
        statusBadge.setBackground(new Color(0xE6F5EE));
        statusBadge.setBorder(new LineBorder(new Color(0xB8DFCD), 1));
        statusBadge.setHorizontalAlignment(SwingConstants.CENTER); card.add(statusBadge);

        JLabel itemQuantity = new JLabel("×" + order.getQuantity());
        itemQuantity.setBounds(615, 16, 50, 24);
        itemQuantity.setFont(new Font("Tahoma", Font.BOLD, 14));
        itemQuantity.setForeground(INK_LIGHT); card.add(itemQuantity);

        String cleanPrice = order.getPrice().replaceAll("[^\\d]", "");
        int baseValue = cleanPrice.isEmpty() ? 0 : Integer.parseInt(cleanPrice);
        int finalSum  = baseValue * order.getQuantity();

        JLabel unitPrice = new JLabel(order.getPrice());
        unitPrice.setBounds(560, 52, 180, 22);
        unitPrice.setFont(new Font("Tahoma", Font.PLAIN, 13));
        unitPrice.setForeground(INK_LIGHT);
        unitPrice.setHorizontalAlignment(SwingConstants.RIGHT); card.add(unitPrice);

        JLabel totalPrice = new JLabel("\u20B1" + String.format("%,d", finalSum));
        totalPrice.setBounds(560, 76, 180, 28);
        totalPrice.setFont(new Font("Tahoma", Font.BOLD, 18));
        totalPrice.setForeground(ACCENT);
        totalPrice.setHorizontalAlignment(SwingConstants.RIGHT); card.add(totalPrice);

        dynamicCardContainer.add(card);
        cardYOffset += 146;

        if (cardYOffset > dynamicCardContainer.getPreferredSize().height)
            dynamicCardContainer.setPreferredSize(new Dimension(790, cardYOffset + 30));

        dynamicCardContainer.revalidate(); dynamicCardContainer.repaint();
    }

    public ArrayList<OrderItem> getOrderItems() { return orderItemsList; }

    private void saveOrderToDB(OrderItem order) {
        String cleanPrice = order.getPrice().replaceAll("[^\\d]", "");
        int baseValue  = cleanPrice.isEmpty() ? 0 : Integer.parseInt(cleanPrice);
        int totalPrice = baseValue * order.getQuantity();
        String username = (LoginGui.registeredUser == null || LoginGui.registeredUser.isEmpty())
                          ? "Walk-In Customer" : LoginGui.registeredUser;
        String sql = "INSERT INTO orders (username, product_name, variant, price, quantity, total_price, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username); ps.setString(2, order.getName());
            ps.setString(3, order.getVariant()); ps.setString(4, order.getPrice());
            ps.setInt(5, order.getQuantity()); ps.setInt(6, totalPrice); ps.setString(7, order.getImagePath());
            ps.executeUpdate();
        } catch (SQLException ex) { System.err.println("Failed to save order to DB: " + ex.getMessage()); }
    }

    private void setImage(JLabel label, String path) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(76, 88, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
        } else { label.setText("📱"); label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32)); }
    }
}
