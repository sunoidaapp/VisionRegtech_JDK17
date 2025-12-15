package com.vision.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMssqlConnection {

    // Change these as per your DB details
    private static final String URL =
            "jdbc:sqlserver://YOUR_SERVER:1433;databaseName=YOUR_DB;encrypt=false";

    private static final String USER = "YOUR_USERNAME";
    private static final String PASSWORD = "YOUR_PASSWORD";

    public static void main(String[] args) {
        Connection connection = null;

        try {
            // Load SQL Server JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            System.out.println("Connecting to SQL Server...");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            if (connection != null) {
                System.out.println("Connection Successful!");
            } else {
                System.out.println("Connection Failed!");
            }

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection Closed.");
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
