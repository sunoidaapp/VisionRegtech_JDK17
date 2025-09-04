package com.vision.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;

import jcifs.util.Base64;

@Component
public class CommonUtils {

	private static String strErrMsgs[] = new String[50];
	private static String strSeverityFlags[] = new String[50];
	private static String SPACE = " ";
	private static final SecureRandom random = new SecureRandom();

	static {
		strErrMsgs[0] = new String("System error.  Contact System Admin");
		strErrMsgs[1] = new String("Successful !!");
		strErrMsgs[2] = new String("Invalid Status flag in database table");
		strErrMsgs[3] = new String("Duplicate key insertion attempted");
		strErrMsgs[4] = new String("Record pending for deletion. Can't update now");
		strErrMsgs[5] = new String("Attempt to modify a non-existent record");
		strErrMsgs[6] = new String("Attempt to delete a non-existent record");
		strErrMsgs[7] = new String("Attempt to delete record waiting for approval");
		strErrMsgs[8] = new String("No such pending record exists !!");
		strErrMsgs[9] = new String("Record is already pending for add approve.  Can't add again.");
		strErrMsgs[10] = new String("Record already present in approved but in Inactive/Delete  state");
		strErrMsgs[11] = new String("Cannot delete an inactive/delete record");
		strErrMsgs[12] = new String("No valid records to approve");
		strErrMsgs[13] = new String("No valid records to reject");
		strErrMsgs[14] = new String("Maker cannot approve pending records");
		strErrMsgs[15] = new String("Maker cannot reject pending records");
		strErrMsgs[16] = new String("Record might be Approved / Deleted");
		strErrMsgs[17] = new String("Cannot delete an unauthorised records");
		strErrMsgs[18] = new String("Cannot modify an record to delete state");
		strErrMsgs[19] = new String("FX Rate is not Maintained for the Month/Year");
		strErrMsgs[21] = new String("Cannot delete while balances are maintained");
		strErrMsgs[22] = new String("Cannot authorise without balance records");
		strErrMsgs[23] = new String("No Operation Performed");
		strErrMsgs[24] = new String("Maker cannot approve pending child records");
		strErrMsgs[25] = new String("Validation Finished - No Errors ");
//		strErrMsgs[26] = new String("Validation Finished - Errors Found ");
		strErrMsgs[26] = new String("Errors Found ");
		strErrMsgs[27] = new String("Trying to Modify an Approved Record which has been Modified by another User");
		strErrMsgs[28] = new String("Cannot Authorise. Balance Amounts Exceed Tolerance Amount");
		strErrMsgs[29] = new String("Cannot Authorise. Balance exceeds Tolerance(0)");
		strErrMsgs[30] = new String("Warning : Audit Trail Operation is not completed");
		strErrMsgs[31] = new String("Cannot Authorise. EOP Balance Amounts Exceed Tolerance Amount");
		strErrMsgs[32] = new String("Cannot Authorise. Average Balance Amounts Exceed Tolerance Amount");
		strErrMsgs[33] = new String("Cannot Authorise. P&L Balance Amounts Exceed Tolerance Amount");
		strErrMsgs[34] = new String("Cannot Authorise. Pool Balance Amounts Exceed Tolerance Amount");
		strErrMsgs[35] = new String("Allocations Data for Current Year cannot be deleted");
		strErrMsgs[37] = new String("No data found in Build Controls data table ");
		strErrMsgs[38] = new String("Cannot Add, Modify or Delete the record. Build Module is Running");
		strErrMsgs[39] = new String("Cannot Approve the record. Build Module is Running");
		strErrMsgs[40] = new String(".txt file is not found");
		strErrMsgs[41] = new String("Sorry - No Records Found!");
		strErrMsgs[42] = new String("No such Unauthorized record exists !!");
		strErrMsgs[43] = new String("Attempt to delete record waiting for authorize");
		strErrMsgs[44] = new String("Posted record can not be deleted");
		strErrMsgs[45] = new String("No data found in Approved table");
		strErrMsgs[46] = new String("Record does not exist or might be deleted");
		strErrMsgs[47] = new String(
				"Unable to read/write file from/to the server.\n OR \nRemote service un-available.");
		strErrMsgs[48] = new String("Invalid Error Code or Error Code not mapped in the system.");
		strErrMsgs[49] = new String("Year is not maintain in Period controls table.");

		strSeverityFlags[0] = new String("F");
		strSeverityFlags[1] = new String("");
		strSeverityFlags[2] = new String("E");
		strSeverityFlags[3] = new String("E");
		strSeverityFlags[4] = new String("E");
		strSeverityFlags[5] = new String("E");
		strSeverityFlags[6] = new String("E");
		strSeverityFlags[7] = new String("E");
		strSeverityFlags[8] = new String("E");
		strSeverityFlags[9] = new String("E");
		strSeverityFlags[10] = new String("E");
		strSeverityFlags[11] = new String("E");
		strSeverityFlags[12] = new String("E");
		strSeverityFlags[13] = new String("E");
		strSeverityFlags[14] = new String("E");
		strSeverityFlags[15] = new String("E");
		strSeverityFlags[16] = new String("F");
		strSeverityFlags[17] = new String("F");
		strSeverityFlags[18] = new String("F");
		strSeverityFlags[19] = new String("E");
		strSeverityFlags[20] = new String("E");
		strSeverityFlags[21] = new String("E");
		strSeverityFlags[22] = new String("E");
		strSeverityFlags[23] = new String("E");
		strSeverityFlags[24] = new String("E");
		strSeverityFlags[25] = new String("");
		strSeverityFlags[26] = new String("");
		strSeverityFlags[27] = new String("F");
		strSeverityFlags[28] = new String("F");
		strSeverityFlags[29] = new String("F");
		strSeverityFlags[30] = new String("W");
		strSeverityFlags[31] = new String("F");
		strSeverityFlags[32] = new String("F");
		strSeverityFlags[33] = new String("F");
		strSeverityFlags[34] = new String("F");
		strSeverityFlags[35] = new String("F");
		strSeverityFlags[36] = new String("W");
		strSeverityFlags[38] = new String("E");
		strSeverityFlags[39] = new String("E");
		strSeverityFlags[40] = new String("E");
		strSeverityFlags[41] = new String("W");
		strSeverityFlags[42] = new String("E");
		strSeverityFlags[43] = new String("E");
		strSeverityFlags[44] = new String("E");
		strSeverityFlags[45] = new String("F");
		strSeverityFlags[46] = new String("F");
		strSeverityFlags[48] = new String("F");
		strSeverityFlags[49] = new String("F");
	}

