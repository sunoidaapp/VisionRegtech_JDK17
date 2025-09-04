package com.vision.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.ADFAcqVersionsVb;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.CommonVb;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.TemplateStgVb;
import com.vision.vb.UploadFilingVb;
@Component
public class UploadFilingDao extends AbstractDao<UploadFilingVb>{
	@Value("${app.databaseType}")
	private String databaseType;
	@Autowired
	CommonApiDao commonApiDao;
	public List<UploadFilingVb> getQueryResults(UploadFilingVb dObj)
	{
		setServiceDefaults();
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook()).toUpperCase();
		Vector<Object> params = new Vector<Object>();
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("Select TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.FILE_NAME, TAppr.TEMPLATE_NAME, "
				+ "(select LE_BOOK + '-' + LEB_DESCRIPTION from le_book where LE_BOOK=TAppr.LE_BOOK and COUNTRY=TAppr.COUNTRY) as SHARE_HOLDER,"
				+ "Format(TAppr.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, TAppr.FILING_STATUS_NT, TAppr.FILING_STATUS, "
				+ " (SELECT ACQUISITION_STATUS FROM ADF_PROCESS_CONTROL APC WHERE APC.COUNTRY = TAppr.COUNTRY AND APC.LE_BOOK = TAppr.LE_BOOK AND APC.BUSINESS_DATE = TAppr.BUSINESS_DATE AND APC.TEMPLATE_NAME = TAppr.TEMPLATE_NAME) as ACQUISITION_STATUS, "
				+ "TAppr.MAKER, TAppr.VERIFIER," +
				"Format(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED, Format(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION" +
				" From ADF_UPLOAD_FILING TAppr " );
		}else {
			strBufApprove = new StringBuffer("Select TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.FILE_NAME, TAppr.TEMPLATE_NAME, "
					+ "(select LE_BOOK || '-' || LEB_DESCRIPTION from le_book where LE_BOOK=TAppr.LE_BOOK and COUNTRY=TAppr.COUNTRY) as SHARE_HOLDER,"
					+ "To_char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, TAppr.FILING_STATUS_NT, TAppr.FILING_STATUS, "
					+ " (SELECT ACQUISITION_STATUS FROM ADF_PROCESS_CONTROL APC WHERE APC.COUNTRY = TAppr.COUNTRY AND APC.LE_BOOK = TAppr.LE_BOOK AND APC.BUSINESS_DATE = TAppr.BUSINESS_DATE AND APC.TEMPLATE_NAME = TAppr.TEMPLATE_NAME) as ACQUISITION_STATUS, "
					+ "TAppr.MAKER, TAppr.VERIFIER," +
					"to_char(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED, to_char(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION" +
					" From ADF_UPLOAD_FILING TAppr " );
		}
		try
		{
			logger.info("Forming Dynamic query ");
			if (dObj.getFilingStatus() != -1)
			{
				params.addElement(new Integer(dObj.getFilingStatus()));
				CommonUtils.addToQuery("TAppr.FILING_STATUS = ?", strBufApprove);
			}
			/*if (ValidationUtil.isValid(dObj.getUploadDate()))
			{
				
				params.addElement(dObj.getUploadDate());
				CommonUtils.addToQuery("TAppr.UPLOAD_DATE = CONVERT(datetime, ?, 103) ", strBufApprove);
				
			}*/
			//check if the column [TABLE_NAME] should be included in the query
			if (ValidationUtil.isValid(dObj.getCountry()))
			{
				params.addElement(dObj.getCountry().toUpperCase());
				CommonUtils.addToQuery("UPPER(TAppr.COUNTRY) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(trimmedLeBook))
			{
				params.addElement(trimmedLeBook.toUpperCase());
				CommonUtils.addToQuery("UPPER(TAppr.LE_BOOK) = ?", strBufApprove);
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				if ("MSSQL".equalsIgnoreCase(databaseType))
					CommonUtils.addToQuery("TAppr.COUNTRY+ '-' +TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
				else
					CommonUtils.addToQuery("TAppr.COUNTRY || '-' || TAppr.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ", strBufApprove);
			}
			//check if the column [TABLE_DESCRIPTION] should be included in the query
			if (ValidationUtil.isValid(dObj.getTemplateName()))
			{
				params.addElement(dObj.getTemplateName());
				CommonUtils.addToQuery("UPPER(TAppr.TEMPLATE_NAME) LIKE ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getFileName()))
			{
				params.addElement("%" + dObj.getFileName().toUpperCase() + "%");
				CommonUtils.addToQuery("UPPER(TAppr.FILE_NAME) LIKE ?", strBufApprove);
			}
			String orderBy="";
			if ("MSSQL".equalsIgnoreCase(databaseType))
				orderBy=" Order By COUNTRY, LE_BOOK, FILE_NAME, TEMPLATE_NAME, CONVERT(datetime, BUSINESS_DATE, 103)  ";
			else
				orderBy=" Order By COUNTRY, LE_BOOK, FILE_NAME, TEMPLATE_NAME, to_date(BUSINESS_DATE, 'dd-Mon-yyyy')   ";
			return getQueryPopupResults(dObj, new StringBuffer(), strBufApprove, "", orderBy, params);
			
		}catch(Exception ex){
			
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is Null":strBufApprove.toString()));
			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UploadFilingVb uploadFilingVb = new UploadFilingVb();
				uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				uploadFilingVb.setCountry(rs.getString("COUNTRY"));
				uploadFilingVb.setLeBook(rs.getString("LE_BOOK"));
				uploadFilingVb.setFileName(rs.getString("FILE_NAME").trim());
				uploadFilingVb.setBusinessDate(rs.getString("BUSINESS_DATE").trim());
				uploadFilingVb.setUploadDate(rs.getString("FEED_DATE").trim());
				uploadFilingVb.setFilingStatusNt(rs.getInt("FILING_STATUS_NT"));
				uploadFilingVb.setFilingStatus(rs.getInt("FILING_STATUS"));
				uploadFilingVb.setMaker(rs.getLong("MAKER"));
				uploadFilingVb.setVerifier(rs.getLong("VERIFIER"));
				uploadFilingVb.setShareHolder(rs.getString("SHARE_HOLDER"));
				uploadFilingVb.setDateLastModified(rs.getString("DATE_LAST_MODIFIED").trim());
				uploadFilingVb.setDateCreation(rs.getString("DATE_CREATION").trim());
				uploadFilingVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
				uploadFilingVb.setScheduleStatus(rs.getString("ADF_SCHEDULE_STATUS"));
				uploadFilingVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				uploadFilingVb.setAdfNumber(rs.getString("ADF_NUMBER"));
				return uploadFilingVb;
			}
		};
		return mapper;
	}
	public List<UploadFilingVb> getQueryResultsForInsertCheck(UploadFilingVb dObj, int intStatus){

		List<UploadFilingVb> collTemp = null;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		final int intKeyFieldsCount = 4;
		String strQueryAppr = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String("Select TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.FILE_NAME, TAppr.TEMPLATE_NAME, "+ 
	                " (select LE_BOOK + '-' + LEB_DESCRIPTION from le_book where LE_BOOK=TAppr.LE_BOOK and COUNTRY=TAppr.COUNTRY) as SHARE_HOLDER, "+
					" Format(TAppr.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, TAppr.FILING_STATUS_NT, TAppr.FILING_STATUS, TAppr.MAKER, TAppr.VERIFIER, "+ 
					" T1.ACQUISITION_STATUS, T1.EXCEL_TEMPLATE_ID, T2.ADF_SCHEDULE_STATUS, Format(TAppr.UPLOAD_DATE, 'dd-MMM-yyyy') FEED_DATE , T1.ADF_NUMBER,  "+
					" Format(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED, Format(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION "+
					" From ADF_UPLOAD_FILING TAppr, ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 Where TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.TEMPLATE_NAME = ?  "+
					" AND TAppr.BUSINESS_DATE = CONVERT(datetime, ?, 103)  "+
					" AND TAppr.COUNTRY = T1.COUNTRY AND TAppr.LE_BOOK = T1.LE_BOOK AND TAppr.BUSINESS_DATE = T1.BUSINESS_DATE AND TAppr.TEMPLATE_NAME = T1.TEMPLATE_NAME "+ 
					" AND T1.ADF_NUMBER = T2.ADF_NUMBER " );
		}else {
			strQueryAppr = new String("Select TAppr.COUNTRY, TAppr.LE_BOOK, TAppr.FILE_NAME, TAppr.TEMPLATE_NAME, "+ 
	                " (select LE_BOOK || '-' || LEB_DESCRIPTION from le_book where LE_BOOK=TAppr.LE_BOOK and COUNTRY=TAppr.COUNTRY) as SHARE_HOLDER, "+
					" To_char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, TAppr.FILING_STATUS_NT, TAppr.FILING_STATUS, TAppr.MAKER, TAppr.VERIFIER, "+ 
					" T1.ACQUISITION_STATUS, T1.EXCEL_TEMPLATE_ID, T2.ADF_SCHEDULE_STATUS, to_char(TAppr.UPLOAD_DATE, 'dd-Mon-yyyy') FEED_DATE , T1.ADF_NUMBER,  "+
					" to_char(TAppr.DATE_LAST_MODIFIED, 'DD/MM/RRRR HH24:MI:SS') DATE_LAST_MODIFIED, to_char(TAppr.DATE_CREATION, 'DD/MM/RRRR HH24:MI:SS') DATE_CREATION "+
					" From ADF_UPLOAD_FILING TAppr, ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 Where TAppr.COUNTRY = ? AND TAppr.LE_BOOK = ? AND TAppr.TEMPLATE_NAME = ?  "+
					" AND TAppr.BUSINESS_DATE =  to_date(?, 'dd-Mon-yyyy')  "+
					" AND TAppr.COUNTRY = T1.COUNTRY AND TAppr.LE_BOOK = T1.LE_BOOK AND TAppr.BUSINESS_DATE = T1.BUSINESS_DATE AND TAppr.TEMPLATE_NAME = T1.TEMPLATE_NAME "+ 
					" AND T1.ADF_NUMBER = T2.ADF_NUMBER " );
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = trimmedLeBook;
		objParams[2] =dObj.getTemplateName();
		objParams[3] = dObj.getBusinessDate();
		try
		{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,getMapper());
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
	public List<UploadFilingVb> getUploadFillingQueryResults(UploadFilingVb dObj){

		setServiceDefaults();
		dObj.setTotalRows(0);
		String trimmedLeBook=removeDescLeBook(dObj.getShareHolder());
		StringBuffer strQueryAppr = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer(" SELECT T1.COUNTRY, T1.LE_BOOK, T1.TEMPLATE_NAME, (T1.TEMPLATE_NAME + '-' + T2.GENERAL_DESCRIPTION) TEMPLATE_NAME_DESC, "+
			       " Format (T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, "+
			       " T3.MAJOR_BUILD,(SELECT LEB.LE_BOOK + '-' + LEB.LEB_DESCRIPTION FROM le_book LEB WHERE LEB.LE_BOOK = T1.LE_BOOK AND LEB.COUNTRY = T1.COUNTRY) AS SHARE_HOLDER, "+
			       " ISNULL(T4.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, T1.ACQUISITION_STATUS, T1.EXCEL_TEMPLATE_ID, "+
			       " T3.ADF_SCHEDULE_STATUS, Format(ISNULL(T4.Upload_Date,T3.FEED_DATE),'dd-MMM-yyyy') FEED_DATE,   T1.ADF_NUMBER, T1.SUB_ADF_NUMBER, T1.PROCESS_SEQUENCE, "+
			       " CASE WHEN (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL APC WHERE APC.ADF_NUMBER = T1.ADF_NUMBER AND (APC.EOD_INITIATED_FLAG = 'Y' OR APC.AUTH_STATUS in (0,4)) ) > 0 THEN 'Y' ELSE 'N' END AS EOD_INITIATED_FLAG, "+
			       " ISNULL (T3.NODE_OVERRIDE, T3.NODE_REQUEST) AS NODE, T1.FREQUENCY_PROCESS "+
			       " , ( SELECT AST.ALPHA_SUBTAB_DESCRIPTION "
					+ " FROM ALPHA_SUB_TAB AST  "
					+ " WHERE AST.ALPHA_TAB = T3.ADF_SCHEDULE_STATUS_AT  "
					+ " AND AST.ALPHA_SUB_TAB =  "
					+ " (CASE WHEN T3.XL_SPLIT_STATUS = 'Y' AND T3.ADF_SCHEDULE_STATUS = 'P' THEN 'I' ELSE T3.ADF_SCHEDULE_STATUS END)) ADF_SCHE_STATUS, ISNULL(T4.PROCESSED_FLAG,'N') PROCESSED_FLAG  "+			       
			" FROM ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T4, TEMPLATE_NAMES T2, ADF_SCHEDULES T3 WHERE T1.COUNTRY = T4.COUNTRY AND T1.LE_BOOK = T4.LE_BOOK "+
			       " AND T1.Business_Date = T4.Business_Date AND T1.TEMPLATE_NAME = T4.TEMPLATE_NAME AND T1.Template_Name = T2.Template_Name "+
			       " AND T1.ADF_NUMBER = T3.ADF_NUMBER AND T1.country = ? AND T1.le_book = ? AND T1.BUSINESS_DATE = CONVERT(datetime, ?, 103) "+
			       " AND T1.ACQUISITION_PROCESS_TYPE = 'XLS' AND T1.FREQUENCY_PROCESS = ? AND T3.MAJOR_BUILD  =?");
		}else {
			strQueryAppr = new StringBuffer(" SELECT T1.COUNTRY, T1.LE_BOOK, T1.TEMPLATE_NAME, (T1.TEMPLATE_NAME || '-' || T2.GENERAL_DESCRIPTION) TEMPLATE_NAME_DESC, "+
				       " to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, "+
				       " T3.MAJOR_BUILD,(SELECT LEB.LE_BOOK + '-' + LEB.LEB_DESCRIPTION FROM le_book LEB WHERE LEB.LE_BOOK = T1.LE_BOOK AND LEB.COUNTRY = T1.COUNTRY) AS SHARE_HOLDER, "+
				       " NVL(T4.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, T1.ACQUISITION_STATUS, T1.EXCEL_TEMPLATE_ID, "+
				       " T3.ADF_SCHEDULE_STATUS, to_char(NVL(T4.Upload_Date,T3.FEED_DATE),'dd-Mon-yyyy') FEED_DATE,   T1.ADF_NUMBER, T1.SUB_ADF_NUMBER, T1.PROCESS_SEQUENCE, "+
				       " CASE WHEN (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL APC WHERE APC.ADF_NUMBER = T1.ADF_NUMBER AND (APC.EOD_INITIATED_FLAG = 'Y' OR APC.AUTH_STATUS in (0,4)) ) > 0 THEN 'Y' ELSE 'N' END AS EOD_INITIATED_FLAG, "+
				       " NVL (T3.NODE_OVERRIDE, T3.NODE_REQUEST) AS NODE, T1.FREQUENCY_PROCESS "+
				       " , ( SELECT AST.ALPHA_SUBTAB_DESCRIPTION "
						+ " FROM ALPHA_SUB_TAB AST  "
						+ " WHERE AST.ALPHA_TAB = T3.ADF_SCHEDULE_STATUS_AT  "
						+ " AND AST.ALPHA_SUB_TAB =  "
						+ " (CASE WHEN T3.XL_SPLIT_STATUS = 'Y' AND T3.ADF_SCHEDULE_STATUS = 'P' THEN 'I' ELSE T3.ADF_SCHEDULE_STATUS END)) ADF_SCHE_STATUS, NVL(T4.PROCESSED_FLAG,'N') PROCESSED_FLAG  "+			       
				" FROM ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T4, TEMPLATE_NAMES T2, ADF_SCHEDULES T3 WHERE T1.COUNTRY = T4.COUNTRY AND T1.LE_BOOK = T4.LE_BOOK "+
				       " AND T1.Business_Date = T4.Business_Date AND T1.TEMPLATE_NAME = T4.TEMPLATE_NAME AND T1.Template_Name = T2.Template_Name "+
				       " AND T1.ADF_NUMBER = T3.ADF_NUMBER AND T1.country = ? AND T1.le_book = ? AND T1.BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') "+
				       " AND T1.ACQUISITION_PROCESS_TYPE = 'XLS' AND T1.FREQUENCY_PROCESS = ? AND T3.MAJOR_BUILD  =?");
		}
			if(ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())){
				if ("MSSQL".equalsIgnoreCase(databaseType)) 
					strQueryAppr.append("AND T1.COUNTRY+ '-' +T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+")");
				else 
					strQueryAppr.append("AND T1.COUNTRY|| '-' ||T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+")");
				
			}		
		Vector<Object> params = new Vector<Object>();
		params.add(dObj.getCountry());
		params.add(trimmedLeBook.toUpperCase());
		params.add(dObj.getBusinessDate());
		params.add(dObj.getProcessFrequency());
		params.add(dObj.getMajorBuild());
		String orderBy = " ORDER BY ADF_NUMBER, SUB_ADF_NUMBER, PROCESS_SEQUENCE ";
		try
		{
			return getQueryPopupResults(dObj,new StringBuffer(), strQueryAppr, new String(), orderBy, params, getUploadFillingMapper());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			return null;
		}
	}	
	public List<UploadFilingVb> getQueryResults(UploadFilingVb dObj, int intStatus){
		List<UploadFilingVb> collTemp = null;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		
		StringBuffer strQueryAppr = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer(" Select T1.COUNTRY, T1.LE_BOOK, "+
			" (select ALPHA_SUB_TAB.alpha_subtab_description from ALPHA_SUB_TAB where ALPHA_SUB_TAB.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS and ALPHA_SUB_TAB.alpha_tab=T1.FREQUENCY_PROCESS_AT) as FREQUENCY_PROCESS,T3.MAJOR_BUILD AS MAJOR_BUILD, "+
			" T1.TEMPLATE_NAME, (T1.TEMPLATE_NAME + '-' + T2.GENERAL_DESCRIPTION) TEMPLATE_NAME_DESC, Format(T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, "+
			" (SELECT LEB.LE_BOOK + '-' + LEB.LEB_DESCRIPTION FROM le_book LEB WHERE LEB.LE_BOOK = T1.LE_BOOK AND LEB.COUNTRY = T1.COUNTRY) AS SHARE_HOLDER, "+
			" ISNULL(T4.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, T1.ACQUISITION_STATUS, T1.EXCEL_TEMPLATE_ID, T3.ADF_SCHEDULE_STATUS, "+
			" Format(ISNULL(T4.Upload_Date,T3.FEED_DATE),'dd-MMM-yyyy') FEED_DATE, T1.ADF_NUMBER, T1.SUB_ADF_NUMBER, T1.PROCESS_SEQUENCE, "+
			" CASE WHEN (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL APC WHERE APC.ADF_NUMBER = T1.ADF_NUMBER AND (APC.EOD_INITIATED_FLAG = 'Y' OR APC.AUTH_STATUS in (0,4) )) > 0 THEN 'Y' ELSE 'N' END AS EOD_INITIATED_FLAG, "+
			" ISNULL(T3.NODE_OVERRIDE,T3.NODE_REQUEST) as NODE "
			+ " , ( SELECT AST.ALPHA_SUBTAB_DESCRIPTION "
			+ " FROM ALPHA_SUB_TAB AST  "
			+ " WHERE AST.ALPHA_TAB = T3.ADF_SCHEDULE_STATUS_AT  "
			+ " AND AST.ALPHA_SUB_TAB =  "
			+ " (CASE WHEN T3.XL_SPLIT_STATUS = 'Y' AND T3.ADF_SCHEDULE_STATUS = 'P' THEN 'I' ELSE T3.ADF_SCHEDULE_STATUS END)) ADF_SCHE_STATUS , ISNULL(T4.PROCESSED_FLAG,'N') PROCESSED_FLAG "
			+ " FROM ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T4, TEMPLATE_NAMES T2, ADF_SCHEDULES T3 "+
			" WHERE T1.COUNTRY = T4.COUNTRY AND T1.LE_BOOK = T4.LE_BOOK AND T1.Business_Date = T4.Business_Date AND T1.TEMPLATE_NAME = T4.TEMPLATE_NAME "+
			" AND T1.Template_Name = T2.Template_Name AND T1.ADF_NUMBER = T3.ADF_NUMBER AND T1.country = ? AND T1.le_book = ? "+
			" AND T1.BUSINESS_DATE = CONVERT(datetime, ?, 103) AND T1.ACQUISITION_PROCESS_TYPE = 'XLS' AND T1.FREQUENCY_PROCESS = ? AND T3.MAJOR_BUILD = ?");
		}else {
			strQueryAppr = new StringBuffer(" Select T1.COUNTRY, T1.LE_BOOK, "+
					" (select ALPHA_SUB_TAB.alpha_subtab_description from ALPHA_SUB_TAB where ALPHA_SUB_TAB.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS and ALPHA_SUB_TAB.alpha_tab=T1.FREQUENCY_PROCESS_AT) as FREQUENCY_PROCESS,T3.MAJOR_BUILD AS MAJOR_BUILD, "+
					" T1.TEMPLATE_NAME, (T1.TEMPLATE_NAME || '-' || T2.GENERAL_DESCRIPTION) TEMPLATE_NAME_DESC, to_char(T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, "+
					" (SELECT LEB.LE_BOOK || '-' || LEB.LEB_DESCRIPTION FROM le_book LEB WHERE LEB.LE_BOOK = T1.LE_BOOK AND LEB.COUNTRY = T1.COUNTRY) AS SHARE_HOLDER, "+
					" NVL(T4.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, T1.ACQUISITION_STATUS, T1.EXCEL_TEMPLATE_ID, T3.ADF_SCHEDULE_STATUS, "+
					" to_char(NVL(T4.Upload_Date,T3.FEED_DATE),'dd-Mon-yyyy') FEED_DATE, T1.ADF_NUMBER, T1.SUB_ADF_NUMBER, T1.PROCESS_SEQUENCE, "+
					" CASE WHEN (SELECT COUNT (1) FROM ADF_PROCESS_CONTROL APC WHERE APC.ADF_NUMBER = T1.ADF_NUMBER AND (APC.EOD_INITIATED_FLAG = 'Y' OR APC.AUTH_STATUS in (0,4) )) > 0 THEN 'Y' ELSE 'N' END AS EOD_INITIATED_FLAG, "+
					" NVL(T3.NODE_OVERRIDE,T3.NODE_REQUEST) as NODE "
					+ " , ( SELECT AST.ALPHA_SUBTAB_DESCRIPTION "
					+ " FROM ALPHA_SUB_TAB AST  "
					+ " WHERE AST.ALPHA_TAB = T3.ADF_SCHEDULE_STATUS_AT  "
					+ " AND AST.ALPHA_SUB_TAB =  "
					+ " (CASE WHEN T3.XL_SPLIT_STATUS = 'Y' AND T3.ADF_SCHEDULE_STATUS = 'P' THEN 'I' ELSE T3.ADF_SCHEDULE_STATUS END)) ADF_SCHE_STATUS , NVL(T4.PROCESSED_FLAG,'N') PROCESSED_FLAG "
					+ " FROM ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T4, TEMPLATE_NAMES T2, ADF_SCHEDULES T3 "+
					" WHERE T1.COUNTRY = T4.COUNTRY AND T1.LE_BOOK = T4.LE_BOOK AND T1.Business_Date = T4.Business_Date AND T1.TEMPLATE_NAME = T4.TEMPLATE_NAME "+
					" AND T1.Template_Name = T2.Template_Name AND T1.ADF_NUMBER = T3.ADF_NUMBER AND T1.country = ? AND T1.le_book = ? "+
					" AND T1.BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND T1.ACQUISITION_PROCESS_TYPE = 'XLS' AND T1.FREQUENCY_PROCESS = ? AND T3.MAJOR_BUILD = ?");
		}
		if(ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())){
			if ("MSSQL".equalsIgnoreCase(databaseType)) 
				strQueryAppr.append("AND T1.COUNTRY+ '-' +T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+")");
			else 
				strQueryAppr.append("AND T1.COUNTRY|| '-' ||T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+")");

		}
		
		Vector<Object> params = new Vector<Object>();
		params.add(dObj.getCountry());
		params.add(trimmedLeBook.toUpperCase());
		params.add(dObj.getBusinessDate());
		params.add(dObj.getProcessFrequency());
		params.add(dObj.getMajorBuild());
		String orderBy = " ORDER BY ADF_NUMBER, SUB_ADF_NUMBER, PROCESS_SEQUENCE, EXCEL_FILE_PATTERN ";
		try
		{
			return getQueryPopupResults(dObj,new StringBuffer(), strQueryAppr, new String(), orderBy, params, getUploadFillingMapper());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			return null;
		}
	}
	@Override
	protected List<UploadFilingVb> selectApprovedRecord(UploadFilingVb vObject){
		return getQueryResults(vObject, Constants.STATUS_ZERO);
	}
	
	public int getCountOfRunningTemplates(String adfNumber, int processNumber){
		int count = 0;
		final int intKeyFieldsCount = 1;
		String strQuery = "";
		if(processNumber == 1){
			strQuery = new String("SELECT COUNT(1) FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 "
					+ " WHERE T1.ADF_NUMBER = T2.ADF_NUMBER AND T1.ADF_NUMBER = ? AND (T1.EOD_INITIATED_FLAG = 'Y' OR T1.AUTH_STATUS IN (0,4))");
		}else if(processNumber == 2){
			strQuery = new String("SELECT COUNT(1) FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 "
					+ " WHERE T1.ADF_NUMBER = T2.ADF_NUMBER AND T1.ADF_NUMBER = ? AND T2.ADF_SCHEDULE_STATUS IN ('I', 'M') ");
		}else if(processNumber == 3){
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				strQuery = new String(" SELECT COUNT(1) "
						+ " FROM ADF_SCHEDULES T1 "
						+ " WHERE T1.ADF_NUMBER = ? "
						+ " AND T1.ADF_SCHEDULE_STATUS IN (SELECT VVN.RETURN_VALUE_CHAR FROM VISION_VARIABLES_NARROW VVN WHERE VVN.VARIABLE = 'NON_ADF_SCHEDULE_PICKUP_STATUS_LIST')"
						+ " AND EXISTS ( SELECT 1 "
						+ " FROM ADF_PROCESS_CONTROL T2 , ADF_UPLOAD_FILING T5 "
						+ " WHERE T2.ADF_NUMBER = T1.ADF_NUMBER"
						+ " AND T2.ACQUISITION_STATUS IN (SELECT VVN.RETURN_VALUE_CHAR FROM VISION_VARIABLES_NARROW VVN WHERE VVN.VARIABLE = 'NON_ADF_TEMPLATE_PICKUP_STATUS_LIST')"
						+ " AND T2.NEXT_PROCESS_TIME BETWEEN T2.START_TIME AND T2.END_TIME"
						+ " AND T2.NEXT_PROCESS_TIME   <= GetDate()"
						+ " AND T2.EXT_ITERATION_COUNT <= (SELECT VVN.RETURN_VALUE_INT FROM VISION_VARIABLES_NARROW VVN WHERE VVN.VARIABLE = 'MAX_ITERATION_COUNT')"
						+ " AND T2.COUNTRY = T5.COUNTRY"
						+ " AND T2.LE_BOOK = T5.LE_BOOK"
						+ " AND T2.BUSINESS_DATE = T5.BUSINESS_DATE"
						+ " AND T2.TEMPLATE_NAME = T5.TEMPLATE_NAME) ");
			}else {
				strQuery = new String(" SELECT COUNT(1) "
						+ " FROM ADF_SCHEDULES T1 "
						+ " WHERE T1.ADF_NUMBER = ? "
						+ " AND T1.ADF_SCHEDULE_STATUS IN (SELECT VVN.RETURN_VALUE_CHAR FROM VISION_VARIABLES_NARROW VVN WHERE VVN.VARIABLE = 'NON_ADF_SCHEDULE_PICKUP_STATUS_LIST')"
						+ " AND EXISTS ( SELECT 1 "
						+ " FROM ADF_PROCESS_CONTROL T2 , ADF_UPLOAD_FILING T5 "
						+ " WHERE T2.ADF_NUMBER = T1.ADF_NUMBER"
						+ " AND T2.ACQUISITION_STATUS IN (SELECT VVN.RETURN_VALUE_CHAR FROM VISION_VARIABLES_NARROW VVN WHERE VVN.VARIABLE = 'NON_ADF_TEMPLATE_PICKUP_STATUS_LIST')"
						+ " AND T2.NEXT_PROCESS_TIME BETWEEN T2.START_TIME AND T2.END_TIME"
						+ " AND T2.NEXT_PROCESS_TIME   <= sysdate"
						+ " AND T2.EXT_ITERATION_COUNT <= (SELECT VVN.RETURN_VALUE_INT FROM VISION_VARIABLES_NARROW VVN WHERE VVN.VARIABLE = 'MAX_ITERATION_COUNT')"
						+ " AND T2.COUNTRY = T5.COUNTRY"
						+ " AND T2.LE_BOOK = T5.LE_BOOK"
						+ " AND T2.BUSINESS_DATE = T5.BUSINESS_DATE"
						+ " AND T2.TEMPLATE_NAME = T5.TEMPLATE_NAME) ");
			}
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] =adfNumber;
		try
		{
			count = getJdbcTemplate().queryForObject(strQuery.toString(),objParams, int.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public ExceptionCode doInsertRecord(List<UploadFilingVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strErrorDesc  = "";
		strCurrentOperation = "Add";
		setServiceDefaults();
		try {
			String sysDate=getSysteDateForAdfProcess();
			String fileNameCheck = "";
			if(vObjects!=null && !vObjects.isEmpty())
				fileNameCheck = vObjects.get(0).getFileName();
			long versionNumer = 0;
			boolean isFirstRecor = false;
			for(UploadFilingVb vObject : vObjects){
				String templateName = vObject.getTemplateName();
				templateName = templateName.split("-")[0];
				vObject.setTemplateName(templateName.trim());
				if(vObject.isChecked()){
					if(!fileNameCheck.equalsIgnoreCase(vObject.getFileName())){
						fileNameCheck = vObject.getFileName();
						versionNumer = getMaxVersionNo(vObject.getFileName());
					}
					System.out.println("Bef : "+versionNumer+" File Name "+vObject.getFileName());
					logger.info("Bef : "+versionNumer+" File Name "+vObject.getFileName());
					logger.error("Bef : "+versionNumer+" File Name "+vObject.getFileName());
					if(!isFirstRecor){
//						vObject.setVersionNo(getMaxVersionNo(vObject.getFileName()));
						versionNumer = getMaxVersionNo(vObject.getFileName());
						isFirstRecor = true;
					}
					System.out.println("Af : "+versionNumer+" File Name "+vObject.getFileName());
					logger.info("Af : "+versionNumer+" File Name "+vObject.getFileName());
					logger.error("Af : "+versionNumer+" File Name "+vObject.getFileName());
					vObject.setVersionNo(versionNumer);
/*					if(!ValidationUtil.isValid(vObject.getUploadDate())){
						vObject.setUploadDate(dbGetDate());
					}*/
					List<UploadFilingVb> collTemp = getQueryResultsForInsertCheck(vObject, Constants.STATUS_ZERO);
				    if (collTemp == null){
				    	throw new RuntimeCustomException(getResultObject(Constants.ERRONEOUS_OPERATION));
				    }
				    if (vObject.getFilingStatus() != 1 && collTemp != null && !collTemp.isEmpty()){
				    	UploadFilingVb vObjectPersis = collTemp.get(0);
				    	vObjectPersis.setMaker(intCurrentUserId);
/*				    	if(vObjectPersis.getFilingStatus() == 1){
				    		strErrorDesc="File Upload in Progress for Template ID : "+vObjectPersis.getTemplateName();
				    		throw new RuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
				    	}*/
				    	vObjectPersis.setFilingStatus(4);
				    	vObjectPersis.setFileName(vObject.getFileName());
						retVal = doUpdateAppr(vObjectPersis);
						if (retVal != Constants.SUCCESSFUL_OPERATION)
						{
							throw new RuntimeCustomException(getResultObject(retVal));
						}
						if("C".equalsIgnoreCase(vObjectPersis.getScheduleStatus()) 
								|| "E".equalsIgnoreCase(vObjectPersis.getScheduleStatus())  
								|| "R".equalsIgnoreCase(vObjectPersis.getScheduleStatus())){
							vObjectPersis.setScheduleStatus("P");
							retVal = doUpdateScheduleStatus(vObjectPersis);
							if (retVal != Constants.SUCCESSFUL_OPERATION)
							{
								throw new RuntimeCustomException(getResultObject(retVal));
							}
						}
						vObjectPersis.setAcquisitionStatus("YTP");
						vObject.setAcquisitionStatus("YTP");
						retVal = doUpdateProcessStatus(vObjectPersis);
						retVal = doUpdateADFDataAcqProcessStatus("C", vObject);
						vObject.setMaker(intCurrentUserId);
						vObjectPersis.setFilingStatus(4);
				    }
				    else
					{
				    	//Try inserting the record
				    	vObject.setFilingStatus(5);
				        vObject.setMaker(intCurrentUserId);
				        String strDBDate = getSystemDate();
				        vObject.setDateCreation(strDBDate);
				        vObject.setDateLastModified(strDBDate);
				        retVal = doInsertionAppr(vObject); 
						if (retVal != Constants.SUCCESSFUL_OPERATION)
						{
							throw new RuntimeCustomException(getResultObject(retVal));
						}
						if("C".equalsIgnoreCase(vObject.getScheduleStatus()) 
								|| "E".equalsIgnoreCase(vObject.getScheduleStatus())  
								|| "R".equalsIgnoreCase(vObject.getScheduleStatus())){
							vObject.setScheduleStatus("P");
							retVal = doUpdateScheduleStatus(vObject);
							if (retVal != Constants.SUCCESSFUL_OPERATION)
							{
								throw new RuntimeCustomException(getResultObject(retVal));
							}
						}
						vObject.setAcquisitionStatus("YTP");
						retVal = doUpdateProcessStatus(vObject);
						retVal = doUpdateADFDataAcqProcessStatus("C", vObject);
/*						if("E".equalsIgnoreCase(vObject.getScheduleStatus())){
							
						}*/
					}
				}
			}
			return getResultObject(Constants.SUCCESSFUL_OPERATION);
		}catch (RuntimeCustomException rcException) {
			throw rcException;
		}catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}catch(Exception ex){
			logger.error("Error in Add.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	@Override
	protected ExceptionCode doInsertRecordForNonTrans(UploadFilingVb vObject) throws RuntimeCustomException{
		List<UploadFilingVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		strApproveOperation =Constants.ADD;
		strErrorDesc  = "";
		strCurrentOperation = Constants.ADD;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		collTemp = selectApprovedRecord(vObject);
		if (collTemp == null){
			logger.error("Collection is null for Select Approved Record");
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		}
		// If record already exists in the approved table, reject the addition
		if (collTemp.size() > 0 ){
			retVal = doUpdateAppr(vObject);
			
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(retVal);
			return exceptionCode;
		}else{	
			// Try inserting the record
			vObject.setVerifier(0);
			vObject.setRecordIndicator(Constants.STATUS_INSERT);
			String systemDate = getSystemDate();
			vObject.setDateLastModified(systemDate);
			vObject.setDateCreation(systemDate);
			retVal = doInsertionPend(vObject);
			if (retVal != Constants.SUCCESSFUL_OPERATION){
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			}
			exceptionCode = getResultObject(retVal);
			return exceptionCode;
		}
	}
	@Override
	protected int doUpdateAppr(UploadFilingVb vObject){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String nodeName = System.getenv("VISION_NODE_NAME");
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update ADF_UPLOAD_FILING SET FILE_NAME = ?, NODE_REQUEST = ?, " +
			" FILING_STATUS_NT = ?, FILING_STATUS = ?, DATE_LAST_MODIFIED = GetDate(), MAKER = ?,  VERIFIER = ?,  UPLOAD_DATE = CONVERT(datetime, ?, 103) " +
			" , PROCESSED_FLAG = 'N' Where COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? AND BUSINESS_DATE = CONVERT(datetime, ?, 103) ";
		}else {
			query = "Update ADF_UPLOAD_FILING SET FILE_NAME = ?, NODE_REQUEST = ?, " +
					" FILING_STATUS_NT = ?, FILING_STATUS = ?, DATE_LAST_MODIFIED = sysdate, MAKER = ?,  VERIFIER = ?,  UPLOAD_DATE = to_date( ?, 'dd-Mon-yyyy') " +
					" , PROCESSED_FLAG = 'N' Where COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? AND BUSINESS_DATE = to_date ( ?, 'dd-Mon-yyyy') ";
		}
		Object[] args = {vObject.getFileName(), nodeName, vObject.getFilingStatusNt(), vObject.getFilingStatus(), vObject.getMaker(), vObject.getVerifier()
				, vObject.getUploadDate(), vObject.getCountry(), trimmedLeBook, 
				vObject.getTemplateName(), vObject.getBusinessDate()};
		return getJdbcTemplate().update(query,args);
	}
	public int doUpdateScheduleStatus(UploadFilingVb vObject){
		String query = "update ADF_SCHEDULES SET ADF_SCHEDULE_STATUS = ?, SUBMITTER_ID = '"+intCurrentUserId+"', XL_SPLIT_STATUS = 'N' WHERE ADF_NUMBER = ? ";
		Object[] args = {vObject.getScheduleStatus(), vObject.getAdfNumber()};
		return getJdbcTemplate().update(query,args);
	}
	public int doUpdateProcessStatus(UploadFilingVb vObject){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "UPDATE ADF_PROCESS_CONTROL  SET ACQUISITION_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? AND BUSINESS_DATE = CONVERT(datetime, ?, 103) AND TEMPLATE_NAME = ? ";
		}else {
			query = "UPDATE ADF_PROCESS_CONTROL  SET ACQUISITION_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? AND BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND TEMPLATE_NAME = ? ";
		}
		Object[] args = {vObject.getAcquisitionStatus(), vObject.getCountry(), trimmedLeBook, vObject.getBusinessDate(), vObject.getTemplateName()};
		return getJdbcTemplate().update(query,args);
	}	
	public int doUpdateADFDataAcqProcessStatus(String tempateControlStatus, UploadFilingVb vObject){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "UPDATE ADF_DATA_ACQUISITION   SET  TEMPLATE_CONTROL_STATUS = ? WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_NAME = ? ";
		Object[] args = {tempateControlStatus, vObject.getCountry(), trimmedLeBook, vObject.getTemplateName()};
		return getJdbcTemplate().update(query,args);
	}
	@Override
	protected int doInsertionAppr(UploadFilingVb vObject){
		String nodeName = System.getenv("VISION_NODE_NAME");
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into ADF_UPLOAD_FILING ( COUNTRY, LE_BOOK, FILE_NAME, TEMPLATE_NAME, BUSINESS_DATE, UPLOAD_DATE, FILING_STATUS_NT, FILING_STATUS,"
				+ " MAKER, VERIFIER, INTERNAL_STATUS, CURR_VERSION_NO, DATE_LAST_MODIFIED, DATE_CREATION, NODE_REQUEST, PROCESSED_FLAG) "+
			"Values (?, ?, ?, ?, CONVERT(datetime, ?, 103),"
			+ "GetDate()"
			+ ", ?, ?, ?, ?, ?, ?, GetDate(), GetDate(), ?, ?)";
		}else {
			query = "Insert Into ADF_UPLOAD_FILING ( COUNTRY, LE_BOOK, FILE_NAME, TEMPLATE_NAME, BUSINESS_DATE, UPLOAD_DATE, FILING_STATUS_NT, FILING_STATUS,"
					+ " MAKER, VERIFIER, INTERNAL_STATUS, CURR_VERSION_NO, DATE_LAST_MODIFIED, DATE_CREATION, NODE_REQUEST, PROCESSED_FLAG) "+
				"Values (?, ?, ?, ?, to_char( ?, 'dd-Mon-yyyy'),"
				+ "sysdate"
				+ ", ?, ?, ?, ?, ?, ?, sysdate, sysdate, ?, ?)";
		}
		Object[] args = { vObject.getCountry(), trimmedLeBook, vObject.getFileName(), vObject.getTemplateName(),
				vObject.getBusinessDate(), /*vObject.getUploadDate(),*/ vObject.getFilingStatusNt(),
				vObject.getFilingStatus(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus(),
				vObject.getVersionNo() + 1, nodeName, "N"};  
		return getJdbcTemplate().update(query,args);
	}
	public  synchronized long  getMaxSequence(){
		StringBuffer strBufApprove = new StringBuffer("Select MAX(TAppr.UPLOAD_SEQUENCE) From VISION_UPLOAD TAppr ");
		try{
			return getJdbcTemplate().queryForObject(strBufApprove.toString(), int.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	public long getUploadStatus(UploadFilingVb vObject){
		StringBuffer strBufApprove = new StringBuffer("select UPLOAD_STATUS from VISION_UPLOAD where upload_sequence = '"+vObject.getUploadSequence()+"' ");
		try{
			return getJdbcTemplate().queryForObject(strBufApprove.toString(), long.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	@Transactional(rollbackForClassName={"java.lang.Exception"})
	public synchronized int doInsertionApprUpload(UploadFilingVb vObject){
		long maxUploadSequence = getMaxSequence();
		vObject.setUploadSequence(++maxUploadSequence+"");
		String addXlsxExtension = vObject.getFileName();
		if(ValidationUtil.isValid(vObject.getFileName())){
			addXlsxExtension = vObject.getFileName()+".XLSX";
		}
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into VISION_UPLOAD ( TABLE_NAME, FILE_NAME, UPLOAD_SEQUENCE, UPLOAD_DATE, " +
				"UPLOAD_STATUS_NT, UPLOAD_STATUS, MAKER, DATE_LAST_MODIFIED, DATE_CREATION) "+
				"Values (?, ?, ?, To_Date(?, 'DD-MON-RRRR HH24:MI:SS'), ?, ?, ?, GetDate(), GetDate())";
		}else {
			query = "Insert Into VISION_UPLOAD ( TABLE_NAME, FILE_NAME, UPLOAD_SEQUENCE, UPLOAD_DATE, " +
					"UPLOAD_STATUS_NT, UPLOAD_STATUS, MAKER, DATE_LAST_MODIFIED, DATE_CREATION) "+
					"Values (?, ?, ?, to_char(?, 'DD-MON-RRRR HH24:MI:SS'), ?, ?, ?, sysdate, sysdate)";
		}
		Object[] args = {vObject.getFeedStgName(),addXlsxExtension,vObject.getUploadSequence(),
			vObject.getVisionTableUploadDate(), vObject.getUploadStatusNt(),vObject.getUploadStatus(),vObject.getMaker()};  
		return getJdbcTemplate().update(query,args);
	}
	public long getMaxVersionNo(ADFAcqVersionsVb vObject){
		final int intKeyFieldsCount = 5;
		long count = 0;
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String strQuery = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQuery = new String("SELECT ISNULL(MAX(VERSION_NO), 0) FROM ACQ_VERSION_NO WHERE COUNTRY = ? AND LE_BOOK = ? AND "
				+ "BUSINESS_DATE = CONVERT(datetime, ?, 103) AND FEED_DATE = CONVERT(datetime, ?, 103) AND TEMPLATE_NAME = ? ");
		}else {
			strQuery = new String("SELECT NVL(MAX(VERSION_NO), 0) FROM ACQ_VERSION_NO WHERE COUNTRY = ? AND LE_BOOK = ? AND "
					+ "BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND FEED_DATE = to_date( ?, 'dd-Mon-yyyy') AND TEMPLATE_NAME = ? ");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] =vObject.getCountry();
		objParams[1] =trimmedLeBook;
		objParams[2] =vObject.getBusinessDate();
		objParams[3] =vObject.getFeedDate();
		objParams[4] =vObject.getTemplateName();
		try
		{
			count = getJdbcTemplate().queryForObject(strQuery.toString(),objParams, long.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	public long getMaxVersionNo(String country, String leBook, String businessDate, String tempalteName){
		final int intKeyFieldsCount = 5;
		long count = 0;
		String trimmedLeBook=removeDescLeBook(leBook);
		String strQuery = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQuery = new String("SELECT ISNULL(MAX(VERSION_NO), 0) FROM ACQ_VERSION_NO WHERE COUNTRY = ? AND LE_BOOK = ? AND "
				+ "BUSINESS_DATE = CONVERT(datetime, ?, 103) AND FEED_DATE = CONVERT(datetime, ?, 103) AND TEMPLATE_NAME = ? ");
		}else {
			strQuery = new String("SELECT NVL(MAX(VERSION_NO), 0) FROM ACQ_VERSION_NO WHERE COUNTRY = ? AND LE_BOOK = ? AND "
					+ "BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND FEED_DATE = to_date( ?, 'dd-Mon-yyyy') AND TEMPLATE_NAME = ? ");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] =country;
		objParams[1] =trimmedLeBook;
		objParams[2] =businessDate;
		objParams[3] =businessDate;
		objParams[4] =tempalteName;
		try
		{
			count = getJdbcTemplate().queryForObject(strQuery.toString(),objParams, long.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	public String getADFBuildStatus(String country, String leBook, String businessDate, String tempalteName){
		final int intKeyFieldsCount = 3;
		String adfStatus = "";
		String trimmedLeBook=removeDescLeBook(leBook);
		String strQuery = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQuery = new String("SELECT ISNULL(MAX(ADF_SCHEDULE_STATUS), 'C') ADF_SCHEDULE_STATUS FROM ADF_SCHEDULES WHERE ADF_NUMBER =("
				+ "SELECT MAX(ADF_NUMBER) FROM ADF_PROCESS_CONTROL WHERE COUNTRY = ? AND LE_BOOK = ? AND "
				+ "BUSINESS_DATE = CONVERT(datetime, ?, 103) )");
		}else {
			strQuery = new String("SELECT NVL(MAX(ADF_SCHEDULE_STATUS), 'C') ADF_SCHEDULE_STATUS FROM ADF_SCHEDULES WHERE ADF_NUMBER =("
					+ "SELECT MAX(ADF_NUMBER) FROM ADF_PROCESS_CONTROL WHERE COUNTRY = ? AND LE_BOOK = ? AND "
					+ "BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') )");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] =country;
		objParams[1] =trimmedLeBook;
		objParams[2] =businessDate;
		try
		{
			adfStatus = getJdbcTemplate().queryForObject(strQuery.toString(), objParams,String.class);
			return  adfStatus;
		}catch(Exception ex){
			ex.printStackTrace();
			return "C";
		}
	}
public int getADFBuildStatusWith(String originalFileName){
		final int intKeyFieldsCount = 1;
		int count = 0;
		String strQuery = new String("SELECT COUNT(1) FROM ADF_SCHEDULES T1 WHERE  T1.ADF_SCHEDULE_STATUS IN ('I','M') AND T1.ADF_NUMBER IN "
				+ "(SELECT DISTINCT T2.ADF_NUMBER FROM ADF_PROCESS_CONTROL T2 WHERE T2.EXCEL_FILE_PATTERN = ?)");
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = originalFileName.toUpperCase();
		try
		{
			count = getJdbcTemplate().queryForObject(strQuery.toString(), objParams, int.class);
			return  count;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}	
	public List<String> getDynamicHashVariable(String variableName){
		String strQuery = new String("SELECT VARIABLE_SCRIPT, SCRIPT_TYPE FROM VISION_DYNAMIC_HASH_VAR WHERE VARIABLE_NAME = '"+variableName+"' ");
			return getJdbcTemplate().query(strQuery,new ResultSetExtractor<ArrayList<String>>(){
				public ArrayList<String> extractData(ResultSet rs) throws SQLException ,org.springframework.dao.DataAccessException {
					ArrayList<String> commonVbs = new ArrayList<String>();
					while(rs.next()) {
						commonVbs.add(rs.getString("VARIABLE_SCRIPT"));
						commonVbs.add(rs.getString("SCRIPT_TYPE"));
					}
					return commonVbs;
				}
			});
	}
	@Transactional(rollbackForClassName={"java.lang.Exception"})
	public int insertUpdateAdfAcqVersion(ADFAcqVersionsVb vObject){
		
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		/*String sql="select COUNT(1) VERSION_NO from ACQ_VERSION_NO where COUNTRY='"+vObject.getCountry()+"' AND LE_BOOK='"+trimmedLeBook+"' AND FEED_DATE=To_Date('"+vObject.getFeedDate()+"', 'dd-MMM-yyyy') AND TEMPLATE_NAME='"+vObject.getTemplateName()+"' AND BUSINESS_DATE=To_Date('"+vObject.getBusinessDate()+"', 'dd-MMM-yyyy')";
		
		long count=getJdbcTemplate().queryForLong(sql);
				
		if(count>0){
			String query = "Select MAX(VERSION_NO) From ACQ_VERSION_NO where COUNTRY='"+vObject.getCountry()+"' "
					+ "AND LE_BOOK='"+trimmedLeBook+"' AND FEED_DATE=To_Date('"+vObject.getFeedDate()+"', 'dd-MMM-yyyy')"
							+ " AND TEMPLATE_NAME='"+vObject.getTemplateName()+"'"
									+ "AND BUSINESS_DATE=To_Date('"+vObject.getBusinessDate()+"', 'dd-MMM-yyyy')";
			try{
				vObject.setVersionNo(getJdbcTemplate().queryForInt(query));
				return 1;
			}catch(Exception ex){
				ex.printStackTrace();
				return 0;
			}
		}else{*/
			long  versionNo = getMaxVersionNo(vObject);
			vObject.setVersionNo(++versionNo);
			String query = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "Insert Into ACQ_VERSION_NO (COUNTRY, LE_BOOK, BUSINESS_DATE, FEED_DATE, TEMPLATE_NAME, ACQUISITION_PROCESS_TYPE_AT, ACQUISITION_PROCESS_TYPE, "+
				" VERSION_NO, PROCESS_START_TIME, PROCESS_END_TIME, EXT_START_TIME, EXT_END_TIME, UPL_START_TIME, UPL_END_TIME, INT_START_TIME, INT_END_TIME, ACQ_VERSION_STATUS_NT, "+
				" ACQ_VERSION_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
					" Values (?, ?, CONVERT(datetime, ?, 103), CONVERT(datetime, ?, 103), ?, ?, ?, ?, CONVERT(datetime, ?, 103), ?, ?, ?, CONVERT(datetime, ?, 103), ?, ?, ?"
					+ ", ?, ?, ?, ?, ?, ?, ?, GetDate(), GetDate() ) ";
			}else {
				query = "Insert Into ACQ_VERSION_NO (COUNTRY, LE_BOOK, BUSINESS_DATE, FEED_DATE, TEMPLATE_NAME, ACQUISITION_PROCESS_TYPE_AT, ACQUISITION_PROCESS_TYPE, "+
						" VERSION_NO, PROCESS_START_TIME, PROCESS_END_TIME, EXT_START_TIME, EXT_END_TIME, UPL_START_TIME, UPL_END_TIME, INT_START_TIME, INT_END_TIME, ACQ_VERSION_STATUS_NT, "+
						" ACQ_VERSION_STATUS, RECORD_INDICATOR_NT, RECORD_INDICATOR, MAKER, VERIFIER, INTERNAL_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
							" Values (?, ?, to_char( ?, 'dd-Mon-yyyy'), to_char( ?, 'dd-Mon-yyyy'), ?, ?, ?, ?, to_char(?,'DD-MON-RRRR HH24:MI:SS'), ?, ?, ?, to_char(?,'DD-MON-RRRR HH24:MI:SS'), ?, ?, ?"
							+ ", ?, ?, ?, ?, ?, ?, ?, sysdate, sysdate ) "; 
			}
			Object args[] = {vObject.getCountry(), trimmedLeBook, vObject.getBusinessDate(), vObject.getFeedDate(), vObject.getTemplateName(), vObject.getAcquisitionProcessTypeAt(),
					vObject.getAcquisitionProcessType(), vObject.getVersionNo(), vObject.getProcessStartTime(), vObject.getProcessEndTime(), vObject.getExtStartTime()
					, vObject.getExtEndTime(), vObject.getUplStartTime(), vObject.getUplEndTime(), vObject.getIntStartTime(), vObject.getIntEndTime(), vObject.getAcqVersionStatusNt(), vObject.getAcqVersionStatus(), 
					vObject.getRecordIndicatorNt(), vObject.getRecordIndicator(), vObject.getMaker(), vObject.getVerifier(), vObject.getInternalStatus()};
			return getJdbcTemplate().update(query,args);			
		//}
	}	
	
	@Override
	protected void setServiceDefaults(){
		serviceName = "AdfUploadFiling";
		serviceDesc = "ADF Upload Filing";
		tableName = "ADF_UPLOAD_FILING";
		childTableName = "ADF_UPLOAD_FILING";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	/**
	 * 
	 * @param dObj
	 * @return count
	 */
	public int getCountOfUploadTables(UploadFilingVb dObj)
	{
		Object objParams[]=new Object[2];;
		StringBuffer strBufApprove = new StringBuffer("select count(1) from VISION_TABLES MAXTABLES, PROFILE_PRIVILEGES PP, VISION_USERS MU " +
				"where MAXTABLES.TABLE_NAME =? AND PP.MENU_GROUP =MAXTABLES.MENU_GROUP"+ 
				" AND MAXTABLES.UPLOAD_ALLOWED  ='Y' AND PP.P_EXCEL_UPLOAD ='Y' AND MU.USER_GROUP=PP.USER_GROUP " +
				" AND MU.USER_PROFILE=PP.USER_PROFILE AND MU.VISION_ID = ? ");
		try{
			objParams[0] = dObj.getTemplateName() == null? "" : dObj.getTemplateName().toUpperCase();
			objParams[1] = dObj.getMaker();
			return getJdbcTemplate().queryForObject(strBufApprove.toString(), objParams, int.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	
	public List<UploadFilingVb> getDefaultTemplates(UploadFilingVb dObj){
		List<UploadFilingVb> collTemp = null;
		final int intKeyFieldsCount = 5;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		String strQueryAppr = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String("select T1.COUNTRY, T1.LE_BOOK ,T1.TEMPLATE_NAME, (select LE_BOOK + '-' + LEB_DESCRIPTION from le_book where country = ? "
				+ " and le_book = ? ) as SHARE_HOLDER , ISNULL(FILING_STATUS, -1) FILING_STATUS "+
                 ", T1.ACQUISITION_STATUS, T1.EXCEL_FILE_PATTERN from ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T2 where  "+
                 " T1.COUNTRY = T2.COUNTRY "+
                 " AND T1.LE_BOOK = T2.LE_BOOK "+
                 " AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME "+
                 " AND T1.COUNTRY = ? and T1.LE_BOOK = ? and T1.BUSINESS_DATE = CONVERT(datetime, ?, 103) ORDER BY COUNTRY, LE_BOOK, TEMPLATE_NAME ");
		}else {
			strQueryAppr = new String("select T1.COUNTRY, T1.LE_BOOK ,T1.TEMPLATE_NAME, (select LE_BOOK || '-' || LEB_DESCRIPTION from le_book where country = ? "
					+ " and le_book = ? ) as SHARE_HOLDER , NVL(FILING_STATUS, -1) FILING_STATUS "+
	                 ", T1.ACQUISITION_STATUS, T1.EXCEL_FILE_PATTERN from ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T2 where  "+
	                 " T1.COUNTRY = T2.COUNTRY "+
	                 " AND T1.LE_BOOK = T2.LE_BOOK "+
	                 " AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME "+
	                 " AND T1.COUNTRY = ? and T1.LE_BOOK = ? and T1.BUSINESS_DATE = to_date(? ,'dd-Mon-yyyy') ORDER BY COUNTRY, LE_BOOK, TEMPLATE_NAME ");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = trimmedLeBook;
		objParams[2] = dObj.getCountry();
		objParams[3] = trimmedLeBook;
		objParams[4] = dObj.getBusinessDate();
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UploadFilingVb uploadFilingVb = new UploadFilingVb();
					uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
					uploadFilingVb.setCountry(rs.getString("COUNTRY"));
					uploadFilingVb.setLeBook(rs.getString("LE_BOOK"));
					uploadFilingVb.setShareHolder(rs.getString("SHARE_HOLDER"));
					uploadFilingVb.setFilingStatus(rs.getInt("FILING_STATUS"));
					uploadFilingVb.setFileName(rs.getString("EXCEL_FILE_PATTERN"));
					uploadFilingVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
//					uploadFilingVb.setUploadDate(rs.getString("UPLOAD_DATE"));
					return uploadFilingVb;
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
	public List<UploadFilingVb> getDataFromProcessControl(UploadFilingVb dObj){
		List<UploadFilingVb> collTemp = null;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		final int intKeyFieldsCount = 6;
		String strQueryAppr = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String("select T1.COUNTRY, T1.LE_BOOK ,T1.TEMPLATE_NAME,Format(T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE,T1.ACQUISITION_PROCESS_TYPE,"+
				 " (select LE_BOOK + '-' + LEB_DESCRIPTION from le_book where country = ? and le_book = ? ) as SHARE_HOLDER , ISNULL(FILING_STATUS, -1) FILING_STATUS,"+
                 " TRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(T1.EXCEL_FILE_PATTERN,'#COUNTRY#',T1.COUNTRY),'#LE_BOOK#',T1.LE_BOOK),'#PROCESS_FREQ#',T1.FREQUENCY_PROCESS),'#BUSINESS_DATE#',Format(T1.Business_Date,'dd-MMM-yyyy')),'#TEMPLATE_NAME#',T1.TEMPLATE_NAME)) EXCEL_FILE_PATTERN,"+
                 " T1.INTEGRITY_SCRIPT_TYPE , T1.ACQUISITION_STATUS, Format(T1.BUSINESS_DATE, 'RRRRMM') YEAR_MONTH,"+
				 " T3.DEFAULT_ACQ_PROCESS_TYPE,T3.Feed_Stg_Name, T1.FILE_PATTERN, T1.ACQUISITION_READINESS_SCRIPTS, T3.FEED_CATEGORY,"+
                 " T1.PREACTIVITY_SCRIPT_TYPE,T1.PREACTIVITY_SCRIPTS,T1.INTEGRITY_SCRIPT_TYPE,T1.INTEGRITY_SCRIPT_NAME,T1.FREQUENCY_PROCESS,T1.READINESS_SCRIPTS_TYPE,T1.ACQUISITION_READINESS_SCRIPTS"+
                 " FROM ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T2,TEMPLATE_NAMES T3 where  "+
                 " T1.TEMPLATE_NAME = T3. TEMPLATE_NAME"+
                 " AND T1.COUNTRY = T2.COUNTRY "+
                 " AND T1.LE_BOOK = T2.LE_BOOK "+
                 " AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME "+
                 " AND T1.COUNTRY = ? and T1.LE_BOOK = ? and T1.BUSINESS_DATE = CONVERT(datetime, ?, 103) and T1.TEMPLATE_NAME = ? ORDER BY COUNTRY, LE_BOOK, TEMPLATE_NAME ");
		}else {
			strQueryAppr = new String("select T1.COUNTRY, T1.LE_BOOK ,T1.TEMPLATE_NAME,to_char(T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE,T1.ACQUISITION_PROCESS_TYPE,"+
					 " (select LE_BOOK || '-' || LEB_DESCRIPTION from le_book where country = ? and le_book = ? ) as SHARE_HOLDER , NVL(FILING_STATUS, -1) FILING_STATUS,"+
	                 " TRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(T1.EXCEL_FILE_PATTERN,'#COUNTRY#',T1.COUNTRY),'#LE_BOOK#',T1.LE_BOOK),'#PROCESS_FREQ#',T1.FREQUENCY_PROCESS),'#BUSINESS_DATE#',to_char(T1.Business_Date,'dd-Mon-yyyy')),'#TEMPLATE_NAME#',T1.TEMPLATE_NAME)) EXCEL_FILE_PATTERN,"+
	                 " T1.INTEGRITY_SCRIPT_TYPE , T1.ACQUISITION_STATUS, to_char(T1.BUSINESS_DATE, 'RRRRMM') YEAR_MONTH,"+
					 " T3.DEFAULT_ACQ_PROCESS_TYPE,T3.Feed_Stg_Name, T1.FILE_PATTERN, T1.ACQUISITION_READINESS_SCRIPTS, T3.FEED_CATEGORY,"+
	                 " T1.PREACTIVITY_SCRIPT_TYPE,T1.PREACTIVITY_SCRIPTS,T1.INTEGRITY_SCRIPT_TYPE,T1.INTEGRITY_SCRIPT_NAME,T1.FREQUENCY_PROCESS,T1.READINESS_SCRIPTS_TYPE,T1.ACQUISITION_READINESS_SCRIPTS"+
	                 " FROM ADF_PROCESS_CONTROL T1, ADF_UPLOAD_FILING T2,TEMPLATE_NAMES T3 where  "+
	                 " T1.TEMPLATE_NAME = T3. TEMPLATE_NAME"+
	                 " AND T1.COUNTRY = T2.COUNTRY "+
	                 " AND T1.LE_BOOK = T2.LE_BOOK "+
	                 " AND T1.TEMPLATE_NAME = T2.TEMPLATE_NAME "+
	                 " AND T1.COUNTRY = ? and T1.LE_BOOK = ? and T1.BUSINESS_DATE = to_date(? ,'dd-Mon-yyyy') and T1.TEMPLATE_NAME = ? ORDER BY COUNTRY, LE_BOOK, TEMPLATE_NAME ");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = trimmedLeBook;
		objParams[2] = dObj.getCountry();
		objParams[3] = trimmedLeBook;
		objParams[4] = dObj.getBusinessDate();
		objParams[5] = dObj.getTemplateName();
		try
		{
			logger.info("Executing approved query");
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UploadFilingVb uploadFilingVb = new UploadFilingVb();
					uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
					uploadFilingVb.setCountry(rs.getString("COUNTRY"));
					uploadFilingVb.setLeBook(rs.getString("LE_BOOK"));
					uploadFilingVb.setShareHolder(rs.getString("SHARE_HOLDER"));
					uploadFilingVb.setFilingStatus(rs.getInt("FILING_STATUS"));
					uploadFilingVb.setFileName(rs.getString("EXCEL_FILE_PATTERN"));
//					uploadFilingVb.setUploadDate(rs.getString("UPLOAD_DATE"));
					uploadFilingVb.setIntegrityScriptType(rs.getString("INTEGRITY_SCRIPT_TYPE"));
					uploadFilingVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
					uploadFilingVb.setYearMonth(rs.getString("YEAR_MONTH"));
					uploadFilingVb.setDefaultAcqProcessType(rs.getString("DEFAULT_ACQ_PROCESS_TYPE"));
					uploadFilingVb.setFilePattern(rs.getString("FILE_PATTERN"));
					uploadFilingVb.setAcquisitionReadinessScripts(rs.getString("ACQUISITION_READINESS_SCRIPTS"));
					uploadFilingVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
					uploadFilingVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
					uploadFilingVb.setPreActivityScriptType(rs.getString("PREACTIVITY_SCRIPT_TYPE"));
					uploadFilingVb.setPreActivityScript(rs.getString("PREACTIVITY_SCRIPTS"));
					uploadFilingVb.setIntegrityScriptType(rs.getString("INTEGRITY_SCRIPT_TYPE"));
					uploadFilingVb.setIntegrityScriptName(rs.getString("INTEGRITY_SCRIPT_NAME"));
					uploadFilingVb.setFrequencyProcess(rs.getString("FREQUENCY_PROCESS"));
					uploadFilingVb.setAcquisitionReadinessScripts(rs.getString("ACQUISITION_READINESS_SCRIPTS"));
					uploadFilingVb.setFeedStgName(rs.getString("Feed_Stg_Name"));
					uploadFilingVb.setFeedCategory(rs.getString("FEED_CATEGORY"));
					return uploadFilingVb;
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
	public List<TemplateStgVb> getTemplateStgData(UploadFilingVb uploadFilingVb) {
		setServiceDefaults();
		int intKeyFieldsCount = 2;
		String query = new String("SELECT T1.REPORT_ID,T1.SESSION_ID,T1.TEMPLATE_NAME, "+
                 " T1.ROW_ID,T1.COL_ID,T1.CELL_DATA,T1.COL_TYPE,T1.SORT_FIELD,   "+
                 " T1.CREATE_NEW ,T2.XBRL_TAG, T2.XBRL_TAXONOMY,T2.UNIT_REF  "+
                 " FROM TEMPLATES_STG T1,XBRL_CONFIGURATION T2 "+
                 " WHERE T1.REPORT_ID = ?  "+
                 " AND T1.SESSION_ID= ? "+
                 " AND T1.REPORT_ID = T2.TEMPLATE_NAME "+
                 " AND T1.COL_ID = T2.COL_ID "+
                 " AND T1.CELL_DATA IS NOT NULL "+
                 " ORDER BY Tab_ID, Row_Id,Col_Id,Sort_Field");
				
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = uploadFilingVb.getTemplateName();
		params[1] = uploadFilingVb.getSessionId();
		try{
			return getJdbcTemplate().query(query, params, getTemplateStgMapper());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			return null;
		}
	}
	private RowMapper getTemplateStgMapper(){
		RowMapper mapper = new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateStgVb templateStgVb = new TemplateStgVb();
				templateStgVb.setReportId(rs.getString("REPORT_ID"));
				templateStgVb.setSessionId(rs.getString("SESSION_ID"));
				templateStgVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				templateStgVb.setRowId(rs.getString("ROW_ID"));
				templateStgVb.setColId(rs.getString("COL_ID"));
				templateStgVb.setCellData(rs.getString("CELL_DATA"));
				templateStgVb.setColType(rs.getString("COL_TYPE"));
				templateStgVb.setSortField(rs.getString("SORT_FIELD"));
				templateStgVb.setCreateNew(rs.getString("CREATE_NEW"));
				templateStgVb.setTagId(rs.getString("XBRL_TAG"));
				templateStgVb.setUnitRef(rs.getString("UNIT_REF"));
				templateStgVb.setContextPrefix(rs.getString("XBRL_TAXONOMY"));
				return templateStgVb;
			}
		};
		return mapper;
	}

	protected RowMapper getUploadFillingMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UploadFilingVb uploadFilingVb = new UploadFilingVb();
				uploadFilingVb.setCountry(rs.getString("COUNTRY"));
				uploadFilingVb.setLeBook(rs.getString("LE_BOOK"));
				uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				uploadFilingVb.setMakerName(rs.getString("TEMPLATE_NAME_DESC"));
				uploadFilingVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				uploadFilingVb.setProcessFrequency(rs.getString("FREQUENCY_PROCESS"));
				if(ValidationUtil.isValid(rs.getString("MAJOR_BUILD")))
				uploadFilingVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				uploadFilingVb.setShareHolder(rs.getString("SHARE_HOLDER"));
				if(ValidationUtil.isValid(rs.getString("EXCEL_FILE_PATTERN"))){
					uploadFilingVb.setFileName(rs.getString("EXCEL_FILE_PATTERN"));
				}else{
					uploadFilingVb.setFileName(rs.getString("EXCEL_FILE_PATTERN"));
				}
				uploadFilingVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
				uploadFilingVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
				uploadFilingVb.setScheduleStatus(rs.getString("ADF_SCHEDULE_STATUS"));
				uploadFilingVb.setUploadDate(rs.getString("FEED_DATE"));
				uploadFilingVb.setAdfNumber(rs.getString("ADF_NUMBER"));
				uploadFilingVb.setEodInitiatedFlag(rs.getString("EOD_INITIATED_FLAG"));
				uploadFilingVb.setFileNode(rs.getString("NODE"));
				uploadFilingVb.setVerifierName(rs.getString("ADF_SCHE_STATUS"));
				uploadFilingVb.setUFProcessedFlag(rs.getString("PROCESSED_FLAG"));
				
				//uploadFilingVb.setFeedStgName(rs.getString("Feed_Stg_Name"));
				//uploadFilingVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE")); 
				
				
				
				
				return uploadFilingVb;
			}
		};
		return mapper;
	}
	@Transactional(rollbackForClassName={"java.lang.Exception"})
	public synchronized int insetAuditTrialData(UploadFilingVb vObject, String description, String detailDesc){
		long maxSeq = getMaxSequence(vObject);
		int intValue = insetAuditTrailAdfProcessData(maxSeq+1, description, detailDesc,vObject);
		return intValue;
	}
	
	private int insetAuditTrailAdfProcessData(long auditTrailSequenceId, String description, String detailDesc,UploadFilingVb vObject){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into ADF_AUDIT_TRAIL (COUNTRY, LE_BOOK, BUSINESS_DATE, FEED_DATE, TEMPLATE_NAME, AUDIT_TRAIL_SEQUENCE_ID, "+
				" DATETIME_STAMP, AUDIT_DESCRIPTION, AUDIT_DESCRIPTION_DETAIL, TYPE_OF_ALERT_AT, TYPE_OF_ALERT, "+
				" ALERT_TYPE_NT, ALERT_TYPE, ACTION_STATUS_NT, ACTION_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
				" Values ('"+vObject.getCountry()+"', '"+trimmedLeBook+"', to_date('"+vObject.getBusinessDate()+"', 'dd-MMM-yyyy'), "
				+ " '"+vObject.getTemplateName()+"', '"+auditTrailSequenceId+"', GetDate(), "
				+ "'"+description+"', '"+detailDesc+"', 1075, 'NA', "
				+ "1007, 9999, 1008, 9999, GetDate(), GetDate() ) ";
		}else {
			query = "Insert Into ADF_AUDIT_TRAIL (COUNTRY, LE_BOOK, BUSINESS_DATE, FEED_DATE, TEMPLATE_NAME, AUDIT_TRAIL_SEQUENCE_ID, "+
					" DATETIME_STAMP, AUDIT_DESCRIPTION, AUDIT_DESCRIPTION_DETAIL, TYPE_OF_ALERT_AT, TYPE_OF_ALERT, "+
					" ALERT_TYPE_NT, ALERT_TYPE, ACTION_STATUS_NT, ACTION_STATUS, DATE_LAST_MODIFIED, DATE_CREATION)"+
					" Values ('"+vObject.getCountry()+"', '"+trimmedLeBook+"', to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy'), "
					+ " '"+vObject.getTemplateName()+"', '"+auditTrailSequenceId+"', sysdate, "
					+ "'"+description+"', '"+detailDesc+"', 1075, 'NA', "
					+ "1007, 9999, 1008, 9999, sysdate, sysdate ) "; 
		}
		try {
			getJdbcTemplate().execute(query);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	@SuppressWarnings("deprecation")
	private long getMaxSequence(UploadFilingVb vObject){
		long count = 1000;
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String strQuery = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQuery = new String("SELECT ISNULL(MAX(AUDIT_TRAIL_SEQUENCE_ID), 1000) MAX_COUNT FROM ADF_AUDIT_TRAIL WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
				+ "AND BUSINESS_DATE = TO_DATE('"+vObject.getBusinessDate()+"', 'dd-MMM-yyyy') "
				+ "AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ");
		}else {
			strQuery = new String("SELECT NVL(MAX(AUDIT_TRAIL_SEQUENCE_ID), 1000) MAX_COUNT FROM ADF_AUDIT_TRAIL WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
					+ "AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy') "
					+ "AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ");
		}
		try
		{
			return getJdbcTemplate().queryForObject(strQuery, long.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return count;
		}
	}
	@Transactional(rollbackForClassName={"java.lang.Exception"})
	public int updateStausOfEachProcess(UploadFilingVb vObject,String columnName, String value){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "UPDATE ADF_PROCESS_CONTROL set "+columnName+" = '"+value+"' WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
				+ "AND BUSINESS_DATE = CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103) AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ";
		}else{
			query = "UPDATE ADF_PROCESS_CONTROL set "+columnName+" = '"+value+"' WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
					+ "AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy') AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ";
		}
		try{
			if("N".equalsIgnoreCase(value)){
				if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = "UPDATE ADF_PROCESS_CONTROL set "+columnName+" = '"+value+"', NEXT_PROCESS_TIME = "
						+ "(SELECT NEXT_PROCESS_TIME+((SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE =  'ADF_IDLE_TIME') /60)/1440 "
						+ "FROM ADF_PROCESS_CONTROL WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
						+ "AND BUSINESS_DATE = CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103)  AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"')  "
						+ "WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' AND BUSINESS_DATE = CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103)  "
						+ "AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ";
				}else {
					query = "UPDATE ADF_PROCESS_CONTROL set "+columnName+" = '"+value+"', NEXT_PROCESS_TIME = "
							+ "(SELECT NEXT_PROCESS_TIME+((SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE =  'ADF_IDLE_TIME') /60)/1440 "
							+ "FROM ADF_PROCESS_CONTROL WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
							+ "AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy') AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"')  "
							+ "WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy') "
							+ "AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ";
				}
				////System.out.println("query : "+query);
				getJdbcTemplate().execute(query);
				return Constants.SUCCESSFUL_OPERATION;
			}else{
				////System.out.println("query : "+query);
				getJdbcTemplate().execute(query);
				return Constants.SUCCESSFUL_OPERATION;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	@Transactional(rollbackForClassName={"java.lang.Exception"})
	public int updateStausOfUploadFiling(UploadFilingVb vObject,String columnName, String value){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "UPDATE ADF_UPLOAD_FILING set "+columnName+" = '"+value+"' WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
				+ "AND BUSINESS_DATE =  CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103)AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ";
		}else {
			query = "UPDATE ADF_UPLOAD_FILING set "+columnName+" = '"+value+"' WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
					+ "AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy') AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ";
		}
		try{
				getJdbcTemplate().execute(query);
				return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	@Transactional(rollbackForClassName={"java.lang.Exception"})
	public int updateStausOfVersionTable(UploadFilingVb vObject, String columnName, String columnValue){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "UPDATE ACQ_VERSION_NO set "+columnName+" = CONVERT(datetime, '"+columnValue+"', 103) WHERE COUNTRY = ? AND LE_BOOK = ? AND "
				+ "BUSINESS_DATE = CONVERT(datetime, ?, 103) AND FEED_DATE = CONVERT(datetime, ?, 103) AND TEMPLATE_NAME = ? AND VERSION_NO = ?  ";
		}else {
			query = "UPDATE ACQ_VERSION_NO set "+columnName+" = to_date('"+columnValue+"', 'DD-MON-RRRR HH24:MI:SS')  WHERE COUNTRY = ? AND LE_BOOK = ? AND "
					+ "BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND FEED_DATE = to_date( ?, 'dd-Mon-yyyy') AND TEMPLATE_NAME = ? AND VERSION_NO = ?  ";
		}
		Object args[] = {vObject.getCountry(), trimmedLeBook, vObject.getBusinessDate(), vObject.getFeedDate(), vObject.getTemplateName(), vObject.getVersionNo()};
		logger.info("Update Query : "+query+ " Arguemnts : "+vObject.getCountry()+", "+trimmedLeBook+", "+vObject.getBusinessDate()+", "+vObject.getFeedDate()+", "+vObject.getTemplateName()+", "+vObject.getVersionNo());
		retVal = getJdbcTemplate().update(query,args);
		return retVal;
	}
	public String getSysteDateForAdfProcess(){
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) 
			return getJdbcTemplate().queryForObject("SELECT Format(GetDate(),'dd-MMM-yyy hh:MM:ss') SYSTEM_DAT  ",String.class);
		else
			return getJdbcTemplate().queryForObject("SELECT to_date(sysdate,'dd-Mon-yyy HH:mi:ss') SYSTEM_DAT  ",String.class);
	}
	public String getBusinessDate(String country, String leBook) throws DataAccessException {
		String sql = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) 
			sql = "SELECT Format(BUSINESS_DATE,'dd-MMM-yyyy') SYSTEM_DAT FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
		else
			sql = "SELECT to_char(BUSINESS_DATE,'dd-Mon-yyyy') SYSTEM_DAT FROM VISION_BUSINESS_DAY WHERE COUNTRY = ? AND LE_BOOK = ? ";
		Object[] lParams = new Object[2];
		String trimmedLeBook=removeDescLeBook(leBook);
		lParams[0] = country;
		lParams[1] = trimmedLeBook.toUpperCase();
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("SYSTEM_DAT"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if(commonVbs != null && !commonVbs.isEmpty()){
			return commonVbs.get(0).getMakerName();
		}
		return "";
	}
	public ArrayList<String> getCountOfErrorsInDBP(UploadFilingVb vObject){
		ArrayList<String> commonVbs = new ArrayList<String>();
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook()).toUpperCase();
		String strQuery = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQuery = new String("SELECT COUNT(1) ERROR_COUNT, MIN(CASE WHEN T2.ERROR_TYPE IN (1,3) THEN 'W' WHEN T2.ERROR_TYPE IN (2) THEN 'S' ELSE 'N' END) ERROR_TYPE "
				+ "FROM VISION_DBP_ERRORS T1, ERROR_CODES T2 WHERE T1.ERROR_CODE = T2.ERROR_CODE AND COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
				+ "AND BUSINESS_DATE = CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103)  "
				+ "AND FEED_DATE =  CONVERT(datetime, '"+vObject.getFeedDate()+"', 103)   AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ");
		}else {
			strQuery = new String("SELECT COUNT(1) ERROR_COUNT, MIN(CASE WHEN T2.ERROR_TYPE IN (1,3) THEN 'W' WHEN T2.ERROR_TYPE IN (2) THEN 'S' ELSE 'N' END) ERROR_TYPE "
					+ "FROM VISION_DBP_ERRORS T1, ERROR_CODES T2 WHERE T1.ERROR_CODE = T2.ERROR_CODE AND COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook+"' "
					+ "AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy') "
					+ "AND FEED_DATE = to_date('"+vObject.getFeedDate()+"', 'dd-Mon-yyyy') AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' ");
		}
		return getJdbcTemplate().query(strQuery,new ResultSetExtractor<ArrayList<String>>(){
			public ArrayList<String> extractData(ResultSet rs) throws SQLException ,org.springframework.dao.DataAccessException {
				ArrayList<String> commonVbs = new ArrayList<String>();
				while(rs.next()) {
					commonVbs.add(rs.getString("ERROR_COUNT"));
					commonVbs.add(rs.getString("ERROR_TYPE"));
				}
				return commonVbs;
			}
		});
	}
	
	public String replaceMacroValues(UploadFilingVb vObject,String adfAdFilePath,String adfLogPath, String adfFileUploadPath, String source){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook()).toUpperCase();
		String procedureValue=source;
		String dbPostFix = "";
		String dbPreFix = "";
		String parentBuild = "";
		String majorBuild = "";
		String logPath = "";
		if(procedureValue.indexOf("#COUNTRY#") > 0){
				procedureValue = procedureValue.replaceAll("#COUNTRY#",vObject.getCountry());
		}
		if(procedureValue.contains("#VIEW_NAME#")){
			procedureValue = procedureValue.replaceAll("#VIEW_NAME#", "BNR_"+vObject.getTemplateName());
		}	
		if(procedureValue.contains("#SUFFIX#")){
				procedureValue = procedureValue.replaceAll("#SUFFIX#", dbPostFix);
		}
		if(procedureValue.contains("#PREFIX#")){
			procedureValue = procedureValue.replaceAll("#PREFIX#", dbPreFix);
	}
		if(procedureValue.indexOf("#LE_BOOK#") > 0){
			procedureValue = procedureValue.replaceAll("#LE_BOOK#",trimmedLeBook);
		}
		if(procedureValue.indexOf("#BUSINESS_DATE#") > 0){
				procedureValue = procedureValue.replaceAll("#BUSINESS_DATE#",vObject.getBusinessDate());
		}
		if(procedureValue.indexOf("#YEAR_MONTH#") > 0){
				procedureValue = procedureValue.replaceAll("#YEAR_MONTH#",vObject.getYearMonth());
		}
		if(procedureValue.indexOf("#FEED_DATE#") > 0){
				procedureValue = procedureValue.replaceAll("#FEED_DATE#",vObject.getFeedDate());
		}
		if(procedureValue.indexOf("#FEED_CATEGORY#") > 0 ){
				procedureValue = procedureValue.replaceAll("#FEED_CATEGORY#",vObject.getFeedCategory());
		}
		if(procedureValue.indexOf("#PROGRAM#") > 0 ){
				procedureValue = procedureValue.replaceAll("#PROGRAM#",vObject.getProgram());
		}
		if(procedureValue.indexOf("#DEFAULT_ACQ_PROCESS_TYPE#") > 0 ){
				procedureValue = procedureValue.replaceAll("#DEFAULT_ACQ_PROCESS_TYPE#",vObject.getDefaultAcqProcessType());
		}
		if(procedureValue.indexOf("#VBD_CL#") > 0){
				procedureValue = procedureValue.replaceAll("#VBD_CL#",vObject.getFeedDate());
		}
		if(procedureValue.indexOf("#FEED_STG_NAME#") > 0){
				procedureValue = procedureValue.replaceAll("#FEED_STG_NAME#",vObject.getFeedStgName());
		}
		if(procedureValue.indexOf("#DATE_LAST_RUN#") > 0){
				procedureValue = procedureValue.replaceAll("#DATE_LAST_RUN#",vObject.getDateLastRun());
		}
		if(procedureValue.indexOf("#DATE_LAST_EXTRACTION#") > 0){
				procedureValue = procedureValue.replaceAll("#DATE_LAST_EXTRACTION#",vObject.getDateLastRun());
		}
		if(procedureValue.indexOf("#DEBUG_FLAG#") > 0){
				procedureValue = procedureValue.replaceAll("#DEBUG_FLAG#",vObject.getDebugMode());
		}
		if(procedureValue.indexOf("#DEBUG_MODE#") > 0){
				procedureValue = procedureValue.replaceAll("#DEBUG_MODE#",vObject.getDebugMode());
		}
		if(procedureValue.indexOf("#TEMPLATE_NAME#") > 0){
				procedureValue = procedureValue.replaceAll("#TEMPLATE_NAME#",vObject.getTemplateName());	
		}
		if(procedureValue.indexOf("#PARENT_BUILD#") > 0){
				procedureValue = procedureValue.replaceAll("#PARENT_BUILD#",parentBuild);	
		}
		if(procedureValue.indexOf("#MAJOR_BUILD#") > 0){
				procedureValue = procedureValue.replaceAll("#MAJOR_BUILD#",majorBuild);	
		}
		if(procedureValue.indexOf("#TABLE_NAME#") > 0){
				procedureValue = procedureValue.replaceAll("#TABLE_NAME#",vObject.getFeedStgName());	
		}
		if(procedureValue.indexOf("#LOGPATH#") > 0){
				procedureValue = procedureValue.replaceAll("#LOGPATH#",logPath);	
		}
		if(procedureValue.indexOf("#FREQUENCY#") > 0){
				procedureValue = procedureValue.replaceAll("#FREQUENCY#",vObject.getFrequencyProcess());	
		}
		return procedureValue;
	}
	public int updateExtractionErrorCount(UploadFilingVb vObject){
		String trimmedLeBook=removeDescLeBook(vObject.getLeBook()).toUpperCase();
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "UPDATE ADF_PROCESS_CONTROL SET  EXT_ITERATION_COUNT = (SELECT ISNULL(MAX(EXT_ITERATION_COUNT), 0)+1 FROM ADF_PROCESS_CONTROL "
				+ "WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+vObject.getLeBook()+"' AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' AND BUSINESS_DATE = CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103)  "
				+ "WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+vObject.getLeBook()+"' AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' AND BUSINESS_DATE = CONVERT(datetime, '"+vObject.getBusinessDate()+"', 103)";
		}else {
			query = "UPDATE ADF_PROCESS_CONTROL SET  EXT_ITERATION_COUNT = (SELECT NVL(MAX(EXT_ITERATION_COUNT), 0)+1 FROM ADF_PROCESS_CONTROL "
					+ "WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+vObject.getLeBook()+"' AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy'))  "
					+ "WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+vObject.getLeBook()+"' AND TEMPLATE_NAME = '"+vObject.getTemplateName()+"' AND BUSINESS_DATE = to_date('"+vObject.getBusinessDate()+"', 'dd-Mon-yyyy')";			
		}
		try{
			getJdbcTemplate().execute(query);
			return Constants.SUCCESSFUL_OPERATION;
		}catch(Exception e){
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	public List<String> callReadinessProc(int outParams, String procedure){
		List<String> outParamData = new ArrayList<String>(outParams);
		CallableStatement cs=null;
		Connection con = getConnection();
		try {
			cs = con.prepareCall("{call "+procedure+"}");
		
			for(int idx= 1;idx<=outParams; idx++){
				cs.registerOutParameter(idx, java.sql.Types.VARCHAR);
			}
			cs.execute();
			for(int idx= 1;idx<=outParams; idx++){
				outParamData.add(cs.getString(idx));
			}
		    cs.close();
		} catch (Exception e) {
			e.printStackTrace();
			strErrorDesc = e.getMessage().trim();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
		return outParamData;
	}
	public List<UploadFilingVb> getIntegrityErrors(UploadFilingVb dObj){
		List<UploadFilingVb> collTemp = null;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook()).toUpperCase();
		final int intKeyFieldsCount = 5;
		String strQueryAppr = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String("SELECT COUNT(1), LE_BOOK, BUSINESS_DATE, TEMPLATE_NAME,  T1.ERROR_DESCRIPTION FROM VISION_DBP_ERRORS T1, ERROR_CODES T2 "
				+ "WHERE T1.ERROR_CODE = T2.ERROR_CODE AND COUNTRY = ? "
				+ "AND LE_BOOK = ? AND BUSINESS_DATE = CONVERT(datetime, ?, 103) AND FEED_DATE = CONVERT(datetime, ?, 103)  AND TEMPLATE_NAME = ? "
				+ "group by LE_BOOK, BUSINESS_DATE, TEMPLATE_NAME,  T1.ERROR_DESCRIPTION " );
		}else {
			strQueryAppr = new String("SELECT COUNT(1), LE_BOOK, BUSINESS_DATE, TEMPLATE_NAME,  T1.ERROR_DESCRIPTION FROM VISION_DBP_ERRORS T1, ERROR_CODES T2 "
					+ "WHERE T1.ERROR_CODE = T2.ERROR_CODE AND COUNTRY = ? "
					+ "AND LE_BOOK = ? AND BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND FEED_DATE = to_date( ?, 'dd-Mon-yyyy')  AND TEMPLATE_NAME = ? "
					+ "group by LE_BOOK, BUSINESS_DATE, TEMPLATE_NAME,  T1.ERROR_DESCRIPTION " );
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = trimmedLeBook;
		objParams[2] = dObj.getBusinessDate();
		objParams[3] = dObj.getUploadDate();
		objParams[4] = dObj.getTemplateName();
		
		try
		{
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UploadFilingVb uploadFilingVb = new UploadFilingVb();
					uploadFilingVb.setCountry(rs.getString("REPORT_CATEGORY"));
					uploadFilingVb.setLeBook(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
					uploadFilingVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
					uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
					uploadFilingVb.setMakerName(rs.getString("ERROR_DESCRIPTION"));
					return uploadFilingVb;
				}
			};
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,mapper);
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
	
	public List<UploadFilingVb> getUploadErrors(UploadFilingVb dObj){
		List<UploadFilingVb> collTemp = null;
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook()).toUpperCase();
		final int intKeyFieldsCount = 5;
		String strQueryAppr = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new String("SELECT SEQUENCE_ID, ERROR_MSG FROM VISION_PREUPLOAD_ERRORS T1 "
				+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND BUSINESS_DATE = CONVERT(datetime, ?, 103) AND FEED_DATE = CONVERT(datetime, ?, 103)  AND TEMPLATE_NAME = ? "
				+ " ORDER by SEQUENCE_ID " );
		}else {
			strQueryAppr = new String("SELECT SEQUENCE_ID, ERROR_MSG FROM VISION_PREUPLOAD_ERRORS T1 "
					+ " WHERE COUNTRY = ? AND LE_BOOK = ? AND BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') AND FEED_DATE = to_date( ?, 'dd-Mon-yyyy')  AND TEMPLATE_NAME = ? "
					+ " ORDER by SEQUENCE_ID " );
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = trimmedLeBook.toUpperCase();
		objParams[2] = dObj.getBusinessDate();
		objParams[3] = dObj.getUploadDate();
		objParams[4] = dObj.getTemplateName();
		
		try
		{
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UploadFilingVb uploadFilingVb = new UploadFilingVb();
					uploadFilingVb.setUploadSequence(rs.getString("SEQUENCE_ID"));
					uploadFilingVb.setMakerName(rs.getString("ERROR_MSG"));
					return uploadFilingVb;
				}
			};
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,mapper);
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

	public List<ColumnHeadersVb> getColumnHeaders(String reportId, String sessionId) {
		setServiceDefaults();
		int intKeyFieldsCount = 2;
		String	query = new String("SELECT REPORT_ID, SESSION_ID, LABEL_ROW_NUM, LABEL_COL_NUM, CAPTION, COLUMN_WIDTH, COL_TYPE, ROW_SPAN, COL_SPAN, NUMERIC_COLUMN_NO " +
				"FROM COLUMN_HEADERS_STG WHERE REPORT_ID = ? AND SESSION_ID=? ORDER BY 3,4");
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = reportId;
		params[1] = sessionId;
		try{
			return getJdbcTemplate().query(query, params, getColumnHeadersMapper());
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			return null;
		}
	}
	private RowMapper getColumnHeadersMapper(){
		RowMapper mapper = new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ColumnHeadersVb columnHeadersVb = new ColumnHeadersVb();
				columnHeadersVb.setReportId(rs.getString("REPORT_ID"));
				columnHeadersVb.setSessionId(rs.getString("SESSION_ID"));
				columnHeadersVb.setLabelRowNum(rs.getInt("LABEL_ROW_NUM"));
				columnHeadersVb.setLabelColNum(rs.getInt("LABEL_COL_NUM"));
				columnHeadersVb.setCaption(rs.getString("CAPTION"));
				columnHeadersVb.setColumnWidth(rs.getString("COLUMN_WIDTH"));
				columnHeadersVb.setColType(rs.getString("COL_TYPE"));
				columnHeadersVb.setRowSpanNum(rs.getInt("ROW_SPAN"));
				columnHeadersVb.setNumericColumnNo(rs.getInt("NUMERIC_COLUMN_NO"));
				columnHeadersVb.setColSpanNum(rs.getInt("COL_SPAN"));
				
				return columnHeadersVb;
			}
		};
		return mapper;
	}

	public String getListReportDataForExportAsXMLString(PromptTreeVb promptTree, final List<ColumnHeadersVb> columnHeaders, long min, long max) {
		String query = "";
		try{
			query = "SELECT T1.* FROM "+promptTree.getTableName()+" T1 WHERE SORT_FIELD <="+max+" AND SORT_FIELD >"+min +" ORDER BY SORT_FIELD";
			System.out.println("Query :"+query);
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					StringBuffer lResult = new StringBuffer("");
					lResult.append("<tableData>");
				     while(rs.next()){
					    lResult.append("<tableRow>");
					    for(ColumnHeadersVb columnHeader:columnHeaders){
//					    	System.out.println("Col Name :"+columnHeader.getCaption());
				    	    String value = "";
					    	if(columnHeader.getCaption().contains(","))
							{
						        String[] parts = columnHeader.getCaption().split(",");
								value = rs.getString(parts[0]);
							    if(value == null) value="";
							    lResult.append("<").append(parts[1]).append(">").append(StringEscapeUtils.escapeXml(value)).append("</").append(parts[1]).append(">");
							}else{
								value = rs.getString(columnHeader.getCaption());
							    if(value == null) value="";
							    lResult.append("<").append(columnHeader.getCaption()).append(">").append(StringEscapeUtils.escapeXml(value)).append("</").append(columnHeader.getCaption()).append(">");
							}
						    	
						   /* String value = rs.getString(columnHeader.getCaption());
						    if(value == null) value="";
						    lResult.append("<").append(columnHeader.getCaption()).append(">").append(StringEscapeUtils.escapeXml(value)).append("</").append(columnHeader.getCaption()).append(">");*/
					    }
					    try{
					    	String HEADERTITLE_TEXTValue = rs.getString("HEADERTITLE_TEXT");
							if(HEADERTITLE_TEXTValue == null) HEADERTITLE_TEXTValue=""; 
							lResult.append("<").append("headerText").append(">").append(StringEscapeUtils.escapeXml(HEADERTITLE_TEXTValue)).append("</").append("headerText").append(">");
						
							String value = rs.getString("FORMAT_TYPE");
							if(value == null) value=""; 
							lResult.append("<").append("formatType").append(">").append(StringEscapeUtils.escapeXml(value)).append("</").append("formatType").append(">");
						}catch(Exception e){
							//Do nothing this is a format column
						}
					    lResult.append("</tableRow>");
				     }
				     lResult.append("</tableData>");
					return lResult.toString();
				}
			};
			return (String)getJdbcTemplate().query(query, mapper);
		}catch(BadSqlGrammarException ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			strErrorDesc = parseErrorMsg(new UncategorizedSQLException( "","", ex.getSQLException())); 
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			strErrorDesc = ex.getMessage(); 
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}
	}
	public PromptTreeVb callProcToPopulateListReportData(UploadFilingVb uploadFilingVb, String legalVehcile, int currentUserId) {
		setServiceDefaults();
		strCurrentOperation = "Query";
		strErrorDesc = "";
		Connection con = null;
		CallableStatement cs =  null;
		PromptTreeVb promptTreeVb = new PromptTreeVb();
		try{
			String scalingFactor = "1";
			String sessionId = "SC"+currentUserId;
			String trimmedLeBook=removeDescLeBook(uploadFilingVb.getLeBook());
			//For the dash boards the session id will be set by the caller.
			String legalVehicle = legalVehcile+"-"+uploadFilingVb.getCountry()+"-"+trimmedLeBook.toUpperCase();
			con = getConnection();
			StringBuffer templateName = new StringBuffer();
			if(uploadFilingVb.getExportData().indexOf(",")>0) {
				templateName.append("ALL");
			}else {
				String[] templateNames = uploadFilingVb.getExportData().split(",");
				int i=0;
				for (String str : templateNames) {
					templateName.append(str.split("-")[0].trim());
					if (i < templateNames.length-1) templateName.append(",");
					i++;
				}				
			}
				

			
			cs = con.prepareCall("{call PR_RS_ACQALERT02 (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cs.setString(1, String.valueOf(intCurrentUserId));//VisionId
	        cs.setString(2, sessionId);//Session Id
	        cs.setString(3, uploadFilingVb.getExportReportId());//Session Id
        	cs.setString(4, scalingFactor);//P_Scaling_Factor
        	cs.setString(5, legalVehicle);//P_Prompt_Value_1
        	cs.setString(6, uploadFilingVb.getBusinessDate());//P_Prompt_Value_2
        	cs.setString(7, uploadFilingVb.getFeedDate());//P_Prompt_Value_3
        	cs.setString(8, templateName.toString());//P_Prompt_Value_4
        	cs.setString(9, uploadFilingVb.getFrequencyProcess());//P_Prompt_Value_5
        	cs.setString(10, uploadFilingVb.getMajorBuild());//P_Prompt_Value_6
        	cs.setString(11, "");//P_Prompt_Value_7
        	cs.setString(12, "");//P_Prompt_Value_8
        	cs.registerOutParameter(13, java.sql.Types.VARCHAR); //Table Name
	        cs.registerOutParameter(14, java.sql.Types.VARCHAR); //Status 
	        /*P_Status = -1, if there is an error; P_ErrorMsg will contain the error string
				p_Status =  0, if procedure executes successfully. P_ErrorMsg will contain nothing in this case
				p_Status =  1, Procedure has fetched NO records for the given query criteria. P_ErrorMsg will contain nothing.*/
	        cs.registerOutParameter(15, java.sql.Types.VARCHAR); //Error Message
			cs.execute();
			promptTreeVb.setSessionId(sessionId);
			promptTreeVb.setReportId(uploadFilingVb.getExportReportId());
			promptTreeVb.setTableName(cs.getString(13));
            promptTreeVb.setStatus(cs.getString(14));
            promptTreeVb.setErrorMessage(cs.getString(15));
		    cs.close();
		    if("-1".equalsIgnoreCase(promptTreeVb.getStatus())){
		    	strErrorDesc = promptTreeVb.getErrorMessage();
				throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		    }
		}catch(Exception ex){
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
		return promptTreeVb;
	}
	public void callProcToCleanUpTables(UploadFilingVb vObj) {
		Connection con = null;
		CallableStatement cs =  null;
		try{
			con = getConnection();
			cs = con.prepareCall("{call PR_RS_CLEANUP(?, ?, ?, ?, ?)}");
	        cs.setString(1, String.valueOf(intCurrentUserId));//Report Id
	        cs.setString(2, "SC"+intCurrentUserId);//Group Report Id
	        cs.setString(3, vObj.getExportReportId());//Group Report Id
	        cs.registerOutParameter(4, java.sql.Types.VARCHAR);//Chart type list
	        cs.registerOutParameter(5, java.sql.Types.VARCHAR); //Status 
	        /*P_Status = -1, if there is an error; P_ErrorMsg will contain the error string
				p_Status =  0, if procedure executes successfully. P_ErrorMsg will contain nothing in this case
				p_Status =  1, Procedure has fetched NO records for the given query criteria. P_ErrorMsg will contain nothing.*/
			cs.execute();
			//Comenting the below code bcos we dont need the return data. 
			//vObj.setErrorStatus(cs.getString(4));
			//vObj.setErrorMessage(cs.getString(5));
		    cs.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
	}
	public String getLegalVehicle(UploadFilingVb vObj){
		String trimmedLeBook=removeDescLeBook(vObj.getLeBook());	
		
		return getJdbcTemplate().queryForObject("SELECT LEGAL_VEHICLE FROM LE_BOOK WHERE COUNTRY = '"+vObj.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook.toUpperCase()+"' ",String.class);
	}
	
	public String getLegalVehicleDesc(String legalVehicle){
		try {
			if ("MSSQL".equalsIgnoreCase(databaseType)) 
				return getJdbcTemplate().queryForObject("select LEGAL_VEHICLE+' - '+ lv_DESCRIPTION  from legal_vehicles where LEGAL_VEHICLE = '"+legalVehicle+"'  ",String.class);
			else
				return getJdbcTemplate().queryForObject("select LEGAL_VEHICLE||' - '|| lv_DESCRIPTION  from legal_vehicles where LEGAL_VEHICLE = '"+legalVehicle+"'  ",String.class);
		}catch(Exception e) {
			return "";
		}
	}
	public String getTemplateNameDesc(String legalVehicle){
		try {
			return getJdbcTemplate().queryForObject("SELECT GENERAL_DESCRIPTION FROM TEMPLATE_NAMES WHERE TEMPLATE_NAME = '"+legalVehicle+"'  ",String.class);
		}catch(Exception e) {
			return "";
		}
	}
	public String getMajorBuildDesc(String majorBuild){
		try {
			System.out.println("majorBuild : "+majorBuild);
			return getJdbcTemplate().queryForObject("SELECT program_description FROM PROGRAMS  WHERE PROGRAM = '"+majorBuild+"'  ",String.class);
		}catch(Exception e) {
			return "";
		}
	}
	public String getAlphaSubTabDescription(int alphaTab, String alphaSubTab){
		if ("MSSQL".equalsIgnoreCase(databaseType)) 
			return getJdbcTemplate().queryForObject("select ALPHA_SUB_TAB+' - '+ ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB where alpha_tab = "+alphaTab+" and ALPHA_SUB_TAB = '"+alphaSubTab+"' ",String.class);
		else
			return getJdbcTemplate().queryForObject("select ALPHA_SUB_TAB||' - '|| ALPHA_SUBTAB_DESCRIPTION from ALPHA_SUB_TAB where alpha_tab = "+alphaTab+" and ALPHA_SUB_TAB = '"+alphaSubTab+"' ",String.class);
	}
	public  synchronized long  getMaxVersionNo(String fileName){
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType))
			strBufApprove = new StringBuffer("Select ISNULL(MAX(CURR_VERSION_NO), 0) FROM ADF_UPLOAD_FILING WHERE FILE_NAME = '"+fileName+"'");
		else
			strBufApprove = new StringBuffer("Select NVL(MAX(CURR_VERSION_NO), 0) FROM ADF_UPLOAD_FILING WHERE FILE_NAME = '"+fileName+"'");
		try{
			return getJdbcTemplate().queryForObject(strBufApprove.toString(), long.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return 1;
		}
	}
	public int doUpdateVersionNo(String fileName, long versionNO){
		String query = "Update ADF_UPLOAD_FILING SET CURR_VERSION_NO = ? WHERE FILE_NAME = ? AND INTERNAL_STATUS = 0 ";
		Object[] args = {versionNO+1, fileName};
		int retVal = getJdbcTemplate().update(query,args);
		if(retVal == 0){
			query = "SELECT COUNT(1) FROM ADF_UPLOAD_FILING WHERE FILE_NAME = '"+fileName+"' AND INTERNAL_STATUS = 1 ";
			int recordCount = getJdbcTemplate().queryForObject(query, int.class);
			if(recordCount>0){
				query = "Update ADF_UPLOAD_FILING SET INTERNAL_STATUS = 0 WHERE FILE_NAME = '"+fileName+"' AND INTERNAL_STATUS = 1 ";
				retVal = getJdbcTemplate().update(query);
			}
		}
		return retVal;
	}	
	public String getListReportDataForExportAsXMLStringHeader(PromptTreeVb promptTree, final List<ColumnHeadersVb> columnHeaders, long min, long max) {
		String query = "";
		try{
			query = "SELECT T1.* FROM "+promptTree.getTableName()+" T1 WHERE SORT_FIELD < 1 ORDER BY SORT_FIELD";
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs)  throws SQLException, DataAccessException {
					String lResult="";
				     while(rs.next()){
							String value = rs.getString("FORMAT_TYPE");
							if("FHT".equalsIgnoreCase(value)){
								return lResult="FHT";
							}
				     }
					return lResult;
				}
			};
			return (String)getJdbcTemplate().query(query, mapper);
		}catch(BadSqlGrammarException ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			strErrorDesc = parseErrorMsg(new UncategorizedSQLException( "","", ex.getSQLException())); 
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(((query==null)? "query is Null":query));
			strErrorDesc = ex.getMessage(); 
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}
	}
	public String getXBRLFileName(UploadFilingVb vObj){
		String trimmedLeBook=removeDescLeBook(vObj.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType))
			query = "SELECT FILE_PATTERN FROM ADF_PROCESS_CONTROL WHERE COUNTRY = '"+vObj.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook.toUpperCase()+"' "
					+ "AND CONVERT(datetime, '"+vObj.getBusinessDate()+"', 103) AND TEMPLATE_NAME = '"+vObj.getTemplateName()+"' ";
		else
			query = "SELECT FILE_PATTERN FROM ADF_PROCESS_CONTROL WHERE COUNTRY = '"+vObj.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook.toUpperCase()+"' "
					+ "AND BUSINESS_DATE=to_date('"+vObj.getBusinessDate()+"','dd-Mon-yyyy') AND TEMPLATE_NAME = '"+vObj.getTemplateName()+"' ";
		
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("FILE_PATTERN"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(query, mapper);
		if(commonVbs != null && !commonVbs.isEmpty()){
			return commonVbs.get(0).getMakerName();
		}
		return "";
	}	
	public List<UploadFilingVb> getProcessFrequency(UploadFilingVb uploadFilingVb){
		List<UploadFilingVb> collTemp = null;
//		String trimLeBook=removeDescLeBook(uploadFilingVb.getLeBook());
//		String trimLeBook = uploadFilingVb.getLeBook();
		String leBook = uploadFilingVb.getLeBook();
//		int ocunt = StringUtils.countMatches(leBook, "-");
		int occurance = StringUtils.countOccurrencesOf(leBook, "-");
		String trimmedLeBook = " ";
		if(occurance == 1){
			if(uploadFilingVb.getLeBook().indexOf("- ") > 0){
				trimmedLeBook = removeDescLeBook(uploadFilingVb.getLeBook());
				trimmedLeBook = "'"+uploadFilingVb.getCountry()+"-"+trimmedLeBook+"'";
			}else{
				trimmedLeBook = uploadFilingVb.getLeBook();	
			}
		}else{
			trimmedLeBook = uploadFilingVb.getLeBook();
		}		
		StringBuffer strQueryAppr = new StringBuffer("SELECT DISTINCT t3.ALPHA_SUBTAB_DESCRIPTION,t1.FREQUENCY_PROCESS "
				+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2,ALPHA_SUB_TAB t3 "
				+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+uploadFilingVb.getCountry()+"' AND t1.LE_BOOK IN ("+trimmedLeBook+") "
				        + "AND t3.ALPHA_TAB = t1.FREQUENCY_PROCESS_AT "
						+ "AND t3.ALPHA_SUB_TAB = t1.FREQUENCY_PROCESS "
						+ "AND t1.ACQUISITION_PROCESS_TYPE = 'XLS'");
		try
		{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getFrequencyProcessMapper());
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	public RowMapper getFrequencyProcessMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
					vObject.setAlphaSubTab(rs.getString("FREQUENCY_PROCESS"));
				 	vObject.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}
	public String getNodeRequestByAdfNumber(String adfNumber){
		String sql = "SELECT CASE WHEN NODE_OVERRIDE IS  NULL THEN NODE_REQUEST ELSE NODE_OVERRIDE END NODE FROM ADF_SCHEDULES WHERE ADF_NUMBER = "+adfNumber+" ";
		return getJdbcTemplate().queryForObject(sql, String.class);
	}
	public List<UploadFilingVb> getBusnessDate(UploadFilingVb uploadFilingVb){
		List<UploadFilingVb> collTemp = null;
		String trimLeBook=removeDescLeBook(uploadFilingVb.getLeBook());
		StringBuffer strQueryAppr = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strQueryAppr = new StringBuffer("SELECT DISTINCT Format(t1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE "
				+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2 "
				+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.ACQUISITION_PROCESS_TYPE = 'XLS' and t1.COUNTRY = '"+uploadFilingVb.getCountry()+"' and t1.LE_BOOK = '"+trimLeBook+"'"
				+ " AND T1.FREQUENCY_PROCESS='"+uploadFilingVb.getProcessFrequency()+"' AND T1.MAJOR_BUILD = '"+uploadFilingVb.getMajorBuild()+"'");
		}else {
			strQueryAppr = new StringBuffer("SELECT DISTINCT to_char(t1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE "
					+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2 "
					+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.ACQUISITION_PROCESS_TYPE = 'XLS' and t1.COUNTRY = '"+uploadFilingVb.getCountry()+"' and t1.LE_BOOK = '"+trimLeBook+"'"
					+ " AND T1.FREQUENCY_PROCESS='"+uploadFilingVb.getProcessFrequency()+"' AND T1.MAJOR_BUILD = '"+uploadFilingVb.getMajorBuild()+"'");
		}
		try
		{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getReportCategoryMapper());
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	protected RowMapper getReportCategoryMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				vObject.setAlphaSubTab(rs.getString("BUSINESS_DATE"));
				vObject.setDescription(rs.getString("BUSINESS_DATE"));
				return vObject;
			}
		};
		return mapper;
	}
	public RowMapper getQueryPopupMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UploadFilingVb uploadFilingVb = new UploadFilingVb();
				uploadFilingVb.setCountry(rs.getString("COUNTRY"));
				uploadFilingVb.setLeBook(rs.getString("LE_BOOK"));
				uploadFilingVb.setShareHolder(rs.getString("SHARE_HOLDER"));
				uploadFilingVb.setProcessFrequency(rs.getString("FREQUENCY_PROCESS"));
				uploadFilingVb.setDescription(rs.getString("FREQUENCY_PROCESS_DESC"));
				uploadFilingVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				uploadFilingVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				uploadFilingVb.setAdfNumber(rs.getString("ADF_NUMBER"));
				return uploadFilingVb;
			}
		};
		return mapper;
	}
	public List<UploadFilingVb> getQueryPopupResults(UploadFilingVb dObj){
		Vector<Object> params = new Vector<Object>();
		String trimmedLeBook=removeDescLeBook(dObj.getLeBook());
		dObj.setVerificationRequired(false);
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("SELECT DISTINCT  T1.COUNTRY,T1.LE_BOOK, "
				+ "T1.LE_BOOK+ '-' +(Select LEB.Leb_description from LE_Book LEB Where LEB.Country = T1.Country And LEB.LE_Book = T1.LE_Book) as SHARE_HOLDER,"
				+ "(SELECT AST.Alpha_Subtab_Description FROM Alpha_Sub_Tab AST where AST.alpha_tab=T1.Frequency_Process_AT And AST.Alpha_Sub_Tab = T1.Frequency_Process) as Frequency_Process_Desc,T1.Frequency_Process,"
				+ " T2.MAJOR_BUILD, Format(T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, T1.ADF_NUMBER FROM "
				+ " ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 "
				+ "where T1.ADF_NUMBER = T2.ADF_NUMBER and  T2.ACQUISITION_PROCESS_TYPE = 'XLS' ");
		}else {
			strBufApprove = new StringBuffer("SELECT DISTINCT  T1.COUNTRY,T1.LE_BOOK, "
					+ "T1.LE_BOOK || '-' || (Select LEB.Leb_description from LE_Book LEB Where LEB.Country = T1.Country And LEB.LE_Book = T1.LE_Book) as SHARE_HOLDER,"
					+ "(SELECT AST.Alpha_Subtab_Description FROM Alpha_Sub_Tab AST where AST.alpha_tab=T1.Frequency_Process_AT And AST.Alpha_Sub_Tab = T1.Frequency_Process) as Frequency_Process_Desc,T1.Frequency_Process,"
					+ " T2.MAJOR_BUILD, to_char(T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, T1.ADF_NUMBER FROM "
					+ " ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2 "
					+ "where T1.ADF_NUMBER = T2.ADF_NUMBER and  T2.ACQUISITION_PROCESS_TYPE = 'XLS' ");
		}
		try
		{
			/*//String queryString="";
			if (ValidationUtil.isValid(dObj.getLegalVehicle()))
			{
				params.addElement(dObj.getLegalVehicle());
				strBufApprove.append(" AND T3.COUNTRY = T1.COUNTRY ");
				strBufApprove.append(" AND T3.LE_BOOK = T1.LE_BOOK ");
				strBufApprove.append(" AND T1.COUNTRY LIKE '"+dObj.getCountry()+"'  AND T3.LEGAL_VEHICLE IN(SELECT LEGAL_VEHICLE FROM LEGAL_VEHICLES ");
				strBufApprove.append(" START WITH UPPER(LEGAL_VEHICLE) = ?  CONNECT BY PRIOR LEGAL_VEHICLE = PARENT_LV  AND LEGAL_VEHICLE != PARENT_LV)");
				//CommonUtils.addToQuery("UPPER(T1.LE_BOOK) like ?", strBufApprove);
			}*/
			if (ValidationUtil.isValid(trimmedLeBook))
			{
				params.addElement("%" + trimmedLeBook.toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T1.LE_BOOK) like ?");
				//CommonUtils.addToQuery("UPPER(T1.LE_BOOK) like ?", strBufApprove);
			}
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append("AND T1.COUNTRY+ '-' +T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ");
				else 
					strBufApprove.append("AND T1.COUNTRY|| '-' ||T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ");
			}
			if (ValidationUtil.isValid(dObj.getProcessFrequency()) && !"-1".equalsIgnoreCase(dObj.getProcessFrequency()))
			{
				params.addElement("%" + dObj.getProcessFrequency().toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T1.FREQUENCY_PROCESS) like ?");
				//CommonUtils.addToQuery("UPPER(T1.FREQUENCY_PROCESS) like ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getMajorBuild()) && !"-1".equalsIgnoreCase(dObj.getMajorBuild()))
			{
				params.addElement("%" + dObj.getMajorBuild().toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T2.MAJOR_BUILD) like ?");
				//CommonUtils.addToQuery("UPPER(T1.FREQUENCY_PROCESS) like ?", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate()) && !"-1".equalsIgnoreCase(dObj.getBusinessDate()))
			{
				params.addElement(dObj.getBusinessDate());
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND UPPER(T1.BUSINESS_DATE) = CONVERT(datetime, ?, 103)");
				else
					strBufApprove.append(" AND UPPER(T1.BUSINESS_DATE) = to_date( ?, 'dd-Mon-yyyy')");
				//CommonUtils.addToQuery("T1.BUSINESS_DATE = Format(?, 'dd-MMM-yyyy')", strBufApprove);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error(((strBufApprove==null)? "strBufApprove is null":strBufApprove.toString()));

			if (params != null)
				for(int i=0 ; i< params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}

		String orderBy = " Order By COUNTRY, LE_BOOK, BUSINESS_DATE";
		return getQueryPopupResults(dObj,new StringBuffer(), strBufApprove, new String(), orderBy, params, getQueryPopupMapper());
	}
	
	public int getTemplateDependencyCheckCount(UploadFilingVb dObj) {
		try {
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			String templateName = "";

			templateName = (dObj.getTemplateName().replaceAll(",", "','"));
			String sql = "";
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				sql = "select count(1) from adf_process_Control t1 "
					+ " Where T1.TEMPLATE_NAME in (Select AD.Dependent_Template from ADF_DEPENDENCIES AD "
					+ " Where Template_name in ('" + templateName + "') ) "
					+ " And LTRIM(RTRIM(T1.ACQUISITION_STATUS)) NOT IN ('COMP','COMPNSERR','COMPSERR','INTCOMP','INTIP','INTERR','INTRESUB') "
					+ " And T1.COUNTRY = '" + dObj.getCountry() + "'  And T1.LE_BOOK = '" + CalLeBook.trim() + "' "
					+ " And Format(t1.BUSINESS_DATE, 'dd-MMM-yyyy') = '" + dObj.getBusinessDate() + "'  "
					+ " And T1.Template_Name not in ('" + templateName + "') ";
			}else {
				sql = "select count(1) from adf_process_Control t1 "
						+ " Where T1.TEMPLATE_NAME in (Select AD.Dependent_Template from ADF_DEPENDENCIES AD "
						+ " Where Template_name in ('" + templateName + "') ) "
						+ " And LTRIM(RTRIM(T1.ACQUISITION_STATUS)) NOT IN ('COMP','COMPNSERR','COMPSERR','INTCOMP','INTIP','INTERR','INTRESUB') "
						+ " And T1.COUNTRY = '" + dObj.getCountry() + "'  And T1.LE_BOOK = '" + CalLeBook.trim() + "' "
						+ " And to_date(t1.BUSINESS_DATE, 'dd-Mon-yyyy') = '" + dObj.getBusinessDate() + "'  "
						+ " And T1.Template_Name not in ('" + templateName + "') ";
			}
			int i = getJdbcTemplate().queryForObject(sql, int.class);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	@SuppressWarnings("unchecked")
	public List<UploadFilingVb> getCommentDrilldownResults(UploadFilingVb vObj, String tableName) throws DataAccessException {
		String sql = "Select * from "+tableName+" order by SORT_FIELD";
		return getJdbcTemplate().query(sql, getCommentDrilldownMapper());
	}
	 
	 protected RowMapper getCommentDrilldownMapper(){
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UploadFilingVb uploadFilingVb = new UploadFilingVb();
					uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
					uploadFilingVb.setRecordCount(rs.getString("RECORDS_COUNT"));
					uploadFilingVb.setScheduleStatus(rs.getString("STATUS"));
					uploadFilingVb.setComment(rs.getString("COMMENTS"));
					uploadFilingVb.setTimeElapsed(rs.getString("TIME_ELAPSED"));
					return uploadFilingVb;
				}
			};
			return mapper;
		}
		public List<UploadFilingVb> getMajorBuild(UploadFilingVb uploadFilingVb){
			List<UploadFilingVb> collTemp = null;
//			String trimLeBook=removeDescLeBook(uploadFilingVb.getLeBook());
//			String trimLeBook = uploadFilingVb.getLeBook();
			String leBook = uploadFilingVb.getLeBook();
//			int ocunt = StringUtils.countMatches(leBook, "-");
			int occurance = StringUtils.countOccurrencesOf(leBook, "-");
			String trimmedLeBook = " ";
			if(occurance == 1){
				if(uploadFilingVb.getLeBook().indexOf("- ") > 0){
					trimmedLeBook = removeDescLeBook(uploadFilingVb.getLeBook());
					trimmedLeBook = "'"+uploadFilingVb.getCountry()+"-"+trimmedLeBook+"'";
				}else{
					trimmedLeBook = uploadFilingVb.getLeBook();	
				}
			}else{
				trimmedLeBook = uploadFilingVb.getLeBook();
			}		
			StringBuffer strQueryAppr = new StringBuffer("SELECT DISTINCT T1.MAJOR_BUILD "
					+" FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2,ALPHA_SUB_TAB t3 "
					+" WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+uploadFilingVb.getCountry()+"' AND t1.LE_BOOK IN ("+trimmedLeBook+")"
					+" AND t3.ALPHA_TAB = t1.FREQUENCY_PROCESS_AT "
					+" AND t3.ALPHA_SUB_TAB = t1.FREQUENCY_PROCESS " 
					+" AND t1.ACQUISITION_PROCESS_TYPE = 'XLS' "
					+" AND T1.Frequency_Process = '"+uploadFilingVb.getProcessFrequency()+"'");
			try
			{
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getMajorBuildMapper());
				return collTemp;
			}
			catch(Exception ex){
				ex.printStackTrace();
				logger.error("Error: getQueryResultsForReview Exception :   ");
					logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
				return null;
			}
		}
		public RowMapper getMajorBuildMapper(){
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					AlphaSubTabVb vObject = new AlphaSubTabVb();
					vObject.setAlphaSubTab(rs.getString("MAJOR_BUILD"));
				 	vObject.setDescription(rs.getString("MAJOR_BUILD"));
					return vObject;
				}
			};
			return mapper;
		}
		public ExceptionCode getPanelInfo1(UploadFilingVb dObj) {
			ExceptionCode exceptionCode = new ExceptionCode();
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			Vector<Object> params = new Vector<Object>();
			try {
				/*StringBuffer strQueryAppr = new StringBuffer("SELECT DISTINCT T1.country,                                "+                                             
						"                 T1.le_book,                                                                    "+
						"                 T1.le_book + ' - '                                                             "+
						"                 + (SELECT LEB.leb_description                                                  "+
						"                    FROM   le_book LEB                                                          "+
						"                    WHERE  LEB.country = T1.country                                             "+
						"                           AND LEB.le_book = T1.le_book)                 AS                     "+
						"                 STAKEHOLDER, T1.Frequency_Process,                                                                 "+
						"                 (SELECT AST.alpha_subtab_description                                           "+
						"                  FROM   alpha_sub_tab AST                                                      "+
						"                  WHERE  AST.alpha_tab = T1.frequency_process_at                                "+
						"                         AND AST.alpha_sub_tab = T1.frequency_process)   AS                     "+
						"                 Frequency_Process_Desc,                                                        "+
						"                 Format(T1.business_date, 'dd-MMM-yyyy') BUSINESS_DATE,                         "+
						"                 CASE WHEN GETDATE() > DATEADD(MINUTE,DATEDIFF(MINUTE,'1900-01-01 00:00:00',    "+
						" ISNULL(T3.NONADF_START_TIME,T4.NONADF_START_TIME)),T1.BUSINESS_DATE) THEN 'DUE' else 'LIVE' END  "+
						"                 SCHEDULE_START_TIME                                                            "+
						" FROM   adf_process_control T1,                                                                 "+
						"        adf_schedules T2,                                                                       "+
						" 	   ADF_ACQUISITION_CONTROL t3,                                                               "+
						" 	   ADF_ACQUISITION_TIMESLOT T4,                                                              "+
						" 	   LE_BOOK T5                                                                                "+
						" WHERE  T1.adf_number = T2.adf_number                                                           "+
						"        AND T1.COUNTRY = T3.COUNTRY                                                             "+
						" 	   AND T1.LE_BOOK = T3.LE_BOOK                                                               "+
						" 	   AND T1.FREQUENCY_PROCESS = T3.FREQUENCY_PROCESS                                           "+
						" 	   AND T1.COUNTRY = T5.COUNTRY                                                               "+
						" 	   AND T1.LE_BOOK = T5.LE_BOOK                                                               "+
						" 	   AND T4.CATEGORY_TYPE = T5.CATEGORY_TYPE                                                   "+
						" 	   AND T1.FREQUENCY_PROCESS = T4.FREQUENCY_PROCESS                                           "+
						" 	   AND T2.acquisition_process_type = 'XLS'  ");
*/				
				StringBuffer strQueryAppr = new StringBuffer();
				if ("MSSQL".equalsIgnoreCase(databaseType)) {
					strQueryAppr = new StringBuffer("SELECT T1.COUNTRY, "+
					"        T1.LE_BOOK, "+
					"        T1.LE_BOOK + ' - ' + LEB.LEB_DESCRIPTION AS STAKEHOLDER, "+
					"        T1.FREQUENCY_PROCESS, "+
					"        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
					"           FROM ALPHA_SUB_TAB AST "+
					"          WHERE     AST.ALPHA_TAB = T1.FREQUENCY_PROCESS_AT "+
					"                AND AST.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) AS FREQUENCY_PROCESS_DESC, "+
					"        Format (T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, "+
					"        case when min(T1.EOD_INITIATED_FLAG) = 'Y' Then 'LIVE' else 'DUE' End SCHEDULE_START_TIME,"
					+ "CASE WHEN T1.FREQUENCY_PROCESS = 'DLY' THEN FORMAT (T1.BUSINESS_DATE, 'dd-MMM-yyyy') " + 
					"            WHEN T1.FREQUENCY_PROCESS = 'MTH' THEN FORMAT (T1.BUSINESS_DATE, 'MMM-yyyy') " + 
					"            WHEN T1.FREQUENCY_PROCESS = 'QTR' THEN FORMAT (T1.BUSINESS_DATE, 'MMM-yyyy') " + 
					"            WHEN T1.FREQUENCY_PROCESS = 'ANN' THEN FORMAT (T1.BUSINESS_DATE, 'yyyy') " + 
					"       ELSE FORMAT (T1.BUSINESS_DATE, 'dd-MMM-yyyy') END DISPLAY_BUSINESS_DATE " + 
					"   FROM ADF_PROCESS_CONTROL T1, "+
					"        ADF_SCHEDULES T2, "+
					"        LE_BOOK LEB "+
					" WHERE     T1.ADF_NUMBER = T2.ADF_NUMBER "+
					"       AND T1.COUNTRY = LEB.COUNTRY "+
					"       AND T1.LE_BOOK = LEB.LE_BOOK "+
					"       AND T2.ACQUISITION_PROCESS_TYPE = 'XLS'  ");
				}else {
					strQueryAppr = new StringBuffer("SELECT T1.COUNTRY, "+
							"        T1.LE_BOOK, "+
							"        T1.LE_BOOK || ' - ' || LEB.LEB_DESCRIPTION AS STAKEHOLDER, "+
							"        T1.FREQUENCY_PROCESS, "+
							"        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
							"           FROM ALPHA_SUB_TAB AST "+
							"          WHERE     AST.ALPHA_TAB = T1.FREQUENCY_PROCESS_AT "+
							"                AND AST.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) AS FREQUENCY_PROCESS_DESC, "+
							"        to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, "+
							"        case when min(T1.EOD_INITIATED_FLAG) = 'Y' Then 'LIVE' else 'DUE' End SCHEDULE_START_TIME,"
							+ "CASE WHEN T1.FREQUENCY_PROCESS = 'DLY' THEN to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') " + 
							"            WHEN T1.FREQUENCY_PROCESS = 'MTH' THEN To_char (T1.BUSINESS_DATE, 'Mon-yyyy') " + 
							"            WHEN T1.FREQUENCY_PROCESS = 'QTR' THEN to_char (T1.BUSINESS_DATE, 'Mon-yyyy') " + 
							"            WHEN T1.FREQUENCY_PROCESS = 'ANN' THEN to_char (T1.BUSINESS_DATE, 'yyyy') " + 
							"       ELSE to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') END DISPLAY_BUSINESS_DATE " + 
							"   FROM ADF_PROCESS_CONTROL T1, "+
							"        ADF_SCHEDULES T2, "+
							"        LE_BOOK LEB "+
							" WHERE     T1.ADF_NUMBER = T2.ADF_NUMBER "+
							"       AND T1.COUNTRY = LEB.COUNTRY "+
							"       AND T1.LE_BOOK = LEB.LE_BOOK "+
							"       AND T2.ACQUISITION_PROCESS_TYPE = 'XLS'  ");
				}
				if (ValidationUtil.isValid(dObj.getCountry())){
					params.addElement(dObj.getCountry().toUpperCase());
					strQueryAppr.append(" AND T1.COUNTRY = '"+dObj.getCountry().toUpperCase()+"' ");
				}
				if(ValidationUtil.isValid(dObj.getLeBook())) {
					if (ValidationUtil.isValid(CalLeBook) && !"-".equalsIgnoreCase(CalLeBook)){
						params.addElement(CalLeBook.trim());
						strQueryAppr.append(" AND T1.LE_BOOK = '"+CalLeBook.trim()+"' ");
					}
				}
				if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())) {
					if ("MSSQL".equalsIgnoreCase(databaseType))
						strQueryAppr.append("and T1.COUNTRY+ '-' +T1.LE_BOOK IN ( "+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
					else
						strQueryAppr.append("and T1.COUNTRY|| '-' ||T1.LE_BOOK IN ( "+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
				}
				String groupOrderBy = "";
				if ("MSSQL".equalsIgnoreCase(databaseType)) {
				groupOrderBy = " GROUP BY "+
						"        T1.COUNTRY, "+
						"        T1.LE_BOOK, "+
						"        T1.LE_BOOK + ' - ' + LEB.LEB_DESCRIPTION, "+
						"        T1.FREQUENCY_PROCESS_AT, "+
						"        T1.FREQUENCY_PROCESS, "+
						"        T1.BUSINESS_DATE, "
						+ "CASE WHEN T1.FREQUENCY_PROCESS = 'DLY' THEN FORMAT (T1.BUSINESS_DATE, 'dd-MMM-yyyy') " + 
						"            WHEN T1.FREQUENCY_PROCESS = 'MTH' THEN FORMAT (T1.BUSINESS_DATE, 'MMM-yyyy') " + 
						"            WHEN T1.FREQUENCY_PROCESS = 'QTR' THEN FORMAT (T1.BUSINESS_DATE, 'MMM-yyyy') " + 
						"            WHEN T1.FREQUENCY_PROCESS = 'ANN' THEN FORMAT (T1.BUSINESS_DATE, 'yyyy') " + 
						"       ELSE FORMAT (T1.BUSINESS_DATE, 'dd-MMM-yyyy') END " + 					
						" Order By  "+
						"        T1.COUNTRY, "+
						"        T1.LE_BOOK, "+
						"        T1.FREQUENCY_PROCESS, "+
						"        T1.BUSINESS_DATE ";
				}else {
					groupOrderBy = " GROUP BY "+
							"        T1.COUNTRY, "+
							"        T1.LE_BOOK, "+
							"        T1.LE_BOOK || ' - ' || LEB.LEB_DESCRIPTION, "+
							"        T1.FREQUENCY_PROCESS_AT, "+
							"        T1.FREQUENCY_PROCESS, "+
							"        T1.BUSINESS_DATE, "
							+ "CASE WHEN T1.FREQUENCY_PROCESS = 'DLY' THEN to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') " + 
							"            WHEN T1.FREQUENCY_PROCESS = 'MTH' THEN to_char (T1.BUSINESS_DATE, 'Mon-yyyy') " + 
							"            WHEN T1.FREQUENCY_PROCESS = 'QTR' THEN to_char (T1.BUSINESS_DATE, 'Mon-yyyy') " + 
							"            WHEN T1.FREQUENCY_PROCESS = 'ANN' THEN to_char (T1.BUSINESS_DATE, 'yyyy') " + 
							"       ELSE to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') END " + 					
							" Order By  "+
							"        T1.COUNTRY, "+
							"        T1.LE_BOOK, "+
							"        T1.FREQUENCY_PROCESS, "+
							"        T1.BUSINESS_DATE ";
				}
				strQueryAppr.append(groupOrderBy);
				if(SessionContextHolder.getContext().getVisionId() == 1232 || SessionContextHolder.getContext().getVisionId() == 1084) {
					System.out.println("Pannel 1 : "+strQueryAppr.toString());
				}
				exceptionCode = commonApiDao.getCommonResultDataQuery(strQueryAppr.toString());
			}catch(Exception ex) {
				ex.printStackTrace();
				logger.error("Error: Exception in getting the Acq - E Upload Filing Panel1 results : ");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(ex.getMessage());
			}
			return exceptionCode;
		}
		
		public ExceptionCode getPanelInfo2(UploadFilingVb dObj) {
			ExceptionCode exceptionCode = new ExceptionCode();
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			Vector<Object> params = new Vector<Object>();
			try {
				/*StringBuffer strQueryAppr = new StringBuffer("SELECT DISTINCT T1.country,                                "+                                             
						"                 T1.le_book,                                                                    "+
						"                 T1.le_book + ' - '                                                             "+
						"                 + (SELECT LEB.leb_description                                                  "+
						"                    FROM   le_book LEB                                                          "+
						"                    WHERE  LEB.country = T1.country                                             "+
						"                           AND LEB.le_book = T1.le_book)                 AS                     "+
						"                 STAKEHOLDER, T1.Frequency_Process, T2.major_build,   t6.program_description major_build_desc ,                                                             "+
						"                 (SELECT AST.alpha_subtab_description                                           "+
						"                  FROM   alpha_sub_tab AST                                                      "+
						"                  WHERE  AST.alpha_tab = T1.frequency_process_at                                "+
						"                         AND AST.alpha_sub_tab = T1.frequency_process)   AS                     "+
						"                 Frequency_Process_Desc,  "+                                                        
						"  (SELECT AST.alpha_subtab_description "+
						"  FROM   alpha_sub_tab AST "+
						"  WHERE  AST.alpha_tab = T2.adf_schedule_status_at "+
						"  AND AST.alpha_sub_tab = T2.adf_schedule_status) "+
						"  ADF_SCHEDULE_STATUS, "+
						"  Format(T1.business_date, 'dd-MMM-yyyy') "+
						"  BUSINESS_DATE, "+
						"  DATEADD(MINUTE,DATEDIFF(MINUTE,'1900-01-01 00:00:00',ISNULL(T3.NONADF_START_TIME,T4.NONADF_START_TIME)),T1.BUSINESS_DATE) "+
						"  SCHEDULE_START_TIME, "+
						"  DATEADD(MINUTE,DATEDIFF(MINUTE,'1900-01-01 00:00:00',ISNULL(T3.NONADF_END_TIME, T4.NONADF_END_TIME)),T1.BUSINESS_DATE) "+
						"  SCHEDULE_END_TIME                                                      "+
						"  FORMAT(DATEADD(MINUTE,DATEDIFF(MINUTE,'1900-01-01 00:00:00',ISNULL(T3.NONADF_START_TIME,T4.NONADF_START_TIME)),T1.BUSINESS_DATE),'dd-MMM-yyyy HH:mm:ss')  "+
						"  SCHEDULE_START_TIME, "+
						"  FORMAT(DATEADD(MINUTE,DATEDIFF(MINUTE,'1900-01-01 00:00:00',ISNULL(T3.NONADF_END_TIME, T4.NONADF_END_TIME)),T1.BUSINESS_DATE),'dd-MMM-yyyy HH:mm:ss')  "+
						"  SCHEDULE_END_TIME                                                      "+
						" FROM   adf_process_control T1,                                                                 "+
						"        adf_schedules T2,                                                                       "+
						" 	   ADF_ACQUISITION_CONTROL t3,                                                               "+
						" 	   ADF_ACQUISITION_TIMESLOT T4,                                                              "+
						" 	   LE_BOOK T5  , programs T6                                                                 "+
						" WHERE  T1.adf_number = T2.adf_number                                                           "+
						"        AND T1.COUNTRY = T3.COUNTRY                                                             "+
						" 	   AND T1.LE_BOOK = T3.LE_BOOK                                                               "+
						" 	   AND T1.FREQUENCY_PROCESS = T3.FREQUENCY_PROCESS                                           "+
						" 	   AND T1.COUNTRY = T5.COUNTRY                                                               "+
						" 	   AND T1.LE_BOOK = T5.LE_BOOK                                                               "+
						" 	   AND T4.CATEGORY_TYPE = T5.CATEGORY_TYPE                                                   "+
						" 	   AND T1.FREQUENCY_PROCESS = T4.FREQUENCY_PROCESS                                           "+
						" 	   AND T2.acquisition_process_type = 'XLS' "
						+ " AND  T2.MAJOR_BUILD = T6.PROGRAM ");
*/				
				StringBuffer strQueryAppr = new StringBuffer();
				if ("MSSQL".equalsIgnoreCase(databaseType)) {
					strQueryAppr = new StringBuffer(" SELECT  "+
					"        T1.COUNTRY, "+
					"        T1.LE_BOOK, "+
					"        T1.LE_BOOK + ' - ' + LEB.LEB_DESCRIPTION AS STAKEHOLDER, "+
					"        T2.MAJOR_BUILD,  "+
					"        T1.FREQUENCY_PROCESS, "+
					"        T2.MAJOR_BUILD, "+
					"        T6.PROGRAM_DESCRIPTION MAJOR_BUILD_DESC, "+
					"        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
					"           FROM ALPHA_SUB_TAB AST "+
					"          WHERE     AST.ALPHA_TAB = T1.FREQUENCY_PROCESS_AT "+
					"                AND AST.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) "+
					"           AS FREQUENCY_PROCESS_DESC, "+
					"        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
					"           FROM ALPHA_SUB_TAB AST "+
					"          WHERE     AST.ALPHA_TAB = T2.ADF_SCHEDULE_STATUS_AT "+
					"                AND AST.ALPHA_SUB_TAB = T2.ADF_SCHEDULE_STATUS) "+
					"           ADF_SCHEDULE_STATUS, "+
					"        FORMAT (T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, "+
					"        MIN(T1.START_TIME) SCHEDULE_START_TIME, "+
					" 	   MAX(T1.END_TIME) SCHEDULE_END_TIME, "
					+ "sum(case when T1.ACQUISITION_STATUS = 'YTP' OR T1.ACQUISITION_STATUS like '%RESUB' then 1 else 0 end) PENDING, " + 
					"	   sum(case when T1.ACQUISITION_STATUS in ('COMPNSERR','COMP') then 1 else 0 end) COMPLETED, " + 
	//				"	   sum(case when T1.ACQUISITION_STATUS != 'COMPNSERR' And (T1.ACQUISITION_STATUS in ('COMPSERR') OR T1.ACQUISITION_STATUS like '%ERR') then 1 else 0 end) ERRORED, "
					" SUM(case when T1.ACQUISITION_STATUS != 'COMPNSERR' And (T1.ACQUISITION_STATUS like '%KILL' OR T1.ACQUISITION_STATUS like '%ERR') then 1 else 0 end) ERRORED, " + 
					" SUM(case when T1.ACQUISITION_STATUS not in ('YTP','COMP','COMPNSERR','COMPSERR') " + 
					"	                 And T1.ACQUISITION_STATUS not like '%RESUB' " + 
					"					 And T1.ACQUISITION_STATUS not like '%ERR'"
					+ "	And T1.ACQUISITION_STATUS not like '%KILL' then 1 else 0 end) INPROGRESS " + 
					"   FROM ADF_PROCESS_CONTROL T1, "+
					"        ADF_SCHEDULES T2, "+
					"        LE_BOOK LEB, "+
					"        PROGRAMS T6 "+
					"  WHERE     T1.ADF_NUMBER = T2.ADF_NUMBER "+
					"        AND T1.COUNTRY = LEB.COUNTRY "+
					"        AND T1.LE_BOOK = LEB.LE_BOOK "+
					"        AND T2.MAJOR_BUILD = T6.PROGRAM  "+
					"        AND T2.ACQUISITION_PROCESS_TYPE = 'XLS' ");
				}else {
					strQueryAppr = new StringBuffer(" SELECT  "+
							"        T1.COUNTRY, "+
							"        T1.LE_BOOK, "+
							"        T1.LE_BOOK || ' - ' || LEB.LEB_DESCRIPTION AS STAKEHOLDER, "+
							"        T2.MAJOR_BUILD,  "+
							"        T1.FREQUENCY_PROCESS, "+
							"        T2.MAJOR_BUILD, "+
							"        T6.PROGRAM_DESCRIPTION MAJOR_BUILD_DESC, "+
							"        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
							"           FROM ALPHA_SUB_TAB AST "+
							"          WHERE     AST.ALPHA_TAB = T1.FREQUENCY_PROCESS_AT "+
							"                AND AST.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) "+
							"           AS FREQUENCY_PROCESS_DESC, "+
							"        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
							"           FROM ALPHA_SUB_TAB AST "+
							"          WHERE     AST.ALPHA_TAB = T2.ADF_SCHEDULE_STATUS_AT "+
							"                AND AST.ALPHA_SUB_TAB = T2.ADF_SCHEDULE_STATUS) "+
							"           ADF_SCHEDULE_STATUS, "+
							"        to_char (T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, "+
							"        MIN(T1.START_TIME) SCHEDULE_START_TIME, "+
							" 	   MAX(T1.END_TIME) SCHEDULE_END_TIME, "
							+ "sum(case when T1.ACQUISITION_STATUS = 'YTP' OR T1.ACQUISITION_STATUS like '%RESUB' then 1 else 0 end) PENDING, " + 
							"	   sum(case when T1.ACQUISITION_STATUS in ('COMPNSERR','COMP') then 1 else 0 end) COMPLETED, " + 
//							"	   sum(case when T1.ACQUISITION_STATUS != 'COMPNSERR' And (T1.ACQUISITION_STATUS in ('COMPSERR') OR T1.ACQUISITION_STATUS like '%ERR') then 1 else 0 end) ERRORED, "
							" SUM(case when T1.ACQUISITION_STATUS != 'COMPNSERR' And (T1.ACQUISITION_STATUS like '%KILL' OR T1.ACQUISITION_STATUS like '%ERR') then 1 else 0 end) ERRORED, " + 
							" SUM(case when T1.ACQUISITION_STATUS not in ('YTP','COMP','COMPNSERR','COMPSERR') " + 
							"	                 And T1.ACQUISITION_STATUS not like '%RESUB' " + 
							"					 And T1.ACQUISITION_STATUS not like '%ERR'"
							+ "	And T1.ACQUISITION_STATUS not like '%KILL' then 1 else 0 end) INPROGRESS " + 
							"   FROM ADF_PROCESS_CONTROL T1, "+
							"        ADF_SCHEDULES T2, "+
							"        LE_BOOK LEB, "+
							"        PROGRAMS T6 "+
							"  WHERE     T1.ADF_NUMBER = T2.ADF_NUMBER "+
							"        AND T1.COUNTRY = LEB.COUNTRY "+
							"        AND T1.LE_BOOK = LEB.LE_BOOK "+
							"        AND T2.MAJOR_BUILD = T6.PROGRAM  "+
							"        AND T2.ACQUISITION_PROCESS_TYPE = 'XLS' ");
				}
				if (ValidationUtil.isValid(dObj.getCountry())){
					params.addElement(dObj.getCountry().toUpperCase());
					strQueryAppr.append(" AND T1.COUNTRY = '"+dObj.getCountry().toUpperCase()+"' ");
				}
				if (ValidationUtil.isValid(CalLeBook)){
					params.addElement(CalLeBook.trim());
					strQueryAppr.append(" AND T1.LE_BOOK = '"+CalLeBook.trim()+"' ");
				}
				if (ValidationUtil.isValid(dObj.getFrequencyProcess())){
					params.addElement(dObj.getFrequencyProcess());
					strQueryAppr.append(" AND T1.FREQUENCY_PROCESS = '"+dObj.getFrequencyProcess()+"' ");
				}
				if (ValidationUtil.isValid(dObj.getBusinessDate()))	{
			    	params.addElement(dObj.getBusinessDate());
			    	if ("MSSQL".equalsIgnoreCase(databaseType))
			    		strQueryAppr.append(" AND T1.BUSINESS_DATE = CONVERT(date, '"+dObj.getBusinessDate()+"', 103) ");
			    	else
			    		strQueryAppr.append(" AND T1.BUSINESS_DATE = to_date('"+dObj.getBusinessDate()+"', 'dd-Mon-yyyy')  ");
				}
				if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())) {
					if ("MSSQL".equalsIgnoreCase(databaseType))
						strQueryAppr.append("and T1.COUNTRY+ '-' +T1.LE_BOOK IN ( "+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
					else
						strQueryAppr.append("and T1.COUNTRY|| '-' ||T1.LE_BOOK IN ( "+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
				}
				String orderGroupBy = "";
				if ("MSSQL".equalsIgnoreCase(databaseType)) {
					orderGroupBy = " GROUP BY  "+
						"        T1.COUNTRY, "+
						"        T1.LE_BOOK, "+
						"        T1.LE_BOOK + ' - ' + LEB.LEB_DESCRIPTION, "+
						"        T1.FREQUENCY_PROCESS, "+
						"        T2.MAJOR_BUILD, "+
						"        T1.FREQUENCY_PROCESS_AT, "+
						"        T1.FREQUENCY_PROCESS, "+
						"        T2.ADF_SCHEDULE_STATUS_AT, "+
						"        T2.ADF_SCHEDULE_STATUS, "+
						"        T1.BUSINESS_DATE,"
						+ "		 T6.PROGRAM_DESCRIPTION  "+
						" Order By " + 
						"	   CASE WHEN T2.MAJOR_BUILD = 'OTHERS' THEN 999 " + 
						"	   ELSE ROW_NUMBER() OVER (ORDER BY T1.COUNTRY,T1.LE_BOOK,T2.MAJOR_BUILD) END ";
				}else {
					orderGroupBy = " GROUP BY  "+
							"        T1.COUNTRY, "+
							"        T1.LE_BOOK, "+
							"        T1.LE_BOOK || ' - ' || LEB.LEB_DESCRIPTION, "+
							"        T1.FREQUENCY_PROCESS, "+
							"        T2.MAJOR_BUILD, "+
							"        T1.FREQUENCY_PROCESS_AT, "+
							"        T1.FREQUENCY_PROCESS, "+
							"        T2.ADF_SCHEDULE_STATUS_AT, "+
							"        T2.ADF_SCHEDULE_STATUS, "+
							"        T1.BUSINESS_DATE,"
							+ "		 T6.PROGRAM_DESCRIPTION  "+
							" Order By " + 
							"	   CASE WHEN T2.MAJOR_BUILD = 'OTHERS' THEN 999 " + 
							"	   ELSE ROW_NUMBER() OVER (ORDER BY T1.COUNTRY,T1.LE_BOOK,T2.MAJOR_BUILD) END ";
				}
				strQueryAppr.append(orderGroupBy);
				if(SessionContextHolder.getContext().getVisionId() == 1232 || SessionContextHolder.getContext().getVisionId() == 1084) {
					System.out.println("=============================================================");
					System.out.println("Pannel 2 : "+strQueryAppr.toString());
					System.out.println("=============================================================");
				}
				exceptionCode = commonApiDao.getCommonResultDataQuery(strQueryAppr.toString());
			}catch(Exception ex) {
				ex.printStackTrace();
				logger.error("Error: Exception in getting the Acq - E Upload Filing Panel2 results : ");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(ex.getMessage());
			}
			return exceptionCode;
		}
		
		public ExceptionCode getPanelInfo3(UploadFilingVb dObj) {
			ExceptionCode exceptionCode = new ExceptionCode();
			String CalLeBook = removeDescLeBook(dObj.getLeBook());
			Vector<Object> params = new Vector<Object>();
			try {
				
/*				StringBuffer strQueryAppr = new StringBuffer("SELECT T1.country," + 
						"        T1.le_book,		ISNULL (T2.NODE_OVERRIDE, T2.NODE_REQUEST) AS NODE, 													"+ 
						"        TAppr.file_name,   T2.ADF_SCHEDULE_STATUS,  Format(TAppr.UPLOAD_DATE, 'dd-MMM-yyyy') FEED_DATE ,                                                      "+
						"        T1.TEMPLATE_NAME+' - '+T3.GENERAL_DESCRIPTION TEMPLATE_NAME  ,    "
						+ "     ISNULL(TAppr.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN, T1.ACQUISITION_STATUS,      "
						+ " (SELECT AST.alpha_subtab_description FROM alpha_sub_tab AST WHERE     AST.alpha_tab = T1.ACQUISITION_STATUS_at "
						+ " AND AST.alpha_sub_tab = T1.ACQUISITION_STATUS) ACQUISITION_STATUS_DESC,           "+
						"        T1.excel_template_id,   T1.TEMPLATE_NAME TEMPLATE_NAME_DESC, t2.Adf_Number,         "+
						"        (SELECT le_book + '-' + leb_description                                    "+
						"         FROM   le_book                                                            "+
						"         WHERE  le_book = T1.le_book                                               "+
						"                AND country = T1.country)                      AS STAKEHOLDER,	    "+
						"        Format(T1.business_date, 'dd-MMM-yyyy')                                    "+
						"        BUSINESS_DATE,                                                             "+
						"        (SELECT AST.alpha_subtab_description                                       "+
						"         FROM   alpha_sub_tab AST                                                  "+
						"         WHERE  AST.alpha_tab = T2.adf_schedule_status_at                          "+
						"                AND AST.alpha_sub_tab = T2.adf_schedule_status)   adf_status_desc, "
						//"        T2.start_time       SCHEDULE_START_TIME,   "
						+ "  Format (T2.start_time, 'dd-MMM-yyyy HH:mm:ss') SCHEDULE_START_TIME, "
						+ "   Format (T2.end_time, 'dd-MMM-yyyy HH:mm:ss') SCHEDULE_END_TIME,     "+
					//	"        T2.end_time       SCHEDULE_END_TIME,             "+
						"       CONVERT (char (8),                                                          "+
						"          dateadd(second,Datediff (second, t2.start_time, t2.end_time),0),8)       "+
						"             duration                                  FROM                        "+
						"        adf_process_control T1 INNER JOIN                                          "+
						"        adf_schedules T2                                                           "+
						" 	   ON             T1.adf_number = T2.adf_number    "
						+ "INNER JOIN    TEMPLAtE_NAMES  T3  ON   T1.TEMPLATE_NAME =   T3.TEMPLATE_NAME     "+
						" 	   LEFT OUTER JOIN                                                              "+
						" 	   adf_upload_filing TAppr                                                      "+
						" 	   ON                                                                           "+
						" 	   (TAppr.country = T1.country                                                  "+
						"        AND TAppr.le_book = T1.le_book                                             "+
						"        AND TAppr.business_date = T1.business_date                                 "+
						"        AND TAppr.template_name = T1.template_name)      where                          ");*/
					
				StringBuffer strQueryAppr = new StringBuffer();
				if ("MSSQL".equalsIgnoreCase(databaseType)) {				
					strQueryAppr = new StringBuffer(" SELECT "+
						"  T1.COUNTRY, "+
						"  T1.LE_BOOK, "+
						"  ISNULL (T2.NODE_OVERRIDE, T2.NODE_REQUEST) AS NODE, "+
						"  TAPPR.FILE_NAME, "+
						"  T2.ADF_SCHEDULE_STATUS, "+
						"  T2.MAJOR_BUILD, "+
//						"  FORMAT(TAPPR.UPLOAD_DATE, 'dd-MMM-yyyy') FEED_DATE , "+
						"  FORMAT(T2.FEED_DATE, 'dd-MMM-yyyy') FEED_DATE , "+
						"  T1.TEMPLATE_NAME+' - '+T3.GENERAL_DESCRIPTION TEMPLATE_NAME  , "+
						"  ISNULL(TAPPR.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN,  "+
						"  T1.ACQUISITION_STATUS, "+
						"  (SELECT AST.ALPHA_SUBTAB_DESCRIPTION  "+
						"   FROM ALPHA_SUB_TAB AST  "+
						"   WHERE AST.ALPHA_TAB = T1.ACQUISITION_STATUS_AT "+
						"   AND AST.ALPHA_SUB_TAB = T1.ACQUISITION_STATUS) ACQUISITION_STATUS_DESC, "+
						"  T1.EXCEL_TEMPLATE_ID, "+
						"  T1.TEMPLATE_NAME TEMPLATE_NAME_DESC,  "+
						"  T2.ADF_NUMBER, "+
						"  T1.LE_BOOK + ' - ' + LEB.LEB_DESCRIPTION AS STAKEHOLDER, "+
						"  FORMAT(T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, "+
						"  (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
						"   FROM  ALPHA_SUB_TAB AST "+
						"   WHERE AST.ALPHA_TAB = T2.ADF_SCHEDULE_STATUS_AT "+
						"     AND AST.ALPHA_SUB_TAB = T2.ADF_SCHEDULE_STATUS)   ADF_STATUS_DESC, "+
						"  Format (V1.PROCESS_START_TIME, 'dd-MMM-yyyy HH:mm:ss') SCHEDULE_START_TIME, "+
						"  Format (isNull(V1.PROCESS_END_TIME,isNull(V1.INT_END_TIME,isNull(V1.UPL_END_TIME,isNull(V1.EXT_END_TIME,isNull(V1.PREEXT_END_TIME,V1.PROCESS_START_TIME))))), 'dd-MMM-yyyy HH:mm:ss') SCHEDULE_END_TIME, "+
						"  CONVERT(char(8), "+
						"  dateadd(second,DATEDIFF( "+
						" SECOND,V1.PROCESS_START_TIME, "+
						" isNull(V1.PROCESS_END_TIME,isNull(V1.INT_END_TIME,isNull(V1.UPL_END_TIME,isNull(V1.EXT_END_TIME,isNull(V1.PREEXT_END_TIME,V1.PROCESS_START_TIME)))))),0) "+
						"  ,8) DURATION "+
						" FROM ADF_PROCESS_CONTROL T1 "+
						" INNER JOIN  ADF_SCHEDULES T2 "+
						"   ON (T1.ADF_NUMBER = T2.ADF_NUMBER) "+
						" INNER JOIN TEMPLATE_NAMES T3 "+
						"   ON (T1.TEMPLATE_NAME = T3.TEMPLATE_NAME) "+
						" INNER JOIN LE_BOOK LEB "+
						"   ON (T1.COUNTRY = LEB.COUNTRY AND T1.LE_BOOK = LEB.LE_BOOK) "+
						" LEFT OUTER JOIN ADF_UPLOAD_FILING TAPPR "+
						"   ON (TAPPR.COUNTRY = T1.COUNTRY "+
						"   AND TAPPR.LE_BOOK = T1.LE_BOOK "+
						"   AND TAPPR.BUSINESS_DATE = T1.BUSINESS_DATE "+
						"   AND TAPPR.TEMPLATE_NAME = T1.TEMPLATE_NAME) "+
						" LEFT OUTER JOIN ACQ_VERSION_NO V1 "+
						"   ON (V1.COUNTRY = T1.COUNTRY "+
						"   AND V1.LE_BOOK = T1.LE_BOOK "+
						"   AND V1.BUSINESS_DATE = T1.BUSINESS_DATE "+
						"   AND V1.FEED_DATE = T2.FEED_DATE "+
						"   AND V1.TEMPLATE_NAME = T1.TEMPLATE_NAME "+
						"   AND V1.VERSION_NO = (SELECT MAX (V2.VERSION_NO) "+
						"                        FROM ACQ_VERSION_NO V2 "+
						"                        WHERE V1.COUNTRY = V2.COUNTRY "+
						"                          AND V1.LE_BOOK = V2.LE_BOOK "+
						"                          AND V1.BUSINESS_DATE = V2.BUSINESS_DATE "+
						"                          AND V1.FEED_DATE = V2.FEED_DATE "+
						"                          AND V1.TEMPLATE_NAME = V2.TEMPLATE_NAME)) "+
						" WHERE ");
				}else {
					strQueryAppr = new StringBuffer(" SELECT "+
							"  T1.COUNTRY, "+
							"  T1.LE_BOOK, "+
							"  NVL (T2.NODE_OVERRIDE, T2.NODE_REQUEST) AS NODE, "+
							"  TAPPR.FILE_NAME, "+
							"  T2.ADF_SCHEDULE_STATUS, "+
							"  T2.MAJOR_BUILD, "+
//							"  to_char(TAPPR.UPLOAD_DATE, 'dd-Mon-yyyy') FEED_DATE , "+
							"  to_char(T2.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE , "+
							"  T1.TEMPLATE_NAME || ' - ' || T3.GENERAL_DESCRIPTION TEMPLATE_NAME  , "+
							"  NVL(TAPPR.FILE_NAME,T1.EXCEL_FILE_PATTERN) EXCEL_FILE_PATTERN,  "+
							"  T1.ACQUISITION_STATUS, "+
							"  (SELECT AST.ALPHA_SUBTAB_DESCRIPTION  "+
							"   FROM ALPHA_SUB_TAB AST  "+
							"   WHERE AST.ALPHA_TAB = T1.ACQUISITION_STATUS_AT "+
							"   AND AST.ALPHA_SUB_TAB = T1.ACQUISITION_STATUS) ACQUISITION_STATUS_DESC, "+
							"  T1.EXCEL_TEMPLATE_ID, "+
							"  T1.TEMPLATE_NAME TEMPLATE_NAME_DESC,  "+
							"  T2.ADF_NUMBER, "+
							"  T1.LE_BOOK || ' - ' || LEB.LEB_DESCRIPTION AS STAKEHOLDER, "+
							"  to_char(T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, "+
							"  (SELECT AST.ALPHA_SUBTAB_DESCRIPTION "+
							"   FROM  ALPHA_SUB_TAB AST "+
							"   WHERE AST.ALPHA_TAB = T2.ADF_SCHEDULE_STATUS_AT "+
							"     AND AST.ALPHA_SUB_TAB = T2.ADF_SCHEDULE_STATUS)   ADF_STATUS_DESC, "+
							"  to_char (V1.PROCESS_START_TIME, 'dd-Mon-yyyy HH:mi:ss') SCHEDULE_START_TIME, "+
							"  to_char (NVL(V1.PROCESS_END_TIME,NVL(V1.INT_END_TIME,NVL(V1.UPL_END_TIME,NVL(V1.EXT_END_TIME,NVL(V1.PREEXT_END_TIME,V1.PROCESS_START_TIME))))), 'dd-Mon-yyyy HH:mi:ss') SCHEDULE_END_TIME, "+
/*							"  to_char( "+
							"  ((to_date(V1.PROCESS_START_TIME) -  "+
							" to_date(NVL(V1.PROCESS_END_TIME,NVL(V1.INT_END_TIME,NVL(V1.UPL_END_TIME,NVL(V1.EXT_END_TIME,NVL(V1.PREEXT_END_TIME,V1.PROCESS_START_TIME)))))))*24*60*60) "+
							" ) DURATION "+*/
							" to_char(to_date(round((NVL (V1.PROCESS_END_TIME,NVL (V1.INT_END_TIME,NVL (V1.UPL_END_TIME,NVL (V1.EXT_END_TIME,NVL (V1.PREEXT_END_TIME,V1.PROCESS_START_TIME))))) - V1.PROCESS_START_TIME)*24*60*60,0),'sssss'),'hh24:mi:ss') DURATION "
							+ "FROM ADF_PROCESS_CONTROL T1 "+
							" INNER JOIN  ADF_SCHEDULES T2 "+
							"   ON (T1.ADF_NUMBER = T2.ADF_NUMBER) "+
							" INNER JOIN TEMPLATE_NAMES T3 "+
							"   ON (T1.TEMPLATE_NAME = T3.TEMPLATE_NAME) "+
							" INNER JOIN LE_BOOK LEB "+
							"   ON (T1.COUNTRY = LEB.COUNTRY AND T1.LE_BOOK = LEB.LE_BOOK) "+
							" LEFT OUTER JOIN ADF_UPLOAD_FILING TAPPR "+
							"   ON (TAPPR.COUNTRY = T1.COUNTRY "+
							"   AND TAPPR.LE_BOOK = T1.LE_BOOK "+
							"   AND TAPPR.BUSINESS_DATE = T1.BUSINESS_DATE "+
							"   AND TAPPR.TEMPLATE_NAME = T1.TEMPLATE_NAME) "+
							" LEFT OUTER JOIN ACQ_VERSION_NO V1 "+
							"   ON (V1.COUNTRY = T1.COUNTRY "+
							"   AND V1.LE_BOOK = T1.LE_BOOK "+
							"   AND V1.BUSINESS_DATE = T1.BUSINESS_DATE "+
							"   AND V1.FEED_DATE = T2.FEED_DATE "+
							"   AND V1.TEMPLATE_NAME = T1.TEMPLATE_NAME "+
							"   AND V1.VERSION_NO = (SELECT MAX (V2.VERSION_NO) "+
							"                        FROM ACQ_VERSION_NO V2 "+
							"                        WHERE V1.COUNTRY = V2.COUNTRY "+
							"                          AND V1.LE_BOOK = V2.LE_BOOK "+
							"                          AND V1.BUSINESS_DATE = V2.BUSINESS_DATE "+
							"                          AND V1.FEED_DATE = V2.FEED_DATE "+
							"                          AND V1.TEMPLATE_NAME = V2.TEMPLATE_NAME)) "+
							" WHERE ");
				}
					if (ValidationUtil.isValid(dObj.getCountry())){
						strQueryAppr.append(" T1.COUNTRY = '"+dObj.getCountry().toUpperCase()+"' ");
					}
					if (ValidationUtil.isValid(CalLeBook)){
						strQueryAppr.append(" AND T1.LE_BOOK = '"+CalLeBook.trim()+"' ");
					}
					if (ValidationUtil.isValid(dObj.getFrequencyProcess())){
						strQueryAppr.append(" AND T1.FREQUENCY_PROCESS = '"+dObj.getFrequencyProcess()+"' ");
					}
					if (ValidationUtil.isValid(dObj.getMajorBuild()))	{
				    	strQueryAppr.append(" AND T2.major_build = '"+dObj.getMajorBuild()+"' ");
					}
					if (ValidationUtil.isValid(dObj.getBusinessDate()))	{
				    	if ("MSSQL".equalsIgnoreCase(databaseType))
				    		strQueryAppr.append(" AND T1.BUSINESS_DATE = CONVERT(date, ?, 103) ");
				    	else
				    		strQueryAppr.append(" AND T1.BUSINESS_DATE = to_date(?, 'dd-Mon-yyyy')  ");
					}
					if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())) {
						if ("MSSQL".equalsIgnoreCase(databaseType))
							strQueryAppr.append("and T1.COUNTRY+ '-' +T1.LE_BOOK IN ( "+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
						else
							strQueryAppr.append("and T1.COUNTRY|| '-' ||T1.LE_BOOK IN ( "+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
						
					}
					params.add(dObj.getBusinessDate());
					String orderBy="";
					//List<UploadFilingVb> resultlst =  getQueryPopupResults(dObj,new StringBuffer(), strQueryAppr, new String(), "", params, Panel3Mapper());
					strQueryAppr.append(" ORDER BY T1.ADF_NUMBER,T1.SUB_ADF_NUMBER,T1.PROCESS_SEQUENCE ");
					if(SessionContextHolder.getContext().getVisionId() == 1232 || SessionContextHolder.getContext().getVisionId() == 1084) {
						System.out.println("=============================================================");
						System.out.println("Pannel 3 : "+strQueryAppr.toString());
						System.out.println("=============================================================");
					}
					Object objParams[] = new Object[1];
					objParams[0] = dObj.getBusinessDate();
					List<UploadFilingVb> collTemp = null;
					collTemp = getJdbcTemplate().query(strQueryAppr.toString(),objParams,Panel3Mapper());
					exceptionCode.setResponse(collTemp);

					//exceptionCode = commonApiDao.getCommonResultDataQuery(strQueryAppr.toString());
			}catch(Exception ex) {
				ex.printStackTrace();
				logger.error("Error: Exception in getting the Acq - E Upload Filing Panel3 results : ");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(ex.getMessage());
			}
			return exceptionCode;
		}
		protected RowMapper Panel3Mapper(){
			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					UploadFilingVb uploadFilingVb = new UploadFilingVb();
					uploadFilingVb.setCountry(rs.getString("COUNTRY"));
					uploadFilingVb.setLeBook(rs.getString("LE_BOOK"));
					uploadFilingVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
					uploadFilingVb.setMakerName(rs.getString("TEMPLATE_NAME_DESC"));
					uploadFilingVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
					if(ValidationUtil.isValid(rs.getString("EXCEL_FILE_PATTERN"))){
						uploadFilingVb.setFileName(rs.getString("EXCEL_FILE_PATTERN"));
					}else{
						uploadFilingVb.setFileName(rs.getString("EXCEL_FILE_PATTERN"));
					}
					uploadFilingVb.setAcquisitionStatus(rs.getString("ACQUISITION_STATUS"));
					uploadFilingVb.setExcelTemplateId(rs.getString("EXCEL_TEMPLATE_ID"));
					uploadFilingVb.setScheduleStatus(rs.getString("ADF_SCHEDULE_STATUS"));
					uploadFilingVb.setDescription(rs.getString("ADF_STATUS_DESC"));
					uploadFilingVb.setUploadDate(rs.getString("FEED_DATE"));
					uploadFilingVb.setFeedDate(rs.getString("FEED_DATE"));
					uploadFilingVb.setAdfNumber(rs.getString("ADF_NUMBER"));
					uploadFilingVb.setFileNode(rs.getString("NODE"));
					uploadFilingVb.setScheduleStartTime(rs.getString("SCHEDULE_START_TIME"));
					uploadFilingVb.setScheduleEndTime(rs.getString("SCHEDULE_END_TIME"));
					uploadFilingVb.setDuration(rs.getString("DURATION"));
					//uploadFilingVb.setEodInitiatedFlag(rs.getString("EOD_INITIATED_FLAG"));
					
					uploadFilingVb.setAcquisitionStatusDesc(rs.getString("ACQUISITION_STATUS_DESC"));   
					return uploadFilingVb;
				}
			};
			return mapper;
		}
		

}