package at.fhtw.sampleapp.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE; // Singleton pattern using enum to ensure single instance

    // Method to establish a connection to the database
    public Connection getConnection()
    {
        try {
            // Attempt to connect to the database using JDBC
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/postgres", // Database URL
                    "postgres", // Username
                    "cs2022Hu$"); // Password
        } catch (SQLException e) {
            // Throws a custom exception if the connection attempt fails
            throw new DataAccessException("Failed to establish database connection", e);
        }
    }

    public static void main(String[] args) {
        // Attempt to get a database connection
        Connection connection = DatabaseManager.INSTANCE.getConnection();

        // Check if the connection was successfully established
        if (connection != null) {
            System.out.println("Database connection established successfully!");
        } else {
            System.out.println("Failed to establish database connection.");
        }

        try {
            String sql = "SELECT * FROM users"; // SQL query to select all users

            // Prepare the SQL statement for execution
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            // Execute the query
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            // Throws a custom exception if the query execution fails
            throw new DataAccessException("Query execution failed", e);
        }

        // Close the connection when done (important if not using a connection pool)
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            // Print the stack trace if closing the connection fails
            e.printStackTrace();
        }
    }
}