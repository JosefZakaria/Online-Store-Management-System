import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierDAO {

    // Metod för att lägga till en ny leverantör
    public void addSupplier(String name, String phone, String address) {
        String query = "INSERT INTO Suppliers (name, phone, address) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, address);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Leverantör tillagd.");
            } else {
                System.out.println("Misslyckades med att lägga till leverantören.");
            }
        } catch (SQLException e) {
            System.out.println("Misslyckades med att lägga till leverantören.");
        }
    }

    // Metod för att visa alla leverantörer
    public void getAllSuppliers() {
        String query = "SELECT * FROM Suppliers";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("\n### Alla leverantörer ###");
            while (resultSet.next()) {
                System.out.println("Leverantör-ID: " + resultSet.getInt("supplier_id"));
                System.out.println("Namn: " + resultSet.getString("name"));
                System.out.println("Telefon: " + resultSet.getString("phone"));
                System.out.println("Adress: " + resultSet.getString("address"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            System.out.println("Misslyckades med att hämta leverantörer.");
        }
    }

    public boolean checkSupplier(int supplierId) {
        String query = "SELECT * FROM Suppliers WHERE supplier_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, supplierId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
           System.out.println("Misslyckades med att kontrollera leverantören.");
        }
        return false;
    }
}
