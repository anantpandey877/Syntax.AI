package ai.syntax.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {

   
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    
    public static void init(String driver, String url, String user, String password) {
        try {
            Class.forName(driver);
            dbUrl = url;
            dbUser = user;
            dbPassword = password;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver: " + driver, e);
        }
    }

   
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            System.err.println("[DbUtil ERROR] Database connection failed!");
            System.err.println("[DbUtil INFO] SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
            return null;
        }
    }
}