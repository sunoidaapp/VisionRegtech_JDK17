package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BusinessLineGLVb;
import com.vision.vb.BusinessLineHeaderVb;

@Component
public class BusinessLineConfigGLDao extends AbstractDao<BusinessLineGLVb> {
	
	protected RowMapper getBusinessLineGLMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessLineGLVb vObject = new BusinessLineGLVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setPlGl(rs.getString("PL_GL"));
				vObject.setPlGlDesc(rs.getString("GL_DESCRIPTION"));
				vObject.setbAcid(rs.getString("BACID"));
				vObject.setbAcidDesc(rs.getString("ACCOUNT_NAME"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "BusinessLineGlConfig";
		serviceDesc = "Business Line GL Config";
		tableName = "RA_MST_BUSINESS_LINE_GL";
		childTableName = "RA_MST_BUSINESS_LINE_GL";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	public List<BusinessLineGLVb> getBusinessGLDetails(BusinessLineHeaderVb vObject,int intStatus){
		List<BusinessLineGLVb> collTemp = null;
		String query = "";
		try
		{	
			if(!vObject.isVerificationRequired() || vObject.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT T1.COUNTRY,T1.LE_BOOK,T1.BUSINESS_LINE_ID,T1.PL_GL,T2.GL_DESCRIPTION,T1.BACID,T3.ACCOUNT_NAME " + 
						" FROM RA_MST_BUSINESS_LINE_GL T1,GL_CODES T2,ACCOUNTS_DLY T3 " + 
						" WHERE T2.VISION_GL = T1.PL_GL " + 
						" AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK " + 
						" AND T1.BACID = T3.ACCOUNT_NO AND T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.BUSINESS_LINE_ID = ? ";
			}else {
				query = " SELECT T1.COUNTRY,T1.LE_BOOK,T1.BUSINESS_LINE_ID,T1.PL_GL,T2.GL_DESCRIPTION,T1.BACID,T3.ACCOUNT_NAME " + 
						" FROM RA_MST_BUSINESS_LINE_GL_PEND T1,GL_CODES T2,ACCOUNTS_DLY T3 " + 
						" WHERE T2.VISION_GL = T1.PL_GL " + 
						" AND T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK = T2.LE_BOOK " + 
						" AND T1.BACID = T3.ACCOUNT_NO AND T1.COUNTRY = ? AND T1.LE_BOOK = ? AND T1.BUSINESS_LINE_ID = ? ";
			}
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(vObject.getCountry());// country
			objParams[1] = new String(vObject.getLeBook());
			objParams[2] = new String(vObject.getBusinessLineId());
			collTemp = getJdbcTemplate().query(query,objParams,getBusinessLineGLMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int deleteBusinessLineGlAppr(BusinessLineHeaderVb vObject){
		String query = "Delete from RA_MST_BUSINESS_LINE_GL where COUNTRY = ? AND LE_BOOK = ? AND BUSINESS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteBusinessLineGlPend(BusinessLineHeaderVb vObject){
		String query = "Delete from RA_MST_BUSINESS_LINE_GL_PEND where COUNTRY = ? AND LE_BOOK = ? AND BUSINESS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprBusinessLineGL(BusinessLineGLVb vObject){
		String query =  " Insert Into RA_MST_BUSINESS_LINE_GL(COUNTRY,LE_BOOK, BUSINESS_LINE_ID,PL_GL ,BACID) "+
				" Values (?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getPlGl(),vObject.getbAcid()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendBusinessLineGL(BusinessLineGLVb vObject){
		String query =   "Insert Into RA_MST_BUSINESS_LINE_GL_PEND(COUNTRY,LE_BOOK, BUSINESS_LINE_ID,PL_GL ,BACID) "+
				" Values (?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),
				vObject.getPlGl(),vObject.getbAcid()};
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode deleteAndInsertApprGl(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		ArrayList<BusinessLineGLVb> BusinessLineGllst = (ArrayList<BusinessLineGLVb>)vObject.getBusinessLineGllst();
		List<BusinessLineGLVb> collTemp = null;
		collTemp = getBusinessGLDetails(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteBusinessLineGlAppr(vObject);
		}
		for(BusinessLineGLVb vObjDetailVb : BusinessLineGllst) {
			vObjDetailVb.setCountry(vObject.getCountry());
			vObjDetailVb.setLeBook(vObject.getLeBook());
			vObjDetailVb.setBusinessLineId(vObject.getBusinessLineId());
			vObjDetailVb.setRecordIndicator(Constants.STATUS_ZERO);
			vObjDetailVb.setMaker(intCurrentUserId);
			vObjDetailVb.setVerifier(intCurrentUserId);
			
			retVal = doInsertionApprBusinessLineGL(vObjDetailVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendGl(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		ArrayList<BusinessLineGLVb> transLineGllst = (ArrayList<BusinessLineGLVb>)vObject.getBusinessLineGllst();
		List<BusinessLineGLVb> collTemp = null;
		collTemp = getBusinessGLDetails(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteBusinessLineGlPend(vObject);
		}
		for(BusinessLineGLVb vObjDetailVb : transLineGllst) {
			vObjDetailVb.setCountry(vObject.getCountry());
			vObjDetailVb.setLeBook(vObject.getLeBook());
			vObjDetailVb.setBusinessLineId(vObject.getBusinessLineId());
			vObjDetailVb.setRecordIndicator(Constants.STATUS_ZERO);
			vObjDetailVb.setMaker(intCurrentUserId);
			vObjDetailVb.setVerifier(intCurrentUserId);
			
			retVal = doInsertionPendBusinessLineGL(vObjDetailVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	@Override
	protected String getAuditString(BusinessLineGLVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("PL_GL"+auditDelimiterColVal+vObject.getPlGl().trim());
			else
				strAudit.append("PL_GL"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("BACID"+auditDelimiterColVal+vObject.getbAcid().trim());
			else
				strAudit.append("BACID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
		}catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}

	}