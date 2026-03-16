package com.pharmacy.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Singleton Connection Instance
    private static Connection connection;

    // Load Environment Variables using dotenv-java
    private static final Dotenv dotenv = Dotenv.load();

    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "3306");
    private static final String DB_NAME = dotenv.get("DB_NAME", "pharmacy_db");
    private static final String DB_USER = dotenv.get("DB_USER", "root");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD", "root");

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    /**
     * Gets the active database connection. Auto-reconnects if closed.
     * @return active java.sql.Connection
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                        DB_HOST, DB_PORT, DB_NAME);

                System.out.println("Attempting to connect to database at: " + DB_HOST + ":" + DB_PORT);
                connection = DriverManager.getConnection(jdbcUrl, DB_USER, DB_PASSWORD);
                System.out.println("Database Connection Successful!");
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the active database connection when the application exits.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database Connection Closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error while closing the database connection: " + e.getMessage());
        }
    }
}
