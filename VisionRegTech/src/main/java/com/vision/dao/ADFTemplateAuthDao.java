package com.vision.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.Paginationhelper;
import com.vision.util.ValidationUtil;
import com.vision.vb.ADFTemplateAuthVb;
import com.vision.vb.AlphaSubTabVb;
import com.vision.vb.RsAccessVb;
import com.vision.vb.VisionUsersVb;

import freemarker.template.Configuration;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
@Component
public class ADFTemplateAuthDao extends AbstractDao<ADFTemplateAuthVb> {
	
	@Value("${app.databaseType}")
	private String databaseType;
	
	private JavaMailSenderImpl javaMailSender;
	
	public JavaMailSenderImpl getJavaMailSender() {
		return javaMailSender;
	}
	public void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	@Autowired
	Configuration config;

	@Value("${spring.mail.host}")
	private String host;
	
	@Value("${spring.mail.port}")
	private String port;
	
	@Autowired
	CommonDao commonDao;
	
	@Autowired
	VisionUsersDao visionUsersDao;
	
	@Value("${spring.mail.username}")
	public String username;
	
	@Value("${spring.mail.password}")
	public String password;
	
				
	protected RowMapper getQueryMapper(final String tempTable){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ADFTemplateAuthVb adfTemplateAuthVb = new ADFTemplateAuthVb();
				adfTemplateAuthVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				adfTemplateAuthVb.setRecordCount(rs.getString("RECORDS_COUNT"));
//				adfTemplateAuthVb.setRecordCount(Integer.parseInt(rs.getString("RECORDS_COUNT").replaceAll(",", "")));
				adfTemplateAuthVb.setTimeCollapsed(rs.getString("TIME_ELAPSED"));
				adfTemplateAuthVb.setADFTempAuthStatus(rs.getString("ACQ_STATUS"));
				
				adfTemplateAuthVb.setADFTempAuthStatusDesc(rs.getString("ACQ_STATUS_DESC"));
				adfTemplateAuthVb.setStartTime(rs.getString("START_TIME"));
				adfTemplateAuthVb.setEndTime(rs.getString("END_TIME"));
				adfTemplateAuthVb.setAuthStatus(rs.getInt("AUTH_STATUS"));
				/*if(ValidationUtil.isValid(rs.getString("AUTH_STATUS"))){
					adfTemplateAuthVb.setAuthStatus(rs.getInt("AUTH_STATUS"));
				}*/
				adfTemplateAuthVb.setCountry(rs.getString("COUNTRY"));
				adfTemplateAuthVb.setLeBook(rs.getString("LE_BOOK"));
				adfTemplateAuthVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfTemplateAuthVb.setFeedDate(rs.getString("FEED_DATE"));
				adfTemplateAuthVb.setProcessFrequency(rs.getString("FREQUENCY_PROCESS"));
				adfTemplateAuthVb.setEmailID(rs.getString("CB_EMAIL_IDS"));
				adfTemplateAuthVb.setEodInitiatedFlag(rs.getString("EOD_FLAG"));
				adfTemplateAuthVb.setTempTable(tempTable);
				adfTemplateAuthVb.setAuthStatusDesc(rs.getString("AUTH_STATUS_DESCRIPTION"));
				adfTemplateAuthVb.setFileName(rs.getString("EXCEL_FILE_PATTERN")); 
				
				return adfTemplateAuthVb;
			}
		};
		return mapper;
	}
	@Override
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ADFTemplateAuthVb adfTemplateAuthVb = new ADFTemplateAuthVb();
				adfTemplateAuthVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
			//	adfTemplateAuthVb.setRecordCount(Integer.parseInt(rs.getString("RECORDS_COUNT").replaceAll(",", "")));
				adfTemplateAuthVb.setRecordCount(rs.getString("RECORDS_COUNT"));
				adfTemplateAuthVb.setTimeCollapsed(rs.getString("TIME_ELAPSED"));
				adfTemplateAuthVb.setADFTempAuthStatus(rs.getString("ACQ_STATUS"));
				adfTemplateAuthVb.setStartTime(rs.getString("START_TIME"));
				adfTemplateAuthVb.setEndTime(rs.getString("END_TIME"));
				adfTemplateAuthVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfTemplateAuthVb.setFeedDate(rs.getString("FEED_DATE"));
				adfTemplateAuthVb.setProcessFrequency(rs.getString("FREQUENCY_PROCESS"));
				adfTemplateAuthVb.setEodInitiatedFlag(rs.getString("EOD_FLAG"));
				return adfTemplateAuthVb;
			}
		};
		return mapper;
	}
	protected RowMapper getCommentMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ADFTemplateAuthVb adfTemplateAuthVb = new ADFTemplateAuthVb();
				adfTemplateAuthVb.setComment(rs.getString("COMMENTS"));
				adfTemplateAuthVb.setErrorCode(rs.getString("ERROR_CODE"));
				adfTemplateAuthVb.setErrColumnName(rs.getString("ERR_COLUMN_NAME"));
				adfTemplateAuthVb.setErrDescription(rs.getString("ERROR_DESCRIPTION"));
				adfTemplateAuthVb.setErrType(rs.getString("ERROR_TYPE"));
				return adfTemplateAuthVb;
			}
		};
		return mapper;
	}
	protected RowMapper getCommentDrilldownMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ADFTemplateAuthVb adfTemplateAuthVb = new ADFTemplateAuthVb();
				adfTemplateAuthVb.setPrimaryKeyColumn(rs.getString("PRIMARY_KEY_COLUMN"));
				adfTemplateAuthVb.setColumnValue(rs.getString("COLUMN_VALUE"));
				return adfTemplateAuthVb;
			}
		};
		return mapper;
	}
	@SuppressWarnings("unchecked")
	public List<ADFTemplateAuthVb> getCommentResults(ADFTemplateAuthVb vObj) throws DataAccessException {
		String TempTemplateName=removeDescTemplateName(vObj.getTemplateName());
		String sql = "Select TRS.ERROR_CODE, TRS.ERR_COLUMN_NAME, TRS.COMMENTS, "+ 
			    "Case When EC.ERROR_DESCRIPTION Like 'Invalid Num Tab and Num Sub Tab value' "+ 
			    "OR EC.ERROR_DESCRIPTION Like 'Invalid Alpha Tab and Alpha Sub Tab value' "+
			    "Then 'Please check template guidelines.' "+
			    "Else EC.ERROR_DESCRIPTION End ERROR_DESCRIPTION, "+ 
			    "(Select N.Num_SubTab_Description From NUM_SUB_TAB N Where N.NUM_TAB = EC.ERROR_TYPE_NT And N.NUM_SUB_TAB = EC.ERROR_TYPE) Error_Type "+
			    "from  "+vObj.getTempTable()+" TRS left JOIN ERROR_CODES EC "+
			    " ON  TRS.ERROR_CODE = EC.ERROR_CODE Where template_name = ?  ";
		Object[] lParams = new Object[1];
		lParams[0] = TempTemplateName;
		return getJdbcTemplate().query(sql, lParams, getCommentMapper());
	}
	@SuppressWarnings("unchecked")
	public List<ADFTemplateAuthVb> getCommentDrilldownResults(ADFTemplateAuthVb vObj) throws DataAccessException {
		String TempLeBook=removeDescLeBook(vObj.getLeBook());
		String TempTemplateName=removeDescTemplateName(vObj.getTemplateName());
		String sql = "Select REPLACE(REPLACE(REPLACE(PRIMARY_KEY_COLUMN,'}{',' , '), '{',''), '}','') PRIMARY_KEY_COLUMN,COLUMN_VALUE "+ 
					   " from VISION_DBP_ERRORS VDE , "+vObj.getTempTable()+" TRS "+ 
					   " where VDE.Country = ? "+
					   " And VDE.Le_Book = ? "+ 
					   " And VDE.BUSINESS_DATE = ? "+ 
					   " And VDE.FEED_DATE = ? "+ 
					   " And VDE.TEMPLATE_NAME = ? "+ 
					   " AND VDE.ERROR_CODE = ? "+ 
					   " And ERR_COLUMN_NAME = ? "+ 
					   " And VDE.Template_Name = TRS.Template_Name "+ 
					   " And VDE.Column_Name = TRS.Err_Column_Name "+ 
					   " And VDE.ERROR_CODE = TRS.ERROR_CODE";
		Object[] lParams = new Object[7];
		lParams[0] = new String(vObj.getCountry());	//[COUNTRY]
		lParams[1] = new String(TempLeBook);	//[LE_BOOK]
		lParams[2] = new String(vObj.getBusinessDate());	//[BUSINESS_DATE]
		lParams[3] = new String(vObj.getFeedDate());
		lParams[4] = new String(TempTemplateName);	//[TEMPLATE_NAME]
		lParams[5] = new String(vObj.getErrorCode());
		lParams[6] = new String(vObj.getErrColumnName());
		return getJdbcTemplate().query(sql, lParams, getCommentDrilldownMapper());
	}
	public String getLeBook(ADFTemplateAuthVb vObj){
		String trimmedLeBook=removeDescLeBook(vObj.getLeBook());	
		if ("MSSQL".equalsIgnoreCase(databaseType))
			return getJdbcTemplate().queryForObject("SELECT LE_BOOK + '-' + LEB_DESCRIPTION FROM LE_BOOK WHERE COUNTRY = '"+vObj.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook.toUpperCase()+"' ",String.class);
		else
			return getJdbcTemplate().queryForObject("SELECT LE_BOOK || '-' || LEB_DESCRIPTION FROM LE_BOOK WHERE COUNTRY = '"+vObj.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook.toUpperCase()+"' ",String.class);
	}
	public String getLegalVehicle(ADFTemplateAuthVb vObj){
		String trimmedLeBook=removeDescLeBook(vObj.getLeBook());		
		return getJdbcTemplate().queryForObject("SELECT LEGAL_VEHICLE FROM LE_BOOK WHERE COUNTRY = '"+vObj.getCountry()+"' AND LE_BOOK = '"+trimmedLeBook.toUpperCase()+"' ",String.class);
	}
	public  ExceptionCode getQueryResults(ADFTemplateAuthVb adfTemplateAuthVb, String templateName){
		ExceptionCode exceptionCode = new ExceptionCode();
		setServiceDefaults();
		String TempLeBook=removeDescLeBook(adfTemplateAuthVb.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
		query = "SELECT DISTINCT r.TEMPLATE_NAME + '-' + (SELECT TEMPLATE_NAMEs.GENERAL_DESCRIPTION FROM TEMPLATE_NAMEs WHERE TEMPLATE_NAMEs.TEMPLATE_NAME = r.TEMPLATE_NAME) AS TEMPLATE_NAME, "+
			"r.RECORDS_COUNT,r.TIME_ELAPSED,r.ACQ_STATUS,  "
			+ " (select ALPHA_SUBTAB_DESCRIPTION from alpha_sub_tab where alpha_tab = '1084' and alpha_sub_tab = r.acq_status) ACQ_STATUS_DESC , "
			+ " format (r.START_TIME, 'dd-MMM-yyyy HH:mm:ss') START_TIME,format (r.END_TIME, 'dd-MMM-yyyy HH:mm:ss') END_TIME, "+
			"s.CB_EMAIL_IDS,ISNULL(s.AUTH_STATUS,3) AUTH_STATUS,r.COUNTRY,LEB.LE_BOOK+ '-' +LEB.LEB_Description as LE_BOOK ,format (r.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE,format (p.FEED_DATE, 'dd-MMM-yyyy') FEED_DATE, "+
			"s.FREQUENCY_PROCESS,Case When (Select count(1) from ADF_PROCESS_CONTROL X1  "+
			"Where X1.Country = s.Country And X1.LE_BOOK = s.le_Book And X1.Business_Date = s.Business_Date And X1.Frequency_Process = s.Frequency_Process "+
			"  And "+
			" (   X1.EOD_INITIATED_FLAG = 'Y' "+
            " OR X1.AUTH_STATUS IN (0, 4))) != 0 "+
			" Then 'Y' Else 'N' End  EOD_FLAG , "+
			"s.ADF_NUMBER,s.SUB_ADF_NUMBER,s.PROCESS_SEQUENCE, s.EXCEL_FILE_PATTERN ,  "
			+ " (select isnull(NUM_SUBTAB_DESCRIPTION, 'Not Applicable') from num_sub_tab where num_tab = '1065' and num_sub_tab = s.auth_status) auth_status_description "
			+ " FROM ? r, ADF_PROCESS_CONTROL s, ADF_SCHEDULES p,  LE_BOOK LEB  "+
			"WHERE r.COUNTRY = s.COUNTRY AND r.LE_BOOK = s.LE_BOOK AND r.BUSINESS_DATE = s.BUSINESS_DATE AND r.TEMPLATE_NAME = s.TEMPLATE_NAME AND r.FREQUENCY_PROCESS = s.FREQUENCY_PROCESS "+
			"AND p.ADF_NUMBER = s.ADF_NUMBER and  r.Le_Book = LEB.Le_Book";
		}else {
			query = "SELECT DISTINCT r.TEMPLATE_NAME || '-' || (SELECT TEMPLATE_NAMEs.GENERAL_DESCRIPTION FROM TEMPLATE_NAMEs WHERE TEMPLATE_NAMEs.TEMPLATE_NAME = r.TEMPLATE_NAME) AS TEMPLATE_NAME, "+
					"r.RECORDS_COUNT,r.TIME_ELAPSED,r.ACQ_STATUS,  "
					+ " (select ALPHA_SUBTAB_DESCRIPTION from alpha_sub_tab where alpha_tab = '1084' and alpha_sub_tab = r.acq_status) ACQ_STATUS_DESC , "
					+ " To_Char (r.START_TIME, 'dd-Mon-yyyy HH:mi:ss') START_TIME,To_Char (r.END_TIME, 'dd-Mon-yyyy HH:mi:ss') END_TIME, "+
					"s.CB_EMAIL_IDS,NVL(s.AUTH_STATUS,3) AUTH_STATUS,r.COUNTRY,LEB.LE_BOOK|| '-' ||LEB.LEB_Description as LE_BOOK ,To_Char (r.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE,To_Char (p.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE, "+
					"s.FREQUENCY_PROCESS,Case When (Select count(1) from ADF_PROCESS_CONTROL X1  "+
					"Where X1.Country = s.Country And X1.LE_BOOK = s.le_Book And X1.Business_Date = s.Business_Date And X1.Frequency_Process = s.Frequency_Process "+
					"  And "+
					" (   X1.EOD_INITIATED_FLAG = 'Y' "+
		            " OR X1.AUTH_STATUS IN (0, 4))) != 0 "+
					" Then 'Y' Else 'N' End  EOD_FLAG , "+
					"s.ADF_NUMBER,s.SUB_ADF_NUMBER,s.PROCESS_SEQUENCE, s.EXCEL_FILE_PATTERN ,  "
					+ " (select NVL(NUM_SUBTAB_DESCRIPTION, 'Not Applicable') from num_sub_tab where num_tab = '1065' and num_sub_tab = s.auth_status) auth_status_description "
					+ " FROM ? r, ADF_PROCESS_CONTROL s, ADF_SCHEDULES p,  LE_BOOK LEB  "+
					"WHERE r.COUNTRY = s.COUNTRY AND r.LE_BOOK = s.LE_BOOK AND r.BUSINESS_DATE = s.BUSINESS_DATE AND r.TEMPLATE_NAME = s.TEMPLATE_NAME AND r.FREQUENCY_PROCESS = s.FREQUENCY_PROCESS "+
					"AND p.ADF_NUMBER = s.ADF_NUMBER and  r.Le_Book = LEB.Le_Book";
		}
		Connection con = null;
		List<ADFTemplateAuthVb> collTemp = new ArrayList<>();
		CallableStatement cs =  null;
		try{
//			String sessionId = String.valueOf(System.currentTimeMillis());
			String sessionId = "SC"+SessionContextHolder.getContext().getVisionId();
			con = getConnection();
			cs = con.prepareCall("{call PR_RS_ACQALERT02_JAVA (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			cs.setString(1, String.valueOf(intCurrentUserId));//VisionId
			cs.setString(2, sessionId);//Session
	        cs.setString(3, "ACQALERT02");//Report Id
	        cs.setInt(4, 1);//Scalingfactor
	        cs.setString(5, adfTemplateAuthVb.getLegalVehicle()+"-"+adfTemplateAuthVb.getCountry()+"-"+TempLeBook);
	        cs.setString(6, adfTemplateAuthVb.getBusinessDate());
	        cs.setString(7, adfTemplateAuthVb.getFeedDate());
	        cs.setString(8, templateName);
        	cs.setString(9, adfTemplateAuthVb.getProcessFrequency());
        	cs.setString(10, adfTemplateAuthVb.getMajorBuild());//P_Prompt_Value_6
        	cs.setString(11, "");//P_Prompt_Value_7
        	cs.setString(12, "");//P_Prompt_Value_8
        	cs.registerOutParameter(13, java.sql.Types.VARCHAR); //Table Name
	        cs.registerOutParameter(14, java.sql.Types.VARCHAR); //Status 		
	        cs.registerOutParameter(15, java.sql.Types.VARCHAR); //Error Message
	        
		 	cs.execute();
		 	
		 	adfTemplateAuthVb.setOutputTab(cs.getString(13));
		 	adfTemplateAuthVb.setStatus(cs.getString(14));
		 	adfTemplateAuthVb.setErrorMessage(cs.getString(15));
		    cs.close();
		    
		    if("0".equalsIgnoreCase(adfTemplateAuthVb.getStatus())){
				if(ValidationUtil.isValid(adfTemplateAuthVb.getOutputTab())){
					query = query.replace("?", adfTemplateAuthVb.getOutputTab());
					adfTemplateAuthVb.setQuery(query);
					collTemp =  getQueryPopupResults1(adfTemplateAuthVb);
				}
				exceptionCode.setResponse(collTemp);
				exceptionCode.setErrorMsg("ADF Template Auth - Query - Successful ");
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			} else {
				exceptionCode.setErrorMsg("ADF Template Auth - Query Failed - "+adfTemplateAuthVb.getErrorMessage());
				exceptionCode.setErrorCode(Integer.parseInt(adfTemplateAuthVb.getStatus()));
				exceptionCode.setResponse(new ArrayList<>());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
		return exceptionCode;
	}
	public List<ADFTemplateAuthVb> getQueryPopupResults1(ADFTemplateAuthVb dObj){
		Vector<Object> params = new Vector<Object>();
		dObj.setVerificationRequired(false);
		StringBuffer strBufPending = new StringBuffer("");
		String strWhereNotExists = "";
		StringBuffer strBufApprove =  new StringBuffer(dObj.getQuery());
		String orderBy= " order by ADF_NUMBER, SUB_ADF_NUMBER, PROCESS_SEQUENCE";
		try{
				return getQueryPopupResultsForMainPage(dObj, null, strBufApprove, strWhereNotExists, orderBy, params,getQueryMapper(dObj.getOutputTab()));
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
	public RowMapper getQueryPopupMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ADFTemplateAuthVb adfTemplateAuthVb = new ADFTemplateAuthVb();
				adfTemplateAuthVb.setCountry(rs.getString("COUNTRY"));
				adfTemplateAuthVb.setLeBook(rs.getString("LE_BOOK"));
				adfTemplateAuthVb.setProcessFrequency(rs.getString("FREQUENCY_PROCESS"));
				adfTemplateAuthVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				adfTemplateAuthVb.setFeedDate(rs.getString("FEED_DATE"));
//				adfTemplateAuthVb.setAcquisitionProcessType(rs.getString("ACQUISITION_PROCESS_TYPE"));
				adfTemplateAuthVb.setMajorBuild(rs.getString("MAJOR_BUILD"));
				adfTemplateAuthVb.setDescription(rs.getString("FREQUENCY_PROCESS_DESC"));
//				adfTemplateAuthVb.setMakerName(rs.getString("PROCESS_TYPE_DESC"));
				return adfTemplateAuthVb;
			}
		};
		return mapper;
	}
	public List<ADFTemplateAuthVb> getQueryPopupResults(ADFTemplateAuthVb dObj){
		Vector<Object> params = new Vector<Object>();
		String TempLeBook=removeDescLeBook(dObj.getLeBook());
		dObj.setVerificationRequired(false);
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strBufApprove = new StringBuffer("SELECT DISTINCT "
				+ " LEB.COUNTRY,"
				+ " LEB.LE_BOOK+ '-' +LEB.LEB_Description as LE_BOOK,"
				+ " (select ALPHA_SUB_TAB.alpha_subtab_description from ALPHA_SUB_TAB where ALPHA_SUB_TAB.alpha_tab=T1.FREQUENCY_PROCESS_AT And ALPHA_SUB_TAB.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) as FREQUENCY_PROCESS_DESC,"
				+ " T1.FREQUENCY_PROCESS,"
				+ " T2.MAJOR_BUILD,Format(T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE,"
				+ " Format(FEED_DATE, 'dd-MMM-yyyy') FEED_DATE "
				+ " FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2, LE_BOOK LEB "
				+ " where T1.ADF_NUMBER = T2.ADF_NUMBER"
				+ " And T1.Country = LEB.Country"
				+ " And T1.Le_Book = LEB.Le_Book");
		}else {
			strBufApprove = new StringBuffer("SELECT DISTINCT "
					+ " LEB.COUNTRY,"
					+ " LEB.LE_BOOK || '-' ||LEB.LEB_Description as LE_BOOK,"
					+ " (select ALPHA_SUB_TAB.alpha_subtab_description from ALPHA_SUB_TAB where ALPHA_SUB_TAB.alpha_tab=T1.FREQUENCY_PROCESS_AT And ALPHA_SUB_TAB.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) as FREQUENCY_PROCESS_DESC,"
					+ " T1.FREQUENCY_PROCESS,"
					+ " T2.MAJOR_BUILD,To_Char(T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE,"
					+ " To_Char(FEED_DATE, 'dd-Mon-yyyy') FEED_DATE "
					+ " FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2, LE_BOOK LEB "
					+ " where T1.ADF_NUMBER = T2.ADF_NUMBER"
					+ " And T1.Country = LEB.Country"
					+ " And T1.Le_Book = LEB.Le_Book");
		}
		try
		{
			//String queryString="";
/*			if (ValidationUtil.isValid(dObj.getLegalVehicle()))
			{
				params.addElement(dObj.getLegalVehicle());
				strBufApprove.append(" AND T3.COUNTRY = T1.COUNTRY ");
				strBufApprove.append(" AND T3.LE_BOOK = T1.LE_BOOK ");
				strBufApprove.append(" AND T1.COUNTRY LIKE '"+dObj.getCountry()+"'  AND T3.LEGAL_VEHICLE IN(SELECT LEGAL_VEHICLE FROM LEGAL_VEHICLES ");
				strBufApprove.append(" START WITH UPPER(LEGAL_VEHICLE) = ?  CONNECT BY PRIOR LEGAL_VEHICLE = PARENT_LV  AND LEGAL_VEHICLE != PARENT_LV)");
				//CommonUtils.addToQuery("UPPER(T1.LE_BOOK) like ?", strBufApprove);
			}*/
			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook()))
			{
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND T1.COUNTRY+ '-' +T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ");
				else
					strBufApprove.append(" AND T1.COUNTRY|| '-' ||T1.LE_BOOK IN("+SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase()+") ");
			}			
			if (ValidationUtil.isValid(TempLeBook))
			{
				params.addElement("%" + TempLeBook.toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T1.LE_BOOK) like ?");
				//CommonUtils.addToQuery("UPPER(T1.LE_BOOK) like ?", strBufApprove);
			}
			
			if (ValidationUtil.isValid(dObj.getProcessFrequency()) && !"-1".equalsIgnoreCase(dObj.getProcessFrequency()))
			{
				params.addElement("%" + dObj.getProcessFrequency().toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T1.FREQUENCY_PROCESS) like ?");
				//CommonUtils.addToQuery("UPPER(T1.FREQUENCY_PROCESS) like ?", strBufApprove);
			}
			/*if (ValidationUtil.isValid(dObj.getAcquisitionProcessType()) && !"-1".equalsIgnoreCase(dObj.getAcquisitionProcessType()))
			{
				params.addElement("%" + dObj.getAcquisitionProcessType().toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T1.ACQUISITION_PROCESS_TYPE) like ?");
			}*/
			if (ValidationUtil.isValid(dObj.getMajorBuild()) && !"-1".equalsIgnoreCase(dObj.getMajorBuild()))
			{
				params.addElement("%" + dObj.getMajorBuild().toUpperCase() + "%" );
				strBufApprove.append(" AND UPPER(T2.MAJOR_BUILD) like ?");
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate()) && !"-1".equalsIgnoreCase(dObj.getBusinessDate()))
			{
				params.addElement(dObj.getBusinessDate());
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND UPPER(T1.BUSINESS_DATE) = CONVERT(datetime, ?, 103)");
				else
					strBufApprove.append(" AND UPPER(T1.BUSINESS_DATE) = To_Date( ?, 'dd-Mon-yyyy')");
				//CommonUtils.addToQuery("T1.BUSINESS_DATE = Format(?, 'dd-MMM-yyyy')", strBufApprove);
			}
			if (ValidationUtil.isValid(dObj.getFeedDate()) && !"-1".equalsIgnoreCase(dObj.getFeedDate()))
			{
				params.addElement(dObj.getFeedDate());
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND UPPER(T2.FEED_DATE) = CONVERT(datetime, ?, 103)");
				else
					strBufApprove.append(" AND UPPER(T1.FEED_DATE) = To_Date( ?, 'dd-Mon-yyyy')");
				//CommonUtils.addToQuery("T2.FEED_DATE = Format(?, 'dd-MMM-yyyy')", strBufApprove);
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
	public String removeDescTemplateName(String promptString){
		if(ValidationUtil.isValid(promptString)){
			return promptString.substring(0,promptString.indexOf("-")>0?promptString.indexOf("-"): promptString.length());
		}else return promptString;
	}
	public List<ADFTemplateAuthVb> getQueryResultsForReview(ADFTemplateAuthVb dObj, int intStatus){
		List<ADFTemplateAuthVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String TempLeBook=removeDescLeBook(dObj.getLeBook());
		String TempTemplateName=removeDescTemplateName(dObj.getTemplateName());
		StringBuffer strQueryAppr = new StringBuffer();
		StringBuffer strQueryPend = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
		strQueryAppr = new StringBuffer("Select TAppr.COUNTRY,TAppr.LE_BOOK, " + 
			"TAppr.TEMPLATE_NAME,Format(TAppr.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, " + 
			"TAppr.RECORD_COUNT,TAppr.START_TIME,TAppr.END_TIME,TAppr.TIME_COLLAPSED,TAppr.COMMENT,TAppr.REVIEW," + 
			"TAppr.ADF_TEMP_AUTH_STATUS_NT,TAppr.ADF_TEMP_AUTH_STATUS,TAppr.RECORD_INDICATOR,TAppr.RECORD_INDICATOR_NT, " + 
			"TAppr.MAKER,TAppr.VERIFIER,TAppr.INTERNAL_STATUS,Format(TAppr.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION, " + 
			"Format(TAppr.DATE_LAST_MODIFIED, 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED" +
			" From ADF_PROCESS_CONTROL TAppr " + 
			"Where TAppr.COUNTRY = ?  And TAppr.LE_BOOK = ?  And TAppr.TEMPLATE_NAME = ?  And TAppr.BUSINESS_DATE = CONVERT(datetime, ?, 103) ");

		strQueryPend = new StringBuffer("Select TPend.COUNTRY,TPend.LE_BOOK, " + 
			"TPend.TEMPLATE_NAME,Format(TPend.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, " + 
			"TPend.RECORD_COUNT,TPend.START_TIME,TPend.END_TIME,TPend.TIME_COLLAPSED,TPend.COMMENT,TPend.REVIEW," + 
			"TPend.ADF_TEMP_AUTH_STATUS_NT,TPend.ADF_TEMP_AUTH_STATUS,TPend.RECORD_INDICATOR,TPend.RECORD_INDICATOR_NT, " + 
			"TPend.MAKER,TPend.VERIFIER,TPend.INTERNAL_STATUS,Format(TPend.DATE_CREATION, 'dd-MMM-yyyy HH:mm:ss') DATE_CREATION, " + 
			"Format(TPend.DATE_LAST_MODIFIED, 'dd-MMM-yyyy HH:mm:ss') DATE_LAST_MODIFIED From ADF_PROCESS_CONTROL_PEND TPend " + 
			"Where TPend.COUNTRY = ?  And TPend.LE_BOOK = ?  And TPend.TEMPLATE_NAME = ?  And TPend.BUSINESS_DATE = CONVERT(datetime, ?, 103) ");
		}else {
			strQueryAppr = new StringBuffer("Select TAppr.COUNTRY,TAppr.LE_BOOK, " + 
					"TAppr.TEMPLATE_NAME,To_Char(TAppr.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, " + 
					"TAppr.RECORD_COUNT,TAppr.START_TIME,TAppr.END_TIME,TAppr.TIME_COLLAPSED,TAppr.COMMENT,TAppr.REVIEW," + 
					"TAppr.ADF_TEMP_AUTH_STATUS_NT,TAppr.ADF_TEMP_AUTH_STATUS,TAppr.RECORD_INDICATOR,TAppr.RECORD_INDICATOR_NT, " + 
					"TAppr.MAKER,TAppr.VERIFIER,TAppr.INTERNAL_STATUS,To_Char(TAppr.DATE_CREATION, 'dd-Mon-yyyy HH:mi:ss') DATE_CREATION, " + 
					"To_Char(TAppr.DATE_LAST_MODIFIED, 'dd-Mon-yyyy HH:mi:ss') DATE_LAST_MODIFIED" +
					" From ADF_PROCESS_CONTROL TAppr " + 
					"Where TAppr.COUNTRY = ?  And TAppr.LE_BOOK = ?  And TAppr.TEMPLATE_NAME = ?  And TAppr.BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') ");

				strQueryPend = new StringBuffer("Select TPend.COUNTRY,TPend.LE_BOOK, " + 
					"TPend.TEMPLATE_NAME,To_Char(TPend.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, " + 
					"TPend.RECORD_COUNT,TPend.START_TIME,TPend.END_TIME,TPend.TIME_COLLAPSED,TPend.COMMENT,TPend.REVIEW," + 
					"TPend.ADF_TEMP_AUTH_STATUS_NT,TPend.ADF_TEMP_AUTH_STATUS,TPend.RECORD_INDICATOR,TPend.RECORD_INDICATOR_NT, " + 
					"TPend.MAKER,TPend.VERIFIER,TPend.INTERNAL_STATUS,To_Char(TPend.DATE_CREATION, 'dd-Mon-yyyy HH:mi:ss') DATE_CREATION, " + 
					"To_Char(TPend.DATE_LAST_MODIFIED, 'dd-Mon-yyyy HH:mi:ss') DATE_LAST_MODIFIED From ADF_PROCESS_CONTROL_PEND TPend " + 
					"Where TPend.COUNTRY = ?  And TPend.LE_BOOK = ?  And TPend.TEMPLATE_NAME = ?  And TPend.BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') ");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = new String(dObj.getCountry());	//[COUNTRY]
		objParams[1] = new String(TempLeBook);	//[LE_BOOK]
		objParams[2] = new String(dObj.getBusinessDate());	//[BUSINESS_DATE]
		objParams[3] = new String(TempTemplateName);	//[TEMPLATE_NAME]
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
	@Override
	protected List<ADFTemplateAuthVb> selectApprovedRecord(ADFTemplateAuthVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_ZERO);
	}

	@Override
	protected List<ADFTemplateAuthVb> doSelectPendingRecord(ADFTemplateAuthVb vObject){
		return getQueryResultsForReview(vObject, Constants.STATUS_PENDING);
	}

	public ExceptionCode doVerification(List<ADFTemplateAuthVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strCurrentOperation =Constants.Verification;
		strErrorDesc  = "";
		int val = 0;
		setServiceDefaults();
		try {
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					val = doUpdateApprADF(vObject1);
					doInsetAudit(vObject1, 0, "Approved");
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
			logger.error("Error in Modify.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode doResubmission(List<ADFTemplateAuthVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strCurrentOperation =Constants.Resubmission;
		strErrorDesc  = "";
		int val = 0;
		setServiceDefaults();
		try {
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					val = doUpdateApprResubmission(vObject1);
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
			logger.error("Error in Modify.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode doRejection(List<ADFTemplateAuthVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strCurrentOperation =Constants.Rejection;
		strErrorDesc  = "";
		setServiceDefaults();
		int val = 0;
		try {
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					//Praksah
					val = doUpdateApprRejection(vObject1);
					doInsetAudit(vObject1, 1, "Rejected");
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
			logger.error("Error in Modify.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode doResubmitApprRecordADF(ADFTemplateAuthVb vObj,List<ADFTemplateAuthVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strCurrentOperation = Constants.Resubmission;
		strErrorDesc  = "";
		int val = 0;
		int val1 = 0;
		List<ADFTemplateAuthVb> collTemp = null;
		List<ADFTemplateAuthVb> adfTemplateAuthList = new ArrayList<ADFTemplateAuthVb>();
		try {
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					adfTemplateAuthList.add(vObject1);
					collTemp = selectFeedStagingName(vObject1);
					if (collTemp == null){
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}else if(collTemp.size()>0){
						 vObject1.setFeedDate(collTemp.get(0).getFeedDate());
					}else{
						exceptionCode = getResultObject(Constants.NO_RECORDS_TO_REJECT);
						throw buildRuntimeCustomException(exceptionCode);
					}
				}
			}
			
			if(adfTemplateAuthList != null && !adfTemplateAuthList.isEmpty()){
				exceptionCode = doSendEmail(vObj, adfTemplateAuthList);
				if(exceptionCode.getErrorCode()==Constants.SUCCESSFUL_OPERATION){
					vObj.setEmailStatus(1);
					for(ADFTemplateAuthVb vObject1 : vObjects){
						if(vObject1.isChecked()){
							val1 = doUpdateApprResubmission(vObject1);
						}
					}
				}else{
					vObj.setEmailStatus(2);
				}
			}
			
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					doInsetAudit(vObject1, 2, "Resubmitted");
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
			logger.error("Error in Modify.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public ExceptionCode doUpdateApprRecordADF(List<ADFTemplateAuthVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strApproveOperation =Constants.Verification;
		strErrorDesc  = "";
		int val = 0;
		try {
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					val = doUpdateApprADF(vObject1);
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
			logger.error("Error in Modify.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	public int doUpdateApprADF(ADFTemplateAuthVb vObject){
		vObject.setAuthId(intCurrentUserId);
		vObject.setAuthStatus(0);
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		String TempTemplateName=removeDescTemplateName(vObject.getTemplateName());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Update ADF_PROCESS_CONTROL Set " + 
			"AUTH_ID = ?, AUTH_TIME = GetDate() , AUTH_STATUS = ? " + 
			"Where COUNTRY = ? " +
			" And LE_BOOK = ? " +
			" And BUSINESS_DATE = ? " +
			" And TEMPLATE_NAME = ? ";
		}else {
			query = "Update ADF_PROCESS_CONTROL Set " + 
			"AUTH_ID = ?, AUTH_TIME = sysdate , AUTH_STATUS = ? " + 
			"Where COUNTRY = ? " +
			" And LE_BOOK = ? " +
			" And BUSINESS_DATE = ? " +
			" And TEMPLATE_NAME = ? ";
		}
		Object args[] = {vObject.getAuthId(),vObject.getAuthStatus(),vObject.getCountry(),TempLeBook,vObject.getBusinessDate(),TempTemplateName};

		return getJdbcTemplate().update(query,args);
	}
	public int doUpdateApprResubmission(ADFTemplateAuthVb vObject){
		vObject.setAuthId(intCurrentUserId);
		vObject.setAuthStatus(2);
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		String TempTemplateName=removeDescTemplateName(vObject.getTemplateName());
		String query = "Update ADF_PROCESS_CONTROL Set " + 
		"AUTH_ID = ?, AUTH_STATUS = ? " + 
		"Where COUNTRY = ? " +
		" And LE_BOOK = ? " +
		" And BUSINESS_DATE = ? " +
		" And TEMPLATE_NAME = ? ";
//		System.out.println("Query "+query);
//		System.out.println("Val: "+vObject.getAuthId()+":"+vObject.getAuthStatus()+":"+vObject.getCountry()+":"+TempLeBook+":"+vObject.getBusinessDate()+":"+TempTemplateName);

		Object args[] = {vObject.getAuthId(),vObject.getAuthStatus(),vObject.getCountry(),TempLeBook,vObject.getBusinessDate(),TempTemplateName};

		return getJdbcTemplate().update(query,args);
	}
	public int doUpdateApprRejection(ADFTemplateAuthVb vObject){
		vObject.setAuthId(intCurrentUserId);
		vObject.setAuthStatus(1);
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		String TempTemplateName=removeDescTemplateName(vObject.getTemplateName());
		String query = "Update ADF_PROCESS_CONTROL Set " + 
		"AUTH_ID = ?, AUTH_STATUS = ? " + 
		"Where COUNTRY = ? " +
		" And LE_BOOK = ? " +
		" And BUSINESS_DATE = ? " +
		" And TEMPLATE_NAME = ? ";

		Object args[] = {vObject.getAuthId(),vObject.getAuthStatus(),vObject.getCountry(),TempLeBook,vObject.getBusinessDate(),TempTemplateName};

		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode doRejectApprRecordADF(List<ADFTemplateAuthVb> vObjects) throws RuntimeCustomException {
		ExceptionCode exceptionCode =null;
		strCurrentOperation =Constants.Rejection;
		strErrorDesc  = "";
		List<ADFTemplateAuthVb> collTemp = null;
		int val =0;
		try {
			for(ADFTemplateAuthVb vObject1 : vObjects){
				if(vObject1.isChecked()){
					collTemp = selectFeedStagingName(vObject1);
					if (collTemp == null){
						exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
						throw buildRuntimeCustomException(exceptionCode);
					}else if(collTemp.size()>0){
						val = doRejectApprADF(collTemp.get(0));
					}else{
						exceptionCode = getResultObject(Constants.NO_RECORDS_TO_REJECT);
						throw buildRuntimeCustomException(exceptionCode);
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
			logger.error("Error in Reject.",ex);
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}
	protected List<ADFTemplateAuthVb> selectFeedStagingName(ADFTemplateAuthVb vObject){
		return getQueryResultsForFeedStaging(vObject, Constants.STATUS_ZERO);
	}
	public List<ADFTemplateAuthVb> getQueryResultsForFeedStaging(ADFTemplateAuthVb dObj, int intStatus){

		List<ADFTemplateAuthVb> collTemp = null;
		final int intKeyFieldsCount = 4;
		String CalLeBook = removeDescLeBook(dObj.getLeBook());
		String TempTemplateName=removeDescTemplateName(dObj.getTemplateName());
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("SELECT p.COUNTRY, p.LE_BOOK, Format(p.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE, p.ADF_NUMBER,t.FEED_STG_NAME, t.FEED_CATEGORY,Format(s.FEED_DATE, 'dd-MMM-yyyy') FEED_DATE "+
				 " FROM ADF_PROCESS_CONTROL p, TEMPLATE_NAMES t, ADF_SCHEDULES s "+
							 " where "+
								 " t.TEMPLATE_NAME=p.TEMPLATE_NAME "+
								 " and s.BUSINESS_DATE=p.BUSINESS_DATE "+ 
								 " and s.COUNTRY =p.COUNTRY "+
								 " and s.LE_BOOK =p.LE_BOOK "+
								 " and p.ADF_NUMBER=s.ADF_NUMBER "+
								 " and  p.COUNTRY = ? and p.LE_BOOK =? and s.BUSINESS_DATE=? and t.TEMPLATE_NAME=?");
		}else {
			strBufApprove = new StringBuffer("SELECT p.COUNTRY, p.LE_BOOK, To_Char(p.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE, p.ADF_NUMBER,t.FEED_STG_NAME, t.FEED_CATEGORY,To_Char(s.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE "+
					 " FROM ADF_PROCESS_CONTROL p, TEMPLATE_NAMES t, ADF_SCHEDULES s "+
								 " where "+
									 " t.TEMPLATE_NAME=p.TEMPLATE_NAME "+
									 " and s.BUSINESS_DATE=p.BUSINESS_DATE "+ 
									 " and s.COUNTRY =p.COUNTRY "+
									 " and s.LE_BOOK =p.LE_BOOK "+
									 " and p.ADF_NUMBER=s.ADF_NUMBER "+
									 " and  p.COUNTRY = ? and p.LE_BOOK =? and s.BUSINESS_DATE=? and t.TEMPLATE_NAME=?");
		}
		Object objParams[] = new Object[intKeyFieldsCount];
		objParams[0] = dObj.getCountry();
		objParams[1] = CalLeBook;
		objParams[2] = dObj.getBusinessDate();
		objParams[3] = TempTemplateName;
		
		try
		{
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strBufApprove.toString(),objParams,getFeedStagingNameMapper());
			return collTemp;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForFeedStagingName Exception :   ");
				logger.error(((strBufApprove == null) ? "strQueryAppr is Null" : strBufApprove.toString()));
			if (objParams != null)
				for(int i=0 ; i< objParams.length; i++)
					logger.error("objParams[" + i + "]" + objParams[i].toString());
			return null;
		}
	}
	
	protected RowMapper getFeedStagingNameMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ADFTemplateAuthVb aDFTemplateAuthVb = new ADFTemplateAuthVb();
				aDFTemplateAuthVb.setCountry(rs.getString("COUNTRY"));
				aDFTemplateAuthVb.setLeBook(rs.getString("LE_BOOK"));
				aDFTemplateAuthVb.setBusinessDate(rs.getString("BUSINESS_DATE"));
				aDFTemplateAuthVb.setAdfNumber(rs.getInt("ADF_NUMBER"));
				aDFTemplateAuthVb.setFeedStgName(rs.getString("FEED_STG_NAME"));
				aDFTemplateAuthVb.setFeedcategory(rs.getString("FEED_CATEGORY"));
				aDFTemplateAuthVb.setFeedDate(rs.getString("FEED_DATE"));
				return aDFTemplateAuthVb;
			}
		};
		return mapper;
	}
	public int doRejectApprADF(ADFTemplateAuthVb vObject){
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "update "+vObject.getFeedStgName()+" set "+
				 " FEED_STATUS='X' "+
				 " Where COUNTRY = ? " +
				 		" And LE_BOOK = ? " +
				 		" And BUSINESS_DATE = CONVERT(datetime, ?, 103) " +
				 		" And FEED_DATE = CONVERT(datetime, ?, 103) " +
				 		" And FEED_CATEGORY = ? ";
		}else {
			query = "update "+vObject.getFeedStgName()+" set "+
					 " FEED_STATUS='X' "+
					 " Where COUNTRY = ? " +
					 		" And LE_BOOK = ? " +
					 		" And BUSINESS_DATE = to_date( ?, 'dd-Mon-yyyy') " +
					 		" And FEED_DATE = to_date( ?, 'dd-Mon-yyyy') " +
					 		" And FEED_CATEGORY = ? ";
		}
		Object args[] = {vObject.getCountry(),TempLeBook,vObject.getBusinessDate(),vObject.getFeedDate(),vObject.getFeedcategory()};
		return getJdbcTemplate().update(query,args);
	}
	public ExceptionCode doSendEmail(ADFTemplateAuthVb vObject, List<ADFTemplateAuthVb> adfTemplateAuthList){
		ExceptionCode exceptionCode = null;
		try{
			/*MimeMessagePreparator msg = prepareEmail(vObject, adfTemplateAuthList);
			getJavaMailSender().send(msg);*/
			exceptionCode = prepareAndSendMail(vObject, adfTemplateAuthList);
		//	exceptionCode = CommonUtils.getResultObject("ADF Template Re-Submission", Constants.SUCCESSFUL_OPERATION, "E-Mail", "");
		}catch (MailAuthenticationException e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("ADF Template Re-Submission", Constants.ERRONEOUS_OPERATION, "E-Mail", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}catch (Exception e){
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("ADF Template Re-Submission", Constants.ERRONEOUS_OPERATION, "E-Mail", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		return exceptionCode;
	}
	private MimeMessagePreparator prepareEmail(final ADFTemplateAuthVb vObject, final List<ADFTemplateAuthVb> adfTemplateAuthList){
		MimeMessagePreparator msg = new MimeMessagePreparator(){
			public void prepare(MimeMessage mimeMessage) throws Exception {
				Map<String , Object> map = new HashMap<String, Object>();
				String msgBody = "";
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				message.setFrom("Visionbi@bnr.rw");
				//message.setTo(vObject.getEmailID());
				map.put("adfTemplatesList", adfTemplateAuthList);
			    int cnt = 0;
				for(ADFTemplateAuthVb vObj : adfTemplateAuthList){
					if(adfTemplateAuthList.get(cnt).getEmailID()!=null && adfTemplateAuthList.get(cnt).getEmailID()!=""){
						message.setTo(adfTemplateAuthList.get(cnt).getEmailID());
					}
					cnt++;
				} 
				String userName=SessionContextHolder.getContext().getUserName();
				String dataNote= " from "+userName+" [ Vision Id : "+intCurrentUserId+" ]";
				map.put("note", dataNote);
				map.put("noteCmd", vObject.getEmailNote());

//msgBody = VelocityEngineUtils.mergeTemplateIntoString(getVelocityEngine(), "com/vision/wb/ADF_RE_SUBMISSION.vm", map);
				message.setText(msgBody,true);
				message.setSentDate(new Date());
				String[] parts= null;
				if(ValidationUtil.isValid(vObject.getLeBook())){
					 parts=vObject.getLeBook().split("-");
				}
				message.setSubject("Re-Submission request of "+parts[1]+" ( "+vObject.getCountry()+" - "+parts[0]+" ) for "+"[ "+vObject.getBusinessDate()+" ]");
			}
		};
		return msg;
	}
	
	@Override
	protected String frameErrorMessage(ADFTemplateAuthVb vObject, String strOperation){
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		// specify all the key fields and their values first
		String strErrMsg = new String("");
		try {
			strErrMsg =  strErrMsg + " COUNTRY:" + vObject.getCountry();
			strErrMsg =  strErrMsg + " LE_BOOK:" + TempLeBook;
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
	protected String getAuditString(ADFTemplateAuthVb vObject){
		final String auditDelimiter = vObject.getAuditDelimiter();
		final String auditDelimiterColVal = vObject.getAuditDelimiterColVal();
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		StringBuffer strAudit = new StringBuffer("");
		try{
			if(ValidationUtil.isValid(vObject.getCountry()))
				strAudit.append("COUNTRY"+auditDelimiterColVal+vObject.getCountry().trim());
			else
				strAudit.append("COUNTRY"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);

			if(ValidationUtil.isValid(TempLeBook))
				strAudit.append("LE_BOOK"+auditDelimiterColVal+TempLeBook);
			else
				strAudit.append("LE_BOOK"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getBusinessDate()))
				strAudit.append("BUSINESS_DATE"+auditDelimiterColVal+vObject.getBusinessDate().trim());
			else
				strAudit.append("BUSINESS_DATE"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getTemplateName()))
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+vObject.getTemplateName().trim());
			else
				strAudit.append("TEMPLATE_NAME"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAuthId()))
				strAudit.append("AUTH_ID"+auditDelimiterColVal+vObject.getAuthId());
			else
				strAudit.append("AUTH_ID"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
			if(ValidationUtil.isValid(vObject.getAuthStatus()))
				strAudit.append("AUTH_STATUS"+auditDelimiterColVal+vObject.getAuthStatus());
			else
				strAudit.append("AUTH_STATUS"+auditDelimiterColVal+"NULL");
			strAudit.append(auditDelimiter);
			
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
	@Override
	protected void setServiceDefaults(){
		serviceName = "ADFTemplateAuth";
		serviceDesc = CommonUtils.getResourceManger().getString("adfTemplateAuth");
		tableName = "ADF_TEMPLATE_AUTH";
		childTableName = "ADF_TEMPLATE_AUTH";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}
	public List<RsAccessVb> getProcessFrequency(ADFTemplateAuthVb adfTemplateAuthVb, String requestFrequency){
		List<RsAccessVb> collTemp = null;
//		String trimmedLeBook=removeDescLeBook(adfTemplateAuthVb.getLeBook());
		
		String leBook = adfTemplateAuthVb.getLeBook();
//		int ocunt = StringUtils.countMatches(leBook, "-");
/*		int occurance = StringUtils.countOccurrencesOf(leBook, "-");
		String trimmedLeBook = " ";
		if(occurance == 1){
			trimmedLeBook = removeDescLeBook(adfTemplateAuthVb.getLeBook());
			trimmedLeBook = "'"+adfTemplateAuthVb.getCountry()+"-"+trimmedLeBook+"'";
		}else if(ValidationUtil.isValid(adfTemplateAuthVb.getLeBook())){
			trimmedLeBook = adfTemplateAuthVb.getLeBook();
		}else
			trimmedLeBook = "' '";*/
		int occurance = StringUtils.countOccurrencesOf(leBook, "-");
		String trimmedLeBook = " ";
		if(occurance == 1){
			if(adfTemplateAuthVb.getLeBook().indexOf("- ") > 0){
				trimmedLeBook = removeDescLeBook(adfTemplateAuthVb.getLeBook());
				trimmedLeBook = "'"+adfTemplateAuthVb.getCountry()+"-"+trimmedLeBook+"'";
			}else{
				trimmedLeBook = adfTemplateAuthVb.getLeBook();	
			}
		}else{
			trimmedLeBook = adfTemplateAuthVb.getLeBook();
		}
		
		StringBuffer strQueryAppr = new StringBuffer("SELECT DISTINCT t3.ALPHA_SUBTAB_DESCRIPTION,t1.FREQUENCY_PROCESS ALPHA_SUBTAB "
				+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2,ALPHA_SUB_TAB t3 "
				+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+adfTemplateAuthVb.getCountry()+"' "
						+ " AND t3.ALPHA_SUB_TAB = t1.FREQUENCY_PROCESS ");
		
		if(ValidationUtil.isValid(trimmedLeBook)) {
			strQueryAppr  = strQueryAppr.append(" AND t1.LE_BOOK IN ('"+trimmedLeBook+"') ");
		}
		
		try
		{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getFrequencyProcessMapper(requestFrequency));
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	public List<RsAccessVb> getBusnessDate(ADFTemplateAuthVb adfTemplateAuthVb, String requestDate){
		List<RsAccessVb> collTemp = null;
		String trimmedLeBook=removeDescLeBook(adfTemplateAuthVb.getLeBook());
		StringBuffer strQueryAppr = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("SELECT DISTINCT Format(t1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE "
				+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2 "
				+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+adfTemplateAuthVb.getCountry()+"' " );
		}else {
			strQueryAppr = new StringBuffer("SELECT DISTINCT To_Char(t1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE "
					+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2 "
					+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+adfTemplateAuthVb.getCountry()+"' " );
		}
		if(ValidationUtil.isValid(trimmedLeBook)) {
			strQueryAppr  = strQueryAppr.append(" AND t1.LE_BOOK IN ('"+trimmedLeBook+"') ");
		}
		
		if(ValidationUtil.isValid(adfTemplateAuthVb.getProcessFrequency())) {
			strQueryAppr  = strQueryAppr.append(" AND T1.Frequency_Process = '"+adfTemplateAuthVb.getProcessFrequency()+"' ");
		}
		
		if(ValidationUtil.isValid(adfTemplateAuthVb.getMajorBuild() )) {
			strQueryAppr  = strQueryAppr.append(" AND T1.MAJOR_BUILD = '"+adfTemplateAuthVb.getMajorBuild()+"' ");
		}
		
		try
		{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getReportCategoryMapper(requestDate));
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	public List<RsAccessVb> getFeedDate(ADFTemplateAuthVb adfTemplateAuthVb, String requestDate){
		List<RsAccessVb> collTemp1 = null;
		String trimmedLeBook=removeDescLeBook(adfTemplateAuthVb.getLeBook());
		StringBuffer strQueryAppr = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("SELECT DISTINCT Format(t1.FEED_DATE, 'dd-MMM-yyyy') FEED_DATE "
				+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2 "
				+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+adfTemplateAuthVb.getCountry()+"' ");
		}else {
			strQueryAppr = new StringBuffer("SELECT DISTINCT To_Char(t1.FEED_DATE, 'dd-Mon-yyyy') FEED_DATE "
					+ "FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2 "
					+ "WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+adfTemplateAuthVb.getCountry()+"' ");
		}
		if(ValidationUtil.isValid(trimmedLeBook)) {
			strQueryAppr  = strQueryAppr.append(" AND t1.LE_BOOK IN ('"+trimmedLeBook+"') ");
		}
		
		if(ValidationUtil.isValid(adfTemplateAuthVb.getProcessFrequency())) {
			strQueryAppr  = strQueryAppr.append(" AND T1.FREQUENCY_PROCESS = '"+adfTemplateAuthVb.getProcessFrequency()+"' ");
		}
		
		if(ValidationUtil.isValid(adfTemplateAuthVb.getMajorBuild() )) {
			strQueryAppr  = strQueryAppr.append(" AND T1.MAJOR_BUILD = '"+adfTemplateAuthVb.getMajorBuild()+"' ");
		}
		
		if(ValidationUtil.isValid(adfTemplateAuthVb.getBusinessDate() )) {
			strQueryAppr  = strQueryAppr.append(" AND T1.BUSINESS_DATE = '"+adfTemplateAuthVb.getBusinessDate()+"' ");
		}
		
		try
		{
			collTemp1 = getJdbcTemplate().query(strQueryAppr.toString(),getReportCategoryMapper(requestDate));
			return collTemp1;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	protected RowMapper getReportCategoryMapper(final String requestDate){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				if("BusinessDate".equalsIgnoreCase(requestDate)){
					vObject.setAlphaSubTab(rs.getString("BUSINESS_DATE"));
					vObject.setDescription(rs.getString("BUSINESS_DATE"));
				}else{
					vObject.setAlphaSubTab(rs.getString("FEED_DATE"));
					vObject.setDescription(rs.getString("FEED_DATE"));					
				}
				return vObject;
			}
		};
		return mapper;
	}
	public RowMapper getFrequencyProcessMapper(final String requestFrequency){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				//if("ProcessFrequency".equalsIgnoreCase(requestFrequency)){
					vObject.setAlphaSubTab(rs.getString("ALPHA_SUBTAB"));
				 	vObject.setDescription(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				//}
				return vObject;
			}
		};
		return mapper;
	}
	protected List<ADFTemplateAuthVb> getQueryPopupResultsForMainPage(ADFTemplateAuthVb dObj,StringBuffer pendingQuery, StringBuffer approveQuery, 
			String whereNotExistsQuery,	String orderBy, Vector<Object> params, RowMapper rowMapper){
		Object objParams[]=null;
		int Ctr = 0;
		int Ctr2 = 0;
		List<ADFTemplateAuthVb> result=null;
		Paginationhelper<ADFTemplateAuthVb> paginationhelper = new Paginationhelper<ADFTemplateAuthVb>(); 
		if(dObj.isVerificationRequired() && dObj.getRecordIndicator() != 0){
		}else{
			String query = ValidationUtil.convertQuery(approveQuery.toString(), orderBy);
			if ("MSSQL".equalsIgnoreCase(databaseType)) {
				query = ValidationUtil.convertQuery(approveQuery.toString(), orderBy);
			}else {
				query = approveQuery.toString()+orderBy;
			}
			if(dObj.getTotalRows()  <= 0){
				result = paginationhelper.fetchPage(getJdbcTemplate(), query, 
						objParams, dObj.getCurrentPage(), dObj.getMaxRecords(), rowMapper == null ? getMapper(): rowMapper);
				dObj.setTotalRows(paginationhelper.getTotalRows());
			}else{
				result = paginationhelper.fetchPage(getJdbcTemplate(), query, 
						objParams, dObj.getCurrentPage(), dObj.getMaxRecords(), dObj.getTotalRows(), rowMapper == null ? getMapper(): rowMapper); 
			}
		}
		return result;
	}
	public List<ADFTemplateAuthVb> getMajorBuild(ADFTemplateAuthVb uploadFilingVb, String requestFrequency){
		List<ADFTemplateAuthVb> collTemp = null;
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
		StringBuffer strQueryAppr = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strQueryAppr = new StringBuffer("SELECT DISTINCT T1.MAJOR_BUILD+' - '+t4.PROGRAM_DESCRIPTION MAJOR_BUILD "
				+" FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2,ALPHA_SUB_TAB t3, programs t4  "
				+" WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+uploadFilingVb.getCountry()+"' "
				+" AND t3.ALPHA_TAB = t1.FREQUENCY_PROCESS_AT "
				+" AND t3.ALPHA_SUB_TAB = t1.FREQUENCY_PROCESS "
				+ "  AND T1.MAJoR_BuILd = t4.PROGRAM ");
		}else {
			strQueryAppr = new StringBuffer("SELECT DISTINCT T1.MAJOR_BUILD||' - '||t4.PROGRAM_DESCRIPTION MAJOR_BUILD "
					+" FROM ADF_SCHEDULES t1, ADF_PROCESS_CONTROL t2,ALPHA_SUB_TAB t3, programs t4  "
					+" WHERE t1.ADF_NUMBER = t2.adf_number and t1.COUNTRY = '"+uploadFilingVb.getCountry()+"' "
					+" AND t3.ALPHA_TAB = t1.FREQUENCY_PROCESS_AT "
					+" AND t3.ALPHA_SUB_TAB = t1.FREQUENCY_PROCESS "
					+ "  AND T1.MAJoR_BuILd = t4.PROGRAM ");
		}
		if(ValidationUtil.isValid(trimmedLeBook)) {
			strQueryAppr  = strQueryAppr.append(" AND t1.LE_BOOK IN ('"+trimmedLeBook+"') ");
		}
		if(ValidationUtil.isValid(uploadFilingVb.getProcessFrequency())) {
			strQueryAppr  = strQueryAppr.append(" AND T1.Frequency_Process = '"+uploadFilingVb.getProcessFrequency()+"' ");
		}
		
		try
		{
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(),getMajorBuildMapper(requestFrequency));
			return collTemp;
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.error("Error: getQueryResultsForReview Exception :   ");
				logger.error(((strQueryAppr == null) ? "strQueryAppr is null" : strQueryAppr.toString()));
			return null;
		}
	}
	public RowMapper getMajorBuildMapper(final String requestFrequency){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				AlphaSubTabVb vObject = new AlphaSubTabVb();
				if("MajorBuild".equalsIgnoreCase(requestFrequency)){
					vObject.setAlphaSubTab(rs.getString("MAJOR_BUILD"));
				 	vObject.setDescription(rs.getString("MAJOR_BUILD"));
				}
				return vObject;
			}
		};
		return mapper;
	}
	public int doInsetAudit(ADFTemplateAuthVb vObject, int status, String description){
		vObject.setAuthId(intCurrentUserId);
		vObject.setAuthStatus(status);
		String query = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			query = "Insert Into ADF_PROCESS_CONTROL_AUTH_TEMP ( COUNTRY, LE_BOOK, BUSINESS_DATE, TEMPLATE_NAME, "
				+ "AUTH_ID, AUTH_STATUS, AUTH_STATUS_DESC, "
				+ "AUTH_TIME, DATE_LAST_MODIFIED, DATE_CREATION)"+
				"Values (?, ?, ?, ?, ?, ?, ?, GetDate(), GetDate(), GetDate())";
		}else {
			query = "Insert Into ADF_PROCESS_CONTROL_AUTH_TEMP ( COUNTRY, LE_BOOK, BUSINESS_DATE, TEMPLATE_NAME, "
					+ "AUTH_ID, AUTH_STATUS, AUTH_STATUS_DESC, "
					+ "AUTH_TIME, DATE_LAST_MODIFIED, DATE_CREATION)"+
					"Values (?, ?, ?, ?, ?, ?, ?, sysdate, sysdate, sysdate)";
		}
		String TempLeBook=removeDescLeBook(vObject.getLeBook());
		String TempTemplateName=removeDescTemplateName(vObject.getTemplateName());
		Object args[] = {vObject.getCountry(),TempLeBook,vObject.getBusinessDate(),TempTemplateName, vObject.getAuthId(),
				vObject.getAuthStatus(), description};

		return getJdbcTemplate().update(query,args);
	}
	public class PopupAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String authUserName = username;// "vision.support@kdic.go.ke";
			String authPassWord = password; //"App!@!0n@123";
			System.out.println("UP : "+authUserName +" : "+authPassWord);
			return new PasswordAuthentication(authUserName, authPassWord);
		}
	}
	@SuppressWarnings({ "static-access", "deprecation" })
	public ExceptionCode prepareAndSendMail(final ADFTemplateAuthVb vObject, final List<ADFTemplateAuthVb> adfTemplateAuthList) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			Session session = null;
			getEmailProperites();
			Authenticator auth = new PopupAuthenticator();
			session = Session.getDefaultInstance(props,auth);
			MimeMessage message = new MimeMessage(session);
//			System.out.println();
			Map<String, Object> map = new HashMap<String, Object>();
			String msgBody = "";
			/*if (ValidationUtil.isValid(vObject.getUserEmailId())) {
		        message.addRecipient(Message.RecipientType.TO,new InternetAddress(vObject.getUserEmailId()));
			}*/

			for(int cnt = 0 ; cnt<adfTemplateAuthList.size();cnt++) {
				if(adfTemplateAuthList.get(cnt).getEmailID()!=null && adfTemplateAuthList.get(cnt).getEmailID()!=""){
					//message.setTo(adfTemplateAuthList.get(cnt).getEmailID());
					// adfTemplateAuthList.get(cnt).setEmailID("prakashika.govindarajan@sunoida.com");
					 message.addRecipient(Message.RecipientType.TO, new InternetAddress(adfTemplateAuthList.get(cnt).getEmailID()));
				}
			}
			
			VisionUsersVb vObje = new VisionUsersVb();
			String currentUser = commonDao.findVisionVariableValue("SYSTEM_USER_ID");
			String supportMailId = commonDao.findVisionVariableValue("SUPPORT_MAIL_ID");
			if(!ValidationUtil.isValid(supportMailId)){
				supportMailId = "data@kdic.go.ke";
			}
			VisionUsersVb systemUser = null;
			if (!ValidationUtil.isValid(currentUser))
				currentUser = "9999";
			vObje.setVisionId(Integer.parseInt(currentUser));
			vObje.setVerificationRequired(false);
			vObje.setRecordIndicator(0);
			vObje.setUserStatus(0);
			String userName=SessionContextHolder.getContext().getUserName();
			String dataNote= " from "+userName+" [ Vision Id : "+intCurrentUserId+" ]";
			map.put("note", dataNote);
			map.put("noteCmd", vObject.getEmailNote());
//			System.out.println("Emal Note : "+vObject.getEmailNote());
			List<VisionUsersVb> result = visionUsersDao.getQueryPopupResults(vObje);
			if (result != null && !result.isEmpty()) {
				systemUser = result.get(0);
			}
			if (systemUser == null) {
				logger.error("Error: System User is null");
				exceptionCode = CommonUtils.getResultObject("Email Schedule", Constants.ERRONEOUS_OPERATION, "E-Mail",
						"");
				return exceptionCode;
			}
			String fromMailId = systemUser.getUserEmailId();
			if (!ValidationUtil.isValid(fromMailId)) {
				fromMailId = "Vision.Support@sunoida.com";
			}
//			fromMailId = "Vision.Support@sunoida.com";
			message.setFrom(new InternetAddress(fromMailId));
			map.put("adfTemplatesList", adfTemplateAuthList);
			msgBody  = FreeMarkerTemplateUtils.processTemplateIntoString(config.getTemplate("ADF_RE_SUBMISSION.vm"), map);

			Multipart multipart = new MimeMultipart( "alternative" );
		    MimeBodyPart textPart = new MimeBodyPart();
		    //textPart.setText( htmlData.toString(), "utf-8" );
		    textPart.setContent( msgBody.toString(), "text/html; charset=utf-8" );
		    multipart.addBodyPart( textPart );
		   message.setContent(multipart);
		   
		//	message.setText(msgBody);
			
		   String[] parts= null;
		   String leBook = "";
		   String leBookDesc = "";
			if(ValidationUtil.isValid(vObject.getLeBook())){
				 parts=vObject.getLeBook().split("-");
				 leBook = parts[0];
				 leBookDesc = parts[1];
			}else {
				
			}
		  message.setSubject("Re-Submission request of "+leBookDesc+" ( "+vObject.getCountry()+" - "+leBook+" ) for "+"[ "+vObject.getBusinessDate()+" ]");
			message.setSentDate(new Date());
			
//			Transport transport = session.getTransport("smtp");
//			transport.send(message);
			Transport.send(message);
			exceptionCode.setErrorMsg("Adf Template Re-Submission Successful");
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode = CommonUtils.getResultObject("Email Schedule", Constants.ERRONEOUS_OPERATION, "E-Mail", "");
			exceptionCode.setErrorMsg("Adf Template Re-Submission Failed");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		}
		return exceptionCode;
	}
	
	Properties props = System.getProperties();
	
	public void getEmailProperites() {

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
	}

	public List<ADFTemplateAuthVb> filterSchedules(ADFTemplateAuthVb dObj) {
		List<ADFTemplateAuthVb> collTemp = null;
		Vector<Object> params = new Vector<Object>();
		String TempLeBook = removeDescLeBook(dObj.getLeBook());
		dObj.setVerificationRequired(false);
		StringBuffer strBufApprove = new StringBuffer();
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			strBufApprove = new StringBuffer("SELECT DISTINCT  LEB.COUNTRY,"
				+ " LEB.LE_BOOK+ '-' +LEB.LEB_Description as LE_BOOK,"
				+ " (select ALPHA_SUB_TAB.alpha_subtab_description from ALPHA_SUB_TAB where ALPHA_SUB_TAB.alpha_tab=T1.FREQUENCY_PROCESS_AT And ALPHA_SUB_TAB.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) as FREQUENCY_PROCESS_DESC,"
				+ " T1.FREQUENCY_PROCESS, T2.MAJOR_BUILD,Format(T1.BUSINESS_DATE, 'dd-MMM-yyyy') BUSINESS_DATE,"
				+ " Format(FEED_DATE, 'dd-MMM-yyyy') FEED_DATE "
				+ " FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2, LE_BOOK LEB "
				+ " where T1.ADF_NUMBER = T2.ADF_NUMBER And T1.Country = LEB.Country"
				+ " And T1.Le_Book = LEB.Le_Book ");
		}else {
			strBufApprove = new StringBuffer("SELECT DISTINCT  LEB.COUNTRY,"
					+ " LEB.LE_BOOK || '-' || LEB.LEB_Description as LE_BOOK,"
					+ " (select ALPHA_SUB_TAB.alpha_subtab_description from ALPHA_SUB_TAB where ALPHA_SUB_TAB.alpha_tab=T1.FREQUENCY_PROCESS_AT And ALPHA_SUB_TAB.ALPHA_SUB_TAB = T1.FREQUENCY_PROCESS) as FREQUENCY_PROCESS_DESC,"
					+ " T1.FREQUENCY_PROCESS, T2.MAJOR_BUILD,To_Char(T1.BUSINESS_DATE, 'dd-Mon-yyyy') BUSINESS_DATE,"
					+ " To_Char(FEED_DATE, 'dd-Mon-yyyy') FEED_DATE "
					+ " FROM ADF_PROCESS_CONTROL T1, ADF_SCHEDULES T2, LE_BOOK LEB "
					+ " where T1.ADF_NUMBER = T2.ADF_NUMBER And T1.Country = LEB.Country"
					+ " And T1.Le_Book = LEB.Le_Book ");
		}
		try {
			if (ValidationUtil.isValid(dObj.getCountry())) {
				params.addElement(dObj.getCountry());
				strBufApprove.append(" AND UPPER(T1.COUNTRY) = ?");
			}

			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())) {
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND T1.COUNTRY+ '-' +T1.LE_BOOK IN("+ SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
				else
					strBufApprove.append(" AND T1.COUNTRY|| '-' ||T1.LE_BOOK IN("+ SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
			}

			if (ValidationUtil.isValid(TempLeBook)) {
				params.addElement("%" + TempLeBook.toUpperCase() + "%");
				strBufApprove.append(" AND UPPER(T1.LE_BOOK) like ?");
			}

			if (ValidationUtil.isValid(dObj.getProcessFrequency())
					&& !"-1".equalsIgnoreCase(dObj.getProcessFrequency())) {
				params.addElement("%" + dObj.getProcessFrequency().toUpperCase() + "%");
				strBufApprove.append(" AND UPPER(T1.FREQUENCY_PROCESS) like ?");
			}
			if (ValidationUtil.isValid(dObj.getMajorBuild()) && !"-1".equalsIgnoreCase(dObj.getMajorBuild())) {
				String majorBuild  = dObj.getMajorBuild().toUpperCase().split("-")[0];
				params.addElement("%" + majorBuild.trim() + "%");
				strBufApprove.append(" AND UPPER(T2.MAJOR_BUILD) like ?");
			}
			if (ValidationUtil.isValid(dObj.getBusinessDate()) && !"-1".equalsIgnoreCase(dObj.getBusinessDate())) {
				params.addElement(dObj.getBusinessDate());
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND UPPER(T1.BUSINESS_DATE) = CONVERT(datetime, ?, 103)");
				else
					strBufApprove.append(" AND UPPER(T1.BUSINESS_DATE) = to_date( ?, 'dd-Mon-yyyy')");
			}
			if (ValidationUtil.isValid(dObj.getFeedDate()) && !"-1".equalsIgnoreCase(dObj.getFeedDate())) {
				params.addElement(dObj.getFeedDate());
				if ("MSSQL".equalsIgnoreCase(databaseType))
					strBufApprove.append(" AND UPPER(T1.FEED_DATE) = CONVERT(datetime, ?, 103)");
				else
					strBufApprove.append(" AND UPPER(T1.FEED_DATE) = to_date( ?, 'dd-Mon-yyyy')");
			}

			String orderBy = " Order By COUNTRY, LE_BOOK, BUSINESS_DATE";
			strBufApprove = strBufApprove.append(orderBy);

			Object[] args = params.toArray();
			collTemp = getJdbcTemplate().query(strBufApprove.toString(), args, getQueryPopupMapper());
			return collTemp;

		} catch (Exception ex) {
			ex.printStackTrace();
			if (params != null)
				for (int i = 0; i < params.size(); i++)
					logger.error("objParams[" + i + "]" + params.get(i).toString());
			return null;
		}
	}

}