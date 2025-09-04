package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.vb.EtlOperationDetailVb;
import com.vision.vb.EtlOperationManagerHeaderVb;
@Component
public class EtlOperationManagerDao extends AbstractDao<EtlOperationManagerHeaderVb>{
	
	@Autowired
	CommonDao commonDao;
	@Value("${app.databaseType}")
	private String databaseType;
	
	protected RowMapper getHeaderMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlOperationManagerHeaderVb vObject = new EtlOperationManagerHeaderVb();
				vObject.setPosBusDate(rs.getString("POSBUS_DATE"));
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionProcess(rs.getString("EXTRACTION_PROCESS"));
				vObject.setEtlInitiated(rs.getString("ETL_INITIATED"));
				vObject.setEtlReinitiated(rs.getString("ETL_REINITIATED"));
				vObject.setYetToStart(rs.getString("YET_TO_START"));
				vObject.setInProgress(rs.getString("IN_PROGRESS"));
				vObject.setCompleted(rs.getString("COMPLETED"));
				vObject.setErrored(rs.getString("ERRORED"));
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlOperationDetailVb vObject = new EtlOperationDetailVb();
				vObject.setPosBusDate(rs.getString("POSBUS_DATE"));
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setFrequency(rs.getString("FREQUENCY"));
				vObject.setExtractionName(rs.getString("EXTRACTION_NAME"));
				vObject.setEtlCategory(rs.getString("ETL_CATEGORY"));
				vObject.setFeedDescription(rs.getString("FEED_DESCRIPTION"));
				vObject.setStartTime(rs.getString("START_TIME"));
				vObject.setEndTime(rs.getString("END_TIME"));
				vObject.setDuration(rs.getString("DURATION"));
				vObject.setEtlStatus(rs.getString("ETL_STATUS"));
				vObject.setExecutionType(rs.getString("EXECUTION_TYPE"));
				vObject.setLogFile(rs.getString("LOG_FILE"));
				return vObject;
			}
		};
		return mapper;
	}
	
	
	public List<EtlOperationManagerHeaderVb> getQueryHeaderResults(EtlOperationManagerHeaderVb dObj){
		List<EtlOperationManagerHeaderVb> collTemp = null;
		setServiceDefaults();
		String sqlQuery = null;
		String selectedDateField = ""+commonDao.getDbFunction("CONVERT")+"(DATE,POSTING_DATE)";
		String resultDateField = ""+commonDao.getDbFunction("CONVERT")+"(DATE,BUSINESS_DATE)";
		if("P".equalsIgnoreCase(dObj.getDateType())) {
			selectedDateField = ""+commonDao.getDbFunction("CONVERT")+"(DATE,POSTING_DATE)";
			resultDateField = ""+commonDao.getDbFunction("CONVERT")+"(DATE,BUSINESS_DATE)";
		}else {
			selectedDateField = ""+commonDao.getDbFunction("CONVERT")+"(DATE,BUSINESS_DATE)";
			resultDateField = ""+commonDao.getDbFunction("CONVERT")+"(DATE,POSTING_DATE)";
		}
		sqlQuery = new String("WITH 	"+
				" T1 AS (SELECT COUNT(DISTINCT "+commonDao.getDbFunction("DATEFUNC")+"("+resultDateField+", '"+commonDao.getDbFunction("DATEFORMAT")+"')) POSBUS_DATE"+
				" FROM RA_TRN_POSTING_HISTORY WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
				" T2 AS (SELECT COUNT(DISTINCT COUNTRY) COUNTRY FROM RA_TRN_POSTING_HISTORY "+
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
				" T3 AS (SELECT COUNT(DISTINCT LE_BOOK) LE_BOOK FROM RA_TRN_POSTING_HISTORY"+ 
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
				" T4 AS (SELECT COUNT(DISTINCT EXTRACTION_FREQUENCY) EXTRACTION_FREQUENCY FROM RA_TRN_POSTING_HISTORY "+
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
				" T5 AS (SELECT COUNT(DISTINCT EXTRACTION_SEQUENCE) EXTRACTION_PROCESS FROM RA_TRN_POSTING_HISTORY"+
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
				" T6 AS (SELECT COUNT(*) ETL_INITIATED FROM RA_TRN_POSTING_HISTORY"+
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")),"+
				" T7 AS (SELECT COUNT(*) ETL_REINITIATED FROM RA_TRN_POSTING_HISTORY"+
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+") AND POSTING_SEQUENCE >1),"+
				" T8 AS (SELECT "+
				" SUM(CASE WHEN POSTED_STATUS = 'P' THEN 1 ELSE 0 END) YET_TO_START,"+
				" SUM(CASE WHEN POSTED_STATUS = 'I' THEN 1 ELSE 0 END) IN_PROGRESS,"+
				" SUM(CASE WHEN POSTED_STATUS = 'C' THEN 1 ELSE 0 END) COMPLETED,"+
				" SUM(CASE WHEN POSTED_STATUS = 'E' THEN 1 ELSE 0 END) ERRORED"+
				" FROM RA_TRN_POSTING_HISTORY"+
				" WHERE COUNTRY IN ("+dObj.getCountry()+") AND LE_BOOK IN ("+dObj.getLeBook()+") AND EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+"))"+
				" SELECT * FROM T1, T2, T3, T4, T5, T6, T7, T8	");
		try
		{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(sqlQuery.toString(),getHeaderMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
				logger.error(((sqlQuery == null) ? "sqlQuery is Null" : sqlQuery.toString()));
			/*if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());*/
			return null;
		}
	}

	public List<EtlOperationDetailVb> getQueryDetailResults(EtlOperationManagerHeaderVb dObj){
		List<EtlOperationDetailVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		setServiceDefaults();
		String sqlQuery = null;
		String orderBy = "";
		String selectedDateField = "CONVERT(DATE,T1.POSTING_DATE)";
		String resultDateField = "CONVERT(DATE,T1.BUSINESS_DATE)";
		if("P".equalsIgnoreCase(dObj.getDateType())) {
			selectedDateField = "CONVERT(DATE,T1.POSTING_DATE)";
			resultDateField = "CONVERT(DATE,T1.BUSINESS_DATE)";
		}else {
			selectedDateField = "CONVERT(DATE,T1.BUSINESS_DATE)";
			resultDateField = "CONVERT(DATE,T1.POSTING_DATE)";
		}
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sqlQuery = new String("SELECT  "+	
					" TO_CHAR("+resultDateField+",'DD-Mon-RRRR') POSBUS_DATE,  "+
					" T1.COUNTRY,  "+ 
					" T1.LE_BOOK,  "+
					" T1.EXTRACTION_FREQUENCY FREQUENCY,  "+
					" EXTRACTION_DESCRIPTION EXTRACTION_NAME, "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) ETL_CATEGORY, "+
					" CASE WHEN T2.EXTRACTION_ENGINE ='BLD' THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = EXTRACTION_FEED_ID) " +
					" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END FEED_DESCRIPTION," +
					" TO_CHAR(T1.START_TIME,'HH:MM:SS') START_TIME,TO_CHAR(T1.END_TIME,'HH:MM:SS') END_TIME,,"+
					" ROUND((END_TIME-START_TIME) * 1440)||' Mins' DURATION, "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB = T1.POSTED_STATUS)  ETL_STATUS, "+
					" CASE WHEN T1.POSTING_SEQUENCE =1 THEN 'New Initiate' ELSE 'Re-Initiate' END EXECUTION_TYPE, "+
					" T1.LOG_FILE_NAME LOG_FILE "+
					" FROM 	 "+
					" RA_TRN_POSTING_HISTORY T1, "+
					" RA_MST_EXTRACTION_HEADER T2, "+
					" RA_MST_EXTRACTION_DETAIL T3 "+
					" WHERE 	 "+
					" T1.COUNTRY = T2.COUNTRY "+
					" AND T1.LE_BOOK = T2.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
					" AND T1.COUNTRY = T3.COUNTRY "+
					" AND T1.LE_BOOK = T3.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
					" AND T3.FEED_ID = T1.EXTRACTION_FEED_ID "+
					" AND T1.COUNTRY IN ("+dObj.getCountry()+")  "+
					" AND T1.LE_BOOK IN ("+dObj.getLeBook()+")  "+
					" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")");
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		sqlQuery = new String("SELECT  "+	
				" Format("+resultDateField+",'dd-MMM-yyyy') POSBUS_DATE,  "+
				" T1.COUNTRY,  "+ 
				" T1.LE_BOOK,  "+
				" T1.EXTRACTION_FREQUENCY FREQUENCY,  "+
				" EXTRACTION_DESCRIPTION EXTRACTION_NAME, "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) ETL_CATEGORY, "+
				" CASE WHEN T2.EXTRACTION_ENGINE ='BLD' THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = EXTRACTION_FEED_ID) " +
				" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END FEED_DESCRIPTION," +
				" CONVERT(varchar,T1.START_TIME, 108) START_TIME,CONVERT(varchar,T1.END_TIME, 108) END_TIME,"+
				" CAST(DATEDIFF(MINUTE, T1.START_TIME, T1.END_TIME) AS VARCHAR)+' Mins' DURATION, "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 7039 AND ALPHA_SUB_TAB = T1.POSTED_STATUS)  ETL_STATUS, "+
				" CASE WHEN T1.POSTING_SEQUENCE =1 THEN 'New Initiate' ELSE 'Re-Initiate' END EXECUTION_TYPE, "+
				" T1.LOG_FILE_NAME LOG_FILE "+
				" FROM 	 "+
				" RA_TRN_POSTING_HISTORY T1, "+
				" RA_MST_EXTRACTION_HEADER T2, "+
				" RA_MST_EXTRACTION_DETAIL T3 "+
				" WHERE 	 "+
				" T1.COUNTRY = T2.COUNTRY "+
				" AND T1.LE_BOOK = T2.LE_BOOK "+
				" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
				" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
				" AND T3.FEED_ID = T1.EXTRACTION_FEED_ID "+
				" AND T1.COUNTRY = T3.COUNTRY "+
				" AND T1.LE_BOOK = T3.LE_BOOK "+
				" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
				" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
				" AND T1.COUNTRY IN ("+dObj.getCountry()+")  "+
				" AND T1.LE_BOOK IN ("+dObj.getLeBook()+")  "+
				" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
				" AND "+selectedDateField+" IN ("+dObj.getPosBusDate()+")");
		if("P".equalsIgnoreCase(dObj.getDateType())) {
			orderBy = "ORDER BY "+selectedDateField+" desc,COUNTRY,LE_BOOK,FREQUENCY,EXTRACTION_NAME,ETL_CATEGORY";
		}else
			orderBy = "ORDER BY "+resultDateField+" desc,COUNTRY,LE_BOOK,FREQUENCY,EXTRACTION_NAME,ETL_CATEGORY";
		
		sqlQuery = sqlQuery +orderBy; 
		}
		try
		{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(sqlQuery.toString(),getDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
				logger.error(((sqlQuery == null) ? "sqlQuery is Null" : sqlQuery.toString()));
				return null;
		}
	}
	/*public String getVisionBusinessDay(String coun) {
		String sql = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "select TO_CHAR(BUSINESS_DATE,'DD-Mon-RRRR') BUSINESS_DATE  from VISION_BUSINESS_DAY";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			sql = "	SELECT distinct FORMAT(BUSINESS_DATE,'dd-MMM-yyyy') BUSINESS_DATE FROM VISION_BUSINESS_DAY";
			
		}
		return getJdbcTemplate().queryForObject(sql,String.class);
	}*/

}
