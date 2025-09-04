package com.vision.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcSQLServerConnection {
		public void dbConnect(String db_connect_string,
	            String db_userid,
	            String db_password)
	   {
	      try {
	         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	         System.out.println("Connecting...");
	         Connection conn = DriverManager.getConnection(db_connect_string,
	                  db_userid, db_password);
	         System.out.println("connected");
	         Statement statement = conn.createStatement();
	         String queryString = "select * from error_codes where error_type = 3";
	         ResultSet rs = statement.executeQuery(queryString);
	         while (rs.next()) {
	            System.out.println(rs.getString(1)+" - "+rs.getString(2));
	            System.out.println();
	         }
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	   }
	   public static void main(String[] args)
	   {
		   JdbcSQLServerConnection connServer = new JdbcSQLServerConnection();
		/*
		 * spring.datasource.url=jdbc:sqlserver://SERVER115;instance=SQL2012;port=1450;
		 * DatabaseName=VISION
		 * spring.datasource.url=jdbc:sqlserver://10.16.1.29\\VISIONBISQL2019;
		 * DatabaseName=VISION_BNR
		 */
	      connServer.dbConnect("jdbc:sqlserver://10.16.1.29\\VISIONBISQL2019;databaseName=VISION_BNR", "VISION","Vision@12");
	   }		
}
