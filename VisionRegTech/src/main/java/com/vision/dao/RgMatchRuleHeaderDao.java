package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.RgMatchRuleDetailVb;
import com.vision.vb.RgMatchRuleHeaderVb;
import com.vision.vb.SmartSearchVb;

@Component
public class RgMatchRuleHeaderDao extends AbstractDao<RgMatchRuleHeaderVb> {

	@Autowired
	RgMatchRuleDetailsDao rgMatchRuleDetailsDao;

	@Autowired
	CommonDao commonDao;

	/******* Mapper Start **********/
	String CountryApprDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.COUNTRY) COUNTRY_DESC";
	String CountryPendDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.COUNTRY) COUNTRY_DESC";
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

	String ApproachTypeAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 6017, "TAppr.APPROACH_TYPE",
			"APPROACH_TYPE_DESC");
	String ApproachTypeAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 6017, "TPend.APPROACH_TYPE",
			"APPROACH_TYPE_DESC");

	String RuleStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.RULE_STATUS",
			"RULE_STATUS_DESC");
	String RuleStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.RULE_STATUS",
			"RULE_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgMatchRuleHeaderVb vObject = new RgMatchRuleHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setApproachTypeAt(rs.getInt("APPROACH_TYPE_AT"));
				vObject.setApproachType(rs.getString("APPROACH_TYPE"));
				vObject.setApproachTypeDesc(rs.getString("APPROACH_TYPE_DESC"));
				vObject.setRuleId(rs.getString("RULE_ID"));
				vObject.setRuleDescription(rs.getString("RULE_DESCRIPTION"));
				vObject.setRulePriority(rs.getInt("RULE_PRIORITY"));
				if (rs.getString("FORCE_MATCH") != null) {
					vObject.setForceMatch(rs.getString("FORCE_MATCH"));
				} else {
					vObject.setForceMatch("N");
				}
				vObject.setThreshold(rs.getInt("THRESHOLD"));
				vObject.setRuleStatusNt(rs.getInt("RULE_STATUS_NT"));
				vObject.setRuleStatus(rs.getInt("RULE_STATUS"));
				vObject.setRuleStatusDesc(rs.getString("RULE_STATUS_DESC"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
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

	/******* Mapper End **********/
	public List<RgMatchRuleHeaderVb> getQueryPopupResults(RgMatchRuleHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM ( Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.APPROACH_TYPE_AT" + ",TAppr.APPROACH_TYPE" + "," + ApproachTypeAtApprDesc + ",TAppr.RULE_ID"
				+ ",TAppr.RULE_DESCRIPTION" + ",TAppr.RULE_PRIORITY" + ",TAppr.FORCE_MATCH" + ",TAppr.THRESHOLD"
				+ ",TAppr.RULE_STATUS_NT" + ",TAppr.RULE_STATUS" + "," + RuleStatusNtApprDesc
				+ ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + "," + RecordIndicatorNtApprDesc
				+ ",TAppr.MAKER" + "," + makerApprDesc + ",TAppr.VERIFIER" + "," + verifierApprDesc
				+ ",TAppr.INTERNAL_STATUS"
				+ ", To_Char(TAppr.DATE_LAST_MODIFIED, 'DD-Mon-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1"
				+ ", To_Char(TAppr.DATE_CREATION, 'DD-Mon-YYYY HH24:MI:SS') DATE_CREATION"
				+ " from RG_CUSTMATCH_RULE_HEADER TAppr)TAppr ");
		String strWhereNotExists = new String(" Not Exists (Select 'X' From RG_CUSTMATCH_RULE_HEADER_PEND TPend "
				+ " WHERE TAppr.COUNTRY = TPend.COUNTRY " + " AND TAppr.LE_BOOK = TPend.LE_BOOK "
				+ " AND TAppr.APPROACH_TYPE = TPend.APPROACH_TYPE " + " AND TAppr.RULE_ID = TPend.RULE_ID )");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM ( Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.APPROACH_TYPE_AT" + ",TPend.APPROACH_TYPE" + "," + ApproachTypeAtPendDesc + ",TPend.RULE_ID"
				+ ",TPend.RULE_DESCRIPTION" + ",TPend.RULE_PRIORITY" + ",TPend.FORCE_MATCH" + ",TPend.THRESHOLD"
				+ ",TPend.RULE_STATUS_NT" + ",TPend.RULE_STATUS" + "," + RuleStatusNtPendDesc
				+ ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + "," + RecordIndicatorNtPendDesc
				+ ",TPend.MAKER" + "," + makerPendDesc + ",TPend.VERIFIER" + "," + verifierPendDesc
				+ ",TPend.INTERNAL_STATUS"
				+ ", To_Char(TPend.DATE_LAST_MODIFIED, 'DD-Mon-YYYY HH24:MI:SS') DATE_LAST_MODIFIED,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1"
				+ ", To_Char(TPend.DATE_CREATION, 'DD-Mon-YYYY HH24:MI:SS') DATE_CREATION"
				+ " from RG_CUSTMATCH_RULE_HEADER_PEND TPend)TPend ");
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

					case "approachTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.APPROACH_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.APPROACH_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleId":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ID) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ID) " + val, strBufPending, data.getJoinType());
						break;

					case "ruleDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "rulePriority":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_PRIORITY) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_PRIORITY) " + val, strBufPending,
								data.getJoinType());
						break;

					case "forceMatch":
						CommonUtils.addToQuerySearch(" upper(TAppr.FORCE_MATCH) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FORCE_MATCH) " + val, strBufPending,
								data.getJoinType());
						break;

					case "threshold":
						CommonUtils.addToQuerySearch(" upper(TAppr.THRESHOLD) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.THRESHOLD) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			// dObj.getCountry - KE
			if (ValidationUtil.isValid(dObj.getCountry())) {
				CommonUtils.addToQuery(" TAppr.COUNTRY IN ('" + dObj.getCountry() + "') ", strBufApprove);
				CommonUtils.addToQuery(" TPend.COUNTRY IN ('" + dObj.getCountry() + "') ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				CommonUtils.addToQuery(" TAppr.LE_BOOK IN ('" + dObj.getLeBook() + "') ", strBufApprove);
				CommonUtils.addToQuery(" TPend.LE_BOOK IN ('" + dObj.getLeBook() + "') ", strBufPending);
			}
			String orderBy = " Order By COUNTRY, le_book, RULE_PRIORITY ";
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

	public List<RgMatchRuleHeaderVb> getQueryResults(RgMatchRuleHeaderVb dObj, int intStatus) {
		setServiceDefaults();
		List<RgMatchRuleHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.APPROACH_TYPE_AT"
				+ ",TAppr.APPROACH_TYPE" + "," + ApproachTypeAtApprDesc + ",TAppr.RULE_ID" + ",TAppr.RULE_DESCRIPTION"
				+ ",TAppr.RULE_PRIORITY" + ",TAppr.FORCE_MATCH" + ",TAppr.THRESHOLD" + ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS" + "," + RuleStatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR" + "," + RecordIndicatorNtApprDesc + ",TAppr.MAKER" + "," + makerApprDesc
				+ ",TAppr.VERIFIER" + "," + verifierApprDesc + ",TAppr.INTERNAL_STATUS"
				+ ", To_Char(TAppr.DATE_LAST_MODIFIED, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED"
				+ ", To_Char(TAppr.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION"
				+ " From RG_CUSTMATCH_RULE_HEADER TAppr  " + " WHERE COUNTRY = ?  " + " AND LE_BOOK = ?  "
				+ " AND APPROACH_TYPE = ?   " + " AND RULE_ID = ? ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.APPROACH_TYPE_AT"
				+ ",TPend.APPROACH_TYPE" + "," + ApproachTypeAtPendDesc + ",TPend.RULE_ID" + ",TPend.RULE_DESCRIPTION"
				+ ",TPend.RULE_PRIORITY" + ",TPend.FORCE_MATCH" + ",TPend.THRESHOLD" + ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS" + "," + RuleStatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR" + "," + RecordIndicatorNtPendDesc + ",TPend.MAKER" + "," + makerPendDesc
				+ ",TPend.VERIFIER" + "," + verifierPendDesc + ",TPend.INTERNAL_STATUS"
				+ ", To_Char(TPend.DATE_LAST_MODIFIED, 'DD-MM-YYYY HH24:MI:SS') DATE_LAST_MODIFIED"
				+ ", To_Char(TPend.DATE_CREATION, 'DD-MM-YYYY HH24:MI:SS') DATE_CREATION"
				+ " From RG_CUSTMATCH_RULE_HEADER_PEND TPend " + " WHERE COUNTRY = ?  " + " AND LE_BOOK = ?  "
				+ " AND APPROACH_TYPE = ?   " + " AND RULE_ID = ? ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getApproachType();
		objParams[3] = dObj.getRuleId();
		try {
			// if(!dObj.isVerificationRequired()){intStatus =0;}
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
	protected List<RgMatchRuleHeaderVb> selectApprovedRecord(RgMatchRuleHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<RgMatchRuleHeaderVb> doSelectPendingRecord(RgMatchRuleHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(RgMatchRuleHeaderVb records) {
		return records.getRuleStatus();
	}

	@Override
	protected void setStatus(RgMatchRuleHeaderVb vObject, int status) {
		vObject.setRuleStatus(status);
	}

	@Override
	protected int doInsertionAppr(RgMatchRuleHeaderVb vObject) {
		String query = "Insert Into RG_CUSTMATCH_RULE_HEADER "
				+ " (COUNTRY, LE_BOOK, APPROACH_TYPE_AT, APPROACH_TYPE, RULE_ID, RULE_DESCRIPTION, "
				+ "RULE_PRIORITY, FORCE_MATCH, THRESHOLD, RULE_STATUS_NT, RULE_STATUS, "
				+ "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SysDate, SysDate)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getApproachTypeAt(),
				vObject.getApproachType(), vObject.getRuleId(), vObject.getRuleDescription(), vObject.getRulePriority(),
				vObject.getForceMatch(), vObject.getThreshold(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(RgMatchRuleHeaderVb vObject) {
		String query = "Insert Into RG_CUSTMATCH_RULE_HEADER_PEND "
				+ " (COUNTRY, LE_BOOK, APPROACH_TYPE_AT, APPROACH_TYPE, RULE_ID, RULE_DESCRIPTION, "
				+ "RULE_PRIORITY, FORCE_MATCH, THRESHOLD, RULE_STATUS_NT, RULE_STATUS, "
				+ "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SysDate, SysDate)";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getApproachTypeAt(),
				vObject.getApproachType(), vObject.getRuleId(), vObject.getRuleDescription(), vObject.getRulePriority(),
				vObject.getForceMatch(), vObject.getThreshold(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(RgMatchRuleHeaderVb vObject) {
		String query = "Insert Into RG_CUSTMATCH_RULE_HEADER_PEND "
				+ " (COUNTRY, LE_BOOK, APPROACH_TYPE_AT, APPROACH_TYPE, RULE_ID, RULE_DESCRIPTION,"
				+ " RULE_PRIORITY, FORCE_MATCH, THRESHOLD, RULE_STATUS_NT, RULE_STATUS,"
				+ " RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SysDate, To_Date(?, 'DD-MM-YYYY HH24:MI:SS'))";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getApproachTypeAt(),
				vObject.getApproachType(), vObject.getRuleId(), vObject.getRuleDescription(), vObject.getRulePriority(),
				vObject.getForceMatch(), vObject.getThreshold(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(RgMatchRuleHeaderVb vObject) {
		String query = "Update RG_CUSTMATCH_RULE_HEADER Set APPROACH_TYPE_AT = ?, RULE_ID = ?, "
				+ " RULE_DESCRIPTION = ?, RULE_PRIORITY = ?, FORCE_MATCH = ?, THRESHOLD = ?, RULE_STATUS_NT = ?,"
				+ " RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?,"
				+ " INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = SysDate "
				+ " WHERE COUNTRY = ?  AND LE_BOOK = ?  AND APPROACH_TYPE = ? AND RULE_ID = ?";
		Object[] args = { vObject.getApproachTypeAt(), vObject.getRuleId(), vObject.getRuleDescription(),
				vObject.getRulePriority(), vObject.getForceMatch(), vObject.getThreshold(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getApproachType(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(RgMatchRuleHeaderVb vObject) {
		String query = "Update RG_CUSTMATCH_RULE_HEADER_PEND Set APPROACH_TYPE_AT = ?, RULE_ID = ?, "
				+ " RULE_DESCRIPTION = ?, RULE_PRIORITY = ?, FORCE_MATCH = ?, THRESHOLD = ?, RULE_STATUS_NT = ?,"
				+ " RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?,"
				+ " INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = SysDate  "
				+ " WHERE COUNTRY = ?  AND LE_BOOK = ?  AND APPROACH_TYPE = ? AND RULE_ID = ?";
		Object[] args = { vObject.getApproachTypeAt(), vObject.getRuleId(), vObject.getRuleDescription(),
				vObject.getRulePriority(), vObject.getForceMatch(), vObject.getThreshold(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),
				vObject.getLeBook(), vObject.getApproachType(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(RgMatchRuleHeaderVb vObject) {
		try {
			String query = "Delete From RG_CUSTMATCH_RULE_HEADER Where COUNTRY = ?  AND LE_BOOK = ?  AND APPROACH_TYPE = ? AND RULE_ID = ? ";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getApproachType(),
					vObject.getRuleId() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}

	@Override
	protected int deletePendingRecord(RgMatchRuleHeaderVb vObject) {
		try {
			String query = "Delete From RG_CUSTMATCH_RULE_HEADER_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND APPROACH_TYPE = ? AND RULE_ID = ? ";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getApproachType(),
					vObject.getRuleId() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}

	@Override
	protected String getAuditString(RgMatchRuleHeaderVb vObject) {
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

		strAudit.append("APPROACH_TYPE_AT" + auditDelimiterColVal + vObject.getApproachTypeAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getApproachType()))
			strAudit.append("APPROACH_TYPE" + auditDelimiterColVal + vObject.getApproachType().trim());
		else
			strAudit.append("APPROACH_TYPE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleDescription()))
			strAudit.append("RULE_DESCRIPTION" + auditDelimiterColVal + vObject.getRuleDescription().trim());
		else
			strAudit.append("RULE_DESCRIPTION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("RULE_PRIORITY" + auditDelimiterColVal + vObject.getRulePriority());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getForceMatch()))
			strAudit.append("FORCE_MATCH" + auditDelimiterColVal + vObject.getForceMatch().trim());
		else
			strAudit.append("FORCE_MATCH" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("THRESHOLD" + auditDelimiterColVal + vObject.getThreshold());
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
		serviceName = "Customer Match Rule Config";
		serviceDesc = "Customer Match Rule Config";
		tableName = "RG_CUSTMATCH_RULE_HEADER";
		childTableName = "RG_CUSTMATCH_RULE_HEADER";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	public ExceptionCode doInsertApprRecordForNonTrans(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		List<RgMatchRuleHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation = Constants.ADD;
		setServiceDefaults();
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int intStaticDeletionFlag = getStatus(((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0));
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
		// Try inserting the record
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doInsertionAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = deleteAndInsertforRuleMactchDet(vObject, Constants.STATUS_ZERO);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);
		/*
		 * exceptionCode = writeAuditLog(vObject, null); if(exceptionCode.getErrorCode()
		 * != Constants.SUCCESSFUL_OPERATION){ exceptionCode =
		 * getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}

	public ExceptionCode doInsertRecordForNonTrans(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		List<RgMatchRuleHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.ADD;
		strErrorDesc = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null) {
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0) {
			int staticDeletionFlag = getStatus(((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0));
			if (staticDeletionFlag == Constants.PASSIVATE) {
				logger.info("Collection size is greater than zero - Duplicate record found, but inactive");
				exceptionCode = getResultObject(Constants.RECORD_ALREADY_PRESENT_BUT_INACTIVE);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				logger.info("Collection size is greater than zero - Duplicate record found");
				exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}

		// Try to see if the record already exists in the pending table, but not in
		// approved table
		collTemp = null;
		collTemp = doSelectPendingRecord(vObject);

		// The collTemp variable could not be null. If so, there is no problem fetching
		// data
		// return back error code to calling routine
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// if record already exists in pending table, modify the record
		if (collTemp.size() > 0) {
			RgMatchRuleHeaderVb vObjectLocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
			if (vObjectLocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				exceptionCode = getResultObject(Constants.PENDING_FOR_ADD_ALREADY);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			// Try inserting the record
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			vObject.setDateCreation(systemDate);
			retVal = doInsertionPend(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = deleteAndInsertforRuleMactchDet(vObject, Constants.STATUS_PENDING);
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
					exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
			exceptionCode = getResultObject(retVal);
			return exceptionCode;
		}
	}

	public ExceptionCode deleteAndInsertforRuleMactchDet(RgMatchRuleHeaderVb vObject, int status) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if (rgMatchRuleDetailsDao.getCntforCustRuleMatchDet(vObject, status) > 0)
				rgMatchRuleDetailsDao.deleteCustMatchRuleDet(vObject, status);
			if (vObject.getMatchRuleDetailLst() != null && !vObject.getMatchRuleDetailLst().isEmpty()) {
				for (RgMatchRuleDetailVb matchRuleDetailVb : vObject.getMatchRuleDetailLst()) {
					matchRuleDetailVb.setRecordIndicator(vObject.getRecordIndicator());
					matchRuleDetailVb.setMaker(vObject.getMaker());
					matchRuleDetailVb.setVerifier(vObject.getVerifier());
					matchRuleDetailVb.setCountry(vObject.getCountry());
					matchRuleDetailVb.setLeBook(vObject.getLeBook());
					matchRuleDetailVb.setRuleId(vObject.getRuleId());
					exceptionCode = rgMatchRuleDetailsDao.insertCustRuleMatchDetails(matchRuleDetailVb, status);
					if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION)
						return exceptionCode;
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public List<AlphaSubTabVb> getTableNamesList() {
		List tableNamesList = new ArrayList<>();
		try {
			String sql = "SELECT TABLE_NAME ID , Table_Name DESCRIPTION FROM RG_Source_Table_Mapping WHERE Table_Status = 0";
			tableNamesList = getJdbcTemplate().query(sql, commonDao.getPageLoadValuesMapper());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableNamesList;
	}

	protected ExceptionCode doUpdateApprRecordForNonTrans(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		List<RgMatchRuleHeaderVb> collTemp = null;
		RgMatchRuleHeaderVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
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
			exceptionCode = deleteAndInsertforRuleMactchDet(vObject, Constants.STATUS_ZERO);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION)
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		/*
		 * exceptionCode = writeAuditLog(vObject, vObjectlocal);
		 * if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
		 * exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}

	protected ExceptionCode doUpdateRecordForNonTrans(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		List<RgMatchRuleHeaderVb> collTemp = null;
		RgMatchRuleHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		// Search if record already exists in pending. If it already exists, check for
		// status
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0) {
			vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);

			// Check if the record is pending for deletion. If so return the error
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE) {
				exceptionCode = getResultObject(Constants.RECORD_PENDING_FOR_DELETION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT) {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePend(vObject);
			} else {
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePend(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = deleteAndInsertforRuleMactchDet(vObject, Constants.STATUS_PENDING);
			if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION)
				throw buildRuntimeCustomException(exceptionCode);
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
				vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			// Record is there in approved, but not in pending. So add it to pending
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_UPDATE);
			retVal = doInsertionPendWithDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = deleteAndInsertforRuleMactchDet(vObject, Constants.STATUS_PENDING);
				if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION)
					throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}

	protected ExceptionCode doDeleteApprRecordForNonTrans(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		List<RgMatchRuleHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		RgMatchRuleHeaderVb vObjectlocal = null;
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
			int intStaticDeletionFlag = getStatus(((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
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
			if (rgMatchRuleDetailsDao.getCntforCustRuleMatchDet(vObject, Constants.STATUS_ZERO) > 0) {
				retVal = rgMatchRuleDetailsDao.deleteCustMatchRuleDet(vObject, Constants.STATUS_ZERO);
			}
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
		return exceptionCode;
	}

	protected ExceptionCode doDeleteRecordForNonTrans(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		RgMatchRuleHeaderVb vObjectlocal = null;
		List<RgMatchRuleHeaderVb> collTemp = null;
		List<RgMatchRuleDetailVb> custMatchRuleDetLst = null;
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
			vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
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
			vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
			vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		// vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		retVal = doInsertionPendWithDc(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}

		custMatchRuleDetLst = rgMatchRuleDetailsDao.getQueryResultsforMatchRuleDet(vObject, Constants.STATUS_ZERO);
		if (custMatchRuleDetLst != null && !custMatchRuleDetLst.isEmpty()) {
			vObjectlocal.setMatchRuleDetailLst(custMatchRuleDetLst);
			deleteAndInsertforRuleMactchDet(vObjectlocal, Constants.STATUS_PENDING);
		}

		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	public ExceptionCode doRejectRecord(RgMatchRuleHeaderVb vObject) throws RuntimeCustomException {
		RgMatchRuleHeaderVb vObjectlocal = null;
		List<RgMatchRuleHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
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
			vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);
			// Delete the record from the Pending table
			if (deletePendingRecord(vObjectlocal) == 0) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			int retVal = rgMatchRuleDetailsDao.deleteCustMatchRuleDet(vObjectlocal, Constants.STATUS_PENDING);
			if (retVal == Constants.ERRONEOUS_OPERATION) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
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

	public ExceptionCode doApproveRecord(RgMatchRuleHeaderVb vObject, boolean staticDelete)
			throws RuntimeCustomException {
		RgMatchRuleHeaderVb oldContents = null;
		RgMatchRuleDetailVb oldContentsDet = null;
		RgMatchRuleHeaderVb vObjectlocal = null;
		RgMatchRuleDetailVb vObjectlocalDet = null;
		List<RgMatchRuleHeaderVb> collTemp = null;
		List<RgMatchRuleDetailVb> collTempDet = null;
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

			vObjectlocal = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()) {
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			collTempDet = rgMatchRuleDetailsDao.getQueryResultsforMatchRuleDet(vObject, Constants.STATUS_PENDING);
			if (collTempDet != null && collTempDet.size() > 0) {
				vObjectlocalDet = ((ArrayList<RgMatchRuleDetailVb>) collTempDet).get(0);
				vObjectlocal.setMatchRuleDetailLst(collTempDet);
			}

			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT) {
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<RgMatchRuleHeaderVb>) collTemp).get(0);

				collTempDet = rgMatchRuleDetailsDao.getQueryResultsforMatchRuleDet(vObject, Constants.STATUS_ZERO);
				if (collTempDet == null || collTempDet.isEmpty()) {
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContentsDet = ((ArrayList<RgMatchRuleDetailVb>) collTempDet).get(0);
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
				vObjectlocal.setMatchRuleDetailLst(
						rgMatchRuleDetailsDao.getQueryResultsforMatchRuleDet(vObjectlocal, Constants.STATUS_PENDING));
				for (RgMatchRuleDetailVb matchRuleDetVb : vObjectlocal.getMatchRuleDetailLst()) {
					exceptionCode = rgMatchRuleDetailsDao.insertCustRuleMatchDetails(matchRuleDetVb,
							Constants.STATUS_ZERO);
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
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
				exceptionCode = deleteAndInsertforRuleMactchDet(vObjectlocal, Constants.STATUS_ZERO);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
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
					retVal = rgMatchRuleDetailsDao.deleteCustMatchRuleDet(vObjectlocal, Constants.STATUS_ZERO);
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

			if (retVal == Constants.SUCCESSFUL_OPERATION) {
				rgMatchRuleDetailsDao.deleteCustMatchRuleDet(vObjectlocal, Constants.STATUS_PENDING);
			} else {
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
