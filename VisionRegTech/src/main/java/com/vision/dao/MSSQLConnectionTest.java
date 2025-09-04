package com.vision.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MSSQLConnectionTest {
    public static void main(String[] args) {
        // Define connection parameters
        String url = "jdbc:sqlserver://10.16.1.38:52866;databaseName=VISION_RA";
        //jdbc:sqlserver://10.16.1.38;instance=VISIONBISQL2019;port=52866;DatabaseName=VISION_RA;encrypt=false;trustServerCertificate=false
        String user = "Vision";
        String password = "Vision_RA";

        Connection connection = null;

        try {
            // Establish the connection
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection to SQL Server established successfully!");
            // Create a statement to fetch the database date
            String query = "SELECT GETDATE();"; // Adjust the query for your DBMS
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Print the database date
            if (resultSet.next()) {
                System.out.println("Database Date: " + resultSet.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Connection failed. Error:");
            e.printStackTrace();
        } finally {
            // Close the connection
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Connection closed.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
