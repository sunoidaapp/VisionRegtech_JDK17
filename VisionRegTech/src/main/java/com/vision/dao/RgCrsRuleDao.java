package com.vision.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.RgCrsRuleDetailsVb;
import com.vision.vb.RgCrsRuleVb;
import com.vision.vb.SmartSearchVb;

@Component
public class RgCrsRuleDao extends AbstractDao<RgCrsRuleVb> {

	@Autowired
	private RgCrsRuleDetailsDao rgCrsRuleDetailsDao;
	/******* Mapper Start **********/
	String CountryApprDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.COUNTRY) COUNTRY_DESC";
	String CountryPendDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.COUNTRY) COUNTRY_DESC";
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

	String VisionSbuAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 3, "TAppr.VISION_SBU",
			"VISION_SBU_DESC");
	String VisionSbuAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 3, "TPend.VISION_SBU",
			"VISION_SBU_DESC");

	String RuleStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.RULE_STATUS",
			"RULE_STATUS_DESC");
	String RuleStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.RULE_STATUS",
			"RULE_STATUS_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgCrsRuleVb vObject = new RgCrsRuleVb();
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
				if (rs.getString("RULE_DESCRIPTION") != null) {
					vObject.setRuleDescription(rs.getString("RULE_DESCRIPTION"));
				} else {
					vObject.setRuleDescription("");
				}
				vObject.setPriority(rs.getInt("PRIORITY"));
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
				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/
	public List<RgCrsRuleVb> getQueryPopupResults(RgCrsRuleVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from (Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.VISION_SBU_AT" + ",TAppr.VISION_SBU, " + VisionSbuAtApprDesc + ",TAppr.RULE_ID"
				+ ",TAppr.RULE_DESCRIPTION" + ",TAppr.PRIORITY" + ",TAppr.RULE_STATUS_NT" + ",TAppr.RULE_STATUS, "
				+ RuleStatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR, "
				+ RecordIndicatorApprDesc + ",TAppr.MAKER, " + makerApprDesc + ",TAppr.VERIFIER, " + verifierApprDesc
				+ ",TAppr.INTERNAL_STATUS" + ", " + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " from RG_CRS_RULE TAppr)TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From RG_CRS_RULE_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.VISION_SBU = TPend.VISION_SBU AND TAppr.RULE_ID = TPend.RULE_ID )");
		StringBuffer strBufPending = new StringBuffer("select * from (Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.VISION_SBU_AT" + ",TPend.VISION_SBU, " + VisionSbuAtPendDesc + ",TPend.RULE_ID"
				+ ",TPend.RULE_DESCRIPTION" + ",TPend.PRIORITY" + ",TPend.RULE_STATUS_NT" + ",TPend.RULE_STATUS, "
				+ RuleStatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR, "
				+ RecordIndicatorPendDesc + ",TPend.MAKER, " + makerPendDesc + ",TPend.VERIFIER, " + verifierPendDesc
				+ ",TPend.INTERNAL_STATUS" + ", " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " from RG_CRS_RULE_PEND TPend)Tpend ");
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
//					case "country":
//						CommonUtils.addToQuery("TAppr.COUNTRY = ?", strBufApprove);
//						CommonUtils.addToQuery("TPend.COUNTRY = ?", strBufPending);
//						params.addElement(data.getValue());
//						break;
//						
//					case "leBook":
//						CommonUtils.addToQuery("TAppr.LE_BOOK = ?", strBufApprove);
//						CommonUtils.addToQuery("TPend.LE_BOOK = ?", strBufPending);
//						params.addElement(data.getValue());
//						break;
//					case "visionSbu":
//						CommonUtils.addToQuery("TAppr.VISION_SBU = ?", strBufApprove);
//						CommonUtils.addToQuery("TPend.VISION_SBU = ?", strBufPending);
//						params.addElement(data.getValue());
//						break;

					case "visionSbuDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.VISION_SBU_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VISION_SBU_DESC) " + val, strBufPending,
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

					case "priority":
						CommonUtils.addToQuerySearch(" upper(TAppr.PRIORITY) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PRIORITY) " + val, strBufPending,
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

					case "statusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_STATUS_DESC) " + val, strBufPending,
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
			if (ValidationUtil.isValid(dObj.getCountry())) {
				CommonUtils.addToQuery(" TAppr.COUNTRY = ?", strBufApprove);
				CommonUtils.addToQuery(" TPend.COUNTRY = ?", strBufPending);
				params.add(dObj.getCountry());
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				CommonUtils.addToQuery(" TAppr.LE_BOOK = ?", strBufApprove);
				CommonUtils.addToQuery(" TPend.LE_BOOK = ?", strBufPending);
				params.add(dObj.getLeBook());
			}
			if (ValidationUtil.isValid(dObj.getVisionSbu())) {
				CommonUtils.addToQuery(" TAppr.VISION_SBU = ?", strBufApprove);
				CommonUtils.addToQuery(" TPend.VISION_SBU = ?", strBufPending);
				params.add(dObj.getVisionSbu());
			}
			String orderBy = " Order By COUNTRY, LE_BOOK, VISION_SBU, PRIORITY, RULE_ID ";
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

	public List<RgCrsRuleVb> getQueryResults(RgCrsRuleVb dObj, int intStatus) {

		setServiceDefaults();

		List<RgCrsRuleVb> collTemp = null;

		final int intKeyFieldsCount = 4;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.VISION_SBU_AT"
				+ ",TAppr.VISION_SBU, " + VisionSbuAtApprDesc + ",TAppr.RULE_ID" + ",TAppr.RULE_DESCRIPTION"
				+ ",TAppr.PRIORITY" + ",TAppr.RULE_STATUS_NT" + ",TAppr.RULE_STATUS, " + RuleStatusNtApprDesc
				+ ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR, " + RecordIndicatorApprDesc
				+ ",TAppr.MAKER, " + makerApprDesc + ",TAppr.VERIFIER, " + verifierApprDesc + ",TAppr.INTERNAL_STATUS"
				+ ", " + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", "
				+ dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION"
				+ " From RG_CRS_RULE TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.VISION_SBU_AT"
				+ ",TPend.VISION_SBU, " + VisionSbuAtPendDesc + ",TPend.RULE_ID" + ",TPend.RULE_DESCRIPTION"
				+ ",TPend.PRIORITY" + ",TPend.RULE_STATUS_NT" + ",TPend.RULE_STATUS, " + RuleStatusNtPendDesc
				+ ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR, " + RecordIndicatorPendDesc
				+ ",TPend.MAKER, " + makerPendDesc + ",TPend.VERIFIER, " + verifierPendDesc + ",TPend.INTERNAL_STATUS"
				+ ", " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED" + ", "
				+ dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION"
				+ " From RG_CRS_RULE_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getVisionSbu();
		objParams[3] = dObj.getRuleId();

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

	@Override
	protected List<RgCrsRuleVb> selectApprovedRecord(RgCrsRuleVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<RgCrsRuleVb> doSelectPendingRecord(RgCrsRuleVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(RgCrsRuleVb records) {
		return records.getRuleStatus();
	}

	@Override
	protected void setStatus(RgCrsRuleVb vObject, int status) {
		vObject.setRuleStatus(status);
	}

	@Override
	protected int doInsertionAppr(RgCrsRuleVb vObject) {
		String query = "Insert Into RG_CRS_RULE (COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, RULE_ID, RULE_DESCRIPTION, PRIORITY,"
				+ " RULE_STATUS_NT, RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbuAt(), vObject.getVisionSbu(),
				vObject.getRuleId(), vObject.getRuleDescription(), vObject.getPriority(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(RgCrsRuleVb vObject) {
		String query = "Insert Into RG_CRS_RULE_PEND (COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, RULE_ID, RULE_DESCRIPTION, PRIORITY,"
				+ " RULE_STATUS_NT, RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbuAt(), vObject.getVisionSbu(),
				vObject.getRuleId(), vObject.getRuleDescription(), vObject.getPriority(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(RgCrsRuleVb vObject) {
		String query = "Insert Into RG_CRS_RULE_PEND (COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, RULE_ID, RULE_DESCRIPTION, PRIORITY, "
				+ "RULE_STATUS_NT, RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + dateTimeConvert + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbuAt(), vObject.getVisionSbu(),
				vObject.getRuleId(), vObject.getRuleDescription(), vObject.getPriority(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(RgCrsRuleVb vObject) {
		String query = "Update RG_CRS_RULE Set VISION_SBU_AT = ?, RULE_DESCRIPTION = ?, PRIORITY = ?, RULE_STATUS_NT = ?, RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, "
				+ "RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + "  WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ? ";
		Object[] args = { vObject.getVisionSbuAt(), vObject.getRuleDescription(), vObject.getPriority(),
				vObject.getRuleStatusNt(), vObject.getRuleStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId() };

		return getJdbcTemplate().update(query, args);

	}

	@Override
	protected int doUpdatePend(RgCrsRuleVb vObject) {
		String query = "Update RG_CRS_RULE_PEND Set VISION_SBU_AT = ?, RULE_DESCRIPTION = ?, PRIORITY = ?, RULE_STATUS_NT = ?, RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, "
				+ "RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + "  WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ? ";
		Object[] args = { vObject.getVisionSbuAt(), vObject.getRuleDescription(), vObject.getPriority(),
				vObject.getRuleStatusNt(), vObject.getRuleStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(RgCrsRuleVb vObject) {
		String query = "Delete From RG_CRS_RULE Where COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(RgCrsRuleVb vObject) {
		String query = "Delete From RG_CRS_RULE_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND VISION_SBU = ?  AND RULE_ID = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionSbu(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(RgCrsRuleVb vObject) {
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

		if (ValidationUtil.isValid(vObject.getRuleDescription()))
			strAudit.append("RULE_DESCRIPTION" + auditDelimiterColVal + vObject.getRuleDescription().trim());
		else
			strAudit.append("RULE_DESCRIPTION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("PRIORITY" + auditDelimiterColVal + vObject.getPriority());
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
		serviceName = "RgCrsRule";
		serviceDesc = "RG CRS Rule";
		tableName = "RG_CRS_RULE";
		childTableName = "RG_CRS_RULE";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	protected ExceptionCode doInsertApprRecordForNonTrans(RgCrsRuleVb vObject) throws RuntimeCustomException {
		ResourceBundle rsb = CommonUtils.getResourceManger();
		List<RgCrsRuleVb> collTemp = null;
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
			int intStaticDeletionFlag = getStatus(((ArrayList<RgCrsRuleVb>) collTemp).get(0));
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
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		vObject.setDateCreation(systemDate);

		if (vObject.getRuleDetailsList() != null && vObject.getRuleDetailsList().size() > 0) {
			for (RgCrsRuleDetailsVb rgCrsRuleDetailsVb : vObject.getRuleDetailsList()) {
				rgCrsRuleDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				rgCrsRuleDetailsVb.setStaticDelete(vObject.isStaticDelete());
				exceptionCode = getRgCrsRuleDetailsDao().doInsertApprRecordForNonTrans(rgCrsRuleDetailsVb);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					return exceptionCode;
				}
			}
		}
		/*
		 * exceptionCode = writeAuditLog(vObject, null); if(exceptionCode.getErrorCode()
		 * != Constants.SUCCESSFUL_OPERATION){ exceptionCode =
		 * getResultObject(Constants.AUDIT_TRAIL_ERROR); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */
		return exceptionCode;
	}

	protected ExceptionCode doInsertRecordForNonTrans(RgCrsRuleVb vObject) throws RuntimeCustomException {
		List<RgCrsRuleVb> collTemp = null;
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
			int staticDeletionFlag = getStatus(((ArrayList<RgCrsRuleVb>) collTemp).get(0));
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
			RgCrsRuleVb vObjectLocal = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);
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
			}
		}
		if (vObject.getRuleDetailsList() != null && vObject.getRuleDetailsList().size() > 0) {
			for (RgCrsRuleDetailsVb rgCrsRuleDetailsVb : vObject.getRuleDetailsList()) {
				rgCrsRuleDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				rgCrsRuleDetailsVb.setStaticDelete(vObject.isStaticDelete());
				exceptionCode = getRgCrsRuleDetailsDao().doInsertRecordForNonTrans(rgCrsRuleDetailsVb);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					return exceptionCode;
				}
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	protected ExceptionCode doUpdateApprRecordForNonTrans(RgCrsRuleVb vObject) throws RuntimeCustomException {
		List<RgCrsRuleVb> collTemp = null;
		RgCrsRuleVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);
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
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);

		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}

		if (vObject.getRuleDetailsList() != null && vObject.getRuleDetailsList().size() > 0) {
			for (RgCrsRuleDetailsVb rgCrsRuleDetailsVb : vObject.getRuleDetailsList()) {
				rgCrsRuleDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				rgCrsRuleDetailsVb.setStaticDelete(vObject.isStaticDelete());
				if (rgCrsRuleDetailsVb.isNewRecord()) {
					strCurrentOperation = Constants.ADD;
					exceptionCode = getRgCrsRuleDetailsDao().doInsertApprRecordForNonTrans(rgCrsRuleDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				} else {
					strCurrentOperation = Constants.MODIFY;
					exceptionCode = getRgCrsRuleDetailsDao().doUpdateApprRecordForNonTrans(rgCrsRuleDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	protected ExceptionCode doUpdateRecordForNonTrans(RgCrsRuleVb vObject) throws RuntimeCustomException {
		List<RgCrsRuleVb> collTemp = null;
		RgCrsRuleVb vObjectlocal = null;
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
			vObjectlocal = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);

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
				vObjectlocal = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);
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
			}
		}

		if (vObject.getRuleDetailsList() != null && vObject.getRuleDetailsList().size() > 0) {
			for (RgCrsRuleDetailsVb rgCrsRuleDetailsVb : vObject.getRuleDetailsList()) {
				rgCrsRuleDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				rgCrsRuleDetailsVb.setStaticDelete(vObject.isStaticDelete());
				if (rgCrsRuleDetailsVb.isNewRecord()) {
					strCurrentOperation = Constants.ADD;
					exceptionCode = getRgCrsRuleDetailsDao().doInsertRecordForNonTrans(rgCrsRuleDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				} else {
					strCurrentOperation = Constants.MODIFY;
					exceptionCode = getRgCrsRuleDetailsDao().doUpdateRecordForNonTrans(rgCrsRuleDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	public ExceptionCode doApproveRecord(RgCrsRuleVb vObject, boolean staticDelete) throws RuntimeCustomException {
		RgCrsRuleVb oldContents = null;
		RgCrsRuleVb vObjectlocal = null;
		List<RgCrsRuleVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
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

			vObjectlocal = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);

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
				oldContents = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);
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
			}

			List<RgCrsRuleDetailsVb> rgCrsRuleDetailsList = getRgCrsRuleDetailsDao()
					.getQueryApprOrPendResultsByParent(vObject, 1);
			if (rgCrsRuleDetailsList != null && rgCrsRuleDetailsList.size() > 0) {
				for (RgCrsRuleDetailsVb rgCrsRuleDetailsVb : rgCrsRuleDetailsList) {
					rgCrsRuleDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
					rgCrsRuleDetailsVb.setStaticDelete(vObject.isStaticDelete());
					exceptionCode = getRgCrsRuleDetailsDao().doApproveForTransaction(rgCrsRuleDetailsVb, staticDelete);
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						strErrorDesc = exceptionCode.getErrorMsg();
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
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

	public ExceptionCode doRejectRecord(RgCrsRuleVb vObject) throws RuntimeCustomException {
		RgCrsRuleVb vObjectlocal = null;
		List<RgCrsRuleVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<RgCrsRuleVb>) collTemp).get(0);
			// Delete the record from the Pending table
			if (deletePendingRecord(vObjectlocal) == 0) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			List<RgCrsRuleDetailsVb> rgCrsRuleDetailsList = getRgCrsRuleDetailsDao()
					.getQueryApprOrPendResultsByParent(vObject, 1);
			if (rgCrsRuleDetailsList != null && rgCrsRuleDetailsList.size() > 0) {
				for (RgCrsRuleDetailsVb rgCrsRuleDetailsVb : rgCrsRuleDetailsList) {
					rgCrsRuleDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
					rgCrsRuleDetailsVb.setStaticDelete(vObject.isStaticDelete());
					exceptionCode = getRgCrsRuleDetailsDao().doRejectRecord(rgCrsRuleDetailsVb);
					if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						strErrorDesc = exceptionCode.getErrorMsg();
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
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

	public RgCrsRuleDetailsDao getRgCrsRuleDetailsDao() {
		return rgCrsRuleDetailsDao;
	}

	public void setRgCrsRuleDetailsDao(RgCrsRuleDetailsDao rgCrsRuleDetailsDao) {
		this.rgCrsRuleDetailsDao = rgCrsRuleDetailsDao;
	}

	public ExceptionCode getDataByValueOrPattern(RgCrsRuleDetailsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList datalst = new ArrayList();
		try {
			String orgColumnNames = vObject.getColumnName();
			String tableQuery = String.format(
					"SELECT DISTINCT %s FROM CUSTOMERS WHERE COUNTRY = '%s' AND LE_BOOK = '%s' ORDER BY %s",
					orgColumnNames, vObject.getCountry(), vObject.getLeBook(), orgColumnNames);

			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Set<String> values = new TreeSet<>();
					while (rs.next()) {
						LinkedHashMap<String, Object> columns = new LinkedHashMap<String, Object>();
						for (int i = 1; i <= colCount; i++) {
							String columnName = metaData.getColumnName(i);
							String columnValue = "" + rs.getObject(columnName);
							if ("PATTERN".equalsIgnoreCase(vObject.getColumnValueBy())) {
								if (ValidationUtil.isValid(columnValue)) {
									columnValue = CommonUtils.replaceCharacterRanges(columnValue);
								}
							}
							values.add(columnValue);
							columns.put("ColumnValue", columnValue);
						}
//						datalst.add(columns);
					}
					return values;
				}
			};
			Set<String> values = (Set<String>) getJdbcTemplate().query(tableQuery, mapper);

			for (String value : values) {
				LinkedHashMap<String, Object> columns = new LinkedHashMap<String, Object>();
				columns.put("ColumnValue", value);
				datalst.add(columns);
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setResponse(datalst);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in Gaining Connector Table Datas - Cause:" + e.getMessage());
			return exceptionCode;
		}

		return exceptionCode;
	}



	public ExceptionCode getDataExpression(RgCrsRuleDetailsVb vObject) {ExceptionCode exceptionCode = new ExceptionCode();
	ArrayList datalst = new ArrayList();
	try {
	    String sql = String.format(
	        "SELECT COUNTRY, LE_BOOK, VISION_SBU_AT, VISION_SBU, EXP_ID, EXP_DESCRIPTION, EXPRESSION " +
	        "FROM RG_PRE_EXPRESSION " +
	        "WHERE COUNTRY = '%s' AND LE_BOOK = '%s' AND VISION_SBU = '%s' AND EXP_STATUS = %d",
	        vObject.getCountry(),
	        vObject.getLeBook(),
	        vObject.getVisionSbu(),
	        0
	    );

	    ResultSetExtractor mapper = new ResultSetExtractor() {
	        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
	            ResultSetMetaData metaData = rs.getMetaData();
	            int colCount = metaData.getColumnCount();
	            List<Map<String, Object>> list = new ArrayList<>();

	            while (rs.next()) {
	                LinkedHashMap<String, Object> columns = new LinkedHashMap<>();
	                for (int i = 1; i <= colCount; i++) {
	                    String columnName = metaData.getColumnName(i);
	                    String columnValue = String.valueOf(rs.getObject(columnName));
	                    columns.put(columnName, columnValue); // ✅ Add ColumnName → Value
	                }
	                list.add(columns); // ✅ Directly add the row
	            }
	            return list;
	        }
	    };

	    datalst = (ArrayList) getJdbcTemplate().query(sql, mapper);

	    exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	    exceptionCode.setResponse(datalst);

	} catch (Exception e) {
	    exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	    exceptionCode.setErrorMsg("Problem while fetching data - Cause: " + e.getMessage());
	    return exceptionCode;
	}

	return exceptionCode;
}
}
