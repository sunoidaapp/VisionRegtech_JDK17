package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.EtlPostingsVb;
@Component
public class EtlPostingsDao extends AbstractDao<EtlPostingsVb> {
	@Value("${app.databaseType}")
	private String databaseType;
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				EtlPostingsVb vObject = new EtlPostingsVb();
				vObject.setPostingDate(rs.getString("POSTING_DATE"));
				vObject.setBusinessDate(rs.getString("BUSINESS_DATE"));
				vObject.setPostingType(rs.getString("POSTING_TYPE"));
				vObject.setPostedBy(rs.getString("POSTED_USER_ID"));
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionFrequencyDesc(rs.getString("EXTRACTION_FREQUENCY_DESC"));
				vObject.setExtractionSequence(rs.getString("EXTRACTION_SEQUENCE"));
				vObject.setExtractionDescription(rs.getString("EXTRACTION_DESCRIPTION"));
				vObject.setFeedCategory(rs.getString("FEED_CATEGORY"));
				vObject.setFeedCategoryDesc(rs.getString("FEED_CATEGORY_DESC"));
				vObject.setExtractionFeedId(rs.getString("EXTRACTION_FEED_ID"));
				vObject.setExtractionFeedIdDesc(rs.getString("EXTRACTION_FEED_ID_DESC"));
				vObject.setDebugMode(rs.getString("DEBUG_MODE"));
				vObject.setDependentFlag(rs.getString("DEPENDENT_FLAG"));
				vObject.setDependentFeedId(rs.getString("DEPENDENT_FEED_ID"));
				vObject.setPostingSequence(rs.getString("POSTING_SEQUENCE"));				
				vObject.setExtractionEngine(rs.getString("EXTRACTION_ENGINE"));
				vObject.setExtractionEngineDesc(rs.getString("EXTRACTION_ENGINE_DESC"));
				vObject.setExrtactionType(rs.getString("EXTRACTION_TYPE"));
				vObject.setExrtactionTypeDesc(rs.getString("EXTRACTION_TYPE_DESC"));
				vObject.setScheduleType(rs.getString("SCHEDULE_TYPE"));
				vObject.setScheduleTypeDesc(rs.getString("SCHEDULE_TYPE_DESC"));
				vObject.setScheduleTime(rs.getString("SCHEDULE_TIME"));
				vObject.setEventName(rs.getString("EVENT_NAME"));
				vObject.setEventNameDesc(rs.getString("EVENT_NAME_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "EtlPosting";
		serviceDesc = "Etl Posting";
		tableName = "RA_TRN_POSTING_HISTORY";
		childTableName = "RA_TRN_POSTING_HISTORY";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	public List<EtlPostingsVb> getQueryPopupResults(EtlPostingsVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer reInitiate = null;
		StringBuffer newPosting = null;
		String orderBy = "";
		final int intKeyFieldsCount = 3;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			reInitiate = new StringBuffer("SELECT "+
					" TO_CHAR(SYSDATE,'DD-Mon-RRRR HH:MM:SS') POSTING_DATE,TO_CHAR((TO_DATE('"+dObj.getBusinessDate()+"','DD-MON-YYYY HH:MI:SS'),'DD-Mon-RRRR'), 'dd-MMM-yyyy') BUSINESS_DATE,'"+1+"' POSTING_TYPE,'"+dObj.getPostedBy()+"' POSTED_USER_ID,"+
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.EXTRACTION_SEQUENCE,"+
					" T1.EXTRACTION_DESCRIPTION,T2.FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" T2.FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE ='BLD' THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = EXTRACTION_FEED_ID) "+
					" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END EXTRACTION_FEED_ID_DESC,"+
					" T3.DEBUG_MODE,"+
					" T3.POSTING_SEQUENCE+1 POSTING_SEQUENCE,T1.EXTRACTION_ENGINE,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = T1.EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"+
					" T1.EXTRACTION_TYPE,CASE WHEN T1.EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" T1.SCHEDULE_TYPE,CASE WHEN T1.SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" TO_CHAR(T1.SCHEDULE_TIME,'HH:MM:SS') SCHEDULE_TIME,"+
					" T1.EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC"+
					" FROM "+
					" RA_MST_EXTRACTION_HEADER T1, "+
					" RA_MST_EXTRACTION_DETAIL T2, "+
					" RA_TRN_POSTING_HISTORY T3 "+
					" WHERE "+
					" T1.COUNTRY = T2.COUNTRY "+
					" AND T1.LE_BOOK = T2.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
					" AND T2.FEED_ID = T3.EXTRACTION_FEED_ID "+
					" AND T1.COUNTRY = T3.COUNTRY "+
					" AND T1.LE_BOOK = T3.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
					" AND T3.BUSINESS_DATE = '"+dObj.getBusinessDate()+"' "+
					" AND T3.POSTED_STATUS IN ('C','E') AND T1.COUNTRY IN ("+dObj.getCountry()+") AND T1.LE_BOOK IN ("+dObj.getLeBook()+") "+
					" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND T1.EXTRACTION_SEQUENCE IN ("+dObj.getExtractionSequence()+") "+
					" AND T1.EXTRACTION_STATUS = 0 "+
					" AND T3.POSTING_SEQUENCE = (SELECT MAX(POSTING_SEQUENCE) FROM RA_TRN_POSTING_HISTORY S1 "+
					" 	    WHERE T1.COUNTRY = S1.COUNTRY "+
					"        AND T1.LE_BOOK = S1.LE_BOOK "+
					"        AND T1.EXTRACTION_FREQUENCY = S1.EXTRACTION_FREQUENCY "+
					"        AND T1.EXTRACTION_SEQUENCE = S1.EXTRACTION_SEQUENCE "+
					" 	   AND S1.BUSINESS_DATE = T3.BUSINESS_DATE "+
					" 	   ) ");
			
			newPosting = new StringBuffer("SELECT	"+			
					" TO_CHAR(SYSDATE,'DD-Mon-RRRR HH:MM:SS') POSTING_DATE, TO_CHAR((TO_DATE('"+dObj.getBusinessDate()+"','DD-MON-YYYY HH:MI:SS'),'DD-Mon-RRRR'), 'dd-MMM-yyyy') BUSINESS_DATE,'"+1+"' POSTING_TYPE,'"+dObj.getPostedBy()+"'POSTED_USER_ID,		"+		
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.EXTRACTION_SEQUENCE,	"+			
					" EXTRACTION_DESCRIPTION,FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE ='BLD' THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = FEED_ID) "+
					" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = FEED_ID) END EXTRACTION_FEED_ID_DESC,"+
					" DEBUG_MODE,		"+		
					" '"+1+"' POSTING_SEQUENCE,EXTRACTION_ENGINE,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"+
					" EXTRACTION_TYPE,CASE WHEN EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" SCHEDULE_TYPE,CASE WHEN SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" TO_CHAR(SCHEDULE_TIME,'HH:MM:SS') SCHEDULE_TIME,"+				
					" EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC				"+
					" FROM				"+
					" RA_MST_EXTRACTION_HEADER T1,"+				
					" RA_MST_EXTRACTION_DETAIL T2		"+		
					" WHERE				"+
					" T1.COUNTRY = T2.COUNTRY		"+		
					" AND T1.LE_BOOK = T2.LE_BOOK		"+		
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY"+				
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE		"+	
					" AND T1.COUNTRY IN ("+dObj.getCountry()+") AND T1.LE_BOOK IN ("+dObj.getLeBook()+") "+
					" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND T1.EXTRACTION_SEQUENCE IN ("+dObj.getExtractionSequence()+")"+
					" AND T1.EXTRACTION_STATUS = 0 "+
					" AND NOT EXISTS (SELECT 'X' FROM RA_TRN_POSTING_HISTORY B 	"+			
					" WHERE B.COUNTRY = T1.COUNTRY  "+
					" AND B.LE_BOOK = T1.LE_BOOK  "+
					" AND B.EXTRACTION_FREQUENCY = T1.EXTRACTION_FREQUENCY "+
					" AND B.EXTRACTION_SEQUENCE = T1.EXTRACTION_SEQUENCE "+
					" AND B.EXTRACTION_FEED_ID = T2.FEED_ID "+
					" AND B.BUSINESS_DATE = '"+dObj.getBusinessDate()+"' "+
					" AND B.COUNTRY IN ("+dObj.getCountry()+") AND B.LE_BOOK IN ("+dObj.getLeBook()+") "+
					" AND B.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND B.EXTRACTION_SEQUENCE IN ("+dObj.getExtractionSequence()+"))");
			
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			reInitiate = new StringBuffer("SELECT "+
					" FORMAT(cast(getdate() as datetime), 'dd-MMM-yyyy HH:mm:ss') POSTING_DATE,FORMAT(cast('"+dObj.getBusinessDate()+"' as date), 'dd-MMM-yyyy') BUSINESS_DATE,'"+1+"' POSTING_TYPE,'"+dObj.getPostedBy()+"' POSTED_USER_ID,"+
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.EXTRACTION_SEQUENCE,"+
					" T1.EXTRACTION_DESCRIPTION,T2.FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" T2.FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE ='BLD' THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = EXTRACTION_FEED_ID) "+
					" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = EXTRACTION_FEED_ID) END EXTRACTION_FEED_ID_DESC,"+
					" T3.DEBUG_MODE,T3.DEPENDENT_FLAG,T3.DEPENDENT_FEED_ID,"+
					" T3.POSTING_SEQUENCE+1 POSTING_SEQUENCE,T1.EXTRACTION_ENGINE,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = T1.EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"+
					" T1.EXTRACTION_TYPE,CASE WHEN T1.EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" T1.SCHEDULE_TYPE,CASE WHEN T1.SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" CONVERT(varchar, T1.SCHEDULE_TIME, 108) SCHEDULE_TIME,"+
					" T1.EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC"+
					" FROM "+
					" RA_MST_EXTRACTION_HEADER T1, "+
					" RA_MST_EXTRACTION_DETAIL T2, "+
					" RA_TRN_POSTING_HISTORY T3 "+
					" WHERE "+
					" T1.COUNTRY = T2.COUNTRY "+
					" AND T1.LE_BOOK = T2.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE "+
					" AND T2.FEED_ID = T3.EXTRACTION_FEED_ID "+
					" AND T1.COUNTRY = T3.COUNTRY "+
					" AND T1.LE_BOOK = T3.LE_BOOK "+
					" AND T1.EXTRACTION_FREQUENCY = T3.EXTRACTION_FREQUENCY "+
					" AND T1.EXTRACTION_SEQUENCE = T3.EXTRACTION_SEQUENCE "+
					" AND T3.BUSINESS_DATE = '"+dObj.getBusinessDate()+"' "+
					" AND T3.POSTED_STATUS IN ('C','E') AND T1.COUNTRY IN ("+dObj.getCountry()+") AND T1.LE_BOOK IN ("+dObj.getLeBook()+") "+
					" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND T1.EXTRACTION_SEQUENCE IN ("+dObj.getExtractionSequence()+") "+
					" AND T1.EXTRACTION_STATUS = 0 "+
					" AND T3.POSTING_SEQUENCE = (SELECT MAX(POSTING_SEQUENCE) FROM RA_TRN_POSTING_HISTORY S1 "+
					" 	    WHERE T1.COUNTRY = S1.COUNTRY "+
					"        AND T1.LE_BOOK = S1.LE_BOOK "+
					"        AND T1.EXTRACTION_FREQUENCY = S1.EXTRACTION_FREQUENCY "+
					"        AND T1.EXTRACTION_SEQUENCE = S1.EXTRACTION_SEQUENCE "+
					" 	   AND S1.BUSINESS_DATE = T3.BUSINESS_DATE "+
					" 	   ) ");
			
			newPosting = new StringBuffer("SELECT	"+			
					" FORMAT(cast(getdate() as datetime), 'dd-MMM-yyyy HH:mm:ss') POSTING_DATE, FORMAT(cast('"+dObj.getBusinessDate()+"' as date), 'dd-MMM-yyyy') BUSINESS_DATE,'"+1+"' POSTING_TYPE,'"+dObj.getPostedBy()+"'POSTED_USER_ID,		"+		
					" T1.COUNTRY,T1.LE_BOOK,T1.EXTRACTION_FREQUENCY,CASE WHEN T1.EXTRACTION_FREQUENCY= 'D' THEN 'Daily' ELSE 'Monthly' END EXTRACTION_FREQUENCY_DESC,"+
					" T1.EXTRACTION_SEQUENCE,	"+			
					" EXTRACTION_DESCRIPTION,FEED_CATEGORY,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = FEED_CATEGORY_AT AND ALPHA_SUB_TAB = FEED_CATEGORY) FEED_CATEGORY_DESC,"+
					" FEED_ID EXTRACTION_FEED_ID,"+
					" CASE WHEN T1.EXTRACTION_ENGINE ='BLD' THEN (SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM = FEED_ID) "+
					" ELSE (SELECT GENERAL_DESCRIPTION FROM Template_Names WHERE TEMPLATE_NAME = FEED_ID) END EXTRACTION_FEED_ID_DESC,"+
					" DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID,"+
					" '"+1+"' POSTING_SEQUENCE,EXTRACTION_ENGINE,"+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = EXTRACTION_ENGINE_AT AND ALPHA_SUB_TAB = EXTRACTION_ENGINE) EXTRACTION_ENGINE_DESC,"+
					" EXTRACTION_TYPE,CASE WHEN EXTRACTION_TYPE = 'M' THEN 'Manual' ELSE 'Schedule' END EXTRACTION_TYPE_DESC,"+
					" SCHEDULE_TYPE,CASE WHEN SCHEDULE_TYPE = 'T' THEN 'Time' ELSE 'Event' END SCHEDULE_TYPE_DESC,"+
					" CONVERT(varchar, SCHEDULE_TIME, 108) SCHEDULE_TIME,"+				
					" EVENT_NAME, (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = T1.EVENT_NAME_AT AND ALPHA_SUB_TAB = T1.EVENT_NAME) EVENT_NAME_DESC				"+
					" FROM				"+
					" RA_MST_EXTRACTION_HEADER T1,"+				
					" RA_MST_EXTRACTION_DETAIL T2		"+		
					" WHERE				"+
					" T1.COUNTRY = T2.COUNTRY		"+		
					" AND T1.LE_BOOK = T2.LE_BOOK		"+		
					" AND T1.EXTRACTION_FREQUENCY = T2.EXTRACTION_FREQUENCY"+				
					" AND T1.EXTRACTION_SEQUENCE = T2.EXTRACTION_SEQUENCE		"+	
					" AND T1.COUNTRY IN ("+dObj.getCountry()+") AND T1.LE_BOOK IN ("+dObj.getLeBook()+") "+
					" AND T1.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND T1.EXTRACTION_SEQUENCE IN ("+dObj.getExtractionSequence()+")"+
					" AND T1.EXTRACTION_STATUS = 0 "+
					" AND NOT EXISTS (SELECT 'X' FROM RA_TRN_POSTING_HISTORY B 	"+			
					" WHERE B.COUNTRY = T1.COUNTRY  "+
					" AND B.LE_BOOK = T1.LE_BOOK  "+
					" AND B.EXTRACTION_FREQUENCY = T1.EXTRACTION_FREQUENCY "+
					" AND B.EXTRACTION_SEQUENCE = T1.EXTRACTION_SEQUENCE "+
					" AND B.EXTRACTION_FEED_ID = T2.FEED_ID "+
					" AND B.BUSINESS_DATE = '"+dObj.getBusinessDate()+"' "+
					" AND B.COUNTRY IN ("+dObj.getCountry()+") AND B.LE_BOOK IN ("+dObj.getLeBook()+") "+
					" AND B.EXTRACTION_FREQUENCY IN ("+dObj.getExtractionFrequency()+") "+
					" AND B.EXTRACTION_SEQUENCE IN ("+dObj.getExtractionSequence()+"))");
		}
		try
		{
			
			orderBy=" Order by COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_DESCRIPTION,FEED_CATEGORY,EXTRACTION_FEED_ID ";
			if("I".equalsIgnoreCase(dObj.getPostingType())) {
				return getQueryPopupResults(dObj,null, newPosting, null, orderBy, params);
			}else {
				return getQueryPopupResults(dObj,null, reInitiate, null, orderBy, params);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			//logger.error(((newPosting==null)? "strBufApprove is Null":newPosting.toString()));
			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
			}
}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertApprRecord(List<EtlPostingsVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;
		setServiceDefaults();
		try {
				for(EtlPostingsVb vObject : vObjects){
					retVal = doInsertionPosting(vObject);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					} else {
						retVal = doInsertionPostingHistory(vObject);
						if (retVal != Constants.SUCCESSFUL_OPERATION){
							exceptionCode = getResultObject(retVal);
							throw buildRuntimeCustomException(exceptionCode);
						} 
					}
				}
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
				return exceptionCode;
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	
	protected int doInsertionPostingHistory(EtlPostingsVb vObject){
		if(!ValidationUtil.isValid(vObject.getPostedStatus())) {
			vObject.setPostedStatus("P");
		}
		String scheduleTime = "";
		if("M".equalsIgnoreCase(vObject.getExrtactionType())) {
			if("ORACLE".equalsIgnoreCase(databaseType))
				scheduleTime = "TO_CHAR(SYSDATE,'HH:MM:SS')";
			else if("MSSQL".equalsIgnoreCase(databaseType)) {
				scheduleTime = "CONVERT(VARCHAR,getdate(),108)";
			}
		}else {
			scheduleTime ="'"+vObject.getScheduleTime()+"'";
		}
		String query =  " Insert Into RA_TRN_POSTING_HISTORY(POSTING_TYPE_AT,POSTING_TYPE,POSTING_DATE, BUSINESS_DATE,"
				+ "COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,POSTING_SEQUENCE,"
				+ "EXTRACTION_ENGINE_AT,EXTRACTION_ENGINE, EXRTACTION_TYPE,SCHEDULE_TYPE,SCHEDULE_TIME,EVENT_NAME_AT,EVENT_NAME,"
				+ "START_TIME,END_TIME,POSTED_STATUS,POSTED_BY,POSTED_TIME,LOG_FILE_NAME,DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+scheduleTime+",?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = {vObject.getPostingTypeAt(),vObject.getPostingType(),vObject.getPostingDate(),vObject.getBusinessDate(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getExtractionFeedId(),vObject.getPostingSequence(),
				vObject.getExtractionEngineAt(),vObject.getExtractionEngine(),vObject.getExrtactionType(),vObject.getScheduleType(),
				vObject.getEventNameAt(),vObject.getEventName(),vObject.getStartTime(),vObject.getEndTime(),vObject.getPostedStatus(),
				vObject.getPostedBy(),vObject.getPostedTime(),vObject.getLogFileName(),vObject.getDebugMode(),
				vObject.getDependentFlag(),vObject.getDependentFeedId()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPosting(EtlPostingsVb vObject){
		if(!ValidationUtil.isValid(vObject.getPostedStatus())) {
			vObject.setPostedStatus("P");
		}
		String scheduleTime = "";
		if("M".equalsIgnoreCase(vObject.getExrtactionType())) {
			if("ORACLE".equalsIgnoreCase(databaseType))
				scheduleTime = "TO_CHAR(SYSDATE,'HH:MM:SS')";
			else if("MSSQL".equalsIgnoreCase(databaseType)) {
				scheduleTime = "CONVERT(VARCHAR,getdate(),108)";
			}
		}else {
			scheduleTime = "'"+vObject.getScheduleTime()+"'";
		}
		String query =  " Insert Into RA_TRN_POSTING(POSTING_TYPE_AT,POSTING_TYPE,POSTING_DATE, BUSINESS_DATE,"
				+ "COUNTRY,LE_BOOK,EXTRACTION_FREQUENCY,EXTRACTION_SEQUENCE,EXTRACTION_FEED_ID,POSTING_SEQUENCE,"
				+ "EXTRACTION_ENGINE_AT,EXTRACTION_ENGINE, EXRTACTION_TYPE,SCHEDULE_TYPE,SCHEDULE_TIME,EVENT_NAME_AT,EVENT_NAME,"
				+ "START_TIME,END_TIME,POSTED_STATUS,POSTED_BY,POSTED_TIME,DEBUG_MODE,DEPENDENT_FLAG,DEPENDENT_FEED_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+scheduleTime+",?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getPostingTypeAt(),vObject.getPostingType(),vObject.getPostingDate(),vObject.getBusinessDate(),
				vObject.getCountry(),vObject.getLeBook(),vObject.getExtractionFrequency(),vObject.getExtractionSequence(),
				vObject.getExtractionFeedId(),vObject.getPostingSequence(),
				vObject.getExtractionEngineAt(),vObject.getExtractionEngine(),vObject.getExrtactionType(),vObject.getScheduleType(),
				vObject.getEventNameAt(),vObject.getEventName(),vObject.getStartTime(),vObject.getEndTime(),vObject.getPostedStatus(),
				vObject.getPostedBy(),vObject.getPostedTime(),vObject.getDebugMode(),
				vObject.getDependentFlag(),vObject.getDependentFeedId()};
		return getJdbcTemplate().update(query,args);
	}
}
