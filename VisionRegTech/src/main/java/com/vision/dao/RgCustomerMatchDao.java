package com.vision.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.RgCustomerMatchVb;
import com.vision.vb.RgMatchRuleDetailVb;
import com.vision.vb.RgSourceTableMappings;
import com.vision.vb.SmartSearchVb;

@Component
public class RgCustomerMatchDao extends AbstractDao<RgCustomerMatchVb> {
	
	@Autowired
	CommonDao commonDao;

	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgCustomerMatchVb vObject = new RgCustomerMatchVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setRuleId(rs.getString("RULE_ID"));
				vObject.setSetNumber(rs.getString("SET_NUMBER"));
				vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
				vObject.setCustRelationStatusNt(rs.getInt("CUST_RELATION_STATUS_NT"));
				vObject.setCustRelationStatus(rs.getInt("CUST_RELATION_STATUS"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("Date_Creation"));
				vObject.setCustomerName(rs.getString("CUSTOMER_NAME"));
				vObject.setCustRelationStatusDesc(rs.getString("CUST_RELATION_STATUS_DESC"));
				vObject.setForceMatch(rs.getString("FORCE_MATCH"));
				vObject.setRelationCustomerId(rs.getString("RELATION_CUSTOMER_ID"));
				vObject.setRelationCustomerName(rs.getString("RELATION_CUSTOMER_NAME"));
				vObject.setRelationType(rs.getString("RELATION_TYPE"));
				vObject.setRelationTypeDesc(rs.getString("RELATION_TYPE_DESC"));
				vObject.setRuleDesc(rs.getString("RULE_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	protected RowMapper getMapperNew() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgCustomerMatchVb vObject = new RgCustomerMatchVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setRuleId(rs.getString("RULE_ID"));
				vObject.setSetNumber(rs.getString("SET_NUMBER"));
				vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
				vObject.setCustRelationStatus(rs.getInt("CUST_RELATION_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
//				vObject.setCustomerName(rs.getString("CUSTOMER_NAME"));
				vObject.setCustRelationStatusDesc(rs.getString("CUST_RELATION_STATUS_DESC"));
//				vObject.setForceMatch(rs.getString("FORCE_MATCH"));
				vObject.setRelationCustomerId(rs.getString("RELATION_CUSTOMER_ID"));
//				vObject.setRelationCustomerName(rs.getString("RELATION_CUSTOMER_NAME"));
				vObject.setRelationType(rs.getString("RELATION_TYPE"));
				vObject.setRelationTypeDesc(rs.getString("RELATION_TYPE_DESC"));
//				vObject.setRuleDesc(rs.getString("RULE_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	@Override
	public List<RgCustomerMatchVb> getQueryPopupResults(RgCustomerMatchVb dObj) {

		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("SELECT * FROM (                                   "
				+ "    SELECT                                                                            "
				+ "        T1.COUNTRY,                                                                   "
				+ "        T1.LE_BOOK,                                                                   "
				+ "        T1.RULE_ID,                                                                   "
				+ "        T1.SET_NUMBER,                                                                "
				+ "        T1.CUSTOMER_ID,                                                               "
				+ "        T1.CUST_RELATION_STATUS_NT,                                                   "
				+ "        T1.CUST_RELATION_STATUS,                                                      "
				+ "        (SELECT NUM_SUBTAB_DESCRIPTION                                                "
				+ "         FROM NUM_SUB_TAB                                                             "
				+ "         WHERE NUM_TAB = 207                                                          "
				+ "           AND NUM_SUB_TAB = T1.CUST_RELATION_STATUS) AS CUST_RELATION_STATUS_DESC,   "
				+ "        T1.RECORD_INDICATOR_NT,                                                       "
				+ "        T1.RECORD_INDICATOR,                                                          "
				+ "        T1.DATE_LAST_MODIFIED,                                                        "
				+ "        T1.DATE_CREATION,                                                             "
				+ "        T2.CUSTOMER_NAME AS CUSTOMER_NAME,                                            "
				+ "        T1.RELATION_TYPE, T1.RELATION_TYPE_AT,                                        "
				+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB                                  "
		        + " WHERE ALPHA_TAB = 810 AND ALPHA_SUB_TAB = T1.RELATION_TYPE) AS RELATION_TYPE_DESC,   "
				+ "        T1.RELATION_CUSTOMER_ID,                                                      "
				+ "        T3.CUSTOMER_NAME AS RELATION_CUSTOMER_NAME,                                   "
				+ "        (SELECT FORCE_MATCH                                                           "
				+ "         FROM RG_CUSTMATCH_RULE_HEADER                                                "
				+ "         WHERE COUNTRY = T1.COUNTRY                                                   "
				+ "           AND LE_BOOK = T1.LE_BOOK                                                   "
				+ "           AND RULE_ID = T1.RULE_ID) AS FORCE_MATCH,                                  "
				+ "        (SELECT RULE_DESCRIPTION                                                      "
				+ "         FROM RG_CUSTMATCH_RULE_HEADER                                                "
				+ "         WHERE COUNTRY = T1.COUNTRY                                                   "
				+ "           AND LE_BOOK = T1.LE_BOOK                                                   "
				+ "           AND RULE_ID = T1.RULE_ID) AS RULE_DESCRIPTION                              "
				+ "    FROM RG_CUSTOMER_RELATIONSHIPS T1                                                 "
				+ "        JOIN CUSTOMERS T2 ON T1.COUNTRY = T2.COUNTRY                                  "
				+ "                         AND T1.LE_BOOK = T2.LE_BOOK                                  "
				+ "                         AND T1.CUSTOMER_ID = T2.CUSTOMER_ID                          "
				+ "        LEFT JOIN CUSTOMERS T3 ON T1.COUNTRY = T3.COUNTRY                             "
				+ "                              AND T1.LE_BOOK = T3.LE_BOOK                             "
				+ "                              AND T1.RELATION_CUSTOMER_ID = T3.CUSTOMER_ID            "
				+ "       WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ?                                        "
				+ ") TAPPR                                                                               ");

		params.addElement(dObj.getCountry());
		params.addElement(dObj.getLeBook());

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
//					case "country":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) " + val, strBufApprove, data.getJoinType());
//						break;
//
//					case "leBook":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) " + val, strBufApprove, data.getJoinType());
//						break;

					case "rule":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_DESCRIPTION) " + val, strBufApprove, data.getJoinType());
						break;

					case "setNumber":
						CommonUtils.addToQuerySearch(" upper(TAPPR.SET_NUMBER) " + val, strBufApprove, data.getJoinType());
						break;

					case "customerId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove, data.getJoinType());
						break;
						
					case "relationCustomerId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RELATION_CUSTOMER_ID) " + val, strBufApprove, data.getJoinType());
						break;
						
					case "relationCustomerName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RELATION_CUSTOMER_NAME) " + val, strBufApprove, data.getJoinType());
						break;
//					
//					case "parentCid":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.PROP_PARENT_CID) " + val, strBufApprove, data.getJoinType());
//						break;
//						
//					case "curParentCid":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUR_PARENT_CID) " + val, strBufApprove, data.getJoinType());
//						break;
						
					case "customerName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove, data.getJoinType());
						break;
						
					case "relationType":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RELATION_TYPE_DESC) " + val, strBufApprove, data.getJoinType());
						break;
						
					case "custRelationStatus":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUST_RELATION_STATUS_DESC) " + val, strBufApprove, data.getJoinType());
						break;
					default:
					}
					count++;
				}
			}
			String orderBy = "ORDER BY TO_NUMBER(SET_NUMBER), CUSTOMER_ID, RELATION_CUSTOMER_ID ";
			return getQueryPopupResults(dObj, new StringBuffer(), strBufApprove, new String(), orderBy, params,	getMapper());
		} catch (Exception ex) { 
			ex.printStackTrace();
			if (params != null)
				for (int i = 0; i < params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}

	}

	public List<RgCustomerMatchVb> getQueryResults(RgCustomerMatchVb dObj, int intStatus) {
		setServiceDefaults();
		List<RgCustomerMatchVb> collTemp = null;
		final int intKeyFieldsCount = 5;
		String strQueryAppr = new String("SELECT                                                                      "
				+ "				        T1.COUNTRY,                                                                   "
				+ "				        T1.LE_BOOK,                                                                   "
				+ "				        T1.RULE_ID,                                                                   "
				+ "				        T1.SET_NUMBER,                                                                "
				+ "				        T1.CUSTOMER_ID,                                                               "
				+ "				        T1.CUST_RELATION_STATUS_NT,                                                   "
				+ "				        T1.CUST_RELATION_STATUS,                                                      "
				+ "				        (SELECT NUM_SUBTAB_DESCRIPTION                                                "
				+ "				         FROM NUM_SUB_TAB                                                             "
				+ "				         WHERE NUM_TAB = 207                                                          "
				+ "				           AND NUM_SUB_TAB = T1.CUST_RELATION_STATUS) AS CUST_RELATION_STATUS_DESC,   "
				+ "				        T1.RECORD_INDICATOR_NT,                                                       "
				+ "				        T1.RECORD_INDICATOR,                                                          "
				+ "				        T1.DATE_LAST_MODIFIED,                                                        "
				+ "				        T1.DATE_CREATION,                                                             "
				+ "				        T1.RELATION_TYPE, T1.RELATION_TYPE_AT,                                        "
				+ "				 (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB                                  "
				+ "		         WHERE ALPHA_TAB = 810 AND ALPHA_SUB_TAB = T1.RELATION_TYPE) AS RELATION_TYPE_DESC,   "
				+ "				        T1.RELATION_CUSTOMER_ID                                                       "
				+ "				    FROM RG_CUSTOMER_RELATIONSHIPS T1 WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? AND     "
				+ "  T1.RULE_ID = ? AND T1.SET_NUMBER = ? AND T1.CUSTOMER_ID = ?                                      ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = dObj.getLeBook();
		objParams[2] = dObj.getRuleId();
		objParams[3] = dObj.getSetNumber();
		objParams[4] = dObj.getCustomerId();
		try {
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), objParams, getMapperNew());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			if (objParams != null)
				for (int i = 0; i < objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}

	@Override
	protected int doUpdateAppr(RgCustomerMatchVb vObject) {
		String query = "Update RG_CUSTOMER_RELATIONSHIPS set PARENT_CID = ?, ULTIMATE_PARENT= ?, DATE_LAST_MODIFIED = "
				+ getDbFunction("SYSDATE") + " where COUNTRY= ? AND LE_BOOK= ? AND RULE_ID= ? " 
				+ " AND SET_NUMBER = ?  AND CUSTOMER_ID= ?";
		Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId(), vObject.getSetNumber(),
				vObject.getCustomerId() };
		return getJdbcTemplate().update(query, args);
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "Rg Customer Match";
		serviceDesc = "Rg Customer Match";
		tableName = "RG_CUSTOMER_RELATIONSHIPS";
		childTableName = "RG_CUSTOMER_RELATIONSHIPS";
	}

	protected List<RgCustomerMatchVb> selectApprovedRecord(RgCustomerMatchVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<RgCustomerMatchVb> doSelectPendingRecord(RgCustomerMatchVb vObject) {
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected int getStatus(RgCustomerMatchVb records) {
		return 0;
	}

	@Override
	protected void setStatus(RgCustomerMatchVb vObject, int status) {
	}

	public int updateProcessStatus(RgCustomerMatchVb vObject) {
		try {
			String query = "UPDATE RG_CUSTOMER_RELATIONSHIPS SET  CUST_RELATION_STATUS = ?, DATE_LAST_MODIFIED = "
					+ getDbFunction("SYSDATE") + " WHERE COUNTRY = ? AND LE_BOOK= ? AND RULE_ID = ? AND "
					+ " SET_NUMBER = ? AND CUSTOMER_ID = ? ";
			Object[] args = { vObject.getCustRelationStatus(), vObject.getCountry(), vObject.getLeBook(),
					vObject.getRuleId(), vObject.getSetNumber(), vObject.getCustomerId() };
			getJdbcTemplate().update(query, args);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	
	public List<AlphaSubTabVb> getCountryLeBook() throws DataAccessException {
		try {
	//		String sql = "SELECT distinct COUNTRY||'-'||LE_BOOK ID, COUNTRY||' - '||LE_BOOK DESCRIPTION FROM LE_BOOK WHERE LEB_STATUS = 0  ORDER BY id";
			String sql = "SELECT distinct COUNTRY||'-'||LE_BOOK ID, LEB_DESCRIPTION DESCRIPTION FROM LE_BOOK WHERE LEB_STATUS = 0  ORDER BY id";
			List<AlphaSubTabVb> countryLeBookLst = getJdbcTemplate().query(sql, commonDao.getPageLoadValuesMapper());
			return countryLeBookLst;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<AlphaSubTabVb>();
		}
	}

	public RgCustomerMatchVb getCustomerMatchDetails(RgCustomerMatchVb vObject) {
		try {
//			String sql = " Select t1.Country,t1.LE_Book,t1.Customer_ID,t1.Customer_Name,  "
//					+ " t2.Match_Customer_ID,t3.Customer_Name Match_Customer_NAME, t2.Rule_ID,T5.RULE_DESCRIPTION,  "
//					+ " t2.Parent_CID,t4.Customer_NAme Parent_NAme,t2.Set_Number  "
//					+ " from Customers t1,RG_CUSTOMER_RELATIONSHIPs t2,Customers t3,Customers t4,RG_CustMatch_Rule_Header t5  "
//					+ " where t1.country = t2.country   "
//					+ " and t1.LE_book = t2.LE_Book  "
//					+ " and t1.Customer_Id = t2.Customer_ID  "
//					+ " and t1.country = t3.country   "
//					+ " and t1.LE_book = t3.LE_Book  "
//					+ " and t2.Match_Customer_ID = t3.Customer_ID  "
//					+ " and t2.country = t4.country   "
//					+ " and t2.LE_book = t4.LE_Book  "
//					+ " and t2.Parent_CID = t4.Customer_ID  "
//					+ " and t1.Country= ? and t1.LE_Book = ? and t1.Customer_ID = ?  "
//					+ " and t2.country = t5.country   "
//					+ " and t2.LE_book = t5.LE_Book   "
//					+ " and t2.Rule_ID = t5.Rule_ID   ";

			String sql = "    SELECT                                                                            "
					+ "        T1.COUNTRY,                                                                   "
					+ "        T1.LE_BOOK,                                                                   "
					+ "        T1.RULE_ID,                                                                   "
					+ "        T1.SET_NUMBER,                                                                "
					+ "        T1.CUSTOMER_ID,                                                               "
					+ "        T1.CUST_RELATION_STATUS_NT,                                                   "
					+ "        T1.CUST_RELATION_STATUS,                                                      "
					+ "        (SELECT NUM_SUBTAB_DESCRIPTION                                                "
					+ "         FROM NUM_SUB_TAB                                                             "
					+ "         WHERE NUM_TAB = 207                                                          "
					+ "           AND NUM_SUB_TAB = T1.CUST_RELATION_STATUS) AS CUST_RELATION_STATUS_DESC,   "
					+ "        T1.RECORD_INDICATOR_NT,                                                       "
					+ "        T1.RECORD_INDICATOR,                                                          "
					+ "        T1.DATE_LAST_MODIFIED,                                                        "
					+ "        T1.DATE_CREATION,                                                             "
					+ "        T2.CUSTOMER_NAME AS CUSTOMER_NAME,                                            "
					+ "        T1.RELATION_TYPE, T1.RELATION_TYPE_AT,                                        "
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB                                  "
					+ " WHERE ALPHA_TAB = 810 AND ALPHA_SUB_TAB = T1.RELATION_TYPE) AS RELATION_TYPE_DESC,   "
					+ "        T1.RELATION_CUSTOMER_ID,                                                      "
					+ "        T3.CUSTOMER_NAME AS RELATION_CUSTOMER_NAME,                                   "
					+ "        (SELECT FORCE_MATCH                                                           "
					+ "         FROM RG_CUSTMATCH_RULE_HEADER                                                "
					+ "         WHERE COUNTRY = T1.COUNTRY                                                   "
					+ "           AND LE_BOOK = T1.LE_BOOK                                                   "
					+ "           AND RULE_ID = T1.RULE_ID) AS FORCE_MATCH,                                  "
					+ "        (SELECT RULE_DESCRIPTION                                                      "
					+ "         FROM RG_CUSTMATCH_RULE_HEADER                                                "
					+ "         WHERE COUNTRY = T1.COUNTRY                                                   "
					+ "           AND LE_BOOK = T1.LE_BOOK                                                   "
					+ "           AND RULE_ID = T1.RULE_ID) AS RULE_DESCRIPTION                              "
					+ "    FROM RG_CUSTOMER_RELATIONSHIPS T1                                                 "
					+ "        JOIN CUSTOMERS T2 ON T1.COUNTRY = T2.COUNTRY                                  "
					+ "                         AND T1.LE_BOOK = T2.LE_BOOK                                  "
					+ "                         AND T1.CUSTOMER_ID = T2.CUSTOMER_ID                          "
					+ "        LEFT JOIN CUSTOMERS T3 ON T1.COUNTRY = T3.COUNTRY                             "
					+ "                              AND T1.LE_BOOK = T3.LE_BOOK                             "
					+ "                              AND T1.RELATION_CUSTOMER_ID = T3.CUSTOMER_ID            "
					+ "       WHERE T1.COUNTRY = ? AND T1.LE_BOOK = ? and T1.CUSTOMER_ID = ?                 ";

			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getCustomerId() };
			return getJdbcTemplate().query(sql, new ResultSetExtractor<RgCustomerMatchVb>() {
				@Override
				public RgCustomerMatchVb extractData(ResultSet rs) throws SQLException, DataAccessException {
					RgCustomerMatchVb vObject = new RgCustomerMatchVb();
					while (rs.next()) {
						vObject.setCountry(rs.getString("COUNTRY"));
						vObject.setLeBook(rs.getString("LE_BOOK"));
						vObject.setRuleId(rs.getString("RULE_ID"));
						vObject.setSetNumber(rs.getString("SET_NUMBER"));
						vObject.setCustomerId(rs.getString("CUSTOMER_ID"));
						vObject.setCustRelationStatusNt(rs.getInt("CUST_RELATION_STATUS_NT"));
						vObject.setCustRelationStatus(rs.getInt("CUST_RELATION_STATUS"));
						vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
						vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
						vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
						vObject.setDateCreation(rs.getString("Date_Creation"));
						vObject.setCustomerName(rs.getString("CUSTOMER_NAME"));
						vObject.setCustRelationStatusDesc(rs.getString("CUST_RELATION_STATUS_DESC"));
						vObject.setForceMatch(rs.getString("FORCE_MATCH"));
						vObject.setRelationCustomerId(rs.getString("RELATION_CUSTOMER_ID"));
						vObject.setRelationCustomerName(rs.getString("RELATION_CUSTOMER_NAME"));
						vObject.setRelationType(rs.getString("RELATION_TYPE"));
						vObject.setRelationTypeDesc(rs.getString("RELATION_TYPE_DESC"));
						vObject.setRuleDesc(rs.getString("RULE_DESCRIPTION"));
					}
					return vObject;
				}
			}, args);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ExceptionCode getRuleDetail(RgCustomerMatchVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<RgCustomerMatchVb> customersList = new ArrayList<RgCustomerMatchVb>();
		try {
			String sql = " SELECT Country,   LE_Book, Rule_ID, Sequence, Table_Name,"
					+ " Column_Name, Operator, Score, filter_condition,  Condition_Value_1, "
					+ " Condition_Value_2  FROM RG_CustMatch_Rule_Detail "
					+ " WHERE Country = ? AND LE_Book = ? AND Rule_ID = ? ORDER BY Sequence";

			String args[] = { dObj.getCountry(), dObj.getLeBook(), dObj.getRuleId() };
			customersList = (ArrayList) jdbcTemplate.query(sql, getCustomerMatchDetailMapper(), args);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setResponse(customersList);
		return exceptionCode;
	}
	public RowMapper getCustomerMatchDetailMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgMatchRuleDetailVb vObject = new RgMatchRuleDetailVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setRuleId(rs.getString("RULE_ID"));
				vObject.setTableName(rs.getString("TABLE_NAME"));
				vObject.setColumnName(rs.getString("COLUMN_NAME"));
				vObject.setOperator(rs.getString("OPERATOR"));
				return vObject;
			}
		};
		return mapper;
	}
	/*public String getRgSourceTableMappings(String ruleId) throws RuntimeCustomException {
		try {
			String query =  " SELECT TABLE_NAME,TABLE_ALIAS FROM RG_SOURCE_TABLE_MAPPING "
					+ " WHERE TABLE_NAME IN(select TABLE_NAME from RG_CustMatch_Rule_Detail where rule_id = '"+ruleId+"') "
					+ " ORDER BY TABLE_ALIAS ";
			
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					Boolean recPresent = false;
					StringJoiner tableNameJoiner = new StringJoiner(",");
					while(rs.next()){
						recPresent = true;
						String tableName = rs.getString("TABLE_NAME")+" "+rs.getString("TABLE_ALIAS");
						tableNameJoiner.add(tableName);
					}
					return tableNameJoiner.toString();
				}
			};
			return (String)jdbcTemplate.query(query,mapper);
		}catch(Exception e) {
			return null;
		}
	}*/
	public String getRgSourceColumnMappings(String ruleId) throws RuntimeCustomException {
		try {
			String query = " SELECT T2.TABLE_ALIAS,T1.COLUMN_NAME FROM RG_SOURCE_COLUMN_MAPPING T1,RG_SOURCE_TABLE_MAPPING T2 "
					+ " WHERE T1.TABLE_NAME = T2.SOURCE_TABLE "
					+ " and  t1.TABLE_NAME IN (select TABLE_NAME from RG_CustMatch_Rule_Detail where rule_id = '"
					+ ruleId + "') "
					+ " and t1.COLUMN_NAME  IN (select COLUMN_NAME from RG_CustMatch_Rule_Detail where rule_id = '"
					+ ruleId + "') " + " ORDER BY T1.COLUMN_NAME  ";

			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					StringJoiner columnNameAliasJoiner = new StringJoiner(",");
					StringJoiner columnNameJoiner = new StringJoiner(",");
					Boolean recPresent = false;
					while (rs.next()) {
						recPresent = true;
						RgSourceTableMappings vObject = new RgSourceTableMappings();
						String columnNameWithAlias = rs.getString("TABLE_ALIAS") + "." + rs.getString("COLUMN_NAME");
						columnNameJoiner.add(rs.getString("COLUMN_NAME"));
						columnNameAliasJoiner.add(columnNameWithAlias);
					}
					return columnNameAliasJoiner.toString() + "!@#" + columnNameJoiner.toString();
				}
			};
			return (String) jdbcTemplate.query(query, mapper);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ExceptionCode getRgSourceTableMappings(String ruleId) throws RuntimeCustomException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<RgSourceTableMappings> tableMappingslst = new ArrayList<>();
		try {
			String query = " SELECT * FROM RG_SOURCE_TABLE_MAPPING "
					+ " WHERE TABLE_NAME IN(select TABLE_NAME from RG_CustMatch_Rule_Detail where rule_id = '" + ruleId
					+ "') " + "ORDER BY TABLE_ALIAS ";

			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					Boolean recPresent = false;
					while (rs.next()) {
						recPresent = true;
						RgSourceTableMappings vObject = new RgSourceTableMappings();
						vObject.setTableAlias(rs.getString("TABLE_ALIAS"));
						vObject.setTableName(rs.getString("TABLE_NAME"));
						vObject.setSourceTable(rs.getString("SOURCE_TABLE"));
						vObject.setGenericCondition(rs.getString("GENERIC_CONDITION"));
						tableMappingslst.add(vObject);
					}
					if (!recPresent) {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("No Data found in RG_SOURCE_TABLE_MAPPING");
					}
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setResponse(tableMappingslst);
					return exceptionCode;
				}
			};
			return (ExceptionCode) jdbcTemplate.query(query, mapper);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		if (exceptionCode.getErrorCode() != Constants.ERRONEOUS_OPERATION) {
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}
		return exceptionCode;
	}
	
	public List<RgCustomerMatchVb> selectRecordsForBulkOperation(RgCustomerMatchVb vObject) {
		try {
			String query = "SELECT RULE_ID, SET_NUMBER, CUSTOMER_ID FROM RG_CUSTOMER_RELATIONSHIPS "
					+ " WHERE COUNTRY = ? AND LE_BOOK= ? ";
			Object[] args = { vObject.getCountry(), vObject.getLeBook() };
			return getJdbcTemplate().query(query, new ResultSetExtractor<List<RgCustomerMatchVb>>() {
				@Override
				public List<RgCustomerMatchVb> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<RgCustomerMatchVb> collTemp = new ArrayList<>();
					while (rs.next()) {
						RgCustomerMatchVb obj = new RgCustomerMatchVb();
						obj.setRuleId(rs.getString("RULE_ID"));
						obj.setSetNumber(rs.getString("SET_NUMBER"));
						obj.setCustomerId(rs.getString("CUSTOMER_ID"));
						collTemp.add(obj);
					}
					return collTemp;
				}
			}, args);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ExceptionCode getTableData(String tableName) {
		ExceptionCode exceptionCode = new ExceptionCode();
		Map<String, Object> result = new HashMap<>();
		try {
			String query = "SELECT * FROM " + tableName + " WHERE ROWNUM <= 10 ";
			result = getJdbcTemplate().query(query, (ResultSetExtractor<Map<String, Object>>) rs -> {
				Map<String, Object> response = new HashMap<>();
				List<Map<String, String>> data = new ArrayList<>();
				Set<String> headers = new LinkedHashSet<>();
				ResultSetMetaData metaData = rs.getMetaData();
				int colCount = metaData.getColumnCount();
				while (rs.next()) {
					Map<String, String> row = new HashMap<>();
					for (int i = 1; i <= colCount; i++) {
						String columnName = metaData.getColumnName(i).toUpperCase();
						headers.add(columnName);
						row.put(columnName, rs.getString(i));
					}
					data.add(row);
				}
				response.put("data", data);
				return response;
			});
			exceptionCode.setResponse(result);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		return exceptionCode;
	}
}
