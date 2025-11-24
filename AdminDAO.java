import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {


    // Metod för att kontrollera om en admin är giltig
    public boolean getAdmin(String username, String password) {
        String query = "SELECT admin_id FROM admins WHERE username = ?" +
                " AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int adminId = resultSet.getInt("admin_id");
                if (adminId > 0) {
                    return true;
                }
            } else {
                System.out.println("Ingen admin hittades med dessa uppgifter.");
            }
        } catch (SQLException e) {
            System.out.println("Något gick fel. Försök igen.");
        }
        return false; // Returnera false om admin inte hittades
    }
}
