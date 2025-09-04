package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateMappingVb;

@Component
public class TemplateMappingDao extends AbstractDao<TemplateMappingVb> {
	@Autowired
	CommonDao commonDao;
	@Value("${app.databaseType}")
	private String databaseType;

	protected void setStatus(TemplateMappingVb vObject, int status) {
		vObject.setMappingStatus(status);
	}

	@Override
	protected int getStatus(TemplateMappingVb records) {
		return records.getMappingStatus();
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "TemplateMapping";
		serviceDesc = "Template Mapping";
		tableName = "RG_TEMPLATE_MAPPING";
		childTableName = "RG_TEMPLATE_MAPPING";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	protected int doInsertionAppr(TemplateMappingVb vObject) {
		String query = " INSERT INTO RG_TEMPLATE_MAPPING (COUNTRY,LE_BOOK,TEMPLATE_ID,SEQUENCE_NO,SOURCE_COLUMN,TARGET_COLUMN,TEMPLATE_STATUS_NT "
				+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,AUTO_FILTER)                    "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?," + commonDao.getDbFunction("SYSDATE") + ","
				+ commonDao.getDbFunction("SYSDATE") + ",'N')";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getSequenceNo(),
				vObject.getSourceColumn(), vObject.getTargetColumn(), vObject.getMappingStatusNt(),
				vObject.getMappingStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };

