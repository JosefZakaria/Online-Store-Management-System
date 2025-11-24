import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    // Metod för att lägga till en ny kund
    public int addCustomer(String firstName, String lastName, String email, String address, String city, String country, String phone) {
        String query = "INSERT INTO Customers (first_name, last_name, email, address, city, country, phone) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING customer_id";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, city);
            preparedStatement.setString(6, country);
            preparedStatement.setString(7, phone);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("---------------- ");
                return resultSet.getInt("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Returnera -1 om kunden inte kunde läggas till
    }

    // Metod för att hämta en kunds ID baserat på e-post
    public int getCustomerIdByEmail(String email) {
        String query = "SELECT customer_id FROM Customers WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("customer_id");
            } else {
                System.out.println("Ingen kund hittades med e-post: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Returnera -1 om kunden inte hittades
    }

    // Metod för att hämta alla kunder
    public void getAllCustomers() {
        String query = "SELECT * FROM Customers";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("### Alla kunder ###");
            while (resultSet.next()) {
                System.out.println("Kund-ID: " + resultSet.getInt("customer_id"));
                System.out.println("Namn: " + resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
                System.out.println("E-post: " + resultSet.getString("email"));
                System.out.println("Telefon: " + resultSet.getString("phone"));
                System.out.println("Adress: " + resultSet.getString("address") + ", " + resultSet.getString("city") + ", " + resultSet.getString("country"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att uppdatera en kunds information
    public void updateCustomer(int customerId, String firstName, String lastName, String email, String address, String city, String country, String phone) {
        String query = "UPDATE Customers SET first_name = ?, last_name = ?, email = ?, address = ?, city = ?, country = ?, phone = ? WHERE customer_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, city);
            preparedStatement.setString(6, country);
            preparedStatement.setString(7, phone);
            preparedStatement.setInt(8, customerId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Kund med ID " + customerId + " uppdaterad!");
            } else {
                System.out.println("Ingen kund hittades med ID " + customerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att ta bort en kund
    public void deleteCustomer(int customerId) {
        String query = "DELETE FROM Customers WHERE customer_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Kund med ID " + customerId + " borttagen.");
            } else {
                System.out.println("Ingen kund hittades med ID " + customerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
