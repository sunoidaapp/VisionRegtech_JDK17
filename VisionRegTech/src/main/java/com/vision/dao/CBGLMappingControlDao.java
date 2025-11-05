
package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CBGLMappingControlVb;
import com.vision.vb.SmartSearchVb;

@Component
public class CBGLMappingControlDao extends AbstractDao<CBGLMappingControlVb> {
	@Override
	protected List<CBGLMappingControlVb> selectApprovedRecord(CBGLMappingControlVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<CBGLMappingControlVb> doSelectPendingRecord(CBGLMappingControlVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(CBGLMappingControlVb records) {
		return records.getCbGlCtrlStatus();
	}

	@Override
	protected void setStatus(CBGLMappingControlVb vObject, int status) {
		vObject.setCbGlCtrlStatus(status);
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "CB GL Mapping Control";
		serviceDesc = "CB GL Mapping Control";
		tableName = "CB_GL_MAPPING_CONTROL";
		childTableName = "CB_GL_MAPPING_CONTROL";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

	String glCodeApprDesc = "(SELECT  GL_DESCRIPTION FROM CB_GL_CODES WHERE   GL_CODE = TAppr.CB_GL_CODE ) GL_DESCRIPTION";
	String glCodePendDesc = "(SELECT  GL_DESCRIPTION FROM CB_GL_CODES WHERE  GL_CODE = TPend.CB_GL_CODE) GL_DESCRIPTION";

	String StatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.CB_GL_CTRL_STATUS",
			"CB_GL_CTRL_STATUS_DESC");
	String StatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.CB_GL_CTRL_STATUS",
			"CB_GL_CTRL_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	String RuleValue01AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5012, "TAppr.RULE_VALUE_01",
			"RULE_VALUE_01_DESC");
	String RuleValue01AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5012, "TPend.RULE_VALUE_01",
			"RULE_VALUE_01_DESC");

