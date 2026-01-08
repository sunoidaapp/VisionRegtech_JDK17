package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateMappingVb;
import com.vision.vb.VisionUsersVb;

@Component
public class TemplateConfigHeaderDao extends AbstractDao<TemplateConfigVb> {

	@Autowired
	CommonDao commonDao;
	@Autowired
	TemplateMappingDao templateMappingDao;
	@Value("${app.databaseType}")
	private String databaseType;

	@Override
	protected void setServiceDefaults() {
		serviceName = "TemplateConfigHeader";
		serviceDesc = "Template Config Header";
		tableName = "RG_TEMPLATE_CONFIG";
		childTableName = "RG_TEMPLATE_CONFIG";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	String templateStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.TEMPLATE_STATUS",
			"TEMPLATE_STATUS_DESC");
	String templateStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.TEMPLATE_STATUS",
			"TEMPLATE_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateConfigVb vObject = new TemplateConfigVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setTemplateDescription(rs.getString("TEMPLATE_DESCRIPTION"));
				vObject.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
				vObject.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
				vObject.setTypeOfSubmission(rs.getString("TYPE_OF_SUBMISSION"));
				vObject.setCsvDelimiter(rs.getString("CSV_DELIMITER"));
				vObject.setSourceType(rs.getString("SOURCE_TYPE"));
				vObject.setDbConnectivityType(rs.getString("DB_CONNECTIVITY_TYPE"));
				vObject.setDbConnectivityDetails(rs.getString("DB_CONNECTIVITY_DETAILS"));
				vObject.setSourceTable(rs.getString("SOURCE_TABLE"));
				vObject.setSourceTableFilter(rs.getString("SOURCE_TABLE_FILTER"));
				vObject.setSoureTableReadiness(rs.getString("SOURCE_TABLE_READINESS"));
				vObject.setUploadFileName(rs.getString("UPLOAD_FILE_NAME"));
				vObject.setApiConnectivityType(rs.getString("API_CONNECTIVITY_TYPE"));
				vObject.setApiConnectivityDetails(rs.getString("API_CONNECTIVITY_DETAILS"));
				vObject.setAuthConnectivityType(rs.getString("AUTH_CONNECTIVITY_TYPE"));
				vObject.setAuthConnectivityDetails(rs.getString("AUTH_CONNECTIVITY_DETAILS"));
				vObject.setMainAPIStructure(rs.getString("MAIN_API_STRUCTURE"));
				vObject.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				vObject.setTemplateStatusDesc(rs.getString("TEMPLATE_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDataList(rs.getString("DATALIST"));
				vObject.setSubmissionTime(rs.getString("RG_SUBMISSION_TIME"));
				vObject.setCbkFileName(rs.getString("CBK_FILE_NAME"));
				vObject.setCategoryType(rs.getString("CATEGORY_TYPE"));
				vObject.setCategoryTypeDesc(rs.getString("CATEGORY_TYPE_DESC"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				return vObject;
			}
		};
		return mapper;
	}

	protected void setStatus(TemplateConfigVb vObject, int status) {
		vObject.setTemplateStatus(status);
	}

	protected int getStatus(TemplateConfigVb records) {
		return records.getTemplateStatus();
	}

	protected int doInsertionAppr(TemplateConfigVb vObject) {
	

		String query = " INSERT INTO RG_TEMPLATE_CONFIG (COUNTRY,LE_BOOK,TEMPLATE_ID,TEMPLATE_DESCRIPTION,PROCESS_FREQUENCY_AT,PROCESS_FREQUENCY"
				+ " ,TYPE_OF_SUBMISSION_AT,TYPE_OF_SUBMISSION,CSV_DELIMITER_AT,CSV_DELIMITER,SOURCE_TYPE_AT"
				+ " ,SOURCE_TYPE,DB_CONNECTIVITY_TYPE_AT,DB_CONNECTIVITY_TYPE,DB_CONNECTIVITY_DETAILS,SOURCE_TABLE,SOURCE_TABLE_FILTER"
				+ " ,SOURCE_TABLE_READINESS,UPLOAD_FILE_NAME,API_CONNECTIVITY_TYPE_AT,API_CONNECTIVITY_TYPE,API_CONNECTIVITY_DETAILS"
				+ " ,AUTH_CONNECTIVITY_TYPE_AT,AUTH_CONNECTIVITY_TYPE,AUTH_CONNECTIVITY_DETAILS,MAIN_API_STRUCTURE,TEMPLATE_STATUS_NT"
				+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,RG_SUBMISSION_TIME,"
				+ "AUTO_SUBMIT,DATALIST,CATEGORY_TYPE,CBK_FILE_NAME,INTERNAL_STATUS )"
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ commonDao.getDbFunction("SYSDATE") + "," + commonDao.getDbFunction("SYSDATE") + ",?,?,?,?,?,?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),
				vObject.getTemplateDescription(), vObject.getProcessFrequencyAt(), vObject.getProcessFrequency(),
				vObject.getTypeOfSubmissionAt(), vObject.getTypeOfSubmission(), vObject.getCsvDelimiterAt(),
				vObject.getCsvDelimiter(), vObject.getSourceTypeAt(), vObject.getSourceType(),
				vObject.getDbConnectivityTypeAt(), vObject.getDbConnectivityType(), vObject.getDbConnectivityDetails(),
				vObject.getSourceTable(), vObject.getSourceTableFilter(), vObject.getSoureTableReadiness(),
				vObject.getUploadFileName(), vObject.getApiConnectivityTypeAt(), vObject.getApiConnectivityType(),
				vObject.getApiConnectivityDetails(), vObject.getAuthConnectivityTypeAt(),
				vObject.getAuthConnectivityType(), vObject.getAuthConnectivityDetails(), vObject.getMainAPIStructure(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getSubmissionTime(),
				vObject.getAutoSubmit(), vObject.getDataList(),vObject.getCategoryType(),vObject.getCbkFileName(),vObject.getInternalStatus()};

		return getJdbcTemplate().update(query, args);

	}

	protected int doInsertionPend(TemplateConfigVb vObject) {
		
		String query = " INSERT INTO RG_TEMPLATE_CONFIG_PEND (COUNTRY,LE_BOOK,TEMPLATE_ID,TEMPLATE_DESCRIPTION,PROCESS_FREQUENCY_AT,PROCESS_FREQUENCY"
				+ " ,TYPE_OF_SUBMISSION_AT,TYPE_OF_SUBMISSION,CSV_DELIMITER_AT,CSV_DELIMITER,SOURCE_TYPE_AT"
				+ " ,SOURCE_TYPE,DB_CONNECTIVITY_TYPE_AT,DB_CONNECTIVITY_TYPE,DB_CONNECTIVITY_DETAILS,SOURCE_TABLE,SOURCE_TABLE_FILTER"
				+ " ,SOURCE_TABLE_READINESS,UPLOAD_FILE_NAME,API_CONNECTIVITY_TYPE_AT,API_CONNECTIVITY_TYPE,API_CONNECTIVITY_DETAILS"
				+ " ,AUTH_CONNECTIVITY_TYPE_AT,AUTH_CONNECTIVITY_TYPE,AUTH_CONNECTIVITY_DETAILS,MAIN_API_STRUCTURE,TEMPLATE_STATUS_NT"
				+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,RG_SUBMISSION_TIME,AUTO_SUBMIT,DATALIST,CATEGORY_TYPE,CBK_FILE_NAME,INTERNAL_STATUS )"
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ commonDao.getDbFunction("SYSDATE") + "," + commonDao.getDbFunction("SYSDATE") + ",?,?,?,?,?,?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),
				vObject.getTemplateDescription(), vObject.getProcessFrequencyAt(), vObject.getProcessFrequency(),
				vObject.getTypeOfSubmissionAt(), vObject.getTypeOfSubmission(), vObject.getCsvDelimiterAt(),
				vObject.getCsvDelimiter(), vObject.getSourceTypeAt(), vObject.getSourceType(),
				vObject.getDbConnectivityTypeAt(), vObject.getDbConnectivityType(), vObject.getDbConnectivityDetails(),
				vObject.getSourceTable(), vObject.getSourceTableFilter(), vObject.getSoureTableReadiness(),
				vObject.getUploadFileName(), vObject.getApiConnectivityTypeAt(), vObject.getApiConnectivityType(),
				vObject.getApiConnectivityDetails(), vObject.getAuthConnectivityTypeAt(),
				vObject.getAuthConnectivityType(), vObject.getAuthConnectivityDetails(), vObject.getMainAPIStructure(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getSubmissionTime(),
				vObject.getAutoSubmit(), vObject.getDataList(),vObject.getCategoryType(),vObject.getCbkFileName(),vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPendTemplateConfigHeadersDc(TemplateConfigVb vObject) {
		String query = "";
		
			query = " INSERT INTO RG_TEMPLATE_CONFIG_PEND (COUNTRY,LE_BOOK,TEMPLATE_ID,TEMPLATE_DESCRIPTION,PROCESS_FREQUENCY_AT,PROCESS_FREQUENCY"
					+ " ,TYPE_OF_SUBMISSION_AT,TYPE_OF_SUBMISSION,CSV_DELIMITER_AT,CSV_DELIMITER,SOURCE_TYPE_AT"
					+ " ,SOURCE_TYPE,DB_CONNECTIVITY_TYPE_AT,DB_CONNECTIVITY_TYPE,DB_CONNECTIVITY_DETAILS,SOURCE_TABLE,SOURCE_TABLE_FILTER"
					+ " ,SOURCE_TABLE_READINESS,UPLOAD_FILE_NAME,API_CONNECTIVITY_TYPE_AT,API_CONNECTIVITY_TYPE,API_CONNECTIVITY_DETAILS"
					+ " ,AUTH_CONNECTIVITY_TYPE_AT,AUTH_CONNECTIVITY_TYPE,AUTH_CONNECTIVITY_DETAILS,MAIN_API_STRUCTURE,TEMPLATE_STATUS_NT"
					+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,RG_SUBMISSION_TIME,AUTO_SUBMIT,DATALIST,CATEGORY_TYPE,CBK_FILE_NAME,INTERNAL_STATUS )"
					+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
					+ commonDao.getDbFunction("SYSDATE") + ", "+dateTimeConvert+",?,?,?,?,?,?)";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),
				vObject.getTemplateDescription(), vObject.getProcessFrequencyAt(), vObject.getProcessFrequency(),
				vObject.getTypeOfSubmissionAt(), vObject.getTypeOfSubmission(), vObject.getCsvDelimiterAt(),
				vObject.getCsvDelimiter(), vObject.getSourceTypeAt(), vObject.getSourceType(),
				vObject.getDbConnectivityTypeAt(), vObject.getDbConnectivityType(), vObject.getDbConnectivityDetails(),
				vObject.getSourceTable(), vObject.getSourceTableFilter(), vObject.getSoureTableReadiness(),
				vObject.getUploadFileName(), vObject.getApiConnectivityTypeAt(), vObject.getApiConnectivityType(),
				vObject.getApiConnectivityDetails(), vObject.getAuthConnectivityTypeAt(),
				vObject.getAuthConnectivityType(), vObject.getAuthConnectivityDetails(), vObject.getMainAPIStructure(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getDateCreation(),
				vObject.getSubmissionTime(), vObject.getAutoSubmit(), vObject.getDataList(),vObject.getCategoryType(),vObject.getCbkFileName() ,vObject.getInternalStatus()};

		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdateAppr(TemplateConfigVb vObject) {
		
		String query = " 		 UPDATE RG_TEMPLATE_CONFIG SET INTERNAL_STATUS = ?,TEMPLATE_DESCRIPTION = ?                                                         "
				+ "  ,PROCESS_FREQUENCY_AT = ?,PROCESS_FREQUENCY =?                                                                 "
				+ "  ,TYPE_OF_SUBMISSION_AT = ?,TYPE_OF_SUBMISSION = ?,CSV_DELIMITER_AT = ?,CSV_DELIMITER = ?,SOURCE_TYPE_AT = ?    "
				+ "  ,SOURCE_TYPE = ?,DB_CONNECTIVITY_TYPE_AT =?,DB_CONNECTIVITY_TYPE = ?,DB_CONNECTIVITY_DETAILS = ?,SOURCE_TABLE = ?,SOURCE_TABLE_FILTER = ? "
				+ "  ,SOURCE_TABLE_READINESS = ?,UPLOAD_FILE_NAME = ?,API_CONNECTIVITY_TYPE_AT=?,API_CONNECTIVITY_TYPE = ?,API_CONNECTIVITY_DETAILS =?          "
				+ "  ,AUTH_CONNECTIVITY_TYPE_AT=?,AUTH_CONNECTIVITY_TYPE = ?,AUTH_CONNECTIVITY_DETAILS = ?,MAIN_API_STRUCTURE = ?,TEMPLATE_STATUS_NT =?         "
				+ "  ,TEMPLATE_STATUS = ?,RECORD_INDICATOR_NT = ?,RECORD_INDICATOR = ?,MAKER = ?,VERIFIER = ?                       "
				+ "  ,DATE_LAST_MODIFIED = " + commonDao.getDbFunction("SYSDATE")
				+ ",RG_SUBMISSION_TIME = ? ,AUTO_SUBMIT = ? ,DATALIST = ?,CATEGORY_TYPE = ? ,CBK_FILE_NAME = ?  "
				+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ?                                                      ";
		Object[] args = { vObject.getInternalStatus(),vObject.getTemplateDescription(), vObject.getProcessFrequencyAt(),
				vObject.getProcessFrequency(), vObject.getTypeOfSubmissionAt(), vObject.getTypeOfSubmission(),
				vObject.getCsvDelimiterAt(), vObject.getCsvDelimiter(), vObject.getSourceTypeAt(),
				vObject.getSourceType(), vObject.getDbConnectivityTypeAt(), vObject.getDbConnectivityType(),
				vObject.getDbConnectivityDetails(), vObject.getSourceTable(), vObject.getSourceTableFilter(),
				vObject.getSoureTableReadiness(), vObject.getUploadFileName(), vObject.getApiConnectivityTypeAt(),
				vObject.getApiConnectivityType(), vObject.getApiConnectivityDetails(),
				vObject.getAuthConnectivityTypeAt(), vObject.getAuthConnectivityType(),
				vObject.getAuthConnectivityDetails(), vObject.getMainAPIStructure(), vObject.getTemplateStatusNt(),
				vObject.getTemplateStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getSubmissionTime(), vObject.getAutoSubmit(),
				vObject.getDataList(),vObject.getCategoryType(),vObject.getCbkFileName(), vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doUpdatePendTemplateConfigHeader(TemplateConfigVb vObject) {
	
		String query = "  UPDATE RG_TEMPLATE_CONFIG_PEND SET INTERNAL_STATUS = ? TEMPLATE_DESCRIPTION = ?                                                         "
				+ "  ,PROCESS_FREQUENCY_AT = ?,PROCESS_FREQUENCY =?                                                                 "
				+ "  ,TYPE_OF_SUBMISSION_AT = ?,TYPE_OF_SUBMISSION = ?,CSV_DELIMITER_AT = ?,CSV_DELIMITER = ?,SOURCE_TYPE_AT = ?    "
				+ "  ,SOURCE_TYPE = ?,DB_CONNECTIVITY_TYPE_AT =?,DB_CONNECTIVITY_TYPE = ?,DB_CONNECTIVITY_DETAILS = ?,SOURCE_TABLE = ?,SOURCE_TABLE_FILTER = ? "
				+ "  ,SOURCE_TABLE_READINESS = ?,UPLOAD_FILE_NAME = ?,API_CONNECTIVITY_TYPE_AT=?,API_CONNECTIVITY_TYPE = ?,API_CONNECTIVITY_DETAILS =?          "
				+ "  ,AUTH_CONNECTIVITY_TYPE_AT=?,AUTH_CONNECTIVITY_TYPE = ?,AUTH_CONNECTIVITY_DETAILS = ?,MAIN_API_STRUCTURE = ?,TEMPLATE_STATUS_NT =?         "
				+ "  ,TEMPLATE_STATUS = ?,RECORD_INDICATOR_NT = ?,RECORD_INDICATOR = ?,MAKER = ?,VERIFIER = ?                       "
				+ "  ,DATE_LAST_MODIFIED = " + commonDao.getDbFunction("SYSDATE")
				+ ",RG_SUBMISSION_TIME = ? ,AUTO_SUBMIT = ? ,DATALIST = ? ,CATEGORY_TYPE = ?,CBK_FILE_NAME = ?"
				+ "  WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ?                                                           ";
		Object[] args = {vObject.getInternalStatus(), vObject.getTemplateDescription(), vObject.getProcessFrequencyAt(),
				vObject.getProcessFrequency(), vObject.getTypeOfSubmissionAt(), vObject.getTypeOfSubmission(),
				vObject.getCsvDelimiterAt(), vObject.getCsvDelimiter(), vObject.getSourceTypeAt(),
				vObject.getSourceType(), vObject.getDbConnectivityTypeAt(), vObject.getDbConnectivityType(),
				vObject.getDbConnectivityDetails(), vObject.getSourceTable(), vObject.getSourceTableFilter(),
				vObject.getSoureTableReadiness(), vObject.getUploadFileName(), vObject.getApiConnectivityTypeAt(),
				vObject.getApiConnectivityType(), vObject.getApiConnectivityDetails(),
				vObject.getAuthConnectivityTypeAt(), vObject.getAuthConnectivityType(),
				vObject.getAuthConnectivityDetails(), vObject.getMainAPIStructure(), vObject.getTemplateStatusNt(),
				vObject.getTemplateStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getSubmissionTime(), vObject.getAutoSubmit(),
				vObject.getDataList(),vObject.getCategoryType(),vObject.getCbkFileName()
				, vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doDeleteAppr(TemplateConfigVb vObject) {
		String query = "DELETE FROM RG_TEMPLATE_CONFIG  WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deletePendingRecord(TemplateConfigVb vObject) {
		String query = "DELETE FROM RG_TEMPLATE_CONFIG_PEND  WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId() };
		return getJdbcTemplate().update(query, args);
	}

	public List<TemplateConfigVb> getQueryPopupResults(TemplateConfigVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		strBufApprove = new StringBuffer("select * from(SELECT TAPPR.COUNTRY, " + " TAPPR.LE_BOOK, TAPPR.TEMPLATE_ID, "
				+ " TAPPR.TEMPLATE_DESCRIPTION,TAPPR.PROCESS_FREQUENCY_AT," + " TAPPR.PROCESS_FREQUENCY,"
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TAPPR.PROCESS_FREQUENCY AND ALPHA_TAB =TAPPR.PROCESS_FREQUENCY_AT)PROCESS_FREQUENCY_DESC,"
				+ " TAPPR.TYPE_OF_SUBMISSION_AT," + " TAPPR.TYPE_OF_SUBMISSION,TAPPR.CSV_DELIMITER_AT,"
				+ " TAPPR.CSV_DELIMITER, TAPPR.SOURCE_TYPE_AT," + " TAPPR.SOURCE_TYPE, TAPPR.DB_CONNECTIVITY_TYPE,"
				+ " TAPPR.DB_CONNECTIVITY_DETAILS,TAPPR.SOURCE_TABLE,"
				+ " TAPPR.SOURCE_TABLE_FILTER,TAPPR.SOURCE_TABLE_READINESS,"
				+ " TAPPR.UPLOAD_FILE_NAME, TAPPR.API_CONNECTIVITY_TYPE,"
				+ " TAPPR.API_CONNECTIVITY_DETAILS,TAPPR.AUTH_CONNECTIVITY_TYPE,"
				+ " TAPPR.AUTH_CONNECTIVITY_DETAILS,TAPPR.MAIN_API_STRUCTURE," + " TAPPR.TEMPLATE_STATUS,"
				+ templateStatusNtApprDesc + " ,TAPPR.RECORD_INDICATOR_NT," + " TAPPR.RECORD_INDICATOR,"
				+ RecordIndicatorNtApprDesc + " ,TAPPR.MAKER,"
				+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TAppr.MAKER,0) ) MAKER_NAME," + "TAPPR.VERIFIER,"
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TAppr.VERIFIER,0) ) VERIFIER_NAME," + " "
				+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED,TAPPR.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
				+  " TAPPR.RG_SUBMISSION_TIME,DATALIST,TAPPR.CATEGORY_TYPE,TAPPR.CBK_FILE_NAME, "
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TAPPR.CATEGORY_TYPE AND ALPHA_TAB =TAPPR.CATEGORY_TYPE_AT)CATEGORY_TYPE_DESC ,TAPPR.INTERNAL_STATUS "
				+ " FROM RG_TEMPLATE_CONFIG TAPPR)Tappr");

