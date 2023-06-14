package ca.uwaterloo.cs446.journeytogether;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public Connection establishConnection() {
        Connection connection = null;
        String url = "jdbc:mysql://143.110.221.216:3306/journey_together";
        String username = "uwaterloo";
        String password = "a@XMN=FF3DB";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    // Other methods for database operations

}
