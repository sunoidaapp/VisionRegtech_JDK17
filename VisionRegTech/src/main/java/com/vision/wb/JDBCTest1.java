package com.vision.wb;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class JDBCTest1 {
	/*
	 * public static void main(String args[]) throws SQLException { Connection con =
	 * null; try{ Class.forName("oracle.jdbc.driver.OracleDriver"); String dbURL =
	 * "jdbc:oracle:thin:@10.16.1.101:1521:VISIONBI"; System.out.println("jdbcurl="
	 * + dbURL); String strUserID = "devuser"; String strPassword = "vision123";
	 * System.out.println("Connecting database......");
	 * con=DriverManager.getConnection(dbURL,strUserID,strPassword);
	 * System.out.println("Connected to the database."); Statement
	 * stmt=con.createStatement(); System.out.println("Executing query"); ResultSet
	 * rs=stmt.executeQuery("SELECT sysdate FROM DUAL"); while(rs.next())
	 * System.out.println(rs.getString("1")); con.close(); }catch(Exception e){
	 * System.out.println(e);} finally { if(con!=null){ con.close(); } } }
	 */
//	public static void main(String[] args) {
//
//		Connection conn = null;
//
//		try {
//
//			String dbURL = "jdbc:oracle:thin:@10.16.1.101:1521:VISIONBI";
//			String user = "devuser";
//			String pass = "vision123";
//			conn = DriverManager.getConnection(dbURL, user, pass);
//			if (conn != null) {
//				
//				System.out.println("Driver ip: " + dbURL );
//							}
//System.out.println("Connected to Database...!!! " );
//
//		} catch (SQLException ex) {
//System.out.println("Not Connected to Database...!!! " );
//			ex.printStackTrace();
//		} finally {
//			try {
//				if (conn != null && !conn.isClosed()) {
//					conn.close();
//				}
//			} catch (SQLException ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
	
	public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // Customize the format as needed

        try {
            // Parse the timestamp string representation to a Date
            Date date = inputFormat.parse(timestamp.toString());
            // Format the Date to the desired output format
            String formattedDate = outputFormat.format(date);
            System.out.println("Formatted Date: " + formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}