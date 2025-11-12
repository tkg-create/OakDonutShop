package entity;
import java.math.BigDecimal;

/**
 * MenuItem represents a single item on the Oak Donuts menu, containing the name and price of the item.
 */
public class MenuItem {
    private String name;
    private BigDecimal price;

    /**
     * Constructs a MenuItem with the given name and price.
     * @param name Name of the item
     * @param price Price of the item
     */
    public MenuItem(String name, BigDecimal price) { this.name = name; this.price = price; }

    /** @return The item name */
    public String getName() { return name; }

    /** @return The item price */
    public BigDecimal getPrice() { return price; }

    /**
     * Returns a string containing the menu item and its price.
     * @return String in the format "Name ($Price)"
     */
    @Override
    public String toString() { return name + " ($" + price + ")"; }
}