		return getJdbcTemplate().update(query, args);
	}

	protected int doInsertionPend(TemplateMappingVb vObject) {
		String query = " INSERT INTO RG_TEMPLATE_MAPPING_PEND (COUNTRY,LE_BOOK,TEMPLATE_ID,SEQUENCE_NO,SOURCE_COLUMN,TARGET_COLUMN,TEMPLATE_STATUS_NT "
				+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,AUTO_FILTER)                    "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?," + commonDao.getDbFunction("SYSDATE") + ","
				+ commonDao.getDbFunction("SYSDATE") + ",'N')";

		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getSequenceNo(),
				vObject.getSourceColumn(), vObject.getTargetColumn(), vObject.getMappingStatusNt(),
				vObject.getMappingStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier() };

		return getJdbcTemplate().update(query, args);
	}
	
	protected int doInsertionPendWithDc(TemplateMappingVb vObject) {
		String query ="";
		if("ORACLE".equalsIgnoreCase(databaseType)) {
		 query = " INSERT INTO RG_TEMPLATE_MAPPING_PEND (COUNTRY,LE_BOOK,TEMPLATE_ID,SEQUENCE_NO,SOURCE_COLUMN,TARGET_COLUMN,TEMPLATE_STATUS_NT "
				+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,AUTO_FILTER)                    "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?," + commonDao.getDbFunction("SYSDATE") + ",To_Date(?, 'DD-MM-YYYY HH24:MI:SS') ,'N')";
		} else {
		 query = " INSERT INTO RG_TEMPLATE_MAPPING_PEND (COUNTRY,LE_BOOK,TEMPLATE_ID,SEQUENCE_NO,SOURCE_COLUMN,TARGET_COLUMN,TEMPLATE_STATUS_NT "
				+ " ,TEMPLATE_STATUS,RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION,AUTO_FILTER)                    "
				+ " Values (?,?,?,?,?,?,?,?,?,?,?,?," + commonDao.getDbFunction("SYSDATE") + ", CONVERT(datetime, ?, 103),'N')";
		}
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getSequenceNo(),
				vObject.getSourceColumn(), vObject.getTargetColumn(), vObject.getMappingStatusNt(),
				vObject.getMappingStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(),
				vObject.getMaker(), vObject.getVerifier(),vObject.getDateCreation()};

		return getJdbcTemplate().update(query, args);
	}
	protected int doUpdateAppr(TemplateMappingVb vObject) {
		String query = " Update RG_TEMPLATE_MAPPING set SOURCE_COLUMN = ?,TARGET_COLUMN = ?,"
				+ " TEMPLATE_STATUS = ?,RECORD_INDICATOR = ?,MAKER = ?,"
				+ " VERIFIER =?,DATE_LAST_MODIFIED = "+commonDao.getDbFunction("SYSDATE")+" "
				+ " where COUNTRY = ? And LE_BOOK = ? And TEMPLATE_ID= ? And SEQUENCE_NO= ? ";

		Object[] args = {vObject.getSourceColumn(), vObject.getTargetColumn(),
				vObject.getMappingStatus(), vObject.getRecordIndicator(),vObject.getMaker(), vObject.getVerifier(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getSequenceNo()};
		return getJdbcTemplate().update(query, args);
	}
	protected int doUpdatePend(TemplateMappingVb vObject) {
		String query = " Update RG_TEMPLATE_MAPPING_PEND set SOURCE_COLUMN = ?,TARGET_COLUMN = ?,"
				+ " TEMPLATE_STATUS = ?,RECORD_INDICATOR = ?,MAKER = ?,"
				+ " VERIFIER =?,DATE_LAST_MODIFIED = "+commonDao.getDbFunction("SYSDATE")+" "
				+ " where COUNTRY = ? And LE_BOOK = ? And TEMPLATE_ID= ? And SEQUENCE_NO= ? ";

		Object[] args = {vObject.getSourceColumn(), vObject.getTargetColumn(),
				vObject.getMappingStatus(), vObject.getRecordIndicator(),vObject.getMaker(), vObject.getVerifier(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(), vObject.getSequenceNo()};
		return getJdbcTemplate().update(query, args);
	}

	protected int doDeleteAppr(TemplateMappingVb vObject) {
		String query = " DELETE FROM RG_TEMPLATE_MAPPING  WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND SEQUENCE_NO =?   ";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),vObject.getSequenceNo() };
		return getJdbcTemplate().update(query, args);
	}

	protected int deletePendingRecord(TemplateMappingVb vObject) {
		String query = " DELETE FROM RG_TEMPLATE_MAPPING_PEND  WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND SEQUENCE_NO =?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),vObject.getSequenceNo() };
		return getJdbcTemplate().update(query, args);
	}

	public List<TemplateMappingVb> getQueryResults(TemplateConfigVb dObj,int intStatus){
		List<TemplateMappingVb> collTemp = null;
		String query = "";
		String orderBy = " Order by SEQUENCE_NO ";
		try
		{	
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT"+
						" TAPPR.COUNTRY,"+
						"TAPPR.LE_BOOK,"+
						"TAPPR.TEMPLATE_ID,"+
						"TAPPR.SOURCE_COLUMN,"+
						"TAPPR.TARGET_COLUMN,"+
						"TAPPR.TEMPLATE_STATUS,"+
						"TAPPR.RECORD_INDICATOR,"+
						"TAPPR.MAKER,"+
						" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"+
						"TAPPR.VERIFIER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TAPPR.SEQUENCE_NO,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TAPPR.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TAPPR.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING TAPPR"+
						" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND TAPPR.TEMPLATE_ID = ?";
			}else {
				query = " SELECT"+
						" TPEND.COUNTRY,"+
						"TPEND.LE_BOOK,"+
						"TPEND.TEMPLATE_ID,"+
						"TPEND.SOURCE_COLUMN,"+
						"TPEND.TARGET_COLUMN,"+
						"TPEND.TEMPLATE_STATUS,"+
						"TPEND.RECORD_INDICATOR,"+
						"TPEND.MAKER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.MAKER,0) ) MAKER_NAME,"+
						"TPEND.VERIFIER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TPEND.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TPEND.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TPEND.SEQUENCE_NO,"
						+ " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TPEND.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TPEND.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING_PEND TPEND"+
						" WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND TPEND.TEMPLATE_ID = ? ";
			}
			query=query+orderBy;
			Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId() };
			collTemp = getJdbcTemplate().query(query,args,getTemplateMppingMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	
	public List<TemplateMappingVb> getQueryResultsReview(TemplateMappingVb dObj,int intStatus){
		List<TemplateMappingVb> collTemp = null;
		String query = "";
		try
		{	
			if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT"+
						" TAPPR.COUNTRY,"+
						"TAPPR.LE_BOOK,"+
						"TAPPR.TEMPLATE_ID,"+
						"TAPPR.SOURCE_COLUMN,"+
						"TAPPR.TARGET_COLUMN,"+
						"TAPPR.TEMPLATE_STATUS,"+
						"TAPPR.RECORD_INDICATOR,"+
						"TAPPR.MAKER,"+
						" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"+
						"TAPPR.VERIFIER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TAPPR.SEQUENCE_NO,"+
						" (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TAPPR.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TAPPR.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING TAPPR"+
						"  WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND TAPPR.TEMPLATE_ID = ? AND  TAPPR.SEQUENCE_NO = ?  ";
			}else {
				query = " SELECT"+
						" TPEND.COUNTRY,"+
						"TPEND.LE_BOOK,"+
						"TPEND.TEMPLATE_ID,"+
						"TPEND.SOURCE_COLUMN,"+
						"TPEND.TARGET_COLUMN,"+
						"TPEND.TEMPLATE_STATUS,"+
						"TPEND.RECORD_INDICATOR,"+
						"TPEND.MAKER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.MAKER,0) ) MAKER_NAME,"+
						"TPEND.VERIFIER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TPEND.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TPEND.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TPEND.SEQUENCE_NO,"+
						" (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TPEND.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TPEND.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING_PEND TPEND"+
						"  WHERE TPEND.COUNTRY = ? AND TPEND.LE_BOOK = ? AND TPEND.TEMPLATE_ID = ? AND  TPEND.SEQUENCE_NO = ?  ";
			}
			
			Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId(),dObj.getSequenceNo() };
			collTemp = getJdbcTemplate().query(query,args,getTemplateMppingMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected RowMapper getTemplateMppingMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateMappingVb vObject = new TemplateMappingVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setSourceColumn(rs.getString("SOURCE_COLUMN"));
				vObject.setTargetColumn(rs.getString("TARGET_COLUMN"));
				vObject.setMappingStatus(rs.getInt("TEMPLATE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setSequenceNo(rs.getInt("SEQUENCE_NO"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMappingStatusDesc(rs.getString("MAPPING_STATUS_DESC"));
				return vObject;
			}
		};
		return mapper;
	}
	protected List<TemplateMappingVb> selectApprovedRecord(TemplateMappingVb vObject){
		return getQueryResultsReview(vObject, Constants.STATUS_ZERO);
	}
	
	protected List<TemplateMappingVb> doSelectPendingRecord(TemplateMappingVb vObject){
		return getQueryResultsReview(vObject, Constants.STATUS_PENDING);
	}
	
	public ExceptionCode doInsertApprRecordForNonTrans(TemplateMappingVb vObject) throws RuntimeCustomException {
		List<TemplateMappingVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		
		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && collTemp.size() > 0){
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setMappingStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		
		retVal = doInsertionAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, null);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	
	public ExceptionCode doInsertRecordForNonTrans(TemplateMappingVb vObject) throws RuntimeCustomException {
		List<TemplateMappingVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp != null && collTemp.size() > 0){
			logger.error("!!");
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_INSERT);
		vObject.setMappingStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionPend(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	
	public ExceptionCode doUpdateApprRecordForNonTrans(TemplateMappingVb vObject) throws RuntimeCustomException  {
		List<TemplateMappingVb> collTemp = null;
		TemplateMappingVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		
		vObject.setMaker(getIntCurrentUserId());
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		
		if (collTemp.size() == 0){
			retVal = doInsertionAppr(vObject);
		}else {
			vObjectlocal = ((ArrayList<TemplateMappingVb>)collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			retVal = doUpdateAppr(vObject);
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	
	public ExceptionCode doUpdateRecordForNonTrans(TemplateMappingVb vObject) throws RuntimeCustomException  {
		List<TemplateMappingVb> collTemp = null;
		TemplateMappingVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0){
			vObjectlocal = ((ArrayList<TemplateMappingVb>)collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePend(vObject);
			}else{
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePend(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}else {
			collTemp = null;
			collTemp = selectApprovedRecord(vObject);

			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			// Even if record is not there in Appr. table reject the record
			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			//This is required for Audit Trail.
			if (collTemp.size() > 0){
				vObjectlocal = ((ArrayList<TemplateMappingVb>)collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    retVal = doInsertionPendWithDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}
	protected ExceptionCode doDeleteApprRecordForNonTrans(TemplateMappingVb vObject) throws RuntimeCustomException {
		List<TemplateMappingVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		TemplateMappingVb vObjectlocal = null;
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			int intStaticDeletionFlag = getStatus(((ArrayList<TemplateMappingVb>)collTemp).get(0));
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<TemplateMappingVb>)collTemp).get(0);
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		if(vObject.isChecked()) {
			if(vObject.isStaticDelete()){
				vObjectlocal.setMaker(getIntCurrentUserId());
				vObject.setVerifier(getIntCurrentUserId());
				vObject.setRecordIndicator(Constants.STATUS_ZERO);
//				setStatus(vObject, Constants.PASSIVATE);
				setStatus(vObjectlocal, Constants.PASSIVATE);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				retVal = doUpdateAppr(vObjectlocal);
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
			}else{
				//delete the record from the Approve Table
				retVal = doDeleteAppr(vObject);
//				vObject.setRecordIndicator(-1);
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);

			}
		}
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if(vObject.isStaticDelete()){
			setStatus(vObjectlocal, Constants.STATUS_ZERO);
			setStatus(vObject, Constants.PASSIVATE);
			exceptionCode = writeAuditLog(vObject,vObjectlocal);
		}else{
			exceptionCode = writeAuditLog(null,vObject);
			vObject.setRecordIndicator(-1);
		}
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	
	protected ExceptionCode doDeleteRecordForNonTrans(TemplateMappingVb vObject) throws RuntimeCustomException {
		TemplateMappingVb vObjectlocal = null;
		List<TemplateMappingVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation = Constants.DELETE;
		strErrorDesc  = "";
		strCurrentOperation = Constants.DELETE;
		setServiceDefaults();
		collTemp = selectApprovedRecord(vObject);
	
	
		if (collTemp == null){
			logger.error("Collection is null");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			vObjectlocal = ((ArrayList<TemplateMappingVb>)collTemp).get(0);
			int intStaticDeletionFlag = getStatus(vObjectlocal);
			if (intStaticDeletionFlag == Constants.PASSIVATE){
				exceptionCode = getResultObject(Constants.CANNOT_DELETE_AN_INACTIVE_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		else{
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// check to see if the record already exists in the pending table
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// If records are there, check for the status and decide what error to return back
		if (collTemp.size() > 0){
			exceptionCode = getResultObject(Constants.TRYING_TO_DELETE_APPROVAL_PENDING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}

		// insert the record into pending table with status 3 - deletion
		if(vObjectlocal==null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0){
		    vObjectlocal=((ArrayList<TemplateMappingVb>)collTemp).get(0);
		    vObjectlocal.setDateCreation(vObject.getDateCreation());
		}
		//vObjectlocal.setDateCreation(vObject.getDateCreation());
		vObjectlocal.setMaker(getIntCurrentUserId());
		vObjectlocal.setMappingStatus(Constants.PASSIVATE);
		vObjectlocal.setRecordIndicator(Constants.STATUS_DELETE);
		vObjectlocal.setVerifier(0);
		retVal = doInsertionPendWithDc(vObjectlocal);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setMappingStatus(Constants.STATUS_DELETE);
		vObject.setVerifier(0);
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	
	@Override
	protected String getAuditString(TemplateMappingVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(vObject.getLeBook()))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+vObject.getLeBook().trim());
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTemplateId()))
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+vObject.getTemplateId().trim());
			else
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getSourceColumn()))
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+vObject.getSourceColumn().trim());
			else
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
						
			strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
			strAudit.append(auditDelimiter);
			
			if(vObject.getRecordIndicator() == -1)
				vObject.setRecordIndicator(0);
			strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
			strAudit.append(auditDelimiter);
			
			strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
			strAudit.append(auditDelimiter);
			
			strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateLastModified() != null && !vObject.getDateLastModified().equalsIgnoreCase(""))
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
			else
				strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(vObject.getDateCreation() != null && !vObject.getDateCreation().equalsIgnoreCase(""))
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
			else
				strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	public List<TemplateMappingVb> getQueryPopupResults(TemplateMappingVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		strBufApprove = new StringBuffer("SELECT"+
						" TAPPR.COUNTRY,"+
						"TAPPR.LE_BOOK,"+
						"TAPPR.TEMPLATE_ID,"+
						"TAPPR.SOURCE_COLUMN,"+
						"TAPPR.TARGET_COLUMN,"+
						"TAPPR.TEMPLATE_STATUS,"+
						"TAPPR.RECORD_INDICATOR,"+
						"TAPPR.MAKER,"+
						" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"+
						"TAPPR.VERIFIER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TAPPR.SEQUENCE_NO,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TAPPR.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TAPPR.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING TAPPR");

		strWhereNotExists = new String(
				" NOT EXISTS (SELECT 'X' FROM RG_TEMPLATE_MAPPING_PEND TPEND WHERE TAPPR.COUNTRY = TPEND.COUNTRY "
						+ " AND TAPPR.LE_BOOK = TPEND.LE_BOOK"
						+ " AND TAPPR.TEMPLATE_ID =TPEND.TEMPLATE_ID and TAPPR.SEQUENCE_NO = TPEND.SEQUENCE_NO)");

		strBufPending = new StringBuffer(" SELECT"+
						" TPEND.COUNTRY,"+
						"TPEND.LE_BOOK,"+
						"TPEND.TEMPLATE_ID,"+
						"TPEND.SOURCE_COLUMN,"+
						"TPEND.TARGET_COLUMN,"+ 
						"TPEND.TEMPLATE_STATUS,"+
						"TPEND.RECORD_INDICATOR,"+
						"TPEND.MAKER,"+
						" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.MAKER,0) ) MAKER_NAME,"+
						"TPEND.VERIFIER,"+
						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TPEND.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TPEND.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TPEND.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TPEND.SEQUENCE_NO,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TPEND.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TPEND.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING_PEND TPEND ");

		try {
			if (ValidationUtil.isValid(dObj.getCountry())) {
				params.addElement(dObj.getCountry());
				CommonUtils.addToQuery("TAppr.COUNTRY = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.COUNTRY = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				params.addElement(dObj.getLeBook());
				CommonUtils.addToQuery("TAppr.LE_BOOK = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.LE_BOOK = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getTemplateId())) {
				params.addElement(dObj.getTemplateId());
				CommonUtils.addToQuery("TAppr.TEMPLATE_ID = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.TEMPLATE_ID = ?", strBufPending);
			}
			orderBy = " Order by SEQUENCE_NO ";
			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params,getTemplateMppingMapper());
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
	public String jsonFormation(TemplateConfigVb dObj) {
		String jsonData="";
		List<TemplateMappingVb> collTemp= new ArrayList<>();
		if(collTemp != null && collTemp.size() > 0) {
			ObjectMapper objectMapper = new ObjectMapper();
			/*
			 * collTemp.forEach(mapping ->{ try { String data =
			 * objectMapper.writeValueAsString(mapping); } catch (JsonProcessingException e)
			 * { e.printStackTrace(); } });
			 */
			for(TemplateMappingVb templateMappingVb:collTemp) {
				 try {
					 jsonData=objectMapper.writeValueAsString(templateMappingVb);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonData;
	}
}
