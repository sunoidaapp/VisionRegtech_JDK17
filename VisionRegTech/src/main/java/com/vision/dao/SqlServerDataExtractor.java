package com.vision.dao;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SqlServerDataExtractor{

	private	final static int ERRONEOUS_EXIT	=1;
	private	final static int SUCCESSFUL_EXIT=0;
	private	static String visionUserId = null;
	private	static String visionPassword = null;
	private	static String connectionString =  "jdbc:oracle:thin:@10.1.0.45:";//"jdbc:oracle:thin:@localhost:"
	private	static String port = "1521";
	private	static PrintWriter logWriter;
	private	static Connection visionConnection;

	private	static void closeOpenResources(){
		try {
			if(visionConnection != null){
				visionConnection.close();
				visionConnection = null;
			}
		}catch (SQLException e)	{
			logWriter.printf("Error	closing	the vision database connection.	Msg[%s]",e.getMessage());
		}
		if(logWriter !=	null){
			logWriter.flush();
			logWriter.close();
			logWriter = null;
		}
	}
	private	static List<FeedDetails> getProcess(){
		String query = "SELECT VSD.COUNTRY,VSD.LE_BOOK,FEED_NAME,IP_ADDRESS, DATABASE_TYPE, DATABASE_NAME,DATABASE_PORT,USER_NAME,PASSWORD,QUERY,COMPLETED_FLAG,"+
						" Format(DATE_LAST_RUN,'RRRR-MM-DD') DATE_LAST_RUN, DESTINATION_TABLE, TRUNCATE_FLAG FROM VISION_SQLSERVER_DETAILS VSD, VISION_BUSINESS_DAY VBD"+
						" WHERE VSD.DATE_LAST_RUN != VBD.BUSINESS_DATE AND VSD.COUNTRY = VBD.COUNTRY AND VSD.LE_BOOK = VBD.LE_BOOK";
		List<FeedDetails> result = new ArrayList<FeedDetails>();
		Statement statement = null;
		ResultSet rs = null;
		try {
			statement = visionConnection.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()){
					FeedDetails feedDetails	= new FeedDetails();
					feedDetails.setCountry(rs.getString("COUNTRY"));
					feedDetails.setLeBook(rs.getString("LE_BOOK"));
					feedDetails.setFeedName(rs.getString("FEED_NAME"));
					feedDetails.setIpAddress(rs.getString("IP_ADDRESS"));
					feedDetails.setDataBaseType(rs.getString("DATABASE_TYPE"));
					feedDetails.setDataBaseName(rs.getString("DATABASE_NAME"));
					feedDetails.setPort(rs.getString("DATABASE_PORT"));
					feedDetails.setUserName(rs.getString("USER_NAME"));
					feedDetails.setPassword(rs.getString("PASSWORD"));
					feedDetails.setQuery(rs.getString("QUERY"));
					feedDetails.setCompletedFlag(rs.getString("COMPLETED_FLAG"));
					feedDetails.setDateLastRun(rs.getString("DATE_LAST_RUN"));
					feedDetails.setDestinationTabe(rs.getString("DESTINATION_TABLE"));
					feedDetails.setTruncateFlag(rs.getString("TRUNCATE_FLAG"));
					result.add(feedDetails);
		    }
		} catch	(SQLException e) {
				logWriter.println("Error fetching data form vision_sqlserver_details table");
				closeOpenResources();
				System.exit(ERRONEOUS_EXIT);
		}finally{
			if(rs != null){
				try {
					rs.close();
					rs = null;
				} catch	(SQLException e) {}
			}
			if(statement !=	null){
				try {
					statement.close();
					statement = null;
				} catch	(SQLException e) {}
			}
			query =	null;
		}
		return result;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd	HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd"); 
		int errorCount = 0;
		logWriter = new	PrintWriter(System.getenv("VISION_ETL_LOG_FILE_PATH")+"Sql_Server_Data_Extractor_"+sdf1.format(new Date())+".log");
		try
		{
			
				Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch	(Exception e){
					logWriter.println("Oracle Driver not found in classpath");
				closeOpenResources();
			 	System.exit(ERRONEOUS_EXIT);
			
		}
		if(getUserIdAndPassword() != SUCCESSFUL_EXIT){
			closeOpenResources();
			System.exit(ERRONEOUS_EXIT);
		}
		try{
				logWriter.printf("Attempting connection	to the database	as user: %s, Connection: %s\n",	visionUserId, connectionString);
				visionConnection = DriverManager.getConnection(connectionString, visionUserId, visionPassword);
				logWriter.printf("Connected to database	as user: %s, Connection: %s\n",	visionUserId,connectionString);
			}catch(SQLException sqlEx){
				logWriter.printf("Error	connecting to the database! Code [%d], Msg [%s]\n", sqlEx.getErrorCode(), sqlEx.getMessage());
				closeOpenResources();
				System.exit(ERRONEOUS_EXIT);
			}
			List<FeedDetails> fetchDetailsResult = getProcess();
			if(fetchDetailsResult == null || fetchDetailsResult.isEmpty()){
				logWriter.println("No feeds found in vision_sqlserver_details table to Process");
				closeOpenResources();
				System.exit(SUCCESSFUL_EXIT);
			}
		try
		{
			List<DataExtractor> threads = new ArrayList<DataExtractor>();
			for(FeedDetails	fetchDetails: fetchDetailsResult){
				DataExtractor extractor = new DataExtractor(fetchDetails, visionUserId,	visionPassword,	connectionString, logWriter);
				if(extractor.getStatus() == ERRONEOUS_EXIT){
					logWriter.printf("Error	starting feed [%s]\n", fetchDetails.getFeedName());
					updateCompletionFlag(fetchDetails);
					errorCount++;
				}else{
					String date = sdf.format(new Date());
					logWriter.printf("Starting feed [%s] at [%s]\n", fetchDetails.getFeedName(),date);
					threads.add(extractor);
					extractor.start();
					while(extractor.getStatus() == -1){
						Thread.sleep(1000);
					}
					if(extractor.getStatus() == ERRONEOUS_EXIT){
						errorCount++;
					}
				}
			}
			for(DataExtractor extractor:threads){
				while(extractor.getState() != Thread.State.TERMINATED){Thread.sleep(1000);}
			}
			logWriter.printf("Feeds [%s] out of [%s] completed successfully.",(fetchDetailsResult.size()-errorCount),fetchDetailsResult.size());
		} catch(Exception e){
				logWriter.printf("Error processing to the database! Msg [%s]\n",  e.getMessage());
				closeOpenResources();
				System.exit(ERRONEOUS_EXIT);
		}finally{
				closeOpenResources();
		}
		if(errorCount>0){
				System.exit(ERRONEOUS_EXIT);
		}	
		System.exit(SUCCESSFUL_EXIT);
	}
	private	static int updateCompletionFlag(FeedDetails fetchDetails){
		String query = "UPDATE VISION_SQLSERVER_DETAILS	SET COMPLETED_FLAG='F' WHERE FEED_NAME=?";
		PreparedStatement st = null;
		try{
			st = visionConnection.prepareStatement(query);
			st.setString(1,	fetchDetails.getFeedName());
			st.executeUpdate();
			visionConnection.commit();
		}catch(SQLException sqlEx){
			logWriter.printf("Error	updating status	in table vision_sqlserver_details for Feed[%s]!	Code [%d], Msg [%s]\n",
					fetchDetails.getFeedName(), sqlEx.getErrorCode(), sqlEx.getMessage());
		}finally{
			if(st != null){
				try {
					st.close();
				} catch	(SQLException e) {}
				st =null;
			}
		}
		return SUCCESSFUL_EXIT;
	}
	private	static int getUserIdAndPassword(){
		String tempStr;
		tempStr	=  System.getenv("VISION_USER_NAME");
		if (tempStr == null){
			logWriter.println("Error reading values	for parameter VISION_USER_NAME !!\n");
			return ERRONEOUS_EXIT;
		}
		visionUserId = tempStr;
		tempStr	= System.getenv("VISION_PASSWORD");
		if (tempStr == null)
		{
			logWriter.println("Error reading values	for parameter VISION_PASSWORD !!\n");
			return ERRONEOUS_EXIT;
		}
		visionPassword = tempStr;
		tempStr	= System.getenv("ORACLE_PORT");
		if (tempStr != null){
			port = tempStr;
		}
		connectionString = connectionString + port;
		tempStr	= System.getenv("ORACLE_SID");
		if (tempStr == null)
		{
			logWriter.println("Error reading values	for parameter ORACLE_SID !!\n");
			return ERRONEOUS_EXIT;
		}
		connectionString = connectionString +":"+ tempStr;
		return SUCCESSFUL_EXIT;
	}
}
class FeedDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	private	String userName;
	private	String password;
	private	String port;
	private	String ipAddress;
	private	String dataBaseType;
	private	String dataBaseName;
	private	String query;
	private	String destinationTabe;
	private	String dateLastRun;
	private	String completedFlag="N";
	private	String feedName;
	private	String country;
	private	String leBook;
	private	String dateLastRunDay;
	private	String dateLastRunMonth;
	private	String dateLastRunYear;
	private String truncateFlag;

	public void setTruncateFlag(String truncateFlag){
			this.truncateFlag = truncateFlag;
	}
	public String getTruncateFlag(){
		return truncateFlag;
	}
	public String getDateLastRunDay() {
		return dateLastRunDay;
	}
	public void setDateLastRunDay(String dateLastRunDay) {
		this.dateLastRunDay = dateLastRunDay;
	}
	public String getDateLastRunMonth() {
		return dateLastRunMonth;
	}
	public void setDateLastRunMonth(String dateLastRunMonth) {
		this.dateLastRunMonth =	dateLastRunMonth;
	}
	public String getDateLastRunYear() {
		return dateLastRunYear;
	}
	public void setDateLastRunYear(String dateLastRunYear) {
		this.dateLastRunYear = dateLastRunYear;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName =	userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password =	password;
	}
	public String getPort()	{
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getDataBaseName()	{
		return dataBaseName;
	}
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}
	public String getDataBaseType()	{
		return dataBaseType;
	}
	public void setDataBaseType(String dataBaseType) {
		this.dataBaseType = dataBaseType;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getDestinationTabe() {
		return destinationTabe;
	}
	public void setDestinationTabe(String destinationTabe) {
		this.destinationTabe = destinationTabe;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String	ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDateLastRun() {
		return dateLastRun;
	}
	public void setDateLastRun(String dateLastRun) {
		this.dateLastRun = dateLastRun;
	}
	public String getCompletedFlag() {
		return completedFlag;
	}
	public void setCompletedFlag(String completedFlag) {
		this.completedFlag = completedFlag;
	}
	public String getFeedName() {
		return feedName;
	}
	public void setFeedName(String feedName) {
		this.feedName =	feedName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLeBook() {
		return leBook;
	}
	public void setLeBook(String leBook) {
		this.leBook = leBook;
	}
}
class DataExtractor extends Thread{
	private	static final int SUCCESSFUL_EXIT = 0;
	private	static final int ERRONEOUS_EXIT	= 1;
	private	static PrintWriter logWriter;
	private	static Connection visionConnection;
	private	static Connection extDBConnection;
	private	FeedDetails extDatabaseDetails;
	private	String visionBusinessDay;
	private	String visionBusinessDayRegular;
	private	int status = -1;
	private	SimpleDateFormat sdf1 =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
	List<Integer> columnTypes= new ArrayList<Integer>();
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public DataExtractor(FeedDetails extDatabaseDetails, String visionUserId, String visionPassword, String	connectionString,PrintWriter pLogWriter){
		super();
		try{
			this.extDatabaseDetails	= extDatabaseDetails;
			init(visionUserId, visionPassword, connectionString, pLogWriter);
		/*	if("MSSQL".equalsIgnoreCase(extDatabaseDetails.getDataBaseType())){
				System.out.println("MSQSQL");
				pLogWriter.println("MSQSQL");
				init(visionUserId, visionPassword, connectionString, pLogWriter);
				pLogWriter.println("MSQSQL Init Completed");
			}else if("MYSQL".equalsIgnoreCase(extDatabaseDetails.getDataBaseType())){
				pLogWriter.println("MYSQL");
				initMYSQL(visionUserId, visionPassword, connectionString, pLogWriter);
				pLogWriter.println("MYSQL Init Completed");
			}*/
		}catch(Exception e){
			System.out.println(e);
			pLogWriter.println(e);
			status = ERRONEOUS_EXIT;
		}
	}
	private	void initMYSQL(String visionUserId, String visionPassword, String connectionString,PrintWriter pLogWriter){
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch	(Exception e){
			pLogWriter.println("SQL	Server Driver not found	in classpath");
			closeOpenResources();
			status = ERRONEOUS_EXIT;
			return;
		}
		try{

			logWriter = new	PrintWriter(System.getenv("VISION_ETL_LOG_FILE_PATH")+"Sql_Server_Data_Extractor_"+extDatabaseDetails.getFeedName()+sdf.format(new Date())+".log");
			logWriter.printf("Attempting connection	to the database["+extDatabaseDetails.getDataBaseType()+"] as user: %s, Connection: %s\n",	visionUserId, connectionString);
			visionConnection = DriverManager.getConnection(connectionString, visionUserId, visionPassword);
			logWriter.printf("Connected to database	as user: %s, Connection: %s\n",	visionUserId,connectionString);

			logWriter.printf("Attempting connection	to the database	as user: %s, Connection: %s\n",	extDatabaseDetails.getUserName(), extDatabaseDetails.getDataBaseName());
			String	url = "jdbc:mysql://"+extDatabaseDetails.getIpAddress()+":"+extDatabaseDetails.getPort()+"/"+extDatabaseDetails.getDataBaseName()+"?profileSQL=true";
			extDBConnection = DriverManager.getConnection(url, extDatabaseDetails.getUserName(), extDatabaseDetails.getPassword());
			logWriter.printf("Connected to database	as user: %s, Connection: %s\n",	extDatabaseDetails.getUserName(), extDatabaseDetails.getDataBaseName());

			setVisionBusinessDay();
			if(visionBusinessDay ==	null){
				status = ERRONEOUS_EXIT;
			}
			if("Y".equalsIgnoreCase(extDatabaseDetails.getTruncateFlag())){
				logWriter.printf("Truncate command issued for the table[%s]", extDatabaseDetails.getDestinationTabe());
				Statement st= visionConnection.createStatement();
				st.execute("truncate table "+extDatabaseDetails.getDestinationTabe());
			}	
		}catch(SQLException sqlEx){
			logWriter.printf("Error	connecting to the database! Code [%d], Msg [%s]\n", sqlEx.getErrorCode(), sqlEx.getMessage());
			status = ERRONEOUS_EXIT;
			closeOpenResources();
		} catch	(FileNotFoundException e) {
			status = ERRONEOUS_EXIT;
			e.printStackTrace();
			logWriter.println(e.getMessage());
			System.out.print("Unable to create log file for	the child process");
			closeOpenResources();
		}catch(Exception e){
			logWriter.printf("Error! Msg [%s]\n",  e.getMessage());
			e.printStackTrace(logWriter);
			status = ERRONEOUS_EXIT;
			closeOpenResources();
		}
	}
	private	void init(String visionUserId, String visionPassword, String connectionString,PrintWriter pLogWriter){
		
		if("MYSQL".equalsIgnoreCase(extDatabaseDetails.getDataBaseType())){
			System.out.println("MYSQL");
			pLogWriter.println("MYSQL");
			initMYSQL(visionUserId, visionPassword, connectionString, pLogWriter);
			pLogWriter.println("MYSQL Init Completed");
			return;
		}
		try{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch	(Exception e){
			pLogWriter.println("SQL	Server Driver not found	in classpath");
			closeOpenResources();
			status = ERRONEOUS_EXIT;
			return;
		}
		try{

			logWriter = new	PrintWriter(System.getenv("VISION_ETL_LOG_FILE_PATH")+"Sql_Server_Data_Extractor_"+extDatabaseDetails.getFeedName()+sdf.format(new Date())+".log");	
			logWriter.printf("Attempting connection	to the database["+extDatabaseDetails.getDataBaseType()+"] as user: %s, Connection: %s\n",	visionUserId, connectionString);
			visionConnection = DriverManager.getConnection(connectionString, visionUserId, visionPassword);
			logWriter.printf("Connected to database	as user: %s, Connection: %s\n",	visionUserId,connectionString);

			logWriter.printf("Attempting connection	to the database	as user: %s, Connection: %s\n",	extDatabaseDetails.getUserName(), extDatabaseDetails.getDataBaseName());
			String	url = "jdbc:sqlserver://"+extDatabaseDetails.getIpAddress()+":"+extDatabaseDetails.getPort()+";DatabaseName="+extDatabaseDetails.getDataBaseName();
			if(extDatabaseDetails.getUserName() == null || extDatabaseDetails.getUserName().equalsIgnoreCase("")|| extDatabaseDetails.getUserName().isEmpty()){
				url = url+";integratedSecurity=true;";
				extDBConnection = DriverManager.getConnection(url);
			}else{
				extDBConnection = DriverManager.getConnection(url, extDatabaseDetails.getUserName(), extDatabaseDetails.getPassword());
			}
			logWriter.printf("Connected to database	as user: %s, Connection: %s\n",	extDatabaseDetails.getUserName(), extDatabaseDetails.getDataBaseName());

			setVisionBusinessDay();
			if(visionBusinessDay ==	null){
				status = ERRONEOUS_EXIT;
			}
			if("Y".equalsIgnoreCase(extDatabaseDetails.getTruncateFlag())){
				logWriter.printf("Truncate command issued for the table[%s]", extDatabaseDetails.getDestinationTabe());
				Statement st= visionConnection.createStatement();
				st.execute("truncate table "+extDatabaseDetails.getDestinationTabe());
			}
		}catch(SQLException sqlEx){
			logWriter.printf("Error	connecting to the database! Code [%d], Msg [%s]\n", sqlEx.getErrorCode(), sqlEx.getMessage());
			status = ERRONEOUS_EXIT;
			closeOpenResources();
		} catch	(FileNotFoundException e) {
			status = ERRONEOUS_EXIT;
			e.printStackTrace();
			logWriter.println(e.getMessage());
			System.out.print("Unable to create log file for	the child process");
			closeOpenResources();
		}catch(Exception e){
			logWriter.printf("Error! Msg [%s]\n",  e.getMessage());
			e.printStackTrace(logWriter);
			status = ERRONEOUS_EXIT;
			closeOpenResources();
		}
	}
	private	void setVisionBusinessDay(){
		String query = "SELECT Format(BUSINESS_DATE,'RRRR-MM-DD'),Format(BUSINESS_DATE,'RRRR-MM-DD') FROM VISION_BUSINESS_DAY	WHERE COUNTRY=?	AND LE_BOOK = ?";
		PreparedStatement st = null;
		ResultSet rs = null;
		try{
			st = visionConnection.prepareStatement(query);
			st.setString(1,	extDatabaseDetails.getCountry());
			st.setString(2,	extDatabaseDetails.getLeBook());
			rs = st.executeQuery();
			if(rs.next()){
				visionBusinessDay= rs.getString(1);
				visionBusinessDayRegular= rs.getString(2);
				return;
			}
		}catch(SQLException sqlEx){
			logWriter.printf("Error	reading	vision_business_day! Code [%d],	Msg [%s]\n",
					sqlEx.getErrorCode(), sqlEx.getMessage());
		}finally{
			if(rs != null){
				try {
					rs.close();
				} catch	(SQLException e) {}
				rs =null;
			}
			if(st != null){
				try {
					st.close();
				} catch	(SQLException e) {}
				st =null;
			}
		}
		logWriter.printf("No data found	in table vision_business_day for Country [%s] ,	LE Book	[%s]\n",
				extDatabaseDetails.getCountry(), extDatabaseDetails.getLeBook());
	}
	private	static void closeOpenResources(){
		try {
			if(visionConnection != null){
				visionConnection.close();
				visionConnection = null;
			}
			if(extDBConnection != null){
				extDBConnection.close();
				extDBConnection	= null;
			}
		}catch (SQLException e)	{
			logWriter.printf("Error	closing	the vision database connection.	Msg[%s]",e.getMessage());
		}finally{
			if(logWriter !=	null){
				logWriter.flush();
				logWriter.close();
				logWriter = null;
			}
		}
	}

	public void run() {
		
		PreparedStatement statement = null;
		
		int columnCount	= 0;
		ResultSet rs=null;
		StringBuilder insertQuery = new	StringBuilder();
		try{
			long startTime = System.currentTimeMillis();
			statement = extDBConnection.prepareStatement(extDatabaseDetails.getQuery(),
				    ResultSet.TYPE_SCROLL_INSENSITIVE,
				    ResultSet.CONCUR_READ_ONLY);
			
			statement.setString(1, extDatabaseDetails.getCountry());
			statement.setString(2, extDatabaseDetails.getLeBook());
			int paramIndex = extDatabaseDetails.getQuery().split("\\?").length -1;
			if(paramIndex > 2){
				statement.setDate(3,  java.sql.Date.valueOf(visionBusinessDayRegular));
			}
			if(paramIndex > 3){
				statement.setDate(4, java.sql.Date.valueOf(visionBusinessDay));
				statement.setDate(5, java.sql.Date.valueOf(extDatabaseDetails.getDateLastRun()));
			}
			logWriter.printf("Executing Query with parameters:[%s]\n",paramIndex);
			logWriter.println(extDatabaseDetails.getQuery());
			rs= statement.executeQuery();
			logWriter.printf("Fetching records from	[%s] at	[%s]\n", extDatabaseDetails.getFeedName(),sdf1.format(new Date()));
			while(rs.next()){
					columnCount = prepareInsertQuery(rs, insertQuery);
					if(insertQuery == null || columnCount <= 1){
						updateCompletionFlag();
						return;
					}
					status = insertDataToDestinationTable(rs, insertQuery.toString(), columnCount);
					if(status != SUCCESSFUL_EXIT){
						//logWriter.printf("Error processing records from [%s] to [%s]\n", startIndex, lastIndex);
						return;
					}
			}
			if(columnCount ==0){
				logWriter.println("No records found to process.");
			}	
			updateCompletionFlagAndLastRundate();
			long timeTaken = (System.currentTimeMillis() - startTime)/1000;
			logWriter.printf("Completed feed [%s] at [%s]. Total time taken	[%s]s\n", extDatabaseDetails.getFeedName(),sdf1.format(new Date()),timeTaken);
			status=SUCCESSFUL_EXIT;
		}catch (SQLException e)	{
			status=ERRONEOUS_EXIT;
			//logWriter.printf("Error processing records from [%s] to [%s],	Code [%d], Msg [%s]\n",	startIndex, lastIndex, e.getErrorCode(), e.getMessage());
			logWriter.printf("Error processing records Code [%s], Msg[%s] ", e.getErrorCode(),e.getMessage());
			e.printStackTrace(logWriter);
		}catch (Exception e) {
			status=ERRONEOUS_EXIT;
			e.printStackTrace(logWriter);
			//logWriter.printf("Error processing records from [%s] to [%s],	Code [%d], Msg [%s]\n",	startIndex, lastIndex, e.getErrorCode(), e.getMessage());
			logWriter.printf("Error processing records Code  Msg[%s] ", e);
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch (SQLException e)	{}
				rs = null;
			}
			if(statement !=	null){
				try{
					statement.close();
				}catch (SQLException e)	{}
				statement = null;
			}
			closeOpenResources();
		}
	}
	private	int insertDataToDestinationTable(ResultSet rs, String query,int	columnCount){
		PreparedStatement statement = null;
		final int batchSize = 1000;
		int loopCount =	0;
		long count = 0;
		try{
			statement = visionConnection.prepareStatement(query);
			while(rs.next()){
				for (loopCount = 1; loopCount <	columnCount; loopCount++){
					if(columnTypes.get(loopCount-1)	== 91){
						if(rs.getString(loopCount) == null){
							statement.setNull(loopCount, 91);
						}else{
							statement.setDate(loopCount, java.sql.Date.valueOf(rs.getString(loopCount)));
					  }		
					}else if(columnTypes.get(loopCount-1) == 92){
						if(rs.getString(loopCount) == null){
							statement.setNull(loopCount, 92);
						}else{
							statement.setTime(loopCount, Time.valueOf(rs.getString(loopCount)));
						}	
					}else if(columnTypes.get(loopCount-1) == 93){
						if(rs.getString(loopCount) == null){
								statement.setNull(loopCount, 93);
						}else{
								statement.setTimestamp(loopCount, Timestamp.valueOf(rs.getString(loopCount)));
						}	
					}else{
						statement.setString(loopCount, rs.getString(loopCount));
					}
				}
				statement.addBatch();
				if(++count % batchSize == 0) {
					statement.executeBatch();
			    }
			}
			statement.executeBatch();
			visionConnection.commit();
			logWriter.printf("No more records to process. Total records processed [%s]\n",(count));
		}catch(SQLException sqlEx){
			sqlEx.printStackTrace(logWriter);
			logWriter.printf("Error inserting data to table[%s] for Feed[%s]! Code [%d], Msg [%s]! Row[%s] Coc[%s]\n",
					extDatabaseDetails.getDestinationTabe(), extDatabaseDetails.getFeedName(), sqlEx.getErrorCode(), sqlEx.getMessage(),
					count, loopCount);
			return ERRONEOUS_EXIT;
		}catch(Exception sqlEx){
			sqlEx.printStackTrace(logWriter);
			logWriter.printf("Error inserting data to table[%s] for Feed[%s]!  Msg [%s]!  Row[%s] Coc[%s]\n",
					extDatabaseDetails.getDestinationTabe(), extDatabaseDetails.getFeedName(), sqlEx.getMessage(),
					count, loopCount);
			return ERRONEOUS_EXIT;
		}finally{
			if(statement !=	null){
				try{
					statement.close();
				}catch (SQLException e)	{}
				statement = null;
			}
		}
		return SUCCESSFUL_EXIT;
	}
	private	int prepareInsertQuery(ResultSet rs, StringBuilder query){
		query.append("INSERT INTO ").append(extDatabaseDetails.getDestinationTabe())
		.append('(');
		int i =	0;
		try {
			ResultSetMetaData md = rs.getMetaData();
			StringBuilder paramString = new	StringBuilder();
			for (i = 1; i <= md.getColumnCount(); i++){
				if (i != 1){
					query.append(',');
					paramString.append(',');
				}
				columnTypes.add( md.getColumnType(i));
				query.append(md.getColumnName(i));
				paramString.append("? ");
				query.append(' ');
			}
			query.append(")	VALUES (");
			query.append(paramString).append(')');
		} catch	(SQLException sqlEx) {
			logWriter.printf("Error	getting	metadata for table for Feed[%s]! Code [%d], Msg	[%s]\n",
					extDatabaseDetails.getFeedName(), sqlEx.getErrorCode(),	sqlEx.getMessage());
			return 0;
		}
		return i;
	}
	private	int updateCompletionFlagAndLastRundate(){
		String query = "UPDATE vision_sqlserver_details	SET COMPLETED_FLAG='C',	DATE_LAST_RUN=?	WHERE FEED_NAME=?";
		PreparedStatement st = null;
		try{
			st = visionConnection.prepareStatement(query);
			st.setDate(1, java.sql.Date.valueOf(visionBusinessDayRegular));
			st.setString(2,	extDatabaseDetails.getFeedName());
			st.executeUpdate();
			visionConnection.commit();
		}catch(SQLException sqlEx){
			logWriter.printf("Error	updating status	in table vision_sqlserver_details for Feed[%s]!	Code [%d], Msg [%s]\n",
					extDatabaseDetails.getFeedName(), sqlEx.getErrorCode(),	sqlEx.getMessage());
		}finally{
			if(st != null){
				try {
					st.close();
				} catch	(SQLException e) {}
				st =null;
			}
		}
		return SUCCESSFUL_EXIT;
	}
	private	int updateCompletionFlag(){
		String query = "UPDATE VISION_SQLSERVER_DETAILS	SET COMPLETED_FLAG='F' WHERE FEED_NAME=?";
		PreparedStatement st = null;
		try{
			st = visionConnection.prepareStatement(query);
			st.setString(1,	extDatabaseDetails.getFeedName());
			st.executeUpdate();
			visionConnection.commit();
		}catch(SQLException sqlEx){
			logWriter.printf("Error	updating status	in table vision_sqlserver_details for Feed[%s]!	Code [%d], Msg [%s]\n",
					extDatabaseDetails.getFeedName(), sqlEx.getErrorCode(),	sqlEx.getMessage());
		}finally{
			if(st != null){
				try {
					st.close();
				} catch	(SQLException e) {}
				st =null;
			}
		}
		return SUCCESSFUL_EXIT;
	}
}
