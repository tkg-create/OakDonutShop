package entity;

import core.DB;
import java.sql.*;
import java.util.*;


/**
 * OrderDAO handles database access for MenuItem objects, retrieving items from the Derby database.
 */
public class OrderDAO {
    private final Connection conn;

    /** Constructs a OrderDAO and connects to the database.
     * Throws RuntimeException if connection fails. */
    public OrderDAO() {
        try {
            conn = DB.getConnection();
            ensureTable();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to database", e);
        }
    }

    /**
     * Makes sure the order history table exists.
     * If it doesn't, creates it with columns for order ID, items, total, and order time.
     */
    private void ensureTable() throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        try (ResultSet rs = md.getTables(null, null, "ORDER_HISTORY", null)) {
            if (!rs.next()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(
                            "CREATE TABLE ORDER_HISTORY (" +
                                    "ORDER_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                    "ITEMS VARCHAR(500), " +
                                    "TOTAL DECIMAL(10,2), " +
                                    "ORDER_TIME TIMESTAMP)"
                    );
                }
            }
        }
    }

    /**
     * Inserts a new order record into the ORDER_HISTORY table.
     * @param order The OrderRecord to be stored.
     */
    public void insertOrder(OrderRecord order) throws SQLException {
        String sql = "INSERT INTO ORDER_HISTORY (ITEMS, TOTAL, ORDER_TIME) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getItems());
            ps.setBigDecimal(2, order.getTotal());
            ps.setTimestamp(3, Timestamp.valueOf(order.getOrderTime()));
            ps.executeUpdate();
        }
    }

    /**
     * Gets all recorded orders.
     * @return A list of all stored OrderRecord objects.
     */
    public List<OrderRecord> getAllOrders() throws SQLException {
        List<OrderRecord> orders = new ArrayList<>();
        String sql = "SELECT ORDER_ID, ITEMS, TOTAL, ORDER_TIME FROM ORDER_HISTORY ORDER BY ORDER_TIME DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(new OrderRecord(
                        rs.getInt("ORDER_ID"),
                        rs.getString("ITEMS"),
                        rs.getBigDecimal("TOTAL"),
                        rs.getTimestamp("ORDER_TIME").toLocalDateTime()
                ));
            }
        }
        return orders;
    }
}
