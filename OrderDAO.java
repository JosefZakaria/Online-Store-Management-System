import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {

    private int customerId;
    private int orderId;

    // Metod för att lägga till en produkt till en order
    public void addProductToOrder(int orderId, int productId, int quantity) {
        String query = """
        INSERT INTO OrderDetails (order_id, product_id, quantity, final_price)
        SELECT ?, ?, ?, (p.base_price * ?)
        FROM Products p
        WHERE p.product_id = ?;
    """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setInt(4, quantity);
            preparedStatement.setInt(5, productId);

            preparedStatement.executeUpdate();
            System.out.println("Produkten har lagts till i order-ID: " + orderId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att bekräfta en kunds order

    public void submitOrder(int customerId) {
        String query = "UPDATE Orders SET is_confirmed = TRUE WHERE customer_id = ? AND is_confirmed = FALSE";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Order bekräftad!");
            } else {
                System.out.println("Ingen obekräftad order hittades.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att rensa obekräftade orderdetaljer
    public void clearOrder(int customerId) {
        String query = """
            DELETE FROM OrderDetails
            WHERE order_id = (SELECT order_id FROM Orders WHERE customer_id = ? AND is_confirmed = FALSE)""";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order rensad.");
            } else {
                System.out.println("Ingen obekräftad order att rensa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att ta bort en obekräftad order
    public boolean deleteUnconfirmedOrder(int customerId, int orderId) {
        String deleteOrderDetailsQuery = "DELETE FROM OrderDetails WHERE order_id = ?";
        String deleteOrderQuery = "DELETE FROM Orders WHERE order_id = ? AND customer_id = ? AND is_confirmed = FALSE";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Starta en transaktion
            connection.setAutoCommit(false);

            // Ta bort från OrderDetails
            try (PreparedStatement deleteOrderDetailsStmt = connection.prepareStatement(deleteOrderDetailsQuery)) {
                deleteOrderDetailsStmt.setInt(1, orderId);
                deleteOrderDetailsStmt.executeUpdate();
            }

            // Ta bort från Orders
            try (PreparedStatement deleteOrderStmt = connection.prepareStatement(deleteOrderQuery)) {
                deleteOrderStmt.setInt(1, orderId);
                deleteOrderStmt.setInt(2, customerId);
                int rowsDeleted = deleteOrderStmt.executeUpdate();

                if (rowsDeleted > 0) {
                    // Bekräfta transaktionen
                    connection.commit();
                    System.out.println("Obekräftad order med ID " + orderId + " har tagits bort.");
                    return true;
                } else {
                    System.out.println("Ingen obekräftad order hittades med ID " + orderId);
                    connection.rollback(); // Återställ vid fel
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double calculateTotalPrice(int orderId) {
        String query = "SELECT final_price FROM OrderDetails WHERE order_id = ?";
        double totalPrice = 0.0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalPrice += resultSet.getDouble("final_price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPrice;
    }
    public void updateOrderTotalInJava(int orderId) {
        double totalPrice = calculateTotalPrice(orderId);

        String query = "UPDATE Orders SET total_price = ? WHERE order_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, totalPrice);
            preparedStatement.setInt(2, orderId);

            preparedStatement.executeUpdate();
            System.out.println("Totalpriset för order-ID " + orderId + " har uppdaterats till: " + totalPrice);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Metod för att visa en kunds ordrar
    public void viewOrdersByCustomer(int customerId) {
        String query = """
        SELECT 
            o.order_id,
            o.total_price,
            o.is_confirmed,
            o.order_date,
            p.name AS product_name,
            od.quantity,
            od.final_price
        FROM Orders o
        JOIN OrderDetails od ON o.order_id = od.order_id
        JOIN Products p ON od.product_id = p.product_id
        WHERE o.customer_id = ?
        ORDER BY o.order_date DESC, o.order_id;
    """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            int currentOrderId = -1;
            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");

                if (orderId != currentOrderId) {
                    currentOrderId = orderId;
                    System.out.println("\n### Order-ID: " + orderId + " ###");
                    System.out.println("Totalt pris: " + resultSet.getDouble("total_price"));
                    System.out.println("Bekräftad: " + (resultSet.getBoolean("is_confirmed") ? "Ja" : "Nej"));
                    System.out.println("Orderdatum: " + resultSet.getTimestamp("order_date"));
                    System.out.println("Produkter:");
                }

                System.out.println(" - Produkt: " + resultSet.getString("product_name"));
                System.out.println("   Antal: " + resultSet.getInt("quantity"));
                System.out.println("   Pris: " + resultSet.getDouble("final_price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    // Metod för att bekräfta ordrar
    public void confirmOrders() {
        String querySelect = "SELECT * FROM Orders WHERE is_confirmed = FALSE";
        String queryUpdate = "UPDATE Orders SET is_confirmed = TRUE WHERE order_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(querySelect);
             PreparedStatement updateStatement = connection.prepareStatement(queryUpdate);
             ResultSet resultSet = selectStatement.executeQuery()) {

            System.out.println("### Bekräftar ordrar ###");
            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                int customerId = resultSet.getInt("customer_id");
                double totalPrice = resultSet.getDouble("total_price");

                System.out.println("Order-ID: " + orderId);
                System.out.println("Kund-ID: " + customerId);
                System.out.println("Totalt pris: " + totalPrice);

                // Uppdatera orderstatus till bekräftad
                updateStatement.setInt(1, orderId);
                updateStatement.executeUpdate();
                System.out.println("Order-ID " + orderId + " bekräftad.");
                System.out.println("----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att visa produkter med flest ordrar per månad
    public void getTopProductsByMonth() {
        String query = """
            
                SELECT p.name AS product_name, DATE_TRUNC('month', o.order_date) AS order_month, SUM(od.quantity) AS total_quantity
            FROM Orders o
            JOIN OrderDetails od ON o.order_id = od.order_id
            JOIN Products p ON od.product_id = p.product_id
            GROUP BY p.name, order_month
            ORDER BY order_month, total_quantity DESC""";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("### Produkter med flest ordrar per månad ###");
            while (resultSet.next()) {
                System.out.println("Produkt: " + resultSet.getString("product_name"));
                System.out.println("Månad: " + resultSet.getDate("order_month"));
                System.out.println("Antal sålda: " + resultSet.getInt("total_quantity"));
                System.out.println("----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Metod för att lägga till en ny order
    public int addOrder(int customerId, boolean isConfirmed) {
        String query = """
        INSERT INTO Orders (customer_id, total_price, is_confirmed)
        VALUES (?, 
               (SELECT COALESCE(SUM(final_price), 0) 
                FROM OrderDetails od
                JOIN Orders o ON od.order_id = o.order_id
                WHERE o.customer_id = ? AND o.is_confirmed = FALSE), 
               ?)
        RETURNING order_id;
    """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, customerId);
            preparedStatement.setBoolean(3, isConfirmed);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("order_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void getAllOrders() {
        String query = "SELECT * FROM Orders";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("### Alla ordrar ###");
            while (resultSet.next()) {
                System.out.println("Order-ID: " + resultSet.getInt("order_id"));
                System.out.println("Kund-ID: " + resultSet.getInt("customer_id"));
                System.out.println("Totalpris: " + resultSet.getDouble("total_price"));
                System.out.println("Bekräftad: " + resultSet.getBoolean("is_confirmed"));
                System.out.println("Orderdatum: " + resultSet.getTimestamp("order_date"));
                System.out.println("----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getOrderStatus(int orderId) {
        String query = "SELECT is_confirmed FROM Orders WHERE order_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("is_confirmed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
    }
        return false;

    }
    public int getUnconfirmedOrderId(int customerId) {
        String query = "SELECT order_id FROM Orders WHERE customer_id = ? AND is_confirmed = FALSE";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("order_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Returnera -1 om ingen obekräftad order hittas
    }

}
