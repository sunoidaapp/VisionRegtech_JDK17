package com.vision.dao;

import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AdfControlsVb;
import com.vision.vb.AdfSchedulesDetailsVb;
import com.vision.vb.AdfSchedulesVb;
import com.vision.vb.VisionUsersVb;
@Component
public class AdfControlsDao extends AbstractDao<AdfControlsVb> {
	public RowMapper getQueryPopupMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfControlsVb buildControlsVb = new AdfControlsVb();
				buildControlsVb.setCountry(rs.getString("COUNTRY"));
				buildControlsVb.setLeBook(rs.getString("LE_BOOK"));
				buildControlsVb.setBuild(rs.getString("BUILD"));
				buildControlsVb.setSubBuildNumber(rs.getString("SUB_BUILD_NUMBER"));
				buildControlsVb.setBcSequence(rs.getString("BC_SEQUENCE"));
				buildControlsVb.setBuildModule(rs.getString("BUILD_MODULE"));
				return buildControlsVb;
			}
		};
		return mapper;
	}
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfControlsVb buildControlsVb = new AdfControlsVb();
				buildControlsVb.setCountry(rs.getString("COUNTRY"));
				buildControlsVb.setLeBook(rs.getString("LE_BOOK"));
				buildControlsVb.setBuild(rs.getString("BUILD"));
				buildControlsVb.setSubBuildNumber(rs.getString("SUB_BUILD_NUMBER"));
				buildControlsVb.setBcSequence(rs.getString("BC_SEQUENCE"));
				buildControlsVb.setBuildModule(rs.getString("BUILD_MODULE"));
				buildControlsVb.setRunItAt(rs.getInt("RUN_IT_AT"));
				buildControlsVb.setRunIt(rs.getString("RUN_IT"));
				buildControlsVb.setLastStartDate(rs.getString("LAST_START_DATE"));
				buildControlsVb.setLastBuildDate(rs.getString("LAST_BUILD_DATE"));
				buildControlsVb.setBuildControlsStatusAt(rs.getInt("BUILD_CONTROLS_STATUS_AT"));
				buildControlsVb.setBuildControlsStatus(rs.getString("BUILD_CONTROLS_STATUS"));
				buildControlsVb.setSubmitterId(rs.getString("SUBMITTER_ID"));
				buildControlsVb.setBuildNumber(rs.getString("BUILD_NUMBER"));
				buildControlsVb.setExpandFlag(rs.getString("EXPAND_FLAG"));
				buildControlsVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				buildControlsVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				buildControlsVb.setMaker(rs.getInt("MAKER"));
				buildControlsVb.setVerifier(rs.getInt("VERIFIER"));
				buildControlsVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				buildControlsVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				buildControlsVb.setDateCreation(rs.getString("DATE_CREATION"));
				return buildControlsVb;
			}
		};
		return mapper;
	}

	protected RowMapper getQueryListMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfControlsVb buildControlsVb = new AdfControlsVb();
				buildControlsVb.setCountry(rs.getString("COUNTRY"));
				buildControlsVb.setLeBook(rs.getString("LE_BOOK"));
				buildControlsVb.setBuild(rs.getString("BUILD"));
				buildControlsVb.setSubBuildNumber(rs.getString("SUB_BUILD_NUMBER"));
				buildControlsVb.setBcSequence(rs.getString("BC_SEQUENCE"));
				buildControlsVb.setBuildModule(rs.getString("BUILD_MODULE"));
				buildControlsVb.setProgramDescription(rs.getString("PROGRAM_DESCRIPTION"));
				buildControlsVb.setRunItAt(207);
				buildControlsVb.setRunIt(rs.getString("RUN_IT"));
				buildControlsVb.setLastBuildDate(rs.getString("LAST_BUILD_DATE"));
				buildControlsVb.setBuildLevel(rs.getInt("BUILD_LEVEL"));
				buildControlsVb.setProgramType(rs.getString("PROGRAM_TYPE"));
				buildControlsVb.setExpandFlag(rs.getString("EXPAND_FLAG"));
				return buildControlsVb;
			}
		};
		return mapper;
	}
	
	protected RowMapper getQueryListModify(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfControlsVb adfControlsVb = new AdfControlsVb();
				adfControlsVb.setCountry(rs.getString("COUNTRY"));
				adfControlsVb.setLeBook(rs.getString("LE_BOOK"));
				adfControlsVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfControlsVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				adfControlsVb.setFilePattern(rs.getString("FILE_PATTERN"));
				adfControlsVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				adfControlsVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				adfControlsVb.setNextProcessTime(rs.getString("NEXT_PROCESS_TIME"));
				adfControlsVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
				adfControlsVb.setConnectivityType(rs.getString("CONNECTIVITY_TYPE"));
				adfControlsVb.setConnectivityDetails(rs.getString("CONNECTIVITY_DETAILS"));
				adfControlsVb.setDatabaseType(rs.getString("DATABASE_TYPE"));
				adfControlsVb.setDatabaseConnectivityDetails(rs.getString("DATABASE_CONNECTIVITY_DETAILS"));
				adfControlsVb.setServerFolderDetails(rs.getString("SERVER_FOLDER_DETAILS"));
				adfControlsVb.setSourceScriptType(rs.getString("SOURCE_SCRIPT_TYPE"));
				adfControlsVb.setSourceServerScripts(rs.getString("SOURCE_SERVER_SCRIPTS"));
				adfControlsVb.setTargetScriptType(rs.getString("TARGET_SCRIPT_TYPE"));
				adfControlsVb.setTargetServerScripts(rs.getString("TARGET_SERVER_SCRIPTS"));
				adfControlsVb.setReadinessScriptsType(rs.getString("READINESS_SCRIPTS_TYPE"));
				adfControlsVb.setAcquisitionReadinessScripts(rs.getString("ACQUISITION_READINESS_SCRIPTS"));
				adfControlsVb.setPreactivityScriptType(rs.getString("PREACTIVITY_SCRIPT_TYPE"));
				adfControlsVb.setPreactivityScripts(rs.getString("PREACTIVITY_SCRIPTS"));
				adfControlsVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
				adfControlsVb.setStartTime(rs.getString("START_TIME"));
				adfControlsVb.setEndTime(rs.getString("END_TIME"));
				adfControlsVb.setSubAdfNumber(rs.getString("SUB_ADF_NUMBER"));
				adfControlsVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				adfControlsVb.setAccessPermission(rs.getString("ACCESS_PERMISSION"));
				adfControlsVb.setAcquisitionReadinessFlag(rs.getString("ACQUISITION_READINESS_FLAG"));
				adfControlsVb.setRecordsFetchedCount(rs.getString("RECORDS_FETCHED_COUNT"));
				adfControlsVb.setDateLastExtraction(rs.getString("DATE_LAST_EXTRACTION"));
				adfControlsVb.setDebugMode(rs.getString("DEBUG_MODE"));
				adfControlsVb.setDebug(rs.getString("DEBUG_MODE"));
				adfControlsVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
				adfControlsVb.setIntegrityScriptType(rs.getString("INTEGRITY_SCRIPT_TYPE"));
				adfControlsVb.setIntegrityScriptName(rs.getString("INTEGRITY_SCRIPT_NAME"));
				adfControlsVb.setAlert1Timeslot(rs.getString("ALERT1_TIMESLOT"));
				adfControlsVb.setAlert1EmailIds(rs.getString("ALERT1_EMAIL_IDS"));
				adfControlsVb.setAlert1MobileNo(rs.getString("ALERT1_MOBILE_NO"));
				adfControlsVb.setAlert2Timeslot(rs.getString("ALERT2_TIMESLOT"));
				adfControlsVb.setAlert2EmailIds(rs.getString("ALERT2_EMAIL_IDS"));
				adfControlsVb.setAlert2MobileNo(rs.getString("ALERT2_MOBILE_NO"));
				adfControlsVb.setAlert3Timeslot(rs.getString("ALERT3_TIMESLOT"));
				adfControlsVb.setAlert3EmailIds(rs.getString("ALERT3_EMAIL_IDS"));
				adfControlsVb.setAlert3MobileNo(rs.getString("ALERT3_MOBILE_NO"));
				adfControlsVb.setAdfSuccessfulEmailIds(rs.getString("ADF_SUCCESSFUL_EMAIL_IDS"));
				adfControlsVb.setAdfFailedEmailIds(rs.getString("ADF_FAILED_EMAIL_IDS"));
				adfControlsVb.setAutoauthTime(rs.getString("AUTOAUTH_TIME"));
//				adfControlsVb.setAcquProcesscontrolStatus(rs.getString("ACQU_PROCESSCONTROL_STATUS"));
				adfControlsVb.setAcquProcesscontrolStatus(rs.getString("ACQUISITION_STATUS"));
				adfControlsVb.setCbEmailIds(rs.getString("CB_EMAIL_IDS"));
				/*adfControlsVb.setCountry(rs.getString("COUNTRY"));
				adfControlsVb.setLeBook(rs.getString("LE_BOOK"));
				adfControlsVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				adfControlsVb.setSubAdfNumber(rs.getString("SUB_ADF_NUMBER"));
				adfControlsVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				adfControlsVb.setTemplateName(rs.getString("TEMPLATE_NAME"));*/
				adfControlsVb.setMajorBuild("MAJOR_BUILD");
				adfControlsVb.setRunItAt(207);
				adfControlsVb.setRunIt(rs.getString("RUN_IT"));
				adfControlsVb.setLastBuildDate(rs.getString("DATE_LAST_EXTRACTION"));
				return adfControlsVb;
			}
	};
	return mapper;
}
	protected RowMapper getQueryListMapperMajorBuildChange(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfControlsVb adfControlsVb = new AdfControlsVb();
				adfControlsVb.setCountry(rs.getString("COUNTRY"));
				adfControlsVb.setLeBook(rs.getString("LE_BOOK"));
				adfControlsVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfControlsVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				adfControlsVb.setFilePattern(rs.getString("FILE_PATTERN"));
				adfControlsVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				adfControlsVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				adfControlsVb.setNextProcessTime(rs.getString("NEXT_PROCESS_TIME"));
				adfControlsVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
				adfControlsVb.setConnectivityType(rs.getString("CONNECTIVITY_TYPE"));
				adfControlsVb.setConnectivityDetails(rs.getString("CONNECTIVITY_DETAILS"));
				adfControlsVb.setDatabaseType(rs.getString("DATABASE_TYPE"));
				adfControlsVb.setDatabaseConnectivityDetails(rs.getString("DATABASE_CONNECTIVITY_DETAILS"));
				adfControlsVb.setServerFolderDetails(rs.getString("SERVER_FOLDER_DETAILS"));
				adfControlsVb.setSourceScriptType(rs.getString("SOURCE_SCRIPT_TYPE"));
				adfControlsVb.setSourceServerScripts(rs.getString("SOURCE_SERVER_SCRIPTS"));
				adfControlsVb.setTargetScriptType(rs.getString("TARGET_SCRIPT_TYPE"));
				adfControlsVb.setTargetServerScripts(rs.getString("TARGET_SERVER_SCRIPTS"));
				adfControlsVb.setReadinessScriptsType(rs.getString("READINESS_SCRIPTS_TYPE"));
				adfControlsVb.setAcquisitionReadinessScripts(rs.getString("ACQUISITION_READINESS_SCRIPTS"));
				adfControlsVb.setPreactivityScriptType(rs.getString("PREACTIVITY_SCRIPT_TYPE"));
				adfControlsVb.setPreactivityScripts(rs.getString("PREACTIVITY_SCRIPTS"));
				adfControlsVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
				adfControlsVb.setStartTime(rs.getString("START_TIME"));
				adfControlsVb.setEndTime(rs.getString("END_TIME"));
				adfControlsVb.setSubAdfNumber(rs.getString("SUB_ADF_NUMBER"));
				adfControlsVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				adfControlsVb.setAccessPermission(rs.getString("ACCESS_PERMISSION"));
				adfControlsVb.setAcquisitionReadinessFlag(rs.getString("ACQUISITION_READINESS_FLAG"));
				adfControlsVb.setRecordsFetchedCount(rs.getString("RECORDS_FETCHED_COUNT"));
				adfControlsVb.setDateLastExtraction(rs.getString("DATE_LAST_EXTRACTION"));
				adfControlsVb.setDebugMode(rs.getString("DEBUG_MODE"));
				adfControlsVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
				adfControlsVb.setIntegrityScriptType(rs.getString("INTEGRITY_SCRIPT_TYPE"));
				adfControlsVb.setIntegrityScriptName(rs.getString("INTEGRITY_SCRIPT_NAME"));
				adfControlsVb.setAlert1Timeslot(rs.getString("ALERT1_TIMESLOT"));
				adfControlsVb.setAlert1EmailIds(rs.getString("ALERT1_EMAIL_IDS"));
				adfControlsVb.setAlert1MobileNo(rs.getString("ALERT1_MOBILE_NO"));
				adfControlsVb.setAlert2Timeslot(rs.getString("ALERT2_TIMESLOT"));
				adfControlsVb.setAlert2EmailIds(rs.getString("ALERT2_EMAIL_IDS"));
				adfControlsVb.setAlert2MobileNo(rs.getString("ALERT2_MOBILE_NO"));
				adfControlsVb.setAlert3Timeslot(rs.getString("ALERT3_TIMESLOT"));
				adfControlsVb.setAlert3EmailIds(rs.getString("ALERT3_EMAIL_IDS"));
				adfControlsVb.setAlert3MobileNo(rs.getString("ALERT3_MOBILE_NO"));
				adfControlsVb.setAdfSuccessfulEmailIds(rs.getString("ADF_SUCCESSFUL_EMAIL_IDS"));
				adfControlsVb.setAdfFailedEmailIds(rs.getString("ADF_FAILED_EMAIL_IDS"));
				adfControlsVb.setAutoauthTime(rs.getString("AUTOAUTH_TIME"));
				adfControlsVb.setAcquProcesscontrolStatus(rs.getString("ACQU_PROCESSCONTROL_STATUS"));
				adfControlsVb.setCbEmailIds(rs.getString("CB_EMAIL_IDS"));
				/*adfControlsVb.setCountry(rs.getString("COUNTRY"));
				adfControlsVb.setLeBook(rs.getString("LE_BOOK"));
				adfControlsVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				adfControlsVb.setSubAdfNumber(rs.getString("SUB_ADF_NUMBER"));
				adfControlsVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				adfControlsVb.setTemplateName(rs.getString("TEMPLATE_NAME"));*/
				adfControlsVb.setMajorBuild("MAJOR_BUILD");
				adfControlsVb.setRunItAt(207);
				adfControlsVb.setRunIt(rs.getString("RUN_IT"));
				adfControlsVb.setLastBuildDate(rs.getString("DATE_LAST_EXTRACTION"));
				return adfControlsVb;
			}
		};
		return mapper;
	}
	public List<AdfControlsVb>  getQueryListForModify(AdfSchedulesVb adfSchedulesVb){
		List<AdfControlsVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		
		
		String query = new String(" Select T1.COUNTRY, T1.LE_BOOK, T1.BUSINESS_DATE, (select TN.TEMPLATE_NAME||' - '||TN.GENERAL_DESCRIPTION from TEMPLATE_NAMES TN where  TN.TEMPLATE_NAME=T1.TEMPLATE_NAME) TEMPLATE_NAME, T1.FILE_PATTERN, T1.EXCEL_FILE_PATTERN, T1.EXCEL_TEMPLATE_ID "+
				" , TO_CHAR (T1.NEXT_PROCESS_TIME, 'DD-MON-YYYY HH24:MI:SS') AS NEXT_PROCESS_TIME, T1.ACQUISITION_PROCESS_TYPE, T1.CONNECTIVITY_TYPE "+
				" , T1.CONNECTIVITY_DETAILS, T1.DATABASE_TYPE, T1.DATABASE_CONNECTIVITY_DETAILS, T1.SERVER_FOLDER_DETAILS, T1.SOURCE_SCRIPT_TYPE "+
				" , T1.SOURCE_SERVER_SCRIPTS, T1.TARGET_SCRIPT_TYPE, T1.TARGET_SERVER_SCRIPTS, T1.READINESS_SCRIPTS_TYPE, T1.ACQUISITION_READINESS_SCRIPTS "+
				" , T1.PREACTIVITY_SCRIPT_TYPE, T1.PREACTIVITY_SCRIPTS, T1.FREQUENCY_PROCESS, TO_CHAR (T1.START_TIME, 'DD-MON-YYYY HH24:MI:SS') AS START_TIME "+
				" , TO_CHAR (T1.END_TIME, 'DD-MON-YYYY HH24:MI:SS') AS END_TIME, T1.SUB_ADF_NUMBER, T1.PROCESS_SEQUENCE, T1.ACCESS_PERMISSION "+
				" , T1.ACQUISITION_READINESS_FLAG, T1.RECORDS_FETCHED_COUNT, TO_CHAR (T1.DATE_LAST_EXTRACTION, 'DD-MM-YYYY') AS DATE_LAST_EXTRACTION "+
				" , T1.DEBUG_MODE, T1.ACQUISITION_STATUS, T1.INTEGRITY_SCRIPT_TYPE, T1.INTEGRITY_SCRIPT_NAME, T1.ALERT1_TIMESLOT, T1.ALERT1_EMAIL_IDS "+
				" , T1.ALERT1_MOBILE_NO, T1.ALERT2_TIMESLOT, T1.ALERT2_EMAIL_IDS, T1.ALERT2_MOBILE_NO, T1.ALERT3_TIMESLOT, T1.ALERT3_EMAIL_IDS "+
				" , T1.ALERT3_MOBILE_NO, T1.ADF_SUCCESSFUL_EMAIL_IDS, T1.ADF_FAILED_EMAIL_IDS, T1.AUTOAUTH_TIME,T1.CB_EMAIL_IDS,T1.ACQUISITION_STATUS "+
				" , T1.RECORD_INDICATOR, T1.MAKER, T1.VERIFIER, T1.INTERNAL_STATUS, T1.DATE_LAST_MODIFIED, T1.DATE_CREATION  "+
				" , 'N' RUN_IT, T2.MAJOR_BUILD from ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 where T1.ADF_NUMBER=T2.ADF_NUMBER AND T1.ADF_NUMBER=? ");
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
		query = new String(" Select T1.COUNTRY, T1.LE_BOOK, T1.BUSINESS_DATE, (select (TN.TEMPLATE_NAME,' - ',TN.GENERAL_DESCRIPTION) from TEMPLATE_NAMES TN where  TN.TEMPLATE_NAME=T1.TEMPLATE_NAME) TEMPLATE_NAME, T1.FILE_PATTERN, T1.EXCEL_FILE_PATTERN, T1.EXCEL_TEMPLATE_ID "+
				" , Format (T1.NEXT_PROCESS_TIME, 'dd-MMM-yyyy HH:mm:ss') AS NEXT_PROCESS_TIME, T1.ACQUISITION_PROCESS_TYPE, T1.CONNECTIVITY_TYPE "+
				" , T1.CONNECTIVITY_DETAILS, T1.DATABASE_TYPE, T1.DATABASE_CONNECTIVITY_DETAILS, T1.SERVER_FOLDER_DETAILS, T1.SOURCE_SCRIPT_TYPE "+
				" , T1.SOURCE_SERVER_SCRIPTS, T1.TARGET_SCRIPT_TYPE, T1.TARGET_SERVER_SCRIPTS, T1.READINESS_SCRIPTS_TYPE, T1.ACQUISITION_READINESS_SCRIPTS "+
				" , T1.PREACTIVITY_SCRIPT_TYPE, T1.PREACTIVITY_SCRIPTS, T1.FREQUENCY_PROCESS, Format (T1.START_TIME, 'dd-MMM-yyyy HH:mm:ss') AS START_TIME "+
				" , Format (T1.END_TIME, 'dd-MMM-yyyy HH:mm:ss') AS END_TIME, T1.SUB_ADF_NUMBER, T1.PROCESS_SEQUENCE, T1.ACCESS_PERMISSION "+
				" , T1.ACQUISITION_READINESS_FLAG, T1.RECORDS_FETCHED_COUNT, Format (T1.DATE_LAST_EXTRACTION, 'dd-MMM-yyyy') AS DATE_LAST_EXTRACTION "+
				" , T1.DEBUG_MODE, T1.ACQUISITION_STATUS, T1.INTEGRITY_SCRIPT_TYPE, T1.INTEGRITY_SCRIPT_NAME, T1.ALERT1_TIMESLOT, T1.ALERT1_EMAIL_IDS "+
				" , T1.ALERT1_MOBILE_NO, T1.ALERT2_TIMESLOT, T1.ALERT2_EMAIL_IDS, T1.ALERT2_MOBILE_NO, T1.ALERT3_TIMESLOT, T1.ALERT3_EMAIL_IDS "+
				" , T1.ALERT3_MOBILE_NO, T1.ADF_SUCCESSFUL_EMAIL_IDS, T1.ADF_FAILED_EMAIL_IDS, T1.AUTOAUTH_TIME,T1.CB_EMAIL_IDS,T1.ACQUISITION_STATUS "+
				" , T1.RECORD_INDICATOR, T1.MAKER, T1.VERIFIER, T1.INTERNAL_STATUS, T1.DATE_LAST_MODIFIED, T1.DATE_CREATION  "+
				" , 'N' RUN_IT, T2.MAJOR_BUILD from ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 where T1.ADF_NUMBER=T2.ADF_NUMBER AND T1.ADF_NUMBER=? ");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = adfSchedulesVb.getAdfNumber();
		query+=" Order by T1.COUNTRY,T1.LE_BOOK,T1.PROCESS_SEQUENCE,T1.TEMPLATE_NAME";
		try{
			collTemp = getJdbcTemplate().query(query,objParams, getQueryListModify());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			logger.error(((query == null) ? "strQueryAppr is null" : query.toString()));
			return null;
		}
	}
	public List<AdfControlsVb>  getQueryList(AdfSchedulesVb adfSchedulesVb) // ADF Controls = ADF_DATA_ACQUISITION
	{
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		List<AdfControlsVb> collTemp = null;
		final int intKeyFieldsCount = 5;
		String CalLeBook = removeDescLeBook(adfSchedulesVb.getLeBook());
		String frequencyProcess = adfSchedulesVb.getFrequencyProcess();
		String userGroupProie = visionUsersVb.getUserGroup()+"-"+visionUsersVb.getUserProfile();
 
		
		String query = new String(" SELECT T1.COUNTRY, "
				+"        T1.LE_BOOK, "
				+"        TO_CHAR (TO_DATE ('01-01-1900', 'DD-MM-YYYY'), 'DD-MM-YYYY') "
				+"            AS BUSINESS_DATE, "
				+"        (SELECT TN.TEMPLATE_NAME || ' - ' || TN.GENERAL_DESCRIPTION "
				+"           FROM TEMPLATE_NAMES TN "
				+"          WHERE TN.TEMPLATE_NAME = T1.TEMPLATE_NAME) "
				+"            AS TEMPLATE_NAME, "
				+"        REPLACE ( "
				+"            REPLACE ( "
				+"                REPLACE ( "
				+"                    REPLACE ( "
				+"                        REPLACE (NVL (T1.FILE_PATTERN, T8.FILE_PATTERN), "
				+"                                 '#COUNTRY#', "
				+"                                 T1.COUNTRY), "
				+"                        '#LE_BOOK#', "
				+"                        T1.LE_BOOK), "
				+"                    '#TEMPLATE_NAME#', "
				+"                    T1.TEMPLATE_NAME), "
				+"                '#PROCESS_FREQ#', "
				+"                T1.FREQUENCY_PROCESS), "
				+"            '#ACQUISITION_PROCESS_TYPE#', "
				+"            T1.ACQUISITION_PROCESS_TYPE) "
				+"            AS FILE_PATTERN, "
				+"        REPLACE ( "
				+"            REPLACE ( "
				+"                REPLACE ( "
				+"                    REPLACE ( "
				+"                        REPLACE ( "
				+"                            NVL (T1.EXCEL_FILE_PATTERN, T8.EXCEL_FILE_PATTERN), "
				+"                            '#COUNTRY#', "
				+"                            T1.COUNTRY), "
				+"                        '#LE_BOOK#', "
				+"                        T1.LE_BOOK), "
				+"                    '#TEMPLATE_NAME#', "
				+"                    T1.TEMPLATE_NAME), "
				+"                '#PROCESS_FREQ#', "
				+"                T1.FREQUENCY_PROCESS), "
				+"            '#ACQUISITION_PROCESS_TYPE#', "
				+"            T1.ACQUISITION_PROCESS_TYPE) "
				+"            AS EXCEL_FILE_PATTERN, "
				+"        NVL (T1.EXCEL_TEMPLATE_ID, T8.EXCEL_TEMPLATE_ID) "
				+"            AS EXCEL_TEMPLATE_ID, "
				+"        TO_CHAR (SYSDATE, 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS NEXT_PROCESS_TIME, "
				+"        T1.ACQUISITION_PROCESS_TYPE, "
				+"        T1.CONNECTIVITY_TYPE, "
				+"        T1.CONNECTIVITY_DETAILS, "
				+"        T1.DATABASE_TYPE, "
				+"        T1.DATABASE_CONNECTIVITY_DETAILS, "
				+"        T1.SERVER_FOLDER_DETAILS, "
				+"        T1.SOURCE_SCRIPT_TYPE, "
				+"        T1.SOURCE_SERVER_SCRIPTS, "
				+"        T1.TARGET_SCRIPT_TYPE, "
				+"        T1.TARGET_SERVER_SCRIPTS, "
				+"        T1.READINESS_SCRIPTS_TYPE, "
				+"        T1.ACQUISITION_READINESS_SCRIPTS, "
				+"        T1.PREACTIVITY_SCRIPT_TYPE, "
				+"        T1.PREACTIVITY_SCRIPTS, "
				+"        T2.FREQUENCY_PROCESS, "
				+"        TO_CHAR (SYSDATE, 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS START_TIME, "
				+"        TO_CHAR (SYSDATE, 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS END_TIME, "
				+"        CASE "
				+"            WHEN NVL (T1.SUB_ADF_NUMBER, 0) = 0 "
				+"            THEN "
				+"                NVL (T8.SUB_ADF_NUMBER, 1) "
				+"            ELSE "
				+"                NVL (T1.SUB_ADF_NUMBER, 1) "
				+"        END "
				+"            AS SUB_ADF_NUMBER, "
				+"        CASE "
				+"            WHEN NVL (T1.PROCESS_SEQUENCE, 0) = 0 "
				+"            THEN "
				+"                NVL (T8.PROCESS_SEQUENCE, 0) "
				+"            ELSE "
				+"                NVL (T1.PROCESS_SEQUENCE, 0) "
				+"        END "
				+"            AS PROCESS_SEQUENCE, "
				+"        T1.ACCESS_PERMISSION, "
				+"        'N' "
				+"            AS ACQUISITION_READINESS_FLAG, "
				+"        0 "
				+"            AS RECORDS_FETCHED_COUNT, "
				+"        TO_CHAR (T1.DATE_LAST_EXTRACTION, 'DD-MON-YYYY') "
				+"            AS DATE_LAST_EXTRACTION, "
				+"        T1.DEBUG_MODE, "
				+"        'YTP' "
				+"            AS ACQUISITION_STATUS, "
				+"        T1.INTEGRITY_SCRIPT_TYPE, "
				+"        T1.INTEGRITY_SCRIPT_NAME, "
				+"        TO_CHAR (SYSDATE + (1 / 24), 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS ALERT1_TIMESLOT, "
				+"        T2.ALERT1_EMAIL_IDS, "
				+"        T2.ALERT1_MOBILE_NO, "
				+"        TO_CHAR (SYSDATE + (1.5 / 24), 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS ALERT2_TIMESLOT, "
				+"        T2.ALERT2_EMAIL_IDS, "
				+"        T2.ALERT2_MOBILE_NO, "
				+"        TO_CHAR (SYSDATE + (2 / 24), 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS ALERT3_TIMESLOT, "
				+"        T2.ALERT3_EMAIL_IDS, "
				+"        T2.ALERT3_MOBILE_NO, "
				+"        T2.ADF_SUCCESSFUL_EMAIL_IDS, "
				+"        T2.ADF_FAILED_EMAIL_IDS, "
				+"        T2.CB_EMAIL_IDS, "
				+"        TO_CHAR (SYSDATE + (3 / 24), 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS AUTOAUTH_TIME, "
				+"        T1.DATA_ACQU_STATUS "
				+"            AS ACQU_PROCESSCONTROL_STATUS, "
				+"        T1.RECORD_INDICATOR, "
				+"        T1.MAKER, "
				+"        T1.VERIFIER, "
				+"        T1.INTERNAL_STATUS, "
				+"        TO_CHAR (SYSDATE, 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS DATE_LAST_MODIFIED, "
				+"        TO_CHAR (SYSDATE, 'DD-MON-YYYY HH24:MI:SS') "
				+"            AS DATE_CREATION, "
				+"        'Y' "
				+"            AS RUN_IT, "
				+"        T1.MAJOR_BUILD "
				+"   FROM ADF_DATA_ACQUISITION      T1, "
				+"        ADF_ACQUISITION_CONTROL   T2, "
				+"        ADF_ACQUISITION_TIMESLOT  T3, "
				+"        LE_BOOK                   T4, "
				+"        TEMPLATE_NAMES            T8 "
				+"  WHERE     T1.COUNTRY = T2.COUNTRY "
				+"        AND T1.LE_BOOK = T2.LE_BOOK "
				+"        AND T1.FREQUENCY_PROCESS = T2.FREQUENCY_PROCESS "
				+"        AND NVL (T1.MAJOR_BUILD, 'ACQ' || T1.FREQUENCY_PROCESS || 'FEED') = "
				+"            T2.MAJOR_BUILD "
				+"        AND T2.FREQUENCY_PROCESS = T3.FREQUENCY_PROCESS "
				+"        AND T1.COUNTRY = T4.COUNTRY "
				+"        AND T1.LE_BOOK = T4.LE_BOOK "
				+"        AND T3.CATEGORY_TYPE = 'B' "
				+"        AND T8.TEMPLATE_NAME = T1.TEMPLATE_NAME "
				+"        AND T1.DATA_ACQU_STATUS = 0 "
				+"        AND T2.ACQU_CONTROL_STATUS = 0 "
				+"        AND T3.ACQU_TIMESLOT_STATUS = 0 "
				+"        AND T4.LEB_STATUS = 0 "
				+"        AND T1.CONNECTIVITY_DETAILS != 'DUMMY_CONNECTIONS_DETAILS' "
				+"        AND T1.COUNTRY = ? "
				+"        AND T1.LE_BOOK = ? "
				+"        AND T1.ACQUISITION_PROCESS_TYPE = ? "
				+"        AND T1.FREQUENCY_PROCESS = ? "
				+"        AND NVL (T1.MAJOR_BUILD, 'ACQ' || T1.FREQUENCY_PROCESS || 'FEED') = ? "
				+"        AND (   EXISTS "
				+"                    (SELECT 1 "
				+"                       FROM build_access blda "
				+"                      WHERE     blda.user_group = '"+visionUsersVb.getUserGroup()+"' "
				+"                            AND blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "
				+"                            AND blda.major_build = 'ALL' "
				+"                            AND blda.ba_status = 0) "
				+"             OR EXISTS "
				+"                    (SELECT 1 "
				+"                       FROM build_access blda "
				+"                      WHERE     blda.user_group = '"+visionUsersVb.getUserGroup()+"' "
				+"                            AND blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "
				+"                            AND blda.major_build = T1.Major_Build "
				+"                            AND blda.ba_status = 0 "
				+"                            AND INSTR ( "
				+"                                       ',' "
				+"                                    || NVL (blda.exclude_template_list, "
				+"                                            '@!xyz!@') "
				+"                                    || ',', "
				+"                                    ',' || T1.template_name || ',') = "
				+"                                0)) ");
		if("MSSQL".equalsIgnoreCase(databaseType)||"SQLSERVER".equalsIgnoreCase(databaseType)) {
		query = new String(" SELECT T1.COUNTRY, "+
		         " T1.LE_BOOK, "+
		         " format( CONVERT(DATE, '01-01-1900',103),'dd-MM-yyyy') BUSINESS_DATE, "+
		         " (SELECT CONCAT(TN.TEMPLATE_NAME, ' - ',TN.GENERAL_DESCRIPTION) "+
		           "  FROM TEMPLATE_NAMES TN "+
		           " WHERE TN.TEMPLATE_NAME = T1.TEMPLATE_NAME) "+
		            " TEMPLATE_NAME, "+
		           " REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(ISNULL (T1.FILE_PATTERN, T8.FILE_PATTERN) "+
                         " ,'#COUNTRY#',T1.COUNTRY)"+
                         ",'#LE_BOOK#',T1.LE_BOOK) "+
                         ",'#TEMPLATE_NAME#',T1.TEMPLATE_NAME) "+
                         " ,'#PROCESS_FREQ#',T1.FREQUENCY_PROCESS) "+
                         " ,'#ACQUISITION_PROCESS_TYPE#',T1.ACQUISITION_PROCESS_TYPE) FILE_PATTERN,"+ 
                 "REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(ISNULL (T1.EXCEL_FILE_PATTERN, T8.EXCEL_FILE_PATTERN)"+ 
                           ",'#COUNTRY#',T1.COUNTRY)"+
                           ",'#LE_BOOK#',T1.LE_BOOK)"+
                           ",'#TEMPLATE_NAME#',T1.TEMPLATE_NAME)"+
                           ",'#PROCESS_FREQ#',T1.FREQUENCY_PROCESS)"+
                           ",'#ACQUISITION_PROCESS_TYPE#',T1.ACQUISITION_PROCESS_TYPE) EXCEL_FILE_PATTERN,"+
		         " ISNULL (T1.EXCEL_TEMPLATE_ID, T8.EXCEL_TEMPLATE_ID) EXCEL_TEMPLATE_ID, "+
		         " Format (getDate(), 'dd-MMM-yyyy HH:mm:ss') AS NEXT_PROCESS_TIME, "+
		         " T1.ACQUISITION_PROCESS_TYPE, "+
		         " T1.CONNECTIVITY_TYPE, "+
		         " T1.CONNECTIVITY_DETAILS, "+
		         " T1.DATABASE_TYPE, "+
		         " T1.DATABASE_CONNECTIVITY_DETAILS, "+
		         " T1.SERVER_FOLDER_DETAILS, "+
		         " T1.SOURCE_SCRIPT_TYPE, "+
		         " T1.SOURCE_SERVER_SCRIPTS, "+
		         " T1.TARGET_SCRIPT_TYPE, "+
		         " T1.TARGET_SERVER_SCRIPTS, "+
		         " T1.READINESS_SCRIPTS_TYPE, "+
		         " T1.ACQUISITION_READINESS_SCRIPTS, "+
		         " T1.PREACTIVITY_SCRIPT_TYPE, "+
		         " T1.PREACTIVITY_SCRIPTS, "+
		         " T2.FREQUENCY_PROCESS, "+
		         " Format (getDate(), 'dd-MMM-yyyy HH:mm:ss') AS START_TIME, "+
		         " Format (getDate(), 'dd-MMM-yyyy HH:mm:ss') AS END_TIME, "+
		         " CASE "+
		            " WHEN ISNULL (T1.SUB_ADF_NUMBER, 0) = 0 THEN ISNULL (T8.SUB_ADF_NUMBER, 1) "+
		            " ELSE ISNULL (T1.SUB_ADF_NUMBER, 1) "+
		         " END "+
		            " SUB_ADF_NUMBER, "+
		         " CASE "+
		            " WHEN ISNULL (T1.PROCESS_SEQUENCE, 0) = 0 "+
		            " THEN "+
		               " ISNULL (T8.PROCESS_SEQUENCE, 0) "+
		            " ELSE "+
		               " ISNULL (T1.PROCESS_SEQUENCE, 0) "+
		         " END "+
		            " PROCESS_SEQUENCE, "+
		         " T1.ACCESS_PERMISSION, "+
		         " 'N' ACQUISITION_READINESS_FLAG, "+
		         " 0 RECORDS_FETCHED_COUNT, "+
		         " Format (T1.DATE_LAST_EXTRACTION, 'dd-MMM-yyyy') "+
		            " AS DATE_LAST_EXTRACTION, "+
		         " T1.DEBUG_MODE, "+
		         " 'YTP' ACQUISITION_STATUS, "+
		         " T1.INTEGRITY_SCRIPT_TYPE, "+
		         " T1.INTEGRITY_SCRIPT_NAME, "+
		         " Format (getDate() + (1/24),'dd-MMM-yyyy HH:mm:ss') "+
		            " AS ALERT1_TIMESLOT, "+
		         " T2.ALERT1_EMAIL_IDS, "+
		         " T2.ALERT1_MOBILE_NO, "+
		         " Format (getDate() + (1.5/24),'dd-MMM-yyyy HH:mm:ss') "+
		            " AS ALERT2_TIMESLOT, "+
		         " T2.ALERT2_EMAIL_IDS, "+
		         " T2.ALERT2_MOBILE_NO, "+
		         " Format (getDate() + (2/24),'dd-MMM-yyyy HH:mm:ss') "+
		            " AS ALERT3_TIMESLOT, "+
		         " T2.ALERT3_EMAIL_IDS, "+
		         " T2.ALERT3_MOBILE_NO, "+
		         " T2.ADF_SUCCESSFUL_EMAIL_IDS, "+
		         " T2.ADF_FAILED_EMAIL_IDS, "+
		         " T2.CB_EMAIL_IDS, "+
		         " Format (getDate() + (3/24),'dd-MMM-yyyy HH:mm:ss') "+
		           " AS AUTOAUTH_TIME, "+
		         " T1.DATA_ACQU_STATUS ACQU_PROCESSCONTROL_STATUS, "+
		         " T1.RECORD_INDICATOR, "+
		         " T1.MAKER, "+
		         " T1.VERIFIER, "+
		         " T1.INTERNAL_STATUS, "+
		         " Format (getDate(), 'dd-MMM-yyyy HH:mm:ss') AS DATE_LAST_MODIFIED, "+
		         " Format (getDate(), 'dd-MMM-yyyy HH:mm:ss') AS DATE_CREATION, "+
		         " 'Y' RUN_IT, "+
		         " T1.MAJOR_BUILD "+
		    " FROM ADF_DATA_ACQUISITION T1, "+
		         " ADF_ACQUISITION_CONTROL T2, "+
		         " ADF_ACQUISITION_TIMESLOT T3, "+
		         " LE_BOOK T4, "+
		         " TEMPLATE_NAMES T8 "+
		   " WHERE     T1.COUNTRY = T2.COUNTRY "+
		         " AND T1.LE_BOOK = T2.LE_BOOK "+
		         " AND T1.FREQUENCY_PROCESS = T2.FREQUENCY_PROCESS "+
		         //" AND T1.ACQUISITION_PROCESS_TYPE=T2.ACQUISITION_PROCESS_TYPE "+
		         " AND ISNULL (T1.MAJOR_BUILD, CONCAT('ACQ', T1.FREQUENCY_PROCESS,'FEED')) = T2.MAJOR_BUILD "+
		         " AND T2.FREQUENCY_PROCESS = T3.FREQUENCY_PROCESS "+
//		         " AND T2.ACQUISITION_PROCESS_TYPE=T3.ACQUISITION_PROCESS_TYPE"+
		         " AND T1.COUNTRY = T4.COUNTRY "+
		         " AND T1.LE_BOOK = T4.LE_BOOK "+
		         " AND T3.CATEGORY_TYPE='B' "+
		         " AND T8.TEMPLATE_NAME = T1.TEMPLATE_NAME "+
		         " AND T1.DATA_ACQU_STATUS = 0 "+
		         " AND T2.ACQU_CONTROL_STATUS = 0 "+
		         " AND T3.ACQU_TIMESLOT_STATUS = 0 "+
		         " AND T4.LEB_STATUS = 0 "+
		         " AND T1.CONNECTIVITY_DETAILS != 'DUMMY_CONNECTIONS_DETAILS' "+
		         " AND T1.COUNTRY = ? "+
		         " AND T1.LE_BOOK = ? "+
		         " AND T1.ACQUISITION_PROCESS_TYPE = ? "+
		         " AND T1.FREQUENCY_PROCESS = ? "+
		         " AND ISNULL (T1.MAJOR_BUILD, CONCAT('ACQ', T1.FREQUENCY_PROCESS,'FEED')) = ? "+
		         " AND (Exists (select 1 from build_access blda "+
		         " where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "+
		         " and blda.major_build = 'ALL' and blda.ba_status = 0) OR Exists (select 1 "+
		         " from build_access blda where blda.user_group = '"+visionUsersVb.getUserGroup()+"' and blda.user_profile = '"+visionUsersVb.getUserProfile()+"' "+
		         " and blda.major_build = T1.Major_Build and blda.ba_status = 0 "+
		         " and CHARINDEX(','+T1.template_name+',',','+ISNULL(blda. exclude_template_list,'@!xyz!@')+',') = 0)) ");
	}
		
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = adfSchedulesVb.getCountry(); 
		objParams[1] = CalLeBook;
		objParams[2] = adfSchedulesVb.getAcquisitionProcessType();
		objParams[3] = frequencyProcess.trim();
		objParams[4] = adfSchedulesVb.getMajorBuild();
		
		query+=" Order by T1.COUNTRY,T1.LE_BOOK,T1.PROCESS_SEQUENCE,T1.TEMPLATE_NAME";
		
		try{
			collTemp = getJdbcTemplate().query(query,objParams, getQueryListMapperMajorBuildChange());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			logger.error(((query == null) ? "strQueryAppr is null" : query.toString()));
			return null;
		}
	}
	public List<AdfControlsVb>  getQueryListFromProcessControl(AdfSchedulesVb adfSchedulesVb) // ADF Controls = ADF_DATA_ACQUISITION
	{
		List<AdfControlsVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String CalLeBook = removeDescLeBook(adfSchedulesVb.getLeBook());
		String frequencyProcess = adfSchedulesVb.getFrequencyProcess();
		/*frequencyProcess = frequencyProcess.replaceFirst("ACQ", "");
		frequencyProcess = frequencyProcess.substring(0, frequencyProcess.length()-4);*/
		String query = new String ("SELECT T1.COUNTRY, "+
				"    T1.LE_BOOK,  "+
				"    (CASE WHEN T1.FREQUENCY_PROCESS IN ('DLY','WLK') "+
				"        THEN "+
				"            T7.BUSINESS_DATE "+
				"    ELSE "+
				"            T7.CM_BUSINESS_DATE "+
				"    END) BUSINESS_DATE, "+
				"    T1.TEMPLATE_NAME, "+
				"    NVL(T1.FILE_PATTERN,T8.FILE_PATTERN) FILE_PATTERN, "+
				"    NVL(T1.EXCEL_FILE_PATTERN,T8.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, "+
				"    NVL(T1.EXCEL_TEMPLATE_ID,T8.EXCEL_TEMPLATE_ID) EXCEL_TEMPLATE_ID, "+
				/*"    TO_CHAR(CASE "+
				"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'ADF' THEN T3.ADF_START_TIME "+
				"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'XLS' THEN T3.NONADF_START_TIME "+
				"    ELSE LEAST (T3.ADF_START_TIME,T3.NONADF_START_TIME) END + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY')),'DD-MON-YYYY HH24:MI:SS') "+
				"    NEXT_PROCESS_TIME, "+*/
				"    TO_DATE(SYSDATE,'DD-MON-YYYY HH24:MI:SS') AS NEXT_PROCESS_TIME, "+
				"    T1.ACQUISITION_PROCESS_TYPE, "+
				"    T1.CONNECTIVITY_TYPE, "+
				"    T1.CONNECTIVITY_DETAILS, "+
				"    T1.DATABASE_TYPE, "+
				"    T1.DATABASE_CONNECTIVITY_DETAILS, "+
				"    T1.SERVER_FOLDER_DETAILS, "+
				"    T1.SOURCE_SCRIPT_TYPE, "+
				"    T1.SOURCE_SERVER_SCRIPTS, "+
				"    T1.TARGET_SCRIPT_TYPE, "+
				"    T1.TARGET_SERVER_SCRIPTS, "+
				"    T1.READINESS_SCRIPTS_TYPE, "+
				"    T1.ACQUISITION_READINESS_SCRIPTS, "+
				"    T1.PREACTIVITY_SCRIPT_TYPE, "+
				"    T1.PREACTIVITY_SCRIPTS, "+
				"    T2.FREQUENCY_PROCESS, "+
				/*"    TO_CHAR(CASE "+
				"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'ADF' THEN T3.ADF_START_TIME "+
				"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'XLS' THEN T3.NONADF_START_TIME "+
				"    ELSE LEAST (T3.ADF_START_TIME, T3.NONADF_START_TIME) "+
				"    END + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY')),'DD-MON-YYYY HH24:MI:SS')  AS "+
				"    START_TIME, "+*/
				"    TO_DATE(SYSDATE,'DD-MON-YYYY HH24:MI:SS') AS START_TIME, "+
				/*"    TO_CHAR(CASE "+
				"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'ADF' THEN T3.ADF_END_TIME "+
				"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'XLS' THEN T3.NONADF_END_TIME "+
				"    ELSE GREATEST (T3.ADF_END_TIME,T3.NONADF_END_TIME) "+
				"    END + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY')),'DD-MON-YYYY HH24:MI:SS')  AS "+
				"    END_TIME, "+*/
				"    TO_DATE(SYSDATE,'DD-MON-YYYY HH24:MI:SS') AS END_TIME, "+
				"    Case When NVL(T1.SUB_ADF_NUMBER,0) = 0 Then NVL(T8.SUB_ADF_NUMBER,1) Else NVL(T1.SUB_ADF_NUMBER,1) End SUB_ADF_NUMBER, "+
				"    Case When NVL(T1.PROCESS_SEQUENCE,0) = 0 Then NVL(T8.PROCESS_SEQUENCE,0) Else NVL(T1.PROCESS_SEQUENCE,0) End PROCESS_SEQUENCE, "+
				"    T1.ACCESS_PERMISSION, "+
				"    'N' ACQUISITION_READINESS_FLAG, "+
				"    0 RECORDS_FETCHED_COUNT, "+
				"    TO_CHAR(T1.DATE_LAST_EXTRACTION ,'DD-MON-YYYY HH24:MI:SS') AS DATE_LAST_EXTRACTION, "+
				"    T1.DEBUG_MODE, "+
				"    'YTP' ACQUISITION_STATUS, "+
				"    T1.INTEGRITY_SCRIPT_TYPE, "+
				"    T1.INTEGRITY_SCRIPT_NAME, "+
				"    TO_CHAR((T2.ALERT1_TIMESLOT + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'DD-MON-YYYY HH24:MI:SS') AS ALERT1_TIMESLOT, "+
				"    T2.ALERT1_EMAIL_IDS, "+
				"    T2.ALERT1_MOBILE_NO, "+
				"    TO_CHAR((T2.ALERT2_TIMESLOT + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'DD-MON-YYYY HH24:MI:SS') AS ALERT2_TIMESLOT, "+
				"    T2.ALERT2_EMAIL_IDS, "+
				"    T2.ALERT2_MOBILE_NO, "+
				"    TO_CHAR((T2.ALERT3_TIMESLOT + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'DD-MON-YYYY HH24:MI:SS') AS ALERT3_TIMESLOT , "+
				"    T2.ALERT3_EMAIL_IDS, "+
				"    T2.ALERT3_MOBILE_NO, "+
				"    T2.ADF_SUCCESSFUL_EMAIL_IDS, "+
				"    T2.ADF_FAILED_EMAIL_IDS, "+
				"    TO_CHAR((T2.AUTOAUTH_TIME + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'DD-MON-YYYY HH24:MI:SS') AS AUTOAUTH_TIME, "+
				"    T1.DATA_ACQU_STATUS ACQU_PROCESSCONTROL_STATUS, "+
				"    T1.RECORD_INDICATOR, "+
				"    T1.MAKER, "+
				"    T1.VERIFIER, "+
				"    T1.INTERNAL_STATUS, "+
				"    TO_CHAR(sysdate,'DD-MON-YYYY HH24:MI:SS') AS DATE_LAST_MODIFIED, "+
				"    TO_CHAR(sysdate,'DD-MON-YYYY HH24:MI:SS') AS DATE_CREATION, "+
				"	 'Y' RUN_IT, "+
				"	 T1.MAJOR_BUILD "+
				"FROM ADF_DATA_ACQUISITION T1, "+
				"    ADF_ACQUISITION_CONTROL T2, "+
				"    ADF_ACQUISITION_TIMESLOT T3, "+
				"    LE_BOOK T4, "+
				"    (select T7.*, T9.CM_Business_Date from VISION_BUSINESS_DAY t7, "+
				"    (SELECT LAST_DAY(TO_DATE(H1.VALUE||LPAD(H2.VALUE,2,'0'),'RRRRMM')) CM_BUSINESS_DATE "+
				"            FROM VISION_VARIABLES H1, "+
				"                VISION_VARIABLES H2 "+
				"            WHERE H1.VARIABLE = 'CURRENT_YEAR' "+
				"            AND H2.VARIABLE = 'CURRENT_MONTH') T9 ) T7, "+
				"    TEMPLATE_NAMES T8 "+
				" WHERE T1.COUNTRY = T2.COUNTRY "+
				" AND T1.LE_BOOK = T2.LE_BOOK "+
				" AND T1.FREQUENCY_PROCESS = T2.FREQUENCY_PROCESS "+
				" AND T1.ACQUISITION_PROCESS_TYPE=T2.ACQUISITION_PROCESS_TYPE "+
				" AND T3.CATEGORY_TYPE = T4.CATEGORY_TYPE "+
				" AND T2.FREQUENCY_PROCESS = T3.FREQUENCY_PROCESS "+
				" AND T2.ACQUISITION_PROCESS_TYPE=T3.ACQUISITION_PROCESS_TYPE"+
				" AND T1.COUNTRY = T4.COUNTRY "+
				" AND T1.LE_BOOK = T4.LE_BOOK "+
				" AND T1.COUNTRY = T7.COUNTRY "+
				" AND T1.LE_BOOK = T7.LE_BOOK "+
				" AND T8.TEMPLATE_NAME = T1.TEMPLATE_NAME "+
				" AND T1.DATA_ACQU_STATUS = 0 "+
				" AND T2.ACQU_CONTROL_STATUS = 0 "+
				" AND T3.ACQU_TIMESLOT_STATUS = 0 "+
				" AND T4.LEB_STATUS = 0 "+
				" AND T1.CONNECTIVITY_DETAILS != 'DUMMY_CONNECTIONS_DETAILS' "+
				" AND T1.COUNTRY=? AND T1.LE_BOOK=? AND T1.FREQUENCY_PROCESS=? ");
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
		 query = new String("SELECT T1.COUNTRY, "+
			"    T1.LE_BOOK,  "+
			"    (CASE WHEN T1.FREQUENCY_PROCESS IN ('DLY','WLK') "+
			"        THEN "+
			"            T7.BUSINESS_DATE "+
			"    ELSE "+
			"            T7.CM_BUSINESS_DATE "+
			"    END) BUSINESS_DATE, "+
			"    T1.TEMPLATE_NAME, "+
			"    ISNULL(T1.FILE_PATTERN,T8.FILE_PATTERN) FILE_PATTERN, "+
			"    ISNULL(T1.EXCEL_FILE_PATTERN,T8.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, "+
			"    ISNULL(T1.EXCEL_TEMPLATE_ID,T8.EXCEL_TEMPLATE_ID) EXCEL_TEMPLATE_ID, "+
			/*"    Format(CASE "+
			"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'ADF' THEN T3.ADF_START_TIME "+
			"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'XLS' THEN T3.NONADF_START_TIME "+
			"    ELSE LEAST (T3.ADF_START_TIME,T3.NONADF_START_TIME) END + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY')),'dd-MMM-yyyy HH:mm:ss') "+
			"    NEXT_PROCESS_TIME, "+*/
			"    TO_DATE(getDate(),'dd-MMM-yyyy HH:mm:ss') AS NEXT_PROCESS_TIME, "+
			"    T1.ACQUISITION_PROCESS_TYPE, "+
			"    T1.CONNECTIVITY_TYPE, "+
			"    T1.CONNECTIVITY_DETAILS, "+
			"    T1.DATABASE_TYPE, "+
			"    T1.DATABASE_CONNECTIVITY_DETAILS, "+
			"    T1.SERVER_FOLDER_DETAILS, "+
			"    T1.SOURCE_SCRIPT_TYPE, "+
			"    T1.SOURCE_SERVER_SCRIPTS, "+
			"    T1.TARGET_SCRIPT_TYPE, "+
			"    T1.TARGET_SERVER_SCRIPTS, "+
			"    T1.READINESS_SCRIPTS_TYPE, "+
			"    T1.ACQUISITION_READINESS_SCRIPTS, "+
			"    T1.PREACTIVITY_SCRIPT_TYPE, "+
			"    T1.PREACTIVITY_SCRIPTS, "+
			"    T2.FREQUENCY_PROCESS, "+
			/*"    Format(CASE "+
			"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'ADF' THEN T3.ADF_START_TIME "+
			"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'XLS' THEN T3.NONADF_START_TIME "+
			"    ELSE LEAST (T3.ADF_START_TIME, T3.NONADF_START_TIME) "+
			"    END + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY')),'dd-MMM-yyyy HH:mm:ss')  AS "+
			"    START_TIME, "+*/
			"    TO_DATE(getDate(),'dd-MMM-yyyy HH:mm:ss') AS START_TIME, "+
			/*"    Format(CASE "+
			"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'ADF' THEN T3.ADF_END_TIME "+
			"    WHEN T1.ACQUISITION_PROCESS_TYPE = 'XLS' THEN T3.NONADF_END_TIME "+
			"    ELSE GREATEST (T3.ADF_END_TIME,T3.NONADF_END_TIME) "+
			"    END + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY')),'dd-MMM-yyyy HH:mm:ss')  AS "+
			"    END_TIME, "+*/
			"    TO_DATE(getDate(),'dd-MMM-yyyy HH:mm:ss') AS END_TIME, "+
			"    Case When ISNULL(T1.SUB_ADF_NUMBER,0) = 0 Then ISNULL(T8.SUB_ADF_NUMBER,1) Else ISNULL(T1.SUB_ADF_NUMBER,1) End SUB_ADF_NUMBER, "+
			"    Case When ISNULL(T1.PROCESS_SEQUENCE,0) = 0 Then ISNULL(T8.PROCESS_SEQUENCE,0) Else ISNULL(T1.PROCESS_SEQUENCE,0) End PROCESS_SEQUENCE, "+
			"    T1.ACCESS_PERMISSION, "+
			"    'N' ACQUISITION_READINESS_FLAG, "+
			"    0 RECORDS_FETCHED_COUNT, "+
			"    Format(T1.DATE_LAST_EXTRACTION ,'dd-MMM-yyyy HH:mm:ss') AS DATE_LAST_EXTRACTION, "+
			"    T1.DEBUG_MODE, "+
			"    'YTP' ACQUISITION_STATUS, "+
			"    T1.INTEGRITY_SCRIPT_TYPE, "+
			"    T1.INTEGRITY_SCRIPT_NAME, "+
			"    Format((T2.ALERT1_TIMESLOT + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'dd-MMM-yyyy HH:mm:ss') AS ALERT1_TIMESLOT, "+
			"    T2.ALERT1_EMAIL_IDS, "+
			"    T2.ALERT1_MOBILE_NO, "+
			"    Format((T2.ALERT2_TIMESLOT + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'dd-MMM-yyyy HH:mm:ss') AS ALERT2_TIMESLOT, "+
			"    T2.ALERT2_EMAIL_IDS, "+
			"    T2.ALERT2_MOBILE_NO, "+
			"    Format((T2.ALERT3_TIMESLOT + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'dd-MMM-yyyy HH:mm:ss') AS ALERT3_TIMESLOT , "+
			"    T2.ALERT3_EMAIL_IDS, "+
			"    T2.ALERT3_MOBILE_NO, "+
			"    T2.ADF_SUCCESSFUL_EMAIL_IDS, "+
			"    T2.ADF_FAILED_EMAIL_IDS, "+
			"    Format((T2.AUTOAUTH_TIME + (T7.BUSINESS_DATE - TO_DATE('01-JAN-1900','DD-MON-YYYY'))),'dd-MMM-yyyy HH:mm:ss') AS AUTOAUTH_TIME, "+
			"    T1.DATA_ACQU_STATUS ACQU_PROCESSCONTROL_STATUS, "+
			"    T1.RECORD_INDICATOR, "+
			"    T1.MAKER, "+
			"    T1.VERIFIER, "+
			"    T1.INTERNAL_STATUS, "+
			"    Format(getDate(),'dd-MMM-yyyy HH:mm:ss') AS DATE_LAST_MODIFIED, "+
			"    Format(getDate(),'dd-MMM-yyyy HH:mm:ss') AS DATE_CREATION, "+
			"	 'Y' RUN_IT, "+
			"	 T1.MAJOR_BUILD "+
			"FROM ADF_DATA_ACQUISITION T1, "+
			"    ADF_ACQUISITION_CONTROL T2, "+
			"    ADF_ACQUISITION_TIMESLOT T3, "+
			"    LE_BOOK T4, "+
			"    (select T7.*, T9.CM_Business_Date from VISION_BUSINESS_DAY t7, "+
			"    (SELECT LAST_DAY(TO_DATE(H1.VALUE||LPAD(H2.VALUE,2,'0'),'RRRRMM')) CM_BUSINESS_DATE "+
			"            FROM VISION_VARIABLES H1, "+
			"                VISION_VARIABLES H2 "+
			"            WHERE H1.VARIABLE = 'CURRENT_YEAR' "+
			"            AND H2.VARIABLE = 'CURRENT_MONTH') T9 ) T7, "+
			"    TEMPLATE_NAMES T8 "+
			" WHERE T1.COUNTRY = T2.COUNTRY "+
			" AND T1.LE_BOOK = T2.LE_BOOK "+
			" AND T1.FREQUENCY_PROCESS = T2.FREQUENCY_PROCESS "+
//			" AND T1.ACQUISITION_PROCESS_TYPE=T2.ACQUISITION_PROCESS_TYPE "+
			" AND T3.CATEGORY_TYPE = 'B' "+
			" AND T2.FREQUENCY_PROCESS = T3.FREQUENCY_PROCESS "+
//			" AND T2.ACQUISITION_PROCESS_TYPE=T3.ACQUISITION_PROCESS_TYPE"+
			" AND T1.COUNTRY = T4.COUNTRY "+
			" AND T1.LE_BOOK = T4.LE_BOOK "+
			" AND T1.COUNTRY = T7.COUNTRY "+
			" AND T1.LE_BOOK = T7.LE_BOOK "+
			" AND T8.TEMPLATE_NAME = T1.TEMPLATE_NAME "+
			" AND T1.DATA_ACQU_STATUS = 0 "+
			" AND T2.ACQU_CONTROL_STATUS = 0 "+
			" AND T3.ACQU_TIMESLOT_STATUS = 0 "+
			" AND T4.LEB_STATUS = 0 "+
			" AND T1.CONNECTIVITY_DETAILS != 'DUMMY_CONNECTIONS_DETAILS' "+
			" AND T1.COUNTRY=? AND T1.LE_BOOK=? AND T1.FREQUENCY_PROCESS=? ");

		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = adfSchedulesVb.getCountry(); //1 
		objParams[1] = CalLeBook; //2
		objParams[2] = frequencyProcess.trim(); //3
		query+=" Order by T1.COUNTRY,T1.LE_BOOK,T1.PROCESS_SEQUENCE,T1.TEMPLATE_NAME";
		try{
			collTemp = getJdbcTemplate().query(query,objParams, getQueryListMapperMajorBuildChange());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			logger.error(((query == null) ? "strQueryAppr is null" : query.toString()));
			return null;
		}
	}
	
	public Long getMaxBuildNumber(){
		String query = new String("Select max(ADF_NUMBER) From ADF_SCHEDULES");
		try
		{
			return getJdbcTemplate().queryForObject(query, Long.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 0l;
		}
	}
	@Override
	protected List<AdfControlsVb> selectApprovedRecord(AdfControlsVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<AdfControlsVb> doSelectPendingRecord(AdfControlsVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected void setServiceDefaults(){
		serviceName = "AdfControls";
		serviceDesc = CommonUtils.getResourceManger().getString("AdfControls");
		tableName = "ADF_DATA_ACQUISITION";
		childTableName = "ADF_DATA_ACQUISITION";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}


	@Override
	protected String frameErrorMessage(AdfControlsVb vObject, String strOperation)
	{
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg =  strErrMsg + " COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + " LE_BOOK:" + vObject.getLeBook();
			strErrMsg =  strErrMsg + " BUILD:" + vObject.getBuild();
			strErrMsg =  strErrMsg + " SUB_BUILD_NUMBER:" + vObject.getSubBuildNumber();
			strErrMsg =  strErrMsg + " BC_SEQUENCE:" + vObject.getBcSequence();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}


	@Override
	protected String getAuditString(AdfControlsVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getBusinessDate()))
				strAudit.append("BUSINESS_DATE"+auditDelimiterColVal+vObject.getBusinessDate().trim());
			else
				strAudit.append("BUSINESS_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getTemplateName()))
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+vObject.getTemplateName().trim());
			else
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getFilePattern()))
				strAudit.append("FILE_PATTERN"+auditDelimiterColVal+vObject.getFilePattern().trim());
			else
				strAudit.append("FILE_PATTERN"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getExcelFilePattern()))
				strAudit.append("EXCEL_FILE_PATTERN"+auditDelimiterColVal+vObject.getExcelFilePattern().trim());
			else
				strAudit.append("EXCEL_FILE_PATTERN"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getExcelTemplateId()))
				strAudit.append("EXCEL_TEMPLATE_ID"+auditDelimiterColVal+vObject.getExcelTemplateId().trim());
			else
				strAudit.append("EXCEL_TEMPLATE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getNextProcessTime()))
				strAudit.append("NEXT_PROCESS_TIME"+auditDelimiterColVal+vObject.getNextProcessTime().trim());
			else
				strAudit.append("NEXT_PROCESS_TIME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquisitionProcessTypeAt()))
				strAudit.append("ACQUISITION_PROCESS_TYPE_AT"+auditDelimiterColVal+vObject.getAcquisitionProcessTypeAt());
			else
				strAudit.append("ACQUISITION_PROCESS_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquisitionProcessType()))
				strAudit.append("ACQUISITION_PROCESS_TYPE"+auditDelimiterColVal+vObject.getAcquisitionProcessType().trim());
			else
				strAudit.append("ACQUISITION_PROCESS_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getConnectivityTypeAt()))
				strAudit.append("CONNECTIVITY_TYPE_AT"+auditDelimiterColVal+vObject.getConnectivityTypeAt());
			else
				strAudit.append("CONNECTIVITY_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getConnectivityType()))
				strAudit.append("CONNECTIVITY_TYPE"+auditDelimiterColVal+vObject.getConnectivityType().trim());
			else
				strAudit.append("CONNECTIVITY_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getConnectivityDetails()))
				strAudit.append("CONNECTIVITY_DETAILS"+auditDelimiterColVal+vObject.getConnectivityDetails().trim());
			else
				strAudit.append("CONNECTIVITY_DETAILS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getDatabaseTypeAt()))
				strAudit.append("DATABASE_TYPE_AT"+auditDelimiterColVal+vObject.getDatabaseTypeAt());
			else
				strAudit.append("DATABASE_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getDatabaseType()))
				strAudit.append("DATABASE_TYPE"+auditDelimiterColVal+vObject.getDatabaseType().trim());
			else
				strAudit.append("DATABASE_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getDatabaseConnectivityDetails()))
				strAudit.append("DATABASE_CONNECTIVITY_DETAILS"+auditDelimiterColVal+vObject.getDatabaseConnectivityDetails().trim());
			else
				strAudit.append("DATABASE_CONNECTIVITY_DETAILS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getServerFolderDetails()))
				strAudit.append("SERVER_FOLDER_DETAILS"+auditDelimiterColVal+vObject.getServerFolderDetails().trim());
			else
				strAudit.append("SERVER_FOLDER_DETAILS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getSourceScriptTypeAt()))
				strAudit.append("SOURCE_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getSourceScriptTypeAt());
			else
				strAudit.append("SOURCE_SCRIPT_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getSourceScriptType()))
				strAudit.append("SOURCE_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getSourceScriptType().trim());
			else
				strAudit.append("SOURCE_SCRIPT_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getSourceServerScripts()))
				strAudit.append("SOURCE_SERVER_SCRIPTS"+auditDelimiterColVal+vObject.getSourceServerScripts().trim());
			else
				strAudit.append("SOURCE_SERVER_SCRIPTS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getTargetScriptTypeAt()))
				strAudit.append("TARGET_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getTargetScriptTypeAt());
			else
				strAudit.append("TARGET_SCRIPT_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getTargetScriptType()))
				strAudit.append("TARGET_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getTargetScriptType().trim());
			else
				strAudit.append("TARGET_SCRIPT_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getTargetServerScripts()))
				strAudit.append("TARGET_SERVER_SCRIPTS"+auditDelimiterColVal+vObject.getTargetServerScripts().trim());
			else
				strAudit.append("TARGET_SERVER_SCRIPTS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			
			if(ValidationUtil.isValid(vObject.getReadinessScriptsTypeAt()))
				strAudit.append("READINESS_SCRIPTS_TYPE_AT"+auditDelimiterColVal+vObject.getReadinessScriptsTypeAt());
			else
				strAudit.append("READINESS_SCRIPTS_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getReadinessScriptsType()))
				strAudit.append("READINESS_SCRIPTS_TYPE"+auditDelimiterColVal+vObject.getReadinessScriptsType().trim());
			else
				strAudit.append("READINESS_SCRIPTS_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquisitionReadinessScripts()))
				strAudit.append("ACQUISITION_READINESS_SCRIPTS"+auditDelimiterColVal+vObject.getAcquisitionReadinessScripts().trim());
			else
				strAudit.append("ACQUISITION_READINESS_SCRIPTS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getPreactivityScriptTypeAt()))
				strAudit.append("PREACTIVITY_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getPreactivityScriptTypeAt());
			else
				strAudit.append("PREACTIVITY_SCRIPT_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getPreactivityScriptType()))
				strAudit.append("PREACTIVITY_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getPreactivityScriptType().trim());
			else
				strAudit.append("PREACTIVITY_SCRIPT_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getPreactivityScripts()))
				strAudit.append("PREACTIVITY_SCRIPTS"+auditDelimiterColVal+vObject.getPreactivityScripts().trim());
			else
				strAudit.append("PREACTIVITY_SCRIPTS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getFrequencyProcessAt()))
				strAudit.append("FREQUENCY_PROCESS_AT"+auditDelimiterColVal+vObject.getFrequencyProcessAt());
			else
				strAudit.append("FREQUENCY_PROCESS_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getFrequencyProcess()))
				strAudit.append("FREQUENCY_PROCESS"+auditDelimiterColVal+vObject.getFrequencyProcess().trim());
			else
				strAudit.append("FREQUENCY_PROCESS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getStartTime()))
				strAudit.append("START_TIME"+auditDelimiterColVal+vObject.getStartTime().trim());
			else
				strAudit.append("START_TIME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getEndTime()))
				strAudit.append("END_TIME"+auditDelimiterColVal+vObject.getEndTime().trim());
			else
				strAudit.append("END_TIME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getProcessSequence()))
				strAudit.append("PROCESS_SEQUENCE"+auditDelimiterColVal+vObject.getProcessSequence().trim());
			else
				strAudit.append("PROCESS_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAccessPermission()))
				strAudit.append("ACCESS_PERMISSION"+auditDelimiterColVal+vObject.getAccessPermission().trim());
			else
				strAudit.append("ACCESS_PERMISSION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquisitionReadinessFlag()))
				strAudit.append("ACQUISITION_READINESS_FLAG"+auditDelimiterColVal+vObject.getAcquisitionReadinessFlag().trim());
			else
				strAudit.append("ACQUISITION_READINESS_FLAG"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getRecordsFetchedCount()))
				strAudit.append("RECORDS_FETCHED_COUNT"+auditDelimiterColVal+vObject.getRecordsFetchedCount().trim());
			else
				strAudit.append("RECORDS_FETCHED_COUNT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getDateLastExtraction()))
				strAudit.append("DATE_LAST_EXTRACTION"+auditDelimiterColVal+vObject.getDateLastExtraction().trim());
			else
				strAudit.append("DATE_LAST_EXTRACTION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getDebugMode()))
				strAudit.append("DEBUG_MODE"+auditDelimiterColVal+vObject.getDebugMode().trim());
			else
				strAudit.append("DEBUG_MODE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquisitionStatusAt()))
				strAudit.append("ACQUISITION_STATUS_AT"+auditDelimiterColVal+vObject.getAcquisitionStatusAt());
			else
				strAudit.append("ACQUISITION_STATUS_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquisitionStatus()))
				strAudit.append("ACQUISITION_STATUS"+auditDelimiterColVal+vObject.getAcquisitionStatus().trim());
			else
				strAudit.append("ACQUISITION_STATUS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getIntegrityScriptName()))
				strAudit.append("INTEGRITY_SCRIPT_NAME"+auditDelimiterColVal+vObject.getIntegrityScriptName().trim());
			else
				strAudit.append("INTEGRITY_SCRIPT_NAME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			
			if(ValidationUtil.isValid(vObject.getAlert1Timeslot()))
				strAudit.append("ALERT1_TIMESLOT"+auditDelimiterColVal+vObject.getAlert1Timeslot().trim());
			else
				strAudit.append("ALERT1_TIMESLOT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAlert1EmailIds()))
				strAudit.append("ALERT1_EMAIL_IDS"+auditDelimiterColVal+vObject.getAlert1EmailIds().trim());
			else
				strAudit.append("ALERT1_EMAIL_IDS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAlert1MobileNo()))
				strAudit.append("ALERT1_MOBILE_NO"+auditDelimiterColVal+vObject.getAlert1MobileNo().trim());
			else
				strAudit.append("ALERT1_MOBILE_NO"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAlert2Timeslot()))
				strAudit.append("ALERT2_TIMESLOT"+auditDelimiterColVal+vObject.getAlert2Timeslot().trim());
			else
				strAudit.append("ALERT2_TIMESLOT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAlert2EmailIds()))
				strAudit.append("ALERT2_EMAIL_IDS"+auditDelimiterColVal+vObject.getAlert2EmailIds().trim());
			else
				strAudit.append("ALERT2_EMAIL_IDS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAlert2MobileNo()))
				strAudit.append("ALERT2_MOBILE_NO"+auditDelimiterColVal+vObject.getAlert2MobileNo().trim());
			else
				strAudit.append("ALERT2_MOBILE_NO"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAlert3Timeslot()))
				strAudit.append("ALERT3_TIMESLOT"+auditDelimiterColVal+vObject.getAlert3Timeslot().trim());
			else
				strAudit.append("ALERT3_TIMESLOT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAlert3EmailIds()))
				strAudit.append("ALERT3_EMAIL_IDS"+auditDelimiterColVal+vObject.getAlert3EmailIds().trim());
			else
				strAudit.append("ALERT3_EMAIL_IDS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAlert3MobileNo()))
				strAudit.append("ALERT3_MOBILE_NO"+auditDelimiterColVal+vObject.getAlert3MobileNo().trim());
			else
				strAudit.append("ALERT3_MOBILE_NO"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAdfSuccessfulEmailIds()))
				strAudit.append("ADF_SUCCESSFUL_EMAIL_IDS"+auditDelimiterColVal+vObject.getAdfSuccessfulEmailIds().trim());
			else
				strAudit.append("ADF_SUCCESSFUL_EMAIL_IDS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAdfFailedEmailIds()))
				strAudit.append("ADF_FAILED_EMAIL_IDS"+auditDelimiterColVal+vObject.getAdfFailedEmailIds().trim());
			else
				strAudit.append("ADF_FAILED_EMAIL_IDS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAutoauthTime()))
				strAudit.append("AUTOAUTH_TIME"+auditDelimiterColVal+vObject.getAutoauthTime().trim());
			else
				strAudit.append("AUTOAUTH_TIME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAcquProcesscontrolStatus()))
				strAudit.append("ACQU_PROCESSCONTROL_STATUS"+auditDelimiterColVal+vObject.getAcquProcesscontrolStatus().trim());
			else
				strAudit.append("ACQU_PROCESSCONTROL_STATUS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getNodeRequest()))
				strAudit.append("NODE_REQUEST"+auditDelimiterColVal+vObject.getNodeRequest().trim());
			else
				strAudit.append("NODE_REQUEST"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getNodeOverride()))
				strAudit.append("NODE_OVERRIDE"+auditDelimiterColVal+vObject.getNodeOverride().trim());
			else
				strAudit.append("NODE_OVERRIDE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getNodeRequestTime()))
				strAudit.append("NODE_REQUEST_TIME"+auditDelimiterColVal+vObject.getNodeRequestTime().trim());
			else
				strAudit.append("NODE_REQUEST_TIME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getNodeOverrideTime()))
				strAudit.append("NODE_OVERRIDE_TIME"+auditDelimiterColVal+vObject.getNodeOverrideTime().trim());
			else
				strAudit.append("NODE_OVERRIDE_TIME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getIntegrityScriptTypeAt()))
				strAudit.append("INTEGRITY_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getIntegrityScriptTypeAt());
			else
				strAudit.append("INTEGRITY_SCRIPT_TYPE_AT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getIntegrityScriptType()))
				strAudit.append("INTEGRITY_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getIntegrityScriptType().trim());
			else
				strAudit.append("INTEGRITY_SCRIPT_TYPE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getAdfNumber()))
				strAudit.append("ADF_NUMBER"+auditDelimiterColVal+vObject.getAdfNumber());
			else
				strAudit.append("ADF_NUMBER"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getSubAdfNumber()))
				strAudit.append("SUB_ADF_NUMBER"+auditDelimiterColVal+vObject.getSubAdfNumber());
			else
				strAudit.append("SUB_ADF_NUMBER"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getCbEmailIds()))
				strAudit.append("CB_EMAIL_IDS"+auditDelimiterColVal+vObject.getCbEmailIds().trim());
			else
				strAudit.append("CB_EMAIL_IDS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(vObject.getExpandFlag() != null)
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getExpandFlag().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
			strAudit.append(auditDelimiter);
			if(ValidationUtil.isValid(vObject.getDateLastModified()))
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getDateCreation()))
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	protected ExceptionCode updateBuildControls(AdfSchedulesDetailsVb adfSchedulesDetailsVb, String updateAllAvailableTemplateStatus) {
		final int intKeyFieldsCount = 1;
		/*String query = new String("UPDATE ADF_DATA_ACQUISITION T1 set T1.TEMPLATE_CONTROL_STATUS='C'  WHERE T1.TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
			"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK "+
			"AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME AND T2.ACQUISITION_STATUS NOT IN ('YTP','COMP'))");*/
		String query = new String("UPDATE ADF_DATA_ACQUISITION set TEMPLATE_CONTROL_STATUS='C'  WHERE TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
				"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND COUNTRY = T2.COUNTRY AND LE_BOOK = T2.LE_BOOK "+
				"AND TEMPLATE_NAME = T2.TEMPLATE_NAME )");
		if(ValidationUtil.isValid(updateAllAvailableTemplateStatus) && "true".equalsIgnoreCase(updateAllAvailableTemplateStatus)){
			query = new String("UPDATE ADF_DATA_ACQUISITION  set TEMPLATE_CONTROL_STATUS='C'  WHERE TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
					"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND COUNTRY = T2.COUNTRY AND LE_BOOK = T2.LE_BOOK )");
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = new String(adfSchedulesDetailsVb.getAdfNumber());
		try
		{
			getJdbcTemplate().update(query,params);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){	
			ex.printStackTrace();
			strErrorDesc = ex.getMessage();
			ExceptionCode eceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			return eceptionCode;
		}
	}
	protected ExceptionCode updateModifyBuildControls(AdfSchedulesDetailsVb adfSchedulesDetailsVb) {
		final int intKeyFieldsCount = 2;
		/*String query = new String("UPDATE ADF_DATA_ACQUISITION T1 set T1.TEMPLATE_CONTROL_STATUS='C'  WHERE T1.TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
			"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK "+
			"AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME AND T2.ACQUISITION_STATUS NOT IN ('YTP','COMP'))");*/
		String query = new String("UPDATE ADF_DATA_ACQUISITION set TEMPLATE_CONTROL_STATUS='C'  WHERE TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
				"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND COUNTRY = T2.COUNTRY AND LE_BOOK = T2.LE_BOOK AND TEMPLATE_NAME =T2.TEMPLATE_NAME "+
				"AND T2.TEMPLATE_NAME = ? )");
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = new String(adfSchedulesDetailsVb.getAdfNumber());
		params[1] = new String(adfSchedulesDetailsVb.getTemplateName());
		try
		{
			getJdbcTemplate().update(query,params);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage();
			ExceptionCode eceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			return eceptionCode;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	protected ExceptionCode updateAddBuildControls(AdfSchedulesDetailsVb adfSchedulesDetailsVb) {
		final int intKeyFieldsCount = 2;
		/*String query = new String("UPDATE ADF_DATA_ACQUISITION T1 set T1.TEMPLATE_CONTROL_STATUS='C'  WHERE T1.TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
			"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK "+
			"AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME AND T2.ACQUISITION_STATUS NOT IN ('YTP','COMP'))");*/
		String query = new String("UPDATE ADF_DATA_ACQUISITION  set TEMPLATE_CONTROL_STATUS='C'  WHERE TEMPLATE_CONTROL_STATUS NOT IN ('I', 'W', 'M') "+
				"AND EXISTS (SELECT 'X' FROM ADF_PROCESS_CONTROL T2 Where T2.ADF_NUMBER=? AND COUNTRY = T2.COUNTRY AND LE_BOOK = T2.LE_BOOK  AND TEMPLATE_NAME =T2.TEMPLATE_NAME "+
				"AND T2.TEMPLATE_NAME = ? )");
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = new String(adfSchedulesDetailsVb.getAdfNumber());
		params[1] = new String(adfSchedulesDetailsVb.getTemplateName());
		try
		{
			getJdbcTemplate().update(query,params);
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage();
			ExceptionCode eceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			return eceptionCode;
		}
	}
	public int doUpdateApprForAdd(AdfControlsVb vObject){
		String templateName=vObject.getTemplateName();
		if(vObject.getTemplateName().contains(" - ")){
			String templateNameArr[] = vObject.getTemplateName().split(" - ");
			templateName=templateNameArr[0];
		}
		String query = ("Update ADF_PROCESS_CONTROL Set  DATE_LAST_EXTRACTION = "+dateTimeConvert+", ACQUISITION_STATUS= ?, DEBUG_MODE= ?,"+  
                       " records_fetched_count = Case When ? like 'INT%' OR ? like 'UPL%' Then  records_fetched_count Else 0 End , "+
                       " ADF_SUCCESSFUL_EMAIL_FLAG = Case When  ADF_SUCCESSFUL_EMAIL_FLAG like '%I%' Then  ADF_SUCCESSFUL_EMAIL_FLAG Else 'NN' End, "+
                       " ADF_FAILED_EMAIL_FLAG = Case When  ADF_FAILED_EMAIL_FLAG like '%I%' Then  ADF_FAILED_EMAIL_FLAG Else 'NN' End, "+
                       " Next_Process_Time = Case When  Next_Process_Time >  End_Time Then  End_Time Else  Next_Process_Time End, "+
                       " EXT_ITERATION_COUNT = Case When  EXT_ITERATION_COUNT > (select VV.Return_Value_Int from Vision_Variables_Narrow VV Where VV.Variable = 'MAX_ITERATION_COUNT') "+
                       "Then (select VV.Return_Value_Int from Vision_Variables_Narrow VV Where VV.Variable = 'MAX_ITERATION_COUNT') "+
                       "Else  EXT_ITERATION_COUNT End "+
                       "WHERE  COUNTRY = ? AND  LE_BOOK = ? AND  BUSINESS_DATE = "+dateTimeConvert+" AND  TEMPLATE_NAME=? AND  FREQUENCY_PROCESS= ? ");
		
		Object[] args = {vObject.getDateLastExtraction(), vObject.getAcquisitionStatus(), vObject.getDebugMode(), vObject.getAcquisitionStatus(),vObject.getAcquisitionStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getBusinessDate(), templateName, vObject.getFrequencyProcess()};
		return getJdbcTemplate().update(query, args);
	}
	
	
	public long getRecordCount(AdfControlsVb vObject){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String templateName=vObject.getTemplateName();
		if(vObject.getTemplateName().contains(" - ")){
			String templateNameArr[] = vObject.getTemplateName().split(" - ");
			templateName=templateNameArr[0];
		}
		String sql="SELECT COUNT(1) FROM ADF_PROCESS_CONTROL WHERE COUNTRY= ?  AND LE_BOOK= ? "
				+ "AND BUSINESS_DATE="+dateConvert+" AND FREQUENCY_PROCESS=? AND TEMPLATE_NAME=? ";
		Object [] params = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessDate(),vObject.getFrequencyProcess(),templateName};
		long count=getJdbcTemplate().queryForObject(sql,params, long.class);
		return count;
	}
	public int doInsertionAppr(AdfControlsVb vObject){
//		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		if(vObject.getAdfSuccessfulEmailIds()==null){
			vObject.setAdfSuccessfulEmailIds("");
		}
		if(vObject.getAlert1Timeslot()==null){
			vObject.setAlert1Timeslot("");
		}
		if(vObject.getAlert1MobileNo()==null){
			vObject.setAlert1MobileNo("");
		}
		if(vObject.getAlert1EmailIds()==null){
			vObject.setAlert1EmailIds("");
		}
		if(vObject.getAlert2Timeslot()==null){
			vObject.setAlert2Timeslot("");
		}
		if(vObject.getAlert2MobileNo()==null){
			vObject.setAlert2MobileNo("");
		}
		if(vObject.getAlert2EmailIds()==null){
			vObject.setAlert2EmailIds("");
		}
		if(vObject.getAlert3Timeslot()==null){
			vObject.setAlert3Timeslot("");
		}
		if(vObject.getAlert3MobileNo()==null){
			vObject.setAlert3MobileNo("");
		}
		if(vObject.getAlert3EmailIds()==null){
			vObject.setAlert3EmailIds("");
		}
		if(vObject.getDatabaseConnectivityDetails()==null){
			vObject.setDatabaseConnectivityDetails("");
		}
		if(vObject.getIntegrityScriptName()==null){
			vObject.setIntegrityScriptName("");
		}
		if(vObject.getSourceServerScripts()==null){
			vObject.setSourceServerScripts("");
		}
		String AdfStartDay=vObject.getStartTime();
		String AdfEndDay=vObject.getEndTime();
		String AdfNextDay=vObject.getNextProcessTime();
		String LeBookVal = removeDescLeBook(vObject.getLeBook());
		String defYes="Y";
		String defNo="NN";
		String eodFlag="N";
		int result = 0;
		String modifiedNode=vObject.getNode();                                                                                    
		if(vObject.getNode().length()>2){                                                                      
			modifiedNode = vObject.getNode().substring(vObject.getNode().length()-2, vObject.getNode().length());
		}
		vObject.setRecordIndicator(0);
		String templateName=vObject.getTemplateName();
		if(vObject.getTemplateName().contains(" - ")){
			String templateNameArr[] = vObject.getTemplateName().split(" - ");
			templateName=templateNameArr[0];
		}
		try{
			
		String query = "Insert Into ADF_PROCESS_CONTROL  (COUNTRY , LE_BOOK  ,BUSINESS_DATE ,TEMPLATE_NAME, NEXT_PROCESS_TIME ,ACQUISITION_PROCESS_TYPE_AT ,ACQUISITION_PROCESS_TYPE, " +      
			  "CONNECTIVITY_TYPE_AT ,CONNECTIVITY_TYPE, CONNECTIVITY_DETAILS,DATABASE_TYPE_AT,DATABASE_TYPE, DATABASE_CONNECTIVITY_DETAILS,  " +
			  "SERVER_FOLDER_DETAILS ,SOURCE_SCRIPT_TYPE_AT,SOURCE_SCRIPT_TYPE, FREQUENCY_PROCESS_AT , FREQUENCY_PROCESS , START_TIME, " +                    
			  "END_TIME,PROCESS_SEQUENCE , ACCESS_PERMISSION,  ACQUISITION_READINESS_FLAG, RECORDS_FETCHED_COUNT,ACQUISITION_STATUS_AT, " +         
			  "ACQUISITION_STATUS,ALERT1_TIMESLOT , ALERT1_EMAIL_IDS , ALERT1_MOBILE_NO , ALERT2_TIMESLOT , ALERT2_EMAIL_IDS ,ALERT2_MOBILE_NO, " +
			  "ALERT3_TIMESLOT, ALERT3_EMAIL_IDS,ALERT3_MOBILE_NO ,ADF_SUCCESSFUL_EMAIL_IDS,  ADF_FAILED_EMAIL_IDS,AUTOAUTH_TIME,TARGET_SCRIPT_TYPE_AT, " +         
			  "TARGET_SCRIPT_TYPE ,READINESS_SCRIPTS_TYPE_AT, READINESS_SCRIPTS_TYPE ,EOD_INITIATED_FLAG , EXCEL_TEMPLATE_ID,FILE_PATTERN , " +               
			  "ACQU_PROCESSCONTROL_STATUS ,PREACTIVITY_SCRIPT_TYPE_AT, PREACTIVITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_TYPE_AT, " +      
			  "INTEGRITY_SCRIPT_TYPE, RECORD_INDICATOR_NT ,RECORD_INDICATOR , MAKER ,  VERIFIER, INTERNAL_STATUS,DATE_LAST_MODIFIED , DATE_CREATION , " +                
			  "MANDATORY_FLAG ,NODE_REQUEST,NODE_REQUEST_TIME , EXCEL_FILE_PATTERN," +           
			 "DATE_LAST_EXTRACTION,  DEBUG_MODE, ALERT1_SEND_FLAG, ALERT2_SEND_FLAG, ALERT3_SEND_FLAG ,ADF_FAILED_EMAIL_FLAG  ,ADF_SUCCESSFUL_EMAIL_FLAG, ADF_NUMBER, SUB_ADF_NUMBER, CB_EMAIL_IDS, " +          
			 "SOURCE_SERVER_SCRIPTS, TARGET_SERVER_SCRIPTS,ACQUISITION_READINESS_SCRIPTS, PREACTIVITY_SCRIPTS,INTEGRITY_SCRIPT_NAME ) " ;
				
		String values =  "   Values (?, ?, "
			    +"   TO_DATE(?, 'DD/MM/YYYY'),  "
			    +"   ?, "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   ?, ?, ?, ?, "
			    +"   '"+vObject.getConnectivityDetails().replaceAll("'", "''")+"', "
			    +"   ?, ?, "
			    +"   '"+vObject.getDatabaseConnectivityDetails().replaceAll("'", "''")+"', "
			    +"   ?, ?, ?,  "
			    +"   ?, ?, "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   ?, ?, ?, ?, ?, ?,  "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   '"+vObject.getAlert1EmailIds()+"', "
			    +"   ?, "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   '"+vObject.getAlert2EmailIds()+"', "
			    +"   ?, "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   '"+vObject.getAlert3EmailIds()+"', "
			    +"   ?, '"+vObject.getAdfSuccessfulEmailIds()+"', "
			    +"   '"+vObject.getAdfFailedEmailIds()+"', "
			    +"   TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), "
			    +"   ?, ?, ?, ?, ?, ?, "
			    +"   REPLACE(REPLACE(REPLACE(?,'#BUSINESS_DATE#',TO_DATE(?, 'DD-MM-RRRR')),'#FEED_DATE#',TO_DATE(?, 'DD-MM-RRRR')),'#BUSINESS_DATE_YYYYMMDD#',TO_CHAR(TO_DATE(?, 'DD-MM-RRRR'), 'YYYYMMDD')), "
			    +"   ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
			    +"   SYSDATE, SYSDATE ,"
			    +"   ? ,? , SYSDATE, "
			    +"   REPLACE(REPLACE(REPLACE(?,'#BUSINESS_DATE#',TO_DATE(?, 'DD-MM-RRRR')),'#FEED_DATE#',TO_DATE(?, 'DD-MM-RRRR')),'#BUSINESS_DATE_YYYYMMDD#',TO_CHAR(TO_DATE(?, 'DD-MM-RRRR'), 'YYYYMMDD')), "
			    +"   TO_DATE(?, 'DD-MM-RRRR'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		if("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			values =	"	Values (?, ?, convert(date, ?, 103),  ?, convert(datetime, ?, 103), ?, ?, ?, ?, '"+vObject.getConnectivityDetails().replaceAll("'", "''")+"', ?, ?, " +
					"	'"+vObject.getDatabaseConnectivityDetails().replaceAll("'", "''")+"', ?, ?, ?, " +
					"	 ?, ?, convert(datetime, ?, 103), convert(datetime, ?, 103), ?, ?, ?, ?, ?, ?, " +
					"	convert(datetime, ?, 103), '"+vObject.getAlert1EmailIds()+"', ?, convert(datetime, ?, 103), '"+vObject.getAlert2EmailIds()+"', ?, " +
					"	convert(datetime, ?, 103), '"+vObject.getAlert3EmailIds()+"', ?, '"+vObject.getAdfSuccessfulEmailIds()+"', " +
					"	'"+vObject.getAdfFailedEmailIds()+"', convert(datetime, ?, 103), ?, ?, ?, ?, ?, ?, "
							+ "REPLACE(REPLACE(REPLACE(?,'#BUSINESS_DATE#',convert(date, ?, 103)),'#FEED_DATE#',convert(date, ?, 103)),'#BUSINESS_DATE_YYYYMMDD#',format(convert(date,convert(date, ?, 103),111),'yyyyMMdd')), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,getDate(), getDate() ,"+
					" ? ,? , getDate(), REPLACE(REPLACE(REPLACE(?,'#BUSINESS_DATE#',convert(date, ?, 103)),'#FEED_DATE#',convert(date, ?, 103)),'#BUSINESS_DATE_YYYYMMDD#',format(convert(date,convert(date, ?, 103),111),'yyyyMMdd')), convert(date, ?, 103), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		}
		
		Object[] args = {vObject.getCountry(), LeBookVal, vObject.getBusinessDate(), templateName,  AdfNextDay, 
				vObject.getAcquisitionProcessTypeAt(), vObject.getAcquisitionProcessType(), vObject.getConnectivityTypeAt(), vObject.getConnectivityType(), 
				vObject.getDatabaseTypeAt(), vObject.getDatabaseType(), vObject.getServerFolderDetails(), vObject.getSourceScriptTypeAt(), 
				vObject.getSourceScriptType(), vObject.getFrequencyProcessAt(), vObject.getFrequencyProcess(), AdfStartDay, AdfEndDay, 
				vObject.getProcessSequence(), vObject.getAccessPermission(), vObject.getAcquisitionReadinessFlag(), vObject.getRecordsFetchedCount(), 
				vObject.getAcquisitionStatusAt(), vObject.getAcquisitionStatus(),  vObject.getAlert1Timeslot(), 
				vObject.getAlert1MobileNo(), vObject.getAlert2Timeslot(), vObject.getAlert2MobileNo(), vObject.getAlert3Timeslot(), 
				vObject.getAlert3MobileNo(),
				vObject.getAutoauthTime(), vObject.getTargetScriptTypeAt(), vObject.getTargetScriptType(), vObject.getReadinessScriptsTypeAt(), 
				vObject.getReadinessScriptsType(),eodFlag,  vObject.getExcelTemplateId(), 
				vObject.getFilePattern(),vObject.getBusinessDate(),vObject.getFeedDate(),vObject.getBusinessDate(), vObject.getAcquProcesscontrolStatus(), vObject.getPreactivityScriptTypeAt(), 
				vObject.getPreactivityScriptType(), vObject.getIntegrityScriptTypeAt(),vObject.getIntegrityScriptType(), 
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), 
				vObject.getInternalStatus(), 
				defYes,modifiedNode,
				vObject.getExcelFilePattern(),vObject.getBusinessDate(),vObject.getFeedDate(),vObject.getBusinessDate(),vObject.getDateLastExtraction(),vObject.getDebugMode(),defNo,
				defNo,defNo,defNo
				,defNo,vObject.getAdfNumber(),vObject.getSubAdfNumber(),vObject.getCbEmailIds()};
		query = query + values;
		PreparedStatement ps= getConnection().prepareStatement(query);
		for(int i=1;i<=args.length;i++){
			ps.setObject(i,args[i-1]);	
		}
		String sourceServerScript = vObject.getSourceServerScripts();
		try{
			if(!ValidationUtil.isValid(sourceServerScript))
				sourceServerScript="";
		}catch(Exception e){
			e.printStackTrace();
			sourceServerScript="";
		}
		Reader reader = new StringReader(sourceServerScript);
		ps.setCharacterStream(args.length+1, new StringReader(sourceServerScript), sourceServerScript.length());
		String targetServerScript = vObject.getTargetServerScripts();
		try{
			if(!ValidationUtil.isValid(targetServerScript))
				targetServerScript="";
		}catch(Exception e){
			e.printStackTrace();
			targetServerScript="";			
		}
		reader = new StringReader(targetServerScript);
		ps.setCharacterStream(args.length+2, reader, targetServerScript.length());
		String acquisitionReadinessScripts = vObject.getAcquisitionReadinessScripts();
		try{
			if(!ValidationUtil.isValid(acquisitionReadinessScripts))
				acquisitionReadinessScripts="";
		}catch(Exception e){
			e.printStackTrace();
			acquisitionReadinessScripts="";			
		}
		reader = new StringReader(acquisitionReadinessScripts);
		ps.setCharacterStream(args.length+3, reader, acquisitionReadinessScripts.length());
		String preactivityScripts = vObject.getPreactivityScripts();
		try{
			if(!ValidationUtil.isValid(preactivityScripts))
				preactivityScripts="";
		}catch(Exception e){
			e.printStackTrace();
			preactivityScripts="";			
		}
		reader = new StringReader(preactivityScripts);
		ps.setCharacterStream(args.length+4, reader, preactivityScripts.length());
		String integrityscriptname = vObject.getIntegrityScriptName();
		try{
			if(!ValidationUtil.isValid(integrityscriptname))
				integrityscriptname="";
		}catch(Exception e){
			e.printStackTrace();
			integrityscriptname="";			
		}
		reader = new StringReader(integrityscriptname);
		ps.setCharacterStream(args.length+5, reader, integrityscriptname.length());
		result = ps.executeUpdate();
		}catch (Exception e) {
			retVal = Constants.ERRONEOUS_OPERATION;
			exceptionCode = getResultObject(retVal);
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
			throw buildRuntimeCustomException(exceptionCode);
		}
	return result;
	}
	public List<AdfControlsVb> getMajorBuildList(){
		String query = new String("Select ");
		Object params[] = null;
		try
		{
			return  getJdbcTemplate().query(query, params, getQueryResultsMapperBuildList());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			return null;
		}
	}
	protected RowMapper getQueryResultsMapperBuildList(){
		
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AdfControlsVb adfSchedulesDetailsVb = new AdfControlsVb();
				adfSchedulesDetailsVb.setAcquisitionStatus(rs.getString("MAJOR_BUILD"));//
				return adfSchedulesDetailsVb;
			}
		};
		return mapper;
	}
	protected int doDeleteUploadFiling(AdfSchedulesDetailsVb vObject) throws SQLException{
		String query = "Delete From ADF_UPLOAD_FILING   Where Exists (Select 1 From ADF_PROCESS_CONTROL T2 "+
                       "Where T2.Country = Country And T2.Le_Book = Le_Book And T2.Business_Date = Business_Date "+
                       "And T2.Template_Name = Template_Name And T2.Adf_Number = ?)";
		Object[] args = {vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doDeleteAdfAlertEmail(AdfSchedulesDetailsVb vObject)throws SQLException{
		String query =  "Delete From ADF_ALERT_EMAIL  Where Exists  (Select 1 From ADF_PROCESS_CONTROL T2 "+
				        "Where T2.Country = Country  And T2.Le_Book = Le_Book "+
				        "And T2.Business_Date = Business_Date And T2.Template_Name = Template_Name And T2.Adf_Number = ?)";
		Object[] args = {vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doDeleteAdfAlertSms(AdfSchedulesDetailsVb vObject)throws SQLException{
		String query =  "Delete From ADF_ALERT_SMS Where Exists (Select 1 From ADF_PROCESS_CONTROL T2 Where T2.Country = Country And T2.Le_Book = Le_Book "+
			     "And T2.Business_Date = Business_Date And T2.Template_Name = Template_Name And T2.Adf_Number = ?)";
		Object[] args = {vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
}
