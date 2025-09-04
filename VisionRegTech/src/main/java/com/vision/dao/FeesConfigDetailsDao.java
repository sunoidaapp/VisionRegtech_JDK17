package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class FeesConfigDetailsDao extends AbstractDao<FeesConfigDetailsVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	FeesConfigTierDao feesConfigTierDao;
	
	protected RowMapper getFeesConfigDetailMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigDetailsVb vObject = new FeesConfigDetailsVb();
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
				vObject.setFeeCcy(rs.getString("FEE_CCY"));
				vObject.setInterestBasis(rs.getString("INTEREST_BASIS"));
				vObject.setFeeBasis(rs.getString("FEE_BASIS"));
				vObject.setFeeType(rs.getString("FEE_TYPE"));
				vObject.setTierType(rs.getString("TIER_TYPE"));
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
		serviceName = "FeesConfigDetails";
		serviceDesc = "Fees Config Details";
		tableName = "RA_MST_FEES_DETAILS";
		childTableName = "RA_MST_FEES_DETAILS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	public List<FeesConfigDetailsVb> getFeesConfigDetails(FeesConfigHeaderVb dObj,int intStatus){
		List<FeesConfigDetailsVb> collTemp = null;
		String query = "";
		try
		{	
			if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == Constants.STATUS_ZERO) {
				query = " SELECT  COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE,  "+
						" FEE_CONFIG_CODE,CONTRACT_ID,"+commonDao.getDbFunction("DATEFUNC")+"(EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,PRODUCT_ID,TRAN_CCY,          "+
						" FEE_CCY,INTEREST_BASIS,FEE_BASIS, FEE_TYPE,TIER_TYPE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FEE_AMT,'N5'))) FEE_AMT,FEE_PERCENTAGE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MIN_FEE,'N5'))) MIN_FEE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MAX_FEE,'N5'))) MAX_FEE,CHANNEL_ID   "+
						" FROM RA_MST_FEES_DETAILS TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND " + 
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  "+
						" TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? ";
			}else {
				query = " SELECT  COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE,  "+
						" FEE_CONFIG_CODE,CONTRACT_ID,"+commonDao.getDbFunction("DATEFUNC")+"(EFFECTIVE_DATE, '"+commonDao.getDbFunction("DATEFORMAT")+"') EFFECTIVE_DATE,PRODUCT_ID,TRAN_CCY,          "+
						" FEE_CCY,INTEREST_BASIS,FEE_BASIS, FEE_TYPE,TIER_TYPE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(FEE_AMT,'N5'))) FEE_AMT,FEE_PERCENTAGE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MIN_FEE,'N5'))) MIN_FEE,RTRIM(LTRIM("+commonDao.getDbFunction("DATEFUNC")+"(MAX_FEE,'N5'))) MAX_FEE,CHANNEL_ID   "+
						" FROM RA_MST_FEES_DETAILS_PEND TAPPR WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND " + 
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
			
			collTemp = getJdbcTemplate().query(query,objParams,getFeesConfigDetailMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Exception ");
			return null;
		}
	}
	protected int doInsertionApprFeesDetails(FeesConfigDetailsVb vObject){
		String query =  " Insert Into RA_MST_FEES_DETAILS(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE,  PRODUCT_ID, TRAN_CCY, FEE_CCY,"+
				" INTEREST_BASIS,  FEE_BASIS,  FEE_TYPE,  TIER_TYPE,"+
				"FEE_AMT, FEE_PERCENTAGE, MIN_FEE, MAX_FEE,CHANNEL_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getProductId(),vObject.getTranCcy(),
				vObject.getFeeCcy(),vObject.getInterestBasis(),vObject.getFeeBasis(),
				vObject.getFeeType(),vObject.getTierType(),vObject.getFeeAmt(),
				vObject.getFeePercentage(),vObject.getMinFee(),vObject.getMaxFee(),vObject.getChannel()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesDetails(FeesConfigDetailsVb vObject){
		String query =  " Insert Into RA_MST_FEES_DETAILS_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE,  PRODUCT_ID, TRAN_CCY, FEE_CCY,"+
				" INTEREST_BASIS,  FEE_BASIS,  FEE_TYPE,  TIER_TYPE,"+
				"FEE_AMT, FEE_PERCENTAGE, MIN_FEE, MAX_FEE,CHANNEL_ID) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getProductId(),vObject.getTranCcy(),
				vObject.getFeeCcy(),vObject.getInterestBasis(),vObject.getFeeBasis(),
				vObject.getFeeType(),vObject.getTierType(),vObject.getFeeAmt(),
				vObject.getFeePercentage(),vObject.getMinFee(),vObject.getMaxFee(),vObject.getChannel()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesDetailsAppr(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_DETAILS WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteFeesDetailsPend(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_DETAILS_PEND WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	public ExceptionCode deleteAndInsertApprFeeDetail(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<FeesConfigDetailsVb> collTemp = null;
		collTemp = getFeesConfigDetails(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteFeesDetailsAppr(vObject);
		}
		List<FeesConfigTierVb> collTempTier = null;
		collTempTier =  feesConfigTierDao.getFeesConfigTierByGroup(vObject, Constants.STATUS_ZERO);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = feesConfigTierDao.deleteFeesTierAppr(vObject);
		}
		List<FeesConfigDetailsVb> detaillst = vObject.getFeesConfigDetaillst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(FeesConfigDetailsVb feeDetailVb : detaillst){
				feeDetailVb.setCountry(vObject.getCountry());
				feeDetailVb.setLeBook(vObject.getLeBook());
				feeDetailVb.setTransLineId(vObject.getTransLineId());
				feeDetailVb.setBusinessLineId(vObject.getBusinessLineId());
				feeDetailVb.setFeeConfigType(vObject.getFeeConfigType());
				feeDetailVb.setFeeConfigCode(vObject.getFeeConfigCode());
				feeDetailVb.setContractId(vObject.getContractId());
				feeDetailVb.setEffectiveDate(vObject.getEffectiveDate());
				feeDetailVb.setFeeAmt(feeDetailVb.getFeeAmt().replaceAll(",", "")); 
				feeDetailVb.setMinFee(feeDetailVb.getMinFee().replaceAll(",", ""));
				feeDetailVb.setMaxFee(feeDetailVb.getMaxFee().replaceAll(",", ""));
				if(Double.parseDouble(feeDetailVb.getMinFee()) >  Double.parseDouble(feeDetailVb.getMaxFee())) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Max Fee amount should be greather than the Min Fee Amt for the Product Id["+feeDetailVb.getProductId()+"]");
					throw buildRuntimeCustomException(exceptionCode);
				}
				feeDetailVb.setFeePercentage(feeDetailVb.getFeePercentage().replaceAll(",", ""));
				if(!ValidationUtil.isValid(feeDetailVb.getTierType())) {
					feeDetailVb.setTierType("NA");
				}
				if(!ValidationUtil.isValid(feeDetailVb.getChannel())) {
					feeDetailVb.setChannel("NA");
				}
				retVal = doInsertionApprFeesDetails(feeDetailVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				exceptionCode=feesConfigTierDao.doInsertApprFeeTier(feeDetailVb);
			}
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode deleteAndInsertPendFeeDetail(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode =  new ExceptionCode();
		List<FeesConfigDetailsVb> collTemp = null;
		collTemp = getFeesConfigDetails(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = deleteFeesDetailsPend(vObject);
		}
		List<FeesConfigTierVb> collTempTier = null;
		collTempTier =  feesConfigTierDao.getFeesConfigTierByGroup(vObject, 1);
		if (collTemp != null && collTemp.size() > 0 ){
			int delCnt = feesConfigTierDao.deleteFeesTierPend(vObject);
		}
		List<FeesConfigDetailsVb> detaillst = vObject.getFeesConfigDetaillst();
		if(detaillst != null && !detaillst.isEmpty()) {
			for(FeesConfigDetailsVb feeDetailVb : detaillst){
				feeDetailVb.setCountry(vObject.getCountry());
				feeDetailVb.setLeBook(vObject.getLeBook());
				feeDetailVb.setTransLineId(vObject.getTransLineId());
				feeDetailVb.setBusinessLineId(vObject.getBusinessLineId());
				feeDetailVb.setFeeConfigType(vObject.getFeeConfigType());
				feeDetailVb.setFeeConfigCode(vObject.getFeeConfigCode());
				feeDetailVb.setContractId(vObject.getContractId());
				feeDetailVb.setEffectiveDate(vObject.getEffectiveDate());
				feeDetailVb.setFeeAmt(feeDetailVb.getFeeAmt().replaceAll(",", "")); 
				feeDetailVb.setMinFee(feeDetailVb.getMinFee().replaceAll(",", ""));
				feeDetailVb.setMaxFee(feeDetailVb.getMaxFee().replaceAll(",", ""));
				feeDetailVb.setFeePercentage(feeDetailVb.getFeePercentage().replaceAll(",", ""));
				if(!ValidationUtil.isValid(feeDetailVb.getTierType())) {
					feeDetailVb.setTierType("NA");
				}
				if(!ValidationUtil.isValid(feeDetailVb.getChannel())) {
					feeDetailVb.setChannel("NA");
				}
				if(Double.parseDouble(feeDetailVb.getMinFee()) >  Double.parseDouble(feeDetailVb.getMaxFee())) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Max Fee amount should be greather than the Min Fee Amt for the Product Id["+feeDetailVb.getProductId()+"]");
					throw buildRuntimeCustomException(exceptionCode);
				}
				retVal = doInsertionPendFeesDetails(feeDetailVb);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				exceptionCode=feesConfigTierDao.deleteAndInsertPendFeeTier(feeDetailVb);
			}	
		}
		exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	@Override
	protected String getAuditString(FeesConfigDetailsVb vObject){
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
			
			strAudit.append("FEE_CONFIG_TYPE"+auditDelimiterColVal+vObject.getFeeConfigType());
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
			
			strAudit.append("FEE_CCY"+auditDelimiterColVal+vObject.getFeeCcy());
			strAudit.append(auditDelimiter);
			
			strAudit.append("INTEREST_BASIS"+auditDelimiterColVal+vObject.getInterestBasis());
			strAudit.append(auditDelimiter);
			
			strAudit.append("FEE_BASIS"+auditDelimiterColVal+vObject.getFeeBasis());
			strAudit.append(auditDelimiter);
			
			strAudit.append("FEE_TYPE"+auditDelimiterColVal+vObject.getFeeType());
			strAudit.append(auditDelimiter);
			
			strAudit.append("AMOUNT_OR_COUNT"+auditDelimiterColVal+vObject.getTierType());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getFeeAmt()))
				strAudit.append("FEE_AMT"+auditDelimiterColVal+vObject.getFeeAmt().trim());
			else
				strAudit.append("FEE_AMT"+auditDelimiterColVal+"NULL");
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