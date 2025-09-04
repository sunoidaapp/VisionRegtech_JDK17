package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.FeesConfigTierVb;

@Component
public class FeesConfigTierDao extends AbstractDao<FeesConfigTierVb> {
	@Autowired
	CommonDao commonDao;
	
	@Value("${app.databaseType}")
	private String databaseType;
	protected RowMapper getFeesConfigTierMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigTierVb vObject = new FeesConfigTierVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setFeeConfigType(rs.getString("FEE_CONFIG_TYPE"));
				vObject.setFeeConfigCode(rs.getString("FEE_CONFIG_CODE"));
				vObject.setContractId(rs.getString("CONTRACT_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setProductId(rs.getString("PRODUCT_ID"));
				vObject.setTranCcy(rs.getString("TRAN_CCY"));
				vObject.setFeeSequence(rs.getInt("FEE_SEQUENCE"));
				vObject.setFromValue(rs.getString("FROM_VALUE"));
				vObject.setToValue(rs.getString("TO_VALUE"));
				vObject.setFeeAmt(rs.getString("FEE_AMT"));
				vObject.setFeePercentage(rs.getString("FEE_PERCENTAGE"));
				vObject.setMinFee(rs.getString("MIN_FEE"));
				vObject.setMaxFee(rs.getString("MAX_FEE"));
				vObject.setChannel(rs.getString("CHANNEL_ID"));
				return vObject;
			}
		};
		return mapper;
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "FeesConfigTier";
		serviceDesc = "Fees Config Tier";
		tableName = "RA_MST_FEES_TIER";
		childTableName = "RA_MST_FEES_TIER";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	public List<FeesConfigTierVb> getFeesConfigTierByGroup(FeesConfigHeaderVb dObj,int intStatus){
		List<FeesConfigTierVb> collTemp = null;
		String query = "";
		String effectiveDate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDate = " TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR')";
		}else if ("MSSQL".equalsIgnoreCase(databaseType)){
			effectiveDate = " FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy')";
		}
		try
		{	
			if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT  COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE,  "+
						" FEE_CONFIG_CODE,CONTRACT_ID,"+effectiveDate+" EFFECTIVE_DATE,"+
					    " PRODUCT_ID,TRAN_CCY,FEE_SEQUENCE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FROM_VALUE,'N5'))) FROM_VALUE,"+
						" RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(TO_VALUE,'N5'))) TO_VALUE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FEE_AMT,'N5')))  FEE_AMT,FEE_PERCENTAGE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MIN_FEE,'N5'))) MIN_FEE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MAX_FEE,'N5'))) MAX_FEE,CHANNEL_ID "+
						" FROM RA_MST_FEES_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND " + 
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  "+
						" TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? ";
			}else {
				query = " SELECT  COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE,  "+
						" FEE_CONFIG_CODE,CONTRACT_ID,"+effectiveDate+" EFFECTIVE_DATE,"+
						" PRODUCT_ID,TRAN_CCY,FEE_SEQUENCE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FROM_VALUE,'N5'))) FROM_VALUE,"+
						" RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(TO_VALUE,'N5'))) TO_VALUE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FEE_AMT,'N5')))  FEE_AMT,FEE_PERCENTAGE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MIN_FEE,'N5'))) MIN_FEE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MAX_FEE,'N5'))) MAX_FEE,CHANNEL_ID "+
						" FROM RA_MST_FEES_TIER_PEND TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND " + 
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  "+
						" TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? ";
			}
			
