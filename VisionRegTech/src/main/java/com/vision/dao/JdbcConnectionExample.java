package com.vision.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JdbcConnectionExample {

	public static void main(String[] args) {
		// PostgreSQL database connection details
		String url = "jdbc:postgresql://10.16.1.235:5432/vision";
		String user = "visiongdi";
		String password = "Sunoida@123";

		// SQL query to execute
		String query = "SELECT * FROM VISION_TABLES";
//		String query = "SELECT * FROM \"VISION_TABLES\"";


		// Connect and execute the query
		try (Connection conn = DriverManager.getConnection(url, user, password);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			// Get metadata to fetch column names
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// Print column names and values for each row
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object value = rs.getObject(i);
					System.out.print(columnName + ": " + value + "  ");
				}
				System.out.println(); // New line after each row
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
