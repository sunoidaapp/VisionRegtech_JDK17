package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Vector;
import org.springframework.jdbc.core.RowMapper;
import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CbGlMappingsVb;
import com.vision.vb.SmartSearchVb;

@Component
public class CbGlMappingsDao extends AbstractDao<CbGlMappingsVb> {

	/******* Mapper Start **********/

	String Attribute01AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5022, "TAppr.ATTRIBUTE_01",
			"ATTRIBUTE_01_DESC");
	String Attribute01AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5022, "TPend.ATTRIBUTE_01",
			"ATTRIBUTE_01_DESC");

	String Attribute02AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5023, "TAppr.ATTRIBUTE_02",
			"ATTRIBUTE_02_DESC");
	String Attribute02AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5023, "TPend.ATTRIBUTE_02",
			"ATTRIBUTE_02_DESC");

	String Attribute03AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5024, "TAppr.ATTRIBUTE_03",
			"ATTRIBUTE_03_DESC");
	String Attribute03AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5024, "TPend.ATTRIBUTE_03",
			"ATTRIBUTE_03_DESC");

	String GlMapStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_MAP_STATUS",
			"GL_MAP_STATUS_DESC");
	String GlMapStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_MAP_STATUS",
			"GL_MAP_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	String RuleIdApprDesc = "";
	String RuleIdPendDesc = "";

	String BankGlCodeApprDesc = "(SELECT GL_DESCRIPTION   FROM GL_CODES T1  WHERE GL_STATUS = 0 AND T1.COUNTRY=TAPPR.COUNTRY AND T1.LE_BOOK = TAPPR.LE_BOOK AND T1.VISION_GL = TAPPR.BANK_GL_CODE)BANK_GL_CODE_DESC";
	String BankGlCodePendDesc = "(SELECT GL_DESCRIPTION   FROM GL_CODES T1  WHERE GL_STATUS = 0 AND T1.COUNTRY=TPEND.COUNTRY AND T1.LE_BOOK = TPEND.LE_BOOK AND T1.VISION_GL =  TPEND.BANK_GL_CODE)BANK_GL_CODE_DESC";

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CbGlMappingsVb vObject = new CbGlMappingsVb();
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
				if (rs.getString("BANK_GL_CODE") != null) {
					vObject.setBankGlCode(rs.getString("BANK_GL_CODE"));
				} else {
					vObject.setBankGlCode("");
				}
				if (rs.getString("RULE_ID") != null) {
					vObject.setRuleId(rs.getString("RULE_ID"));
				} else {
					vObject.setRuleId("");
				}
				if (rs.getString("ATTRIBUTE_01_AT") != null) {
					vObject.setAttribute01At(rs.getString("ATTRIBUTE_01_AT"));
				} else {
					vObject.setAttribute01At("");
				}
				if (rs.getString("ATTRIBUTE_01") != null) {
					vObject.setAttribute01(rs.getString("ATTRIBUTE_01"));
				} else {
					vObject.setAttribute01("");
				}
				if (rs.getString("ATTRIBUTE_02_AT") != null) {
					vObject.setAttribute02At(rs.getString("ATTRIBUTE_02_AT"));
				} else {
					vObject.setAttribute02At("");
				}
				if (rs.getString("ATTRIBUTE_02") != null) {
					vObject.setAttribute02(rs.getString("ATTRIBUTE_02"));
				} else {
					vObject.setAttribute02("");
				}
				if (rs.getString("ATTRIBUTE_03_AT") != null) {
					vObject.setAttribute03At(rs.getString("ATTRIBUTE_03_AT"));
				} else {
					vObject.setAttribute03At("");
				}
				if (rs.getString("ATTRIBUTE_03") != null) {
					vObject.setAttribute03(rs.getString("ATTRIBUTE_03"));
				} else {
					vObject.setAttribute03("");
				}
				vObject.setGlMapStatusNt(rs.getInt("GL_MAP_STATUS_NT"));
				vObject.setGlMapStatus(rs.getInt("GL_MAP_STATUS"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
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
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setGlMapStatusDesc(rs.getString("GL_MAP_STATUS_DESC"));
//				vObject.setRuleIdDesc(rs.getString("RULE_ID_DESC"));
				vObject.setBankGlCodeDesc(rs.getString("BANK_GL_CODE_DESC"));
				vObject.setAttribute01Desc(rs.getString("ATTRIBUTE_01_DESC"));
				vObject.setAttribute02Desc(rs.getString("ATTRIBUTE_02_DESC"));
				vObject.setAttribute03Desc(rs.getString("ATTRIBUTE_03_DESC"));

				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/
	public List<CbGlMappingsVb> getQueryPopupResults(CbGlMappingsVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.BANK_GL_CODE" + ",TAppr.RULE_ID" + ",TAppr.ATTRIBUTE_01_AT" + ",TAppr.ATTRIBUTE_01"
				+ ",TAppr.ATTRIBUTE_02_AT" + ",TAppr.ATTRIBUTE_02" + ",TAppr.ATTRIBUTE_03_AT" + ",TAppr.ATTRIBUTE_03"
				+ ",TAppr.GL_MAP_STATUS_NT" + ",TAppr.GL_MAP_STATUS" + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER" + ",TAppr.VERIFIER" + ",TAppr.INTERNAL_STATUS" + " , "
				+ RecordIndicatorNtApprDesc + " , " + Attribute01AtApprDesc + " , " + Attribute02AtApprDesc + " , "
				+ Attribute03AtApprDesc + " , " + makerApprDesc + " , " + verifierApprDesc + " , "
				+ GlMapStatusNtApprDesc + " , " + BankGlCodeApprDesc + " ," + dateFormat
				+ " (TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED, " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION " + " from CB_GL_MAPPINGS TAppr)TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From CB_GL_MAPPINGS_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.BANK_GL_CODE = TPend.BANK_GL_CODE AND TAppr.RULE_ID = TPend.RULE_ID )");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM (Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.BANK_GL_CODE" + ",TPend.RULE_ID" + ",TPend.ATTRIBUTE_01_AT" + ",TPend.ATTRIBUTE_01"
				+ ",TPend.ATTRIBUTE_02_AT" + ",TPend.ATTRIBUTE_02" + ",TPend.ATTRIBUTE_03_AT" + ",TPend.ATTRIBUTE_03"
				+ ",TPend.GL_MAP_STATUS_NT" + ",TPend.GL_MAP_STATUS" + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR" + ",TPend.MAKER" + ",TPend.VERIFIER" + ",TPend.INTERNAL_STATUS" + " , "
				+ RecordIndicatorNtPendDesc + " , " + Attribute01AtPendDesc + " , " + Attribute02AtPendDesc + " , "
				+ Attribute03AtPendDesc + " , " + makerPendDesc + " , " + verifierPendDesc + " , "
				+ GlMapStatusNtPendDesc + " , " + BankGlCodePendDesc

				+ " , " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED, "
				+ dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " from CB_GL_MAPPINGS_PEND TPend )TPend");
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

					case "bankGlCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.BANK_GL_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BANK_GL_CODE) " + val, strBufPending,
								data.getJoinType());
						break;
					case "bankGlCodeDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.BANK_GL_CODE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BANK_GL_CODE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "ruleId":
						CommonUtils.addToQuerySearch(" upper(TAppr.RULE_ID) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RULE_ID) " + val, strBufPending, data.getJoinType());
						break;

					case "attribute01":
						CommonUtils.addToQuerySearch(" upper(TAppr.ATTRIBUTE_01) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ATTRIBUTE_01) " + val, strBufPending,
								data.getJoinType());
						break;

					case "attribute02":
						CommonUtils.addToQuerySearch(" upper(TAppr.ATTRIBUTE_02) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ATTRIBUTE_02) " + val, strBufPending,
								data.getJoinType());
						break;

					case "attribute03":
						CommonUtils.addToQuerySearch(" upper(TAppr.ATTRIBUTE_03) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ATTRIBUTE_03) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glMapStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_MAP_STATUS) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_MAP_STATUS) " + val, strBufPending,
								data.getJoinType());
						break;

					case "recordIndicator":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR) " + val, strBufPending,
								data.getJoinType());
						break;
					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPEND.RECORD_INDICATOR_DESC) " + val, strBufPending,
								data.getJoinType());
						break;
					case "glMapStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_MAP_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_MAP_STATUS_DESC) " + val, strBufPending,
								data.getJoinType());
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
			String orderBy = "order by DATE_LAST_MODIFIED desc";
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

	public List<CbGlMappingsVb> getQueryResults(CbGlMappingsVb dObj, int intStatus) {

		setServiceDefaults();

		List<CbGlMappingsVb> collTemp = null;

		final int intKeyFieldsCount = 4;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.BANK_GL_CODE"
				+ ",TAppr.RULE_ID" + ",TAppr.ATTRIBUTE_01_AT" + ",TAppr.ATTRIBUTE_01" + ",TAppr.ATTRIBUTE_02_AT"
				+ ",TAppr.ATTRIBUTE_02" + ",TAppr.ATTRIBUTE_03_AT" + ",TAppr.ATTRIBUTE_03" + ",TAppr.GL_MAP_STATUS_NT"
				+ ",TAppr.GL_MAP_STATUS" + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER"
				+ ",TAppr.VERIFIER" + ",TAppr.INTERNAL_STATUS" + " , " + RecordIndicatorNtApprDesc + " , "
				+ Attribute01AtApprDesc + " , " + Attribute02AtApprDesc + " , " + Attribute03AtApprDesc + " , "
				+ BankGlCodeApprDesc + " , " + makerApprDesc + " , " + verifierApprDesc + " , " + GlMapStatusNtApprDesc
				+ "," + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED " + ", "
				+ dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " From CB_GL_MAPPINGS TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND BANK_GL_CODE = ?  AND RULE_ID = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.BANK_GL_CODE"
				+ ",TPend.RULE_ID" + ",TPend.ATTRIBUTE_01_AT" + ",TPend.ATTRIBUTE_01" + ",TPend.ATTRIBUTE_02_AT"
				+ ",TPend.ATTRIBUTE_02" + ",TPend.ATTRIBUTE_03_AT" + ",TPend.ATTRIBUTE_03" + ",TPend.GL_MAP_STATUS_NT"
				+ ",TPend.GL_MAP_STATUS" + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + ",TPend.MAKER"
				+ ",TPend.VERIFIER" + ",TPend.INTERNAL_STATUS" + " , " + RecordIndicatorNtPendDesc + " , "
				+ Attribute01AtPendDesc + " , " + Attribute02AtPendDesc + " , " + Attribute03AtPendDesc + " , "
				+ BankGlCodePendDesc + " , " + makerPendDesc + " , " + verifierPendDesc + " , " + GlMapStatusNtPendDesc
				+ "," + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED " + ", "
				+ dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " From CB_GL_MAPPINGS_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND BANK_GL_CODE = ?  AND RULE_ID = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getBankGlCode();
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
	protected List<CbGlMappingsVb> selectApprovedRecord(CbGlMappingsVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<CbGlMappingsVb> doSelectPendingRecord(CbGlMappingsVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(CbGlMappingsVb records) {
		return records.getGlMapStatus();
	}

	@Override
	protected void setStatus(CbGlMappingsVb vObject, int status) {
		vObject.setGlMapStatus(status);
	}

	@Override
	protected int doInsertionAppr(CbGlMappingsVb vObject) {
		String query = "Insert Into CB_GL_MAPPINGS (COUNTRY, LE_BOOK, BANK_GL_CODE, RULE_ID, ATTRIBUTE_01_AT, ATTRIBUTE_01, ATTRIBUTE_02_AT, ATTRIBUTE_02, ATTRIBUTE_03_AT, ATTRIBUTE_03, GL_MAP_STATUS_NT, GL_MAP_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId(),
				vObject.getAttribute01At(), vObject.getAttribute01(), vObject.getAttribute02At(),
				vObject.getAttribute02(), vObject.getAttribute03At(), vObject.getAttribute03(),
				vObject.getGlMapStatusNt(), vObject.getGlMapStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(CbGlMappingsVb vObject) {
		String query = "Insert Into CB_GL_MAPPINGS_PEND (COUNTRY, LE_BOOK, BANK_GL_CODE, RULE_ID, ATTRIBUTE_01_AT, ATTRIBUTE_01, ATTRIBUTE_02_AT, ATTRIBUTE_02, ATTRIBUTE_03_AT, ATTRIBUTE_03, GL_MAP_STATUS_NT, GL_MAP_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId(),
				vObject.getAttribute01At(), vObject.getAttribute01(), vObject.getAttribute02At(),
				vObject.getAttribute02(), vObject.getAttribute03At(), vObject.getAttribute03(),
				vObject.getGlMapStatusNt(), vObject.getGlMapStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(CbGlMappingsVb vObject) {
		String query = "Insert Into CB_GL_MAPPINGS_PEND (COUNTRY, LE_BOOK, BANK_GL_CODE, RULE_ID, ATTRIBUTE_01_AT, ATTRIBUTE_01, ATTRIBUTE_02_AT, ATTRIBUTE_02, ATTRIBUTE_03_AT, ATTRIBUTE_03, GL_MAP_STATUS_NT, GL_MAP_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + dateTimeConvert
				+ ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId(),
				vObject.getAttribute01At(), vObject.getAttribute01(), vObject.getAttribute02At(),
				vObject.getAttribute02(), vObject.getAttribute03At(), vObject.getAttribute03(),
				vObject.getGlMapStatusNt(), vObject.getGlMapStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(CbGlMappingsVb vObject) {
		String query = "Update CB_GL_MAPPINGS Set ATTRIBUTE_01_AT = ?, ATTRIBUTE_01 = ?, ATTRIBUTE_02_AT = ?, ATTRIBUTE_02 = ?, ATTRIBUTE_03_AT = ?, ATTRIBUTE_03 = ?, "
				+ "GL_MAP_STATUS_NT = ?, GL_MAP_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " " + " WHERE COUNTRY = ?  AND LE_BOOK = ?  AND BANK_GL_CODE = ?  AND RULE_ID = ? ";
		Object[] args = { vObject.getAttribute01At(), vObject.getAttribute01(), vObject.getAttribute02At(),
				vObject.getAttribute02(), vObject.getAttribute03At(), vObject.getAttribute03(),
				vObject.getGlMapStatusNt(), vObject.getGlMapStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(CbGlMappingsVb vObject) {
		String query = "Update CB_GL_MAPPINGS_PEND Set ATTRIBUTE_01_AT = ?, ATTRIBUTE_01 = ?, ATTRIBUTE_02_AT = ?, ATTRIBUTE_02 = ?, ATTRIBUTE_03_AT = ?, ATTRIBUTE_03 = ?, "
				+ "GL_MAP_STATUS_NT = ?, GL_MAP_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " " + "  WHERE COUNTRY = ?  AND LE_BOOK = ?  AND BANK_GL_CODE = ?  AND RULE_ID = ? ";
		Object[] args = { vObject.getAttribute01At(), vObject.getAttribute01(), vObject.getAttribute02At(),
				vObject.getAttribute02(), vObject.getAttribute03At(), vObject.getAttribute03(),
				vObject.getGlMapStatusNt(), vObject.getGlMapStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(CbGlMappingsVb vObject) {
		String query = "Delete From CB_GL_MAPPINGS Where COUNTRY = ?  AND LE_BOOK = ?  AND BANK_GL_CODE = ?  AND RULE_ID = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(CbGlMappingsVb vObject) {
		String query = "Delete From CB_GL_MAPPINGS_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND BANK_GL_CODE = ?  AND RULE_ID = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getBankGlCode(), vObject.getRuleId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(CbGlMappingsVb vObject) {
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

		if (ValidationUtil.isValid(vObject.getBankGlCode()))
			strAudit.append("BANK_GL_CODE" + auditDelimiterColVal + vObject.getBankGlCode().trim());
		else
			strAudit.append("BANK_GL_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRuleId()))
			strAudit.append("RULE_ID" + auditDelimiterColVal + vObject.getRuleId().trim());
		else
			strAudit.append("RULE_ID" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAttribute01At()))
			strAudit.append("ATTRIBUTE_01_AT" + auditDelimiterColVal + vObject.getAttribute01At().trim());
		else
			strAudit.append("ATTRIBUTE_01_AT" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAttribute01()))
			strAudit.append("ATTRIBUTE_01" + auditDelimiterColVal + vObject.getAttribute01().trim());
		else
			strAudit.append("ATTRIBUTE_01" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAttribute02At()))
			strAudit.append("ATTRIBUTE_02_AT" + auditDelimiterColVal + vObject.getAttribute02At().trim());
		else
			strAudit.append("ATTRIBUTE_02_AT" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAttribute02()))
			strAudit.append("ATTRIBUTE_02" + auditDelimiterColVal + vObject.getAttribute02().trim());
		else
			strAudit.append("ATTRIBUTE_02" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAttribute03At()))
			strAudit.append("ATTRIBUTE_03_AT" + auditDelimiterColVal + vObject.getAttribute03At().trim());
		else
			strAudit.append("ATTRIBUTE_03_AT" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAttribute03()))
			strAudit.append("ATTRIBUTE_03" + auditDelimiterColVal + vObject.getAttribute03().trim());
		else
			strAudit.append("ATTRIBUTE_03" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_MAP_STATUS_NT" + auditDelimiterColVal + vObject.getGlMapStatusNt());
		strAudit.append(auditDelimiter);

		strAudit.append("GL_MAP_STATUS" + auditDelimiterColVal + vObject.getGlMapStatus());
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
		serviceName = "CbGlMappings";
		serviceDesc = "Cb Gl Mappings";
		tableName = "CB_GL_MAPPINGS";
		childTableName = "CB_GL_MAPPINGS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();

	}

}
