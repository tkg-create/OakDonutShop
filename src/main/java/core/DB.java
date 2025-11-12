package core;

import java.sql.*;

/**
 * DB manages the Derby connection and schema, and ensures the existence of the menu table.
 */
public class DB {
    private static final String DB_URL = "jdbc:derby:oakdb;create=true";
    private static Connection connonection;

    /**
     * Returns the connection and creates it if needed
     */
    public static Connection getConnection() throws SQLException {
        if (connonection == null || connonection.isClosed()) {
            connonection = DriverManager.getConnection(DB_URL);
            ensureSchema(); // ensure table exists and has initial data
        }
        return connonection;
    }

    /**
     * Makes sure menu table exists and inserts the menu items
     */
    private static void ensureSchema() {
        try (Statement stmt = getConnection().createStatement()) {
            DatabaseMetaData md = getConnection().getMetaData();

            // Check if menu table exists
            try (ResultSet rs = md.getTables(null, null, "MENU", null)) {
                if (!rs.next()) {
                    stmt.executeUpdate(
                            "CREATE TABLE MENU (" +
                                    "MENU_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                                    "NAME VARCHAR(100), PRICE DECIMAL(10,2))"
                    );

                    // Insert menu items
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Glazed Donut', 1.49)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Chocolate Frosted Donut', 1.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Maple Bar', 2.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Boston Creme Donut', 2.49)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Apple Cider Donut', 1.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Apple Fritter', 2.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Jelly Donut', 2.49)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Donut Holes', 2.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Apple Strudel', 2.49)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Blueberry Tart', 2.49)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Cinnamon Coffee Cake', 2.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Chocolate Croissant', 1.99)");
                    stmt.executeUpdate("INSERT INTO MENU (NAME, PRICE) VALUES ('Chocolate Éclair', 1.99)");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
