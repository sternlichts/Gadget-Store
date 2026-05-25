package Component;

import Model.DBConnection;
import Model.OrderItem;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

public class Product extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG        = new Color(0xFAF7F2);
    private static final Color CARD      = Color.WHITE;
    private static final Color INK       = new Color(0x1A1A2E);
    private static final Color INK_LIGHT = new Color(0x6B6B80);
    private static final Color ACCENT    = new Color(0xE8923A);
    private static final Color BORDER    = new Color(0xE2DDD6);
    private static final Color DANGER    = new Color(0xDC5A3C);

    private JPanel listContainer;
    private JScrollPane scrollPane;
    private ArrayList<InventoryItem> productList;

    public Product() {
        setBackground(BG);
        setLayout(null);

        JPanel topBar = new JPanel(null);
        topBar.setBackground(CARD);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        topBar.setBounds(0, 0, 810, 64);
        add(topBar);

        JLabel lblTitle = new JLabel("Inventory Stock");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitle.setForeground(INK); lblTitle.setBounds(28, 16, 300, 32); topBar.add(lblTitle);

        JPanel headerRow = new JPanel(null);
        headerRow.setBackground(new Color(0xF0EDE7));
        headerRow.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        headerRow.setBounds(0, 64, 810, 36); add(headerRow);

        JLabel hName = new JLabel("Product"); hName.setFont(new Font("Tahoma", Font.BOLD, 11));
        hName.setForeground(INK_LIGHT); hName.setBounds(110, 10, 400, 16); headerRow.add(hName);
        JLabel hStock = new JLabel("Stock"); hStock.setFont(new Font("Tahoma", Font.BOLD, 11));
        hStock.setForeground(INK_LIGHT); hStock.setBounds(680, 10, 80, 16); headerRow.add(hStock);

        listContainer = new JPanel();
        listContainer.setLayout(null);
        listContainer.setOpaque(false);

        scrollPane = new JScrollPane(listContainer);
        scrollPane.setBounds(0, 100, 810, 530);
        scrollPane.setBorder(null); scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane);

        JPanel actionBar = new JPanel(null);
        actionBar.setBackground(CARD);
        actionBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        actionBar.setBounds(0, 625, 810, 58); add(actionBar);

        JButton btnEditStock    = createActionButton("Edit Stock",    28,  10, 160, ACCENT);
        JButton btnUpdateProduct = createActionButton("Update Product", 206, 10, 160, new Color(0x4A7FC1));
        JButton btnRemoveProduct = createActionButton("Remove Product", 384, 10, 160, DANGER);

        actionBar.add(btnEditStock); actionBar.add(btnUpdateProduct); actionBar.add(btnRemoveProduct);

        initializeInventoryData();
        refreshInventoryList();
    }

    private void initializeInventoryData() {
        productList = new ArrayList<>();
        productList.add(new InventoryItem("Samsung Galaxy S25 Ultra", "/LOGIN/img/samsung-galaxy-s25-ultra-sm-s938-1.jpg"));
        productList.add(new InventoryItem("iPhone 17 Pro Max", "/LOGIN/img/apple-iphone-17-pro-max-nano-sim.jpg"));
        productList.add(new InventoryItem("Xiaomi 17 Pro Max Leica Edition", "/LOGIN/img/xiaomi-17-pro-max-3.jpg"));
        productList.add(new InventoryItem("Vivo X300 Ultra", "/LOGIN/img/vivo-x300-ultra-2.jpg"));
    }

    public void refreshInventoryList() {
        listContainer.removeAll();
        int currentY = 0; int rowHeight = 90;

        for (InventoryItem item : productList) {
            int currentStock = DBConnection.getStock(item.getName());

            JPanel row = new JPanel(null);
            row.setBounds(0, currentY, 810, 85);
            row.setBackground(currentY / rowHeight % 2 == 0 ? CARD : new Color(0xFAF7F2));
            row.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));

            JLabel imgLabel = new JLabel();
            imgLabel.setBounds(20, 12, 62, 62);
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imgLabel.setBackground(new Color(0xF7F4EF)); imgLabel.setOpaque(true);
            imgLabel.setBorder(new LineBorder(BORDER, 1));
            setImageScaled(imgLabel, item.getImagePath(), 52, 52); row.add(imgLabel);

            JLabel nameLabel = new JLabel(item.getName());
            nameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
            nameLabel.setForeground(INK); nameLabel.setBounds(100, 30, 500, 22); row.add(nameLabel);

            JLabel stockLabel = new JLabel("×" + currentStock);
            stockLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
            Color stockColor = currentStock <= 0 ? DANGER : (currentStock <= 3 ? new Color(0xC97A2A) : new Color(0x2D8A5E));
            stockLabel.setForeground(stockColor);
            stockLabel.setBounds(680, 30, 80, 22); row.add(stockLabel);

            listContainer.add(row);
            currentY += rowHeight;
        }

        listContainer.setPreferredSize(new Dimension(810, currentY));
        listContainer.revalidate(); listContainer.repaint();
    }

    private JButton createActionButton(String text, int x, int y, int width, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setBounds(x, y, width, 36);
        btn.setFont(new Font("Tahoma", Font.BOLD, 13));
        btn.setForeground(Color.WHITE); btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusable(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> handleInventoryAction(text));
        return btn;
    }

    private void handleInventoryAction(String actionType) {
        if (productList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items available in inventory."); return;
        }
        String[] options = new String[productList.size()];
        for (int i = 0; i < productList.size(); i++) options[i] = productList.get(i).getName();
        String selectedProduct = (String) JOptionPane.showInputDialog(this,
            "Select a product:", actionType, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selectedProduct == null) return;
        switch (actionType) {
            case "Edit Stock":    executeEditStock(selectedProduct); break;
            case "Update Product": executeUpdateProduct(selectedProduct); break;
            case "Remove Product": executeRemoveProduct(selectedProduct); break;
        }
    }

    private void executeEditStock(String productName) {
        int activeStock = DBConnection.getStock(productName);
        String input = JOptionPane.showInputDialog(this,
            "Current stock for '" + productName + "' is " + activeStock + ".\nEnter new stock value:", activeStock);
        if (input == null || input.trim().isEmpty()) return;
        try {
            int newStockValue = Integer.parseInt(input.trim());
            if (newStockValue < 0) throw new NumberFormatException();
            int deltaOffset = newStockValue - activeStock;
            if (deltaOffset >= 0) DBConnection.decreaseStock(productName, -deltaOffset);
            else DBConnection.decreaseStock(productName, Math.abs(deltaOffset));
            refreshInventoryList();
            JOptionPane.showMessageDialog(this, "Stock for '" + productName + "' updated successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid entry. Please enter a positive integer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeUpdateProduct(String productName) {
        String inputName = JOptionPane.showInputDialog(this, "New product name:", productName);
        if (inputName == null || inputName.trim().isEmpty()) return;
        for (InventoryItem item : productList) {
            if (item.getName().equals(productName)) { item.setName(inputName.trim()); break; }
        }
        refreshInventoryList();
        JOptionPane.showMessageDialog(this, "Product name updated. Please sync with database manually.");
    }

    private void executeRemoveProduct(String productName) {
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Remove '" + productName + "' from inventory?", "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmation == JOptionPane.YES_OPTION) {
            productList.removeIf(item -> item.getName().equals(productName));
            refreshInventoryList();
            JOptionPane.showMessageDialog(this, "'" + productName + "' removed from inventory.");
        }
    }

    private void setImageScaled(JLabel label, String path, int width, int height) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
        } else { label.setText("📱"); label.setFont(new Font("Tahoma", Font.PLAIN, 24)); }
    }

    private static class InventoryItem {
        private String name, imagePath;
        public InventoryItem(String name, String imagePath) { this.name = name; this.imagePath = imagePath; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImagePath() { return imagePath; }
    }
}