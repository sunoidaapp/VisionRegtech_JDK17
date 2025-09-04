
package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionDynamicHashVariablesVb;
@Component
public class VisionDynamicHashVariablesDao extends AbstractDao<VisionDynamicHashVariablesVb> {
	
	String VariableStatusNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TAppr.VARIABLE_STATUS", "VARIABLE_STATUS_DESC");
	String VariableStatusNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1, "TPend.VARIABLE_STATUS", "VARIABLE_STATUS_DESC");

	String RecordIndicatorNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TAppr.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	String RecordIndicatorNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 7, "TPend.RECORD_INDICATOR", "RECORD_INDICATOR_DESC");
	
	String VariableTypeNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1056, "TAppr.VARIABLE_TYPE", "VARIABLE_TYPE_DESC");
	String VariableTypeNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("NT", 1056, "TPend.VARIABLE_TYPE", "VARIABLE_TYPE_DESC");

	String ScriptTypeNtApprDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1083, "TAppr.SCRIPT_TYPE", "SCRIPT_TYPE_DESC");
	String ScriptTypeNtPendDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 1083, "TPend.SCRIPT_TYPE", "SCRIPT_TYPE_DESC");
	
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionDynamicHashVariablesVb visionDynamicHashVariablesVb = new VisionDynamicHashVariablesVb();
				visionDynamicHashVariablesVb = new VisionDynamicHashVariablesVb();
				visionDynamicHashVariablesVb.setVariableName(rs.getString("VARIABLE_NAME").trim());
				visionDynamicHashVariablesVb.setVariableTypeNt(rs.getInt("VARIABLE_TYPE_NT"));
				visionDynamicHashVariablesVb.setVariableType(rs.getInt("VARIABLE_TYPE"));
				visionDynamicHashVariablesVb.setVariableTypeDesc(rs.getString("VARIABLE_TYPE_DESC"));
				visionDynamicHashVariablesVb.setScriptTypeAt(rs.getInt("SCRIPT_TYPE_AT"));
				visionDynamicHashVariablesVb.setScriptType(rs.getString("SCRIPT_TYPE"));
				visionDynamicHashVariablesVb.setScriptTypeDesc(rs.getString("SCRIPT_TYPE_DESC"));
				visionDynamicHashVariablesVb.setVariableScript(rs.getString("VARIABLE_SCRIPT"));
				visionDynamicHashVariablesVb.setSortOrder(rs.getInt("SORT_ORDER"));
				visionDynamicHashVariablesVb.setVariableStatusNt(rs.getInt("VARIABLE_STATUS_NT"));
				visionDynamicHashVariablesVb.setVariableStatus(rs.getInt("VARIABLE_STATUS"));
				visionDynamicHashVariablesVb.setStatusDesc(rs.getString("VARIABLE_STATUS_DESC"));
				visionDynamicHashVariablesVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				visionDynamicHashVariablesVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				visionDynamicHashVariablesVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				visionDynamicHashVariablesVb.setMaker(rs.getLong("MAKER"));
				visionDynamicHashVariablesVb.setMakerName(rs.getString("MAKER_NAME"));
				visionDynamicHashVariablesVb.setVerifier(rs.getLong("VERIFIER"));
				visionDynamicHashVariablesVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				visionDynamicHashVariablesVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				visionDynamicHashVariablesVb.setDateCreation(rs.getString("DATE_CREATION"));
				visionDynamicHashVariablesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				return visionDynamicHashVariablesVb;
			}
		};
		return mapper;
	}
	
	public List<VisionDynamicHashVariablesVb> getQueryPopupResults(VisionDynamicHashVariablesVb dObj){
		
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("select * from (Select TAppr.VARIABLE_NAME," +
				"TAppr.VARIABLE_TYPE_NT, TAppr.VARIABLE_TYPE," +VariableTypeNtApprDesc+
				", TAppr.SCRIPT_TYPE_AT, TAppr.SCRIPT_TYPE,TAppr.VARIABLE_SCRIPT, "+ScriptTypeNtApprDesc
				+ ",TAppr.SORT_ORDER,TAppr.VARIABLE_STATUS_NT," +
				"TAppr.VARIABLE_STATUS, "+VariableStatusNtApprDesc
				+ ", TAppr.RECORD_INDICATOR_NT," +
				"TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
				+ ", TAppr.MAKER, "+makerApprDesc
				+ ", TAppr.VERIFIER," +verifierApprDesc+
				", TAppr.INTERNAL_STATUS, "
				+ dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED,TAppr.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," +
				dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION" +
				" From VISION_DYNAMIC_HASH_VAR TAppr)TAppr ");
			String strWhereNotExists = new String( " Not Exists (Select * From VISION_DYNAMIC_HASH_VAR_PEND TPend" +
				" Where TPend.VARIABLE_NAME = TAppr.VARIABLE_NAME) ");
			StringBuffer strBufPending = new StringBuffer("select * from (Select TPend.VARIABLE_NAME," +
				"TPend.VARIABLE_TYPE_NT, TPend.VARIABLE_TYPE," +VariableTypeNtPendDesc+
				", TPend.SCRIPT_TYPE_AT, TPend.SCRIPT_TYPE,TPend.VARIABLE_SCRIPT, "+ScriptTypeNtPendDesc
				+ ",TPend.SORT_ORDER,TPend.VARIABLE_STATUS_NT," +
				"TPend.VARIABLE_STATUS, "+VariableStatusNtPendDesc
				+ ", TPend.RECORD_INDICATOR_NT," +
				"TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
				+ ",TPend.MAKER, "+makerPendDesc
				+ ",TPend.VERIFIER," +verifierPendDesc+
				", TPend.INTERNAL_STATUS, "+dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED,TPend.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," +
				dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION" +
				" From VISION_DYNAMIC_HASH_VAR_PEND TPend )TPend");
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
					case "variableName":
						CommonUtils.addToQuerySearch(" upper(TAppr.VARIABLE_NAME) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VARIABLE_NAME) "+ val, strBufPending, data.getJoinType());
						break;

					case "variableType":
						CommonUtils.addToQuerySearch(" upper(TAppr.VARIABLE_TYPE_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VARIABLE_TYPE_DESC) "+ val, strBufPending, data.getJoinType());
						break;

					case "variableScript":
						CommonUtils.addToQuerySearch(" upper(TAppr.SCRIPT_TYPE) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.SCRIPT_TYPE) "+ val, strBufPending, data.getJoinType());
						break;

					case "variableStatus":
						CommonUtils.addToQuerySearch(" upper(TAppr.VARIABLE_STATUS_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.VARIABLE_STATUS_DESC) "+ val, strBufPending, data.getJoinType());
						break;

					case "recordIndicatorDesc":
						CommonUtils.addToQuerySearch(" upper(TAppr.RECORD_INDICATOR_DESC) "+ val, strBufApprove, data.getJoinType());
						CommonUtils.addToQuerySearch(" upper(TPend.RECORD_INDICATOR_DESC) "+ val, strBufPending, data.getJoinType());
						break;

						default:
					}
					count++;
				}
			}
			String orderBy=" Order By DATE_LAST_MODIFIED_1  DESC ";
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
	public List<VisionDynamicHashVariablesVb> getQueryResults(VisionDynamicHashVariablesVb dObj, int intStatus){

		List<VisionDynamicHashVariablesVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		setServiceDefaults();
		String strQueryAppr = new String("Select TAppr.VARIABLE_NAME," +
				"TAppr.VARIABLE_TYPE_NT, TAppr.VARIABLE_TYPE," +VariableTypeNtApprDesc+
				",TAppr.SCRIPT_TYPE_AT, TAppr.SCRIPT_TYPE,TAppr.VARIABLE_SCRIPT, "+ScriptTypeNtApprDesc
				+ ",TAppr.SORT_ORDER,TAppr.VARIABLE_STATUS_NT," +
				"TAppr.VARIABLE_STATUS, "+VariableStatusNtApprDesc
				+ ", TAppr.RECORD_INDICATOR_NT," +
				"TAppr.RECORD_INDICATOR, "+RecordIndicatorNtApprDesc
				+ ", TAppr.MAKER, "+makerApprDesc
				+ ", TAppr.VERIFIER," +verifierApprDesc+
				", TAppr.INTERNAL_STATUS, "+dateFormat+"(TAppr.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED," +
				dateFormat+"(TAppr.DATE_CREATION, "+dateFormatStr+") DATE_CREATION" +
				" From VISION_DYNAMIC_HASH_VAR TAppr " + 
				"Where TAppr.VARIABLE_NAME = ?");
			String strQueryPend = new String("Select TPend.VARIABLE_NAME," +
				"TPend.VARIABLE_TYPE_NT, TPend.VARIABLE_TYPE," +VariableTypeNtPendDesc+
				",TPend.SCRIPT_TYPE_AT, TPend.SCRIPT_TYPE,TPend.VARIABLE_SCRIPT, "+ScriptTypeNtPendDesc
				+ ",TPend.SORT_ORDER,TPend.VARIABLE_STATUS_NT," +
				"TPend.VARIABLE_STATUS, "+VariableStatusNtPendDesc
				+ ", TPend.RECORD_INDICATOR_NT," +
				"TPend.RECORD_INDICATOR, "+RecordIndicatorNtPendDesc
				+ ", TPend.MAKER, "+makerPendDesc
				+ ", TPend.VERIFIER," +verifierPendDesc+
				", TPend.INTERNAL_STATUS, "+dateFormat+"(TPend.DATE_LAST_MODIFIED, "+dateFormatStr+") DATE_LAST_MODIFIED," +
				dateFormat+"(TPend.DATE_CREATION, "+dateFormatStr+") DATE_CREATION" +
				" From VISION_DYNAMIC_HASH_VAR_PEND TPend " + 
			"Where TPend.VARIABLE_NAME = ?");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getVariableName());//[VARIABLE]

		try
		{if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
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
	protected List<VisionDynamicHashVariablesVb> selectApprovedRecord(VisionDynamicHashVariablesVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	@Override
	protected List<VisionDynamicHashVariablesVb> doSelectPendingRecord(VisionDynamicHashVariablesVb vObject){
		return getQueryResults(vObject, Constants.STATUS_PENDING);
	}
	@Override
	protected int getStatus(VisionDynamicHashVariablesVb records){return records.getVariableStatus();}
	@Override
	protected void setStatus(VisionDynamicHashVariablesVb vObject,int status){vObject.setVariableStatus(status);}
	@Override
	protected int doInsertionAppr(VisionDynamicHashVariablesVb vObject){
		String query = "Insert Into VISION_DYNAMIC_HASH_VAR ( VARIABLE_NAME, VARIABLE_TYPE_NT, VARIABLE_TYPE,"+
			"SCRIPT_TYPE_AT, SCRIPT_TYPE, VARIABLE_SCRIPT, SORT_ORDER, VARIABLE_STATUS_NT, VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR,"+
			"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"
			+ " Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+","+systemDate+")";
		Object[] args = {vObject.getVariableName(), vObject.getVariableTypeNt(), vObject.getVariableType(), vObject.getScriptTypeAt(),
				vObject.getScriptType(), vObject.getVariableScript(), vObject.getSortOrder(), vObject.getVariableStatusNt(), vObject.getVariableStatus(),vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus()};  
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionPend(VisionDynamicHashVariablesVb vObject){
		String query = "Insert Into VISION_DYNAMIC_HASH_VAR_PEND (VARIABLE_NAME, VARIABLE_TYPE_NT, VARIABLE_TYPE,"+
			"SCRIPT_TYPE_AT, SCRIPT_TYPE,VARIABLE_SCRIPT,SORT_ORDER,VARIABLE_STATUS_NT, VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR,"+
			"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "
			+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "+systemDate+","+systemDate+")";
		Object[] args = {vObject.getVariableName(), vObject.getVariableTypeNt(), vObject.getVariableType(), vObject.getScriptTypeAt(),
				vObject.getScriptType(), vObject.getVariableScript(), vObject.getSortOrder(),vObject.getVariableStatusNt(), vObject.getVariableStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus()};
		return getJdbcTemplate().update(query,args);	
	}
	@Override
	protected int doInsertionPendWithDc(VisionDynamicHashVariablesVb vObject){
		String query = "Insert Into VISION_DYNAMIC_HASH_VAR_PEND ( VARIABLE_NAME, VARIABLE_TYPE_NT, VARIABLE_TYPE,"+
			"SCRIPT_TYPE_AT, SCRIPT_TYPE,VARIABLE_SCRIPT,SORT_ORDER,VARIABLE_STATUS_NT, VARIABLE_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR,"+
			"MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION) "
			+ "Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, "+systemDate+","+dateTimeConvert+")";
		Object[] args = {vObject.getVariableName(), vObject.getVariableTypeNt(), vObject.getVariableType(), vObject.getScriptTypeAt(),
				vObject.getScriptType(), vObject.getVariableScript(), vObject.getSortOrder(),vObject.getVariableStatusNt(), vObject.getVariableStatus(), vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus(), vObject.getDateCreation()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(VisionDynamicHashVariablesVb vObject){
		String query = "Update VISION_DYNAMIC_HASH_VAR Set VARIABLE_TYPE_NT = ?, VARIABLE_TYPE = ?, SCRIPT_TYPE_AT = ?, " +
				"SCRIPT_TYPE = ?,VARIABLE_SCRIPT=?, SORT_ORDER=?, VARIABLE_STATUS_NT=?,VARIABLE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, " +
				"INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+" Where VARIABLE_NAME = ?";
		Object[] args = {vObject.getVariableTypeNt(), vObject.getVariableType(), vObject.getScriptTypeAt(),
				vObject.getScriptType(), vObject.getVariableScript(), vObject.getSortOrder(),vObject.getVariableStatusNt(),
				vObject.getVariableStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus(),vObject.getVariableName()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(VisionDynamicHashVariablesVb vObject){
		String query = "Update VISION_DYNAMIC_HASH_VAR_PEND Set  VARIABLE_TYPE_NT = ?, VARIABLE_TYPE = ?, SCRIPT_TYPE_AT = ?, " +
				"SCRIPT_TYPE = ?,VARIABLE_SCRIPT=?, SORT_ORDER=?,VARIABLE_STATUS_NT=?,VARIABLE_STATUS = ?, RECORD_INDICATOR_NT = ?, RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, " +
				"INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = "+systemDate+" Where VARIABLE_NAME = ?";
		Object[] args = {vObject.getVariableTypeNt(), vObject.getVariableType(), vObject.getScriptTypeAt(),
				vObject.getScriptType(), vObject.getVariableScript(), vObject.getSortOrder(),vObject.getVariableStatusNt(),
				vObject.getVariableStatus(), vObject.getRecordIndicatorNt(),
				vObject.getRecordIndicator(), vObject.getMaker(),
				vObject.getVerifier(), vObject.getInternalStatus(),vObject.getVariableName()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doDeleteAppr(VisionDynamicHashVariablesVb vObject){
		String query = "Delete From VISION_DYNAMIC_HASH_VAR Where VARIABLE_NAME = ?";
		Object[] args = {vObject.getVariableName()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int deletePendingRecord(VisionDynamicHashVariablesVb vObject){
		String query = "Delete From VISION_DYNAMIC_HASH_VAR_PEND Where VARIABLE_NAME = ?";
		Object[] args = {vObject.getVariableName()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected String frameErrorMessage(VisionDynamicHashVariablesVb vObject, String strOperation){
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try{
			
			strErrMsg =  strErrMsg + "VARIABLE_NAME:" + vObject.getVariableName();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
			// Return back the error message string
			return strErrMsg;
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}
	@Override
	protected String getAuditString(VisionDynamicHashVariablesVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		StringBuffer strAudit = new StringBuffer("");
		
		if(ValidationUtil.isValid(vObject.getVariableName()))
			strAudit.append("VARIABLE_NAME"+auditDelimiterColVal+vObject.getVariableName().trim());
		else
			strAudit.append("VARIABLE_NAME"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		strAudit.append("VARIABLE_TYPE_NT"+auditDelimiterColVal+vObject.getVariableTypeNt());
		strAudit.append(auditDelimiter);
		
		strAudit.append("VARIABLE_TYPE"+auditDelimiterColVal+vObject.getVariableType());
		strAudit.append(auditDelimiter);
		
		strAudit.append("SCRIPT_TYPE_AT"+auditDelimiterColVal+vObject.getScriptTypeAt());
		strAudit.append(auditDelimiter);
		
		strAudit.append("SCRIPT_TYPE"+auditDelimiterColVal+vObject.getScriptType());
		strAudit.append(auditDelimiter);
		
		if(ValidationUtil.isValid(vObject.getVariableScript()))
			strAudit.append("VARIABLE_SCRIPT"+auditDelimiterColVal+vObject.getVariableScript().trim());
		else
			strAudit.append("VARIABLE_SCRIPT"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		strAudit.append("SORT_ORDER"+auditDelimiterColVal+vObject.getSortOrder());
		strAudit.append(auditDelimiter);
		
		if(vObject.getVariableStatus()== -1)
			vObject.setVariableStatus(0);
		strAudit.append("VARIABLE_STATUS"+auditDelimiterColVal+vObject.getVariableStatus());
		strAudit.append(auditDelimiter);
		strAudit.append("RECORD_INDICATOR_NT"+auditDelimiterColVal+vObject.getRecordIndicatorNt());
		strAudit.append(auditDelimiter);
		if(vObject.getRecordIndicator()== -1)
			vObject.setRecordIndicator(0);
		strAudit.append("RECORD_INDICATOR"+auditDelimiterColVal+vObject.getRecordIndicator());
		strAudit.append(auditDelimiter);
		strAudit.append("MAKER"+auditDelimiterColVal+vObject.getMaker());
		strAudit.append(auditDelimiter);
		strAudit.append("VERIFIER"+auditDelimiterColVal+vObject.getVerifier());
		strAudit.append(auditDelimiter);
		strAudit.append("INTERNAL_STATUS"+auditDelimiterColVal+vObject.getInternalStatus());
		strAudit.append(auditDelimiter);
		if(ValidationUtil.isValid(vObject.getDateLastModified()))
			strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+vObject.getDateLastModified().trim());
		else
			strAudit.append("DATE_LAST_MODIFIED"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);

		if(ValidationUtil.isValid(vObject.getDateCreation()))
			strAudit.append("DATE_CREATION"+auditDelimiterColVal+vObject.getDateCreation().trim());
		else
			strAudit.append("DATE_CREATION"+auditDelimiterColVal+"NULL");
		strAudit.append(auditDelimiter);
		return strAudit.toString();
	}
	
	public String getVisionBusinessDate(String connectivityDetails, String dataBaseColumn){
		  Object params[] = new Object[6];
		  params[0] = connectivityDetails;
		  params[1] = dataBaseColumn;
		  params[2] = connectivityDetails;
		  params[3] = connectivityDetails;
		  params[4] = dataBaseColumn;
		  params[5] = dataBaseColumn;
		  String returnValue =getJdbcTemplate().queryForObject("SELECT "+
		       " SUBSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),1,INSTR(SUBSTR(STR,INSTR(STR,'$@!',1)+3),'#}')-1) ParamVar"+
		       " FROM "+
		       " (SELECT"+ 
		       " Case When INSTR( ? , ? ) > 0 Then"+ 
		       " SUBSTR( ? ,INSTR( ? , ? )+LENGTH( ? ))"+ 
		       " Else"+
		       " NULL"+
		       " End STR"+
		       " FROM DUAL)",
		      params,String.class);
		  return (!ValidationUtil.isValid(returnValue) ? "" : returnValue);
		 }
	@Override
	protected void setServiceDefaults(){
		serviceName = "visionDynamicHashVariables";
		serviceDesc = CommonUtils.getResourceManger().getString("visionDynamicHashVariables");
		tableName = "VISION_DYNAMIC_HASH_VAR";
		childTableName = "VISION_DYNAMIC_HASH_VAR";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	public List<VisionDynamicHashVariablesVb> getDisplayTagList(String macroVarType, String ConnectorScript) throws DataAccessException {
		String macrovarName;

		
			macrovarName = "DB_DETAILS";
			if (!ValidationUtil.isValid(macroVarType))
				macroVarType = "ORACLE";

		String sql = " SELECT MACROVAR_NAME, MACROVAR_TYPE, TAG_NAME,  DISPLAY_NAME,  MASKED_FLAG,  MANDATORY_FLAG, ENCRYPTION, "
				+ " SUBSTR (SUBSTR (STR, INSTR (STR, '$@!', 1) + 3),  1, " + " INSTR (SUBSTR (STR, INSTR (STR, '$@!', 1) + 3), '#}') - 1)  VALUE "
				+ " FROM (SELECT X1.MACROVAR_NAME, X1.MACROVAR_TYPE, X1.TAG_NAME, X1.TAG_NO, X1.DISPLAY_NAME, "
				+ " X1.MASKED_FLAG, X1.MANDATORY_FLAG, X1.ENCRYPTION,  (SELECT CASE  WHEN INSTR (  ?,"
				+ " REPLACE ( (TAG_NAME), ' ', '_')) > 0  THEN  SUBSTR (  ?,  INSTR (  ?, "
				+ " REPLACE ( (TAG_NAME), ' ', '_'))  + LENGTH (REPLACE ( (TAG_NAME), ' ', '_')))  ELSE " + " NULL  END  STR  FROM DUAL)  STR  FROM MACROVAR_TAGGING X1 "
				+ " WHERE X1.MACROVAR_NAME = ?  AND X1.MACROVAR_TYPE = ?)  ORDER BY TAG_NO ";
		if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
			sql = " Select MACROVAR_NAME,MACROVAR_TYPE,TAG_NAME,DISPLAY_NAME,MASKED_FLAG,ENCRYPTION,MANDATORY_FLAG,  " + 
					"				 Substring(Substring(STR,CharIndex('$@!',STR,1)+3, Len(STR)),1,CharIndex('#}',  " + 
					"				 Substring(STR,CharIndex('$@!',STR,1)+3, Len(STR)))-1) Value   " + 
					"				 From   " + 
					"				 (SELECT X1.MACROVAR_NAME,X1.MACROVAR_TYPE,X1.TAG_NAME,X1.TAG_NO,X1.DISPLAY_NAME,X1.MASKED_FLAG,X1.ENCRYPTION,X1.MANDATORY_FLAG,   " + 
					"				 (SELECT Case When CharIndex(REPLACE((TAG_NAME),' ','_'), ?) > 0       Then Substring(? ,  " + 
					"				 CharIndex(REPLACE((TAG_NAME),' ','_'), ?)+LEN(REPLACE((TAG_NAME),' ','_')), Len(?))  " + 
					"				 Else NULL End STR) STR FROM MACROVAR_TAGGING X1  " + 
					"				 WHERE X1.MACROVAR_NAME = ? and X1.MACROVAR_TYPE = ?) T1 ORDER BY TAG_NO";
		}
		Object[] lParams = null;
		
		if ("ORACLE".equalsIgnoreCase(databaseType)) {
			lParams = new Object[5];
			lParams[0] = ConnectorScript;
			lParams[1] = ConnectorScript;
			lParams[2] = ConnectorScript;
			lParams[3] = macrovarName;
			lParams[4] = macroVarType;
		} else if ("MSSQL".equalsIgnoreCase(databaseType)) {
			lParams = new Object[6];
			lParams[0] = ConnectorScript;
			lParams[1] = ConnectorScript;
			lParams[2] = ConnectorScript;
			lParams[3] = ConnectorScript;
			lParams[4] = macrovarName;
			lParams[5] = macroVarType;
		}

		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				VisionDynamicHashVariablesVb vObject = new VisionDynamicHashVariablesVb();
				vObject.setMacroName(rs.getString("MACROVAR_NAME"));
				vObject.setConnectorType(rs.getString("MACROVAR_TYPE"));
				vObject.setTagName(rs.getString("TAG_NAME"));
				vObject.setTagValue(rs.getString("Value"));
				vObject.setDisplayName(rs.getString("DISPLAY_NAME"));
				vObject.setMaskedFlag(rs.getString("MASKED_FLAG"));
				vObject.setEncryption(rs.getString("ENCRYPTION"));
				vObject.setMandatoryFlag(rs.getString("MANDATORY_FLAG"));
				return vObject;
			}
		};
		return getJdbcTemplate().query(sql, lParams, mapper);
	}
	public String getScriptValue(String variableName,Boolean flag) {
		Object params[] = new Object[1];
		params[0] = variableName;
		String scriptType = "";
		String query = "select VARIABLE_SCRIPT from VISION_DYNAMIC_HASH_VAR WHERE UPPER(VARIABLE_NAME)=UPPER(?)";
	if(!flag)
		query = "select VARIABLE_SCRIPT from VISION_DYNAMIC_HASH_VAR_PEND WHERE UPPER(VARIABLE_NAME)=UPPER(?)";
		try {
		String returnValue = getJdbcTemplate().queryForObject(query,
				params, String.class);
		return returnValue;
		}catch(Exception e) {
			return null;
		}
	}
}