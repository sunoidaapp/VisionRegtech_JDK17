package com.vision.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.CommonVb;
@Component
public class EtlServiceDao extends AbstractDao<CommonVb>{
	
	@Value("${app.databaseType}")
	private String databaseType;
	@Autowired
	CommonDao commonDao;
	
	public ExceptionCode getEtlPostngDetailGenBuild(){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> postingData = new HashMap<String,String>();
		try
		{	
			String orginalQuery= "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE FROM( "+
						" SELECT ROWNUM RN,COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,TRUNC(BUSINESS_DATE) BUSINESS_DATE,POSTING_DATE FROM "+
						" ( "+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') <= TO_CHAR(SYSDATE,'HH24:MI:SS') "+
						" AND EXTRACTION_ENGINE = 'BLD' "+
						" AND DEPENDENT_FLAG = 'N' "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') <= TO_CHAR(SYSDATE,'HH24:MI:SS') "+
						" AND EXTRACTION_ENGINE = 'BLD' "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID NOT IN (SELECT EXTRACTION_FEED_ID FROM RA_TRN_POSTING) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE) "+
						" WHERE RN <=1";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				orginalQuery = " SELECT top 1.COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+ 
						" POSTING_SEQUENCE,CONVERT(DATE,BUSINESS_DATE) BUSINESS_DATE,POSTING_DATE FROM  "+
						" ( "+
						" SELECT *  "+
						" FROM RA_TRN_POSTING "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'BLD' "+
						" AND DEPENDENT_FLAG = 'N'  "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING S1 "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'BLD' "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID IN (SELECT S2.EXTRACTION_FEED_ID FROM RA_TRN_POSTING_HISTORY S2 WHERE "+
						"     S2.POSTING_SEQUENCE = S1.POSTING_SEQUENCE AND S2.POSTED_STATUS ='C' "+
						"	 AND S2.POSTING_DATE = S1.POSTING_DATE AND S2.BUSINESS_DATE = S1.BUSINESS_DATE "+
						"	 ) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE";
			}
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(postingData);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public ExceptionCode getEtlPostngDetailAdfBuild(){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> postingData = new HashMap<String,String>();
		try
		{	
			String orginalQuery= "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,BUSINESS_DATE,POSTING_DATE FROM( "+
						" SELECT ROWNUM RN,COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE,TRUNC(BUSINESS_DATE) BUSINESS_DATE,POSTING_DATE FROM "+
						" ("+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') <= TO_CHAR(SYSDATE,'HH24:MI:SS') "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'N' "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING "+
						" WHERE TO_CHAR(SCHEDULE_TIME,'HH24:MI:SS') <= TO_CHAR(SYSDATE,'HH24:MI:SS') "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID NOT IN (SELECT EXTRACTION_FEED_ID FROM RA_TRN_POSTING) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE)"+
						" WHERE RN <=1";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				orginalQuery = " SELECT top 1.COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+ 
						" POSTING_SEQUENCE,FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE,POSTING_DATE FROM  "+
						" ( "+
						" SELECT *  "+
						" FROM RA_TRN_POSTING "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'N'  "+
						" AND POSTED_STATUS = 'P' "+
						" UNION ALL "+
						" SELECT * "+
						" FROM RA_TRN_POSTING S1 "+
						" WHERE CONVERT(VARCHAR,SCHEDULE_TIME,108) <= CONVERT(VARCHAR,getdate(),108) "+
						" AND EXTRACTION_ENGINE = 'ETL' "+
						" AND DEPENDENT_FLAG = 'Y' "+
						" AND POSTED_STATUS = 'P' "+
						" AND DEPENDENT_FEED_ID IN (SELECT S2.EXTRACTION_FEED_ID FROM RA_TRN_POSTING_HISTORY S2 WHERE "+
						"     S2.POSTING_SEQUENCE = S1.POSTING_SEQUENCE AND S2.POSTED_STATUS ='C' "+
						"	 AND S2.POSTING_DATE = S1.POSTING_DATE AND S2.BUSINESS_DATE = S1.BUSINESS_DATE "+
						"	 ) "+
						" ) T1 ORDER BY COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID, "+
						" POSTING_SEQUENCE";
			}
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if(dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(postingData);
					}else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	public int doUpdatePostings(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){
		String dateUpdate = " ";
		if("I".equalsIgnoreCase(postingStatus)) {
			dateUpdate = " ,START_TIME= "+commonDao.getDbFunction("SYSDATE")+" ";
		}else {
			dateUpdate = " ,END_TIME = "+commonDao.getDbFunction("SYSDATE")+" ";			
		}
		String query = " Update RA_TRN_POSTING SET POSTED_STATUS = ? "+dateUpdate+" WHERE COUNTRY = ? AND LE_BOOK = ? "+
				 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
				 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		Object[] args = {postingStatus,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int doUpdatePostingsHistory(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){
		String dateUpdate = " ";
		if("I".equalsIgnoreCase(postingStatus)) {
			dateUpdate = " ,START_TIME= "+commonDao.getDbFunction("SYSDATE")+" ";
		}else {
			dateUpdate = " ,END_TIME = "+commonDao.getDbFunction("SYSDATE")+" ";			
		}
		String query = " Update RA_TRN_POSTING_HISTORY SET POSTED_STATUS = ? "+dateUpdate+" WHERE COUNTRY = ? AND LE_BOOK = ? "+
				 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
				 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		Object[] args = {postingStatus,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int doDeletePostings(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String postingStatus){

		String query = " DELETE FROM RA_TRN_POSTING WHERE COUNTRY = ? AND LE_BOOK = ? "+
				 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
				 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		Object[] args = {country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public int updateLogFileName(String country,String leBook,String extractionFrequency,String extractionSequence,String extractionFeedId,String postingSequence,
			String business_date,String postingDate,String logFileName) {
		
		String query = " Update RA_TRN_POSTING_HISTORY SET LOG_FILE_NAME =? WHERE COUNTRY = ? AND LE_BOOK = ? "+
				 " AND EXTRACTION_FREQUENCY = ? AND EXTRACTION_SEQUENCE = ? AND EXTRACTION_FEED_ID = ? "+
				 " AND POSTING_SEQUENCE = ? AND BUSINESS_DATE = ? AND POSTING_DATE = ? ";
		Object[] args = {logFileName,country,leBook,extractionFrequency,extractionSequence,extractionFeedId,postingSequence,business_date,postingDate};
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode doSelectAdfAcquisition(String country,String leBook,String  feedId,String postingSeq,String businessDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> acqData = new HashMap<String,String>();
		try
		{	
			String orginalQuery= "";
			if ("ORACLE".equalsIgnoreCase(databaseType)) {
				orginalQuery = " SELECT COUNTRY,LE_BOOK,'' BUSINESS_DATE,TEMPLATE_NAME,FILE_PATTERN, "+
						" EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,'' NEXT_PROCESS_TIME, "+
						" 'ADF' ACQUISTION_PROCESS_TYPE,CONNECTIVITY_TYPE,CONNECTIVITY_DETAILS, "+ 
						" DATABASE_TYPE,DATABASE_CONNECTIVITY_DETAILS, "+
						" SOURCE_SCRIPT_TYPE,SOURCE_SERVER_SCRIPTS,"+
						" TARGET_SCRIPT_TYPE,"+
						" TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS, "+
						" '' ACQU_PROCESSCONTROL_STATUS,'' ADF_NUMBER,SUB_ADF_NUMBER,PREACTIVITY_SCRIPTS FROM ADF_DATA_ACQUISITION "+
						" WHERE TEMPLATE_NAME = '"+feedId+"' ";
			}else if ("MSSQL".equalsIgnoreCase(databaseType)){
				orginalQuery = " SELECT COUNTRY,LE_BOOK,'' BUSINESS_DATE,TEMPLATE_NAME,FILE_PATTERN, "+
						" EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,'' NEXT_PROCESS_TIME, "+
						" 'ADF' ACQUISTION_PROCESS_TYPE,CONNECTIVITY_TYPE,CONNECTIVITY_DETAILS, "+ 
						" DATABASE_TYPE,DATABASE_CONNECTIVITY_DETAILS, "+
						" SOURCE_SCRIPT_TYPE,SOURCE_SERVER_SCRIPTS,"+
						" TARGET_SCRIPT_TYPE,"+
						" TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS, "+
						" '' ACQU_PROCESSCONTROL_STATUS,'' ADF_NUMBER,SUB_ADF_NUMBER,PREACTIVITY_SCRIPT_TYPE,PREACTIVITY_SCRIPTS FROM ADF_DATA_ACQUISITION "+
						" WHERE TEMPLATE_NAME = '"+feedId+"' AND COUNTRY ='"+country+"' AND LE_BOOK ='"+leBook+"'  ";
			}
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							acqData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					if(dataPresent) {
						int recCnt = checkProcessControlDataExists(country,leBook,feedId,businessDate);
						if(recCnt > 0) {
							deleteProcessControlDataExists(country, leBook, feedId, businessDate);
						}
						ExceptionCode exceptionCodePr = doInsertAdfProcessControl(acqData,postingSeq,businessDate);
						if(exceptionCodePr.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							exceptionCode.setErrorMsg(exceptionCodePr.getErrorMsg());
							exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						}else {
							exceptionCode.setResponse(getAdfProcessControlInfo(country, leBook, feedId, businessDate));
							exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);	
						}
						//exceptionCode.setResponse(acqData);
					}else {
						exceptionCode.setErrorMsg("ADF Data Acquisition not maintained for country["+country+"]LEBook["+leBook+"]FeedId["+feedId+"]");
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}
					return exceptionCode;
				}
			};
			return (ExceptionCode)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}
	protected ExceptionCode doInsertAdfProcessControl(HashMap<String,String> insertMap,String postingSeq,String businessDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String query =  " Insert Into ADF_PROCESS_CONTROL(COUNTRY,LE_BOOK,BUSINESS_DATE,TEMPLATE_NAME,FILE_PATTERN, "+
					" EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,NEXT_PROCESS_TIME, "+
					" acquisition_process_type,CONNECTIVITY_TYPE,CONNECTIVITY_DETAILS,"+ 
					" DATABASE_TYPE,DATABASE_CONNECTIVITY_DETAILS,"+
					" SOURCE_SCRIPT_TYPE,SOURCE_SERVER_SCRIPTS,"+
					" TARGET_SCRIPT_TYPE,"+
					" TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS,"+
					" ACQU_PROCESSCONTROL_STATUS,ADF_NUMBER,SUB_ADF_NUMBER,"+
					" START_TIME,END_TIME,ALERT1_TIMESLOT,RECORD_INDICATOR,"+
					" MAKER,VERIFIER,DATE_CREATION,DATE_LAST_MODIFIED,PREACTIVITY_SCRIPTS) "+
			" Values ("
				+ "'"+insertMap.get("COUNTRY")+"',"
				+ "'"+insertMap.get("LE_BOOK")+"',"
				+ "'"+businessDate+"',"
				+ "'"+insertMap.get("TEMPLATE_NAME")+"',"
				+ "'"+insertMap.get("FILE_PATTERN")+"',"
				+ "'"+insertMap.get("EXCEL_FILE_PATTERN")+"',"
				+ "'"+insertMap.get("EXCEL_TEMPLATE_ID")+"',"
				+ ""+commonDao.getDbFunction("SYSDATE")+", "
				+ "'ADF',"
				+ "'"+insertMap.get("CONNECTIVITY_TYPE")+"',"
				+ "'"+insertMap.get("CONNECTIVITY_DETAILS")+"'," 
				+ "'"+insertMap.get("DATABASE_TYPE")+"',"
				+ "'"+insertMap.get("DATABASE_CONNECTIVITY_DETAILS")+"',"
				+ "'"+insertMap.get("SOURCE_SCRIPT_TYPE")+"',"
				+ "'"+insertMap.get("SOURCE_SERVER_SCRIPTS")+"',"
				+ "'"+insertMap.get("TARGET_SCRIPT_TYPE")+"',"
				+ "'"+insertMap.get("TARGET_SERVER_SCRIPTS")+"',"
				+ "'"+insertMap.get("READINESS_SCRIPTS_TYPE")+"',"
				+ "'"+insertMap.get("ACQUISITION_READINESS_SCRIPTS")+"',"
				+ "'1',"
				+ "'0',"
				+ "'"+insertMap.get("SUB_ADF_NUMBER")+"',"
				+ ""+commonDao.getDbFunction("SYSDATE")+", "
				+ ""+commonDao.getDbFunction("SYSDATE")+", "
				+ ""+commonDao.getDbFunction("SYSDATE")+", "
				+ "'0', "
				+ "'9999', "
				+ "'9999', "
				+ ""+commonDao.getDbFunction("SYSDATE")+", "
				+ ""+commonDao.getDbFunction("SYSDATE")+", "
				+ "'"+insertMap.get("PREACTIVITY_SCRIPTS")+"'"
				+ ")";
	
			query = replaceHashPrompts(insertMap,postingSeq,businessDate,query);
			int i = getJdbcTemplate().update(query);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception e) {
			System.out.println("Error while inserting ADF Process Control Data!!!!!");
			System.out.println(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	private int checkProcessControlDataExists(String country,String leBook,String feedId,String businessDate) {
		int cnt = 0;
		try {
			String query = "SELECT COUNT(1) FROM ADF_PROCESS_CONTROL WHERE COUNTRY = ? AND LE_BOOK = ? "+
					" AND TEMPLATE_NAME = ? AND BUSINESS_DATE = ? ";
			
			Object[] args = {country,leBook,feedId,businessDate};
			cnt = getJdbcTemplate().queryForObject(query,args,Integer.class);
			return cnt; 
		}catch(Exception e) {
			return 0;
		}
	}
	public int deleteProcessControlDataExists(String country,String leBook,String feedId,String businessDate) {
		int cnt = 0;
		try {
			String query = "DELETE FROM ADF_PROCESS_CONTROL WHERE COUNTRY = ? AND LE_BOOK = ? "+
					" AND TEMPLATE_NAME = ? AND BUSINESS_DATE = ? ";
			
			Object[] args = {country,leBook,feedId,businessDate};
			return getJdbcTemplate().update(query,args);
		}catch(Exception e) {
			return 0;
		}
	}
	public String replaceHashPrompts(HashMap<String,String> insertMap,String postingSeq,String businessDate,String insertQuery) {
		try {
			if(insertMap != null) {
				insertQuery = insertQuery.replaceAll("#COUNTRY#", insertMap.get("COUNTRY"));
				insertQuery = insertQuery.replaceAll("#LE_BOOK#", insertMap.get("LE_BOOK"));
				insertQuery = insertQuery.replaceAll("#TEMPLATE_NAME#", insertMap.get("TEMPLATE_NAME"));
			}
			insertQuery = insertQuery.replaceAll("#BUSINESS_DATE#", businessDate);
			insertQuery = insertQuery.replaceAll("#POSTING_SEQ#", postingSeq);
			return insertQuery;
		}catch(Exception e) {
			e.printStackTrace();
			return insertQuery;
		}
	}
	public HashMap<String,String> getAdfProcessControlInfo(String country,String leBook,String feedId,String businessDate){
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String,String> postingData = new HashMap<String,String>();
		try
		{	
			String orginalQuery = "SELECT FILE_PATTERN,TARGET_SCRIPT_TYPE,TARGET_SERVER_SCRIPTS,READINESS_SCRIPTS_TYPE,ACQUISITION_READINESS_SCRIPTS, "+
					" PREACTIVITY_SCRIPT_TYPE,PREACTIVITY_SCRIPTS "+
					" FROM ADF_PROCESS_CONTROL WHERE COUNTRY = '"+country+"' AND LE_BOOK = '"+leBook+"' "+
					" AND TEMPLATE_NAME = '"+feedId+"' AND BUSINESS_DATE = '"+businessDate+"' ";
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while(rs.next()){
						dataPresent = true;
						for(int cn = 1;cn <= colCount;cn++) {
							String columnName = metaData.getColumnName(cn);
							postingData.put(columnName.toUpperCase(), rs.getString(columnName));
						}
					}
					return postingData;
				}
			};
			return (HashMap<String,String>)getJdbcTemplate().query(orginalQuery, mapper);
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return null;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode sqlBulkInsert(String targetScript,String adfDelimiter,String dataFile) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		try {
			dataFile = dataFile.replaceAll("\\\\", "\\\\\\\\");
			targetScript = targetScript.replaceAll("#UPLOAD_FILE_PATH#", dataFile);
			
			int retVal = getJdbcTemplate().update(targetScript);
			exceptionCode.setResponse(retVal);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode executeTargetScript(String sql) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			int i = getJdbcTemplate().update(sql);
			if(i == Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(i);	
			}else {
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}catch(Exception e) {
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	public int executePreactivityScript(String sql) {
		try {
			return getJdbcTemplate().update(sql);
		}catch(Exception e) {
			return 0;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode sqlLoader(String targetScript,String adfDelimiter,String adfDataFilePath,String filePattern) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		try {
			String logPath =  commonDao.findVisionVariableValue("RA_SERV_LOGPATH");
			String userName =  System.getenv("VISION_USER_NAME");
			String password =  System.getenv("VISION_PASSWORD");
			String dbStr = "sqlldr vision/vision123@10.16.1.222:1521/vsnnxt control="+adfDataFilePath+filePattern+".ctl log="+logPath+filePattern+"_Loader.log";

	        Process proc;
			proc = Runtime.getRuntime().exec(dbStr);
			proc.waitFor();
	        InputStream in = proc.getInputStream();
	        InputStreamReader isr = new InputStreamReader(in);
	        BufferedReader br = new BufferedReader(isr);
	        String line = null;
	        StringBuffer sb = new StringBuffer();
			while( (line = br.readLine()) != null ) {
			    sb.append(line+"\n");
			}
	        InputStream inE = proc.getErrorStream();
	        InputStreamReader iser = new InputStreamReader(inE);
	        BufferedReader erbr = new BufferedReader(iser);
			while( (line = erbr.readLine()) != null ){
			    sb.append( "\nError stream:" );
			    sb.append(line);
			}
			int eValue = proc.waitFor();
	        //System.out.println(eValue);
			if(eValue != 0) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
}
