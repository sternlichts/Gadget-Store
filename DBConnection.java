package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

	private static final String HOST = "jdbc:mysql://127.0.0.1:3306/gadget_store?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {

            boolean needNew = false;
            try {
                needNew = (connection == null || connection.isClosed() || !connection.isValid(2));
            } catch (SQLException ignore) {
                needNew = true;
            }

            if (needNew) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "MySQL JDBC Driver not found. Add mysql-connector-j-*.jar to your build path.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Cannot connect to database. Check HOST/USERNAME/PASSWORD.\n" + e.getMessage(), e);
        }
        return connection;
    }

    public static int getStock(String productName) {
        String sql = "SELECT stock FROM products WHERE name = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, productName);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("stock");
        } catch (SQLException e) {
            System.err.println("getStock error: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }
        return -1;
    }

    public static boolean decreaseStock(String productName, int quantity) {
        String sql = "UPDATE products SET stock = stock - ? WHERE name = ? AND stock >= ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setString(2, productName);
            ps.setInt(3, quantity);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("decreaseStock error: " + e.getMessage());
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ignored) {}
    }
}
