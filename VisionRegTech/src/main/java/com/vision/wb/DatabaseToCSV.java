package com.vision.wb;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseToCSV {

    public static void main(String[] args) {
        // Database connection details
        String jdbcUrl = "jdbc:oracle:thin:@10.16.1.106:1521:VISDB";
        String username = "devuser";
        String password = "vision123";
        
        // SQL query
        String query = "SELECT * FROM ACCOUNT_BALANCES_RG";
        
        String tmpFilePath = System.getProperty("java.io.tmpdir");
        // CSV file path
        String csvFilePath = tmpFilePath+File.separator+"output.csv";
        
        // Establish connection to the database

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);
             FileWriter fileWriter = new FileWriter(csvFilePath)) {

            // Get metadata to get column names
            int columnCount = resultSet.getMetaData().getColumnCount();
            
            // Write column names to CSV file
            for (int i = 1; i <= columnCount; i++) {
                fileWriter.append(resultSet.getMetaData().getColumnName(i));
                if (i < columnCount) {
                    fileWriter.append("|");
                }
            }
            fileWriter.append("\n");
            
            // Write data rows to CSV file
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    fileWriter.append(resultSet.getString(i));
                    if (i < columnCount) {
                        fileWriter.append("|");
                    }
                }
                fileWriter.append("\n");
            }
            
            System.out.println("Data has been successfully exported to CSV file.");

        } catch (SQLException e) {
            System.err.println("SQL exception occurred: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO exception occurred: " + e.getMessage());
        }
    }
}
