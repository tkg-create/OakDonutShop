package entity;

import core.DB;
import java.sql.*;
import java.util.*;

/**
 * MenuDAO handles database access for MenuItem objects, retrieving items from the Derby database.
 */
public class MenuDAO {
    private final Connection connonection;

    /**
     * Constructs a MenuDAO and connects to the database.
     * Throws RuntimeException if connection fails.
     */
    public MenuDAO() {
        try {
            connonection = DB.getConnection();
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to get database connection", ex);
        }
    }

    /**
     * Retrieves all items from the database.
     * @return List of MenuItem objects
     */
    public List<MenuItem> getAll() {
        List<MenuItem> out = new ArrayList<>();
        String sql = "SELECT MENU_ID, NAME, PRICE FROM MENU";

        try (Statement s = connonection.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                // Create MenuItem from database row
                out.add(new MenuItem(rs.getString(2), rs.getBigDecimal(3)));
            }

        } catch (SQLException ex) { ex.printStackTrace(); }
        return out;
    }

}
