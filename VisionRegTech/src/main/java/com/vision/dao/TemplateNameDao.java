package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.ReportStgVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TableColumnsVb;
import com.vision.vb.TemplateNameVb;

@Component
public class TemplateNameDao extends AbstractDao<TemplateNameVb> {
	private final String separator = "!|#";
	private static String owner = "";

	String templateStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.TEMPLATE_STATUS",
			"TEMPLATE_STATUS_DESC");
	String templateStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.TEMPLATE_STATUS",
			"TEMPLATE_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	String processAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1079, "TAppr.DEFAULT_ACQ_PROCESS_TYPE",
			"DEFAULT_ACQ_PROCESS_TYPE_DESC");
	String processAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1079, "TPend.DEFAULT_ACQ_PROCESS_TYPE",
			"DEFAULT_ACQ_PROCESS_TYPE_DESC");
	

	String feedAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 7601, "TAppr.FEED_CATEGORY",
			"FEED_FREQUENCY_DESC");
	String feedAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 7601, "TPend.FEED_CATEGORY",
			"FEED_FREQUENCY_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameVb templateNameVb = new TemplateNameVb();
				templateNameVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				if (rs.getString("GENERAL_DESCRIPTION") != null) {
					templateNameVb.setGeneralDescription(rs.getString("GENERAL_DESCRIPTION"));
				} else {
					templateNameVb.setGeneralDescription("");
				}
				if (rs.getString("FEED_CATEGORY") != null) {
					templateNameVb.setFeedCategory(rs.getString("FEED_CATEGORY"));
				} else {
					templateNameVb.setFeedCategory("");
				}
				if (rs.getString("PROGRAM") != null) {
					templateNameVb.setProgram(rs.getString("PROGRAM"));
				} else {
					templateNameVb.setProgram("");
				}
				if (rs.getString("PROCESS_SEQUENCE") != null) {
					templateNameVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				} else {
					templateNameVb.setProcessSequence("");
				}
				if (rs.getString("FILE_PATTERN") != null) {
					templateNameVb.setFilePattern(rs.getString("FILE_PATTERN"));
				} else {
					templateNameVb.setFilePattern("");
				}
				if (rs.getString("EXCEL_FILE_PATTERN") != null) {
					templateNameVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				} else {
					templateNameVb.setExcelFilePattern("");
				}
				if (rs.getString("EXCEL_TEMPLATE_ID") != null) {
					templateNameVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				} else {
					templateNameVb.setExcelTemplateId("");
				}
				if (rs.getString("CURR_VERSION_NO") != null) {
					templateNameVb.setCurrVersionNo(rs.getString("CURR_VERSION_NO"));
				} else {
					templateNameVb.setCurrVersionNo("");
				}
				templateNameVb.setDefaultAcquProcessTypeAT(rs.getInt("DEFAULT_ACQ_PROCESS_TYPE_AT"));
				if (rs.getString("DEFAULT_ACQ_PROCESS_TYPE") != null) {
					templateNameVb.setDefaultAcquProcessType(rs.getString("DEFAULT_ACQ_PROCESS_TYPE"));
					templateNameVb.setDefaultAcquProcessTypeDesc(rs.getString("DEFAULT_ACQ_PROCESS_TYPE_DESC"));
				} else {
					templateNameVb.setDefaultAcquProcessType("");
					templateNameVb.setDefaultAcquProcessTypeDesc("");
				}

				templateNameVb.setTemplateStatusNt(rs.getInt("TEMPLATE_STATUS_NT"));
				templateNameVb.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				templateNameVb.setStatusDesc(rs.getString("TEMPLATE_STATUS_DESC"));
				templateNameVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				templateNameVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateNameVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				templateNameVb.setMaker(rs.getLong("MAKER"));
				templateNameVb.setVerifier(rs.getLong("VERIFIER"));
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				templateNameVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				templateNameVb.setDateCreation(rs.getString("DATE_CREATION"));
				if (rs.getString("FEED_STG_NAME") != null) {
					templateNameVb.setFeedStgName(rs.getString("FEED_STG_NAME"));
				} else {
					templateNameVb.setFeedStgName("");
				}
				if (rs.getString("VIEW_NAME") != null) {
					templateNameVb.setViewName(rs.getString("VIEW_NAME"));
				} else {
					templateNameVb.setViewName("");
				}
				if (rs.getString("PROGRAM_DESC") != null) {
					templateNameVb.setProgramDesc(rs.getString("PROGRAM_DESC"));
				} else {
					templateNameVb.setProgramDesc("");
				}
				if (rs.getString("EFFECTIVE_DATE") != null) {
					templateNameVb.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				} else {
					templateNameVb.setEffectiveDate("");
				}
				templateNameVb.setMakerName(rs.getString("MAKER_NAME"));
				templateNameVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				templateNameVb.setFeedFrequencyDesc(rs.getString("FEED_FREQUENCY_DESC"));
				return templateNameVb;
			}
		};
		return mapper;
	}

	private RowMapper getQueryMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameVb templateNameVb = new TemplateNameVb();
				templateNameVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				if (rs.getString("GENERAL_DESCRIPTION") != null) {
					templateNameVb.setGeneralDescription(rs.getString("GENERAL_DESCRIPTION"));
				} else {
					templateNameVb.setGeneralDescription("");
				}
				if (rs.getString("FEED_CATEGORY") != null) {
					templateNameVb.setFeedCategory(rs.getString("FEED_CATEGORY"));
				} else {
					templateNameVb.setFeedCategory("");
				}
				if (rs.getString("PROGRAM") != null) {
					templateNameVb.setProgram(rs.getString("PROGRAM"));
				} else {
					templateNameVb.setProgram("");
				}
				templateNameVb.setDefaultAcquProcessTypeAT(rs.getInt("DEFAULT_ACQ_PROCESS_TYPE_AT"));
				templateNameVb.setDefaultAcquProcessType(rs.getString("DEFAULT_ACQ_PROCESS_TYPE"));
				templateNameVb.setDefaultAcquProcessTypeDesc(rs.getString("DEFAULT_ACQ_PROCESS_TYPE_DESC"));
				templateNameVb.setTemplateStatusNt(rs.getInt("TEMPLATE_STATUS_NT"));
				templateNameVb.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				templateNameVb.setDbStatus(rs.getInt("TEMPLATE_STATUS"));
				templateNameVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				templateNameVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateNameVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				templateNameVb.setStatusDesc(rs.getString("TEMPLATE_STATUS_DESC"));
				templateNameVb.setMaker(rs.getLong("MAKER"));
				templateNameVb.setVerifier(rs.getLong("VERIFIER"));
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				templateNameVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				templateNameVb.setDateCreation(rs.getString("DATE_CREATION"));
				if (rs.getString("FEED_STG_NAME") != null) {
					templateNameVb.setFeedStgName(rs.getString("FEED_STG_NAME"));
				} else {
					templateNameVb.setFeedStgName("");
				}
				if (rs.getString("VIEW_NAME") != null) {
					templateNameVb.setViewName(rs.getString("VIEW_NAME"));
				} else {
					templateNameVb.setViewName("");
				}
				if (rs.getString("PROGRAM_DESC") != null) {
					templateNameVb.setProgramDesc(rs.getString("PROGRAM_DESC"));
				} else {
					templateNameVb.setProgramDesc("");
				}
				if (rs.getString("PROCESS_SEQUENCE") != null) {
					templateNameVb.setProcessSequence(rs.getString("PROCESS_SEQUENCE"));
				} else {
					templateNameVb.setProcessSequence("");
				}
				if (rs.getString("FILE_PATTERN") != null) {
					templateNameVb.setFilePattern(rs.getString("FILE_PATTERN"));
				} else {
					templateNameVb.setFilePattern("");
				}
				if (rs.getString("EXCEL_FILE_PATTERN") != null) {
					templateNameVb.setExcelFilePattern(rs.getString("EXCEL_FILE_PATTERN"));
				} else {
					templateNameVb.setExcelFilePattern("");
				}
				if (rs.getString("EXCEL_TEMPLATE_ID") != null) {
					templateNameVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				} else {
					templateNameVb.setExcelTemplateId("");
				}
				if (rs.getString("CURR_VERSION_NO") != null) {
					templateNameVb.setCurrVersionNo(rs.getString("CURR_VERSION_NO"));
				} else {
					templateNameVb.setCurrVersionNo("");
				}
				templateNameVb.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				templateNameVb.setSubAdfNumber(rs.getInt("SUB_ADF_NUMBER"));
				return templateNameVb;
			}
		};
		return mapper;
	}

	private RowMapper getTemplateDesignMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameVb templateNameVb = new TemplateNameVb();
				templateNameVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				if (rs.getString("COLUMN_NAME") != null) {
					templateNameVb.setColumnName(rs.getString("COLUMN_NAME"));
				} else {
					templateNameVb.setColumnName("");
				}
				if (rs.getString("COLUMN_ID") != null) {
					templateNameVb.setColumnId(rs.getString("COLUMN_ID"));
				} else {
					templateNameVb.setColumnId("");
				}
				if (rs.getString("DATA_TYPE") != null) {
					templateNameVb.setDataType(rs.getString("DATA_TYPE"));
				} else {
					templateNameVb.setDataType("");
				}
				if (rs.getString("CHAR_USED") != null) {
					templateNameVb.setCharUsed(rs.getString("CHAR_USED"));
				} else {
					templateNameVb.setCharUsed("");
				}
				if (rs.getString("DATA_LENGTH") != null) {
					templateNameVb.setDataLength(rs.getString("DATA_LENGTH"));
				} else {
					templateNameVb.setDataLength("");
				}
				if (rs.getString("DATA_SCALING") != null) {
					templateNameVb.setDataScaling(rs.getString("DATA_SCALING"));
				} else {
					templateNameVb.setDataScaling("");
				}
				if (rs.getString("DATA_INDEX") != null) {
					templateNameVb.setDataIndex(rs.getString("DATA_INDEX"));
				} else {
					templateNameVb.setDataIndex("");
				}
				if (rs.getString("GUIDELINES") != null) {
					templateNameVb.setGuidelines(rs.getString("GUIDELINES"));
				} else {
					templateNameVb.setGuidelines("");
				}
				templateNameVb.setMandatoryFlag(rs.getInt("MANDATORY_FLAG"));
				if (rs.getString("DEFAULT_VALUES") != null) {
					templateNameVb.setDefaultValues(rs.getString("DEFAULT_VALUES"));
				} else {
					templateNameVb.setDefaultValues("");
				}
				if (rs.getString("COLUMN_DESCRIPTION") != null) {
					templateNameVb.setColumnDescription(rs.getString("COLUMN_DESCRIPTION"));
				} else {
					templateNameVb.setColumnDescription("");
				}
				if (rs.getString("DATA_SAMPLE") != null) {
					templateNameVb.setDataSample(rs.getString("DATA_SAMPLE"));
				} else {
					templateNameVb.setDataSample("");
				}
				if (rs.getString("EFFECTIVE_DATE") != null) {
					templateNameVb.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				} else {
					templateNameVb.setEffectiveDate("");
				}
				if (rs.getString("ERROR_DESCRIPTION") != null)
					templateNameVb.setErrorDescription(rs.getString("ERROR_DESCRIPTION"));
				else
					templateNameVb.setErrorDescription("");
				templateNameVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));

				return templateNameVb;
			}
		};
		return mapper;
	}

	public List<TemplateNameVb> getQueryPopupResults(TemplateNameVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer(
				"select * from (Select TAppr.TEMPLATE_NAME, TAppr.GENERAL_DESCRIPTION, TAppr.FEED_CATEGORY,TAppr.FEED_FREQUENCY,"
						+ "TAppr.PROGRAM,(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM =TAppr.PROGRAM) AS PROGRAM_DESC, TAppr.DEFAULT_ACQ_PROCESS_TYPE_AT, "
						+ "TAppr.DEFAULT_ACQ_PROCESS_TYPE," + processAtApprDesc +","+feedAtApprDesc
						+ ",TAppr.VIEW_NAME,TAppr.FEED_STG_NAME, " + "TAppr.TEMPLATE_STATUS_NT,"
						+ "TAppr.TEMPLATE_STATUS," + templateStatusNtApprDesc
						+ ",TAppr.PROCESS_SEQUENCE,TAppr.FILE_PATTERN ,TAppr.EXCEL_FILE_PATTERN , TAppr.EXCEL_TEMPLATE_ID, TAppr.CURR_VERSION_NO,"
						+ dbFunctionFormats("TAPPR.EFFECTIVE_DATE", "DATE_FORMAT", null) + " EFFECTIVE_DATE," + " "
						+ "TAppr.SUB_ADF_NUMBER, " + "TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
						+ RecordIndicatorNtApprDesc + ", TAppr.MAKER, TAppr.VERIFIER," + "TAppr.INTERNAL_STATUS, "
						+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
						+ " DATE_LAST_MODIFIED,TAPPR.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
						+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"+makerApprDesc+" , "+verifierApprDesc
						+ " From TEMPLATE_NAMES TAppr )TAppr");

		String strWhereNotExists = new String(
				"  Not Exists (Select 'X' From TEMPLATE_NAMES_PEND TPend Where TPend.TEMPLATE_NAME = TAppr.TEMPLATE_NAME)");

		StringBuffer strBufPending = new StringBuffer(
				"select * from (Select TPend.TEMPLATE_NAME, TPend.GENERAL_DESCRIPTION, TPend.FEED_CATEGORY,TPend.FEED_FREQUENCY,"
						+ "TPend.PROGRAM,(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM =TPend.PROGRAM) AS PROGRAM_DESC, TPend.DEFAULT_ACQ_PROCESS_TYPE_AT, "
						+ "TPend.DEFAULT_ACQ_PROCESS_TYPE," + processAtPendDesc +","+feedAtPendDesc
						+ ",TPend.VIEW_NAME,TPend.FEED_STG_NAME, " + "TPend.TEMPLATE_STATUS_NT,"
						+ "TPend.TEMPLATE_STATUS," + templateStatusNtPendDesc
						+ ",TPend.PROCESS_SEQUENCE, TPend.FILE_PATTERN ,TPend.EXCEL_FILE_PATTERN ,TPend.EXCEL_TEMPLATE_ID, TPend.CURR_VERSION_NO,"
						+ dbFunctionFormats("TPend.EFFECTIVE_DATE", "DATE_FORMAT", null) + " EFFECTIVE_DATE," + " "
						+ "TPend.SUB_ADF_NUMBER, " + "TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR,"
						+ RecordIndicatorNtPendDesc + ", TPend.MAKER, TPend.VERIFIER," + "TPend.INTERNAL_STATUS,"
						+ dbFunctionFormats("TPend.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
						+ " DATE_LAST_MODIFIED,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
						+ dbFunctionFormats("TPend.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION, "+makerPendDesc +" , "+verifierPendDesc
						+ " From TEMPLATE_NAMES_PEND TPend)TPend ");
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
					case "templateName":
						CommonUtils.addToQuerySearch(" upper(TAppr.TEMPLATE_NAME) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TEMPLATE_NAME) " + val, strBufPending,
								data.getJoinType());
						break;

					case "generalDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.GENERAL_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GENERAL_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "defaultAcquProcessTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.DEFAULT_ACQ_PROCESS_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.DEFAULT_ACQ_PROCESS_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "feedFrequencyDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.FEED_FREQUENCY_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEED_FREQUENCY_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "statusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.TEMPLATE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TEMPLATE_STATUS_DESC) " + val, strBufPending,
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

			String orderBy = " Order By DATE_LAST_MODIFIED_1 desc";
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

	public List<TemplateNameVb> getQueryResults(TemplateNameVb dObj, int intStatus) {

		List<TemplateNameVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		setServiceDefaults();
		String strQueryAppr = new String("Select TAppr.TEMPLATE_NAME, TAppr.GENERAL_DESCRIPTION, TAppr.FEED_CATEGORY,"
				+ "TAppr.PROGRAM,(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM =TAppr.PROGRAM) AS PROGRAM_DESC, TAppr.DEFAULT_ACQ_PROCESS_TYPE_AT, "
				+ "TAppr.DEFAULT_ACQ_PROCESS_TYPE, " + processAtApprDesc + ",TAppr.TEMPLATE_STATUS_NT,"
				+ templateStatusNtApprDesc + ",TAppr.FEED_STG_NAME,TAppr.VIEW_NAME,"
				+ "TAppr.TEMPLATE_STATUS,TAppr.PROCESS_SEQUENCE,TAppr.FILE_PATTERN ,TAppr.EXCEL_FILE_PATTERN , TAppr.EXCEL_TEMPLATE_ID, TAppr.CURR_VERSION_NO,"
				+ dbFunctionFormats("TAppr.EFFECTIVE_DATE", "DATE_FORMAT", null) + " EFFECTIVE_DATE" + " "
				+ ", TAppr.SUB_ADF_NUMBER," + "TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR,"
				+ RecordIndicatorNtApprDesc + " ,TAppr.MAKER, TAppr.VERIFIER," + "TAppr.INTERNAL_STATUS, "
				+ dbFunctionFormats("TAppr.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null) + " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TAppr.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION "
				+ " From TEMPLATE_NAMES TAppr Where TAppr.TEMPLATE_NAME = ?");

		String strQueryPend = new String("Select TPend.TEMPLATE_NAME, TPend.GENERAL_DESCRIPTION, TPend.FEED_CATEGORY,"
				+ "TPend.PROGRAM,(SELECT PROGRAM_DESCRIPTION FROM PROGRAMS WHERE PROGRAM =TPend.PROGRAM) AS PROGRAM_DESC, TPend.DEFAULT_ACQ_PROCESS_TYPE_AT, "
				+ "TPend.DEFAULT_ACQ_PROCESS_TYPE, " + processAtPendDesc + ",TPend.TEMPLATE_STATUS_NT,"
				+ templateStatusNtPendDesc + ",TPend.FEED_STG_NAME,TPend.VIEW_NAME,"
				+ "TPend.TEMPLATE_STATUS,TPend.PROCESS_SEQUENCE,TPend.FILE_PATTERN ,TPend.EXCEL_FILE_PATTERN , TPend.EXCEL_TEMPLATE_ID, TPend.CURR_VERSION_NO,"
				+ dbFunctionFormats("TPend.EFFECTIVE_DATE", "DATE_FORMAT", null) + " EFFECTIVE_DATE," + " "
				+ "TPend.SUB_ADF_NUMBER, " + "TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, "
				+ RecordIndicatorNtPendDesc + ",TPend.MAKER, TPend.VERIFIER," + "TPend.INTERNAL_STATUS, "
				+ dbFunctionFormats("TPend.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null) + " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TPend.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION "
				+ " From TEMPLATE_NAMES_PEND  TPend Where TPend.TEMPLATE_NAME = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getTemplateName()); // [TEMPLATE_NAME]

		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getQueryMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getQueryMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	public List<TemplateNameVb> getQueryResultsPopup(TemplateNameVb dObj, int intStatus) {

		List<TemplateNameVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = new String(
				"SELECT TAPPR.VERSION_NO,TAPPR.TEMPLATE_NAME ,TAPPR.COLUMN_NAME ,TAPPR.COLUMN_ID ,TAPPR.DATA_TYPE ,TAPPR.CHAR_USED ,TAPPR.DATA_LENGTH ,TAPPR.DATA_SCALING ,"
						+ "TAPPR.DATA_INDEX ,TAPPR.GUIDELINES ,TAPPR.MANDATORY_FLAG ,TAPPR.DEFAULT_VALUES ,TAPPR.COLUMN_DESCRIPTION ,"
						+ "TAPPR.DATA_SAMPLE ,"
						+ "To_Char(TAppr.EFFECTIVE_DATE, 'DD-MM-YYYY') EFFECTIVE_DATE, ERROR_DESCRIPTION "
						+ " FROM TEMPLATE_DESIGN_AD TAPPR Where TAppr.VERSION_NO = ? AND  TAppr.TEMPLATE_NAME = ? AND TAppr.COLUMN_NAME = ? ORDER BY TAPPR.COLUMN_ID");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCurrVersionNo());// [TEMPLATE_NAME]
		objParams[1] = new String(dObj.getTemplateName());
		objParams[2] = new String(dObj.getColumnName()); // [COLUMN_NAME]
		try {
			/*
			 * if(!dObj.isVerificationRequired()){intStatus =0;} if(intStatus == 0) {
			 */
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getTemplateDesignMapper());
			// }
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	public int getInternalStatus(TemplateNameVb dObj) {
		int count = 0;
		final int intKeyFieldsCount = 2;
		String strQuery = new String(
				"Select  NLV(INTERNAL_STATUS, 0) FROM TEMPLATE_DESIGN TAPPR Where TAppr.TEMPLATE_NAME = ? AND TAppr.COLUMN_NAME = ? ORDER BY TAPPR.COLUMN_ID ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getTemplateName()); // [TEMPLATE_NAME]
		objParams[1] = new String(dObj.getColumnName()); // [COLUMN_NAME]
		try {
			count = getJdbcTemplate().queryForObject(strQuery.toString(), objParams, Integer.class);
			return count;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Override
	protected List<TemplateNameVb> selectApprovedRecord(TemplateNameVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<TemplateNameVb> doSelectPendingRecord(TemplateNameVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	protected List<TemplateNameVb> doSelectPendingRecordPopup(TemplateNameVb vObject) {
		return getQueryResultsPopup(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(TemplateNameVb records) {
		return records.getTemplateStatus();
	}

	@Override
	protected void setStatus(TemplateNameVb vObject, int status) {
		vObject.setTemplateStatus(status);
	}

	@Override
	protected int doInsertionAppr(TemplateNameVb vObject) {
		String query = "Insert Into TEMPLATE_NAMES(TEMPLATE_NAME, GENERAL_DESCRIPTION, FEED_CATEGORY, PROGRAM, DEFAULT_ACQ_PROCESS_TYPE_AT, "
				+ "DEFAULT_ACQ_PROCESS_TYPE,TEMPLATE_STATUS_NT,TEMPLATE_STATUS,PROCESS_SEQUENCE,FILE_PATTERN,EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,CURR_VERSION_NO,EFFECTIVE_DATE,SUB_ADF_NUMBER,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "
				+ "Values (?, ?, ?, ?, ?,?,?,?, ?, ?, ?, ?, ?,"+dateConvert+", ?,?,?, ?,?,?, "+systemDate+","+systemDate+", ?, ?)";

		Object[] args = { vObject.getTemplateName(), vObject.getGeneralDescription(), vObject.getFeedCategory(),
				vObject.getProgram(), vObject.getDefaultAcquProcessTypeAT(), vObject.getDefaultAcquProcessType(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getProcessSequence(),
				vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(),
				vObject.getCurrVersionNo(), vObject.getEffectiveDate(), vObject.getSubAdfNumber(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getFeedStgName(), vObject.getViewName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(TemplateNameVb vObject) {
		String query = "Insert Into TEMPLATE_NAMES_PEND(TEMPLATE_NAME, GENERAL_DESCRIPTION, FEED_CATEGORY, PROGRAM, DEFAULT_ACQ_PROCESS_TYPE_AT, "
				+ "DEFAULT_ACQ_PROCESS_TYPE,TEMPLATE_STATUS_NT,TEMPLATE_STATUS,PROCESS_SEQUENCE,FILE_PATTERN,EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,CURR_VERSION_NO,EFFECTIVE_DATE,SUB_ADF_NUMBER,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "
				+ "Values (?, ?, ?, ?, ?,?,?,?, ?, ?, ?, ?, ?,"+dateConvert+",?,?, ?, ?,?,?, "+systemDate+","+systemDate+", ?, ?)";

		Object[] args = { vObject.getTemplateName(), vObject.getGeneralDescription(), vObject.getFeedCategory(),
				vObject.getProgram(), vObject.getDefaultAcquProcessTypeAT(), vObject.getDefaultAcquProcessType(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getProcessSequence(),
				vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(),
				vObject.getCurrVersionNo(), vObject.getEffectiveDate(), vObject.getSubAdfNumber(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getFeedStgName(), vObject.getViewName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(TemplateNameVb vObject) {
		String query = "Insert Into TEMPLATE_NAMES_PEND(TEMPLATE_NAME, GENERAL_DESCRIPTION, FEED_CATEGORY, PROGRAM, DEFAULT_ACQ_PROCESS_TYPE_AT, "
				+ "DEFAULT_ACQ_PROCESS_TYPE,TEMPLATE_STATUS_NT,TEMPLATE_STATUS,PROCESS_SEQUENCE,FILE_PATTERN,EXCEL_FILE_PATTERN,EXCEL_TEMPLATE_ID,CURR_VERSION_NO,EFFECTIVE_DATE,SUB_ADF_NUMBER,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "
				+ "Values (?, ?, ?, ?, ?,?,?,?, ?, ?, ?, ?, ?,"+dateConvert+",?,?, ?, ?,?,?, "+systemDate+","+dateTimeConvert+", ?, ?)";

		Object[] args = { vObject.getTemplateName(), vObject.getGeneralDescription(), vObject.getFeedCategory(),
				vObject.getProgram(), vObject.getDefaultAcquProcessTypeAT(), vObject.getDefaultAcquProcessType(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getProcessSequence(),
				vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(),
				vObject.getCurrVersionNo(), vObject.getEffectiveDate(), vObject.getSubAdfNumber(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(),vObject.getDateCreation(), vObject.getFeedStgName(), vObject.getViewName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(TemplateNameVb vObject) {
		String query = "Update TEMPLATE_NAMES Set GENERAL_DESCRIPTION = ?, FEED_CATEGORY = ?, " + "PROGRAM = ?,"
				+ "DEFAULT_ACQ_PROCESS_TYPE_AT = ?, DEFAULT_ACQ_PROCESS_TYPE = ?,  TEMPLATE_STATUS_NT=?,TEMPLATE_STATUS=? ,"
				+ "PROCESS_SEQUENCE = ?,FILE_PATTERN = ?,EXCEL_FILE_PATTERN = ?,EXCEL_TEMPLATE_ID = ? ,CURR_VERSION_NO=?, EFFECTIVE_DATE = "+dateConvert+",SUB_ADF_NUMBER=?, "
				+ "INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, DATE_LAST_MODIFIED = "+systemDate+", FEED_STG_NAME = ?, VIEW_NAME = ? Where TEMPLATE_NAME = ?";
		Object[] args = { vObject.getGeneralDescription(), vObject.getFeedCategory(), vObject.getProgram(),
				vObject.getDefaultAcquProcessTypeAT(), vObject.getDefaultAcquProcessType(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getProcessSequence(),
				vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(),
				vObject.getCurrVersionNo(), vObject.getEffectiveDate(), vObject.getSubAdfNumber(),
				vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getFeedStgName(), vObject.getViewName(),
				vObject.getTemplateName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(TemplateNameVb vObject) {
		String query = "Update TEMPLATE_NAMES_PEND Set  GENERAL_DESCRIPTION = ?, FEED_CATEGORY = ?, " + "PROGRAM = ?,"
				+ "DEFAULT_ACQ_PROCESS_TYPE_AT = ?, DEFAULT_ACQ_PROCESS_TYPE = ?,   TEMPLATE_STATUS_NT=?,TEMPLATE_STATUS=?, "
				+ "PROCESS_SEQUENCE = ?,FILE_PATTERN = ?,EXCEL_FILE_PATTERN = ?,EXCEL_TEMPLATE_ID = ? ,CURR_VERSION_NO=?, EFFECTIVE_DATE = "+dateConvert+",SUB_ADF_NUMBER=?, "
				+ "INTERNAL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, DATE_LAST_MODIFIED = "+systemDate+", FEED_STG_NAME = ?, VIEW_NAME = ? Where TEMPLATE_NAME = ?";
		Object[] args = { vObject.getGeneralDescription(), vObject.getFeedCategory(), vObject.getProgram(),
				vObject.getDefaultAcquProcessTypeAT(), vObject.getDefaultAcquProcessType(),
				vObject.getTemplateStatusNt(), vObject.getTemplateStatus(), vObject.getProcessSequence(),
				vObject.getFilePattern(), vObject.getExcelFilePattern(), vObject.getExcelTemplateId(),
				vObject.getCurrVersionNo(), vObject.getEffectiveDate(), vObject.getSubAdfNumber(),
				vObject.getInternalStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getFeedStgName(), vObject.getViewName(),
				vObject.getTemplateName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(TemplateNameVb vObject) {
		String query = "Delete From TEMPLATE_NAMES Where TEMPLATE_NAME = ?";
		Object[] args = { vObject.getTemplateName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(TemplateNameVb vObject) {
		String query = "Delete From TEMPLATE_NAMES_PEND Where TEMPLATE_NAME = ?";
		Object[] args = { vObject.getTemplateName() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String frameErrorMessage(TemplateNameVb vObject, String strOperation) {
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg = strErrMsg + "TEMPLATE_NAME:" + vObject.getTemplateName();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		} catch (Exception ex) {
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}

	@Override
	protected String getAuditString(TemplateNameVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try {
			if (ValidationUtil.isValid(vObject.getTemplateName()))
				strAudit.append("TEMPLATE_NAME" + auditDelimiterColVal + vObject.getTemplateName().trim());
			else
				strAudit.append("TEMPLATE_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getGeneralDescription()))
				strAudit.append("GENERAL_DESCRIPTION" + auditDelimiterColVal + vObject.getGeneralDescription().trim());
			else
				strAudit.append("GENERAL_DESCRIPTION" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFeedCategory()))
				strAudit.append("FEED_CATEGORY" + auditDelimiterColVal + vObject.getFeedCategory().trim());
			else
				strAudit.append("FEED_CATEGORY" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFeedStgName()))
				strAudit.append("FEED_STG_NAME" + auditDelimiterColVal + vObject.getFeedStgName().trim());
			else
				strAudit.append("FEED_STG_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getViewName()))
				strAudit.append("VIEW_NAME" + auditDelimiterColVal + vObject.getViewName().trim());
			else
				strAudit.append("VIEW_NAME" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append(
					"DEFAULT_ACQ_PROCESS_TYPE_AT" + auditDelimiterColVal + vObject.getDefaultAcquProcessTypeAT());
			strAudit.append(auditDelimiter);

			strAudit.append("DEFAULT_ACQ_PROCESS_TYPE" + auditDelimiterColVal + vObject.getDefaultAcquProcessType());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProgram()))
				strAudit.append("PROGRAM" + auditDelimiterColVal + vObject.getProgram().trim());
			else
				strAudit.append("PROGRAM" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getProcessSequence()))
				strAudit.append("PROCESS_SEQUENCE" + auditDelimiterColVal + vObject.getProcessSequence().trim());
			else
				strAudit.append("PROCESS_SEQUENCE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getExcelTemplateId()))
				strAudit.append("EXCEL_TEMPLATE_ID" + auditDelimiterColVal + vObject.getExcelTemplateId().trim());
			else
				strAudit.append("EXCEL_TEMPLATE_ID" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getFilePattern()))
				strAudit.append("FILE_PATTERN" + auditDelimiterColVal + vObject.getFilePattern().trim());
			else
				strAudit.append("FILE_PATTERN" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getExcelFilePattern()))
				strAudit.append("EXCEL_FILE_PATTERN" + auditDelimiterColVal + vObject.getExcelFilePattern().trim());
			else
				strAudit.append("EXCEL_FILE_PATTERN" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getCurrVersionNo()))
				strAudit.append("CURR_VERSION_NO" + auditDelimiterColVal + vObject.getCurrVersionNo().trim());
			else
				strAudit.append("CURR_VERSION_NO" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE" + auditDelimiterColVal + vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			strAudit.append("SUB_ADF_NUMBER" + auditDelimiterColVal + vObject.getSubAdfNumber());
			strAudit.append(auditDelimiter);

			strAudit.append("TEMPLATE_STATUS_NT" + auditDelimiterColVal + vObject.getTemplateStatusNt());
			strAudit.append(auditDelimiter);

			strAudit.append("TEMPLATE_STATUS" + auditDelimiterColVal + vObject.getTemplateStatus());
			strAudit.append(auditDelimiter);

			strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);

			if (vObject.getRecordIndicator() == -1) {
				vObject.setRecordIndicator(0);
			}
			strAudit.append("RECORD_INDICATOR" + auditDelimiterColVal + vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);

			strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
			strAudit.append(auditDelimiter);

			strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
			strAudit.append(auditDelimiter);

			strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + vObject.getInternalStatus());
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDateLastModified()))
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED" + auditDelimiterColVal + "NULL");
			strAudit.append(auditDelimiter);

			if (ValidationUtil.isValid(vObject.getDateCreation()))
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

	@Override
	protected void setServiceDefaults() {
		serviceName = "TemplateNames";
		serviceDesc = CommonUtils.getResourceManger().getString("TemplateNames");
		tableName = "TEMPLATE_NAMES";
		childTableName = "TEMPLATE_NAMES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	@Override
	protected String getBuildStatus(TemplateNameVb templateNameVb) {
		return getBuildModuleStatus(templateNameVb.getTemplateName(), templateNameVb.getTemplateName());
	}

	public RowMapper getQueryPopupMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameVb templateNameVb = new TemplateNameVb();
				// templateNameVb.setAcBucketApplicationCode(Integer.parseInt(rs.getString(1)));
				// templateNameVb.setTotalRows(totalRows);
				return templateNameVb;
			}
		};
		return mapper;
	}

	public List<TemplateNameVb> getTemplatePopUp(TemplateNameVb dObj) {
		setServiceDefaults();
		String query = new String(
				"SELECT TAPPR.TEMPLATE_NAME ,TAPPR.COLUMN_NAME ,TAPPR.COLUMN_ID ,TAPPR.DATA_TYPE ,TAPPR.CHAR_USED ,TAPPR.DATA_LENGTH ,TAPPR.DATA_SCALING ,"
						+ "TAPPR.DATA_INDEX ,TAPPR.GUIDELINES ,TAPPR.MANDATORY_FLAG ,TAPPR.DEFAULT_VALUES ,TAPPR.COLUMN_DESCRIPTION ,"
						+ "TAPPR.DATA_SAMPLE ,INTERNAL_STATUS, "
						+ "To_Char(TAppr.EFFECTIVE_DATE, 'DD-MM-YYYY') EFFECTIVE_DATE, ERROR_DESCRIPTION "
						+ " FROM TEMPLATE_DESIGN TAPPR Where TAppr.TEMPLATE_NAME = '" + dObj.getTemplateName()
						+ "' AND NVL(TAppr.INTERNAL_STATUS,0) <> 3 ORDER BY TAPPR.COLUMN_ID ");
		try {
			return getJdbcTemplate().query(query, getTemplateDesignMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query));
			return null;
		}
	}

	public int getMaxColumnId(String templateName) {
		String strQuery = new String(
				"select max(COLUMN_id)+1 FROM TEMPLATE_DESIGN  where TEMPLATE_NAME = '" + templateName + "' ");
		try {
			return getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class);

		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public int getMaxIndex(String templateName) {
		String strQuery = new String(
				"select max(DATA_INDEX)+1 FROM TEMPLATE_DESIGN  WHERE TEMPLATE_NAME = '" + templateName + "' ");
		try {
			return getJdbcTemplate().queryForObject(strQuery.toString(), Integer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public int doInsertionTemplateNamesAppr(TemplateNameVb vObject) {
		String query = "Insert Into TEMPLATE_DESIGN(TEMPLATE_NAME, COLUMN_NAME, COLUMN_ID, DATA_TYPE, CHAR_USED, DATA_LENGTH, DATA_SCALING, DATA_INDEX, GUIDELINES, MANDATORY_FLAG_NT, "
				+ "MANDATORY_FLAG , DEFAULT_VALUES, COLUMN_DESCRIPTION, DATA_SAMPLE, EFFECTIVE_DATE, TEMPLATE_DESIGN_STATUS_NT, TEMPLATE_DESIGN_STATUS,"
				+ "RECORD_INDICATOR_NT,RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, FEED_STG_NAME, VIEW_NAME) "
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, To_Date(?, 'DD-MM-YYYY'), ?, ?, ?, ?, ?, ?, ?, SysDate, SysDate)";
		Object[] args = { vObject.getTemplateName(), vObject.getColumnName(), vObject.getColumnId(),
				vObject.getDataType(), vObject.getCharUsed(), vObject.getDataLength(), vObject.getDataScaling(),
				vObject.getDataIndex(), vObject.getGuidelines(), vObject.getMandatoryFlagNt(),
				vObject.getMandatoryFlag(), vObject.getDefaultValues(), vObject.getColumnDescription(),
				vObject.getDataSample(), vObject.getEffectiveDate(), 1, 0, vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getFeedStgName(), vObject.getViewName() };
		return getJdbcTemplate().update(query, args);
	}

	public int doUpdateTemplateNamesAppr(TemplateNameVb vObject) {
		String query = "Update TEMPLATE_DESIGN Set  INTERNAL_STATUS = ?, "
				+ " DATE_LAST_MODIFIED = "+systemDate+" WHERE TEMPLATE_NAME = ? AND COLUMN_NAME = ? ";
		Object[] args = { vObject.getInternalStatus(), vObject.getTemplateName(), vObject.getColumnName() };
		return getJdbcTemplate().update(query, args);
	}

	protected int doDeleteTemplateNamesAppr(TemplateNameVb vObject) {
		String query = "Delete From TEMPLATE_DESIGN Where TEMPLATE_NAME = ? AND COLUMN_NAME = ? ";
		Object[] args = { vObject.getTemplateName(), vObject.getColumnName() };
		return getJdbcTemplate().update(query, args);
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	protected int doInsertionApprPopup(TemplateNameVb vObject) {
		int columnId = getMaxColumnId(vObject.getTemplateName());
		int indexId = getMaxIndex(vObject.getTemplateName());
		if (ValidationUtil.isValid(vObject.getDataIndex())) {
			vObject.setDataIndex("" + indexId);
		}
		String query = "Insert Into TEMPLATE_DESIGN(TEMPLATE_NAME, COLUMN_NAME, COLUMN_ID, DATA_TYPE, CHAR_USED, "
				+ "DATA_LENGTH,DATA_SCALING,DATA_INDEX,GUIDELINES,MANDATORY_FLAG_NT,MANDATORY_FLAG,DEFAULT_VALUES,COLUMN_DESCRIPTION,"
				+ "DATA_SAMPLE,EFFECTIVE_DATE, TEMPLATE_DESIGN_STATUS_NT, TEMPLATE_DESIGN_STATUS, MAKER, VERIFIER, RECORD_INDICATOR_NT, RECORD_INDICATOR, INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "
				+ "Values (?, ?, ?, ?, ?,?,?,?, ?, ?, ?, ?, ?, ?,"+dateConvert+", ?,?, ?,?,?,?,?, "+systemDate+","+systemDate+")";
		Object[] args = { vObject.getTemplateName(), vObject.getColumnName(), columnId, vObject.getDataType(),
				vObject.getCharUsed(), vObject.getDataLength(), vObject.getDataScaling(), vObject.getDataIndex(),
				vObject.getGuidelines(), vObject.getMandatoryFlagNt(), vObject.getMandatoryFlag(),
				vObject.getDefaultValues(), vObject.getColumnDescription(), vObject.getDataSample(),
				vObject.getEffectiveDate(), vObject.getTemplateDesignStatusNt(), vObject.getTemplateDesignStatus(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	public List<TemplateNameVb> getQueryResultsForPopup(TemplateNameVb dObj, int intStatus) {
		List<TemplateNameVb> collTemp = null;
		final int intKeyFieldsCount = 2;
		setServiceDefaults();
		String strQueryAppr = new String("Select TAppr.TEMPLATE_NAME, TAppr.COLUMN_NAME, TAppr.COLUMN_ID,"
				+ "TAppr.DATA_TYPE, " + "TAppr.CHAR_USED, " + "TAppr.DATA_LENGTH,TAppr.DATA_SCALING,TAppr.DATA_INDEX,"
				+ "TAppr.GUIDELINES,TAppr.MANDATORY_FLAG_NT,TAppr.MANDATORY_FLAG ,TAppr.DEFAULT_VALUES , TAppr.COLUMN_DESCRIPTION,"
				+ "TAppr.DATA_SAMPLE," + "To_Char(TAppr.EFFECTIVE_DATE, 'DD-MM-YYYY') EFFECTIVE_DATE, "
				+ "TAppr.TEMPLATE_DESIGN_STATUS_NT," + "TAppr.TEMPLATE_DESIGN_STATUS," + "TAppr.MAKER,"
				+ "TAppr.VERIFIER," + "TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, "
				+ "TAppr.INTERNAL_STATUS, To_Char(TAppr.DATE_LAST_MODIFIED, 'DD-MM-RRRR HH24:MI:SS') DATE_LAST_MODIFIED,"
				+ "To_Char(TAppr.DATE_CREATION, 'DD-MM-RRRR HH24:MI:SS') DATE_CREATION,TAppr.ERROR_DESCRIPTION"
				+ " From TEMPLATE_DESIGN TAppr Where TAppr.TEMPLATE_NAME = ? AND TAppr.COLUMN_NAME = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getTemplateName()); // [TEMPLATE_NAME]
		objParams[1] = new String(dObj.getColumnName()); // [COLUMN_NAME]

		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getTemplateDesignMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doInsertApprRecordForNonTransPopup(TemplateNameVb vObject) throws RuntimeCustomException {
		List<TemplateNameVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecordPopup(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<TemplateNameVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				logger.error("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.error("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setInternalStatus(1);
		retVal = doInsertionApprPopup(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		exceptionCode = writeAuditLog(vObject, null);
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}

	protected List<TemplateNameVb> selectApprovedRecordPopup(TemplateNameVb vObject) {
		return getQueryResultsForPopup(vObject, Constants.STATUS_ZERO);
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doUpdateApprRecordPopup(List<TemplateNameVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		setServiceDefaults();
		try {
			for (TemplateNameVb vObject : vObjects) {
				if (vObject.isChecked()) {
					if (vObject.isNewRecord()) {
						strCurrentOperation = Constants.ADD;
						exceptionCode = doInsertApprRecordForNonTransPopup(vObject);
						if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							throw buildRuntimeCustomException(exceptionCode);
						}
					} else {
						strCurrentOperation = Constants.MODIFY;
						exceptionCode = doUpdateApprRecordForNonTransPopup(vObject);
						if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (RuntimeCustomException rcException) {
			throw rcException;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Modify.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	protected ExceptionCode doUpdateApprRecordForNonTransPopup(TemplateNameVb vObject) throws RuntimeCustomException {
		List<TemplateNameVb> collTemp = null;
		TemplateNameVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
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
		vObjectlocal = ((ArrayList<TemplateNameVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0) {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		if (!vObjectlocal.compare(vObject)) {
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObject.getInternalStatus() == 1) {
				vObject.setInternalStatus(1);
			} else {
				vObject.setInternalStatus(2);
			}
			retVal = doUpdateApprPopup(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			exceptionCode = writeAuditLog(vObject, vObjectlocal);
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	protected int doUpdateApprPopup(TemplateNameVb vObject) {
		if (!"".equalsIgnoreCase(vObject.getDataIndex())) {
			int indexId = getMaxIndex(vObject.getTemplateName());
			if (ValidationUtil.isValid(vObject.getDataIndex())) {
				vObject.setDataIndex("" + indexId);
			}
		}

		String query = "Update TEMPLATE_DESIGN Set COLUMN_ID = ?, DATA_TYPE = ?, "
				+ "CHAR_USED = ?,DATA_LENGTH = ?,DATA_SCALING = ?,DATA_INDEX = ?,GUIDELINES = ?,"
				+ "MANDATORY_FLAG = ?,DEFAULT_VALUES = ?,COLUMN_DESCRIPTION = ?,DATA_SAMPLE = ?,EFFECTIVE_DATE = "+dateConvert+","
				+ "TEMPLATE_DESIGN_STATUS = ?," + "MAKER = ?, VERIFIER = ?, RECORD_INDICATOR = ?,INTERNAL_STATUS = ?,"
				+ "DATE_LAST_MODIFIED = "+systemDate+" Where TEMPLATE_NAME = ? AND COLUMN_NAME = ?";

		Object[] args = { vObject.getColumnId(), vObject.getDataType(), vObject.getCharUsed(), vObject.getDataLength(),
				vObject.getDataScaling(), vObject.getDataIndex(), vObject.getGuidelines(), vObject.getMandatoryFlag(),
				vObject.getDefaultValues(), vObject.getColumnDescription(), vObject.getDataSample(),
				vObject.getEffectiveDate(), vObject.getTemplateDesignStatus(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getRecordIndicator(), vObject.getInternalStatus(),
				vObject.getTemplateName(), vObject.getColumnName() };

		return getJdbcTemplate().update(query, args);
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doDeleteRecordPopup(List<TemplateNameVb> vObjects, TemplateNameVb vObjectParam)
			throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		setServiceDefaults();
		strCurrentOperation = Constants.DELETE;
		try {
			for (TemplateNameVb vObject : vObjects) {
				if (vObject.isChecked()) {
					vObject.setStaticDelete(vObjectParam.isStaticDelete());
					vObject.setVerificationRequired(vObjectParam.isVerificationRequired());
					exceptionCode = doDeleteRecordForNonTransPopup(vObject);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (RuntimeCustomException rcException) {
			throw rcException;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Delete.", ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	protected ExceptionCode doDeleteRecordForNonTransPopup(TemplateNameVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		if (vObject.getInternalStatus() == 1) {
			vObject.setInternalStatus(1);
			retVal = doDeleteTemplateNamesAppr(vObject);
		} else {
			vObject.setInternalStatus(3);
			retVal = doUpdateTemplateNamesAppr(vObject);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	public List<TableColumnsVb> findGuidelineDesc(TemplateNameVb templateNameVb) throws DataAccessException {
		String refTable = templateNameVb.getRefTable();
		String sql = "SELECT DISTINCT COLUMN_NAME,COLUMN_ID FROM ALL_TAB_COLUMNS WHERE TABLE_NAME='" + refTable
				+ "' ORDER BY COLUMN_ID ";
		return getJdbcTemplate().query(sql, getTableColumnsDesc());
	}

	public RowMapper getTableColumnsDesc() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateNameVb templateNameVb = new TemplateNameVb();
				templateNameVb.setRefColumn(rs.getString("COLUMN_NAME"));
				return templateNameVb;
			}
		};
		return mapper;
	}

	public RowMapper getColumnNamesMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ColumnHeadersVb templateNameVb = new ColumnHeadersVb();
				if (rs.getString("COLUMN_NAME") != null)
					templateNameVb.setCaption(rs.getString("COLUMN_NAME"));
				else
					templateNameVb.setCaption("");
				templateNameVb.setColType("T");
				return templateNameVb;
			}
		};
		return mapper;
	}

	public RowMapper getColumnNamesMapper1() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ColumnHeadersVb templateNameVb = new ColumnHeadersVb();
				if (rs.getString("DATA_SAMPLE") != null)
					templateNameVb.setCaption(rs.getString("DATA_SAMPLE"));
				else
					templateNameVb.setCaption("");
				templateNameVb.setColType("T");
				return templateNameVb;
			}
		};
		return mapper;
	}

	public RowMapper getDataNamesMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReportStgVb templateNameVb = new ReportStgVb();
				if (rs.getString("COLUMN_NAME") != null)
					templateNameVb.setDataColumn1(rs.getString("COLUMN_NAME"));
				else
					templateNameVb.setDataColumn1("");
				if (rs.getString("DATA_TYPE") != null)
					templateNameVb.setDataColumn2(rs.getString("DATA_TYPE"));
				else
					templateNameVb.setDataColumn2("");
				if (rs.getString("DATA_LENGTH") != null)
					templateNameVb.setDataColumn3(rs.getString("DATA_LENGTH"));
				else
					templateNameVb.setDataColumn3("");
				if (rs.getString("DATA_INDEX") != null)
					templateNameVb.setDataColumn4(rs.getString("DATA_INDEX"));
				else
					templateNameVb.setDataColumn4("");
				if (rs.getString("GUIDELINES") != null)
					templateNameVb.setDataColumn5(rs.getString("GUIDELINES"));
				else
					templateNameVb.setDataColumn5("");
				if (rs.getString("MANDATORY_FLAG") != null)
					templateNameVb.setDataColumn6(rs.getString("MANDATORY_FLAG"));
				else
					templateNameVb.setDataColumn6("");
				if (rs.getString("COLUMN_DESCRIPTION") != null)
					templateNameVb.setDataColumn7(rs.getString("COLUMN_DESCRIPTION"));
				else
					templateNameVb.setDataColumn7("");
				if (rs.getString("DATA_SAMPLE") != null)
					templateNameVb.setDataColumn8(rs.getString("DATA_SAMPLE"));
				else
					templateNameVb.setDataColumn8("");

				return templateNameVb;
			}
		};
		return mapper;
	}

	public List<ColumnHeadersVb> getTableColumnsForTemplate(TemplateNameVb dObj) {
		List<ColumnHeadersVb> collTemp = null;
		String strQueryAppr = new String(
				"SELECT COLUMN_NAME,DATA_TYPE,DATA_LENGTH from TEMPLATE_DESIGN where template_name= ? AND "
						+ " COLUMN_NAME NOT IN('RECORD_INDICATOR_NT', 'RECORD_INDICATOR', 'MAKER', 'VERIFIER', 'INTERNAL_STATUS', 'DATE_LAST_MODIFIED', 'DATE_CREATION') "
						+ " and (COLUMN_NAME not like '%\\_NT' escape '\\' and  column_name not like '%\\_AT' escape '\\') "
						+ " ORDER BY COLUMN_ID ");
		Object objParams[] = new Object[1];
		objParams[0] = new String(dObj.getTemplateName().toUpperCase());

		try {
			logger.info("TableColumnsForTemplate : Executing query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getColumnNamesMapper());
			// collTemp =
			// getJdbcTemplate().query(strQueryAppr.toString(),getColumnNamesMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getTableColumnsForTemplate Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	public List<ColumnHeadersVb> getTableHeadersForTemplate(TemplateNameVb dObj) {
		List<ColumnHeadersVb> collTemp = null;
		String strQueryAppr = new String(
				"select COLUMN_NAME,DATA_TYPE,DATA_LENGTH from ALL_TAB_COLUMNS where table_name = 'TEMPLATE_DESIGN' "
						+ "and column_name in ('COLUMN_NAME','DATA_TYPE','DATA_LENGTH' ,'DATA_INDEX' ,'GUIDELINES' ,'MANDATORY_FLAG' , 'COLUMN_DESCRIPTION' ,'DATA_SAMPLE') "
						+ "and owner = '" + owner
						+ "' AND  COLUMN_NAME NOT IN('RECORD_INDICATOR_NT', 'RECORD_INDICATOR', 'MAKER', 'VERIFIER', 'INTERNAL_STATUS', 'DATE_LAST_MODIFIED', 'DATE_CREATION') "
						+ "and (COLUMN_NAME not like '%\\_NT' escape '\\' and  column_name not like '%\\_AT' escape '\\')  "
						+ "ORDER BY COLUMN_ID ");
		Object objParams[] = new Object[1];
		objParams[0] = new String(dObj.getTemplateName().toUpperCase());

		try {
			logger.info("TableColumnsForTemplate : Executing query");
			// collTemp =
			// getJdbcTemplate().query(strQueryAppr.toString(),objParams,getColumnNamesMapper());
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), getColumnNamesMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getTableColumnsForTemplate Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	public String getListReportDataForExportAsXMLStringWithMultipleHeaders(TemplateNameVb templateNameVb,
			final List<ColumnHeadersVb> columnHeaders, long min, long max) {
		String query = "";
		try {
			query = "SELECT COLUMN_NAME,DATA_TYPE,DATA_LENGTH,DATA_INDEX,case when GUIDELINES is not null then 'Y' else null end GUIDELINES, "
					+ "case when MANDATORY_FLAG = '1' then 'Y' else 'N' end MANDATORY_FLAG, COLUMN_DESCRIPTION,DATA_SAMPLE from TEMPLATE_DESIGN "
					+ " where template_name= '" + templateNameVb.getTemplateName().toUpperCase() + "' AND "
					+ " COLUMN_NAME NOT IN('RECORD_INDICATOR_NT', 'RECORD_INDICATOR', 'MAKER', 'VERIFIER', 'INTERNAL_STATUS', 'DATE_LAST_MODIFIED', 'DATE_CREATION') "
					+ " and (COLUMN_NAME not like '%\\_NT' escape '\\' and  column_name not like '%\\_AT' escape '\\') "
					+ " ORDER BY COLUMN_ID ";
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					StringBuffer lResult = new StringBuffer("");
					lResult.append("<tableData>");
					while (rs.next()) {
						lResult.append("<tableRow>");
						for (ColumnHeadersVb columnHeader : columnHeaders) {
							String value = "";
							if (ValidationUtil.isValid(columnHeader.getCaption())) {
								value = rs.getString(columnHeader.getCaption());
								if (value == null)
									value = "";
								lResult.append("<").append(columnHeader.getCaption()).append(">")
										.append(StringEscapeUtils.escapeXml(value)).append("</")
										.append(columnHeader.getCaption()).append(">");
							}
						}
						try {
							String value = rs.getString("FORMAT_TYPE");
							if (value == null)
								value = "";
							lResult.append("<").append("formatType").append(">")
									.append(StringEscapeUtils.escapeXml(value)).append("</").append("formatType")
									.append(">");
						} catch (Exception e) {
							// Do nothing this is a format column
						}
						lResult.append("</tableRow>");
					}
					lResult.append("</tableData>");
					return lResult.toString();
				}
			};
			return (String) getJdbcTemplate().query(query, mapper);
		} catch (BadSqlGrammarException ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query));
			strErrorDesc = parseErrorMsg(new UncategorizedSQLException("", "", ex.getSQLException()));
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query));
			strErrorDesc = ex.getMessage();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}
	}

	public List<ColumnHeadersVb> getSampleDataForHeaders(TemplateNameVb dObj) {
		List<ColumnHeadersVb> collTemp = null;
		String strQueryAppr = new String("SELECT DATA_SAMPLE from TEMPLATE_DESIGN  where template_name= ? "
				+ " AND  COLUMN_NAME NOT IN('RECORD_INDICATOR_NT', 'RECORD_INDICATOR', 'MAKER', 'VERIFIER', 'INTERNAL_STATUS', 'DATE_LAST_MODIFIED', 'DATE_CREATION')  "
				+ " and (COLUMN_NAME not like '%\\_NT' escape '\\' and  column_name not like '%\\_AT' escape '\\') "
				+ " ORDER BY COLUMN_ID ");
		Object objParams[] = new Object[1];
		objParams[0] = new String(dObj.getTemplateName().toUpperCase());

		try {
			logger.info("TableColumnsForTemplate : Executing query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getColumnNamesMapper1());
			// collTemp =
			// getJdbcTemplate().query(strQueryAppr.toString(),getColumnNamesMapper1());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getTableColumnsForTemplate Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	public static String getOwner() {
		return owner;
	}

	public static void setOwner(String owner) {
		TemplateNameDao.owner = owner;
	}
}