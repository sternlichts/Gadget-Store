package Component;

import Model.DBConnection;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class LoginGui {

    private JFrame frame;
    private JTextField textField;
    private JPasswordField textField_1;

    private static final Color BG          = new Color(0xFAF7F2);
    private static final Color CARD        = Color.WHITE;
    private static final Color INK         = new Color(0x1A1A2E);
    private static final Color INK_LIGHT   = new Color(0x6B6B80);
    private static final Color ACCENT      = new Color(0xE8923A);
    private static final Color ACCENT_DARK = new Color(0xC97A2A);
    private static final Color BORDER      = new Color(0xE2DDD6);
    private static final Color FIELD_BG    = new Color(0xF7F4EF);
    private static final Color DANGER      = new Color(0xDC5A3C);
    private static final Color INFO        = new Color(0x4A7FC1);
    private static final Color ADMIN_COLOR = new Color(0x7B5EA7);
    private static final Color USER_COLOR  = new Color(0x2D8A5E);

    private static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD,   22);
    private static final Font FONT_LABEL = new Font("Tahoma", Font.PLAIN,  13);
    private static final Font FONT_SMALL = new Font("Tahoma", Font.PLAIN,  11);
    private static final Font FONT_BTN   = new Font("Tahoma", Font.BOLD,   13);

    public static String registeredUser = "";
    public static String registeredPass = "";
    public static String registeredRole = "user";

    private String selectedRole = "user";

    private boolean isValidUsername(String username) {
        return username.length() >= 8;
    }

    private boolean isValidPassword(String password) {
        String regex =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.matches(regex, password);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                LoginGui window = new LoginGui();
                window.frame.setVisible(true);
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public LoginGui() { initialize(); }

    private void initialize() {
        frame = new JFrame("Gadget Store");
        frame.setSize(420, 580);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel root = new JPanel(null);
        root.setBackground(BG);
        frame.setContentPane(root);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(ACCENT);
        accentBar.setBounds(0, 0, 5, 580);
        root.add(accentBar);

        JPanel card = new JPanel(null);
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        card.setBounds(30, 30, 350, 510);
        root.add(card);

        JPanel header = new JPanel(null);
        header.setBackground(INK);
        header.setBounds(0, 0, 350, 72);
        card.add(header);

        JLabel storeIcon = new JLabel("📱");
        storeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        storeIcon.setBounds(22, 18, 32, 36);
        header.add(storeIcon);

        JLabel lblTitle = new JLabel("GADGET STORE");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(60, 14, 230, 28);
        header.add(lblTitle);

        JLabel lblSub = new JLabel("Sign in to your account");
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(new Color(0xAAA8B8));
        lblSub.setBounds(60, 43, 230, 18);
        header.add(lblSub);

        JLabel lblRoleTitle = new JLabel("LOGIN AS");
        lblRoleTitle.setFont(new Font("Tahoma", Font.BOLD, 10));
        lblRoleTitle.setForeground(INK_LIGHT);
        lblRoleTitle.setBounds(30, 88, 200, 16);
        card.add(lblRoleTitle);

        JPanel rolePanel = new JPanel(null);
        rolePanel.setBounds(30, 106, 290, 40);
        rolePanel.setBackground(new Color(0xF0EDE7));
        rolePanel.setBorder(new LineBorder(BORDER, 1));
        card.add(rolePanel);

        JPanel rolePill = new JPanel();
        rolePill.setBounds(1, 1, 143, 38);
        rolePill.setBackground(USER_COLOR);
        rolePill.setBorder(null);
        rolePanel.add(rolePill);

        JLabel btnUser = new JLabel("👤  User", SwingConstants.CENTER);
        btnUser.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnUser.setForeground(Color.WHITE);
        btnUser.setBounds(0, 0, 145, 40);
        btnUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rolePanel.add(btnUser);

        JLabel btnAdmin = new JLabel("🔧  Admin", SwingConstants.CENTER);
        btnAdmin.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnAdmin.setForeground(INK_LIGHT);
        btnAdmin.setBounds(145, 0, 145, 40);
        btnAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rolePanel.add(btnAdmin);

        JLabel lblSelectedRole = new JLabel("Signing in as: User");
        lblSelectedRole.setFont(new Font("Tahoma", Font.ITALIC, 10));
        lblSelectedRole.setForeground(USER_COLOR);
        lblSelectedRole.setBounds(30, 148, 290, 14);
        card.add(lblSelectedRole);

        btnUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedRole = "user";
                rolePill.setBounds(1, 1, 143, 38);
                rolePill.setBackground(USER_COLOR);
                btnUser.setForeground(Color.WHITE);
                btnAdmin.setForeground(INK_LIGHT);
                lblSelectedRole.setText("Signing in as: User");
                lblSelectedRole.setForeground(USER_COLOR);
                rolePanel.repaint();
            }
        });
        btnAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedRole = "admin";
                rolePill.setBounds(146, 1, 143, 38);
                rolePill.setBackground(ADMIN_COLOR);
                btnAdmin.setForeground(Color.WHITE);
                btnUser.setForeground(INK_LIGHT);
                lblSelectedRole.setText("Signing in as: Admin");
                lblSelectedRole.setForeground(ADMIN_COLOR);
                rolePanel.repaint();
            }
        });

        addFieldLabel(card, "USERNAME", 30, 170);
        textField = addStyledField(card, 30, 188, 290);

        addFieldLabel(card, "PASSWORD", 30, 236);
        textField_1 = new JPasswordField();
        styleField(textField_1, 30, 254, 290);
        card.add(textField_1);

        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(FONT_SMALL);
        showPass.setForeground(INK_LIGHT);
        showPass.setBackground(CARD);
        showPass.setBounds(30, 292, 140, 20);
        card.add(showPass);
        showPass.addActionListener(e ->
            textField_1.setEchoChar(showPass.isSelected() ? (char) 0 : '\u2022'));

        JButton btnLogin = makePrimaryBtn("Log In", ACCENT, ACCENT_DARK);
        btnLogin.setBounds(30, 326, 290, 40);
        card.add(btnLogin);

        JButton btnCreate = makeLinkBtn("Create account");
        btnCreate.setBounds(30, 380, 130, 28);
        card.add(btnCreate);

        JButton btnForgot = makeLinkBtn("Forgot password?");
        btnForgot.setForeground(DANGER);
        btnForgot.setBounds(195, 380, 125, 28);
        card.add(btnForgot);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBounds(30, 422, 290, 1);
        card.add(sep);

        JButton btnClear = makeOutlineBtn("Clear");
        btnClear.setBounds(30, 436, 135, 36);
        card.add(btnClear);

        JButton btnExit = makeOutlineBtn("Exit");
        btnExit.setForeground(DANGER);
        btnExit.setBorder(new LineBorder(new Color(0xF0C0B8), 1));
        btnExit.setBounds(185, 436, 135, 36);
        card.add(btnExit);

        JLabel footer = new JLabel("© 2025 Gadget Store. All rights reserved.", SwingConstants.CENTER);
        footer.setFont(new Font("Tahoma", Font.PLAIN, 10));
        footer.setForeground(INK_LIGHT);
        footer.setBounds(30, 484, 290, 16);
        card.add(footer);

        btnExit.addActionListener(e -> System.exit(0));
        btnClear.addActionListener(e -> { textField.setText(""); textField_1.setText(""); });

        btnLogin.addActionListener(e -> {
            String user = textField.getText().trim();
            String pass = new String(textField_1.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter username and password.");
                return;
            }
            if (!isValidUsername(user)) {
                JOptionPane.showMessageDialog(frame, "Username must be at least 8 characters.");
                return;
            }
            if (!isValidPassword(pass)) {
                JOptionPane.showMessageDialog(frame,
                    "Password must contain:\n" +
                    "• At least 8 characters\n" +
                    "• One uppercase letter\n" +
                    "• One lowercase letter\n" +
                    "• One number\n" +
                    "• One special character");
                return;
            }

            Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "SELECT password, role FROM users WHERE username = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, user);
                rs = ps.executeQuery();

                if (rs.next()) {
                    String dbPass = rs.getString("password");
                    String dbRole = rs.getString("role");

                    if (!pass.equals(dbPass)) {
                        JOptionPane.showMessageDialog(frame, "Incorrect password. Please try again.");
                        textField_1.setText("");
                        return;
                    }

                    if (!selectedRole.equalsIgnoreCase(dbRole)) {
                        String expected = dbRole.substring(0, 1).toUpperCase() + dbRole.substring(1);
                        JOptionPane.showMessageDialog(frame,
                            "This account is registered as \"" + expected + "\".\n" +
                            "Please select the correct role before logging in.",
                            "Role Mismatch", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    registeredUser = user;
                    registeredPass = dbPass;
                    registeredRole = dbRole;

                    JOptionPane.showMessageDialog(frame,
                        "Login Successful!\nWelcome, " + user + " (" + expected(dbRole) + ")");
                    ParentForm pf = new ParentForm();
                    pf.setVisible(true);
                    frame.dispose();

                } else {
                    JOptionPane.showMessageDialog(frame, "Account is not yet created.");
                    textField.setText(""); textField_1.setText("");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
                try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            }
        });

        btnCreate.addActionListener(e -> {
            RegistrationForm rf = new RegistrationForm();
            rf.setVisible(true);
        });

        btnForgot.addActionListener(e -> {
            String userCheck = JOptionPane.showInputDialog(frame, "Enter Username:");
            if (userCheck == null || userCheck.trim().isEmpty()) return;
            Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();
                String sql = "SELECT security_answer1, security_answer2, security_answer3 FROM users WHERE username = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, userCheck.trim());
                rs = ps.executeQuery();
                if (!rs.next()) { JOptionPane.showMessageDialog(frame, "Username not found."); return; }
                String a1 = rs.getString("security_answer1");
                String a2 = rs.getString("security_answer2");
                String a3 = rs.getString("security_answer3");
                rs.close(); rs = null; ps.close(); ps = null;
                String q1 = JOptionPane.showInputDialog("Pet's name:");
                String q2 = JOptionPane.showInputDialog("Birthplace:");
                String q3 = JOptionPane.showInputDialog("Favorite food:");
                if (q1 == null || q2 == null || q3 == null) return;
                if (q1.equalsIgnoreCase(a1) && q2.equalsIgnoreCase(a2) && q3.equalsIgnoreCase(a3)) {
                    String newPass = JOptionPane.showInputDialog("Enter new password:");
                    if (newPass != null && !newPass.isEmpty()) {
                        String upd = "UPDATE users SET password = ? WHERE username = ?";
                        ps = conn.prepareStatement(upd);
                        ps.setString(1, newPass); ps.setString(2, userCheck.trim());
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(frame, "Password updated!");
                    }
                } else { JOptionPane.showMessageDialog(frame, "Wrong answers!"); }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage());
            } finally {
                try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
                try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            }
        });
    }

    private String expected(String role) {
        if (role == null) return "User";
        return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
    }

    private void addFieldLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Tahoma", Font.BOLD, 10));
        lbl.setForeground(INK_LIGHT);
        lbl.setBounds(x, y, 200, 16);
        p.add(lbl);
    }

    private JTextField addStyledField(JPanel p, int x, int y, int w) {
        JTextField f = new JTextField();
        styleField(f, x, y, w);
        p.add(f);
        return f;
    }

    private void styleField(JTextField f, int x, int y, int w) {
        f.setBounds(x, y, w, 34);
        f.setFont(new Font("Tahoma", Font.PLAIN, 13));
        f.setBackground(FIELD_BG);
        f.setForeground(INK);
        f.setCaretColor(ACCENT);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(0, 10, 0, 10)
        ));
    }

    private JButton makePrimaryBtn(String text, Color bg, Color hover) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeOutlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btn.setForeground(INK);
        btn.setBackground(CARD);
        btn.setBorder(new LineBorder(BORDER, 1));
        btn.setFocusable(false);	
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeLinkBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btn.setForeground(INFO);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusable(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}