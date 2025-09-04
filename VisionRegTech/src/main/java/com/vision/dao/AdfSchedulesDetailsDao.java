package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AdfSchedulesDetailsVb;
import com.vision.vb.AdfSchedulesVb;
import com.vision.vb.AlphaSubTabVb;
@Component
public class AdfSchedulesDetailsDao extends AbstractDao<AdfSchedulesDetailsVb> {
	
//	public String calcDuration(String durationSec) {
//		int seconds = Integer.parseInt(durationSec) % 60;
//		int minutes = Integer.parseInt(durationSec) / 60;
//		if (minutes > 1 || minutes == 1)
//			durationSec = minutes + " Min " + seconds + " Sec ";
//		else
//			durationSec = seconds + " Sec ";
//		return durationSec;
//	}
	public String calcDuration(String durationSec) {
	    // Convert the string to a double first
	    double duration = Double.parseDouble(durationSec);

	    // Convert to total seconds as an integer
	    int totalSeconds = (int) duration;

	    // Break down into minutes and seconds
	    int minutes = totalSeconds / 60;
	    int seconds = totalSeconds % 60;

	    // Build the output string
	    if (minutes > 0)
	        return minutes + " Min " + seconds + " Sec";
	    else
	        return seconds + " Sec";
	}
	protected RowMapper getQueryResultsMapper(){
		 
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfSchedulesDetailsVb adfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
				
				adfSchedulesDetailsVb.setAcquisitionStatusAt(rs.getInt("ACQUISITION_STATUS_AT"));
				adfSchedulesDetailsVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
				adfSchedulesDetailsVb.setStatusDesc(rs.getString("ACQUISITION_STATUS_DESC"));
				/*if(ValidationUtil.isValid(rs.getInt("RECORDS_FETCHED_COUNT")))
					adfSchedulesDetailsVb.setRecordsFetchedCount(rs.getInt("RECORDS_FETCHED_COUNT"));*/
				if(ValidationUtil.isValid(rs.getString("RECORDS_FETCHED_COUNT")))
					adfSchedulesDetailsVb.setRecordsFetchedCount(rs.getString("RECORDS_FETCHED_COUNT"));
				adfSchedulesDetailsVb.setNextProcessTime(rs.getString("NEXT_PROCESS_TIME"));
				adfSchedulesDetailsVb.setDateLastExtraction(rs.getString("DATE_LAST_EXTRACTION"));
				adfSchedulesDetailsVb.setDuration(calcDuration(rs.getString("DURATION")));

				adfSchedulesDetailsVb.setAdfNumber(rs.getString("ADF_NUMBER"));
				adfSchedulesDetailsVb.setSubAdfNumber(rs.getString("SUB_ADF_NUMBER"));
				adfSchedulesDetailsVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				adfSchedulesDetailsVb.setCountry(rs.getString("COUNTRY"));
				adfSchedulesDetailsVb.setLeBook(rs.getString("LE_BOOK"));
				adfSchedulesDetailsVb.setAssociatedBuild(rs.getString("ASSOCIATED_BUILD"));
				adfSchedulesDetailsVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				adfSchedulesDetailsVb.setGeneralDescription(rs.getString("GENERAL_DESCRIPTION"));
				adfSchedulesDetailsVb.setRunItAt(rs.getInt("RUN_IT_AT"));
				adfSchedulesDetailsVb.setRunIt(rs.getString("RUN_IT"));
				adfSchedulesDetailsVb.setProgramTypeAt(rs.getInt("PROGRAM_TYPE_AT"));
				adfSchedulesDetailsVb.setProgramType(rs.getString("PROGRAM_TYPE"));
				adfSchedulesDetailsVb.setDebugMode(rs.getString("DEBUG_MODE"));
				adfSchedulesDetailsVb.setStartTime(rs.getString("START_TIME"));
				adfSchedulesDetailsVb.setEndTime(rs.getString("END_TIME"));
				adfSchedulesDetailsVb.setProgressPercent(rs.getString("PROGRESS_PERCENT"));
				adfSchedulesDetailsVb.setDateCreation(rs.getString("DATE_CREATION"));
				adfSchedulesDetailsVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				
				
				StringBuffer metadata = new StringBuffer();
				ResourceBundle rsb=CommonUtils.getResourceManger();
				if(ValidationUtil.isValid(rs.getString("EXT_ITERATION_COUNT"))){
					adfSchedulesDetailsVb.setExtIterationCount(rs.getString("EXT_ITERATION_COUNT"));
					metadata.append(rsb.getString("extIterationCount")+" : "+rs.getString("EXT_ITERATION_COUNT")+"||");
				}else{
					metadata.append(rsb.getString("extIterationCount")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("MANDATORY_FLAG"))){
					adfSchedulesDetailsVb.setMandatoryFlag(rs.getString("MANDATORY_FLAG"));
					metadata.append(rsb.getString("mandatoryFlag")+" : "+rs.getString("MANDATORY_FLAG")+"||");
				}else{
					metadata.append(rsb.getString("mandatoryFlag")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("CONNECTIVITY_TYPE"))){
					adfSchedulesDetailsVb.setConnectivityType(rs.getString("CONNECTIVITY_TYPE"));
					metadata.append(rsb.getString("connectivitytype")+" : "+rs.getString("CONNECTIVITY_TYPE")+"||");
				}else{
					metadata.append(rsb.getString("connectivitytype")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("CONNECTIVITY_DETAILS"))){
					adfSchedulesDetailsVb.setConnectivityDetails(rs.getString("CONNECTIVITY_DETAILS"));
					metadata.append(rsb.getString("connectivitydetails")+" : "+rs.getString("CONNECTIVITY_DETAILS")+"||");
				}else{
					metadata.append(rsb.getString("connectivitydetails")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("DATABASE_TYPE"))){
					adfSchedulesDetailsVb.setDatabaseType(rs.getString("DATABASE_TYPE"));
					metadata.append(rsb.getString("databaseType")+" : "+rs.getString("DATABASE_TYPE")+"||");
				}else{
					metadata.append(rsb.getString("databaseType")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("DATABASE_CONNECTIVITY_DETAILS"))){
					adfSchedulesDetailsVb.setDatabaseConnectivityDetails(rs.getString("DATABASE_CONNECTIVITY_DETAILS"));
					metadata.append(rsb.getString("databaseConnectivityType")+" : "+rs.getString("DATABASE_CONNECTIVITY_DETAILS")+"||");
				}else{
					metadata.append(rsb.getString("databaseConnectivityType")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("ALERT1_TIMESLOT"))){
					adfSchedulesDetailsVb.setAlert1Timeslot(rs.getString("ALERT1_TIMESLOT"));
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("timeSlot")+" : "+rs.getString("ALERT1_TIMESLOT")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("timeSlot")+" :||");
				}
				/*if(ValidationUtil.isValid(rs.getString("ALERT1_EMAIL_IDS"))){
					adfSchedulesDetailsVb.setAlert1EmailIds(rs.getString("ALERT1_EMAIL_IDS"));
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("emailID")+" : "+rs.getString("ALERT1_EMAIL_IDS")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("emailID")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("ALERT1_MOBILE_NO"))){
					adfSchedulesDetailsVb.setAlert1MobileNo(rs.getString("ALERT1_MOBILE_NO"));
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("mobileNumber")+" : "+rs.getString("ALERT1_MOBILE_NO")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("mobileNumber")+" :||");
				}*/
				if(ValidationUtil.isValid(rs.getString("ALERT1_SEND_FLAG"))){
					adfSchedulesDetailsVb.setAlert1SendFlag(rs.getString("ALERT1_SEND_FLAG"));	
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("alertSendFlag")+" : "+rs.getString("ALERT1_SEND_FLAG")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"1"+rsb.getString("alertSendFlag")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("ALERT2_TIMESLOT"))){
					adfSchedulesDetailsVb.setAlert2Timeslot(rs.getString("ALERT2_TIMESLOT"));	
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("timeSlot")+" : "+rs.getString("ALERT2_TIMESLOT")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("timeSlot")+" :||");
				}
				/*if(ValidationUtil.isValid(rs.getString("ALERT2_EMAIL_IDS"))){
					adfSchedulesDetailsVb.setAlert2EmailIds(rs.getString("ALERT2_EMAIL_IDS"));	
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("emailID")+" : "+rs.getString("ALERT2_EMAIL_IDS")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("emailID")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("ALERT2_MOBILE_NO"))){
					adfSchedulesDetailsVb.setAlert2MobileNo(rs.getString("ALERT2_MOBILE_NO"));	
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("mobileNumber")+" : "+rs.getString("ALERT2_MOBILE_NO")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("mobileNumber")+" :||");
				}*/
				if(ValidationUtil.isValid(rs.getString("ALERT2_SEND_FLAG"))){
					adfSchedulesDetailsVb.setAlert2SendFlag(rs.getString("ALERT2_SEND_FLAG"));	
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("alertSendFlag")+" : "+rs.getString("ALERT2_SEND_FLAG")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"2"+rsb.getString("alertSendFlag")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("ALERT3_TIMESLOT"))){
					adfSchedulesDetailsVb.setAlert3Timeslot(rs.getString("ALERT3_TIMESLOT"));	
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("timeSlot")+" : "+rs.getString("ALERT3_TIMESLOT")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("timeSlot")+" :||");
				}
				/*if(ValidationUtil.isValid(rs.getString("ALERT3_EMAIL_IDS"))){
					adfSchedulesDetailsVb.setAlert3EmailIds(rs.getString("ALERT3_EMAIL_IDS"));	
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("emailID")+" : "+rs.getString("ALERT3_EMAIL_IDS")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("emailID")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("ALERT3_MOBILE_NO"))){
					adfSchedulesDetailsVb.setAlert3MobileNo(rs.getString("ALERT3_MOBILE_NO"));
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("mobileNumber")+" : "+rs.getString("ALERT3_MOBILE_NO")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("mobileNumber")+" :||");
				}*/
				if(ValidationUtil.isValid(rs.getString("ALERT3_SEND_FLAG"))){
					adfSchedulesDetailsVb.setAlert3SendFlag(rs.getString("ALERT3_SEND_FLAG"));
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("alertSendFlag")+" : "+rs.getString("ALERT3_SEND_FLAG")+"||");
				}else{
					metadata.append(rsb.getString("alert")+"3"+rsb.getString("alertSendFlag")+" :||");
				}
				/*if(ValidationUtil.isValid(rs.getString("ADF_SUCCESSFUL_EMAIL_IDS"))){
					adfSchedulesDetailsVb.setAdfSuccessfulEmailIds(rs.getString("ADF_SUCCESSFUL_EMAIL_IDS"));
					metadata.append(rsb.getString("successful")+rsb.getString("emailID")+" : "+rs.getString("ADF_SUCCESSFUL_EMAIL_IDS")+"||");
				}else{
					metadata.append(rsb.getString("successful")+rsb.getString("emailID")+" :||");
				}*/
				if(ValidationUtil.isValid(rs.getString("ADF_SUCCESSFUL_EMAIL_FLAG"))){
					adfSchedulesDetailsVb.setAdfSuccessfulEmailFlag(rs.getString("ADF_SUCCESSFUL_EMAIL_FLAG"));
					metadata.append(rsb.getString("successEmailFlag")+" : "+rs.getString("ADF_SUCCESSFUL_EMAIL_FLAG")+"||");
				}else{
					metadata.append(rsb.getString("successEmailFlag")+" :||");
				}
				/*if(ValidationUtil.isValid(rs.getString("ADF_FAILED_EMAIL_IDS"))){
					adfSchedulesDetailsVb.setAdfFailedEmailIds(rs.getString("ADF_FAILED_EMAIL_IDS"));					
					metadata.append(rsb.getString("failed")+rsb.getString("emailID")+" : "+rs.getString("ADF_FAILED_EMAIL_IDS")+"||");
				}else{
					metadata.append(rsb.getString("failed")+rsb.getString("emailID")+" :||");
				}*/
				if(ValidationUtil.isValid(rs.getString("ADF_FAILED_EMAIL_FLAG"))){
					adfSchedulesDetailsVb.setAdfFailedEmailFlag(rs.getString("ADF_FAILED_EMAIL_FLAG"));					
					metadata.append(rsb.getString("failEmailFlag")+" : "+rs.getString("ADF_FAILED_EMAIL_FLAG")+"||");
				}else{
					metadata.append(rsb.getString("failEmailFlag")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("AUTOAUTH_TIME"))){
					adfSchedulesDetailsVb.setAutoauthTime(rs.getString("AUTOAUTH_TIME"));					
					metadata.append(rsb.getString("autoAuthTime")+" : "+rs.getString("AUTOAUTH_TIME")+"||");
				}else{
					metadata.append(rsb.getString("autoAuthTime")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("AUTH_ID"))){
					adfSchedulesDetailsVb.setAuthId(rs.getString("AUTH_ID"));					
					metadata.append(rsb.getString("authID")+" : "+rs.getString("AUTH_ID")+"||");
				}else{
					metadata.append(rsb.getString("authID")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("AUTH_TIME"))){
					adfSchedulesDetailsVb.setAuthTime(rs.getString("AUTH_TIME"));					
					metadata.append(rsb.getString("authTime")+" : "+rs.getString("AUTH_TIME")+"||");
				}else{
					metadata.append(rsb.getString("authTime")+" :||");
				}
				if(ValidationUtil.isValid(rs.getString("AUTH_STATUS"))){
					adfSchedulesDetailsVb.setAuthStatus(rs.getString("AUTH_STATUS"));					
					metadata.append(rsb.getString("authStatus")+" : "+rs.getString("AUTH_STATUS"));
				}else{
					metadata.append(rsb.getString("authStatus")+" :");
				}
				adfSchedulesDetailsVb.setAdfMetadata(String.valueOf(metadata));
				return adfSchedulesDetailsVb;
			}
		};
		return mapper;
	}

	public List<AdfSchedulesDetailsVb> getQueryDisplayResults(String buildNo) {

		final int intKeyFieldsCount = 1;
		String query = new String();
		if("MSSQL".equalsIgnoreCase(databaseType)||"SQLSERVER".equalsIgnoreCase(databaseType)) {
		 query = new String("SELECT TAppr.ADF_NUMBER, TAppr.SUB_ADF_NUMBER, TAppr.PROCESS_SEQUENCE, TAppr.COUNTRY, "+
					"				 TAppr.LE_BOOK, T1.MAJOR_BUILD ASSOCIATED_BUILD, TAppr.TEMPLATE_NAME, 207 RUN_IT_AT, "+
					"				 'Y' RUN_IT, 201 PROGRAM_TYPE_AT, 'MODULE' PROGRAM_TYPE, TAppr.ACQUISITION_STATUS_AT, "+
					"				 TAppr.ACQUISITION_STATUS,"
					+"         (SELECT ALPHA_SUBTAB_DESCRIPTION  "
					+"         FROM ALPHA_SUB_TAB  "
					+"         WHERE ALPHA_TAB = 1084  "
					+"           AND TAppr.ACQUISITION_STATUS = ALPHA_SUB_TAB "
					+"     ) AS ACQUISITION_STATUS_DESC,  "
					+ " TAppr.DEBUG_MODE, TAppr.START_TIME, TAppr.END_TIME, "+
					"				 TAppr.DATE_LAST_MODIFIED, TAppr.DATE_CREATION, 0 PROGRESS_PERCENT, "+
					"				 TAppr.TEMPLATE_NAME+' - '+TPAppr.GENERAL_DESCRIPTION GENERAL_DESCRIPTION, "+
					"				 ROUND ( "+
					"				    ISNULL((SELECT "+
					"				       Case When V1.PREEXT_START_TIME is not null AND V1.PREEXT_END_TIME is not null then DATEDIFF(MINUTE,ISNULL(V1.PREEXT_START_TIME,GETDATE()),V1.PREEXT_END_TIME) Else 0 End "+
					"				       + "+
					"				       Case When V1.EXT_START_TIME is not null AND V1.EXT_START_TIME is not null  then DATEDIFF(MINUTE,ISNULL(V1.EXT_START_TIME,GETDATE()),V1.EXT_END_TIME) Else 0 End "+
					"				       + "+
					"				       Case When V1.UPL_START_TIME is not null AND V1.UPL_START_TIME is not null then DATEDIFF(MINUTE,ISNULL(V1.UPL_START_TIME,GETDATE()),V1.UPL_END_TIME) Else 0 End "+
					"				       + "+
					"				       Case When V1.INT_START_TIME is not null then DATEDIFF(MINUTE,ISNULL(V1.INT_START_TIME,GETDATE()),V1.INT_END_TIME) Else 0 End "+
					"				       FROM ACQ_VERSION_NO V1 "+
					"				      WHERE     V1.Country = T1.Country "+
					"				            AND V1.LE_Book = T1.Le_Book "+
					"				            AND V1.Business_Date = T1.Business_Date "+
					"				            AND V1.Feed_Date = T1.FEed_Date "+
					"				            AND V1.TEmplate_name = TAppr.Template_Name "+
					"				            AND V1.Version_No = "+
					"				                   (SELECT MAX (V2.Version_No) "+
					"				                      FROM acq_Version_No V2 "+
					"				                     WHERE     V1.Country = V2.Country "+
					"				                           AND v1.Le_Book = V2.Le_Book "+
					"				                           AND V1.Business_Date = V2.Business_Date "+
					"				                           AND V1.FEed_Date = V2.Feed_Date "+
					"				                           AND V1.Template_Name = V2.Template_Name)),0) * 1440,0) DURATION, "+
					"				 TAppr.Records_Fetched_Count,"
					+dbFunctionFormats("TAppr.Next_Process_Time", "DATETIME_FORMAT", null)
					+ " Next_Process_Time,TAppr.Ext_Iteration_Count, "
					
					+dbFunctionFormats("TAppr.Date_Last_extraction", "DATETIME_FORMAT", null)
					+"				Date_Last_extraction ,(Select AA.Alpha_SubTab_Description From Alpha_Sub_Tab AA Where AA.Alpha_Tab = TAppr.Mandatory_Flag_AT And AA.Alpha_Sub_Tab = TAppr.Mandatory_Flag) Mandatory_Flag,"+
					"				 TAppr.CONNECTIVITY_TYPE, "+
					"				 Case When TAppr.CONNECTIVITY_TYPE = 'MACROVAR' Then TAppr.CONNECTIVITY_DETAILS Else Null End CONNECTIVITY_DETAILS, "+
					"				 TAppr.DATABASE_TYPE, Case When TAppr.DATABASE_TYPE = 'MACROVAR' Then TAppr.DATABASE_CONNECTIVITY_DETAILS Else Null End DATABASE_CONNECTIVITY_DETAILS, "+
					dbFunctionFormats("TAppr.ALERT1_TIMESLOT", "DATETIME_FORMAT", null)
					+"   ALERT1_TIMESLOT,ALERT1_TIMESLOT,  "
					+"     TAppr.ALERT1_EMAIL_IDS,  "
					+"     TAppr.ALERT1_MOBILE_NO,  "
					+"     TAppr.ALERT1_SEND_FLAG,  "
					+ dbFunctionFormats("TAppr.ALERT2_TIMESLOT", "DATETIME_FORMAT", null)
					+"    ALERT2_TIMESLOT,ALERT2_TIMESLOT,  "
					+"     TAppr.ALERT2_EMAIL_IDS,  "
					+"     TAppr.ALERT2_MOBILE_NO,  "
					+"     TAppr.ALERT2_SEND_FLAG,  "
					+ dbFunctionFormats("TAppr.ALERT3_TIMESLOT", "DATETIME_FORMAT", null)
					+"     ALERT3_TIMESLOT,  "
					+"     TAppr.ALERT3_EMAIL_IDS,  "
					+"     TAppr.ALERT3_MOBILE_NO,  "
					+"     TAppr.ALERT3_SEND_FLAG,  "
					+"     TAppr.ADF_SUCCESSFUL_EMAIL_IDS,  "
					+"     TAppr.ADF_SUCCESSFUL_EMAIL_FLAG,  "
					+"     TAppr.ADF_FAILED_EMAIL_IDS,  "
					+"     TAppr.ADF_FAILED_EMAIL_FLAG,  "
					+ dbFunctionFormats("TAppr.AUTOAUTH_TIME", "DATETIME_FORMAT", null) +" AUTOAUTH_TIME,TAppr.AUTOAUTH_TIME, TAppr.AUTH_ID, TAppr.AUTH_TIME, "+
					"				 (Select num_subtab_Description from num_sub_tab nst Where nst.num_tab = TAppr.AUTH_STATUS_NT and nst.num_sub_tab = TAppr.AUTH_STATUS) Auth_Status "+
					"				     FROM ADF_PROCESS_CONTROL TAppr, Template_Names TPAppr, ADF_SCHEDULES T1 "+
					"				    WHERE     TAppr.ADF_NUMBER = ? "+
					"				          AND TAppr.ADF_NUMBER = T1.ADF_NUMBER "+
					"				          AND TAppr.TEMPLATE_NAME = TPAppr.TEMPLATE_NAME "+
					"				 ORDER BY TAppr.SUB_ADF_NUMBER, TAppr.PROCESS_SEQUENCE ");
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
		
		 query = new String (" SELECT "
				+"     TAppr.ADF_NUMBER,  "
				+"     TAppr.SUB_ADF_NUMBER,  "
				+"     TAppr.PROCESS_SEQUENCE,  "
				+"     TAppr.COUNTRY,  "
				+"     TAppr.LE_BOOK,  "
				+"     T1.MAJOR_BUILD AS ASSOCIATED_BUILD,  "
				+"     TAppr.template_name ,  "
				+ "     TAppr.template_name ||' - '||GENERAL_DESCRIPTION GENERAL_DESCRIPTION ,"
				+"     207 AS RUN_IT_AT,  "
				+"     'Y' AS RUN_IT,  "
				+"     201 AS PROGRAM_TYPE_AT,  "
				+"     'MODULE' AS PROGRAM_TYPE,  "
				+"     TAppr.ACQUISITION_STATUS_AT,  "
				+"     TAppr.ACQUISITION_STATUS,  "
				+"         (SELECT ALPHA_SUBTAB_DESCRIPTION  "
				+"         FROM ALPHA_SUB_TAB  "
				+"         WHERE ALPHA_TAB = 1084  "
				+"           AND TAppr.ACQUISITION_STATUS = ALPHA_SUB_TAB "
				+"     ) AS ACQUISITION_STATUS_DESC,  "
				+"     TAppr.DEBUG_MODE,  "
				+"     TAppr.START_TIME,  "
				+"     TAppr.END_TIME,  "
				+"     TAppr.DATE_LAST_MODIFIED,  "
				+"     TAppr.DATE_CREATION,  "
				+"     0 AS PROGRESS_PERCENT,  "
//				+"     TAppr.TEMPLATE_NAME  AS GENERAL_DESCRIPTION,  "
				+"     LPAD( "
				+"         NVL((SELECT SUM(CASE  "
				+"                             WHEN V1.PREEXT_START_TIME IS NOT NULL  "
				+"                                  AND V1.PREEXT_END_TIME IS NOT NULL THEN  "
				+"                                 (V1.PREEXT_END_TIME - V1.PREEXT_START_TIME) * 24 * 60 * 60  "
				+"                             ELSE 0  "
				+"                         END + "
				+"                         CASE  "
				+"                             WHEN V1.EXT_START_TIME IS NOT NULL  "
				+"                                  AND V1.EXT_END_TIME IS NOT NULL THEN  "
				+"                                 (V1.EXT_END_TIME - V1.EXT_START_TIME) * 24 * 60 * 60  "
				+"                             ELSE 0  "
				+"                         END +  "
				+"                         CASE  "
				+"                             WHEN V1.UPL_START_TIME IS NOT NULL  "
				+"                                  AND V1.UPL_END_TIME IS NOT NULL THEN  "
				+"                                 (V1.UPL_END_TIME - V1.UPL_START_TIME) * 24 * 60 * 60  "
				+"                             ELSE 0  "
				+"                         END +  "
				+"                         CASE  "
				+"                             WHEN V1.INT_START_TIME IS NOT NULL  "
				+"                                  AND V1.INT_END_TIME IS NOT NULL THEN  "
				+"                                 (V1.INT_END_TIME - V1.INT_START_TIME) * 24 * 60 * 60  "
				+"                             ELSE 0  "
				+"                         END "
				+"                     ) "
				+"                 FROM ACQ_VERSION_NO V1  "
				+"                 WHERE V1.Country = T1.Country  "
				+"                   AND V1.LE_Book = T1.Le_Book  "
				+"                   AND V1.Business_Date = T1.Business_Date  "
				+"                   AND V1.Feed_Date = T1.Feed_Date  "
				+"                   AND V1.Template_Name = TAppr.Template_Name  "
				+"                   AND V1.Version_No = ( "
				+"                       SELECT MAX(V2.Version_No)  "
				+"                       FROM ACQ_VERSION_NO V2  "
				+"                       WHERE V1.Country = V2.Country  "
				+"                         AND V1.LE_Book = V2.LE_Book  "
				+"                         AND V1.Business_Date = V2.Business_Date  "
				+"                         AND V1.Feed_Date = V2.Feed_Date  "
				+"                         AND V1.Template_Name = V2.Template_Name "
				+"                   ) "
				+"             ), 0 "
				+"         ), 8, '0' "
				+"     ) AS DURATION, "
				+"     TO_CHAR(TAppr.Records_Fetched_Count, 'FM999999999999990') AS Records_Fetched_Count,  "
				+"     TO_CHAR(TAppr.Next_Process_Time, 'DD-Mon-YYYY HH24:MI:SS') AS Next_Process_Time,  "
				+"     TAppr.Ext_Iteration_Count,  "
				+"     TO_CHAR(TAppr.Date_Last_extraction, 'DD-Mon-YYYY HH24:MI:SS') AS Date_Last_extraction,  "
				+"     ( "
				+"         SELECT AA.Alpha_SubTab_Description  "
				+"         FROM Alpha_Sub_Tab AA  "
				+"         WHERE AA.Alpha_Tab = TAppr.Mandatory_Flag_AT  "
				+"           AND AA.Alpha_Sub_Tab = TAppr.Mandatory_Flag "
				+"     ) AS Mandatory_Flag,  "
				+"     TAppr.CONNECTIVITY_TYPE,  "
				+"     CASE  "
				+"         WHEN TAppr.CONNECTIVITY_TYPE = 'MACROVAR' THEN TAppr.CONNECTIVITY_DETAILS  "
				+"         ELSE NULL  "
				+"     END AS CONNECTIVITY_DETAILS,  "
				+"     TAppr.DATABASE_TYPE,  "
				+"     CASE  "
				+"         WHEN TAppr.DATABASE_TYPE = 'MACROVAR' THEN TAppr.DATABASE_CONNECTIVITY_DETAILS  "
				+"         ELSE NULL  "
				+"     END AS DATABASE_CONNECTIVITY_DETAILS,  "
				+ dbFunctionFormats("TAppr.ALERT1_TIMESLOT", "DATETIME_FORMAT", null)
				+"   ALERT1_TIMESLOT,  "
				+"     TAppr.ALERT1_EMAIL_IDS,  "
				+"     TAppr.ALERT1_MOBILE_NO,  "
				+"     TAppr.ALERT1_SEND_FLAG,  "
				+ dbFunctionFormats("TAppr.ALERT2_TIMESLOT", "DATETIME_FORMAT", null)
				+"    ALERT2_TIMESLOT,  "
				+"     TAppr.ALERT2_EMAIL_IDS,  "
				+"     TAppr.ALERT2_MOBILE_NO,  "
				+"     TAppr.ALERT2_SEND_FLAG,  "
				+ dbFunctionFormats("TAppr.ALERT3_TIMESLOT", "DATETIME_FORMAT", null)
				+"     ALERT3_TIMESLOT,  "
				+"     TAppr.ALERT3_EMAIL_IDS,  "
				+"     TAppr.ALERT3_MOBILE_NO,  "
				+"     TAppr.ALERT3_SEND_FLAG,  "
				+"     TAppr.ADF_SUCCESSFUL_EMAIL_IDS,  "
				+"     TAppr.ADF_SUCCESSFUL_EMAIL_FLAG,  "
				+"     TAppr.ADF_FAILED_EMAIL_IDS,  "
				+"     TAppr.ADF_FAILED_EMAIL_FLAG,  "
				+ dbFunctionFormats("TAppr.AUTOAUTH_TIME", "DATETIME_FORMAT", null)
				+"    AUTOAUTH_TIME,  "
				+"     TAppr.AUTH_ID,  "
				+"     TAppr.AUTH_TIME,  "
				+"         (SELECT num_subtab_Description  "
				+"         FROM num_sub_tab nst  "
				+"         WHERE nst.num_tab = TAppr.AUTH_STATUS_NT  "
				+"           AND nst.num_sub_tab = TAppr.AUTH_STATUS "
				+"     ) AS Auth_Status  "
				+" FROM  "
				+"     ADF_PROCESS_CONTROL TAppr,  "
				+"     Template_Names TPAppr,  "
				+"     ADF_SCHEDULES T1  "
				+" WHERE  "
				+"     TAppr.ADF_NUMBER = ? "
				+"     AND TAppr.ADF_NUMBER = T1.ADF_NUMBER  "
				+"     AND TAppr.TEMPLATE_NAME = TPAppr.TEMPLATE_NAME  "
				+" ORDER BY  "
				+"     TAppr.SUB_ADF_NUMBER,  "
				+"     TAppr.PROCESS_SEQUENCE ");
		}

		Object params[] = new Object[intKeyFieldsCount];
		params[0] = buildNo;
		try {
			return getJdbcTemplate().query(query, params, getQueryResultsMapper());

		} catch (Exception ex) {

			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query.toString()));
			if (params != null)
				for (int i = 0; i < params.length; i++)
					logger.error("objParams[" + i + "]" + params[i]);
			return null;
		}
	}
	public Integer getSubbildCountForMajorBuild(String buildNo){
		
		final int intKeyFieldsCount = 1;
		String query = new String("Select count(1) From ADF_PROCESS_CONTROL Where ADF_NUMBER = ? ");
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = buildNo; 
		try
		{
			int count = getJdbcTemplate().queryForObject(query, params, int.class);
			return count;
			
		}catch(Exception ex){
			
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query.toString()));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i]);
			return 0;
		}
	}
	@Override
	protected ExceptionCode doInsertApprRecordForNonTrans(AdfSchedulesDetailsVb vObject) throws RuntimeCustomException {
		List<AdfSchedulesDetailsVb> collTemp = null;
		strApproveOperation ="Add";
		strErrorDesc  = "";
		strCurrentOperation = "Add";
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null)
		{
			return getResultObject(Constants.ERRONEOUS_OPERATION);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 )
		{
			return getResultObject(Constants.DUPLICATE_KEY_INSERTION);
		}
		retVal = doInsertionAppr(vObject);
		if(retVal != Constants.SUCCESSFUL_OPERATION)
		{
			return getResultObject(Constants.ERRONEOUS_OPERATION);
		}
		return getResultObject(retVal);
	}
	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(AdfSchedulesDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strApproveOperation ="Add";
		strErrorDesc  = "";
		strCurrentOperation = "Add";
		setServiceDefaults();
		
		int recordCount = getSubbildCountForMajorBuild(vObject.getAdfNumber());
		retVal = doDeleteAppr(vObject);
		if(retVal != recordCount){
			strErrorDesc  = "Unable to delete all the detail records.";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	@Override
	protected int doDeleteAppr(AdfSchedulesDetailsVb vObject){
		String query = "Delete From ADF_PROCESS_CONTROL Where ADF_NUMBER = ? ";
		Object[] args = {vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	private int getRecordCountByStatus(AdfSchedulesDetailsVb adfSchedulesDetailsVb){
		String query = new String("SELECT COUNT(1) as RCCOUNT FROM ADF_PROCESS_CONTROL WHERE ADF_NUMBER = ? ");
		Object[] args = {adfSchedulesDetailsVb.getAdfNumber()};
		int count = getJdbcTemplate().queryForObject(query, args, int.class);
		return count;
//		return getJdbcTemplate().queryForInt(query,args);
	}
	private int getRecordCountByStatusForImmediateReInitiate(AdfSchedulesDetailsVb adfSchedulesDetailsVb){
		String query = new String("SELECT COUNT(1) as RCCOUNT FROM ADF_PROCESS_CONTROL WHERE ADF_NUMBER = ? ");
		Object[] args = {adfSchedulesDetailsVb.getAdfNumber()};
//		return getJdbcTemplate().queryForInt(query,args);
		int count = getJdbcTemplate().queryForObject(query, args, int.class);
		return count;
	}
	private int deleteRecordsByStatus(AdfSchedulesDetailsVb adfSchedulesDetailsVb){
		String query = new String("Delete From ADF_PROCESS_CONTROL Where ADF_NUMBER = ? AND ACQUISITION_STATUS = ?");
		Object[] args = {adfSchedulesDetailsVb.getAdfNumber(), adfSchedulesDetailsVb.getAcquisitionStatus()};
		return getJdbcTemplate().update(query,args);
	}
	private int updatePendingRecordsStatus(AdfSchedulesDetailsVb adfSchedulesDetailsVb, String operation){
		if(operation.equalsIgnoreCase("transfer")){
			String query = new String("UPDATE ADF_PROCESS_CONTROL SET NODE_OVERRIDE= ?,NODE_OVERRIDE_TIME= "+systemDate+
            "  Where ADF_NUMBER = ?");
			Object[] args = {adfSchedulesDetailsVb.getNode(), adfSchedulesDetailsVb.getAdfNumber()};
			return getJdbcTemplate().update(query,args);
		}else{
			String query = new String(" UPDATE ADF_PROCESS_CONTROL T1 SET T1.ACQUISITION_STATUS = ?, "+
					" T1.ADF_SUCCESSFUL_EMAIL_FLAG = CASE WHEN T1.ADF_SUCCESSFUL_EMAIL_FLAG LIKE '%I%' "+
					" THEN T1.ADF_SUCCESSFUL_EMAIL_FLAG ELSE 'NN' END, "+
					" T1.ADF_FAILED_EMAIL_FLAG = CASE WHEN T1.ADF_FAILED_EMAIL_FLAG LIKE '%I%' "+
					" THEN T1.ADF_FAILED_EMAIL_FLAG ELSE 'NN' END, "+
					" T1.Next_Process_Time = CASE When "+systemDate+"< T1.Start_Time Then T1.Start_Time "+
					" When "+systemDate+" > T1.End_Time Then T1.End_Time "+
					" When T1.Acquisition_Process_Type = 'RTT' Then T1.Next_Process_Time "+
					" ELSE "+systemDate+" END, "+
					" T1.Date_Last_Extraction = (SELECT T2.Date_Last_Extraction "+
					" FROM ADF_Data_Acquisition T2 WHERE T2.Country = T1.Country "+
					" AND T2.Le_Book = T1.Le_Book AND T2.Template_Name = T1.Template_Name), "+
					" T1.EXT_ITERATION_COUNT = CASE WHEN T1.EXT_ITERATION_COUNT > "+
					" (SELECT VV.Return_Value_Int FROM Vision_Variables_Narrow VV "+
					" WHERE VV.Variable = 'MAX_ITERATION_COUNT') THEN (SELECT VV.Return_Value_Int "+
					" FROM Vision_Variables_Narrow VV WHERE VV.Variable = 'MAX_ITERATION_COUNT') "+
					" ELSE T1.EXT_ITERATION_COUNT END, T1.DATE_LAST_MODIFIED = "+systemDate+
					" WHERE T1.ADF_NUMBER = ?");
					
			/*
			String query = new String(" UPDATE ADF_PROCESS_CONTROL T1 SET T1.ACQUISITION_STATUS = ?, "+
					" T1.ADF_SUCCESSFUL_EMAIL_FLAG = CASE WHEN T1.ADF_SUCCESSFUL_EMAIL_FLAG LIKE '%I%' "+
					" THEN T1.ADF_SUCCESSFUL_EMAIL_FLAG ELSE 'NN' END, "+
					" T1.ADF_FAILED_EMAIL_FLAG = CASE WHEN T1.ADF_FAILED_EMAIL_FLAG LIKE '%I%' "+
					" THEN T1.ADF_FAILED_EMAIL_FLAG ELSE 'NN' END, "+
					" T1.Next_Process_Time = CASE When getDate() < T1.Start_Time Then T1.Start_Time "+
					" When getDate() > T1.End_Time Then T1.End_Time "+
					" When T1.Acquisition_Process_Type = 'RTT' Then T1.Next_Process_Time  "+
					" ELSE getDate() END, "+
					" T1.Date_Last_Extraction = (SELECT T2.Date_Last_Extraction "+
					" FROM ADF_Data_Acquisition T2 WHERE T2.Country = T1.Country "+
					" AND T2.Le_Book = T1.Le_Book AND T2.Template_Name = T1.Template_Name), "+
					" T1.EXT_ITERATION_COUNT = CASE WHEN T1.EXT_ITERATION_COUNT > "+
					" (SELECT VV.Return_Value_Int FROM Vision_Variables_Narrow VV "+
					" WHERE VV.Variable = 'MAX_ITERATION_COUNT') THEN (SELECT VV.Return_Value_Int "+
					" FROM Vision_Variables_Narrow VV WHERE VV.Variable = 'MAX_ITERATION_COUNT') "+
					" ELSE T1.EXT_ITERATION_COUNT END, T1.DATE_LAST_MODIFIED = getDate() WHERE T1.ADF_NUMBER = ? ");
*/			
			Object[] args = {adfSchedulesDetailsVb.getAcquisitionStatus(), adfSchedulesDetailsVb.getAdfNumber()};
			return getJdbcTemplate().update(query,args);
		}
	}
	private int updatePendingRecordsStatusforImidiateResubmit(AdfSchedulesDetailsVb adfSchedulesDetailsVb){

			String query = new String("Update ADF_PROCESS_CONTROL T1 set T1.ACQUISITION_STATUS = case when T1.ACQUISITION_STATUS = 'VCIP' THEN 'VCRESUB'         "+                                                                                                                                                                                                   
					" when T1.ACQUISITION_STATUS ='VCERR' THEN 'VCRESUB'  "+                                                                                                                                                                                                                                                     
					" when T1.ACQUISITION_STATUS ='VCCOMP' THEN 'PREEXRESUB'   "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='PREEXTIP' THEN 'PREEXRESUB' "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='PREEXTERR' THEN 'PREEXRESUB' "+                                                                                                                                                                                                                                               
					" when T1.ACQUISITION_STATUS ='PREEXTCOMP' THEN 'EXTRESUB'  "+                                                                                                                                                                                                                                               
					" when T1.ACQUISITION_STATUS ='EXTIP' THEN 'EXTRESUB'    "+                                                                                                                                                                                                                                                  
					" when T1.ACQUISITION_STATUS ='EXTERR' THEN 'EXTRESUB'    "+                                                                                                                                                                                                                                                 
					" when T1.ACQUISITION_STATUS ='READYERR' THEN 'EXTRESUB'  "+                                                                                                                                                                                                                                                 
					" when T1.ACQUISITION_STATUS ='EXTCOMP' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End) "+                                                                                                               
					" when T1.ACQUISITION_STATUS ='UPLIP' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End) "+                                                                                                                
					" when T1.ACQUISITION_STATUS ='UPLERR' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End) "+                                                                                                               
					" when T1.ACQUISITION_STATUS ='UPLCOMP' THEN 'UPLCOMP' "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='INTIP' THEN 'INTRESUB'  "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='INTERR' THEN 'INTRESUB' "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='INTCOMP' THEN 'INTCOMP' "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='DEPERR' THEN 'YTP'    "+                                                                                                                                                                                                                                                      
					" when T1.ACQUISITION_STATUS ='SELFDEPERR' THEN 'YTP'  "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='WAIT' THEN 'YTP'     "+                                                                                                                                                                                                                                                       
					" when T1.ACQUISITION_STATUS ='EXTKILL' THEN 'EXTRESUB'    "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='READYERR' THEN 'EXTRESUB'   "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='INTKILL' THEN 'INTRESUB'  "+                                                                                                                                                                                                                                                  
					" when T1.ACQUISITION_STATUS ='PREEXTKILL' THEN 'PREEXRESUB' "+                                                                                                                                                                                                                                              
					" when T1.ACQUISITION_STATUS ='VCKILL' THEN 'VCRESUB'    "+                                                                                                                                                                                                                                                  
					" when T1.ACQUISITION_STATUS ='WAITKILL' THEN 'YTP'     "+                                                                                                                                                                                                                                                   
					" when T1.ACQUISITION_STATUS ='COMP' THEN 'INTRESUB'    "+                                                                                                                                                                                                                                                   
					" when T1.ACQUISITION_STATUS ='COMPNSERR' THEN 'INTRESUB'   "+                                                                                                                                                                                                                                               
					" when T1.ACQUISITION_STATUS ='COMPSERR' THEN 'INTRESUB'   "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='UPLKILL' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End)    "+                                                                                                           
					" when T1.ACQUISITION_STATUS ='UPLRECERR' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End)  "+                                                                                                           
					" else T1.ACQUISITION_STATUS end ,     "+                                                                                                                                                                                                                                                                    
					" T1.ADF_SUCCESSFUL_EMAIL_FLAG = Case When T1.ADF_SUCCESSFUL_EMAIL_FLAG like '%I%' Then T1.ADF_SUCCESSFUL_EMAIL_FLAG Else 'NN' End, "+                                                                                                                                                                       
					" T1.ADF_FAILED_EMAIL_FLAG = Case When T1.ADF_FAILED_EMAIL_FLAG like '%I%' Then T1.ADF_FAILED_EMAIL_FLAG Else 'NN' End ,   "+                                                                                                                                                                                
					" T1.Next_Process_Time = Case When "+systemDate+" < T1.Start_Time Then T1.Start_Time   "+                                                                                                                                                                                                                         
					" When "+systemDate+" > T1.End_Time Then T1.End_Time  "+                                                                                                                                                                                                                                                          
					" When T1.Acquisition_Process_Type = 'RTT' Then T1.Next_Process_Time     "+                                                                                                                                                                                                                                  
					" else "+systemDate+" End,                  "+                                                                                                                                                                                                                                                                    
					" T1.Date_Last_Extraction = Case When T1.Acquisition_Status not in ('COMP','COMPSERR','COMPNSERR') Then (Select T2.Date_Last_Extraction From ADF_Data_Acquisition T2 Where T2.Country = T1.Country And T2.Le_Book = T1.Le_Book And T2.Template_Name = T1.Template_Name ) Else T1.Date_Last_Extraction End , "+
					" T1.EXT_ITERATION_COUNT = Case When T1.EXT_ITERATION_COUNT > (select VV.Return_Value_Int from Vision_Variables_Narrow VV Where VV.Variable = 'MAX_ITERATION_COUNT') "+                                                                                                                                                       
					" Then (select VV.Return_Value_Int from Vision_Variables_Narrow VV Where VV.Variable = 'MAX_ITERATION_COUNT')      "+                                                                                                                                                                                        
					" Else T1.EXT_ITERATION_COUNT End,    "+                                                                                                                                                                                                                                                                     
					" T1.DATE_LAST_MODIFIED = "+systemDate+                                                                                                                                                                                                                                                                    
					" where T1.ADF_NUMBER=?  ");                                                                                                                                                                                                                                                                                 
					
			if("MSSQL".equalsIgnoreCase(databaseType)||"SQLSERVER".equalsIgnoreCase(databaseType)) {
		 query = new String("Update T1 set T1.ACQUISITION_STATUS = case when T1.ACQUISITION_STATUS = 'VCIP' THEN 'VCRESUB'         "+                                                                                                                                                                                                   
					" when T1.ACQUISITION_STATUS ='VCERR' THEN 'VCRESUB'  "+                                                                                                                                                                                                                                                     
					" when T1.ACQUISITION_STATUS ='VCCOMP' THEN 'PREEXRESUB'   "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='PREEXTIP' THEN 'PREEXRESUB' "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='PREEXTERR' THEN 'PREEXRESUB' "+                                                                                                                                                                                                                                               
					" when T1.ACQUISITION_STATUS ='PREEXTCOMP' THEN 'EXTRESUB'  "+                                                                                                                                                                                                                                               
					" when T1.ACQUISITION_STATUS ='EXTIP' THEN 'EXTRESUB'    "+                                                                                                                                                                                                                                                  
					" when T1.ACQUISITION_STATUS ='EXTERR' THEN 'EXTRESUB'    "+                                                                                                                                                                                                                                                 
					" when T1.ACQUISITION_STATUS ='READYERR' THEN 'EXTRESUB'  "+                                                                                                                                                                                                                                                 
					" when T1.ACQUISITION_STATUS ='EXTCOMP' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End) "+                                                                                                               
					" when T1.ACQUISITION_STATUS ='UPLIP' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End) "+                                                                                                                
					" when T1.ACQUISITION_STATUS ='UPLERR' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End) "+                                                                                                               
					" when T1.ACQUISITION_STATUS ='UPLCOMP' THEN 'UPLCOMP' "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='INTIP' THEN 'INTRESUB'  "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='INTERR' THEN 'INTRESUB' "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='INTCOMP' THEN 'INTCOMP' "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='DEPERR' THEN 'YTP'    "+                                                                                                                                                                                                                                                      
					" when T1.ACQUISITION_STATUS ='SELFDEPERR' THEN 'YTP'  "+                                                                                                                                                                                                                                                    
					" when T1.ACQUISITION_STATUS ='WAIT' THEN 'YTP'     "+                                                                                                                                                                                                                                                       
					" when T1.ACQUISITION_STATUS ='EXTKILL' THEN 'EXTRESUB'    "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='READYERR' THEN 'EXTRESUB'   "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='INTKILL' THEN 'INTRESUB'  "+                                                                                                                                                                                                                                                  
					" when T1.ACQUISITION_STATUS ='PREEXTKILL' THEN 'PREEXRESUB' "+                                                                                                                                                                                                                                              
					" when T1.ACQUISITION_STATUS ='VCKILL' THEN 'VCRESUB'    "+                                                                                                                                                                                                                                                  
					" when T1.ACQUISITION_STATUS ='WAITKILL' THEN 'YTP'     "+                                                                                                                                                                                                                                                   
					" when T1.ACQUISITION_STATUS ='COMP' THEN 'INTRESUB'    "+                                                                                                                                                                                                                                                   
					" when T1.ACQUISITION_STATUS ='COMPNSERR' THEN 'INTRESUB'   "+                                                                                                                                                                                                                                               
					" when T1.ACQUISITION_STATUS ='COMPSERR' THEN 'INTRESUB'   "+                                                                                                                                                                                                                                                
					" when T1.ACQUISITION_STATUS ='UPLKILL' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End)    "+                                                                                                           
					" when T1.ACQUISITION_STATUS ='UPLRECERR' THEN (case When (Select X1.Node_Override from ADF_SCHEDULES X1 Where X1.ADF_NUMBER = T1.ADF_NUMBER) is not null then 'EXTRESUB' else 'UPLRESUB' End)  "+                                                                                                           
					" else T1.ACQUISITION_STATUS end ,     "+                                                                                                                                                                                                                                                                    
					" T1.ADF_SUCCESSFUL_EMAIL_FLAG = Case When T1.ADF_SUCCESSFUL_EMAIL_FLAG like '%I%' Then T1.ADF_SUCCESSFUL_EMAIL_FLAG Else 'NN' End, "+                                                                                                                                                                       
					" T1.ADF_FAILED_EMAIL_FLAG = Case When T1.ADF_FAILED_EMAIL_FLAG like '%I%' Then T1.ADF_FAILED_EMAIL_FLAG Else 'NN' End ,   "+                                                                                                                                                                                
					" T1.Next_Process_Time = Case When GETDATE() < T1.Start_Time Then T1.Start_Time   "+                                                                                                                                                                                                                         
					" When GETDATE() > T1.End_Time Then T1.End_Time  "+                                                                                                                                                                                                                                                          
					" When T1.Acquisition_Process_Type = 'RTT' Then T1.Next_Process_Time     "+                                                                                                                                                                                                                                  
					" else GETDATE() End,                  "+                                                                                                                                                                                                                                                                    
					" T1.Date_Last_Extraction = Case When T1.Acquisition_Status not in ('COMP','COMPSERR','COMPNSERR') Then (Select T2.Date_Last_Extraction From ADF_Data_Acquisition T2 Where T2.Country = T1.Country And T2.Le_Book = T1.Le_Book And T2.Template_Name = T1.Template_Name ) Else T1.Date_Last_Extraction End , "+
					" T1.EXT_ITERATION_COUNT = Case When T1.EXT_ITERATION_COUNT > (select VV.Return_Value_Int from Vision_Variables_Narrow VV Where VV.Variable = 'MAX_ITERATION_COUNT') "+                                                                                                                                                       
					" Then (select VV.Return_Value_Int from Vision_Variables_Narrow VV Where VV.Variable = 'MAX_ITERATION_COUNT')      "+                                                                                                                                                                                        
					" Else T1.EXT_ITERATION_COUNT End,    "+                                                                                                                                                                                                                                                                     
					" T1.DATE_LAST_MODIFIED = GETDATE()  "+                                                                                                                                                                                                                                                                      
					" from ADF_PROCESS_CONTROL T1     "+                                                                                                                                                                                                                                                                         
					" where T1.ADF_NUMBER=?  ");
			}
		
		Object[] args = {adfSchedulesDetailsVb.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "AdfSchedules";
		serviceDesc = "AdfSchedules";
		tableName = "ADF_SCHEDULES";
		childTableName = "ADF_PROCESS_CONTROL";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	protected ExceptionCode updateStatusOfNonCompletedBuildsForImidiateResubmit(AdfSchedulesDetailsVb adfSchedulesDetailsVb, String operation) {
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc  = "";
		strCurrentOperation = "Re-Initiate";
		try
		{	
			AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
			lAdfSchedulesDetailsVb.setAdfNumber(adfSchedulesDetailsVb.getAdfNumber());
			lAdfSchedulesDetailsVb.setAcquisitionStatus("COMP");
			lAdfSchedulesDetailsVb.setNode(adfSchedulesDetailsVb.getNode());
			int recordCount = getRecordCountByStatusForImmediateReInitiate(adfSchedulesDetailsVb);
			lAdfSchedulesDetailsVb.setAcquisitionStatus("YTP");
			lAdfSchedulesDetailsVb.setStartTime("");
			lAdfSchedulesDetailsVb.setEndTime("");
			retVal = updatePendingRecordsStatusforImidiateResubmit(lAdfSchedulesDetailsVb);
			if(retVal != recordCount){
				strErrorDesc  = "Unable to update all the detail records.";
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION); 
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			logger.error(strErrorDesc, ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	protected ExceptionCode updateStatusOfNonCompletedBuilds(AdfSchedulesDetailsVb adfSchedulesDetailsVb, String operation, String reInitiateStatus) {
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc  = "";
		strCurrentOperation = "Re-Initiate";
		try
		{	
			AdfSchedulesDetailsVb lAdfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
			lAdfSchedulesDetailsVb.setAdfNumber(adfSchedulesDetailsVb.getAdfNumber());
			lAdfSchedulesDetailsVb.setAcquisitionStatus("COMP");
			lAdfSchedulesDetailsVb.setNode(adfSchedulesDetailsVb.getNode());
			int recordCount = getRecordCountByStatus(adfSchedulesDetailsVb);
			lAdfSchedulesDetailsVb.setAcquisitionStatus(reInitiateStatus);
			lAdfSchedulesDetailsVb.setStartTime("");
			lAdfSchedulesDetailsVb.setEndTime("");
			retVal = updatePendingRecordsStatus(lAdfSchedulesDetailsVb, operation);
			if(retVal != recordCount){
				strErrorDesc  = "Unable to update all the detail records.";
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION); 
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			logger.error(strErrorDesc, ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public List<AdfSchedulesDetailsVb> getMajorBuildList(){
		
		String query = "select DISTINCT NVL(MAJOR_BUILD,'ACQ'||FREQUENCY_PROCESS||'FEED') as MAJOR_BUILD from ADF_DATA_ACQUISITION";
		
		if("MSSQL".equalsIgnoreCase(databaseType)||"SQLSERVER".equalsIgnoreCase(databaseType)) {
			query = "select DISTINCT ISNULL(MAJOR_BUILD,concat('ACQ',FREQUENCY_PROCESS,'FEED')) MAJOR_BUILD from ADF_DATA_ACQUISITION";
		}
		Object params[] = null;
		try{
			return  getJdbcTemplate().query(query, params, getQueryResultsMapperBuildList());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			return null;
		}
	}
	protected RowMapper getQueryResultsMapperBuildList(){
		
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfSchedulesDetailsVb adfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
				adfSchedulesDetailsVb.setAssociatedBuild(rs.getString("MAJOR_BUILD"));//
				return adfSchedulesDetailsVb;
			}
		};
		return mapper;
	}
	public  String  getDateFromVisionBusinessDate(AdfSchedulesVb adfSchedulesVb){
		String CalLeBook = removeDescLeBook(adfSchedulesVb.getLeBook());
		String query = " Select "+dateFormat+"(Feed_Date, "+formatdate+") Feed_Date from Adf_feed_Controls where Country = '"+adfSchedulesVb.getCountry()+"' and LE_Book = '"+CalLeBook+"' "+ 
					   " and Acquisition_Process_type = '"+adfSchedulesVb.getAcquisitionProcessType()+"' and FREQUENCY_PROCESS='"+adfSchedulesVb.getFrequencyProcess()+"' "+
					   " and Major_build='"+adfSchedulesVb.getMajorBuild()+"' ";
		try{
			return getJdbcTemplate().queryForObject(query, String.class);
		}catch(Exception ex){
			logger.error("!!Feed Date not maintained in the Adf Feed Controls table for the build "+adfSchedulesVb.getMajorBuild()+"!!!!");
			return "";
		}
	}
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	public List<AlphaSubTabVb> findActiveAlphaSubTabsByAlphaTab(int alphaTab) throws DataAccessException {
		String sql = "select alpha_sub_tab,alpha_subtab_description, case when  alpha_sub_Tab = 'YTP' THEN 1 "
				+ " WHEN alpha_sub_Tab = 'PREEXRESUB' THEN 2 "
				+ " WHEN alpha_sub_Tab = 'EXTRESUB' THEN 3 "
				+ " WHEN alpha_sub_Tab = 'UPLRESUB' THEN 4 "
				+ " WHEN alpha_sub_Tab = 'INTRESUB' THEN 5 ELSE 99 END sort_field "
				+ " from alpha_sub_tab where alpha_tab = "+alphaTab+" and (alpha_Sub_tab = 'YTP' OR alpha_Sub_Tab like '%RESUB') "+
				"And alpha_sub_tab not in  ('DEPRESUB','VCRESUB') "+
				"order by sort_field ";
		return  getJdbcTemplate().query(sql, getMapper());
	}
	protected RowMapper getOrderMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb alphaSubTabVb = new AlphaSubTabVb();
				alphaSubTabVb.setAlphaTab(rs.getInt("ALPHA_TAB"));
				alphaSubTabVb.setAlphaSubTab(rs.getString("ALPHA_SUB_TAB"));
				alphaSubTabVb.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				alphaSubTabVb.setAlphaSubTabStatusNt(rs.getInt("ALPHA_SUBTAB_STATUS_NT"));
				alphaSubTabVb.setAlphaSubTabStatus(rs.getInt("ALPHA_SUBTAB_STATUS"));
				alphaSubTabVb.setDbStatus(rs.getInt("ALPHA_SUBTAB_STATUS"));
				alphaSubTabVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				alphaSubTabVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				alphaSubTabVb.setMaker(rs.getLong("MAKER"));
				alphaSubTabVb.setVerifier(rs.getLong("VERIFIER"));
				alphaSubTabVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				alphaSubTabVb.setDateCreation(rs.getString("DATE_CREATION"));
				alphaSubTabVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return alphaSubTabVb;
			}
		};
		return mapper;
	}
	public List<AlphaSubTabVb> findActiveAlphaSubTabsByAlphaTabOrderBy(int alphaTab) throws DataAccessException {
		String sql = "SELECT   "
				+" CASE WHEN ALPHA_SUB_TAB = 'P' THEN 1   "
				+" WHEN ALPHA_SUB_TAB = 'R' THEN 2  "
				+" WHEN ALPHA_SUB_TAB = 'W' THEN 3  "
				+" WHEN ALPHA_SUB_TAB = 'M' THEN 4  "
				+" WHEN ALPHA_SUB_TAB = 'I' THEN 5  "
				+" WHEN ALPHA_SUB_TAB = 'K' THEN 6  "
				+" WHEN ALPHA_SUB_TAB = 'E' THEN 7  "
				+" WHEN ALPHA_SUB_TAB = 'C' THEN 8  "
				+" ELSE 99 END AS ORDER1, T1.*   "
				+" FROM ALPHA_SUB_TAB T1 WHERE ALPHA_SUBTAB_STATUS = 0 AND ALPHA_TAB = 1204 AND ALPHA_SUB_TAB !='Z' ORDER BY ORDER1";
		return  getJdbcTemplate().query(sql, getOrderMapper());
	}
	
	public List<AdfSchedulesDetailsVb> getAdfSchedulesAudit(AdfSchedulesDetailsVb dObj){
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		String query = new String("Select T1.Audit_Trail_Sequence_Id,"+dateFormat
				+ "(Datetime_Stamp ,"+ dateFormatStr + ")Datetime_Stamp"
				+ ",T1.Audit_Description,T1.Audit_Description_Detail From ADF_Audit_Trail T1" +
                                   " Where T1.Country = '"+dObj.getCountry()+"' And T1.Le_Book = '"+CalLeBook+"' "
                                   		+ "And To_Char(BUSINESS_DATE, 'dd-Mon-yyyy') = '"+dObj.getBusinessDate()+"'"
                                   				+ " And To_Char(FEED_DATE, 'dd-Mon-yyyy') = '"+dObj.getFeedDate()+"' And T1.Template_Name = '"+dObj.getTemplateName()+"'");
if("MSSQL".equalsIgnoreCase(databaseType)||"SQLSERVER".equalsIgnoreCase(databaseType)) {
	query = "Select T1.Audit_Trail_Sequence_Id, Datetime_Stamp,T1.Audit_Description,T1.Audit_Description_Detail From ADF_Audit_Trail T1" +
            " Where T1.Country = '"+dObj.getCountry()+"' And T1.Le_Book = '"+CalLeBook+"' "
       		+ "And Format(BUSINESS_DATE, 'dd-MMM-yyyy') = '"+dObj.getBusinessDate()+"'"
       				+ " And Format(FEED_DATE, 'dd-MMM-yyyy') = '"+dObj.getFeedDate()+"' And T1.Template_Name = '"+dObj.getTemplateName()+"'";
}
		dObj.setQuery(query);
		try{
			return getQueryPopupResults(dObj);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query.toString()));
			return null;
		}
    }
	
	public List<AdfSchedulesDetailsVb> getQueryPopupResults(AdfSchedulesDetailsVb dObj){
		StringBuffer strBufApprove = new StringBuffer("");
		Vector<Object> params = new Vector<Object>();
		String strWhereNotExists = "";
		StringBuffer strBufPending =  new StringBuffer(dObj.getQuery());
		String orderBy=" Order by Audit_Trail_Sequence_Id desc";
		List<AdfSchedulesDetailsVb> collTemp = null;
		try{
//			return getQueryPopupResults(dObj,strBufPending, null, strWhereNotExists, orderBy, params,getMapperAudit());
			collTemp = getJdbcTemplate().query(dObj.getQuery().concat(" "+orderBy),getMapperAudit());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
		return collTemp;
	}
	
	protected RowMapper getMapperAudit(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfSchedulesDetailsVb adfSchedulesDetailsVb = new AdfSchedulesDetailsVb();
				adfSchedulesDetailsVb.setAuditTrailSequenceId(rs.getString(1));
				adfSchedulesDetailsVb.setDateTimeStamp(rs.getString(2));
				adfSchedulesDetailsVb.setAuditDescription(rs.getString(3));
				adfSchedulesDetailsVb.setAuditDescriptionDetail(rs.getString(4));
				return adfSchedulesDetailsVb;
			}
		};
		return mapper;
	}
		
		
}