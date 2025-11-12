package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderRecord represents a single order
 * Containing a unique ID, a list of the ordered items, the total cost, and when it was ordered.
 */
public class OrderRecord {
    private int orderId;
    private String items;
    private BigDecimal total;
    private LocalDateTime orderTime;

    /** Reads existing records from database */
    public OrderRecord(int orderId, String items, BigDecimal total, LocalDateTime orderTime) {
        this.orderId = orderId;
        this.items = items;
        this.total = total;
        this.orderTime = orderTime;
    }

    /** Create and inserts new orders into database */
    public OrderRecord(String items, BigDecimal total, LocalDateTime orderTime) {
        this(-1, items, total, orderTime);
    }

    // The getters
    public int getOrderId() { return orderId; }
    public String getItems() { return items; }
    public BigDecimal getTotal() { return total; }
    public LocalDateTime getOrderTime() { return orderTime; }

    @Override
    public String toString() {
        return "Order #" + orderId + " | $" + total + " | " + orderTime + " | " + items;
    }
}
