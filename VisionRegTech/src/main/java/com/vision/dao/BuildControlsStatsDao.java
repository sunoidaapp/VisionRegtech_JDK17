package com.vision.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

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
import com.vision.vb.BuildControlsStatsVb;
@Component
public class BuildControlsStatsDao extends AbstractDao<BuildControlsStatsVb> {
	public RowMapper getQueryPopupMapper(){
		RowMapper mapper = new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BuildControlsStatsVb buildControlsStatsVb = new BuildControlsStatsVb();
				buildControlsStatsVb.setYear(rs.getString("YEAR"));
				buildControlsStatsVb.setCountry(rs.getString("COUNTRY"));
				buildControlsStatsVb.setLeBook(rs.getString("LE_BOOK"));
				buildControlsStatsVb.setDataSource(rs.getString("DATA_SOURCE"));
				return buildControlsStatsVb;
			}
		};
		return mapper;
	}
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				BuildControlsStatsVb buildControlsStatsVb = new BuildControlsStatsVb();
				buildControlsStatsVb.setYear(rs.getString("YEAR"));
				buildControlsStatsVb.setCountry(rs.getString("COUNTRY"));
				buildControlsStatsVb.setLeBook(rs.getString("LE_BOOK"));
				buildControlsStatsVb.setDataSourceAt(rs.getInt("DATA_SOURCE_AT"));
				buildControlsStatsVb.setDataSource(rs.getString("DATA_SOURCE"));
				buildControlsStatsVb.setBuildModule(rs.getString("BUILD_MODULE"));
				buildControlsStatsVb.setLastBuildDate(rs.getString("LAST_BUILD_DATE"));
				buildControlsStatsVb.setRecordIndicatorNt(rs.getInt("RECORD_INDICATOR_NT"));
				buildControlsStatsVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				buildControlsStatsVb.setMaker(rs.getInt("MAKER"));
				buildControlsStatsVb.setVerifier(rs.getInt("VERIFIER"));
				buildControlsStatsVb.setInternalStatus(rs.getInt("INTERNAL_STATUS"));
				buildControlsStatsVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				buildControlsStatsVb.setDateCreation(rs.getString("DATE_CREATION"));
				return buildControlsStatsVb;
			}
		};
		return mapper;
	}

	public List<BuildControlsStatsVb> getQueryPopupResults(BuildControlsStatsVb dObj){
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select Distinct TAppr.YEAR, TAppr.COUNTRY, " + 
			" TAppr.LE_BOOK, TAppr.DATA_SOURCE" + 
			" From BUILD_CONTROLS_STATS TAppr ");

		String strWhereNotExists = new String(" Not Exists (Select 'X' From BUILD_CONTROLS_STATS_PEND TPend Where " + 
			"TAppr.YEAR = TPend.YEAR " + 
			"And TAppr.COUNTRY = TPend.COUNTRY " + 
			"And TAppr.LE_BOOK = TPend.LE_BOOK " + 
			"And TAppr.DATA_SOURCE = TPend.DATA_SOURCE " + 
			"And TAppr.BUILD_MODULE = TPend.BUILD_MODULE)");

		StringBuffer strBufPending = new StringBuffer("Select Distinct TPend.YEAR, TPend.COUNTRY, " + 
			" TPend.LE_BOOK, TPend.DATA_SOURCE" + 
			" From BUILD_CONTROLS_STATS_PEND TPend ");

		try
		{
			if (ValidationUtil.isValid(dObj.getYear()))
			{
				params.addElement(dObj.getYear());
				CommonUtils.addToQuery("TAppr.YEAR = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.YEAR = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement("%" + dObj.getCountry().toUpperCase() + "%" );
				CommonUtils.addToQuery("UPPER(TAppr.COUNTRY) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.COUNTRY) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook()))
			{
				params.addElement("%" + dObj.getLeBook().toUpperCase() + "%" );
				CommonUtils.addToQuery("UPPER(TAppr.LE_BOOK) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.LE_BOOK) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getDataSource()) && !"-1".equalsIgnoreCase(dObj.getDataSource()))
			{
				params.addElement(dObj.getDataSource());
				CommonUtils.addToQuery("UPPER(TAppr.DATA_SOURCE) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.DATA_SOURCE) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBuildModule()))
			{
				params.addElement("%" + dObj.getBuildModule().toUpperCase() + "%" );
				CommonUtils.addToQuery("UPPER(TAppr.BUILD_MODULE) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.BUILD_MODULE) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLastBuildDate()))
			{
				params.addElement(dObj.getLastBuildDate());
				CommonUtils.addToQuery("TAppr.LAST_BUILD_DATE = CONVERT(datetime, ?, 103) ", strBufApprove);
				CommonUtils.addToQuery("TPend.LAST_BUILD_DATE = CONVERT(datetime, ?, 103) ", strBufPending);
			}
			//check if the column [RECORD_INDICATOR] should be included in the query
			if (dObj.getRecordIndicator() != -1){
				if (dObj.getRecordIndicator() > 3){
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				}else{
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}

		String orderBy = " Order By YEAR, COUNTRY, LE_BOOK, DATA_SOURCE";
		return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params, getQueryPopupMapper());
	}

	public List<BuildControlsStatsVb> getQueryResults(BuildControlsStatsVb dObj, int intStatus){
		setServiceDefaults();
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer("Select TAppr.YEAR,TAppr.COUNTRY, " + 
			"TAppr.LE_BOOK,TAppr.DATA_SOURCE_AT,TAppr.DATA_SOURCE,TAppr.BUILD_MODULE,Format(TAppr.LAST_BUILD_DATE, " + 
			" 'dd-MMM-yyyy HH:mm:ss') LAST_BUILD_DATE,TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, " + 
			"TAppr.MAKER,TAppr.VERIFIER,TAppr.INTERNAL_STATUS,Format(TAppr.DATE_LAST_MODIFIED, " + 
			" 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,Format(TAppr.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION From BUILD_CONTROLS_STATS TAppr ");

		String strWhereNotExists = new String(" Not Exists (Select 'X' From BUILD_CONTROLS_STATS_PEND TPend Where " + 
			"TAppr.YEAR = TPend.YEAR " + 
			"And TAppr.COUNTRY = TPend.COUNTRY " + 
			"And TAppr.LE_BOOK = TPend.LE_BOOK " + 
			"And TAppr.DATA_SOURCE = TPend.DATA_SOURCE " + 
			"And TAppr.BUILD_MODULE = TPend.BUILD_MODULE) ");

		StringBuffer strBufPending = new StringBuffer("Select TPend.YEAR,TPend.COUNTRY, " + 
			"TPend.LE_BOOK,TPend.DATA_SOURCE_AT,TPend.DATA_SOURCE,TPend.BUILD_MODULE,Format(TPend.LAST_BUILD_DATE, " + 
			" 'dd-MMM-yyyy HH:mm:ss') LAST_BUILD_DATE,TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, " + 
			"TPend.MAKER,TPend.VERIFIER,TPend.INTERNAL_STATUS,Format(TPend.DATE_LAST_MODIFIED, " + 
			" 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED,Format(TPend.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION From BUILD_CONTROLS_STATS_PEND TPend ");

		try
		{
			if (ValidationUtil.isValid(dObj.getYear()))
			{
				params.addElement(dObj.getYear());
				CommonUtils.addToQuery("TAppr.YEAR = ?", strBufApprove);
				CommonUtils.addToQuery("TPend.YEAR = ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement("%" + dObj.getCountry().toUpperCase() + "%" );
				CommonUtils.addToQuery("UPPER(TAppr.COUNTRY) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.COUNTRY) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLeBook()))
			{
				params.addElement("%" + dObj.getLeBook().toUpperCase() + "%" );
				CommonUtils.addToQuery("UPPER(TAppr.LE_BOOK) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.LE_BOOK) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getDataSource()) && !"-1".equalsIgnoreCase(dObj.getDataSource()))
			{
				params.addElement(dObj.getDataSource());
				CommonUtils.addToQuery("UPPER(TAppr.DATA_SOURCE) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.DATA_SOURCE) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getBuildModule()))
			{
				params.addElement("%" + dObj.getBuildModule().toUpperCase() + "%" );
				CommonUtils.addToQuery("UPPER(TAppr.BUILD_MODULE) like ?", strBufApprove);
				CommonUtils.addToQuery("UPPER(TPend.BUILD_MODULE) like ?", strBufPending);
			}
			if (ValidationUtil.isValid(dObj.getLastBuildDate()))
			{
				params.addElement(dObj.getLastBuildDate());
				CommonUtils.addToQuery("TAppr.LAST_BUILD_DATE = CONVERT(datetime, ?, 103) ", strBufApprove);
				CommonUtils.addToQuery("TPend.LAST_BUILD_DATE = CONVERT(datetime, ?, 103) ", strBufPending);
			}
			//check if the column [RECORD_INDICATOR] should be included in the query
			if (dObj.getRecordIndicator() != -1){
				if (dObj.getRecordIndicator() > 3){
					params.addElement(new Integer(0));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR > ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR > ?", strBufPending);
				}else{
					params.addElement(new Integer(dObj.getRecordIndicator()));
					CommonUtils.addToQuery("TAppr.RECORD_INDICATOR = ?", strBufApprove);
					CommonUtils.addToQuery("TPend.RECORD_INDICATOR = ?", strBufPending);
				}
			}

			String orderBy = " Order By COUNTRY, LE_BOOK, YEAR, DATA_SOURCE, BUILD_MODULE";
			return getQueryPopupResults(dObj,strBufPending, strBufApprove, strWhereNotExists, orderBy, params);
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is null":strBufApprove.toString()));
			logger.error("UNION");
			logger.error(((strBufPending==null)? "strBufPending is Null":strBufPending.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}

	public List<BuildControlsStatsVb> getQueryResultsForReview(BuildControlsStatsVb dObj, int intStatus){

		List<BuildControlsStatsVb> collTemp = null;
		final int intKeyFieldsCount = 5;
		StringBuffer strQueryAppr = new StringBuffer("Select TAppr.YEAR,TAppr.COUNTRY, " + 
			"TAppr.LE_BOOK,TAppr.DATA_SOURCE_AT,TAppr.DATA_SOURCE,TAppr.BUILD_MODULE,Format(TAppr.LAST_BUILD_DATE, " + 
			" 'dd-MMM-yyyy HH:mm:ss') LAST_BUILD_DATE," +
			"TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, " + 
			"TAppr.MAKER,TAppr.VERIFIER,TAppr.INTERNAL_STATUS,Format(TAppr.DATE_LAST_MODIFIED, " + 
			" 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TAppr.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION" +
			" From BUILD_CONTROLS_STATS TAppr " + 
			"Where TAppr.YEAR = ?  And TAppr.COUNTRY = ?  And TAppr.LE_BOOK = ?  And TAppr.DATA_SOURCE = ?  And TAppr.BUILD_MODULE = ? ");

		StringBuffer strQueryPend = new StringBuffer("Select TPend.YEAR,TPend.COUNTRY, " + 
			"TPend.LE_BOOK,TPend.DATA_SOURCE_AT,TPend.DATA_SOURCE,TPend.BUILD_MODULE,Format(TPend.LAST_BUILD_DATE, " + 
			" 'dd-MMM-yyyy HH:mm:ss') LAST_BUILD_DATE," +
			"TPend.RECORD_INDICATOR_NT,TPend.RECORD_INDICATOR, " + 
			"TPend.MAKER,TPend.VERIFIER,TPend.INTERNAL_STATUS,Format(TPend.DATE_LAST_MODIFIED, " + 
			" 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TPend.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION" +
			" From BUILD_CONTROLS_STATS_PEND TPend " + 
			"Where TPend.YEAR = ?  And TPend.COUNTRY = ?  And TPend.LE_BOOK = ?  And TPend.DATA_SOURCE = ?  And TPend.BUILD_MODULE = ? ");

		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new Integer(dObj.getYear());	//[YEAR]
		objParams[1] = new String(dObj.getCountry());	//[COUNTRY]
		objParams[2] = new String(dObj.getLeBook());	//[LE_BOOK]
		objParams[3] = new String(dObj.getDataSource());	//[DATA_SOURCE]
		objParams[4] = new String(dObj.getBuildModule());	//[BUILD_MODULE]
		try
		{
			if(!dObj.isVerificationRequired()){intStatus =0;}
			if(intStatus == 0)
			{
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			}else{
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(),objParams,getMapper());
			}
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			if(intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is null" : strQueryPend.toString()));

			return null;
		}
	}
	public List<BuildControlsStatsVb> getQueryResultsForBuild(BuildControlsStatsVb dObj){

		List<BuildControlsStatsVb> collTemp = null;
		final int intKeyFieldsCount = 3;
		StringBuffer strQueryAppr = new StringBuffer("Select TAppr.YEAR,TAppr.COUNTRY, " + 
			"TAppr.LE_BOOK,TAppr.DATA_SOURCE_AT,TAppr.DATA_SOURCE,TAppr.BUILD_MODULE,Format(TAppr.LAST_BUILD_DATE, " + 
			" 'dd-MMM-yyyy HH:mm:ss') LAST_BUILD_DATE," +
			"TAppr.RECORD_INDICATOR_NT,TAppr.RECORD_INDICATOR, " + 
			"TAppr.MAKER,TAppr.VERIFIER,TAppr.INTERNAL_STATUS,Format(TAppr.DATE_LAST_MODIFIED, " + 
			" 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED," +
			"Format(TAppr.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION" +
			" From BUILD_CONTROLS_STATS TAppr " + 
			"Where TAppr.YEAR = ?  And TAppr.DATA_SOURCE = ?  And TAppr.BUILD_MODULE = ? ");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new Integer(dObj.getYear());	//[YEAR]
		objParams[1] = new String(dObj.getDataSource());	//[DATA_SOURCE]
		objParams[2] = new String(dObj.getBuildModule());	//[BUILD_MODULE]
		try
		{
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}	
	@Override
	protected List<BuildControlsStatsVb> selectApprovedRecord(BuildControlsStatsVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<BuildControlsStatsVb> doSelectPendingRecord(BuildControlsStatsVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}

	@Override
	protected void setServiceDefaults(){
		serviceName = "BuildControlsStats";
		serviceDesc = CommonUtils.getResourceManger().getString("buildControlsStats");
		tableName = "BUILD_CONTROLS_STATS";
		childTableName = "BUILD_CONTROLS_STATS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	@Override
	protected int doInsertionPendWithDc(BuildControlsStatsVb vObject){
		String query = "Insert Into BUILD_CONTROLS_STATS_PEND( " + 
		"YEAR, COUNTRY, LE_BOOK, DATA_SOURCE_AT, DATA_SOURCE, BUILD_MODULE, LAST_BUILD_DATE, " + 
			" RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, " + 
			" DATE_CREATION) " + 
			"Values (?, ?, ?, ?, ?, ?,  " + 
			"CONVERT(datetime, ?, 103), ?, ?, ?, ?, ?, GetDate(),  " + 
			"CONVERT(datetime, ?, 103))";

		Object args[] = {vObject.getYear(), vObject.getCountry(), vObject.getLeBook(), 
			 vObject.getDataSourceAt(), vObject.getDataSource(), vObject.getBuildModule(), vObject.getLastBuildDate(), 
			 vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), 
			 vObject.getVerifier(), vObject.getInternalStatus(), vObject.getDateCreation()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int doUpdateAppr(BuildControlsStatsVb vObject){
		String query = "Update BUILD_CONTROLS_STATS Set " + 
		"DATA_SOURCE_AT = ?, LAST_BUILD_DATE = CONVERT(datetime, ?, 103), RECORD_INDICATOR_NT = ?, " + 
		" RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() " + 
		"Where YEAR = ? " +
		" And COUNTRY = ? " +
		" And LE_BOOK = ? " +
		" And DATA_SOURCE = ? " +
		" And BUILD_MODULE = ? ";

		Object args[] = {vObject.getDataSourceAt(), vObject.getLastBuildDate(), vObject.getRecordIndicatorNt(),
		 vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
		vObject.getYear(), 
		vObject.getCountry(), 
		vObject.getLeBook(), 
		vObject.getDataSource(), 
		vObject.getBuildModule()};

		return getJdbcTemplate().update(query,args);
	}
	public int doUpdateLBDForAll(BuildControlsStatsVb vObject){
		String query = "Update BUILD_CONTROLS_STATS Set " + 
		"DATA_SOURCE_AT = ?, LAST_BUILD_DATE = CONVERT(datetime, ?, 103), RECORD_INDICATOR_NT = ?, " + 
		" RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() " + 
		"Where YEAR = ? " +
		" And DATA_SOURCE = ? " +
		" And BUILD_MODULE = ? ";

		Object args[] = {vObject.getDataSourceAt(), vObject.getLastBuildDate(), vObject.getRecordIndicatorNt(),
		 vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
		vObject.getYear(), 
		vObject.getDataSource(), 
		vObject.getBuildModule()};

		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doUpdatePend(BuildControlsStatsVb vObject){
		String query = "Update BUILD_CONTROLS_STATS_PEND Set " + 
		"DATA_SOURCE_AT = ?, LAST_BUILD_DATE = CONVERT(datetime, ?, 103), RECORD_INDICATOR_NT = ?," +
		" RECORD_INDICATOR = ?, MAKER = ?, VERIFIER = ?, INTERNAL_STATUS = ?, DATE_LAST_MODIFIED = GetDate() " + 
		"Where YEAR = ? " +
		" And COUNTRY = ? " +
		" And LE_BOOK = ? " +
		" And DATA_SOURCE = ? " +
		" And BUILD_MODULE = ? ";

		Object args[] = {vObject.getDataSourceAt(), vObject.getLastBuildDate(), vObject.getRecordIndicatorNt(),  
		 vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(), 
		vObject.getYear(), 
		vObject.getCountry(), 
		vObject.getLeBook(), 
		vObject.getDataSource(), 
		vObject.getBuildModule()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected int deletePendingRecord(BuildControlsStatsVb vObject){
		String query = "Delete From BUILD_CONTROLS_STATS_PEND Where " + 
		"YEAR = ? And " + 
		"COUNTRY = ? And " + 
		"LE_BOOK = ? And " + 
		"DATA_SOURCE = ? And " + 
		"BUILD_MODULE = ? " ;

		Object args[] = {vObject.getYear(), vObject.getCountry(), vObject.getLeBook(), 
			 vObject.getDataSource(), vObject.getBuildModule()};

		return getJdbcTemplate().update(query,args);
	}

	@Override
	protected String frameErrorMessage(BuildControlsStatsVb vObject, String strOperation)
	{
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg =  strErrMsg + " YEAR:" + vObject.getYear();
			strErrMsg =  strErrMsg + " COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + " LE_BOOK:" + vObject.getLeBook();
			strErrMsg =  strErrMsg + " DATA_SOURCE:" + vObject.getDataSource();
			strErrMsg =  strErrMsg + " BUILD_MODULE:" + vObject.getBuildModule();
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
	protected String getAuditString(BuildControlsStatsVb vObject)
	{
		StringBuffer strAudit = new StringBuffer("");
		try
		{
			strAudit.append(vObject.getYear());
			strAudit.append("!|#");
			if(vObject.getCountry() != null)
				strAudit.append(vObject.getCountry().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			if(vObject.getLeBook() != null)
				strAudit.append(vObject.getLeBook().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			strAudit.append(vObject.getDataSourceAt());
			strAudit.append("!|#");
			if(vObject.getDataSource() != null)
				strAudit.append(vObject.getDataSource().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			if(vObject.getBuildModule() != null)
				strAudit.append(vObject.getBuildModule().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

			if(vObject.getLastBuildDate() != null)
				strAudit.append(vObject.getLastBuildDate().trim());
			else
				strAudit.append("NULL");
			strAudit.append("!|#");

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
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doUpdateApprForAll(BuildControlsStatsVb vObject) throws RuntimeCustomException  {
		ExceptionCode exceptionCode = null;
		strApproveOperation ="Modify";
		strErrorDesc  = "";
		strCurrentOperation = "Modify";
		setServiceDefaults();
		try {
			return doUpdateApprRecordForAll(vObject);
		}catch (RuntimeCustomException rcException){
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Modify.",ex);
			logger.error( ((vObject==null)? "vObject is Null":vObject.toString()));
			strErrorDesc = ex.getMessage();
			strErrorDesc = parseErrorMsg((UncategorizedSQLException) ex);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	protected ExceptionCode doUpdateApprRecordForAll(BuildControlsStatsVb vObject) throws RuntimeCustomException  {
		BuildControlsStatsVb vObjectlocal = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation ="Modify";
		strErrorDesc  = "";
		strCurrentOperation = "Modify";
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		vObject.setRecordIndicator(Constants.STATUS_ZERO);
		vObject.setVerifier(getIntCurrentUserId());
		retVal = doUpdateLBDForAll(vObject);
		String systemDate = getSystemDate();
		vObject.setDateLastModified(systemDate);
		exceptionCode = writeAuditLog(vObject, vObjectlocal);
		if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
			exceptionCode = getResultObject(Constants.AUDIT_TRAIL_ERROR);
			throw buildRuntimeCustomException(exceptionCode);
		}
		return exceptionCode;
	}	
}
