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
import org.springframework.transaction.annotation.Transactional;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CbRefDetailsVb;
import com.vision.vb.CbRefHeaderVb;
import com.vision.vb.SmartSearchVb;

@Component
public class CbRefHeaderDao extends AbstractDao<CbRefHeaderVb> {

	@Autowired
	private CbRefDetailsDao cbRefDetailsDao;
	/******* Mapper Start **********/
	String LeBookApprDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TAppr.LE_BOOK AND COUNTRY = TAppr.COUNTRY ) LE_BOOK_DESC";
	String LeBookTPendDesc = "(SELECT  LEB_DESCRIPTION FROM LE_BOOK WHERE  LE_BOOK = TPend.LE_BOOK AND COUNTRY = TPend.COUNTRY ) LE_BOOK_DESC";

	String StatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.REF_STATUS",
			"REF_STATUS_DESC");
	String StatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.REF_STATUS",
			"REF_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CbRefHeaderVb vObject = new CbRefHeaderVb();
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
//				if (rs.getString("LE_BOOK_DESC") != null) {
//					vObject.setLegalEntity(rs.getString("LE_BOOK_DESC"));
//				}
				if (rs.getString("REF_CODE") != null) {
					vObject.setRefCode(rs.getString("REF_CODE"));
				} else {
					vObject.setRefCode("");
				}
				if (rs.getString("REF_DESCRIPTION") != null) {
					vObject.setRefDescription(rs.getString("REF_DESCRIPTION"));
				} else {
					vObject.setRefDescription("");
				}
				vObject.setRefStatusNt(rs.getInt("REF_STATUS_NT"));
				vObject.setRefStatus(rs.getInt("REF_STATUS"));
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

				vObject.setStatusDesc(rs.getString("REF_STATUS_DESC"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));

				if (ValidationUtil.isValid(rs.getString("MAKER_NAME")))
					vObject.setMakerName(rs.getString("MAKER_NAME"));
				if (ValidationUtil.isValid(rs.getString("VERIFIER_NAME")))
					vObject.setVerifierName(rs.getString("VERIFIER_NAME"));

				return vObject;
			}
		};
		return mapper;
	}

	/******* Mapper End **********/

	public List<CbRefHeaderVb> getQueryPopupResults(CbRefHeaderVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (Select TAppr.COUNTRY" + ",TAppr.LE_BOOK, "
				+ LeBookApprDesc + ",TAppr.REF_CODE" + ",TAppr.REF_DESCRIPTION" + ",TAppr.REF_STATUS_NT"
				+ ",TAppr.REF_STATUS, " + StatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR, "
				+ RecordIndicatorNtApprDesc + ",TAppr.MAKER, " + makerApprDesc + ",TAppr.VERIFIER, " + verifierApprDesc
				+ ",TAppr.INTERNAL_STATUS" + ", " + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " from CB_REF_HEADER TAppr) TAppr ");
		String strWhereNotExists = new String(
				" Not Exists (Select 'X' From CB_REF_HEADER_PEND TPend WHERE TAppr.COUNTRY = TPend.COUNTRY AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.REF_CODE = TPend.REF_CODE )");
		StringBuffer strBufPending = new StringBuffer("SELECT * FROM (Select TPend.COUNTRY" + ",TPend.LE_BOOK, "
				+ LeBookTPendDesc + ",TPend.REF_CODE" + ",TPend.REF_DESCRIPTION" + ",TPend.REF_STATUS_NT"
				+ ",TPend.REF_STATUS, " + StatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR, "
				+ RecordIndicatorNtPendDesc + ",TPend.MAKER, " + makerPendDesc + ",TPend.VERIFIER, " + verifierPendDesc
				+ ",TPend.INTERNAL_STATUS" + ", " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION" + " from CB_REF_HEADER_PEND TPend) TPend ");
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

					case "refCode":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_CODE) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_CODE) " + val, strBufPending,
								data.getJoinType());
						break;

					case "refDescription":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_DESCRIPTION) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_DESCRIPTION) " + val, strBufPending,
								data.getJoinType());
						break;

					case "refStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.REF_STATUS_DESC) " + val, strBufApprove,
								data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.REF_STATUS_DESC) " + val, strBufPending,
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
				CommonUtils.addToQuery(" COUNTRY IN ('" + dObj.getCountry() + "') ", strBufApprove);
				CommonUtils.addToQuery(" COUNTRY IN ('" + dObj.getCountry() + "') ", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				String CalLeBook = removeDescLeBook(dObj.getLeBook());
				CommonUtils.addToQuery(" LE_BOOK IN ('" +CalLeBook + "') ", strBufApprove);
				CommonUtils.addToQuery(" LE_BOOK IN ('" + CalLeBook + "') ", strBufPending);
			}
			String orderBy = " Order By COUNTRY, LE_BOOK, REF_CODE ";
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

	public List<CbRefHeaderVb> getQueryResults(CbRefHeaderVb dObj, int intStatus) {
		setServiceDefaults();
		List<CbRefHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = new String("Select TAppr.COUNTRY" + ",TAppr.LE_BOOK, " + LeBookApprDesc
				+ ",TAppr.REF_CODE" + ",TAppr.REF_DESCRIPTION" + ",TAppr.REF_STATUS_NT" + ",TAppr.REF_STATUS, "
				+ StatusNtApprDesc + ",TAppr.RECORD_INDICATOR_NT" + ",TAppr.RECORD_INDICATOR, "
				+ RecordIndicatorNtApprDesc + ",TAppr.MAKER, " + makerApprDesc + ",TAppr.VERIFIER, " + verifierApprDesc
				+ ",TAppr.INTERNAL_STATUS" + ", " + dateFormat + "(TAppr.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TAppr.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION"
				+ " From CB_REF_HEADER TAppr WHERE COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ?  ");
		String strQueryPend = new String("Select TPend.COUNTRY" + ",TPend.LE_BOOK, " + LeBookTPendDesc
				+ ",TPend.REF_CODE" + ",TPend.REF_DESCRIPTION" + ",TPend.REF_STATUS_NT" + ",TPend.REF_STATUS, "
				+ StatusNtPendDesc + ",TPend.RECORD_INDICATOR_NT" + ",TPend.RECORD_INDICATOR, "
				+ RecordIndicatorNtPendDesc + ",TPend.MAKER, " + makerPendDesc + ",TPend.VERIFIER, " + verifierPendDesc
				+ ",TPend.INTERNAL_STATUS" + ", " + dateFormat + "(TPend.DATE_LAST_MODIFIED, " + dateFormatStr
				+ ") DATE_LAST_MODIFIED" + ", " + dateFormat + "(TPend.DATE_CREATION, " + dateFormatStr
				+ ") DATE_CREATION"
				+ " From CB_REF_HEADER_PEND TPend WHERE COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ?   ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = CalLeBook;
		objParams[2] = dObj.getRefCode();

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
	protected List<CbRefHeaderVb> selectApprovedRecord(CbRefHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<CbRefHeaderVb> doSelectPendingRecord(CbRefHeaderVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(CbRefHeaderVb records) {
		return records.getRefStatus();
	}

	@Override
	protected void setStatus(CbRefHeaderVb vObject, int status) {
		vObject.setRefStatus(status);
	}

	@Override
	protected int doInsertionAppr(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into CB_REF_HEADER (COUNTRY, LE_BOOK, REF_CODE, REF_DESCRIPTION, REF_STATUS_NT, REF_STATUS, "
				+ "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(),CalLeBook, vObject.getRefCode(), vObject.getRefDescription(),
				vObject.getRefStatusNt(), vObject.getRefStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPend(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into CB_REF_HEADER_PEND (COUNTRY, LE_BOOK, REF_CODE, REF_DESCRIPTION, REF_STATUS_NT, REF_STATUS, "
				+ "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + systemDate + ")";
		Object[] args = { vObject.getCountry(), CalLeBook, vObject.getRefCode(), vObject.getRefDescription(),
				vObject.getRefStatusNt(), vObject.getRefStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus() };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doInsertionPendWithDc(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Insert Into CB_REF_HEADER_PEND (COUNTRY, LE_BOOK, REF_CODE, REF_DESCRIPTION, REF_STATUS_NT, "
				+ "REF_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
				+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + systemDate + ", " + dateTimeConvert + ")";
		Object[] args = { vObject.getCountry(), CalLeBook, vObject.getRefCode(), vObject.getRefDescription(),
				vObject.getRefStatusNt(), vObject.getRefStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getDateCreation() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdateAppr(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update CB_REF_HEADER Set REF_DESCRIPTION = ?, REF_STATUS_NT = ?, REF_STATUS = ?, "
				+ "RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "
				+ systemDate + " WHERE COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ? ";
		Object[] args = { vObject.getRefDescription(), vObject.getRefStatusNt(), vObject.getRefStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getCountry(),CalLeBook, vObject.getRefCode(), };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doUpdatePend(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Update CB_REF_HEADER_PEND Set REF_DESCRIPTION = ?, REF_STATUS_NT = ?, REF_STATUS = ?, "
				+ "RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED =  "
				+ systemDate + " WHERE COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ? ";
		Object[] args = { vObject.getRefDescription(), vObject.getRefStatusNt(), vObject.getRefStatus(),
				vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(),
				vObject.getInternalStatus(), vObject.getCountry(), CalLeBook, vObject.getRefCode(), };

		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int doDeleteAppr(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From CB_REF_HEADER Where COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ?  ";
		Object[] args = { vObject.getCountry(), CalLeBook, vObject.getRefCode() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected int deletePendingRecord(CbRefHeaderVb vObject) {
		String CalLeBook = removeDescLeBook(vObject.getLeBook());
		String query = "Delete From CB_REF_HEADER_PEND Where COUNTRY = ?  AND LE_BOOK = ?  AND REF_CODE = ?  ";
		Object[] args = { vObject.getCountry(), CalLeBook, vObject.getRefCode() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected String getAuditString(CbRefHeaderVb vObject) {
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

		if (ValidationUtil.isValid(vObject.getRefCode()))
			strAudit.append("REF_CODE" + auditDelimiterColVal + vObject.getRefCode().trim());
		else
			strAudit.append("REF_CODE" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		if (ValidationUtil.isValid(vObject.getRefDescription()))
			strAudit.append("REF_DESCRIPTION" + auditDelimiterColVal + vObject.getRefDescription().trim());
		else
			strAudit.append("REF_DESCRIPTION" + auditDelimiterColVal + "NULL");
		strAudit.append(auditDelimiter);

		strAudit.append("REF_STATUS_NT" + auditDelimiterColVal + vObject.getRefStatusNt());
		strAudit.append(auditDelimiter);

		strAudit.append("REF_STATUS" + auditDelimiterColVal + vObject.getRefStatus());
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
		serviceName = "Cbrefheader";
		serviceDesc = "CB Ref Header";
		tableName = "CB_REF_HEADER";
		childTableName = "CB_REF_HEADER";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();

	}

	protected ExceptionCode doInsertApprRecordForNonTrans(CbRefHeaderVb vObject) throws RuntimeCustomException {
		List<CbRefHeaderVb> collTemp = null;
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
			int intStaticDeletionFlag = getStatus(((ArrayList<CbRefHeaderVb>) collTemp).get(0));
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

		if (vObject.getCbRefDetailsList() != null && !vObject.getCbRefDetailsList().isEmpty()) {
			for (CbRefDetailsVb cbRefDetailsVb : vObject.getCbRefDetailsList()) {
				cbRefDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				cbRefDetailsVb.setStaticDelete(vObject.isStaticDelete());
//				exceptionCode = cbRefDetailsDao.doUpdateApprRecordForNonTrans(cbRefDetailsVb);
				if (cbRefDetailsVb.isNewRecord()) {
					strCurrentOperation = Constants.ADD;
					exceptionCode = cbRefDetailsDao.doInsertApprRecordForNonTrans(cbRefDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
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

	protected ExceptionCode doInsertRecordForNonTrans(CbRefHeaderVb vObject) throws RuntimeCustomException {
		List<CbRefHeaderVb> collTemp = null;
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
			int staticDeletionFlag = getStatus(((ArrayList<CbRefHeaderVb>) collTemp).get(0));
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
			CbRefHeaderVb vObjectLocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
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
			if (vObject.getCbRefDetailsList() != null && !vObject.getCbRefDetailsList().isEmpty()) {
				for (CbRefDetailsVb cbRefDetailsVb : vObject.getCbRefDetailsList()) {
					cbRefDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
					cbRefDetailsVb.setStaticDelete(vObject.isStaticDelete());
//					exceptionCode = cbRefDetailsDao.doUpdateApprRecordForNonTrans(cbRefDetailsVb);
					if (cbRefDetailsVb.isNewRecord()) {
						strCurrentOperation = Constants.ADD;
						exceptionCode = cbRefDetailsDao.doInsertRecordForNonTrans(cbRefDetailsVb);
						if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
			}
			return exceptionCode;
		}
	}

	protected ExceptionCode doUpdateApprRecordForNonTrans(CbRefHeaderVb vObject) throws RuntimeCustomException {
		List<CbRefHeaderVb> collTemp = null;
		CbRefHeaderVb vObjectlocal = null;
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
		/*
		 * collTemp = selectApprovedRecord(vObject); if (collTemp == null){
		 * exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION); throw
		 * buildRuntimeCustomException(exceptionCode); }
		 */ collTemp = getQueryResults(vObject, 0);
		if (collTemp == null || collTemp.size() == 0) {
			/*
			 * exceptionCode =
			 * getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD); throw
			 * buildRuntimeCustomException(exceptionCode);
			 */
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			vObject.setVerifier(getIntCurrentUserId());
			retVal = doInsertionAppr(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}

		} else {
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			vObject.setVerifier(getIntCurrentUserId());
			vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());
//			retVal = doUpdateDefaultFlag(vObject);
			retVal = doUpdateAppr(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
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
		if (vObject.getCbRefDetailsList() != null && !vObject.getCbRefDetailsList().isEmpty()) {
			for (CbRefDetailsVb cbRefDetailsVb : vObject.getCbRefDetailsList()) {
				cbRefDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				cbRefDetailsVb.setStaticDelete(vObject.isStaticDelete());
//			exceptionCode = cbRefDetailsDao.doUpdateApprRecordForNonTrans(cbRefDetailsVb);
				if (cbRefDetailsVb.isNewRecord()) {
					strCurrentOperation = Constants.ADD;
					exceptionCode = cbRefDetailsDao.doInsertApprRecordForNonTrans(cbRefDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				} else {
					strCurrentOperation = Constants.MODIFY;
					exceptionCode = cbRefDetailsDao.doUpdateApprRecordForNonTrans(cbRefDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}

	protected ExceptionCode doUpdateRecordForNonTrans(CbRefHeaderVb vObject) throws RuntimeCustomException {
		List<CbRefHeaderVb> collTemp = null;
		CbRefHeaderVb vObjectlocal = null;
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
			vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);

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
//				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
//				throw buildRuntimeCustomException(exceptionCode);
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
			} else {
				// This is required for Audit Trail.
				if (collTemp.size() > 0) {
					vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
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
		}
		if (vObject.getCbRefDetailsList() != null && !vObject.getCbRefDetailsList().isEmpty()) {
			for (CbRefDetailsVb cbRefDetailsVb : vObject.getCbRefDetailsList()) {
				cbRefDetailsVb.setVerificationRequired(vObject.isVerificationRequired());
				cbRefDetailsVb.setStaticDelete(vObject.isStaticDelete());
//			exceptionCode = cbRefDetailsDao.doUpdateApprRecordForNonTrans(cbRefDetailsVb);
				if (cbRefDetailsVb.isNewRecord()) {
					strCurrentOperation = Constants.ADD;
					exceptionCode = cbRefDetailsDao.doInsertRecordForNonTrans(cbRefDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				} else {
					strCurrentOperation = Constants.MODIFY;
					exceptionCode = cbRefDetailsDao.doUpdateRecordForNonTrans(cbRefDetailsVb);
					if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	protected ExceptionCode doDeleteApprRecordForNonTrans(CbRefHeaderVb vObject) throws RuntimeCustomException {
		List<CbRefHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		CbRefHeaderVb vObjectlocal = null;
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
			int intStaticDeletionFlag = getStatus(((ArrayList<CbRefHeaderVb>) collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE) {
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		} else {
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
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
		List<CbRefDetailsVb> cbRefDetailsList = cbRefDetailsDao.getQueryResultsByParent(vObject, 0);
		if (cbRefDetailsList != null && !cbRefDetailsList.isEmpty()) {
			for (CbRefDetailsVb detailsVb : cbRefDetailsList) {
				detailsVb.setVerificationRequired(vObject.isVerificationRequired());
				detailsVb.setStaticDelete(vObject.isStaticDelete());
				exceptionCode = cbRefDetailsDao.doDeleteApprRecordForNonTrans(detailsVb);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					strErrorDesc = exceptionCode.getErrorMsg();
					exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}

		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	protected ExceptionCode doDeleteRecordForNonTrans(CbRefHeaderVb vObject) throws RuntimeCustomException {
		CbRefHeaderVb vObjectlocal = null;
		List<CbRefHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
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
			vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
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
		vObject.setRecordIndicator(Constants.STATUS_DELETE);
		vObject.setVerifier(0);

		List<CbRefDetailsVb> cbRefDetailsList = cbRefDetailsDao.getQueryResultsByParent(vObject, 0);
		if (cbRefDetailsList != null && !cbRefDetailsList.isEmpty()) {
			for (CbRefDetailsVb detailsVb : cbRefDetailsList) {
				detailsVb.setVerificationRequired(vObject.isVerificationRequired());
				detailsVb.setStaticDelete(vObject.isStaticDelete());
				exceptionCode = cbRefDetailsDao.doDeleteRecordForNonTrans(detailsVb);
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					strErrorDesc = exceptionCode.getErrorMsg();
					exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
					throw buildRuntimeCustomException(exceptionCode);
				}
			}
		}

		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}

	public ExceptionCode doApproveRecord(CbRefHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		CbRefHeaderVb oldContents = null;
		CbRefHeaderVb vObjectlocal = null;
		List<CbRefHeaderVb> collTemp = null;
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

			vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);

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
				oldContents = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
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

			List<CbRefDetailsVb> cbRefDetailsList = cbRefDetailsDao.getQueryResultsByParent(vObject, 1);
			if (cbRefDetailsList != null && !cbRefDetailsList.isEmpty()) {
				for (CbRefDetailsVb detailsVb : cbRefDetailsList) {
					detailsVb.setVerificationRequired(vObject.isVerificationRequired());
					detailsVb.setStaticDelete(vObject.isStaticDelete());
					exceptionCode = cbRefDetailsDao.doApproveForTransaction(detailsVb, staticDelete);
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

	public ExceptionCode doRejectRecord(CbRefHeaderVb vObject) throws RuntimeCustomException {
		CbRefHeaderVb vObjectlocal = null;
		List<CbRefHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<CbRefHeaderVb>) collTemp).get(0);
			// Delete the record from the Pending table
			if (deletePendingRecord(vObjectlocal) == 0) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			List<CbRefDetailsVb> cbRefDetailsList = cbRefDetailsDao.getQueryResultsByParent(vObject, 1);
			if (cbRefDetailsList != null && !cbRefDetailsList.isEmpty()) {
				for (CbRefDetailsVb detailsVb : cbRefDetailsList) {
					detailsVb.setVerificationRequired(vObject.isVerificationRequired());
					detailsVb.setStaticDelete(vObject.isStaticDelete());
					exceptionCode = cbRefDetailsDao.doRejectForTransaction(detailsVb);
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
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertRecord(List<CbRefHeaderVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		try {
				for(CbRefHeaderVb vObject : vObjects){
					if(vObject.isNewRecord()){
						exceptionCode = doInsertRecordForNonTrans(vObject);
						if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
							throw buildRuntimeCustomException(exceptionCode);
						}
					}else {
						exceptionCode = doUpdateRecordForNonTrans(vObject);
						if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
				return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertApprRecord(List<CbRefHeaderVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		try {
				for(CbRefHeaderVb vObject : vObjects){
					if(vObject.isNewRecord()){
						exceptionCode = doInsertApprRecordForNonTrans(vObject);
						if(exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION ){
							throw buildRuntimeCustomException(exceptionCode);
						}
					}else {
						exceptionCode = doUpdateApprRecordForNonTrans(vObject);
						if (exceptionCode == null || exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							throw buildRuntimeCustomException(exceptionCode);
						}
					}
				}
				return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
}
