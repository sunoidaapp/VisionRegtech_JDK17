package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CronStatusVb;
@Component
public class CronStatusDao extends AbstractDao<CronStatusVb> {
	@Value("${app.databaseType}")
	private String databaseType;
	
	@SuppressWarnings("unchecked")
	public List<CronStatusVb> findEnvironment() throws DataAccessException {
		String sql = " SELECT "+
				" DISTINCT CASE WHEN SERVER_ENVIRONMENT = 'UAT' THEN 2 "+
				" WHEN SERVER_ENVIRONMENT = 'PRODUCTION' THEN 1 "+
				" WHEN SERVER_ENVIRONMENT = 'DRSITE' THEN 3 "+
				" else 99 END AS ORDERING, "+
				" ALPHA_SUBTAB_DESCRIPTION as server_environment from vision_node_credentials t1, alpha_sub_tab t2 "+
				" where t1.server_environment = t2.alpha_sub_tab "+
				" and t2.alpha_tab = '1289'  ORDER BY ORDERING ";
		return  getJdbcTemplate().query(sql,  getEnvironmentMapper());
	}
	protected RowMapper getEnvironmentMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CronStatusVb cronStatusVb = new CronStatusVb();
				cronStatusVb.setServerEnvironment(rs.getString("server_environment"));
				return cronStatusVb;
			}
		};
		return mapper;
	}
	
	@SuppressWarnings("unchecked")
	public List<CronStatusVb> findCronStatusList(CronStatusVb dObj, String env) throws DataAccessException {
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("select replace(t1.variable,'_'+t2.SERVER_NAME+'_'+t2.NODE_NAME,'') VARIABLE, "
					+ "t1.VARIABLE_STATUS_NT, t1.VALUE, t2.SERVER_ENVIRONMENT ,t2.SERVER_NAME , t2.NODE_NAME, t2.NODE_NAME+'('+t2.NODE_IP+')' NODE_IP,"
					+ " t2.NODE_USER, t2.NODE_PWD ,t2.MAKER ,t2.VERIFIER, t2.RECORD_INDICATOR_NT , "
					+ "t2.RECORD_INDICATOR ,t2.INTERNAL_STATUS ,t2.DATE_LAST_MODIFIED , t2.DATE_CREATION  from vision_variables t1,VISION_NODE_CREDENTIALS t2 where  " +
					" (t1.variable like 'BUILD_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'PING_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'ADF_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'NON_ADF_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_EMAIL_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_SMS_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'TEMPLATE_DESIGN_'+t2.SERVER_NAME+'_'+t2.NODE_NAME) " + 
					" and t2.server_environment = ? ");
		}else {
			strBufApprove = new StringBuffer("select replace(t1.variable,'_'||t2.SERVER_NAME||'_'||t2.NODE_NAME,'') VARIABLE, "
					+ "t1.VARIABLE_STATUS_NT, t1.VALUE, t2.SERVER_ENVIRONMENT ,t2.SERVER_NAME , t2.NODE_NAME, t2.NODE_NAME||'('||t2.NODE_IP||')' NODE_IP,"
					+ " t2.NODE_USER, t2.NODE_PWD ,t2.MAKER ,t2.VERIFIER, t2.RECORD_INDICATOR_NT , "
					+ "t2.RECORD_INDICATOR ,t2.INTERNAL_STATUS ,t2.DATE_LAST_MODIFIED , t2.DATE_CREATION  from vision_variables t1,VISION_NODE_CREDENTIALS t2 where  " +
					" (t1.variable like 'BUILD_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'PING_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'ADF_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'NON_ADF_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_EMAIL_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_SMS_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'TEMPLATE_DESIGN_'||t2.SERVER_NAME||'_'||t2.NODE_NAME) " + 
					" and t2.server_environment = ? ");
		}
		Vector<Object> params = new Vector<Object>();
		params.add(env);
		String orderBy = " order by  SERVER_ENVIRONMENT ";
		return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, new String(), orderBy, params);
	}
	@Override 
	
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CronStatusVb cronStatusVb = new CronStatusVb();
				cronStatusVb.setServerName(rs.getString("SERVER_NAME"));
				cronStatusVb.setServerEnvironment(rs.getString("SERVER_ENVIRONMENT"));
				cronStatusVb.setNodeIp(rs.getString("NODE_IP"));
				cronStatusVb.setNodeName(rs.getString("NODE_NAME"));
				cronStatusVb.setNodeUser(rs.getString("NODE_USER"));
				cronStatusVb.setNodePwd(rs.getString("NODE_PWD"));
				cronStatusVb.setCronName(rs.getString("VARIABLE"));
				cronStatusVb.setCronStatusAt(rs.getInt("VARIABLE_STATUS_NT"));
				cronStatusVb.setCronStatus(rs.getString("VALUE"));
				cronStatusVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				cronStatusVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				cronStatusVb.setMaker(rs.getInt("MAKER"));
				cronStatusVb.setVerifier(rs.getInt("VERIFIER"));
				cronStatusVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				cronStatusVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				cronStatusVb.setDateCreation(rs.getString("DATE_CREATION"));
				return cronStatusVb;
			}
		};
		return mapper;
	}
	
	protected RowMapper getMapper1(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CronStatusVb cronStatusVb = new CronStatusVb();
				cronStatusVb.setServerName(rs.getString("SERVER_NAME"));
				cronStatusVb.setServerEnvironment(rs.getString("SERVER_ENVIRONMENT"));
				cronStatusVb.setNodeIp(rs.getString("NODE_IP"));
				cronStatusVb.setNodeName(rs.getString("NODE_NAME"));
				cronStatusVb.setNodeUser(rs.getString("NODE_USER"));
				cronStatusVb.setNodePwd(rs.getString("NODE_PWD"));
				cronStatusVb.setCronName(rs.getString("VARIABLE"));
				cronStatusVb.setCronStatusAt(rs.getInt("VARIABLE_STATUS_NT"));
				cronStatusVb.setCronStatus(rs.getString("VALUE"));
				cronStatusVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				cronStatusVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				cronStatusVb.setMaker(rs.getInt("MAKER"));
				cronStatusVb.setVerifier(rs.getInt("VERIFIER"));
				cronStatusVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				cronStatusVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				cronStatusVb.setDateCreation(rs.getString("DATE_CREATION"));
				return cronStatusVb;
			}
		};
		return mapper;
	}
	
	public RowMapper getQueryPopupMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CronStatusVb cronStatusVb = new CronStatusVb();
				cronStatusVb.setServerEnvironment(rs.getString("SERVER_ENVIRONMENT"));
				return cronStatusVb;
			}
		};
		return mapper;
	}
	

	public List<CronStatusVb> getQueryPopupResults(CronStatusVb dObj){
		setServiceDefaults();
		dObj.setVerificationRequired(false);
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("select replace(t1.variable,'_'||t2.SERVER_NAME||'_'||t2.NODE_NAME,'') VARIABLE, "
				+ "t1.VARIABLE_STATUS_NT, t1.VALUE, t2.SERVER_ENVIRONMENT ,t2.SERVER_NAME , t2.NODE_NAME, t2.NODE_NAME||'('||t2.NODE_IP||')' NODE_IP,"
				+ " t2.NODE_USER, t2.NODE_PWD ,t2.MAKER ,t2.VERIFIER, t2.RECORD_INDICATOR_NT , "
				+ "t2.RECORD_INDICATOR ,t2.INTERNAL_STATUS ,t2.DATE_LAST_MODIFIED , t2.DATE_CREATION  from vision_variables t1,VISION_NODE_CREDENTIALS t2 where  " +
				" (t1.variable like 'BUILD_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " +
				" or  " + 
				" t1.variable like 'PING_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " +
				" or  " + 
				" t1.variable like 'ADF_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'NON_ADF_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'ADF_ALERT_EMAIL_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'ADF_ALERT_SMS_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'TEMPLATE_DESIGN_'||t2.SERVER_NAME||'_'||t2.NODE_NAME) ");
		}else {
			strBufApprove = new StringBuffer("select replace(t1.variable,'_'+t2.SERVER_NAME+'_'+t2.NODE_NAME,'') VARIABLE, "
					+ "t1.VARIABLE_STATUS_NT, t1.VALUE, t2.SERVER_ENVIRONMENT ,t2.SERVER_NAME , t2.NODE_NAME, t2.NODE_NAME+'('+t2.NODE_IP+')' NODE_IP,"
					+ " t2.NODE_USER, t2.NODE_PWD ,t2.MAKER ,t2.VERIFIER, t2.RECORD_INDICATOR_NT , "
					+ "t2.RECORD_INDICATOR ,t2.INTERNAL_STATUS ,t2.DATE_LAST_MODIFIED , t2.DATE_CREATION  from vision_variables t1,VISION_NODE_CREDENTIALS t2 where  " +
					" (t1.variable like 'BUILD_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'PING_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'ADF_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'NON_ADF_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_EMAIL_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_SMS_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'TEMPLATE_DESIGN_'+t2.SERVER_NAME+'_'+t2.NODE_NAME) ");
		}
		try
		{
			if (ValidationUtil.isValid(dObj.getServerEnvironment()) && !"-1".equalsIgnoreCase(dObj.getServerEnvironment()))
			{
				params.addElement(dObj.getServerEnvironment().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.SERVER_ENVIRONMENT) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getServerName()) && !"-1".equalsIgnoreCase(dObj.getServerName()))
			{
				params.addElement(dObj.getServerName().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.SERVER_NAME) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getNodeName()))
			{
				params.addElement(dObj.getNodeName().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.NODE_NAME) = ?", strBufApprove);
			}
			String orderBy = " order by SERVER_ENVIRONMENT ";
			return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, new String(), orderBy, params, getMapper());
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is null":strBufApprove.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}

	public List<CronStatusVb> getQueryResults(CronStatusVb dObj){
		setServiceDefaults();
		dObj.setVerificationRequired(false);
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("select replace(t1.variable,'_'||t2.SERVER_NAME||'_'||t2.NODE_NAME,'') VARIABLE, "
				+ "t1.VARIABLE_STATUS_NT, t1.VALUE, t2.SERVER_ENVIRONMENT ,t2.SERVER_NAME , t2.NODE_NAME, t2.NODE_IP ,"
				+ " t2.NODE_USER, t2.NODE_PWD ,t2.MAKER ,t2.VERIFIER, t2.RECORD_INDICATOR_NT , "
				+ "t2.RECORD_INDICATOR ,t2.INTERNAL_STATUS ,t2.DATE_LAST_MODIFIED , t2.DATE_CREATION  from vision_variables t1,VISION_NODE_CREDENTIALS t2 where  " +
				" (t1.variable like 'BUILD_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " +
				" or  " + 
				" t1.variable like 'PING_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " +
				" or  " + 
				" t1.variable like 'ADF_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'NON_ADF_CRON_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'ADF_ALERT_EMAIL_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'ADF_ALERT_SMS_'||t2.SERVER_NAME||'_'||t2.NODE_NAME " + 
				" or " + 
				" t1.variable like 'TEMPLATE_DESIGN_'||t2.SERVER_NAME||'_'||t2.NODE_NAME) ");
		}else {
			strBufApprove = new StringBuffer("select replace(t1.variable,'_'+t2.SERVER_NAME+'_'+t2.NODE_NAME,'') VARIABLE, "
					+ "t1.VARIABLE_STATUS_NT, t1.VALUE, t2.SERVER_ENVIRONMENT ,t2.SERVER_NAME , t2.NODE_NAME, t2.NODE_IP ,"
					+ " t2.NODE_USER, t2.NODE_PWD ,t2.MAKER ,t2.VERIFIER, t2.RECORD_INDICATOR_NT , "
					+ "t2.RECORD_INDICATOR ,t2.INTERNAL_STATUS ,t2.DATE_LAST_MODIFIED , t2.DATE_CREATION  from vision_variables t1,VISION_NODE_CREDENTIALS t2 where  " +
					" (t1.variable like 'BUILD_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'PING_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " +
					" or  " + 
					" t1.variable like 'ADF_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'NON_ADF_CRON_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_EMAIL_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'ADF_ALERT_SMS_'+t2.SERVER_NAME+'_'+t2.NODE_NAME " + 
					" or " + 
					" t1.variable like 'TEMPLATE_DESIGN_'+t2.SERVER_NAME+'_'+t2.NODE_NAME) ");
		}
		try
		{
			if (ValidationUtil.isValid(dObj.getServerEnvironment()) && !"-1".equalsIgnoreCase(dObj.getServerEnvironment()))
			{
				params.addElement(dObj.getServerEnvironment().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.SERVER_ENVIRONMENT) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getServerName()) && !"-1".equalsIgnoreCase(dObj.getServerName()))
			{
				params.addElement(dObj.getServerName().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.SERVER_NAME) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getNodeName()))
			{
				params.addElement(dObj.getNodeName().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.NODE_NAME) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getNodeName()))
			{
				params.addElement(dObj.getNodeName().toUpperCase());
				CommonUtils.addToQuery("UPPER(t2.NODE_NAME) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getCronName()))
			{
				params.addElement(dObj.getCronName().toUpperCase());
				CommonUtils.addToQuery(" UPPER(replace(t1.variable,'_'||t2.SERVER_NAME||'_'||t2.NODE_NAME,'')) = ?", strBufApprove);
			}
			String orderBy = " order by SERVER_ENVIRONMENT ";
			return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, new String(), orderBy, params, getMapper1());
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is null":strBufApprove.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}

	protected RowMapper getQueryListMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CronStatusVb cronStatusVb = new CronStatusVb();
				cronStatusVb.setServerName(rs.getString("SERVER_NAME"));
				cronStatusVb.setServerName(rs.getString("SERVER_ENVIRONMENT"));
				cronStatusVb.setServerName(rs.getString("NODE_IP"));
				cronStatusVb.setNodeName(rs.getString("NODE_NAME"));
				cronStatusVb.setNodeName(rs.getString("NODE_USER"));
				cronStatusVb.setNodeName(rs.getString("NODE_PWD"));
				cronStatusVb.setCronName(rs.getString("CRON_NAME"));
				cronStatusVb.setCronStatusAt(rs.getInt("CRON_STATUS_AT"));
				cronStatusVb.setCronStatus(rs.getString("CRON_STATUS"));
				return cronStatusVb;
			}
		};
		return mapper;
	}

	public long getLastFetchIntervel(){
		String strBufApprove = new String("SELECT round((sysdate - LAST_FETCH_TIME) * (24 * 60))  Fetch_Intvl " +
				" FROM BUILD_CRON_FETCH_DET ");
		long count = 3;
		try
		{
//			return getJdbcTemplate().queryForLong(strBufApprove);
			count = getJdbcTemplate().queryForObject(strBufApprove, Long.class);
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			return 3;
		}
		return count;
	}
	
	@Override
	protected List<CronStatusVb> selectApprovedRecord(CronStatusVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<CronStatusVb> doSelectPendingRecord(CronStatusVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected void setServiceDefaults(){
		serviceName = "CronStatus";
		serviceDesc = "Cron Status"; //CommonUtils.getResourceManger().getString("cronStatus");
		tableName = "VISION_NODE_CREDENTIALS";
		childTableName = "VISION_NODE_CREDENTIALS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}


	@Override
	protected String frameErrorMessage(CronStatusVb vObject, String strOperation)
	{
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg =  strErrMsg + " SERVER_NAME:" + vObject.getServerName();
			strErrMsg =  strErrMsg + " NODE_NAME:" + vObject.getNodeName();
			strErrMsg =  strErrMsg + " CRON_NAME:" + vObject.getCronName();
			strErrMsg =  strErrMsg + " CRON_STATUS:" + vObject.getCronStatus();
			// Now concatenate the error message that has been sent
			if ("Approve".equalsIgnoreCase(strOperation))
				strErrMsg = strErrMsg + " failed during approve Operation. Bulk Approval aborted !!";
			else
				strErrMsg = strErrMsg + " failed during reject Operation. Bulk Rejection aborted !!";
		}catch(Exception ex){
			strErrorDesc = ex.getMessage();
			strErrMsg = strErrMsg + strErrorDesc;
			logger.error(strErrMsg, ex);
		}
		// Return back the error message string
		return strErrMsg;
	}


	@Override
	protected String getAuditString(CronStatusVb vObject)
	{
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			if(vObject.getServerEnvironment() != null)
				strAudit.append(vObject.getServerEnvironment().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");
			if(vObject.getServerName() != null)
				strAudit.append(vObject.getServerName().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");
			
			if(vObject.getNodeName() != null)
				strAudit.append(vObject.getNodeName().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");
			
			if(vObject.getCronName() != null)
				strAudit.append(vObject.getCronName().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			strAudit.append(vObject.getCronStatusAt());
			strAudit.append("!|#");
			strAudit.append(vObject.getCronStatus());
			strAudit.append("!|#");

/*			if(vObject.getExpandFlag() != null)
				strAudit.append(vObject.getExpandFlag().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");
*/
			strAudit.append(vObject.getRecordIndicatorNt());
			strAudit.append("!|#");
			strAudit.append(vObject.getRecordIndicator());
			strAudit.append("!|#");
			strAudit.append(vObject.getMaker());
			strAudit.append("!|#");
			strAudit.append(vObject.getVerifier());
			strAudit.append("!|#");
			strAudit.append(vObject.getInternalStatus());
			strAudit.append("!|#");
			if(vObject.getDateLastModified() != null)
				strAudit.append(vObject.getDateLastModified().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			if(vObject.getDateCreation() != null)
				strAudit.append(vObject.getDateCreation().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

		}
		catch(Exception ex)
		{
			strErrorDesc = ex.getMessage();
			strAudit = strAudit.append(strErrorDesc);
			ex.printStackTrace();
		}
		return strAudit.toString();
	}
	public int doUpdateStartCron(String cronName){
		String query = "UPDATE VISION_VARIABLES Set VALUE = 'RUNNING' Where Variable ='"+cronName+"' ";
		int count=0;
		try{
			count = getJdbcTemplate().update(query);
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	public int doUpdateStopCron(String cronName, String statusValue){
		String query = "UPDATE VISION_VARIABLES Set VALUE = '"+statusValue+"' Where VARIABLE ='"+cronName+"' ";
		int count=0;
		try{
			count = getJdbcTemplate().update(query);
		}catch(Exception e){
			e.printStackTrace();
		}
		return count;
	}
	public int doUpdateAppr(CronStatusVb vObject){
		String query = "Update VISION_NODE_CREDENTIALS Set MAKER = ?, VERIFIER = ?, DATE_LAST_MODIFIED = GetDate() "
				+ "Where SERVER_ENVIRONMENT = ? AND SERVER_NAME  = ? AND NODE_NAME = ?";
		Object[] args = {intCurrentUserId,intCurrentUserId,vObject.getServerEnvironment(), vObject.getServerName(),vObject.getNodeName()};
		return getJdbcTemplate().update(query,args);
	}
	public int doInsertionAudit(CronStatusVb vObject, String status){
		String query = "";
			query = "Insert Into VISION_NODE_CREDENTIALS_AUDIT ( SERVER_ENVIRONMENT , SERVER_NAME , NODE_NAME , NODE_IP , "
					+ "NODE_USER , NODE_PWD , MAKER , VERIFIER, DATE_LAST_MODIFIED, DATE_CREATION,NODE_STATUS ,CRON_NAME )"+
					"Values (?, ?, ?, ?, ?,?,?,?, GetDate(), GetDate(), ?, ?)";
		Object[] args = {vObject.getServerEnvironment(), vObject.getServerName(), vObject.getNodeName(), vObject.getNodeIp(),
				vObject.getNodeUser(), vObject.getNodePwd(),vObject.getMaker(),
			vObject.getVerifier(), status, vObject.getCronName()};  
		return getJdbcTemplate().update(query,args);
	}	
}
