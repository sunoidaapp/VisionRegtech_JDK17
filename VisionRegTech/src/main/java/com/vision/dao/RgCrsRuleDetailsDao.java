package com.vision.dao;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.MagnifierResultVb;
import com.vision.vb.RgCrsRuleDetailsVb;
import com.vision.vb.RgCrsRuleVb;
import com.vision.vb.SmartSearchVb;

@Component
public class RgCrsRuleDetailsDao extends AbstractDao<RgCrsRuleDetailsVb> {

	/******* Mapper Start **********/
	String VisionSbuAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 3, "TAppr.VISION_SBU",
			"VISION_SBU_DESC");
	String VisionSbuAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 3, "TPend.VISION_SBU",
			"VISION_SBU_DESC");

	String RuleTypeAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 857, "TAppr.RULE_TYPE",
			"RULE_TYPE_DESC");
	String RuleTypeAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 857, "TPend.RULE_TYPE",
			"RULE_TYPE_DESC");

	String RuleAlphaAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 854, "TAppr.RULE_ALPHA",
			"RULE_ALPHA_DESC");
	String RuleAlphaAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 854, "TPend.RULE_ALPHA",
			"RULE_ALPHA_DESC");

	String RuleNumAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 854, "TAppr.RULE_NUM", "RULE_NUM_DESC");
	String RuleNumAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 854, "TPend.RULE_NUM", "RULE_NUM_DESC");

	String RuleStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.RULE_STATUS",
			"RULE_STATUS_DESC");
	String RuleStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.RULE_STATUS",
			"RULE_STATUS_DESC");
	@Value("#{ '${app.cloud}' == 'Y' ? 'CRS_CUSTOMERS' : 'CUSTOMERS' }")
	private String Customers;

	@Autowired
	MagnifierDao magnifierDao;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgCrsRuleDetailsVb vObject = new RgCrsRuleDetailsVb();
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
				vObject.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
				if (rs.getString("VISION_SBU") != null) {
					vObject.setVisionSbu(rs.getString("VISION_SBU"));
				} else {
					vObject.setVisionSbu("");
				}
				if (rs.getString("VISION_SBU_DESC") != null) {
					vObject.setVisionSbuDesc(rs.getString("VISION_SBU_DESC"));
				}
				if (rs.getString("RULE_ID") != null) {
					vObject.setRuleId(rs.getString("RULE_ID"));
				} else {
					vObject.setRuleId("");
				}
				if (rs.getString("COLUMN_NAME") != null) {
					vObject.setColumnName(rs.getString("COLUMN_NAME"));
				} else {
					vObject.setColumnName("");
				}
				vObject.setRuleTypeAt(rs.getInt("RULE_TYPE_AT"));
				if (rs.getString("RULE_TYPE") != null) {
					vObject.setRuleType(rs.getString("RULE_TYPE"));
				} else {
					vObject.setRuleType("");
				}
				if (rs.getString("RULE_TYPE_DESC") != null) {
					vObject.setRuleTypeDesc(rs.getString("RULE_TYPE_DESC"));
				}
				if (rs.getString("RULE_DESCRIPTION") != null) {
					vObject.setRuleDescription(rs.getString("RULE_DESCRIPTION"));
				} else {
					vObject.setRuleDescription("");
				}
				vObject.setRuleSequence(rs.getInt("RULE_SEQUENCE"));
				if (rs.getString("LOOKUP_TABLE_NAME") != null) {
					vObject.setLookupTableName(rs.getString("LOOKUP_TABLE_NAME"));
				} else {
					vObject.setLookupTableName("");
				}
				if (rs.getString("LOOKUP_COLUMN") != null) {
					vObject.setLookupColumn(rs.getString("LOOKUP_COLUMN"));
				} else {
					vObject.setLookupColumn("");
				}
				if (rs.getString("LOOKUP_CONDITION") != null) {
					vObject.setLookupCondition(rs.getString("LOOKUP_CONDITION"));
				} else {
					vObject.setLookupCondition("");
				}
				if (rs.getString("VALUE_PATT_FLAG") != null) {
					vObject.setValuePattFlag(rs.getString("VALUE_PATT_FLAG"));
				} else {
					vObject.setValuePattFlag("");
				}
				if (rs.getString("VALUE_PATT_VALUES") != null) {
					vObject.setValuePattValues(rs.getString("VALUE_PATT_VALUES"));
				} else {
					vObject.setValuePattValues("");
				}
				if (rs.getString("RULE_ALPHA_FLAG") != null) {
					vObject.setRuleAlphaFlag(rs.getString("RULE_ALPHA_FLAG"));
				} else {
					vObject.setRuleAlphaFlag("");
				}
				vObject.setRuleAlphaAt(rs.getInt("RULE_ALPHA_AT"));
				if (rs.getString("RULE_ALPHA") != null) {
					vObject.setRuleAlpha(rs.getString("RULE_ALPHA"));
				} else {
					vObject.setRuleAlpha("");
				}
				if (rs.getString("RULE_ALPHA_DESC") != null) {
					vObject.setRuleAlphaDesc(rs.getString("RULE_ALPHA_DESC"));
				}
				if (rs.getString("RULE_ALPHA_CHAR") != null) {
					vObject.setRuleAlphaChar(rs.getString("RULE_ALPHA_CHAR"));
				} else {
					vObject.setRuleAlphaChar("");
				}
				if (rs.getString("RULE_NUM_FLAG") != null) {
					vObject.setRuleNumFlag(rs.getString("RULE_NUM_FLAG"));
				} else {
					vObject.setRuleNumFlag("");
				}
				vObject.setRuleNumAt(rs.getInt("RULE_NUM_AT"));
				if (rs.getString("RULE_NUM") != null) {
					vObject.setRuleNum(rs.getString("RULE_NUM"));
				} else {
					vObject.setRuleNum("");
				}
				if (rs.getString("RULE_NUM_DESC") != null) {
					vObject.setRuleNumDesc(rs.getString("RULE_NUM_DESC"));
				}
				if (rs.getString("RULE_NUM_CHAR") != null) {
					vObject.setRuleNumChar(rs.getString("RULE_NUM_CHAR"));
				} else {
					vObject.setRuleNumChar("");
				}
				vObject.setRuleLenMin(rs.getInt("RULE_LEN_MIN"));
				vObject.setRuleLenMax(rs.getInt("RULE_LEN_MAX"));
				vObject.setRuleStatusNt(rs.getInt("RULE_STATUS_NT"));
				vObject.setRuleStatus(rs.getInt("RULE_STATUS"));
				vObject.setStatusDesc(rs.getString("RULE_STATUS_DESC"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
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
				if (rs.getString("MAKER_NAME") != null) {
					vObject.setMakerName(rs.getString("MAKER_NAME"));
				}

				if (rs.getString("VERIFIER_NAME") != null) {
					vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				}
				if (rs.getString("FILTER_CONDITION") != null) {
					vObject.setFilterCondition(rs.getString("FILTER_CONDITION"));
				}
				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/
	public List<RgCrsRuleDetailsVb> getQueryPopupResults(RgCrsRuleDetailsVb dObj) {
		String lookupCondiAppr = getDbFunction("CONVERT") + "(TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
		String lookupCondiPend = getDbFunction("CONVERT") + "(TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			lookupCondiAppr = getDbFunction("CONVERT") + "(VARCHAR(MAX), TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
			lookupCondiPend = getDbFunction("CONVERT") + "(VARCHAR(MAX), TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		}
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.VISION_SBU_AT"
				+ ",TAppr.VISION_SBU, " + VisionSbuAtApprDesc + ",TAppr.RULE_ID" + ",TAppr.COLUMN_NAME"
				+ ",TAppr.RULE_TYPE_AT" + ",TAppr.RULE_TYPE, " + RuleTypeAtApprDesc + ",TAppr.RULE_DESCRIPTION"
				+ ",TAppr.RULE_SEQUENCE" + ",TAppr.LOOKUP_TABLE_NAME" + ",TAppr.LOOKUP_COLUMN" + "," + lookupCondiAppr
				+ ",TAppr.VALUE_PATT_FLAG" + ",TAppr.VALUE_PATT_VALUES" + ",TAppr.RULE_ALPHA_FLAG"
				+ ",TAppr.RULE_ALPHA_AT" + ",TAppr.RULE_ALPHA, " + RuleAlphaAtApprDesc + ",TAppr.RULE_ALPHA_CHAR"
				+ ",TAppr.RULE_NUM_FLAG" + ",TAppr.RULE_NUM_AT" + ",TAppr.RULE_NUM, " + RuleNumAtApprDesc
				+ ",TAppr.RULE_NUM_CHAR" + ",TAppr.RULE_LEN_MIN" + ",TAppr.RULE_LEN_MAX" + ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS, " + RuleStatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR, " + RecordIndicatorApprDesc + ",TAppr.MAKER, " + makerApprDesc
				+ ",TAppr.VERIFIER, " + verifierApprDesc + ",TAppr.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TAppr.FILTER_CONDITION "
				+ " from RG_CRS_RULE_DETAILS TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From RG_CRS_RULE_DETAILS_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.VISION_SBU = TPend.VISION_SBU AND TAppr.RULE_ID = TPend.RULE_ID AND TAppr.COLUMN_NAME = TPend.COLUMN_NAME AND TAppr.RULE_TYPE = TPend.RULE_TYPE )");
		StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.VISION_SBU_AT"
				+ ",TPend.VISION_SBU, " + VisionSbuAtPendDesc + ",TPend.RULE_ID" + ",TPend.COLUMN_NAME"
				+ ",TPend.RULE_TYPE_AT" + ",TPend.RULE_TYPE, " + RuleTypeAtPendDesc + ",TPend.RULE_DESCRIPTION"
				+ ",TPend.RULE_SEQUENCE" + ",TPend.LOOKUP_TABLE_NAME" + ",TPend.LOOKUP_COLUMN" + "," + lookupCondiPend
				+ ",TPend.VALUE_PATT_FLAG" + ",TPend.VALUE_PATT_VALUES" + ",TPend.RULE_ALPHA_FLAG"
				+ ",TPend.RULE_ALPHA_AT" + ",TPend.RULE_ALPHA, " + RuleAlphaAtPendDesc + ",TPend.RULE_ALPHA_CHAR"
				+ ",TPend.RULE_NUM_FLAG" + ",TPend.RULE_NUM_AT" + ",TPend.RULE_NUM, " + RuleNumAtPendDesc
				+ ",TPend.RULE_NUM_CHAR" + ",TPend.RULE_LEN_MIN" + ",TPend.RULE_LEN_MAX" + ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS, " + RuleStatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR, " + RecordIndicatorPendDesc + ",TPend.MAKER, " + makerPendDesc
				+ ",TPend.VERIFIER, " + verifierPendDesc + ",TPend.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TPend.FILTER_CONDITION "
				+ " from RG_CRS_RULE_DETAILS_PEND TPend ");
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

					case "visionSbu":
						CommonUtils.addToQuerySearch(" upper(TAppr.VISION_SBU) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VISION_SBU) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleId":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ID) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ID) " + val, strBufPending, data.getJoinType());
						break;

					case "columnName":
						CommonUtils.addToQuerySearch(" upper(TAppr.COLUMN_NAME) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COLUMN_NAME) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleType":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_TYPE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_TYPE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleSequence":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_SEQUENCE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_SEQUENCE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "lookupTableName":
						CommonUtils.addToQuerySearch(" upper(TAppr.LOOKUP_TABLE_NAME) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LOOKUP_TABLE_NAME) " + val, strBufPending,
								data.getJoinType());
						break;

					case "lookupColumn":
						CommonUtils.addToQuerySearch(" upper(TAppr.LOOKUP_COLUMN) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LOOKUP_COLUMN) " + val, strBufPending,
								data.getJoinType());
						break;

					case "lookupCondition":
						CommonUtils.addToQuerySearch(" upper(TAppr.LOOKUP_CONDITION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LOOKUP_CONDITION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "valuePattFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.VALUE_PATT_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VALUE_PATT_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "valuePattValues":
						CommonUtils.addToQuerySearch(" upper(TAppr.VALUE_PATT_VALUES) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VALUE_PATT_VALUES) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleAlphaFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ALPHA_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ALPHA_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleAlpha":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ALPHA) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ALPHA) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleAlphaChar":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ALPHA_CHAR) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ALPHA_CHAR) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleNumFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_NUM_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_NUM_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleNum":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_NUM) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_NUM) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleNumChar":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_NUM_CHAR) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_NUM_CHAR) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleLenMin":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_LEN_MIN) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_LEN_MIN) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleLenMax":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_LEN_MAX) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_LEN_MAX) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_STATUS) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_STATUS) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicator":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR) " + val, strBufPending,
								data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			String orderBy = " Order By COUNTRY, LE_BOOK, VISION_SBU, RULE_ID, COLUMN_NAME, RULE_TYPE ";
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

	public List<RgCrsRuleDetailsVb> getQueryResults(RgCrsRuleDetailsVb dObj, int intStatus) {

		setServiceDefaults();
		String lookupCondiAppr = getDbFunction("CONVERT") + "(TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
		String lookupCondiPend = getDbFunction("CONVERT") + "(TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			lookupCondiAppr = getDbFunction("CONVERT") + "(VARCHAR(MAX), TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
			lookupCondiPend = getDbFunction("CONVERT") + "(VARCHAR(MAX), TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		}
		List<RgCrsRuleDetailsVb> collTemp = null;

		final int intKeyFieldsCount = 6;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.VISION_SBU_AT"
				+ ",TAppr.VISION_SBU, " + VisionSbuAtApprDesc + ",TAppr.RULE_ID" + ",TAppr.COLUMN_NAME"
				+ ",TAppr.RULE_TYPE_AT" + ",TAppr.RULE_TYPE, " + RuleTypeAtApprDesc + ",TAppr.RULE_DESCRIPTION"
				+ ",TAppr.RULE_SEQUENCE" + ",TAppr.LOOKUP_TABLE_NAME" + ",TAppr.LOOKUP_COLUMN" + "," + lookupCondiAppr
				+ ",TAppr.VALUE_PATT_FLAG" + ",TAppr.VALUE_PATT_VALUES" + ",TAppr.RULE_ALPHA_FLAG"
				+ ",TAppr.RULE_ALPHA_AT" + ",TAppr.RULE_ALPHA, " + RuleAlphaAtApprDesc + ",TAppr.RULE_ALPHA_CHAR"
				+ ",TAppr.RULE_NUM_FLAG" + ",TAppr.RULE_NUM_AT" + ",TAppr.RULE_NUM, " + RuleNumAtApprDesc
				+ ",TAppr.RULE_NUM_CHAR" + ",TAppr.RULE_LEN_MIN" + ",TAppr.RULE_LEN_MAX" + ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS, " + RuleStatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR, " + RecordIndicatorApprDesc + ",TAppr.MAKER, " + makerApprDesc
				+ ",TAppr.VERIFIER, " + verifierApprDesc + ",TAppr.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TAppr.FILTER_CONDITION "
				+ " From RG_CRS_RULE_DETAILS TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  AND COLUMN_NAME = ?  AND RULE_TYPE = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.VISION_SBU_AT"
				+ ",TPend.VISION_SBU, " + VisionSbuAtPendDesc + ",TPend.RULE_ID" + ",TPend.COLUMN_NAME"
				+ ",TPend.RULE_TYPE_AT" + ",TPend.RULE_TYPE, " + RuleTypeAtPendDesc + ",TPend.RULE_DESCRIPTION"
				+ ",TPend.RULE_SEQUENCE" + ",TPend.LOOKUP_TABLE_NAME" + ",TPend.LOOKUP_COLUMN" + "," + lookupCondiPend
				+ ",TPend.VALUE_PATT_FLAG" + ",TPend.VALUE_PATT_VALUES" + ",TPend.RULE_ALPHA_FLAG"
				+ ",TPend.RULE_ALPHA_AT" + ",TPend.RULE_ALPHA, " + RuleAlphaAtPendDesc + ",TPend.RULE_ALPHA_CHAR"
				+ ",TPend.RULE_NUM_FLAG" + ",TPend.RULE_NUM_AT" + ",TPend.RULE_NUM, " + RuleNumAtPendDesc
				+ ",TPend.RULE_NUM_CHAR" + ",TPend.RULE_LEN_MIN" + ",TPend.RULE_LEN_MAX" + ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS, " + RuleStatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR, " + RecordIndicatorPendDesc + ",TPend.MAKER, " + makerPendDesc
				+ ",TPend.VERIFIER, " + verifierPendDesc + ",TPend.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TPend.FILTER_CONDITION "
				+ " From RG_CRS_RULE_DETAILS_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  AND COLUMN_NAME = ?  AND RULE_TYPE = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getVisionSbu();
		objParams[3] = dObj.getRuleId();
		objParams[4] = dObj.getColumnName();
		objParams[5] = dObj.getRuleType();

		try {
			if (!dObj.isVerificationRequired()) {
				intStatus = 0;
			}
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper());
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

	public List<RgCrsRuleDetailsVb> getQueryResultsAllByParent(RgCrsRuleVb dObj) {
		setServiceDefaults();

		List<RgCrsRuleDetailsVb> collTemp = null;

		String lookupCondiAppr = getDbFunction("CONVERT") + "(TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
		String lookupCondiPend = getDbFunction("CONVERT") + "(TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			lookupCondiAppr = getDbFunction("CONVERT") + "(VARCHAR(MAX), TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
			lookupCondiPend = getDbFunction("CONVERT") + "(VARCHAR(MAX), TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		}
//		CONVERT(VARCHAR(MAX), TAppr.LOOKUP_CONDITION) AS LOOKUP_CONDITION
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.VISION_SBU_AT"
				+ ",TAppr.VISION_SBU, " + VisionSbuAtApprDesc + ",TAppr.RULE_ID" + ",TAppr.COLUMN_NAME"
				+ ",TAppr.RULE_TYPE_AT" + ",TAppr.RULE_TYPE, " + RuleTypeAtApprDesc + ",TAppr.RULE_DESCRIPTION"
				+ ",TAppr.RULE_SEQUENCE" + ",TAppr.LOOKUP_TABLE_NAME" + ",TAppr.LOOKUP_COLUMN" + "," + lookupCondiAppr
				+ ",TAppr.VALUE_PATT_FLAG" + ",TAppr.VALUE_PATT_VALUES" + ",TAppr.RULE_ALPHA_FLAG"
				+ ",TAppr.RULE_ALPHA_AT" + ",TAppr.RULE_ALPHA, " + RuleAlphaAtApprDesc + ",TAppr.RULE_ALPHA_CHAR"
				+ ",TAppr.RULE_NUM_FLAG" + ",TAppr.RULE_NUM_AT" + ",TAppr.RULE_NUM, " + RuleNumAtApprDesc
				+ ",TAppr.RULE_NUM_CHAR" + ",TAppr.RULE_LEN_MIN" + ",TAppr.RULE_LEN_MAX" + ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS, " + RuleStatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR, " + RecordIndicatorApprDesc + ",TAppr.MAKER, " + makerApprDesc
				+ ",TAppr.VERIFIER, " + verifierApprDesc + ",TAppr.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TAppr.FILTER_CONDITION "
				+ " From RG_CRS_RULE_DETAILS TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ? ");

		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From RG_CRS_RULE_DETAILS_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.VISION_SBU = TPend.VISION_SBU AND TAppr.RULE_ID = TPend.RULE_ID AND TAppr.COLUMN_NAME = TPend.COLUMN_NAME AND TAppr.RULE_TYPE = TPend.RULE_TYPE )");

		StringBuffer strQueryPend = new StringBuffer("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.VISION_SBU_AT"
				+ ",TPend.VISION_SBU, " + VisionSbuAtPendDesc + ",TPend.RULE_ID" + ",TPend.COLUMN_NAME"
				+ ",TPend.RULE_TYPE_AT" + ",TPend.RULE_TYPE, " + RuleTypeAtPendDesc + ",TPend.RULE_DESCRIPTION"
				+ ",TPend.RULE_SEQUENCE" + ",TPend.LOOKUP_TABLE_NAME" + ",TPend.LOOKUP_COLUMN" + "," + lookupCondiPend
				+ ",TPend.VALUE_PATT_FLAG" + ",TPend.VALUE_PATT_VALUES" + ",TPend.RULE_ALPHA_FLAG"
				+ ",TPend.RULE_ALPHA_AT" + ",TPend.RULE_ALPHA, " + RuleAlphaAtPendDesc + ",TPend.RULE_ALPHA_CHAR"
				+ ",TPend.RULE_NUM_FLAG" + ",TPend.RULE_NUM_AT" + ",TPend.RULE_NUM, " + RuleNumAtPendDesc
				+ ",TPend.RULE_NUM_CHAR" + ",TPend.RULE_LEN_MIN" + ",TPend.RULE_LEN_MAX" + ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS, " + RuleStatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR, " + RecordIndicatorPendDesc + ",TPend.MAKER, " + makerPendDesc
				+ ",TPend.VERIFIER, " + verifierPendDesc + ",TPend.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TPend.FILTER_CONDITION "
				+ " From RG_CRS_RULE_DETAILS_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ? ");

		String orderBy = " Order By COUNTRY, LE_BOOK, VISION_SBU, RULE_ID, RULE_SEQUENCE ";

		Object objParams[] = null;

		try {
			Vector<Object> params = new Vector<Object>();
			params.addElement(dObj.getCountry());
			params.addElement(dObj.getLeBook());
			params.addElement(dObj.getVisionSbu());
			params.addElement(dObj.getRuleId());

			int Ctr = 0;
			int Ctr2 = 0;
			String query = "";
			if (dObj.isVerificationRequired()) {
				objParams = new Object[params.size() * 2];
				for (Ctr = 0; Ctr < params.size(); Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr);
				for (Ctr2 = 0; Ctr2 < params.size(); Ctr2++, Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr2);

				strBufApprove.append(" AND " + strWhereNotExists);
				strQueryPend.append(orderBy);

				query = strBufApprove.toString() + " Union " + strQueryPend.toString();

			} else {
				objParams = new Object[params.size()];
				for (Ctr = 0; Ctr < params.size(); Ctr++)
					objParams[Ctr] = (Object) params.elementAt(Ctr);
				strBufApprove.append(orderBy);
				query = strBufApprove.toString();
			}
			collTemp = getJdbcTemplate().query(query, objParams, getMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	public List<RgCrsRuleDetailsVb> getQueryApprOrPendResultsByParent(RgCrsRuleVb dObj, int intStatus) {
		setServiceDefaults();
		List<RgCrsRuleDetailsVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String lookupCondiAppr = getDbFunction("CONVERT") + "(TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
		String lookupCondiPend = getDbFunction("CONVERT") + "(TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			lookupCondiAppr = getDbFunction("CONVERT") + "(VARCHAR(MAX), TAppr.LOOKUP_CONDITION) LOOKUP_CONDITION";
			lookupCondiPend = getDbFunction("CONVERT") + "(VARCHAR(MAX), TPend.LOOKUP_CONDITION) LOOKUP_CONDITION";
		}

		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.VISION_SBU_AT"
				+ ",TAppr.VISION_SBU, " + VisionSbuAtApprDesc + ",TAppr.RULE_ID" + ",TAppr.COLUMN_NAME"
				+ ",TAppr.RULE_TYPE_AT" + ",TAppr.RULE_TYPE, " + RuleTypeAtApprDesc + ",TAppr.RULE_DESCRIPTION"
				+ ",TAppr.RULE_SEQUENCE" + ",TAppr.LOOKUP_TABLE_NAME" + ",TAppr.LOOKUP_COLUMN" + "," + lookupCondiAppr
				+ ",TAppr.VALUE_PATT_FLAG" + ",TAppr.VALUE_PATT_VALUES" + ",TAppr.RULE_ALPHA_FLAG"
				+ ",TAppr.RULE_ALPHA_AT" + ",TAppr.RULE_ALPHA, " + RuleAlphaAtApprDesc + ",TAppr.RULE_ALPHA_CHAR"
				+ ",TAppr.RULE_NUM_FLAG" + ",TAppr.RULE_NUM_AT" + ",TAppr.RULE_NUM, " + RuleNumAtApprDesc
				+ ",TAppr.RULE_NUM_CHAR" + ",TAppr.RULE_LEN_MIN" + ",TAppr.RULE_LEN_MAX" + ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS, " + RuleStatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR, " + RecordIndicatorApprDesc + ",TAppr.MAKER, " + makerApprDesc
				+ ",TAppr.VERIFIER, " + verifierApprDesc + ",TAppr.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TAppr.FILTER_CONDITION "
				+ " From RG_CRS_RULE_DETAILS TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ? ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.VISION_SBU_AT"
				+ ",TPend.VISION_SBU, " + VisionSbuAtPendDesc + ",TPend.RULE_ID" + ",TPend.COLUMN_NAME"
				+ ",TPend.RULE_TYPE_AT" + ",TPend.RULE_TYPE, " + RuleTypeAtPendDesc + ",TPend.RULE_DESCRIPTION"
				+ ",TPend.RULE_SEQUENCE" + ",TPend.LOOKUP_TABLE_NAME" + ",TPend.LOOKUP_COLUMN" + "," + lookupCondiPend
				+ ",TPend.VALUE_PATT_FLAG" + ",TPend.VALUE_PATT_VALUES" + ",TPend.RULE_ALPHA_FLAG"
				+ ",TPend.RULE_ALPHA_AT" + ",TPend.RULE_ALPHA, " + RuleAlphaAtPendDesc + ",TPend.RULE_ALPHA_CHAR"
				+ ",TPend.RULE_NUM_FLAG" + ",TPend.RULE_NUM_AT" + ",TPend.RULE_NUM, " + RuleNumAtPendDesc
				+ ",TPend.RULE_NUM_CHAR" + ",TPend.RULE_LEN_MIN" + ",TPend.RULE_LEN_MAX" + ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS, " + RuleStatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR, " + RecordIndicatorPendDesc + ",TPend.MAKER, " + makerPendDesc
				+ ",TPend.VERIFIER, " + verifierPendDesc + ",TPend.INTERNAL_STATUS" + ", " + dateFormat
				+ "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat
				+ "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, TPend.FILTER_CONDITION "
				+ " From RG_CRS_RULE_DETAILS_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ? ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getVisionSbu();
		objParams[3] = dObj.getRuleId();

		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), objParams, getMapper());
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

	@Override
	protected List<RgCrsRuleDetailsVb> selectApprovedRecord(RgCrsRuleDetailsVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<RgCrsRuleDetailsVb> doSelectPendingRecord(RgCrsRuleDetailsVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(RgCrsRuleDetailsVb records) {
		return records.getRuleStatus();
	}

	@Override
	protected void setStatus(RgCrsRuleDetailsVb vObject, int status) {
		vObject.setRuleStatus(status);
	}

	@Override
	protected int doInsertionAppr(RgCrsRuleDetailsVb vObject) {
		String query = "Insert Into RG_CRS_RULE_DETAILS (COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, RULE_ID, COLUMN_NAME, RULE_TYPE_AT, RULE_TYPE, "
				+ "RULE_DESCRIPTION, RULE_SEQUENCE, LOOKUP_TABLE_NAME, LOOKUP_COLUMN, VALUE_PATT_FLAG, VALUE_PATT_VALUES, RULE_ALPHA_FLAG, "
				+ "RULE_ALPHA_AT, RULE_ALPHA, RULE_ALPHA_CHAR, RULE_NUM_FLAG, RULE_NUM_AT, RULE_NUM, RULE_NUM_CHAR, RULE_LEN_MIN, RULE_LEN_MAX, RULE_STATUS_NT, "
				+ "RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, FILTER_CONDITION, DATE_LAST_MODIFIED, DATE_CREATION, LOOKUP_CONDITION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + systemDate + ", ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbuAt(), vObject.getVisionSbu(),
				vObject.getRuleId(), vObject.getColumnName(), vObject.getRuleTypeAt(), vObject.getRuleType(),
				vObject.getRuleDescription(), vObject.getRuleSequence(), vObject.getLookupTableName(),
				vObject.getLookupColumn(), vObject.getValuePattFlag(), vObject.getValuePattValues(),
				vObject.getRuleAlphaFlag(), vObject.getRuleAlphaAt(), vObject.getRuleAlpha(),
				vObject.getRuleAlphaChar(), vObject.getRuleNumFlag(), vObject.getRuleNumAt(), vObject.getRuleNum(),
				vObject.getRuleNumChar(), vObject.getRuleLenMin(), vObject.getRuleLenMax(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getFilterCondition() };
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					int argumentLength = args.length;
					PreparedStatement ps = connection.prepareStatement(query);
					for (int i = 1; i <= argumentLength; i++) {
						ps.setObject(i, args[i - 1]);
					}
					String clobData = ValidationUtil.isValid(vObject.getLookupCondition())
							? vObject.getLookupCondition()
							: "";
					ps.setCharacterStream(++argumentLength, new StringReader(clobData), clobData.length());

					return ps;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in EDW Object: " + e.getMessage());
		}
		return result;
	}

	@Override
	protected int doInsertionPend(RgCrsRuleDetailsVb vObject) {
		String query = "Insert Into RG_CRS_RULE_DETAILS_PEND (COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, RULE_ID, COLUMN_NAME, RULE_TYPE_AT, RULE_TYPE, "
				+ "RULE_DESCRIPTION, RULE_SEQUENCE, LOOKUP_TABLE_NAME, LOOKUP_COLUMN, VALUE_PATT_FLAG, VALUE_PATT_VALUES, RULE_ALPHA_FLAG, "
				+ "RULE_ALPHA_AT, RULE_ALPHA, RULE_ALPHA_CHAR, RULE_NUM_FLAG, RULE_NUM_AT, RULE_NUM, RULE_NUM_CHAR, RULE_LEN_MIN, RULE_LEN_MAX, RULE_STATUS_NT, "
				+ "RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, FILTER_CONDITION, DATE_LAST_MODIFIED, DATE_CREATION, LOOKUP_CONDITION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + systemDate + ", ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbuAt(), vObject.getVisionSbu(),
				vObject.getRuleId(), vObject.getColumnName(), vObject.getRuleTypeAt(), vObject.getRuleType(),
				vObject.getRuleDescription(), vObject.getRuleSequence(), vObject.getLookupTableName(),
				vObject.getLookupColumn(), vObject.getValuePattFlag(), vObject.getValuePattValues(),
				vObject.getRuleAlphaFlag(), vObject.getRuleAlphaAt(), vObject.getRuleAlpha(),
				vObject.getRuleAlphaChar(), vObject.getRuleNumFlag(), vObject.getRuleNumAt(), vObject.getRuleNum(),
				vObject.getRuleNumChar(), vObject.getRuleLenMin(), vObject.getRuleLenMax(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getFilterCondition() };
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					int argumentLength = args.length;
					PreparedStatement ps = connection.prepareStatement(query);
					for (int i = 1; i <= argumentLength; i++) {
						ps.setObject(i, args[i - 1]);
					}
					String clobData = ValidationUtil.isValid(vObject.getLookupCondition())
							? vObject.getLookupCondition()
							: "";
					ps.setCharacterStream(++argumentLength, new StringReader(clobData), clobData.length());

					return ps;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in EDW Object: " + e.getMessage());
		}
		return result;
	}

	@Override
	protected int doInsertionPendWithDc(RgCrsRuleDetailsVb vObject) {
		String query = "Insert Into RG_CRS_RULE_DETAILS_PEND (COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, RULE_ID, COLUMN_NAME, RULE_TYPE_AT, RULE_TYPE, "
				+ "RULE_DESCRIPTION, RULE_SEQUENCE, LOOKUP_TABLE_NAME, LOOKUP_COLUMN, VALUE_PATT_FLAG, VALUE_PATT_VALUES, RULE_ALPHA_FLAG, "
				+ "RULE_ALPHA_AT, RULE_ALPHA, RULE_ALPHA_CHAR, RULE_NUM_FLAG, RULE_NUM_AT, RULE_NUM, RULE_NUM_CHAR, RULE_LEN_MIN, RULE_LEN_MAX, RULE_STATUS_NT, "
				+ "RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, FILTER_CONDITION, DATE_LAST_MODIFIED, DATE_CREATION, LOOKUP_CONDITION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + dateTimeConvert + ", ?)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbuAt(), vObject.getVisionSbu(),
				vObject.getRuleId(), vObject.getColumnName(), vObject.getRuleTypeAt(), vObject.getRuleType(),
				vObject.getRuleDescription(), vObject.getRuleSequence(), vObject.getLookupTableName(),
				vObject.getLookupColumn(), vObject.getValuePattFlag(), vObject.getValuePattValues(),
				vObject.getRuleAlphaFlag(), vObject.getRuleAlphaAt(), vObject.getRuleAlpha(),
				vObject.getRuleAlphaChar(), vObject.getRuleNumFlag(), vObject.getRuleNumAt(), vObject.getRuleNum(),
				vObject.getRuleNumChar(), vObject.getRuleLenMin(), vObject.getRuleLenMax(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getFilterCondition(),
				vObject.getDateCreation() };
		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					int argumentLength = args.length;
					PreparedStatement ps = connection.prepareStatement(query);
					for (int i = 1; i <= argumentLength; i++) {
						ps.setObject(i, args[i - 1]);
					}
					String clobData = ValidationUtil.isValid(vObject.getLookupCondition())
							? vObject.getLookupCondition()
							: "";
					ps.setCharacterStream(++argumentLength, new StringReader(clobData), clobData.length());

					return ps;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in EDW Object: " + e.getMessage());
		}
		return result;
	}

	@Override
	protected int doUpdateAppr(RgCrsRuleDetailsVb vObject) {
		String query = "Update RG_CRS_RULE_DETAILS Set LOOKUP_CONDITION = ?,  VISION_SBU_AT = ?, RULE_TYPE_AT = ?, RULE_DESCRIPTION = ?, RULE_SEQUENCE = ?, LOOKUP_TABLE_NAME = ?, "
				+ "LOOKUP_COLUMN = ?, VALUE_PATT_FLAG = ?, VALUE_PATT_VALUES = ?, RULE_ALPHA_FLAG = ?, RULE_ALPHA_AT = ?, RULE_ALPHA = ?, "
				+ "RULE_ALPHA_CHAR = ?, RULE_NUM_FLAG = ?, RULE_NUM_AT = ?, RULE_NUM = ?, RULE_NUM_CHAR = ?, RULE_LEN_MIN = ?, RULE_LEN_MAX = ?, RULE_STATUS_NT = ?, "
				+ "RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, FILTER_CONDITION = ? , DATE_LAST_MODIFIED = "
				+ systemDate + "  "
				+ "WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  AND COLUMN_NAME = ?  AND RULE_TYPE = ? ";
		Object[] args = { vObject.getVisionSbuAt(), vObject.getRuleTypeAt(), vObject.getRuleDescription(),
				vObject.getRuleSequence(), vObject.getLookupTableName(), vObject.getLookupColumn(),
				vObject.getValuePattFlag(), vObject.getValuePattValues(), vObject.getRuleAlphaFlag(),
				vObject.getRuleAlphaAt(), vObject.getRuleAlpha(), vObject.getRuleAlphaChar(), vObject.getRuleNumFlag(),
				vObject.getRuleNumAt(), vObject.getRuleNum(), vObject.getRuleNumChar(), vObject.getRuleLenMin(),
				vObject.getRuleLenMax(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getFilterCondition(),

				vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId(),
				vObject.getColumnName(), vObject.getRuleType() };

		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query);
					int psIndex = 0;
					String clobData = ValidationUtil.isValid(vObject.getLookupCondition())
							? vObject.getLookupCondition()
							: "";
					ps.setCharacterStream(++psIndex, new StringReader(clobData), clobData.length());

					for (int i = 1; i <= args.length; i++) {
						ps.setObject(++psIndex, args[i - 1]);
					}
					return ps;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in EDW Object: " + e.getMessage());
		}
		return result;
	}

	@Override
	protected int doUpdatePend(RgCrsRuleDetailsVb vObject) {
		String query = "Update RG_CRS_RULE_DETAILS_PEND Set LOOKUP_CONDITION = ?,  VISION_SBU_AT = ?, RULE_TYPE_AT = ?, RULE_DESCRIPTION = ?, RULE_SEQUENCE = ?, LOOKUP_TABLE_NAME = ?, "
				+ "LOOKUP_COLUMN = ?, VALUE_PATT_FLAG = ?, VALUE_PATT_VALUES = ?, RULE_ALPHA_FLAG = ?, RULE_ALPHA_AT = ?, RULE_ALPHA = ?, "
				+ "RULE_ALPHA_CHAR = ?, RULE_NUM_FLAG = ?, RULE_NUM_AT = ?, RULE_NUM = ?, RULE_NUM_CHAR = ?, RULE_LEN_MIN = ?, RULE_LEN_MAX = ?, RULE_STATUS_NT = ?, "
				+ "RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, FILTER_CONDITION = ? , DATE_LAST_MODIFIED = "
				+ systemDate + "  "
				+ "WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  AND COLUMN_NAME = ?  AND RULE_TYPE = ? ";
		Object[] args = { vObject.getVisionSbuAt(), vObject.getRuleTypeAt(), vObject.getRuleDescription(),
				vObject.getRuleSequence(), vObject.getLookupTableName(), vObject.getLookupColumn(),
				vObject.getValuePattFlag(), vObject.getValuePattValues(), vObject.getRuleAlphaFlag(),
				vObject.getRuleAlphaAt(), vObject.getRuleAlpha(), vObject.getRuleAlphaChar(), vObject.getRuleNumFlag(),
				vObject.getRuleNumAt(), vObject.getRuleNum(), vObject.getRuleNumChar(), vObject.getRuleLenMin(),
				vObject.getRuleLenMax(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getFilterCondition(),

				vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId(),
				vObject.getColumnName(), vObject.getRuleType() };

		int result = 0;
		try {
			return getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(query);
					int psIndex = 0;
					String clobData = ValidationUtil.isValid(vObject.getLookupCondition())
							? vObject.getLookupCondition()
							: "";
					ps.setCharacterStream(++psIndex, new StringReader(clobData), clobData.length());

					for (int i = 1; i <= args.length; i++) {
						ps.setObject(++psIndex, args[i - 1]);
					}
					return ps;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage();
			logger.error("inset Error in EDW Object: " + e.getMessage());
		}
		return result;
	}

	@Override
	protected int doDeleteAppr(RgCrsRuleDetailsVb vObject) {
		String query = "Delete From RG_CRS_RULE_DETAILS Where COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  AND COLUMN_NAME = ?  AND RULE_TYPE = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId(),
				vObject.getColumnName(), vObject.getRuleType() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(RgCrsRuleDetailsVb vObject) {
		String query = "Delete From RG_CRS_RULE_DETAILS_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  AND COLUMN_NAME = ?  AND RULE_TYPE = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId(),
				vObject.getColumnName(), vObject.getRuleType() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(RgCrsRuleDetailsVb vObject) {
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
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

		strAudit.append("VISION_SBU_AT" + auditDelimiterColVal + vObject.getVisionSbuAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getVisionSbu()))
			strAudit.append("VISION_SBU" + auditDelimiterColVal + vObject.getVisionSbu().trim());
		else
			strAudit.append("VISION_SBU" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleId()))
			strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId().trim());
		else
			strAudit.append("RULE_ID" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getColumnName()))
			strAudit.append("COLUMN_NAME" + auditDelimiterColVal + vObject.getColumnName().trim());
		else
			strAudit.append("COLUMN_NAME" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_TYPE_AT" + auditDelimiterColVal + vObject.getRuleTypeAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleType()))
			strAudit.append("RULE_TYPE" + auditDelimiterColVal + vObject.getRuleType().trim());
		else
			strAudit.append("RULE_TYPE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleDescription()))
			strAudit.append("RULE_DESCRIPTION" + auditDelimiterColVal + vObject.getRuleDescription().trim());
		else
			strAudit.append("RULE_DESCRIPTION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_SEQUENCE" + auditDelimiterColVal + vObject.getRuleSequence());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getLookupTableName()))
			strAudit.append("LOOKUP_TABLE_NAME" + auditDelimiterColVal + vObject.getLookupTableName().trim());
		else
			strAudit.append("LOOKUP_TABLE_NAME" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getLookupColumn()))
			strAudit.append("LOOKUP_COLUMN" + auditDelimiterColVal + vObject.getLookupColumn().trim());
		else
			strAudit.append("LOOKUP_COLUMN" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getLookupCondition()))
			strAudit.append("LOOKUP_CONDITION" + auditDelimiterColVal + vObject.getLookupCondition().trim());
		else
			strAudit.append("LOOKUP_CONDITION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getValuePattFlag()))
			strAudit.append("VALUE_PATT_FLAG" + auditDelimiterColVal + vObject.getValuePattFlag().trim());
		else
			strAudit.append("VALUE_PATT_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getValuePattValues()))
			strAudit.append("VALUE_PATT_VALUES" + auditDelimiterColVal + vObject.getValuePattValues().trim());
		else
			strAudit.append("VALUE_PATT_VALUES" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleAlphaFlag()))
			strAudit.append("RULE_ALPHA_FLAG" + auditDelimiterColVal + vObject.getRuleAlphaFlag().trim());
		else
			strAudit.append("RULE_ALPHA_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_ALPHA_AT" + auditDelimiterColVal + vObject.getRuleAlphaAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleAlpha()))
			strAudit.append("RULE_ALPHA" + auditDelimiterColVal + vObject.getRuleAlpha().trim());
		else
			strAudit.append("RULE_ALPHA" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleAlphaChar()))
			strAudit.append("RULE_ALPHA_CHAR" + auditDelimiterColVal + vObject.getRuleAlphaChar().trim());
		else
			strAudit.append("RULE_ALPHA_CHAR" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleNumFlag()))
			strAudit.append("RULE_NUM_FLAG" + auditDelimiterColVal + vObject.getRuleNumFlag().trim());
		else
			strAudit.append("RULE_NUM_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_NUM_AT" + auditDelimiterColVal + vObject.getRuleNumAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleNum()))
			strAudit.append("RULE_NUM" + auditDelimiterColVal + vObject.getRuleNum().trim());
		else
			strAudit.append("RULE_NUM" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleNumChar()))
			strAudit.append("RULE_NUM_CHAR" + auditDelimiterColVal + vObject.getRuleNumChar().trim());
		else
			strAudit.append("RULE_NUM_CHAR" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_LEN_MIN" + auditDelimiterColVal + vObject.getRuleLenMin());
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_LEN_MAX" + auditDelimiterColVal + vObject.getRuleLenMax());
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_STATUS_NT" + auditDelimiterColVal + vObject.getRuleStatusNt());
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_STATUS" + auditDelimiterColVal + vObject.getRuleStatus());
		strAudit.append(auditDelimiter);

		strAudit.append("RECORD_INDICATOR_NT" + auditDelimiterColVal + vObject.getRecordIndicatorNt());
		strAudit.append(auditDelimiter);

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

		return strAudit.toString();
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "RgCrsRuleDetails";
		serviceDesc = "RG CRS Rule Details";
		tableName = "RG_CRS_RULE_DETAILS";
		childTableName = "RG_CRS_RULE_DETAILS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

//	public StringBuffer getDisplayQuery(RgCrsRuleDetailsVb dObj) {
//		StringBuffer strBufApprove = new StringBuffer("SELECT T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T2.CUSTOMER_NAME, T1.VISION_SBU_AT, T1.VISION_SBU, \r\n"
//				+ "T1.RULE_ID,\r\n"
//				+ "T3.RULE_DESCRIPTION, \r\n"
//				+dateFormat+"(RUN_DATE, "+formatdate+") RUN_DATE"
//				+ ", VERSION_NO, t1.PRIORITY, T1.CRS_FLAG, T1.CRS_OVERRIDE, FINAL_CRS_FLAG \r\n"
//				+ "FROM RG_CRS_RULE_AUDIT T1 JOIN CUSTOMERS T2 ON T1.COUNTRY = T2.COUNTRY                                  \r\n"
//				+ "	AND T1.LE_BOOK = T2.LE_BOOK                                  \r\n"
//				+ "	AND T1.CUSTOMER_ID = T2.CUSTOMER_ID                          \r\n"
//				+ "	LEFT JOIN RG_CRS_RULE T3 ON T1.COUNTRY = T3.COUNTRY                             \r\n"
//				+ "	AND T1.LE_BOOK = T3.LE_BOOK                             \r\n"
//				+ "	AND T1.RULE_ID = T3.RULE_ID\r\n"
//				+ "	AND T1.VISION_SBU = T3.VISION_SBU\r\n"
//				+ "	WHERE T1.COUNTRY = '"+dObj.getCountry()+"' AND T1.LE_BOOK = '"+dObj.getLeBook()+"' AND T1.VISION_SBU = '"+dObj.getVisionSbu()+"' \r\n"
//				+ " AND "+dateFormat+"(T1.RUN_DATE, "+formatdate+") = "+dateFormat+"(SYSDATE, "+formatdate+") "
//				+ "	AND T1.VERSION_NO = (\r\n"
//				+ " SELECT MAX(VERSION_NO) \r\n"
//				+ " FROM RG_CRS_RULE_AUDIT T2\r\n"
//				+ " WHERE T2.COUNTRY = T1.COUNTRY\r\n"
//				+ " AND T2.LE_BOOK = T1.LE_BOOK\r\n"
//				+ " AND T2.CUSTOMER_ID = T1.CUSTOMER_ID\r\n"
//				+ " AND T2.RULE_ID = T1.RULE_ID \r\n"
//				+ " AND T2.RUN_DATE = T1.RUN_DATE\r\n"
//				+ " )");
//		return strBufApprove;
//	}
//	public List<RgCrsRuleDetailsVb> getQueryResultsToDisplay(RgCrsRuleDetailsVb dObj) {
//		List<RgCrsRuleDetailsVb> collTemp = null;
//		dObj.setVerificationRequired(false);
//
//		MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
//	magnifierResultVb.setPromptValue1("'CUSTOMERS'");
//	magnifierResultVb.setQuery("CUSTCOLUMN");
//	magnifierResultVb.setLastIndex(50);
//	magnifierResultVb.setCurrentPage(1);
//	List<MagnifierResultVb> collTmp = magnifierDao.getQueryPopupResults(magnifierResultVb);
//	StringJoiner magColumns = new StringJoiner(",");
//	if (collTmp != null && collTmp.size() > 0) {
//		collTmp.forEach(n->{
//			magColumns.add("T1."+n.getColumnNine() +" AS "+n.getColumnNine());
//		});
//	}
//		String runDateExpr = (databaseType.equalsIgnoreCase("MSSQL") || databaseType.equalsIgnoreCase("SQLSERVER"))
//				? "CONVERT(VARCHAR(11), T2.RUN_DATE, 106)" // SQL Server
//				: "TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY')"; // Oracle
//
//		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (SELECT " + "    T1.COUNTRY, " + "    T1.LE_BOOK, "
//				+ "    T1.CUSTOMER_ID, " + "    T1.CUSTOMER_NAME, " + "    T1.VISION_SBU_AT, " + "    T1.VISION_SBU, "
//				+ "    T2.RULE_ID, " + "    T2.RULE_DESCRIPTION, " + "    " + runDateExpr + " AS RUN_DATE, "
//				+ "    T2.VERSION_NO, " + "    T2.PRIORITY, " + "    COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
//				+ "    T1.CRS_OVERRIDE, " + "    T1.CRS_FLAG AS FINAL_CRS_FLAG " + "FROM CUSTOMERS T1 "
//				+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 " + "    ON T1.COUNTRY     = T2.COUNTRY "
//				+ "   AND T1.LE_BOOK     = T2.LE_BOOK " + "   AND T1.CUSTOMER_ID = T2.CUSTOMER_ID " + "LEFT JOIN ( "
//				+ "    SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(T2.RUN_DATE) AS Run_Date "
//				+ "    FROM RG_CRS_RULE_AUDIT T2 " + "    WHERE T2.COUNTRY    = ? " + "      AND T2.LE_BOOK    = ? "
//				+ "      AND T2.VISION_SBU = ? " + "    GROUP BY T2.Country, T2.LE_Book, T2.Customer_id " + ") T3 "
//				+ "   ON T2.COUNTRY     = T3.COUNTRY " + "  AND T2.LE_BOOK     = T3.LE_BOOK "
//				+ "  AND T2.CUSTOMER_ID = T3.CUSTOMER_ID " + "  AND T2.RUN_DATE    = T3.RUN_DATE "
//				+ "WHERE T1.COUNTRY    = ? " + "  AND T1.LE_BOOK    = ? " + "  AND T1.VISION_SBU = ? "
//				+ "  AND COALESCE(T2.VERSION_NO,0) = COALESCE(( " + "        SELECT MAX(T21.VERSION_NO) "
//				+ "        FROM RG_CRS_RULE_AUDIT T21 " + "        WHERE T21.COUNTRY     = T3.COUNTRY "
//				+ "          AND T21.LE_BOOK     = T3.LE_BOOK " + "          AND T21.CUSTOMER_ID = T3.CUSTOMER_ID "
//				+ "          AND T21.RUN_DATE    = T3.RUN_DATE " + "  ),0) "
//				+ "  AND (COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y'))TAPPR");
//
//		String orderBy = "ORDER BY PRIORITY";
//
//		Object objParams[] = null;
//
//		try {
//			Vector<Object> params = new Vector<Object>();
//			params.addElement(dObj.getCountry());
//			params.addElement(dObj.getLeBook());
//			params.addElement(dObj.getVisionSbu());
//			params.addElement(dObj.getCountry());
//			params.addElement(dObj.getLeBook());
//			params.addElement(dObj.getVisionSbu());
//			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
//				int count = 1;
//				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
//					if (count == dObj.getSmartSearchOpt().size()) {
//						data.setJoinType("");
//					} else {
//						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
//								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
//							data.setJoinType("AND");
//						}
//					}
//					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
//					switch (data.getObject()) {
//					case "customerId":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "customerName":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "ruleId":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_ID) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "ruleDescription":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_DESCRIPTION) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "priority":
//						CommonUtils.addToQuerySearch("upper( TAPPR.PRIORITY )" + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "crsFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_FLAG )" + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "crsOverrideFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_OVERRIDE) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "finalFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.FINAL_CRS_FLAG) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "dateCreation":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RUN_DATE) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					default:
//						break;
//
//					}
//					count++;
//				}
//			}
////			RowMapper mapper = new RowMapper() {
////				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
////					RgCrsRuleDetailsVb vObject = new RgCrsRuleDetailsVb();
////					if (rs.getString("COUNTRY") != null) {
////						vObject.setCountry(rs.getString("COUNTRY"));
////					} else {
////						vObject.setCountry("");
////					}
////					if (rs.getString("LE_BOOK") != null) {
////						vObject.setLeBook(rs.getString("LE_BOOK"));
////					} else {
////						vObject.setLeBook("");
////					}
////					vObject.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
////					if (rs.getString("VISION_SBU") != null) {
////						vObject.setVisionSbu(rs.getString("VISION_SBU"));
////					} else {
////						vObject.setVisionSbu("");
////					}
////					if (rs.getString("RULE_ID") != null) {
////						vObject.setRuleId(rs.getString("RULE_ID"));
////					} else {
////						vObject.setRuleId("");
////					}
////					if (rs.getString("CUSTOMER_ID") != null) {
////						vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
////					}
////					if (rs.getString("CUSTOMER_NAME") != null) {
////						vObject.setCustomerName(rs.getString("CUSTOMER_NAME"));
////					}
////					if (rs.getString("RULE_DESCRIPTION") != null) {
////						vObject.setRuleDescription(rs.getString("RULE_DESCRIPTION"));
////					}
////					if (rs.getString("RUN_DATE") != null) {
////						vObject.setDateCreation(rs.getString("RUN_DATE"));
////					}
////
////					vObject.setPriority(rs.getInt("PRIORITY"));
////
////					if (rs.getString("CRS_FLAG") != null) {
////						vObject.setCrsFlag(rs.getString("CRS_FLAG"));
////					}
////
////					if (rs.getString("CRS_OVERRIDE") != null) {
////						vObject.setCrsOverrideFlag(rs.getString("CRS_OVERRIDE"));
////					}
////					if (rs.getString("FINAL_CRS_FLAG") != null) {
////						vObject.setFinalFlag(rs.getString("FINAL_CRS_FLAG"));
////					}
////
//////					if (rs.getString("COLUMN_NAME") != null) {
//////						vObject.setColumnName(rs.getString("COLUMN_NAME"));
//////					} else {
//////						vObject.setColumnName("");
//////					}
////
////					return vObject;
////				}
////			};
//			RowMapper<RgCrsRuleDetailsVb> mapper = (rs, rowNum) -> {
//			    RgCrsRuleDetailsVb vObject = new RgCrsRuleDetailsVb();
//
//			    // Standard field mapping (your original mapper logic)
//			    vObject.setCountry(rs.getString("COUNTRY") != null ? rs.getString("COUNTRY") : "");
//			    vObject.setLeBook(rs.getString("LE_BOOK") != null ? rs.getString("LE_BOOK") : "");
//			    vObject.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
//			    vObject.setVisionSbu(rs.getString("VISION_SBU") != null ? rs.getString("VISION_SBU") : "");
//			    vObject.setRuleId(rs.getString("RULE_ID") != null ? rs.getString("RULE_ID") : "");
//			    vObject.setCustomerId(rs.getString("CUSTOMER_ID") != null ? rs.getString("CUSTOMER_ID") : "");
//			    vObject.setCustomerName(rs.getString("CUSTOMER_NAME") != null ? rs.getString("CUSTOMER_NAME") : "");
//			    vObject.setRuleDescription(rs.getString("RULE_DESCRIPTION") != null ? rs.getString("RULE_DESCRIPTION") : "");
//			    vObject.setDateCreation(rs.getString("RUN_DATE"));
//			    vObject.setPriority(rs.getInt("PRIORITY"));
//			    vObject.setCrsFlag(rs.getString("CRS_FLAG"));
//			    vObject.setCrsOverrideFlag(rs.getString("CRS_OVERRIDE"));
//			    vObject.setFinalFlag(rs.getString("FINAL_CRS_FLAG"));
//
//			    // --- DYNAMIC MAPPER PART (ONLY FOR MAGNIFIER COLUMNS) ---
//			    if (collTmp != null && !collTmp.isEmpty()) {
//			        Set<String> allowedColumns = collTmp.stream()
//			                .map(n -> n.getColumnNine().toUpperCase())
//			                .collect(Collectors.toSet());
//
//			        ResultSetMetaData meta = rs.getMetaData();
//			        int colCount = meta.getColumnCount();
//
//			        for (int i = 1; i <= colCount; i++) {
//			            String colName = meta.getColumnLabel(i).toUpperCase();
//			            if (!allowedColumns.contains(colName)) continue; // skip non-magnifier columns
//
//			            Object value = rs.getObject(i);
//			            vObject.setDynamicField(colName, value); // store in dynamic map
//
//			            // Optional: try to call setter if exists
//			            try {
//			                String setterName = "set" + Character.toUpperCase(colName.charAt(0)) + colName.substring(1).toLowerCase();
//			                Method[] methods = vObject.getClass().getMethods();
//			                for (Method method : methods) {
//			                    if (method.getName().equalsIgnoreCase(setterName)) {
//			                        method.invoke(vObject, value);
//			                        break;
//			                    }
//			                }
//			            } catch (Exception e) {
//			                // ignore
//			            }
//			        }
//			    }
//
//			    return vObject;
//			};
//
////			collTemp = getJdbcTemplate().query(query,objParams,mapper);
//			return getQueryPopupResults(dObj, new StringBuffer(), strBufApprove, "", orderBy, params, mapper);
////			return collTemp;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.error("Error: getQueryResults Exception :   ");
//			if (objParams != null)
//				for (int i = 0; i < objParams.length; i++)
//					logger.error("objParams[" + i + "]" + objParams[i].toString());
//			return null;
//		}
//	}
//	public List<RgCrsRuleDetailsVb> getQueryResultsToDisplay(RgCrsRuleDetailsVb dObj) {
//		dObj.setVerificationRequired(false);
//
//		try {
//			// --- Prepare magnifier ---
//			MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
//			magnifierResultVb.setPromptValue1("'CUSTOMERS'");
//			magnifierResultVb.setQuery("CUSTCOLUMN");
//			magnifierResultVb.setMaxRecords(100);
//
//			// --- Standard columns (uppercase for comparison only) ---
//			Set<String> standardColumns = Set.of("COUNTRY", "LE_BOOK", "CUSTOMER_ID", "CUSTOMER_NAME", "VISION_SBU_AT",
//					"VISION_SBU", "RULE_ID", "RULE_DESCRIPTION", "RUN_DATE", "VERSION_NO", "PRIORITY", "CRS_FLAG",
//					"CRS_OVERRIDE", "FINAL_CRS_FLAG");
//
//			// --- Fetch magnifier columns ---
//			List<MagnifierResultVb> collTmp = magnifierDao.getQueryPopupResults(magnifierResultVb);
//			Set<String> magnifierColumnsSet = new LinkedHashSet<>(); // SQL expressions for query
//			Set<String> magnifierColumnNames = new HashSet<>(); // Alias names for RowMapper
//
//			if (collTmp != null && !collTmp.isEmpty()) {
//				for (MagnifierResultVb n : collTmp) {
//					String columnName = n.getColumnNine();
//					String aliasName = n.getColumnTen();
//
//					if (ValidationUtil.isValid(columnName)
//							&& !standardColumns.contains(columnName.trim().toUpperCase())) {
//
//						// Preserve alias exactly as in DB
//						String sqlExpr = "T1." + columnName.trim() + " AS \"" + aliasName.trim() + "\"";
//						magnifierColumnsSet.add(sqlExpr);
//						magnifierColumnNames.add(aliasName.trim());
//					}
//				}
//			}
//
//			// --- Convert magnifierColumns set to SQL string ---
//			String magColumnsStr = magnifierColumnsSet.isEmpty() ? "" : ", " + String.join(", ", magnifierColumnsSet);
//
//			// --- Run date expression ---
//			String runDateExpr = (databaseType.equalsIgnoreCase("MSSQL") || databaseType.equalsIgnoreCase("SQLSERVER"))
//					? "CONVERT(VARCHAR(11), T2.RUN_DATE, 106)" // SQL Server
//					: "TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY')"; // Oracle
//
//			// --- Build main query ---
//			StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (SELECT "
//					+ "T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, " + "T1.VISION_SBU_AT, T1.VISION_SBU, "
//					+ "T2.RULE_ID, T2.RULE_DESCRIPTION, " + runDateExpr + " AS RUN_DATE, "
//					+ "T2.VERSION_NO, T2.PRIORITY, " + "COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
//					+ "T1.CRS_OVERRIDE, T1.CRS_FLAG AS FINAL_CRS_FLAG" + magColumnsStr + " FROM CUSTOMERS T1 "
//					+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 ON T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK AND T1.CUSTOMER_ID = T2.CUSTOMER_ID "
//					+ "LEFT JOIN (SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(T2.RUN_DATE) AS Run_Date "
//					+ "FROM RG_CRS_RULE_AUDIT T2 WHERE T2.COUNTRY = ? AND T2.LE_BOOK = ? AND T2.VISION_SBU = ? "
//					+ "GROUP BY T2.Country, T2.LE_Book, T2.Customer_id) T3 "
//					+ "ON T2.COUNTRY = T3.COUNTRY AND T2.LE_BOOK = T3.LE_BOOK AND T2.CUSTOMER_ID = T3.CUSTOMER_ID AND T2.RUN_DATE = T3.RUN_DATE "
//					+ "WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.VISION_SBU = ? "
//					+ "AND COALESCE(T2.VERSION_NO,0) = COALESCE((SELECT MAX(T21.VERSION_NO) FROM RG_CRS_RULE_AUDIT T21 "
//					+ "WHERE T21.COUNTRY = T3.COUNTRY AND T21.LE_BOOK = T3.LE_BOOK AND T21.CUSTOMER_ID = T3.CUSTOMER_ID AND T21.RUN_DATE = T3.RUN_DATE),0) "
//					+ "AND (COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')) TAPPR");
//
//			String orderBy = "ORDER BY PRIORITY";
//
//			// --- Query parameters ---
//			Vector<Object> params = new Vector<>();
//			params.add(dObj.getCountry());
//			params.add(dObj.getLeBook());
//			params.add(dObj.getVisionSbu());
//			params.add(dObj.getCountry());
//			params.add(dObj.getLeBook());
//			params.add(dObj.getVisionSbu());
//
//			// --- Add smart search filters ---
//			if (dObj.getSmartSearchOpt() != null && !dObj.getSmartSearchOpt().isEmpty()) {
//				int count = 1;
//				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
//					if (count == dObj.getSmartSearchOpt().size()) {
//						data.setJoinType("");
//					} else {
//						if (!ValidationUtil.isValid(data.getJoinType()) || (!"AND".equalsIgnoreCase(data.getJoinType())
//								&& !"OR".equalsIgnoreCase(data.getJoinType()))) {
//							data.setJoinType("AND");
//						}
//					}
//					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
//					switch (data.getObject()) {
//					case "customerId":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "customerName":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "ruleId":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_ID) " + val, strBufApprove, data.getJoinType());
//						break;
//					case "ruleDescription":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_DESCRIPTION) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "priority":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.PRIORITY) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "crsFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_FLAG) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "crsOverrideFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_OVERRIDE) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "finalFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.FINAL_CRS_FLAG) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					case "dateCreation":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RUN_DATE) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//					}
//					count++;
//				}
//			}
//
//			// --- RowMapper ---
//			RowMapper<RgCrsRuleDetailsVb> mapper = (rs, rowNum) -> {
//				RgCrsRuleDetailsVb vObject = new RgCrsRuleDetailsVb();
//
//				// --- Standard fields ---
//				vObject.setCountry(rs.getString("COUNTRY") != null ? rs.getString("COUNTRY") : "");
//				vObject.setLeBook(rs.getString("LE_BOOK") != null ? rs.getString("LE_BOOK") : "");
//				vObject.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
//				vObject.setVisionSbu(rs.getString("VISION_SBU") != null ? rs.getString("VISION_SBU") : "");
//				vObject.setRuleId(rs.getString("RULE_ID") != null ? rs.getString("RULE_ID") : "");
//				vObject.setCustomerId(rs.getString("CUSTOMER_ID") != null ? rs.getString("CUSTOMER_ID") : "");
//				vObject.setCustomerName(rs.getString("CUSTOMER_NAME") != null ? rs.getString("CUSTOMER_NAME") : "");
//				vObject.setRuleDescription(
//						rs.getString("RULE_DESCRIPTION") != null ? rs.getString("RULE_DESCRIPTION") : "");
//				vObject.setDateCreation(rs.getString("RUN_DATE"));
//				vObject.setPriority(rs.getInt("PRIORITY"));
//				vObject.setCrsFlag(rs.getString("CRS_FLAG"));
//				vObject.setCrsOverrideFlag(rs.getString("CRS_OVERRIDE"));
//				vObject.setFinalFlag(rs.getString("FINAL_CRS_FLAG"));
//
//				// --- Dynamic magnifier fields ---
//				ResultSetMetaData meta = rs.getMetaData();
//				int colCount = meta.getColumnCount();
//				for (int i = 1; i <= colCount; i++) {
//					String colLabel = meta.getColumnLabel(i); // alias from SQL
//					if (magnifierColumnNames.contains(colLabel)) {
//						Object value = rs.getObject(i);
//						vObject.setDynamicField(colLabel, value);
//					}
//				}
//
//				return vObject;
//			};
//
//			// --- Execute query ---
//			return getQueryPopupResults(dObj, new StringBuffer(), strBufApprove, "", orderBy, params, mapper);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.error("Error: getQueryResults Exception", ex);
//			return null;
//		}
//	}
//
//	public StringBuffer getDisplayQuery(RgCrsRuleDetailsVb dObj) {
//		String country = dObj.getCountry();
//		String leBook = dObj.getLeBook();
//		String visionSbu = dObj.getVisionSbu();
//
//		// WHERE clauses
//		String condition1 = String.format("WHERE T2.COUNTRY = %s AND T2.LE_BOOK = %s AND T2.VISION_SBU = %s",
//				quote(country), quote(leBook), quote(visionSbu));
//
//		String condition2 = String.format("WHERE T1.COUNTRY = %s AND T1.LE_BOOK = %s AND T1.VISION_SBU = %s",
//				quote(country), quote(leBook), quote(visionSbu));
//
//		String sql;
//
//		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
//			sql = String.format("SELECT " + "    T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, "
//					+ "    T1.VISION_SBU_AT, T1.VISION_SBU, " + "    T2.RULE_ID, T2.RULE_DESCRIPTION, "
//					+ "    CONVERT(VARCHAR(11), T2.RUN_DATE, 106) AS RUN_DATE, " + "    T2.VERSION_NO, T2.PRIORITY, "
//					+ "    ISNULL(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
//					+ "    T1.CRS_OVERRIDE, T1.CRS_FLAG AS FINAL_CRS_FLAG " + "FROM " + Customers + " T1 "
//					+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 " + "   ON T1.COUNTRY = T2.COUNTRY "
//					+ "  AND T1.LE_BOOK = T2.LE_BOOK " + "  AND T1.CUSTOMER_ID = T2.CUSTOMER_ID " + "LEFT JOIN ( "
//					+ "    SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(T2.RUN_DATE) AS Run_Date "
//					+ "    FROM RG_CRS_RULE_AUDIT T2 %s " + "    GROUP BY T2.Country, T2.LE_Book, T2.Customer_id "
//					+ ") T3 " + "   ON T2.COUNTRY = T3.COUNTRY " + "  AND T2.LE_BOOK = T3.LE_BOOK "
//					+ "  AND T2.CUSTOMER_ID = T3.CUSTOMER_ID " + "  AND T2.RUN_DATE = T3.RUN_DATE %s "
//					+ "AND ISNULL(T2.VERSION_NO,0) = ISNULL(( " + "    SELECT MAX(T21.VERSION_NO) "
//					+ "    FROM RG_CRS_RULE_AUDIT T21 " + "    WHERE T21.COUNTRY = T3.COUNTRY "
//					+ "      AND T21.LE_BOOK = T3.LE_BOOK " + "      AND T21.CUSTOMER_ID = T3.CUSTOMER_ID "
//					+ "      AND T21.RUN_DATE = T3.RUN_DATE " + "),0) "
//					+ "AND (ISNULL(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')", condition1, condition2);
//		} else {
//			sql = String.format("WITH Audit_Data AS ( "
//					+ "    SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(RUN_DATE) Run_Date "
//					+ "    FROM RG_CRS_RULE_AUDIT T2 %s " + "    GROUP BY T2.Country, T2.LE_Book, T2.Customer_id "
//					+ ") " + "SELECT " + "    T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, "
//					+ "    T1.VISION_SBU_AT, T1.VISION_SBU, " + "    T2.RULE_ID, T2.RULE_DESCRIPTION, "
//					+ "    TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY') RUN_DATE, " + "    T2.VERSION_NO, T2.PRIORITY, "
//					+ "    NVL(T2.CRS_FLAG, T1.CRS_FLAG) CRS_FLAG, "
//					+ "    T1.CRS_OVERRIDE, T1.CRS_FLAG FINAL_CRS_FLAG " + "FROM " + Customers + " T1 "
//					+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 " + "   ON T1.COUNTRY = T2.COUNTRY "
//					+ "  AND T1.LE_BOOK = T2.LE_BOOK " + "  AND T1.CUSTOMER_ID = T2.CUSTOMER_ID "
//					+ "LEFT JOIN Audit_Data T3 " + "   ON T2.COUNTRY = T3.COUNTRY " + "  AND T2.LE_BOOK = T3.LE_BOOK "
//					+ "  AND T2.CUSTOMER_ID = T3.CUSTOMER_ID " + "  AND T2.RUN_DATE = T3.RUN_DATE %s "
//					+ "AND NVL(T2.VERSION_NO, 0) = NVL(( " + "    SELECT MAX(T21.VERSION_NO) "
//					+ "    FROM RG_CRS_RULE_AUDIT T21 " + "    WHERE T21.COUNTRY = T3.COUNTRY "
//					+ "      AND T21.LE_BOOK = T3.LE_BOOK " + "      AND T21.CUSTOMER_ID = T3.CUSTOMER_ID "
//					+ "      AND T21.RUN_DATE = T3.RUN_DATE " + "),0) "
//					+ "AND (NVL(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')", condition1, condition2);
//		}
//
//		return new StringBuffer(sql);
//	}
//
//	/**
//	 * Safely wraps values in single quotes and escapes internal quotes.
//	 */
//	private String quote(String value) {
//		if (value == null) {
//			return "NULL";
//		}
//		return "'" + value.replace("'", "''") + "'";
//	}
//
//	public StringBuffer getDisplayQueryWithAllColumns(RgCrsRuleDetailsVb dObj) {
//		String country = dObj.getCountry();
//		String leBook = dObj.getLeBook();
//		String visionSbu = dObj.getVisionSbu();
//
//		// --- Prepare magnifier columns ---
//		MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
//		magnifierResultVb.setPromptValue1("'CUSTOMERS'");
//		magnifierResultVb.setQuery("CUSTCOLUMN");
//		magnifierResultVb.setMaxRecords(100);
//
//		Set<String> standardColumns = Set.of("COUNTRY", "LE_BOOK", "CUSTOMER_ID", "CUSTOMER_NAME", "VISION_SBU_AT",
//				"VISION_SBU", "RULE_ID", "RULE_DESCRIPTION", "RUN_DATE", "VERSION_NO", "PRIORITY", "CRS_FLAG",
//				"CRS_OVERRIDE", "FINAL_CRS_FLAG");
//
//		List<MagnifierResultVb> collTmp = magnifierDao.getQueryPopupResults(magnifierResultVb);
//		Set<String> magnifierColumnsSet = new LinkedHashSet<>();
//
//		if (collTmp != null && !collTmp.isEmpty()) {
//			for (MagnifierResultVb n : collTmp) {
//				String columnName = n.getColumnNine();
//				String aliasName = n.getColumnTen();
//
//				if (ValidationUtil.isValid(columnName) && !standardColumns.contains(columnName.trim().toUpperCase())) {
//
//					magnifierColumnsSet.add("T1." + columnName.trim() + " AS \"" + aliasName.trim() + "\"");
//				}
//			}
//		}
//
//		String magColumnsStr = magnifierColumnsSet.isEmpty() ? "" : ", " + String.join(", ", magnifierColumnsSet);
//
//		// --- COALESCE-based query (works for all platforms) ---
//		String sql = "SELECT * FROM (SELECT " + "T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, "
//				+ "T1.VISION_SBU_AT, T1.VISION_SBU, " + "T2.RULE_ID, T2.RULE_DESCRIPTION, "
////	            + "COALESCE(TO_CHAR(T2.RUN_DATE,'DD-Mon-YYYY'), TO_CHAR(T1.RUN_DATE,'DD-Mon-YYYY')) AS RUN_DATE, "
//				+ " TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY') as RUN_DATE,"
//
//				+ "T2.VERSION_NO, T2.PRIORITY, " + "COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
//				+ "T1.CRS_OVERRIDE, T1.CRS_FLAG AS FINAL_CRS_FLAG" + magColumnsStr + " FROM CUSTOMERS T1 "
//				+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 ON T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK AND T1.CUSTOMER_ID = T2.CUSTOMER_ID "
//				+ "LEFT JOIN (SELECT T2.COUNTRY, T2.LE_BOOK, T2.CUSTOMER_ID, MAX(T2.RUN_DATE) AS RUN_DATE "
//				+ "FROM RG_CRS_RULE_AUDIT T2 WHERE T2.COUNTRY = '" + country + "' AND T2.LE_BOOK = '" + leBook
//				+ "' AND T2.VISION_SBU = '" + visionSbu + "' " + "GROUP BY T2.COUNTRY, T2.LE_BOOK, T2.CUSTOMER_ID) T3 "
//				+ "ON T2.COUNTRY = T3.COUNTRY AND T2.LE_BOOK = T3.LE_BOOK AND T2.CUSTOMER_ID = T3.CUSTOMER_ID AND T2.RUN_DATE = T3.RUN_DATE "
//				+ "WHERE T1.COUNTRY = '" + country + "' AND T1.LE_BOOK = '" + leBook + "' AND T1.VISION_SBU = '"
//				+ visionSbu + "' "
//				+ "AND COALESCE(T2.VERSION_NO,0) = COALESCE((SELECT MAX(T21.VERSION_NO) FROM RG_CRS_RULE_AUDIT T21 "
//				+ "WHERE T21.COUNTRY = T3.COUNTRY AND T21.LE_BOOK = T3.LE_BOOK AND T21.CUSTOMER_ID = T3.CUSTOMER_ID AND T21.RUN_DATE = T3.RUN_DATE),0) "
//				+ "AND (COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')) TAPPR " + "ORDER BY PRIORITY";
//
//		return new StringBuffer(sql);
//	}
//
//	public List<RgCrsRuleDetailsVb> getQueryResultsToDisplay(RgCrsRuleDetailsVb dObj) {
//		List<RgCrsRuleDetailsVb> collTemp = null;
//		dObj.setVerificationRequired(false);
//		/*
//		 * StringBuffer strBufApprove = new
//		 * StringBuffer("SELECT T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T2.CUSTOMER_NAME, T1.VISION_SBU_AT, T1.VISION_SBU, \r\n"
//		 * + "T1.RULE_ID,\r\n" + "T3.RULE_DESCRIPTION, \r\n"
//		 * +dateFormat+"(RUN_DATE, "+formatdate+") RUN_DATE" +
//		 * ", VERSION_NO, t1.PRIORITY, T1.CRS_FLAG, T1.CRS_OVERRIDE, FINAL_CRS_FLAG \r\n"
//		 * +
//		 * "FROM RG_CRS_RULE_AUDIT T1 JOIN CUSTOMERS T2 ON T1.COUNTRY = T2.COUNTRY                                  \r\n"
//		 * + "	AND T1.LE_BOOK = T2.LE_BOOK                                  \r\n" +
//		 * "	AND T1.CUSTOMER_ID = T2.CUSTOMER_ID                          \r\n" +
//		 * "	LEFT JOIN RG_CRS_RULE T3 ON T1.COUNTRY = T3.COUNTRY                             \r\n"
//		 * + "	AND T1.LE_BOOK = T3.LE_BOOK                             \r\n" +
//		 * "	AND T1.RULE_ID = T3.RULE_ID\r\n" +
//		 * "	AND T1.VISION_SBU = T3.VISION_SBU\r\n" +
//		 * "	WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.VISION_SBU = ? \r\n" +
//		 * " AND "+dateFormat+"(T1.RUN_DATE, "+formatdate+") = "+dateFormat+"("+
//		 * systemDate+", "+formatdate+") " + "	AND T1.VERSION_NO = (\r\n" +
//		 * " SELECT MAX(VERSION_NO) \r\n" + " FROM RG_CRS_RULE_AUDIT T2\r\n" +
//		 * " WHERE T2.COUNTRY = T1.COUNTRY\r\n" + " AND T2.LE_BOOK = T1.LE_BOOK\r\n" +
//		 * " AND T2.CUSTOMER_ID = T1.CUSTOMER_ID\r\n" +
//		 * " AND T2.RULE_ID = T1.RULE_ID \r\n" + " AND T2.RUN_DATE = T1.RUN_DATE\r\n" +
//		 * " )");
//		 */
//		String runDateExpr = (databaseType.equalsIgnoreCase("MSSQL") || databaseType.equalsIgnoreCase("SQLSERVER"))
//				? "CONVERT(VARCHAR(11), T2.RUN_DATE, 106)" // SQL Server
//				: "TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY')"; // Oracle
//
//		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (SELECT " + "    T1.COUNTRY, " + "    T1.LE_BOOK, "
//				+ "    T1.CUSTOMER_ID, " + "    T1.CUSTOMER_NAME, " + "    T1.VISION_SBU_AT, " + "    T1.VISION_SBU, "
//				+ "    T2.RULE_ID, " + "    T2.RULE_DESCRIPTION, " + "    " + runDateExpr + " AS RUN_DATE, "
//				+ "    T2.VERSION_NO, " + "    T2.PRIORITY, " + "    COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
//				+ "    T1.CRS_OVERRIDE, " + "    T1.CRS_FLAG AS FINAL_CRS_FLAG " + "FROM "+Customers+" T1 "
//				+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 " + "    ON T1.COUNTRY     = T2.COUNTRY "
//				+ "   AND T1.LE_BOOK     = T2.LE_BOOK " + "   AND T1.CUSTOMER_ID = T2.CUSTOMER_ID " + "LEFT JOIN ( "
//				+ "    SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(T2.RUN_DATE) AS Run_Date "
//				+ "    FROM RG_CRS_RULE_AUDIT T2 " + "    WHERE T2.COUNTRY    = ? " + "      AND T2.LE_BOOK    = ? "
//				+ "      AND T2.VISION_SBU = ? " + "    GROUP BY T2.Country, T2.LE_Book, T2.Customer_id " + ") T3 "
//				+ "   ON T2.COUNTRY     = T3.COUNTRY " + "  AND T2.LE_BOOK     = T3.LE_BOOK "
//				+ "  AND T2.CUSTOMER_ID = T3.CUSTOMER_ID " + "  AND T2.RUN_DATE    = T3.RUN_DATE "
//				+ "WHERE T1.COUNTRY    = ? " + "  AND T1.LE_BOOK    = ? " + "  AND T1.VISION_SBU = ? "
//				+ "  AND COALESCE(T2.VERSION_NO,0) = COALESCE(( " + "        SELECT MAX(T21.VERSION_NO) "
//				+ "        FROM RG_CRS_RULE_AUDIT T21 " + "        WHERE T21.COUNTRY     = T3.COUNTRY "
//				+ "          AND T21.LE_BOOK     = T3.LE_BOOK " + "          AND T21.CUSTOMER_ID = T3.CUSTOMER_ID "
//				+ "          AND T21.RUN_DATE    = T3.RUN_DATE " + "  ),0) "
//				+ "  AND (COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y'))TAPPR");
//
//		String orderBy = "ORDER BY PRIORITY";
//
//		Object objParams[] = null;
//
//		try {
//			Vector<Object> params = new Vector<Object>();
//			params.addElement(dObj.getCountry());
//			params.addElement(dObj.getLeBook());
//			params.addElement(dObj.getVisionSbu());
//			params.addElement(dObj.getCountry());
//			params.addElement(dObj.getLeBook());
//			params.addElement(dObj.getVisionSbu());
//			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
//				int count = 1;
//				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
//					if (count == dObj.getSmartSearchOpt().size()) {
//						data.setJoinType("");
//					} else {
//						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
//								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
//							data.setJoinType("AND");
//						}
//					}
//					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
//					switch (data.getObject()) {
//					case "customerId":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "customerName":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "ruleId":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_ID) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "ruleDescription":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_DESCRIPTION) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "priority":
//						CommonUtils.addToQuerySearch("upper( TAPPR.PRIORITY )" + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "crsFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_FLAG )" + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "crsOverrideFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_OVERRIDE) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "finalFlag":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.FINAL_CRS_FLAG) " + val, strBufApprove,
//								data.getJoinType());
//						break;
//
//					case "dateCreation":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.RUN_DATE) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					default:
//						break;
//
//					}
//					count++;
//				}
//			}
//			RowMapper mapper = new RowMapper() {
//				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//					RgCrsRuleDetailsVb vObject = new RgCrsRuleDetailsVb();
//					if (rs.getString("COUNTRY") != null) {
//						vObject.setCountry(rs.getString("COUNTRY"));
//					} else {
//						vObject.setCountry("");
//					}
//					if (rs.getString("LE_BOOK") != null) {
//						vObject.setLeBook(rs.getString("LE_BOOK"));
//					} else {
//						vObject.setLeBook("");
//					}
//					vObject.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
//					if (rs.getString("VISION_SBU") != null) {
//						vObject.setVisionSbu(rs.getString("VISION_SBU"));
//					} else {
//						vObject.setVisionSbu("");
//					}
//					if (rs.getString("RULE_ID") != null) {
//						vObject.setRuleId(rs.getString("RULE_ID"));
//					} else {
//						vObject.setRuleId("");
//					}
//					if (rs.getString("CUSTOMER_ID") != null) {
//						vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
//					}
//					if (rs.getString("CUSTOMER_NAME") != null) {
//						vObject.setCustomerName(rs.getString("CUSTOMER_NAME"));
//					}
//					if (rs.getString("RULE_DESCRIPTION") != null) {
//						vObject.setRuleDescription(rs.getString("RULE_DESCRIPTION"));
//					}
//					if (rs.getString("RUN_DATE") != null) {
//						vObject.setDateCreation(rs.getString("RUN_DATE"));
//					}
//
//					vObject.setPriority(rs.getInt("PRIORITY"));
//
//					if (rs.getString("CRS_FLAG") != null) {
//						vObject.setCrsFlag(rs.getString("CRS_FLAG"));
//					}
//
//					if (rs.getString("CRS_OVERRIDE") != null) {
//						vObject.setCrsOverrideFlag(rs.getString("CRS_OVERRIDE"));
//					}
//					if (rs.getString("FINAL_CRS_FLAG") != null) {
//						vObject.setFinalFlag(rs.getString("FINAL_CRS_FLAG"));
//					}
//
////					if (rs.getString("COLUMN_NAME") != null) {
////						vObject.setColumnName(rs.getString("COLUMN_NAME"));
////					} else {
////						vObject.setColumnName("");
////					}
//
//					return vObject;
//				}
//			};
//
////			collTemp = getJdbcTemplate().query(query,objParams,mapper);
//			return getQueryPopupResults(dObj, new StringBuffer(), strBufApprove, "", orderBy, params, mapper);
////			return collTemp;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			logger.error("Error: getQueryResults Exception :   ");
//			if (objParams != null)
//				for (int i = 0; i < objParams.length; i++)
//					logger.error("objParams[" + i + "]" + objParams[i].toString());
//			return null;
//		}
//	}

	public StringBuffer getDisplayQuery(RgCrsRuleDetailsVb dObj) {
		String country = dObj.getCountry();
		String leBook = dObj.getLeBook();
		String visionSbu = dObj.getVisionSbu();

		// WHERE clauses
		String condition1 = String.format("WHERE T2.COUNTRY = %s AND T2.LE_BOOK = %s AND T2.VISION_SBU = %s",
				quote(country), quote(leBook), quote(visionSbu));

		String condition2 = String.format("WHERE T1.COUNTRY = %s AND T1.LE_BOOK = %s AND T1.VISION_SBU = %s",
				quote(country), quote(leBook), quote(visionSbu));

		String sql;

		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			sql = String.format("SELECT " + "    T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, "
					+ "    T1.VISION_SBU_AT, T1.VISION_SBU, " + "    T2.RULE_ID, T2.RULE_DESCRIPTION, "
					+ "    CONVERT(VARCHAR(11), T2.RUN_DATE, 106) AS RUN_DATE, " + "    T2.VERSION_NO, T2.PRIORITY, "
					+ "    ISNULL(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
					+ "    T1.CRS_OVERRIDE, T1.CRS_FLAG AS FINAL_CRS_FLAG " + "FROM " + Customers + " T1 "
					+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 " + "   ON T1.COUNTRY = T2.COUNTRY "
					+ "  AND T1.LE_BOOK = T2.LE_BOOK " + "  AND T1.CUSTOMER_ID = T2.CUSTOMER_ID " + "LEFT JOIN ( "
					+ "    SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(T2.RUN_DATE) AS Run_Date "
					+ "    FROM RG_CRS_RULE_AUDIT T2 %s " + "    GROUP BY T2.Country, T2.LE_Book, T2.Customer_id "
					+ ") T3 " + "   ON T2.COUNTRY = T3.COUNTRY " + "  AND T2.LE_BOOK = T3.LE_BOOK "
					+ "  AND T2.CUSTOMER_ID = T3.CUSTOMER_ID " + "  AND T2.RUN_DATE = T3.RUN_DATE %s "
					+ "AND ISNULL(T2.VERSION_NO,0) = ISNULL(( " + "    SELECT MAX(T21.VERSION_NO) "
					+ "    FROM RG_CRS_RULE_AUDIT T21 " + "    WHERE T21.COUNTRY = T3.COUNTRY "
					+ "      AND T21.LE_BOOK = T3.LE_BOOK " + "      AND T21.CUSTOMER_ID = T3.CUSTOMER_ID "
					+ "      AND T21.RUN_DATE = T3.RUN_DATE " + "),0) "
					+ "AND (ISNULL(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')", condition1, condition2);
		} else {
			sql = String.format("WITH Audit_Data AS ( "
					+ "    SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(RUN_DATE) Run_Date "
					+ "    FROM RG_CRS_RULE_AUDIT T2 %s " + "    GROUP BY T2.Country, T2.LE_Book, T2.Customer_id "
					+ ") " + "SELECT " + "    T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, "
					+ "    T1.VISION_SBU_AT, T1.VISION_SBU, " + "    T2.RULE_ID, T2.RULE_DESCRIPTION, "
					+ "    TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY') RUN_DATE, " + "    T2.VERSION_NO, T2.PRIORITY, "
					+ "    NVL(T2.CRS_FLAG, T1.CRS_FLAG) CRS_FLAG, "
					+ "    T1.CRS_OVERRIDE, T1.CRS_FLAG FINAL_CRS_FLAG " + "FROM " + Customers + " T1 "
					+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 " + "   ON T1.COUNTRY = T2.COUNTRY "
					+ "  AND T1.LE_BOOK = T2.LE_BOOK " + "  AND T1.CUSTOMER_ID = T2.CUSTOMER_ID "
					+ "LEFT JOIN Audit_Data T3 " + "   ON T2.COUNTRY = T3.COUNTRY " + "  AND T2.LE_BOOK = T3.LE_BOOK "
					+ "  AND T2.CUSTOMER_ID = T3.CUSTOMER_ID " + "  AND T2.RUN_DATE = T3.RUN_DATE %s "
					+ "AND NVL(T2.VERSION_NO, 0) = NVL(( " + "    SELECT MAX(T21.VERSION_NO) "
					+ "    FROM RG_CRS_RULE_AUDIT T21 " + "    WHERE T21.COUNTRY = T3.COUNTRY "
					+ "      AND T21.LE_BOOK = T3.LE_BOOK " + "      AND T21.CUSTOMER_ID = T3.CUSTOMER_ID "
					+ "      AND T21.RUN_DATE = T3.RUN_DATE " + "),0) "
					+ "AND (NVL(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')", condition1, condition2);
		}

		return new StringBuffer(sql);
	}

	/**
	 * Safely wraps values in single quotes and escapes internal quotes.
	 */
	private String quote(String value) {
		if (value == null) {
			return "NULL";
		}
		return "'" + value.replace("'", "''") + "'";
	}

	public List<RgCrsRuleDetailsVb> getQueryResultsToDisplay(RgCrsRuleDetailsVb dObj) {
		dObj.setVerificationRequired(false);

		try {
			// --- Prepare magnifier ---
			MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
			magnifierResultVb.setPromptValue1("'CUSTOMERS'");
			magnifierResultVb.setQuery("CUSTCOLUMN");
			magnifierResultVb.setMaxRecords(100);

			Set<String> standardColumns = new HashSet<>(Arrays.asList("COUNTRY", "LE_BOOK", "CUSTOMER_ID", "CUSTOMER_NAME", "VISION_SBU_AT",
					"VISION_SBU", "RULE_ID", "RULE_DESCRIPTION", "RUN_DATE", "VERSION_NO", "PRIORITY", "CRS_FLAG",
					"CRS_OVERRIDE", "FINAL_CRS_FLAG"));

			// --- Fetch magnifier columns ---
			List<MagnifierResultVb> collTmp = magnifierDao.getQueryPopupResults(magnifierResultVb);
			Set<String> magnifierColumnsSet = new LinkedHashSet<>(); // SQL expressions for query
			Set<String> magnifierColumnNames = new HashSet<>(); // Alias names for RowMapper

			if (collTmp != null && !collTmp.isEmpty()) {
				for (MagnifierResultVb n : collTmp) {
					String columnName = n.getColumnNine();
					String aliasName = n.getColumnTen().trim().replaceAll(" ", "_");

					if (ValidationUtil.isValid(columnName)
							&& !standardColumns.contains(columnName.trim().toUpperCase())) {

						// Preserve alias exactly as in DB
						String sqlExpr = "T1." + columnName.trim() + " AS \"" + aliasName + "\"";
						magnifierColumnsSet.add(sqlExpr);
						magnifierColumnNames.add(aliasName.trim());
					}
				}
			}

			// --- Convert magnifierColumns set to SQL string ---
			String magColumnsStr = magnifierColumnsSet.isEmpty() ? "" : ", " + String.join(", ", magnifierColumnsSet);

			// --- Run date expression ---
			String runDateExpr = (databaseType.equalsIgnoreCase("MSSQL") || databaseType.equalsIgnoreCase("SQLSERVER"))
					? "CONVERT(VARCHAR(11), T2.RUN_DATE, 106)" // SQL Server
					: "TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY')"; // Oracle

			// --- Build main query ---
			StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (SELECT "
					+ "T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, " + "T1.VISION_SBU_AT, T1.VISION_SBU, "
					+ "T2.RULE_ID, T2.RULE_DESCRIPTION, " + runDateExpr + " AS RUN_DATE, "
					+ "T2.VERSION_NO, T2.PRIORITY, " + "COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
					+ "T1.CRS_OVERRIDE, T1.CRS_FLAG AS FINAL_CRS_FLAG" + magColumnsStr + " FROM CUSTOMERS T1 "
					+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 ON T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK AND T1.CUSTOMER_ID = T2.CUSTOMER_ID "
					+ "LEFT JOIN (SELECT T2.Country, T2.LE_Book, T2.Customer_id, MAX(T2.RUN_DATE) AS Run_Date "
					+ "FROM RG_CRS_RULE_AUDIT T2 WHERE T2.COUNTRY = ? AND T2.LE_BOOK = ? AND T2.VISION_SBU = ? "
					+ "GROUP BY T2.Country, T2.LE_Book, T2.Customer_id) T3 "
					+ "ON T2.COUNTRY = T3.COUNTRY AND T2.LE_BOOK = T3.LE_BOOK AND T2.CUSTOMER_ID = T3.CUSTOMER_ID AND T2.RUN_DATE = T3.RUN_DATE "
					+ "WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.VISION_SBU = ? "
					+ "AND COALESCE(T2.VERSION_NO,0) = COALESCE((SELECT MAX(T21.VERSION_NO) FROM RG_CRS_RULE_AUDIT T21 "
					+ "WHERE T21.COUNTRY = T3.COUNTRY AND T21.LE_BOOK = T3.LE_BOOK AND T21.CUSTOMER_ID = T3.CUSTOMER_ID AND T21.RUN_DATE = T3.RUN_DATE),0) "
					+ "AND (COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')) TAPPR");

			String orderBy = "ORDER BY PRIORITY";

			// --- Query parameters ---
			Vector<Object> params = new Vector<>();
			params.add(dObj.getCountry());
			params.add(dObj.getLeBook());
			params.add(dObj.getVisionSbu());
			params.add(dObj.getCountry());
			params.add(dObj.getLeBook());
			params.add(dObj.getVisionSbu());

			// --- Add smart search filters ---
			if (dObj.getSmartSearchOpt() != null && !dObj.getSmartSearchOpt().isEmpty()) {
				int count = 1;
				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
					if (count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if (!ValidationUtil.isValid(data.getJoinType()) || (!"AND".equalsIgnoreCase(data.getJoinType())
								&& !"OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "customerId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "customerName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "ruleId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_ID) " + val, strBufApprove, data.getJoinType());
						break;
					case "ruleDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "priority":
						CommonUtils.addToQuerySearch(" upper(TAPPR.PRIORITY) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "crsFlag":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_FLAG) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "crsOverrideFlag":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CRS_OVERRIDE) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "finalFlag":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FINAL_CRS_FLAG) " + val, strBufApprove,
								data.getJoinType());
						break;
					case "dateCreation":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RUN_DATE) " + val, strBufApprove,
								data.getJoinType());
						break;
					}
					count++;
				}
			}

			// --- RowMapper ---
			RowMapper<RgCrsRuleDetailsVb> mapper = (rs, rowNum) -> {
				RgCrsRuleDetailsVb vObject = new RgCrsRuleDetailsVb();

				// --- Standard fields ---
				vObject.setCountry(rs.getString("COUNTRY") != null ? rs.getString("COUNTRY") : "");
				vObject.setLeBook(rs.getString("LE_BOOK") != null ? rs.getString("LE_BOOK") : "");
				vObject.setVisionSbuAt(rs.getInt("VISION_SBU_AT"));
				vObject.setVisionSbu(rs.getString("VISION_SBU") != null ? rs.getString("VISION_SBU") : "");
				vObject.setRuleId(rs.getString("RULE_ID") != null ? rs.getString("RULE_ID") : "");
				vObject.setCustomerId(rs.getString("CUSTOMER_ID") != null ? rs.getString("CUSTOMER_ID") : "");
				vObject.setCustomerName(rs.getString("CUSTOMER_NAME") != null ? rs.getString("CUSTOMER_NAME") : "");
				vObject.setRuleDescription(
						rs.getString("RULE_DESCRIPTION") != null ? rs.getString("RULE_DESCRIPTION") : "");
				vObject.setDateCreation(rs.getString("RUN_DATE"));
				vObject.setPriority(rs.getInt("PRIORITY"));
				vObject.setCrsFlag(rs.getString("CRS_FLAG"));
				vObject.setCrsOverrideFlag(rs.getString("CRS_OVERRIDE"));
				vObject.setFinalFlag(rs.getString("FINAL_CRS_FLAG"));

				// --- Dynamic magnifier fields ---
				ResultSetMetaData meta = rs.getMetaData();
				int colCount = meta.getColumnCount();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

				for (int i = 1; i <= colCount; i++) {
				    String colLabel = meta.getColumnLabel(i); // alias from SQL
				    if (magnifierColumnNames.contains(colLabel)) {
				        Object value = rs.getObject(i);

				        // Check if column is DATE/TIMESTAMP type
				        int colType = meta.getColumnType(i);
				        if (value != null && 
				            (colType == Types.DATE || colType == Types.TIMESTAMP || colType == Types.TIME)) {
				            
				            Timestamp dateValue = rs.getTimestamp(i); // works for DATE/TIMESTAMP
				            value = sdf.format(dateValue);
				        }

				        String fieldName = CommonUtils.toPascalCaseKeepUnderscore(colLabel);
				        vObject.setDynamicField(fieldName, value);
				    }
				}


				return vObject;
			};

			// ...
			if(dObj.isCount()) {
				String countSql = "SELECT COUNT(1) FROM (" + strBufApprove + ") Q";
			    int total = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);
			    dObj.setTotalRows(total);
			    return java.util.Collections.emptyList(); 
			}

			// --- Execute query ---
			return getQueryPopupResults(dObj, new StringBuffer(), strBufApprove, "", orderBy, params, mapper);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception", ex);
			return null;
		}
	}

	public StringBuffer getDisplayQueryWithAllColumns(RgCrsRuleDetailsVb dObj) {
		String country = dObj.getCountry();
		String leBook = dObj.getLeBook();
		String visionSbu = dObj.getVisionSbu();

		// --- Prepare magnifier columns ---
		MagnifierResultVb magnifierResultVb = new MagnifierResultVb();
		magnifierResultVb.setPromptValue1("'CUSTOMERS'");
		magnifierResultVb.setQuery("CUSTCOLUMN");
		magnifierResultVb.setMaxRecords(100);
		String runDateExpr = (databaseType.equalsIgnoreCase("MSSQL") || databaseType.equalsIgnoreCase("SQLSERVER"))
				? "CONVERT(VARCHAR(11), T2.RUN_DATE, 106)" // SQL Server
				: "TO_CHAR(T2.RUN_DATE, 'DD-Mon-YYYY')"; // Oracle

		Set<String> standardColumns = new HashSet<>(Arrays.asList("COUNTRY", "LE_BOOK", "CUSTOMER_ID", "CUSTOMER_NAME", "VISION_SBU_AT",
				"VISION_SBU", "RULE_ID", "RULE_DESCRIPTION", "RUN_DATE", "VERSION_NO", "PRIORITY", "CRS_FLAG",
				"CRS_OVERRIDE", "FINAL_CRS_FLAG"));

		List<MagnifierResultVb> collTmp = magnifierDao.getQueryPopupResults(magnifierResultVb);
		Set<String> magnifierColumnsSet = new LinkedHashSet<>();

		if (collTmp != null && !collTmp.isEmpty()) {
			for (MagnifierResultVb n : collTmp) {
				String columnName = n.getColumnNine();
				String aliasName = n.getColumnTen().trim().replaceAll(" ", "_").toUpperCase();

				if (ValidationUtil.isValid(columnName) && !standardColumns.contains(columnName.trim().toUpperCase())) {

					magnifierColumnsSet.add("T1." + columnName.trim() + " AS \"" + aliasName + "\"");
				}
			}
		}

		String magColumnsStr = magnifierColumnsSet.isEmpty() ? "" : ", " + String.join(", ", magnifierColumnsSet);

		// --- COALESCE-based query (works for all platforms) ---
		String sql = "SELECT * FROM (SELECT " + "T1.COUNTRY, T1.LE_BOOK, T1.CUSTOMER_ID, T1.CUSTOMER_NAME, "
				+ "T1.VISION_SBU_AT, T1.VISION_SBU, " + "T2.RULE_ID, T2.RULE_DESCRIPTION, "+
//	            + "COALESCE(TO_CHAR(T2.RUN_DATE,'DD-Mon-YYYY'), TO_CHAR(T1.RUN_DATE,'DD-Mon-YYYY')) AS RUN_DATE, "
runDateExpr+ " RUN_DATE,"

				+ "T2.VERSION_NO, T2.PRIORITY, " + "COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) AS CRS_FLAG, "
				+ "T1.CRS_OVERRIDE, T1.CRS_FLAG AS FINAL_CRS_FLAG" + magColumnsStr + " FROM CUSTOMERS T1 "
				+ "LEFT JOIN RG_CRS_RULE_AUDIT T2 ON T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK AND T1.CUSTOMER_ID = T2.CUSTOMER_ID "
				+ "LEFT JOIN (SELECT T2.COUNTRY, T2.LE_BOOK, T2.CUSTOMER_ID, MAX(T2.RUN_DATE) AS RUN_DATE "
				+ "FROM RG_CRS_RULE_AUDIT T2 WHERE T2.COUNTRY = '" + country + "' AND T2.LE_BOOK = '" + leBook
				+ "' AND T2.VISION_SBU = '" + visionSbu + "' " + "GROUP BY T2.COUNTRY, T2.LE_BOOK, T2.CUSTOMER_ID) T3 "
				+ "ON T2.COUNTRY = T3.COUNTRY AND T2.LE_BOOK = T3.LE_BOOK AND T2.CUSTOMER_ID = T3.CUSTOMER_ID AND T2.RUN_DATE = T3.RUN_DATE "
				+ "WHERE T1.COUNTRY = '" + country + "' AND T1.LE_BOOK = '" + leBook + "' AND T1.VISION_SBU = '"
				+ visionSbu + "' "
				+ "AND COALESCE(T2.VERSION_NO,0) = COALESCE((SELECT MAX(T21.VERSION_NO) FROM RG_CRS_RULE_AUDIT T21 "
				+ "WHERE T21.COUNTRY = T3.COUNTRY AND T21.LE_BOOK = T3.LE_BOOK AND T21.CUSTOMER_ID = T3.CUSTOMER_ID AND T21.RUN_DATE = T3.RUN_DATE),0) "
				+ "AND (COALESCE(T2.CRS_FLAG, T1.CRS_FLAG) = 'Y' OR T1.CRS_FLAG = 'Y')) TAPPR ";
//				+ "ORDER BY PRIORITY";

		return new StringBuffer(sql);
	}

}
