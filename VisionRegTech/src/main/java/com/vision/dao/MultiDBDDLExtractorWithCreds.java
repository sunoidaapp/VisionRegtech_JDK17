package com.vision.dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class MultiDBDDLExtractorWithCreds {

	public static void main(String[] args) {

		DbConfig[] configs = {
				new DbConfig("ORACLE", "jdbc:oracle:thin:@10.16.1.106:1521:visdb", "VISIONGDI", "vision123",
						"VISIONGDI", new String[] { "RG_TEMPLATE_CONFIG", "VISION_USERS" }),
				new DbConfig("MSSQL",
						"jdbc:sqlserver://10.16.1.38;instance=VISIONBISQL2019;port=52866;DatabaseName=VISION_NCBA;encrypt=false;trustServerCertificate=false",
						"Vision", "Vision_RA", "dbo",
						new String[] { "RA_Recon_Tab_Tax", "RA_RECON_TAB_TAX_HIS", "RA_Recon_Tab_Tax_PEND",
								"RA_Recon_Tab_Relations_Tax_PEND", "RA_RECON_TAB_RELATIONS_TAX_HIS",
								"RA_Recon_Tab_Relations_Tax", "RA_RECON_RULE_COLUMNS_TAX", "RA_RECON_COLUMNS_TAX_PEND",
								"RA_RECON_COLUMNS_TAX_HIS", "RA_RECON_ACT_FILTER_TAX_PEND",
								"RA_RECON_ACT_FILTER_TAX_HIS", "RA_RECON_ACT_FILTER_TAX" }) };

		for (DbConfig cfg : configs) {
			String outputFile = "C:\\Vision_Adf\\FATCA_CRS\\" + cfg.dbType + "_DDL.sql";
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
				writer.write(cfg.dbType + "\n");
				writer.write("-----------------\n");
				if (cfg.dbType.equalsIgnoreCase("ORACLE")) {
					writeOracleDDL(cfg, writer);
				} else if (cfg.dbType.equalsIgnoreCase("MSSQL")) {
					writeMSSQLDDL(cfg, writer);
				}
				System.out.println(cfg.dbType + " DDL written to " + outputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void writeOracleDDL(DbConfig cfg, BufferedWriter writer) {
		try (Connection conn = DriverManager.getConnection(cfg.url, cfg.user, cfg.password)) {
			for (String tableName : cfg.tables) {
				tableName = tableName.toUpperCase();
				writer.write("-- DDL for table: " + tableName + "\n");

				String ddlQuery = "SELECT DBMS_METADATA.GET_DDL('TABLE','" + tableName + "','"
						+ cfg.schema.toUpperCase() + "') AS DDL FROM DUAL";
				try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(ddlQuery)) {
					if (rs.next()) {
						Clob clob = rs.getClob("DDL");
						if (clob != null) {
							String ddl = clob.getSubString(1, (int) clob.length());
							writer.write(ddl + ";\n\n");
						}
					}
				}

				String trigQuery = "SELECT DBMS_METADATA.GET_DDL('TRIGGER', TRIGGER_NAME) AS TRIG FROM USER_TRIGGERS WHERE TABLE_NAME = '"
						+ tableName + "'";
				try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(trigQuery)) {
					while (rs.next()) {
						Clob trigClob = rs.getClob("TRIG");
						if (trigClob != null) {
							String trigDDL = trigClob.getSubString(1, (int) trigClob.length());
							writer.write(trigDDL + ";\n");
						}
					}
				}
			}
		} catch (Exception e) {
			try {
				writer.write("-- ERROR extracting Oracle DDL: " + e.getMessage() + "\n");
			} catch (IOException io) {
				io.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private static void writeMSSQLDDL(DbConfig cfg, BufferedWriter writer) {
		try (Connection conn = DriverManager.getConnection(cfg.url, cfg.user, cfg.password)) {
			DatabaseMetaData meta = conn.getMetaData();
			for (String tableName : cfg.tables) {
				writer.write("-- DDL for table: " + tableName + "\n");

				ResultSet columns = meta.getColumns(null, cfg.schema, tableName, null);
				StringBuilder ddl = new StringBuilder("CREATE TABLE " + tableName + " (\n");
				while (columns.next()) {
					String colName = columns.getString("COLUMN_NAME");
					String colType = columns.getString("TYPE_NAME");
					int colSize = columns.getInt("COLUMN_SIZE");
					int nullable = columns.getInt("NULLABLE");

					ddl.append("  ").append(colName).append(" ").append(colType);
					if (colSize > 0)
						ddl.append("(").append(colSize).append(")");
					if (nullable == DatabaseMetaData.columnNoNulls)
						ddl.append(" NOT NULL");
					ddl.append(",\n");
				}
				columns.close();

				ResultSet pk = meta.getPrimaryKeys(null, cfg.schema, tableName);
				StringBuilder pkCols = new StringBuilder();
				while (pk.next()) {
					if (pkCols.length() > 0)
						pkCols.append(", ");
					pkCols.append(pk.getString("COLUMN_NAME"));
				}
				pk.close();

				if (pkCols.length() > 0) {
					ddl.append("  PRIMARY KEY(").append(pkCols).append(")\n");
				} else if (ddl.toString().endsWith(",\n")) {
					ddl.setLength(ddl.length() - 2);
					ddl.append("\n");
				}

				ddl.append(");\n\n");
				writer.write(ddl.toString());

				String trigQuery = "SELECT name FROM sys.triggers WHERE parent_id = OBJECT_ID('" + cfg.schema + "."
						+ tableName + "')";
				try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(trigQuery)) {
					while (rs.next()) {
						writer.write("-- Trigger: " + rs.getString("name") + "\n");
					}
				}
			}
		} catch (Exception e) {
			try {
				writer.write("-- ERROR extracting MSSQL DDL: " + e.getMessage() + "\n");
			} catch (IOException io) {
				io.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	static class DbConfig {
		String dbType, url, user, password, schema;
		String[] tables;

		DbConfig(String dbType, String url, String user, String password, String schema, String[] tables) {
			this.dbType = dbType;
			this.url = url;
			this.user = user;
			this.password = password;
			this.schema = schema;
			this.tables = tables;
		}
	}
}
