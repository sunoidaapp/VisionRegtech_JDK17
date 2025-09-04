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
import com.vision.vb.RgFatcaRulesVb;
import com.vision.vb.SmartSearchVb;

@Component
public class RgFatcaRulesDao extends AbstractDao<RgFatcaRulesVb> {

	/******* Mapper Start **********/
	String CountryApprDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TAppr.COUNTRY) COUNTRY_DESC";
	String CountryPendDesc = "(SELECT COUNTRY_DESCRIPTION FROM COUNTRIES WHERE COUNTRY = TPend.COUNTRY) COUNTRY_DESC";
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

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
				RgFatcaRulesVb vObject = new RgFatcaRulesVb();
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
				if (rs.getString("FATCA_PRIMARY_NATIONALITY_FLAG") != null) {
					vObject.setFatcaPrimaryNationalityFlag(rs.getString("FATCA_PRIMARY_NATIONALITY_FLAG"));
				} else {
					vObject.setFatcaPrimaryNationalityFlag("");
				}
				if (rs.getString("FATCA_PRIMARY_DUAL_NATIONALITY_FLAG") != null) {
					vObject.setFatcaPrimaryDualNationalityFlag(rs.getString("FATCA_PRIMARY_DUAL_NATIONALITY_FLAG"));
				} else {
					vObject.setFatcaPrimaryDualNationalityFlag("");
				}
				if (rs.getString("FATCA_PRIMARY_RESIDENCE_FLAG") != null) {
					vObject.setFatcaPrimaryResidenceFlag(rs.getString("FATCA_PRIMARY_RESIDENCE_FLAG"));
				} else {
					vObject.setFatcaPrimaryResidenceFlag("");
				}
				if (rs.getString("FATCA_PRIMARY_DOMICILE_FLAG") != null) {
					vObject.setFatcaPrimaryDomicileFlag(rs.getString("FATCA_PRIMARY_DOMICILE_FLAG"));
				} else {
					vObject.setFatcaPrimaryDomicileFlag("");
				}
				if (rs.getString("FATCA_FAMILY_NATIONALITY_FLAG") != null) {
					vObject.setFatcaFamilyNationalityFlag(rs.getString("FATCA_FAMILY_NATIONALITY_FLAG"));
				} else {
					vObject.setFatcaFamilyNationalityFlag("");
				}
				if (rs.getString("FATCA_FAMILY_DUAL_NATIONALITY_FLAG") != null) {
					vObject.setFatcaFamilyDualNationalityFlag(rs.getString("FATCA_FAMILY_DUAL_NATIONALITY_FLAG"));
				} else {
					vObject.setFatcaFamilyDualNationalityFlag("");
				}
				if (rs.getString("FATCA_COUNTRY_LIST") != null) {
					vObject.setFatcaCountryList(rs.getString("FATCA_COUNTRY_LIST"));
				} else {
					vObject.setFatcaCountryList("");
				}
				if (rs.getString("CRS_PRIMARY_NATIONALITY_FLAG") != null) {
					vObject.setCrsPrimaryNationalityFlag(rs.getString("CRS_PRIMARY_NATIONALITY_FLAG"));
				} else {
					vObject.setCrsPrimaryNationalityFlag("");
				}
				if (rs.getString("CRS_PRIMARY_DUAL_NATIONALITY_FLAG") != null) {
					vObject.setCrsPrimaryDualNationalityFlag(rs.getString("CRS_PRIMARY_DUAL_NATIONALITY_FLAG"));
				} else {
					vObject.setCrsPrimaryDualNationalityFlag("");
				}
				if (rs.getString("CRS_PRIMARY_RESIDENCE_FLAG") != null) {
					vObject.setCrsPrimaryResidenceFlag(rs.getString("CRS_PRIMARY_RESIDENCE_FLAG"));
				} else {
					vObject.setCrsPrimaryResidenceFlag("");
				}
				if (rs.getString("CRS_PRIMARY_DOMICILE_FLAG") != null) {
					vObject.setCrsPrimaryDomicileFlag(rs.getString("CRS_PRIMARY_DOMICILE_FLAG"));
				} else {
					vObject.setCrsPrimaryDomicileFlag("");
				}
				if (rs.getString("CRS_FAMILY_NATIONALITY_FLAG") != null) {
					vObject.setCrsFamilyNationalityFlag(rs.getString("CRS_FAMILY_NATIONALITY_FLAG"));
				} else {
					vObject.setCrsFamilyNationalityFlag("");
				}
				if (rs.getString("CRS_FAMILY_DUAL_NATIONALITY_FLAG") != null) {
					vObject.setCrsFamilyDualNationalityFlag(rs.getString("CRS_FAMILY_DUAL_NATIONALITY_FLAG"));
				} else {
					vObject.setCrsFamilyDualNationalityFlag("");
				}
				if (rs.getString("CRS_COUNTRY_LIST") != null) {
					vObject.setCrsCountryList(rs.getString("CRS_COUNTRY_LIST"));
				} else {
					vObject.setCrsCountryList("");
				}
				vObject.setRuleStatusNt(rs.getInt("RULE_STATUS_NT"));
				vObject.setRuleStatus(rs.getInt("RULE_STATUS"));
				vObject.setRuleStatusDesc(rs.getString("RULE_STATUS_DESC"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
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
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/
	public List<RgFatcaRulesVb> getQueryPopupResults(RgFatcaRulesVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.FATCA_PRIMARY_NATIONALITY_FLAG" + ",TAppr.FATCA_PRIMARY_DUAL_NATIONALITY_FLAG"
				+ ",TAppr.FATCA_PRIMARY_RESIDENCE_FLAG" + ",TAppr.FATCA_PRIMARY_DOMICILE_FLAG"
				+ ",TAppr.FATCA_FAMILY_NATIONALITY_FLAG" + ",TAppr.FATCA_FAMILY_DUAL_NATIONALITY_FLAG,"
				+clobFormat+" (TAppr.FATCA_COUNTRY_LIST)FATCA_COUNTRY_LIST  "
				+ ",TAppr.CRS_PRIMARY_NATIONALITY_FLAG"
				+ ",TAppr.CRS_PRIMARY_DUAL_NATIONALITY_FLAG" + ",TAppr.CRS_PRIMARY_RESIDENCE_FLAG"
				+ ",TAppr.CRS_PRIMARY_DOMICILE_FLAG" + ",TAppr.CRS_FAMILY_NATIONALITY_FLAG"
				+ ",TAppr.CRS_FAMILY_DUAL_NATIONALITY_FLAG," 
				+clobFormat+" (TAppr.CRS_COUNTRY_LIST)CRS_COUNTRY_LIST  "
				+ ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS" + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER ,"+RecordIndicatorNtApprDesc+" , "+RuleStatusNtApprDesc
				+ ",TAppr.VERIFIER" + ",TAppr.INTERNAL_STATUS" + ", " +makerApprDesc +" , "+verifierApprDesc + ", " 
				+ dateFormat + "(TAppr.DATE_LAST_MODIFIED, "
				+ dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " from RG_FATCA_RULES TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From RG_FATCA_RULES_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK )");
		StringBuffer strBufPending = new StringBuffer("Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.FATCA_PRIMARY_NATIONALITY_FLAG" + ",TPend.FATCA_PRIMARY_DUAL_NATIONALITY_FLAG"
				+ ",TPend.FATCA_PRIMARY_RESIDENCE_FLAG" + ",TPend.FATCA_PRIMARY_DOMICILE_FLAG"
				+ ",TPend.FATCA_FAMILY_NATIONALITY_FLAG" + ",TPend.FATCA_FAMILY_DUAL_NATIONALITY_FLAG,"
				+clobFormat+" (TPend.FATCA_COUNTRY_LIST)FATCA_COUNTRY_LIST  "
				+ ",TPend.CRS_PRIMARY_NATIONALITY_FLAG"
				+ ",TPend.CRS_PRIMARY_DUAL_NATIONALITY_FLAG" + ",TPend.CRS_PRIMARY_RESIDENCE_FLAG"
				+ ",TPend.CRS_PRIMARY_DOMICILE_FLAG" + ",TPend.CRS_FAMILY_NATIONALITY_FLAG"
				+ ",TPend.CRS_FAMILY_DUAL_NATIONALITY_FLAG," 
				+clobFormat+" (TPend.CRS_COUNTRY_LIST)CRS_COUNTRY_LIST "
				+ ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS" + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + ",TPend.MAKER ,"+RecordIndicatorNtPendDesc+" , "+RuleStatusNtPendDesc
				+ ",TPend.VERIFIER" + ",TPend.INTERNAL_STATUS" + ", " +makerPendDesc +" , "+verifierPendDesc + ", "
				+ dateFormat + "(TPend.DATE_LAST_MODIFIED, "
				+ dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " from RG_FATCA_RULES_PEND TPend ");
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

					case "fatcaPrimaryNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_PRIMARY_NATIONALITY_FLAG) " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_PRIMARY_NATIONALITY_FLAG) " + val,
								strBufPending, data.getJoinType());
						break;

					case "fatcaPrimaryDualNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_PRIMARY_DUAL_NATIONALITY_FLAG) " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_PRIMARY_DUAL_NATIONALITY_FLAG) " + val,
								strBufPending, data.getJoinType());
						break;

					case "fatcaPrimaryResidenceFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_PRIMARY_RESIDENCE_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_PRIMARY_RESIDENCE_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "fatcaPrimaryDomicileFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_PRIMARY_DOMICILE_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_PRIMARY_DOMICILE_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "fatcaFamilyNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_FAMILY_NATIONALITY_FLAG) " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_FAMILY_NATIONALITY_FLAG) " + val,
								strBufPending, data.getJoinType());
						break;

					case "fatcaFamilyDualNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_FAMILY_DUAL_NATIONALITY_FLAG) " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_FAMILY_DUAL_NATIONALITY_FLAG) " + val,
								strBufPending, data.getJoinType());
						break;

					case "fatcaCountryList":
						CommonUtils.addToQuerySearch(" upper(TAppr.FATCA_COUNTRY_LIST) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FATCA_COUNTRY_LIST) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crsPrimaryNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_PRIMARY_NATIONALITY_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_PRIMARY_NATIONALITY_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crsPrimaryDualNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_PRIMARY_DUAL_NATIONALITY_FLAG) " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_PRIMARY_DUAL_NATIONALITY_FLAG) " + val,
								strBufPending, data.getJoinType());
						break;

					case "crsPrimaryResidenceFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_PRIMARY_RESIDENCE_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_PRIMARY_RESIDENCE_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crsPrimaryDomicileFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_PRIMARY_DOMICILE_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_PRIMARY_DOMICILE_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crsFamilyNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_FAMILY_NATIONALITY_FLAG) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_FAMILY_NATIONALITY_FLAG) " + val, strBufPending,
								data.getJoinType());
						break;

					case "crsFamilyDualNationalityFlag":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_FAMILY_DUAL_NATIONALITY_FLAG) " + val,
								strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_FAMILY_DUAL_NATIONALITY_FLAG) " + val,
								strBufPending, data.getJoinType());
						break;

					case "crsCountryList":
						CommonUtils.addToQuerySearch(" upper(TAppr.CRS_COUNTRY_LIST) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CRS_COUNTRY_LIST) " + val, strBufPending,
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
			//  dObj.getCountry - KE 
			if (ValidationUtil.isValid(dObj.getCountry())) {
				CommonUtils.addToQuery(" TAppr.COUNTRY IN ('" + dObj.getCountry() + "') ", strBufApprove);
				CommonUtils.addToQuery(" TPend.COUNTRY IN ('" + dObj.getCountry() + "') ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				CommonUtils.addToQuery(" TAppr.LE_BOOK IN ('" + dObj.getLeBook() + "') ", strBufApprove);
				CommonUtils.addToQuery(" TPend.LE_BOOK IN ('" + dObj.getLeBook() + "') ", strBufPending);
			}
			String orderBy = " Order By COUNTRY, LE_BOOK  ";
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

	public List<RgFatcaRulesVb> getQueryResults(RgFatcaRulesVb dObj, int intStatus) {

		setServiceDefaults();

		List<RgFatcaRulesVb> collTemp = null;

		final int intKeyFieldsCount = 2;
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK"
				+ ",TAppr.FATCA_PRIMARY_NATIONALITY_FLAG" + ",TAppr.FATCA_PRIMARY_DUAL_NATIONALITY_FLAG"
				+ ",TAppr.FATCA_PRIMARY_RESIDENCE_FLAG" + ",TAppr.FATCA_PRIMARY_DOMICILE_FLAG"
				+ ",TAppr.FATCA_FAMILY_NATIONALITY_FLAG" + ",TAppr.FATCA_FAMILY_DUAL_NATIONALITY_FLAG,"
				+clobFormat+" (TAppr.FATCA_COUNTRY_LIST)FATCA_COUNTRY_LIST  "
				+ ",TAppr.CRS_PRIMARY_NATIONALITY_FLAG"
				+ ",TAppr.CRS_PRIMARY_DUAL_NATIONALITY_FLAG" + ",TAppr.CRS_PRIMARY_RESIDENCE_FLAG"
				+ ",TAppr.CRS_PRIMARY_DOMICILE_FLAG" + ",TAppr.CRS_FAMILY_NATIONALITY_FLAG"
				+ ",TAppr.CRS_FAMILY_DUAL_NATIONALITY_FLAG," 
				+clobFormat+" (TAppr.CRS_COUNTRY_LIST)CRS_COUNTRY_LIST  "
				+ ",TAppr.RULE_STATUS_NT"
				+ ",TAppr.RULE_STATUS" + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR" + ",TAppr.MAKER, "+RecordIndicatorNtApprDesc+" , "+RuleStatusNtApprDesc
				+ ",TAppr.VERIFIER" + ",TAppr.INTERNAL_STATUS" + ", " +makerApprDesc +" , "+verifierApprDesc +" , "
				+ dateFormat + "(TAppr.DATE_LAST_MODIFIED, "
				+ dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " From RG_FATCA_RULES TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK"
				+ ",TPend.FATCA_PRIMARY_NATIONALITY_FLAG" + ",TPend.FATCA_PRIMARY_DUAL_NATIONALITY_FLAG"
				+ ",TPend.FATCA_PRIMARY_RESIDENCE_FLAG" + ",TPend.FATCA_PRIMARY_DOMICILE_FLAG"
				+ ",TPend.FATCA_FAMILY_NATIONALITY_FLAG" + ",TPend.FATCA_FAMILY_DUAL_NATIONALITY_FLAG,"
				+clobFormat+" (TPend.FATCA_COUNTRY_LIST)FATCA_COUNTRY_LIST  "
				+ ",TPend.CRS_PRIMARY_NATIONALITY_FLAG"
				+ ",TPend.CRS_PRIMARY_DUAL_NATIONALITY_FLAG" + ",TPend.CRS_PRIMARY_RESIDENCE_FLAG"
				+ ",TPend.CRS_PRIMARY_DOMICILE_FLAG" + ",TPend.CRS_FAMILY_NATIONALITY_FLAG"
				+ ",TPend.CRS_FAMILY_DUAL_NATIONALITY_FLAG," 
				+clobFormat+" (TPend.CRS_COUNTRY_LIST)CRS_COUNTRY_LIST "
				+ ",TPend.RULE_STATUS_NT"
				+ ",TPend.RULE_STATUS" + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR" + ",TPend.MAKER, "+RecordIndicatorNtPendDesc+" , "+RuleStatusNtPendDesc
				+ ",TPend.VERIFIER" + ",TPend.INTERNAL_STATUS" + ", " +makerPendDesc +" , "+verifierPendDesc +" , "
				+ dateFormat + "(TPend.DATE_LAST_MODIFIED, "
				+ dateFormatStr + ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " From RG_FATCA_RULES_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();

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
	protected List<RgFatcaRulesVb> selectApprovedRecord(RgFatcaRulesVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<RgFatcaRulesVb> doSelectPendingRecord(RgFatcaRulesVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(RgFatcaRulesVb records) {
		return records.getRuleStatus();
	}

	@Override
	protected void setStatus(RgFatcaRulesVb vObject, int status) {
		vObject.setRuleStatus(status);
	}

	@Override
	protected int doInsertionAppr(RgFatcaRulesVb vObject) {
		String query = "Insert Into RG_FATCA_RULES (COUNTRY, LE_BOOK, FATCA_PRIMARY_NATIONALITY_FLAG, FATCA_PRIMARY_DUAL_NATIONALITY_FLAG,"
				+ " FATCA_PRIMARY_RESIDENCE_FLAG, FATCA_PRIMARY_DOMICILE_FLAG, FATCA_FAMILY_NATIONALITY_FLAG, FATCA_FAMILY_DUAL_NATIONALITY_FLAG,"
				+ " FATCA_COUNTRY_LIST, CRS_PRIMARY_NATIONALITY_FLAG, CRS_PRIMARY_DUAL_NATIONALITY_FLAG, CRS_PRIMARY_RESIDENCE_FLAG, CRS_PRIMARY_DOMICILE_FLAG,"
				+ " CRS_FAMILY_NATIONALITY_FLAG, CRS_FAMILY_DUAL_NATIONALITY_FLAG, CRS_COUNTRY_LIST, RULE_STATUS_NT, RULE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR,"
				+ " MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+systemDate+")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getFatcaPrimaryNationalityFlag(),
				vObject.getFatcaPrimaryDualNationalityFlag(), vObject.getFatcaPrimaryResidenceFlag(),
				vObject.getFatcaPrimaryDomicileFlag(), vObject.getFatcaFamilyNationalityFlag(),
				vObject.getFatcaFamilyDualNationalityFlag(), vObject.getFatcaCountryList(),
				vObject.getCrsPrimaryNationalityFlag(), vObject.getCrsPrimaryDualNationalityFlag(),
				vObject.getCrsPrimaryResidenceFlag(), vObject.getCrsPrimaryDomicileFlag(),
				vObject.getCrsFamilyNationalityFlag(), vObject.getCrsFamilyDualNationalityFlag(),
				vObject.getCrsCountryList(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(RgFatcaRulesVb vObject) {
		String query = "Insert Into RG_FATCA_RULES_PEND (COUNTRY, LE_BOOK, FATCA_PRIMARY_NATIONALITY_FLAG, FATCA_PRIMARY_DUAL_NATIONALITY_FLAG,"
				+ " FATCA_PRIMARY_RESIDENCE_FLAG, FATCA_PRIMARY_DOMICILE_FLAG, FATCA_FAMILY_NATIONALITY_FLAG, FATCA_FAMILY_DUAL_NATIONALITY_FLAG, "
				+ "FATCA_COUNTRY_LIST, CRS_PRIMARY_NATIONALITY_FLAG, CRS_PRIMARY_DUAL_NATIONALITY_FLAG, CRS_PRIMARY_RESIDENCE_FLAG, CRS_PRIMARY_DOMICILE_FLAG,"
				+ " CRS_FAMILY_NATIONALITY_FLAG, CRS_FAMILY_DUAL_NATIONALITY_FLAG, CRS_COUNTRY_LIST, RULE_STATUS_NT, RULE_STATUS, RECORD_INDICATOR_NT, "
				+ "RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+systemDate+")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getFatcaPrimaryNationalityFlag(),
				vObject.getFatcaPrimaryDualNationalityFlag(), vObject.getFatcaPrimaryResidenceFlag(),
				vObject.getFatcaPrimaryDomicileFlag(), vObject.getFatcaFamilyNationalityFlag(),
				vObject.getFatcaFamilyDualNationalityFlag(), vObject.getFatcaCountryList(),
				vObject.getCrsPrimaryNationalityFlag(), vObject.getCrsPrimaryDualNationalityFlag(),
				vObject.getCrsPrimaryResidenceFlag(), vObject.getCrsPrimaryDomicileFlag(),
				vObject.getCrsFamilyNationalityFlag(), vObject.getCrsFamilyDualNationalityFlag(),
				vObject.getCrsCountryList(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(RgFatcaRulesVb vObject) {
		String query = "Insert Into RG_FATCA_RULES_PEND (COUNTRY, LE_BOOK, FATCA_PRIMARY_NATIONALITY_FLAG, FATCA_PRIMARY_DUAL_NATIONALITY_FLAG,"
				+ " FATCA_PRIMARY_RESIDENCE_FLAG, FATCA_PRIMARY_DOMICILE_FLAG, FATCA_FAMILY_NATIONALITY_FLAG, FATCA_FAMILY_DUAL_NATIONALITY_FLAG, "
				+ "FATCA_COUNTRY_LIST, CRS_PRIMARY_NATIONALITY_FLAG, CRS_PRIMARY_DUAL_NATIONALITY_FLAG, CRS_PRIMARY_RESIDENCE_FLAG, CRS_PRIMARY_DOMICILE_FLAG,"
				+ " CRS_FAMILY_NATIONALITY_FLAG, CRS_FAMILY_DUAL_NATIONALITY_FLAG, CRS_COUNTRY_LIST, RULE_STATUS_NT, RULE_STATUS, RECORD_INDICATOR_NT, "
				+ "RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+", "+dateTimeConvert+")";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getFatcaPrimaryNationalityFlag(),
				vObject.getFatcaPrimaryDualNationalityFlag(), vObject.getFatcaPrimaryResidenceFlag(),
				vObject.getFatcaPrimaryDomicileFlag(), vObject.getFatcaFamilyNationalityFlag(),
				vObject.getFatcaFamilyDualNationalityFlag(), vObject.getFatcaCountryList(),
				vObject.getCrsPrimaryNationalityFlag(), vObject.getCrsPrimaryDualNationalityFlag(),
				vObject.getCrsPrimaryResidenceFlag(), vObject.getCrsPrimaryDomicileFlag(),
				vObject.getCrsFamilyNationalityFlag(), vObject.getCrsFamilyDualNationalityFlag(),
				vObject.getCrsCountryList(), vObject.getRuleStatusNt(), vObject.getRuleStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getDateCreation() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(RgFatcaRulesVb vObject) {
		String query = "Update RG_FATCA_RULES Set FATCA_PRIMARY_NATIONALITY_FLAG = ?, FATCA_PRIMARY_DUAL_NATIONALITY_FLAG = ?,"
				+ " FATCA_PRIMARY_RESIDENCE_FLAG = ?, FATCA_PRIMARY_DOMICILE_FLAG = ?, FATCA_FAMILY_NATIONALITY_FLAG = ?, "
				+ "FATCA_FAMILY_DUAL_NATIONALITY_FLAG = ?, FATCA_COUNTRY_LIST = ?, CRS_PRIMARY_NATIONALITY_FLAG = ?,"
				+ " CRS_PRIMARY_DUAL_NATIONALITY_FLAG = ?, CRS_PRIMARY_RESIDENCE_FLAG = ?, CRS_PRIMARY_DOMICILE_FLAG = ?, "
				+ "CRS_FAMILY_NATIONALITY_FLAG = ?, CRS_FAMILY_DUAL_NATIONALITY_FLAG = ?, CRS_COUNTRY_LIST = ?, RULE_STATUS_NT = ?,"
				+ " RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " WHERE COUNTRY = ?  AND LE_BOOK = ? ";
		Object[] args = { vObject.getFatcaPrimaryNationalityFlag(), vObject.getFatcaPrimaryDualNationalityFlag(),
				vObject.getFatcaPrimaryResidenceFlag(), vObject.getFatcaPrimaryDomicileFlag(),
				vObject.getFatcaFamilyNationalityFlag(), vObject.getFatcaFamilyDualNationalityFlag(),
				vObject.getFatcaCountryList(), vObject.getCrsPrimaryNationalityFlag(),
				vObject.getCrsPrimaryDualNationalityFlag(), vObject.getCrsPrimaryResidenceFlag(),
				vObject.getCrsPrimaryDomicileFlag(), vObject.getCrsFamilyNationalityFlag(),
				vObject.getCrsFamilyDualNationalityFlag(), vObject.getCrsCountryList(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), vObject.getCountry(),
				vObject.getLeBook() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(RgFatcaRulesVb vObject) {
		String query = "Update RG_FATCA_RULES_PEND Set FATCA_PRIMARY_NATIONALITY_FLAG = ?, FATCA_PRIMARY_DUAL_NATIONALITY_FLAG = ?,"
				+ " FATCA_PRIMARY_RESIDENCE_FLAG = ?, FATCA_PRIMARY_DOMICILE_FLAG = ?, FATCA_FAMILY_NATIONALITY_FLAG = ?, FATCA_FAMILY_DUAL_NATIONALITY_FLAG = ?,"
				+ " FATCA_COUNTRY_LIST = ?, CRS_PRIMARY_NATIONALITY_FLAG = ?, CRS_PRIMARY_DUAL_NATIONALITY_FLAG = ?, CRS_PRIMARY_RESIDENCE_FLAG = ?, "
				+ "CRS_PRIMARY_DOMICILE_FLAG = ?, CRS_FAMILY_NATIONALITY_FLAG = ?, CRS_FAMILY_DUAL_NATIONALITY_FLAG = ?, CRS_COUNTRY_LIST = ?, RULE_STATUS_NT = ?,"
				+ " RULE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " WHERE COUNTRY = ?  AND LE_BOOK = ? ";
		Object[] args = { vObject.getFatcaPrimaryNationalityFlag(), vObject.getFatcaPrimaryDualNationalityFlag(),
				vObject.getFatcaPrimaryResidenceFlag(), vObject.getFatcaPrimaryDomicileFlag(),
				vObject.getFatcaFamilyNationalityFlag(), vObject.getFatcaFamilyDualNationalityFlag(),
				vObject.getFatcaCountryList(), vObject.getCrsPrimaryNationalityFlag(),
				vObject.getCrsPrimaryDualNationalityFlag(), vObject.getCrsPrimaryResidenceFlag(),
				vObject.getCrsPrimaryDomicileFlag(), vObject.getCrsFamilyNationalityFlag(),
				vObject.getCrsFamilyDualNationalityFlag(), vObject.getCrsCountryList(), vObject.getRuleStatusNt(),
				vObject.getRuleStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),vObject.getCountry(),
				vObject.getLeBook() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(RgFatcaRulesVb vObject) {
		String query = "Delete From RG_FATCA_RULES Where COUNTRY = ?  AND LE_BOOK = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(RgFatcaRulesVb vObject) {
		String query = "Delete From RG_FATCA_RULES_PEND Where COUNTRY = ?  AND LE_BOOK = ?  ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(RgFatcaRulesVb vObject) {
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

		if (ValidationUtil.isValid(vObject.getFatcaPrimaryNationalityFlag()))
			strAudit.append("FATCA_PRIMARY_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getFatcaPrimaryNationalityFlag().trim());
		else
			strAudit.append("FATCA_PRIMARY_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getFatcaPrimaryDualNationalityFlag()))
			strAudit.append("FATCA_PRIMARY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getFatcaPrimaryDualNationalityFlag().trim());
		else
			strAudit.append("FATCA_PRIMARY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getFatcaPrimaryResidenceFlag()))
			strAudit.append("FATCA_PRIMARY_RESIDENCE_FLAG" + auditDelimiterColVal
					+ vObject.getFatcaPrimaryResidenceFlag().trim());
		else
			strAudit.append("FATCA_PRIMARY_RESIDENCE_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getFatcaPrimaryDomicileFlag()))
			strAudit.append("FATCA_PRIMARY_DOMICILE_FLAG" + auditDelimiterColVal
					+ vObject.getFatcaPrimaryDomicileFlag().trim());
		else
			strAudit.append("FATCA_PRIMARY_DOMICILE_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getFatcaFamilyNationalityFlag()))
			strAudit.append("FATCA_FAMILY_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getFatcaFamilyNationalityFlag().trim());
		else
			strAudit.append("FATCA_FAMILY_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getFatcaFamilyDualNationalityFlag()))
			strAudit.append("FATCA_FAMILY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getFatcaFamilyDualNationalityFlag().trim());
		else
			strAudit.append("FATCA_FAMILY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getFatcaCountryList()))
			strAudit.append("FATCA_COUNTRY_LIST" + auditDelimiterColVal + vObject.getFatcaCountryList().trim());
		else
			strAudit.append("FATCA_COUNTRY_LIST" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsPrimaryNationalityFlag()))
			strAudit.append("CRS_PRIMARY_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getCrsPrimaryNationalityFlag().trim());
		else
			strAudit.append("CRS_PRIMARY_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsPrimaryDualNationalityFlag()))
			strAudit.append("CRS_PRIMARY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getCrsPrimaryDualNationalityFlag().trim());
		else
			strAudit.append("CRS_PRIMARY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsPrimaryResidenceFlag()))
			strAudit.append(
					"CRS_PRIMARY_RESIDENCE_FLAG" + auditDelimiterColVal + vObject.getCrsPrimaryResidenceFlag().trim());
		else
			strAudit.append("CRS_PRIMARY_RESIDENCE_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsPrimaryDomicileFlag()))
			strAudit.append(
					"CRS_PRIMARY_DOMICILE_FLAG" + auditDelimiterColVal + vObject.getCrsPrimaryDomicileFlag().trim());
		else
			strAudit.append("CRS_PRIMARY_DOMICILE_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsFamilyNationalityFlag()))
			strAudit.append("CRS_FAMILY_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getCrsFamilyNationalityFlag().trim());
		else
			strAudit.append("CRS_FAMILY_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsFamilyDualNationalityFlag()))
			strAudit.append("CRS_FAMILY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal
					+ vObject.getCrsFamilyDualNationalityFlag().trim());
		else
			strAudit.append("CRS_FAMILY_DUAL_NATIONALITY_FLAG" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getCrsCountryList()))
			strAudit.append("CRS_COUNTRY_LIST" + auditDelimiterColVal + vObject.getCrsCountryList().trim());
		else
			strAudit.append("CRS_COUNTRY_LIST" + auditDelimiterColVal + "NULL");
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
		serviceName = "FATCA CRS RULE";
		serviceDesc = "FATCA CRS RULE";
		tableName = "RG_FATCA_RULES";
		childTableName = "RG_FATCA_RULES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();

	}

}
