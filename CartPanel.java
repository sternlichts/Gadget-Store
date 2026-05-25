package Component;

import Model.DBConnection;
import Model.OrderItem;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

public class CartPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG        = new Color(0xFAF7F2);
    private static final Color CARD      = Color.WHITE;
    private static final Color INK       = new Color(0x1A1A2E);
    private static final Color INK_LIGHT = new Color(0x6B6B80);
    private static final Color ACCENT    = new Color(0xE8923A);
    private static final Color BORDER    = new Color(0xE2DDD6);
    private static final Color SUCCESS   = new Color(0x2D8A5E);
    private static final Color DANGER    = new Color(0xDC5A3C);

    private ArrayList<OrderItem> cartItemsList;
    private Customers customersPanelInstance;
    private Orders ordersPanelInstance;
    private JList<String> navigationList;
    private ParentForm parentForm;

    private JPanel itemsContainer;
    private JScrollPane scrollPane;
    private JLabel lblTotalPrice;
    private JButton btnCheckout;

    private JLabel lblHeaderProduct;
    private JLabel lblHeaderDetails;
    private JLabel lblHeaderQuantity;
    private JLabel lblHeaderAction;
    private JPanel emptyCartPanel;

    public CartPanel(ArrayList<OrderItem> cartItemsList, Customers customers, Orders orders,
            JList<String> navigationList, ParentForm parentForm) {
        this.cartItemsList = cartItemsList;
        this.customersPanelInstance = customers;
        this.ordersPanelInstance = orders;
        this.navigationList = navigationList;
        this.parentForm = parentForm;

        setBackground(BG);
        setLayout(null);

        JPanel topBar = new JPanel(null);
        topBar.setBackground(CARD);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        topBar.setBounds(0, 0, 810, 64);
        add(topBar);

        JLabel lblTitle = new JLabel("My Cart");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitle.setForeground(INK);
        lblTitle.setBounds(28, 16, 200, 32); topBar.add(lblTitle);

        JPanel headerRow = new JPanel(null);
        headerRow.setBackground(new Color(0xF0EDE7));
        headerRow.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        headerRow.setBounds(0, 64, 810, 36); add(headerRow);

        lblHeaderProduct  = makeColHeader("Product",  28,  0, 180);
        lblHeaderDetails  = makeColHeader("Details",  220, 0, 200);
        lblHeaderQuantity = makeColHeader("Quantity", 440, 0, 120);
        lblHeaderAction   = makeColHeader("Remove",   580, 0, 80);
        headerRow.add(lblHeaderProduct); headerRow.add(lblHeaderDetails);
        headerRow.add(lblHeaderQuantity); headerRow.add(lblHeaderAction);

        itemsContainer = new JPanel();
        itemsContainer.setLayout(null);
        itemsContainer.setOpaque(false);

        scrollPane = new JScrollPane(itemsContainer);
        scrollPane.setBounds(0, 100, 810, 540);
        scrollPane.setBorder(null); scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane);

        JPanel bottomBar = new JPanel(null);
        bottomBar.setBackground(CARD);
        bottomBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        bottomBar.setBounds(0, 620, 810, 60); add(bottomBar);

        lblTotalPrice = new JLabel("Total: ₱0");
        lblTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTotalPrice.setForeground(INK);
        lblTotalPrice.setBounds(28, 14, 320, 32); bottomBar.add(lblTotalPrice);

        btnCheckout = new JButton("Checkout →") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0xC97A2A) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnCheckout.setFont(new Font("Tahoma", Font.BOLD, 15));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setContentAreaFilled(false); btnCheckout.setBorderPainted(false);
        btnCheckout.setFocusable(false); btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.setBounds(620, 12, 158, 36); bottomBar.add(btnCheckout);
        btnCheckout.addActionListener(e -> performCartCheckout());

        buildEmptyCartPanel();
    }

    private JLabel makeColHeader(String text, int x, int y, int w) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 11));
        l.setForeground(INK_LIGHT); l.setBounds(x, y + 10, w, 16);
        return l;
    }

    private void buildEmptyCartPanel() {
        emptyCartPanel = new JPanel(null);
        emptyCartPanel.setBounds(0, 100, 810, 540);
        emptyCartPanel.setOpaque(false);
        emptyCartPanel.setVisible(false);

        JLabel lblIcon = new JLabel();
        lblIcon.setBounds(310, 120, 190, 120);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        setImageScaled(lblIcon, "/LOGIN/empty-cart.png", 130, 110);
        emptyCartPanel.add(lblIcon);

        JLabel lblStatus = new JLabel("Your cart is empty", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblStatus.setForeground(INK);
        lblStatus.setBounds(130, 258, 550, 40); emptyCartPanel.add(lblStatus);

        JLabel lblSub = new JLabel("Browse our products and add items to your cart", SwingConstants.CENTER);
        lblSub.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblSub.setForeground(INK_LIGHT);
        lblSub.setBounds(130, 298, 550, 22); emptyCartPanel.add(lblSub);

        JButton btnShop = new JButton("Browse Products") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0xC97A2A) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnShop.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnShop.setForeground(Color.WHITE); btnShop.setContentAreaFilled(false);
        btnShop.setBorderPainted(false); btnShop.setFocusable(false);
        btnShop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShop.setBounds(290, 338, 230, 44); emptyCartPanel.add(btnShop);
        btnShop.addActionListener(e -> navigationList.setSelectedIndex(0));
        add(emptyCartPanel);
    }

    public void refresh() {
        itemsContainer.removeAll();
        if (cartItemsList.isEmpty()) {
            toggleCartVisibility(false); emptyCartPanel.setVisible(true);
            revalidate(); repaint(); return;
        }
        toggleCartVisibility(true); emptyCartPanel.setVisible(false);
        int currentY = 0; int rowHeight = 105; long totalPrice = 0;
        for (OrderItem item : cartItemsList) {
            totalPrice += cleanPriceString(item.getPrice()) * item.getQuantity();
            buildRow(item, currentY); currentY += rowHeight;
        }
        itemsContainer.setPreferredSize(new Dimension(790, currentY));
        lblTotalPrice.setText(String.format("Total:  \u20B1%,d", totalPrice));
        itemsContainer.revalidate(); itemsContainer.repaint();
    }

    private void buildRow(OrderItem item, int baseY) {

        JPanel row = new JPanel(null);
        row.setBounds(0, baseY, 810, 100);
        row.setBackground(CARD);
        row.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        itemsContainer.add(row);

        JLabel imgLabel = new JLabel();
        imgLabel.setBounds(12, 10, 80, 80);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setBackground(new Color(0xF7F4EF)); imgLabel.setOpaque(true);
        imgLabel.setBorder(new LineBorder(BORDER, 1));
        setImageScaled(imgLabel, item.getImagePath(), 65, 72); row.add(imgLabel);

        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        nameLabel.setForeground(INK);
        nameLabel.setBounds(106, 22, 320, 20); row.add(nameLabel);

        JLabel varLabel = new JLabel(item.getVariant() + "  ·  " + item.getPrice());
        varLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        varLabel.setForeground(INK_LIGHT);
        varLabel.setBounds(106, 44, 320, 16); row.add(varLabel);

        long itemTotal = cleanPriceString(item.getPrice()) * item.getQuantity();
        JLabel itemTotalLabel = new JLabel(String.format("\u20B1%,d", itemTotal));
        itemTotalLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
        itemTotalLabel.setForeground(ACCENT);
        itemTotalLabel.setBounds(106, 62, 200, 20); row.add(itemTotalLabel);

        JPanel qtyPanel = new JPanel(null);
        qtyPanel.setBounds(436, 34, 96, 30);
        qtyPanel.setBorder(new LineBorder(BORDER, 1)); qtyPanel.setBackground(CARD);

        JButton btnMinus = createQtyButton("−", 0, 0);
        JTextField txtQty = new JTextField(String.valueOf(item.getQuantity()));
        txtQty.setFont(new Font("Tahoma", Font.BOLD, 12));
        txtQty.setHorizontalAlignment(JTextField.CENTER); txtQty.setEditable(false);
        txtQty.setFocusable(false); txtQty.setBackground(CARD);
        txtQty.setBorder(new MatteBorder(0, 1, 0, 1, BORDER)); txtQty.setBounds(30, 0, 36, 30);
        JButton btnPlus = createQtyButton("+", 66, 0);

        btnMinus.addActionListener(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                txtQty.setText(String.valueOf(item.getQuantity()));
                recalculateTotal(); itemsContainer.repaint();
            } else JOptionPane.showMessageDialog(this, "Quantity cannot be less than 1.");
        });
        btnPlus.addActionListener(e -> {
            int maxStock = DBConnection.getStock(item.getName());
            if (item.getQuantity() < maxStock) {
                item.setQuantity(item.getQuantity() + 1);
                txtQty.setText(String.valueOf(item.getQuantity()));
                recalculateTotal(); itemsContainer.repaint();
            } else JOptionPane.showMessageDialog(this, "Maximum available stock reached.");
        });

        qtyPanel.add(btnMinus); qtyPanel.add(txtQty); qtyPanel.add(btnPlus);
        row.add(qtyPanel);

        JButton btnRemove = new JButton("🗑");
        btnRemove.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btnRemove.setBounds(570, 32, 40, 34);
        btnRemove.setBackground(new Color(0xFFF0EE)); btnRemove.setForeground(DANGER);
        btnRemove.setBorder(new LineBorder(new Color(0xF5C6C0), 1));
        btnRemove.setFocusable(false); btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemove.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Remove " + item.getName() + " from cart?", "Remove Item", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) { cartItemsList.remove(item); refresh(); }
        });
        row.add(btnRemove);
    }

    private void recalculateTotal() {
        long total = 0;
        for (OrderItem item : cartItemsList) total += cleanPriceString(item.getPrice()) * item.getQuantity();
        lblTotalPrice.setText(String.format("Total:  \u20B1%,d", total));
    }

    private void toggleCartVisibility(boolean isVisible) {
        scrollPane.setVisible(isVisible); lblTotalPrice.setVisible(isVisible);
        btnCheckout.setVisible(isVisible);
        lblHeaderProduct.setVisible(isVisible); lblHeaderDetails.setVisible(isVisible);
        lblHeaderQuantity.setVisible(isVisible); lblHeaderAction.setVisible(isVisible);
    }

    private void performCartCheckout() {
        if (cartItemsList.isEmpty()) return;
        for (OrderItem item : cartItemsList) {
            int available = DBConnection.getStock(item.getName());
            if (item.getQuantity() > available) {
                JOptionPane.showMessageDialog(this,
                    "'" + item.getName() + "' only has " + available + " unit(s) remaining.\nPlease adjust the quantity before checking out.",
                    "Insufficient Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        parentForm.ensureDeliveryProfile();
        String activeUser = (LoginGui.registeredUser == null || LoginGui.registeredUser.isEmpty())
                ? "Walk-In Customer" : LoginGui.registeredUser;
        boolean anyFailed = false;
        for (OrderItem item : cartItemsList) {
            boolean ok = DBConnection.decreaseStock(item.getName(), item.getQuantity());
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                    "Stock for '" + item.getName() + "' changed during checkout. Item skipped.",
                    "Stock Error", JOptionPane.WARNING_MESSAGE);
                anyFailed = true; continue;
            }
            ordersPanelInstance.addDynamicOrderCard(item);
        }
        customersPanelInstance.updateCustomerProfile(activeUser, parentForm.getSessionContact(), parentForm.getSessionAddress());
        cartItemsList.clear(); refresh(); parentForm.refreshHomePanel();
        String message = anyFailed
            ? "Some items were skipped due to stock changes.\nThe rest were ordered successfully!"
            : "Order placed successfully!\nDelivery profile saved.";
        JOptionPane.showMessageDialog(this, message, "Checkout Complete", JOptionPane.INFORMATION_MESSAGE);
        navigationList.setSelectedValue("Order History", true);
    }

    private JButton createQtyButton(String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 30, 30); btn.setFont(new Font("Tahoma", Font.PLAIN, 13));
        btn.setFocusable(false); btn.setBackground(CARD); btn.setBorder(null);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); return btn;
    }

    private long cleanPriceString(String priceStr) {
        String clean = priceStr.replaceAll("[^\u20B10-9]", "").replace("\u20B1", "");
        return clean.isEmpty() ? 0 : Long.parseLong(clean);
    }

    private void setImageScaled(JLabel label, String path, int width, int height) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img)); label.setText("");
        } else {
            label.setIcon(null); label.setText("🛒");
            label.setFont(new Font("Tahoma", Font.PLAIN, 48)); label.setForeground(new Color(80, 80, 80));
        }
    }
}