package Component;

import Model.DBConnection;
import Model.OrderItem;
import java.awt.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParentForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color BG          = new Color(0xFAF7F2);
    private static final Color SIDEBAR_BG  = new Color(0x1A1A2E);
    private static final Color SIDEBAR_SEL = new Color(0xE8923A);
    private static final Color SIDEBAR_HOV = new Color(0x2A2A42);
    private static final Color ACCENT      = new Color(0xE8923A);
    private static final Color CARD        = Color.WHITE;
    private static final Color INK         = new Color(0x1A1A2E);
    private static final Color INK_LIGHT   = new Color(0x6B6B80);
    private static final Color BORDER      = new Color(0xE2DDD6);
    private static final Color SUCCESS     = new Color(0x2D8A5E);
    private static final Color WARNING     = new Color(0xC97A2A);
    private static final Color DANGER      = new Color(0xDC5A3C);
    private static final Color ADMIN_COLOR = new Color(0x7B5EA7);
    private static final Color USER_COLOR  = new Color(0x2D8A5E);

    private JPanel       cardPanel;
    private CardLayout   cardLayout;
    private Customers    customersPanelInstance;
    private Orders       ordersPanelInstance;
    private Product      productPanelInstance;
    private ReportsPanel reportsPanelInstance;
    private JList<String> list;

    private ArrayList<OrderItem> cartItemsList = new ArrayList<>();
    private CartPanel cartPanel;
    private JPanel homePanelInstance;

    private String sessionContact = null;
    private String sessionAddress = null;

    private static final String[] ADMIN_MENU_ITEMS = { "Products", "Cart", "Order History", "Inventory Stock", "Customers", "Reports", "Logout" };
    private static final String[] ADMIN_MENU_ICONS = { "🏠", "🛒", "📋", "📦", "👥", "📊", "⬅" };

    private static final String[] USER_MENU_ITEMS  = { "Products", "Cart", "Order History", "Logout" };
    private static final String[] USER_MENU_ICONS  = { "🏠", "🛒", "📋", "⬅" };

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try { new ParentForm().setVisible(true); }
            catch (Exception e) { e.printStackTrace(); }
        });
    }

    public ParentForm() {
        setTitle("Gadget Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(80, 40, 1000, 720);
        setResizable(false);
        getContentPane().setLayout(null);
        getContentPane().setBackground(BG);

        boolean isAdmin = "admin".equalsIgnoreCase(LoginGui.registeredRole);

        String[] menuItems = isAdmin ? ADMIN_MENU_ITEMS : USER_MENU_ITEMS;
        String[] menuIcons = isAdmin ? ADMIN_MENU_ICONS : USER_MENU_ICONS;

        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBounds(0, 0, 190, 720);
        getContentPane().add(sidebar);

        JPanel logoArea = new JPanel(null);
        logoArea.setBackground(new Color(0x12122A));
        logoArea.setBounds(0, 0, 190, 86);
        sidebar.add(logoArea);

        JLabel ico = new JLabel("📱");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        ico.setBounds(18, 10, 28, 28);
        logoArea.add(ico);

        JLabel storeName = new JLabel("GADGET STORE");
        storeName.setFont(new Font("Tahoma", Font.BOLD, 13));
        storeName.setForeground(Color.WHITE);
        storeName.setBounds(50, 8, 130, 20);
        logoArea.add(storeName);

        JLabel userLabel = new JLabel(LoginGui.registeredUser.isEmpty()
                ? "Walk-In Customer" : LoginGui.registeredUser);
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
        userLabel.setForeground(new Color(0x8888A0));
        userLabel.setBounds(50, 30, 130, 16);
        logoArea.add(userLabel);

        JPanel roleBadge = new JPanel(null);
        roleBadge.setBounds(50, 50, 80, 20);
        roleBadge.setBackground(isAdmin ? ADMIN_COLOR : USER_COLOR);
        roleBadge.setBorder(new LineBorder(isAdmin ? ADMIN_COLOR.darker() : USER_COLOR.darker(), 1));
        logoArea.add(roleBadge);

        JLabel roleBadgeText = new JLabel(isAdmin ? "⚙ Admin" : "👤 User", SwingConstants.CENTER);
        roleBadgeText.setFont(new Font("Tahoma", Font.BOLD, 9));
        roleBadgeText.setForeground(Color.WHITE);
        roleBadgeText.setBounds(0, 0, 80, 20);
        roleBadge.add(roleBadgeText);

        list = new JList<>(menuItems);
        list.setBounds(0, 86, 190, 634);
        list.setBackground(SIDEBAR_BG);
        list.setForeground(new Color(0xCCCCDD));
        list.setFont(new Font("Tahoma", Font.PLAIN, 13));
        list.setSelectionBackground(ACCENT);
        list.setSelectionForeground(Color.WHITE);
        list.setFixedCellHeight(52);
        list.setBorder(new EmptyBorder(8, 0, 0, 0));

        final String[] finalMenuIcons = menuIcons;
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object val,
                    int idx, boolean sel, boolean focus) {
                JPanel row = new JPanel(null);
                row.setPreferredSize(new Dimension(190, 52));
                row.setBackground(sel ? ACCENT : SIDEBAR_BG);

                JLabel iconLbl = new JLabel(idx < finalMenuIcons.length ? finalMenuIcons[idx] : "");
                iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                iconLbl.setBounds(18, 14, 24, 24);
                row.add(iconLbl);

                JLabel txtLbl = new JLabel(val.toString());
                txtLbl.setFont(new Font("Tahoma", sel ? Font.BOLD : Font.PLAIN, 13));
                txtLbl.setForeground(sel ? Color.WHITE : new Color(0xCCCCDD));
                txtLbl.setBounds(48, 16, 130, 20);
                row.add(txtLbl);

                if (sel) {
                    JPanel indicator = new JPanel();
                    indicator.setBackground(Color.WHITE);
                    indicator.setBounds(186, 16, 3, 20);
                    row.add(indicator);
                }
                return row;
            }
        });
        sidebar.add(list);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBounds(190, 0, 810, 720);
        cardPanel.setBackground(BG);
        getContentPane().add(cardPanel);

        customersPanelInstance = new Customers();
        ordersPanelInstance    = new Orders();
        productPanelInstance   = new Product();
        reportsPanelInstance   = new ReportsPanel(ordersPanelInstance);

        String activeSessionUser = (LoginGui.registeredUser == null || LoginGui.registeredUser.isEmpty())
                ? "Walk-In Customer" : LoginGui.registeredUser;

        customersPanelInstance.registerUserOnLogin(activeSessionUser);
        loadDeliveryProfileFromDB(activeSessionUser);

        cartPanel = new CartPanel(cartItemsList, customersPanelInstance, ordersPanelInstance, list, this);
        homePanelInstance = createHomePanel();

        cardPanel.add(homePanelInstance,      "Products");
        cardPanel.add(cartPanel,              "Cart");
        cardPanel.add(ordersPanelInstance,    "Order History");
        cardPanel.add(productPanelInstance,   "Inventory Stock");
        cardPanel.add(customersPanelInstance, "Customers");
        cardPanel.add(reportsPanelInstance,   "Reports");

        JPanel accessDenied = createAccessDeniedPanel();
        cardPanel.add(accessDenied, "AccessDenied");

        cartPanel.refresh();
        list.setSelectedIndex(0);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = list.getSelectedValue();
                if (selected == null) return;

                if (selected.equals("Logout")) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this, "Logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        LoginGui login = new LoginGui();
                        login.setVisible(true);
                    }
                    return;
                }

                if (!isAdmin) {
                    boolean allowed = selected.equals("Products") ||
                                      selected.equals("Cart") ||
                                      selected.equals("Order History");
                    if (!allowed) {
                        cardLayout.show(cardPanel, "AccessDenied");
                        return;
                    }
                }

                if (selected.equals("Cart"))            cartPanel.refresh();
                if (selected.equals("Inventory Stock")) productPanelInstance.refreshInventoryList();
                if (selected.equals("Customers"))       customersPanelInstance.refreshCustomerTable();
                if (selected.equals("Reports"))         reportsPanelInstance.generateReportIfReady();
                if (selected.equals("Products"))        refreshHomePanel();
                cardLayout.show(cardPanel, selected);
            }
        });
    }

    private JPanel createAccessDeniedPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        JPanel card = new JPanel(null);
        card.setBackground(CARD);
        card.setBorder(new LineBorder(BORDER, 1));
        card.setBounds(205, 200, 400, 240);
        panel.add(card);

        JLabel icon = new JLabel("🔒", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setBounds(0, 30, 400, 60);
        card.add(icon);

        JLabel title = new JLabel("Access Restricted", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setForeground(INK);
        title.setBounds(0, 100, 400, 28);
        card.add(title);

        JLabel sub = new JLabel("This section is for Admins only.", SwingConstants.CENTER);
        sub.setFont(new Font("Tahoma", Font.PLAIN, 13));
        sub.setForeground(INK_LIGHT);
        sub.setBounds(0, 132, 400, 20);
        card.add(sub);

        JLabel badge = new JLabel("Your role: User", SwingConstants.CENTER);
        badge.setFont(new Font("Tahoma", Font.BOLD, 11));
        badge.setForeground(USER_COLOR);
        badge.setBounds(140, 162, 120, 20);
        badge.setOpaque(true);
        badge.setBackground(new Color(0xE6F5EE));
        badge.setBorder(new LineBorder(new Color(0xB8DFCD), 1));
        card.add(badge);

        return panel;
    }

    private void loadDeliveryProfileFromDB(String username) {
        if (username.equals("Walk-In Customer")) {
            sessionContact = "Not Provided"; sessionAddress = "Not Provided"; return;
        }
        String sql = "SELECT MAX(contact) AS contact, MAX(address) AS address FROM customers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbContact = rs.getString("contact");
                    String dbAddress = rs.getString("address");
                    boolean contactReal = dbContact != null && !dbContact.trim().isEmpty() && !dbContact.equalsIgnoreCase("Not Provided");
                    boolean addressReal = dbAddress != null && !dbAddress.trim().isEmpty() && !dbAddress.equalsIgnoreCase("Not Provided");
                    if (contactReal && addressReal) { sessionContact = dbContact.trim(); sessionAddress = dbAddress.trim(); }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Could not load delivery profile from DB: " + ex.getMessage());
        }
    }

    public String getSessionContact() { return sessionContact; }
    public String getSessionAddress() { return sessionAddress; }

    public void ensureDeliveryProfile() {
        if (sessionContact != null) return;
        String activeUser = (LoginGui.registeredUser == null || LoginGui.registeredUser.isEmpty())
                ? "Walk-In Customer" : LoginGui.registeredUser;
        if (activeUser.equals("Walk-In Customer")) {
            sessionContact = "Not Provided"; sessionAddress = "Not Provided"; return;
        }
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        Object[] fields = { "Contact Number:", contactField, "Shipping / Billing Address:", addressField };
        int opt = JOptionPane.showConfirmDialog(this, fields,
            "Delivery Profile — saved permanently to your account",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            sessionContact = contactField.getText().trim().isEmpty() ? "Not Provided" : contactField.getText().trim();
            sessionAddress = addressField.getText().trim().isEmpty() ? "Not Provided" : addressField.getText().trim();
        } else { sessionContact = "Not Provided"; sessionAddress = "Not Provided"; }
        customersPanelInstance.updateCustomerProfile(activeUser, sessionContact, sessionAddress);
    }

    public void refreshHomePanel() {
        cardPanel.remove(homePanelInstance);
        homePanelInstance = createHomePanel();
        cardPanel.add(homePanelInstance, "Products");
        cardLayout.show(cardPanel, "Products");
        list.setSelectedValue("Products", false);
    }

    public void addToCart(OrderItem item) {
        for (OrderItem existing : cartItemsList) {
            if (existing.getName().equals(item.getName()) && existing.getVariant().equals(item.getVariant())) {
                int maxStock = DBConnection.getStock(item.getName());
                if (existing.getQuantity() + item.getQuantity() <= maxStock)
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                else existing.setQuantity(maxStock);
                return;
            }
        }
        cartItemsList.add(item);
    }

    private JPanel createHomePanel() {
        JPanel home = new JPanel(null);
        home.setBackground(BG);

        JPanel topBar = new JPanel(null);
        topBar.setBackground(CARD);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        topBar.setBounds(0, 0, 810, 64);
        home.add(topBar);

        JLabel pageTitle = new JLabel("Products");
        pageTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        pageTitle.setForeground(INK);
        pageTitle.setBounds(28, 16, 300, 32);
        topBar.add(pageTitle);

        JLabel lblDateTime = new JLabel();
        lblDateTime.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblDateTime.setForeground(INK_LIGHT);
        lblDateTime.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDateTime.setBounds(360, 18, 290, 24);
        topBar.add(lblDateTime);
        startDateTimeClock(lblDateTime);

        JButton btnCartMenu = new JButton("🛒  View Cart");
        btnCartMenu.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnCartMenu.setForeground(Color.WHITE);
        btnCartMenu.setBackground(ACCENT);
        btnCartMenu.setBorder(new EmptyBorder(0, 16, 0, 16));
        btnCartMenu.setFocusable(false);
        btnCartMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCartMenu.setBounds(668, 16, 120, 32);
        btnCartMenu.addActionListener(e -> list.setSelectedValue("Cart", true));
        topBar.add(btnCartMenu);

        int cardW = 340, cardH = 290;
        int col1X = 28, col2X = 28 + cardW + 20;
        int row1Y = 84, row2Y = 84 + cardH + 20;

        home.add(buildProductCard(col1X, row1Y, "Samsung Galaxy S25 Ultra", "\u20B183,190", "12GB + 1TB",
            "/LOGIN/img/samsung-galaxy-s25-ultra-sm-s938-1.jpg",
            "• Display: 6.9 inches Dynamic AMOLED 2X\n• Processor: Snapdragon 8 Elite\n" +
            "• Memory: 12GB RAM / 1TB Storage\n• Camera: 200 MP Quad Camera Setup\n• Battery: 5000 mAh with 45W Fast Charging"));

        home.add(buildProductCard(col2X, row1Y, "iPhone 17 Pro Max", "\u20B186,990", "12GB + 1TB",
            "/LOGIN/img/apple-iphone-17-pro-max-nano-sim.jpg",
            "• Display: 6.9 inches Super Retina XDR OLED\n• Processor: Apple A19 Pro\n" +
            "• Memory: 12GB RAM / 1TB Storage\n• Camera: 48 MP Triple Camera Setup\n• Battery: 4823 mAh with 40W Fast Charging"));

        home.add(buildProductCard(col1X, row2Y, "Vivo X300 Ultra", "\u20B1143,300", "16GB + 1TB",
            "/LOGIN/img/vivo-x300-ultra-2.jpg",
            "• Display: 6.9 inches AMOLED\n• Processor: Snapdragon 8 Elite Gen 5\n" +
            "• Memory: 16GB RAM / 1TB Storage\n• Camera: 200 MP Triple Camera Setup\n• Battery: 6600 mAh with 100W Fast Charging"));

        home.add(buildProductCard(col2X, row2Y, "Xiaomi 17 Pro Max Leica Edition", "\u20B199,999", "16GB + 1TB",
            "/LOGIN/img/xiaomi-17-pro-max-3.jpg",
            "• Display: 6.9 inches AMOLED 2X\n• Processor: Snapdragon 8 Elite Gen 5\n" +
            "• Memory: 16GB RAM / 1TB Storage\n• Camera: 50 MP Triple Camera Setup\n• Battery: 7500 mAh with 100W Fast Charging"));

        return home;
    }

    private JPanel buildProductCard(int x, int y, String name, String price,
                                     String variant, String imagePath, String details) {
        JPanel card = new JPanel(null);
        card.setBounds(x, y, 340, 280);
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(0, 0, 0, 0)));

        JPanel imgArea = new JPanel(new GridBagLayout());
        imgArea.setBackground(new Color(0xF7F4EF));
        imgArea.setBounds(0, 0, 340, 160);
        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setImageScaled(imgLabel, imagePath, 120, 140);
        imgArea.add(imgLabel);
        card.add(imgArea);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        nameLabel.setForeground(INK);
        nameLabel.setBounds(16, 170, 308, 20);
        card.add(nameLabel);

        JLabel varLabel = new JLabel(variant);
        varLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        varLabel.setForeground(INK_LIGHT);
        varLabel.setBounds(16, 192, 200, 16);
        card.add(varLabel);

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        priceLabel.setForeground(ACCENT);
        priceLabel.setBounds(16, 210, 180, 26);
        card.add(priceLabel);

        int currentStock = DBConnection.getStock(name);
        JLabel stockLabel = new JLabel();
        stockLabel.setBounds(230, 213, 94, 22);
        stockLabel.setFont(new Font("Tahoma", Font.BOLD, 10));
        stockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stockLabel.setOpaque(true);
        updateStockBadge(stockLabel, currentStock);
        card.add(stockLabel);

        JPanel soldOutOverlay = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 140));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose(); super.paintComponent(g);
            }
        };
        soldOutOverlay.setBounds(0, 0, 340, 280);
        soldOutOverlay.setOpaque(false);
        JLabel soldOutText = new JLabel("SOLD OUT");
        soldOutText.setFont(new Font("Tahoma", Font.BOLD, 20));
        soldOutText.setForeground(Color.WHITE);
        soldOutOverlay.add(soldOutText);
        soldOutOverlay.setVisible(currentStock <= 0);
        card.add(soldOutOverlay);
        card.setCursor(currentStock > 0 ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int stock = DBConnection.getStock(name);
                if (stock <= 0) {
                    JOptionPane.showMessageDialog(ParentForm.this, name + " is currently SOLD OUT.", "Sold Out", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                openProductPage(name, price, variant, imagePath, details, stockLabel, soldOutOverlay, card);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (DBConnection.getStock(name) > 0)
                    card.setBorder(new CompoundBorder(new LineBorder(ACCENT, 2), new EmptyBorder(0, 0, 0, 0)));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(0, 0, 0, 0)));
            }
        });
        return card;
    }

    private void updateStockBadge(JLabel lbl, int stock) {
        if (stock <= 0) {
            lbl.setText("SOLD OUT"); lbl.setBackground(new Color(0xDC5A3C)); lbl.setForeground(Color.WHITE);
        } else if (stock <= 3) {
            lbl.setText("Low: " + stock); lbl.setBackground(new Color(0xF5A623)); lbl.setForeground(Color.WHITE);
        } else {
            lbl.setText("In Stock: " + stock); lbl.setBackground(new Color(0xD4EDDA)); lbl.setForeground(new Color(0x2D8A5E));
        }
    }

    private void openProductPage(String productName, String price, String variant,
                                  String imagePath, String specifications,
                                  JLabel stockLabel, JPanel soldOutOverlay, JPanel parentCard) {
        JFrame detailFrame = new JFrame(productName);
        detailFrame.setSize(480, 580);
        detailFrame.setLocationRelativeTo(this);
        detailFrame.getContentPane().setLayout(null);
        detailFrame.getContentPane().setBackground(BG);

        JPanel imgPanel = new JPanel(new GridBagLayout());
        imgPanel.setBackground(new Color(0xF7F4EF));
        imgPanel.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        imgPanel.setBounds(0, 0, 480, 200);
        JLabel mainImg = new JLabel();
        mainImg.setHorizontalAlignment(SwingConstants.CENTER);
        setImageScaled(mainImg, imagePath, 140, 170);
        imgPanel.add(mainImg);
        detailFrame.add(imgPanel);

        JPanel detCard = new JPanel(null);
        detCard.setBackground(CARD);
        detCard.setBorder(new LineBorder(BORDER, 1));
        detCard.setBounds(20, 215, 440, 315);
        detailFrame.add(detCard);

        JLabel lblTitle2 = new JLabel(productName);
        lblTitle2.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTitle2.setForeground(INK);
        lblTitle2.setBounds(18, 16, 400, 26);
        detCard.add(lblTitle2);

        JLabel lblVariant = new JLabel(variant);
        lblVariant.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblVariant.setForeground(INK_LIGHT);
        lblVariant.setBounds(18, 44, 300, 18);
        detCard.add(lblVariant);

        JLabel lblPrice2 = new JLabel(price);
        lblPrice2.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblPrice2.setForeground(ACCENT);
        lblPrice2.setBounds(18, 66, 220, 30);
        detCard.add(lblPrice2);

        int currentStock = DBConnection.getStock(productName);
        JLabel detailStock = new JLabel();
        detailStock.setBounds(260, 72, 160, 22);
        detailStock.setFont(new Font("Tahoma", Font.BOLD, 12));
        updateDetailStockLabel(detailStock, currentStock);
        detCard.add(detailStock);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBounds(18, 102, 404, 1);
        detCard.add(sep);

        JTextArea txtSpecs = new JTextArea(specifications);
        txtSpecs.setFont(new Font("Tahoma", Font.PLAIN, 12));
        txtSpecs.setBackground(CARD); txtSpecs.setEditable(false);
        txtSpecs.setLineWrap(true); txtSpecs.setWrapStyleWord(true);
        txtSpecs.setBounds(18, 110, 404, 100);
        detCard.add(txtSpecs);

        JButton btnAddToCart = new JButton("Add to Cart");
        btnAddToCart.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnAddToCart.setBounds(18, 222, 185, 40);
        btnAddToCart.setBackground(CARD); btnAddToCart.setForeground(INK);
        btnAddToCart.setBorder(new LineBorder(BORDER, 1));
        btnAddToCart.setFocusable(false); btnAddToCart.setEnabled(currentStock > 0);
        if (currentStock > 0) {
            btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAddToCart.addActionListener(e ->
                showAddToCartQuantityDialog(detailFrame, productName, price, variant, imagePath));
        }
        detCard.add(btnAddToCart);

        JButton btnOrder = new JButton("Buy Now") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? (getModel().isPressed() ? new Color(0xC97A2A) : ACCENT) : new Color(0xCCCCCC));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnOrder.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnOrder.setForeground(Color.WHITE); btnOrder.setContentAreaFilled(false);
        btnOrder.setBorderPainted(false); btnOrder.setFocusable(false);
        btnOrder.setEnabled(currentStock > 0); btnOrder.setBounds(220, 222, 185, 40);
        detCard.add(btnOrder);
        btnOrder.addActionListener(e -> {
            int stockNow = DBConnection.getStock(productName);
            if (stockNow <= 0) {
                JOptionPane.showMessageDialog(detailFrame, productName + " is now SOLD OUT.", "Sold Out", JOptionPane.WARNING_MESSAGE);
                btnOrder.setEnabled(false); btnAddToCart.setEnabled(false); return;
            }
            detailFrame.dispose();
            showCheckoutDialog(productName, price, variant, imagePath, stockLabel, detailStock, soldOutOverlay, parentCard);
        });

        detailFrame.setVisible(true);
    }

    private void showAddToCartQuantityDialog(JFrame parentFrame, String name, String price,
                                              String variant, String imagePath) {
        JDialog qtyDialog = new JDialog(parentFrame, "Select Quantity", true);
        qtyDialog.setSize(380, 240);
        qtyDialog.setLocationRelativeTo(parentFrame);
        qtyDialog.getContentPane().setBackground(CARD);
        qtyDialog.setLayout(null);

        JLabel lblTitle = new JLabel("Add to Cart", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setForeground(INK); lblTitle.setBounds(20, 18, 335, 24); qtyDialog.add(lblTitle);

        JLabel lblItemName = new JLabel(name, SwingConstants.CENTER);
        lblItemName.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblItemName.setForeground(INK_LIGHT); lblItemName.setBounds(20, 44, 335, 18); qtyDialog.add(lblItemName);

        final int[] selectQty = {1};
        int maxAvailable = DBConnection.getStock(name);

        JLabel lblQtyDisplay = new JLabel("Quantity:  " + selectQty[0], SwingConstants.CENTER);
        lblQtyDisplay.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblQtyDisplay.setBounds(100, 84, 170, 34); qtyDialog.add(lblQtyDisplay);

        JButton btnMinus = new JButton("−");
        btnMinus.setFont(new Font("Tahoma", Font.BOLD, 16)); btnMinus.setBounds(52, 84, 44, 34);
        btnMinus.setFocusable(false); btnMinus.setBackground(new Color(0xF7F4EF));
        btnMinus.setBorder(new LineBorder(BORDER, 1));
        btnMinus.addActionListener(e -> {
            if (selectQty[0] > 1) { selectQty[0]--; lblQtyDisplay.setText("Quantity:  " + selectQty[0]); }
        });
        qtyDialog.add(btnMinus);

        JButton btnPlus = new JButton("+");
        btnPlus.setFont(new Font("Tahoma", Font.BOLD, 16)); btnPlus.setBounds(278, 84, 44, 34);
        btnPlus.setFocusable(false); btnPlus.setBackground(new Color(0xF7F4EF));
        btnPlus.setBorder(new LineBorder(BORDER, 1));
        btnPlus.addActionListener(e -> {
            if (selectQty[0] < maxAvailable) { selectQty[0]++; lblQtyDisplay.setText("Quantity:  " + selectQty[0]); }
            else JOptionPane.showMessageDialog(qtyDialog, "Cannot select more than " + maxAvailable + " units.", "Stock Limit", JOptionPane.WARNING_MESSAGE);
        });
        qtyDialog.add(btnPlus);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btnCancel.setBounds(50, 150, 120, 34); btnCancel.setBackground(CARD);
        btnCancel.setBorder(new LineBorder(BORDER, 1)); btnCancel.setFocusable(false);
        btnCancel.addActionListener(e -> qtyDialog.dispose()); qtyDialog.add(btnCancel);

        JButton btnOk = new JButton("Add to Cart") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? SUCCESS.darker() : SUCCESS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnOk.setFont(new Font("Tahoma", Font.BOLD, 12)); btnOk.setForeground(Color.WHITE);
        btnOk.setContentAreaFilled(false); btnOk.setBorderPainted(false);
        btnOk.setBounds(195, 150, 120, 34); btnOk.setFocusable(false);
        btnOk.addActionListener(e -> {
            addToCart(new OrderItem(name, price, variant, imagePath, selectQty[0]));
            cartPanel.refresh(); qtyDialog.dispose();
            JOptionPane.showMessageDialog(parentFrame,
                selectQty[0] + "x " + name + " added to your cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        qtyDialog.add(btnOk);
        qtyDialog.setVisible(true);
    }

    private void updateDetailStockLabel(JLabel lbl, int stock) {
        if (stock <= 0) { lbl.setText("⚠ SOLD OUT"); lbl.setForeground(DANGER); }
        else if (stock <= 3) { lbl.setText("⚠ Only " + stock + " left!"); lbl.setForeground(WARNING); }
        else { lbl.setText("✔ In Stock: " + stock); lbl.setForeground(SUCCESS); }
    }

    private void showCheckoutDialog(String name, String price, String variant, String imagePath,
                                     JLabel cardStockLabel, JLabel detailStockLabel,
                                     JPanel soldOutOverlay, JPanel parentCard) {
        JDialog checkoutDialog = new JDialog(this, "Checkout", true);
        checkoutDialog.setLayout(null);
        checkoutDialog.setSize(500, 440);
        checkoutDialog.setLocationRelativeTo(this);
        checkoutDialog.getContentPane().setBackground(CARD);

        JPanel dHeader = new JPanel(null);
        dHeader.setBackground(INK);
        dHeader.setBounds(0, 0, 500, 52); checkoutDialog.add(dHeader);
        JLabel dTitle = new JLabel("Confirm Order");
        dTitle.setFont(new Font("Tahoma", Font.BOLD, 16)); dTitle.setForeground(Color.WHITE);
        dTitle.setBounds(20, 14, 300, 24); dHeader.add(dTitle);

        JLabel popupImg = new JLabel();
        popupImg.setBounds(20, 68, 130, 140);
        popupImg.setHorizontalAlignment(SwingConstants.CENTER);
        popupImg.setBackground(new Color(0xF7F4EF)); popupImg.setOpaque(true);
        setImageScaled(popupImg, imagePath, 110, 130); checkoutDialog.add(popupImg);

        JLabel popupName = new JLabel(name);
        popupName.setBounds(165, 76, 310, 26);
        popupName.setFont(new Font("Tahoma", Font.BOLD, 16)); checkoutDialog.add(popupName);

        JLabel popupSpecs = new JLabel(variant);
        popupSpecs.setBounds(165, 104, 200, 20);
        popupSpecs.setFont(new Font("Tahoma", Font.PLAIN, 13));
        popupSpecs.setForeground(INK_LIGHT); checkoutDialog.add(popupSpecs);

        int stockNow = DBConnection.getStock(name);
        JLabel checkoutStockLabel = new JLabel();
        checkoutStockLabel.setBounds(165, 128, 270, 20);
        checkoutStockLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        updateDetailStockLabel(checkoutStockLabel, stockNow); checkoutDialog.add(checkoutStockLabel);

        final int[] quantity = {1};
        JLabel qtyLabel = new JLabel("Quantity:  " + quantity[0]);
        qtyLabel.setBounds(165, 155, 120, 30);
        qtyLabel.setFont(new Font("Tahoma", Font.PLAIN, 15)); checkoutDialog.add(qtyLabel);

        JButton btnPlus = new JButton("+"); btnPlus.setBounds(290, 156, 38, 28); btnPlus.setFocusable(false);
        btnPlus.setBackground(new Color(0xF7F4EF)); btnPlus.setBorder(new LineBorder(BORDER, 1));
        checkoutDialog.add(btnPlus);
        JButton btnMinus2 = new JButton("−"); btnMinus2.setBounds(332, 156, 38, 28); btnMinus2.setFocusable(false);
        btnMinus2.setBackground(new Color(0xF7F4EF)); btnMinus2.setBorder(new LineBorder(BORDER, 1));
        checkoutDialog.add(btnMinus2);

        btnPlus.addActionListener(e -> {
            int available = DBConnection.getStock(name);
            if (quantity[0] < available) { quantity[0]++; qtyLabel.setText("Quantity:  " + quantity[0]); }
            else JOptionPane.showMessageDialog(checkoutDialog, "Only " + available + " unit(s) available.", "Stock Limit", JOptionPane.INFORMATION_MESSAGE);
        });
        btnMinus2.addActionListener(e -> {
            if (quantity[0] > 1) { quantity[0]--; qtyLabel.setText("Quantity:  " + quantity[0]); }
        });

        JSeparator sep = new JSeparator(); sep.setForeground(BORDER);
        sep.setBounds(20, 218, 460, 1); checkoutDialog.add(sep);

        String displayedUser = (LoginGui.registeredUser == null || LoginGui.registeredUser.isEmpty())
                ? "Walk-In Customer" : LoginGui.registeredUser;
        JLabel userSep = new JLabel("Buyer: " + displayedUser, SwingConstants.CENTER);
        userSep.setBounds(20, 226, 460, 26); userSep.setFont(new Font("Tahoma", Font.BOLD, 13));
        userSep.setForeground(INK_LIGHT); checkoutDialog.add(userSep);

        JButton btnBuyNow = new JButton("Place Order") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0xC97A2A) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnBuyNow.setFont(new Font("Tahoma", Font.BOLD, 16)); btnBuyNow.setForeground(Color.WHITE);
        btnBuyNow.setContentAreaFilled(false); btnBuyNow.setBorderPainted(false);
        btnBuyNow.setBounds(140, 270, 220, 48); btnBuyNow.setFocusable(false);
        checkoutDialog.add(btnBuyNow);

        btnBuyNow.addActionListener(e -> {
            int qty = quantity[0];
            boolean success = DBConnection.decreaseStock(name, qty);
            if (!success) {
                int remaining = DBConnection.getStock(name);
                if (remaining <= 0) JOptionPane.showMessageDialog(checkoutDialog, name + " is now SOLD OUT!", "Sold Out", JOptionPane.ERROR_MESSAGE);
                else {
                    JOptionPane.showMessageDialog(checkoutDialog, "Not enough stock! Only " + remaining + " left.", "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                    quantity[0] = remaining; qtyLabel.setText("Quantity:  " + quantity[0]);
                }
                updateDetailStockLabel(checkoutStockLabel, DBConnection.getStock(name)); return;
            }
            String activeUser = (LoginGui.registeredUser == null || LoginGui.registeredUser.isEmpty())
                    ? "Walk-In Customer" : LoginGui.registeredUser;
            ensureDeliveryProfile();
            int newStock = DBConnection.getStock(name);
            updateStockBadge(cardStockLabel, newStock);
            if (newStock <= 0) { soldOutOverlay.setVisible(true); parentCard.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); parentCard.repaint(); }
            customersPanelInstance.updateCustomerProfile(activeUser, sessionContact, sessionAddress);
            ordersPanelInstance.addDynamicOrderCard(new OrderItem(name, price, variant, imagePath, qty));
            checkoutDialog.dispose(); refreshHomePanel();
            JOptionPane.showMessageDialog(this, "Order placed successfully!\nDelivery profile saved.");
        });
        checkoutDialog.setVisible(true);
    }

    private void setImageScaled(JLabel label, String path, int width, int height) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
        } else { label.setText("No Image"); }
    }

    private void startDateTimeClock(JLabel lblDateTime) {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd yyyy | hh:mm:ss a");
            lblDateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }
}