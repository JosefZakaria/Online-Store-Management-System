import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://pgserver.mau.se/ap7713";
    private static final String USER = "ap7713";
    private static final String PASSWORD = "nhjhgkpd";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
