package com.vision.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BuildSchedulesDetailsVb;
import com.vision.vb.BuildSchedulesVb;
@Component
public class BuildSchedulesDao extends AbstractDao<BuildSchedulesVb> {
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	@Autowired
	private BuildControlsDao buildControlsDao;
	
	@Autowired
	private BuildSchedulesDetailsDao buildSchedulesDetailsDao;
	
	@Override
	protected void setServiceDefaults(){
		serviceName = "BuildSchedules";
		serviceDesc = CommonUtils.getResourceManger().getString("BuildSchedules");
		tableName = "BUILD_SCHEDULES";
		childTableName = "BUILD_SCHEDULES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	
	public long getLastFetchIntervel(){
		String strBufApprove = new String("SELECT DATEDIFF(MINUTE, LAST_FETCH_TIME,GetDate())  Fetch_Intvl  FROM BUILD_CRON_FETCH_DET ");
		try
		{
//			return getJdbcTemplate().queryForLong(strBufApprove);
			long count = getJdbcTemplate().queryForObject(strBufApprove, long.class);
			
			return count ;
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			return 3;
		}
	}
	protected RowMapper getQueryResultsMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BuildSchedulesVb buildSchedulesVb = new BuildSchedulesVb();
				buildSchedulesVb.setBuild(rs.getString("BUILD"));
				buildSchedulesVb.setScheduledDate(rs.getString("SCHEDULED_DATE"));
				buildSchedulesVb.setBuildNumber(rs.getString("BUILD_NUMBER"));
				buildSchedulesVb.setSubmitterId(rs.getInt("SUBMITTER_ID"));
				buildSchedulesVb.setSubmitterName(rs.getInt("SUBMITTER_ID")+" - "+rs.getString("SUBMITTER_Name"));
				buildSchedulesVb.setSupportContact(rs.getString("SUPPORT_CONTACT"));
				buildSchedulesVb.setNotify(rs.getString("NOTIFY"));
				buildSchedulesVb.setBuildScheduleStatusAt(rs.getInt("BUILD_SCHEDULE_STATUS_AT"));
				buildSchedulesVb.setBuildScheduleStatus(rs.getString("BUILD_SCHEDULE_STATUS"));
				buildSchedulesVb.setStatusDesc(rs.getString("BUILD_SCHEDULE_STATUS_DESC"));
				buildSchedulesVb.setParallelProcsCount(rs.getInt("PARALLEL_PROCS_COUNT"));
				buildSchedulesVb.setRecurringFrequencyAt(rs.getInt("RECURRING_FREQUENCY_AT"));
				buildSchedulesVb.setRecurringFrequency(rs.getString("RECURRING_FREQUENCY"));
				buildSchedulesVb.setStartTime(rs.getString("START_TIME"));
				buildSchedulesVb.setEndTime(rs.getString("END_TIME"));
				buildSchedulesVb.setDuration(rs.getString("Duration"));
				buildSchedulesVb.setCountry(rs.getString("COUNTRY"));
				buildSchedulesVb.setStakeHolder(rs.getString("STAKE_HOLDER"));
				buildSchedulesVb.setLeBook(rs.getString("LE_BOOK"));
				buildSchedulesVb.setDateCreation(rs.getString("DATE_CREATION"));
				buildSchedulesVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				buildSchedulesVb.setNode(rs.getString("NODE"));
				buildSchedulesVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				buildSchedulesVb.setFeedDate(rs.getString("FEED_DATE"));
				return buildSchedulesVb;
			}
		};
		return mapper;
	}
	public List<BuildSchedulesVb> getQueryResults(BuildSchedulesVb dObj, int status){
		setServiceDefaults();
		Vector<Object> params = new Vector<Object>();
		dObj.setMaxRecords(10000);
		StringBuffer strBufApprove = new StringBuffer(); 
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("Select TAppr.BUILD," +
				"Format(TAppr.SCHEDULED_DATE, 'dd-MMM-yyyy HH:mm:ss') SCHEDULED_DATE, TAppr.BUILD_NUMBER," +
				"TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT," +
				"   TAppr.submitter_id,V1.user_name submitter_name,  Format(TAppr.START_TIME, 'dd-MMM-yyyy HH:mm:ss') START_TIME," +
				"Format(TAppr.END_TIME, 'dd-MMM-yyyy HH:mm:ss') END_TIME, TAppr.BUILD_SCHEDULE_STATUS_AT," +
				"TAppr.BUILD_SCHEDULE_STATUS, "
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 206 AND ALPHA_SUB_TAB = TAppr.BUILD_SCHEDULE_STATUS) BUILD_SCHEDULE_STATUS_DESC,"
				+ "Format(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED," +
				"Format(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION, TAppr.COUNTRY," +
				"TAppr.LE_BOOK, "
				+" ( SELECT TAppr.LE_BOOK+' - '+LEB.LEB_SHORT_DESCRIPTION FROM LE_BOOK LEB WHERE LEB.COUNTRY =TAppr.COUNTRY AND LEB.LE_BOOK = TAppr.LE_BOOK) STAKE_HOLDER,  "
				+ "TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, " +
				" DATEDIFF(hour,start_time, GetDate() )  Duration "
				+ ", isnull(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, Format(TAppr.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, Format(TAppr.FEED_DATE, 'dd-MMM-yyyy') FEED_DATE " +
				" From BUILD_SCHEDULES TAppr, PROGRAMS TPAppr,Vision_users v1 Where TAppr.BUILD = TPAppr.PROGRAM and TPAppr.PROGRAM_TYPE = 'MAJORBLD' AND TAppr.SUBMITTER_ID = v1.VISION_ID ");
		}else {
			
			strBufApprove = new StringBuffer("Select TAppr.BUILD," +
					"To_Char(TAppr.SCHEDULED_DATE, 'dd-Mon-yyyy HH24:MI:SS') SCHEDULED_DATE, TAppr.BUILD_NUMBER," +
					"TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT," +
					"   TAppr.submitter_id,V1.user_name submitter_name,  To_Char(TAppr.START_TIME, 'dd-MMM-yyyy HH24:MI:SS') START_TIME," +
					"To_Char(TAppr.END_TIME, 'dd-Mon-yyyy HH24:MI:SS') END_TIME, TAppr.BUILD_SCHEDULE_STATUS_AT," +
					"TAppr.BUILD_SCHEDULE_STATUS, "
					+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 206 AND ALPHA_SUB_TAB = TAppr.BUILD_SCHEDULE_STATUS) BUILD_SCHEDULE_STATUS_DESC,"
					+ "To_Char(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED," +
					"To_Char(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION, TAppr.COUNTRY," +
					"TAppr.LE_BOOK, "
					+" ( SELECT TAppr.LE_BOOK+' - '+LEB.LEB_SHORT_DESCRIPTION FROM LE_BOOK LEB WHERE LEB.COUNTRY =TAppr.COUNTRY AND LEB.LE_BOOK = TAppr.LE_BOOK) STAKE_HOLDER,  "
					+ "TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, " +
					" round((sysdate - start_time) * (24 * 60))   Duration "
					+ ", isnull(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, To_Char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, To_Char(TAppr.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE " +
					" From BUILD_SCHEDULES TAppr, PROGRAMS TPAppr,Vision_users v1 Where TAppr.BUILD = TPAppr.PROGRAM and TPAppr.PROGRAM_TYPE = 'MAJORBLD' AND TAppr.SUBMITTER_ID = v1.VISION_ID ");			
		}
		try
		{
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			if (ValidationUtil.isValid(dObj.getCountry())){
				params.addElement(dObj.getCountry().toUpperCase());
				strBufApprove.append(" AND TAppr.COUNTRY = ? ");
			}
			if (ValidationUtil.isValid(CalLeBook)){
				params.addElement(CalLeBook);
				strBufApprove.append(" AND TAppr.LE_BOOK = ? ");
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())){
				strBufApprove.append(" AND TAppr.COUNTRY IN("+SessionContextHolder.getContext().getCountry().toUpperCase()+") ");
				strBufApprove.append(" AND TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getLeBook().toUpperCase()+") ");
			}	
			
			if (ValidationUtil.isValid(dObj.getBuild())  && !"-1".equalsIgnoreCase(dObj.getBuild())){
				params.addElement(dObj.getBuild().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.BUILD) = UPPER(?) ");
			}else if (ValidationUtil.isValid(dObj.getBuildName())  && !"-1".equalsIgnoreCase(dObj.getBuildName())){
				params.addElement(dObj.getBuildName().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.BUILD) = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getBuildNumber())){
				params.addElement(dObj.getBuildNumber());
				strBufApprove.append(" AND TAppr.BUILD_NUMBER = ?");
			}
			if (ValidationUtil.isValid(dObj.getSubmitterId())  && dObj.getSubmitterId()!=0){
				params.addElement(dObj.getSubmitterId());
				strBufApprove.append(" AND TAppr.SUBMITTER_ID = ? ");
			}
			if (ValidationUtil.isValid(dObj.getBuildScheduleStatus()) && !"-1".equalsIgnoreCase(dObj.getBuildScheduleStatus())){
				params.addElement(dObj.getBuildScheduleStatus());
				strBufApprove.append(" AND TAppr.BUILD_SCHEDULE_STATUS = UPPER(?) ");
			}else if (ValidationUtil.isValid(dObj.getStatus()) && !"-1".equalsIgnoreCase(dObj.getStatus())){
				params.addElement(dObj.getStatus());
				strBufApprove.append(" AND TAppr.BUILD_SCHEDULE_STATUS = UPPER(?) ");
			}
			
			if(ValidationUtil.isValid(dObj.getRecurringFrequency()) && !"-1".equalsIgnoreCase(dObj.getRecurringFrequency())){
				params.addElement(dObj.getRecurringFrequency().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.RECURRING_FREQUENCY) = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getNode()) && !"-1".equalsIgnoreCase(dObj.getNode())){
				params.addElement(dObj.getNode());
				strBufApprove.append(" AND isnull(node_override, node_request) = ? ");
			}
			String orderBy = " Order By BUILD, BUILD_NUMBER, SCHEDULED_DATE";
			return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, "", orderBy, params, getQueryResultsMapper());
			
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}
	
	public BuildSchedulesVb getQueryResultsForDetails(String buildNumber, String build)	{
		final int intKeyFieldsCount = 2;
		String strBufApprove = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strBufApprove = new String("Select TAppr.BUILD," +
			"Format(TAppr.SCHEDULED_DATE, 'dd-MMM-yyyy HH:mm:ss') SCHEDULED_DATE, TAppr.BUILD_NUMBER," +
			"TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT," +
			"  TAppr.submitter_id,v1.user_name submitter_name , Format(TAppr.START_TIME, 'dd-MMM-yyyy HH:mm:ss') START_TIME," +
			"Format(TAppr.END_TIME, 'dd-MMM-yyyy HH:mm:ss') END_TIME, TAppr.BUILD_SCHEDULE_STATUS_AT," +
			"TAppr.BUILD_SCHEDULE_STATUS,"
			+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 206 AND ALPHA_SUB_TAB = TAppr.BUILD_SCHEDULE_STATUS) BUILD_SCHEDULE_STATUS_DESC,"
			+ " Format(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED," +
			"Format(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION, TAppr.COUNTRY," +
			"TAppr.LE_BOOK, "
			+ "( SELECT TAppr.LE_BOOK+' - '+LEB.LEB_SHORT_DESCRIPTION FROM LE_BOOK LEB WHERE LEB.COUNTRY =TAppr.COUNTRY AND LEB.LE_BOOK = TAppr.LE_BOOK) STAKE_HOLDER, "
			+ "TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, " +
			//" round((decode(BUILD_SCHEDULE_STATUS,'I',GetDate(),end_time) - start_time) * (24 * 60))  Duration " +
			" case when BUILD_SCHEDULE_STATUS  = 'I' then (DATEDIFF(MINUTE,start_time ,GetDate() ))  else (DATEDIFF(MINUTE,start_time,end_time)) end Duration"
			+ ", isnull(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, Format(TAppr.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, Format(TAppr.FEED_DATE, 'dd-MMM-yyyy') FEED_DATE "+
			" From BUILD_SCHEDULES TAppr, VISION_USERS V1 " +
			"Where TAppr.SUBMITTER_ID = v1.VISION_ID  AND TAppr.BUILD_NUMBER = ? AND TAppr.BUILD = ?");
		}else {
			strBufApprove = new String("Select TAppr.BUILD," +
					"To_Char(TAppr.SCHEDULED_DATE, 'dd-Mon-yyyy HH24:MI:SS') SCHEDULED_DATE, TAppr.BUILD_NUMBER," +
					"TAppr.PARALLEL_PROCS_COUNT, TAppr.NOTIFY, TAppr.SUPPORT_CONTACT," +
					"  TAppr.submitter_id,v1.user_name submitter_name , To_Char(TAppr.START_TIME, 'dd-Mon-yyyy HH24:MI:SS') START_TIME," +
					"To_Char(TAppr.END_TIME, 'dd-Mon-yyyy HH24:MI:SS') END_TIME, TAppr.BUILD_SCHEDULE_STATUS_AT," +
					"TAppr.BUILD_SCHEDULE_STATUS,"
					+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB = 206 AND ALPHA_SUB_TAB = TAppr.BUILD_SCHEDULE_STATUS) BUILD_SCHEDULE_STATUS_DESC,"
					+ " To_Char(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED," +
					"To_Char(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION, TAppr.COUNTRY," +
					"TAppr.LE_BOOK, "
					+ "( SELECT TAppr.LE_BOOK+' - '+LEB.LEB_SHORT_DESCRIPTION FROM LE_BOOK LEB WHERE LEB.COUNTRY =TAppr.COUNTRY AND LEB.LE_BOOK = TAppr.LE_BOOK) STAKE_HOLDER, "
					+ "TAppr.RECURRING_FREQUENCY_AT, TAppr.RECURRING_FREQUENCY, " +
					//" round((decode(BUILD_SCHEDULE_STATUS,'I',GetDate(),end_time) - start_time) * (24 * 60))  Duration " +
//					" case when BUILD_SCHEDULE_STATUS  = 'I' then (DATEDIFF(MINUTE,start_time ,GetDate() ))  else (DATEDIFF(MINUTE,start_time,end_time)) end Duration"
					" case when BUILD_SCHEDULE_STATUS  = 'I' then round((decode(BUILD_SCHEDULE_STATUS,'I',GetDate(),end_time) - start_time) * (24 * 60))  "
					+ "else round((decode(BUILD_SCHEDULE_STATUS,'I',GetDate(),end_time) - start_time) * (24 * 60)) end Duration"
					+ ", nvl(TAppr.NODE_OVERRIDE,TAppr.NODE_REQUEST) NODE, To_Char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, To_Char(TAppr.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE "+
					" From BUILD_SCHEDULES TAppr, VISION_USERS V1 " +
					"Where TAppr.SUBMITTER_ID = v1.VISION_ID  AND TAppr.BUILD_NUMBER = ? AND TAppr.BUILD = ?");
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = buildNumber;//[BUILD_NUMBER]
		params[1] = build;//[BUILD]

		try
		{
			List<BuildSchedulesVb> result = getJdbcTemplate().query(strBufApprove, params, getQueryResultsMapper());
			if(result == null || result.isEmpty()){
				return null;
			}else{
				return result.get(0);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return null;
		}
	}
	@Override
	protected ExceptionCode doUpdateApprRecordForNonTrans(BuildSchedulesVb buildSchedulesVb) throws RuntimeCustomException{
		ExceptionCode exceptionCode = null;
		buildSchedulesVb.setMaker(intCurrentUserId);
		if (ValidationUtil.isValid(buildSchedulesVb.getBuildName())  && !"-1".equalsIgnoreCase(buildSchedulesVb.getBuildName())){
			 buildSchedulesVb.setBuild(buildSchedulesVb.getBuildName().toUpperCase());
		}
		int recCount = checkBuildForCtryLeBook(buildSchedulesVb.getBuild(),buildSchedulesVb.getCountry(),buildSchedulesVb.getLeBook());
		if(recCount <= 0){
			strErrorDesc = "Cannot run this build["+buildSchedulesVb.getBuild()+"] for Country["+buildSchedulesVb.getCountry()+"] and LE Book["+buildSchedulesVb.getLeBook()+"]";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		if(!("P".equalsIgnoreCase(buildSchedulesVb.getBuildScheduleStatus()) || "E".equalsIgnoreCase(buildSchedulesVb.getBuildScheduleStatus()))){
			strErrorDesc = "Build must be in Pending or Error Status. " ;
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode); 
		}
		if("P".equalsIgnoreCase(buildSchedulesVb.getBuildScheduleStatus())){
			long lScheduleDate = getDateTimeInMS(buildSchedulesVb.getScheduledDate(),"dd-MMM-yyyy HH:mm:ss");
			long lCurrentDate = getDateTimeInMS(getSystemDate(),"dd/MM/yyyy HH:mm:ss");
			if( (lScheduleDate - lCurrentDate) <  (2*60*1000) ){
				strErrorDesc = "Cannot modify build, scheduled to run in less than 2 minutes.";
				exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
				throw buildRuntimeCustomException(exceptionCode);
			}
		}
		buildSchedulesVb.setBuildScheduleStatus("P");
		retVal = doUpdateAppr(buildSchedulesVb);
	    if(retVal != Constants.SUCCESSFUL_OPERATION){
	    	exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
	    }
	    
	    //Delete ALL Detail records before insert.
	    BuildSchedulesDetailsVb lBuildSchedulesDetailsVb = new BuildSchedulesDetailsVb();
	    lBuildSchedulesDetailsVb.setBuildNumber(buildSchedulesVb.getBuildNumber());
	    exceptionCode = getBuildSchedulesDetailsDao().doDeleteApprRecordForNonTrans(lBuildSchedulesDetailsVb);
	    
	    if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			strErrorDesc = exceptionCode.getErrorMsg();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode); 
		}
	    //Insert the Detail records
	    for (BuildSchedulesDetailsVb vBCObject: buildSchedulesVb.getBuildSchedulesDetails()) {
	    	vBCObject.setBuildNumber(buildSchedulesVb.getBuildNumber());
	    	exceptionCode = getBuildSchedulesDetailsDao().doInsertApprRecordForNonTrans(vBCObject);
	    	if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				throw buildRuntimeCustomException(exceptionCode); 
			}
		}
		
		if(retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	@Override
	protected ExceptionCode doInsertApprRecordForNonTrans(BuildSchedulesVb buildSchedulesVb) throws RuntimeCustomException{
		ExceptionCode exceptionCode = null;
		buildSchedulesVb.setMaker(intCurrentUserId);
		if (ValidationUtil.isValid(buildSchedulesVb.getBuildName())  && !"-1".equalsIgnoreCase(buildSchedulesVb.getBuildName())){
			 buildSchedulesVb.setBuild(buildSchedulesVb.getBuildName().toUpperCase());
		}
		int recCount = checkBuildForCtryLeBook(buildSchedulesVb.getBuild(),buildSchedulesVb.getCountry(),buildSchedulesVb.getLeBook());
		if(recCount <= 0){
			strErrorDesc = "Cannot run this build["+buildSchedulesVb.getBuild()+"] for Country["+buildSchedulesVb.getCountry()+"] and LE Book["+buildSchedulesVb.getLeBook()+"]";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		Long seq = getBuildControlsDao().getMaxBuildNumber();
		seq++;
		buildSchedulesVb.setBuildNumber(seq+"");
		retVal = doInsertionAppr(buildSchedulesVb);
		
		if(retVal != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(retVal);
			throw buildRuntimeCustomException(exceptionCode);
		}
		for (BuildSchedulesDetailsVb vBCObject: buildSchedulesVb.getBuildSchedulesDetails()) {
			vBCObject.setBuildNumber(seq+"");
			exceptionCode = getBuildSchedulesDetailsDao().doInsertApprRecordForNonTrans(vBCObject);
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				throw buildRuntimeCustomException(exceptionCode); 
			}
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	@Override
	protected ExceptionCode doDeleteApprRecordForNonTrans(BuildSchedulesVb vObject) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		BuildSchedulesVb vObjectlocal = null;
		if (ValidationUtil.isValid(vObject.getBuildName())  && !"-1".equalsIgnoreCase(vObject.getBuildName())){
			vObject.setBuild(vObject.getBuildName().toUpperCase());
		}
		vObjectlocal = getQueryResultsForDetails(vObject.getBuildNumber(), vObject.getBuild());
		if (vObjectlocal == null){
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			throw buildRuntimeCustomException(exceptionCode);
		}
		long lScheduleDate = getDateTimeInMS(vObjectlocal.getScheduledDate(),"dd-MMM-yyyy HH:mm:ss");
		long lCurrentDate = getDateTimeInMS(getSystemDate(),"dd/MM/yyyy HH:mm:ss");
		if( !"E".equalsIgnoreCase(vObjectlocal.getBuildScheduleStatus()) && !"K".equalsIgnoreCase(vObjectlocal.getBuildScheduleStatus())
				&& (lScheduleDate - lCurrentDate) <  (2*60*1000)){
			strErrorDesc = "Cannot delete build, scheduled to run in less than 2 minutes.";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode); 
		}
		BuildSchedulesDetailsVb lBuildSchedulesDetailsVb = new BuildSchedulesDetailsVb();
		lBuildSchedulesDetailsVb.setBuildNumber(vObject.getBuildNumber());
		
		exceptionCode = getBuildControlsDao().updateBuildControls(lBuildSchedulesDetailsVb);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			throw buildRuntimeCustomException(exceptionCode); 
		}
		ExceptionCode lResultObject = getBuildSchedulesDetailsDao().doDeleteApprRecordForNonTrans(lBuildSchedulesDetailsVb);
		if(lResultObject.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			strErrorDesc = lResultObject.getErrorMsg();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		retVal = doDeleteAppr(vObjectlocal);
		if(retVal != Constants.SUCCESSFUL_OPERATION){
			strErrorDesc = "Unable to delete the record.";
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return getResultObject(Constants.SUCCESSFUL_OPERATION);
	}
	public ExceptionCode reInitiate(List<BuildSchedulesVb> vObjects){
		BuildSchedulesVb vObjectlocal = null;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = "Re-Initiate";
		try
		{
			
			for(BuildSchedulesVb vObject : vObjects){
				if(vObject.isChecked()){
					if (ValidationUtil.isValid(vObject.getBuildName())  && !"-1".equalsIgnoreCase(vObject.getBuildName())){
						vObject.setBuild(vObject.getBuildName().toUpperCase());
					}
					// check to see if the record already exists in the approved table
					vObjectlocal = getQueryResultsForDetails(vObject.getBuildNumber(),vObject.getBuild());

					// If records are there, check for the status and decide what error to return back
					if (vObjectlocal == null){
						exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
						throw buildRuntimeCustomException(exceptionCode);
					}	
					if(!("E".equalsIgnoreCase(vObjectlocal.getBuildScheduleStatus()) || "K".equalsIgnoreCase(vObjectlocal.getBuildScheduleStatus()))){
						strErrorDesc = "You can only Re-Initiate builds with status Errored or Terminated.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
					//First run the build Control Query
					BuildSchedulesDetailsVb lBuildSchedulesDetailsVb = new BuildSchedulesDetailsVb();
					lBuildSchedulesDetailsVb.setBuildNumber(vObject.getBuildNumber());
					exceptionCode = getBuildControlsDao().updateBuildControls(lBuildSchedulesDetailsVb);
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					lBuildSchedulesDetailsVb = new BuildSchedulesDetailsVb();
					lBuildSchedulesDetailsVb.setBuildNumber(vObject.getBuildNumber());
					exceptionCode = getBuildSchedulesDetailsDao().updateStatusOfNonCompletedBuilds(lBuildSchedulesDetailsVb);
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					vObjectlocal.setSubmitterId((int)intCurrentUserId);
					vObjectlocal.setBuildScheduleStatus("P");
					retVal = updateBuildSchedulesStatus(vObjectlocal);
					if(retVal != Constants.SUCCESSFUL_OPERATION){
						strErrorDesc = "Unable to reset the record status to 'P'.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
//			strErrorDesc = parseErrorMsg((UncategorizedSQLException) ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	private long getDateTimeInMS(String date, String formate){
		DateFormat lFormat = new SimpleDateFormat(formate);
		try {
			Date lDate = lFormat.parse(date);
			return lDate.getTime();
		} catch (Exception e) {
			return System.currentTimeMillis();
		}
	}
	private int checkBuildForCtryLeBook(String buildName,String country,String leBook) // Build Controls Details
	{
		final int intKeyFieldsCount = 3;
		String query = new String("SELECT count(1) FROM BUILD_CONTROLS WHERE BUILD = ? AND COUNTRY = ? AND LE_BOOK = ?"); 
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = buildName; 
		params[1] = country;
		params[2] = leBook; 
		try
		{
			int count = getJdbcTemplate().queryForObject(query,params, int.class);
			
			return count ;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return 0;
		}
	}
	public int checkExpandFlagFor(BuildSchedulesVb vObject){
		final int intKeyFieldsCount = 1;
		String query = new String("select count(1) from BUILD_SCHEDULES_DETAILS BSD JOIN BUILD_CONTROLS  BC ON ASSOCIATED_BUILD = BUILD AND " +
				"BC.BUILD_MODULE=BSD.BUILD_MODULE WHERE BSD.BUILD_NUMBER=? AND EXPAND_FLAG = 'Y'"); 
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = vObject.getBuildNumber(); 
		try
		{
			int count = getJdbcTemplate().queryForObject(query,params, int.class);
			
			return count ;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			return 0;
		}
	}
	@Override
	protected int doInsertionAppr(BuildSchedulesVb vObject){
		String query = "Insert Into BUILD_SCHEDULES ( BUILD, SCHEDULED_DATE, BUILD_NUMBER, PARALLEL_PROCS_COUNT,"+
						"NOTIFY, SUPPORT_CONTACT, SUBMITTER_ID, START_TIME, END_TIME, BUILD_SCHEDULE_STATUS_AT, "+
						"BUILD_SCHEDULE_STATUS, DATE_LAST_MODIFIED, DATE_CREATION, COUNTRY, LE_BOOK, RECURRING_FREQUENCY_AT,"+
						"RECURRING_FREQUENCY, NODE_REQUEST, BUSINESS_DATE, FEED_DATE) Values (?, CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, ?, ?, ?, ?, "+
						"GetDate(), GetDate(), ?, ?, ?, ?, ?, CONVERT(datetime, ?, 103), CONVERT(datetime, ?, 103))";
		if (ValidationUtil.isValid(vObject.getBuildName())  && !"-1".equalsIgnoreCase(vObject.getBuildName())){
			vObject.setBuild(vObject.getBuildName().toUpperCase());
		}
		Object[] args = {vObject.getBuild(), vObject.getScheduledDate(), vObject.getBuildNumber(),
				vObject.getParallelProcsCount(), vObject.getNotify(), vObject.getSupportContact(),
				vObject.getSubmitterId(),vObject.getStartTime(),vObject.getEndTime(),vObject.getBuildScheduleStatusAt(),
				vObject.getBuildScheduleStatus(),vObject.getCountry(),vObject.getLeBook(),vObject.getRecurringFrequencyAt(),
				vObject.getRecurringFrequency(),vObject.getNode(),vObject.getBusinessDate(),vObject.getFeedDate()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdateAppr(BuildSchedulesVb vObject){
		String query = "Update BUILD_SCHEDULES Set SCHEDULED_DATE = CONVERT(datetime, ?, 103),"+
			"PARALLEL_PROCS_COUNT = ?, NOTIFY = ?, SUPPORT_CONTACT = ?, SUBMITTER_ID = ?, START_TIME = NULL,"+
			"END_TIME = NULL, BUILD_SCHEDULE_STATUS = ?, DATE_LAST_MODIFIED = GetDate(), COUNTRY = ?, LE_BOOK = ?,"+
			"RECURRING_FREQUENCY = ?, NODE_REQUEST = ?, BUSINESS_DATE = CONVERT(datetime, ?, 103), FEED_DATE = CONVERT(datetime, ?, 103) "
			+ " Where BUILD = ? AND BUILD_NUMBER = ?";
		if (ValidationUtil.isValid(vObject.getBuildName())  && !"-1".equalsIgnoreCase(vObject.getBuildName())){
			vObject.setBuild(vObject.getBuildName().toUpperCase());
		}
		Object[] args = {vObject.getScheduledDate(), vObject.getParallelProcsCount(), vObject.getNotify(), vObject.getSupportContact(),
			vObject.getSubmitterId(), vObject.getBuildScheduleStatus(), vObject.getCountry(), vObject.getLeBook(), 
			vObject.getRecurringFrequency(), vObject.getNode(), vObject.getBusinessDate(), vObject.getFeedDate(), vObject.getBuild(), vObject.getBuildNumber()};
		return getJdbcTemplate().update(query,args);
	}
	private int updateBuildSchedulesStatus(BuildSchedulesVb buildSchedulesDetailsVb){
		final int intKeyFieldsCount = 4;
		String query = new String("Update BUILD_SCHEDULES Set SUBMITTER_ID = ?, BUILD_SCHEDULE_STATUS = ?, DATE_LAST_MODIFIED = GetDate() "+
				"Where BUILD = ? AND BUILD_NUMBER = ?");
		if (ValidationUtil.isValid(buildSchedulesDetailsVb.getBuildName())  && !"-1".equalsIgnoreCase(buildSchedulesDetailsVb.getBuildName())){
			buildSchedulesDetailsVb.setBuild(buildSchedulesDetailsVb.getBuildName().toUpperCase());
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = buildSchedulesDetailsVb.getSubmitterId();
		params[1] = buildSchedulesDetailsVb.getBuildScheduleStatus();
		params[2] = buildSchedulesDetailsVb.getBuild();
		params[3] = buildSchedulesDetailsVb.getBuildNumber();
		try
		{
			return getJdbcTemplate().update(query,params);
		}catch(Exception ex){
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			logger.error(ex.getMessage(), ex);
			return 0;
		}
		
	}
	@Override
	protected int doDeleteAppr(BuildSchedulesVb vObject){
		String query = "Delete From BUILD_SCHEDULES Where BUILD = ? AND BUILD_NUMBER = ?";
		if (ValidationUtil.isValid(vObject.getBuildName())  && !"-1".equalsIgnoreCase(vObject.getBuildName())){
			vObject.setBuild(vObject.getBuildName().toUpperCase());
		}
		Object[] args = {vObject.getBuild(),vObject.getBuildNumber()};
		return getJdbcTemplate().update(query,args);
	}
	public BuildControlsDao getBuildControlsDao() {
		return buildControlsDao;
	}

	public void setBuildControlsDao(BuildControlsDao buildControlsDao) {
		this.buildControlsDao = buildControlsDao;
	}

	public BuildSchedulesDetailsDao getBuildSchedulesDetailsDao() {
		return buildSchedulesDetailsDao;
	}

	public void setBuildSchedulesDetailsDao(
			BuildSchedulesDetailsDao buildSchedulesDetailsDao) {
		this.buildSchedulesDetailsDao = buildSchedulesDetailsDao;
	}
	public long findCronCount(String environmentVariable){
		String sql="SELECT COUNT(1) FROM VISION_VARIABLES WHERE VARIABLE IN ( " +
				" SELECT 'BUILD_CRON_'+SERVER_NAME+'_'+NODE_NAME FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT='"+environmentVariable+"' AND NODE_STATUS=0)";
		
		long count = getJdbcTemplate().queryForObject(sql, long.class);
		
		return count ;
	}
	public List<BuildSchedulesVb> findCronName(BuildSchedulesVb dObj,String enironmentVariable){
		List<BuildSchedulesVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = new String("SELECT (substring(variable,12, 100)) AS NAME FROM VISION_VARIABLES WHERE VARIABLE IN (  " +
				" SELECT 'BUILD_CRON_'+SERVER_NAME+'_'+NODE_NAME FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT=? AND NODE_STATUS=0)");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = enironmentVariable;
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					BuildSchedulesVb buildSchedulesVb = new BuildSchedulesVb();
					buildSchedulesVb.setCronName(rs.getString("NAME"));
					return buildSchedulesVb;
				}
			};
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams, mapper);
			
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	public List<BuildSchedulesVb> findCronStatus(BuildSchedulesVb dObj,String enironmentVariable){
		List<BuildSchedulesVb> collTemp = null;
		final int intKeyFieldsCount = 1;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = new String("SELECT VALUE AS STATUS FROM VISION_VARIABLES WHERE VARIABLE IN ( " +
				" SELECT 'BUILD_CRON_'+SERVER_NAME+'_'+NODE_NAME FROM VISION_NODE_CREDENTIALS WHERE SERVER_ENVIRONMENT=? AND NODE_STATUS=0)");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = enironmentVariable;
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					BuildSchedulesVb buildSchedulesVb = new BuildSchedulesVb();
					buildSchedulesVb.setCronStatus(rs.getString("STATUS"));
					return buildSchedulesVb;
				}
			};
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams, mapper);
			
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	public List<BuildSchedulesVb> getDateForBusinessFeedDate(String Country,String fullLeBook, String majorBuild){
		List<BuildSchedulesVb> collTemp = null;
		String CalLeBook = removeDescLeBook(fullLeBook);
		String query="SELECT "+ 
				 " CASE WHEN X1.Frequency_Process = 'DLY' THEN FORMAT(X7.BUSINESS_DATE, 'dd-MMM-yyyy') "+ 
	             " WHEN X1.Frequency_Process = 'MTH' THEN format(EOMONTH(Convert(Date, Convert(Varchar,X7.Business_YEAR_MONTH)+'01')) , 'dd-MMM-yyyy') "+ 
	             " WHEN X1.Frequency_Process = 'WKY' THEN format(X7.BUSINESS_WEEK_DATE, 'dd-MMM-yyyy') "+ 
	             " WHEN X1.Frequency_Process = 'QTR' THEN format(EOMONTH(Convert(Date, Convert(Varchar,X7.BUSINESS_QTR_YEAR_MONTH)+'01')) , 'dd-MMM-yyyy') "+ 
	             " WHEN X1.Frequency_Process = 'HYR' THEN format(EOMONTH(Convert(Date, Convert(Varchar,X7.BUSINESS_HYR_YEAR_MONTH)+'01')) , 'dd-MMM-yyyy') "+
	             " WHEN X1.Frequency_Process = 'ANN' THEN format(EOMONTH(Convert(Date, '31-Dec-'+Convert(Varchar,X7.BUSINESS_YEAR))) , 'dd-MMM-yyyy') "+ 
	             " END BUSINESS_DATE, FREQUENCY_PROCESS  FROM VISION_BUSINESS_DAY X7 ,(Select ISNULL(MIN(T1.Frequency_Process),'DLY') Frequency_Process "+
	             " From ADF_Data_Acquisition T1 Where T1.Country = '"+Country+"' And T1.Le_Book = '"+CalLeBook+"' And ISNULL(T1.Major_Build,'ACQ'+T1.Frequency_Process+'FEED') = '"+majorBuild+"') X1 "+
	             " WHERE X7.COUNTRY = '"+Country+"' And X7.LE_BOOK = '"+CalLeBook+"'";

/*		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = enironmentVariable;*/
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					BuildSchedulesVb buildSchedulesVb = new BuildSchedulesVb();
					buildSchedulesVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
					buildSchedulesVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));;
					return buildSchedulesVb;
				}
			};
			collTemp = getJdbcTemplate().query(query.toString(), mapper);
			
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((query == null) ? "strQueryAppr is Null" : query.toString()));
			return null;
		}
	}	
	public ExceptionCode transfer(List<BuildSchedulesVb> vObjects, String updateNode){
		BuildSchedulesVb vObjectlocal = null;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		strCurrentOperation = "Transfer";
		try
		{
			for(BuildSchedulesVb vObject : vObjects){
				if(vObject.isChecked()){
					if (ValidationUtil.isValid(vObject.getBuildName())  && !"-1".equalsIgnoreCase(vObject.getBuildName())){
						vObject.setBuild(vObject.getBuildName().toUpperCase());
					}
					// check to see if the record already exists in the approved table
					vObjectlocal = getQueryResultsForDetails(vObject.getBuildNumber(),vObject.getBuild());

					// If records are there, check for the status and decide what error to return back
					if (vObjectlocal == null){
						exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
						throw buildRuntimeCustomException(exceptionCode);
					}
					if(!("E".equalsIgnoreCase(vObjectlocal.getBuildScheduleStatus()) || "K".equalsIgnoreCase(vObjectlocal.getBuildScheduleStatus()))){
						strErrorDesc = "You can only Transfer builds with status Errored or Terminated.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
					//First run the build Control Query
					BuildSchedulesDetailsVb lBuildSchedulesDetailsVb = new BuildSchedulesDetailsVb();
					lBuildSchedulesDetailsVb.setBuildNumber(vObject.getBuildNumber());
					exceptionCode = getBuildControlsDao().updateBuildControls(lBuildSchedulesDetailsVb);
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					lBuildSchedulesDetailsVb = new BuildSchedulesDetailsVb();
					lBuildSchedulesDetailsVb.setBuildNumber(vObject.getBuildNumber());
					exceptionCode = getBuildSchedulesDetailsDao().updateStatusOfNonCompletedBuilds(lBuildSchedulesDetailsVb);
					if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
						throw buildRuntimeCustomException(exceptionCode); 
					}
					vObjectlocal.setSubmitterId((int)intCurrentUserId);
					vObjectlocal.setBuildScheduleStatus("P");
					vObjectlocal.setNode(updateNode);
					
					retVal = updateBuildSchedulesNode(vObjectlocal);
					if(retVal != Constants.SUCCESSFUL_OPERATION){
						strErrorDesc = "Unable to reset the record status to 'P'.";
						exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			strErrorDesc = ex.getMessage().trim();
			ex.printStackTrace();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	private int updateBuildSchedulesNode(BuildSchedulesVb buildSchedulesDetailsVb){
		final int intKeyFieldsCount = 5;
		String query = new String("Update BUILD_SCHEDULES Set SUBMITTER_ID = ?,NODE_OVERRIDE = ?, BUILD_SCHEDULE_STATUS = ?, DATE_LAST_MODIFIED = SysDate "+
				"Where BUILD = ? AND BUILD_NUMBER = ?");
		if (ValidationUtil.isValid(buildSchedulesDetailsVb.getBuildName())  && !"-1".equalsIgnoreCase(buildSchedulesDetailsVb.getBuildName())){
			buildSchedulesDetailsVb.setBuild(buildSchedulesDetailsVb.getBuildName().toUpperCase());
		}
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = buildSchedulesDetailsVb.getSubmitterId();
		params[1] = buildSchedulesDetailsVb.getNode();
		params[2] = buildSchedulesDetailsVb.getBuildScheduleStatus();
		params[3] = buildSchedulesDetailsVb.getBuild();
		params[4] = buildSchedulesDetailsVb.getBuildNumber();
		try
		{
			return getJdbcTemplate().update(query,params);
		}catch(Exception ex){
			logger.error(((query==null)? "query is Null":query));
			if (params != null)
				for(int i=0 ; i< params.length; i++)
					logger.error("objParams[" + i + "]" + params[i].toString());
			logger.error(ex.getMessage(), ex);
			return 0;
		}
	}
	protected RowMapper getQueryforBuildScheduleStatusMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BuildSchedulesVb buildSchedulesVb = new BuildSchedulesVb();
				buildSchedulesVb.setBuildScheduleStatus(rs.getString("BUILD_SCHEDULE_STATUS"));
				buildSchedulesVb.setStatusDesc(rs.getString("BUILD_SCHEDULE_STATUS_DESC"));
				buildSchedulesVb.setParallelProcsCount(rs.getInt("STATUS_COUNT"));
				return buildSchedulesVb;
			}
		};
		return mapper;
	}
	public List<BuildSchedulesVb> getQueryResultsForStatusBars(BuildSchedulesVb dObj, int status){
		setServiceDefaults();
		Vector<Object> params = new Vector<Object>();
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		dObj.setMaxRecords(10000);
		StringBuffer strBufApprove = new StringBuffer(" SELECT BUILD_SCHEDULE_STATUS, "
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_TAB =1204 AND BUILD_SCHEDULE_STATUS = ALPHA_SUB_TAB) BUILD_SCHEDULE_STATUS_DESC, "
				+ " COUNT(BUILD_NUMBER) STATUS_COUNT "+
			 " FROM BUILD_SCHEDULES TAppr, LE_BOOK LEB  "+
			 " WHERE TAppr.Country = LEB.Country And TAppr.Le_Book = LEB.Le_Book ");

		try{
			if (ValidationUtil.isValid(dObj.getCountry())){
				params.addElement(dObj.getCountry().toUpperCase());
				strBufApprove.append(" AND TAppr.COUNTRY = ? ");
			}
			if (ValidationUtil.isValid(CalLeBook)){
				params.addElement(CalLeBook);
				strBufApprove.append(" AND TAppr.LE_BOOK = ? ");
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())){
				strBufApprove.append(" AND TAppr.COUNTRY IN("+SessionContextHolder.getContext().getCountry().toUpperCase()+") ");
				strBufApprove.append(" AND TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getLeBook().toUpperCase()+") ");
			}	
			
			if (ValidationUtil.isValid(dObj.getBuild())  && !"-1".equalsIgnoreCase(dObj.getBuild())){
				params.addElement(dObj.getBuild().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.BUILD) = UPPER(?) ");
			}else if (ValidationUtil.isValid(dObj.getBuildName())  && !"-1".equalsIgnoreCase(dObj.getBuildName())){
				params.addElement(dObj.getBuildName().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.BUILD) = UPPER(?) ");
			}
			if (ValidationUtil.isValid(dObj.getBuildNumber())){
				params.addElement(dObj.getBuildNumber());
				strBufApprove.append(" AND TAppr.BUILD_NUMBER = ?");
			}
			if (ValidationUtil.isValid(dObj.getSubmitterId())  && dObj.getSubmitterId()!=0){
				params.addElement(dObj.getSubmitterId());
				strBufApprove.append(" AND TAppr.SUBMITTER_ID = ? ");
			}
			if (ValidationUtil.isValid(dObj.getBuildScheduleStatus()) && !"-1".equalsIgnoreCase(dObj.getBuildScheduleStatus())){
				params.addElement(dObj.getBuildScheduleStatus());
				strBufApprove.append(" AND TAppr.BUILD_SCHEDULE_STATUS = UPPER(?) ");
			}else if (ValidationUtil.isValid(dObj.getStatus()) && !"-1".equalsIgnoreCase(dObj.getStatus())){
				params.addElement(dObj.getStatus());
				strBufApprove.append(" AND TAppr.BUILD_SCHEDULE_STATUS = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getRecurringFrequency()) && !"-1".equalsIgnoreCase(dObj.getRecurringFrequency())){
				params.addElement(dObj.getRecurringFrequency().toUpperCase());
				strBufApprove.append(" AND UPPER(TAppr.RECURRING_FREQUENCY) = UPPER(?) ");
			}
			if(ValidationUtil.isValid(dObj.getNode()) && !"-1".equalsIgnoreCase(dObj.getNode())){
				params.addElement(dObj.getNode());
				strBufApprove.append(" AND isnull(node_override, node_request) = ? ");
			}
			strBufApprove.append(" GROUP BY BUILD_SCHEDULE_STATUS ");

			Object objParams[] = new Object[params.size()];
			for(int Ctr=0; Ctr < params.size(); Ctr++)
				objParams[Ctr] = (Object) params.elementAt(Ctr);
			
			return getJdbcTemplate().query(strBufApprove.toString(),objParams,getQueryforBuildScheduleStatusMapper());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}


}