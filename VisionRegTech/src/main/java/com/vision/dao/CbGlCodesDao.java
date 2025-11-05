package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.*;
import com.vision.vb.CbGlCodesVb;
import com.vision.vb.SmartSearchVb;

@Component
public class CbGlCodesDao extends AbstractDao<CbGlCodesVb> {

	/******* Mapper Start **********/

	String GlTypeNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7001, "TAppr.GL_TYPE", "GL_TYPE_DESC");
	String GlTypeNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7001, "TPend.GL_TYPE", "GL_TYPE_DESC");

	String GlAttribute1AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5004, "TAppr.GL_ATTRIBUTE_1",
			"GL_ATTRIBUTE_1_DESC");
	String GlAttribute1AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5004, "TPend.GL_ATTRIBUTE_1",
			"GL_ATTRIBUTE_1_DESC");

	String GlAttribute2AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5005, "TAppr.GL_ATTRIBUTE_2",
			"GL_ATTRIBUTE_2_DESC");
	String GlAttribute2AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5005, "TPend.GL_ATTRIBUTE_2",
			"GL_ATTRIBUTE_2_DESC");

	String GlAttribute3AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5006, "TAppr.GL_ATTRIBUTE_3",
			"GL_ATTRIBUTE_3_DESC");
	String GlAttribute3AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5006, "TPend.GL_ATTRIBUTE_3",
			"GL_ATTRIBUTE_3_DESC");

	String ParentGlCodeApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5030, "TAppr.PARENT_GL_CODE",
			"PARENT_GL_CODE_DESC");
	String ParentGlCodePendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5030, "TPend.PARENT_GL_CODE",
			"PARENT_GL_CODE_DESC");

	String GlStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.GL_STATUS",
			"GL_STATUS_DESC");
	String GlStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.GL_STATUS",
			"GL_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CbGlCodesVb vObject = new CbGlCodesVb();
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
				if (rs.getString("GL_CODE") != null) {
					vObject.setGlCode(rs.getString("GL_CODE"));
				} else {
					vObject.setGlCode("");
				}
				if (rs.getString("GL_DESCRIPTION") != null) {
					vObject.setGlDescription(rs.getString("GL_DESCRIPTION"));
				} else {
					vObject.setGlDescription("");
				}
				vObject.setGlTypeNt(rs.getInt("GL_TYPE_NT"));
				vObject.setGlType(rs.getInt("GL_TYPE"));
				if (rs.getString("PARENT_GL_CODE") != null) {
					vObject.setParentGlCode(rs.getString("PARENT_GL_CODE"));
				} else {
					vObject.setParentGlCode("");
				}
				vObject.setGlAttribute1At(rs.getInt("GL_ATTRIBUTE_1_AT"));
				if (rs.getString("GL_ATTRIBUTE_1") != null) {
					vObject.setGlAttribute1(rs.getString("GL_ATTRIBUTE_1"));
				} else {
					vObject.setGlAttribute1("");
				}
				vObject.setGlAttribute2At(rs.getInt("GL_ATTRIBUTE_2_AT"));
				if (rs.getString("GL_ATTRIBUTE_2") != null) {
					vObject.setGlAttribute2(rs.getString("GL_ATTRIBUTE_2"));
				} else {
					vObject.setGlAttribute2("");
				}
				vObject.setGlAttribute3At(rs.getInt("GL_ATTRIBUTE_3_AT"));
				if (rs.getString("GL_ATTRIBUTE_3") != null) {
					vObject.setGlAttribute3(rs.getString("GL_ATTRIBUTE_3"));
				} else {
					vObject.setGlAttribute3("");
				}
				vObject.setGlStatusNt(rs.getInt("GL_STATUS_NT"));
				vObject.setGlStatus(rs.getInt("GL_STATUS"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
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
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setGlStatus(rs.getInt("GL_STATUS"));
				vObject.setGlStatusDesc(rs.getString("GL_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setGlAttribute1Desc(rs.getString("GL_ATTRIBUTE_1_DESC"));
				vObject.setGlAttribute2Desc(rs.getString("GL_ATTRIBUTE_2_DESC"));
				vObject.setGlAttribute3Desc(rs.getString("GL_ATTRIBUTE_3_DESC"));
				vObject.setGlTypeDesc(rs.getString("GL_TYPE_DESC"));
				vObject.setParentGlCodeDesc(rs.getString("PARENT_GL_CODE_DESC"));
				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/
	public List<CbGlCodesVb> getQueryPopupResults(CbGlCodesVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from (Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.GL_CODE" + ",TAppr.GL_DESCRIPTION" + ",TAppr.GL_TYPE_NT" + ",TAppr.GL_TYPE"
				+ ",TAppr.PARENT_GL_CODE" + ",TAppr.GL_ATTRIBUTE_1_AT" + ",TAppr.GL_ATTRIBUTE_1"
				+ ",TAppr.GL_ATTRIBUTE_2_AT" + ",TAppr.GL_ATTRIBUTE_2" + ",TAppr.GL_ATTRIBUTE_3_AT"
				+ ",TAppr.GL_ATTRIBUTE_3" + ",TAppr.GL_STATUS_NT" + ",TAppr.GL_STATUS" + ",TAppr.RECORD_INDICATOR_NT"
				+ ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER" + " , " + makerApprDesc + " , " + verifierApprDesc + " , "
				+ RecordIndicatorNtApprDesc + " , " + GlStatusNtApprDesc + " , " + GlAttribute1AtApprDesc + " , "
				+ GlAttribute2AtApprDesc + " , " + GlAttribute3AtApprDesc + " , " + GlTypeNtApprDesc + " , "
				+ ParentGlCodeApprDesc + ",TAppr.VERIFIER" + ",TAppr.INTERNAL_STATUS," + dateFormat
				+ "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED, " + dateFormat
				+ "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ ",DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1" + " from CB_GL_CODES TAppr)TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From CB_GL_CODES_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.GL_CODE = TPend.GL_CODE )");
		StringBuffer strBufPending = new StringBuffer("select * from (Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.GL_CODE" + ",TPend.GL_DESCRIPTION" + ",TPend.GL_TYPE_NT" + ",TPend.GL_TYPE"
				+ ",TPend.PARENT_GL_CODE" + ",TPend.GL_ATTRIBUTE_1_AT" + ",TPend.GL_ATTRIBUTE_1"
				+ ",TPend.GL_ATTRIBUTE_2_AT" + ",TPend.GL_ATTRIBUTE_2" + ",TPend.GL_ATTRIBUTE_3_AT"
				+ ",TPend.GL_ATTRIBUTE_3" + ",TPend.GL_STATUS_NT" + ",TPend.GL_STATUS" + ",TPend.RECORD_INDICATOR_NT"
				+ ",TPend.RECORD_INDICATOR" + ",TPend.MAKER" + " , " + makerPendDesc + " , " + verifierPendDesc + " , "
				+ RecordIndicatorNtPendDesc + " , " + GlStatusNtPendDesc + " , " + GlAttribute1AtPendDesc + " , "
				+ GlAttribute2AtPendDesc + " , " + GlAttribute3AtPendDesc + " , " + GlTypeNtPendDesc + " , "
				+ ParentGlCodePendDesc + ",TPend.VERIFIER" + ",TPend.INTERNAL_STATUS," + dateFormat

				+ "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED, " + dateFormat
				+ "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ ", DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1" + " from CB_GL_CODES_PEND TPend )TPend");
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

					case "glCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_CODE) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_CODE) " + val, strBufPending, data.getJoinType());
						break;

					case "glDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_TYPE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_TYPE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "parentGlCodeDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.PARENT_GL_CODE_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PARENT_GL_CODE_DESC) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_STATUS_DESC) " + val, strBufPending,
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
			String orderBy = " Order By DATE_LAST_MODIFIED_1 DESC";
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

	public List<CbGlCodesVb> getQueryResults(CbGlCodesVb dObj, int intStatus) {

		setServiceDefaults();

		List<CbGlCodesVb> collTemp = null;

		final int intKeyFieldsCount = 3;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.GL_CODE"
				+ ",TAppr.GL_DESCRIPTION" + ",TAppr.GL_TYPE_NT" + ",TAppr.GL_TYPE" + ",TAppr.PARENT_GL_CODE"
				+ ",TAppr.GL_ATTRIBUTE_1_AT" + ",TAppr.GL_ATTRIBUTE_1" + ",TAppr.GL_ATTRIBUTE_2_AT"
				+ ",TAppr.GL_ATTRIBUTE_2" + ",TAppr.GL_ATTRIBUTE_3_AT" + ",TAppr.GL_ATTRIBUTE_3" + ",TAppr.GL_STATUS_NT"
				+ ",TAppr.GL_STATUS" + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER" + " , "
				+ makerApprDesc + " , " + verifierApprDesc + " , " + RecordIndicatorNtApprDesc + " , "
				+ GlStatusNtApprDesc + " , " + GlAttribute1AtApprDesc + " , " + GlAttribute2AtApprDesc + " , "
				+ GlAttribute3AtApprDesc + " , " + GlTypeNtApprDesc + " , " + ParentGlCodeApprDesc + ",TAppr.VERIFIER"
				+ ",TAppr.INTERNAL_STATUS," + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED " + ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION " + " From CB_GL_CODES TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND GL_CODE = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.GL_CODE"
				+ ",TPend.GL_DESCRIPTION" + ",TPend.GL_TYPE_NT" + ",TPend.GL_TYPE" + ",TPend.PARENT_GL_CODE"
				+ ",TPend.GL_ATTRIBUTE_1_AT" + ",TPend.GL_ATTRIBUTE_1" + ",TPend.GL_ATTRIBUTE_2_AT"
				+ ",TPend.GL_ATTRIBUTE_2" + ",TPend.GL_ATTRIBUTE_3_AT" + ",TPend.GL_ATTRIBUTE_3" + ",TPend.GL_STATUS_NT"
				+ ",TPend.GL_STATUS" + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + ",TPend.MAKER" + " , "
				+ makerPendDesc + " , " + verifierPendDesc + " , " + RecordIndicatorNtPendDesc + " , "
				+ GlStatusNtPendDesc + " , " + GlAttribute1AtPendDesc + " , " + GlAttribute2AtPendDesc + " , "
				+ GlAttribute3AtPendDesc + " , " + GlTypeNtPendDesc + " , " + ParentGlCodePendDesc + ",TPend.VERIFIER"
				+ ",TPend.INTERNAL_STATUS," + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED " + ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION "
				+ " From CB_GL_CODES_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND GL_CODE = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getGlCode();

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
	protected List<CbGlCodesVb> selectApprovedRecord(CbGlCodesVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<CbGlCodesVb> doSelectPendingRecord(CbGlCodesVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(CbGlCodesVb records) {
		return records.getGlStatus();
	}

	@Override
	protected void setStatus(CbGlCodesVb vObject, int status) {
		vObject.setGlStatus(status);
	}

	@Override
	protected int doInsertionAppr(CbGlCodesVb vObject) {
		String query = "Insert Into CB_GL_CODES (COUNTRY, LE_BOOK, GL_CODE, GL_DESCRIPTION, GL_TYPE_NT, GL_TYPE, PARENT_GL_CODE, GL_ATTRIBUTE_1_AT, GL_ATTRIBUTE_1, GL_ATTRIBUTE_2_AT, GL_ATTRIBUTE_2, GL_ATTRIBUTE_3_AT, GL_ATTRIBUTE_3, GL_STATUS_NT, GL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", "
				+ systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode(), vObject.getGlDescription(),
				vObject.getGlTypeNt(), vObject.getGlType(), vObject.getParentGlCode(), vObject.getGlAttribute1At(),
				vObject.getGlAttribute1(), vObject.getGlAttribute2At(), vObject.getGlAttribute2(),
				vObject.getGlAttribute3At(), vObject.getGlAttribute3(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(CbGlCodesVb vObject) {
		String query = "Insert Into CB_GL_CODES_PEND (COUNTRY, LE_BOOK, GL_CODE, GL_DESCRIPTION, GL_TYPE_NT, GL_TYPE, PARENT_GL_CODE, GL_ATTRIBUTE_1_AT, GL_ATTRIBUTE_1, GL_ATTRIBUTE_2_AT, GL_ATTRIBUTE_2, GL_ATTRIBUTE_3_AT, GL_ATTRIBUTE_3, GL_STATUS_NT, GL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", "
				+ systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode(), vObject.getGlDescription(),
				vObject.getGlTypeNt(), vObject.getGlType(), vObject.getParentGlCode(), vObject.getGlAttribute1At(),
				vObject.getGlAttribute1(), vObject.getGlAttribute2At(), vObject.getGlAttribute2(),
				vObject.getGlAttribute3At(), vObject.getGlAttribute3(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(CbGlCodesVb vObject) {
		String query = "Insert Into CB_GL_CODES_PEND (COUNTRY, LE_BOOK, GL_CODE, GL_DESCRIPTION, GL_TYPE_NT, GL_TYPE, PARENT_GL_CODE, GL_ATTRIBUTE_1_AT, GL_ATTRIBUTE_1, GL_ATTRIBUTE_2_AT, GL_ATTRIBUTE_2, GL_ATTRIBUTE_3_AT, GL_ATTRIBUTE_3, GL_STATUS_NT, GL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", "
				+ dateTimeConvert + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode(), vObject.getGlDescription(),
				vObject.getGlTypeNt(), vObject.getGlType(), vObject.getParentGlCode(), vObject.getGlAttribute1At(),
				vObject.getGlAttribute1(), vObject.getGlAttribute2At(), vObject.getGlAttribute2(),
				vObject.getGlAttribute3At(), vObject.getGlAttribute3(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(CbGlCodesVb vObject) {
		String query = "Update CB_GL_CODES Set GL_DESCRIPTION = ?, GL_TYPE_NT = ?, GL_TYPE = ?, PARENT_GL_CODE = ?, GL_ATTRIBUTE_1_AT = ?, GL_ATTRIBUTE_1 = ?,"
				+ " GL_ATTRIBUTE_2_AT = ?, GL_ATTRIBUTE_2 = ?, GL_ATTRIBUTE_3_AT = ?, GL_ATTRIBUTE_3 = ?, GL_STATUS_NT = ?, GL_STATUS = ?, RECORD_INDICATOR_NT = ?, "
				+ "RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " WHERE COUNTRY = ?  AND LE_BOOK = ?  AND GL_CODE = ? ";
		Object[] args = { vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
				vObject.getParentGlCode(), vObject.getGlAttribute1At(), vObject.getGlAttribute1(),
				vObject.getGlAttribute2At(), vObject.getGlAttribute2(), vObject.getGlAttribute3At(),
				vObject.getGlAttribute3(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(CbGlCodesVb vObject) {
		String query = "Update CB_GL_CODES_PEND Set GL_DESCRIPTION = ?, GL_TYPE_NT = ?, GL_TYPE = ?, PARENT_GL_CODE = ?, GL_ATTRIBUTE_1_AT = ?, "
				+ "GL_ATTRIBUTE_1 = ?, GL_ATTRIBUTE_2_AT = ?, GL_ATTRIBUTE_2 = ?, GL_ATTRIBUTE_3_AT = ?, GL_ATTRIBUTE_3 = ?, GL_STATUS_NT = ?, GL_STATUS = ?, "
				+ "RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + "  WHERE COUNTRY = ?  AND LE_BOOK = ?  AND GL_CODE = ? ";
		Object[] args = { vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
				vObject.getParentGlCode(), vObject.getGlAttribute1At(), vObject.getGlAttribute1(),
				vObject.getGlAttribute2At(), vObject.getGlAttribute2(), vObject.getGlAttribute3At(),
				vObject.getGlAttribute3(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(CbGlCodesVb vObject) {
		String query = "Delete From CB_GL_CODES Where COUNTRY = ?  AND LE_BOOK = ?  AND GL_CODE = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(CbGlCodesVb vObject) {
		String query = "Delete From CB_GL_CODES_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND GL_CODE = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getGlCode() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(CbGlCodesVb vObject) {
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

		if (ValidationUtil.isValid(vObject.getGlCode()))
			strAudit.append("GL_CODE" + auditDelimiterColVal + vObject.getGlCode().trim());
		else
			strAudit.append("GL_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlDescription()))
			strAudit.append("GL_DESCRIPTION" + auditDelimiterColVal + vObject.getGlDescription().trim());
		else
			strAudit.append("GL_DESCRIPTION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_TYPE_NT" + auditDelimiterColVal + vObject.getGlTypeNt());
		strAudit.append(auditDelimiter);

		strAudit.append("GL_TYPE" + auditDelimiterColVal + vObject.getGlType());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getParentGlCode()))
			strAudit.append("PARENT_GL_CODE" + auditDelimiterColVal + vObject.getParentGlCode().trim());
		else
			strAudit.append("PARENT_GL_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_ATTRIBUTE_1_AT" + auditDelimiterColVal + vObject.getGlAttribute1At());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlAttribute1()))
			strAudit.append("GL_ATTRIBUTE_1" + auditDelimiterColVal + vObject.getGlAttribute1().trim());
		else
			strAudit.append("GL_ATTRIBUTE_1" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_ATTRIBUTE_2_AT" + auditDelimiterColVal + vObject.getGlAttribute2At());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlAttribute2()))
			strAudit.append("GL_ATTRIBUTE_2" + auditDelimiterColVal + vObject.getGlAttribute2().trim());
		else
			strAudit.append("GL_ATTRIBUTE_2" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_ATTRIBUTE_3_AT" + auditDelimiterColVal + vObject.getGlAttribute3At());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlAttribute3()))
			strAudit.append("GL_ATTRIBUTE_3" + auditDelimiterColVal + vObject.getGlAttribute3().trim());
		else
			strAudit.append("GL_ATTRIBUTE_3" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_STATUS_NT" + auditDelimiterColVal + vObject.getGlStatusNt());
		strAudit.append(auditDelimiter);

		strAudit.append("GL_STATUS" + auditDelimiterColVal + vObject.getGlStatus());
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
		serviceName = "CbGlCodes";
		serviceDesc = "Cb Gl Codes";
		tableName = "CB_GL_CODES";
		childTableName = "CB_GL_CODES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();

	}

}
