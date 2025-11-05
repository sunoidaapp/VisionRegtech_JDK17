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
import com.vision.vb.GlCodesVb;
import com.vision.vb.SmartSearchVb;

@Component
public class GlCodesDao extends AbstractDao<GlCodesVb> {

	/******* Mapper Start **********/
	String CountryApprDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.COUNTRY) COUNTRY_DESC";
	String CountryPendDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.COUNTRY) COUNTRY_DESC";
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

	String GlTypeNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7001, "TAppr.GL_TYPE", "GL_TYPE_DESC");
	String GlTypeNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7001, "TPend.GL_TYPE", "GL_TYPE_DESC");

	String AccountTypeAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5025, "TAppr.ACCOUNT_TYPE",
			"ACCOUNT_TYPE_DESC");
	String AccountTypeAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5025, "TPend.ACCOUNT_TYPE",
			"ACCOUNT_TYPE_DESC");

	String ProductTypeAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5026, "TAppr.PRODUCT_TYPE",
			"PRODUCT_TYPE_DESC");
	String ProductTypeAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5026, "TPend.PRODUCT_TYPE",
			"PRODUCT_TYPE_DESC");

	String ChartOfAccountAtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5027, "TAppr.CHART_OF_ACCOUNT",
			"CHART_OF_ACCOUNT_DESC");
	String ChartOfAccountAtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5027, "TPend.CHART_OF_ACCOUNT",
			"CHART_OF_ACCOUNT_DESC");

	String GlAttribute01AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5028, "TAppr.GL", "GL_DESC");
	String GlAttribute01AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5028, "TPend.GL", "GL_DESC");

	String GlAttribute02AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5029, "TAppr.GL", "GL_DESC");
	String GlAttribute02AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5029, "TPend.GL", "GL_DESC");

	String GlAttribute03AtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5031, "TAppr.GL", "GL_DESC");
	String GlAttribute03AtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 5031, "TPend.GL", "GL_DESC");

	String InterestBasisNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7002, "TAppr.INTEREST_BASIS",
			"INTEREST_BASIS_DESC");
	String InterestBasisNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7002, "TPend.INTEREST_BASIS",
			"INTEREST_BASIS_DESC");

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
				GlCodesVb vObject = new GlCodesVb();
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
				if (rs.getString("VISION_GL") != null) {
					vObject.setVisionGl(rs.getString("VISION_GL"));
				} else {
					vObject.setVisionGl("");
				}
				if (rs.getString("GL_DESCRIPTION") != null) {
					vObject.setGlDescription(rs.getString("GL_DESCRIPTION"));
				} else {
					vObject.setGlDescription("");
				}
				vObject.setGlTypeNt(rs.getInt("GL_TYPE_NT"));
				vObject.setGlType(rs.getInt("GL_TYPE"));
				if (rs.getString("DEFAULT_POOL_CODE") != null) {
					vObject.setDefaultPoolCode(rs.getString("DEFAULT_POOL_CODE"));
				} else {
					vObject.setDefaultPoolCode("");
				}
				vObject.setGlSignReversal(rs.getInt("GL_SIGN_REVERSAL"));
				if (rs.getString("DR_CR_MAP_IND") != null) {
					vObject.setDrCrMapInd(rs.getString("DR_CR_MAP_IND"));
				} else {
					vObject.setDrCrMapInd("");
				}
				if (rs.getString("GL_STATUS_DATE") != null) {
					vObject.setGlStatusDate(rs.getString("GL_STATUS_DATE"));
				} else {
					vObject.setGlStatusDate("");
				}
				vObject.setAccountTypeAt(rs.getInt("ACCOUNT_TYPE_AT"));
				if (rs.getString("ACCOUNT_TYPE") != null) {
					vObject.setAccountType(rs.getString("ACCOUNT_TYPE"));
				} else {
					vObject.setAccountType("");
				}
				vObject.setProductTypeAt(rs.getInt("PRODUCT_TYPE_AT"));
				if (rs.getString("PRODUCT_TYPE") != null) {
					vObject.setProductType(rs.getString("PRODUCT_TYPE"));
				} else {
					vObject.setProductType("");
				}
				vObject.setChartOfAccountAt(rs.getInt("CHART_OF_ACCOUNT_AT"));
				if (rs.getString("CHART_OF_ACCOUNT") != null) {
					vObject.setChartOfAccount(rs.getString("CHART_OF_ACCOUNT"));
				} else {
					vObject.setChartOfAccount("");
				}
				vObject.setGlAttribute01At(rs.getInt("GL_ATTRIBUTE_01_AT"));
				if (rs.getString("GL_ATTRIBUTE_01") != null) {
					vObject.setGlAttribute01(rs.getString("GL_ATTRIBUTE_01"));
				} else {
					vObject.setGlAttribute01("");
				}
				vObject.setGlAttribute02At(rs.getInt("GL_ATTRIBUTE_02_AT"));
				if (rs.getString("GL_ATTRIBUTE_02") != null) {
					vObject.setGlAttribute02(rs.getString("GL_ATTRIBUTE_02"));
				} else {
					vObject.setGlAttribute02("");
				}
				vObject.setGlAttribute03At(rs.getInt("GL_ATTRIBUTE_03_AT"));
				if (rs.getString("GL_ATTRIBUTE_03") != null) {
					vObject.setGlAttribute03(rs.getString("GL_ATTRIBUTE_03"));
				} else {
					vObject.setGlAttribute03("");
				}
				if (rs.getString("RESERVE_CODE") != null) {
					vObject.setReserveCode(rs.getString("RESERVE_CODE"));
				} else {
					vObject.setReserveCode("");
				}
				vObject.setInterestBasisNt(rs.getInt("INTEREST_BASIS_NT"));
				vObject.setInterestBasis(rs.getInt("INTEREST_BASIS"));
				vObject.setGlStatusNt(rs.getInt("GL_STATUS_NT"));
				vObject.setGlStatus(rs.getInt("GL_STATUS"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
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
				vObject.setGlStatusDesc(rs.getString("GL_STATUS_DESC"));
				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/
	public List<GlCodesVb> getQueryPopupResults(GlCodesVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from (Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.VISION_GL" + ",TAppr.GL_DESCRIPTION" + ",TAppr.GL_TYPE_NT" + ",TAppr.GL_TYPE"
				+ ",TAppr.DEFAULT_POOL_CODE" + ",TAppr.GL_SIGN_REVERSAL" + ",TAppr.DR_CR_MAP_IND"
				+ ",TAppr.GL_STATUS_DATE" + ",TAppr.ACCOUNT_TYPE_AT" + ",TAppr.ACCOUNT_TYPE" + ",TAppr.PRODUCT_TYPE_AT"
				+ ",TAppr.PRODUCT_TYPE" + ",TAppr.CHART_OF_ACCOUNT_AT" + ",TAppr.CHART_OF_ACCOUNT"
				+ ",TAppr.GL_ATTRIBUTE_01_AT" + ",TAppr.GL_ATTRIBUTE_01" + ",TAppr.GL_ATTRIBUTE_02_AT"
				+ ",TAppr.GL_ATTRIBUTE_02" + ",TAppr.GL_ATTRIBUTE_03_AT" + ",TAppr.GL_ATTRIBUTE_03"
				+ ",TAppr.RESERVE_CODE" + ",TAppr.INTEREST_BASIS_NT" + ",TAppr.INTEREST_BASIS" + ",TAppr.GL_STATUS_NT"
				+ ",TAppr.GL_STATUS" + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER,"+makerApprDesc+""
				+ ",TAppr.VERIFIER , "+verifierApprDesc+"" + ",TAppr.INTERNAL_STATUS , " + dateFormat+"(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED, " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ ", DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1" + ",  " + RecordIndicatorNtApprDesc + " , "
				+ GlStatusNtApprDesc + "" + " from GL_CODES TAppr)TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From GL_CODES_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.VISION_GL = TPend.VISION_GL )");
		StringBuffer strBufPending = new StringBuffer("select * from (Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.VISION_GL" + ",TPend.GL_DESCRIPTION" + ",TPend.GL_TYPE_NT" + ",TPend.GL_TYPE"
				+ ",TPend.DEFAULT_POOL_CODE" + ",TPend.GL_SIGN_REVERSAL" + ",TPend.DR_CR_MAP_IND"
				+ ",TPend.GL_STATUS_DATE" + ",TPend.ACCOUNT_TYPE_AT" + ",TPend.ACCOUNT_TYPE" + ",TPend.PRODUCT_TYPE_AT"
				+ ",TPend.PRODUCT_TYPE" + ",TPend.CHART_OF_ACCOUNT_AT" + ",TPend.CHART_OF_ACCOUNT"
				+ ",TPend.GL_ATTRIBUTE_01_AT" + ",TPend.GL_ATTRIBUTE_01" + ",TPend.GL_ATTRIBUTE_02_AT"
				+ ",TPend.GL_ATTRIBUTE_02" + ",TPend.GL_ATTRIBUTE_03_AT" + ",TPend.GL_ATTRIBUTE_03"
				+ ",TPend.RESERVE_CODE" + ",TPend.INTEREST_BASIS_NT" + ",TPend.INTEREST_BASIS" + ",TPend.GL_STATUS_NT"
				+ ",TPend.GL_STATUS" + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + ",TPend.MAKER,"+makerPendDesc+""
				+ ",TPend.VERIFIER,"+verifierPendDesc+",TPend.INTERNAL_STATUS , " + dateFormat+"(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED, " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION "
				+ " ,DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + RecordIndicatorNtPendDesc + " , " + GlStatusNtPendDesc
				+ " from GL_CODES_PEND TPend)TPend ");
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

					case "visionGl":
						CommonUtils.addToQuerySearch(" upper(TAppr.VISION_GL) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VISION_GL) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glType":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_TYPE) " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_TYPE) " + val, strBufPending, data.getJoinType());
						break;

					case "defaultPoolCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.DEFAULT_POOL_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.DEFAULT_POOL_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glSignReversal":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_SIGN_REVERSAL) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_SIGN_REVERSAL) " + val, strBufPending,
								data.getJoinType());
						break;

					case "drCrMapInd":
						CommonUtils.addToQuerySearch(" upper(TAppr.DR_CR_MAP_IND) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.DR_CR_MAP_IND) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glStatusDate":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_STATUS_DATE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_STATUS_DATE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "accountType":
						CommonUtils.addToQuerySearch(" upper(TAppr.ACCOUNT_TYPE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.ACCOUNT_TYPE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "productType":
						CommonUtils.addToQuerySearch(" upper(TAppr.PRODUCT_TYPE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.PRODUCT_TYPE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "chartOfAccount":
						CommonUtils.addToQuerySearch(" upper(TAppr.CHART_OF_ACCOUNT) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CHART_OF_ACCOUNT) " + val, strBufPending,
								data.getJoinType());
						break;

					case "reserveCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.RESERVE_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RESERVE_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "interestBasis":
						CommonUtils.addToQuerySearch(" upper(TAppr.INTEREST_BASIS) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.INTEREST_BASIS) " + val, strBufPending,
								data.getJoinType());
						break;

					case "glStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.GL_STATUS) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.GL_STATUS) " + val, strBufPending,
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
			String orderBy = " Order By DATE_LAST_MODIFIED_1 desc  ";
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

	public List<GlCodesVb> getQueryResults(GlCodesVb dObj, int intStatus) {

		setServiceDefaults();

		List<GlCodesVb> collTemp = null;

		final int intKeyFieldsCount = 3;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK" + ",TAppr.VISION_GL"
				+ ",TAppr.GL_DESCRIPTION" + ",TAppr.GL_TYPE_NT" + ",TAppr.GL_TYPE" + ",TAppr.DEFAULT_POOL_CODE"
				+ ",TAppr.GL_SIGN_REVERSAL" + ",TAppr.DR_CR_MAP_IND"
				+ ",to_char(TAppr.GL_STATUS_DATE,'DD-Mon-YYYY') GL_STATUS_DATE" + ",TAppr.ACCOUNT_TYPE_AT"
				+ ",TAppr.ACCOUNT_TYPE" + ",TAppr.PRODUCT_TYPE_AT" + ",TAppr.PRODUCT_TYPE"
				+ ",TAppr.CHART_OF_ACCOUNT_AT" + ",TAppr.CHART_OF_ACCOUNT" + ",TAppr.GL_ATTRIBUTE_01_AT"
				+ ",TAppr.GL_ATTRIBUTE_01" + ",TAppr.GL_ATTRIBUTE_02_AT" + ",TAppr.GL_ATTRIBUTE_02"
				+ ",TAppr.GL_ATTRIBUTE_03_AT" + ",TAppr.GL_ATTRIBUTE_03" + ",TAppr.RESERVE_CODE"
				+ ",TAppr.INTEREST_BASIS_NT" + ",TAppr.INTEREST_BASIS" + ",TAppr.GL_STATUS_NT" + ",TAppr.GL_STATUS"
				+ ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER, "+makerApprDesc+"" + ",TAppr.VERIFIER,"+verifierApprDesc+""
				+ ",TAppr.INTERNAL_STATUS," + dateFormat+"(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED "
				+ ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION ,"
				+ RecordIndicatorNtApprDesc + " , " + GlStatusNtApprDesc
				+ " From GL_CODES TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_GL = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK" + ",TPend.VISION_GL"
				+ ",TPend.GL_DESCRIPTION" + ",TPend.GL_TYPE_NT" + ",TPend.GL_TYPE" + ",TPend.DEFAULT_POOL_CODE"
				+ ",TPend.GL_SIGN_REVERSAL" + ",TPend.DR_CR_MAP_IND"
				+ ",to_char(TPend.GL_STATUS_DATE,'DD-Mon-YYYY') GL_STATUS_DATE" + ",TPend.ACCOUNT_TYPE_AT"
				+ ",TPend.ACCOUNT_TYPE" + ",TPend.PRODUCT_TYPE_AT" + ",TPend.PRODUCT_TYPE"
				+ ",TPend.CHART_OF_ACCOUNT_AT" + ",TPend.CHART_OF_ACCOUNT" + ",TPend.GL_ATTRIBUTE_01_AT"
				+ ",TPend.GL_ATTRIBUTE_01" + ",TPend.GL_ATTRIBUTE_02_AT" + ",TPend.GL_ATTRIBUTE_02"
				+ ",TPend.GL_ATTRIBUTE_03_AT" + ",TPend.GL_ATTRIBUTE_03" + ",TPend.RESERVE_CODE"
				+ ",TPend.INTEREST_BASIS_NT" + ",TPend.INTEREST_BASIS" + ",TPend.GL_STATUS_NT" + ",TPend.GL_STATUS"
				+ ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + ",TPend.MAKER ,"+makerPendDesc+"" + ",TPend.VERIFIER, "+verifierPendDesc+""
				+ ",TPend.INTERNAL_STATUS," + dateFormat+"(TPend.DATE_LAST_MODIFIED, " + dateFormatStr + ") DATE_LAST_MODIFIED "
				+ ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr + ") DATE_CREATION, "
				+ RecordIndicatorNtPendDesc + " , " + GlStatusNtPendDesc
				+ " From GL_CODES_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_GL = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getVisionGl();

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
	protected List<GlCodesVb> selectApprovedRecord(GlCodesVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<GlCodesVb> doSelectPendingRecord(GlCodesVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(GlCodesVb records) {
		return records.getGlStatus();
	}

	@Override
	protected void setStatus(GlCodesVb vObject, int status) {
		vObject.setGlStatus(status);
	}

	@Override
	protected int doInsertionAppr(GlCodesVb vObject) {
		String query = "Insert Into GL_CODES (COUNTRY, LE_BOOK, VISION_GL, GL_DESCRIPTION, GL_TYPE_NT, GL_TYPE, DEFAULT_POOL_CODE, GL_SIGN_REVERSAL, DR_CR_MAP_IND, GL_STATUS_DATE, ACCOUNT_TYPE_AT, ACCOUNT_TYPE, PRODUCT_TYPE_AT, PRODUCT_TYPE, CHART_OF_ACCOUNT_AT, CHART_OF_ACCOUNT, GL_ATTRIBUTE_01_AT, GL_ATTRIBUTE_01, GL_ATTRIBUTE_02_AT, GL_ATTRIBUTE_02, GL_ATTRIBUTE_03_AT, GL_ATTRIBUTE_03, RESERVE_CODE, INTEREST_BASIS_NT, INTEREST_BASIS, GL_STATUS_NT, GL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl(), vObject.getGlDescription(),
				vObject.getGlTypeNt(), vObject.getGlType(), vObject.getDefaultPoolCode(), vObject.getGlSignReversal(),
				vObject.getDrCrMapInd(), vObject.getGlStatusDate(), vObject.getAccountTypeAt(),
				vObject.getAccountType(), vObject.getProductTypeAt(), vObject.getProductType(),
				vObject.getChartOfAccountAt(), vObject.getChartOfAccount(), vObject.getGlAttribute01At(),
				vObject.getGlAttribute01(), vObject.getGlAttribute02At(), vObject.getGlAttribute02(),
				vObject.getGlAttribute03At(), vObject.getGlAttribute03(), vObject.getReserveCode(),
				vObject.getInterestBasisNt(), vObject.getInterestBasis(), vObject.getGlStatusNt(),
				vObject.getGlStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(GlCodesVb vObject) {
		String query = "Insert Into GL_CODES_PEND (COUNTRY, LE_BOOK, VISION_GL, GL_DESCRIPTION, GL_TYPE_NT, GL_TYPE, DEFAULT_POOL_CODE, GL_SIGN_REVERSAL, DR_CR_MAP_IND, GL_STATUS_DATE, ACCOUNT_TYPE_AT, ACCOUNT_TYPE, PRODUCT_TYPE_AT, PRODUCT_TYPE, CHART_OF_ACCOUNT_AT, CHART_OF_ACCOUNT, GL_ATTRIBUTE_01_AT, GL_ATTRIBUTE_01, GL_ATTRIBUTE_02_AT, GL_ATTRIBUTE_02, GL_ATTRIBUTE_03_AT, GL_ATTRIBUTE_03, RESERVE_CODE, INTEREST_BASIS_NT, INTEREST_BASIS, GL_STATUS_NT, GL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl(), vObject.getGlDescription(),
				vObject.getGlTypeNt(), vObject.getGlType(), vObject.getDefaultPoolCode(), vObject.getGlSignReversal(),
				vObject.getDrCrMapInd(), vObject.getGlStatusDate(), vObject.getAccountTypeAt(),
				vObject.getAccountType(), vObject.getProductTypeAt(), vObject.getProductType(),
				vObject.getChartOfAccountAt(), vObject.getChartOfAccount(), vObject.getGlAttribute01At(),
				vObject.getGlAttribute01(), vObject.getGlAttribute02At(), vObject.getGlAttribute02(),
				vObject.getGlAttribute03At(), vObject.getGlAttribute03(), vObject.getReserveCode(),
				vObject.getInterestBasisNt(), vObject.getInterestBasis(), vObject.getGlStatusNt(),
				vObject.getGlStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(GlCodesVb vObject) {
		String query = "Insert Into GL_CODES_PEND (COUNTRY, LE_BOOK, VISION_GL, GL_DESCRIPTION, GL_TYPE_NT, GL_TYPE, DEFAULT_POOL_CODE, GL_SIGN_REVERSAL, DR_CR_MAP_IND, "
				+ "GL_STATUS_DATE, ACCOUNT_TYPE_AT, ACCOUNT_TYPE, PRODUCT_TYPE_AT, PRODUCT_TYPE, CHART_OF_ACCOUNT_AT, CHART_OF_ACCOUNT, GL_ATTRIBUTE_01_AT, GL_ATTRIBUTE_01, GL_ATTRIBUTE_02_AT, GL_ATTRIBUTE_02, GL_ATTRIBUTE_03_AT, GL_ATTRIBUTE_03, RESERVE_CODE, INTEREST_BASIS_NT, INTEREST_BASIS, GL_STATUS_NT, GL_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
				+ systemDate + ", " + dateTimeConvert + ")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl(), vObject.getGlDescription(),
				vObject.getGlTypeNt(), vObject.getGlType(), vObject.getDefaultPoolCode(), vObject.getGlSignReversal(),
				vObject.getDrCrMapInd(), vObject.getGlStatusDate(), vObject.getAccountTypeAt(),
				vObject.getAccountType(), vObject.getProductTypeAt(), vObject.getProductType(),
				vObject.getChartOfAccountAt(), vObject.getChartOfAccount(), vObject.getGlAttribute01At(),
				vObject.getGlAttribute01(), vObject.getGlAttribute02At(), vObject.getGlAttribute02(),
				vObject.getGlAttribute03At(), vObject.getGlAttribute03(), vObject.getReserveCode(),
				vObject.getInterestBasisNt(), vObject.getInterestBasis(), vObject.getGlStatusNt(),
				vObject.getGlStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(GlCodesVb vObject) {
		String query = "Update GL_CODES Set GL_DESCRIPTION = ?, GL_TYPE_NT = ?, GL_TYPE = ?, DEFAULT_POOL_CODE = ?, "
				+ "GL_SIGN_REVERSAL = ?, DR_CR_MAP_IND = ?, GL_STATUS_DATE = " + dateConvert
				+ ", ACCOUNT_TYPE_AT = ?, ACCOUNT_TYPE = ?, PRODUCT_TYPE_AT = ?,"
				+ " PRODUCT_TYPE = ?, CHART_OF_ACCOUNT_AT = ?, CHART_OF_ACCOUNT = ?, GL_ATTRIBUTE_01_AT = ?, GL_ATTRIBUTE_01 = ?, GL_ATTRIBUTE_02_AT = ?,"
				+ " GL_ATTRIBUTE_02 = ?, GL_ATTRIBUTE_03_AT = ?, GL_ATTRIBUTE_03 = ?, RESERVE_CODE = ?, INTEREST_BASIS_NT = ?, INTEREST_BASIS = ?, GL_STATUS_NT = ?,"
				+ " GL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " " + "WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_GL = ? ";
		Object[] args = { vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
				vObject.getDefaultPoolCode(), vObject.getGlSignReversal(), vObject.getDrCrMapInd(),
				vObject.getGlStatusDate(), vObject.getAccountTypeAt(), vObject.getAccountType(),
				vObject.getProductTypeAt(), vObject.getProductType(), vObject.getChartOfAccountAt(),
				vObject.getChartOfAccount(), vObject.getGlAttribute01At(), vObject.getGlAttribute01(),
				vObject.getGlAttribute02At(), vObject.getGlAttribute02(), vObject.getGlAttribute03At(),
				vObject.getGlAttribute03(), vObject.getReserveCode(), vObject.getInterestBasisNt(),
				vObject.getInterestBasis(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(GlCodesVb vObject) {
		String query = "Update GL_CODES_PEND Set GL_DESCRIPTION = ?, GL_TYPE_NT = ?, GL_TYPE = ?, DEFAULT_POOL_CODE = ?, GL_SIGN_REVERSAL = ?, DR_CR_MAP_IND = ?, "
				+ "GL_STATUS_DATE = " + dateConvert
				+ ", ACCOUNT_TYPE_AT = ?, ACCOUNT_TYPE = ?, PRODUCT_TYPE_AT = ?, PRODUCT_TYPE = ?, CHART_OF_ACCOUNT_AT = ?, CHART_OF_ACCOUNT = ?,"
				+ " GL_ATTRIBUTE_01_AT = ?, GL_ATTRIBUTE_01 = ?, GL_ATTRIBUTE_02_AT = ?, GL_ATTRIBUTE_02 = ?, GL_ATTRIBUTE_03_AT = ?, GL_ATTRIBUTE_03 = ?, RESERVE_CODE = ?, "
				+ "INTEREST_BASIS_NT = ?, INTEREST_BASIS = ?, GL_STATUS_NT = ?, GL_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?,"
				+ " INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = " + systemDate
				+ "  WHERE COUNTRY = ?  AND LE_BOOK = ?  AND VISION_GL = ? ";
		Object[] args = { vObject.getGlDescription(), vObject.getGlTypeNt(), vObject.getGlType(),
				vObject.getDefaultPoolCode(), vObject.getGlSignReversal(), vObject.getDrCrMapInd(),
				vObject.getGlStatusDate(), vObject.getAccountTypeAt(), vObject.getAccountType(),
				vObject.getProductTypeAt(), vObject.getProductType(), vObject.getChartOfAccountAt(),
				vObject.getChartOfAccount(), vObject.getGlAttribute01At(), vObject.getGlAttribute01(),
				vObject.getGlAttribute02At(), vObject.getGlAttribute02(), vObject.getGlAttribute03At(),
				vObject.getGlAttribute03(), vObject.getReserveCode(), vObject.getInterestBasisNt(),
				vObject.getInterestBasis(), vObject.getGlStatusNt(), vObject.getGlStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(GlCodesVb vObject) {
		String query = "Delete From GL_CODES Where COUNTRY = ?  AND LE_BOOK = ?  AND VISION_GL = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(GlCodesVb vObject) {
		String query = "Delete From GL_CODES_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND VISION_GL = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getVisionGl() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(GlCodesVb vObject) {
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

		if (ValidationUtil.isValid(vObject.getVisionGl()))
			strAudit.append("VISION_GL" + auditDelimiterColVal + vObject.getVisionGl().trim());
		else
			strAudit.append("VISION_GL" + auditDelimiterColVal + "NULL");
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

		if (ValidationUtil.isValid(vObject.getDefaultPoolCode()))
			strAudit.append("DEFAULT_POOL_CODE" + auditDelimiterColVal + vObject.getDefaultPoolCode().trim());
		else
			strAudit.append("DEFAULT_POOL_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_SIGN_REVERSAL" + auditDelimiterColVal + vObject.getGlSignReversal());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getDrCrMapInd()))
			strAudit.append("DR_CR_MAP_IND" + auditDelimiterColVal + vObject.getDrCrMapInd().trim());
		else
			strAudit.append("DR_CR_MAP_IND" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlStatusDate()))
			strAudit.append("GL_STATUS_DATE" + auditDelimiterColVal + vObject.getGlStatusDate().trim());
		else
			strAudit.append("GL_STATUS_DATE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("ACCOUNT_TYPE_AT" + auditDelimiterColVal + vObject.getAccountTypeAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getAccountType()))
			strAudit.append("ACCOUNT_TYPE" + auditDelimiterColVal + vObject.getAccountType().trim());
		else
			strAudit.append("ACCOUNT_TYPE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("PRODUCT_TYPE_AT" + auditDelimiterColVal + vObject.getProductTypeAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getProductType()))
			strAudit.append("PRODUCT_TYPE" + auditDelimiterColVal + vObject.getProductType().trim());
		else
			strAudit.append("PRODUCT_TYPE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("CHART_OF_ACCOUNT_AT" + auditDelimiterColVal + vObject.getChartOfAccountAt());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getChartOfAccount()))
			strAudit.append("CHART_OF_ACCOUNT" + auditDelimiterColVal + vObject.getChartOfAccount().trim());
		else
			strAudit.append("CHART_OF_ACCOUNT" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_ATTRIBUTE_01_AT" + auditDelimiterColVal + vObject.getGlAttribute01At());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlAttribute01()))
			strAudit.append("GL_ATTRIBUTE_01" + auditDelimiterColVal + vObject.getGlAttribute01().trim());
		else
			strAudit.append("GL_ATTRIBUTE_01" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_ATTRIBUTE_02_AT" + auditDelimiterColVal + vObject.getGlAttribute02At());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlAttribute02()))
			strAudit.append("GL_ATTRIBUTE_02" + auditDelimiterColVal + vObject.getGlAttribute02().trim());
		else
			strAudit.append("GL_ATTRIBUTE_02" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("GL_ATTRIBUTE_03_AT" + auditDelimiterColVal + vObject.getGlAttribute03At());
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getGlAttribute03()))
			strAudit.append("GL_ATTRIBUTE_03" + auditDelimiterColVal + vObject.getGlAttribute03().trim());
		else
			strAudit.append("GL_ATTRIBUTE_03" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getReserveCode()))
			strAudit.append("RESERVE_CODE" + auditDelimiterColVal + vObject.getReserveCode().trim());
		else
			strAudit.append("RESERVE_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("INTEREST_BASIS_NT" + auditDelimiterColVal + vObject.getInterestBasisNt());
		strAudit.append(auditDelimiter);

		strAudit.append("INTEREST_BASIS" + auditDelimiterColVal + vObject.getInterestBasis());
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
		serviceName = "GlCodes";
		serviceDesc = "Gl Codes";
		tableName = "GL_CODES";
		childTableName = "GL_CODES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();

	}

}
