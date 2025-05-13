import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ShoppingCart {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/shopping_cart";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "WJ28@krhps";
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Include it in your library path.");
        }
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}