		strWhereNotExists = new String(
				" NOT EXISTS (SELECT 'X' FROM RG_TEMPLATE_CONFIG_PEND TPEND WHERE TAPPR.COUNTRY = TPEND.COUNTRY "
						+ " AND TAPPR.LE_BOOK = TPEND.LE_BOOK" + " AND TAPPR.TEMPLATE_ID =TPEND.TEMPLATE_ID )");

		strBufPending = new StringBuffer("select * from (SELECT TPEND.COUNTRY, " + " TPEND.LE_BOOK, TPEND.TEMPLATE_ID, "
				+ " TPEND.TEMPLATE_DESCRIPTION,TPEND.PROCESS_FREQUENCY_AT,"
				+ " TPEND.PROCESS_FREQUENCY,(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TPEND.PROCESS_FREQUENCY AND ALPHA_TAB =TPEND.PROCESS_FREQUENCY_AT)PROCESS_FREQUENCY_DESC, "
				+ "TPEND.TYPE_OF_SUBMISSION_AT," + " TPEND.TYPE_OF_SUBMISSION,TPEND.CSV_DELIMITER_AT,"
				+ " TPEND.CSV_DELIMITER, TPEND.SOURCE_TYPE_AT," + " TPEND.SOURCE_TYPE, TPEND.DB_CONNECTIVITY_TYPE,"
				+ " TPEND.DB_CONNECTIVITY_DETAILS,TPEND.SOURCE_TABLE,"
				+ " TPEND.SOURCE_TABLE_FILTER,TPEND.SOURCE_TABLE_READINESS,"
				+ " TPEND.UPLOAD_FILE_NAME, TPEND.API_CONNECTIVITY_TYPE,"
				+ " TPEND.API_CONNECTIVITY_DETAILS,TPEND.AUTH_CONNECTIVITY_TYPE,"
				+ " TPEND.AUTH_CONNECTIVITY_DETAILS,TPEND.MAIN_API_STRUCTURE," + " TPEND.TEMPLATE_STATUS,"
				+ templateStatusNtPendDesc + " ,TPEND.RECORD_INDICATOR_NT," + " TPEND.RECORD_INDICATOR,"
				+ RecordIndicatorNtPendDesc + " ,TPEND.MAKER,"
				+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TPEND.MAKER,0) ) MAKER_NAME," + "TPEND.VERIFIER,"
				+ "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
				+ "(TPEND.VERIFIER,0) ) VERIFIER_NAME," + " "
				+ dbFunctionFormats("TPEND.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
				+ " DATE_LAST_MODIFIED,TPEND.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
				+ dbFunctionFormats("TPEND.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION, "
				+ " TPEND.RG_SUBMISSION_TIME,DATALIST,TPEND.CATEGORY_TYPE,TPEND.CBK_FILE_NAME,"
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TPEND.CATEGORY_TYPE AND ALPHA_TAB =TPEND.CATEGORY_TYPE_AT)CATEGORY_TYPE_DESC,TPEND.INTERNAL_STATUS "
				+ " FROM RG_TEMPLATE_CONFIG_PEND TPEND)tpend");
		

		try {

			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
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
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.COUNTRY) " + val, strBufPending, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.LE_BOOK) " + val, strBufPending, data.getJoinType());
						break;

					case "templateId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TEMPLATE_ID) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.TEMPLATE_ID) " + val, strBufPending,
								data.getJoinType());
						break;

					case "templateDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TEMPLATE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.TEMPLATE_DESCRIPTION" + ") " + val, strBufPending,
								data.getJoinType());
						break;

					case "templateStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TEMPLATE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.TEMPLATE_STATUS_DESC ) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "dateCreation":
						CommonUtils.addToQuerySearch(
								" " + commonDao.getDbFunction("DATEFUNC") + "(TAPPR.DATE_CREATION,'DD-MM-YYYY "
										+ commonDao.getDbFunction("TIME") + "') " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(
								" " + commonDao.getDbFunction("DATEFUNC") + "(TPEND.DATE_CREATION,'DD-MM-YYYY "
										+ commonDao.getDbFunction("TIME") + "')  " + val,
								strBufPending, data.getJoinType());
						break;

					case "dateLastModified":
						CommonUtils.addToQuerySearch(
								" " + commonDao.getDbFunction("DATEFUNC") + "(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "
										+ commonDao.getDbFunction("TIME") + "') " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(
								" " + commonDao.getDbFunction("DATEFUNC") + "(TPEND.DATE_LAST_MODIFIED,'DD-MM-YYYY "
										+ commonDao.getDbFunction("TIME") + "') " + val,
								strBufPending, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" (TAPPR.MAKER_NAME) IN (" + val + ") ", strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" (TPEND.MAKER_NAME) IN (" + val + ") ", strBufPending,
								data.getJoinType());
						break;
					case "processFrequencyDesc":

						CommonUtils.addToQuerySearch(" upper(TAPPR.PROCESS_FREQUENCY_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.PROCESS_FREQUENCY_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "categoryType":

						CommonUtils.addToQuerySearch(" upper(TAPPR.CATEGORY_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.CATEGORY_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if (("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))) {
				if (ValidationUtil.isValid(visionUsersVb.getCountry())) {
					CommonUtils.addToQuery(" COUNTRY IN (" + toSqlInList(visionUsersVb.getCountry()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN (" + toSqlInList(visionUsersVb.getCountry()) + ") ", strBufPending);
				}
	 
				if (ValidationUtil.isValid(visionUsersVb.getLeBook())) {
					CommonUtils.addToQuery(" LE_BOOK IN (" + toSqlInList(visionUsersVb.getLeBook()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN (" +toSqlInList(visionUsersVb.getLeBook()) + ") ", strBufPending);
				}
			} else {
				if (ValidationUtil.isValid(dObj.getCountry())) {
					CommonUtils.addToQuery(" TAppr.COUNTRY IN (" + toSqlInList(dObj.getCountry()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" TPend.COUNTRY IN (" + toSqlInList(dObj.getCountry()) + ") ", strBufPending);
				}
				if (ValidationUtil.isValid(dObj.getLeBook())) {
					CommonUtils.addToQuery(" TAppr.LE_BOOK IN (" + toSqlInList(dObj.getLeBook()) + ") ", strBufApprove);
					CommonUtils.addToQuery(" TPend.LE_BOOK IN (" + toSqlInList(dObj.getLeBook()) + ") ", strBufPending);
				}
			}
//			 orderBy = " Order By "+dbFunctionFormats("DATE_LAST_MODIFIED","TO_DATETIME_FORMAT",null)+" DESC";
			orderBy = " Order by DATE_LAST_MODIFIED_1 DESC";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((strBufApprove == null) ? "strBufApprove is Null" : strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending == null) ? "strBufPending is Null" : strBufPending.toString()));

			if (params != null)
				for (int i = 0; i < params.size(); i++)
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

	public List<TemplateConfigVb> getQueryResults(TemplateConfigVb dObj, int intStatus) {
		List<TemplateConfigVb> collTemp = null;
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(" SELECT TAPPR.COUNTRY,                        "
				+ "        TAPPR.LE_BOOK,TAPPR.TEMPLATE_ID, "
				+ "        TAPPR.TEMPLATE_DESCRIPTION,TAPPR.PROCESS_FREQUENCY_AT," + "        TAPPR.PROCESS_FREQUENCY,"
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TAPPR.PROCESS_FREQUENCY AND ALPHA_TAB =TAPPR.PROCESS_FREQUENCY_AT)PROCESS_FREQUENCY_DESC,"
				+ "        TAPPR.TYPE_OF_SUBMISSION_AT," + "        TAPPR.TYPE_OF_SUBMISSION,TAPPR.CSV_DELIMITER_AT,"
				+ "        TAPPR.CSV_DELIMITER,TAPPR.SOURCE_TYPE_AT,"
				+ "        TAPPR.SOURCE_TYPE,TAPPR.DB_CONNECTIVITY_TYPE,"
				+ "        TAPPR.DB_CONNECTIVITY_DETAILS,TAPPR.SOURCE_TABLE,"
				+ "        TAPPR.SOURCE_TABLE_FILTER,TAPPR.SOURCE_TABLE_READINESS,"
				+ "        TAPPR.UPLOAD_FILE_NAME,TAPPR.API_CONNECTIVITY_TYPE,"
				+ "        TAPPR.API_CONNECTIVITY_DETAILS,TAPPR.AUTH_CONNECTIVITY_TYPE,"
				+ "        TAPPR.AUTH_CONNECTIVITY_DETAILS,TAPPR.MAIN_API_STRUCTURE,"
				+ "        TAPPR.TEMPLATE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TEMPLATE_STATUS_DESC,"
				+ "        TAPPR.RECORD_INDICATOR_NT,"
				+ "        TAPPR.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ "        TAPPR.RECORD_INDICATOR,TAPPR.MAKER," + "        TAPPR.VERIFIER," + " "
				+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null) + " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
				+ "RG_SUBMISSION_TIME,DATALIST,AUTO_SUBMIT,TAPPR.CATEGORY_TYPE,TAPPR.CBK_FILE_NAME ,TAppr.INTERNAL_STATUS "
				+ " FROM RG_TEMPLATE_CONFIG TAPPR" + " ,NUM_SUB_TAB T3,NUM_SUB_TAB T4 Where "
				+ " t3.NUM_tab =  TAPPR.TEMPLATE_STATUS_NT AND t3.NUM_sub_tab = TAPPR.TEMPLATE_STATUS"
				+ " AND t4.NUM_tab = TAPPR.RECORD_INDICATOR_NT AND t4.NUM_sub_tab = TAPPR.RECORD_INDICATOR"
				+ " AND TAPPR.COUNTRY =? AND TAPPR.LE_BOOK = ? AND  TAPPR.TEMPLATE_ID = ?");
		strQueryPend = new String(" SELECT TPEND.COUNTRY,                        "
				+ "        TPEND.LE_BOOK,TPEND.TEMPLATE_ID, "
				+ "        TPEND.TEMPLATE_DESCRIPTION,TPEND.PROCESS_FREQUENCY_AT," + "        TPEND.PROCESS_FREQUENCY,"
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TPEND.PROCESS_FREQUENCY AND ALPHA_TAB =TPEND.PROCESS_FREQUENCY_AT)PROCESS_FREQUENCY_DESC,"
				+ "        TPEND.TYPE_OF_SUBMISSION_AT," + "        TPEND.TYPE_OF_SUBMISSION,TPEND.CSV_DELIMITER_AT,"
				+ "        TPEND.CSV_DELIMITER,TPEND.SOURCE_TYPE_AT,"
				+ "        TPEND.SOURCE_TYPE,TPEND.DB_CONNECTIVITY_TYPE,"
				+ "        TPEND.DB_CONNECTIVITY_DETAILS,TPEND.SOURCE_TABLE,"
				+ "        TPEND.SOURCE_TABLE_FILTER,TPEND.SOURCE_TABLE_READINESS,"
				+ "        TPEND.UPLOAD_FILE_NAME,TPEND.API_CONNECTIVITY_TYPE,"
				+ "        TPEND.API_CONNECTIVITY_DETAILS,TPEND.AUTH_CONNECTIVITY_TYPE,"
				+ "        TPEND.AUTH_CONNECTIVITY_DETAILS,TPEND.MAIN_API_STRUCTURE,"
				+ "        TPEND.TEMPLATE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TEMPLATE_STATUS_DESC,"
				+ "        TPEND.RECORD_INDICATOR_NT,"
				+ "        TPEND.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ "        TPEND.RECORD_INDICATOR,TPEND.MAKER," + "        TPEND.VERIFIER," + " "
				+ dbFunctionFormats("TPEND.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null) + " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TPEND.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
				+ "RG_SUBMISSION_TIME,DATALIST,AUTO_SUBMIT,TPEND.CATEGORY_TYPE,TPEND.CBK_FILE_NAME ,TPEND.INTERNAL_STATUS "
				+ " FROM RG_TEMPLATE_CONFIG_PEND TPEND" + " ,NUM_SUB_TAB T3,NUM_SUB_TAB T4 Where "
				+ " t3.NUM_tab =  TPEND.TEMPLATE_STATUS_NT AND t3.NUM_sub_tab = TPEND.TEMPLATE_STATUS"
				+ " AND t4.NUM_tab = TPEND.RECORD_INDICATOR_NT AND t4.NUM_sub_tab = TPEND.RECORD_INDICATOR"
				+ " AND TPEND.COUNTRY =? AND TPEND.LE_BOOK = ? AND  TPEND.TEMPLATE_ID = ?");

		Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId() };

		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), args, getDetailMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), args, getDetailMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (args != null)
				for (int i = 0; i < args.length; i++)
					logger.error("objParams[" + i + "]" + args[i].toString());
			return null;
		}
	}

	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateConfigVb vObject = new TemplateConfigVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setTemplateDescription(rs.getString("TEMPLATE_DESCRIPTION"));
				vObject.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
				vObject.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
				vObject.setTypeOfSubmission(rs.getString("TYPE_OF_SUBMISSION"));
				vObject.setCsvDelimiter(rs.getString("CSV_DELIMITER"));
				vObject.setSourceType(rs.getString("SOURCE_TYPE"));
				vObject.setDbConnectivityType(rs.getString("DB_CONNECTIVITY_TYPE"));
				vObject.setDbConnectivityDetails(rs.getString("DB_CONNECTIVITY_DETAILS"));
				vObject.setSourceTable(rs.getString("SOURCE_TABLE"));
				vObject.setSourceTableFilter(rs.getString("SOURCE_TABLE_FILTER"));
				vObject.setSoureTableReadiness(rs.getString("SOURCE_TABLE_READINESS"));
				vObject.setUploadFileName(rs.getString("UPLOAD_FILE_NAME"));
				vObject.setApiConnectivityType(rs.getString("API_CONNECTIVITY_TYPE"));
				vObject.setApiConnectivityDetails(rs.getString("API_CONNECTIVITY_DETAILS"));
				vObject.setAuthConnectivityType(rs.getString("AUTH_CONNECTIVITY_TYPE"));
				vObject.setAuthConnectivityDetails(rs.getString("AUTH_CONNECTIVITY_DETAILS"));
				vObject.setMainAPIStructure(rs.getString("MAIN_API_STRUCTURE"));
				vObject.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				vObject.setTemplateStatusDesc(rs.getString("TEMPLATE_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDataList(rs.getString("DATALIST"));
				vObject.setSubmissionTime(rs.getString("RG_SUBMISSION_TIME"));
				vObject.setAutoSubmit(rs.getString("AUTO_SUBMIT"));
				vObject.setSubmissionTime(rs.getString("RG_SUBMISSION_TIME"));
				vObject.setCbkFileName(rs.getString("CBK_FILE_NAME"));
				vObject.setCategoryType(rs.getString("CATEGORY_TYPE"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				return vObject;
			}
		};
		return mapper;
	}

	protected List<TemplateConfigVb> selectApprovedRecord(TemplateConfigVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	protected List<TemplateConfigVb> doSelectPendingRecord(TemplateConfigVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	public ExceptionCode doInsertApprRecordForNonTrans(TemplateConfigVb vObject) throws RuntimeCustomException {
		List<TemplateConfigVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();

		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setTemplateStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		if (ValidationUtil.isValid(vObject.getSourceType()) && "DB".equalsIgnoreCase(vObject.getSourceType())) {
			vObject.setUploadFileName("NA");
		}
		if (ValidationUtil.isValid(vObject.getSourceType()) && "XL".equalsIgnoreCase(vObject.getSourceType())) {
			vObject.setDbConnectivityType("NA");
			vObject.setDbConnectivityDetails("NA");
			vObject.setSourceTable("NA");
			vObject.setSourceTableFilter("NA");
			vObject.setSoureTableReadiness("NA");
		}
		retVal = doInsertionAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getMappinglst() != null && vObject.getMappinglst().size() > 0) {
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				exceptionCode = templateMappingDao.doInsertApprRecordForNonTrans(templateMappingVb);
			}
		}
		exceptionCode = writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	public ExceptionCode doInsertRecordForNonTrans(TemplateConfigVb vObject) throws RuntimeCustomException {
		List<TemplateConfigVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();

		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			int activeStatus = collTemp.get(0).getTemplateStatus();
			if (activeStatus == Constants.STATUS_ZERO) {
				logger.error("!!");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && collTemp.size() > 0) {
			logger.error("!!");
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		vObject.setTemplateStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(0);
		if (ValidationUtil.isValid(vObject.getSourceType()) && "DB".equalsIgnoreCase(vObject.getSourceType())) {
			vObject.setUploadFileName("NA");
		}
		if (ValidationUtil.isValid(vObject.getSourceType()) && "XL".equalsIgnoreCase(vObject.getSourceType())) {
			vObject.setDbConnectivityType("NA");
			vObject.setDbConnectivityDetails("NA");
			vObject.setSourceTable("NA");
			vObject.setSourceTableFilter("NA");
			vObject.setSoureTableReadiness("NA");
		}
		retVal = doInsertionPend(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if (vObject.getMappinglst() != null && vObject.getMappinglst().size() > 0) {
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				exceptionCode = templateMappingDao.doInsertRecordForNonTrans(templateMappingVb);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public ExceptionCode doUpdateApprRecordForNonTrans(TemplateConfigVb vObject) throws RuntimeCustomException {
		List<TemplateConfigVb> collTemp = null;
		TemplateConfigVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		exceptionCode = templateMappingDao.doUpdateApprRecord(vObject.getMappinglst());
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	public ExceptionCode doUpdateRecordForNonTrans(TemplateConfigVb vObject) throws RuntimeCustomException {
		List<TemplateConfigVb> collTemp = null;
		TemplateConfigVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		if (ValidationUtil.isValid(vObject.getSourceType()) && "DB".equalsIgnoreCase(vObject.getSourceType())) {
			vObject.setUploadFileName("NA");
		}
		if (ValidationUtil.isValid(vObject.getSourceType()) && "XL".equalsIgnoreCase(vObject.getSourceType())) {
			vObject.setDbConnectivityType("NA");
			vObject.setDbConnectivityDetails("NA");
			vObject.setSourceTable("NA");
			vObject.setSourceTableFilter("NA");
			vObject.setSoureTableReadiness("NA");
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePendTemplateConfigHeader(vObject);
			} else {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePendTemplateConfigHeader(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = templateMappingDao.doUpdateRecord(vObject.getMappinglst());
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} else {
			collTemp = null;
			collTemp = selectApprovedRecord(vObject);

			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// Even if record is not there in Appr. table reject the record
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// This is required for Audit Trail.
			if (collTemp.size() > 0) {
				vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			retVal = doInsertionPendTemplateConfigHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = templateMappingDao.doUpdateRecord(vObject.getMappinglst());
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	protected ExceptionCode doDeleteApprRecordForNonTrans(TemplateConfigVb vObject) throws RuntimeCustomException {
		List<TemplateConfigVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		TemplateConfigVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<TemplateConfigVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if (vObject.isStaticDelete()) {
			vObjectlocal.setMaker(getIntCurrentUserId());
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
//			setStatus(vObject, Constants.PASSIVATE);
			setStatus(vObjectlocal, Constants.PASSIVATE);
			vObjectlocal.setVerifier(getIntCurrentUserId());
			vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
			retVal = doUpdateAppr(vObjectlocal);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
		} else {
			// delete the record from the Approve Table
			retVal = doDeleteAppr(vObject);
//			vObject.setRecordIndicator(-1);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);

		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (vObject.isStaticDelete()) {
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject, vObjectlocal);
		} else {
			exceptionCode = writeAuditLog(null, vObject);
			vObject.setRecordIndicator(-1);
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}

		List<TemplateMappingVb> collTempMapping = templateMappingDao.getQueryResults(vObject, Constants.STATUS_ZERO);
		if (collTempMapping != null && collTempMapping.size() > 0) {
			for (TemplateMappingVb templateMappingVb : collTempMapping) {
				templateMappingVb.setChecked(vObject.isChecked());
				exceptionCode = templateMappingDao.doDeleteApprRecordForNonTrans(templateMappingVb);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		return exceptionCode;
	}

	protected ExceptionCode doDeleteRecordForNonTrans(TemplateConfigVb vObject) throws RuntimeCustomException {
		TemplateConfigVb vObjectlocal = null;
		List<TemplateConfigVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);

		if (collTemp == null) {
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// check to see if the record already exists in the pending table
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return
		// back
		if (collTemp.size() > 0) {
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// insert the record into pending table with status 3 - deletion
		if (vObjectlocal == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
			vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		retVal = doInsertionPendTemplateConfigHeadersDc(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);

		List<TemplateMappingVb> collTempMapping = templateMappingDao.getQueryResults(vObject, 0);
		if (collTempMapping != null && collTempMapping.size() > 0) {
			for (TemplateMappingVb templateMappingVb : collTempMapping) {
				templateMappingVb.setChecked(vObject.isChecked());
				exceptionCode = templateMappingDao.doDeleteRecordForNonTrans(templateMappingVb);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	/*
	 * protected ExceptionCode doDeleteRecordForNonTrans(TemplateConfigVb vObject)
	 * throws RuntimeCustomException { TemplateConfigVb vObjectlocal = null;
	 * List<TemplateConfigVb> collTemp = null; ExceptionCode exceptionCode = null;
	 * strApproveOperation = Constants.DELETE; strErrorDesc = "";
	 * strCurrentOperation = Constants.DELETE; setServiceDefaults(); collTemp =
	 * selectApprovedRecord(vObject); List<TemplateMappingVb> collTempMapping =
	 * null;
	 * 
	 * if (collTemp == null) { logger.error("Collection is null"); exceptionCode =
	 * getResultObject(Constants.ERRONEOUS_OPERATION); throw
	 * buildRuntimeCustomException(exceptionCode); } // If record already exists in
	 * the approved table, reject the addition if (collTemp.size() > 0) {
	 * vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0); int
	 * intStaticDeletionFlag = getStatus(vObjectlocal); if (intStaticDeletionFlag ==
	 * Constants.PASSIVATE) { exceptionCode =
	 * getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD); throw
	 * buildRuntimeCustomException(exceptionCode); } } else { exceptionCode =
	 * getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD); throw
	 * buildRuntimeCustomException(exceptionCode); }
	 * 
	 * // check to see if the record already exists in the pending table collTemp =
	 * doSelectPendingRecord(vObject); if (collTemp == null) { exceptionCode =
	 * getResultObject(Constants.ERRONEOUS_OPERATION); throw
	 * buildRuntimeCustomException(exceptionCode); }
	 * 
	 * // If records are there, check for the status and decide what error to return
	 * // back if (collTemp.size() > 0) { exceptionCode =
	 * getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD); throw
	 * buildRuntimeCustomException(exceptionCode); }
	 * 
	 * // insert the record into pending table with status 3 - deletion if
	 * (vObjectlocal == null) { exceptionCode =
	 * getResultObject(Constants.ERRONEOUS_OPERATION); throw
	 * buildRuntimeCustomException(exceptionCode); } if (collTemp.size() > 0) {
	 * vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
	 * vObjectlocal.setDateCreation(vObject.getDateCreation()); } //
	 * vObjectlocal.setDateCreation(vObject.getDateCreation());
	 * vObjectlocal.setMaker(getIntCurrentUserId()); setStatus(vObjectlocal,
	 * Constants.PASSIVATE);
	 * vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
	 * vObjectlocal.setVerifier(0); retVal =
	 * doInsertionPendTemplateConfigHeadersDc(vObjectlocal); if (retVal !=
	 * Constants.SUCCESSFUL_OPERATION) { exceptionCode = getResultObject(retVal);
	 * throw buildRuntimeCustomException(exceptionCode); }
	 * vObject.setRecordIndicator(Constants.STATUS_DELETE); vObject.setVerifier(0);
	 * // exceptionCode=templateMappingDao.doDeleteRecordForNonTrans();
	 * collTempMapping = templateMappingDao.getQueryResults(vObject,
	 * Constants.SUCCESSFUL_OPERATION); if (collTempMapping != null &&
	 * collTempMapping.size() > 0) { for (TemplateMappingVb templateMappingVb :
	 * collTempMapping) { templateMappingVb.setChecked(vObject.isChecked());
	 * exceptionCode =
	 * templateMappingDao.doDeleteRecordForNonTrans(templateMappingVb); } if
	 * (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
	 * exceptionCode = getResultObject(retVal); throw
	 * buildRuntimeCustomException(exceptionCode); } } return
	 * getResultObject(Constants.SUCCESSFUL_OPERATION); }
	 */
	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode bulkApprove(List<TemplateConfigVb> vObjects, boolean staticDelete)
			throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.APPROVE;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		try {
			boolean foundFlag = false;
			for (TemplateConfigVb object : vObjects) {
				exceptionCode = doApproveRecord(object, staticDelete);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setResponse(vObjects);
					exceptionCode.setOtherInfo(vObjects);
				}
				foundFlag = true;
			}

			if (foundFlag == false) {
				logger.error("No Records To Approve");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				logger.error("Error in Bulk Approve. " + exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return exceptionCode;
		} catch (Exception ex) {
			logger.error("Error in Bulk Approve.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doBulkReject(List<TemplateConfigVb> vObjects) throws RuntimeCustomException {
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		try {
			boolean foundFlag = false;
			for (TemplateConfigVb object : vObjects) {
				foundFlag = true;
				exceptionCode = doRejectRecord(object);
			}
			if (foundFlag == false) {
				logger.error("No Records To Reject");
				exceptionCode = getResultObject(Constants.NO_RECORDS_TO_REJECT);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				logger.error("Error in Bulk Reject. " + exceptionCode.getErrorMsg());
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return exceptionCode;
		} catch (Exception ex) {
			logger.error("Error in Bulk Reject.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	public ExceptionCode doRejectRecord(TemplateConfigVb vObject) throws RuntimeCustomException {
		TemplateConfigVb templateConfigVb = null;
		List<TemplateConfigVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		if (vObject != null)
			vObject.setMaker(getIntCurrentUserId());
		try {
			if (vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3)
				vObject.setRecordIndicator(0);
			else
				vObject.setRecordIndicator(-1);
			// See if such a pending request exists in the pending table
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			templateConfigVb = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
			retVal = deletePendingRecord(vObject);

			List<TemplateMappingVb> collTempMapping = new ArrayList<>();
			if (vObject.getMappinglst() != null && !vObject.getMappinglst().isEmpty())
				collTempMapping = vObject.getMappinglst();
			else
				collTempMapping = templateMappingDao.getQueryResults(vObject, Constants.STATUS_PENDING);
			if (collTempMapping != null && collTempMapping.size() > 0) {
				for (TemplateMappingVb templateMappingVb : collTempMapping) {
					exceptionCode = templateMappingDao.doRejectRecord(templateMappingVb);
				}
			}
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Reject.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Override
	protected String getAuditString(TemplateConfigVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY" + auditDelimiterColVal + vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK" + auditDelimiterColVal + vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTemplateId()))
				strAudit.append("TEMPLATE_NAME" + auditDelimiterColVal + vObject.getTemplateId().trim());
			else
				strAudit.append("TEMPLATE_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTemplateDescription()))
				strAudit.append(
						"TEMPLATE_DESCRIPTION" + auditDelimiterColVal + vObject.getTemplateDescription().trim());
			else
				strAudit.append("TEMPLATE_DESCRIPTION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("PROCESS_FREQUENCY_AT" + auditDelimiterColVal + vObject.getProcessFrequencyAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProcessFrequency()))
				strAudit.append("PROCESS_FREQUENCY" + auditDelimiterColVal + vObject.getProcessFrequency().trim());
			else
				strAudit.append("PROCESS_FREQUENCY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("TYPE_OF_SUBMISSION_AT" + auditDelimiterColVal + vObject.getTypeOfSubmissionAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getTypeOfSubmission()))
				strAudit.append("TYPE_OF_SUBMISSION" + auditDelimiterColVal + vObject.getTypeOfSubmission().trim());
			else
				strAudit.append("TYPE_OF_SUBMISSION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("CSV_DELIMITER_AT" + auditDelimiterColVal + vObject.getCsvDelimiterAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCsvDelimiter()))
				strAudit.append("CSV_DELIMITER" + auditDelimiterColVal + vObject.getCsvDelimiter().trim());
			else
				strAudit.append("CSV_DELIMITER" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("SOURCE_TYPE_AT" + auditDelimiterColVal + vObject.getSourceTypeAt());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSourceType()))
				strAudit.append("SOURCE_TYPE" + auditDelimiterColVal + vObject.getSourceType().trim());
			else
				strAudit.append("SOURCE_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDbConnectivityType()))
				strAudit.append("DB_CONNECTIVITY_TYPE" + auditDelimiterColVal + vObject.getDbConnectivityType());
			else
				strAudit.append("DB_CONNECTIVITY_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDbConnectivityDetails()))
				strAudit.append("DB_CONNECTIVITY_DETAILS" + auditDelimiterColVal + vObject.getDbConnectivityDetails());
			else
				strAudit.append("DB_CONNECTIVITY_DETAILS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSourceTable()))
				strAudit.append("SOURCE_TABLE" + auditDelimiterColVal + vObject.getSourceTable());
			else
				strAudit.append("SOURCE_TABLE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSourceTableFilter()))
				strAudit.append("SOURCE_TABLE_FILTER" + auditDelimiterColVal + vObject.getSourceTableFilter());
			else
				strAudit.append("SOURCE_TABLE_FILTER" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getSoureTableReadiness()))
				strAudit.append("SOURCE_TABLE_READINESS" + auditDelimiterColVal + vObject.getSoureTableReadiness());
			else
				strAudit.append("SOURCE_TABLE_READINESS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getUploadFileName()))
				strAudit.append("UPLOAD_FILE_NAME" + auditDelimiterColVal + vObject.getUploadFileName());
			else
				strAudit.append("UPLOAD_FILE_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getApiConnectivityType()))
				strAudit.append("API_CONNECTIVITY_TYPE" + auditDelimiterColVal + vObject.getApiConnectivityType());
			else
				strAudit.append("API_CONNECTIVITY_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getApiConnectivityDetails()))
				strAudit.append(
						"API_CONNECTIVITY_DETAILS" + auditDelimiterColVal + vObject.getApiConnectivityDetails());
			else
				strAudit.append("API_CONNECTIVITY_DETAILS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getAuthConnectivityType()))
				strAudit.append("AUTH_CONNECTIVITY_TYPE" + auditDelimiterColVal + vObject.getAuthConnectivityType());
			else
				strAudit.append("AUTH_CONNECTIVITY_TYPE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getAuthConnectivityDetails()))
				strAudit.append(
						"AUTH_CONNECTIVITY_DETAILS" + auditDelimiterColVal + vObject.getAuthConnectivityDetails());
			else
				strAudit.append("AUTH_CONNECTIVITY_DETAILS" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getMainAPIStructure()))
				strAudit.append("MAIN_API_STRUCTURE" + auditDelimiterColVal + vObject.getMainAPIStructure());
			else
				strAudit.append("MAIN_API_STRUCTURE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("TEMPLATE_STATUS_NT" + auditDelimiterColVal + vObject.getTemplateStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("TEMPLATE_STATUS" + auditDelimiterColVal + vObject.getTemplateStatus());
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);

			if (vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);

			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);

			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);

			if (vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);
		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	public ExceptionCode doInsertUpdateRecordForDbpopup(TemplateConfigVb vObject) throws RuntimeCustomException {
		List<TemplateConfigVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		TemplateConfigVb templateConfigVb = new TemplateConfigVb();
		// setServiceDefaults1();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = getHashVariableQuery(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			templateConfigVb = collTemp.get(0);
			// templateConfigVb.setAuditDbPop(vObject.isAuditDbPop());
		} else {
			vObject.setNewConFlag("Y");
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		if (collTemp.size() > 0)
			vObject.setDateCreation(templateConfigVb.getDateCreation());
		else
			vObject.setDateCreation(systemDate);
		try {
			exceptionCode = doDbInsertUpdateToDynVarTable(vObject, templateConfigVb);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exceptionCode;
	}

	public List<TemplateConfigVb> getDataQuery(TemplateConfigVb vObject) throws DataAccessException {
		String macrovarType = "";
		String macrovarName = "";
		String sql = "";
		if ("FTP".equalsIgnoreCase(vObject.getConnectionType())) {
			macrovarType = vObject.getConnectivityOption();
			macrovarName = "CONNECTIVITY_DETAILS";
			if (!ValidationUtil.isValid(macrovarType))
				macrovarType = "VPNDIRECTFTP";
		} else if ("DB".equalsIgnoreCase(vObject.getConnectionType())) {
			macrovarType = vObject.getConnectivityOption();
			macrovarName = "DB_DETAILS";
			if (!ValidationUtil.isValid(macrovarType))
				macrovarType = "ORACLE";
		} else if ("SERVER".equalsIgnoreCase(vObject.getConnectionType())) {
			macrovarType = vObject.getConnectivityOption();
			macrovarName = "CONNECTIVITY_DETAILS";
			if (!ValidationUtil.isValid(macrovarType))
				macrovarType = "MACROVAR";
		}
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			sql = "Select MACROVAR_NAME,MACROVAR_TYPE,TAG_NAME,DISPLAY_NAME,MASKED_FLAG,ENCRYPTION,SUBSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),1,INSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),'#}')-1) Value "
					+ "From "
					+ "(SELECT X1.MACROVAR_NAME,X1.MACROVAR_TYPE,X1.TAG_NAME,X1.TAG_NO,X1.DISPLAY_NAME,X1.MASKED_FLAG,X1.ENCRYPTION, "
					+ "(SELECT Case When INSTR( ?,REPLACE((TAG_NAME),' ','_')) > 0 " + "      Then SUBSTR( ? , "
					+ "                INSTR( ? , REPLACE((TAG_NAME),' ','_') )+LENGTH(REPLACE((TAG_NAME),' ','_'))) "
					+ "                Else NULL End STR FROM DUAL) STR " + "FROM MACROVAR_TAGGING X1 "
					+ "WHERE X1.MACROVAR_NAME = ? " + "and X1.MACROVAR_TYPE = ?) ORDER BY TAG_NO";
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			;
			sql = "Select MACROVAR_NAME,MACROVAR_TYPE,TAG_NAME,DISPLAY_NAME,MASKED_FLAG,ENCRYPTION,  "
					+ " Substring(Substring(STR,CharIndex('$@!',STR,1)+3, Len(STR)),1,CharIndex('#}',  "
					+ " Substring(STR,CharIndex('$@!',STR,1)+3, Len(STR)))-1) Value   "
					+ " From   (SELECT X1.MACROVAR_NAME,X1.MACROVAR_TYPE,X1.TAG_NAME,X1.TAG_NO,X1.DISPLAY_NAME,X1.MASKED_FLAG,"
					+ " X1.ENCRYPTION,   (SELECT Case When CharIndex(REPLACE((TAG_NAME),' ','_'), ?) > 0      "
					+ " Then Substring(? ,  CharIndex(REPLACE((TAG_NAME),' ','_'), ?)+LEN(REPLACE((TAG_NAME),' ','_')), Len(?))  Else NULL End STR) STR "
					+ " FROM MACROVAR_TAGGING X1  WHERE X1.MACROVAR_NAME = ? and X1.MACROVAR_TYPE = ?) T1 ORDER BY TAG_NO";
		}
		Object[] lParams = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			lParams = new Object[5];
			lParams[0] = vObject.getConnectionScript();
			lParams[1] = vObject.getConnectionScript();
			lParams[2] = vObject.getConnectionScript();
			lParams[3] = macrovarName;
			lParams[4] = macrovarType;
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			lParams = new Object[6];
			lParams[0] = vObject.getConnectionScript();
			lParams[1] = vObject.getConnectionScript();
			lParams[2] = vObject.getConnectionScript();
			lParams[3] = vObject.getConnectionScript();
			lParams[4] = macrovarName;
			lParams[5] = macrovarType;
		}
		return getJdbcTemplate().query(sql, lParams, getDataMapper());
	}

	protected RowMapper getDataMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateConfigVb vObject = new TemplateConfigVb();
				vObject.setMacroName(rs.getString("MACROVAR_NAME"));
				vObject.setMacroType(rs.getString("MACROVAR_TYPE"));
				vObject.setTagName(rs.getString("TAG_NAME"));
				vObject.setTagValue(rs.getString("Value"));
				vObject.setDisplayName(rs.getString("DISPLAY_NAME"));
				vObject.setMasked(rs.getString("MASKED_FLAG"));
				vObject.setEncryption(rs.getString("ENCRYPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	public List<TemplateConfigVb> getHashVariableQuery(TemplateConfigVb dObj) {
		final int intKeyFieldsCount = 1;
		List<TemplateConfigVb> collTemp = null;

		String strQueryAppr = new String("Select VARIABLE_NAME, "
				+ "SCRIPT_TYPE,VARIABLE_SCRIPT,MAKER,DATE_LAST_MODIFIED,DATE_CREATION FROM VISION_DYNAMIC_HASH_VAR Where VARIABLE_NAME = ? ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getVariableName().toUpperCase());
		try {
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getHashMapper());

			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			return null;
		}
	}

	public RowMapper getHashMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateConfigVb vObject = new TemplateConfigVb();
				vObject.setVariableName(rs.getString("VARIABLE_NAME"));
				vObject.setDatabaseType(rs.getString("SCRIPT_TYPE"));
				vObject.setConnectionScript(rs.getString("VARIABLE_SCRIPT"));
				vObject.setMaker(rs.getLong("MAKER"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return vObject;
			}
		};
		return mapper;
	}

	public ExceptionCode doDbInsertUpdateToDynVarTable(TemplateConfigVb data, TemplateConfigVb QueryData)
			throws SQLException {
		int result = 0;
		ExceptionCode exceptionCode = new ExceptionCode();
		// setServiceDefaults1();

		try {
			String VarSript = data.getConnectionScript().replaceAll("\\'", "\\'\\'");
			if (data.getNewConFlag().equalsIgnoreCase("Y")) {
				String query = "Insert into VISION_DYNAMIC_HASH_VAR "
						+ " (VARIABLE_NAME, VARIABLE_TYPE_NT, VARIABLE_TYPE, SCRIPT_TYPE_AT, SCRIPT_TYPE, VARIABLE_SCRIPT, SORT_ORDER, VARIABLE_STATUS_NT, VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "
						+ "Values " + "('" + data.getVariableName().toUpperCase() + "', 1056, 2, 1083, 'MACROVAR', "
						+ "'" + data.getConnectionScript() + "', 1, 1, 0, 7,  " + "0, '" + intCurrentUserId + "', '"
						+ intCurrentUserId + "', 0, " + getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";
				result = getJdbcTemplate().update(query);
				strApproveOperation = Constants.ADD;
				// exceptionCode = writeAuditLog(data, null);
				exceptionCode.setErrorMsg("Success");
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				return exceptionCode;
			} else {
				String query = "UPDATE VISION_DYNAMIC_HASH_VAR SET VARIABLE_SCRIPT='" + VarSript + "', " + "MAKER='"
						+ intCurrentUserId + "', VERIFIER='" + intCurrentUserId + "', DATE_LAST_MODIFIED = "
						+ getDbFunction("SYSDATE") + "  WHERE UPPER(VARIABLE_NAME)=UPPER('"
						+ data.getVariableName().toUpperCase() + "') ";
				result = getJdbcTemplate().update(query);
				strApproveOperation = Constants.MODIFY;
				// exceptionCode = writeAuditLog(data,QueryData);
				exceptionCode.setErrorMsg("Success");
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				return exceptionCode;
			}
		} catch (CannotGetJdbcConnectionException e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}
	}

	public ExceptionCode doApproveRecord(TemplateConfigVb vObject, boolean staticDelete) throws RuntimeCustomException {
		TemplateConfigVb oldContents = null;
		TemplateConfigVb vObjectlocal = null;
		List<TemplateConfigVb> collTemp = null;
		List<TemplateMappingVb> collTempMapping = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
				exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<TemplateConfigVb>) collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<TemplateConfigVb>) collTemp).get(0);
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) { // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionAppr(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}

				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				vObject.setDateCreation(systemDate);
				strApproveOperation = Constants.ADD;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE) { // Modify authorization

				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}

				// If record already exists in the approved table, reject the addition
				if (collTemp.size() > 0) {
					// retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateAppr(vObjectlocal);
				}
				// Modify the existing contents of the record in Approved table
				if (retVal != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				// Set the current operation to write to audit log
				strApproveOperation = Constants.MODIFY;
			} else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) { // Delete authorization
				if (staticDelete) {
					// Update the existing record status in the Approved table to delete
					setStatus(vObjectlocal, Constants.PASSIVATE);
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					retVal = doUpdateAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					setStatus(vObject, Constants.PASSIVATE);
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);

				} else {
					// Delete the existing record from the Approved table
					retVal = doDeleteAppr(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
					String systemDate = getSystemDate();
					vObject.setDateLastModified(systemDate);
				}
				// Set the current operation to write to audit log
				strApproveOperation = Constants.DELETE;
			} else {
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Delete the record from the Pending table
			retVal = deletePendingRecord(vObjectlocal);

			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}

			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete) {
				exceptionCode = writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			} else
				exceptionCode = writeAuditLog(vObjectlocal, oldContents);

			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				if (vObject.getMappinglst() != null && !vObject.getMappinglst().isEmpty())
					collTempMapping = vObject.getMappinglst();
				else
					collTempMapping = templateMappingDao.getQueryResults(vObject, Constants.STATUS_PENDING);
				if (collTempMapping != null && collTempMapping.size() > 0) {
					for (TemplateMappingVb templateMappingVb : collTempMapping) {
						exceptionCode = templateMappingDao.doApproveRecord(templateMappingVb, staticDelete);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Approve.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
}
