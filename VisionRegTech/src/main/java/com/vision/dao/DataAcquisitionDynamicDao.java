package com.vision.dao;

import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.Paginationhelper;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.DataAcquisitionDynamicVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;
@Component
public class DataAcquisitionDynamicDao extends AbstractDao<DataAcquisitionDynamicVb> {
	private final String CBD="CBD";
	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String statusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.DATA_ACQU_STATUS",
			"DATA_ACQU_STATUS_DESC");
	String statusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.DATA_ACQU_STATUS",
			"DATA_ACQU_STATUS_DESC");
	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setCountry(rs.getString("COUNTRY"));
				dataAcqVb.setLeBook(rs.getString("LE_BOOK"));
				dataAcqVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				dataAcqVb.setTemplateDesc(rs.getString("TEMPLATE_DESCRIPTION"));
				if (ValidationUtil.isValid(rs.getString("FEED_STG_NAME")))
					dataAcqVb.setFeedStgName(rs.getString("FEED_STG_NAME"));
				dataAcqVb.setFilePattern(rs.getString("FILE_PATTERN"));
				if (ValidationUtil.isValid(rs.getString("EXCEL_FILE_PATTERN")))
					dataAcqVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				if (ValidationUtil.isValid(rs.getString("EXCEL_TEMPLATE_ID")))
					dataAcqVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				else
					dataAcqVb.setExcelTemplateId("");
				
				dataAcqVb.setAcquisitionProcessTypeAt(rs.getInt("ACQUISITION_PROCESS_TYPE_AT"));
				if (ValidationUtil.isValid(rs.getString("ACQUISITION_PROCESS_TYPE")))
					dataAcqVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
				
				dataAcqVb.setConnectivityTypeAt(rs.getInt("CONNECTIVITY_TYPE_AT"));
				
				if (ValidationUtil.isValid(rs.getString("CONNECTIVITY_TYPE")))
					dataAcqVb.setConnectivityType(rs.getString("CONNECTIVITY_TYPE"));
				if (ValidationUtil.isValid(rs.getString("CONNECTIVITY_DETAILS")))
					dataAcqVb.setConnectivityDetails(rs.getString("CONNECTIVITY_DETAILS"));
				
				dataAcqVb.setDatabaseTypeAt(rs.getInt("DATABASE_TYPE_AT"));
				
				if (ValidationUtil.isValid(rs.getString("DATABASE_TYPE")))
					dataAcqVb.setDatabaseType(rs.getString("DATABASE_TYPE"));
				
				if (ValidationUtil.isValid(rs.getString("DATABASE_CONNECTIVITY_DETAILS")))
					dataAcqVb.setDatabaseConnectivityDetails(rs.getString("DATABASE_CONNECTIVITY_DETAILS"));

				if (ValidationUtil.isValid(rs.getString("SERVER_FOLDER_DETAILS")))
					dataAcqVb.setServerFolderDetails(rs.getString("SERVER_FOLDER_DETAILS"));
				else
					dataAcqVb.setServerFolderDetails("");
				
				dataAcqVb.setSourceScriptTypeAt(rs.getInt("SOURCE_SCRIPT_TYPE_AT"));

				if (ValidationUtil.isValid(rs.getString("SOURCE_SCRIPT_TYPE")))
					dataAcqVb.setSourceScriptType(rs.getString("SOURCE_SCRIPT_TYPE"));
				
				if (ValidationUtil.isValid(rs.getString("SOURCE_SERVER_SCRIPTS")))
					dataAcqVb.setSourceServerScript(
							rs.getString("SOURCE_SERVER_SCRIPTS").replace("\n", " ").replace("\r", " "));
				else
					dataAcqVb.setSourceServerScript(" ");
				
				dataAcqVb.setTargetScriptTypeAt(rs.getInt("TARGET_SCRIPT_TYPE_AT"));
				
				if(ValidationUtil.isValid(rs.getString("TARGET_SCRIPT_TYPE")))				
					dataAcqVb.setTargetScriptType(rs.getString("TARGET_SCRIPT_TYPE"));
				
				if (ValidationUtil.isValid(rs.getString("TARGET_SERVER_SCRIPTS")))
					dataAcqVb.setTargetServerScript(
							rs.getString("TARGET_SERVER_SCRIPTS").replace("\n", " ").replace("\r", " "));
				else
					dataAcqVb.setTargetServerScript(" ");
				
				dataAcqVb.setReadinessScriptTypeAt(rs.getInt("READINESS_SCRIPTS_TYPE_AT"));
				
				if(ValidationUtil.isValid(rs.getString("READINESS_SCRIPTS_TYPE")))
					dataAcqVb.setReadinessScriptType(rs.getString("READINESS_SCRIPTS_TYPE"));
				
				if (ValidationUtil.isValid(rs.getString("ACQUISITION_READINESS_SCRIPTS")))
					dataAcqVb.setAcquisitionReadinessScripts(
							rs.getString("ACQUISITION_READINESS_SCRIPTS").replace("\n", " ").replace("\r", " "));
				else
					dataAcqVb.setAcquisitionReadinessScripts(" ");
				
				dataAcqVb.setPreactivityScriptTypeAt(rs.getInt("PREACTIVITY_SCRIPT_TYPE_AT"));
				
				if(ValidationUtil.isValid(rs.getString("PREACTIVITY_SCRIPT_TYPE")))
					dataAcqVb.setPreactivityScriptType(rs.getString("PREACTIVITY_SCRIPT_TYPE"));
				
				if (ValidationUtil.isValid(rs.getString("PREACTIVITY_SCRIPTS")))
					dataAcqVb.setPreactivityScripts(
							rs.getString("PREACTIVITY_SCRIPTS").replace("\n", " ").replace("\r", " "));
				else
					dataAcqVb.setPreactivityScripts(" ");
				
				if (ValidationUtil.isValid(rs.getString("INTEGRITY_SCRIPT_NAME")))
					dataAcqVb.setIntegrityScriptName(rs.getString("INTEGRITY_SCRIPT_NAME"));
				else
					dataAcqVb.setIntegrityScriptName("");
				
				dataAcqVb.setFrequencyProcessAt(rs.getInt("FREQUENCY_PROCESS_AT"));
				
				if(ValidationUtil.isValid(rs.getString("FREQUENCY_PROCESS")))
					dataAcqVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
				
				if(ValidationUtil.isValid(rs.getString("PROCESS_SEQUENCE")))
					dataAcqVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				
				if (ValidationUtil.isValid(rs.getString("ADF_START_TIME"))) {
					dataAcqVb.setAdfStartTime(rs.getString("ADF_START_TIME"));
				}
				if (ValidationUtil.isValid(rs.getString("ADF_END_TIME"))) {
					dataAcqVb.setAdfEndTime(rs.getString("ADF_END_TIME"));
				}
				if(ValidationUtil.isValid(rs.getString("ACCESS_PERMISSION")))
					dataAcqVb.setAccessPermission(rs.getString("ACCESS_PERMISSION"));

				if(ValidationUtil.isValid(rs.getString("DATE_LAST_EXTRACTION")))
					dataAcqVb.setDateLastExtraction(rs.getString("DATE_LAST_EXTRACTION"));
				
				if(ValidationUtil.isValid(rs.getString("DEBUG_MODE")))
					dataAcqVb.setDebugMode(rs.getString("DEBUG_MODE"));
				
				dataAcqVb.setDataAcqStatusNt(rs.getInt("DATA_ACQU_STATUS_NT"));
				dataAcqVb.setDataAcqStatus(rs.getInt("DATA_ACQU_STATUS"));
				dataAcqVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				dataAcqVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				dataAcqVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				dataAcqVb.setMaker(rs.getLong("MAKER"));
				dataAcqVb.setVerifier(rs.getLong("VERIFIER"));
				dataAcqVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				dataAcqVb.setDateCreation(rs.getString("DATE_CREATION"));
				dataAcqVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				dataAcqVb.setMandatoryFlagAt(rs.getInt("MANDATORY_FLAG_AT"));
				if (rs.getString("MANDATORY_FLAG") != null) {
					dataAcqVb.setMandatoryFlag(rs.getString("MANDATORY_FLAG"));
				} else {
					dataAcqVb.setMandatoryFlag("");
				}
				dataAcqVb.setIntegrityScriptTypeAt(rs.getInt("INTEGRITY_SCRIPT_TYPE_AT"));

				if(ValidationUtil.isValid(rs.getString("INTEGRITY_SCRIPT_TYPE")))
					dataAcqVb.setIntegrityScriptType(rs.getString("INTEGRITY_SCRIPT_TYPE"));
				
				if (ValidationUtil.isValid(dataAcqVb.getAdfStartTime())) {
					String[] AdfTempStartDay = dataAcqVb.getAdfStartTime().split("\\s+");
					String Temp = AdfTempStartDay[0];
					dataAcqVb.setAdfStartTime(AdfTempStartDay[1] + " " + AdfTempStartDay[2] + " " + AdfTempStartDay[3]);
					String[] Temp1 = Temp.split("-");
					if ("0".equalsIgnoreCase(Temp1[0].substring(0, 1))) {
						dataAcqVb.setAdfStartDay(Temp1[0].substring(1, 2));
					} else {
						dataAcqVb.setAdfStartDay(Temp1[0]);
					}
				}
				if (ValidationUtil.isValid(dataAcqVb.getAdfEndTime())) {
					String[] AdfTempEndDay = dataAcqVb.getAdfEndTime().split("\\s+");
					String Temp = AdfTempEndDay[0];
					dataAcqVb.setAdfEndTime(AdfTempEndDay[1] + " " + AdfTempEndDay[2] + " " + AdfTempEndDay[3]);
					String[] Temp2 = Temp.split("-");
					if ("0".equalsIgnoreCase(Temp2[0].substring(0, 1))) {
						dataAcqVb.setAdfEndDay(Temp2[0].substring(1, 2));
					} else {
						dataAcqVb.setAdfEndDay(Temp2[0]);
					}
				}
				dataAcqVb.setTemplateControlStatus(rs.getString("TEMPLATE_CONTROL_STATUS"));
				dataAcqVb.setSubAdfNumber(rs.getString("SUB_ADF_NUMBER"));
				if (rs.getString("MAJOR_BUILD") != null)
					dataAcqVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				dataAcqVb.setRttNextIntervalTypeAt(rs.getInt("RTT_NEXT_INTERVAL_TYPE_AT"));
				if (rs.getString("RTT_NEXT_INTERVAL_TYPE") != null)
					dataAcqVb.setRttNextIntervalType(rs.getString("RTT_NEXT_INTERVAL_TYPE"));
				if (rs.getString("RTT_EVERY_X_MINS") != null)
					dataAcqVb.setRttEveryXMins(rs.getString("RTT_EVERY_X_MINS"));
				if (rs.getString("BUILD_RUN_FLAG") != null)
					dataAcqVb.setBuildRunFlag(rs.getString("BUILD_RUN_FLAG"));
				if (rs.getString("FREQUENCY_PROCESS") != null) {
					dataAcqVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
				} else {
					dataAcqVb.setFrequencyProcess("");
				}

				dataAcqVb.setMakerName(rs.getString("MAKER_NAME"));
				dataAcqVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				return dataAcqVb;
			}
		};
		return mapper;
	}
	
	public RowMapper getQueryPopupMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setCountry(rs.getString("COUNTRY"));
				dataAcqVb.setLeBook(rs.getString("LE_BOOK"));
				return dataAcqVb;
			}
		};
		return mapper;
	}	
	public RowMapper getHashMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setPopDbVarName(rs.getString("VARIABLE_NAME"));
				dataAcqVb.setSourceScriptTypeAt(rs.getInt("SCRIPT_TYPE_AT"));
				dataAcqVb.setDatabaseType(rs.getString("SCRIPT_TYPE"));
				dataAcqVb.setSourceScriptType(rs.getString("VARIABLE_SCRIPT"));
				dataAcqVb.setMaker(rs.getLong("MAKER"));
				dataAcqVb.setDateCreation(rs.getString("DATE_CREATION"));
				dataAcqVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return dataAcqVb;
			}
		};
		return mapper;
	}
	public List<DataAcquisitionDynamicVb> getQueryPopupResults(DataAcquisitionDynamicVb dObj){
		
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		Vector<Object> params = new Vector<Object>();
		//String AdfStartDay=TimeDateModifier(dObj.getAdfStartDay(),dObj.getAdfStartTime());
		//String AdfEndDay=TimeDateModifier(dObj.getAdfEndDay(),dObj.getAdfEndTime());
		
		StringBuffer strBufApprove = new StringBuffer("SELECT distinct  TAppr.COUNTRY,"
				+ " TAppr.LE_BOOK"+getDbFunction("PIPELINE")+"' - '"+getDbFunction("PIPELINE")+"(Select Leb_description from LE_Book where LE_Book.LE_Book = TAppr.LE_Book and LE_Book.Country = TAppr.Country) as LE_BOOK "+
				"FROM ADF_DATA_ACQUISITION TAppr ");
		String strWhereNotExists = new String(" Not Exists (Select 'X' From ADF_DATA_ACQUISITION_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY "
				+ "AND TPend.LE_BOOK = TAppr.LE_BOOK AND TPEND.TEMPLATE_NAME = TAPPR.TEMPLATE_NAME )");
		StringBuffer strBufPending = new StringBuffer("SELECT distinct TPend.COUNTRY, "
				+ "TPend.LE_BOOK"+getDbFunction("PIPELINE")+"' - '"+getDbFunction("PIPELINE")+"(Select Leb_description from LE_Book where LE_Book.LE_Book = TPend.LE_Book and LE_Book.Country = TPend.Country) as LE_BOOK  "+
				"FROM ADF_DATA_ACQUISITION_PEND TPend");
		try
		{
			
			if (ValidationUtil.isValid(dObj.getCountry())){
				params.addElement("%" + dObj.getCountry().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.COUNTRY) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.COUNTRY) LIKE ?", strBufPending);
			}
			
			if (ValidationUtil.isValid(dObj.getLeBook())){
				params.addElement("%" + CalLeBook + "%");
				CommonUtils.addToQuery("UPPER(TAppr.LE_BOOK) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.LE_BOOK) LIKE ?", strBufPending);
			}
			/*if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				CommonUtils.addToQuery("TAppr.COUNTRY||'-'||TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY||'-'||TPend.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufPending);
			}*/
			if (ValidationUtil.isValid(dObj.getTemplateName())){
				params.addElement("%" + dObj.getTemplateName().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.TEMPLATE_NAME) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.TEMPLATE_NAME) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getFilePattern())){
				params.addElement("%" + dObj.getFilePattern().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.FILE_PATTERN) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.FILE_PATTERN) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getExcelFilePattern())){
				params.addElement("%" + dObj.getExcelFilePattern().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.EXCEL_FILE_PATTERN) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.EXCEL_FILE_PATTERN) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getExcelTemplateId())){
				params.addElement("%" + dObj.getExcelTemplateId().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.EXCEL_TEMPLATE_ID) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.EXCEL_TEMPLATE_ID) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getAcquisitionProcessType()) && !"-1".equalsIgnoreCase(dObj.getAcquisitionProcessType())){
				params.addElement(new String(dObj.getAcquisitionProcessType()));
				CommonUtils.addToQuery("TAppr.ACQUISITION_PROCESS_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.ACQUISITION_PROCESS_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getConnectivityType()) && !"-1".equalsIgnoreCase(dObj.getConnectivityType())){
				params.addElement(new String(dObj.getConnectivityType()));
				CommonUtils.addToQuery("TAppr.CONNECTIVITY_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.CONNECTIVITY_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getConnectivityDetails())){
				params.addElement("%" + dObj.getConnectivityDetails().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.CONNECTIVITY_DETAILS) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.CONNECTIVITY_DETAILS) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getDatabaseType()) && !"-1".equalsIgnoreCase(dObj.getDatabaseType())){
				params.addElement(new Integer(dObj.getDatabaseType()));
				CommonUtils.addToQuery("TAppr.DATABASE_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.DATABASE_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getDatabaseConnectivityDetails())){
				params.addElement("%" + dObj.getDatabaseConnectivityDetails().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.DATABASE_CONNECTIVITY_DETAILS) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.DATABASE_CONNECTIVITY_DETAILS) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getServerFolderDetails())){
				params.addElement("%" + dObj.getServerFolderDetails().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.SERVER_FOLDER_DETAILS) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.SERVER_FOLDER_DETAILS) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getIntegrityScriptType()) && !"-1".equalsIgnoreCase(dObj.getIntegrityScriptType())){
				params.addElement(new Integer(dObj.getIntegrityScriptType()));
				CommonUtils.addToQuery("TAppr.INTEGRITY_SCRIPT_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.INTEGRITY_SCRIPT_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getMandatoryFlag()) && !"-1".equalsIgnoreCase(dObj.getMandatoryFlag())){
				params.addElement(new String(dObj.getMandatoryFlag()));
				CommonUtils.addToQuery("TAppr.MANDATORY_FLAG = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.MANDATORY_FLAG = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getSourceScriptType()) && !"-1".equalsIgnoreCase(dObj.getSourceScriptType())){
				params.addElement(new Integer(dObj.getSourceScriptType()));
				CommonUtils.addToQuery("TAppr.SOURCE_SCRIPT_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.SOURCE_SCRIPT_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getTargetScriptType()) && !"-1".equalsIgnoreCase(dObj.getTargetScriptType())){
				params.addElement(new Integer(dObj.getTargetScriptType()));
				CommonUtils.addToQuery("TAppr.TARGET_SCRIPT_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.TARGET_SCRIPT_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getReadinessScriptType()) && !"-1".equalsIgnoreCase(dObj.getReadinessScriptType())){
				params.addElement(new Integer(dObj.getReadinessScriptType()));
				CommonUtils.addToQuery("TAppr.READINESS_SCRIPTS_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.READINESS_SCRIPTS_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getPreactivityScriptType()) && !"-1".equalsIgnoreCase(dObj.getPreactivityScriptType())){
				params.addElement(new Integer(dObj.getPreactivityScriptType()));
				CommonUtils.addToQuery("TAppr.PREACTIVITY_SCRIPT_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PREACTIVITY_SCRIPT_TYPE = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getIntegrityScriptName())){
				params.addElement("%" + dObj.getIntegrityScriptName().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.INTEGRITY_SCRIPT_NAME) LIKE ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.INTEGRITY_SCRIPT_NAME) LIKE ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getFrequencyProcess()) && !"-1".equalsIgnoreCase(dObj.getFrequencyProcess())){
				params.addElement(new String(dObj.getFrequencyProcess()));
				CommonUtils.addToQuery("TAppr.FREQUENCY_PROCESS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.FREQUENCY_PROCESS = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getProcessSequence())){
				params.addElement(new Integer(dObj.getProcessSequence()));
				CommonUtils.addToQuery("TAppr.PROCESS_SEQUENCE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.PROCESS_SEQUENCE = ?", strBufPending);
			}
			/*if (ValidationUtil.isValid(AdfStartDay)){
				params.addElement(AdfStartDay.toUpperCase());
				CommonUtils.addToQuery("UPPER(To_Char(TAppr.ADF_START_TIME, 'HH24:MI')ADF_START_TIME) = ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(To_Char(TPend.ADF_START_TIME, 'HH24:MI')ADF_START_TIME) = ?", strBufPending);
			}
			if (ValidationUtil.isValid(AdfEndDay)){
				params.addElement(AdfEndDay.toUpperCase());
				CommonUtils.addToQuery("UPPER(To_Char(TAppr.ADF_END_TIME, 'HH24:MI')ADF_END_TIME) = ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(To_Char(TPend.ADF_END_TIME, 'HH24:MI')ADF_END_TIME) = ?", strBufPending);
			}*/
			if (ValidationUtil.isValid(dObj.getDateLastExtraction())){
				params.addElement(dObj.getDateLastExtraction().toUpperCase());
				CommonUtils.addToQuery("UPPER(To_Char(TAppr.DATE_LAST_EXTRACTION, 'DD-MM-YYYY')DATE_LAST_EXTRACTION) = ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(To_Char(TPend.DATE_LAST_EXTRACTION, 'DD-MM-YYYY')DATE_LAST_EXTRACTION) = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getIntegrityScriptType()) && !"-1".equalsIgnoreCase(dObj.getIntegrityScriptType())){
				params.addElement(new Integer(dObj.getIntegrityScriptType()));
				CommonUtils.addToQuery("TAppr.SOURCE_SCRIPT_TYPE = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.SOURCE_SCRIPT_TYPE = ?", strBufPending);
			}
			if (dObj.getDataAcqStatus() != -1){
				params.addElement(new Integer(dObj.getDataAcqStatus()));
				CommonUtils.addToQuery("TAppr.DATA_ACQU_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.DATA_ACQU_STATUS = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getTemplateControlStatus()) && !"-1".equalsIgnoreCase(dObj.getTemplateControlStatus())){
				params.addElement(new String(dObj.getTemplateControlStatus()));
				CommonUtils.addToQuery("TAppr.TEMPLATE_CONTROL_STATUS = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.TEMPLATE_CONTROL_STATUS = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getSubAdfNumber())){
				params.addElement(dObj.getSubAdfNumber().toUpperCase());
				CommonUtils.addToQuery("UPPER(TAppr.SUB_ADF_NUMBER) = ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.SUB_ADF_NUMBER) = ?", strBufPending);
			}
			if (dObj.getRecordIndicator() != -1){
				if (dObj.getRecordIndicator() > 3){
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				}else{
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}
			//Update Restriction - UBA
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()) || "Y".equalsIgnoreCase(visionUsersVb.getAutoUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery("TAppr.LE_BOOK IN ("+toSqlInList(visionUsersVb.getLeBook())+") ", strBufApprove);
					CommonUtils.addToQuery("Tpend.COUNTRY IN ("+toSqlInList(visionUsersVb.getCountry())+") ", strBufApprove);
				}
			}
			String orderBy=" Order By LE_BOOK ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params, getQueryPopupMapper());
			
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
	}
	private String toSqlInList(String CcountryLeBook) {
	    return Arrays.stream(CcountryLeBook.split(","))
	            .map(String::trim)
	            .map(val -> "'" + val + "'")
	            .collect(Collectors.joining(","));
	}
      public List<DataAcquisitionDynamicVb> getQueryResults(DataAcquisitionDynamicVb dObj, int intStatus){
    	  
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		Vector<Object> params = new Vector<Object>();
		//String AdfStartDay=TimeDateModifier(dObj.getAdfStartDay(),dObj.getAdfStartTime());
		//String AdfEndDay=TimeDateModifier(dObj.getAdfEndDay(),dObj.getAdfEndTime());
		
		StringBuffer strBufApprove = new StringBuffer("select * from(SELECT TAppr.COUNTRY, "+
				" TAppr.LE_BOOK, "+
				" TAppr.TEMPLATE_NAME, "+
				" TN.GENERAL_DESCRIPTION AS TEMPLATE_DESCRIPTION, "+
				" TN.FEED_STG_NAME AS FEED_STG_NAME, "+
				" TAppr.FILE_PATTERN, "+
				" TAppr.EXCEL_FILE_PATTERN, "+
				" TAppr.EXCEL_TEMPLATE_ID, "+
				" TAppr.ACQUISITION_PROCESS_TYPE_AT, "+
				" TAppr.ACQUISITION_PROCESS_TYPE, "+
				" TAppr.CONNECTIVITY_TYPE_AT, "+
				" TAppr.CONNECTIVITY_TYPE, "+
				" TAppr.CONNECTIVITY_DETAILS, "+
				" TAppr.INTEGRITY_SCRIPT_TYPE_AT, "+
				" TAppr.INTEGRITY_SCRIPT_TYPE, "+
				" TAppr.DATABASE_TYPE_AT, "+
				" TAppr.DATABASE_TYPE, "+
				" TAppr.DATABASE_CONNECTIVITY_DETAILS, "+
				" TAppr.SERVER_FOLDER_DETAILS, "+
				" TAppr.SOURCE_SCRIPT_TYPE_AT, "+
				" TAppr.SOURCE_SCRIPT_TYPE, "+
				clobFormat+" (TAppr.SOURCE_SERVER_SCRIPTS)SOURCE_SERVER_SCRIPTS , "+
				" TAppr.TARGET_SCRIPT_TYPE_AT, "+
				" TAppr.TARGET_SCRIPT_TYPE, "+
				clobFormat+" (TAppr.TARGET_SERVER_SCRIPTS) TARGET_SERVER_SCRIPTS , "+
				" TAppr.READINESS_SCRIPTS_TYPE_AT, "+
				" TAppr.READINESS_SCRIPTS_TYPE, "+
				clobFormat+" (TAppr.ACQUISITION_READINESS_SCRIPTS) ACQUISITION_READINESS_SCRIPTS, "+
				" TAppr.PREACTIVITY_SCRIPT_TYPE_AT, "+
				" TAppr.PREACTIVITY_SCRIPT_TYPE, "+
				clobFormat+" (TAppr.PREACTIVITY_SCRIPTS)PREACTIVITY_SCRIPTS, "+
				" TAppr.INTEGRITY_SCRIPT_NAME, "+
				" TAppr.FREQUENCY_PROCESS_AT, "+
				" TAppr.FREQUENCY_PROCESS, "+
				dateFormat+"  (TAppr.ADF_START_TIME, 'DD-MM-YYYY HH24 : MI') ADF_START_TIME, "+
				dateFormat+"  (TAppr.ADF_END_TIME, 'DD-MM-YYYY HH24 : MI') ADF_END_TIME, "+
				" TAppr.ACCESS_PERMISSION, "+
				dateFormat+"  (TAppr.DATE_LAST_EXTRACTION, 'DD-Mon-YYYY') DATE_LAST_EXTRACTION, "+
				" TAppr.DEBUG_MODE, "+
				" TAppr.MANDATORY_FLAG, "+
				" TAppr.MANDATORY_FLAG_AT, "+
				" TAppr.DATA_ACQU_STATUS_NT," +statusNtApprDesc+
				" ,TAppr.DATA_ACQU_STATUS,"+ 
				" TAppr.RECORD_INDICATOR_NT, "+
				" TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc+
				" ,TAppr.MAKER, "+makerApprDesc+
				" ,TAppr.VERIFIER, "+verifierApprDesc+
				" ,TAppr.INTERNAL_STATUS, "+
				 dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1 ," +
				dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION," +
				" TAppr.TEMPLATE_CONTROL_STATUS, "+
				" Case When "+nullFun+"(TAppr.SUB_ADF_NUMBER, 0) = 0 Then TN.SUB_ADF_NUMBER Else TAppr.SUB_ADF_NUMBER End SUB_ADF_Number, "+
				" Case When "+nullFun+"(TAppr.Process_Sequence, 0) = 0 Then TN.Process_Sequence Else TAppr.Process_Sequence End Process_Sequence, "
				+ " TAppr.MAJOR_BUILD, TAppr.RTT_NEXT_INTERVAL_TYPE_AT, TAppr.RTT_NEXT_INTERVAL_TYPE, TAppr.RTT_EVERY_X_MINS, TAppr.BUILD_RUN_FLAG "+
				" FROM ADF_DATA_ACQUISITION TAppr , Template_Names TN "+
				" Where   TAppr.Template_Name = TN.Template_Name ) TAppr");
		
		String strWhereNotExists = new String(" Not Exists (Select 'X' From ADF_DATA_ACQUISITION_PEND TPend Where TPend.COUNTRY = TAppr.COUNTRY "
				+ "AND TPend.LE_BOOK = TAppr.LE_BOOK AND TPend.TEMPLATE_NAME = TAppr.TEMPLATE_NAME)");
		
		StringBuffer strBufPending = new StringBuffer("select * from(SELECT TPend.COUNTRY, "+
				" TPend.LE_BOOK, "+
				" TPend.TEMPLATE_NAME, "+
				" TN.GENERAL_DESCRIPTION AS TEMPLATE_DESCRIPTION, "+
				" TN.FEED_STG_NAME AS FEED_STG_NAME, "+
				" TPend.FILE_PATTERN, "+
				" TPend.EXCEL_FILE_PATTERN, "+
				" TPend.EXCEL_TEMPLATE_ID, "+
				" TPend.ACQUISITION_PROCESS_TYPE_AT, "+
				" TPend.ACQUISITION_PROCESS_TYPE, "+
				" TPend.CONNECTIVITY_TYPE_AT, "+
				" TPend.CONNECTIVITY_TYPE, "+
				" TPend.CONNECTIVITY_DETAILS, "+
				" TPend.INTEGRITY_SCRIPT_TYPE_AT, "+
				" TPend.INTEGRITY_SCRIPT_TYPE, "+
				" TPend.DATABASE_TYPE_AT, "+
				" TPend.DATABASE_TYPE, "+
				" TPend.DATABASE_CONNECTIVITY_DETAILS, "+
				" TPend.SERVER_FOLDER_DETAILS, "+
				" TPend.SOURCE_SCRIPT_TYPE_AT, "+
				" TPend.SOURCE_SCRIPT_TYPE, "+
				clobFormat+" (TPend.SOURCE_SERVER_SCRIPTS)SOURCE_SERVER_SCRIPTS , "+
				" TPend.TARGET_SCRIPT_TYPE_AT, "+
				" TPend.TARGET_SCRIPT_TYPE, "+
				clobFormat+" (TPend.TARGET_SERVER_SCRIPTS) TARGET_SERVER_SCRIPTS , "+
				" TPend.READINESS_SCRIPTS_TYPE_AT, "+
				" TPend.READINESS_SCRIPTS_TYPE, "+
				clobFormat+" (TPend.ACQUISITION_READINESS_SCRIPTS) ACQUISITION_READINESS_SCRIPTS, "+
				" TPend.PREACTIVITY_SCRIPT_TYPE_AT, "+
				" TPend.PREACTIVITY_SCRIPT_TYPE, "+
				clobFormat+" (TPend.PREACTIVITY_SCRIPTS)PREACTIVITY_SCRIPTS, "+
				" TPend.INTEGRITY_SCRIPT_NAME, "+
				" TPend.FREQUENCY_PROCESS_AT, "+
				" TPend.FREQUENCY_PROCESS, "+
				dateFormat+"  (TPend.ADF_START_TIME, 'DD-MM-YYYY HH24 : MI') ADF_START_TIME, "+
				dateFormat+"  (TPend.ADF_END_TIME, 'DD-MM-YYYY HH24 : MI') ADF_END_TIME, "+
				" TPend.ACCESS_PERMISSION, "+
				dateFormat+"  (TPend.DATE_LAST_EXTRACTION, 'DD-Mon-YYYY') DATE_LAST_EXTRACTION, "+
				" TPend.DEBUG_MODE, "+
				" TPend.MANDATORY_FLAG, "+
				" TPend.MANDATORY_FLAG_AT, "+
				" TPend.DATA_ACQU_STATUS_NT, "+statusNtPendDesc+
				" ,TPend.DATA_ACQU_STATUS, "+
				" TPend.RECORD_INDICATOR_NT, "+
				" TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc+
				" ,TPend.MAKER, "+makerPendDesc+
				" ,TPend.VERIFIER, "+verifierPendDesc+
				" ,TPend.INTERNAL_STATUS, "+
				 dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," +
				dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION," +
				" TPend.TEMPLATE_CONTROL_STATUS, "+
				" Case When "+nullFun+"(TPend.SUB_ADF_NUMBER, 0) = 0 Then TN.SUB_ADF_NUMBER Else TPend.SUB_ADF_NUMBER End SUB_ADF_Number, "+
				" Case When "+nullFun+"(TPend.Process_Sequence, 0) = 0 Then TN.Process_Sequence Else TPend.Process_Sequence End Process_Sequence, "+
				" TPend.MAJOR_BUILD, TPend.RTT_NEXT_INTERVAL_TYPE_AT, TPend.RTT_NEXT_INTERVAL_TYPE, TPend.RTT_EVERY_X_MINS, TPend.BUILD_RUN_FLAG "+
				" FROM ADF_DATA_ACQUISITION_PEND TPend,  Template_Names TN "+
				" Where TPend.Template_Name = TN.Template_Name ) Tpend");		
		try
		{if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
			int count = 1;
			for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
				if (count == dObj.getSmartSearchOpt().size()) {
					data.setJoinType("");
				} else {
					if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
							|| "OR".equalsIgnoreCase(data.getJoinType()))) {
						data.setJoinType("AND");
					}
				}
				String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
				switch (data.getObject()) {
				case "templateDesc":
					CommonUtils.addToQuerySearch(" upper(TAppr.TEMPLATE_DESCRIPTION) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.TEMPLATE_DESCRIPTION) " + val, strBufPending,
							data.getJoinType());
					break;


				case "majorBuild":
					CommonUtils.addToQuerySearch(" upper(TAppr.MAJOR_BUILD) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.MAJOR_BUILD) " + val, strBufPending,
							data.getJoinType());
					break;

				case "subAdfNumber":
					CommonUtils.addToQuerySearch(" upper(TAppr.SUB_ADF_NUMBER) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.SUB_ADF_NUMBER) " + val, strBufPending,
							data.getJoinType());
					break;
				case "dateLastExtraction":
					CommonUtils.addToQuerySearch(" upper(TAppr.DATE_LAST_EXTRACTION) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.DATE_LAST_EXTRACTION) " + val, strBufPending,
							data.getJoinType());
					break;
				case "mandatoryFlag":
					CommonUtils.addToQuerySearch(" upper(TAppr.MANDATORY_FLAG) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.MANDATORY_FLAG) " + val, strBufPending,
							data.getJoinType());
					break;
				case "templateControlStatus":
					CommonUtils.addToQuerySearch(" upper(TAppr.TEMPLATE_CONTROL_STATUS) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.TEMPLATE_CONTROL_STATUS) " + val, strBufPending,
							data.getJoinType());
					break;
				case "acquisitionProcessType":
					CommonUtils.addToQuerySearch(" upper(TAppr.ACQUISITION_PROCESS_TYPE) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.ACQUISITION_PROCESS_TYPE) " + val, strBufPending,
							data.getJoinType());
					break;
				case "frequencyProcess":
					CommonUtils.addToQuerySearch(" upper(TAppr.FREQUENCY_PROCESS) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.FREQUENCY_PROCESS) " + val, strBufPending,
							data.getJoinType());
					break;
					
				case "dataAcqStatus":
					CommonUtils.addToQuerySearch(" upper(TAppr.DATA_ACQU_STATUS_DESC) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.DATA_ACQU_STATUS_DESC) " + val, strBufPending,
							data.getJoinType());
					break;

				case "recordIndicatorDesc":
					CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) " + val, strBufApprove,
							data.getJoinType());
					CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) " + val, strBufPending,
							data.getJoinType());
					break;

				default:
				}
				count++;
			}
		}
			String orderBy=" Order By DATE_LAST_MODIFIED_1 desc ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
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
	}
	
