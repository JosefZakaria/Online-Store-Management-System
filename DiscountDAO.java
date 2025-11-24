import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class DiscountDAO {

    // Metod för att lägga till en ny rabatt
    public void addDiscount(String code, double percentage, String reason, String startDate, String endDate) {
        String query = """
        INSERT INTO Discounts (code, percentage, reason, start_date, end_date)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, code);
            preparedStatement.setDouble(2, percentage);
            preparedStatement.setString(3, reason);
            preparedStatement.setDate(4, java.sql.Date.valueOf(startDate)); // Konvertera startdatum
            preparedStatement.setDate(5, java.sql.Date.valueOf(endDate));   // Konvertera slutdatum

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Rabattkod tillagd: " + code);
            } else {
                System.out.println("Misslyckades med att lägga till rabattkod.");
            }
        } catch (SQLException e) {
            System.err.println("Fel vid tillägg av rabattkod: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metod för att visa rabatt-historik
    public void getDiscountHistory() {
        String query = """
            SELECT p.name AS product_name, d.code, d.percentage, d.reason, d.start_date, d.end_date
            FROM Discounts d
            JOIN ProductDiscounts pd ON d.discount_id = pd.discount_id
            JOIN Products p ON pd.product_id = p.product_id
            """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             var resultSet = preparedStatement.executeQuery()) {

            System.out.println("\n### Rabatt-historik ###");
            while (resultSet.next()) {
                System.out.println("Produkt: " + resultSet.getString("product_name"));
                System.out.println("Rabattkod: " + resultSet.getString("code"));
                System.out.println("Procent: " + resultSet.getDouble("percentage"));
                System.out.println("Anledning: " + resultSet.getString("reason"));
                System.out.println("Giltig från: " + resultSet.getDate("start_date"));
                System.out.println("Giltig till: " + resultSet.getDate("end_date"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metod för att visa alla rabatter
    public void getAllDiscounts() {
        String query = "SELECT * FROM Discounts";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("\n### Alla rabatter ###");
            while (resultSet.next()) {
                System.out.println("Rabatt-ID: " + resultSet.getInt("discount_id"));
                System.out.println("Kod: " + resultSet.getString("code"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
           System.out.println("Misslyckades med att hämta rabatter.");

        }
    }
    public void viewAllDiscounts() {
        String query = "SELECT * FROM Discounts";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("### Lista över rabatter ###");
            while (resultSet.next()) {
                System.out.println("Rabatt-ID: " + resultSet.getInt("discount_id"));
                System.out.println("Kod: " + resultSet.getString("code"));
                System.out.println("Procent: " + resultSet.getDouble("percentage") + "%");
                System.out.println("Anledning: " + resultSet.getString("reason"));
                System.out.println("Startdatum: " + resultSet.getDate("start_date"));
                System.out.println("Slutdatum: " + resultSet.getDate("end_date"));
                System.out.println("--------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Fel vid hämtning av rabatter: " + e.getMessage());
            System.out.println("Misslyckades med att hämta rabatter.");
        }
    }
    public void removeDiscountFromProduct(int discountId, int productId) {
        String query = "DELETE FROM ProductDiscounts WHERE discount_id = ? AND product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, discountId);
            preparedStatement.setInt(2, productId);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Rabatt-ID " + discountId + " har tagits bort från produkt-ID " + productId + ".");
            } else {
                System.out.println("Ingen rabatt hittades för den valda produkten.");
            }
        } catch (SQLException e) {
            System.err.println("Fel vid borttagning av rabatt från produkt: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void assignDiscountToProduct(int discountId, int productId) {
        String query = "INSERT INTO ProductDiscounts (discount_id, product_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, discountId);
            preparedStatement.setInt(2, productId);

            preparedStatement.executeUpdate();
            System.out.println("Rabatt-ID " + discountId + " har kopplats till produkt-ID " + productId + ".");
        } catch (SQLException e) {
            System.err.println("Fel vid koppling av rabatt till produkt: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
