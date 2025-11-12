package program;

import entity.MenuDAO;
import entity.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Main class for Oak Donuts OD.
 * Provides GUI for the menu, adding items to cart, and checking out.
 */
public class Main {
    private JFrame frame;
    private JList<MenuItem> menuList;
    private DefaultListModel<MenuItem> menuModel;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;
    private MenuDAO menuDAO = new MenuDAO();

    /**
     * Constructs the Main GUI and loads the menu items.
     */
    public Main() {
        initUI();
        loadMenu();
        updateTotal();
    }

    /**
     * Initializes the  UI components like the menu list, cart table, buttons, and layout.
     */
    private void initUI() {
        frame = new JFrame("Oak Donuts OD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Menu panel
        JPanel menuPanel = new JPanel(new BorderLayout());
        JLabel menuLabel = new JLabel("Menu");
        menuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuPanel.add(menuLabel, BorderLayout.NORTH);

        menuModel = new DefaultListModel<>();
        menuList = new JList<>(menuModel);
        menuPanel.add(new JScrollPane(menuList), BorderLayout.CENTER);

        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartPanel.add(cartLabel, BorderLayout.NORTH);

        // Cart table
        cartModel = new DefaultTableModel(new Object[]{"Item","Quantity","Total Cost"},0);
        cartTable = new JTable(cartModel);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        mainPanel.add(cartPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        Integer[] quantities = {1, 2, 3, 4, 5};
        JComboBox<Integer> quantitySelector = new JComboBox<>(quantities);
        quantitySelector.setSelectedItem(1);

        buttonPanel.add(new JLabel("Quantity:"));
        buttonPanel.add(quantitySelector);
        JButton buttonAdd = new JButton("Add to Cart →");
        JButton buttonRemove = new JButton("Remove");
        JButton buttonCheckout = new JButton("Checkout");
        JButton buttonOrders = new JButton("View Past Orders");
        totalLabel = new JLabel("Total: $0.00");

        buttonPanel.add(buttonAdd);
        buttonPanel.add(buttonRemove);
        buttonPanel.add(totalLabel);
        buttonPanel.add(buttonCheckout);
        buttonPanel.add(buttonOrders);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Button to add to Cart
        buttonAdd.addActionListener(e -> {
            MenuItem selection = menuList.getSelectedValue();
            if (selection == null) { JOptionPane.showMessageDialog(frame, "Select a menu item"); return; }

            int quantityToAdd = (Integer) quantitySelector.getSelectedItem();

            // Check if item already in cart
            for (int r=0; r<cartModel.getRowCount(); r++) {
                if (cartModel.getValueAt(r,0).equals(selection.getName())) {
                    int q = (int) cartModel.getValueAt(r,1) + quantityToAdd;
                    cartModel.setValueAt(q, r, 1);

                    BigDecimal total = selection.getPrice().multiply(new BigDecimal(q));
                    cartModel.setValueAt("$"+total.toPlainString(), r, 2);

                    updateTotal();
                    return;
                }
            }

            // Add new row if not already in cart
            BigDecimal total = selection.getPrice().multiply(new BigDecimal(quantityToAdd));
            cartModel.addRow(new Object[]{selection.getName(), quantityToAdd, "$"+total.toPlainString()});
            updateTotal();
        });

        // Remove button
        buttonRemove.addActionListener(e -> {
            int r = cartTable.getSelectedRow();
            if (r>=0) { cartModel.removeRow(r); updateTotal(); }
        });

        // Checkout button
        buttonCheckout.addActionListener(e -> {
            if (cartModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame, "Cart is empty");
                return;
            }

            BigDecimal total = BigDecimal.ZERO;
            StringBuilder items = new StringBuilder();

            // Compile list of items and total cost
            for (int r = 0; r < cartModel.getRowCount(); r++) {
                String name = cartModel.getValueAt(r, 0).toString();
                int q = (int) cartModel.getValueAt(r, 1);
                BigDecimal lineTotal = new BigDecimal(cartModel.getValueAt(r, 2).toString().replace("$", ""));
                total = total.add(lineTotal);

                if (items.length() > 0) items.append(", ");
                items.append(name).append(" x").append(q);
            }

            // Store order  in database
            try {
                entity.OrderDAO orderDAO = new entity.OrderDAO();
                entity.OrderRecord record = new entity.OrderRecord(items.toString(), total, java.time.LocalDateTime.now());
                orderDAO.insertOrder(record);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving order: " + ex.getMessage());
                return;
            }

            // Confirmation popup
            JOptionPane.showMessageDialog(
                    frame,
                    "Your order has been placed!\nTotal: $" +
                            total.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString()
            );

            // Clear cart after checkout
            cartModel.setRowCount(0);
            updateTotal();
        });

        // Past orders button
        buttonOrders.addActionListener(e -> {
            try {
                entity.OrderDAO orderDAO = new entity.OrderDAO();
                java.util.List<entity.OrderRecord> orders = orderDAO.getAllOrders();

                // Create window
                JDialog dialog = new JDialog(frame, "Past Orders", true);
                dialog.setSize(500, 300);
                dialog.setLocationRelativeTo(frame);

                // Setup table
                String[] columns = {"Order ID", "Items", "Total", "Order Time"};
                javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0);

                // Fill table
                for (entity.OrderRecord o : orders) {
                    model.addRow(new Object[]{
                            o.getOrderId(),
                            o.getItems(),
                            "$" + o.getTotal().setScale(2, BigDecimal.ROUND_HALF_EVEN),
                            o.getOrderTime().toString()
                    });
                }

                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
                dialog.add(scrollPane, BorderLayout.CENTER);

                // Close button
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(ev -> dialog.dispose());
                JPanel bottomPanel = new JPanel();
                bottomPanel.add(closeButton);
                dialog.add(bottomPanel, BorderLayout.SOUTH);

                dialog.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error loading past orders: " + ex.getMessage());
            }
        });



        frame.setVisible(true);
    }

    /**
     * Loads menu items from the database.
     */
    private void loadMenu() {
        menuModel.clear();
        for (MenuItem m : menuDAO.getAll()) menuModel.addElement(m);
    }

    /**
     * Updates total cost label.
     */
    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (int r=0; r<cartModel.getRowCount(); r++) {
            BigDecimal line = new BigDecimal(cartModel.getValueAt(r,2).toString().replace("$",""));
            total = total.add(line);
        }
        totalLabel.setText("Total: $"+total.setScale(2, BigDecimal.ROUND_HALF_EVEN).toPlainString());
    }

    /**
     * Launches the GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
