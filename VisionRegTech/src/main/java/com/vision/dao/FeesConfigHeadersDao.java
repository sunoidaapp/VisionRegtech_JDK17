package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.vision.vb.FeesConfigDetailsVb;
import com.vision.vb.FeesConfigHeaderVb;
import com.vision.vb.FeesConfigTierVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

@Component
public class FeesConfigHeadersDao extends AbstractDao<FeesConfigHeaderVb> {
	@Autowired
	CommonDao commonDao;
	@Autowired
	FeesConfigDetailsDao feesConfigDetailsDao;
	@Autowired
	FeesConfigTierDao feesConfigTierDao;
	@Value("${app.databaseType}")
	private String databaseType;
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FeesConfigHeaderVb vObject = new FeesConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineIdDesc(rs.getString("TRANS_LINE_ID")+" - "+rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineIdDesc(rs.getString("BUSINESS_LINE_ID")+" - "+rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setFeeConfigType(rs.getString("FEE_CONFIG_TYPE"));
				vObject.setFeeConfigTypeDesc(rs.getString("FEE_CONF_TYPE_DESC"));
				vObject.setFeeConfigCode(rs.getString("FEE_CONFIG_CODE"));
				vObject.setFeeConfigCodeDesc(rs.getString("FEE_CONFIG_CODE_DESC"));
				vObject.setContractId(rs.getString("CONTRACT_ID"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setBusinessLineStatus (rs.getInt("BUSINESS_LINE_STATUS"));
				vObject.setBusinessLineStatusDesc(rs.getString("BUSINESS_LINE_STATUS_Desc"));
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
				FeesConfigHeaderVb vObject = new FeesConfigHeaderVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTransLineId(rs.getString("TRANS_LINE_ID"));
				vObject.setTransLineIdDesc(rs.getString("TRANS_LINE_DESCRIPTION"));
				vObject.setTranLineType(rs.getString("TRANS_LINE_TYPE"));
				vObject.setTranLineGrp(rs.getString("TRAN_LINE_GRP"));
				vObject.setTranLineGrpDesc(rs.getString("TRAN_LINE_GRP_DESC"));
				vObject.setBusinessLineId(rs.getString("BUSINESS_LINE_ID"));
				vObject.setBusinessLineIdDesc(rs.getString("BUSINESS_LINE_DESCRIPTION"));
				vObject.setFeeConfigType(rs.getString("FEE_CONFIG_TYPE"));
				vObject.setFeeConfigCode(rs.getString("FEE_CONFIG_CODE"));
				vObject.setFeeConfigCodeDesc(rs.getString("FEE_CONFIG_CODE_DESC"));
				vObject.setContractId(rs.getString("CONTRACT_ID"));
				vObject.setContractIdDesc(rs.getString("CONTRACT_ID_DESC"));
				vObject.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
				vObject.setBusinessLineStatus (rs.getInt("BUSINESS_LINE_STATUS"));
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
	public List<FeesConfigHeaderVb> getQueryPopupResults(FeesConfigHeaderVb dObj) {
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = null;
		StringBuffer strBufPending = null;
		String strWhereNotExists = null;
		String orderBy = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer(" Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,T5.TRANS_LINE_DESCRIPTION,                                                      "+
					"                  TAppr.BUSINESS_LINE_ID,T6.BUSINESS_LINE_DESCRIPTION ,TAPPR.FEE_CONFIG_TYPE,                                                             "+
					"     			(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_CONFIG_TYPE_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_CONFIG_TYPE)	 "+
					" 				 FEE_CONF_TYPE_DESC,                                                                                                 "+
					" 				 TAPPR.FEE_CONFIG_CODE,                                                                                                                          "+
					" 				 Case when TAPPR.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'                                                                                      "+
					" 					  when TAPPR.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION                                                                        "+
					" 						FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TAPPR.FEE_CONFIG_CODE)                                                             "+
					" 					WHEN TAPPR.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION                                                                                   "+
					" 						FROM OUC_CODES WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND VISION_OUC = TAPPR.FEE_CONFIG_CODE)                                "+
					" 					WHEN TAPPR.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME                                                                                     "+
					" 						FROM CUSTOMERS_DLY WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.FEE_CONFIG_CODE)    "+
					" 					WHEN TAPPR.FEE_CONFIG_TYPE= 'A' THEN (SELECT CUSTOMER_NAME "+                                                                                    
					" 						FROM CUSTOMERS_DLY WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.FEE_CONFIG_CODE)  "+                       
					" 					END FEE_CONFIG_CODE_DESC,                                                                                                                      "+
					" 				 TAPPR.CONTRACT_ID, "+
					" 				TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE, "+
					" 				 TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,                                                                 "+
					"                  TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                 "+
					"                  TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                             "+
					"                  TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                     "+
					"                  "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					"                  FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4,                                                               "+
					" 				 RA_MST_TRANS_LINE_HEADER T5 ,RA_MST_BUSINESS_LINE_HEADER T6                                                                                          "+
					"                   Where                                                                                                                                       "+
					"                  t3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT                                                                                               "+
					"                  and t3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS                                                                                              "+
					"                  and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT                                                                                                   "+
					"                  and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR                                                                                                  "+
					" 				 AND TAppr.TRANS_LINE_ID = T5.TRANS_LINE_ID                                                                                                           "+
					" 				 AND TAppr.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID                                                                                                     "+
					" 				 ) TAppr                                                                                                                                              ");
			
			strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_FEES_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
							+ " AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.TRANS_LINE_ID = TPend.TRANS_LINE_ID AND TAPPR.BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID"
							+ " AND TAPPR.FEE_CONFIG_TYPE = TPEND.FEE_CONFIG_TYPE AND TAPPR.FEE_CONFIG_CODE = TPEND.FEE_CONFIG_CODE AND TAPPR.CONTRACT_ID = TPEND.CONTRACT_ID"
							+ " AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE)");
			
			strBufPending = new StringBuffer(" Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,T5.TRANS_LINE_DESCRIPTION,                                                      "+
					"                  TPend.BUSINESS_LINE_ID,T6.business_line_description ,TPend.FEE_CONFIG_TYPE,                                                             "+
					" 				(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_CONFIG_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.FEE_CONFIG_TYPE)	"+
					" 				 FEE_CONF_TYPE_DESC,                                                                                                 "+
					" 				 TPend.FEE_CONFIG_CODE,                                                                                                                          "+
					" 				 Case when TPend.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'                                                                                      "+
					" 					  when TPend.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION                                                                        "+
					" 						FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TPend.FEE_CONFIG_CODE)                                                             "+
					" 					WHEN TPend.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION                                                                                   "+
					" 						FROM OUC_CODES WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND VISION_OUC = TPend.FEE_CONFIG_CODE)                                "+
					" 					WHEN TPend.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME                                                                                     "+
					" 						FROM CUSTOMERS_DLY WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)                           "+
					" 					WHEN TPend.FEE_CONFIG_TYPE= 'A' THEN (SELECT CUSTOMER_NAME "+                                                                                    
					" 						FROM CUSTOMERS_DLY WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)  "+
					" 					END FEE_CONFIG_CODE_DESC,                                                                                                                      "+
					" 				 TPend.CONTRACT_ID,  "+
					" 				TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,,                                                                                                                           "+
					"				TPend.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC,                                                                 "+
					"                  TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                 "+
					"                  TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                             "+
					"                  TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                     "+
					"                  "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					"                  FROM RA_MST_FEES_HEADER_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4,                                                               "+
					" 				 RA_MST_TRANS_LINE_HEADER T5 ,RA_MST_BUSINESS_LINE_HEADER T6                                                                                          "+
					"                   Where                                                                                                                                       "+
					"                  t3.NUM_tab = TPend.BUSINESS_LINE_STATUS_NT                                                                                               "+
					"                  and t3.NUM_sub_tab = TPend.BUSINESS_LINE_STATUS                                                                                              "+
					"                  and t4.NUM_tab = TPend.RECORD_INDICATOR_NT                                                                                                   "+
					"                  and t4.NUM_sub_tab = TPend.RECORD_INDICATOR                                                                                                  "+
					" 				 AND TPend.TRANS_LINE_ID = T5.TRANS_LINE_ID                                                                                                           "+
					" 				 AND TPend.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID                                                                                                     "+
					" 				 ) TPend ");
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer(
					"Select * from ( SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,T5.TRANS_LINE_DESCRIPTION,      "
							+ "  TAppr.BUSINESS_LINE_ID,T6.BUSINESS_LINE_DESCRIPTION ,TAPPR.FEE_CONFIG_TYPE,  "
							+ "	(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TAPPR.FEE_CONFIG_TYPE_AT and T1.ALPHA_SUB_TAB=TAPPR.FEE_CONFIG_TYPE)									   "
							+ "	 FEE_CONF_TYPE_DESC,                                                                                                  "
							+ "	 TAPPR.FEE_CONFIG_CODE,                                                                                                  "
							+ "	 Case when TAPPR.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'                                                                 "
							+ "		  when TAPPR.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION                                                    "
							+ "			FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TAPPR.FEE_CONFIG_CODE)                                       "
							+ "		WHEN TAPPR.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION                                                                 "
							+ "			FROM OUC_CODES WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND VISION_OUC = TAPPR.FEE_CONFIG_CODE)             "
							+ "		WHEN TAPPR.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME                                                                   "
							+ "			FROM CUSTOMERS_DLY WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.FEE_CONFIG_CODE)    "
							+ "		WHEN TAPPR.FEE_CONFIG_TYPE= 'A' THEN (SELECT CUSTOMER_NAME                                                                   "
							+ "			FROM CUSTOMERS_DLY WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.FEE_CONFIG_CODE)          "
							+ "		END FEE_CONFIG_CODE_DESC,                                                                                                       "
							+ "	 TAPPR.CONTRACT_ID,  "
							+ "	format(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE, "
							+ "	 TAppr.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC, "
							+ "      TAppr.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,            "
							+ "      TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.MAKER,0) ) MAKER_NAME,                              "
							+ "      TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TAppr.VERIFIER,0) ) VERIFIER_NAME,                      "
							+ "      Format(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED ,Format(TAppr.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION "
							+ "      FROM RA_MST_FEES_HEADER TAppr ,NUM_SUB_TAB T3,NUM_SUB_TAB T4,     "
							+ "	 RA_MST_TRANS_LINE_HEADER T5 ,RA_MST_BUSINESS_LINE_HEADER T6          "
							+ "       Where  " + "	 t3.NUM_tab = TAppr.BUSINESS_LINE_STATUS_NT  "
							+ "      and t3.NUM_sub_tab = TAppr.BUSINESS_LINE_STATUS  "
							+ "      and t4.NUM_tab = TAppr.RECORD_INDICATOR_NT  "
							+ "      and t4.NUM_sub_tab = TAppr.RECORD_INDICATOR    "
							+ "	 AND TAppr.TRANS_LINE_ID = T5.TRANS_LINE_ID        "
							+ "	 AND TAppr.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID "
							+ "	 ) TAppr                                                                                                                                              ");
		
		strWhereNotExists = new String(" Not Exists (Select 'X' From RA_MST_FEES_HEADER_PEND TPEND WHERE TAppr.COUNTRY = TPend.COUNTRY"
						+ " AND TAppr.LE_BOOK = TPend.LE_BOOK AND TAppr.TRANS_LINE_ID = TPend.TRANS_LINE_ID AND TAPPR.BUSINESS_LINE_ID = TPEND.BUSINESS_LINE_ID"
						+ " AND TAPPR.FEE_CONFIG_TYPE = TPEND.FEE_CONFIG_TYPE AND TAPPR.FEE_CONFIG_CODE = TPEND.FEE_CONFIG_CODE AND TAPPR.CONTRACT_ID = TPEND.CONTRACT_ID"
						+ " AND TAPPR.EFFECTIVE_DATE = TPEND.EFFECTIVE_DATE)");
		
		strBufPending = new StringBuffer(
				"Select * from ( SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,T5.TRANS_LINE_DESCRIPTION,      "
						+ "  TPend.BUSINESS_LINE_ID,T6.BUSINESS_LINE_DESCRIPTION ,TPend.FEE_CONFIG_TYPE,  "
						+ "	(select T1.ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB T1 where t1.Alpha_tab = TPend.FEE_CONFIG_TYPE_AT and T1.ALPHA_SUB_TAB=TPend.FEE_CONFIG_TYPE)									   "
						+ "	 FEE_CONF_TYPE_DESC,                                                                                                  "
						+ "	 TPend.FEE_CONFIG_CODE,                                                                                                  "
						+ "	 Case when TPend.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'                                                                 "
						+ "		  when TPend.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION                                                    "
						+ "			FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TPend.FEE_CONFIG_CODE)                                       "
						+ "		WHEN TPend.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION                                                                 "
						+ "			FROM OUC_CODES WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND VISION_OUC = TPend.FEE_CONFIG_CODE)             "
						+ "		WHEN TPend.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME                                                                   "
						+ "			FROM CUSTOMERS_DLY WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)    "
						+ "		WHEN TPend.FEE_CONFIG_TYPE= 'A' THEN (SELECT CUSTOMER_NAME                                                                   "
						+ "			FROM CUSTOMERS_DLY WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)          "
						+ "		END FEE_CONFIG_CODE_DESC,                                                                                                       "
						+ "	 TPend.CONTRACT_ID,  "
						+ "	format(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE, "
						+ "	 TPend.BUSINESS_LINE_STATUS,T3.NUM_SUBTAB_DESCRIPTION BUSINESS_LINE_STATUS_DESC, "
						+ "      TPend.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,            "
						+ "      TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.MAKER,0) ) MAKER_NAME,                              "
						+ "      TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = ISNULL(TPend.VERIFIER,0) ) VERIFIER_NAME,                      "
						+ "      Format(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy HH:mm:ss') DATE_LAST_MODIFIED ,Format(TPend.DATE_CREATION, 'dd-MM-yyyy HH:mm:ss') DATE_CREATION "
						+ "      FROM RA_MST_FEES_HEADER_PEND TPend ,NUM_SUB_TAB T3,NUM_SUB_TAB T4,     "
						+ "	 RA_MST_TRANS_LINE_HEADER T5 ,RA_MST_BUSINESS_LINE_HEADER T6          "
						+ "       Where  " + "	 t3.NUM_tab = TPend.BUSINESS_LINE_STATUS_NT  "
						+ "      and t3.NUM_sub_tab = TPend.BUSINESS_LINE_STATUS  "
						+ "      and t4.NUM_tab = TPend.RECORD_INDICATOR_NT  "
						+ "      and t4.NUM_sub_tab = TPend.RECORD_INDICATOR    "
						+ "	 AND TPend.TRANS_LINE_ID = T5.TRANS_LINE_ID        "
						+ "	 AND TPend.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID "
						+ "	 ) TPend  ");
		}
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
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.COUNTRY) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.LE_BOOK) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "transLineIdDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.TRANS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.TRANS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;

					case "businessLineIdDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.BUSINESS_LINE_DESCRIPTION) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.BUSINESS_LINE_DESCRIPTION) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "feeConfigTypeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_CONF_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_CONF_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "feeConfigCodeDesc":
						CommonUtils.addToQuerySearch(" upper(TAPPR.FEE_CONFIG_CODE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.FEE_CONFIG_CODE_DESC) "+ val, strBufPending, data.getJoinType());
						break;
						
					case "effectiveDate":
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TAPPR.EFFECTIVE_DATE 'DD-MMM-YYYY') "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.EFFECTIVE_DATE 'DD-MMM-YYYY') "+ val, strBufPending, data.getJoinType());
						break;
						
					case "contractId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CONTRACT_ID) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.CONTRACT_ID) "+ val, strBufPending, data.getJoinType());
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
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"')  " + val, strBufPending, data.getJoinType());
						break;
									
					case "dateLastModified":
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TAPPR.DATE_LAST_MODIFIED,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"') "  + val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED,'DD-MM-YYYY "+commonDao.getDbFunction("TIME")+"') "  + val, strBufPending, data.getJoinType());
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
			orderBy=" Order by TRANS_LINE_DESCRIPTION ASC,BUSINESS_LINE_DESCRIPTION ASC,FEE_CONFIG_TYPE ASC,EFFECTIVE_DATE DESC ";
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
	public List<FeesConfigHeaderVb> getQueryResults(FeesConfigHeaderVb dObj, int intStatus){
		List<FeesConfigHeaderVb> collTemp = null;
		setServiceDefaults();
		String strQueryAppr = null;
		String strQueryPend = null;
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,T5.TRANS_LINE_TYPE, T5.TRANS_LINE_DESCRIPTION,																																 "+
					" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN T5.TRANS_LINE_PROD_GRP ELSE T5.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP,                                  "+
					" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN                                                                                                        "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_PROD_GRP)    "+
					" ELSE                                                                                                                                         "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_SERV_GRP)    "+
					" END TRAN_LINE_GRP_DESC,                                                                                                                      "+
					" TAppr.BUSINESS_LINE_ID, T6.BUSINESS_LINE_DESCRIPTION,"+
					" TAPPR.FEE_CONFIG_TYPE,                                                                                                "+
					" TAPPR.FEE_CONFIG_CODE, "+
					" Case when TAPPR.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'    "+                                                                                  
					" when TAPPR.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION  "+                                                                      
					" FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TAPPR.FEE_CONFIG_CODE)  "+                                                           
					" WHEN TAPPR.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION  "+                                                                                
					" FROM OUC_CODES WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND VISION_OUC = TAPPR.FEE_CONFIG_CODE) "+                               
					" WHEN TAPPR.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME   "+                                                                                  
					" FROM CUSTOMERS_DLY WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.FEE_CONFIG_CODE)  "+                       
					" END FEE_CONFIG_CODE_DESC,"+
					" TAPPR.CONTRACT_ID,"+
					" (SELECT ACCOUNT_NAME FROM ACCOUNTS_DLY WHERE ACCOUNT_NO=TAPPR.CONTRACT_ID AND COUNTRY= TAppr.COUNTRY AND LE_BOOK=TAppr.LE_BOOK) CONTRACT_ID_DESC,"+
					" TO_CHAR(TAPPR.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,"+
					" TAppr.BUSINESS_LINE_STATUS,TAppr.RECORD_INDICATOR, T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                         "+
					" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
					" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER TAppr ,RA_MST_TRANS_LINE_HEADER T5 , ra_mst_business_line_header T6 ,NUM_SUB_TAB T7                                                                                 "+
					" Where  TAppr.TRANS_LINE_ID = T5.TRANS_LINE_ID  "+
					" AND TAppr.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID "+
					" AND TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND "+
					" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? AND"+
					" T7.NUM_tab = TAppr.RECORD_INDICATOR_NT" + 
					" and T7.NUM_sub_tab = TAppr.RECORD_INDICATOR");
			strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,T5.TRANS_LINE_TYPE, T5.TRANS_LINE_DESCRIPTION,																																 "+
					" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN T5.TRANS_LINE_PROD_GRP ELSE T5.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP,                                  "+
					" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN                                                                                                        "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_PROD_GRP)    "+
					" ELSE                                                                                                                                         "+
					" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_SERV_GRP)    "+
					" END TRAN_LINE_GRP_DESC,                                                                                                                      "+
					" TPend.BUSINESS_LINE_ID, T6.BUSINESS_LINE_DESCRIPTION,"+
					" TPend.FEE_CONFIG_TYPE,                                                                                                "+
					" TPend.FEE_CONFIG_CODE, "+
					" Case when TPend.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'    "+                                                                                  
					" when TPend.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION  "+                                                                      
					" FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TPend.FEE_CONFIG_CODE)  "+                                                           
					" WHEN TPend.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION  "+                                                                                
					" FROM OUC_CODES WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND VISION_OUC = TPend.FEE_CONFIG_CODE) "+                               
					" WHEN TPend.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME   "+                                                                                  
					" FROM CUSTOMERS_DLY WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)  "+                       
					" END FEE_CONFIG_CODE_DESC,"+
					" TPend.CONTRACT_ID,"+
					" (SELECT ACCOUNT_NAME FROM ACCOUNTS_DLY WHERE ACCOUNT_NO=TPend.CONTRACT_ID AND COUNTRY= TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK) CONTRACT_ID_DESC,"+
					" TO_CHAR(TPend.EFFECTIVE_DATE,'DD-Mon-RRRR') EFFECTIVE_DATE,"+
					" TPend.BUSINESS_LINE_STATUS,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
					" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
					" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
					" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
					" FROM RA_MST_FEES_HEADER_PEND TPend ,RA_MST_TRANS_LINE_HEADER T5 , ra_mst_business_line_header T6 , NUM_SUB_TAB T7                                                                                 "+
					" Where  TPend.TRANS_LINE_ID = T5.TRANS_LINE_ID  "+
					" AND TPend.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID "+
					" AND TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND  TPend.TRANS_LINE_ID = ? AND "+
					" TPend.BUSINESS_LINE_ID = ? AND TPend.FEE_CONFIG_TYPE = ? AND  TPend.FEE_CONFIG_CODE = ? AND TPend.CONTRACT_ID = ? AND TPend.EFFECTIVE_DATE = ? AND"+
					" T7.NUM_tab = TPend.RECORD_INDICATOR_NT" + 
					" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strQueryAppr = new String(" SELECT TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.TRANS_LINE_ID,T5.TRANS_LINE_TYPE, T5.TRANS_LINE_DESCRIPTION,																																 "+
				" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN T5.TRANS_LINE_PROD_GRP ELSE T5.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP,                                  "+
				" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN                                                                                                        "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_PROD_GRP)    "+
				" ELSE                                                                                                                                         "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_SERV_GRP)    "+
				" END TRAN_LINE_GRP_DESC,                                                                                                                      "+
				" TAppr.BUSINESS_LINE_ID, T6.BUSINESS_LINE_DESCRIPTION,"+
				" TAPPR.FEE_CONFIG_TYPE,                                                                                                "+
				" TAPPR.FEE_CONFIG_CODE, "+
				" Case when TAPPR.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'    "+                                                                                  
				" when TAPPR.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION  "+                                                                      
				" FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TAPPR.FEE_CONFIG_CODE)  "+                                                           
				" WHEN TAPPR.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION  "+                                                                                
				" FROM OUC_CODES WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND VISION_OUC = TAPPR.FEE_CONFIG_CODE) "+                               
				" WHEN TAPPR.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME   "+                                                                                  
				" FROM CUSTOMERS_DLY WHERE COUNTRY =TAPPR.COUNTRY AND LE_BOOK=TAPPR.LE_BOOK AND CUSTOMER_ID = TAPPR.FEE_CONFIG_CODE)  "+                       
				" END FEE_CONFIG_CODE_DESC,"+
				" TAPPR.CONTRACT_ID,"+
				" (SELECT ACCOUNT_NAME FROM ACCOUNTS_DLY WHERE ACCOUNT_NO=TAPPR.CONTRACT_ID AND COUNTRY= TAppr.COUNTRY AND LE_BOOK=TAppr.LE_BOOK) CONTRACT_ID_DESC,"+
				" "+commonDao.getDbFunction("DATEFUNC")+"(CAST(TAppr.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"+
				" TAppr.BUSINESS_LINE_STATUS,TAppr.RECORD_INDICATOR, T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                         "+
				" TAppr. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,                                  "+
				" TAppr.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TAppr.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER TAppr ,RA_MST_TRANS_LINE_HEADER T5 , ra_mst_business_line_header T6 ,NUM_SUB_TAB T7                                                                                 "+
				" Where  TAppr.TRANS_LINE_ID = T5.TRANS_LINE_ID  "+
				" AND TAppr.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID "+
				" AND TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND "+
				" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? AND"+
				" T7.NUM_tab = TAppr.RECORD_INDICATOR_NT" + 
				" and T7.NUM_sub_tab = TAppr.RECORD_INDICATOR");
		strQueryPend = new String(" SELECT TPend.COUNTRY, TPend.LE_BOOK, TPend.TRANS_LINE_ID,T5.TRANS_LINE_TYPE, T5.TRANS_LINE_DESCRIPTION,																																 "+
				" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN T5.TRANS_LINE_PROD_GRP ELSE T5.TRANS_LINE_SERV_GRP END TRAN_LINE_GRP,                                  "+
				" CASE WHEN T5.TRANS_LINE_TYPE='P' THEN                                                                                                        "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_PROD_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_PROD_GRP)    "+
				" ELSE                                                                                                                                         "+
				" (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =T5.TRANS_LINE_SERV_GRP_AT AND ALPHA_SUB_TAB= T5.TRANS_LINE_SERV_GRP)    "+
				" END TRAN_LINE_GRP_DESC,                                                                                                                      "+
				" TPend.BUSINESS_LINE_ID, T6.BUSINESS_LINE_DESCRIPTION,"+
				" TPend.FEE_CONFIG_TYPE,                                                                                                "+
				" TPend.FEE_CONFIG_CODE, "+
				" Case when TPend.FEE_CONFIG_TYPE= 'D' then 'Not Applicable'    "+                                                                                  
				" when TPend.FEE_CONFIG_TYPE= 'S' THEN (SELECT ALPHA_SUBTAB_DESCRIPTION  "+                                                                      
				" FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 3 AND ALPHA_SUB_TAB =TPend.FEE_CONFIG_CODE)  "+                                                           
				" WHEN TPend.FEE_CONFIG_TYPE= 'B' THEN (SELECT OUC_DESCRIPTION  "+                                                                                
				" FROM OUC_CODES WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND VISION_OUC = TPend.FEE_CONFIG_CODE) "+                               
				" WHEN TPend.FEE_CONFIG_TYPE= 'C' THEN (SELECT CUSTOMER_NAME   "+                                                                                  
				" FROM CUSTOMERS_DLY WHERE COUNTRY =TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK AND CUSTOMER_ID = TPend.FEE_CONFIG_CODE)  "+                       
				" END FEE_CONFIG_CODE_DESC,"+
				" TPend.CONTRACT_ID,"+
				" (SELECT ACCOUNT_NAME FROM ACCOUNTS_DLY WHERE ACCOUNT_NO=TPend.CONTRACT_ID AND COUNTRY= TPend.COUNTRY AND LE_BOOK=TPend.LE_BOOK) CONTRACT_ID_DESC,"+
				" "+commonDao.getDbFunction("DATEFUNC")+"(CAST(TPend.EFFECTIVE_DATE AS DATETIME), 'dd-MMM-yyyy') EFFECTIVE_DATE,"+
				" TPend.BUSINESS_LINE_STATUS,TPend.RECORD_INDICATOR,T7.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,                                                                                           "+
				" TPend. MAKER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.MAKER,0) ) MAKER_NAME,                                  "+
				" TPend.VERIFIER,(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+commonDao.getDbFunction("NVL")+"(TPend.VERIFIER,0) ) VERIFIER_NAME,                          "+
				" "+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_LAST_MODIFIED, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_LAST_MODIFIED ,"+commonDao.getDbFunction("DATEFUNC")+"(TPend.DATE_CREATION, 'dd-MM-yyyy "+commonDao.getDbFunction("TIME")+"') DATE_CREATION "+                                                                                               
				" FROM RA_MST_FEES_HEADER_PEND TPend ,RA_MST_TRANS_LINE_HEADER T5 , ra_mst_business_line_header T6 , NUM_SUB_TAB T7                                                                                 "+
				" Where  TPend.TRANS_LINE_ID = T5.TRANS_LINE_ID  "+
				" AND TPend.BUSINESS_LINE_ID = T6.BUSINESS_LINE_ID "+
				" AND TPend.COUNTRY = ? AND TPend.LE_BOOK = ? AND  TPend.TRANS_LINE_ID = ? AND "+
				" TPend.BUSINESS_LINE_ID = ? AND TPend.FEE_CONFIG_TYPE = ? AND  TPend.FEE_CONFIG_CODE = ? AND TPend.CONTRACT_ID = ? AND TPend.EFFECTIVE_DATE = ? AND"+
				" T7.NUM_tab = TPend.RECORD_INDICATOR_NT" + 
				" and T7.NUM_sub_tab = TPend.RECORD_INDICATOR");
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
	@Override
	protected void setServiceDefaults() {
		serviceName = "FeesConfigHeader";
		serviceDesc = "Fees Config Header";
		tableName = "RA_MST_FEES_HEADER";
		childTableName = "RA_MST_FEES_HEADER";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	protected int doInsertionApprFeesHeaders(FeesConfigHeaderVb vObject){
		String query =  " Insert Into RA_MST_FEES_HEADER(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE, BUSINESS_LINE_STATUS_NT, BUSINESS_LINE_STATUS," + 
				"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getBusinessLineStatusNt(),vObject.getBusinessLineStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesHeaders(FeesConfigHeaderVb vObject){
		String query =  " Insert Into RA_MST_FEES_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE, BUSINESS_LINE_STATUS_NT, BUSINESS_LINE_STATUS," + 
				"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+","+commonDao.getDbFunction("SYSDATE")+")";
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getBusinessLineStatusNt(),vObject.getBusinessLineStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doInsertionPendFeesHeadersDc(FeesConfigHeaderVb vObject){
		String query = "";
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			query =  " Insert Into RA_MST_FEES_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
					"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE, BUSINESS_LINE_STATUS_NT, BUSINESS_LINE_STATUS," + 
					"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
					" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+", To_Date(?, 'DD-MM-YYYY HH24:MI:SS'))";
		}else if ("MSSQL".equalsIgnoreCase(databaseType)) {
		query =  " Insert Into RA_MST_FEES_HEADER_PEND(COUNTRY,LE_BOOK,TRANS_LINE_ID,BUSINESS_LINE_ID,FEE_CONFIG_TYPE," + 
				"FEE_CONFIG_CODE, CONTRACT_ID, EFFECTIVE_DATE, BUSINESS_LINE_STATUS_NT, BUSINESS_LINE_STATUS," + 
				"RECORD_INDICATOR_NT,RECORD_INDICATOR,MAKER,VERIFIER,DATE_LAST_MODIFIED,DATE_CREATION) "+
				" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+commonDao.getDbFunction("SYSDATE")+", CONVERT(datetime, ?, 103))";
		}
		
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate(),vObject.getBusinessLineStatusNt(),vObject.getBusinessLineStatus(),
				vObject.getRecordIndicatorNt(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdateApprHeader(FeesConfigHeaderVb vObject){
		String query = " Update RA_MST_FEES_HEADER set "+
				" BUSINESS_LINE_STATUS= ? ,RECORD_INDICATOR= ? ,MAKER= ? ,"+
				" VERIFIER= ? ,DATE_LAST_MODIFIED= "+commonDao.getDbFunction("SYSDATE")+" "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getBusinessLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),
				vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
	}
	protected int doUpdatePendHeader(FeesConfigHeaderVb vObject){
		String query = " Update RA_MST_FEES_HEADER_PEND set "+
				" BUSINESS_LINE_STATUS= ? ,RECORD_INDICATOR= ? ,MAKER= ? ,"+
				" VERIFIER= ? ,DATE_LAST_MODIFIED= "+commonDao.getDbFunction("SYSDATE")+" "+
				" WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getBusinessLineStatus(),vObject.getRecordIndicator(),
				vObject.getMaker(),vObject.getVerifier(),vObject.getCountry(),vObject.getLeBook(),
				vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),
				vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
	}
	protected int deleteFeesHeaderAppr(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_HEADER where WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected int deleteFeesHeaderPend(FeesConfigHeaderVb vObject){
		String query = "Delete from RA_MST_FEES_HEADER_PEND  WHERE COUNTRY= ? AND LE_BOOK= ? AND TRANS_LINE_ID= ? AND BUSINESS_LINE_ID = ?"+
				" AND FEE_CONFIG_TYPE = ? AND FEE_CONFIG_CODE = ? AND CONTRACT_ID = ? AND EFFECTIVE_DATE= ?";
		Object[] args = {vObject.getCountry(),vObject.getLeBook(),vObject.getTransLineId(),vObject.getBusinessLineId(),
				vObject.getFeeConfigType(),vObject.getFeeConfigCode(),vObject.getContractId(),vObject.getEffectiveDate()};
		return getJdbcTemplate().update(query,args);
		
	}
	protected List<FeesConfigHeaderVb> selectApprovedRecord(FeesConfigHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<FeesConfigHeaderVb> doSelectPendingRecord(FeesConfigHeaderVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(FeesConfigHeaderVb records){return records.getBusinessLineStatus();}
	@Override
	protected void setStatus(FeesConfigHeaderVb vObject,int status){vObject.setBusinessLineStatus(status);
	}
	public ExceptionCode doInsertApprRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		List<FeesConfigHeaderVb> collTemp = null;
		FeesConfigTierVb transLineSbuVb = new FeesConfigTierVb();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		if(!ValidationUtil.isValid(vObject.getContractId())) {
			vObject.setContractId("NA");
		}
		collTemp = selectApprovedRecord(vObject);
		if (collTemp != null && collTemp.size() > 0){
			exceptionCode = getResultObject(Constants.DUPLICATE_KEY_INSERTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setBusinessLineStatus(Constants.STATUS_ZERO);
		vObject.setMaker(intCurrentUserId);
		vObject.setVerifier(intCurrentUserId);
		
		retVal = doInsertionApprFeesHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getFeesConfigDetaillst() != null && vObject.getFeesConfigDetaillst().size() > 0) {
			exceptionCode = feesConfigDetailsDao.deleteAndInsertApprFeeDetail(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		exceptionCode = writeAuditLog(vObject, null);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}
	public ExceptionCode doInsertRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException {
		List<FeesConfigHeaderVb> collTemp = null;
		FeesConfigTierVb transLineSbuVb = new FeesConfigTierVb();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.ADD;
		strApproveOperation =Constants.ADD;		
		setServiceDefaults();
		if(!ValidationUtil.isValid(vObject.getContractId())) {
			vObject.setContractId("NA");
		}
		if(!ValidationUtil.isValid(vObject.getFeeConfigCode())) {
			vObject.setFeeConfigCode("NA"); 
		}
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
		retVal = doInsertionPendFeesHeaders(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		if(vObject.getFeesConfigDetaillst() != null && vObject.getFeesConfigDetaillst().size() > 0) {
			exceptionCode = feesConfigDetailsDao.deleteAndInsertPendFeeDetail(vObject);
		}
		exceptionCode =getResultObject(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
	public ExceptionCode doUpdateApprRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException  {
		List<FeesConfigHeaderVb> collTemp = null;
		FeesConfigHeaderVb vObjectlocal = null;
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
		vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
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
		if(vObject.getFeesConfigDetaillst() != null && vObject.getFeesConfigDetaillst().size() > 0) {
			exceptionCode = feesConfigDetailsDao.deleteAndInsertApprFeeDetail(vObject);
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
	public ExceptionCode doUpdateRecordForNonTrans(FeesConfigHeaderVb vObject) throws RuntimeCustomException  {
		List<FeesConfigHeaderVb> collTemp = null;
		FeesConfigHeaderVb vObjectlocal = null;
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
			vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
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
			if(vObject.getFeesConfigDetaillst() != null && vObject.getFeesConfigDetaillst().size() > 0) {
				feesConfigDetailsDao.deleteAndInsertPendFeeDetail(vObject);
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
				vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
				vObject.setDateCreation(vObjectlocal.getDateCreation());
			}
		    vObject.setDateCreation(vObjectlocal.getDateCreation());
		 // Record is there in approved, but not in pending.  So add it to pending
		    vObject.setVerifier(0);
		    vObject.setRecordIndicator(Constants.STATUS_UPDATE);
		    retVal = doInsertionPendFeesHeadersDc(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if(vObject.getFeesConfigDetaillst() != null && vObject.getFeesConfigDetaillst().size() > 0) {
				feesConfigDetailsDao.deleteAndInsertPendFeeDetail(vObject);
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doRejectForTransaction(FeesConfigHeaderVb vObject)throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doRejectRecord(vObject);
	}
	public ExceptionCode doRejectRecord(FeesConfigHeaderVb vObject)throws RuntimeCustomException {
		FeesConfigHeaderVb vObjectlocal = null;
		List<FeesConfigHeaderVb> collTemp = null;
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
			vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
			retVal = deleteFeesHeaderPend(vObject);
			
			List<FeesConfigDetailsVb> collTempDet = null;
			collTempDet = feesConfigDetailsDao.getFeesConfigDetails(vObject, 1);
			if (collTempDet != null && collTempDet.size() > 0){
				retVal = feesConfigDetailsDao.deleteFeesDetailsPend(vObject);
			}
			List<FeesConfigTierVb> collTempTier = null;
			collTempTier =  feesConfigTierDao.getFeesConfigTierByGroup(vObject, 1);
			if (collTemp != null && collTemp.size() > 0 ){
				int delCnt = feesConfigTierDao.deleteFeesTierPend(vObject);
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
	public ExceptionCode doApproveForTransaction(FeesConfigHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		strErrorDesc  = "";
		strCurrentOperation = Constants.APPROVE;
		setServiceDefaults();
		return doApproveRecord(vObject,staticDelete);
	}
	public ExceptionCode doApproveRecord(FeesConfigHeaderVb vObject, boolean staticDelete) throws RuntimeCustomException {
		FeesConfigHeaderVb oldContents = null;
		FeesConfigDetailsVb oldContentsGl = null;
		FeesConfigTierVb oldContentsSbu = null;
		
		FeesConfigHeaderVb vObjectlocal = null;
		FeesConfigDetailsVb vObjectGllocal = null;
		FeesConfigTierVb vObjectSbulocal = null;
		
		List<FeesConfigHeaderVb> collTemp = null;
		List<FeesConfigDetailsVb> collTempDet = null;
		List<FeesConfigDetailsVb> collTempDetAppr = null;
		List<FeesConfigTierVb> collTempTier = null;
		List<FeesConfigTierVb> collTempTierAppr = null;
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

			vObjectlocal = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);

			if (vObjectlocal.getMaker() == getIntCurrentUserId()){
				exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
				throw buildRuntimeCustomException(exceptionCode);
			}
			
			collTempDet = feesConfigDetailsDao.getFeesConfigDetails(vObjectlocal,1);
			if(collTempDet != null && collTempDet.size() > 0) {
				vObjectGllocal = ((ArrayList<FeesConfigDetailsVb>)collTempDet).get(0);
			}
			
			collTempTier = feesConfigTierDao.getFeesConfigTierByGroup(vObjectlocal,1); // SBU
			if(collTempTier != null && collTempTier.size() > 0) {
				vObjectSbulocal = ((ArrayList<FeesConfigTierVb>)collTempTier).get(0);
			}
			// If it's NOT addition, collect the existing record contents from the
			// Approved table and keep it aside, for writing audit information later.
			if (vObjectlocal.getRecordIndicator() != Constants.STATUS_INSERT){
				collTemp = selectApprovedRecord(vObject);
				if (collTemp == null || collTemp.isEmpty()){
					exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
					throw buildRuntimeCustomException(exceptionCode);
				}
				oldContents = ((ArrayList<FeesConfigHeaderVb>)collTemp).get(0);
				
				collTempDetAppr = feesConfigDetailsDao.getFeesConfigDetails(vObjectlocal,0);
				if(collTempDetAppr != null && collTempDetAppr.size() > 0) {
					oldContentsGl = ((ArrayList<FeesConfigDetailsVb>)collTempDetAppr).get(0);
				}
				
				collTempTierAppr = feesConfigTierDao.getFeesConfigTierByGroup(vObjectlocal,0);
				if(collTempTierAppr != null && collTempTierAppr.size() > 0) {
					oldContentsSbu = ((ArrayList<FeesConfigTierVb>)collTempTierAppr).get(0);
				}
			}

			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_INSERT){  // Add authorization
				// Write the contents of the Pending table record to the Approved table
				vObjectlocal.setRecordIndicator(Constants.STATUS_ZERO);
				vObjectlocal.setVerifier(getIntCurrentUserId());
				retVal = doInsertionApprFeesHeaders(vObjectlocal);
				if (retVal != Constants.SUCCESSFUL_OPERATION){
					exceptionCode = getResultObject(retVal);
					throw buildRuntimeCustomException(exceptionCode);
				}
				if(collTempTierAppr != null && collTempTierAppr.size() > 0) {
					feesConfigTierDao.deleteFeesTierAppr(vObjectlocal);
				}
				if(collTempTier != null && !collTempTier.isEmpty()) {
					collTempTier.forEach(sbuPend -> {
						retVal = feesConfigTierDao.doInsertionApprFeesTier(sbuPend);
					});
				}
				if(collTempDetAppr != null && collTempDetAppr.size() > 0) {
					feesConfigDetailsDao.deleteFeesDetailsAppr(vObjectlocal);
				}
				if(collTempDet != null && !collTempDet.isEmpty()) {
					collTempDet.forEach(glPend -> {
						retVal = feesConfigDetailsDao.doInsertionApprFeesDetails(glPend);
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
				if(collTempTierAppr != null && collTempTierAppr.size() > 0) {
					feesConfigTierDao.deleteFeesTierAppr(vObjectlocal);
				}
				if(collTempTier != null && !collTempTier.isEmpty()) {
					collTempTier.forEach(sbuPend -> {
						retVal = feesConfigTierDao.doInsertionApprFeesTier(sbuPend);
					});
				}
				if(collTempDetAppr != null && collTempDetAppr.size() > 0) {
					feesConfigDetailsDao.deleteFeesDetailsAppr(vObjectlocal);
				}
				if(collTempDet != null && !collTempDet.isEmpty()) {
					collTempDet.forEach(glPend -> {
						retVal = feesConfigDetailsDao.doInsertionApprFeesDetails(glPend);
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
			retVal = deleteFeesHeaderPend(vObjectlocal);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = feesConfigTierDao.deleteFeesTierPend(vObjectlocal);
			retVal = feesConfigDetailsDao.deleteFeesDetailsPend(vObjectlocal);
			// Set the internal status to Approved
			vObject.setInternalStatus(0);
			vObject.setRecordIndicator(Constants.STATUS_ZERO);
			if (vObjectlocal.getRecordIndicator() == Constants.STATUS_DELETE && !staticDelete){
				exceptionCode = writeAuditLog(null, oldContents);
				vObject.setRecordIndicator(-1);
			}
			else
				exceptionCode = writeAuditLog(vObjectlocal, oldContents);

			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
				throw buildRuntimeCustomException(exceptionCode);
			}
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
	protected String getAuditString(FeesConfigHeaderVb vObject){
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
			
			
			strAudit.append("BUSINESS_LINE_STATUS_NT"+auditDelimiterColVal+vObject.getBusinessLineStatusNt());
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
	public int effectiveDateCheck(FeesConfigHeaderVb dObj,int status){
		int cnt = 0;
		String query= "";
		try {
			if(status == 0) {
				query = " SELECT COUNT(1) FROM RA_MST_FEES_HEADER TAPPR "+
						" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND "+
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? ";
			}else {
				query = " SELECT COUNT(1) FROM RA_MST_FEES_HEADER_PEND TAPPR "+
						" WHERE TAPPR.COUNTRY = ? AND TAPPR.LE_BOOK = ? AND  TAPPR.TRANS_LINE_ID = ? AND "+
						" TAPPR.BUSINESS_LINE_ID = ? AND TAPPR.FEE_CONFIG_TYPE = ? AND  TAPPR.FEE_CONFIG_CODE = ? AND TAPPR.CONTRACT_ID = ? AND TAPPR.EFFECTIVE_DATE = ? ";
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
			cnt = getJdbcTemplate().queryForObject(query, objParams,Integer.class);
			return cnt;
		}catch(Exception e) {
			return cnt;
		}
	}
	public int effectiveDateBusinessCheck(FeesConfigHeaderVb dObj){
		int cnt = 0;
		String query= "";
		try {
				query = " SELECT CASE WHEN ? >= BUSINESS_DATE THEN 1 ELSE 0 END EFFECTIVE_DATE FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
			
			Object objParams[] = new Object[3];
			objParams[0] = new String(dObj.getEffectiveDate());// country
			objParams[1] = new String(dObj.getCountry());
			objParams[2] = new String(dObj.getLeBook());
			cnt = getJdbcTemplate().queryForObject(query, objParams,Integer.class);
			return cnt;
		}catch(Exception e) {
			return cnt;
		}
	}
}