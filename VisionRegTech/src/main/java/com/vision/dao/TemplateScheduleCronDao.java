package com.vision.dao;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.CommonVb;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateMappingVb;
import com.vision.vb.TemplateScheduleVb;
import com.vision.vb.VisionUsersVb;

@Component
public class TemplateScheduleCronDao extends AbstractDao<TemplateConfigVb> {

	@Value("${app.databaseType}")
	private String databaseType;
	
	@Autowired
	CommonDao commonDao;
	protected void setServiceDefaults() {
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
		VisionUsersVb visionUsersVb = (VisionUsersVb)SessionContextHolder.getContext();
		userGroup= visionUsersVb.getUserGroup();
		userProfile = visionUsersVb.getUserProfile();
	}
	
//	public ExceptionCode getTemplateConfigDetail() {
//		ExceptionCode exceptionCode = new ExceptionCode();
//		HashMap<String, String> templateData = new HashMap<String, String>();
//		try {
//			String query = "SELECT T1.COUNTRY,T1.LE_BOOK,T1.TEMPLATE_ID,T1.TEMPLATE_DESCRIPTION, UPPER(T1.SOURCE_TABLE) SOURCE_TABLE,T1.SOURCE_TYPE,T1.TYPE_OF_SUBMISSION,T2.RG_PROCESS_ID,T1.CSV_DELIMITER, " + 
//					 dbFunctionFormats("REPORTING_DATE", "YEAR_FORMAT", null) + " REPORTING_DATE" + 
//					//"REPORTING_DATE        " + 
//					"					  FROM Rg_Process_Controls T2, RG_TEMPLATE_CONFIG T1 " + 
//					"					  WHERE   T1.COUNTRY = T2.COUNTRY                                    " + 
//					"					      AND T1.LE_BOOK = T2.LE_BOOK                                    " + 
//					"					      AND T1.TEMPLATE_id = T2.TEMPLATE_id                        " + 
//					"					      AND T2.RG_PROCESS_STATUS IN('SP','VE','VP') ";
// 
////			ResultSetExtractor mapper = new ResultSetExtractor() {
////				@Override
////				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
////					ResultSetMetaData metaData = rs.getMetaData();
////					int colCount = metaData.getColumnCount();
////					Boolean dataPresent = false;
////					while (rs.next()) {
////						dataPresent = true;
////						for (int cn = 1; cn <= colCount; cn++) {
////							String columnName = metaData.getColumnName(cn);
////							templateData.put(columnName.toUpperCase(), rs.getString(columnName));
////						}
////					}
////					if (dataPresent) {
////						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
////						exceptionCode.setResponse(templateData);
////					} else {
////						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
////					}
////					return exceptionCode;
////				}
////			};
//			return (ExceptionCode) getJdbcTemplate().query(query, getMapper());
//		} catch (Exception e) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//			return exceptionCode;
//		}
//	}
	
	public List<TemplateScheduleVb> getTemplateConfigDetail() {
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String, String> templateData = new HashMap<String, String>();
		List<TemplateScheduleVb> collTemp;
		String submitStatus = commonDao.findVisionVariableValue("RG_AUTO_SUBMIT_STATUS");
		submitStatus= submitStatus.replaceAll(",", "','");
		try {
			String query = "SELECT T1.COUNTRY,T1.LE_BOOK,T1.TEMPLATE_ID, " + 
					 dbFunctionFormats("REPORTING_DATE", "DATE_FORMAT", null) + " REPORTING_DATE" + 
					//"REPORTING_DATE        " + 
					"					  FROM Rg_Process_Controls T2, RG_TEMPLATE_CONFIG T1 " + 
					"					  WHERE   T1.COUNTRY = T2.COUNTRY                                    " + 
					"					      AND T1.LE_BOOK = T2.LE_BOOK                                    " + 
					"					      AND T1.TEMPLATE_id = T2.TEMPLATE_id "
					+ "						  AND T1.AUTO_SUBMIT ='Y'                       " + 
					"					      AND T2.RG_PROCESS_STATUS IN('"+submitStatus+"')"
							+ "				  AND T2.AUTO_SUBMIT_DATE_TIME <=  "+systemDate;
			collTemp = getJdbcTemplate().query(query, getMapper1());
			return collTemp;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return null;
		}
	}

