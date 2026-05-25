package Component;

import Model.DBConnection;
import Model.OrderItem;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ReportsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG        = new Color(0xFAF7F2);
    private static final Color CARD      = Color.WHITE;
    private static final Color INK       = new Color(0x1A1A2E);
    private static final Color INK_LIGHT = new Color(0x6B6B80);
    private static final Color ACCENT    = new Color(0xE8923A);
    private static final Color BORDER    = new Color(0xE2DDD6);
    private static final Color HDR_BG    = new Color(0x1A1A2E);
    private static final Color SUCCESS   = new Color(0x2D8A5E);
    private static final Color INFO      = new Color(0x4A7FC1);
    private static final Color PURPLE    = new Color(0x7B5EA7);

    private final Orders ordersPanelInstance;

    private JSpinner spinnerStartDate;
    private JSpinner spinnerEndDate;
    private JComboBox<String> comboReportType;

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JLabel lblTotalOrders;
    private JLabel lblTotalRevenue;
    private JLabel lblTotalUnits;

    private JButton btnGenerate;
    private JButton btnExport;
    private JButton btnPrint;

    private static final String[] DAILY_COLS   = {"Date",    "Orders",  "Units Sold", "Revenue (₱)"};
    private static final String[] MONTHLY_COLS = {"Month",   "Orders",  "Units Sold", "Revenue (₱)"};
    private static final String[] PRODUCT_COLS = {"Product", "Variant", "Units Sold", "Revenue (₱)"};

    public ReportsPanel(Orders orders) {
        this.ordersPanelInstance = orders;
        setLayout(null);
        setBackground(BG);
        setBounds(0, 0, 810, 720);

        buildHeader();
        buildFilterBar();
        buildTable();
        buildSummaryBar();
        buildActionBar();
    }

    public void generateReportIfReady() {
        if (tableModel.getRowCount() == 0) generateReport();
    }

    private void buildHeader() {
        JPanel topBar = new JPanel(null);
        topBar.setBackground(CARD);
        topBar.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        topBar.setBounds(0, 0, 810, 64); add(topBar);

        JLabel lblTitle = new JLabel("Sales Reports");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitle.setForeground(INK); lblTitle.setBounds(28, 16, 300, 32); topBar.add(lblTitle);

        JLabel lblSub = new JLabel("Generate, export and print your store's sales data");
        lblSub.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblSub.setForeground(INK_LIGHT); lblSub.setBounds(28, 42, 500, 16); topBar.add(lblSub);
    }

    private void buildFilterBar() {
        JPanel filterCard = new JPanel(null);
        filterCard.setBounds(20, 76, 770, 74);
        filterCard.setBackground(CARD);
        filterCard.setBorder(new LineBorder(BORDER, 1));
        add(filterCard);

        JLabel lblType = new JLabel("Report Type");
        lblType.setFont(new Font("Tahoma", Font.BOLD, 11)); lblType.setForeground(INK_LIGHT);
        lblType.setBounds(16, 10, 110, 16); filterCard.add(lblType);

        comboReportType = new JComboBox<>(new String[]{"Daily Sales", "Monthly Sales", "Product Sales"});
        comboReportType.setFont(new Font("Tahoma", Font.PLAIN, 13));
        comboReportType.setBounds(16, 28, 160, 30);
        comboReportType.setBackground(CARD); filterCard.add(comboReportType);

        JLabel lblStart = new JLabel("Start Date");
        lblStart.setFont(new Font("Tahoma", Font.BOLD, 11)); lblStart.setForeground(INK_LIGHT);
        lblStart.setBounds(200, 10, 90, 16); filterCard.add(lblStart);

        spinnerStartDate = createDateSpinner(LocalDate.now().minusMonths(1));
        spinnerStartDate.setBounds(200, 28, 130, 30); filterCard.add(spinnerStartDate);

        JLabel lblEnd = new JLabel("End Date");
        lblEnd.setFont(new Font("Tahoma", Font.BOLD, 11)); lblEnd.setForeground(INK_LIGHT);
        lblEnd.setBounds(350, 10, 80, 16); filterCard.add(lblEnd);

        spinnerEndDate = createDateSpinner(LocalDate.now());
        spinnerEndDate.setBounds(350, 28, 130, 30); filterCard.add(spinnerEndDate);

        btnGenerate = new JButton("Generate Report") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0xC97A2A) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btnGenerate.setFont(new Font("Tahoma", Font.BOLD, 13)); btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setContentAreaFilled(false); btnGenerate.setBorderPainted(false);
        btnGenerate.setBounds(588, 22, 162, 34); btnGenerate.setFocusable(false);
        btnGenerate.addActionListener(e -> generateReport()); filterCard.add(btnGenerate);
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(DAILY_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        reportTable = new JTable(tableModel);
        reportTable.setFont(new Font("Tahoma", Font.PLAIN, 13));
        reportTable.setRowHeight(34);
        reportTable.setGridColor(BORDER);
        reportTable.setSelectionBackground(new Color(0xFFF3E8));
        reportTable.setShowVerticalLines(false);
        reportTable.setIntercellSpacing(new Dimension(0, 1));
        reportTable.setFillsViewportHeight(true);

        JTableHeader header = reportTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 12));
        header.setBackground(HDR_BG);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));

        reportTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(col >= 1 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                if (!sel) setBackground(row % 2 == 0 ? CARD : new Color(0xFAF7F2));
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBounds(20, 162, 770, 360);
        scrollPane.setBorder(new LineBorder(BORDER, 1));
        add(scrollPane);
    }

    private void buildSummaryBar() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        summaryPanel.setBounds(20, 534, 770, 72);
        summaryPanel.setBackground(BG); add(summaryPanel);

        lblTotalOrders  = buildSummaryCard(summaryPanel, "Total Orders",  "0",   new Color(0xE8F4FD), INFO);
        lblTotalRevenue = buildSummaryCard(summaryPanel, "Total Revenue", "₱0",  new Color(0xFFF3E8), ACCENT);
        lblTotalUnits   = buildSummaryCard(summaryPanel, "Units Sold",    "0",   new Color(0xEAF7F0), SUCCESS);
    }

    private JLabel buildSummaryCard(JPanel parent, String title, String defaultValue, Color bg, Color accent) {
        JPanel card = new JPanel(null);
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JPanel accentBar = new JPanel(); accentBar.setBackground(accent);
        accentBar.setBounds(0, 0, 4, 72); card.add(accentBar);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblTitle.setForeground(INK_LIGHT); lblTitle.setBounds(18, 14, 200, 14); card.add(lblTitle);

        JLabel lblValue = new JLabel(defaultValue);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblValue.setForeground(INK); lblValue.setBounds(18, 32, 220, 28); card.add(lblValue);

        parent.add(card);
        return lblValue;
    }

    private void buildActionBar() {
        JPanel actionBar = new JPanel(null);
        actionBar.setBackground(CARD);
        actionBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));
        actionBar.setBounds(20, 618, 770, 58); add(actionBar);

        btnExport = makeActionBtn("Export CSV", SUCCESS, 16, 12);
        btnExport.addActionListener(e -> exportToCsv()); actionBar.add(btnExport);

        btnPrint = makeActionBtn("Print Report", PURPLE, 184, 12);
        btnPrint.addActionListener(e -> printReport()); actionBar.add(btnPrint);
    }

    private JButton makeActionBtn(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Tahoma", Font.BOLD, 13)); btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setBounds(x, y, 148, 34); btn.setFocusable(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void generateReport() {
        LocalDate startDate = getSpinnerDate(spinnerStartDate);
        LocalDate endDate   = getSpinnerDate(spinnerEndDate);
        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Start date cannot be after end date.", "Invalid Range", JOptionPane.WARNING_MESSAGE); return;
        }
        List<OrderRecord> records = collectOrderRecords(startDate, endDate);
        if (records.isEmpty()) {
            tableModel.setRowCount(0); updateSummary(0, 0, 0);
            JOptionPane.showMessageDialog(this, "No orders found in the selected date range.", "No Data", JOptionPane.INFORMATION_MESSAGE); return;
        }
        switch ((String) comboReportType.getSelectedItem()) {
            case "Daily Sales"   -> buildDailyReport(records);
            case "Monthly Sales" -> buildMonthlyReport(records);
            case "Product Sales" -> buildProductReport(records);
        }
    }

    private void buildDailyReport(List<OrderRecord> records) {
        setColumns(DAILY_COLS);
        Map<String, long[]> map = new LinkedHashMap<>();
        for (OrderRecord r : records) {
            map.computeIfAbsent(r.date, k -> new long[3]);
            long[] a = map.get(r.date);
            a[0]++; a[1] += r.quantity; a[2] += r.revenue;
        }
        tableModel.setRowCount(0);
        long totO = 0, totU = 0, totR = 0;
        for (Map.Entry<String, long[]> e : map.entrySet()) {
            long[] v = e.getValue();
            tableModel.addRow(new Object[]{e.getKey(), v[0], v[1], formatPeso(v[2])});
            totO += v[0]; totU += v[1]; totR += v[2];
        }
        updateSummary(totO, totR, totU);
    }

    private void buildMonthlyReport(List<OrderRecord> records) {
        setColumns(MONTHLY_COLS);
        Map<String, long[]> map = new LinkedHashMap<>();
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("MMMM yyyy");
        for (OrderRecord r : records) {
            String key;
            try {
                LocalDate d = LocalDate.parse(r.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                key = d.format(monthFmt) + "|" + d.format(labelFmt);
            } catch (Exception ex) { key = r.date + "|" + r.date; }
            map.computeIfAbsent(key, k -> new long[3]);
            long[] a = map.get(key); a[0]++; a[1] += r.quantity; a[2] += r.revenue;
        }
        tableModel.setRowCount(0);
        long totO = 0, totU = 0, totR = 0;
        for (Map.Entry<String, long[]> e : map.entrySet()) {
            String label = e.getKey().split("\\|")[1];
            long[] v = e.getValue();
            tableModel.addRow(new Object[]{label, v[0], v[1], formatPeso(v[2])});
            totO += v[0]; totU += v[1]; totR += v[2];
        }
        updateSummary(totO, totR, totU);
    }

    private void buildProductReport(List<OrderRecord> records) {
        setColumns(PRODUCT_COLS);
        Map<String, long[]> map = new LinkedHashMap<>();
        Map<String, String[]> labels = new LinkedHashMap<>();
        for (OrderRecord r : records) {
            String key = r.productName + "||" + r.variant;
            map.computeIfAbsent(key, k -> new long[2]);
            labels.put(key, new String[]{r.productName, r.variant});
            long[] a = map.get(key); a[0] += r.quantity; a[1] += r.revenue;
        }
        tableModel.setRowCount(0);
        long totO = 0, totU = 0, totR = 0;
        for (Map.Entry<String, long[]> e : map.entrySet()) {
            String[] lbl = labels.get(e.getKey());
            long[] v = e.getValue();
            tableModel.addRow(new Object[]{lbl[0], lbl[1], v[0], formatPeso(v[1])});
            totO++; totU += v[0]; totR += v[1];
        }
        updateSummary(totO, totR, totU);
    }

    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Generate a report first before exporting.", "No Data", JOptionPane.WARNING_MESSAGE); return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("sales_report.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".csv")) file = new File(file.getPath() + ".csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            StringBuilder hdr = new StringBuilder();
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                if (c > 0) hdr.append(","); hdr.append("\"").append(tableModel.getColumnName(c)).append("\"");
            }
            pw.println(hdr);
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    if (c > 0) row.append(",");
                    Object val = tableModel.getValueAt(r, c);
                    row.append("\"").append(val != null ? val : "").append("\"");
                }
                pw.println(row);
            }
            pw.println();
            pw.println("\"Total Orders\",\"" + lblTotalOrders.getText() + "\"");
            pw.println("\"Total Revenue\",\"" + lblTotalRevenue.getText() + "\"");
            pw.println("\"Units Sold\",\"" + lblTotalUnits.getText() + "\"");
            JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Generate a report first before printing.", "No Data", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            MessageFormat header = new MessageFormat("Gadget Store \u2014 " + comboReportType.getSelectedItem() + "  {0}");
            MessageFormat footer = new MessageFormat(
                "Orders: " + lblTotalOrders.getText() + "   Revenue: " + lblTotalRevenue.getText() +
                "   Units: " + lblTotalUnits.getText() + "    Page {0}");
            reportTable.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<OrderRecord> collectOrderRecords(LocalDate start, LocalDate end) {
        List<OrderRecord> list = new ArrayList<>();
        for (OrderItem item : ordersPanelInstance.getOrderItems()) {
            LocalDate orderDate = item.getOrderDate() != null ? item.getOrderDate() : LocalDate.now();
            if (orderDate.isBefore(start) || orderDate.isAfter(end)) continue;
            long price = 0;
            try { price = Long.parseLong(item.getPrice().replaceAll("[^0-9]", "")); } catch (NumberFormatException ignored) {}
            list.add(new OrderRecord(orderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                item.getName(), item.getVariant(), item.getQuantity(), price * item.getQuantity()));
        }
        return list;
    }

    private void setColumns(String[] cols) { tableModel.setColumnCount(0); for (String c : cols) tableModel.addColumn(c); }

    private void updateSummary(long orders, long revenue, long units) {
        lblTotalOrders.setText(String.valueOf(orders));
        lblTotalRevenue.setText("\u20b1" + formatPeso(revenue));
        lblTotalUnits.setText(String.valueOf(units));
    }

    private static String formatPeso(long amount) { return String.format("%,d", amount); }

    private JSpinner createDateSpinner(LocalDate defaultDate) {
        SpinnerDateModel model = new SpinnerDateModel(toDate(defaultDate), null, null, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor); spinner.setFont(new Font("Tahoma", Font.PLAIN, 13));
        return spinner;
    }

    private LocalDate getSpinnerDate(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static Date toDate(LocalDate ld) {
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static class OrderRecord {
        final String date, productName, variant;
        final int quantity; final long revenue;
        OrderRecord(String date, String productName, String variant, int quantity, long revenue) {
            this.date = date; this.productName = productName; this.variant = variant;
            this.quantity = quantity; this.revenue = revenue;
        }
    }
}