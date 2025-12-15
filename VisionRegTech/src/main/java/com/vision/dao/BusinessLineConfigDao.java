package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

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
import com.vision.vb.BusinessLineGLVb;
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;
@Component
public class BusinessLineConfigDao extends AbstractDao<BusinessLineHeaderVb>{
	@Autowired
	BusinessLineConfigGLDao businessLineConfigGLDao;
	
	@Autowired
	CommonDao commonDao;
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessLineHeaderVb vObject = new BusinessLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineDescription(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineTypeDesc(rs.getString("TRANS_LINE_TYPE_DESC"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineIdDesc(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setBusinessLineStatus(rs.getInt("BUSINESS_LINE_STATUS"));
				vObject.setBusinessLineStatusDesc(rs.getString("BUSINESS_LINE_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}
	protected RowMapper getDetailMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BusinessLineHeaderVb vObject = new BusinessLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineDescription(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTranLineGrp(rs.getString("TRAN_LINE_GRP"));
				vObject.setTranLineGrpDesc(rs.getString("TRAN_LINE_GRP_DESC"));
				vObject.setBusinessLineType(rs.getString("BUSINESS_LINE_TYPE"));
				vObject.setIncomeExpenseType(rs.getString("IE_TYPE"));
				vObject.setBusinessLineStatus(rs.getInt("BUSINESS_LINE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}
	public List<BusinessLineHeaderVb> getQueryResults(BusinessLineHeaderVb dObj, int intStatus){
		List<BusinessLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String("SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION,TAppr.TRANS_LINE_TYPE,   " + 
				" TAppr.TRANS_LINE_ID,"+
				" CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "+
				" CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "+
				"  ELSE "+
				"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "+
				"  END TRAN_LINE_GRP_DESC, "+
				" TAppr.BUSINESS_LINE_TYPE,TAppr.IE_TYPE,   " + 
				" TAppr.BUSINESS_LINE_STATUS,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   " + 
				" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,   " + 
				" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,   " + 
				" TAppr.DATE_LAST_MODIFIED,   " + 
				" TAppr.DATE_CREATION FROM RA_MST_BUSINESS_LINE_HEADER TAppr, NUM_SUB_TAB T1,RA_MST_TRANS_LINE_HEADER T2 WHERE   " + 
				" TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.BUSINESS_LINE_ID = ? AND  "+
				" T1.NUM_tab = TAppr.RECORD_INDICATOR_NT" + 
				" and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR"+
				" AND TAPPR.TRANS_LINE_ID = T2.TRANS_LINE_ID ");
		strQueryPend = new String("SELECT TPEND.COUNTRY, TPEND.LE_BOOK, TPEND.BUSINESS_LINE_ID,TPEND.BUSINESS_LINE_DESCRIPTION,TPEND.TRANS_LINE_TYPE,   " + 
				" TPEND.TRANS_LINE_ID,"+
				" CASE WHEN TPEND.TRANS_LINE_TYPE='P' THEN T2.TRANS_LINE_PROD_GRP ELSE T2.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP, "+
				" CASE WHEN TAppr.TRANS_LINE_TYPE='P' THEN "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_PROD_GRP) "+
				"  ELSE "+
				"  (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T2.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T2.TRANS_LINE_SERV_GRP) "+
				"  END TRAN_LINE_GRP_DESC, "+
				" TPEND.BUSINESS_LINE_TYPE,TPEND.IE_TYPE,   " + 
				" TPEND.BUSINESS_LINE_STATUS,TPEND.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,   " + 
				" TPEND. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPEND.MAKER,0) ) MAKER_NAME,   " + 
				" TPEND.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPEND.VERIFIER,0) ) VERIFIER_NAME,   " + 
				" TPEND.DATE_LAST_MODIFIED,   " + 
				" TPEND.DATE_CREATION FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPEND, NUM_SUB_TAB T1,RA_MST_TRANS_LINE_HEADER T2 "+
				" WHERE TPEND.COUNTRY =? AND TPEND.LE_BOOK =? AND TPEND.BUSINESS_LINE_ID = ? AND  "+
				" T1.NUM_tab = TPEND.RECORD_INDICATOR_NT" + 
				" and T1.NUM_sub_tab = TPEND.RECORD_INDICATOR"+
				" AND TPEND.TRANS_LINE_ID = T2.TRANS_LINE_ID ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getBusinessLineId());
		try
		{if(!dObj.isVerificationRequired() || dObj.isReview()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getDetailMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getDetailMapper());
			}
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if(intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	public List<BusinessLineHeaderVb> getQueryPopupResults(BusinessLineHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		strBufApprove = new StringBuffer(" Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.BUSINESS_LINE_ID,TAppr.BUSINESS_LINE_DESCRIPTION, "+   
				"  TAppr.TRANS_LINE_TYPE, t2.alpha_subtab_description TRANS_LINE_TYPE_DESC,TAppr.TRANS_LINE_ID,T5.TRANS_LINE_DESCRIPTION, "+                                 
				"  TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,  "+                                                 
				"  TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,  "+                                                  
				"  TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,  "+              
				"  TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME, "+       
				"  "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION     "+                                                                                           
				"  FROM RA_MST_BUSINESS_LINE_HEADER TAppr ,ALPHA_SUB_TAB T2,NUM_SUB_TAB T3,NUM_SUB_TAB T4,   "+
				"  RA_MST_TRANS_LINE_HEADER T5 Where                 "+                                     
				"  t2.Alpha_tab = TAppr.TRANS_LINE_TYPE_AT           "+                                                                 
				"  and t2.Alpha_sub_tab = TAppr.TRANS_LINE_TYPE      "+                                                                     
				"  and t3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT    "+                                                                            
				"  and t3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS   "+                                                                            
				"  and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT        "+                                                                         
				"  and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR       "+
				"  AND TAPPR.TRANS_LINE_ID = T5.TRANS_LINE_ID        "+
				"  ) TAppr                                           ");
		
		strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_BUSINESS_LINE_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.BUSINESS_LINE_ID = TPend.BUSINESS_LINE_ID)");
		
		strBufPending = new StringBuffer(" Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.BUSINESS_LINE_ID,TPend.BUSINESS_LINE_DESCRIPTION, "+   
				"  TPend.TRANS_LINE_TYPE, t2.alpha_subtab_description TRANS_LINE_TYPE_DESC,TPend.TRANS_LINE_ID,T5.TRANS_LINE_DESCRIPTION, "+                                 
				"  TPend.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,  "+                                                 
				"  TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,  "+                                                  
				"  TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,  "+              
				"  TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME, "+       
				"  "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION     "+                                                                                           
				"  FROM RA_MST_BUSINESS_LINE_HEADER_PEND TPend ,ALPHA_SUB_TAB T2,NUM_SUB_TAB T3,NUM_SUB_TAB T4,   "+
				"  RA_MST_TRANS_LINE_HEADER T5 Where                 "+                                     
				"  t2.Alpha_tab = TPend.TRANS_LINE_TYPE_AT           "+                                                                 
				"  and t2.Alpha_sub_tab = TPend.TRANS_LINE_TYPE      "+                                                                     
				"  and t3.NUM_tab = TPend.BUSINESS_LINE_STATUS_NT    "+                                                                            
				"  and t3.NUM_sub_tab = TPend.BUSINESS_LINE_STATUS   "+                                                                            
				"  and t4.NUM_tab = TPend.RECORD_INDICATOR_NT        "+                                                                         
				"  and t4.NUM_sub_tab = TPend.RECORD_INDICATOR       "+
				"  AND TPend.TRANS_LINE_ID = T5.TRANS_LINE_ID        "+
				"  ) TPend                                           ");				
		try
		{
			
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data: dObj.getSmartSearchOpt()){
					if(count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if(!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType()) || "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "businessLineId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_ID) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_ID) "+ val, strBufPending, data.getJoinType());
						break;

					case "businessLineDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;

						
					case "transLineTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "transLineIdDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "businessLineStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "dateCreation":
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TAPPR.DATE_CREATION,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"') " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"') " + val, strBufPending, data.getJoinType());
						break;
									
					case "dateLastModified":
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"') " + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"') " + val, strBufPending, data.getJoinType());
						break;

					case "makerName":
						CommonUtils.addToQuerySearch(" (TAPPR.MAKER_NAME) IN ("+ val+") ", strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" (TPend.MAKER_NAME) IN ("+ val+") ", strBufPending, data.getJoinType());
						break;

					default:
					}
					count++;
				}
			}
			VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
			if(("Y".equalsIgnoreCase(visionUsersVb.getUpdateRestriction()))){
				if(ValidationUtil.isValid(visionUsersVb.getCountry())){
					CommonUtils.addToQuery(" COUNTRY IN ("+toSqlInList(visionUsersVb.getCountry())+") ", strBufApprove);
					CommonUtils.addToQuery(" COUNTRY IN ("+toSqlInList(visionUsersVb.getCountry())+") ", strBufPending);
				}
				if(ValidationUtil.isValid(visionUsersVb.getLeBook())){
					CommonUtils.addToQuery(" LE_BOOK IN ("+toSqlInList(visionUsersVb.getLeBook())+") ", strBufApprove);
					CommonUtils.addToQuery(" LE_BOOK IN ("+toSqlInList(visionUsersVb.getLeBook())+") ", strBufPending);
				}
			}
			orderBy=" Order by BUSINESS_LINE_ID ";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
			}
}
	private String toSqlInList(String CcountryLeBook) {
	    return Arrays.stream(CcountryLeBook.split(","))
	            .map(String::trim)
	            .map(val -> "'" + val + "'")
	            .collect(Collectors.joining(","));
	}
	protected int deleteBusinessLineHeaderAppr(BusinessLineHeaderVb vObject){
		String query = "Delete from RA_MST_business_LINE_HEADER where COUNTRY = ? AND LE_BOOK = ? AND business_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteBusinessLineHeaderPend(BusinessLineHeaderVb vObject){
		String query = "Delete from RA_MST_business_LINE_HEADER_PEND where COUNTRY = ? AND LE_BOOK = ? AND business_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprBusinessLineHeaders(BusinessLineHeaderVb vObject){
		String query =  " Insert Into RA_MST_business_LINE_HEADER(COUNTRY,LE_BOOK,business_LINE_ID,business_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE" + 
				",business_LINE_STATUS_NT,business_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),vObject.getBusinessLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineId(),vObject.getBusinessLineType(),
				vObject.getIncomeExpenseType(),
				vObject.getBusinessLineStatusNT(),vObject.getBusinessLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendBusinessLineHeaders(BusinessLineHeaderVb vObject){
		String query =  " Insert Into RA_MST_business_LINE_HEADER_PEND(COUNTRY,LE_BOOK,business_LINE_ID,business_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE" + 
				",business_LINE_STATUS_NT,business_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),vObject.getBusinessLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineId(),vObject.getBusinessLineType(),
				vObject.getIncomeExpenseType(),
				vObject.getBusinessLineStatusNT(),vObject.getBusinessLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionPendBusinessLineHeadersDc(BusinessLineHeaderVb vObject){
		String query =  " Insert Into RA_MST_business_LINE_HEADER(COUNTRY,LE_BOOK,business_LINE_ID,business_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				",TRANS_LINE_ID,BUSINESS_LINE_TYPE,IE_TYPE" + 
				",business_LINE_STATUS_NT,business_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+",?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId(),vObject.getBusinessLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineId(),vObject.getBusinessLineType(),
				vObject.getIncomeExpenseType(),
				vObject.getBusinessLineStatusNT(),vObject.getBusinessLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(BusinessLineHeaderVb vObject){
		String query = " Update RA_MST_business_LINE_HEADER set business_LINE_DESCRIPTION= ?" + 
				",TRANS_LINE_TYPE= ?, TRANS_LINE_ID = ?,BUSINESS_LINE_TYPE= ?" + 
				",IE_TYPE= ?,business_LINE_STATUS= ?" + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= "+commonDao.getDbFunction("SYSDATE")+" "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND business_LINE_ID= ? ";
		Object[] args = {vObject.getBusinessLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineId(),vObject.getBusinessLineType(),
				vObject.getIncomeExpenseType(),
				vObject.getBusinessLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdatePendHeader(BusinessLineHeaderVb vObject){
		String query = " Update RA_MST_business_LINE_HEADER_PEND set business_LINE_DESCRIPTION= ?" + 
				",TRANS_LINE_TYPE= ?,TRANS_LINE_ID = ?,BUSINESS_LINE_TYPE= ?" + 
				",IE_TYPE= ?,business_LINE_STATUS= ?" + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= "+commonDao.getDbFunction("SYSDATE")+" "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND business_LINE_ID= ? ";
		Object[] args = {vObject.getBusinessLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineId(),vObject.getBusinessLineType(),
				vObject.getIncomeExpenseType(),
				vObject.getBusinessLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),vObject.getBusinessLineId()};
		return getJdbcTemplate().update(query,args);
	}
	protected List<BusinessLineHeaderVb> selectApprovedRecord(BusinessLineHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<BusinessLineHeaderVb> doSelectPendingRecord(BusinessLineHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(BusinessLineHeaderVb records){return records.getBusinessLineStatus();}
	@Override
	protected void setStatus(BusinessLineHeaderVb vObject,int status){vObject.setBusinessLineStatus(status);
	}
	public ExceptionCode doInsertApprRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
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
		vObject.setBusinessLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionApprBusinessLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		
		if(vObject.getBusinessLineGllst() != null && vObject.getBusinessLineGllst().size() > 0) {
			exceptionCode = businessLineConfigGLDao.deleteAndInsertApprGl(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	public ExceptionCode doInsertRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException {
		List<BusinessLineHeaderVb> collTemp = null;
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
		vObject.setBusinessLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		retVal = doInsertionPendBusinessLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getBusinessLineGllst() != null && vObject.getBusinessLineGllst().size() > 0) {
			exceptionCode = businessLineConfigGLDao.deleteAndInsertPendGl(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode doUpdateApprRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException  {
		List<BusinessLineHeaderVb> collTemp = null;
		BusinessLineHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObjectlocal = ((ArrayList<BusinessLineHeaderVb>)collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getBusinessLineGllst() != null && vObject.getBusinessLineGllst().size() > 0) {
			exceptionCode = businessLineConfigGLDao.deleteAndInsertApprGl(vObject);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode doUpdateRecordForNonTrans(BusinessLineHeaderVb vObject) throws RuntimeCustomException  {
		List<BusinessLineHeaderVb> collTemp = null;
		BusinessLineHeaderVb vObjectlocal = null;
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
			vObjectlocal = ((ArrayList<BusinessLineHeaderVb>)collTemp).get(0);
			vObject.setDateCreation(vObjectlocal.getDateCreation());
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_INSERT);
				retVal = doUpdatePendHeader(vObject);
			}else{
				vObject.setVerifier(0);
				vObject.setRecordIndicator(Constants.STATUS_UPDATE);
				retVal = doUpdatePendHeader(vObject);
			}
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getBusinessLineGllst() != null && vObject.getBusinessLineGllst().size() > 0) {
				businessLineConfigGLDao.deleteAndInsertPendGl(vObject);
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
				vObjectlocal = ((ArrayList<BusinessLineHeaderVb>)collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    retVal = doInsertionPendBusinessLineHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getBusinessLineGllst() != null && vObject.getBusinessLineGllst().size() > 0) {
				businessLineConfigGLDao.deleteAndInsertPendGl(vObject);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doRejectForTransaction(BusinessLineHeaderVb vObject)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}
	public ExceptionCode doRejectRecord(BusinessLineHeaderVb vObject)throws RuntimeCustomException {
		BusinessLineHeaderVb vObjectlocal = null;
		List<BusinessLineHeaderVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc  = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
			if(vObject.getRecordIndicator() == 1 || vObject.getRecordIndicator() == 3 )
			    vObject.setRecordIndicator(0);
			    else
				   vObject.setRecordIndicator(-1);
			// See if such a pending request exists in the pending table
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			vObjectlocal = ((ArrayList<BusinessLineHeaderVb>)collTemp).get(0);
			retVal = deleteBusinessLineHeaderPend(vObject);
			
			List<BusinessLineGLVb> collTempGl = null;
			collTempGl = businessLineConfigGLDao.getBusinessGLDetails(vObject, 1);
			if (collTempGl != null && collTempGl.size() > 0){
				retVal = businessLineConfigGLDao.deleteBusinessLineGlPend(vObject);
			}
			
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Reject.",ex);
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doApproveForTransaction(BusinessLineHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doApproveRecord(vObject,staticDelete);
	}
	public ExceptionCode doApproveRecord(BusinessLineHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		BusinessLineHeaderVb oldContents = null;
		BusinessLineGLVb oldContentsGl = null;
		
		BusinessLineHeaderVb vObjectlocal = null;
		BusinessLineGLVb vObjectGllocal = null;
		
		List<BusinessLineHeaderVb> collTemp = null;
		List<BusinessLineGLVb> collTempGl = null;
		List<BusinessLineGLVb> collTempGlAppr = null;
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		try {
			// See if such a pending request exists in the pending table
			vObject.setVerifier(getIntCurrentUserId());
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null){
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}

			if (collTemp.size() == 0){
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObjectlocal = ((ArrayList<BusinessLineHeaderVb>)collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()){
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			collTempGl = businessLineConfigGLDao.getBusinessGLDetails(vObjectlocal,1);
			if(collTempGl != null && collTempGl.size() > 0) {
				vObjectGllocal = ((ArrayList<BusinessLineGLVb>)collTempGl).get(0);
			}
			
			
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit in"+commonDao.getDbFunction("DATEFUNC")+"ion later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT){
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()){
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<BusinessLineHeaderVb>)collTemp).get(0);
				
				collTempGlAppr = businessLineConfigGLDao.getBusinessGLDetails(vObjectlocal,0);
				if(collTempGlAppr != null && collTempGlAppr.size() > 0) {
					oldContentsGl = ((ArrayList<BusinessLineGLVb>)collTempGlAppr).get(0);
				}
				
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){  // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionApprBusinessLineHeaders(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if(collTempGlAppr != null && collTempGlAppr.size() > 0) {
					businessLineConfigGLDao.deleteBusinessLineGlAppr(vObjectlocal);
				}
				if(collTempGl != null && !collTempGl.isEmpty()) {
					collTempGl.forEach(glPend -> {
						retVal = businessLineConfigGLDao.doInsertionApprBusinessLineGL(glPend);
					});
				}
				
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				vObject.setDateCreation(systemDate);
				strApproveOperation = Constants.ADD;
			}else if (vObjectlocal.getRecordIndicator() == Constants.STATUS_UPDATE){ // Modify authorization
			
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()){
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}	

				// If record already exists in the approved table, reject the addition
				if (collTemp.size() > 0 ){
					//retVal = doUpdateAppr(vObjectlocal, MISConstants.ACTIVATE);
					vObjectlocal.setVerifier(getIntCurrentUserId());
					vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
					retVal = doUpdateApprHeader(vObjectlocal);
					if (retVal != Constants.SUCCESSFUL_OPERATION){
						exceptionCode = getResultObject(retVal);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
				if(collTempGlAppr != null && collTempGlAppr.size() > 0) {
					businessLineConfigGLDao.deleteBusinessLineGlAppr(vObjectlocal);
				}
				if(collTempGl != null && !collTempGl.isEmpty()) {
					collTempGl.forEach(glPend -> {
						retVal = businessLineConfigGLDao.doInsertionApprBusinessLineGL(glPend);
					});
				}
				// Modify the existing contents of the record in Approved table
				
				String systemDate = getSystemDate();
				vObject.setDateLastModified(systemDate);
				// Set the current operation to write to audit log
				strApproveOperation = Constants.MODIFY;
			}else{
				exceptionCode = getResultObject(Constants.INVALID_STATUS_FLAG_IN_DATABASE);
				throw buildRuntimeCustomException(exceptionCode);
			}	

			// Delete the record from the Pending table
			retVal = deleteBusinessLineHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = businessLineConfigGLDao.deleteBusinessLineGlPend(vObjectlocal);
			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete){
				exceptionCode = writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			}
			else
				exceptionCode = writeAuditLog(vObjectlocal, oldContents);

			/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}*/
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Approve.",ex);
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Override
	protected void setServiceDefaults() {
		serviceName = "BusinessLineConfig";
		serviceDesc = "Business Line Config";
		tableName = "RA_MST_BUSINESS_LINE_HEADER";
		childTableName = "RA_MST_BUSINESS_LINE_HEADER";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	@Override
	protected String getAuditString(BusinessLineHeaderVb vObject){
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
				strAudit.append("BUSINESS_LINE_ID "+auditDelimiterColVal+vObject.getBusinessLineId().trim());
			else
				strAudit.append("BUSINESS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("BUSINESS_LINE_DESCRIPTION "+auditDelimiterColVal+vObject.getBusinessLineDescription().trim());
			else
				strAudit.append("BUSINESS_LINE_DESCRIPTION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTransLineId()))
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+vObject.getTransLineId().trim());
			else
				strAudit.append("TRANS_LINE_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			strAudit.append("BUSINESS_LINE_TYPE"+auditDelimiterColVal+vObject.getBusinessLineType());
			strAudit.append(auditDelimiter);
			
			strAudit.append("IE_TYPE"+auditDelimiterColVal+vObject.getIncomeExpenseType());
			strAudit.append(auditDelimiter);
			
			strAudit.append("BUSINESS_LINE_STATUS_NT"+auditDelimiterColVal+vObject.getBusinessLineStatusNT());
			strAudit.append(auditDelimiter);
			
			strAudit.append("BUSINESS_LINE_STATUS"+auditDelimiterColVal+vObject.getBusinessLineStatus());
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
}