	public int updateProcessControlheader(String processStatus,String requestNo,String cbStatus,TemplateScheduleVb dObj ) {
		setServiceDefaults();
	
		String submitId = "";
		if(!"VE".equalsIgnoreCase(processStatus)) {
			submitId = " ,MAKER = '" +intCurrentUserId +"' ";
		}
		String query = "";
		query = "UPDATE RG_TEMPLATES_HEADER SET STATUS = ? ,REQUEST_NO = ? ,CB_STATUS = ? " +submitId
				+ " WHERE COUNTRY =? AND LE_BOOK = ? AND TEMPLATE_ID = ?  AND"
				+ " REPORTING_DATE = "+dateConvert
				+ " AND SUBMISSION_DATE = "+dateConvert;
		Object[] args = { processStatus,requestNo , cbStatus,  dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId(), dObj.getReportingDate(),dObj.getSubmissionDate() };
		return getJdbcTemplate().update(query, args);
	}
	
	public int updateProcessControlheader1(String processStatus,String requestNo,String cbStatus,TemplateScheduleVb dObj ) {
	
		String query = "";
		query = "UPDATE RG_TEMPLATES_HEADER SET STATUS = ? ,REQUEST_NO = ? ,CB_STATUS = ? " 
				+ " WHERE COUNTRY =? AND LE_BOOK = ? AND TEMPLATE_ID = ?  AND"
				+ " REPORTING_DATE = "+dateConvert
				+ " AND SUBMISSION_DATE = "+dateConvert;
		Object[] args = { processStatus,requestNo , cbStatus,  dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId(), dObj.getReportingDate(),dObj.getSubmissionDate() };
		return getJdbcTemplate().update(query, args);
	}

	
	public List<TemplateConfigVb> getTemplateConfigDetail(TemplateConfigVb templateConfigVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		HashMap<String, String> templateData = new HashMap<String, String>();
		try {
			String query = "SELECT T1.COUNTRY,T1.LE_BOOK,T1.TEMPLATE_ID,T1.TEMPLATE_DESCRIPTION, UPPER(T1.SOURCE_TABLE) SOURCE_TABLE,T1.SOURCE_TYPE,T1.TYPE_OF_SUBMISSION,T2.RG_PROCESS_ID,T1.CSV_DELIMITER,T1.DATALIST, " + 
					 dbFunctionFormats("REPORTING_DATE", "YEAR_FORMAT", null) + " REPORTING_DATE" + 
					"					  FROM Rg_Process_Controls T2, RG_TEMPLATE_CONFIG T1 " + 
					"					  WHERE   T1.COUNTRY = T2.COUNTRY                                    " + 
					"					      AND T1.LE_BOOK = T2.LE_BOOK                                    " + 
					"					      AND T1.TEMPLATE_id = T2.TEMPLATE_id                        " + 
					"					      AND T2.RG_PROCESS_STATUS = 'SI' "
					+ " AND T1.COUNTRY = ? "
					+ " AND T1.LE_BOOK= ? "
					+ " AND T1.TEMPLATE_ID = ? "
					+ " AND REPORTING_DATE =  "+dateConvert;

			Object[] args = { templateConfigVb.getCountry(), templateConfigVb.getLeBook(), templateConfigVb.getTemplateId(), templateConfigVb.getReportingDate() };
			return  getJdbcTemplate().query(query, args, getMapper());
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return null;
		}
	}
	