	// Function used by the getQueryResults function of the Query Object class for
	// each service
	// public static void addToQuery(String strFld, StringBuffer strBuf, String
	// strType, String strStrBufType)
	public static void addToQuery(String strFld, StringBuffer strBuf) {
		if (strBuf.toString().endsWith(") ") == true || strBuf.toString().endsWith("?") == true
				|| strBuf.toString().endsWith("IS NULL") == true || strBuf.toString().endsWith(")  ") == true
				|| strBuf.toString().endsWith("'") == true || strBuf.toString().endsWith(")"))
			strBuf.append(" AND " + strFld);
		else
			strBuf.append(" Where " + strFld);
	}

	public static String getFixedLength(String strValue, String strFillValue, int intLength) {
		try {
			if (strValue == null)
				strValue = "";

			while (strValue.length() < intLength) {
				if (strFillValue.equals("0"))
					strValue = strFillValue + strValue;
				else
					strValue += strFillValue;
			}
			return strValue;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Converts the Screen Format Date to DB Format
	 * 
	 * @param String (Screen Format Date)
	 * @return
	 */
	public static String getTableFormatDate(String strDate) {
		if (strDate == null)
			strDate = "";
		else
			strDate = strDate.trim();

		if (strDate.length() > 8 && strDate.indexOf("/") != -1) {
			String strRetDt = strDate.substring(3, 5) + "/" + strDate.substring(0, 2) + "/" + strDate.substring(6);
			return strRetDt;
		} else
			return strDate;

	}

	/**
	 * 
	 * @return String (Unique Reference number for the Audit Entry)
	 */
	public static String getReferenceNo() {

		try {
			String strDay = "";
			String strMonth = "";
			String strYear = "";
			String strHour = "";
			String strMin = "";
			String strSec = "";
			String strMSec = "";
			String strAMPM = "";

			Calendar c = Calendar.getInstance();
			strMonth = c.get(Calendar.MONTH) + 1 + "";
			strDay = c.get(Calendar.DATE) + "";
			strYear = c.get(Calendar.YEAR) + "";
			strAMPM = c.get(Calendar.AM_PM) + "";
			strMin = c.get(Calendar.MINUTE) + "";
			strSec = c.get(Calendar.SECOND) + "";
			strMSec = c.get(Calendar.MILLISECOND) + "";

			if (strAMPM.equals("1"))
				strHour = (c.get(Calendar.HOUR) + 12) + "";
			else
				strHour = c.get(Calendar.HOUR) + "";

			if (strHour.length() == 1)
				strHour = "0" + strHour;

			if (strMin.length() == 1)
				strMin = "0" + strMin;

			if (strSec.length() == 1)
				strSec = "0" + strSec;

			if (strMSec.length() == 1)
				strMSec = "00" + strMSec;
			else if (strMSec.length() == 2)
				strMSec = "0" + strMSec;

			if (strMonth.length() == 1)
				strMonth = "0" + strMonth;

			if (strDay.length() == 1)
				strDay = "0" + strDay;

			return strYear + strMonth + strDay + strHour + strMin + strSec + strMSec;
		} catch (Exception e) {
			return "";
		}
	}

	// Function used by all business objects to generate the result object for an
	// operation
	public static ExceptionCode getResultObject(String strServiceName, int intErrorId, String strOperation,
			String strErrMsg) {
		String strTemp = new String("");
		ResourceBundle rsb = getResourceManger();
		ExceptionCode rObj = new ExceptionCode();
		if (!ValidationUtil.isValid(strOperation)) {
			strTemp = strServiceName + " - " + strErrMsgs[intErrorId];
			rObj.setErrorSevr(strSeverityFlags[intErrorId]);
		} else {
			strTemp = strServiceName + " - " + getErrorMsg(strOperation + " ", rsb) + " - ";
			if (intErrorId == Constants.SUCCESSFUL_OPERATION) {
				strTemp = strTemp + getErrorMsg("Successful ", rsb);
				rObj.setErrorSevr("");
			} else if (intErrorId == Constants.VALIDATION_ERRORS_FOUND) {
				strTemp = strTemp + getErrorMsg("Successful", rsb) + " - " + strErrMsgs[intErrorId];
				rObj.setErrorSevr("");
			} else if (intErrorId == Constants.VALIDATION_NO_ERRORS) {
				strTemp = strTemp + getErrorMsg("Successful", rsb) + " - " + strErrMsgs[intErrorId];
				rObj.setErrorSevr("");
			} else if (intErrorId == Constants.AUDIT_TRAIL_ERROR) {
				strTemp = strTemp + getErrorMsg("Successful", rsb) + " - " + strErrMsgs[intErrorId];
				rObj.setErrorSevr("");
			} else if (intErrorId == Constants.NO_RECORDS_FOUND) {
				strTemp = getErrorMsg(strErrMsgs[intErrorId], rsb);
				rObj.setErrorSevr("");
			} else if (intErrorId == Constants.WE_HAVE_WARNING_DESCRIPTION) {
				if (strErrMsg == null)
					strTemp = strTemp + getErrorMsg("Successful", rsb);
				else
					strTemp = strTemp + getErrorMsg("Successful", rsb) + " - " + strErrMsg;

				rObj.setErrorSevr("");
			} else {
				strTemp = strTemp + getErrorMsg("Failed", rsb) + " - ";
				if (intErrorId != Constants.WE_HAVE_ERROR_DESCRIPTION) {
					if (strErrMsg != null && strErrMsg.length() > 0) {
						strTemp = strTemp + getErrorMsg(strErrMsgs[intErrorId], rsb) + " - " + strErrMsg;

					} else {
						strTemp = strTemp + getErrorMsg(strErrMsgs[intErrorId], rsb);
					}
					rObj.setErrorSevr(strSeverityFlags[intErrorId]);
				} else {
//					strTemp = strTemp + strErrMsg + " - " + strServiceId;
					if (strErrMsg != null && strErrMsg.startsWith(strTemp)) {
						strTemp = strErrMsg;
					} else {
						strTemp = strTemp + strErrMsg;
					}
					rObj.setErrorSevr("W");
				}
			}

		}
		// Set the error code and display message
		rObj.setErrorCode(intErrorId);
		rObj.setErrorMsg(strTemp);
		return rObj;
	}

	public static String Round(String returnValue, int roundToPlaces) {
		if (returnValue == null || returnValue.isEmpty()) {
			returnValue = "0";
		}
		StringBuffer format = new StringBuffer("##,##0.0");
		if (roundToPlaces > 1) {
			for (int i = 1; i < roundToPlaces; i++) {
				format.append("0");
			}
		}
		DecimalFormat decimalFormat = new DecimalFormat(format.toString());
		return decimalFormat.format(Double.valueOf(returnValue));
	}

	public static ResourceBundle getResourceManger() {
		ResourceBundle rsb;
		if (SessionContextHolder.getContext() != null) {
			String language = SessionContextHolder.getContext().getLocale();
			if (language == null || language == "")
				language = "en_US";
			String country = language.substring(0, language.indexOf('_'));
			String lang = language.substring(language.indexOf('_') + 1, language.length());
//			rsb = ResourceBundle.getBundle("locale/message", new Locale(country,lang));
			rsb = ResourceBundle.getBundle("message", new Locale(language.substring(0, language.indexOf('_')),
					language.substring(language.indexOf('_') + 1, language.length())));
		} else {
			String language = "en_US";
			rsb = ResourceBundle.getBundle("message", new Locale(language.substring(0, language.indexOf('_')),
					language.substring(language.indexOf('_') + 1, language.length())));
		}
		return rsb;
	}

	public static String getErrorMsg(String msg, ResourceBundle rsb) {
		String errorMsg = "";
		if (msg.indexOf(" ") > 1 && rsb != null) {
			String fristStr = msg.substring(0, msg.indexOf(" ")).toLowerCase();
			fristStr = fristStr.replaceAll(",", "");
			String remaingStr = msg.substring(msg.indexOf(" "), msg.length());
			remaingStr = remaingStr.replace(" ", "");
			remaingStr = remaingStr.replace("-", "");
			remaingStr = remaingStr.replace("_", "");
			remaingStr = remaingStr.replace("/", "");
			remaingStr = remaingStr.replace("!", "");
			remaingStr = remaingStr.replace(":", "");
			remaingStr = remaingStr.replace("\n", "");
			remaingStr = remaingStr.replace(".", "");
			remaingStr = remaingStr.replace("(0)", "");
			remaingStr = remaingStr.replace(",", "");
			remaingStr = remaingStr.replace("'", "");
			String name = fristStr + remaingStr;
			errorMsg = rsb.getString(name);
		} else {
			errorMsg = msg;
		}
		return errorMsg;
	}

	public static String getDBErrorMsg(String msg, ResourceBundle rsb) {
		String errorMsg = "";
		if (msg.indexOf(" ") > 1) {
			String fristStr = msg.substring(0, msg.indexOf(" ")).toLowerCase();
			fristStr = fristStr.replaceAll(",", "");
			String remaingStr = msg.substring(msg.indexOf(" "), msg.length());
			remaingStr = remaingStr.replace(" ", "");
			remaingStr = remaingStr.replace("-", "");
			remaingStr = remaingStr.replace("_", "");
			remaingStr = remaingStr.replace("/", "");
			remaingStr = remaingStr.replace("!", "");
			remaingStr = remaingStr.replace(":", "");
			remaingStr = remaingStr.replace("\n", "");
			remaingStr = remaingStr.replace(".", "");
			remaingStr = remaingStr.replace("(0)", "");
			remaingStr = remaingStr.replace(",", "");
			String name = fristStr.toLowerCase() + remaingStr.toLowerCase();
			errorMsg = rsb.getString(name);
		} else {
			errorMsg = msg;
		}
		return errorMsg;
	}

	public static void addToQuerySpecialCase(String strFld, StringBuffer strBuf) {
		if (strBuf.toString().endsWith(") ") == true || strBuf.toString().endsWith("?") == true
				|| strBuf.toString().endsWith("IS NULL") == true || strBuf.toString().endsWith("LE_BOOK") == true
				|| strBuf.toString().endsWith("PRODUCT") == true) {
			strBuf.append(" AND " + strFld);
		} else {
			strBuf.append(" Where " + strFld);
		}
	}

	public static String replaceNewLineChar(String str) {
		try {
			if (!str.isEmpty()) {
				return str.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "").replaceAll("$@!", "")
						.replaceAll("null", "").replaceAll("NULL", "").replaceAll(System.lineSeparator(), "");
			}
			return str;
		} catch (Exception e) {
			// Log this exception
			return str;
		}
	}

	public static Connection getDbConnection(String jdbcUrl, String username, String password, String type,
			String version) throws ClassNotFoundException, SQLException, Exception {
		Connection connection = null;
		if ("ORACLE".equalsIgnoreCase(type))
			Class.forName("oracle.jdbc.driver.OracleDriver");
		else if ("MSSQL".equalsIgnoreCase(type))
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		else if ("MYSQL".equalsIgnoreCase(type))
			Class.forName("com.mysql.jdbc.Driver");
		else if ("POSTGRESQL".equalsIgnoreCase(type))
			Class.forName("org.postgresql.Driver");
		else if ("SYBASE".equalsIgnoreCase(type))
			Class.forName("com.sybase.jdbc4.jdbc.SybDataSource");
		else if ("INFORMIX".equalsIgnoreCase(type))
			Class.forName("com.informix.jdbc.IfxDriver");

		connection = DriverManager.getConnection(jdbcUrl, username, password);
		return connection;
	}

	public static ExceptionCode getConnection(String dbScript) {
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection con = null;
		Statement stmt = null;
		String dbIP = "";
		String jdbcUrl = "";
		String dbServiceName = getValue(dbScript, "SERVICE_NAME");
		String dbOracleSid = getValue(dbScript, "SID");
		String dbUserName = getValue(dbScript, "USER");
		if (!ValidationUtil.isValid(dbUserName))
			dbUserName = getValue(dbScript, "DB_USER");
		String dbPassWord = getValue(dbScript, "PWD");
		if (!ValidationUtil.isValid(dbPassWord))
			dbPassWord = getValue(dbScript, "DB_PWD");
		String dbPortNumber = getValue(dbScript, "DB_PORT");
		String dataBaseName = getValue(dbScript, "DB_NAME");
		String dataBaseType = getValue(dbScript, "DATABASE_TYPE");
		String dbInstance = getValue(dbScript, "DB_INSTANCE");
		String dbIp = getValue(dbScript, "DB_IP");
		String serverName = getValue(dbScript, "SERVER_NAME");
		String version = getValue(dbScript, "JAR_VERSION");
		String paramLevel = "";
		String hostName = dbServiceName;
		Boolean sidFlag = false;
		if (!ValidationUtil.isValid(hostName)) {
			hostName = dbOracleSid;
			sidFlag = true;
		}
		if (ValidationUtil.isValid(dbIp))
			dbIP = dbIp;
		try {
			if ("ORACLE".equalsIgnoreCase(dataBaseType)) {
				if (sidFlag)
					jdbcUrl = "jdbc:oracle:thin:@//" + dbIP + ":" + dbPortNumber + "/" + hostName;
				else
					jdbcUrl = "jdbc:oracle:thin:@" + dbIP + ":" + dbPortNumber + ":" + hostName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "ORACLE", version);
			} else if ("MSSQL".equalsIgnoreCase(dataBaseType)) {
				if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(hostName)) {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance
							+ ";databaseName=" + hostName;
				} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(hostName)) {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance;
				} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(hostName)) {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + hostName;
				} else {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + hostName;
				}

				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "MSSQL", version);
			} else if ("MYSQL".equalsIgnoreCase(dataBaseType)) {

				if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "//instanceName=" + dbInstance
							+ "//databaseName=" + dataBaseName;
				} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(dataBaseName)) {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "//instanceName=" + dbInstance;
				} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "/" + dataBaseName;
				} else {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "/" + dataBaseName;
				}

				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "MYSQL", version);
			} else if ("POSTGRESQL".equalsIgnoreCase(dataBaseType)) {
				jdbcUrl = "jdbc:postgresql://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "POSTGRESQL", version);
			} else if ("SYBASE".equalsIgnoreCase(dataBaseType)) {
				/* jdbc:sybase:Tds:194.170.154.136:7834?ServiceName=PROD_RCB */
				// jdbcUrl="jdbc:jtds:sybase://"+dbIP+":"+dbPortNumber+"/"+dataBaseName;
				jdbcUrl = "jdbc:sybase:Tds:" + dbIP + ":" + dbPortNumber + "?ServiceName=" + hostName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "SYBASE", version);
			} else if ("INFORMIX".equalsIgnoreCase(dataBaseType)) {
				jdbcUrl = "jdbc:informix-sqli://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName + ":informixserver="
						+ serverName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "INFORMIX", version);
			}
			if (con == null) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Problem in gaining connection with datasource");
				return exceptionCode;
			}
			String dbSetParam1 = CommonUtils.getValue(dbScript, "DB_SET_PARAM1");
			String dbSetParam2 = CommonUtils.getValue(dbScript, "DB_SET_PARAM2");
			String dbSetParam3 = CommonUtils.getValue(dbScript, "DB_SET_PARAM3");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (ValidationUtil.isValid(dbSetParam1)) {
				paramLevel = "Error when executing Parameter1 - ";
				stmt.executeUpdate(dbSetParam1);
			}
			if (ValidationUtil.isValid(dbSetParam2)) {
				paramLevel = "Error when executing Parameter2 - ";
				stmt.executeUpdate(dbSetParam2);
			}

			if (ValidationUtil.isValid(dbSetParam3)) {
				paramLevel = "Error when executing Parameter3 - ";
				stmt.executeUpdate(dbSetParam3);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in gaining connection - Cause:"
					+ (ValidationUtil.isValid(paramLevel) ? paramLevel : "") + e.getMessage());
			return exceptionCode;
		} catch (SQLException e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in gaining connection - Cause:"
					+ (ValidationUtil.isValid(paramLevel) ? paramLevel : "") + e.getMessage());
			return exceptionCode;
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in gaining connection - Cause:"
					+ (ValidationUtil.isValid(paramLevel) ? paramLevel : "") + e.getMessage());
			return exceptionCode;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("Connector Test Successful");
		exceptionCode.setResponse(con);
		return exceptionCode;
	}

	public static ExceptionCode getConnectionFromScript(String dbScript) {
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection con = null;
		Statement stmt = null;
		String dbIP = "";
		String jdbcUrl = "";
		String dbServiceName = getValue(dbScript, "SERVICE_NAME");
		String dbOracleSid = getValue(dbScript, "SID");
		String dbUserName = getValue(dbScript, "USER");
		if (!ValidationUtil.isValid(dbUserName))
			dbUserName = getValue(dbScript, "DB_USER");
		String dbPassWord = getValue(dbScript, "PWD");
		if (!ValidationUtil.isValid(dbPassWord))
			dbPassWord = getValue(dbScript, "DB_PWD");
		String dbPortNumber = getValue(dbScript, "DB_PORT");
		String dataBaseName = getValue(dbScript, "DB_NAME");
		String dataBaseType = getValue(dbScript, "DATABASE_TYPE");
		String dbInstance = getValue(dbScript, "DB_INSTANCE");
		String dbIp = getValue(dbScript, "DB_IP");
		String serverName = getValue(dbScript, "SERVER_NAME");
		String version = getValue(dbScript, "JAR_VERSION");
		String paramLevel = "";
		String hostName = dbServiceName;
		Boolean sidFlag = false;
		if (!ValidationUtil.isValid(hostName)) {
			hostName = dbOracleSid;
			sidFlag = true;
		}
		if (ValidationUtil.isValid(dbIp))
			dbIP = dbIp;
		try {
			if ("ORACLE".equalsIgnoreCase(dataBaseType)) {
				if (sidFlag)
					jdbcUrl = "jdbc:oracle:thin:@//" + dbIP + ":" + dbPortNumber + "/" + hostName;
				else
					jdbcUrl = "jdbc:oracle:thin:@" + dbIP + ":" + dbPortNumber + ":" + hostName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "ORACLE", version);
			} else if ("MSSQL".equalsIgnoreCase(dataBaseType)) {
				if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(hostName)) {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance
							+ ";databaseName=" + hostName;
				} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(hostName)) {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance;
				} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(hostName)) {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + hostName;
				} else {
					jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + hostName;
				}

				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "MSSQL", version);
			} else if ("MYSQL".equalsIgnoreCase(dataBaseType)) {

				if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "//instanceName=" + dbInstance
							+ "//databaseName=" + dataBaseName;
				} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(dataBaseName)) {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "//instanceName=" + dbInstance;
				} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "/" + dataBaseName;
				} else {
					jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "/" + dataBaseName;
				}

				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "MYSQL", version);
			} else if ("POSTGRESQL".equalsIgnoreCase(dataBaseType)) {
				jdbcUrl = "jdbc:postgresql://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "POSTGRESQL", version);
			} else if ("SYBASE".equalsIgnoreCase(dataBaseType)) {
				/* jdbc:sybase:Tds:194.170.154.136:7834?ServiceName=PROD_RCB */
				// jdbcUrl="jdbc:jtds:sybase://"+dbIP+":"+dbPortNumber+"/"+dataBaseName;
				jdbcUrl = "jdbc:sybase:Tds:" + dbIP + ":" + dbPortNumber + "?ServiceName=" + hostName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "SYBASE", version);
			} else if ("INFORMIX".equalsIgnoreCase(dataBaseType)) {
				jdbcUrl = "jdbc:informix-sqli://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName + ":informixserver="
						+ serverName;
				con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "INFORMIX", version);
			}
			if (con == null) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Problem in gaining connection with datasource");
				return exceptionCode;
			}
			String dbSetParam1 = CommonUtils.getValue(dbScript, "DB_SET_PARAM1");
			String dbSetParam2 = CommonUtils.getValue(dbScript, "DB_SET_PARAM2");
			String dbSetParam3 = CommonUtils.getValue(dbScript, "DB_SET_PARAM3");
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (ValidationUtil.isValid(dbSetParam1)) {
				paramLevel = "Error when executing Parameter1 - ";
				stmt.executeUpdate(dbSetParam1);
			}
			if (ValidationUtil.isValid(dbSetParam2)) {
				paramLevel = "Error when executing Parameter2 - ";
				stmt.executeUpdate(dbSetParam2);
			}

			if (ValidationUtil.isValid(dbSetParam3)) {
				paramLevel = "Error when executing Parameter3 - ";
				stmt.executeUpdate(dbSetParam3);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in gaining connection - Cause:"
					+ (ValidationUtil.isValid(paramLevel) ? paramLevel : "") + e.getMessage());
			return exceptionCode;
		} catch (SQLException e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in gaining connection - Cause:"
					+ (ValidationUtil.isValid(paramLevel) ? paramLevel : "") + e.getMessage());
			return exceptionCode;
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Problem in gaining connection - Cause:"
					+ (ValidationUtil.isValid(paramLevel) ? paramLevel : "") + e.getMessage());
			return exceptionCode;
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("Connector Test Successful");
		exceptionCode.setResponse(jdbcUrl);
		return exceptionCode;
	}

	public static String getValue(String source, String key) {
		try {
			Matcher regexMatcher = Pattern.compile("\\{" + key + ":#(.*?)\\$@!(.*?)\\#}", Pattern.DOTALL)
					.matcher(source);
			if (regexMatcher.find()) {
				if (ValidationUtil.isValid(regexMatcher.group(2)) && "ENCRYPT".equalsIgnoreCase(regexMatcher.group(1))
						&& isBase64(regexMatcher.group(2))) {
					return new String(Base64.decode(regexMatcher.group(2)));
				} else {
					return regexMatcher.group(2);
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String escapeMetaCharacters(String inputString) {
		final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
				">", "-", "&" };
		String outputString = "";
		for (int i = 0; i < metaCharacters.length; i++) {
			if (inputString.contains(metaCharacters[i])) {
				outputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
				inputString = outputString;
			}
		}
		return ValidationUtil.isValid(outputString) ? outputString : inputString;
	}

	public static String getValueForXmlTag(String source, String tagName) {
		try {
			Matcher regexMatcher = Pattern.compile("\\<" + tagName + "\\>(.*?)\\<\\/" + tagName + "\\>", Pattern.DOTALL)
					.matcher(source);
			return regexMatcher.find() ? regexMatcher.group(1) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String randomColorToCountSpecified(int count) {
		StringBuffer returnColorStrBuffer = new StringBuffer("");
		for (int i = 0; i < count; i++) {
			returnColorStrBuffer.append(ValidationUtil.isValid(String.valueOf(returnColorStrBuffer)) ? "," : "");
			long k = i;
			Random rand = new Random(k);
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			Color randomColor = new Color(r, g, b);
			String hex = Integer.toHexString(randomColor.getRGB() & 0xffffff);
			if (hex.length() < 6) {
				if (hex.length() == 5)
					hex = "0" + hex;
				if (hex.length() == 4)
					hex = "00" + hex;
				if (hex.length() == 3)
					hex = "000" + hex;
			}
			hex = "#" + hex;
			returnColorStrBuffer.append(hex);
		}
		return String.valueOf(returnColorStrBuffer);
	}

	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}

	public static String rgb2Hex(Color color) {
		return Integer.toHexString(color.getRGB() & 0xffffff);
	}

	public static boolean checkValidHash(String val) {
		if ("TVC_SESSIONID_STG_1".equalsIgnoreCase(val) || "TVC_SESSIONID_STG_2".equalsIgnoreCase(val)
				|| "TVC_SESSIONID_STG_3".equalsIgnoreCase(val)) {
			return false;
		} else {
			return true;
		}
	}

	public static String replaceHashTag(String str, String[] hashArr, String[] hashValArr) {
		if (hashArr != null && hashValArr != null && hashArr.length == hashValArr.length) {
			Pattern pattern = Pattern.compile("#(.*?)#", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
				int hashIndex = 0;
				String matchedStr = matcher.group(1);
				matchedStr = matchedStr.toUpperCase().replaceAll("\\s", "\\_");
				String useStr = matcher.group(1);
				useStr = escapeMetaCharacters(useStr);
				for (String hashVar : hashArr) {
					if (matchedStr.equalsIgnoreCase(hashVar))
						str = str.replaceAll("#" + useStr + "#", hashValArr[hashIndex]);
					hashIndex++;
				}
			}
		}
		return str;
	}

	public static boolean isAggrigateFunction(String criteria) {
		if (("SUM".equalsIgnoreCase(criteria)) || ("AVG".equalsIgnoreCase(criteria))
				|| ("MIN".equalsIgnoreCase(criteria)) || ("MAX".equalsIgnoreCase(criteria))
				|| ("COUNT".equalsIgnoreCase(criteria))) {
			return true;
		}
		return false;
	}

	public static boolean isBetweenCondition(String criteria) {
		if (("between".equalsIgnoreCase(criteria))) {
			return true;
		}
		return false;
	}

	public static boolean isMultiValueCondition(String criteria) {
		if (("not in".equalsIgnoreCase(criteria)) || ("in".equalsIgnoreCase(criteria))) {
			return true;
		}
		return false;
	}

	public static boolean isNoValueCondition(String criteria) {
		if (("is null".equalsIgnoreCase(criteria)) || ("is not null".equalsIgnoreCase(criteria))) {
			return true;
		}
		return false;
	}

	public static String appendQuots(String value) {
		if (ValidationUtil.isValid(value)) {
			value = value.trim();
			if (value.charAt(0) != '\'') {
				value = "'" + value;
			}
			if (value.charAt(value.length() - 1) != '\'') {
				value = value + "'";
			}
		}
		return value;
	}

	public static String transformXmlDocToString(Document doc) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Object[]> parseScript(String script) {
		if (ValidationUtil.isValid(script)) {
			List<Object[]> returnLst = new ArrayList<Object[]>(3);
			List<String> hashVar = new ArrayList<String>();
			List<String> hashVarDisplayVal = new ArrayList<String>();
			List<String> hashVarActualVal = new ArrayList<String>();
			Matcher matchObj = Pattern.compile("\\{(.*?)\\:\\#(.*?)\\$\\@\\!(.*?)\\#\\}", Pattern.DOTALL)
					.matcher(script);
			while (matchObj.find()) {
				hashVar.add(matchObj.group(1));
				hashVarDisplayVal.add(matchObj.group(2));
				hashVarActualVal.add(matchObj.group(3));
			}
			Object[] hashVarArr = hashVar.toArray();
			Object[] hashVarDisplayValArr = hashVarDisplayVal.toArray();
			Object[] hashVarActualValArr = hashVarActualVal.toArray();
			returnLst.add(hashVarArr);
			returnLst.add(hashVarDisplayValArr);
			returnLst.add(hashVarActualValArr);
			return returnLst;
		} else {
			return null;
		}
	}

	public static String toJavascriptArray(Object[] arr) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < arr.length; i++) {
			sb.append("\"").append(arr[i]).append("\"");
			if (i + 1 < arr.length) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static void callSetter(Object obj, String fieldName, Object value) {
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			pd.getWriteMethod().invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ExceptionCode XmlToJson(String data) {
		String value = "";
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			data = data.replaceAll("\n", "").replaceAll("\r", "");
			data = data.replaceAll(">\\s*<", "><");
			JSONObject asJson = XML.toJSONObject(data);
			value = asJson.toString();
		} catch (Exception e) {
			exceptionCode = CommonUtils.getResultObject("xmlToJson", Constants.WE_HAVE_ERROR_DESCRIPTION,
					"Xml To Json Convertion", e.getMessage());
			exceptionCode.setResponse(value);
			throw new RuntimeCustomException(exceptionCode);
		}
		exceptionCode = CommonUtils.getResultObject("xmlToJson", Constants.SUCCESSFUL_OPERATION, "Sucess", "");
		exceptionCode.setResponse(value);
		return exceptionCode;
	}

	public static ExceptionCode jsonToXml(String data) {
		String value = "";
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			JSONObject asJson = new JSONObject(data);
			value = XML.toString(asJson);
		} catch (Exception e) {
			exceptionCode = CommonUtils.getResultObject("jsonToXml", Constants.WE_HAVE_ERROR_DESCRIPTION,
					"JSON To XML Convertion", e.getMessage());
			exceptionCode.setResponse(value);
			throw new RuntimeCustomException(exceptionCode);
		}
		exceptionCode = CommonUtils.getResultObject("jsonToXml", Constants.SUCCESSFUL_OPERATION, "Sucess", "");
		exceptionCode.setResponse(value);
		return exceptionCode;
	}

	public static void addToOrderByQuery(String strFld, StringBuffer strBuf) {
		if (strBuf.toString().endsWith(" ") == true) {
			strBuf.append(strFld);
		} else {
			strBuf.append(", " + strFld);
		}
	}

	public static Connection getDBConnection(String dbScript) throws ClassNotFoundException, SQLException, Exception {
		Connection con = null;
		String dbIP = "";
		String jdbcUrl = "";
		String dbServiceName = getValue(dbScript, "SERVICE_NAME");
		String dbOracleSid = getValue(dbScript, "SID");

		String dbUserName = getValue(dbScript, "USER");
		if (!ValidationUtil.isValid(dbUserName))
			dbUserName = getValue(dbScript, "DB_USER");
		String dbPassWord = getValue(dbScript, "PWD");
		if (!ValidationUtil.isValid(dbPassWord))
			dbPassWord = getValue(dbScript, "DB_PWD");
		String dbPortNumber = getValue(dbScript, "DB_PORT");
		String dataBaseName = getValue(dbScript, "DB_NAME");
		String dataBaseType = getValue(dbScript, "DATABASE_TYPE");
		String dbInstance = getValue(dbScript, "DB_INSTANCE");
		String dbIp = getValue(dbScript, "DB_IP");
		String serverName = getValue(dbScript, "SERVER_NAME");
		String version = getValue(dbScript, "JAR_VERSION");
		String hostName = dbServiceName;
		Boolean sidFlag = false;
		if (!ValidationUtil.isValid(hostName)) {
			hostName = dbOracleSid;
			sidFlag = true;
		}
		if (ValidationUtil.isValid(dbIp))
			dbIP = dbIp;
		if ("ORACLE".equalsIgnoreCase(dataBaseType)) {
			if (sidFlag)
				jdbcUrl = "jdbc:oracle:thin:@//" + dbIP + ":" + dbPortNumber + "/" + hostName;
			else
				jdbcUrl = "jdbc:oracle:thin:@" + dbIP + ":" + dbPortNumber + ":" + hostName;
			con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "ORACLE", version);
		} else if ("MSSQL".equalsIgnoreCase(dataBaseType)) {
			if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance
						+ ";databaseName=" + dataBaseName;
			} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";instanceName=" + dbInstance;
			} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";dataBaseName=" + dataBaseName;
			} else {
				jdbcUrl = "jdbc:sqlserver://" + dbIP + ":" + dbPortNumber + ";databaseName=" + dataBaseName;
			}

			con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "MSSQL", version);
		} else if ("MYSQL".equalsIgnoreCase(dataBaseType)) {

			if (ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "//instanceName=" + dbInstance
						+ "//databaseName=" + dataBaseName;
			} else if (ValidationUtil.isValid(dbInstance) && !ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "//instanceName=" + dbInstance;
			} else if (!ValidationUtil.isValid(dbInstance) && ValidationUtil.isValid(dataBaseName)) {
				jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "/" + dataBaseName;
			} else {
				jdbcUrl = "jdbc:mysql://" + dbIp + ":" + dbPortNumber + "/" + dataBaseName;
			}

			con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "MYSQL", version);
		} else if ("POSTGRESQL".equalsIgnoreCase(dataBaseType)) {
			jdbcUrl = "jdbc:postgresql://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName;
			con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "POSTGRESQL", version);
		} else if ("SYBASE".equalsIgnoreCase(dataBaseType)) {
			/* jdbc:sybase:Tds:194.170.154.136:7834?ServiceName=PROD_RCB */
			// jdbcUrl="jdbc:jtds:sybase://"+dbIP+":"+dbPortNumber+"/"+dataBaseName;
			jdbcUrl = "jdbc:sybase:Tds:" + dbIP + ":" + dbPortNumber + "?ServiceName=" + hostName;
			con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "SYBASE", version);
		} else if ("INFORMIX".equalsIgnoreCase(dataBaseType)) {
			jdbcUrl = "jdbc:informix-sqli://" + dbIP + ":" + dbPortNumber + "/" + dataBaseName + ":informixserver="
					+ serverName;
			con = getDbConnection(jdbcUrl, dbUserName, dbPassWord, "INFORMIX", version);
		}
		return con;
	}

	public static void addToQuerySearch(String strFld, StringBuffer strBuf, String condition) {
		if (strBuf.toString().endsWith(")  ") == true) {
			strBuf.append(" AND " + strFld + condition + SPACE);
		} else if (strBuf.toString().endsWith(") ") == true || strBuf.toString().endsWith("?") == true
				|| strBuf.toString().endsWith("IS NULL") == true || strBuf.toString().endsWith("AND ") == true
				|| strBuf.toString().endsWith("OR ") == true)
			strBuf.append(strFld + condition + SPACE);
		else
			strBuf.append(" Where " + strFld + condition + SPACE);
	}

	public static String criteriaBasedVal(String criteria, String value) {
		String output = "";
		try {
			switch (criteria) {
			case "LIKE":
				output = " LIKE UPPER('%" + value + "%') ";
				break;

			case "STARTSWITH":
				output = " LIKE UPPER('" + value + "%') ";
				break;

			case "ENDSWITH":
				output = " LIKE UPPER('%" + value + "') ";
				break;

			case "EQUALS":
				output = " = UPPER('" + value + "') ";
				break;

			default:

			}
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String convertToReadableFormat(String columnName) {
		StringBuilder readableName = new StringBuilder();
		boolean nextUpperCase = true;

		for (char c : columnName.toCharArray()) {
			if (c == '_') {
				readableName.append(' ');
				nextUpperCase = true;
			} else {
				if (nextUpperCase) {
					readableName.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					readableName.append(Character.toLowerCase(c));
				}
			}
		}

		return readableName.toString();
	}

	public static void appendXmlTag(StringBuilder builder, String tagName, String content) {
		builder.append("<").append(tagName).append(">").append(escapeXml(content)).append("</").append(tagName)
				.append(">");
	}

	private static String escapeXml(String input) {
		if (input == null) {
			return "";
		}
		return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	public static String readClobSafely(ResultSet rs, String columnName) throws SQLException {
		Reader reader = rs.getCharacterStream(columnName);
		if (reader == null)
			return "";
		StringBuilder sb = new StringBuilder();
		char[] buffer = new char[2048];
		try {
			int len;
			while ((len = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}
			reader.close();
		} catch (IOException e) {
			throw new SQLException("Error reading column " + columnName, e);
		}
		return sb.toString();
	}

	public static boolean isBase64(String str) {
		if (str == null || str.trim().isEmpty())
			return false;
		try {
			byte[] decoded = java.util.Base64.getDecoder().decode(str);
			String reencoded = java.util.Base64.getEncoder().encodeToString(decoded);
			return reencoded.equals(str.replaceAll("\\s+", ""));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

//	 public static ExceptionCode dynamicCaptcha(String text) {
//		 ExceptionCode exceptionCode = new ExceptionCode();
//	        try {
//	            DefaultKaptcha kaptcha = new DefaultKaptcha();
//	            kaptcha.setConfig(getCaptchaConfig());
//	            BufferedImage image = kaptcha.createImage(text);
//
//	            String filePath = System.getProperty("java.io.tmpdir") + "dynamic_captcha.png";
//	            ByteArrayOutputStream baos = new ByteArrayOutputStream();	            
//	            ImageIO.write(image, "png", baos);
//	            baos.flush();
//	            byte[] imageInByte = baos.toByteArray();
//	            baos.close();
//	            String base64Image = java.util.Base64.getEncoder().encodeToString(imageInByte);
//	            exceptionCode.setResponse(base64Image);
////	            exceptionCode.setResponse(imageInByte);
//				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//	        } catch (IOException e) {
//	        	exceptionCode.setResponse(e.getMessage());
//				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//	        }
//	        return exceptionCode;
//	    }
//	 private static Config getCaptchaConfig() {
//	        Properties props = new Properties();
//	        props.setProperty("kaptcha.image.width", "200");
//	        props.setProperty("kaptcha.image.height", "50");
//	        props.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
//	        props.setProperty("kaptcha.textproducer.font.size", "35");
//	        props.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.DefaultNoise");
//	        props.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
//	        props.setProperty("kaptcha.textproducer.char.length", "6");
//	        props.setProperty("kaptcha.textproducer.char.string", "abcde2345678gfynmnpwx");
//	        return new Config(props);
//	    }
	public static ExceptionCode generateCaptchaImageBase64(String text) {
		ExceptionCode exceptionCode = new ExceptionCode();
//		String base64Img = "";
//		int width = 150;
//		int height = 50;
//		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		Graphics2D g2d = bufferedImage.createGraphics();
//
//		// Background
//		Color bgColor = Color.white;
//		g2d.setColor(bgColor);
//		g2d.fillRect(0, 0, width, height);
//
//		// Fonts
//		Font[] fonts = { new Font("Arial", Font.PLAIN, 40), new Font("Courier", Font.ITALIC, 40), };
//
//		// Draw characters with random color, rotation, and font
//		for (int i = 0; i < text.length(); i++) {
//			g2d.setFont(fonts[random.nextInt(fonts.length)]);
////	            g2d.setColor(getRandomColor(30, 150));
//			g2d.setColor(Color.BLACK);
//
//			AffineTransform original = g2d.getTransform();
//			AffineTransform rotate = new AffineTransform();
//
//			int x = 20 + i * 20;
//			int y = 35 + random.nextInt(5); // vertical jitter
//
//			rotate.rotate((random.nextDouble() - 0.5) * 0.5, x, y);
//			g2d.setTransform(rotate);
//
//			g2d.drawString(String.valueOf(text.charAt(i)), x, y);
//			g2d.setTransform(original);
//		}
//
//		// Add noise lines
//		g2d.setStroke(new BasicStroke(1));
//		for (int i = 0; i < 5; i++) {
//			g2d.setColor(getRandomColor(160, 200));
//			int x1 = random.nextInt(width);
//			int y1 = random.nextInt(height);
//			int x2 = random.nextInt(width);
//			int y2 = random.nextInt(height);
//			g2d.drawLine(x1, y1, x2, y2);
//		}
//
//		g2d.dispose();
		String base64Img = "";

		Font font = new Font("Arial", Font.PLAIN, 32);
		BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tempImage.createGraphics();
		g2.setFont(font);
		FontMetrics fm = g2.getFontMetrics();

		int paddingLeft = 10;
		int paddingRight = 10;
		int spacing = 5;  // space between chars

		// Calculate width of entire text with spacing between chars
		int totalWidth = paddingLeft + paddingRight;
		for (int i = 0; i < text.length(); i++) {
		    totalWidth += fm.charWidth(text.charAt(i));
		    if (i < text.length() - 1) {
		        totalWidth += spacing; // spacing between chars
		    }
		}

		int height = 60;
		g2.dispose();

		BufferedImage bufferedImage = new BufferedImage(totalWidth, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		// Transparent background
		g2d.setComposite(AlphaComposite.Clear);
		g2d.fillRect(0, 0, totalWidth, height);
		g2d.setComposite(AlphaComposite.SrcOver);

		g2d.setFont(font);
		g2d.setColor(Color.BLACK);
		fm = g2d.getFontMetrics();

		// Draw each character manually with spacing
		int x = paddingLeft;
		int y = (height + fm.getAscent()) / 2 - 5;

		for (char c : text.toCharArray()) {
		    g2d.drawString(String.valueOf(c), x, y);
		    x += fm.charWidth(c) + spacing;
		}

		g2d.dispose();
		// Encode to base64
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "png", baos);
			byte[] bytes = baos.toByteArray();
			base64Img = java.util.Base64.getEncoder().encodeToString(bytes);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setResponse(base64Img);
			exceptionCode.setErrorMsg("Captcha Generated SucessFully");
		} catch (Exception e) {
//			throw new RuntimeException("CAPTCHA generation failed", e);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setResponse(base64Img);
		}
		return exceptionCode;
	}

	private static Color getRandomColor(int min, int max) {
		int r = min + random.nextInt(max - min);
		int g = min + random.nextInt(max - min);
		int b = min + random.nextInt(max - min);
		return new Color(r, g, b);
	}

}