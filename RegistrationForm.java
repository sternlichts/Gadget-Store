package Component;

import Model.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrationForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color BG          = new Color(0xFAF7F2);
    private static final Color CARD        = Color.WHITE;
    private static final Color INK         = new Color(0x1A1A2E);
    private static final Color INK_LIGHT   = new Color(0x6B6B80);
    private static final Color ACCENT      = new Color(0xE8923A);
    private static final Color BORDER      = new Color(0xE2DDD6);
    private static final Color FIELD_BG    = new Color(0xF7F4EF);
    private static final Color ADMIN_COLOR = new Color(0x7B5EA7);
    private static final Color USER_COLOR  = new Color(0x2D8A5E);

    private JTextField    uField, q1Field, q2Field, q3Field;
    private JPasswordField pField;
    private JComboBox<String> roleCombo;

    public RegistrationForm() {
        setTitle("Create Account");
        setSize(420, 590);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(null);
        root.setBackground(BG);
        setContentPane(root);

        JPanel bar = new JPanel();
        bar.setBackground(ACCENT);
        bar.setBounds(0, 0, 5, 590);
        root.add(bar);

        JPanel card = new JPanel(null);
        card.setBackground(CARD);
        card.setBorder(new LineBorder(BORDER, 1));
        card.setBounds(30, 20, 355, 545);
        root.add(card);

        JPanel header = new JPanel(null);
        header.setBackground(INK);
        header.setBounds(0, 0, 355, 62);
        card.add(header);

        JLabel ico = new JLabel("👤");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        ico.setBounds(20, 14, 28, 34);
        header.add(ico);

        JLabel ttl = new JLabel("CREATE ACCOUNT");
        ttl.setFont(new Font("Tahoma", Font.BOLD, 18));
        ttl.setForeground(Color.WHITE);
        ttl.setBounds(52, 10, 260, 26);
        header.add(ttl);

        JLabel sub = new JLabel("Fill in your details to register");
        sub.setFont(new Font("Tahoma", Font.PLAIN, 11));
        sub.setForeground(new Color(0xAAA8B8));
        sub.setBounds(52, 37, 260, 18);
        header.add(sub);

        int y = 74;
        addLabel(card, "ACCOUNT ROLE", 28, y);
        y += 16;

        JPanel roleRow = new JPanel(null);
        roleRow.setBounds(28, y, 300, 34);
        roleRow.setBackground(CARD);
        card.add(roleRow);

        roleCombo = new JComboBox<>(new String[]{"user", "admin"});
        roleCombo.setFont(new Font("Tahoma", Font.BOLD, 13));
        roleCombo.setBackground(FIELD_BG);
        roleCombo.setBounds(0, 0, 180, 34);
        roleCombo.setBorder(new LineBorder(BORDER, 1));
        roleRow.add(roleCombo);

        JLabel roleDesc = new JLabel("Access: Products, Cart, Orders");
        roleDesc.setFont(new Font("Tahoma", Font.ITALIC, 10));
        roleDesc.setForeground(USER_COLOR);
        roleDesc.setBounds(190, 8, 200, 18);
        roleRow.add(roleDesc);

        JPanel roleBadge = new JPanel();
        roleBadge.setBounds(0, 0, 4, 34);
        roleBadge.setBackground(USER_COLOR);
        roleRow.add(roleBadge);

        roleCombo.addActionListener(e -> {
            String selected = (String) roleCombo.getSelectedItem();
            if ("admin".equals(selected)) {
                roleDesc.setText("Access: All navigation");
                roleDesc.setForeground(ADMIN_COLOR);
                roleBadge.setBackground(ADMIN_COLOR);
                roleCombo.setBorder(new LineBorder(ADMIN_COLOR, 1));
            } else {
                roleDesc.setText("Access: Products, Cart, Orders");
                roleDesc.setForeground(USER_COLOR);
                roleBadge.setBackground(USER_COLOR);
                roleCombo.setBorder(new LineBorder(BORDER, 1));
            }
            roleRow.repaint();
        });

        y += 44;

        addLabel(card, "USERNAME", 28, y);
        uField = addField(card, 28, y + 16, 300);
        y += 56;

        addLabel(card, "PASSWORD", 28, y);
        pField = new JPasswordField();
        styleField(pField, 28, y + 16, 300);
        card.add(pField);
        y += 56;

        JLabel secLbl = new JLabel("SECURITY QUESTIONS");
        secLbl.setFont(new Font("Tahoma", Font.BOLD, 9));
        secLbl.setForeground(INK_LIGHT);
        secLbl.setBounds(28, y, 300, 14);
        card.add(secLbl);
        y += 16;

        addLabel(card, "PET'S NAME", 28, y);
        q1Field = addField(card, 28, y + 16, 300);
        y += 56;

        addLabel(card, "BIRTHPLACE", 28, y);
        q2Field = addField(card, 28, y + 16, 300);
        y += 56;

        addLabel(card, "FAVORITE FOOD", 28, y);
        q3Field = addField(card, 28, y + 16, 300);
        y += 56;

        JButton btn = makePrimaryBtn("Register");
        btn.setBounds(28, y + 4, 300, 40);
        card.add(btn);

        btn.addActionListener(e -> {
            if (uField.getText().isEmpty() || pField.getPassword().length == 0 ||
                q1Field.getText().isEmpty() || q2Field.getText().isEmpty() || q3Field.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            String username = uField.getText().trim();
            String password = new String(pField.getPassword());
            String ans1     = q1Field.getText().trim();
            String ans2     = q2Field.getText().trim();
            String ans3     = q3Field.getText().trim();
            String role     = (String) roleCombo.getSelectedItem();

            Connection conn = null;
            PreparedStatement checkStmt = null, insertStmt = null;
            ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();

                checkStmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
                checkStmt.setString(1, username);
                rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this,
                        "Username \"" + username + "\" is already taken. Please choose another.");
                    return;
                }
                rs.close(); rs = null; checkStmt.close(); checkStmt = null;

                String insertSql =
                    "INSERT INTO users (username, password, role, security_answer1, security_answer2, security_answer3) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, role);
                insertStmt.setString(4, ans1);
                insertStmt.setString(5, ans2);
                insertStmt.setString(6, ans3);
                insertStmt.executeUpdate();

                LoginGui.registeredUser = username;
                LoginGui.registeredPass = password;
                LoginGui.registeredRole = role;

                String roleLabel = role.substring(0, 1).toUpperCase() + role.substring(1);
                JOptionPane.showMessageDialog(this,
                    "Account Created Successfully!\nRole: " + roleLabel);
                dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
                try { if (checkStmt != null) checkStmt.close(); } catch (SQLException ignored) {}
                try { if (insertStmt != null) insertStmt.close(); } catch (SQLException ignored) {}
            }
        });
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Tahoma", Font.BOLD, 9));
        l.setForeground(INK_LIGHT);
        l.setBounds(x, y, 200, 14);
        p.add(l);
    }

    private JTextField addField(JPanel p, int x, int y, int w) {
        JTextField f = new JTextField();
        styleField(f, x, y, w);
        p.add(f);
        return f;
    }

    private void styleField(JTextField f, int x, int y, int w) {
        f.setBounds(x, y, w, 32);
        f.setFont(new Font("Tahoma", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setForeground(INK);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1), new EmptyBorder(0, 10, 0, 10)));
    }

    private JButton makePrimaryBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(0xC97A2A) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Tahoma", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}