			Object objParams[] = new Object[8];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getTransLineId());
			objParams[3] = new String(dObj.getBusinessLineId());
			objParams[4] = new String(dObj.getFeeConfigType());
			objParams[5] = new String(dObj.getFeeConfigCode());
			objParams[6] = new String(dObj.getContractId());
			objParams[7] = new String(dObj.getEffectiveDate());
		// 	objParams[8] = new String(dObj.getProductId());
			
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigTierMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	public List<FeesConfigTierVb> getFeesConfigTier(FeesConfigDetailsVb dObj,int intStatus){
		List<FeesConfigTierVb> collTemp = null;
		String query = "";
		String effectiveDate = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			effectiveDate = "TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR')"; 
		}else if ("MSSQL".equalsIgnoreCase(databaseType)){
			effectiveDate = "FORMAT(CAST(EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy')";
		}
		try
		{	
			if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT  COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE,  "+
						" FEE_CONFIG_CODE,CONTRACT_ID,"+effectiveDate+" EFFECTIVE_DATE,"+
					    " PRODUCT_ID,TRAN_CCY,FEE_SEQUENCE,Case when 'A' = '"+dObj.getTierType()+"' THEN RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FROM_VALUE,'N5'))) ELSE RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FROM_VALUE,'N0'))) END FROM_VALUE,"+
						" Case when 'A' = '"+dObj.getTierType()+"' THEN RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(TO_VALUE,'N5'))) ELSE RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(TO_VALUE,'N0'))) END TO_VALUE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FEE_AMT,'N5')))  FEE_AMT,FEE_PERCENTAGE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MIN_FEE,'N5'))) MIN_FEE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MAX_FEE,'N5'))) MAX_FEE,CHANNEL_ID "+
						" FROM RA_MST_FEES_TIER TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND " + 
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  "+
						" TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ?  AND TAPPR.PRODUCT_ID = ? ";
			}else {
				query = " SELECT  COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE,  "+
						" FEE_CONFIG_CODE,CONTRACT_ID,"+effectiveDate+" EFFECTIVE_DATE,"+
					    " PRODUCT_ID,TRAN_CCY,FEE_SEQUENCE,Case when 'A' = '"+dObj.getTierType()+"' THEN RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FROM_VALUE,'N5'))) ELSE RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FROM_VALUE,'N0'))) END FROM_VALUE,"+
						" Case when 'A' = '"+dObj.getTierType()+"' THEN RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(TO_VALUE,'N5'))) ELSE RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(TO_VALUE,'N0'))) END TO_VALUE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FEE_AMT,'N5')))  FEE_AMT,FEE_PERCENTAGE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MIN_FEE,'N5'))) MIN_FEE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MAX_FEE,'N5'))) MAX_FEE,CHANNEL_ID "+
						" FROM RA_MST_FEES_TIER_PEND TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND " + 
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  "+
						" TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ?  AND TAPPR.PRODUCT_ID = ? ";
			}
			
			Object objParams[] = new Object[9];
			objParams[0] = new String(dObj.getCountry());// country
			objParams[1] = new String(dObj.getLeBook());
			objParams[2] = new String(dObj.getTransLineId());
			objParams[3] = new String(dObj.getBusinessLineId());
			objParams[4] = new String(dObj.getFeeConfigType());
			objParams[5] = new String(dObj.getFeeConfigCode());
			objParams[6] = new String(dObj.getContractId());
			objParams[7] = new String(dObj.getEffectiveDate());
		 	objParams[8] = new String(dObj.getProductId());
			
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigTierMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprFeesTier(FeesConfigTierVb vObject){
		String query =  " Insert Into RA_MST_FEES_TIER(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE,  PRODUCT_ID, TRAN_CCY, FEE_SEQUENCE, FROM_VALUE,"+
				"TO_VALUE, FEE_AMT,FEE_PERCENTAGE, MIN_FEE, MAX_FEE,CHANNEL_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getProductId(),vObject.getTranCcy(),vObject.getFeeSequence(),
				vObject.getFromValue(),vObject.getToValue(),vObject.getFeeAmt(),
				vObject.getFeePercentage(),vObject.getMinFee(),vObject.getMaxFee(),vObject.getChannel()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesTier(FeesConfigTierVb vObject){
		String query =  " Insert Into RA_MST_FEES_TIER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE,  PRODUCT_ID, TRAN_CCY, FEE_SEQUENCE, FROM_VALUE,"+
				"TO_VALUE, FEE_AMT,FEE_PERCENTAGE, MIN_FEE, MAX_FEE,CHANNEL_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getProductId(),vObject.getTranCcy(),vObject.getFeeSequence(),
				vObject.getFromValue(),vObject.getToValue(),vObject.getFeeAmt(),
				vObject.getFeePercentage(),vObject.getMinFee(),vObject.getMaxFee(),vObject.getChannel()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesTierAppr(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_TIER WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteFeesTierPend(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_TIER_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode doInsertApprFeeTier(FeesConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<FeesConfigTierVb> tierlst = vObject.getFeesTierlst();
		for(FeesConfigTierVb feeTierVb : tierlst){
			feeTierVb.setCountry(vObject.getCountry());
			feeTierVb.setLeBook(vObject.getLeBook());
			feeTierVb.setTransLineId(vObject.getTransLineId());
			feeTierVb.setBusinessLineId(vObject.getBusinessLineId());
			feeTierVb.setFeeConfigType(vObject.getFeeConfigType());
			feeTierVb.setFeeConfigCode(vObject.getFeeConfigCode());
			feeTierVb.setContractId(vObject.getContractId());
			feeTierVb.setEffectiveDate(vObject.getEffectiveDate());
			feeTierVb.setProductId(vObject.getProductId());
			feeTierVb.setTranCcy(vObject.getTranCcy());
			feeTierVb.setChannel(vObject.getChannel());
			feeTierVb.setFromValue(feeTierVb.getFromValue().replaceAll(",", ""));
			feeTierVb.setToValue(feeTierVb.getToValue().replaceAll(",", ""));
			feeTierVb.setFeeAmt(feeTierVb.getFeeAmt().replaceAll(",", ""));
			feeTierVb.setMinFee(feeTierVb.getMinFee().replaceAll(",", ""));
			feeTierVb.setMaxFee(feeTierVb.getMaxFee().replaceAll(",", ""));
			feeTierVb.setFeePercentage(feeTierVb.getFeePercentage().replaceAll(",", ""));
			if(Double.parseDouble(feeTierVb.getMinFee()) >  Double.parseDouble(feeTierVb.getMaxFee())) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Tier/Range-Max Fee amount should be greather than the Min Fee Amt for the Product Id["+feeTierVb.getProductId()+"]");
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = doInsertionApprFeesTier(feeTierVb);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		}
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendFeeTier(FeesConfigDetailsVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<FeesConfigTierVb> tierlst = vObject.getFeesTierlst();
		for(FeesConfigTierVb feeTierVb : tierlst){
			feeTierVb.setCountry(vObject.getCountry());
			feeTierVb.setLeBook(vObject.getLeBook());
			feeTierVb.setTransLineId(vObject.getTransLineId());
			feeTierVb.setBusinessLineId(vObject.getBusinessLineId());
			feeTierVb.setFeeConfigType(vObject.getFeeConfigType());
			feeTierVb.setFeeConfigCode(vObject.getFeeConfigCode());
			feeTierVb.setContractId(vObject.getContractId());
			feeTierVb.setEffectiveDate(vObject.getEffectiveDate());
			feeTierVb.setProductId(vObject.getProductId());
			feeTierVb.setTranCcy(vObject.getTranCcy());
			feeTierVb.setChannel(vObject.getChannel());
			feeTierVb.setFromValue(feeTierVb.getFromValue().replaceAll(",", ""));
			feeTierVb.setToValue(feeTierVb.getToValue().replaceAll(",", ""));
			feeTierVb.setFeeAmt(feeTierVb.getFeeAmt().replaceAll(",", ""));
			feeTierVb.setMinFee(feeTierVb.getMinFee().replaceAll(",", ""));
			feeTierVb.setMaxFee(feeTierVb.getMaxFee().replaceAll(",", ""));
			feeTierVb.setFeePercentage(feeTierVb.getFeePercentage().replaceAll(",", ""));
			if(Double.parseDouble(feeTierVb.getMinFee()) >  Double.parseDouble(feeTierVb.getMaxFee())) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Tier/Range-Max Fee amount should be greather than the Min Fee Amt for the Product Id["+feeTierVb.getProductId()+"]");
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = doInsertionPendFeesTier(feeTierVb);
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
	protected String getAuditString(FeesConfigTierVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getBusinessLineId()))
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			
			if(ValidationUtil.isValid(vObject.getFeeConfigCode()))
				strAudit.append("FEE_CONFIG_CODE"+auditDelimiterColVal+vObject.getFeeConfigCode().trim());
			else
				strAudit.append("FEE_CONFIG_CODE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getContractId()))
				strAudit.append("CONTRACT_ID"+auditDelimiterColVal+vObject.getContractId().trim());
			else
				strAudit.append("CONTRACT_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getEffectiveDate()))
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+vObject.getEffectiveDate().trim());
			else
				strAudit.append("EFFECTIVE_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("PRODUCT_ID"+auditDelimiterColVal+vObject.getProductId());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTranCcy()))
				strAudit.append("TRAN_CCY"+auditDelimiterColVal+vObject.getTranCcy().trim());
			else
				strAudit.append("TRAN_CCY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeSequence()))
				strAudit.append("FEE_SEQUENCE"+auditDelimiterColVal+vObject.getFeeSequence());
			else
				strAudit.append("FEE_SEQUENCE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFromValue()))
				strAudit.append("FROM_VALUE"+auditDelimiterColVal+vObject.getFromValue());
			else
				strAudit.append("FROM_VALUE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getToValue()))
				strAudit.append("TO_VALUE"+auditDelimiterColVal+vObject.getToValue());
			else
				strAudit.append("TO_VALUE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeAmt()))
				strAudit.append("FEE_AMOUNT"+auditDelimiterColVal+vObject.getFeeAmt().trim());
			else
				strAudit.append("FEE_AMOUNT"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeePercentage()))
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+vObject.getFeePercentage().trim());
			else
				strAudit.append("FEE_PERCENTAGE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMinFee()))
				strAudit.append("MIN_FEE"+auditDelimiterColVal+vObject.getMinFee().trim());
			else
				strAudit.append("MIN_FEE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getMaxFee()))
				strAudit.append("MAX_FEE"+auditDelimiterColVal+vObject.getMaxFee().trim());
			else
				strAudit.append("MAX_FEE"+auditDelimiterColVal+"NULL");
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
}