	protected RowMapper getMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateConfigVb templateConfigVb = new TemplateConfigVb();
				templateConfigVb.setCountry(rs.getString("COUNTRY"));
				templateConfigVb.setLeBook(rs.getString("LE_BOOK"));
				templateConfigVb.setTemplateId(rs.getString("TEMPLATE_ID"));
				templateConfigVb.setReportingDate(rs.getString("REPORTING_DATE"));
				templateConfigVb.setSourceTable(rs.getString("SOURCE_TABLE"));
				templateConfigVb.setSourceType(rs.getString("SOURCE_TYPE"));
				templateConfigVb.setTypeOfSubmission(rs.getString("TYPE_OF_SUBMISSION"));
				templateConfigVb.setCsvDelimiter(rs.getString("CSV_DELIMITER"));
				templateConfigVb.setTemplateDescription(rs.getString("TEMPLATE_DESCRIPTION"));
				templateConfigVb.setDataList(rs.getString("DATALIST"));
				return templateConfigVb;
			}
		};
		return mapper;
	}
	protected RowMapper getMapper1(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
				templateScheduleVb.setCountry(rs.getString("COUNTRY"));
				templateScheduleVb.setLeBook(rs.getString("LE_BOOK"));
				templateScheduleVb.setTemplateId(rs.getString("TEMPLATE_ID"));
				templateScheduleVb.setReportingDate(rs.getString("REPORTING_DATE"));
				return templateScheduleVb;
			}
		};
		return mapper;
	}
	public ExceptionCode jsonFileWriter(String json, String fileName) {
		ExceptionCode exceptionCode = new ExceptionCode();
		byte[] byteArray = json.getBytes();
		try (FileOutputStream outputStream = new FileOutputStream(fileName);
				InputStream inputStream = new ByteArrayInputStream(byteArray)) {
			int data = 0;
			while ((data = inputStream.read()) != -1) {
				outputStream.write(data);
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("");
		} catch (IOException e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}

		return exceptionCode;
	}

	public ExceptionCode csvFileWriter(ArrayList lst, String fileName, String csvDelimiter) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<Map<String, String>> list = lst;
		// With delimiter
		String delimiter = "";

		if (csvDelimiter.equalsIgnoreCase("|")) {
			String csvStringWithDelimiter = formCsvFromArrayListMap(list, "|");
			exceptionCode = writeCsvToFile(csvStringWithDelimiter, fileName);
//			System.out.println("CSV data (with delimiter) has been successfully written to " + fileName);

		} else {
			// Without delimiter
			String csvStringWithoutDelimiter = formCsvFromArrayListMap(list, ",");
			exceptionCode = writeCsvToFile(csvStringWithoutDelimiter, fileName);
//			System.out.println("CSV data (without delimiter) has been successfully written to " + fileName);
		}
		return exceptionCode;
	}

	public static String formCsvFromArrayListMap(List<Map<String, String>> list, String delimiter) {
		StringJoiner csvJoiner = new StringJoiner("\n");
		// Header (assuming all maps have the same keys)
		if (!list.isEmpty()) {
			Map<String, String> firstMap = list.get(0);
			String header = formCsvWithDelimiter(firstMap.keySet(), delimiter);
			csvJoiner.add(header);
		}
		// Data
		for (Map<String, String> map : list) {
			String dataRow = formCsvWithDelimiter(map.values(), delimiter);
			csvJoiner.add(dataRow);
		}

		return csvJoiner.toString();
	}

	public static String formCsvWithDelimiter(Iterable<String> values, String delimiter) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (String value : values) {
			joiner.add(escapeCsvValue(value));
		}
		return joiner.toString();
	}

	private static String escapeCsvValue(String value) {
		 if (value == null) {
		        return "";  // Return an empty string or handle it based on your needs
		    }
		if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		} else {
			return value;
		}
	}

	public static ExceptionCode writeCsvToFile(String csvContent, String fileName) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try (FileWriter writer = new FileWriter(fileName)) {
			writer.write(csvContent);
		} catch (IOException e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("File Created !!");
		return exceptionCode;
	}
	public ExceptionCode writeXmlToFile(String xmlData, String fileName) {
	    ExceptionCode exceptionCode = new ExceptionCode();
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        writer.write(xmlData);
	        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	    } catch (IOException e) {
	        exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	        exceptionCode.setErrorMsg(e.getMessage());
	        exceptionCode.setErrorSevr("N");
	    }
	    return exceptionCode;
	}

	public int updateProcessControl(String processStatus, TemplateScheduleVb dObj, boolean flag) {
	    setServiceDefaults();
	    StringBuilder query = new StringBuilder();
	    query.append("UPDATE RG_PROCESS_CONTROLS SET RG_PROCESS_STATUS = ?, RECORD_INDICATOR = ?");

	    List<Object> args = new ArrayList<>();
	    args.add(processStatus);
	    args.add(dObj.getRecordIndicator());

	    if (flag) {
	        if ("SI".equalsIgnoreCase(processStatus)) {
	            query.append(", PROCESS_START_TIME = ").append(getDbFunction("SYSDATE"));
	        } else {
	            query.append(", PROCESS_END_TIME = ").append(getDbFunction("SYSDATE"));
	        }
	    }

	    if (!"VE".equalsIgnoreCase(processStatus)) {
	        query.append(", MAKER = ?");
	        args.add(intCurrentUserId);
	    }

	    query.append(" WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND REPORTING_DATE = "+dateConvert+" AND SUBMISSION_DATE = "+dateConvert);
	    args.add(dObj.getCountry());
	    args.add(dObj.getLeBook());
	    args.add(dObj.getTemplateId());
	    args.add(dObj.getReportingDate());
	    args.add(dObj.getSubmissionDate());
try {
	    return getJdbcTemplate().update(query.toString(), args.toArray());
}catch(Exception e) {
	e.printStackTrace();
	return 0;
}
	}
	public int updateProcessControl1(String processStatus, TemplateScheduleVb dObj, boolean flag) {
	    StringBuilder query = new StringBuilder();
	    System.out.println(1);
	    
	    query.append("UPDATE RG_PROCESS_CONTROLS SET RG_PROCESS_STATUS = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED = "+systemDate);

	    List<Object> args = new ArrayList<>();
	    args.add(processStatus);
	    args.add(dObj.getRecordIndicator());

	            query.append(" , PROCESS_END_TIME = ").append(getDbFunction("SYSDATE"));


	    query.append(" WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND REPORTING_DATE = "+dateConvert+" AND SUBMISSION_DATE = "+dateConvert);
	    args.add(dObj.getCountry());
	    args.add(dObj.getLeBook());
	    args.add(dObj.getTemplateId());
	    args.add(dObj.getReportingDate());
	    args.add(dObj.getSubmissionDate());
try { System.out.println(2);
	    return getJdbcTemplate().update(query.toString(), args.toArray());
}catch(Exception e) {
	e.printStackTrace();
	return 0;
}
	}
	public int updateTemplatesHeader(String processStatus, TemplateScheduleVb dObj) {
	    setServiceDefaults();
	    StringBuilder query = new StringBuilder();
	    query.append("UPDATE RG_TEMPLATES_HEADER SET STATUS = ?, RECORD_INDICATOR = ?,DATE_LAST_MODIFIED="+systemDate);

	    List<Object> args = new ArrayList<>();
	    args.add(processStatus);
	    args.add(dObj.getRecordIndicator());

	    if (!"VE".equalsIgnoreCase(processStatus)) {
	        query.append(", SUBMITTER = ?");
	        args.add(intCurrentUserId);
	    }

	    query.append(" WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND REPORTING_DATE = "+dateConvert+" AND SUBMISSION_DATE = "+dateConvert);
	    args.add(dObj.getCountry());
	    args.add(dObj.getLeBook());
	    args.add(dObj.getTemplateId());
	    args.add(dObj.getReportingDate());
	    args.add(dObj.getSubmissionDate());

	    try {
		    return getJdbcTemplate().update(query.toString(), args.toArray());
	}catch(Exception e) {
		e.printStackTrace();
		return 0;
	}
	}

	public int updateSchedules(String processStatus, String country, String leBook, String templateName,
			String processId) {
		String dateUpdate = " ";
		if ("I".equalsIgnoreCase(processStatus)) {
			dateUpdate = " START_TIME= " + getDbFunction("SYSDATE") + " ";
		} else {
			dateUpdate = " END_TIME = " + getDbFunction("SYSDATE") + " ";
		}
		String query = "";
		query = " UPDATE RG_SCHEDULES " + " SET  " + dateUpdate + " WHERE EXISTS ( " + "     SELECT 1 "
				+ "     FROM RG_PROCESS_CONTROL "
				+ "     WHERE RG_SCHEDULES.RG_PROCESS_ID = RG_PROCESS_CONTROL.RG_PROCESS_ID    "
				+ "       AND RG_PROCESS_CONTROL.COUNTRY = RG_SCHEDULES.COUNTRY "
				+ "       AND RG_PROCESS_CONTROL.LE_BOOK = RG_SCHEDULES.LE_BOOK "
				+ "       AND RG_PROCESS_CONTROL.REPORTING_DATE = RG_SCHEDULES.REPORTING_DATE "
				+ "       AND RG_PROCESS_CONTROL.COUNTRY = ? " + "       AND RG_PROCESS_CONTROL.LE_BOOK = ? "
				+ "       AND RG_PROCESS_CONTROL.TEMPLATE_NAME = ? " + " 	     AND RG_SCHEDULES.RG_PROCESS_ID = ?) ";
		Object[] args = { country, leBook, templateName, processId };
		return getJdbcTemplate().update(query, args);
	}
	
	public String reportDate() {
		String date="";
		String query ="";
		
		if("ORACLE".equalsIgnoreCase(databaseType)) {
			query ="SELECT TO_CHAR(SYSDATE,'RRRR-MM-DD') FROM DUAL";
		}else if("MSSQL".equalsIgnoreCase(databaseType)) {
			query ="SELECT FORMAT(GETDATE(), 'yyyy-MM-dd')";
		}
		 date = getJdbcTemplate().queryForObject(query, String.class);
		return date;
	}
	
	public String getDbFunction(String reqFunction) {
		String functionName = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {
			case "DATEFUNC":
				functionName = "FORMAT";
				break;
			case "SYSDATE":
				functionName = "GetDate()";
				break;
			case "NVL":
				functionName = "ISNULL";
				break;
			case "TIME":
				functionName = "HH:mm:ss";
				break;
			case "DATEFORMAT":
				functionName = "dd-MMM-yyyy";
				break;
			case "CONVERT":
				functionName = "CONVERT";
				break;
			case "TYPE":
				functionName = "varchar,";
				break;
			case "TIMEFORMAT":
				functionName = "108";
				break;
			case "PIPELINE":
				functionName = "+";	
				break;
			case "YEAR_FORMAT":
				functionName = "yyyy";
				break;
			case "MONTHYEAR_FORMAT":
				functionName = "MMM-yyyy";
				break;
			case "DATETIME_FORMAT":
				functionName = "dd-MMM-yyyy HH:mm:ss";
				break;
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(reqFunction) {
			case "DATEFUNC":
				functionName = "TO_CHAR";
				break;
			case "SYSDATE":
				functionName = "SYSDATE";
				break;
			case "NVL":
				functionName = "NVL";
				break;
			case "TIME":
				functionName = "HH24:MI:SS";
				break;
			case "DATEFORMAT":
				functionName = "DD-Mon-RRRR";
				break;
			case "CONVERT":
				functionName = "TO_CHAR";
				break;
			case "TYPE":
				functionName = "";
				break;
			case "TIMEFORMAT":
				functionName = "HH:MM:SS";
				break;
			case "PIPELINE":
				functionName = "||";
				break;
			case "YEAR_FORMAT":
				functionName = "RRRR";
				break;
			case "MONTHYEAR_FORMAT":
				functionName = "Mon-YYYY";
				break;
			case "DATETIME_FORMAT":
				functionName = "DD-Mon-RRRR HH24:MI:SS";
				break;
			}
		}
		
		return functionName;
	}
	
	public String findVisionVariableValue(String pVariableName) throws DataAccessException {
		if(!ValidationUtil.isValid(pVariableName)){
			return null;
		}
		String sql = "select VALUE FROM VISION_VARIABLES where UPPER(VARIABLE) = UPPER(?)";
		Object[] lParams = new Object[1];
		lParams[0] = pVariableName;
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				CommonVb commonVb = new CommonVb();
				commonVb.setMakerName(rs.getString("VALUE"));
				return commonVb;
			}
		};
		List<CommonVb> commonVbs = getJdbcTemplate().query(sql, lParams, mapper);
		if(commonVbs != null && !commonVbs.isEmpty()){
			return commonVbs.get(0).getMakerName();
		}
		return null;
	}
	
	public List<TemplateConfigVb> getQueryResults(TemplateConfigVb dObj) {
		List<TemplateConfigVb> collTemp = null;
		String strQueryAppr = null;
		String strQueryPend = null;
		strQueryAppr = new String(" SELECT TAPPR.COUNTRY,                        "
				+ "        TAPPR.LE_BOOK,TAPPR.TEMPLATE_ID, "
				+ "        TAPPR.TEMPLATE_DESCRIPTION,TAPPR.PROCESS_FREQUENCY_AT," + "        TAPPR.PROCESS_FREQUENCY,"
				+ "        TAPPR.TYPE_OF_SUBMISSION_AT," + "        TAPPR.TYPE_OF_SUBMISSION,TAPPR.CSV_DELIMITER_AT,"
				+ "        TAPPR.CSV_DELIMITER,TAPPR.SOURCE_TYPE_AT,"
				+ "        TAPPR.SOURCE_TYPE,TAPPR.DB_CONNECTIVITY_TYPE,"
				+ "        TAPPR.DB_CONNECTIVITY_DETAILS,TAPPR.SOURCE_TABLE,"
				+ "        TAPPR.SOURCE_TABLE_FILTER,TAPPR.SOURCE_TABLE_READINESS,"
				+ "        TAPPR.UPLOAD_FILE_NAME,TAPPR.API_CONNECTIVITY_TYPE,"
				+ "        TAPPR.API_CONNECTIVITY_DETAILS,TAPPR.AUTH_CONNECTIVITY_TYPE,"
				+ "        TAPPR.AUTH_CONNECTIVITY_DETAILS,TAPPR.MAIN_API_STRUCTURE,"
				+ "        TAPPR.TEMPLATE_STATUS,"
				+ "        TAPPR.RECORD_INDICATOR_NT,"
				+ "        TAPPR.RECORD_INDICATOR,"
				+ "        TAPPR.RECORD_INDICATOR,TAPPR.MAKER," + "        TAPPR.VERIFIER," + " "
				+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null) + " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION,TAPPR.DATALIST ,TAPPR.CBK_FILE_NAME"
				+ " FROM RG_TEMPLATE_CONFIG TAPPR WHERE TAPPR.COUNTRY =? AND TAPPR.LE_BOOK = ? AND  TAPPR.TEMPLATE_ID = ?");
		Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId() };

		try {
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), args, getDetailMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	protected RowMapper getDetailMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateConfigVb vObject = new TemplateConfigVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setTemplateDescription(rs.getString("TEMPLATE_DESCRIPTION"));
				vObject.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