	String RuleValue02AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5013, "TAppr.RULE_VALUE_02",
			"RULE_VALUE_02_DESC");
	String RuleValue02AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5013, "TPend.RULE_VALUE_02",
			"RULE_VALUE_02_DESC");

	String RuleValue03AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5014, "TAppr.RULE_VALUE_03",
			"RULE_VALUE_03_DESC");
	String RuleValue03AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5014, "TPend.RULE_VALUE_03",
			"RULE_VALUE_03_DESC");

	String RuleValue04AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5015, "TAppr.RULE_VALUE_04",
			"RULE_VALUE_04_DESC");
	String RuleValue04AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5015, "TPend.RULE_VALUE_04",
			"RULE_VALUE_04_DESC");

	String RuleValue05AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5016, "TAppr.RULE_VALUE_05",
			"RULE_VALUE_05_DESC");
	String RuleValue05AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5016, "TPend.RULE_VALUE_05",
			"RULE_VALUE_05_DESC");

	String RuleValue06AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5017, "TAppr.RULE_VALUE_06",
			"RULE_VALUE_06_DESC");
	String RuleValue06AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5017, "TPend.RULE_VALUE_06",
			"RULE_VALUE_06_DESC");

	String RuleValue07AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5018, "TAppr.RULE_VALUE_07",
			"RULE_VALUE_07_DESC");
	String RuleValue07AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5018, "TPend.RULE_VALUE_07",
			"RULE_VALUE_07_DESC");

	String RuleValue08AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5019, "TAppr.RULE_VALUE_08",
			"RULE_VALUE_08_DESC");
	String RuleValue08AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5019, "TPend.RULE_VALUE_08",
			"RULE_VALUE_08_DESC");

	String RuleValue09AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5020, "TAppr.RULE_VALUE_09",
			"RULE_VALUE_09_DESC");
	String RuleValue09AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5020, "TPend.RULE_VALUE_09",
			"RULE_VALUE_09_DESC");

	String RuleValue10AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5021, "TAppr.RULE_VALUE_10",
			"RULE_VALUE_10_DESC");
	String RuleValue10AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5021, "TPend.RULE_VALUE_10",
			"RULE_VALUE_10_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CBGLMappingControlVb vObject = new CBGLMappingControlVb();
				if (rs.getString("COUNTRY") != null) {
					vObject.setCountry(rs.getString("COUNTRY"));
				} else {
					vObject.setCountry("");
				}
				if (rs.getString("LE_BOOK") != null) {
					vObject.setLeBook(rs.getString("LE_BOOK"));
				} else {
					vObject.setLeBook("");
				}
				if (rs.getString("RULE_ID") != null) {
					vObject.setRuleId(rs.getString("RULE_ID"));
				} else {
					vObject.setRuleId("");
				}
				if (rs.getString("CB_GL_CODE") != null) {
					vObject.setCbGlCode(rs.getString("CB_GL_CODE"));
				} else {
					vObject.setCbGlCode("");
				}
				vObject.setRuleValue01AT(rs.getInt("RULE_VALUE_01_AT"));
				vObject.setRuleValue01(rs.getString("RULE_VALUE_01") != null ? rs.getString("RULE_VALUE_01") : "");
				vObject.setRuleValue02AT(rs.getInt("RULE_VALUE_02_AT"));
				vObject.setRuleValue02(rs.getString("RULE_VALUE_02") != null ? rs.getString("RULE_VALUE_02") : "");
				vObject.setRuleValue03AT(rs.getInt("RULE_VALUE_03_AT"));
				vObject.setRuleValue03(rs.getString("RULE_VALUE_03") != null ? rs.getString("RULE_VALUE_03") : "");
				vObject.setRuleValue04AT(rs.getInt("RULE_VALUE_04_AT"));
				vObject.setRuleValue04(rs.getString("RULE_VALUE_04") != null ? rs.getString("RULE_VALUE_04") : "");
				vObject.setRuleValue05AT(rs.getInt("RULE_VALUE_05_AT"));
				vObject.setRuleValue05(rs.getString("RULE_VALUE_05") != null ? rs.getString("RULE_VALUE_05") : "");
				vObject.setRuleValue06AT(rs.getInt("RULE_VALUE_06_AT"));
				vObject.setRuleValue06(rs.getString("RULE_VALUE_06") != null ? rs.getString("RULE_VALUE_06") : "");
				vObject.setRuleValue07AT(rs.getInt("RULE_VALUE_07_AT"));
				vObject.setRuleValue07(rs.getString("RULE_VALUE_07") != null ? rs.getString("RULE_VALUE_07") : "");
				vObject.setRuleValue08AT(rs.getInt("RULE_VALUE_08_AT"));
				vObject.setRuleValue08(rs.getString("RULE_VALUE_08") != null ? rs.getString("RULE_VALUE_08") : "");
				vObject.setRuleValue09AT(rs.getInt("RULE_VALUE_09_AT"));
				vObject.setRuleValue09(rs.getString("RULE_VALUE_09") != null ? rs.getString("RULE_VALUE_09") : "");
				vObject.setRuleValue10AT(rs.getInt("RULE_VALUE_10_AT"));
				vObject.setRuleValue10(rs.getString("RULE_VALUE_10") != null ? rs.getString("RULE_VALUE_10") : "");
				vObject.setCbGlCodeDesc(rs.getString("GL_DESCRIPTION"));
				vObject.setCbGlCtrlStatusNt(rs.getInt("CB_GL_CTRL_STATUS_NT"));
				vObject.setCbGlCtrlStatus(rs.getInt("CB_GL_CTRL_STATUS"));
				vObject.setRuleValue01Desc(rs.getString("RULE_VALUE_01_DESC"));
				vObject.setRuleValue02Desc(rs.getString("RULE_VALUE_02_DESC"));
				vObject.setRuleValue03Desc(rs.getString("RULE_VALUE_03_DESC"));
				vObject.setRuleValue04Desc(rs.getString("RULE_VALUE_04_DESC"));
				vObject.setRuleValue05Desc(rs.getString("RULE_VALUE_05_DESC"));
				vObject.setRuleValue06Desc(rs.getString("RULE_VALUE_06_DESC"));
				vObject.setRuleValue07Desc(rs.getString("RULE_VALUE_07_DESC"));
				vObject.setRuleValue08Desc(rs.getString("RULE_VALUE_08_DESC"));
				vObject.setRuleValue09Desc(rs.getString("RULE_VALUE_09_DESC"));
				vObject.setRuleValue10Desc(rs.getString("RULE_VALUE_10_DESC"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				if (ValidationUtil.isValid(rs.getString("CB_GL_CTRL_STATUS_DESC")))
					vObject.setCbGlCtrlStatusDesc(rs.getString("CB_GL_CTRL_STATUS_DESC"));
				if (ValidationUtil.isValid(rs.getString("RECORD_INDICATOR_DESC")))
					vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				if (ValidationUtil.isValid(rs.getString("MAKER_NAME")))
					vObject.setMakerName(rs.getString("MAKER_NAME"));
				if (ValidationUtil.isValid(rs.getString("VERIFIER_NAME")))
					vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				if (rs.getString("DATE_LAST_MODIFIED") != null) {
					vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				} else {
					vObject.setDateLastModified("");
				}
				if (rs.getString("DATE_CREATION") != null) {
					vObject.setDateCreation(rs.getString("DATE_CREATION"));
				} else {
					vObject.setDateCreation("");
				}
				return vObject;
			}
		};
		return mapper;
	}

	public RowMapper getQueryPopupMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CBGLMappingControlVb vObject = new CBGLMappingControlVb();
				if (rs.getString("COUNTRY") != null) {
					vObject.setCountry(rs.getString("COUNTRY"));
				} else {
					vObject.setCountry("");
				}
				if (rs.getString("LE_BOOK") != null) {
					vObject.setLeBook(rs.getString("LE_BOOK"));
				} else {
					vObject.setLeBook("");
				}
				if (rs.getString("RULE_ID") != null) {
					vObject.setRuleId(rs.getString("RULE_ID"));
				} else {
					vObject.setRuleId("");
				}
				if (rs.getString("CB_GL_CODE") != null) {
					vObject.setCbGlCode(rs.getString("CB_GL_CODE"));
				} else {
					vObject.setCbGlCode("");
				}
				vObject.setRuleValue01AT(rs.getInt("RULE_VALUE_01_AT"));
				vObject.setRuleValue01(rs.getString("RULE_VALUE_01") != null ? rs.getString("RULE_VALUE_01") : "");
				vObject.setRuleValue02AT(rs.getInt("RULE_VALUE_02_AT"));
				vObject.setRuleValue02(rs.getString("RULE_VALUE_02") != null ? rs.getString("RULE_VALUE_02") : "");
				vObject.setRuleValue03AT(rs.getInt("RULE_VALUE_03_AT"));
				vObject.setRuleValue03(rs.getString("RULE_VALUE_03") != null ? rs.getString("RULE_VALUE_03") : "");
				vObject.setRuleValue04AT(rs.getInt("RULE_VALUE_04_AT"));
				vObject.setRuleValue04(rs.getString("RULE_VALUE_04") != null ? rs.getString("RULE_VALUE_04") : "");
				vObject.setRuleValue05AT(rs.getInt("RULE_VALUE_05_AT"));
				vObject.setRuleValue05(rs.getString("RULE_VALUE_05") != null ? rs.getString("RULE_VALUE_05") : "");
				vObject.setRuleValue06AT(rs.getInt("RULE_VALUE_06_AT"));
				vObject.setRuleValue06(rs.getString("RULE_VALUE_06") != null ? rs.getString("RULE_VALUE_06") : "");
				vObject.setRuleValue07AT(rs.getInt("RULE_VALUE_07_AT"));
				vObject.setRuleValue07(rs.getString("RULE_VALUE_07") != null ? rs.getString("RULE_VALUE_07") : "");
				vObject.setRuleValue08AT(rs.getInt("RULE_VALUE_08_AT"));
				vObject.setRuleValue08(rs.getString("RULE_VALUE_08") != null ? rs.getString("RULE_VALUE_08") : "");
				vObject.setRuleValue09AT(rs.getInt("RULE_VALUE_09_AT"));
				vObject.setRuleValue09(rs.getString("RULE_VALUE_09") != null ? rs.getString("RULE_VALUE_09") : "");
				vObject.setRuleValue10AT(rs.getInt("RULE_VALUE_10_AT"));
				vObject.setRuleValue10(rs.getString("RULE_VALUE_10") != null ? rs.getString("RULE_VALUE_10") : "");
				vObject.setCbGlCtrlStatusNt(rs.getInt("CB_GL_CTRL_STATUS_NT"));
				vObject.setCbGlCtrlStatus(rs.getInt("CB_GL_CTRL_STATUS"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}

	public List<CBGLMappingControlVb> getQueryPopupResults(CBGLMappingControlVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (SELECT TAppr.COUNTRY, " + "TAppr.LE_BOOK, "
				+ "TAppr.RULE_ID, " + "TAppr.RULE_VALUE_01_AT, TAppr.RULE_VALUE_01, " + RuleValue01AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_02_AT, TAppr.RULE_VALUE_02, " + RuleValue02AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_03_AT, TAppr.RULE_VALUE_03, " + RuleValue03AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_04_AT, TAppr.RULE_VALUE_04, " + RuleValue04AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_05_AT, TAppr.RULE_VALUE_05, " + RuleValue05AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_06_AT, TAppr.RULE_VALUE_06, " + RuleValue06AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_07_AT, TAppr.RULE_VALUE_07, " + RuleValue07AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_08_AT, TAppr.RULE_VALUE_08, " + RuleValue08AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_09_AT, TAppr.RULE_VALUE_09, " + RuleValue09AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_10_AT, TAppr.RULE_VALUE_10, " + RuleValue10AtApprDesc + ", " + "TAppr.CB_GL_CODE, "
				+ glCodeApprDesc + ", " + "TAppr.CB_GL_CTRL_STATUS_NT, TAppr.CB_GL_CTRL_STATUS, " + StatusNtApprDesc
				+ ", " + "TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, " + RecordIndicatorNtApprDesc + ", "
				+ "TAppr.MAKER, " + makerApprDesc + ", " + "TAppr.VERIFIER, " + verifierApprDesc + ", "
				+ "TAppr.INTERNAL_STATUS, " + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED, " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " FROM CB_GL_MAPPING_CONTROL TAppr) TAppr");
		String strWhereNotExists = new String(" NOT EXISTS (SELECT 'X' FROM CB_GL_MAPPING_CONTROL_PEND TPend "
				+ " WHERE TAppr.COUNTRY = TPend.COUNTRY " + " AND TAppr.LE_BOOK = TPend.LE_BOOK "
				+ " AND TAppr.RULE_ID = TPend.RULE_ID)");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM (SELECT TPend.COUNTRY, " + "TPend.LE_BOOK, "
				+ "TPend.RULE_ID, " + "TPend.RULE_VALUE_01_AT, TPend.RULE_VALUE_01, " + RuleValue01AtPendDesc + ", "
				+ "TPend.RULE_VALUE_02_AT, TPend.RULE_VALUE_02, " + RuleValue02AtPendDesc + ", "
				+ "TPend.RULE_VALUE_03_AT, TPend.RULE_VALUE_03, " + RuleValue03AtPendDesc + ", "
				+ "TPend.RULE_VALUE_04_AT, TPend.RULE_VALUE_04, " + RuleValue04AtPendDesc + ", "
				+ "TPend.RULE_VALUE_05_AT, TPend.RULE_VALUE_05, " + RuleValue05AtPendDesc + ", "
				+ "TPend.RULE_VALUE_06_AT, TPend.RULE_VALUE_06, " + RuleValue06AtPendDesc + ", "
				+ "TPend.RULE_VALUE_07_AT, TPend.RULE_VALUE_07, " + RuleValue07AtPendDesc + ", "
				+ "TPend.RULE_VALUE_08_AT, TPend.RULE_VALUE_08, " + RuleValue08AtPendDesc + ", "
				+ "TPend.RULE_VALUE_09_AT, TPend.RULE_VALUE_09, " + RuleValue09AtPendDesc + ", "
				+ "TPend.RULE_VALUE_10_AT, TPend.RULE_VALUE_10, " + RuleValue10AtPendDesc + ", " + "TPend.CB_GL_CODE, "
				+ glCodePendDesc + ", " + "TPend.CB_GL_CTRL_STATUS_NT, TPend.CB_GL_CTRL_STATUS, " + StatusNtPendDesc
				+ ", " + "TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, " + RecordIndicatorNtPendDesc + ", "
				+ "TPend.MAKER, " + makerPendDesc + ", " + "TPend.VERIFIER, " + verifierPendDesc + ", "
				+ "TPend.INTERNAL_STATUS, " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED, " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " FROM CB_GL_MAPPING_CONTROL_PEND TPend) TPend");
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
						CommonUtils.addToQuerySearch(" upper(TAppr.COUNTRY) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) " + val, strBufPending, data.getJoinType());
						break;
					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAppr.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) " + val, strBufPending, data.getJoinType());
						break;
					case "ruleId":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ID) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ID) " + val, strBufPending, data.getJoinType());
						break;
					case "ruleValue01Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_01_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_01_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleValue02Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_02_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_02_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleValue03Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_03_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_03_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "ruleValue04Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_04_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_04_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "ruleValue05Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_05_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_05_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "ruleValue06Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_06_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_06_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleValue07Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_07_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_07_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "ruleValue08Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_08_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_08_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "ruleValue09Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_09_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_09_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "ruleValue10Desc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_VALUE_10_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_VALUE_10_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "cbGlCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.CB_GL_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CB_GL_CODE) " + val, strBufPending,
								data.getJoinType());
						break;
					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch("UPPER(TAppr.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch("UPPER(TPend.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "cbGlCtrlStatusDesc":
						CommonUtils.addToQuerySearch("UPPER(TAppr.CB_GL_CTRL_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch("UPPER(TPend.CB_GL_CTRL_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					
					case "maker":
						CommonUtils.addToQuerySearch(" upper(TAppr.MAKER) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.MAKER) " + val, strBufPending, data.getJoinType());
						break;
						
						
					default:
					}
					count++;
				}
			}
			if (ValidationUtil.isValid(dObj.getCountry())) {
				CommonUtils.addToQuery(" COUNTRY IN ('" + dObj.getCountry() + "') ", strBufApprove);
				CommonUtils.addToQuery(" COUNTRY IN ('" + dObj.getCountry() + "') ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				String calLeBook = removeDescLeBook(dObj.getLeBook());
				CommonUtils.addToQuery(" LE_BOOK IN ('" + calLeBook + "') ", strBufApprove);
				CommonUtils.addToQuery(" LE_BOOK IN ('" + calLeBook + "') ", strBufPending);
			}
			String orderBy = " ORDER BY DATE_LAST_MODIFIED Desc";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public String getRuleId() {
		String sql = "";
		try {
			sql = "SELECT 'GM' || LPAD(RULE_ID_SEQ.NEXTVAL, 5, '0') AS Rule_Id FROM dual";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getJdbcTemplate().queryForObject(sql, String.class);
	}

	public String getAlphaSubTabDescription(int alphaTabId, String alphaSubTab) {
		String sql = "SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = ? AND ALPHA_SUB_TAB = ?";
		try {
			return getJdbcTemplate().queryForObject(sql, new Object[] { alphaTabId, alphaSubTab }, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public List<CBGLMappingControlVb> getQueryResults(CBGLMappingControlVb dObj, int intStatus) {
		setServiceDefaults();
		List<CBGLMappingControlVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryAppr = "SELECT TAppr.COUNTRY" + ", TAppr.LE_BOOK " + ", TAppr.RULE_ID "
				+ ", TAppr.RULE_VALUE_01_AT, TAppr.RULE_VALUE_01 " + ", TAppr.RULE_VALUE_02_AT, TAppr.RULE_VALUE_02 "
				+ ", TAppr.RULE_VALUE_03_AT, TAppr.RULE_VALUE_03 " + ", TAppr.RULE_VALUE_04_AT, TAppr.RULE_VALUE_04 "
				+ ", TAppr.RULE_VALUE_05_AT, TAppr.RULE_VALUE_05 " + ", TAppr.RULE_VALUE_06_AT, TAppr.RULE_VALUE_06 "
				+ ", TAppr.RULE_VALUE_07_AT, TAppr.RULE_VALUE_07 " + ", TAppr.RULE_VALUE_08_AT, TAppr.RULE_VALUE_08 "
				+ ", TAppr.RULE_VALUE_09_AT, TAppr.RULE_VALUE_09 " + ", TAppr.RULE_VALUE_10_AT, TAppr.RULE_VALUE_10 "
				+ ", TAppr.CB_GL_CODE " + ", TAppr.CB_GL_CTRL_STATUS_NT, TAppr.CB_GL_CTRL_STATUS "
				+ ", TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR " + ", TAppr.MAKER, " + makerApprDesc
				+ ", TAppr.VERIFIER, " + verifierApprDesc + ", TAppr.INTERNAL_STATUS " + ", " + dateFormat
				+ "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED " + ", " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " FROM CB_GL_MAPPING_CONTROL TAppr "
				+ " WHERE TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.RULE_ID = ?";
		String strQueryPend = "SELECT TPend.COUNTRY" + ", TPend.LE_BOOK " + ", TPend.RULE_ID "
				+ ", TPend.RULE_VALUE_01_AT, TPend.RULE_VALUE_01 " + ", TPend.RULE_VALUE_02_AT, TPend.RULE_VALUE_02 "
				+ ", TPend.RULE_VALUE_03_AT, TPend.RULE_VALUE_03 " + ", TPend.RULE_VALUE_04_AT, TPend.RULE_VALUE_04 "
				+ ", TPend.RULE_VALUE_05_AT, TPend.RULE_VALUE_05 " + ", TPend.RULE_VALUE_06_AT, TPend.RULE_VALUE_06 "
				+ ", TPend.RULE_VALUE_07_AT, TPend.RULE_VALUE_07 " + ", TPend.RULE_VALUE_08_AT, TPend.RULE_VALUE_08 "
				+ ", TPend.RULE_VALUE_09_AT, TPend.RULE_VALUE_09 " + ", TPend.RULE_VALUE_10_AT, TPend.RULE_VALUE_10 "
				+ ", TPend.CB_GL_CODE " + ", TPend.CB_GL_CTRL_STATUS_NT, TPend.CB_GL_CTRL_STATUS "
				+ ", TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR " + ", TPend.MAKER, " + makerPendDesc
				+ ", TPend.VERIFIER, " + verifierPendDesc + ", TPend.INTERNAL_STATUS " + ", " + dateFormat
				+ "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED " + ", " + dateFormat
				+ "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " FROM CB_GL_MAPPING_CONTROL_PEND TPend "
				+ " WHERE TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND TPend.RULE_ID = ?";
		Object[] objParams = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getRuleId();
		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				collTemp = getJdbcTemplate().query(strQueryAppr, objParams, getQueryPopupMapper());
			} else {
				collTemp = getJdbcTemplate().query(strQueryPend, objParams, getQueryPopupMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public List<CBGLMappingControlVb> getQueryResultsForReview(CBGLMappingControlVb dObj, int intStatus) {
		setServiceDefaults();
		List<CBGLMappingControlVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String strQueryAppr = "SELECT " + "TAppr.COUNTRY, " + "TAppr.LE_BOOK, " + "TAppr.RULE_ID, "
				+ "TAppr.RULE_VALUE_01_AT, TAppr.RULE_VALUE_01, " + RuleValue01AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_02_AT, TAppr.RULE_VALUE_02, " + RuleValue02AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_03_AT, TAppr.RULE_VALUE_03, " + RuleValue03AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_04_AT, TAppr.RULE_VALUE_04, " + RuleValue04AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_05_AT, TAppr.RULE_VALUE_05, " + RuleValue05AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_06_AT, TAppr.RULE_VALUE_06, " + RuleValue06AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_07_AT, TAppr.RULE_VALUE_07, " + RuleValue07AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_08_AT, TAppr.RULE_VALUE_08, " + RuleValue08AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_09_AT, TAppr.RULE_VALUE_09, " + RuleValue09AtApprDesc + ", "
				+ "TAppr.RULE_VALUE_10_AT, TAppr.RULE_VALUE_10, " + RuleValue10AtApprDesc + ", " + "TAppr.CB_GL_CODE, "
				+ glCodeApprDesc + ", " + "TAppr.CB_GL_CTRL_STATUS_NT, TAppr.CB_GL_CTRL_STATUS, " + StatusNtApprDesc
				+ ", " + "TAppr.RECORD_INDICATOR_NT, TAppr.RECORD_INDICATOR, " + RecordIndicatorNtApprDesc + ", "
				+ "TAppr.MAKER, " + makerApprDesc + ", " + "TAppr.VERIFIER, " + verifierApprDesc + ", "
				+ "TAppr.INTERNAL_STATUS, " + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED, " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ "FROM CB_GL_MAPPING_CONTROL TAppr " + "WHERE TAppr.COUNTRY = ? " + "AND TAppr.LE_BOOK = ? "
				+ "AND TAppr.RULE_ID = ?";
		String strQueryPend = "SELECT " + "TPend.COUNTRY, " + "TPend.LE_BOOK, " + "TPend.RULE_ID, "
				+ "TPend.RULE_VALUE_01_AT, TPend.RULE_VALUE_01, " + RuleValue01AtPendDesc + ", "
				+ "TPend.RULE_VALUE_02_AT, TPend.RULE_VALUE_02, " + RuleValue02AtPendDesc + ", "
				+ "TPend.RULE_VALUE_03_AT, TPend.RULE_VALUE_03, " + RuleValue03AtPendDesc + ", "
				+ "TPend.RULE_VALUE_04_AT, TPend.RULE_VALUE_04, " + RuleValue04AtPendDesc + ", "
				+ "TPend.RULE_VALUE_05_AT, TPend.RULE_VALUE_05, " + RuleValue05AtPendDesc + ", "
				+ "TPend.RULE_VALUE_06_AT, TPend.RULE_VALUE_06, " + RuleValue06AtPendDesc + ", "
				+ "TPend.RULE_VALUE_07_AT, TPend.RULE_VALUE_07, " + RuleValue07AtPendDesc + ", "
				+ "TPend.RULE_VALUE_08_AT, TPend.RULE_VALUE_08, " + RuleValue08AtPendDesc + ", "
				+ "TPend.RULE_VALUE_09_AT, TPend.RULE_VALUE_09, " + RuleValue09AtPendDesc + ", "
				+ "TPend.RULE_VALUE_10_AT, TPend.RULE_VALUE_10, " + RuleValue10AtPendDesc + ", " + "TPend.CB_GL_CODE, "
				+ glCodePendDesc + ", " + "TPend.CB_GL_CTRL_STATUS_NT, TPend.CB_GL_CTRL_STATUS, " + StatusNtPendDesc
				+ ", " + "TPend.RECORD_INDICATOR_NT, TPend.RECORD_INDICATOR, " + RecordIndicatorNtPendDesc + ", "
				+ "TPend.MAKER, " + makerPendDesc + ", " + "TPend.VERIFIER, " + verifierPendDesc + ", "
				+ "TPend.INTERNAL_STATUS, " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED, " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ "FROM CB_GL_MAPPING_CONTROL_PEND TPend " + "WHERE TPend.COUNTRY = ? " + "AND TPend.LE_BOOK = ? "
				+ "AND TPend.RULE_ID = ?";
		Object[] objParams = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getRuleId();
		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				collTemp = getJdbcTemplate().query(strQueryAppr, objParams, getMapper());
			} else {
				collTemp = getJdbcTemplate().query(strQueryPend, objParams, getMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	protected int doInsertionAppr(CBGLMappingControlVb vObject) {
		String query = "Insert Into CB_GL_MAPPING_CONTROL (COUNTRY, LE_BOOK, RULE_ID, RULE_VALUE_01_AT, RULE_VALUE_01, "
				+ "RULE_VALUE_02_AT, RULE_VALUE_02, RULE_VALUE_03_AT, RULE_VALUE_03, RULE_VALUE_04_AT, RULE_VALUE_04, "
				+ "RULE_VALUE_05_AT, RULE_VALUE_05, RULE_VALUE_06_AT, RULE_VALUE_06, RULE_VALUE_07_AT, RULE_VALUE_07, "
				+ "RULE_VALUE_08_AT, RULE_VALUE_08, RULE_VALUE_09_AT, RULE_VALUE_09, RULE_VALUE_10_AT, RULE_VALUE_10, "
				+ "CB_GL_CODE, CB_GL_CTRL_STATUS_NT, CB_GL_CTRL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
				+ "MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId(), vObject.getRuleValue01AT(),
				vObject.getRuleValue01(), vObject.getRuleValue02AT(), vObject.getRuleValue02(),
				vObject.getRuleValue03AT(), vObject.getRuleValue03(), vObject.getRuleValue04AT(),
				vObject.getRuleValue04(), vObject.getRuleValue05AT(), vObject.getRuleValue05(),
				vObject.getRuleValue06AT(), vObject.getRuleValue06(), vObject.getRuleValue07AT(),
				vObject.getRuleValue07(), vObject.getRuleValue08AT(), vObject.getRuleValue08(),
				vObject.getRuleValue09AT(), vObject.getRuleValue09(), vObject.getRuleValue10AT(),
				vObject.getRuleValue10(), vObject.getCbGlCode(), vObject.getCbGlCtrlStatusNt(),
				vObject.getCbGlCtrlStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(CBGLMappingControlVb vObject) {
		String query = "Insert Into CB_GL_MAPPING_CONTROL_PEND (COUNTRY, LE_BOOK, RULE_ID, RULE_VALUE_01_AT, RULE_VALUE_01, "
				+ "RULE_VALUE_02_AT, RULE_VALUE_02, RULE_VALUE_03_AT, RULE_VALUE_03, RULE_VALUE_04_AT, RULE_VALUE_04, "
				+ "RULE_VALUE_05_AT, RULE_VALUE_05, RULE_VALUE_06_AT, RULE_VALUE_06, RULE_VALUE_07_AT, RULE_VALUE_07, "
				+ "RULE_VALUE_08_AT, RULE_VALUE_08, RULE_VALUE_09_AT, RULE_VALUE_09, RULE_VALUE_10_AT, RULE_VALUE_10, "
				+ "CB_GL_CODE, CB_GL_CTRL_STATUS_NT, CB_GL_CTRL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
				+ "MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId(), vObject.getRuleValue01AT(),
				vObject.getRuleValue01(), vObject.getRuleValue02AT(), vObject.getRuleValue02(),
				vObject.getRuleValue03AT(), vObject.getRuleValue03(), vObject.getRuleValue04AT(),
				vObject.getRuleValue04(), vObject.getRuleValue05AT(), vObject.getRuleValue05(),
				vObject.getRuleValue06AT(), vObject.getRuleValue06(), vObject.getRuleValue07AT(),
				vObject.getRuleValue07(), vObject.getRuleValue08AT(), vObject.getRuleValue08(),
				vObject.getRuleValue09AT(), vObject.getRuleValue09(), vObject.getRuleValue10AT(),
				vObject.getRuleValue10(), vObject.getCbGlCode(), vObject.getCbGlCtrlStatusNt(),
				vObject.getCbGlCtrlStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(CBGLMappingControlVb vObject) {
		String query = "Insert Into CB_GL_MAPPING_CONTROL_PEND (COUNTRY, LE_BOOK, RULE_ID, RULE_VALUE_01_AT, RULE_VALUE_01, "
				+ "RULE_VALUE_02_AT, RULE_VALUE_02, RULE_VALUE_03_AT, RULE_VALUE_03, RULE_VALUE_04_AT, RULE_VALUE_04, "
				+ "RULE_VALUE_05_AT, RULE_VALUE_05, RULE_VALUE_06_AT, RULE_VALUE_06, RULE_VALUE_07_AT, RULE_VALUE_07, "
				+ "RULE_VALUE_08_AT, RULE_VALUE_08, RULE_VALUE_09_AT, RULE_VALUE_09, RULE_VALUE_10_AT, RULE_VALUE_10, "
				+ "CB_GL_CODE, CB_GL_CTRL_STATUS_NT, CB_GL_CTRL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, "
				+ "MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + dateTimeConvert + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId(), vObject.getRuleValue01AT(),
				vObject.getRuleValue01(), vObject.getRuleValue02AT(), vObject.getRuleValue02(),
				vObject.getRuleValue03AT(), vObject.getRuleValue03(), vObject.getRuleValue04AT(),
				vObject.getRuleValue04(), vObject.getRuleValue05AT(), vObject.getRuleValue05(),
				vObject.getRuleValue06AT(), vObject.getRuleValue06(), vObject.getRuleValue07AT(),
				vObject.getRuleValue07(), vObject.getRuleValue08AT(), vObject.getRuleValue08(),
				vObject.getRuleValue09AT(), vObject.getRuleValue09(), vObject.getRuleValue10AT(),
				vObject.getRuleValue10(), vObject.getCbGlCode(), vObject.getCbGlCtrlStatusNt(),
				vObject.getCbGlCtrlStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(CBGLMappingControlVb vObject) {
		String query = "Update CB_GL_MAPPING_CONTROL Set " + "RULE_VALUE_01_AT = ?, RULE_VALUE_01 = ?, "
				+ "RULE_VALUE_02_AT = ?, RULE_VALUE_02 = ?, " + "RULE_VALUE_03_AT = ?, RULE_VALUE_03 = ?, "
				+ "RULE_VALUE_04_AT = ?, RULE_VALUE_04 = ?, " + "RULE_VALUE_05_AT = ?, RULE_VALUE_05 = ?, "
				+ "RULE_VALUE_06_AT = ?, RULE_VALUE_06 = ?, " + "RULE_VALUE_07_AT = ?, RULE_VALUE_07 = ?, "
				+ "RULE_VALUE_08_AT = ?, RULE_VALUE_08 = ?, " + "RULE_VALUE_09_AT = ?, RULE_VALUE_09 = ?, "
				+ "RULE_VALUE_10_AT = ?, RULE_VALUE_10 = ?, "
				+ "CB_GL_CODE = ?, CB_GL_CTRL_STATUS_NT = ?, CB_GL_CTRL_STATUS = ?, "
				+ "RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, " + "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, "
				+ "DATE_LAST_MODIFIED = " + systemDate + " " + "WHERE COUNTRY = ? AND LE_BOOK = ? AND RULE_ID = ?";
		Object[] args = { vObject.getRuleValue01AT(), vObject.getRuleValue01(), vObject.getRuleValue02AT(),
				vObject.getRuleValue02(), vObject.getRuleValue03AT(), vObject.getRuleValue03(),
				vObject.getRuleValue04AT(), vObject.getRuleValue04(), vObject.getRuleValue05AT(),
				vObject.getRuleValue05(), vObject.getRuleValue06AT(), vObject.getRuleValue06(),
				vObject.getRuleValue07AT(), vObject.getRuleValue07(), vObject.getRuleValue08AT(),
				vObject.getRuleValue08(), vObject.getRuleValue09AT(), vObject.getRuleValue09(),
				vObject.getRuleValue10AT(), vObject.getRuleValue10(), vObject.getCbGlCode(),
				vObject.getCbGlCtrlStatusNt(), vObject.getCbGlCtrlStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(CBGLMappingControlVb vObject) {
		String query = "Update CB_GL_MAPPING_CONTROL_PEND Set " + "RULE_VALUE_01_AT = ?, RULE_VALUE_01 = ?, "
				+ "RULE_VALUE_02_AT = ?, RULE_VALUE_02 = ?, " + "RULE_VALUE_03_AT = ?, RULE_VALUE_03 = ?, "
				+ "RULE_VALUE_04_AT = ?, RULE_VALUE_04 = ?, " + "RULE_VALUE_05_AT = ?, RULE_VALUE_05 = ?, "
				+ "RULE_VALUE_06_AT = ?, RULE_VALUE_06 = ?, " + "RULE_VALUE_07_AT = ?, RULE_VALUE_07 = ?, "
				+ "RULE_VALUE_08_AT = ?, RULE_VALUE_08 = ?, " + "RULE_VALUE_09_AT = ?, RULE_VALUE_09 = ?, "
				+ "RULE_VALUE_10_AT = ?, RULE_VALUE_10 = ?, "
				+ "CB_GL_CODE = ?, CB_GL_CTRL_STATUS_NT = ?, CB_GL_CTRL_STATUS = ?, "
				+ "RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, " + "MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, "
				+ "DATE_LAST_MODIFIED = " + systemDate + " " + "WHERE COUNTRY = ? AND LE_BOOK = ? AND RULE_ID = ?";
		Object[] args = { vObject.getRuleValue01AT(), vObject.getRuleValue01(), vObject.getRuleValue02AT(),
				vObject.getRuleValue02(), vObject.getRuleValue03AT(), vObject.getRuleValue03(),
				vObject.getRuleValue04AT(), vObject.getRuleValue04(), vObject.getRuleValue05AT(),
				vObject.getRuleValue05(), vObject.getRuleValue06AT(), vObject.getRuleValue06(),
				vObject.getRuleValue07AT(), vObject.getRuleValue07(), vObject.getRuleValue08AT(),
				vObject.getRuleValue08(), vObject.getRuleValue09AT(), vObject.getRuleValue09(),
				vObject.getRuleValue10AT(), vObject.getRuleValue10(), vObject.getCbGlCode(),
				vObject.getCbGlCtrlStatusNt(), vObject.getCbGlCtrlStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(CBGLMappingControlVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From CB_GL_MAPPING_CONTROL Where COUNTRY = ?  AND LE_BOOK = ?  AND RULE_ID = ?  ";
		Object[] args = { vObject.getCountry(), CalLeBook, vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(CBGLMappingControlVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From CB_GL_MAPPING_CONTROL_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND RULE_ID = ?  ";
		Object[] args = { vObject.getCountry(), CalLeBook, vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(CBGLMappingControlVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		strAudit.append("COUNTRY" + auditDelimiterColVal + vObject.getRuleValue02AT());
		strAudit.append(auditDelimiter);
		strAudit.append("LE_BOOK" + auditDelimiterColVal + vObject.getRuleValue02AT());
		strAudit.append(auditDelimiter);
		strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleValue02AT());
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_01_AT" + auditDelimiterColVal + vObject.getRuleValue01AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue01()))
			strAudit.append("RULE_VALUE_01" + auditDelimiterColVal + vObject.getRuleValue01().trim());
		else
			strAudit.append("RULE_VALUE_01" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_02_AT" + auditDelimiterColVal + vObject.getRuleValue02AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue02()))
			strAudit.append("RULE_VALUE_02" + auditDelimiterColVal + vObject.getRuleValue02().trim());
		else
			strAudit.append("RULE_VALUE_02" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_03_AT" + auditDelimiterColVal + vObject.getRuleValue03AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue03()))
			strAudit.append("RULE_VALUE_03" + auditDelimiterColVal + vObject.getRuleValue03().trim());
		else
			strAudit.append("RULE_VALUE_03" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_04_AT" + auditDelimiterColVal + vObject.getRuleValue04AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue04()))
			strAudit.append("RULE_VALUE_04" + auditDelimiterColVal + vObject.getRuleValue04().trim());
		else
			strAudit.append("RULE_VALUE_04" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_05_AT" + auditDelimiterColVal + vObject.getRuleValue05AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue05()))
			strAudit.append("RULE_VALUE_05" + auditDelimiterColVal + vObject.getRuleValue05().trim());
		else
			strAudit.append("RULE_VALUE_05" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_06_AT" + auditDelimiterColVal + vObject.getRuleValue06AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue06()))
			strAudit.append("RULE_VALUE_06" + auditDelimiterColVal + vObject.getRuleValue06().trim());
		else
			strAudit.append("RULE_VALUE_06" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_07_AT" + auditDelimiterColVal + vObject.getRuleValue07AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue07()))
			strAudit.append("RULE_VALUE_07" + auditDelimiterColVal + vObject.getRuleValue07().trim());
		else
			strAudit.append("RULE_VALUE_07" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_08_AT" + auditDelimiterColVal + vObject.getRuleValue08AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue08()))
			strAudit.append("RULE_VALUE_08" + auditDelimiterColVal + vObject.getRuleValue08().trim());
		else
			strAudit.append("RULE_VALUE_08" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_09_AT" + auditDelimiterColVal + vObject.getRuleValue09AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue09()))
			strAudit.append("RULE_VALUE_09" + auditDelimiterColVal + vObject.getRuleValue09().trim());
		else
			strAudit.append("RULE_VALUE_09" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_VALUE_10_AT" + auditDelimiterColVal + vObject.getRuleValue10AT());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleValue10()))
			strAudit.append("RULE_VALUE_10" + auditDelimiterColVal + vObject.getRuleValue10().trim());
		else
			strAudit.append("RULE_VALUE_10" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
		strAudit.append(auditDelimiter);

		strAudit.append("MAKER" + auditDelimiterColVal + vObject.getMaker());
		strAudit.append(auditDelimiter);

		strAudit.append("VERIFIER" + auditDelimiterColVal + vObject.getVerifier());
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

		strAudit.append("INTERNAL_STATUS" + auditDelimiterColVal + vObject.getInternalStatus());
		strAudit.append(auditDelimiter);

		return strAudit.toString();
	}
}
