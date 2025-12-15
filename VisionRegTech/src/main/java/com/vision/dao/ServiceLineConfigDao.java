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
import com.vision.vb.SmartSearchVb;
import com.vision.vb.TransLineChannelVb;
import com.vision.vb.TransLineGLVb;
import com.vision.vb.TransLineHeaderVb;
import com.vision.vb.TransLineSbuVb;
import com.vision.vb.VisionUsersVb;

@Component
public class ServiceLineConfigDao extends AbstractDao<TransLineHeaderVb> {
	@Autowired
	TransLinesSbuDao transLinesSbuDao;
	
	@Autowired
	TransLinesChannelDao transLinesChannelDao;
	
	@Autowired
	CommonDao commonDao;
	
	@Autowired
	TransLinesGlDao transLinesGlDao;
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TransLineHeaderVb vObject = new TransLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineDescription(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTransLineSubType(rs.getString("TRANS_LINE_SERV_SUB_TYPE"));
				vObject.setTransLineSubTypeDesc(rs.getString("TRANS_LINE_SERV_SUB_TYPE_DESC"));
				vObject.setTransLineGrp(rs.getString("TRANS_LINE_SERV_GRP"));
				vObject.setTransLineGrpDesc(rs.getString("TRANS_LINE_SERV_GRP_DESC"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionFrequencyDesc(rs.getString("EXTRACTION_FREQUENCY_DESC"));
				vObject.setTransLineStatus(rs.getInt("TRANS_LINE_STATUS"));
				vObject.setTransLineStatusDesc(rs.getString("TRANS_LINE_STATUS_Desc"));
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
				TransLineHeaderVb vObject = new TransLineHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTransLineDescription(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTransLineSubType(rs.getString("TRANS_LINE_SERV_SUB_TYPE"));
				vObject.setTransLineGrp(rs.getString("TRANS_LINE_SERV_GRP"));
				vObject.setExtractionFrequency(rs.getString("EXTRACTION_FREQUENCY"));
				vObject.setExtractionMonthDay(rs.getString("EXTRACTION_MONTH_DAY") );
				vObject.setTargetStgTableId(rs.getString("TARGET_STG_TABLE_ID"));
				vObject.setDepartment(rs.getString("DEPT_ID"));
				vObject.setTransLineStatus(rs.getInt("TRANS_LINE_STATUS"));
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
	@Override
	protected void setServiceDefaults() {
		serviceName = "ServiceLineConfig";
		serviceDesc = "Service Line Config";
		tableName = "RA_MST_TRANS_LINE_HEADER";
		childTableName = "RA_MST_TRANS_LINE_HEADER";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	public List<TransLineHeaderVb> getQueryPopupResults(TransLineHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		strBufApprove = new StringBuffer("Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,TAppr.TRANS_LINE_DESCRIPTION,                                                                                                                 "+
				"  TAppr.TRANS_LINE_SERV_SUB_TYPE,                                                                                                                                                                     "+
				" (select T5.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T5 where t5.Alpha_tab = TAppr.TRANS_LINE_SERV_SUB_TYPE_AT and T5.ALPHA_SUB_TAB=TAppr.TRANS_LINE_SERV_SUB_TYPE) TRANS_LINE_SERV_SUB_TYPE_DESC, "+
				"  TAppr.TRANS_LINE_SERV_GRP,                                                                                                                                                                          "+
				" (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAppr.TRANS_LINE_SERV_GRP_AT and T1.ALPHA_SUB_TAB=TAppr.TRANS_LINE_SERV_GRP)                                          "+
				"  TRANS_LINE_SERV_GRP_DESC,                                                                                                                                                                           "+
				"  TAppr.EXTRACTION_FREQUENCY,                                                                                                                                                                         "+
				" (select T4.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T4 where t4.Alpha_tab = TAppr.EXTRACTION_FREQUENCY_AT and T4.ALPHA_SUB_TAB=TAppr.EXTRACTION_FREQUENCY)                                        "+
				"  EXTRACTION_FREQUENCY_DESC,                                                                                                                                                                           "+
				"  TAppr.TRANS_LINE_STATUS,T2.NUM_SUBTAB_DESCRIPTION TRANS_LINE_STATUS_DESC,                                                                                                                           "+
				"  TAppr.RECORD_INDICATOR,T3.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                                                             "+
				"  TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                                                                         "+
				"  TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                                                                                 "+
				"  "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION                                                        "+                                       
				"  FROM RA_MST_TRANS_LINE_HEADER TAppr ,NUM_SUB_TAB T2,NUM_SUB_TAB T3                                                                                                                                  "+
				"   Where TAppr.Trans_Line_Type = 'S'                                                                                                                                                                  "+
				"  and t2.NUM_tab = TAppr.TRANS_LINE_STATUS_NT                                                                                                                                                         "+
				"  and t2.NUM_sub_tab = TAppr.TRANS_LINE_STATUS   "+                                                                                                                                                     
				"  and t3.NUM_tab = TAppr.RECORD_INDICATOR_NT    "+                                                                                                                                                      
				"  and t3.NUM_sub_tab = TAppr.RECORD_INDICATOR "+
				" ) TAppr");
		
		strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_TRANS_LINE_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.TRANS_LINE_ID = TPend.TRANS_LINE_ID)");
		
		strBufPending = new StringBuffer("Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,TPend.TRANS_LINE_DESCRIPTION,                                                                                                                 "+
				"  TPend.TRANS_LINE_SERV_SUB_TYPE,                                                                                                                                                                     "+
				" (select T5.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T5 where t5.Alpha_tab = TPend.TRANS_LINE_SERV_SUB_TYPE_AT and T5.ALPHA_SUB_TAB=TPend.TRANS_LINE_SERV_SUB_TYPE) TRANS_LINE_SERV_SUB_TYPE_DESC, "+
				"  TPend.TRANS_LINE_SERV_GRP,                                                                                                                                                                          "+
				" (select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.TRANS_LINE_SERV_GRP_AT and T1.ALPHA_SUB_TAB=TPend.TRANS_LINE_SERV_GRP)                                          "+
				"  TRANS_LINE_SERV_GRP_DESC,                                                                                                                                                                           "+
				"  TPend.EXTRACTION_FREQUENCY,                                                                                                                                                                         "+
				" (select T4.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T4 where t4.Alpha_tab = TPend.EXTRACTION_FREQUENCY_AT and T4.ALPHA_SUB_TAB=TPend.EXTRACTION_FREQUENCY)                                        "+
				" EXTRACTION_FREQUENCY_DESC,                                                                                                                                                                           "+
				"  TPend.TRANS_LINE_STATUS,T2.NUM_SUBTAB_DESCRIPTION TRANS_LINE_STATUS_DESC,                                                                                                                           "+
				"  TPend.RECORD_INDICATOR,T3.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                                                             "+
				"  TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                                                                         "+
				"  TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                                                                                 "+
				"  "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION                                                        "+                                       
				"  FROM RA_MST_TRANS_LINE_HEADER_PEND TPend ,NUM_SUB_TAB T2,NUM_SUB_TAB T3                                                                                                                                  "+
				"   Where TPend.Trans_Line_Type = 'S'                                                                                                                                                                  "+
				"  and t2.NUM_tab = TPend.TRANS_LINE_STATUS_NT                                                                                                                                                         "+
				"  and t2.NUM_sub_tab = TPend.TRANS_LINE_STATUS   "+                                                                                                                                                     
				"  and t3.NUM_tab = TPend.RECORD_INDICATOR_NT    "+                                                                                                                                                      
				"  and t3.NUM_sub_tab = TPend.RECORD_INDICATOR "+
				" ) TPend");
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
					case "transLineId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_ID) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_ID) "+ val, strBufPending, data.getJoinType());
						break;

					case "transLineDescription":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;

					case "transLineServGrpDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_SERV_GRP_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_SERV_GRP_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "extractionFrequencyDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.EXTRACTION_FREQUENCY_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.EXTRACTION_FREQUENCY_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "transLineStatusDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
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
			orderBy=" Order by TRANS_LINE_ID ";
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

	public List<TransLineHeaderVb> getQueryResults(TransLineHeaderVb dObj, int intStatus){
		List<TransLineHeaderVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String("SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,TAppr.TRANS_LINE_DESCRIPTION,TAppr.TRANS_LINE_TYPE, " + 
				" TAppr.TRANS_LINE_SERV_SUB_TYPE,TAppr.TRANS_LINE_SERV_GRP,TAppr.EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY, " + 
				" TARGET_STG_TABLE_ID,TAppr.DEPT_ID,TAppr.TRANS_LINE_STATUS,TAppr.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC," + 
				" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME, " + 
				" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME, " + 
				" TAppr.DATE_LAST_MODIFIED, " + 
				" TAppr.DATE_CREATION FROM RA_MST_TRANS_LINE_HEADER TAppr, NUM_SUB_TAB T1 WHERE " + 
				" TAppr.COUNTRY =? AND TAPPR.LE_BOOK =? AND TAppr.TRANS_LINE_ID = ?"
				+" AND T1.NUM_tab = TAppr.RECORD_INDICATOR_NT and T1.NUM_sub_tab = TAppr.RECORD_INDICATOR");
		strQueryPend = new String("SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,TPend.TRANS_LINE_DESCRIPTION,TPend.TRANS_LINE_TYPE,  " + 
				" TPend.TRANS_LINE_SERV_SUB_TYPE,TPend.TRANS_LINE_SERV_GRP,TPend.EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY, " + 
				" TARGET_STG_TABLE_ID,TPend.DEPT_ID,TPend.TRANS_LINE_STATUS,TPend.RECORD_INDICATOR,T1.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC," + 
				" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME, " + 
				" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME, " + 
				" TPend.DATE_LAST_MODIFIED, " + 
				" TPend.DATE_CREATION FROM RA_MST_TRANS_LINE_HEADER_PEND TPend, NUM_SUB_TAB T1 WHERE " + 
				" TPend.COUNTRY =? AND TPend.LE_BOOK =? AND TPend.TRANS_LINE_ID = ? " +
				" AND T1.NUM_tab = TPend.RECORD_INDICATOR_NT and T1.NUM_sub_tab = TPend.RECORD_INDICATOR");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());// country
		objParams[1] = new String(dObj.getLeBook());
		objParams[2] = new String(dObj.getTransLineId());
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
	protected int deleteTransLineHeaderAppr(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_HEADER where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteTransLineHeaderPend(TransLineHeaderVb vObject){
		String query = "Delete from RA_MST_TRANS_LINE_HEADER_PEND where COUNTRY = ? AND LE_BOOK = ? AND TRANS_LINE_ID = ? ";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int doInsertionApprTransLineHeaders(TransLineHeaderVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_HEADER(COUNTRY,LE_BOOK,TRANS_LINE_ID,TRANS_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				",TRANS_LINE_SERV_GRP,EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY" + 
				",TRANS_LINE_SERV_SUB_TYPE,TARGET_STG_TABLE_ID"+
				",DEPT_ID,TRANS_LINE_STATUS_NT,TRANS_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getTransLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineGrp(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),
				vObject.getTransLineSubType(),vObject.getTargetStgTableId(),vObject.getDepartment(),
				vObject.getTransLineStatusNT(),vObject.getTransLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineHeaders(TransLineHeaderVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,TRANS_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				",TRANS_LINE_SERV_GRP,EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY" + 
				",TRANS_LINE_SERV_SUB_TYPE,TARGET_STG_TABLE_ID"+
				",DEPT_ID,TRANS_LINE_STATUS_NT,TRANS_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getTransLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineGrp(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),
				vObject.getTransLineSubType(),vObject.getTargetStgTableId(),vObject.getDepartment(),
				vObject.getTransLineStatusNT(),vObject.getTransLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendTransLineHeadersDc(TransLineHeaderVb vObject){
		String query =  " Insert Into RA_MST_TRANS_LINE_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,TRANS_LINE_DESCRIPTION,TRANS_LINE_TYPE" + 
				",TRANS_LINE_SERV_GRP,EXTRACTION_FREQUENCY,EXTRACTION_MONTH_DAY" + 
				",TRANS_LINE_SERV_SUB_TYPE,TARGET_STG_TABLE_ID"+
				",DEPT_ID,TRANS_LINE_STATUS_NT,TRANS_LINE_STATUS" + 
				",RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,INTERNAL_STATUS,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+",?)";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getTransLineDescription(),
				vObject.getTransLineType(),vObject.getTransLineGrp(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),
				vObject.getTransLineSubType(),vObject.getTargetStgTableId(),vObject.getDepartment(),
				vObject.getTransLineStatusNT(),vObject.getTransLineStatus(),vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getInternalStatus(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(TransLineHeaderVb vObject){
		String query = " Update RA_MST_TRANS_LINE_HEADER set TRANS_LINE_DESCRIPTION= ?" + 
				",TRANS_LINE_SERV_SUB_TYPE= ?,TRANS_LINE_SERV_GRP= ?" + 
				",EXTRACTION_FREQUENCY= ?,EXTRACTION_MONTH_DAY= ?" + 
				",TARGET_STG_TABLE_ID= ?,DEPT_ID = ?, TRANS_LINE_STATUS= ?" + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= "+commonDao.getDbFunction("SYSDATE")+""+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? ";
		Object[] args = {vObject.getTransLineDescription(),
				vObject.getTransLineSubType(),vObject.getTransLineGrp(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),vObject.getTargetStgTableId(),
				vObject.getDepartment(),vObject.getTransLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdatePendHeader(TransLineHeaderVb vObject){
		String query = " Update RA_MST_TRANS_LINE_HEADER_PEND set TRANS_LINE_DESCRIPTION= ?" + 
				",TRANS_LINE_SERV_SUB_TYPE= ?,TRANS_LINE_SERV_GRP= ?" + 
				",EXTRACTION_FREQUENCY= ?,EXTRACTION_MONTH_DAY= ?" + 
				",TARGET_STG_TABLE_ID= ?,DEPT_ID = ?, TRANS_LINE_STATUS= ?" + 
				",RECORD_INDICATOR= ?,MAKER= ?,VERIFIER= ?,DATE_LAST_MODIFIED= "+commonDao.getDbFunction("SYSDATE")+""+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? ";
		Object[] args = {vObject.getTransLineDescription(),
				vObject.getTransLineSubType(),vObject.getTransLineGrp(),
				vObject.getExtractionFrequency(),vObject.getExtractionMonthDay(),vObject.getTargetStgTableId(),
				vObject.getDepartment(),vObject.getTransLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId()};
		return getJdbcTemplate().update(query,args);
	}
	protected List<TransLineHeaderVb> selectApprovedRecord(TransLineHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<TransLineHeaderVb> doSelectPendingRecord(TransLineHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(TransLineHeaderVb records){return records.getTransLineStatus();}
	@Override
	protected void setStatus(TransLineHeaderVb vObject,int status){vObject.setTransLineStatus(status);
	}
	public ExceptionCode doInsertApprRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException {
		List<TransLineHeaderVb> collTemp = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
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
		vObject.setTransLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		//vObject.setTransLineServGrp("NA");
		vObject.setTransLineType("S");
		retVal = doInsertionApprTransLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
			exceptionCode = transLinesSbuDao.deleteAndInsertApprSbu(vObject);
		}
		
		if(ValidationUtil.isValid(vObject.getChannelId())) {
			exceptionCode = transLinesChannelDao.deleteAndInsertApprChannel(vObject);
		}
		if(vObject.getTransLineGllst() != null && vObject.getTransLineGllst().size() > 0) {
			exceptionCode = transLinesGlDao.deleteAndInsertApprGl(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		exceptionCode = writeAuditLog(vObject, null);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		return exceptionCode;
	}
	public ExceptionCode doInsertRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException {
		List<TransLineHeaderVb> collTemp = null;
		TransLineSbuVb transLineSbuVb = new TransLineSbuVb();
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
		vObject.setTransLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		//vObject.setTransLineServGrp("NA");
		vObject.setTransLineType("S");
		retVal = doInsertionPendTransLineHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
			exceptionCode = transLinesSbuDao.deleteAndInsertPendSbu(vObject);
		}
		if(ValidationUtil.isValid(vObject.getChannelId())) {
			exceptionCode = transLinesChannelDao.deleteAndInsertPendChannel(vObject);
		}
		if(vObject.getTransLineGllst() != null && vObject.getTransLineGllst().size() > 0) {
			exceptionCode = transLinesGlDao.deleteAndInsertPendGl(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode doUpdateApprRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException  {
		List<TransLineHeaderVb> collTemp = null;
		TransLineHeaderVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
		// Even if record is not there in Appr. table reject the record
		if (collTemp.size() == 0){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		vObject.setDateCreation(vObjectlocal.getDateCreation());
		vObject.setTransLineType("S");
		retVal = doUpdateApprHeader(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
			transLinesSbuDao.deleteAndInsertApprSbu(vObject);
		}
		if(ValidationUtil.isValid(vObject.getChannelId())) {
			transLinesChannelDao.deleteAndInsertApprChannel(vObject);
		}
		if(vObject.getTransLineGllst() != null && vObject.getTransLineGllst().size() > 0) {
			exceptionCode = transLinesGlDao.deleteAndInsertApprGl(vObject);
		}
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		/*if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}*/
		return exceptionCode;
	}
	public ExceptionCode doUpdateRecordForNonTrans(TransLineHeaderVb vObject) throws RuntimeCustomException  {
		List<TransLineHeaderVb> collTemp = null;
		TransLineHeaderVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.MODIFY;
		strErrorDesc  = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		vObject.setTransLineType("S");
		collTemp = doSelectPendingRecord(vObject);
		if (collTemp == null){
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if (collTemp.size() > 0){
			vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
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
			if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
				transLinesSbuDao.deleteAndInsertPendSbu(vObject);
			}
			if(ValidationUtil.isValid(vObject.getChannelId())) {
				transLinesChannelDao.deleteAndInsertPendChannel(vObject);
			}
			if(vObject.getTransLineGllst() != null && vObject.getTransLineGllst().size() > 0) {
				exceptionCode = transLinesGlDao.deleteAndInsertPendGl(vObject);
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
				vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    retVal = doInsertionPendTransLineHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(ValidationUtil.isValid(vObject.getBusinessVertical())) {
				transLinesSbuDao.deleteAndInsertPendSbu(vObject);
			}
			if(ValidationUtil.isValid(vObject.getChannelId())) {
				transLinesChannelDao.deleteAndInsertPendChannel(vObject);
			}
			if(vObject.getTransLineGllst() != null && vObject.getTransLineGllst().size() > 0) {
				exceptionCode = transLinesGlDao.deleteAndInsertPendGl(vObject);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doRejectForTransaction(TransLineHeaderVb vObject)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}
	public ExceptionCode doRejectRecord(TransLineHeaderVb vObject)throws RuntimeCustomException {
		TransLineHeaderVb vObjectlocal = null;
		List<TransLineHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
			retVal = deleteTransLineHeaderPend(vObject);
			
			List<TransLineChannelVb> collTempChannel = null;
			collTempChannel = transLinesChannelDao.getTransChannelDetails(vObject, 1);
			if (collTempChannel != null && collTempChannel.size() > 0){
				retVal = transLinesChannelDao.deleteTransLineChannelPend(vObject);
			}
			List<TransLineSbuVb> collTempSbu = null;
			collTempSbu = transLinesSbuDao.getTransSbuDetails(vObject, 1);
			if (collTempSbu != null && collTempSbu.size() > 0){
				retVal = transLinesSbuDao.deleteTransLineSbuPend(vObject);
			}
			List<TransLineGLVb> collTempGl = null;
			collTempGl = transLinesGlDao.getTransGLDetails(vObject, 1);
			if (collTempGl != null && collTempGl.size() > 0){
				retVal = transLinesGlDao.deleteTransLineGlPend(vObject);
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
	public ExceptionCode doApproveForTransaction(TransLineHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doApproveRecord(vObject,staticDelete);
	}
	public ExceptionCode doApproveRecord(TransLineHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		TransLineHeaderVb oldContents = null;
		TransLineSbuVb oldContentsSbu = null;
		TransLineChannelVb oldContentsChannel = null;
		TransLineGLVb oldContentsGl = null;
		
		TransLineHeaderVb vObjectlocal = null;
		TransLineSbuVb vObjectSbulocal = null;
		TransLineChannelVb vObjectChannellocal = null;
		TransLineGLVb vObjectGllocal = null;

		
		List<TransLineHeaderVb> collTemp = null;
		List<TransLineSbuVb> collTempSbu = null;
		List<TransLineSbuVb> collTempSbuAppr = null;
		List<TransLineChannelVb> collTempChannel = null;
		List<TransLineChannelVb> collTempChannelAppr = null;
		List<TransLineGLVb> collTempGl = null;
		List<TransLineGLVb> collTempGlAppr = null;
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

			vObjectlocal = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()){
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			collTempChannel = transLinesChannelDao.getTransChannelDetails(vObjectlocal,1);
			if(collTempChannel != null && collTempChannel.size() > 0) {
				vObjectChannellocal = ((ArrayList<TransLineChannelVb>)collTempChannel).get(0);
			}
			collTempSbu = transLinesSbuDao.getTransSbuDetails(vObjectlocal,1); // SBU
			if(collTempSbu != null && collTempSbu.size() > 0) {
				vObjectSbulocal = ((ArrayList<TransLineSbuVb>)collTempSbu).get(0);
			}
			collTempGl = transLinesGlDao.getTransGLDetails(vObjectlocal,1);
			if(collTempGl != null && collTempGl.size() > 0) {
				vObjectGllocal = ((ArrayList<TransLineGLVb>)collTempGl).get(0);
			}
			
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT){
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()){
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<TransLineHeaderVb>)collTemp).get(0);
				
				collTempChannelAppr = transLinesChannelDao.getTransChannelDetails(vObjectlocal,0);
				if(collTempChannelAppr != null && collTempChannelAppr.size() > 0) {
					oldContentsChannel = ((ArrayList<TransLineChannelVb>)collTempChannelAppr).get(0);
				}
				collTempSbuAppr = transLinesSbuDao.getTransSbuDetails(vObjectlocal,0);
				if(collTempSbuAppr != null && collTempSbuAppr.size() > 0) {
					oldContentsSbu = ((ArrayList<TransLineSbuVb>)collTempSbuAppr).get(0);
				}
				collTempGlAppr = transLinesGlDao.getTransGLDetails(vObjectlocal,0);
				if(collTempGlAppr != null && collTempGlAppr.size() > 0) {
					oldContentsGl = ((ArrayList<TransLineGLVb>)collTempGlAppr).get(0);
				}
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){  // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionApprTransLineHeaders(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if(collTempSbuAppr != null && collTempSbuAppr.size() > 0) {
					transLinesSbuDao.deleteTransLineSbuAppr(vObjectlocal);
				}
				if(collTempSbu != null && !collTempSbu.isEmpty()) {
					collTempSbu.forEach(sbuPend -> {
						retVal = transLinesSbuDao.doInsertionApprTransLineSBU(sbuPend);
					});
				}
				if(collTempChannelAppr != null && collTempChannelAppr.size() > 0) {
					transLinesChannelDao.deleteTransLineChannelAppr(vObjectlocal);
				}
				if(collTempChannel != null && !collTempChannel.isEmpty()) {
					collTempChannel.forEach(channelPend -> {
						retVal = transLinesChannelDao.doInsertionApprTransLineChannel(channelPend);
					});
				}
				if(collTempGlAppr != null && collTempGlAppr.size() > 0) {
					transLinesGlDao.deleteTransLineGlAppr(vObjectlocal);
				}
				if(collTempGl != null && !collTempGl.isEmpty()) {
					collTempGl.forEach(glPend -> {
						retVal = transLinesGlDao.doInsertionApprTransLineGL(glPend);
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
				if(collTempSbuAppr != null && collTempSbuAppr.size() > 0) {
					transLinesSbuDao.deleteTransLineSbuAppr(vObjectlocal);
				}
				if(collTempSbu != null && !collTempSbu.isEmpty()) {
					collTempSbu.forEach(sbuPend -> {
						retVal = transLinesSbuDao.doInsertionApprTransLineSBU(sbuPend);
					});
				}
				if(collTempChannelAppr != null && collTempChannelAppr.size() > 0) {
					transLinesChannelDao.deleteTransLineChannelAppr(vObjectlocal);
				}
				if(collTempChannel != null && !collTempChannel.isEmpty()) {
					collTempChannel.forEach(channelPend -> {
						retVal = transLinesChannelDao.doInsertionApprTransLineChannel(channelPend);
					});
				}
				if(collTempGlAppr != null && collTempGlAppr.size() > 0) {
					transLinesGlDao.deleteTransLineGlAppr(vObjectlocal);
				}
				if(collTempGl != null && !collTempGl.isEmpty()) {
					collTempGl.forEach(glPend -> {
						retVal = transLinesGlDao.doInsertionApprTransLineGL(glPend);
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
			retVal = deleteTransLineHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = transLinesSbuDao.deleteTransLineSbuPend(vObjectlocal);
			retVal = transLinesChannelDao.deleteTransLineChannelPend(vObjectlocal);
			retVal = transLinesGlDao.deleteTransLineGlPend(vObjectlocal);
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
	protected String getAuditString(TransLineHeaderVb vObject){
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
			
			if(ValidationUtil.isValid(vObject.getTransLineDescription()))
				strAudit.append("TRANS_LINE_DESCRIPTION"+auditDelimiterColVal+vObject.getTransLineDescription().trim());
			else
				strAudit.append("TRANS_LINE_DESCRIPTION"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
				
			strAudit.append("TRANS_LINE_SERV_SUB_TYPE"+auditDelimiterColVal+vObject.getTransLineSubType());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_SERV_GRP"+auditDelimiterColVal+vObject.getTransLineGrp());
			strAudit.append(auditDelimiter);
				
			strAudit.append("EXTRACTION_FREQUENCY"+auditDelimiterColVal+vObject.getExtractionFrequency());
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("EXTRACTION_MONTH_DAY"+auditDelimiterColVal+vObject.getExtractionMonthDay().trim());
			else
				strAudit.append("EXTRACTION_MONTH_DAY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
				
			strAudit.append("TARGET_STG_TABLE_ID"+auditDelimiterColVal+vObject.getTargetStgTableId());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_STATUS_NT"+auditDelimiterColVal+vObject.getTransLineStatusNT());
			strAudit.append(auditDelimiter);
			
			strAudit.append("TRANS_LINE_STATUS"+auditDelimiterColVal+vObject.getTransLineStatus());
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