//				vObject.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
				vObject.setTypeOfSubmission(rs.getString("TYPE_OF_SUBMISSION"));
				vObject.setCsvDelimiter(rs.getString("CSV_DELIMITER"));
				vObject.setSourceType(rs.getString("SOURCE_TYPE"));
				vObject.setDbConnectivityType(rs.getString("DB_CONNECTIVITY_TYPE"));
				vObject.setDbConnectivityDetails(rs.getString("DB_CONNECTIVITY_DETAILS"));
				vObject.setSourceTable(rs.getString("SOURCE_TABLE"));
				vObject.setSourceTableFilter(rs.getString("SOURCE_TABLE_FILTER"));
				vObject.setSoureTableReadiness(rs.getString("SOURCE_TABLE_READINESS"));
				vObject.setUploadFileName(rs.getString("UPLOAD_FILE_NAME"));
				vObject.setApiConnectivityType(rs.getString("API_CONNECTIVITY_TYPE"));
				vObject.setApiConnectivityDetails(rs.getString("API_CONNECTIVITY_DETAILS"));
				vObject.setAuthConnectivityType(rs.getString("AUTH_CONNECTIVITY_TYPE"));
				vObject.setAuthConnectivityDetails(rs.getString("AUTH_CONNECTIVITY_DETAILS"));
				vObject.setMainAPIStructure(rs.getString("MAIN_API_STRUCTURE"));
				vObject.setTemplateStatus(rs.getInt("TEMPLATE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDataList(rs.getString("DATALIST"));
				vObject.setCbkFileName(rs.getString("CBK_FILE_NAME"));
				return vObject;
			}
		};
		return mapper;
	}
	
	public List<TemplateMappingVb> getQueryMappingList(TemplateMappingVb dObj) {
		StringBuffer strBufApprove = null;
		String orderBy = "";
		strBufApprove = new StringBuffer("SELECT"+
						" TAPPR.COUNTRY,"+
						"TAPPR.LE_BOOK,"+
						"TAPPR.TEMPLATE_ID,"+
						"TAPPR.SOURCE_COLUMN,"+
						"TAPPR.TARGET_COLUMN,"+
						"TAPPR.TEMPLATE_STATUS,"+
						"TAPPR.RECORD_INDICATOR,"+
						"TAPPR.MAKER,"+
//						" (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.MAKER,0) ) MAKER_NAME,"+
						"TAPPR.VERIFIER,"+
//						"(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "+getDbFunction("NVL")+"(TAppr.VERIFIER,0) ) VERIFIER_NAME,"+
						dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED","DATETIME_FORMAT",null)+" DATE_LAST_MODIFIED,"+
						dbFunctionFormats("TAPPR.DATE_CREATION","DATETIME_FORMAT",null)+" DATE_CREATION,"+
						"TAPPR.SEQUENCE_NO "+
//						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =1 AND NUM_SUB_TAB =TAPPR.TEMPLATE_STATUS )MAPPING_STATUS_DESC,"+
//						 " (SELECT NUM_SUBTAB_DESCRIPTION FROM NUM_SUB_TAB WHERE NUM_TAB =7 AND NUM_SUB_TAB =TAPPR.RECORD_INDICATOR )RECORD_INDICATOR_DESC"+
						" FROM RG_TEMPLATE_MAPPING TAPPR WHERE TAPPR.COUNTRY =? AND TAPPR.LE_BOOK = ? AND  TAPPR.TEMPLATE_ID = ? ");

		try {
			Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId() };
			orderBy = " Order by SEQUENCE_NO ";
			strBufApprove.append(orderBy);
			return getJdbcTemplate().query(strBufApprove.toString(), args, getTemplateMppingMapper());
//			return getQueryPopupResults(dObj, strBufPending, strBufApprove, strWhereNotExists, orderBy, params,getTemplateMppingMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	protected RowMapper getTemplateMppingMapper(){
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateMappingVb vObject = new TemplateMappingVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setSourceColumn(rs.getString("SOURCE_COLUMN"));
				vObject.setTargetColumn(rs.getString("TARGET_COLUMN"));
				vObject.setMappingStatus(rs.getInt("TEMPLATE_STATUS"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setSequenceNo(rs.getInt("SEQUENCE_NO"));
//				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setMaker(rs.getInt("MAKER"));
//				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
//				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
//				vObject.setMappingStatusDesc(rs.getString("MAPPING_STATUS_DESC"));
				return vObject;
			}
		};
		return mapper;
	}

	public String dbFunctionFormats(String columnName,String formatReq,String dec) {
		String returnStr = "";
		if("MSSQL".equalsIgnoreCase(databaseType)) {
			switch(formatReq) {

				case "NUM_FORMAT":
					returnStr = "RTRIM(LTRIM(FORMAT("+columnName+",'N"+dec+"')))";
					break;
					
				case "DATETIME_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy HH:mm:ss')";
					break;
				
				case "DATE_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy')";
					break;	
				case "TO_DATE_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MM-yyyy')";
					break;
				case "TO_DATETIME_FORMAT":
					returnStr = "Format("+columnName+", 'dd-MMM-yyyy HH:mm:ss')";
					break;
				case "YEAR_FORMAT":
					returnStr = "Format("+columnName+", 'yyyy-MM-dd')";
					break;
				
			}
		}else if("ORACLE".equalsIgnoreCase(databaseType)) {
			switch(formatReq) {
				case "NUM_FORMAT":
					returnStr = "TRIM(TO_CHAR("+columnName+",'999,999,999,999,990.99990'))";
					break;
					
				case "DATETIME_FORMAT":
					returnStr = "TO_CHAR("+columnName+",'DD-Mon-RRRR HH24:MI:SS')";
					break;
					
				case "DATE_FORMAT":
					returnStr = "TO_CHAR("+columnName+",'DD-Mon-RRRR')";
					break;
				case "TO_DATE_FORMAT":
					returnStr = "TO_DATE("+columnName+",'DD-MM-RRRR HH24:MI:SS')";
					break;	
				case "TO_DATETIME_FORMAT":
					returnStr = "TO_DATE("+columnName+",'DD-Mon-RRRR HH24:MI:SS')";
					break;
				case "YEAR_FORMAT":
					returnStr = "TO_CHAR("+columnName+", 'yyyy-MM-dd')";
					break;
			}
		}
		return returnStr;
	}
	public String getScript(TemplateConfigVb vObject) {
		try {
			if (ValidationUtil.isValid(vObject.getDbConnectivityDetails())) {
				String query = "SELECT VARIABLE_SCRIPT FROM VISION_DYNAMIC_HASH_VAR WHERE VARIABLE_NAME = ?";
				Object objParams[] = { vObject.getDbConnectivityDetails() };
				return getJdbcTemplate().queryForObject(query.toString(), objParams, String.class);
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}	
	
	public int insertAuditTrailSubmitData(TemplateConfigVb vObject, int auditSequence,String description, String detailDesc ){
		String query = "";
		
			query = "INSERT INTO RG_SUBMIT_AUDIT_TRAIL (COUNTRY, LE_BOOK, REPORTING_DATE,submission_date, TEMPLATE_ID, AUDIT_TRAIL_SEQUENCE_ID, "
					+ "DATETIME_STAMP, AUDIT_DESCRIPTION, AUDIT_DESCRIPTION_DETAIL) VALUES ( ?,?,"+dateConvert+" , "+dateConvert+",?, ?,"+getDbFunction("SYSDATE")+", ?,?)"; 
			Object objParams[] = {vObject.getCountry(),vObject.getLeBook(),vObject.getReportingDate(),vObject.getSubmissionDate(),vObject.getTemplateId()
					,auditSequence,description,detailDesc};
		try {
			getJdbcTemplate().update(query,objParams);
			return Constants.SUCCESSFUL_OPERATION;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERRONEOUS_OPERATION;
		}
	}
	public int getMaxSequence(TemplateConfigVb vObject){
		int count = 0;
		String strQuery = "";
		
			strQuery = new String("SELECT "+nullFun+"(MAX(AUDIT_TRAIL_SEQUENCE_ID), 0) MAX_COUNT FROM RG_SUBMIT_AUDIT_TRAIL WHERE"
					+ " COUNTRY = ? AND LE_BOOK = ?"
					+ " AND REPORTING_DATE = "+dateConvert
					+ " AND TEMPLATE_ID = ?");
			Object objParams[] = {vObject.getCountry(),vObject.getLeBook(),vObject.getReportingDate(),vObject.getTemplateId()};
		try
		{
			return getJdbcTemplate().queryForObject(strQuery,objParams, Integer.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return count;
		}
	}
	public synchronized int insertAuditTrialData(TemplateConfigVb vObject, String description, String detailDesc){
		int auditSequence = getMaxSequence(vObject);
		 auditSequence++;
		int intValue = insertAuditTrailSubmitData(vObject,auditSequence,description,detailDesc);
		return intValue;
	}
	
	public String getRequestId(TemplateConfigVb vObject){
		String requestId= "";
		 StringBuilder query = new StringBuilder();
		    query.append("SELECT REQUEST_NO FROM RG_TEMPLATES_HEADER ");
		    query.append("WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND REPORTING_DATE = ? AND SUBMISSION_DATE = ?");
		    List<Object> args = new ArrayList<>();
		    args.add(vObject.getCountry());
		    args.add(vObject.getLeBook());
		    args.add(vObject.getTemplateId());
		    args.add(vObject.getReportingDate());
		    args.add(vObject.getSubmissionDate());
		    requestId=getJdbcTemplate().queryForObject(query.toString(),  String.class,args.toArray());
		return requestId;
	}

	
	public int updateProcessControlInternalStatus(int internalStatus, TemplateScheduleVb dObj) {
	    StringBuilder query = new StringBuilder();
	    query.append("UPDATE RG_PROCESS_CONTROLS SET INTERNAL_STATUS = ? ");
	    query.append("WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND REPORTING_DATE = ? AND SUBMISSION_DATE = ?");

	    List<Object> args = new ArrayList<>();
	    args.add(internalStatus);
	    args.add(dObj.getCountry());
	    args.add(dObj.getLeBook());
	    args.add(dObj.getTemplateId());
	    args.add(dObj.getReportingDate());
	    args.add(dObj.getSubmissionDate());

	    return getJdbcTemplate().update(query.toString(), args.toArray());
	}
	public int updateTemplatesHeaderInternalStatus(int internalStatus, TemplateScheduleVb dObj) {
	    StringBuilder query = new StringBuilder();
	    query.append("UPDATE RG_TEMPLATES_HEADER SET INTERNAL_STATUS = ? ");
	    query.append("WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND REPORTING_DATE = ? AND SUBMISSION_DATE = ?");

	    List<Object> args = new ArrayList<>();
	    args.add(internalStatus);
	    args.add(dObj.getCountry());
	    args.add(dObj.getLeBook());
	    args.add(dObj.getTemplateId());
	    args.add(dObj.getReportingDate());
	    args.add(dObj.getSubmissionDate());

	    return getJdbcTemplate().update(query.toString(), args.toArray());
	}

}