//	public List<DataAcquisitionDynamicVb> getQueryResultsForReview(DataAcquisitionDynamicVb dObj, int intStatus){
//		final int intKeyFieldsCount = 3;
//		List<DataAcquisitionDynamicVb> collTemp = null;
//		String CalLeBook = removeDescLeBook(dObj.getLeBook());
//		
//		String strQueryAppr = new String("SELECT TAppr.COUNTRY, TAppr.LE_BOOK||' - '||(Select Leb_description from LE_Book where LE_Book.LE_Book = TAppr.LE_Book and LE_Book.Country = TAppr.Country) as LE_BOOK, TAppr.TEMPLATE_NAME, (SELECT T.GENERAL_DESCRIPTION FROM TEMPLATE_NAMES T WHERE T.TEMPLATE_NAME = TAppr.TEMPLATE_NAME) AS TEMPLATE_DESCRIPTION , (SELECT T.FEED_STG_NAME FROM TEMPLATE_NAMES  T WHERE T.TEMPLATE_NAME = TAppr.TEMPLATE_NAME) AS FEED_STG_NAME, TAppr.FILE_PATTERN, "+
//				"TAppr.EXCEL_FILE_PATTERN, TAppr.EXCEL_TEMPLATE_ID, TAppr.ACQUISITION_PROCESS_TYPE_AT, TAppr.ACQUISITION_PROCESS_TYPE, "+
//				"TAppr.CONNECTIVITY_TYPE_AT, TAppr.CONNECTIVITY_TYPE, TAppr.CONNECTIVITY_DETAILS, TAppr.INTEGRITY_SCRIPT_TYPE_AT, TAppr.INTEGRITY_SCRIPT_TYPE, TAppr.DATABASE_TYPE_AT, "+
//				"TAppr.DATABASE_TYPE, TAppr.DATABASE_CONNECTIVITY_DETAILS, TAppr.SERVER_FOLDER_DETAILS, TAppr.SOURCE_SCRIPT_TYPE_AT, "+
//				"TAppr.SOURCE_SCRIPT_TYPE,TAppr.SOURCE_SERVER_SCRIPTS , TAppr.TARGET_SCRIPT_TYPE_AT, TAppr.TARGET_SCRIPT_TYPE, TAppr.TARGET_SERVER_SCRIPTS , "+
//				"TAppr.READINESS_SCRIPTS_TYPE_AT, TAppr.READINESS_SCRIPTS_TYPE, TAppr.ACQUISITION_READINESS_SCRIPTS , TAppr.PREACTIVITY_SCRIPT_TYPE_AT, "+
//				"TAppr.PREACTIVITY_SCRIPT_TYPE, TAppr.PREACTIVITY_SCRIPTS , TAppr.INTEGRITY_SCRIPT_NAME, TAppr.FREQUENCY_PROCESS_AT, "+
//				"TAppr.FREQUENCY_PROCESS, TAppr.PROCESS_SEQUENCE, To_Char(TAppr.ADF_START_TIME, 'DD-MM-YYYY HH24 : MI') ADF_START_TIME, "+
//				"To_Char(TAppr.ADF_END_TIME, 'DD-MM-YYYY HH24 : MI') ADF_END_TIME, TAppr.ACCESS_PERMISSION, "+
//				"To_Char(TAppr.DATE_LAST_EXTRACTION, 'DD-MM-YYYY') DATE_LAST_EXTRACTION, TAppr.DEBUG_MODE,TAppr.MANDATORY_FLAG,TAppr.MANDATORY_FLAG_AT,TAppr.DATA_ACQU_STATUS_NT, "+
//				"TAppr.DATA_ACQU_STATUS, TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, TAppr.MAKER, TAppr.VERIFIER, TAppr.INTERNAL_STATUS, "+
//				"To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED, To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION, "
//				+ "TAppr.TEMPLATE_CONTROL_STATUS, TAppr.SUB_ADF_NUMBER,  "+
//				" TAppr.MAJOR_BUILD, TAppr.RTT_NEXT_INTERVAL_TYPE_AT, TAppr.RTT_NEXT_INTERVAL_TYPE, TAppr.RTT_EVERY_X_MINS, TAppr.BUILD_RUN_FLAG "+
//				"FROM ADF_DATA_ACQUISITION TAppr WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.TEMPLATE_NAME = ? ");
//	
//		String strQueryPend = new String("SELECT TPend.COUNTRY, TPend.LE_BOOK||' - '||(Select Leb_description from LE_Book where LE_Book.LE_Book = TPend.LE_Book and LE_Book.Country = TPend.Country) as LE_BOOK, TPend.TEMPLATE_NAME, (SELECT T.GENERAL_DESCRIPTION FROM TEMPLATE_NAMES T WHERE T.TEMPLATE_NAME = TPend.TEMPLATE_NAME) AS TEMPLATE_DESCRIPTION , (SELECT T.FEED_STG_NAME FROM TEMPLATE_NAMES T WHERE T.TEMPLATE_NAME = TPend.TEMPLATE_NAME) AS FEED_STG_NAME , TPend.FILE_PATTERN, "+
//				"TPend.EXCEL_FILE_PATTERN, TPend.EXCEL_TEMPLATE_ID, TPend.ACQUISITION_PROCESS_TYPE_AT, TPend.ACQUISITION_PROCESS_TYPE, "+
//				"TPend.CONNECTIVITY_TYPE_AT, TPend.CONNECTIVITY_TYPE, TPend.CONNECTIVITY_DETAILS,TPend.INTEGRITY_SCRIPT_TYPE_AT, TPend.INTEGRITY_SCRIPT_TYPE, TPend.DATABASE_TYPE_AT, "+
//				"TPend.DATABASE_TYPE, TPend.DATABASE_CONNECTIVITY_DETAILS, TPend.SERVER_FOLDER_DETAILS, TPend.SOURCE_SCRIPT_TYPE_AT, "+
//				"TPend.SOURCE_SCRIPT_TYPE, TPend.SOURCE_SERVER_SCRIPTS , TPend.TARGET_SCRIPT_TYPE_AT, TPend.TARGET_SCRIPT_TYPE, TPend.TARGET_SERVER_SCRIPTS , "+
//				"TPend.READINESS_SCRIPTS_TYPE_AT,TPend.READINESS_SCRIPTS_TYPE,TPend.ACQUISITION_READINESS_SCRIPTS,TPend.PREACTIVITY_SCRIPT_TYPE_AT, "+
//				"TPend.PREACTIVITY_SCRIPT_TYPE, TPend.PREACTIVITY_SCRIPTS,TPend.INTEGRITY_SCRIPT_NAME,TPend.FREQUENCY_PROCESS_AT, "+
//				"TPend.FREQUENCY_PROCESS, TPend.PROCESS_SEQUENCE, To_Char(TPend.ADF_START_TIME, 'DD-MM-YYYY HH24 : MI') ADF_START_TIME, "+
//				"To_Char(TPend.ADF_END_TIME, 'DD-MM-YYYY HH24 : MI') ADF_END_TIME, TPend.ACCESS_PERMISSION, "+
//				"To_Char(TPend.DATE_LAST_EXTRACTION, 'DD-MM-YYYY') DATE_LAST_EXTRACTION, TPend.DEBUG_MODE,TPend.MANDATORY_FLAG,Tpend.MANDATORY_FLAG_AT, TPend.DATA_ACQU_STATUS_NT, "+
//				"TPend.DATA_ACQU_STATUS, TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, TPend.MAKER, TPend.VERIFIER, TPend.INTERNAL_STATUS, "+
//				"To_Char(TPend.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED, To_Char(TPend.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION, "
//				+ "TPend.TEMPLATE_CONTROL_STATUS, TPend.SUB_ADF_NUMBER,  "+
//				" TPend.MAJOR_BUILD, TPend.RTT_NEXT_INTERVAL_TYPE_AT, TPend.RTT_NEXT_INTERVAL_TYPE, TPend.RTT_EVERY_X_MINS, TPend.BUILD_RUN_FLAG "+
//				"FROM ADF_DATA_ACQUISITION_PEND TPend WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.TEMPLATE_NAME = ? ");
//
//		Object objParams[] = new Object[intKeyFieldsCount];
//		objParams[0] = new String(dObj.getCountry().toUpperCase());
//		objParams[1] = new String(CalLeBook);
//		objParams[2] = new String(dObj.getTemplateName().toUpperCase());
//
//		try
//		{if(!dObj.isVerificationRequired()){intStatus =0;}
//			if(intStatus == 0)
//			{
//				logger.info("Executing approved query");
//				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
//			}else{
//				logger.info("Executing pending query");
//				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
//			}
//			return collTemp;
//		}catch(Exception ex){
//			ex.printStackTrace();
//			logger.error("Error: getQueryResults Exception :   ");
//			if(intStatus == 0)
//				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
//			else
//				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));
//
//			if (objParams != null)
//				for(int i=0 ; i< objParams.length; i++)
//					logger.error("objParams[" + i + "]" + objParams[i].toString());
//			return null;
//		}
//	}
      
      public List<DataAcquisitionDynamicVb> getQueryResultsForReview(DataAcquisitionDynamicVb dObj, int intStatus){
  		final int intKeyFieldsCount = 3;
  		List<DataAcquisitionDynamicVb> collTemp = null;
  		String CalLeBook = removeDescLeBook(dObj.getLeBook());
  		
  		String strQueryAppr = new String("SELECT TAppr.COUNTRY, "+
				" TAppr.LE_BOOK, "+
				" TAppr.TEMPLATE_NAME, "+
				" TN.GENERAL_DESCRIPTION AS TEMPLATE_DESCRIPTION, "+
				" TN.FEED_STG_NAME AS FEED_STG_NAME, "+
				" TAppr.FILE_PATTERN, "+
				" TAppr.EXCEL_FILE_PATTERN, "+
				" TAppr.EXCEL_TEMPLATE_ID, "+
				" TAppr.ACQUISITION_PROCESS_TYPE_AT, "+
				" TAppr.ACQUISITION_PROCESS_TYPE, "+
				" TAppr.CONNECTIVITY_TYPE_AT, "+
				" TAppr.CONNECTIVITY_TYPE, "+
				" TAppr.CONNECTIVITY_DETAILS, "+
				" TAppr.INTEGRITY_SCRIPT_TYPE_AT, "+
				" TAppr.INTEGRITY_SCRIPT_TYPE, "+
				" TAppr.DATABASE_TYPE_AT, "+
				" TAppr.DATABASE_TYPE, "+
				" TAppr.DATABASE_CONNECTIVITY_DETAILS, "+
				" TAppr.SERVER_FOLDER_DETAILS, "+
				" TAppr.SOURCE_SCRIPT_TYPE_AT, "+
				" TAppr.SOURCE_SCRIPT_TYPE, "+
				" to_char(TAppr.SOURCE_SERVER_SCRIPTS)SOURCE_SERVER_SCRIPTS , "+
				" TAppr.TARGET_SCRIPT_TYPE_AT, "+
				" TAppr.TARGET_SCRIPT_TYPE, "+
				" to_char(TAppr.TARGET_SERVER_SCRIPTS) TARGET_SERVER_SCRIPTS , "+
				" TAppr.READINESS_SCRIPTS_TYPE_AT, "+
				" TAppr.READINESS_SCRIPTS_TYPE, "+
				" to_char(TAppr.ACQUISITION_READINESS_SCRIPTS) ACQUISITION_READINESS_SCRIPTS, "+
				" TAppr.PREACTIVITY_SCRIPT_TYPE_AT, "+
				" TAppr.PREACTIVITY_SCRIPT_TYPE, "+
				" to_char(TAppr.PREACTIVITY_SCRIPTS)PREACTIVITY_SCRIPTS, "+
				" TAppr.INTEGRITY_SCRIPT_NAME, "+
				" TAppr.FREQUENCY_PROCESS_AT, "+
				" TAppr.FREQUENCY_PROCESS, "+
				" TO_CHAR (TAppr.ADF_START_TIME, 'DD-MM-YYYY HH24 : MI') ADF_START_TIME, "+
				" TO_CHAR (TAppr.ADF_END_TIME, 'DD-MM-YYYY HH24 : MI') ADF_END_TIME, "+
				" TAppr.ACCESS_PERMISSION, "+
				" TO_CHAR (TAppr.DATE_LAST_EXTRACTION, 'DD-Mon-YYYY') DATE_LAST_EXTRACTION, "+
				" TAppr.DEBUG_MODE, "+
				" TAppr.MANDATORY_FLAG, "+
				" TAppr.MANDATORY_FLAG_AT, "+
				" TAppr.DATA_ACQU_STATUS_NT, "+
				" TAppr.DATA_ACQU_STATUS, "+
				" TAppr.RECORD_INDICATOR_NT, "+
				" TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc+
				" ,TAppr.MAKER, "+makerApprDesc+
				" ,TAppr.VERIFIER, "+verifierApprDesc+
				" ,TAppr.INTERNAL_STATUS, "+
				" TO_CHAR (TAppr.DATE_LAST_MODIFIED, 'DD-Mon-YYYY HH24:MI:SS') DATE_LAST_MODIFIED, "+
				" TO_CHAR (TAppr.DATE_CREATION, 'DD-Mon-YYYY HH24:MI:SS') DATE_CREATION, "+
				" TAppr.TEMPLATE_CONTROL_STATUS, "+
				" Case When NVL(TAppr.SUB_ADF_NUMBER, 0) = 0 Then TN.SUB_ADF_NUMBER Else TAppr.SUB_ADF_NUMBER End SUB_ADF_Number, "+
				" Case When NVL(TAppr.Process_Sequence, 0) = 0 Then TN.Process_Sequence Else TAppr.Process_Sequence End Process_Sequence, "
				+ " TAppr.MAJOR_BUILD, TAppr.RTT_NEXT_INTERVAL_TYPE_AT, TAppr.RTT_NEXT_INTERVAL_TYPE, TAppr.RTT_EVERY_X_MINS, TAppr.BUILD_RUN_FLAG "+
				" FROM ADF_DATA_ACQUISITION TAppr,  Template_Names TN "
				+ "WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.TEMPLATE_NAME = ?"
				+ "and TN.TEMPLATE_NAME = TAppr.TEMPLATE_NAME");
  	
  		String strQueryPend = new String("SELECT TPend.COUNTRY, "+
				" TPend.LE_BOOK, "+
				" TPend.TEMPLATE_NAME, "+
				" TN.GENERAL_DESCRIPTION AS TEMPLATE_DESCRIPTION, "+
				" TN.FEED_STG_NAME AS FEED_STG_NAME, "+
				" TPend.FILE_PATTERN, "+
				" TPend.EXCEL_FILE_PATTERN, "+
				" TPend.EXCEL_TEMPLATE_ID, "+
				" TPend.ACQUISITION_PROCESS_TYPE_AT, "+
				" TPend.ACQUISITION_PROCESS_TYPE, "+
				" TPend.CONNECTIVITY_TYPE_AT, "+
				" TPend.CONNECTIVITY_TYPE, "+
				" TPend.CONNECTIVITY_DETAILS, "+
				" TPend.INTEGRITY_SCRIPT_TYPE_AT, "+
				" TPend.INTEGRITY_SCRIPT_TYPE, "+
				" TPend.DATABASE_TYPE_AT, "+
				" TPend.DATABASE_TYPE, "+
				" TPend.DATABASE_CONNECTIVITY_DETAILS, "+
				" TPend.SERVER_FOLDER_DETAILS, "+
				" TPend.SOURCE_SCRIPT_TYPE_AT, "+
				" TPend.SOURCE_SCRIPT_TYPE, "+
				" to_char(TPend.SOURCE_SERVER_SCRIPTS)SOURCE_SERVER_SCRIPTS , "+
				" TPend.TARGET_SCRIPT_TYPE_AT, "+
				" TPend.TARGET_SCRIPT_TYPE, "+
				" to_char(TPend.TARGET_SERVER_SCRIPTS) TARGET_SERVER_SCRIPTS , "+
				" TPend.READINESS_SCRIPTS_TYPE_AT, "+
				" TPend.READINESS_SCRIPTS_TYPE, "+
				" to_char(TPend.ACQUISITION_READINESS_SCRIPTS) ACQUISITION_READINESS_SCRIPTS, "+
				" TPend.PREACTIVITY_SCRIPT_TYPE_AT, "+
				" TPend.PREACTIVITY_SCRIPT_TYPE, "+
				" to_char(TPend.PREACTIVITY_SCRIPTS)PREACTIVITY_SCRIPTS, "+
				" TPend.INTEGRITY_SCRIPT_NAME, "+
				" TPend.FREQUENCY_PROCESS_AT, "+
				" TPend.FREQUENCY_PROCESS, "+
				" TO_CHAR (TPend.ADF_START_TIME, 'DD-MM-YYYY HH24 : MI') ADF_START_TIME, "+
				" TO_CHAR (TPend.ADF_END_TIME, 'DD-MM-YYYY HH24 : MI') ADF_END_TIME, "+
				" TPend.ACCESS_PERMISSION, "+
				" TO_CHAR (TPend.DATE_LAST_EXTRACTION, 'DD-Mon-YYYY') DATE_LAST_EXTRACTION, "+
				" TPend.DEBUG_MODE, "+
				" TPend.MANDATORY_FLAG, "+
				" TPend.MANDATORY_FLAG_AT, "+
				" TPend.DATA_ACQU_STATUS_NT, "+
				" TPend.DATA_ACQU_STATUS, "+
				" TPend.RECORD_INDICATOR_NT, "+
				" TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc+
			" ,TPend.MAKER, "+makerPendDesc+
				" ,TPend.VERIFIER, "+verifierPendDesc+
				", TPend.INTERNAL_STATUS, "+
				" TO_CHAR (TPend.DATE_LAST_MODIFIED, 'DD-Mon-YYYY HH24:MI:SS') DATE_LAST_MODIFIED, "+
				" TO_CHAR (TPend.DATE_CREATION, 'DD-Mon-YYYY HH24:MI:SS') DATE_CREATION, "+
				" TPend.TEMPLATE_CONTROL_STATUS, "+
				" Case When NVL(TPend.SUB_ADF_NUMBER, 0) = 0 Then TN.SUB_ADF_NUMBER Else TPend.SUB_ADF_NUMBER End SUB_ADF_Number, "+
				" Case When NVL(TPend.Process_Sequence, 0) = 0 Then TN.Process_Sequence Else TPend.Process_Sequence End Process_Sequence, "+
				" TPend.MAJOR_BUILD, TPend.RTT_NEXT_INTERVAL_TYPE_AT, TPend.RTT_NEXT_INTERVAL_TYPE, TPend.RTT_EVERY_X_MINS, TPend.BUILD_RUN_FLAG "+
				" FROM ADF_DATA_ACQUISITION_PEND TPend,  Template_Names TN "
				+ " WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.TEMPLATE_NAME = ?"
				+ "and TN.TEMPLATE_NAME = TPend.TEMPLATE_NAME");

  		Object objParams[] = new Object[intKeyFieldsCount];
  		objParams[0] = new String(dObj.getCountry().toUpperCase());
  		objParams[1] = new String(CalLeBook);
  		objParams[2] = new String(dObj.getTemplateName().toUpperCase());

  		try
  		{if(!dObj.isVerificationRequired()){intStatus =0;}
  			if(intStatus == 0)
  			{
  				logger.info("Executing approved query");
  				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
  			}else{
  				logger.info("Executing pending query");
  				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
  			}
  			return collTemp;
  		}catch(Exception ex){
  			ex.printStackTrace();
  			logger.error("Error: getQueryResults Exception :   ");
  			if(intStatus == 0)
  				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
  			else
  				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

  			if (objParams != null)
  				for(int i=0 ; i< objParams.length; i++)
  					logger.error("objParams[" + i + "]" + objParams[i].toString());
  			return null;
  		}
  	}
  	
	@Override
	protected List<DataAcquisitionDynamicVb> selectApprovedRecord(DataAcquisitionDynamicVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<DataAcquisitionDynamicVb> doSelectPendingRecord(DataAcquisitionDynamicVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(DataAcquisitionDynamicVb records){return records.getDataAcqStatus();}
	@Override
	protected void setStatus(DataAcquisitionDynamicVb vObject,int status){vObject.setDataAcqStatus(status);}
	
	@Override
	protected int doInsertionAppr(DataAcquisitionDynamicVb vObject){
		PreparedStatement ps= null;
		String startTimeModified="";
		String endTimeModified="";
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		//String startTimeModified=TimeDateModifier(vObject.getAdfStartDay(),vObject.getAdfStartTime()).trim();
		//String endTimeModified=TimeDateModifier(vObject.getAdfEndDay(),vObject.getAdfEndTime()).trim();
		int result=0;
		try {
			String query = "Insert Into ADF_DATA_ACQUISITION (COUNTRY, LE_BOOK, TEMPLATE_NAME, MANDATORY_FLAG, MANDATORY_FLAG_AT, FILE_PATTERN, EXCEL_FILE_PATTERN, EXCEL_TEMPLATE_ID, ACQUISITION_PROCESS_TYPE_AT, ACQUISITION_PROCESS_TYPE, CONNECTIVITY_TYPE_AT, CONNECTIVITY_TYPE, "+
		            " CONNECTIVITY_DETAILS, DATABASE_TYPE_AT, DATABASE_TYPE, DATABASE_CONNECTIVITY_DETAILS, SERVER_FOLDER_DETAILS, SOURCE_SCRIPT_TYPE_AT, SOURCE_SCRIPT_TYPE, TARGET_SCRIPT_TYPE_AT, TARGET_SCRIPT_TYPE, READINESS_SCRIPTS_TYPE_AT, "+
		            " READINESS_SCRIPTS_TYPE, PREACTIVITY_SCRIPT_TYPE_AT, PREACTIVITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_TYPE_AT, INTEGRITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_NAME, FREQUENCY_PROCESS_AT, FREQUENCY_PROCESS, "+
		            " PROCESS_SEQUENCE, ADF_START_TIME, ADF_END_TIME, ACCESS_PERMISSION, DATE_LAST_EXTRACTION, DEBUG_MODE, DATA_ACQU_STATUS_NT, DATA_ACQU_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, "+
		            " VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, TEMPLATE_CONTROL_STATUS_AT, TEMPLATE_CONTROL_STATUS, SUB_ADF_NUMBER, "
		            + " MAJOR_BUILD,RTT_NEXT_INTERVAL_TYPE_AT, RTT_NEXT_INTERVAL_TYPE, RTT_EVERY_X_MINS,BUILD_RUN_FLAG, "
		            + " SOURCE_SERVER_SCRIPTS, TARGET_SERVER_SCRIPTS, ACQUISITION_READINESS_SCRIPTS, PREACTIVITY_SCRIPTS "+
		            " ) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, To_Date(?, 'DD-MM-YYYY HH24 : MI') , To_Date(?, 'DD-MM-YYYY HH24 : MI'), ?, To_Date(?, 'DD-MM-YYYY HH24 : MI'), "+
		            " ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+systemDate+", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
			Object[] args = {vObject.getCountry(), CalLeBook, vObject.getTemplateName(), vObject.getMandatoryFlag(),vObject.getMandatoryFlagAt(), vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(), vObject.getAcquisitionProcessTypeAt(), vObject.getAcquisitionProcessType(), vObject.getConnectivityTypeAt(), 
			vObject.getConnectivityType(), vObject.getConnectivityDetails(), vObject.getDatabaseTypeAt(), vObject.getDatabaseType(), vObject.getDatabaseConnectivityDetails(), vObject.getServerFolderDetails(), vObject.getSourceScriptTypeAt(), vObject.getSourceScriptType(), vObject.getTargetScriptTypeAt(), 
			vObject.getTargetScriptType(), vObject.getReadinessScriptTypeAt(), vObject.getReadinessScriptType(), vObject.getPreactivityScriptTypeAt(), vObject.getPreactivityScriptType(), vObject.getIntegrityScriptTypeAt(), vObject.getIntegrityScriptType(), vObject.getIntegrityScriptName(), vObject.getFrequencyProcessAt(), 
			vObject.getFrequencyProcess(), vObject.getProcessSequence(), startTimeModified, endTimeModified, vObject.getAccessPermission(), vObject.getDateLastExtraction(), vObject.getDebugMode(), vObject.getDataAcqStatusNt(), vObject.getDataAcqStatus(), vObject.getRecordIndicatorNt(), 
			vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getTemplateControlStatusAt(), vObject.getTemplateControlStatus(), vObject.getSubAdfNumber()
			, vObject.getMajorBuild(), vObject.getRttNextIntervalTypeAt(), vObject.getRttNextIntervalType(), vObject.getRttEveryXMins(), vObject.getBuildRunFlag()};/*, vObject.getSourceServerScript(), 
			vObject.getTargetServerScript(), vObject.getAcquisitionReadinessScripts(), vObject.getPreactivityScripts()};*/
			
			ps= getConnection().prepareStatement(query);
			for(int i=1;i<=args.length;i++){
				ps.setObject(i,args[i-1]);	
			}
			String sourceServerScript = vObject.getSourceServerScript();
				try{
					if(sourceServerScript.equalsIgnoreCase(null))
						sourceServerScript="";
				}catch(Exception e){
					sourceServerScript="";
				}
				Reader reader = new StringReader(sourceServerScript);
				ps.setCharacterStream(args.length+1, reader, sourceServerScript.length());
			String targetServerScript = vObject.getTargetServerScript();
				try{
					if(targetServerScript.equalsIgnoreCase(null))
						targetServerScript="";
				}catch(Exception e){
					targetServerScript="";			
				}
				reader = new StringReader(targetServerScript);
				ps.setCharacterStream(args.length+2, reader, targetServerScript.length());
			String acquisitionReadinessScripts = vObject.getAcquisitionReadinessScripts();
				try{
					if(acquisitionReadinessScripts.equalsIgnoreCase(null))
						acquisitionReadinessScripts="";
				}catch(Exception e){
					acquisitionReadinessScripts="";			
				}
				reader = new StringReader(acquisitionReadinessScripts);
				ps.setCharacterStream(args.length+3, reader, acquisitionReadinessScripts.length());
			String preactivityScripts = vObject.getPreactivityScripts();
				try{
					if(preactivityScripts.equalsIgnoreCase(null))
						preactivityScripts="";
				}catch(Exception e){
					preactivityScripts="";			
				}
				reader = new StringReader(preactivityScripts);
				ps.setCharacterStream(args.length+4, reader, preactivityScripts.length());
				result = ps.executeUpdate();
		}catch (Exception e) {
//			System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
		} finally{
			if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	@Override
	protected int doInsertionPend(DataAcquisitionDynamicVb vObject){
		PreparedStatement ps= null;
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String startTimeModified="";
		String endTimeModified="";
		//String startTimeModified=TimeDateModifier(vObject.getAdfStartDay(),vObject.getAdfStartTime()).trim();
		//String endTimeModified=TimeDateModifier(vObject.getAdfEndDay(),vObject.getAdfEndTime()).trim();
		int result = 0;
		try {
			String query = "Insert Into ADF_DATA_ACQUISITION_PEND (COUNTRY, LE_BOOK, TEMPLATE_NAME, MANDATORY_FLAG, MANDATORY_FLAG_AT,FILE_PATTERN, EXCEL_FILE_PATTERN, EXCEL_TEMPLATE_ID, ACQUISITION_PROCESS_TYPE_AT, ACQUISITION_PROCESS_TYPE, CONNECTIVITY_TYPE_AT, CONNECTIVITY_TYPE, "+
					" CONNECTIVITY_DETAILS, DATABASE_TYPE_AT, DATABASE_TYPE, DATABASE_CONNECTIVITY_DETAILS, SERVER_FOLDER_DETAILS, SOURCE_SCRIPT_TYPE_AT, SOURCE_SCRIPT_TYPE, TARGET_SCRIPT_TYPE_AT, TARGET_SCRIPT_TYPE, READINESS_SCRIPTS_TYPE_AT, "+
					" READINESS_SCRIPTS_TYPE, PREACTIVITY_SCRIPT_TYPE_AT, PREACTIVITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_TYPE_AT, INTEGRITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_NAME, FREQUENCY_PROCESS_AT, FREQUENCY_PROCESS, "+
					" PROCESS_SEQUENCE, ADF_START_TIME, ADF_END_TIME, ACCESS_PERMISSION, DATE_LAST_EXTRACTION, DEBUG_MODE, DATA_ACQU_STATUS_NT, DATA_ACQU_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, "+
					" VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, TEMPLATE_CONTROL_STATUS_AT, TEMPLATE_CONTROL_STATUS, SUB_ADF_NUMBER,"
					+ " MAJOR_BUILD,RTT_NEXT_INTERVAL_TYPE_AT, RTT_NEXT_INTERVAL_TYPE, RTT_EVERY_X_MINS,BUILD_RUN_FLAG, "
					+ "SOURCE_SERVER_SCRIPTS, TARGET_SERVER_SCRIPTS, ACQUISITION_READINESS_SCRIPTS, "+
					" PREACTIVITY_SCRIPTS) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, To_Date(?, 'DD-MM-YYYY HH24 : MI') , To_Date(?, 'DD-MM-YYYY HH24 : MI'), ?, To_Date(?, 'DD-MM-YYYY HH24 : MI'), "+
					" ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+systemDate+", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

					Object[] args = {vObject.getCountry(), CalLeBook, vObject.getTemplateName(), vObject.getMandatoryFlag(),vObject.getMandatoryFlagAt(), vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(), vObject.getAcquisitionProcessTypeAt(), vObject.getAcquisitionProcessType(), vObject.getConnectivityTypeAt(), 
					vObject.getConnectivityType(), vObject.getConnectivityDetails(), vObject.getDatabaseTypeAt(), vObject.getDatabaseType(), vObject.getDatabaseConnectivityDetails(), vObject.getServerFolderDetails(), vObject.getSourceScriptTypeAt(), vObject.getSourceScriptType(), vObject.getTargetScriptTypeAt(), 
					vObject.getTargetScriptType(), vObject.getReadinessScriptTypeAt(), vObject.getReadinessScriptType(), vObject.getPreactivityScriptTypeAt(), vObject.getPreactivityScriptType(), vObject.getIntegrityScriptTypeAt(), vObject.getIntegrityScriptType(), vObject.getIntegrityScriptName(), vObject.getFrequencyProcessAt(), 
					vObject.getFrequencyProcess(), vObject.getProcessSequence(), startTimeModified, endTimeModified, vObject.getAccessPermission(), vObject.getDateLastExtraction(), vObject.getDebugMode(), vObject.getDataAcqStatusNt(), vObject.getDataAcqStatus(), vObject.getRecordIndicatorNt(), 
					vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getTemplateControlStatusAt(), 
					vObject.getTemplateControlStatus(), vObject.getSubAdfNumber()
					, vObject.getMajorBuild(), vObject.getRttNextIntervalTypeAt(), vObject.getRttNextIntervalType(), vObject.getRttEveryXMins(), vObject.getBuildRunFlag()};/*, vObject.getSourceServerScript(), 
					vObject.getTargetServerScript(), vObject.getAcquisitionReadinessScripts(), vObject.getPreactivityScripts()};*/
			
		    ps= getConnection().prepareStatement(query);
			for(int i=1;i<=args.length;i++){
				ps.setObject(i,args[i-1]);	
			}
			String sourceServerScript = vObject.getSourceServerScript();
				try{
					if(sourceServerScript.equalsIgnoreCase(null))
						sourceServerScript="";
				}catch(Exception e){
					sourceServerScript="";
				}
				ps.setCharacterStream(args.length+1, new StringReader(sourceServerScript), sourceServerScript.length());
			    String targetServerScript = vObject.getTargetServerScript();
				try{
					if(targetServerScript.equalsIgnoreCase(null))
						targetServerScript="";
				}catch(Exception e){
					targetServerScript="";			
				}
				ps.setCharacterStream(args.length+2, new StringReader(targetServerScript), targetServerScript.length());
			    String acquisitionReadinessScripts = vObject.getAcquisitionReadinessScripts();
				try{
					if(acquisitionReadinessScripts.equalsIgnoreCase(null))
						acquisitionReadinessScripts="";
				}catch(Exception e){
					acquisitionReadinessScripts="";			
				}
				ps.setCharacterStream(args.length+3, new StringReader(acquisitionReadinessScripts), acquisitionReadinessScripts.length());
			    String preactivityScripts = vObject.getPreactivityScripts();
				try{
					if(preactivityScripts.equalsIgnoreCase(null))
						preactivityScripts="";
				}catch(Exception e){
					preactivityScripts="";			
				}
				ps.setCharacterStream(args.length+4, new StringReader(preactivityScripts), preactivityScripts.length());
			    result = ps.executeUpdate();
		}catch (Exception e) {
//			System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
		} finally{
			if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	@Override
	protected int doInsertionPendWithDc(DataAcquisitionDynamicVb vObject){
		PreparedStatement ps= null;
		String startTimeModified="";
		String endTimeModified="";
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		//String startTimeModified=TimeDateModifier(vObject.getAdfStartDay(),vObject.getAdfStartTime()).trim();
		//String endTimeModified=TimeDateModifier(vObject.getAdfEndDay(),vObject.getAdfEndTime()).trim();
		int result = 0;
		try{
			String query = "Insert Into ADF_DATA_ACQUISITION_PEND (COUNTRY, LE_BOOK, TEMPLATE_NAME, MANDATORY_FLAG,MANDATORY_FLAG_AT, FILE_PATTERN, EXCEL_FILE_PATTERN, EXCEL_TEMPLATE_ID, ACQUISITION_PROCESS_TYPE_AT, ACQUISITION_PROCESS_TYPE, CONNECTIVITY_TYPE_AT, CONNECTIVITY_TYPE, "+
					" CONNECTIVITY_DETAILS, DATABASE_TYPE_AT, DATABASE_TYPE, DATABASE_CONNECTIVITY_DETAILS, SERVER_FOLDER_DETAILS, SOURCE_SCRIPT_TYPE_AT, SOURCE_SCRIPT_TYPE, TARGET_SCRIPT_TYPE_AT, TARGET_SCRIPT_TYPE, READINESS_SCRIPTS_TYPE_AT, "+
					" READINESS_SCRIPTS_TYPE, PREACTIVITY_SCRIPT_TYPE_AT, PREACTIVITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_TYPE_AT, INTEGRITY_SCRIPT_TYPE, INTEGRITY_SCRIPT_NAME, FREQUENCY_PROCESS_AT, FREQUENCY_PROCESS, "+
					" PROCESS_SEQUENCE, ADF_START_TIME, ADF_END_TIME, ACCESS_PERMISSION, DATE_LAST_EXTRACTION, DEBUG_MODE, DATA_ACQU_STATUS_NT, DATA_ACQU_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, "+
					" VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, TEMPLATE_CONTROL_STATUS_AT, TEMPLATE_CONTROL_STATUS, SUB_ADF_NUMBER, MAJOR_BUILD,RTT_NEXT_INTERVAL_TYPE_AT, RTT_NEXT_INTERVAL_TYPE, RTT_EVERY_X_MINS,BUILD_RUN_FLAG, "
					+ "SOURCE_SERVER_SCRIPTS, TARGET_SERVER_SCRIPTS, ACQUISITION_READINESS_SCRIPTS, PREACTIVITY_SCRIPTS "+
					" ) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, To_Date(?, 'DD-MM-YYYY HH24 : MI') , To_Date(?, 'DD-MM-YYYY HH24 : MI'), ?, To_Date(?, 'DD-MM-YYYY HH24 : MI'), "+
					" ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+dateTimeConvert+", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

					Object[] args = {vObject.getCountry(), CalLeBook, vObject.getTemplateName(), vObject.getMandatoryFlag(),vObject.getMandatoryFlagAt(),vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(), vObject.getAcquisitionProcessTypeAt(), vObject.getAcquisitionProcessType(), vObject.getConnectivityTypeAt(), 
					vObject.getConnectivityType(), vObject.getConnectivityDetails(), vObject.getDatabaseTypeAt(), vObject.getDatabaseType(), vObject.getDatabaseConnectivityDetails(), vObject.getServerFolderDetails(), vObject.getSourceScriptTypeAt(), vObject.getSourceScriptType(), vObject.getTargetScriptTypeAt(), 
					vObject.getTargetScriptType(), vObject.getReadinessScriptTypeAt(), vObject.getReadinessScriptType(), vObject.getPreactivityScriptTypeAt(), vObject.getPreactivityScriptType(), vObject.getIntegrityScriptTypeAt(), vObject.getIntegrityScriptType(), vObject.getIntegrityScriptName(), vObject.getFrequencyProcessAt(), 
					vObject.getFrequencyProcess(), vObject.getProcessSequence(), startTimeModified, endTimeModified, vObject.getAccessPermission(), vObject.getDateLastExtraction(), vObject.getDebugMode(), vObject.getDataAcqStatusNt(), vObject.getDataAcqStatus(), vObject.getRecordIndicatorNt(), 
					vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getDateCreation(), vObject.getTemplateControlStatusAt(), vObject.getTemplateControlStatus(), vObject.getSubAdfNumber()
					, vObject.getMajorBuild(), vObject.getRttNextIntervalTypeAt(), vObject.getRttNextIntervalType(), vObject.getRttEveryXMins(), vObject.getBuildRunFlag()};/*, vObject.getSourceServerScript(), 
					vObject.getTargetServerScript(), vObject.getAcquisitionReadinessScripts(), vObject.getPreactivityScripts()};*/
			ps= getConnection().prepareStatement(query);
			for(int i=1;i<=args.length;i++){
				ps.setObject(i,args[i-1]);	
			}
			String sourceServerScript = vObject.getSourceServerScript();
				try{
					if(sourceServerScript.equalsIgnoreCase(null))
						sourceServerScript="";
				}catch(Exception e){
					sourceServerScript="";
				}
				ps.setCharacterStream(args.length+1, new StringReader(sourceServerScript), sourceServerScript.length());
			      String targetServerScript = vObject.getTargetServerScript();
				try{
					if(targetServerScript.equalsIgnoreCase(null))
						targetServerScript="";
				}catch(Exception e){
					targetServerScript="";			
				}
				ps.setCharacterStream(args.length+2, new StringReader(targetServerScript), targetServerScript.length());
			      String acquisitionReadinessScripts = vObject.getAcquisitionReadinessScripts();
				try{
					if(acquisitionReadinessScripts.equalsIgnoreCase(null))
						acquisitionReadinessScripts="";
				}catch(Exception e){
					acquisitionReadinessScripts="";			
				}
				ps.setCharacterStream(args.length+3, new StringReader(acquisitionReadinessScripts), acquisitionReadinessScripts.length());
			     String preactivityScripts = vObject.getPreactivityScripts();
				try{
					if(preactivityScripts.equalsIgnoreCase(null))
						preactivityScripts="";
				}catch(Exception e){
					preactivityScripts="";			
				}
			ps.setCharacterStream(args.length+4, new StringReader(preactivityScripts), preactivityScripts.length());
		    result = ps.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			strErrorDesc = e.getMessage();
			logger.error("Update Error : "+e.getMessage());
		} finally{
			if (ps != null) {
				try {
					ps.close();
					ps=null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	@Override
	protected int doUpdateAppr(DataAcquisitionDynamicVb vObject){
		PreparedStatement ps= null;
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String startTimeModified="";
		String endTimeModified="";
		/*if(vObject.isVerificationRequired()){
			startTimeModified=TimeDateModifierNormal(vObject.getAdfStartDay(),vObject.getAdfStartTime()).trim();
			endTimeModified=TimeDateModifierNormal(vObject.getAdfEndDay(),vObject.getAdfEndTime()).trim();
		}else{
			startTimeModified=TimeDateModifier(vObject.getAdfStartDay(),vObject.getAdfStartTime()).trim();
			endTimeModified=TimeDateModifier(vObject.getAdfEndDay(),vObject.getAdfEndTime()).trim();
		}*/
		String query = "UPDATE ADF_DATA_ACQUISITION SET FILE_PATTERN = ?, EXCEL_FILE_PATTERN = ?, EXCEL_TEMPLATE_ID = ?, "+
				"ACQUISITION_PROCESS_TYPE_AT = ?, ACQUISITION_PROCESS_TYPE = ?, CONNECTIVITY_TYPE_AT = ?, CONNECTIVITY_TYPE = ?, "+
				"CONNECTIVITY_DETAILS = ?, INTEGRITY_SCRIPT_TYPE_AT =?, INTEGRITY_SCRIPT_TYPE=?, DATABASE_TYPE_AT = ?, DATABASE_TYPE = ?, DATABASE_CONNECTIVITY_DETAILS = ?, "+
				"SERVER_FOLDER_DETAILS = ?, SOURCE_SCRIPT_TYPE_AT = ?, SOURCE_SCRIPT_TYPE = ?, TARGET_SCRIPT_TYPE_AT = ?, "+
				"TARGET_SCRIPT_TYPE = ?, READINESS_SCRIPTS_TYPE_AT = ?, READINESS_SCRIPTS_TYPE = ?, "+
				"PREACTIVITY_SCRIPT_TYPE_AT = ?, PREACTIVITY_SCRIPT_TYPE = ?, "+
				"INTEGRITY_SCRIPT_NAME = ?, FREQUENCY_PROCESS_AT = ?, FREQUENCY_PROCESS = ?, PROCESS_SEQUENCE = ?, ADF_START_TIME = To_Date(?, 'DD-MM-YYYY HH24 : MI'), "+
				"ADF_END_TIME = To_Date(?, 'DD-MM-YYYY HH24 : MI'), ACCESS_PERMISSION = ?, DATE_LAST_EXTRACTION = To_Date(?, 'DD-MM-YYYY'), DEBUG_MODE = ?, "+
				"MANDATORY_FLAG = ?, MANDATORY_FLAG_AT = ?,DATA_ACQU_STATUS_NT = ?, DATA_ACQU_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, "+
				"VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = SysDate, TEMPLATE_CONTROL_STATUS_AT = ?, TEMPLATE_CONTROL_STATUS = ?, SUB_ADF_NUMBER = ?, "
				+ " MAJOR_BUILD = ? ,RTT_NEXT_INTERVAL_TYPE_AT = ? , RTT_NEXT_INTERVAL_TYPE = ? , RTT_EVERY_X_MINS = ? ,BUILD_RUN_FLAG = ? "
				+ "WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? ";
		Object[] args = {vObject.getFilePattern(), vObject.getExcelFilePattern(),
				vObject.getExcelTemplateId(), vObject.getAcquisitionProcessTypeAt(), vObject.getAcquisitionProcessType(), vObject.getConnectivityTypeAt(), vObject.getConnectivityType(), vObject.getConnectivityDetails(),
				vObject.getIntegrityScriptTypeAt(),vObject.getIntegrityScriptType(), vObject.getDatabaseTypeAt(), vObject.getDatabaseType(), vObject.getDatabaseConnectivityDetails(), vObject.getServerFolderDetails(), vObject.getSourceScriptTypeAt(),
				vObject.getSourceScriptType(), vObject.getTargetScriptTypeAt(), vObject.getTargetScriptType(), vObject.getReadinessScriptTypeAt(),
				vObject.getReadinessScriptType(), vObject.getPreactivityScriptTypeAt(), vObject.getPreactivityScriptType(), 
				vObject.getIntegrityScriptName(), vObject.getFrequencyProcessAt(), vObject.getFrequencyProcess(), vObject.getProcessSequence(), startTimeModified, endTimeModified, vObject.getAccessPermission(),
				vObject.getDateLastExtraction(), vObject.getDebugMode(), vObject.getMandatoryFlag(),vObject.getMandatoryFlagAt(),vObject.getDataAcqStatusNt(), vObject.getDataAcqStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(), vObject.getInternalStatus(), vObject.getTemplateControlStatusAt(), vObject.getTemplateControlStatus(), vObject.getSubAdfNumber()
				, vObject.getMajorBuild(), vObject.getRttNextIntervalTypeAt(), vObject.getRttNextIntervalType(), vObject.getRttEveryXMins(), vObject.getBuildRunFlag(), vObject.getCountry(),CalLeBook, vObject.getTemplateName()};
		
		int result = getJdbcTemplate().update(query,args);
		 try {
			 if(result == Constants.SUCCESSFUL_OPERATION){
					String query1 = "UPDATE ADF_DATA_ACQUISITION SET SOURCE_SERVER_SCRIPTS =?, TARGET_SERVER_SCRIPTS = ?, ACQUISITION_READINESS_SCRIPTS = ?, PREACTIVITY_SCRIPTS = ? "
							+ " where COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? ";
					
					 result=	 getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
								PreparedStatement ps = connection.prepareStatement(query1);
								int psIndex = 0;
								String mainQuery = ValidationUtil.isValid(vObject.getSourceServerScript())?vObject.getSourceServerScript():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getTargetServerScript())?vObject.getTargetServerScript():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getAcquisitionReadinessScripts())?vObject.getAcquisitionReadinessScripts():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								mainQuery = ValidationUtil.isValid(vObject.getPreactivityScripts())?vObject.getPreactivityScripts():"";
								ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
								
								
								ps.setObject(5, vObject.getCountry());
								ps.setObject(6, CalLeBook);
								ps.setObject(7, vObject.getTemplateName());
								return ps;
							}
						});
					
				}
		 }catch (Exception e) {
//				System.out.println(e.getMessage());
				strErrorDesc = e.getMessage();
				logger.error("Update Error : "+e.getMessage());
			} finally{
				if (ps != null) {
					try {
						ps.close();
						ps=null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}
	
	@Override
	protected int doUpdatePend(DataAcquisitionDynamicVb vObject){
		PreparedStatement ps= null;
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String startTimeModified="";
		String endTimeModified="";
		//String startTimeModified=TimeDateModifier(vObject.getAdfStartDay(),vObject.getAdfStartTime()).trim();
		//String endTimeModified=TimeDateModifier(vObject.getAdfEndDay(),vObject.getAdfEndTime()).trim();
		
		String query = "UPDATE ADF_DATA_ACQUISITION_PEND SET FILE_PATTERN = ?, EXCEL_FILE_PATTERN = ?, EXCEL_TEMPLATE_ID = ?, "+
				"ACQUISITION_PROCESS_TYPE_AT = ?, ACQUISITION_PROCESS_TYPE = ?, CONNECTIVITY_TYPE_AT = ?, CONNECTIVITY_TYPE = ?, "+
				"CONNECTIVITY_DETAILS = ?, INTEGRITY_SCRIPT_TYPE_AT =?, INTEGRITY_SCRIPT_TYPE=?, DATABASE_TYPE_AT = ?, DATABASE_TYPE = ?, DATABASE_CONNECTIVITY_DETAILS = ?, "+
				"SERVER_FOLDER_DETAILS = ?, SOURCE_SCRIPT_TYPE_AT = ?, SOURCE_SCRIPT_TYPE = ?,  TARGET_SCRIPT_TYPE_AT = ?, "+
				"TARGET_SCRIPT_TYPE = ?, READINESS_SCRIPTS_TYPE_AT = ?, READINESS_SCRIPTS_TYPE = ?, "+
				"PREACTIVITY_SCRIPT_TYPE_AT = ?, PREACTIVITY_SCRIPT_TYPE = ?, "+
				"INTEGRITY_SCRIPT_NAME = ?, FREQUENCY_PROCESS_AT = ?, FREQUENCY_PROCESS = ?, PROCESS_SEQUENCE = ?, ADF_START_TIME = To_Date(?, 'DD-MM-YYYY HH24 : MI'), "+
				"ADF_END_TIME = To_Date(?, 'DD-MM-YYYY HH24 : MI'), ACCESS_PERMISSION = ?, DATE_LAST_EXTRACTION = To_Date(?, 'DD-MM-YYYY'), DEBUG_MODE = ?, "+
				"MANDATORY_FLAG = ?,MANDATORY_FLAG_AT =?, DATA_ACQU_STATUS_NT = ?, DATA_ACQU_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, "+
				"VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+", TEMPLATE_CONTROL_STATUS_AT = ?, TEMPLATE_CONTROL_STATUS = ?, SUB_ADF_NUMBER = ?,  "
				+ " MAJOR_BUILD = ?,RTT_NEXT_INTERVAL_TYPE_AT = ?, RTT_NEXT_INTERVAL_TYPE = ?, RTT_EVERY_X_MINS = ?,BUILD_RUN_FLAG = ? "
				+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? ";
		Object[] args = {vObject.getFilePattern(), vObject.getExcelFilePattern(),
				vObject.getExcelTemplateId(), vObject.getAcquisitionProcessTypeAt(), vObject.getAcquisitionProcessType(), vObject.getConnectivityTypeAt(), vObject.getConnectivityType(), vObject.getConnectivityDetails(),
				vObject.getIntegrityScriptTypeAt(),vObject.getIntegrityScriptType(), vObject.getDatabaseTypeAt(), vObject.getDatabaseType(), vObject.getDatabaseConnectivityDetails(), vObject.getServerFolderDetails(), vObject.getSourceScriptTypeAt(),
				vObject.getSourceScriptType(),  vObject.getTargetScriptTypeAt(), vObject.getTargetScriptType(), vObject.getReadinessScriptTypeAt(),
				vObject.getReadinessScriptType(), vObject.getPreactivityScriptTypeAt(), vObject.getPreactivityScriptType(), 
				vObject.getIntegrityScriptName(), vObject.getFrequencyProcessAt(), vObject.getFrequencyProcess(), vObject.getProcessSequence(), startTimeModified, endTimeModified, vObject.getAccessPermission(),
				vObject.getDateLastExtraction(), vObject.getDebugMode(), vObject.getMandatoryFlag(),vObject.getMandatoryFlagAt(),vObject.getDataAcqStatusNt(), vObject.getDataAcqStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),vObject.getMaker(),vObject.getVerifier(), 
				vObject.getInternalStatus(), vObject.getTemplateControlStatusAt(), vObject.getTemplateControlStatus(), vObject.getSubAdfNumber()
				, vObject.getMajorBuild(), vObject.getRttNextIntervalTypeAt(), vObject.getRttNextIntervalType(), vObject.getRttEveryXMins(), vObject.getBuildRunFlag(), vObject.getCountry(),CalLeBook, vObject.getTemplateName()};
	     int result = getJdbcTemplate().update(query,args);
		 try {
			 if(result == Constants.SUCCESSFUL_OPERATION){
				String	query1 = "UPDATE ADF_DATA_ACQUISITION_PEND SET SOURCE_SERVER_SCRIPTS =?, TARGET_SERVER_SCRIPTS = ?, ACQUISITION_READINESS_SCRIPTS = ?, PREACTIVITY_SCRIPTS = ? "
							+ " where COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? ";
					result=	 getJdbcTemplate().update(new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							PreparedStatement ps = connection.prepareStatement(query1);
							int psIndex = 0;
							String mainQuery = ValidationUtil.isValid(vObject.getSourceServerScript())?vObject.getSourceServerScript():"";
							ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
							
							mainQuery = ValidationUtil.isValid(vObject.getTargetServerScript())?vObject.getTargetServerScript():"";
							ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
							
							mainQuery = ValidationUtil.isValid(vObject.getAcquisitionReadinessScripts())?vObject.getAcquisitionReadinessScripts():"";
							ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
							
							mainQuery = ValidationUtil.isValid(vObject.getPreactivityScripts())?vObject.getPreactivityScripts():"";
							ps.setCharacterStream(++psIndex, new StringReader(mainQuery), mainQuery.length());
							
							
							ps.setObject(5, vObject.getCountry());
							ps.setObject(6, CalLeBook);
							ps.setObject(7, vObject.getTemplateName());
							return ps;
						}
					});
					
				}
		 }catch (Exception e) {
//				System.out.println(e.getMessage());
				strErrorDesc = e.getMessage();
				logger.error("Update Error : "+e.getMessage());
			} finally{
				if (ps != null) {
					try {
						ps.close();
						ps=null;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}
	
	@Override
	protected int doDeleteAppr(DataAcquisitionDynamicVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From ADF_DATA_ACQUISITION Where COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ?";
		Object[] args = {vObject.getCountry(),CalLeBook, vObject.getTemplateName()};
		return getJdbcTemplate().update(query,args);
	}
	
	@Override
	protected int deletePendingRecord(DataAcquisitionDynamicVb vObject){
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From ADF_DATA_ACQUISITION_PEND Where COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ?";
		Object[] args = {vObject.getCountry(),CalLeBook, vObject.getTemplateName()};
		return getJdbcTemplate().update(query,args);
	}
	
	@Override
	protected String frameErrorMessage(DataAcquisitionDynamicVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try{
			String CalLeBook = removeDescLeBook(vObject.getLeBook());
			strErrMsg =  strErrMsg + "COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + "LE_BOOK:" + CalLeBook;
			strErrMsg =  strErrMsg + "TEMPLATE_NAME:" + vObject.getTemplateName();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}
	@Override
	protected String getAuditString(DataAcquisitionDynamicVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		
		if(!vObject.isAuditDbPop()){
				StringBuffer strAudit = new StringBuffer("");
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry());
				strAudit.append(auditDelimiter);
				strAudit.append("LE_BOOK"+auditDelimiterColVal+removeDescLeBook(vObject.getLeBook()));
				strAudit.append(auditDelimiter);
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+vObject.getTemplateName());
				strAudit.append(auditDelimiter);
				strAudit.append("FILE_PATTERN"+auditDelimiterColVal+vObject.getFilePattern());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getExcelFilePattern()))
					strAudit.append("EXCEL_FILE_PATTERN"+auditDelimiterColVal+vObject.getExcelFilePattern());
				else
					strAudit.append("EXCEL_FILE_PATTERN"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getExcelTemplateId()))
					strAudit.append("EXCEL_TEMPLATE_ID"+auditDelimiterColVal+vObject.getExcelTemplateId());
				else
					strAudit.append("EXCEL_TEMPLATE_ID"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("ACQUISITION_PROCESS_TYPE_AT"+auditDelimiterColVal+vObject.getAcquisitionProcessTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("ACQUISITION_PROCESS_TYPE"+auditDelimiterColVal+vObject.getAcquisitionProcessType());
				strAudit.append(auditDelimiter);
				strAudit.append("CONNECTIVITY_TYPE_AT"+auditDelimiterColVal+vObject.getConnectivityTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("CONNECTIVITY_TYPE_AT"+auditDelimiterColVal+vObject.getConnectivityType());
				strAudit.append(auditDelimiter);
				strAudit.append("CONNECTIVITY_DETAILS"+auditDelimiterColVal+vObject.getConnectivityDetails());
				strAudit.append(auditDelimiter);
				strAudit.append("DATABASE_TYPE_AT"+auditDelimiterColVal+vObject.getDatabaseTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("DATABASE_TYPE"+auditDelimiterColVal+vObject.getDatabaseType());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getDatabaseConnectivityDetails()))
					strAudit.append("DATABASE_CONNECTIVITY_DETAILS"+auditDelimiterColVal+vObject.getDatabaseConnectivityDetails());
				else
					strAudit.append("DATABASE_CONNECTIVITY_DETAILS"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getServerFolderDetails()))
					strAudit.append("SERVER_FOLDER_DETAILS"+auditDelimiterColVal+vObject.getServerFolderDetails());
				else
					strAudit.append("SERVER_FOLDER_DETAILS"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("SOURCE_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getSourceScriptTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("SOURCE_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getSourceScriptType());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getSourceServerScript()))
					strAudit.append("SOURCE_SERVER_SCRIPTS"+auditDelimiterColVal+vObject.getSourceServerScript());
				else
					strAudit.append("SOURCE_SERVER_SCRIPTS"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("TARGET_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getTargetScriptTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("TARGET_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getTargetScriptType());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getTargetServerScript()))
					strAudit.append("TARGET_SERVER_SCRIPTS"+auditDelimiterColVal+vObject.getTargetServerScript());
				else
					strAudit.append("TARGET_SERVER_SCRIPTS"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("READINESS_SCRIPTS_TYPE_AT"+auditDelimiterColVal+vObject.getReadinessScriptTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("READINESS_SCRIPTS_TYPE"+auditDelimiterColVal+vObject.getReadinessScriptType());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getAcquisitionReadinessScripts()))
					strAudit.append("ACQUISITION_READINESS_SCRIPTS"+auditDelimiterColVal+vObject.getAcquisitionReadinessScripts());
				else
					strAudit.append("ACQUISITION_READINESS_SCRIPTS"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("PREACTIVITY_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getPreactivityScriptTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("PREACTIVITY_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getPreactivityScriptType());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getPreactivityScripts()))
					strAudit.append("PREACTIVITY_SCRIPTS"+auditDelimiterColVal+vObject.getPreactivityScripts());
				else
					strAudit.append("PREACTIVITY_SCRIPTS"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("INTEGRITY_SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getIntegrityScriptTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("INTEGRITY_SCRIPT_TYPE"+auditDelimiterColVal+vObject.getIntegrityScriptType());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getIntegrityScriptName()))
					strAudit.append("INTEGRITY_SCRIPT_NAME"+auditDelimiterColVal+vObject.getIntegrityScriptName());
				else
					strAudit.append("INTEGRITY_SCRIPT_NAME"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("FREQUENCY_PROCESS_AT"+auditDelimiterColVal+vObject.getFrequencyProcessAt());
				strAudit.append(auditDelimiter);
				strAudit.append("FREQUENCY_PROCESS"+auditDelimiterColVal+vObject.getFrequencyProcess());
				strAudit.append(auditDelimiter);
				strAudit.append("PROCESS_SEQUENCE"+auditDelimiterColVal+vObject.getProcessSequence());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getAdfStartTime()) && ValidationUtil.isValid(vObject.getAdfStartDay()))
					strAudit.append("ADF_START_TIME"+auditDelimiterColVal+CBD+" + "+vObject.getAdfStartDay()+"("+vObject.getAdfStartTime().trim()+")");
				else
					strAudit.append("ADF_START_TIME"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getAdfEndTime()) && ValidationUtil.isValid(vObject.getAdfEndDay()))
					strAudit.append("ADF_END_TIME"+auditDelimiterColVal+CBD+" + "+vObject.getAdfEndDay()+"("+vObject.getAdfEndTime().trim()+")");
				else
					strAudit.append("ADF_END_TIME"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("ACCESS_PERMISSION"+auditDelimiterColVal+vObject.getAccessPermission());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getDateLastExtraction()))
					strAudit.append("DATE_LAST_EXTRACTION"+auditDelimiterColVal+vObject.getDateLastExtraction());
				else
					strAudit.append("DATE_LAST_EXTRACTION"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("DEBUG_MODE"+auditDelimiterColVal+vObject.getDebugMode());
				strAudit.append(auditDelimiter);
				strAudit.append("MANDATORY_FLAG"+auditDelimiterColVal+vObject.getMandatoryFlag());
				strAudit.append(auditDelimiter);
				strAudit.append("DATA_ACQU_STATUS_NT"+auditDelimiterColVal+vObject.getDataAcqStatusNt());
				strAudit.append(auditDelimiter);
				strAudit.append("DATA_ACQU_STATUS"+auditDelimiterColVal+vObject.getDataAcqStatus());
				strAudit.append(auditDelimiter);
				strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
				strAudit.append(auditDelimiter);
				strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
				strAudit.append(auditDelimiter);
				strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
				strAudit.append(auditDelimiter);
				if(vObject.getRecordIndicator() == -1)
					vObject.setRecordIndicator(0);
				strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
				strAudit.append(auditDelimiter);
				strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
				strAudit.append(auditDelimiter);
				if(ValidationUtil.isValid(vObject.getDateLastModified()))
					strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified());
				else
					strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
		
				if(ValidationUtil.isValid(vObject.getDateCreation()))
					strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation());
				else
					strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
				strAudit.append("TEMPLATE_CONTROL_STATUS_AT"+auditDelimiterColVal+vObject.getTemplateControlStatusAt());
				strAudit.append(auditDelimiter);
				strAudit.append("TEMPLATE_CONTROL_STATUS"+auditDelimiterColVal+vObject.getTemplateControlStatus());
				strAudit.append(auditDelimiter);
				strAudit.append("SUB_ADF_NUMBER"+auditDelimiterColVal+vObject.getSubAdfNumber());
				strAudit.append(auditDelimiter);
				
				strAudit.append("MAJOR_BUILD"+auditDelimiterColVal+vObject.getMajorBuild());
				strAudit.append(auditDelimiter);
				
				strAudit.append("RTT_NEXT_INTERVAL_TYPE_AT"+auditDelimiterColVal+vObject.getRttNextIntervalTypeAt());
				strAudit.append(auditDelimiter);
				
				strAudit.append("RTT_NEXT_INTERVAL_TYPE"+auditDelimiterColVal+vObject.getRttNextIntervalType());
				strAudit.append(auditDelimiter);
				
				strAudit.append("RTT_EVERY_X_MINS"+auditDelimiterColVal+vObject.getRttEveryXMins());
				strAudit.append(auditDelimiter);
				
				strAudit.append("BUILD_RUN_FLAG"+auditDelimiterColVal+vObject.getBuildRunFlag());
				strAudit.append(auditDelimiter);
				
				
				return strAudit.toString();
		}else{
		    
				StringBuffer strAudit = new StringBuffer("");
			    strAudit.append("VARIABLE_NAME"+auditDelimiterColVal+vObject.getPopDbVarName().toUpperCase());
				strAudit.append(auditDelimiter);
				strAudit.append("SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getSourceScriptTypeAt());
				strAudit.append(auditDelimiter);
				strAudit.append("SCRIPT_TYPE"+auditDelimiterColVal+vObject.getDatabaseType());
				strAudit.append(auditDelimiter);
				strAudit.append("VARIABLE_SCRIPT"+auditDelimiterColVal+vObject.getSourceScriptType());
			    strAudit.append(auditDelimiter);
			    strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			    strAudit.append(auditDelimiter);
			    if(ValidationUtil.isValid(vObject.getDateLastModified()))
					strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified());
				else
					strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
		
				if(ValidationUtil.isValid(vObject.getDateCreation()))
					strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation());
				else
					strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
				strAudit.append(auditDelimiter);
		 
		    return strAudit.toString();	
		}
	}
	@Override
	protected void setServiceDefaults(){
		serviceName = "DataAcq";
		serviceDesc = "Data Acquisition";
		tableName = "ADF_DATA_ACQUISITION";
		childTableName = "ADF_DATA_ACQUISITION";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	protected void setServiceDefaults1(){
		serviceName = "DataAcq";
		serviceDesc = "Data Acquisition";
		tableName = "VISION_DYNAMIC_HASH_VAR";
		childTableName = "VISION_DYNAMIC_HASH_VAR";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	public String getValue(String source, String key){
		 try {
//			 Matcher regexMatcher = Pattern.compile(key+"(.*?\\$@!)(.*?)#\\}").matcher(source);
			 Matcher regexMatcher = Pattern.compile("\\{"+key+":#(.*?)\\$@!(.*?)\\#}").matcher(source);
			 return regexMatcher.find()? regexMatcher.group(2) :null;
		} catch (Exception e) {
		     return null;
		}
	}
	public List<AlphaSubTabVb> getType(int pAlphaTab) throws DataAccessException {
		String sql = "SELECT * FROM ALPHA_SUB_TAB WHERE ALPHA_SUBTAB_STATUS = 0 AND ALPHA_TAB = ?  AND ALPHA_SUB_TAB !='MACROVAR' ORDER BY ALPHA_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pAlphaTab;
		return  getJdbcTemplate().query(sql, lParams, getMapperForType());
	}
	
	protected RowMapper getMapperForType(){
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
	public String getScriptValue(String variableName){
		  Object params[] = new Object[1];
		  params[0] = variableName;
		  String returnValue =getJdbcTemplate().queryForObject("select VARIABLE_SCRIPT from VISION_DYNAMIC_HASH_VAR WHERE SCRIPT_TYPE='MACROVAR' AND UPPER(VARIABLE_NAME)=UPPER(?) ",
		      params,String.class);
		  return (!ValidationUtil.isValid(returnValue) ? "" : returnValue);
		 }
	public String checkExist(String variableName){
		  Object params[] = new Object[1];
		  params[0] = variableName;
		  String returnValue =getJdbcTemplate().queryForObject("select VARIABLE_NAME from VISION_DYNAMIC_HASH_VAR WHERE SCRIPT_TYPE='MACROVAR' AND UPPER(VARIABLE_NAME)=UPPER(?) ",
		      params,String.class);
		  return (!ValidationUtil.isValid(returnValue) ? "" : returnValue);
		 }

   public int doInsertToDynVarTable(DataAcquisitionDynamicVb data,String connectScript){
	 String VarSript =connectScript;
		setServiceDefaults();
		int result=0;
		String query ="Insert into VISION_DYNAMIC_HASH_VAR "+
			" (VARIABLE_NAME, VARIABLE_TYPE_NT, VARIABLE_TYPE, SCRIPT_TYPE_AT, SCRIPT_TYPE, VARIABLE_SCRIPT, SORT_ORDER, VARIABLE_STATUS_NT, VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "+ 
			"Values "+
			"('"+data.getPopDbVarName().toUpperCase()+"', 1056, 2, 1083, 'MACROVAR', "+ 
			"'"+VarSript+"', 1, 1, 0, 7,  "+
			"0, '"+intCurrentUserId+"', '"+intCurrentUserId+"', 0, sysdate,sysdate)";
		try {
		    return result= getJdbcTemplate().update(query);
		} catch (CannotGetJdbcConnectionException e) {
			e.printStackTrace();
			return result;
		} 
	  }	
   
	public int doUpdateToDynVarTable(DataAcquisitionDynamicVb data,String connectScript) throws SQLException{
		String VarSript =connectScript;
		setServiceDefaults();
				int result=0;
				setServiceDefaults();
				String query ="UPDATE VISION_DYNAMIC_HASH_VAR "+
					"SET VARIABLE_SCRIPT='"+VarSript+"',MAKER='"+intCurrentUserId+"', VERIFIER='"+intCurrentUserId+"' WHERE UPPER(VARIABLE_NAME)=UPPER('"+data.getPopDbVarName().toUpperCase()+"') ";
				try {
					return result= getJdbcTemplate().update(query);
				} catch (CannotGetJdbcConnectionException e) {
					e.printStackTrace();
					return result;
				} 
		}
		
	public ExceptionCode doDbInsertUpdateToDynVarTable(DataAcquisitionDynamicVb data ,DataAcquisitionDynamicVb QueryData) throws SQLException{
			int result=0;
			ExceptionCode exceptionCode =new ExceptionCode();
			setServiceDefaults1();
			try{
				String VarSript=data.getSourceScriptType().replaceAll("\\'", "\\'\\'");
				if(data.getDbPopOperation().equalsIgnoreCase("true")){
					String query ="Insert into VISION_DYNAMIC_HASH_VAR "+
							" (VARIABLE_NAME, VARIABLE_TYPE_NT, VARIABLE_TYPE, SCRIPT_TYPE_AT, SCRIPT_TYPE, VARIABLE_SCRIPT, SORT_ORDER, VARIABLE_STATUS_NT, VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "+ 
							"Values "+
							"('"+data.getPopDbVarName().toUpperCase()+"', 1056, 2, 1083, 'MACROVAR', "+ 
							"'"+data.getSourceScriptType()+"', 1, 1, 0, 7,  "+
							"0, '"+intCurrentUserId+"', '"+intCurrentUserId+"', 0, sysdate,sysdate)";
					result = getJdbcTemplate().update(query);
					strApproveOperation =Constants.ADD;
					exceptionCode = writeAuditLog(data, null);
					exceptionCode.setErrorMsg(result+":-:"+"Success");
					return exceptionCode;
				}else{
				    String query ="UPDATE VISION_DYNAMIC_HASH_VAR SET VARIABLE_SCRIPT='"+VarSript+"', "
							 	+ "MAKER='"+intCurrentUserId+"', VERIFIER='"+intCurrentUserId+"', DATE_LAST_MODIFIED =Sysdate WHERE UPPER(VARIABLE_NAME)=UPPER('"+data.getPopDbVarName().toUpperCase()+"') ";
					result= getJdbcTemplate().update(query);
					strApproveOperation =Constants.MODIFY;
					exceptionCode = writeAuditLog(data,QueryData);
					exceptionCode.setErrorMsg(2+":-:"+"Success");
					return exceptionCode;
				}  
			}catch (CannotGetJdbcConnectionException e) {
				e.printStackTrace();
				exceptionCode.setErrorMsg(result+":-:"+e.getMessage());
				return exceptionCode;
			} 
	  }
	public List<DataAcquisitionDynamicVb> getServerQuery( DataAcquisitionDynamicVb dataAcquisitionDynamicVb) throws DataAccessException {
		  String sql = "Select MACROVAR_NAME,MACROVAR_TYPE,TAG_NAME, DISPLAY_NAME,SUBSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),1,INSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),'#}')-1) Value "+
						"From "+
						"(SELECT X1.MACROVAR_NAME,X1.MACROVAR_TYPE,X1.TAG_NAME,X1.TAG_NO, X1.DISPLAY_NAME, "+
						"(SELECT Case When INSTR( ?,REPLACE((TAG_NAME),' ','_')) > 0 "+
						"      Then SUBSTR( ? , "+
						"                INSTR( ? , REPLACE((TAG_NAME),' ','_') )+LENGTH(REPLACE((TAG_NAME),' ','_'))) "+
						"                Else NULL End STR FROM DUAL) STR "+
						"FROM MACROVAR_TAGGING X1 "+
						"WHERE X1.MACROVAR_NAME = 'CONNECTIVITY_DETAILS' "+
						"and X1.MACROVAR_TYPE = ?) ORDER BY TAG_NO";
		Object[] lParams = new Object[4];
				lParams[0] = dataAcquisitionDynamicVb.getConnectionString();
				lParams[1] = dataAcquisitionDynamicVb.getConnectionString();
				lParams[2] = dataAcquisitionDynamicVb.getConnectionString();
				lParams[3] = dataAcquisitionDynamicVb.getConnectivityType();
		return  getJdbcTemplate().query(sql, lParams, getServerMapper());
	}
	
	protected RowMapper getServerMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setMacroName(rs.getString("MACROVAR_NAME"));
				dataAcqVb.setMacroType(rs.getString("MACROVAR_TYPE"));
				dataAcqVb.setTagName(rs.getString("TAG_NAME"));
				dataAcqVb.setTagValue(rs.getString("Value"));
				dataAcqVb.setDisplayName(rs.getString("DISPLAY_NAME"));
				return dataAcqVb;
			}
		};
		return mapper;
	}
	public List<DataAcquisitionDynamicVb> getDataQuery( DataAcquisitionDynamicVb dataAcquisitionDynamicVb) throws DataAccessException {
			//String sql = "SELECT MACROVAR_NAME,MACROVAR_TYPE,TAG_NAME FROM MACROVAR_TAGGING WHERE MACROVAR_NAME ='CONNECTIVITY_DETAILS' and MACROVAR_TYPE = ? ORDER BY TAG_NO";
			  String sql = "Select MACROVAR_NAME,MACROVAR_TYPE,TAG_NAME,DISPLAY_NAME,MASKED_FLAG,ENCRYPTION,SUBSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),1,INSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),'#}')-1) Value "+
							"From "+
							"(SELECT X1.MACROVAR_NAME,X1.MACROVAR_TYPE,X1.TAG_NAME,X1.TAG_NO,X1.DISPLAY_NAME,X1.MASKED_FLAG,X1.ENCRYPTION, "+
							"(SELECT Case When INSTR( ?,REPLACE((TAG_NAME),' ','_')) > 0 "+
							"      Then SUBSTR( ? , "+
							"                INSTR( ? , REPLACE((TAG_NAME),' ','_') )+LENGTH(REPLACE((TAG_NAME),' ','_'))) "+
							"                Else NULL End STR FROM DUAL) STR "+
							"FROM MACROVAR_TAGGING X1 "+
							"WHERE X1.MACROVAR_NAME = 'DB_DETAILS' "+
							"and X1.MACROVAR_TYPE = ?) ORDER BY TAG_NO";
			Object[] lParams = new Object[4];
					lParams[0] = dataAcquisitionDynamicVb.getConnectionString();
					lParams[1] = dataAcquisitionDynamicVb.getConnectionString();
					lParams[2] = dataAcquisitionDynamicVb.getConnectionString();
					lParams[3] = dataAcquisitionDynamicVb.getDatabaseType();
		return  getJdbcTemplate().query(sql, lParams, getDataMapper());
	}
	
	protected RowMapper getDataMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setMacroName(rs.getString("MACROVAR_NAME"));
				dataAcqVb.setMacroType(rs.getString("MACROVAR_TYPE"));
				dataAcqVb.setTagName(rs.getString("TAG_NAME"));
				dataAcqVb.setTagValue(rs.getString("Value"));
				dataAcqVb.setDisplayName(rs.getString("DISPLAY_NAME"));
				dataAcqVb.setMasked(rs.getString("MASKED_FLAG"));
				dataAcqVb.setEncryption(rs.getString("ENCRYPTION"));
				return dataAcqVb;
			}
		};
		return mapper;
	}	
	public List getServerTag(DataAcquisitionDynamicVb dataAcquisitionDynamicVb) throws DataAccessException {
		String sql = "SELECT * FROM MACROVAR_TAGGING WHERE MACROVAR_NAME ='CONNECTIVITY_DETAILS' and MACROVAR_TYPE = ? ORDER BY TAG_NO";
		Object[] lParams = new Object[1];
		lParams[0] = dataAcquisitionDynamicVb.getPopConnectivityType();
		return  getJdbcTemplate().query(sql, lParams, getServerTagMapper());	
	}
	protected RowMapper getServerTagMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setMacroName(rs.getString("MACROVAR_NAME"));
				dataAcqVb.setMacroType(rs.getString("MACROVAR_TYPE"));
				dataAcqVb.setTagName(rs.getString("TAG_NAME"));
				return dataAcqVb;
			}
		};
		return mapper;
	}
	public List getDataTag(DataAcquisitionDynamicVb dataAcquisitionDynamicVb) throws DataAccessException {
		String sql = "SELECT * FROM MACROVAR_TAGGING WHERE MACROVAR_NAME ='DB_DETAILS' and MACROVAR_TYPE = ? ORDER BY TAG_NO";
		Object[] lParams = new Object[1];
		lParams[0] = dataAcquisitionDynamicVb.getPopDbConnectivityType();
		return  getJdbcTemplate().query(sql, lParams, getTagMapper());	
	}
	protected RowMapper getTagMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataAcquisitionDynamicVb dataAcqVb = new DataAcquisitionDynamicVb();
				dataAcqVb.setMacroName(rs.getString("MACROVAR_NAME"));
				dataAcqVb.setMacroType(rs.getString("MACROVAR_TYPE"));
				dataAcqVb.setTagName(rs.getString("TAG_NAME"));
				return dataAcqVb;
			}
		};
		return mapper;
	  }
	public String getScriptQuery(String Scriptdet){
		  Object params[] = new Object[1];
		  params[0] = Scriptdet;
		  String returnValue =getJdbcTemplate().queryForObject("select Variable_script from VISION_DYNAMIC_HASH_VAR where variable_type='2' and Variable_Name= ?",
		      params,String.class);
		  return (!ValidationUtil.isValid(returnValue) ? "" : returnValue);
	}

		String TimeDateModifier(String dateValue,String timeValue){
			String Temp1="";
			String Temp2="";
			String Temp3="";
			if(!"-1".equalsIgnoreCase(dateValue) && !timeValue.equalsIgnoreCase("") && !timeValue.equalsIgnoreCase(" ") && !timeValue.equalsIgnoreCase(null)){
				if(dateValue.equalsIgnoreCase(CBD)){
					dateValue=CBD+" + 0";
				}
				try{
					String TempArr[]=dateValue.split("\\+");
					Temp1=TempArr[1].replaceAll("\\s+", "");
				}catch(Exception e){
					Temp1=dateValue.trim();
				}
				int t=0;
				t= Integer.parseInt(Temp1)+1;
				
				if(String.valueOf(t).length()== 1){
					Temp2="0"+t+"/01/1900";	
				}else{
					Temp2=t+"/01/1900";				
				}
				String TempArr1[]=timeValue.split(":");
				Temp3 = (TempArr1[0].trim()+ ":" + TempArr1[1].trim());
			}
			return (Temp2+" "+Temp3);
		}
		String TimeDateModifierNormal(String dateValue,String timeValue){
			String Temp1="";
			String Temp2="";
			String Temp3="";
			if(!"-1".equalsIgnoreCase(dateValue) && !timeValue.equalsIgnoreCase("") && !timeValue.equalsIgnoreCase(" ") && !timeValue.equalsIgnoreCase(null)){
				if(dateValue.equalsIgnoreCase(CBD)){
					dateValue=CBD+" + 0";
				}
				try{
					String TempArr[]=dateValue.split("\\+");
					Temp1=TempArr[1].replaceAll("\\s+", "");
				}catch(Exception e){
					Temp1=dateValue.trim();
				}
				int t=0;
				t= Integer.parseInt(Temp1)+1;
				
				if(String.valueOf(t).length()== 1){
					Temp2="0"+t+"/01/1900";
				}else{
					Temp2="0"+t+"/01/1900";				
				}
				String TempArr1[]=timeValue.split(":");
				Temp3 = (TempArr1[0].trim()+ ":" + TempArr1[1].trim());
			}
			return (Temp2+" "+Temp3);
		}
		protected List<DataAcquisitionDynamicVb> getDynaQueryPopupResults(DataAcquisitionDynamicVb dObj,StringBuffer pendingQuery, StringBuffer approveQuery, 
				String whereNotExistsQuery,	String orderBy, Vector<Object> params){
			return getDynaQueryPopupResults(dObj, pendingQuery, approveQuery, whereNotExistsQuery, orderBy, params, getMapper());
		}
		protected List<DataAcquisitionDynamicVb> getDynaQueryPopupResults(DataAcquisitionDynamicVb dObj,StringBuffer pendingQuery, StringBuffer approveQuery, 
				String whereNotExistsQuery,	String orderBy, Vector<Object> params, RowMapper rowMapper){
			Object objParams[]=null;
			int Ctr = 0;
			int Ctr2 = 0;
			List<DataAcquisitionDynamicVb> result;
			// Now the params are ready with the values.Create an object array and insert the values into it
			if(dObj.isVerificationRequired() && dObj.getRecordIndicator() != 0){
				objParams = new Object[params.size()*2];
			}else{
				objParams = new Object[params.size()];
			}

			for(Ctr=0; Ctr < params.size(); Ctr++)
				objParams[Ctr] = (Object) params.elementAt(Ctr);
			
			pendingQuery.append(orderBy);
			
			Paginationhelper<DataAcquisitionDynamicVb> paginationhelper = new Paginationhelper<DataAcquisitionDynamicVb>(); 
			if(dObj.isVerificationRequired() && dObj.getRecordIndicator() != 0){
				//Same set of parameters are needed for the Pending table query too
				//So add another set of similar values to the objects array
				for(Ctr2=0 ; Ctr2 < params.size(); Ctr2++, Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr2);
				if(whereNotExistsQuery != null && !whereNotExistsQuery.isEmpty() && approveQuery != null)
					CommonUtils.addToQuery(whereNotExistsQuery, approveQuery);
				String query = "";
				if(approveQuery == null || pendingQuery == null){
					if(approveQuery == null){
						query = pendingQuery.toString();
					}else{
						query = approveQuery.toString();
					}
				}else{
					query = approveQuery.toString() + " Union All  " + pendingQuery.toString();
				}
				if(dObj.getTotalRows()  <= 0){
					result = paginationhelper.fetchPage(getJdbcTemplate(), query, 
							objParams, dObj.getCurrentPage(), dObj.getMaxRecords(), rowMapper == null ? getMapper(): rowMapper);
					dObj.setTotalRows(paginationhelper.getTotalRows());
				}else{
					result = paginationhelper.fetchPage(getJdbcTemplate(), query, 
							objParams, dObj.getCurrentPage(), dObj.getMaxRecords(), dObj.getTotalRows(), rowMapper == null ? getMapper(): rowMapper); 
				}
			}else{
				if(dObj.getTotalRows()  <= 0){
					result = paginationhelper.fetchPage(getJdbcTemplate(), approveQuery.toString()+orderBy, 
							objParams, dObj.getCurrentPage(), dObj.getMaxRecords(), rowMapper == null ? getMapper(): rowMapper);
					dObj.setTotalRows(paginationhelper.getTotalRows());
				}else{
					result = paginationhelper.fetchPage(getJdbcTemplate(), approveQuery.toString()+orderBy, 
							objParams, dObj.getCurrentPage(), dObj.getMaxRecords(), dObj.getTotalRows(), rowMapper == null ? getMapper(): rowMapper); 
				}
			}
			return result;
		}
