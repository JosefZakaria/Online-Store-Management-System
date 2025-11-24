import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {

    // Metod för att hämta alla produkter
    public void getAllProducts() {
        String query = "SELECT * FROM Products";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("### Lista över produkter ###");
            while (resultSet.next()) {
                System.out.println("Produkt-ID: " + resultSet.getInt("product_id"));
                System.out.println("Namn: " + resultSet.getString("name"));
                System.out.println("Kod: " + resultSet.getString("code"));
                System.out.println("Pris: " + resultSet.getDouble("base_price"));
                System.out.println("Kvantitet: " + resultSet.getInt("quantity"));
                System.out.println("--------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av produkter: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Metod för att söka produkter efter namn
    public void searchProductsByName(String name) {
        String query = "SELECT * FROM Products WHERE name ILIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Produkt-ID: " + resultSet.getInt("product_id"));
                System.out.println("Namn: " + resultSet.getString("name"));
                System.out.println("Pris: " + resultSet.getDouble("base_price"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att söka produkter efter leverantör

    /*


    public void searchProductsBySupplier(String supplierName) {
        String query = """
            SELECT p.* FROM Products p
            JOIN Suppliers s ON p.supplier_id = s.supplier_id
            WHERE s.name ILIKE ?""";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + supplierName + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Produkt-ID: " + resultSet.getInt("product_id"));
                System.out.println("Namn: " + resultSet.getString("name"));
                System.out.println("Pris: " + resultSet.getDouble("base_price"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
    // Metod för att hämta rabatterade produkter
    public void getDiscountedProducts() {
        String query = """
            SELECT p.name, p.base_price, d.percentage, 
                   (p.base_price * (1 - d.percentage / 100)) AS discounted_price
            FROM Products p
            JOIN ProductDiscounts pd ON p.product_id = pd.product_id
            JOIN Discounts d ON pd.discount_id = d.discount_id
            WHERE CURRENT_DATE BETWEEN d.start_date AND d.end_date""";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("Produkt: " + resultSet.getString("name"));
                System.out.println("Ordinarie pris: " + resultSet.getDouble("base_price"));
                System.out.println("Rabatt: " + resultSet.getDouble("percentage") + "%");
                System.out.println("Rabatterat pris: " + resultSet.getDouble("discounted_price"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att kontrollera lager
    public boolean checkStock(int productId, int quantity) {
        String query = "SELECT quantity FROM Products WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("quantity") >= quantity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Metod för att lägga till en ny produkt
    public void addProduct(String name, String code, double price, int quantity, int supplierId, int discountId) {
        String product = "INSERT INTO Products (name, code, base_price, quantity) VALUES (?, ?, ?, ?)";
        String productsuppliers = "INSERT INTO productsuppliers (product_id, supplier_id) VALUES (?, ?)";
        String productdiscounts = "INSERT INTO productdiscounts (product_id, discount_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(product, PreparedStatement.RETURN_GENERATED_KEYS)) {


            preparedStatement.setString(1, name);
            preparedStatement.setString(2, code);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, quantity);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int productId = generatedKeys.getInt(1);
                        System.out.println("Produkt-ID: " + productId);


                        if (supplierId != 0){
                            try (PreparedStatement preparedStatement2 = connection.prepareStatement(productsuppliers)) {
                                preparedStatement2.setInt(1, productId);
                                preparedStatement2.setInt(2, supplierId);
                                preparedStatement2.executeUpdate();
                            }
                        }

                        if (discountId != 0) {
                            try (PreparedStatement preparedStatement3 = connection.prepareStatement(productdiscounts)) {
                                preparedStatement3.setInt(1, productId);
                                preparedStatement3.setInt(2, discountId);
                                preparedStatement3.executeUpdate();
                            }
                        }
                        System.out.println("Produkt tillagd!");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Misslyckades med att lägga till produkten.");

        }
}
    // Metod för att uppdatera produktkvantitet
    public void updateProductQuantity(int productId, int newQuantity) {
        String query = "UPDATE products SET quantity = ? WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, newQuantity);
            preparedStatement.setInt(2, productId);

            preparedStatement.executeUpdate();
            System.out.println("Produktkvantitet uppdaterad!");

        } catch (SQLException e) {
            System.out.println("Misslyckades med att uppdatera produktkvantitet.");
        }
    }

    // Metod för att ta bort en produkt
    public void deleteProduct(int productId) {
        String deleteDiscounts = "DELETE FROM ProductDiscounts WHERE product_id = ?";
        String deleteSuppliers = "DELETE FROM ProductSuppliers WHERE product_id = ?";
        String deleteOrders = "DELETE FROM OrderDetails WHERE product_id = ?";
        String deleteProduct = "DELETE FROM Products WHERE product_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false); // Börja transaktionen

            // Ta bort från relaterade tabeller
            try (PreparedStatement stmt1 = connection.prepareStatement(deleteDiscounts);
                 PreparedStatement stmt2 = connection.prepareStatement(deleteSuppliers);
                 PreparedStatement stmt3 = connection.prepareStatement(deleteOrders)) {

                stmt1.setInt(1, productId);
                stmt1.executeUpdate();

                stmt2.setInt(1, productId);
                stmt2.executeUpdate();

                stmt3.setInt(1, productId);
                stmt3.executeUpdate();
            }

            // Ta bort själva produkten
            try (PreparedStatement stmt4 = connection.prepareStatement(deleteProduct)) {
                stmt4.setInt(1, productId);
                int rowsDeleted = stmt4.executeUpdate();

                if (rowsDeleted > 0) {
                    System.out.println("Produkten har tagits bort.");
                    connection.commit(); // Bekräfta transaktionen
                } else {
                    System.out.println("Produkten hittades inte.");
                    connection.rollback(); // Återställ om produkten inte hittades
                }
            }
        } catch (SQLException e) {
            System.out.println("Misslyckades med att ta bort produkten.");
            e.printStackTrace();
        }
    }

    public void getProductsWithDiscounts() {
        String query = """
        SELECT p.product_id, p.name AS product_name, d.discount_id, d.code AS discount_code
        FROM Products p
        JOIN ProductDiscounts pd ON p.product_id = pd.product_id
        JOIN Discounts d ON pd.discount_id = d.discount_id
    """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("### Lista över produkter med rabatter ###");
            while (resultSet.next()) {
                System.out.println("Produkt-ID: " + resultSet.getInt("product_id"));
                System.out.println("Produktnamn: " + resultSet.getString("product_name"));
                System.out.println("Rabatt-ID: " + resultSet.getInt("discount_id"));
                System.out.println("Rabattkod: " + resultSet.getString("discount_code"));
                System.out.println("--------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av produkter med rabatter: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void addProduct(String name, String code, double price, int quantity, int supplierId) {

    }

    public boolean doesProductExist(int productId) {
        String query = "SELECT COUNT(*) AS count FROM Products WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
