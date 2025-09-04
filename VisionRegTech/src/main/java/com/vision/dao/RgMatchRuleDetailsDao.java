package com.vision.dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.vb.RgMatchRuleDetailVb;
import com.vision.vb.RgMatchRuleHeaderVb;

@Component
public class RgMatchRuleDetailsDao extends AbstractDao<RgMatchRuleHeaderVb> {

	public int getCntforCustRuleMatchDet(RgMatchRuleHeaderVb vObject, int status) {
		int count = 0;
		try {
			String tableName = "";
			if (status == Constants.STATUS_ZERO)
				tableName = "RG_CUSTMATCH_RULE_DETAIL";
			else
				tableName = "RG_CUSTMATCH_RULE_DETAIL_PEND";

			String sql = "SELECT COUNT(1) FROM " + tableName + " Where COUNTRY = ?  AND LE_BOOK = ? AND RULE_ID = ?";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId() };
			count = getJdbcTemplate().queryForObject(sql, Integer.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public int deleteCustMatchRuleDet(RgMatchRuleHeaderVb vObject,int status){
		if(status == Constants.STATUS_ZERO) 
			tableName = "RG_CUSTMATCH_RULE_DETAIL";
		else
			tableName = "RG_CUSTMATCH_RULE_DETAIL_PEND";
		try {
			String query = "Delete From "+tableName+" Where COUNTRY = ?  AND LE_BOOK = ? AND RULE_ID = ? ";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(),vObject.getRuleId() };
			getJdbcTemplate().update(query,args);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return Constants.ERRONEOUS_OPERATION;
	}
	
	public ExceptionCode insertCustRuleMatchDetails(RgMatchRuleDetailVb vObject,int status) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if(status == Constants.STATUS_ZERO) 
				tableName = "RG_CUSTMATCH_RULE_DETAIL";
			else
				tableName = "RG_CUSTMATCH_RULE_DETAIL_PEND";
			
			String query =	"Insert Into "+tableName+" "
					+ " (COUNTRY, LE_BOOK, RULE_ID, SEQUENCE, "
					+ "TABLE_NAME, COLUMN_NAME, OPERATOR, SCORE,"
					+ "RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED,"
					+ " DATE_CREATION,FILTER_CONDITION,CONDITION_VALUE_1,CONDITION_VALUE_2)"
					+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SysDate, SysDate, ?, ?, ?)";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getRuleId(), vObject.getSequence(),
					vObject.getTableName(), vObject.getColumnName(), vObject.getOperator(), vObject.getScore(),
					vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
					vObject.getVerifier(), vObject.getInternalStatus(), vObject.getFilterCondition(),
					vObject.getConditionValue1(), vObject.getConditionValue2() };
			getJdbcTemplate().update(query, args);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Success");
		}catch(Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	
	public List<RgMatchRuleDetailVb> getQueryResultsforMatchRuleDet(RgMatchRuleHeaderVb dObj,int status){
		List<RgMatchRuleDetailVb> collTemp = new ArrayList<RgMatchRuleDetailVb>();
		String sql = "";
		try {
			if(status == Constants.STATUS_ZERO) {
				sql = "select Country,LE_Book,Rule_ID,Sequence,Table_Name,Column_Name,"
						+ " Operator,Score,Record_Indicator_Nt,Record_Indicator,Maker,Verifier,"
						+ " Internal_Status,Date_Last_Modified,Date_Creation,FILTER_CONDITION,CONDITION_VALUE_1,CONDITION_VALUE_2 from RG_CustMatch_Rule_Detail "
						+ "  WHERE COUNTRY = ?   AND LE_BOOK = ?   AND RULE_ID = ?";
			} else {
				sql = "select Country,LE_Book,Rule_ID,Sequence,Table_Name,Column_Name,"
						+ " Operator,Score,Record_Indicator_Nt,Record_Indicator,Maker,Verifier,"
						+ " Internal_Status,Date_Last_Modified,Date_Creation,FILTER_CONDITION,CONDITION_VALUE_1,CONDITION_VALUE_2 from RG_CustMatch_Rule_Detail_PEND "
						+ " WHERE COUNTRY = ?   AND LE_BOOK = ?   AND RULE_ID = ?";
			}
			
			Object objParams[] = new Object[3];
			objParams[0] = dObj.getCountry();
			objParams[1] = dObj.getLeBook();
			objParams[2] = dObj.getRuleId();
			collTemp = getJdbcTemplate().query(sql,objParams,getMapper());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}
	@Override
	public RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				RgMatchRuleDetailVb vObject = new RgMatchRuleDetailVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setRuleId(rs.getString("RULE_ID"));
				vObject.setSequence(rs.getInt("Sequence"));
				vObject.setTableName(rs.getString("Table_Name"));
				vObject.setColumnName(rs.getString("Column_Name"));
				vObject.setOperator(rs.getString("Operator"));
				vObject.setScore(rs.getFloat("Score"));
				vObject.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setFilterCondition(rs.getString("FILTER_CONDITION"));
				vObject.setConditionValue1(rs.getString("CONDITION_VALUE_1"));
				vObject.setConditionValue2(rs.getString("CONDITION_VALUE_2"));
				return vObject;
			}
		};
		return mapper;
	}
}