public List<DataAcquisitionDynamicVb> getHashVariableQuery(DataAcquisitionDynamicVb dObj){
 final int intKeyFieldsCount = 1;
 List<DataAcquisitionDynamicVb> collTemp = null;
 
  String strQueryAppr = new String("Select VARIABLE_NAME, SCRIPT_TYPE_AT,"+
			"SCRIPT_TYPE,VARIABLE_SCRIPT,MAKER,DATE_LAST_MODIFIED,DATE_CREATION FROM VISION_DYNAMIC_HASH_VAR Where VARIABLE_NAME = ? ");
  
			Object objParams[] = new Object[intKeyFieldsCount];
			objParams[0] = new String(dObj.getPopDbVarName().toUpperCase());
			try
			{
			   collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getHashMapper());	
			   
				return collTemp;
			}catch(Exception ex){
				ex.printStackTrace();
				logger.error("Error: getQueryResults Exception :   ");
				return null;
			}
	}

public ExceptionCode doInsertUpdateRecordForDbpopup(DataAcquisitionDynamicVb vObject) throws RuntimeCustomException{
	List<DataAcquisitionDynamicVb> collTemp = null;
	ExceptionCode exceptionCode = null;
	DataAcquisitionDynamicVb dataDynamicVb = new DataAcquisitionDynamicVb();
	setServiceDefaults1();
	vObject.setMaker(getIntCurrentUserId());
	collTemp = getHashVariableQuery(vObject);
	if (collTemp == null){
		logger.error("Collection is null for Select Approved Record");
		exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
		throw buildRuntimeCustomException(exceptionCode);
	}
	if (collTemp.size() > 0){
		dataDynamicVb = collTemp.get(0);
		dataDynamicVb.setAuditDbPop(vObject.isAuditDbPop());
	}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		if(collTemp.size() > 0)
		vObject.setDateCreation(dataDynamicVb.getDateCreation());
		else
		vObject.setDateCreation(systemDate);
		try {
			exceptionCode = doDbInsertUpdateToDynVarTable(vObject,dataDynamicVb);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exceptionCode;
  }
}