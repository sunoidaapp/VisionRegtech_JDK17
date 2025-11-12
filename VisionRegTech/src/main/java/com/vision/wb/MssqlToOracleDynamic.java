package com.vision.wb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

public class MssqlToOracleDynamic {

	public static void main(String[] args) {
		// MSSQL connection details
		String mssqlUrl = "jdbc:sqlserver://10.16.1.38;instance=VISIONBISQL2019;port=52866;DatabaseName=VISION_fatca;encrypt=false;trustServerCertificate=false";
		String mssqlUser = "Vision";
		String mssqlPass = "Vision_RA";

		// Oracle connection details
		String oracleUrl = "jdbc:oracle:thin:@202.83.25.244:1521:visdb";
		String oracleUser = "visiongdi";
		String oraclePass = "vision123";

		String tableName = "CUSTOMER_MANUAL_COL_LIST";
		String whereCondition = "1=1"; // dynamic filter

		try (Connection mssqlConn = DriverManager.getConnection(mssqlUrl, mssqlUser, mssqlPass);
				Connection oracleConn = DriverManager.getConnection(oracleUrl, oracleUser, oraclePass);
				Statement stmt = mssqlConn.createStatement();
				Statement oracleStmt = oracleConn.createStatement();) {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// ✅ Disable auto-commit so manual commit works
			oracleConn.setAutoCommit(false);

			// ✅ Generate date-based backup table name
			String today = new SimpleDateFormat("ddMMyyyy").format(new Date());
			String backupTable = tableName + "_" + today;

			// ✅ Step 1: Create backup table with today’s date
			try {
				String createBackup = "CREATE TABLE " + backupTable + " AS SELECT * FROM " + tableName;
				int rowsCopied = oracleStmt.executeUpdate(createBackup);
				System.out.println("Backup created: " + backupTable + " → " + rowsCopied + " rows copied.");
			} catch (Exception ex) {
				System.out.println("⚠ Backup table already exists: " + backupTable);
			}

			// ✅ Step 2: Query MSSQL source data
			String query = "SELECT * FROM " + tableName + " WHERE " + whereCondition;
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			// Build dynamic insert for Oracle
			StringJoiner colNames = new StringJoiner(", ");
			StringJoiner placeholders = new StringJoiner(", ");
			boolean[] skip = new boolean[columnCount + 1];

			for (int i = 1; i <= columnCount; i++) {
				String colName = rsmd.getColumnName(i).toUpperCase();
				if (colName.equals("ROWID") || colName.equals("ORA_ROWSCN")) {
					skip[i] = true;
					continue;
				}
				colNames.add(colName);
				placeholders.add("?");
			}

			String insertSQL = "INSERT INTO " + tableName + " (" + colNames + ") VALUES (" + placeholders + ")";
			System.out.println("Generated Insert SQL: " + insertSQL);

			try (PreparedStatement pstmt = oracleConn.prepareStatement(insertSQL)) {
				int rowCount = 0;

				while (rs.next()) {
					int paramIndex = 1;
					for (int i = 1; i <= columnCount; i++) {
						if (skip[i])
							continue;
						pstmt.setObject(paramIndex++, rs.getObject(i));
					}
					pstmt.addBatch();
					rowCount++;
				}

				pstmt.executeBatch();
				oracleConn.commit();
				System.out.println("✅ " + rowCount + " records transferred into " + tableName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
