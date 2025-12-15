package com.vision.dao;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Vector;
import java.util.stream.Collectors;

import jakarta.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.expression.ParseException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.itextpdf.styledxmlparser.jsoup.parser.XmlTreeBuilder;
import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ExcelExportUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.CommonVb;
import com.vision.vb.CustomersVb;
import com.vision.vb.NumSubTabVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateErrorsVb;
import com.vision.vb.TemplateMappingVb;
import com.vision.vb.TemplateScheduleVb;
import com.vision.vb.VisionUsersVb;
import com.vision.wb.TemplateConfigWb;

@Component
public class TemplateScheduleDao extends AbstractDao<TemplateScheduleVb> implements ServletContextAware {
	@Value("${app.databaseType}")
	private String databaseType;

	@Value("${app.cloud}")
	private String cloud;

	@Value("${app.decimal.precision}")
	private String precisionFlag;
	private ServletContext servletContext;

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	@Autowired
	CommonApiDao commonApiDao;
	@Autowired
	CommonDao commonDao;
	@Autowired
	TemplateConfigWb templateConfigWb;
	@Autowired
	TemplateMappingDao templateMappingDao;
	@Autowired
	TemplateScheduleCronDao templateScheduleCronDao;

	@Autowired
	ReportsDao reportsDao;

	private static final String SERVICE_NAME = "RegTech Download";

	@Override
	protected void setServiceDefaults() {
		serviceName = "Adjustment";
		serviceDesc = "Adjustment";
		tableName = "Template_header";
		childTableName = "RG_SCHEDULES";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
		VisionUsersVb visionUsersVb = (VisionUsersVb) SessionContextHolder.getContext();
		userGroup = visionUsersVb.getUserGroup();
		userProfile = visionUsersVb.getUserProfile();
	}

	String statusAtDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 9999, "T1.STATUS", "STATUS_DESC");
	String feedFrequencyAtDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 100, "T2.PROCESS_FREQUENCY",
			"PROCESS_FREQUENCY_DESC");
	String recordIndicatorAtDesc = ValidationUtil.numAlphaTabDescritpionQuery("AT", 9998, "T1.RECORD_INDICATOR",
			"RECORD_INDICATOR_DESC");

	public ExceptionCode getPanelInfo1(TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateScheduleVb> collTemp = new ArrayList<>();
		try {
			StringBuffer sql = new StringBuffer("SELECT DISTINCT T1.COUNTRY,T1.LE_BOOK,T1.PROCESS_FREQUENCY, "
					+ "        (SELECT AST.ALPHA_SUBTAB_DESCRIPTION " + "           FROM ALPHA_SUB_TAB AST "
					+ "          WHERE AST.ALPHA_TAB = T1.PROCESS_FREQUENCY_AT "
					+ "            AND AST.ALPHA_SUB_TAB = T1.PROCESS_FREQUENCY) AS PROCESS_FREQUENCY_DESC,  " + " "
					+ getDbFunction("DATEFUNC") + "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT")
					+ "') REPORTING_DATE,                                  "
					+ "        CASE WHEN T1.PROCESS_FREQUENCY = 'IND' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'DLY' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'WKY' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'MTH' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("MONTHYEAR_FORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'QTR' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("MONTHYEAR_FORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'HYR' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("MONTHYEAR_FORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'ANN' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("YEAR_FORMAT") + "') " + "        ELSE "
					+ getDbFunction("DATEFUNC") + "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT")
					+ "') END DISPLAY_REPORTING_DATE, "
					+ "        CASE WHEN MIN(T2.RG_SCHEDULE_STATUS) = 'Y' Then 'LIVE' else 'DUE' End SCHEDULE_START_TIME, "
					+ "        " + getDbFunction("DATEFUNC") + "(MAX(T1.REPORTING_DATE), '"
					+ getDbFunction("DATEFORMAT") + "') LAST_SUCCESSFUL_DATE, "
					+ "        CASE WHEN T1.PROCESS_FREQUENCY = 'IND' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'DLY' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'WKY' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'MTH' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("MONTHYEAR_FORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'QTR' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("MONTHYEAR_FORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'HYR' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("MONTHYEAR_FORMAT") + "') "
					+ "             WHEN T1.PROCESS_FREQUENCY = 'ANN' THEN " + getDbFunction("DATEFUNC")
					+ "(MAX(T1.REPORTING_DATE), '" + getDbFunction("YEAR_FORMAT") + "') " + "        ELSE "
					+ getDbFunction("DATEFUNC") + "(MAX(T1.REPORTING_DATE), '" + getDbFunction("DATEFORMAT")
					+ "') END DISPLAY_LAST_SUCCESSFUL_DATE " + "   FROM RG_PROCESS_CONTROL T1, RG_SCHEDULES T2  "
					+ "  WHERE     T1.RG_PROCESS_ID = T2.RG_PROCESS_ID   " + "        AND T1.COUNTRY = ? "
					+ "        AND T1.LE_BOOK = ? " + " GROUP BY T1.COUNTRY," + "  T1.LE_BOOK,"
					+ "  T1.PROCESS_FREQUENCY_AT," + "  T1.PROCESS_FREQUENCY," + "  T2.RG_SCHEDULE_STATUS" + " ORDER BY"
					+ " CASE T1.PROCESS_FREQUENCY " + "           WHEN 'IND' THEN 1     "
					+ "             WHEN 'DLY' THEN 2     " + "             WHEN 'WKY' THEN 3     "
					+ "             WHEN 'MTH' THEN 4     " + "             WHEN 'QTR' THEN 5     "
					+ "             WHEN 'HYR' THEN 6     " + "             WHEN 'ANN' THEN 7     "
					+ "             ELSE 8                " + "         END                      ");
//					+ "         SCHEDULE_START_TIME;      ");
			Object args[] = { dObj.getCountry(), dObj.getLeBook() };
			collTemp = getJdbcTemplate().query(sql.toString(), args, getPanelInfo1Mapper());
			if (collTemp != null && collTemp.size() > 0) {
				exceptionCode.setResponse(collTemp);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("success");
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getPanelInfo2(TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateScheduleVb> collTemp = new ArrayList<>();
		try {
			StringBuffer strQueryAppr = new StringBuffer(" SELECT DISTINCT" + " T2.RG_PROCESS_ID,"
					+ " TO_CHAR (T2.REPORTING_DATE, 'DD-Mon-RRRR')" + " REPORTING_DATE,"
					+ " TO_CHAR (T2.START_TIME, 'DD-Mon-RRRR HH24:MI:SS')" + " START_TIME,"
					+ " TO_CHAR (T2.END_TIME, 'DD-Mon-RRRR HH24:MI:SS')" + " END_TIME," + " T2.RG_SCHEDULE_STATUS,"
					+ " T1.PROCESS_FREQUENCY," + " (SELECT AST.ALPHA_SUBTAB_DESCRIPTION" + " FROM ALPHA_SUB_TAB AST"
					+ " WHERE AST.ALPHA_TAB = T1.PROCESS_FREQUENCY_AT"
					+ " AND AST.ALPHA_SUB_TAB = T1.PROCESS_FREQUENCY)" + " AS PROCESS_FREQUENCY_DESC,"
					+ " (SELECT AST.ALPHA_SUBTAB_DESCRIPTION" + " FROM ALPHA_SUB_TAB AST"
					+ " WHERE AST.ALPHA_TAB = T2.RG_SCHEDULE_STATUS_AT"
					+ " AND AST.ALPHA_SUB_TAB = T2.RG_SCHEDULE_STATUS)" + " AS RG_PROCESS_STATUS_DESC"
					+ " FROM RG_PROCESS_CONTROL T1, RG_SCHEDULES T2" + " WHERE T1.RG_PROCESS_ID = T2.RG_PROCESS_ID");
			if (ValidationUtil.isValid(dObj.getCountry())) {
				strQueryAppr.append(" AND T1.COUNTRY = '" + dObj.getCountry().toUpperCase() + "' ");
			}
			if (ValidationUtil.isValid(dObj.getLeBook())) {
				strQueryAppr.append(" AND T1.LE_BOOK = '" + dObj.getLeBook().trim() + "' ");
			}
			if (ValidationUtil.isValid(dObj.getProcessFrequency())) {
				strQueryAppr.append(" AND T1.PROCESS_FREQUENCY = '" + dObj.getProcessFrequency() + "' ");
			}
//			if (ValidationUtil.isValid(dObj.getProcessStatus())) {
//				strQueryAppr.append(" AND T2.RG_SCHEDULE_STATUS = '" + dObj.getProcessStatus() + "' ");
//			}

			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())) {
				strQueryAppr.append("and T1.COUNTRY " + commonDao.getDbFunction("PIPELINE") + " ' - ' "
						+ commonDao.getDbFunction("PIPELINE") + "T1.LE_BOOK IN ( "
						+ SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
			}
			String orderBy = " Order By REPORTING_DATE DESC";
			strQueryAppr.append(orderBy);
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), getPanelInfo2Mapper());
			if (collTemp != null && collTemp.size() > 0) {
				exceptionCode.setResponse(collTemp);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("success");
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: Exception in getting the Acq - E Upload Filing Panel2 results : ");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getPanelInfo3(TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		StringBuffer strQueryAppr = new StringBuffer();
		List<TemplateScheduleVb> collTemp = new ArrayList<>();
		String logDate = "";
		if ("MSSQL".equalsIgnoreCase(databaseType)) {
			logDate = "format(CAST(T2.REPORTING_DATE AS DATETIME), 'yyyy-MM-dd')LOG_DATE";
		} else if ("ORACLE".equalsIgnoreCase(databaseType)) {
			logDate = "TO_CHAR(T2.REPORTING_DATE,'yyyy-MM-dd')LOG_DATE";
		}
		try {
			strQueryAppr.append(
					"  SELECT T1.COUNTRY,T1.LE_BOOK,T1.TEMPLATE_ID,																	"
							+ "         T1.TEMPLATE_DESCRIPTION,                                                           "
							+ "         TO_CHAR (T2.REPORTING_DATE, 'DD-Mon-RRRR') REPORTING_DATE,                         "
							+ "         TO_CHAR(T1.PROCESS_START_TIME,'dd-Mon-yyyy HH24:MI:SS') START_TIME,                                                             "
							+ "       TO_CHAR(T1.PROCESS_END_TIME,'dd-Mon-yyyy HH24:MI:SS') END_TIME,                                                               "
							+ " CASE WHEN T1.PROCESS_START_TIME = TO_TIMESTAMP('1900-01-01 00:00:00', 'RRRR-MM-DD HH24:MI:SS') "
							+ " THEN '0' WHEN T1.PROCESS_END_TIME = TO_TIMESTAMP('1900-01-01 00:00:00', 'RRRR-MM-DD HH24:MI:SS') THEN TO_CHAR ( "
							+ " ROUND(TO_NUMBER (T1.PROCESS_START_TIME - SYSDATE) * 24 * 60 * 60),2) ELSE TO_CHAR( "
							+ " ROUND( NVL (TO_CHAR (TO_NUMBER (T1.PROCESS_END_TIME - T1.PROCESS_START_TIME) * 24 * 60 * 60 ),0),2)) END DURATION, "
							+ "         T1.RG_PROCESS_STATUS,                                                              "
							+ "         (SELECT AST.ALPHA_SUBTAB_DESCRIPTION                                               "
							+ "            FROM ALPHA_SUB_TAB AST                                                          "
							+ "           WHERE AST.ALPHA_TAB = T1.RG_PROCESS_STATUS_AT                                    "
							+ "             AND AST.ALPHA_SUB_TAB = T1.RG_PROCESS_STATUS) AS RG_PROCESS_STATUS_DESC,T1.SOURCE_TYPE,"
							+ "         (SELECT AST.ALPHA_SUBTAB_DESCRIPTION                                               "
							+ "            FROM ALPHA_SUB_TAB AST                                                          "
							+ "           WHERE AST.ALPHA_TAB = T1.SOURCE_TYPE_AT                                    "
							+ "             AND AST.ALPHA_SUB_TAB = T1.SOURCE_TYPE) AS SOURCE_TYPE_DESC,"
							+ "               T1.MAKER,T1.UPLOAD_FILE_NAME,T2.SCHEDULED_DATE,                                                                     "
							+ logDate
							+ "    FROM RG_PROCESS_CONTROL T1,                                                             "
							+ "         RG_SCHEDULES T2                                                                 "
							+ "   WHERE     T1.RG_PROCESS_ID = T2.RG_PROCESS_ID                                            "
							+ " AND T1.RG_PROCESS_ID =? ");

//			if (ValidationUtil.isValid(dObj.getProcessStatus())) {
//				strQueryAppr.append(" and T2.RG_SCHEDULE_STATUS = '" + dObj.getProcessStatus().toUpperCase() + "' ");
//			}

			if (ValidationUtil.isValid(SessionContextHolder.getContext().getUpdateRestrictionLeBook())) {
				strQueryAppr.append("and T1.COUNTRY " + commonDao.getDbFunction("PIPELINE") + " ' - ' "
						+ commonDao.getDbFunction("PIPELINE") + "T1.LE_BOOK IN ( "
						+ SessionContextHolder.getContext().getUpdateRestrictionLeBook().toUpperCase() + ") ");
			}
			String groupOrderBy = "ORDER BY T1.TEMPLATE_ID";
			Object[] args = { dObj.getProcessId() };
			strQueryAppr.append(groupOrderBy);
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), args, Panel3Mapper());
			if (collTemp != null && collTemp.size() > 0) {
				exceptionCode.setResponse(collTemp);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("success");
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getSummaryDetailsforPanel3(TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateScheduleVb> collTemp = new ArrayList<>();
		try {
			String sql = "  SELECT 																				"
					+ "         T1.RG_PROCESS_STATUS,                                                        "
					+ "         (SELECT AST.ALPHA_SUBTAB_DESCRIPTION                                         "
					+ "            FROM ALPHA_SUB_TAB AST                                                    "
					+ "           WHERE AST.ALPHA_TAB = T1.RG_PROCESS_STATUS_AT                              "
					+ "             AND AST.ALPHA_SUB_TAB = T1.RG_PROCESS_STATUS) AS RG_PROCESS_STATUS_DESC, "
					+ "         Count(1) Counter                                                             "
					+ "    FROM RG_PROCESS_CONTROL T1,                                                       "
					+ "         RG_SCHEDULES T2,                                                             "
					+ "         RG_TEMPLATE_CONFIG T3                                                        "
					+ "   WHERE     T1.RG_PROCESS_ID = T2.RG_PROCESS_ID                                      "
					+ "         AND T1.TEMPLATE_ID = T3.TEMPLATE_ID                                      "
					+ "         AND T1.COUNTRY = ?                                                           "
					+ "         AND T1.LE_BOOK = ?                                                           "
					+ "         AND T1.PROCESS_FREQUENCY = ?                                                 "
					+ "         AND  T2.REPORTING_DATE = TO_DATE(?,'DD-Mon-YYYY HH24:MI:SS') "
					// + " AND T1.TEMPLATE_NAME = ? "
					+ "  GROUP BY T1.RG_PROCESS_STATUS,T1.RG_PROCESS_STATUS_AT                               ";

			RowMapper mapper = new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
					templateScheduleVb.setCounter(rs.getString("Counter"));
					if (!ValidationUtil.isValid(rs.getString("Counter")))
						templateScheduleVb.setCounter("0");
					templateScheduleVb.setProcessStatus(rs.getString("RG_PROCESS_STATUS"));
					templateScheduleVb.setProcessStatusDesc(rs.getString("RG_PROCESS_STATUS_DESC"));
					return templateScheduleVb;
				}
			};

//			Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getProcessFrequency(),
//					dObj.getReportingDate(),dObj.getTemplateName() };
			Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getProcessFrequency(),
					dObj.getReportingDate() };
			collTemp = getJdbcTemplate().query(sql, args, mapper);
			if (collTemp != null && collTemp.size() > 0) {
				exceptionCode.setResponse(collTemp);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("success");
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	protected RowMapper Panel3Mapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
				templateScheduleVb.setCountry(rs.getString("COUNTRY"));
				templateScheduleVb.setLeBook(rs.getString("LE_BOOK"));
				templateScheduleVb.setTemplateId(rs.getString("TEMPLATE_ID"));
				templateScheduleVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				templateScheduleVb.setReportingDate(rs.getString("REPORTING_DATE"));
				templateScheduleVb.setStartTime(rs.getString("START_TIME"));
				templateScheduleVb.setEndTime(rs.getString("END_TIME"));
				templateScheduleVb.setDuration(commonDao.calcDuration(rs.getString("DURATION")));
				templateScheduleVb.setProcessStatus(rs.getString("RG_PROCESS_STATUS"));
				templateScheduleVb.setProcessStatusDesc(rs.getString("RG_PROCESS_STATUS_DESC"));
				templateScheduleVb.setLogFile(rs.getString("TEMPLATE_NAME").trim() + "_" + rs.getLong("MAKER") + "_"
						+ rs.getString("LOG_DATE") + ".err");
				templateScheduleVb.setSourceType(rs.getString("SOURCE_TYPE"));
				templateScheduleVb.setSourceTypeDesc(rs.getString("SOURCE_TYPE_DESC"));
				if (ValidationUtil.isValid(rs.getString("UPLOAD_FILE_NAME"))) {
					templateScheduleVb.setXlName(rs.getString("UPLOAD_FILE_NAME"));
				}
				if (ValidationUtil.isValid(rs.getString("SCHEDULED_DATE"))) {
					templateScheduleVb.setScheduleDate(rs.getString("SCHEDULED_DATE"));
				}
				return templateScheduleVb;
			}
		};
		return mapper;
	}

	protected RowMapper getPanelInfo1Mapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
				templateScheduleVb.setCountry(rs.getString("COUNTRY"));
				templateScheduleVb.setLeBook(rs.getString("LE_BOOK"));
				templateScheduleVb.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
				templateScheduleVb.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
				templateScheduleVb.setReportingDate(rs.getString("REPORTING_DATE"));
				templateScheduleVb.setDisplayReportingDate(rs.getString("DISPLAY_REPORTING_DATE"));
				templateScheduleVb.setProcessStatus(rs.getString("SCHEDULE_START_TIME"));
				templateScheduleVb.setLastSucessfulDate(rs.getString("DISPLAY_LAST_SUCCESSFUL_DATE"));
				return templateScheduleVb;
			}
		};
		return mapper;
	}

	protected RowMapper getPanelInfo2Mapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
				templateScheduleVb.setProcessId(rs.getString("RG_PROCESS_ID"));
				templateScheduleVb.setReportingDate(rs.getString("REPORTING_DATE"));
				templateScheduleVb.setStartTime(rs.getString("START_TIME"));
				templateScheduleVb.setEndTime(rs.getString("END_TIME"));
				templateScheduleVb.setProcessStatus(rs.getString("RG_SCHEDULE_STATUS"));
				templateScheduleVb.setProcessStatusDesc(rs.getString("RG_PROCESS_STATUS_DESC"));
				templateScheduleVb.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
				templateScheduleVb.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
				return templateScheduleVb;
			}
		};
		return mapper;
	}

	public int getCountErrored(TemplateScheduleVb vObject) {
		try {
			String query = " SELECT COUNT(1) FROM RG_PROCESS_CONTROL  WHERE COUNTRY = ? AND LE_BOOK = ? "
					+ " AND TEMPLATE_ID = ? AND SUBMISSION_DATE = ? AND RG_PROCESS_STATUS <> 'C' ";

			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),
					vObject.getReportingDate() };
			return getJdbcTemplate().queryForObject(query, args, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}

	public int doUpdateAppr(TemplateScheduleVb vObject) {
		String query = "";
		query = " Update " + vObject.getSourceTable().trim() + " SET VERSION_NO = ? ";
		Object[] args = { getVersionNo(vObject) };
		return getJdbcTemplate().update(query, args);
	}

	protected ExceptionCode doUpdateApprRecordForNonTrans(TemplateScheduleVb vObject) throws RuntimeCustomException {
		List<TemplateScheduleVb> collTemp = null;
		TemplateScheduleVb vObjectlocal = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		strApproveOperation = Constants.MODIFY;
		strErrorDesc = "";
		strCurrentOperation = Constants.MODIFY;
		setServiceDefaults();
		vObject.setMaker(getIntCurrentUserId());
		if ("RUNNING".equalsIgnoreCase(getBuildStatus(vObject))) {
			exceptionCode = getResultObject(Constants.BUILD_IS_RUNNING);
			throw buildRuntimeCustomException(exceptionCode);
		}
		vObject.setProcessStatus("P");
		retVal = doUpdateAppr(vObject);
		if (retVal != Constants.SUCCESSFUL_OPERATION) {
			throw new RuntimeCustomException(getResultObject(retVal));
		} else {
			exceptionCode.setErrorCode(retVal);
			exceptionCode.setErrorMsg("ReInitated successfully ");
		}
		vObject.setMaker(intCurrentUserId);
		return exceptionCode;
	}

	public ExceptionCode reviewDbData(TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateConfigVb> collTemp = getQueryResults(dObj);
		if (collTemp != null && !collTemp.isEmpty()) {
			for (TemplateConfigVb templateConfigVb : collTemp) {
				exceptionCode = reviewDbData(dObj, templateConfigVb);
			}
		}
		return exceptionCode;
	}

	public List<TemplateConfigVb> getQueryResults(TemplateScheduleVb dObj) {
		List<TemplateConfigVb> collTemp = null;
		String strQueryAppr = null;
		strQueryAppr = new String(" SELECT DISTINCT TAPPR.COUNTRY,                        "
				+ "        TAPPR.LE_BOOK,TAPPR.TEMPLATE_ID, "
				+ "        TAPPR.TEMPLATE_DESCRIPTION,TAPPR.PROCESS_FREQUENCY_AT," + "        TAPPR.PROCESS_FREQUENCY,"
				+ "(SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUB_TAB = TAPPR.PROCESS_FREQUENCY AND ALPHA_TAB =TAPPR.PROCESS_FREQUENCY_AT)PROCESS_FREQUENCY_DESC,"
				+ "        TAPPR.TYPE_OF_SUBMISSION_AT," + "        TAPPR.TYPE_OF_SUBMISSION,TAPPR.CSV_DELIMITER_AT,"
				+ "        TAPPR.CSV_DELIMITER,TAPPR.SOURCE_TYPE_AT,"
				+ "        TAPPR.SOURCE_TYPE,TAPPR.DB_CONNECTIVITY_TYPE,"
				+ "        TAPPR.DB_CONNECTIVITY_DETAILS,TAPPR.SOURCE_TABLE,"
				+ "        TAPPR.SOURCE_TABLE_FILTER,TAPPR.SOURCE_TABLE_READINESS,"
				+ "        TAPPR.UPLOAD_FILE_NAME,TAPPR.API_CONNECTIVITY_TYPE,"
				+ "        TAPPR.API_CONNECTIVITY_DETAILS,TAPPR.AUTH_CONNECTIVITY_TYPE,"
				+ "        TAPPR.AUTH_CONNECTIVITY_DETAILS,TAPPR.MAIN_API_STRUCTURE,"
				+ "        TAPPR.TEMPLATE_STATUS,T3.NUM_SUBTAB_DESCRIPTION TEMPLATE_STATUS_DESC,"
				+ "        TAPPR.RECORD_INDICATOR_NT,"
				+ "        TAPPR.RECORD_INDICATOR,T4.NUM_SUBTAB_DESCRIPTION RECORD_INDICATOR_DESC,"
				+ "        TAPPR.RECORD_INDICATOR,TAPPR.MAKER," + "        TAPPR.VERIFIER," + " "
				+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null) + " DATE_LAST_MODIFIED," + " "
				+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null)
				+ " DATE_CREATION,TAPPR.RG_SUBMISSION_TIME,TAPPR.AUTO_SUBMIT,"
				+ "TAPPR.DATALIST,TAPPR.CATEGORY_TYPE,TAPPR.CBK_FILE_NAME" + " FROM RG_TEMPLATE_CONFIG TAPPR"
				+ " ,NUM_SUB_TAB T3,NUM_SUB_TAB T4 Where "
				+ " t3.NUM_tab =  TAPPR.TEMPLATE_STATUS_NT AND t3.NUM_sub_tab = TAPPR.TEMPLATE_STATUS"
				+ " AND t4.NUM_tab = TAPPR.RECORD_INDICATOR_NT AND t4.NUM_sub_tab = TAPPR.RECORD_INDICATOR"
				+ " AND TAPPR.COUNTRY =? AND TAPPR.LE_BOOK = ? AND  TAPPR.TEMPLATE_ID = ?");

		Object[] args = { dObj.getCountry(), dObj.getLeBook(), dObj.getTemplateId() };

		try {
			logger.info("Executing approved query");
			collTemp = getJdbcTemplate().query(strQueryAppr.toString(), args, getDetailMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");

			logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));

			if (args != null)
				for (int i = 0; i < args.length; i++)
					logger.error("objParams[" + i + "]" + args[i].toString());
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
				vObject.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
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
				vObject.setTemplateStatusDesc(rs.getString("TEMPLATE_STATUS_DESC"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_Desc"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				vObject.setDataList(rs.getString("DATALIST"));
				vObject.setSubmissionTime(rs.getString("RG_SUBMISSION_TIME"));
				vObject.setCbkFileName(rs.getString("CBK_FILE_NAME"));
				vObject.setCategoryType(rs.getString("CATEGORY_TYPE"));
				return vObject;
			}
		};
		return mapper;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected RowMapper getMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				Map<String, Object> row = new HashMap<>(columnCount);

				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object columnValue = rs.getObject(i);
					int columnType = metaData.getColumnType(i);
					if (columnValue == null) {
						columnValue = "";
					} else if (columnValue instanceof Timestamp) {
						Timestamp timestamp = (Timestamp) columnValue;
						String timestampString = timestamp.toString();

						if (timestampString.contains(" ")) {
							SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
							try {
								Date date = inputFormat.parse(timestampString);
								// Format the Date to the desired output format
								columnValue = outputFormat.format(date);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else if (columnType == java.sql.Types.NUMERIC && metaData.getScale(i) > 0) {
						if (columnValue != null) {
							BigDecimal bigDecimalValue = new BigDecimal(columnValue.toString());
							columnValue = bigDecimalValue.toPlainString();
						}
					} else if (columnValue instanceof Integer || columnValue instanceof Long
							|| columnValue instanceof Float || columnValue instanceof Double) {
						columnValue = columnValue.toString();
					}

					row.put(columnName, columnValue);
				}

				return row;
			}
		};
		return mapper;

	}

	public List<TemplateScheduleVb> extractData(TemplateScheduleVb dObj, TemplateConfigVb vObject) {
		Vector<Object> params = new Vector<Object>();

		try {
			List<ColumnHeadersVb> lst = getColumns(vObject.getSourceTable().toUpperCase().trim());
			StringJoiner joiner = new StringJoiner(",");
			if (lst != null && lst.size() > 0) {
				if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
					for (ColumnHeadersVb columnHeadersVb : lst) {
						if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
								|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
								|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
								&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0"))
							joiner.add(
									dbFunctionFormats("TAPPR." + columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5")
											+ " " + columnHeadersVb.getDbColumnName());
						else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
								|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
								|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
							joiner.add(dateFormat + "(TAPPR." + columnHeadersVb.getDbColumnName() + "," + formatdate
									+ ") " + columnHeadersVb.getDbColumnName());
						else
							joiner.add("TAPPR." + columnHeadersVb.getDbColumnName());
					}
				} else {
					for (ColumnHeadersVb columnHeadersVb : lst) {
//						if ("Y".equalsIgnoreCase(precisionFlag)) {
						if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
								|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
								|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
								&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0")
								&& "Y".equalsIgnoreCase(precisionFlag))
							joiner.add(dbFunctionFormats(columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5") + " "
									+ columnHeadersVb.getDbColumnName());
						else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
								|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
								|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
							joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + formatdate + ") "
									+ columnHeadersVb.getDbColumnName());
//						}
						else
							joiner.add(columnHeadersVb.getDbColumnName());
					}
				}

			}

			StringBuffer query = new StringBuffer("SELECT 'APPR' TAB, " + joiner + " FROM ");
			String whereNotExists = getStringWhrNotExist(vObject.getSourceTable(), true);
			if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
				query = query.append(
						vObject.getSourceTable().toUpperCase().trim() + " TAPPR," + vObject.getCbkFileName() + " ");
			} else {
				query = query.append(vObject.getSourceTable().toUpperCase().trim() + " TAPPR");
			}
			if (ValidationUtil.isValid(dObj.getBasicFilterStr()) && !dObj.getBasicFilterStr().isEmpty()) {
				CommonUtils.addToQuery(dObj.getBasicFilterStr(), query);
			}
			if (vObject.getSourceTableFilter() != null && !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				CommonUtils.addToQuery(vObject.getSourceTableFilter().replace("T1", "TAPPR"), query);
				if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
					query.append(" AND     TAPPR.COUNTRY = T3.COUNTRY    AND TAPPR.LE_BOOK = T3.LE_BOOK"
//							+ "       AND TAPPR.CUSTOMER_ID = T3.CUSTOMER_ID"
//							+ "       AND TAPPR.MESSAGE_REF_ID = T2.MESSAGE_REF_ID"
							);
				}
				StringBuffer newQuery = new StringBuffer("SELECT * FROM (" + query + ") TAPPR");
				query = newQuery;
			}
			String pend = query.toString().replace("'APPR'", "'PEND'");
			pend = pend.toString().replace("TAPPR", "TPEND");
			pend = pend.replaceAll(vObject.getSourceTable().toUpperCase().trim(),
					vObject.getSourceTable().toUpperCase().trim() + "_PEND");
			StringBuffer queryPend = new StringBuffer(pend);
			String orderBy = " ORDER BY ROW_ID,VERSION_NO DESC";
			return getQueryPopupResults(dObj, queryPend, query, whereNotExists, orderBy, params);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ExceptionCode extractDataToCsv(TemplateConfigVb vObject, String filter) {
		ExceptionCode exceptionCode = new ExceptionCode();

		try {
//			String versionNo = " WHERE VERSION_NO = (SELECT MAX(VERSION_NO ) FROM " + vObject.getSourceTable() + " )";
			List<ColumnHeadersVb> lst = getNeededColumnsForDownload(vObject.getSourceTable().toUpperCase().trim());
			StringJoiner joiner = new StringJoiner(",");
			if (lst != null && lst.size() > 0) {
				for (ColumnHeadersVb columnHeadersVb : lst) {
					if (columnHeadersVb.getColType().equalsIgnoreCase("D") && "Y".equalsIgnoreCase(precisionFlag))
						joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + formatdate + ") "
								+ columnHeadersVb.getDbColumnName());
					else
						joiner.add(columnHeadersVb.getDbColumnName());
				}

			}
			String query = "SELECT " + joiner + " FROM " + vObject.getSourceTable();
//			query = query.concat(versionNo);
			if (ValidationUtil.isValid(filter)) {
				query = query.concat(" WHERE " + filter);
			}
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				query = query + " AND " + vObject.getSourceTableFilter();
			}
			String oderBy = " ORDER BY ROW_ID ";

			query = query.concat(oderBy);
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			String csvFilePath = tmpFilePath + File.separator + vObject.getSourceTable() + ".csv";

			try (FileWriter fileWriter = new FileWriter(csvFilePath)) {
				// Execute the query and process the ResultSet
				getJdbcTemplate().query(query, new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						try {
							writeResultSetToCsv(rs, fileWriter);
						} catch (IOException e) {
							throw new SQLException("Error writing to CSV file", e);
						}
					}
				});
			}

			exceptionCode.setOtherInfo(vObject.getSourceTable());
			exceptionCode.setResponse(tmpFilePath);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);

		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}

		return exceptionCode;
	}

//	private void writeResultSetToCsv(ResultSet rs, FileWriter fileWriter) throws SQLException, IOException {
//		ResultSetMetaData metaData = rs.getMetaData();
//		int columnCount = metaData.getColumnCount();
//
//		// Write column Headers to CSV file
//		for (int i = 1; i <= columnCount; i++) {
//			fileWriter.append(metaData.getColumnName(i));
//			if (i < columnCount) {
//				fileWriter.append("|");
//			}
//		}
//		fileWriter.append("\n");
//
//		// Write datas rows to CSV file
//		do {
//			for (int i = 1; i <= columnCount; i++) {
//				fileWriter.append(rs.getString(i));
//				if (i < columnCount) {
//					fileWriter.append("|");
//				}
//			}
//			fileWriter.append("\n");
//		} while (rs.next());
//	}

	private void writeResultSetToCsv(ResultSet rs, FileWriter fileWriter) throws SQLException, IOException {
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();

		// Write column headers to CSV file
		for (int i = 1; i <= columnCount; i++) {
			fileWriter.append(metaData.getColumnName(i));
			if (i < columnCount) {
				fileWriter.append("|");
			}
		}
		fileWriter.append("\n");

		// Define date and timestamp formats
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat timestampFormat = new SimpleDateFormat("dd-MMM-yyyy");

		// Write data rows to CSV file
		do {
			for (int i = 1; i <= columnCount; i++) {
				int columnType = metaData.getColumnType(i);

				// Check if the column is a Date or Timestamp
				if (columnType == java.sql.Types.DATE) {
					java.sql.Date date = rs.getDate(i);
					if (date != null) {
						fileWriter.append(dateFormat.format(date));
					} else {
						fileWriter.append(""); // Handle null values
					}
				} else if (columnType == java.sql.Types.TIMESTAMP) {
					java.sql.Timestamp timestamp = rs.getTimestamp(i);
					if (timestamp != null) {
						fileWriter.append(timestampFormat.format(timestamp));
					} else {
						fileWriter.append(""); // Handle null values
					}
				} else {
					// For other types, write as string
					String value = rs.getString(i);
					if (value != null) {
						fileWriter.append(value);
					} else {
						fileWriter.append(""); // Handle null values
					}
				}

				if (i < columnCount) {
					fileWriter.append("|");
				}
			}
			fileWriter.append("\n");
		} while (rs.next());

		fileWriter.flush();
	}

	public ExceptionCode reviewDbData(TemplateScheduleVb dObj, TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<ColumnHeadersVb> colHeaders = null;
		String columnHeaderXml = "";
		try {
			TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
			if (vObject != null) {
				ReportsVb reportsVb = new ReportsVb();
				reportsVb.setReportId(dObj.getTemplateId());
				reportsVb.setSubReportId(dObj.getTemplateId());
				List<ColumnHeadersVb> columnHeadersXmllst = reportsDao.getReportColumns(reportsVb);
				if (columnHeadersXmllst != null && columnHeadersXmllst.size() > 0) {
					columnHeaderXml = columnHeadersXmllst.get(0).getColumnXml();
					colHeaders = getColumnHeaders(columnHeaderXml);
					templateScheduleVb.setColumnHeaderLst(colHeaders);
				}
				List<TemplateScheduleVb> collTemp =new ArrayList<>();
				if ("HIS".equalsIgnoreCase(dObj.getTable())) {
					collTemp = extractHistoryData(dObj, vObject);
				} else {
					collTemp = extractData(dObj, vObject);
				}
				templateScheduleVb.setTemplateId(vObject.getTemplateId());
				templateScheduleVb.setCountry(vObject.getCountry());
				templateScheduleVb.setLeBook(vObject.getLeBook());
				List<TemplateErrorsVb> errorLst = getErrorList(vObject);
				if (errorLst != null && errorLst.size() > 0) {
					Map<Object, Map<String, Object>> responseWithRowId = errorLst.stream()
							.collect(Collectors.groupingBy(data -> data.getRowId(), // Group by ROW_ID
									Collectors.toList()))
							.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, // Keep ROW_ID as the key
									entry -> {
										Map<String, Object> map = new HashMap<>();
										map.put("ROW_ID", entry.getKey());
										map.put("VALUES", entry.getValue()); // Append grouped list
										return map;
									}));

					// Set the response
					exceptionCode.setResponse1(responseWithRowId);
				}

				templateScheduleVb.setSourceTable(vObject.getSourceTable().toUpperCase().trim());
				templateScheduleVb.setTypeOfSubmission(vObject.getTypeOfSubmission());
				templateScheduleVb.setDataLst(collTemp);

				if (getPendCount(templateScheduleVb) > 0)
					exceptionCode.setOtherInfo("Y");
				if (getcntSourceTable(templateScheduleVb) > 0)
					exceptionCode.setResponse2("Y");
				else
					exceptionCode.setResponse2("N");
				templateScheduleVb.setTotalRows(dObj.getTotalRows());
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setResponse(templateScheduleVb);

			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);

			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}

	public List<TemplateScheduleVb> getTemplateSchedulesAudit(TemplateScheduleVb dObj) {
		String logCondition = "";
		if (!dObj.isReview()) {
			logCondition = "AND T1.DATETIME_STAMP >= T2.PROCESS_START_TIME ";
		}

		String query = new String("Select T1.Audit_Trail_Sequence_Id, " + dateFormat + "(Datetime_Stamp ,"
				+ dateFormatStr + ")Datetime_Stamp"
				+ ",T1.Audit_Description,T1.Audit_Description_Detail From RG_SUBMIT_AUDIT_TRAIL T1,RG_PROCESS_CONTROLS T2 "
				+ " Where "
				+ " T1.COUNTRY = T2.COUNTRY AND T1.LE_BOOK= T2.LE_BOOK AND T1.TEMPLATE_ID = T2.TEMPLATE_ID  AND T1.REPORTING_DATE = T2.REPORTING_DATE "
//				+ " and T1.submission_date = T2.submission_date   "
				+ logCondition + " AND T1.Country = ? And T1.Le_Book = ? " + "And T1.REPORTING_DATE = " + dateConvert
//				+ " and t1.submission_date = " + dateConvert + ""
						+ " And T1.TEMPLATE_ID = ?");
		String orderBy = " Order by Audit_Trail_Sequence_Id desc";
		Object[] params = { dObj.getCountry(), dObj.getLeBook(), dObj.getReportingDate(),
				dObj.getTemplateId() };
		List<TemplateScheduleVb> collTemp = null;
		try {
			collTemp = getJdbcTemplate().query(query.concat(" " + orderBy), params, getMapperAudit());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return collTemp;
	}

	protected RowMapper getMapperAudit() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb adfSchedulesDetailsVb = new TemplateScheduleVb();
				adfSchedulesDetailsVb.setAuditTrailSequenceId(rs.getString("Audit_Trail_Sequence_Id"));
				adfSchedulesDetailsVb.setDateTimeStamp(rs.getString("Datetime_Stamp"));
				adfSchedulesDetailsVb.setAuditDescription(rs.getString("Audit_Description"));
				adfSchedulesDetailsVb.setAuditDescriptionDetail(rs.getString("Audit_Description_Detail"));
				return adfSchedulesDetailsVb;
			}
		};
		return mapper;
	}

	public ExceptionCode exportReviewData(TemplateScheduleVb dObj, String type) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateConfigVb> collTemp = getQueryResults(dObj);
		if (collTemp != null && !collTemp.isEmpty()) {
			for (TemplateConfigVb templateConfigVb : collTemp) {
				List<TemplateMappingVb> mappingLst = templateMappingDao.getQueryResults(templateConfigVb,
						Constants.STATUS_ZERO);
				if (mappingLst != null && !mappingLst.isEmpty()) {
					templateConfigVb.setMappinglst(mappingLst);
				}
				if ("HIS".equalsIgnoreCase(dObj.getTable())) {
//					templateConfigVb.setSourceTable(templateConfigVb.getSourceTable() + "_HIS");
					exceptionCode = extractHistoryDataForExport(dObj, templateConfigVb, type, dObj.isErrorFlag());
					return exceptionCode;
				} else {
					exceptionCode = reviewExportData(templateConfigVb, type, dObj.isErrorFlag(),
							dObj.getBasicFilterStr());
				}
			}
		}
		return exceptionCode;
	}

	public ExceptionCode reviewExportData(TemplateConfigVb vObject, String type, boolean errorFlag, String filter) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {

			if (type.equalsIgnoreCase("CSV")) {
				exceptionCode = extractDataToCsv(vObject, filter);
			} else if (type.equalsIgnoreCase("XL")) {
				exceptionCode = extractDataToExcel(vObject, errorFlag, filter);
			} else if (type.equalsIgnoreCase("XML")) {
				exceptionCode = extractDataToXml(vObject, filter);
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);

			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}

	public ExceptionCode extractDataToExcel(TemplateConfigVb vObject, boolean errorFlag, String filter) {

		ExceptionCode exceptionCode = new ExceptionCode();
		setServiceDefaults();
		final String orderBy = " ORDER BY VERSION_NO, ROW_ID ";

		try {
			List<ColumnHeadersVb> columns = getNeededColumnsForDownload(vObject.getSourceTable().toUpperCase().trim());
			StringJoiner selectColumns = new StringJoiner(",");

			if (columns != null && !columns.isEmpty()) {
				for (ColumnHeadersVb column : columns) {
					String columnName = column.getDbColumnName();
					if ("D".equalsIgnoreCase(column.getColType()) && "Y".equalsIgnoreCase(precisionFlag)) {
						selectColumns.add(dateFormat + "(" + columnName + "," + formatdate + ") AS " + columnName);
					} else {
						selectColumns.add(columnName);
					}
				}
			}

			String sourceTable = vObject.getSourceTable().trim();
			String baseQuery;
			String errorQuery = "";
			boolean isError = errorFlag;

			baseQuery = String.format("SELECT %s FROM %s TAPPR", selectColumns, sourceTable);

			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				baseQuery += " AND " + vObject.getSourceTableFilter();
			}

			String tempDir = System.getProperty("java.io.tmpdir");
			String fileSuffix = isError ? "_Errors" : "";
			String excelFilePath = tempDir + File.separator + sourceTable + fileSuffix + ".xlsx";

			exceptionCode.setOtherInfo(sourceTable + fileSuffix);

			SXSSFWorkbook workbook = new SXSSFWorkbook();

			if (isError) {
				Sheet summarySheet = workbook.createSheet("Summary");

				String assetFolderUrl = commonDao.getAssetFolderUrl(servletContext);
				Map<Integer, XSSFCellStyle> styles = ExcelExportUtil.createStyles1(workbook, "4285F4");

				vObject.setMaker(intCurrentUserId);
				templateConfigWb.getScreenDao().fetchMakerVerifierNames(vObject);

				ReportsVb reports = new ReportsVb();
				reports.setApplicationTheme("8e8e8e");
				reports.setReportTitle(vObject.getTemplateDescription() + " - Errors");
				reports.setMakerName(vObject.getMakerName());

				ExcelExportUtil.createPromptsPage(reports, summarySheet, workbook, assetFolderUrl, styles, 0);
			}

			Sheet dataSheet = workbook.createSheet(sourceTable.toUpperCase());
			String whereNotExists = getStringWhrNotExist(sourceTable, true);

			StringBuilder fullQuery = new StringBuilder();
			fullQuery.append(baseQuery);

			// Check if baseQuery already has a WHERE clause
			boolean skipWhereNotExists = ValidationUtil.isValid(filter)
					&& filter.toUpperCase().contains("VERSION_NO = 'ALL'");
			if (!skipWhereNotExists) {
				boolean hasWhere = baseQuery.toUpperCase().contains("WHERE");

				if (hasWhere) {
					fullQuery.append(" AND ").append(whereNotExists);
				} else {
					fullQuery.append(" WHERE ").append(whereNotExists);
				}
			}
			// Append UNION with _PEND table
			fullQuery.append(" UNION ").append(baseQuery.replace(sourceTable + " TAPPR", sourceTable + "_PEND TPEND"));

			// Apply outer filter if any
			if (isError) {
				fullQuery = new StringBuilder(
						"SELECT * FROM (" + fullQuery + ") T1 JOIN RG_TEMPLATES_ERRORS T2 ON T1.ROW_ID = T2.ROW_ID "
								+ "  AND T1.VERSION_NO = T2.VERSION_NO" + " WHERE T2.TEMPLATE_ID = '"
								+ vObject.getTemplateId() + "'");

				errorQuery = String.format("SELECT * FROM RG_TEMPLATES_ERRORS WHERE TEMPLATE_ID = '%s'",
						vObject.getTemplateId().trim());
			}
			if (ValidationUtil.isValid(filter) && !isError && !skipWhereNotExists)
				fullQuery = new StringBuilder("SELECT * FROM (" + fullQuery + ") t1 WHERE " + filter);

			// Apply ordering
			if (!isError) {
				fullQuery.append(" ").append(orderBy);
			}

			getJdbcTemplate().query(fullQuery.toString(), rs -> {
				try {
					writeResultSetToExcel(rs, dataSheet);
				} catch (IOException e) {
					throw new SQLException("Error writing to Excel file", e);
				}
			});

			if (isError) {
				Sheet errorSheet = workbook.createSheet("TEMPLATES_ERRORS");
				getJdbcTemplate().query(errorQuery, rs -> {
					try {
						writeResultSetToExcel(rs, errorSheet);
					} catch (IOException e) {
						throw new SQLException("Error writing flagged data to Excel", e);
					}
				});
			}

			try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
				workbook.write(fileOut);
			}
			workbook.close();

			exceptionCode.setResponse(tempDir);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);

		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}

		return exceptionCode;

	}

	private void writeResultSetToExcel(ResultSet rs, Sheet sheet) throws SQLException, IOException {
//		ResultSetMetaData rsmd = rs.getMetaData();
//		int columnCount = rsmd.getColumnCount();
//
//		// Create header row
//		Row headerRow = sheet.createRow(0);
//		for (int i = 1; i <= columnCount; i++) {
//			Cell cell = headerRow.createCell(i - 1);
//			cell.setCellValue(rsmd.getColumnName(i));
//		}
//
//		// Write data rows
//		int rowNum = 1;
//		do {
//			Row row = sheet.createRow(rowNum++);
//			for (int i = 1; i <= columnCount; i++) {
//				Cell cell = row.createCell(i - 1);
//				cell.setCellValue(rs.getString(i));
//			}
//		} while (rs.next());
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		// Create a DataFormat object and a CellStyle for date formatting
		Workbook workbook = sheet.getWorkbook();
		DataFormat dataFormat = workbook.createDataFormat();
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(dataFormat.getFormat("dd-MMM-yyyy")); // Date format

		// Create header row
		Row headerRow = sheet.createRow(0);
		for (int i = 1; i <= columnCount; i++) {
			Cell cell = headerRow.createCell(i - 1);
			cell.setCellValue(rsmd.getColumnName(i));
		}

		// Write data rows
		int rowNum = 1;
		do {
			Row row = sheet.createRow(rowNum++);
			for (int i = 1; i <= columnCount; i++) {
				Cell cell = row.createCell(i - 1);

				// Check the column type to format dates correctly
				int columnType = rsmd.getColumnType(i);
				if (columnType == java.sql.Types.TIMESTAMP) {
					// If the column is a date, set the cell value with a formatted date
					java.sql.Date date = rs.getDate(i);
					if (date != null) {
						cell.setCellValue(date);
						cell.setCellStyle(dateCellStyle);
					}
				} else {
					// For other types, set the cell value as string
					cell.setCellValue(rs.getString(i));
				}
			}
		} while (rs.next());
	}

//	public ExceptionCode writeXmlDataToFile(ResultSet rs, String fileName) {
//		ExceptionCode exceptionCode = new ExceptionCode();
//
//		try {
//			// Generate XML from ResultSet
//			String xmlData = writeResultSetToXml(rs);
//
//			// Beautify the generated XML
//			String beautifiedXmlData = beautifyXML(xmlData);
//
//			// Write Beautified XML to file
//			try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
//				writer.write(beautifiedXmlData);
//				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
////				System.out.println("Beautified XML data has been successfully written to " + fileName);
//			} catch (IOException e) {
//				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//				exceptionCode.setErrorMsg(e.getMessage());
//				exceptionCode.setErrorSevr("N");
//			}
//		} catch (SQLException | ParserConfigurationException e) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//			exceptionCode.setErrorSevr("N");
//		}
//
//		return exceptionCode;
//	}

	public ArrayList<ColumnHeadersVb> getColumnHeaders(String colHeadersXml) {
		ArrayList<ColumnHeadersVb> colHeaders = new ArrayList<ColumnHeadersVb>();
		if (ValidationUtil.isValid(colHeadersXml)) {
			colHeadersXml = ValidationUtil.isValid(colHeadersXml)
					? colHeadersXml.replaceAll("\n", "").replaceAll("\r", "")
					: "";
			String colDetXml = CommonUtils.getValueForXmlTag(colHeadersXml, "COLUMNS");
			int colCount = 0;
			colCount = Integer.parseInt(CommonUtils.getValueForXmlTag(colDetXml, "COLUMN_COUNT"));
			for (int i = 1; i <= colCount; i++) {
				ColumnHeadersVb colHeadersVb = new ColumnHeadersVb();
				String refXml = CommonUtils.getValueForXmlTag(colDetXml, "COLUMN" + i);
				if (!ValidationUtil.isValid(refXml))
					continue;
				if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "LABLE_ROW_NUM")))
					colHeadersVb
							.setLabelRowNum(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "LABLE_ROW_NUM")));

				if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "LABLE_COL_NUM")))
					colHeadersVb
							.setLabelColNum(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "LABLE_COL_NUM")));

				String caption = CommonUtils.getValueForXmlTag(refXml, "CAPTION");
				// #PROMPT_VALUE_1#!@DD-Mon-RRRR!@-0

				colHeadersVb.setCaption(caption);

				// colHeadersVb.setCaption(CommonUtils.getValueForXmlTag(refXml, "CAPTION"));
				colHeadersVb.setColType(CommonUtils.getValueForXmlTag(refXml, "COL_TYPE"));

				if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "ROW_SPAN")))
					colHeadersVb.setRowspan(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "ROW_SPAN")));

				if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "COL_SPAN")))
					colHeadersVb.setColspan(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "COL_SPAN")));
				String dbColName = CommonUtils.getValueForXmlTag(refXml, "SOURCE_COLUMN").toUpperCase();
				colHeadersVb.setDbColumnName(dbColName);
				String drillDownLabel = CommonUtils.getValueForXmlTag(refXml, "DRILLDOWN_LABEL_FLAG");
				if (ValidationUtil.isValid(drillDownLabel) && "Y".equalsIgnoreCase(drillDownLabel)) {
					colHeadersVb.setDrillDownLabel(true);
				} else {
					colHeadersVb.setDrillDownLabel(false);
				}
				colHeadersVb.setScaling(CommonUtils.getValueForXmlTag(refXml, "SCALING"));
				colHeadersVb.setColumnWidth(CommonUtils.getValueForXmlTag(refXml, "COLUMN_WIDTH"));
				colHeadersVb.setGroupingFlag(CommonUtils.getValueForXmlTag(refXml, "GROUPING_FLAG"));
				String sumFlag = CommonUtils.getValueForXmlTag(refXml, "SUM_FLAG");
				if (ValidationUtil.isValid(sumFlag)) {
					colHeadersVb.setSumFlag(sumFlag);
				} else {
					colHeadersVb.setSumFlag("N");
				}
				String readOnly = CommonUtils.getValueForXmlTag(refXml, "READ_ONLY");
				colHeadersVb.setReadOnly(readOnly);
				String showValues = CommonUtils.getValueForXmlTag(refXml, "SHOW_VALUES");
				colHeadersVb.setShowValue(showValues);
				String dec = CommonUtils.getValueForXmlTag(refXml, "DECIMALCNT");
				colHeadersVb.setDecimalCnt(dec);
				colHeaders.add(colHeadersVb);
			}
		}
		return colHeaders;
	}

//	public ExceptionCode doInsertionRecord(String query) {
//		ExceptionCode exceptionCode = new ExceptionCode();
//		try {
//			retVal = getJdbcTemplate().update(query);
//			if (retVal == Constants.ERRONEOUS_OPERATION) {
//				exceptionCode = getResultObject(retVal);
//				throw buildRuntimeCustomException(exceptionCode);
//			} else {
//				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
//			}
//		} catch (Exception e) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//		return exceptionCode;
//
//	}
	public ExceptionCode doInsertionRecord(TemplateScheduleVb vObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			retVal = getJdbcTemplate().update(vObj.getQuery());
		} catch (DataIntegrityViolationException e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(parseErrorMsg(e));
			return exceptionCode;
		}catch (UncategorizedSQLException e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(parseErrorMsg(e));
			return exceptionCode;
		}
		catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
			if (retVal == Constants.ERRONEOUS_OPERATION) {
				exceptionCode = getResultObject(retVal);
				throw buildRuntimeCustomException(exceptionCode);
			} else {
				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
			}
		return exceptionCode;

	}

	public String DateString(String value, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		String date = "";
		try {
			// Attempt to parse the string
			dateFormat.parse(value);
//			if(value.contains(":")) {
			date = dbFunctionFormats(value, "TO_FORMAT", null);
//			}else {
//				date = dbFunctionFormats(value, "TO_DATE_FORMAT", null);
//			}
			return date;
		} catch (ParseException | java.text.ParseException e) {
			// Parsing failed, not a valid date
			return date;
		}
	}

//	public String getStringWhrNotExist(String tableName, boolean flag) {
//		String query = " EXISTS  (SELECT * FROM " + tableName.trim() + "_PEND TPEND  ";
//		if (flag)
//			query = " NOT EXISTS  (SELECT 'X' FROM " + tableName.trim() + "_PEND TPEND  ";
//
//		StringBuffer finalQuery = new StringBuffer(query);
//		List<CommonVb> colNames = commonDao.getPrimaryKey(tableName);
//		if (colNames != null && colNames.size() > 0) {
//			finalQuery.append(" WHERE ");
//			for (int i = 0; i < colNames.size(); i++) {
//				if (!colNames.get(i).getScreenName().equalsIgnoreCase("VERSION_NO")) {
//					if (i > 0) {
//						finalQuery.append(" AND ");
//					}
//					finalQuery.append(
//							"TAppr." + colNames.get(i).getScreenName() + " = TPend." + colNames.get(i).getScreenName());
//				}
//
//			}
//		}
//		finalQuery.append(" )");
//		return finalQuery.toString();
//	}
	public String getStringWhrNotExist(String tableName, boolean flag) {
		String query = " EXISTS  (SELECT * FROM " + tableName.trim() + "_PEND TPEND  ";
		if (flag)
			query = " NOT EXISTS  (SELECT 'X' FROM " + tableName.trim() + "_PEND TPEND  ";

		StringBuffer finalQuery = new StringBuffer(query);
		List<CommonVb> colNames = commonDao.getPrimaryKey(tableName);
		if (colNames != null && colNames.size() > 0) {
			int size = 0;
			CommonUtils.addToOrderByQuery(" WHERE ", finalQuery);
			for (int i = 0; i < colNames.size(); i++) {

				if (!colNames.get(i).getScreenName().equalsIgnoreCase("VERSION_NO")) {
					if (size > 0) {
						CommonUtils.addToOrderByQuery(" AND ", finalQuery);
					}
					CommonUtils.addToOrderByQuery("TAppr." + colNames.get(i).getScreenName() + " = TPend."
							+ colNames.get(i).getScreenName() + " ", finalQuery);
					size++;
				}

			}
		}
		finalQuery.append(" )");
		return finalQuery.toString();
	}

	public int getQueryExtractData(TemplateScheduleVb dObj, String tableName) {
		StringJoiner whereJoiner = new StringJoiner(" AND ");
		try {
			List<CommonVb> colNames = commonDao.getPrimaryKey(tableName);
			StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM ");
			query.append(tableName);
			query.append(" where ");

			List<Map<String, Object>> dataList = (List<Map<String, Object>>) dObj.getDataLst();
			for (Map<String, Object> dataMap : dataList) {
				whereJoiner = new StringJoiner(" AND ");

				for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
					String columnName = entry.getKey();
					Object value = entry.getValue();

					for (CommonVb commonVb : colNames) {
						if (commonVb.getScreenName().equalsIgnoreCase(columnName)) {
							// Construct the WHERE clause for primary key columns
							if (commonVb.getActionType().equalsIgnoreCase(columnName)) {
								value = DateString(value.toString(), "dd-MMM-yyyy HH:mm:ss");
								whereJoiner.add(columnName + "=" + value.toString());
							} else if (value instanceof String) {
								whereJoiner.add(columnName + "='" + value + "'");
							} else {
								whereJoiner.add(columnName + "=" + value);
							}
						}
					}
				}
			}
			query.append(whereJoiner);
			return getJdbcTemplate().queryForObject(query.toString(), Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public ExceptionCode getTemplateScheduleDetails(TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateScheduleVb> collTemp = new ArrayList<>();
		setServiceDefaults();
		String startDate = "";
		String endDate = "";
		String cond = "";
		String dependentCond =" ";
		String listTaggCond =" ";
		String cbStatus= "";
		String cbStatusGroupBy= "";
		if (dObj.getCategoryType().equalsIgnoreCase("RG")) {
			dependentCond = "	 JOIN RG_TEMPLATE_DEPENDENCY  T4 " + "	 ON     T3.TEMPLATE_ID = T4.TEMPLATE_NAME "
					+ "	AND T3.COUNTRY = T4.COUNTRY " + "	  AND T3.LE_BOOK = T4.LE_BOOK   ";
			listTaggCond = " , "+getDbFunction("STRING_AGG")+"(T4.DEPENDENT_TEMPLATE "+getDbFunction("PIPELINE")+" '-' "+getDbFunction("PIPELINE")+" T3.RG_PROCESS_STATUS, ',') "
					+ "	WITHIN GROUP (ORDER BY T4.SUBMISSION_ORDER)  DEPENDENT_TEMPLATES ";
		}
		 cbStatus = "ORACLE".equalsIgnoreCase(databaseType)
			        ? " DBMS_LOB.SUBSTR(T1.CB_STATUS, 4000, 1) AS CB_STATUS "
			        : " T1.CB_STATUS ";
		 cbStatusGroupBy = "ORACLE".equalsIgnoreCase(databaseType)
			        ? " DBMS_LOB.SUBSTR(T1.CB_STATUS, 4000, 1)  "
			        : " T1.CB_STATUS ";
		try {
			StringBuffer strQueryAppr = new StringBuffer(" SELECT " + "		 T1.TEMPLATE_ID ," + "		 T1.COUNTRY,"
					+ "	     T1.LE_BOOK," + "      T1.TEMPLATE_NAME ," + "      T2.PROCESS_FREQUENCY ,"
					+ feedFrequencyAtDesc + "      ,T1.VERSION_NO ," + "      T1.MAKER ," + "      T1.VERIFIER ,"
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
					+ "(T1.MAKER,0) ) MAKER_NAME," + "(SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = "
					+ getDbFunction("NVL") + "(T1.VERIFIER,0) ) VERIFIER_NAME," + "      T1.STATUS ," + statusAtDesc
					+ "      ,T1.ERROR_COUNT ," + "      T1.DESCRIPTION ," + dateFormat + "      (T1.REPORTING_DATE ,"
					+ formatdate + ")REPORTING_DATE," + "      T1.RECORD_INDICATOR ," + recordIndicatorAtDesc
					+ "      ,T1.MAKER_COMMENTS ," + "      T1.VERIFIER_COMMENTS ," + "      T1.DATE_CREATION ,"
					+ "      T1.DATE_LAST_MODIFIED," + "    T2.SOURCE_TYPE,"
					+ " T2.UPLOAD_FILE_NAME,T2.TYPE_OF_SUBMISSION ," + dateFormat + "      (T3.PROCESS_END_TIME ,"
					+ dateFormatStr + ")PROCESS_END_TIME," + "" + dateFormat + "      (T3.PROCESS_START_TIME ,"
					+ dateFormatStr + ")PROCESS_START_TIME, T2.CATEGORY_TYPE ," + "t1.template_id " + pipeLine + " '_' "
					+ pipeLine + dateFormat + "      (T1.REPORTING_DATE ," + formatdate + ") " + pipeLine
					+ " '.JSON' AS Jsonfile," + dateFormat + "      (T1.SUBMISSION_DATE ," + formatdate
					+ ")SUBMISSION_DATE," + " T2.cbk_file_name  CSV_FILE_NAME," + " T1.REQUEST_NO, "+cbStatus+" , "
					+ " (SELECT MIN(USER_NAME) FROM VISION_USERS WHERE VISION_ID = " + getDbFunction("NVL")
					+ "(T1.SUBMITTER,0) ) SUBMITTER_NAME,T1.SUBMITTER," 
					+ "  'ACTUAL' TABLES" + "  "+listTaggCond
					+ "FROM RG_TEMPLATES_HEADER T1 "
					+ " JOIN RG_TEMPLATE_CONFIG T2  " + "     ON T1.TEMPLATE_ID = T2.TEMPLATE_ID "
					+ "     AND T1.COUNTRY = T2.COUNTRY " + "     AND T1.LE_BOOK = T2.LE_BOOK AND T1.STATUS !=' ' "
					+ " JOIN RG_PROCESS_CONTROLS T3  " + "     ON T1.TEMPLATE_ID = T3.TEMPLATE_ID "
					+ "     AND T1.SUBMISSION_DATE = T3.SUBMISSION_DATE " + "     AND T1.COUNTRY = T3.COUNTRY "
					+ "     AND T1.LE_BOOK = T3.LE_BOOK " + " JOIN PRD_TEMPLATE_ACCESS A1  "
					+ "     ON T1.TEMPLATE_ID = A1.TEMPLATE_ID " + "     AND A1.USER_GROUP = '" + userGroup
					+ "'     AND A1.USER_PROFILE =  '" + userProfile + "'"
					+dependentCond
					+ "	WHERE  " + "     T2.TEMPLATE_STATUS = 0 ");

			String orderBy = "  ORDER BY T1.SUBMISSION_DATE, " + "  CASE PROCESS_FREQUENCY " + "  WHEN 'I' THEN 1 "
					+ "  WHEN 'D' THEN 2 " + "  WHEN 'W' THEN 3 " + "  WHEN 'M' THEN 4 " + "  WHEN 'Q' THEN 5 "
					+ "  WHEN 'H' THEN 6 " + "  WHEN 'A' THEN 7 " + "  ELSE 8          " + "  END				"
					+ " , TEMPLATE_ID    ";
			String groupBy ="""
					GROUP BY  
					T1.TEMPLATE_ID,
					         T1.COUNTRY,
					         T1.LE_BOOK,
					         T1.TEMPLATE_NAME,
					         T2.PROCESS_FREQUENCY,
					         T1.VERSION_NO,
					         T1.MAKER,
					         T1.VERIFIER,
					         T1.STATUS,
					         T1.ERROR_COUNT,
					         T1.DESCRIPTION,
					         T1.REPORTING_DATE,
					         T1.RECORD_INDICATOR,
					         T1.MAKER_COMMENTS,
					         T1.VERIFIER_COMMENTS,
					         T1.DATE_CREATION,
					         T1.DATE_LAST_MODIFIED,
					         T2.SOURCE_TYPE,
					         T2.UPLOAD_FILE_NAME,
					         T2.TYPE_OF_SUBMISSION,
					         T3.PROCESS_END_TIME,
					         T3.PROCESS_START_TIME,
					         T2.CATEGORY_TYPE,
					         T1.SUBMISSION_DATE,
					         T2.cbk_file_name,
					         T1.REQUEST_NO,
					        %s ,
					         T1.SUBMITTER
										""".formatted(cbStatusGroupBy);
			if (ValidationUtil.isValid(dObj.getCountry())) {
				String countryLeBook = " AND T1.COUNTRY = '" + dObj.getCountry() + "' AND T1.LE_BOOK = '"
						+ dObj.getLeBook() + "' ";
				strQueryAppr.append(countryLeBook);
			}
			if (ValidationUtil.isValid(dObj.getCategoryType())) {
				String category = " AND T2.CATEGORY_TYPE = ('" + dObj.getCategoryType() + "') ";
				strQueryAppr.append(category);
			}
			if (ValidationUtil.isValid(dObj.getSubmissionDate())) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
				LocalDate reportingDate = LocalDate.parse(dObj.getSubmissionDate(), formatter);
				LocalDate currentDate = LocalDate.now();
				if (reportingDate.equals(currentDate)) {
					if (dObj.getCategoryType().equalsIgnoreCase("RG")) {
						strQueryAppr.append(groupBy);
					}
					
					strQueryAppr.append(orderBy);
					collTemp = getJdbcTemplate().query(strQueryAppr.toString(), getTemplateScheduleDetailsMapper());
				} else {

					cond = "      AND  T1.SUBMISSION_DATE BETWEEN " + dateTimeConvert + "      AND " + dateTimeConvert;

					startDate = dObj.getSubmissionDate() + " 00:00:00";
					endDate = dObj.getSubmissionDate() + " 23:59:59";
					Object[] params = { startDate, endDate, startDate, endDate };
					strQueryAppr.append(cond);
					if (dObj.getCategoryType().equalsIgnoreCase("RG")) {
						strQueryAppr.append(groupBy);
					}
					String hisQuery = strQueryAppr.toString().replaceAll("RG_TEMPLATES_HEADER",
							"RG_TEMPLATES_HEADER_HIS");
					hisQuery = hisQuery.replaceAll("RG_PROCESS_CONTROLS", "RG_PROCESS_CONTROLS_HIS")
							.replaceAll("ACTUAL", "HIS");
					String finalQuery = "SELECT * FROM ( " + strQueryAppr + " UNION ALL " + hisQuery + " )T1 ";
					finalQuery = finalQuery.concat(orderBy);
					collTemp = getJdbcTemplate().query(finalQuery.toString(), params,
							getTemplateScheduleDetailsMapper());
				}
			}

			if (collTemp != null && collTemp.size() > 0) {

				Map<String, Long> countMap = collTemp.stream()
						.collect(Collectors.groupingBy(TemplateScheduleVb::getProcessFrequency, Collectors.counting()));

				Map<String, Long> countMap2 = collTemp.stream()
						.collect(Collectors.groupingBy(TemplateScheduleVb::getProcessStatus, Collectors.counting()));
				exceptionCode.setResponse(collTemp);
//				exceptionCode.setResponse1(countMap);
//				exceptionCode.setResponse2(countMap2);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("success");
			} else {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No Records Found");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: Exception in getting the Template Schedules Details : ");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}
		return exceptionCode;
	}

	protected RowMapper getTemplateScheduleDetailsMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
				templateScheduleVb.setCountry(rs.getString("COUNTRY"));
				templateScheduleVb.setLeBook(rs.getString("LE_BOOK"));
				templateScheduleVb.setTemplateId(rs.getString("TEMPLATE_ID"));
				templateScheduleVb.setTemplateName(rs.getString("TEMPLATE_NAME"));
				templateScheduleVb.setProcessFrequency(rs.getString("PROCESS_FREQUENCY"));
				templateScheduleVb.setProcessFrequencyDesc(rs.getString("PROCESS_FREQUENCY_DESC"));
				templateScheduleVb.setVersion(rs.getInt("VERSION_NO"));
				templateScheduleVb.setMaker(rs.getLong("MAKER"));
				templateScheduleVb.setMakerName(rs.getString("MAKER_NAME"));
				templateScheduleVb.setVerifierName(rs.getString("VERIFIER_NAME"));
				templateScheduleVb.setVerifier(rs.getLong("VERIFIER"));
				templateScheduleVb.setProcessStatus(rs.getString("STATUS"));
				templateScheduleVb.setProcessStatusDesc(rs.getString("STATUS_DESC"));
				// templateScheduleVb.setProcessId(rs.getString("RG_PROCESS_ID"));
				templateScheduleVb.setErrorCount(rs.getInt("ERROR_COUNT"));
				templateScheduleVb.setReportingDate(rs.getString("REPORTING_DATE"));
				templateScheduleVb.setSubmissionDate(rs.getString("SUBMISSION_DATE"));
				templateScheduleVb.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				templateScheduleVb.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				templateScheduleVb.setSourceType(rs.getString("SOURCE_TYPE"));
				templateScheduleVb.setTypeOfSubmission(rs.getString("TYPE_OF_SUBMISSION"));
				templateScheduleVb.setStartTime(rs.getString("PROCESS_START_TIME"));
				templateScheduleVb.setEndTime(rs.getString("PROCESS_END_TIME"));
				templateScheduleVb.setCategoryType(rs.getString("CATEGORY_TYPE"));
				if ("RG".equalsIgnoreCase(templateScheduleVb.getCategoryType())) {
					templateScheduleVb.setDependentTemplates(rs.getString("DEPENDENT_TEMPLATES"));
				}

				templateScheduleVb.setRequestNo(rs.getString("REQUEST_NO"));
//				templateScheduleVb.setCbStatus(rs.getString("CB_STATUS"));
				templateScheduleVb.setTable(rs.getString("TABLES"));
				if (templateScheduleVb.getTypeOfSubmission().equalsIgnoreCase("JSON")) {
					templateScheduleVb.setJsonFile(rs.getString("JSONFILE"));
				} else if (templateScheduleVb.getTypeOfSubmission().equalsIgnoreCase("CSV")) {
					templateScheduleVb.setJsonFile(templateScheduleVb.getTemplateId() + "_"
							+ rs.getString("CSV_FILE_NAME") + "_" + templateScheduleVb.getReportingDate() + ".csv");
				} else {
					templateScheduleVb.setJsonFile(rs.getString("CSV_FILE_NAME") + ".csv");
				}

				templateScheduleVb.setCbStatus(CommonUtils.readClobSafely(rs, "CB_STATUS"));
				templateScheduleVb.setSubmitter(rs.getLong("SUBMITTER"));
				templateScheduleVb.setSubmitterName(rs.getString("SUBMITTER_NAME"));
				return templateScheduleVb;
			}
		};
		return mapper;
	}

	public boolean isDateString(String value, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setLenient(false);
		try {

			if (value.split("-").length == 3) {
				dateFormat.parse(value);
				return true;
			}
		} catch (ParseException | java.text.ParseException e) {
			// Parsing failed, not a valid date

		}
		return false;
	}

	public int getErrorCount(TemplateScheduleVb templateScheduleVb) {
		int cnt = 0;
		String query = "SELECT COUNT(" + nullFun
				+ "(ERROR_CODE,0)) COUNT FROM RG_TEMPLATES_ERRORS WHERE COUNTRY =? AND LE_BOOK = ?"
				+ " AND  TEMPLATE_ID =  ? ";
		Object[] args = { templateScheduleVb.getCountry(), templateScheduleVb.getLeBook(),
				templateScheduleVb.getTemplateId() };
		cnt = getJdbcTemplate().queryForObject(query, args, Integer.class);
		return cnt;
	}

	public int getPendCount(TemplateScheduleVb templateScheduleVb) {
		int cnt = 0;
		String tableName = getSourceTableName(templateScheduleVb);

		String query = "SELECT COUNT(*) FROM " + tableName.trim().toUpperCase() + "_PEND";
		Object[] args = { templateScheduleVb.getCountry(), templateScheduleVb.getLeBook(),
				templateScheduleVb.getTemplateId() };
		cnt = getJdbcTemplate().queryForObject(query, Integer.class);
		return cnt;
	}

	public String getSourceTableName(TemplateScheduleVb templateScheduleVb) {
		String tableName = "";
		String query = "SELECT SOURCE_TABLE FROM RG_TEMPLATE_CONFIG WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ?";
		Object[] args = { templateScheduleVb.getCountry(), templateScheduleVb.getLeBook(),
				templateScheduleVb.getTemplateId() };
		tableName = getJdbcTemplate().queryForObject(query, args, String.class);
		return tableName;
	}

	public RowMapper getCountMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				NumSubTabVb vObject = new NumSubTabVb();
				vObject.setNumTab(rs.getInt("COUNT"));
				vObject.setNumSubTab(rs.getInt("VERSION"));
				vObject.setDescription(ValidationUtil.isValid(rs.getString("AUDIT")) ? rs.getString("AUDIT") : "0");
				return vObject;
			}
		};
		return mapper;
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doInsertRecordForNonTrans(Map<String, Object> dataMap, String tableName,
			TemplateScheduleVb vObject) {
		strCurrentOperation = Constants.SAVE;
		strApproveOperation = Constants.SAVE;
		setServiceDefaults();
		ExceptionCode exceptionCode = null;
		String query = "Insert Into ";
		StringJoiner queryJoiner = new StringJoiner(",");
		StringJoiner setJoiner = new StringJoiner(",");
		StringJoiner whereJoiner = new StringJoiner(" AND ");
		boolean columnsAdded = false;
		List<CommonVb> colNames = commonDao.getPrimaryKey(tableName);
		int cnt = getQueryExtractData(vObject, tableName);
		setJoiner = new StringJoiner(",");
		whereJoiner = new StringJoiner(" AND ");
		TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
		templateScheduleVb.setSourceTable(tableName.replaceAll("_PEND", ""));
		int versionNo = getVersionNo(templateScheduleVb);

		if (cnt > 0) {
			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				String columnName = entry.getKey();
				Object value = entry.getValue();
				boolean isPrimaryKeyColumn = false;
				for (CommonVb commonVb : colNames) {
					if (commonVb.getScreenName().equalsIgnoreCase(columnName)) {
						isPrimaryKeyColumn = true;
						// Construct the WHERE clause for primary key columns
						if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy")) {
							value = DateString(value.toString(), "dd-MMM-yyyy");
							whereJoiner.add(columnName + "=" + value.toString());
						} else if (value instanceof String) {
							whereJoiner.add(columnName + "='" + value + "'");
						} else {
							whereJoiner.add(columnName + "=" + value);
						}
					}
				}
				if (!isPrimaryKeyColumn) {
					// Construct the SET clause for non-primary key columns
					if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy HH:mm:ss")) {
						value = DateString(value.toString(), "dd-MMM-yyyy HH:mm:ss");
						setJoiner.add(columnName + "=" + value.toString());
					} else if (value instanceof String) {
						setJoiner.add(columnName + "='" + value + "'");
					} else if (value instanceof Integer || value instanceof Long || value instanceof Float
							|| value instanceof Double) {
						setJoiner.add(columnName + "=" + value.toString());
					} else if (value == null) {
						setJoiner.add(columnName + "=NULL");
					} else {
						setJoiner.add(columnName + "='" + value.toString() + "'");
					}
				}
			}
			try {
				query = "UPDATE " + tableName + " SET " + setJoiner.toString() + " WHERE " + whereJoiner.toString();
				vObject.setQuery(query);
				exceptionCode = doInsertionRecord(vObject);
			} catch (UncategorizedSQLException e) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(parseErrorMsg(e));
				exceptionCode.setOtherInfo(vObject);
				return exceptionCode;
			}

		} else {

			StringJoiner valueJoiner = new StringJoiner(",", "(", ")");
			String targetColumn = "VERSION_NO";

			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				// Add columns only once
				if (!columnsAdded) {
					if (!entry.getKey().equalsIgnoreCase("TAB"))
						queryJoiner.add(entry.getKey());
				}

				Object value = entry.getValue();
				if (entry.getKey().equalsIgnoreCase(targetColumn) && !entry.getKey().equalsIgnoreCase("TAB")) {
//						versionNo++;
					value = versionNo;
					valueJoiner.add(value.toString());
				} else {// Convert the value to a String, handling different types appropriately
					if (!entry.getKey().equalsIgnoreCase("TAB")) {
						if (entry.getKey().equalsIgnoreCase("MAKER"))
							value = SessionContextHolder.getContext().getVisionId();
						// System.out.println(value);
						if (entry.getKey().equalsIgnoreCase("VERIFIER"))
							value = 0;
						if (entry.getKey().equalsIgnoreCase("DATE_LAST_MODIFIED"))
							value = systemDate;
						if (!entry.getKey().equalsIgnoreCase("DATE_LAST_MODIFIED")) {
							if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy HH:mm:ss")) {
								value = DateString(value.toString(), "dd-MMM-yyyy HH:mm:ss");
								valueJoiner.add(value.toString());
							} else if (value instanceof String) {
								valueJoiner.add("'" + value + "'");
							} else if (value instanceof Integer || value instanceof Long || value instanceof Float
									|| value instanceof Double) {
								valueJoiner.add(value.toString());
							} else if (value == null) {
								valueJoiner.add("NULL");
							} else {
								// Handle other types as needed
								valueJoiner.add("'" + value.toString() + "'");
							}
						} else {
							valueJoiner.add(value.toString());
						}
					}
				}
			}
			columnsAdded = true;
//				if (tableName.contains("AUDIT")) {
//					queryJoiner.add("AUDIT_SEQUENCE");
//					int seq = Integer.parseInt(auditSeq);
//					seq++;
//					valueJoiner.add(String.valueOf(seq));
//				}
			try {
				query = "INSERT INTO " + tableName + " (" + queryJoiner.toString() + ") VALUES "
						+ valueJoiner.toString();
				vObject.setQuery(query);
				exceptionCode = doInsertionRecord(vObject);
			} catch (UncategorizedSQLException e) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(parseErrorMsg(e));
				return exceptionCode;
			}

		}
		return exceptionCode;
	}

	public ExceptionCode doDeleteRecord(TemplateScheduleVb vObject) throws RuntimeCustomException {
		TemplateScheduleVb templateScheduleVb = null;
		List<TemplateScheduleVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.DELETE;
		vObject.setMaker(getIntCurrentUserId());
		try {
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}
			retVal = deleteAllRecords(vObject);
			if (retVal != Constants.ERRONEOUS_OPERATION) {
				vObject.setProcessStatus(" ");
				vObject.setMaker(intCurrentUserId);
				vObject.setVerifier(0);
				System.out.println("1");
				retVal = updateTemplateHeaders(vObject, true);
				retVal =updateProcessControls(vObject, true);
				exceptionCode = getResultObject(retVal);
				exceptionCode.setOtherInfo(vObject.getProcessStatus());
			}

			return exceptionCode;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Reject.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	public ExceptionCode doRejectForTransaction(TemplateScheduleVb vObject) throws RuntimeCustomException {
		List<TemplateScheduleVb> collTemp = null;
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		strErrorDesc = "";
		strCurrentOperation = Constants.REJECT;
		vObject.setMaker(getIntCurrentUserId());
		try {
			collTemp = doSelectPendingRecord(vObject);
			if (collTemp == null) {
				exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
				throw buildRuntimeCustomException(exceptionCode);
			}
			if (collTemp.size() == 0) {
				exceptionCode = getResultObject(Constants.NO_SUCH_PENDING_RECORD);
				throw buildRuntimeCustomException(exceptionCode);
			}

			vObject.setProcessStatus("TR");
			vObject.setRecordIndicator(Constants.STATUS_PENDING);
			vObject.setVerifier(intCurrentUserId);
			retVal = updateTemplateHeaders(vObject, false);
			retVal = updateProcessControls(vObject, false);
			if(retVal == Constants.SUCCESSFUL_OPERATION) {
				retVal = saveComments(vObject);
			}
			exceptionCode = getResultObject(retVal);
			exceptionCode.setOtherInfo(vObject.getProcessStatus());

			return exceptionCode;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Reject.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	protected List<TemplateScheduleVb> selectApprovedRecord(TemplateScheduleVb vObject) {
		return getQueryTableResults(vObject, Constants.STATUS_ZERO, true);
	}

	protected List<TemplateScheduleVb> doSelectPendingRecord(TemplateScheduleVb vObject) {
		return getQueryTableResults(vObject, Constants.STATUS_PENDING, false);
	}

	public List<TemplateScheduleVb> getQueryTableResults(TemplateScheduleVb dObj, int intStatus, boolean flag) {
		List<TemplateScheduleVb> collTemp = null;
		String strQueryAppr = null;
		String strQueryPend = null;
		List<ColumnHeadersVb> lst = getColumns(dObj.getSourceTable().toUpperCase().trim());
		StringJoiner joiner = new StringJoiner(",");
		if (lst != null && lst.size() > 0) {
			for (ColumnHeadersVb columnHeadersVb : lst) {
				if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
						|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
						|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
						&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0"))
					joiner.add(columnHeadersVb.getDbColumnName());
				else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
						|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
						|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
					joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + formatdate + ") "
							+ columnHeadersVb.getDbColumnName());
				else
					joiner.add(columnHeadersVb.getDbColumnName());
			}

		}
		if (flag) {
			strQueryAppr = new String("SELECT " + joiner + " FROM " + dObj.getSourceTable().trim() + "TAPPR");
//			strQueryPend = new String("SELECT " + joiner + " FROM " + dObj.getSourceTable().trim() + "_PEND");
		} else {
			strQueryPend = new String("SELECT " + joiner + " FROM " + dObj.getSourceTable().trim() + "_PEND");
			intStatus = Constants.STATUS_PENDING;
		}
		try {
			if (intStatus == 0) {
				logger.info("Executing approved query");
				collTemp = getJdbcTemplate().query(strQueryAppr.toString(), getMapper());
			} else {
				logger.info("Executing pending query");
				collTemp = getJdbcTemplate().query(strQueryPend.toString(), getMapper());
			}
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error: getQueryResults Exception :   ");
			if (intStatus == 0)
				logger.error(((strQueryAppr == null) ? "strQueryAppr is Null" : strQueryAppr.toString()));
			else
				logger.error(((strQueryPend == null) ? "strQueryPend is Null" : strQueryPend.toString()));
			return null;
		}

	}

	protected int deletePendingRecord(TemplateScheduleVb vObject) {
		String query = "DELETE FROM " + vObject.getSourceTable().trim() + "_PEND";
		return getJdbcTemplate().update(query);
	}

	protected int doDeleteAppr(TemplateScheduleVb vObject) {
		String query = "DELETE FROM " + vObject.getSourceTable().trim() + "";
		return getJdbcTemplate().update(query);
	}

	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
	public ExceptionCode doApproveRecord(TemplateScheduleVb vObject, boolean flag) throws RuntimeCustomException {
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.APPROVE;
		String query = "";
		String selectQuery = "";
		int cnt = 0;
		int versionNo = 0;

		setServiceDefaults();
		try {
			List<Map<String, Object>> dataList = (List<Map<String, Object>>) vObject.getDataLst();
			Map<String, Object> dataMap = dataList.get(0);
			if (dataMap != null && !dataMap.isEmpty()) {
				Object maker = dataMap.get("MAKER");
				String currentUser = Integer.toString((int) intCurrentUserId);
				if (maker.toString().equalsIgnoreCase(currentUser)) {
//					System.out.println("maker: " + maker);
					exceptionCode = getResultObject(Constants.MAKER_CANNOT_APPROVE);
					throw buildRuntimeCustomException(exceptionCode);
				}

				selectQuery = buildSelectQuery(vObject.getSourceTable().trim(), dataMap, vObject);
				if (ValidationUtil.isValid(selectQuery)) {
					cnt = getCntsourceTable(selectQuery);
					if (cnt == 0) {
						String sourceTable = vObject.getSourceTable().trim();
						selectQuery = selectQuery.replace(sourceTable, sourceTable + "_PEND");
						selectQuery = selectQuery.replace("VERIFIER", "'" + currentUser + "' VERIFIER");

					}
					query = buildInsertQuery(vObject.getSourceTable().trim(), dataMap, selectQuery);
					vObject.setQuery(query);
					exceptionCode = doInsertionRecord(vObject);
				}
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION && cnt != 0) {
					exceptionCode = approveRecords(vObject);
					versionNo = Integer.parseInt(exceptionCode.getErrorSevr());
				}
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					// deleting pend records
					deletePendingRecord(vObject);
					vObject.setProcessStatus("SP");
					vObject.setVersion(versionNo);
					vObject.setVerifier(intCurrentUserId);
					int retval = updateTemplateHeaders(vObject, false);
					updateProcessControls(vObject, false);
					exceptionCode.setOtherInfo(vObject.getProcessStatus());
					exceptionCode = getResultObject(retval);
				}
			}
			return exceptionCode;
		} catch (UncategorizedSQLException uSQLEcxception) {
			strErrorDesc = parseErrorMsg(uSQLEcxception);
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		} catch (Exception ex) {
			logger.error("Error in Approve.", ex);
			logger.error(((vObject == null) ? "vObject is Null" : vObject.toString()));
			strErrorDesc = ex.getMessage();
			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
			throw buildRuntimeCustomException(exceptionCode);
		}
	}

	public ExceptionCode approveRecords(TemplateScheduleVb vObject) {
		ExceptionCode exceptionCode = null;
		setServiceDefaults();
		String query = "";
		StringJoiner setJoiner = new StringJoiner(",");
		StringJoiner whereJoiner = new StringJoiner(" AND ");
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) vObject.getDataLst();
		List<CommonVb> colNames = commonDao.getPrimaryKey(vObject.getSourceTable().trim());
		for (Map<String, Object> dataMap : dataList) {
			int cnt = getQueryExtractData(vObject, vObject.getSourceTable().trim());
			setJoiner = new StringJoiner(",");
			whereJoiner = new StringJoiner(" AND ");
			Object versionNo = 0;
			if (cnt > 0) {
				for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
					String columnName = entry.getKey();
					Object value = entry.getValue();
					boolean isPrimaryKeyColumn = false;
					for (CommonVb commonVb : colNames) {
						if (commonVb.getScreenName().equalsIgnoreCase(columnName)) {
							isPrimaryKeyColumn = true;
							// Construct the WHERE clause for primary key columns
							if ("VERSION_NO".equals(columnName)) {
								versionNo = value;
							}
							if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy")) {
								value = DateString(value.toString(), "dd-MMM-yyyy");
								whereJoiner.add(columnName + "=" + value.toString());
							} else if (value instanceof String) {
								whereJoiner.add(columnName + "='" + value + "'");
							} else {
								whereJoiner.add(columnName + "=" + value);
							}
						}
					}
					if (!isPrimaryKeyColumn) {
						// Construct the SET clause for non-primary key columns
						if ("VERIFIER".equals(columnName)) {
							value = intCurrentUserId;
						}
						if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy HH:mm:ss")) {
							value = DateString(value.toString(), "dd-MMM-yyyy HH:mm:ss");
							setJoiner.add(columnName + "=" + value.toString());
						} else if (value instanceof String) {
							setJoiner.add(columnName + "='" + value + "'");
						} else if (value instanceof Integer || value instanceof Long || value instanceof Float
								|| value instanceof Double) {
							setJoiner.add(columnName + "=" + value.toString());
						} else if (value == null) {
							setJoiner.add(columnName + "=NULL");
						} else {
							setJoiner.add(columnName + "='" + value.toString() + "'");
						}
					}
				}

				query = "UPDATE " + vObject.getSourceTable().trim() + " SET " + setJoiner.toString() + " WHERE "
						+ whereJoiner.toString();
				vObject.setQuery(query);
				exceptionCode = doInsertionRecord(vObject);

			}
			exceptionCode.setErrorSevr(String.valueOf(versionNo));
		}

		return exceptionCode;
	}

	public int getVersionNo(TemplateScheduleVb vObject) {

		StringBuffer strBufApprove = new StringBuffer("Select MAX(VERSION_NO) From " + vObject.getSourceTable().trim());
		try {
			int max = getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
			if (max <= 0)
				max = 1;
			else
				max++;
			return max;
		} catch (Exception ex) {
//			ex.printStackTrace();
			return 1;
		}

	}

	public int getApprVersionNo(TemplateScheduleVb vObject) {

		StringBuffer strBufApprove = new StringBuffer(
				"Select " + nullFun + "(MAX(VERSION_NO),0) From " + vObject.getSourceTable().trim());
		try {
			int max = getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
			if (max >= 0)
				max++;
			return max;
		} catch (Exception ex) {
//			ex.printStackTrace();
			return 1;
		}

	}

	public int getcntSourceTable(TemplateScheduleVb vObject) {

		StringBuffer strBufApprove = new StringBuffer("Select count(*) From " + vObject.getSourceTable().trim());
		try {
			int max = getJdbcTemplate().queryForObject(strBufApprove.toString(), Integer.class);
			return max;
		} catch (Exception ex) {
//			ex.printStackTrace();
			return 0;
		}

	}

	public String getMaxVersionNo(String tableName, String rowId) {

		StringBuffer strBufApprove = new StringBuffer(
				"Select MAX(VERSION_NO) From  " + tableName.trim() + " WHERE ROW_ID =" + rowId);
		try {
			String max = getJdbcTemplate().queryForObject(strBufApprove.toString(), String.class);
			return max;
		} catch (Exception ex) {
//			ex.printStackTrace();
			return "0";
		}

	}

	public List reviewAll(TemplateScheduleVb templateScheduleVb) {
		List collTemp = null;
		String whereExists = getStringWhrNotExist(templateScheduleVb.getSourceTable(), false);
		List<ColumnHeadersVb> lst = getColumns(templateScheduleVb.getSourceTable().toUpperCase().trim());
		StringJoiner joiner = new StringJoiner(",");
		if (lst != null && lst.size() > 0) {
			for (ColumnHeadersVb columnHeadersVb : lst) {
				if ((columnHeadersVb.getColType().equalsIgnoreCase("N"))
						&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0"))
					joiner.add(dbFunctionFormats(columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5") + " "
							+ columnHeadersVb.getDbColumnName());
				else
					joiner.add(columnHeadersVb.getDbColumnName());

			}
		}
		String query1 = " SELECT " + "    ROW_NUMBER() OVER (ORDER BY ROW_ID) AS Order_Number,  " + "    t1.* "
				+ "FROM ( " + " SELECT " + joiner + ",'APPR' TAB FROM " + templateScheduleVb.getSourceTable()
				+ " TAPPR WHERE " + whereExists + " UNION ALL  " + " SELECT " + joiner + ", 'PEND' TAB FROM "
				+ templateScheduleVb.getSourceTable().trim() + "_PEND TPEND) t1 " + " ORDER BY ROW_ID,VERSION_NO DESC ";

		collTemp = getJdbcTemplate().query(query1, getMapper());
		return collTemp;
	}

	public String buildSelectQuery(String tableName, Map<String, Object> columns,
			TemplateScheduleVb templateScheduleVb) {
		StringBuilder query = new StringBuilder("SELECT \n");
		int versionNo = getApprVersionNo(templateScheduleVb);
		if (columns != null && !columns.isEmpty()) {
			StringJoiner joiner = new StringJoiner(",\n       ");
			for (String column : columns.keySet()) {
				if (column.equalsIgnoreCase("VERSION_NO")) {
					joiner.add("'" + versionNo + "' " + column);
				} else {
					joiner.add(column);
				}
			}
			query.append("       ").append(joiner.toString()).append("\n");
		} else {
			query.append("       *\n");
		}

		query.append("  FROM ").append(tableName);
		if (versionNo > 0) {
			query.append(" WHERE VERSION_NO = " + (versionNo - 1));
		}

		return query.toString();
	}

	public String buildInsertQuery(String tableName, Map<String, Object> columns, String selectQuery) {
		StringBuilder query = new StringBuilder("INSERT INTO ");
		query.append(tableName).append(" (");

		StringJoiner columnNames = new StringJoiner(", ");

		for (Map.Entry<String, Object> entry : columns.entrySet()) {
			columnNames.add(entry.getKey());
		}

		query.append(columnNames.toString()).append(") " + selectQuery);

		return query.toString();
	}

	public List<TemplateErrorsVb> getErrorList(TemplateConfigVb vObject) {
		List<TemplateErrorsVb> collTemp = null;
		String joiner = columnJoiner("RG_TEMPLATES_ERRORS");
		String query = "SELECT " + "COUNTRY, LE_BOOK, TEMPLATE_ID, ROW_ID, COLUMN_ID, ERROR_CODE, ACTUAL_VALUE, "
				+ "EXPECTED_VALUE, REPORTING_DATE, VERSION_NO,(SELECT ERROR_DESCRIPTION FROM ERROR_CODES WHERE ERROR_CODE = T1.ERROR_CODE) ERROR_DESCRIPTION"
				+ " FROM RG_TEMPLATES_ERRORS T1 WHERE TEMPLATE_ID = ?";
		Object[] args = { vObject.getTemplateId() };
		collTemp = getJdbcTemplate().query(query, args, getTemplateErrorsMapper());
		return collTemp;
	}

	protected RowMapper getTemplateErrorsMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateErrorsVb templateErrorsVb = new TemplateErrorsVb();
				templateErrorsVb.setCountry(rs.getString("COUNTRY"));
				templateErrorsVb.setLeBook(rs.getString("LE_BOOK"));
				templateErrorsVb.setTemplateId(rs.getString("TEMPLATE_ID"));
				templateErrorsVb.setRowId(rs.getString("ROW_ID"));
				templateErrorsVb.setColumnId(rs.getString("COLUMN_ID"));
				templateErrorsVb.setErrorCode(rs.getString("ERROR_CODE"));
				templateErrorsVb.setErrorDesc(rs.getString("ERROR_DESCRIPTION"));
				templateErrorsVb.setVersionNo(rs.getInt("VERSION_NO"));
				templateErrorsVb.setReportingDate(rs.getString("REPORTING_DATE"));
				templateErrorsVb.setActualValue(rs.getString("ACTUAL_VALUE"));
				templateErrorsVb.setExpectedValue(rs.getString("EXPECTED_VALUE"));
				return templateErrorsVb;
			}
		};
		return mapper;
	}

	public String columnJoiner(String tableName) {
		List<CommonVb> lst = commonDao.getColumns(tableName.toUpperCase().trim());
		StringJoiner joiner = new StringJoiner(",");
		if (lst != null && lst.size() > 0) {
			for (CommonVb commonVb : lst) {
				joiner.add(commonVb.getScreenName());
			}
		}
		return joiner.toString();
	}

	public ExceptionCode doInsertAudit(Map<String, Object> dataMap, String tableName, TemplateScheduleVb vObject) {
		ExceptionCode exceptionCode = null;
		strCurrentOperation = Constants.SAVE;
		strApproveOperation = Constants.SAVE;
		setServiceDefaults();
		String query = "Insert Into ";
		StringJoiner queryJoiner = new StringJoiner(",");
		boolean columnsAdded = false;
		String targetColumn = "VERSION_NO";
		int cnt = getAuditSeq(vObject, tableName);
		TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
		templateScheduleVb.setSourceTable(tableName.replaceAll("_AUDIT", ""));
		int versionNo = getVersionNo(templateScheduleVb);

		StringJoiner valueJoiner = new StringJoiner(",", "(", ")");

		for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
			// Add columns only once
			if (!columnsAdded) {
				if (!entry.getKey().equalsIgnoreCase("TAB"))
					queryJoiner.add(entry.getKey());
			}
			Object value = entry.getValue();
			if (entry.getKey().equalsIgnoreCase(targetColumn) && !entry.getKey().equalsIgnoreCase("TAB")) {
//				versionNo++;
				value = versionNo;
				valueJoiner.add(value.toString());
			} else {
				// Convert the value to a String, handling different types appropriately
				if (!entry.getKey().equalsIgnoreCase("TAB")) {
					if (entry.getKey().equalsIgnoreCase("MAKER"))
						value = SessionContextHolder.getContext().getVisionId();
					if (entry.getKey().equalsIgnoreCase("VERIFIER"))
						value = 0;
					if (entry.getKey().equalsIgnoreCase("DATE_LAST_MODIFIED"))
						value = systemDate;
					if (!entry.getKey().equalsIgnoreCase("DATE_LAST_MODIFIED")) {
						if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy HH:mm:ss")) {
							value = DateString(value.toString(), "dd-MMM-yyyy HH:mm:ss");
							valueJoiner.add(value.toString());
						} else if (value instanceof String) {
							valueJoiner.add("'" + value + "'");
						} else if (value instanceof Integer || value instanceof Long || value instanceof Float
								|| value instanceof Double) {
							valueJoiner.add(value.toString());
						} else if (value == null) {
							valueJoiner.add("NULL");
						} else {
							// Handle other types as needed
							valueJoiner.add("'" + value.toString() + "'");
						}
					} else {
						valueJoiner.add(value.toString());
					}
				}
			}
		}
		if (tableName.contains("AUDIT")) {
			queryJoiner.add("AUDIT_SEQUENCE");
			valueJoiner.add(String.valueOf(cnt));
		}
		columnsAdded = true;
		query = "INSERT INTO " + tableName + " (" + queryJoiner.toString() + ") VALUES " + valueJoiner.toString();
		vObject.setQuery(query);
		exceptionCode = doInsertionRecord(vObject);

		return exceptionCode;
	}

//	public List<TemplateScheduleVb> getQueryResultsReview(TemplateScheduleVb dObj, int intStatus ) {
//		List<TemplateScheduleVb> collTemp = null;
//		StringBuffer query = null;
//		String tableName = dObj.getSourceTable().trim();
//		StringJoiner whereJoiner = new StringJoiner(" AND ");
//		
//		if(intStatus == Constants.STATUS_ZERO) {
//			query = new StringBuffer("SELECT * FROM "+tableName);
//		}else {
//			query = new StringBuffer("SELECT * FROM "+tableName+"_PEND");
//		}
//		List<CommonVb> colNames = commonDao.getPrimaryKey(tableName);
//		query.append(" where ");
//
//		List<Map<String, Object>> dataList = (List<Map<String, Object>>) dObj.getDataLst();
//		for (Map<String, Object> dataMap : dataList) {
//			whereJoiner = new StringJoiner(" AND ");
//
//			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
//				String columnName = entry.getKey();
//				Object value = entry.getValue();
//
//				for (CommonVb commonVb : colNames) {
//					if (commonVb.getScreenName().equalsIgnoreCase(columnName)&& !commonVb.getScreenName().equalsIgnoreCase("AUDIT_SEQUENCE")) {
//						// Construct the WHERE clause for primary key columns
//						if (value instanceof String && isDateString((String) value, "dd-MMM-yyyy HH:mm:ss")) {
//							value = DateString(value.toString(), "dd-MMM-yyyy HH:mm:ss");
//							whereJoiner.add(columnName + "=" + value.toString());
//						} else if (value instanceof String) {
//							whereJoiner.add(columnName + "='" + value + "'");
//						} else {
//							whereJoiner.add(columnName + "=" + value);
//						}
//					}
//				}
//			}
//		}
//		query.append(whereJoiner);
//		try {
//			
//				logger.info("Executing pending query");
//				collTemp = getJdbcTemplate().query(query.toString(),  getMapper());
//			
//			return collTemp;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			
//			return null;
//		}
//
//}
	public String getQueryResultsReview(TemplateScheduleVb dObj) {
		StringBuffer query = null;
		StringBuffer pendQuery = null;
		int intStatus = 0;
		String tableName = dObj.getSourceTable().trim();
		StringJoiner whereJoiner = new StringJoiner(" AND ");
		StringJoiner joiner = new StringJoiner(",");

		List<ColumnHeadersVb> lst = getNeededColumns(tableName);
		if (lst != null && lst.size() > 0) {
			for (ColumnHeadersVb columnHeadersVb : lst) {
				if ((columnHeadersVb.getColType().equalsIgnoreCase("N"))
						&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0"))
					joiner.add(dbFunctionFormats(columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5") + " "
							+ columnHeadersVb.getDbColumnName());

//				else if ( columnHeadersVb.getColType().equalsIgnoreCase("D"))
//					joiner.add(dateFormat+"("+columnHeadersVb.getDbColumnName()+","+formatdate +") "+columnHeadersVb.getDbColumnName());
				else
					joiner.add(columnHeadersVb.getDbColumnName());

			}
		}
		query = new StringBuffer("SELECT " + joiner + " ,'APPR' TAB FROM " + tableName);
		query.append(" where ");
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) dObj.getDataLst();
		String rowId = dataList.get(0).get("ROW_ID").toString();
		whereJoiner.add("ROW_ID" + " =" + rowId.toString());
		whereJoiner.add("VERSION_NO" + " =");
		String versionNo = new String(getMaxVersionNo(tableName, rowId));
		query.append(whereJoiner);
		pendQuery = new StringBuffer(query);
		query = query.append(versionNo);
		intStatus = 1;
		if (intStatus != Constants.STATUS_ZERO) {
			String versionNoP = new String(dataList.get(0).get("VERSION_NO").toString());
			pendQuery.append(versionNoP);
		}

		String pend = pendQuery.toString().replaceAll(tableName, tableName + "_PEND");
		pend = pend.replace("'APPR'", "'PEND'");

		String finalQuery = query.toString() + " UNION " + pend;
		return finalQuery.toUpperCase();

	}

	@SuppressWarnings("deprecation")
	public List<ColumnHeadersVb> getNeededColumns(String tableName) {
		ArrayList<ColumnHeadersVb> columName = null;
		String query = " SELECT COLUMN_NAME , COLUMN_NAME COL_ALIAS_NAME, COLUMN_ID,DATA_TYPE,DATA_SCALE FROM USER_TAB_COLUMNS           "
				+ " WHERE UPPER(TABLE_NAME) = ? AND                                                                      "
				+ " (COLUMN_NAME NOT IN ('RECORD_INDICATOR','DATE_CREATION','INTERNAL_STATUS') AND "
				+ " SUBSTR(COLUMN_NAME,-3) NOT IN ('_AT','_NT'))                                                         "
				+ " ORDER BY COLUMN_ID     ";
		try {
			if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
				query = " SELECT COLUMN_NAME ,COLUMN_NAME COL_ALIAS_NAME, ORDINAL_POSITION,DATA_TYPE,NUMERIC_SCALE AS DATA_SCALE  FROM INFORMATION_SCHEMA.columns"
						+ " WHERE TABLE_NAME = ? AND                                                                                   "
						+ " (COLUMN_NAME NOT IN ('RECORD_INDICATOR','DATE_CREATION','INTERNAL_STATUS') AND       "
						+ " RIGHT(COLUMN_NAME,3) NOT IN ('_NT','_AT'))                                                                 "
						+ " ORDER BY ORDINAL_POSITION                                                                                  ";
			}
			Object[] lParams = new Object[1];
			lParams[0] = tableName;

			return getJdbcTemplate().query(query, lParams, new RowMapper<ColumnHeadersVb>() {
				@Override
				public ColumnHeadersVb mapRow(ResultSet rs, int rowNum) throws SQLException {
					ColumnHeadersVb columnHeadersVb = new ColumnHeadersVb();
					columnHeadersVb.setCaption(CommonUtils.convertToReadableFormat(rs.getString("COLUMN_NAME")));
					columnHeadersVb.setDbColumnName(rs.getString("COLUMN_NAME"));
					columnHeadersVb.setLabelColNum(rowNum + 1);
					columnHeadersVb.setColType(rs.getString("DATA_TYPE"));
					if (columnHeadersVb.getColType().equalsIgnoreCase("VARCHAR2")
							|| columnHeadersVb.getColType().equalsIgnoreCase("VARCHAR"))
						columnHeadersVb.setColType("T");
					else if (columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
							|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
							|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
						columnHeadersVb.setColType("N");
					else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
							|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
							|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME2"))
						columnHeadersVb.setColType("D");
					columnHeadersVb.setDecimalCnt(rs.getString("DATA_SCALE"));
					if (!ValidationUtil.isValid(columnHeadersVb.getDecimalCnt()))
						columnHeadersVb.setDecimalCnt("0");
					return columnHeadersVb;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return columName;
	}

	@SuppressWarnings("deprecation")
	public List<ColumnHeadersVb> getNeededColumnsForDownload(String tableName) {
		ArrayList<ColumnHeadersVb> columName = null;
		String query = " SELECT COLUMN_NAME , COLUMN_NAME COL_ALIAS_NAME, COLUMN_ID,DATA_TYPE,DATA_SCALE FROM USER_TAB_COLUMNS           "
				+ " WHERE UPPER(TABLE_NAME) = ? AND                                                                      "
				+ " (COLUMN_NAME NOT IN ('RECORD_INDICATOR','DATE_CREATION','DATE_LAST_MODIFIED','INTERNAL_STATUS','MAKER','VERIFIER') AND "
				+ " SUBSTR(COLUMN_NAME,-3) NOT IN ('_AT','_NT'))                                                         "
				+ " ORDER BY COLUMN_ID     ";
		try {
			if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
				query = " SELECT COLUMN_NAME ,COLUMN_NAME COL_ALIAS_NAME, ORDINAL_POSITION,DATA_TYPE,NUMERIC_SCALE AS DATA_SCALE   FROM INFORMATION_SCHEMA.columns"
						+ " WHERE TABLE_NAME = ? AND                                                                                   "
						+ " (COLUMN_NAME NOT IN ('RECORD_INDICATOR','DATE_CREATION','DATE_LAST_MODIFIED','INTERNAL_STATUS','MAKER','VERIFIER') AND       "
						+ " RIGHT(COLUMN_NAME,3) NOT IN ('_NT','_AT'))                                                                 "
						+ " ORDER BY ORDINAL_POSITION                                                                                  ";
			}
			Object[] lParams = new Object[1];
			lParams[0] = tableName;

			return getJdbcTemplate().query(query, lParams, new RowMapper<ColumnHeadersVb>() {
				@Override
				public ColumnHeadersVb mapRow(ResultSet rs, int rowNum) throws SQLException {
					ColumnHeadersVb columnHeadersVb = new ColumnHeadersVb();
					columnHeadersVb.setCaption(CommonUtils.convertToReadableFormat(rs.getString("COLUMN_NAME")));
					columnHeadersVb.setDbColumnName(rs.getString("COLUMN_NAME"));
					columnHeadersVb.setLabelColNum(rowNum + 1);
					columnHeadersVb.setColType(rs.getString("DATA_TYPE"));
					if (columnHeadersVb.getColType().equalsIgnoreCase("VARCHAR2")
							|| columnHeadersVb.getColType().equalsIgnoreCase("VARCHAR"))
						columnHeadersVb.setColType("T");
					else if (columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
							|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
							|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
						columnHeadersVb.setColType("N");
					else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
							|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
							|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME2"))
						columnHeadersVb.setColType("D");
					columnHeadersVb.setDecimalCnt(rs.getString("DATA_SCALE"));
					if (!ValidationUtil.isValid(columnHeadersVb.getDecimalCnt()))
						columnHeadersVb.setDecimalCnt("0");
					return columnHeadersVb;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return columName;
	}

	@SuppressWarnings("deprecation")
	public List<ColumnHeadersVb> getColumns(String tableName) {
		ArrayList<ColumnHeadersVb> columName = null;
		String query = " SELECT COLUMN_NAME , COLUMN_NAME COL_ALIAS_NAME, COLUMN_ID,DATA_TYPE,DATA_SCALE FROM USER_TAB_COLUMNS           "
				+ " WHERE UPPER(TABLE_NAME) = ?                                                                       ";
		try {
			if ("MSSQL".equalsIgnoreCase(databaseType) || "SQLSERVER".equalsIgnoreCase(databaseType)) {
				query = " SELECT COLUMN_NAME ,COLUMN_NAME COL_ALIAS_NAME, ORDINAL_POSITION,DATA_TYPE,NUMERIC_SCALE AS DATA_SCALE  FROM INFORMATION_SCHEMA.columns"
						+ " WHERE TABLE_NAME = ? ";
			}
			Object[] lParams = new Object[1];
			lParams[0] = tableName;

			return getJdbcTemplate().query(query, lParams, new RowMapper<ColumnHeadersVb>() {
				@Override
				public ColumnHeadersVb mapRow(ResultSet rs, int rowNum) throws SQLException {
					ColumnHeadersVb columnHeadersVb = new ColumnHeadersVb();
					columnHeadersVb.setCaption(CommonUtils.convertToReadableFormat(rs.getString("COLUMN_NAME")));
					columnHeadersVb.setDbColumnName(rs.getString("COLUMN_NAME"));
					columnHeadersVb.setLabelColNum(rowNum + 1);
					columnHeadersVb.setColType(rs.getString("DATA_TYPE"));
					columnHeadersVb.setDecimalCnt(rs.getString("DATA_SCALE"));
					if (!ValidationUtil.isValid(columnHeadersVb.getDecimalCnt()))
						columnHeadersVb.setDecimalCnt("0");
					return columnHeadersVb;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		return columName;
	}

	public TemplateScheduleVb getTemplatesAudit(TemplateScheduleVb dObj) {
		List<TemplateScheduleVb> collTemp = null;
		TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
		String tableName = dObj.getSourceTable().trim().toUpperCase() + "_AUDIT";
		StringJoiner joiner = new StringJoiner(",");
		String orderBy = " ORDER BY VERSION_NO DESC, AUDIT_SEQUENCE DESC";
		StringJoiner whereJoiner = new StringJoiner(" AND ");
		List<ColumnHeadersVb> lst = getNeededColumns(tableName);
		if (lst != null && lst.size() > 0) {
			for (ColumnHeadersVb columnHeadersVb : lst) {

				if (columnHeadersVb.getColType().equalsIgnoreCase("N")
						&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0")
						&& "Y".equalsIgnoreCase(precisionFlag))
					joiner.add(dbFunctionFormats(columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5") + " "
							+ columnHeadersVb.getDbColumnName());
				if (columnHeadersVb.getColType().equalsIgnoreCase("D")
						&& !columnHeadersVb.getDbColumnName().equalsIgnoreCase("DATE_LAST_MODIFIED"))
					joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + formatdate + ") "
							+ columnHeadersVb.getDbColumnName());
				if (columnHeadersVb.getColType().equalsIgnoreCase("D")
						&& columnHeadersVb.getDbColumnName().equalsIgnoreCase("DATE_LAST_MODIFIED"))
					joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + dateFormatStr + ") "
							+ columnHeadersVb.getDbColumnName());
				else
					joiner.add(columnHeadersVb.getDbColumnName());
			}
		}
		templateScheduleVb.setColumnHeaderLst(lst);
		String targetColumn = "ROW_ID";
		StringBuffer query = new StringBuffer("SELECT " + joiner + " FROM " + tableName + " ");
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) dObj.getDataLst();

		String columnValue = dataList.get(0).get(targetColumn).toString();
		whereJoiner.add(targetColumn + "=" + columnValue.toString());
		query.append(" WHERE ");
		query.append(whereJoiner);
		query.append(orderBy);
		try {
			collTemp = getJdbcTemplate().query(query.toString(), getMapper());
			if (collTemp != null && collTemp.size() > 0) {
				templateScheduleVb.setDataLst(collTemp);
			}
			return templateScheduleVb;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query.toString()));
			return null;
		}
	}

	public int getAuditSeq(TemplateScheduleVb vObject, String tableName) {
		StringJoiner whereJoiner = new StringJoiner(" AND ");
		String targetColumn = "ROW_ID";
		StringBuffer query = new StringBuffer("SELECT MAX(AUDIT_SEQUENCE) FROM ");
		query.append(tableName);
		query.append(" where ");
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) vObject.getDataLst();
		for (Map<String, Object> dataMap : dataList) {
			whereJoiner = new StringJoiner(" AND ");

			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				String columnName = entry.getKey();
				Object value = entry.getValue();

				if (columnName.equalsIgnoreCase(targetColumn)) {
					whereJoiner.add(columnName + "=" + value);
				}

			}
		}
		query.append(whereJoiner);
		try {
			int max = getJdbcTemplate().queryForObject(query.toString(), Integer.class);
			if (max <= 0)
				max = 1;
			else
				max++;
			return max;
		} catch (Exception ex) {
//			ex.printStackTrace();
			return 1;
		}

	}

	public ExceptionCode getCommonResultDataQuery(String query) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<LinkedHashMap<String, String>> result = new ArrayList<>();
		try {
			if (!ValidationUtil.isValid(query)) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query Invalid");
				return exceptionCode;
			}

			// Execute query and process result set
			ResultSetExtractor<ExceptionCode> mapper = new ResultSetExtractor<ExceptionCode>() {
				public ExceptionCode extractData(ResultSet rs) throws SQLException, DataAccessException {
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					boolean dataPresent = false;

					while (rs.next()) {
						LinkedHashMap<String, String> resultData = new LinkedHashMap<>();
						dataPresent = true;
						for (int cn = 1; cn <= colCount; cn++) {
							String columnName = metaData.getColumnName(cn);
							Object columnValue = rs.getObject(columnName);

							if (columnValue != null) {
								if (columnValue instanceof java.sql.Timestamp || columnValue instanceof java.sql.Date) {
									// Works for both Oracle and MSSQL
									Date dateValue = (Date) columnValue;
									SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
									columnValue = outputFormat.format(dateValue);
								}
							}

							resultData.put(CommonUtils.convertToReadableFormat(columnName.toUpperCase()),
									columnValue != null ? columnValue.toString() : "");
						}
						result.add(resultData);
					}

					if (dataPresent) {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse(result);
					} else {
						exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					}

					return exceptionCode;
				}
			};

			return (ExceptionCode) getJdbcTemplate().query(query, mapper);

		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public int updateTemplateHeaders(TemplateScheduleVb templateScheduleVb, boolean flag) {
		StringBuilder columns = new StringBuilder("STATUS = ?");
		List<Object> argsList = new ArrayList<>();
		argsList.add(templateScheduleVb.getProcessStatus());

		if (flag) {
			columns.append(", MAKER = ?");
			argsList.add(templateScheduleVb.getMaker());
		} else {
			columns.append(", VERIFIER = ?");
			argsList.add(templateScheduleVb.getVerifier());
		}

		if (templateScheduleVb.getActionType().equalsIgnoreCase("APPROVE")) {
			columns.append(", VERSION_NO = ?");
			argsList.add(templateScheduleVb.getVersion());
		}

		columns.append(" WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND SUBMISSION_DATE = " + dateConvert);
		argsList.add(templateScheduleVb.getCountry());
		argsList.add(templateScheduleVb.getLeBook());
		argsList.add(templateScheduleVb.getTemplateId());
		argsList.add(templateScheduleVb.getSubmissionDate()); // Assuming `dateConvert` is applied inside the method

		String query = "UPDATE RG_TEMPLATES_HEADER SET " + columns.toString();

		return getJdbcTemplate().update(query, argsList.toArray());
	}

	public int updateProcessControls(TemplateScheduleVb templateScheduleVb, boolean flag) {
		StringBuilder columns = new StringBuilder("RG_PROCESS_STATUS = ?");
		List<Object> argsList = new ArrayList<>();
		argsList.add(templateScheduleVb.getProcessStatus());

		if (flag) {
			columns.append(", MAKER = ?");
			argsList.add(templateScheduleVb.getMaker());
		} else {
			columns.append(", VERIFIER = ?");
			argsList.add(templateScheduleVb.getVerifier());
		}

		if (templateScheduleVb.getActionType().equalsIgnoreCase("APPROVE")) {
			columns.append(", VERSION_NO = ?");
			argsList.add(templateScheduleVb.getVersion());
		}

		// WHERE clause
		columns.append(" WHERE COUNTRY = ? AND LE_BOOK = ? AND TEMPLATE_ID = ? AND SUBMISSION_DATE = " + dateConvert);
		argsList.add(templateScheduleVb.getCountry());
		argsList.add(templateScheduleVb.getLeBook());
		argsList.add(templateScheduleVb.getTemplateId());
		argsList.add(templateScheduleVb.getSubmissionDate()); // Make sure this is in proper format if needed

		String query = "UPDATE RG_PROCESS_CONTROLS SET " + columns.toString();

		return getJdbcTemplate().update(query, argsList.toArray());
	}

	public ExceptionCode callProcTovalidateTemplate(TemplateScheduleVb vObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		strCurrentOperation = "Validation";
		strErrorDesc = "";
		Connection con = null;
		CallableStatement cs = null;
		setServiceDefaults();
		try {
			String logPath = commonDao.findVisionVariableValue("GDI_LOG_PATH");
			String Currency = commonDao.findVisionVariableValue("GDI_DEFAULT_CURRENCY");
			String date = new SimpleDateFormat("YYYYMMDD").format(new Date());
			con = getConnection();
			String procedure = databaseType.equalsIgnoreCase("ORACLE") ? "GENERICEXTRACTIONSPACKAGE.DOGENRICPROCEDURE"
					: "DOGENRICPROCEDURE";
			cs = con.prepareCall("{call " + procedure + " (?, ? ,?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ? ,? ,? ,?)}");

			String templateId = vObj.getTemplateId() + "_VAL";
			cs.setString(1, vObj.getCountry());// COUNTRY
			cs.setString(2, vObj.getLeBook());// LE_BOOK
			cs.setString(3, templateId);// TEMPLATE_ID
			cs.setString(4, vObj.getReportingDate());// REPORTING_DATE
			cs.setString(5, vObj.getReportingDate());// FEED_DATE
			cs.setString(6, "TF" + templateId + date);// STG_TABLE
			cs.setString(7, previousDate(vObj.getReportingDate()));// EXTRACTION_DATE
			cs.setString(8, Currency);// DEFAULT CURRENCY
			cs.setString(9, "Y");// parllel run
			cs.setString(10, " ");// DBLINK
			cs.setString(11, logPath);// GDI_LOG_PATH
			cs.setString(12, templateId + date);// GDI_LOG_FILENAME
			cs.setString(13, vObj.getReportingDate());// Business Date
			cs.setString(14, vObj.getReportingDate());// Business Date

			cs.registerOutParameter(15, java.sql.Types.VARCHAR); // Status
			cs.registerOutParameter(16, java.sql.Types.VARCHAR); // Error Message
			cs.execute();
			vObj.setStatus(cs.getString(15));
			vObj.setErrorMessage(cs.getString(16));
			cs.close();
			exceptionCode.setErrorMsg(vObj.getErrorMessage());
			strErrorDesc = vObj.getErrorMessage();
			// The procedure returns 0 to signify success, so we reviewed it and are
			// modifying the exception handling or error code accordingly.
			if (Integer.parseInt(vObj.getStatus()) == Constants.ERRONEOUS_OPERATION) {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			} else {
				exceptionCode.setErrorCode(Integer.parseInt(vObj.getStatus()));
			}
//			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception ex) {
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		} finally {
			JdbcUtils.closeStatement(cs);
		}
		return exceptionCode;
	}

	public static String beautifyXML(String xmlString,String categoryType) {
		try {
			// Escape special characters like '&' in the XML string
			xmlString = xmlString.replaceAll("&(?!amp;|lt;|gt;|quot;|apos;)", "&amp;");

			// Create a DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			// Create a DocumentBuilder
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Convert the XML string to an InputStream
			InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));

			// Parse the XML InputStream into a Document
			Document document = builder.parse(inputStream);

			// Check if the root element has the namespace, if not add it
			Element root = document.getDocumentElement();
			if (!categoryType.equalsIgnoreCase("FATCA")) {
				if (!root.hasAttribute("xmlns:crs")) {
					root.setAttribute("xmlns:crs", "http://example.com/crs-namespace");
				}
			}

			// Create a TransformerFactory
			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			// Create a Transformer for the XML
			Transformer transformer = transformerFactory.newTransformer();

			// Set output properties for pretty-printing
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			// Omit the XML declaration
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			// Create a DOMSource
			DOMSource source = new DOMSource(document);

			// Create a ByteArrayOutputStream to hold the output
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			// Create a StreamResult to get the output
			StreamResult result = new StreamResult(outputStream);

			// Transform the XML Document to a string
			transformer.transform(source, result);
			return outputStream.toString("UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String writeResultSetToXml(ResultSet rs) throws SQLException, ParserConfigurationException {
		StringBuilder xmlOutput = new StringBuilder();

		// Initialize XML document structure

		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		xmlOutput.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlOutput.append("<crs:AccountReports xmlns:crs=\"http://www.example.com/crs\"> \n");
		while (rs.next()) {
			// Start of the AccountReport element

			xmlOutput.append("<crs:AccountReport>");
			xmlOutput.append("<crs:ControllingPerson>");

			// Loop through columns
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnName(i);
				String value = rs.getString(i);

				if (value == null) {
					value = ""; // Handling null values to avoid null pointer exceptions
				}

				// Convert column name to XML-friendly format
				String xmlColumnName = ValidationUtil.toTitleCase(columnName).replaceAll("_", "");

				// Append column as an XML element
				xmlOutput.append("<crs:").append(xmlColumnName).append(">").append(escapeXml(value)).append("</crs:")
						.append(xmlColumnName).append(">");
			}

			// End of the ControllingPerson and AccountReport elements
			xmlOutput.append("</crs:ControllingPerson>");
			xmlOutput.append("</crs:AccountReport>");
		}

		xmlOutput.append("</crs:AccountReports>");

		return xmlOutput.toString();
	}

	private static String escapeXml(String input) {
		if (input == null) {
			return "";
		}
		return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	public ExceptionCode extractDataToXml(TemplateConfigVb vObject, String filter) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			Boolean isFatca = Constants.FATCA.equalsIgnoreCase(vObject.getCategoryType());
			
			String query = "SELECT ";
			String versionNo = "  VERSION_NO = (SELECT MAX(VERSION_NO ) FROM " + vObject.getSourceTable() + 
					" WHERE COUNTRY = '"+vObject.getCountry()+"' AND LE_BOOK = '"+vObject.getLeBook()+"' )";

			String cols = buildSelectColumns(vObject.getMappinglst());


			String headerQuery = "ENTITY_BUILDING_IDENTIFIER,ENTITY_STREET,ENTITY_COUNTRY_CODE,LEGAL_ADDRESS_TYPE,ENTITY_NAME,ENTITY_CITY,"
					+ "SENDING_COMPANY_IN,TRANSMITTING_COUNTRY, RECEIVING_COUNTRY, MESSAGE_TYPE, WARNING, CONTACT, MESSAGE_TYPE_INDIC, DOC_TYPE_IN,MESSAGE_REF_ID ";
			if(isFatca) {
				headerQuery = "SENDING_COMPANY_IN,TRANSMITTING_COUNTRY, RECEIVING_COUNTRY, MESSAGE_TYPE,MESSAGE_REF_ID,ENTITY_NAME,ENTITY_COUNTRY_CODE,"
						+ "ENTITY_POSTAL_CODE,ENTITY_CITY,ENTITY_COUNTRY_CODE,ENTITY_ADDRESS_FREE,FILER_CATEGORY,ENTITY_STREET";
			}
			

			if(cloud.equalsIgnoreCase("Y")) {
				 headerQuery = 
					    "T3.ENTITY_BUILDING_IDENTIFIER, T3.ENTITY_STREET, T3.ENTITY_COUNTRY_CODE, " +
					    "T3.LEGAL_ADDRESS_TYPE, T3.ENTITY_NAME, T3.ENTITY_CITY, " +
					    "T3.SENDING_COMPANY_IN AS SENDING_COMPANY_T3, " +
					    "T3.TRANSMITTING_COUNTRY AS TRANSMITTING_COUNTRY_T3, " +
					    "T3.RECEIVING_COUNTRY AS RECEIVING_COUNTRY_T3, " +
					    "T3.MESSAGE_TYPE AS MESSAGE_TYPE_T3, " +
					    "T3.WARNING AS WARNING_T3, " +
					    "T3.CONTACT AS CONTACT_T3, " +
					    "T3.MESSAGE_TYPE_INDIC AS MESSAGE_TYPE_INDIC_T3, " +
					    "T3.DOC_TYPE_IN AS DOC_TYPE_IN_T3,"
					    + "T3.MESSAGE_REF_ID AS MESSAGE_REF_ID_T3 ";
					    ;

			}
			if (!cloud.equalsIgnoreCase("Y")) {
			    query += cols.toString() + " ," + headerQuery + " FROM " + vObject.getSourceTable() + " T1,"
			            + vObject.getCbkFileName() + " ";
			 
			    String cond = " T1.COUNTRY = T3.COUNTRY AND T1.LE_BOOK = T3.LE_BOOK ";
			    query += " WHERE " + cond;
			} else {
			    query += cols.toString() + " ," + headerQuery + " FROM " + vObject.getSourceTable() + " T1, "
			            + vObject.getCbkFileName() + " ";
			    String cond = " T1.COUNTRY = T3.COUNTRY AND T1.LE_BOOK = T3.LE_BOOK ";
			    query += " WHERE " + cond;
			}

			// Add filter condition
			if (ValidationUtil.isValid(filter)) {
			    if (query.toLowerCase().contains(" where ")) {
			        query += " AND " + filter;
			    } else {
			        query += " WHERE " + filter;
			    }
			}

			// Add source table filter condition
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
			        && !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
			    if (query.toLowerCase().contains(" where ")) {
			        query += " AND " + vObject.getSourceTableFilter();
			    } else {
			        query += " WHERE " + vObject.getSourceTableFilter();
			    }
			}
			 if (query.toLowerCase().contains(" where ")) {
			        query += " AND " + versionNo;
			    } else {
			        query += " WHERE " +versionNo;
			    }
			String orderBy = " ORDER BY ROW_ID";
			query = query.concat(orderBy);

			String tmpFilePath = System.getProperty("java.io.tmpdir");
			String xml="";

			String sql = "Select Count(*) FROM " + vObject.getSourceTable() + " WHERe COUNTRY = ? AND LE_BOOK = ?";

			Object objParams[] = { vObject.getCountry(), vObject.getLeBook() };
			int cnt = getJdbcTemplate().queryForObject(sql, objParams, Integer.class);
			if (cnt == 0) {
				query = "SELECT " + headerQuery + " From " + vObject.getCbkFileName() + " where COUNTRY = '"
						+ vObject.getCountry() + "' AND LE_BOOK = '" + vObject.getLeBook() + "' ";
			}
			
			xml = getJdbcTemplate().query(query, (ResultSetExtractor<String>) rs -> isFatca?generateFatcaXml(rs,cnt):generateCrsXml(rs,cnt));
			
			String path = System.getProperty("java.io.tmpdir") + File.separator + vObject.getSourceTable() + ".xml";
	        
	        String beautified = beautifyXML(xml,vObject.getCategoryType());

	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
	            bw.write(beautified);
	        }
			// Set the response details in the exception code
			exceptionCode.setOtherInfo(vObject.getSourceTable());
			exceptionCode.setResponse(tmpFilePath); // Return the path of the generated XML file
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);

		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}

		return exceptionCode;
	}

	private static void tagElement(StringBuilder builder, String tag, String content) {
		if (content != null) {
			builder.append("<").append(tag).append(">").append(content).append("</").append(tag).append(">");
		}
	}

	/**
	 * Utility method to write an XML element with a single attribute.
	 */
	private static void tagElementWithAttribute(StringBuilder builder, String tag, String content, String attr,
			String attrValue) {
		if (content != null) {
			builder.append("<").append(tag).append(" ").append(attr).append("=\"").append(attrValue).append("\">")
					.append(content).append("</").append(tag).append(">");
		}
	}

	/**
	 * Utility method to write an XML element with multiple attributes.
	 */
	private static void tagElementWithAttributes(StringBuilder builder, String tag, String content,
			String... attributes) {
		if (content != null) {
			builder.append("<").append(tag);
			for (int i = 0; i < attributes.length; i += 2) {
				builder.append(" ").append(attributes[i]).append("=\"").append(attributes[i + 1]).append("\"");
			}
			builder.append(">").append(content).append("</").append(tag).append(">");
		}
	}

	public static String previousDate(String date) {
		String previousDateStr = "";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

		// Parse the input string to a LocalDate
		LocalDate inputDate = LocalDate.parse(date, formatter);

		// Subtract one day
		LocalDate previousDate = inputDate.minusDays(1);

		// Format and print the previous date
		previousDateStr = previousDate.format(formatter);
		return previousDateStr;

	}

	public static List<Map<String, Object>> errorLst(List<Map<String, Object>> l1, List<Map<String, Object>> l2,
			String rowIdKey, String errorColumnName) {
		Map<Object, List<Map<String, Object>>> errorMap = l2.stream()
				.collect(Collectors.groupingBy(errorRow -> errorRow.get(rowIdKey)));

		// Update l1 to include the error list
		for (Map<String, Object> dataRow : l1) {
			Object rowId = dataRow.get(rowIdKey);

			// Add the errors as a list under the new error column directly in l1
			List<Map<String, Object>> errorsForRow = errorMap.getOrDefault(rowId, Collections.emptyList());
			dataRow.put(errorColumnName, errorsForRow);
		}
		return l1;
	}

	public ExceptionCode templateValidation(TemplateScheduleVb templateScheduleVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String Status = "";
		if(!"Y".equalsIgnoreCase(cloud)) {
		exceptionCode = callProcTovalidateTemplate(templateScheduleVb);
		}else {
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}
		
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			templateScheduleCronDao.updateProcessControl("VE", templateScheduleVb, false);
			templateScheduleCronDao.updateProcessControlheader("VE", "", "", templateScheduleVb);
			exceptionCode = CommonUtils.getResultObject("Template", Constants.ERRONEOUS_OPERATION, "Validation", "");
		} else {
			int cnt = getErrorCount(templateScheduleVb);
			Status = "SP";
			if (cnt > 0) {
				Status = "VE";
				exceptionCode = CommonUtils.getResultObject("Template", Constants.ERRONEOUS_OPERATION, "Validation",
						"");
			} else {
				exceptionCode = CommonUtils.getResultObject("Template", Constants.SUCCESSFUL_OPERATION, "Validation",
						"");
				if (getPendCount(templateScheduleVb) > 0) {
					Status = "AP";
				}
			}
			templateScheduleVb.setRecordIndicator(Constants.STATUS_INSERT);
			templateScheduleCronDao.updateProcessControl(Status, templateScheduleVb, false);
			templateScheduleCronDao.updateProcessControlheader(Status, "", "", templateScheduleVb);

		}
		exceptionCode.setResponse(Status);
		return exceptionCode;
	}

	public String getAuthTokenURl(TemplateConfigVb templateConfigVb) {
		try {
			String query = "SELECT AUTH_CONNECTIVITY_DETAILS  FROM RG_TEMPLATE_CONFIG TAPPR WHERE TAPPR.COUNTRY =? AND TAPPR.LE_BOOK = ? AND  TAPPR.TEMPLATE_ID = ?";
			Object objParams[] = { templateConfigVb.getCountry(), templateConfigVb.getLeBook(),
					templateConfigVb.getTemplateId() };
			return getJdbcTemplate().queryForObject(query.toString(), objParams, String.class);
		} catch (Exception e) {
			return "";
		}
	}

	public int getCntsourceTable(String query) {
		int cnt = 0;

		cnt = getJdbcTemplate().queryForObject("SELECT COUNT (*)CNT FROM( " + query.toString() + " )  a1",
				Integer.class);
		return cnt;
	}

	// Bulk Reject
//	@Transactional(rollbackForClassName = { "com.vision.exception.RuntimeCustomException" })
//	public ExceptionCode doBulkReject(TemplateScheduleVb queryPopObj) {
//		setServiceDefaults();
//		ExceptionCode exceptionCode = new ExceptionCode();
//		List<Map<String, Object>> dataLst = queryPopObj.getDataLst(); // This contains the actual input
//
//		if (dataLst == null || dataLst.isEmpty()) {
//			exceptionCode = getResultObject(Constants.NO_RECORDS_TO_REJECT);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//
//		String sourceTable = queryPopObj.getSourceTable();
//
//		try {
//			for (Map<String, Object> record : dataLst) {
//				exceptionCode = deletePendingRecordByPk(sourceTable, record);
//			}
//			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
//				if (getPendCount(queryPopObj) > 0) {
//					queryPopObj.setProcessStatus("AP");
//				} else {
//					queryPopObj.setProcessStatus("VP");
//				}
//				updateTemplateHeaders(queryPopObj);
//				updateProcessControls(queryPopObj);
//				exceptionCode.setOtherInfo(queryPopObj);
//				exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
//			}
//			return exceptionCode;
//
//		} catch (Exception e) {
//			logger.error("Error during bulkReject", e);
//			exceptionCode = getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION);
//			exceptionCode.setOtherInfo(queryPopObj);
//			exceptionCode.setResponse(dataLst);
//			throw buildRuntimeCustomException(exceptionCode);
//		}
//	}

//	protected int deletePendingRecordByPk(String tableName, Map<String, Object> recordMap) {
//		StringBuilder query = new StringBuilder("DELETE FROM " + tableName + "_PEND WHERE ");
//		List<Object> params = new ArrayList<>();
//
//		List<CommonVb> pkColumns = commonDao.getPrimaryKey(tableName);
//		if (pkColumns == null || pkColumns.isEmpty()) {
//			throw new RuntimeException("Primary key columns not found for table: " + tableName);
//		}
//
//		int count = 0;
//		for (CommonVb pk : pkColumns) {
//			String column = pk.getScreenName();
//			if (!"VERSION_NO".equalsIgnoreCase(column)) {
//				if (count++ > 0) {
//					query.append(" AND ");
//				}
//				query.append(column).append(" = ?");
//				Object value = recordMap.get(column);
//				if (value == null) {
//					throw new RuntimeException("Missing PK value for column: " + column);
//				}
//				params.add(value);
//			}
//		}
//
//		logger.debug("Executing delete: " + query + " with params: " + params);
//		return getJdbcTemplate().update(query.toString(), params.toArray());
//	}
	protected ExceptionCode deletePendingRecordByPk(String tableName, Map<String, Object> recordMap) {
		setServiceDefaults();
		String baseTable = tableName.trim();
		StringBuilder whereClause = new StringBuilder();
		List<Object> params = new ArrayList<>();
		ExceptionCode exceptionCode = new ExceptionCode();

		List<CommonVb> pkColumns = commonDao.getPrimaryKey(baseTable);
		if (pkColumns == null || pkColumns.isEmpty()) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Primary key columns not found for table: " + baseTable);
		}

		int count = 0;
		for (CommonVb pk : pkColumns) {
			String column = pk.getScreenName();
			if (!"VERSION_NO".equalsIgnoreCase(column)) {
				if (count++ > 0) {
					whereClause.append(" AND ");
				}
				whereClause.append(column).append(" = ?");
				Object value = recordMap.get(column);
				if (value == null) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Missing PK value for column: " + column);
				}
				params.add(value);
			}
		}

		// 1. Check if record exists
		String checkQuery = "SELECT COUNT(1) FROM " + baseTable + "_PEND WHERE " + whereClause;
		Integer recordCount = getJdbcTemplate().queryForObject(checkQuery, params.toArray(), Integer.class);

		if (recordCount == null || recordCount == 0) {
			// 2. Record not found: throw ExceptionCode
			exceptionCode = getResultObject(Constants.ATTEMPT_TO_DELETE_UNEXISTING_RECORD);
			exceptionCode.setErrorMsg("Record not found in " + baseTable + "_PEND for deletion.");
			return exceptionCode;
		}

		// 3. Proceed with actual delete
		String deleteQuery = "DELETE FROM " + baseTable + "_PEND WHERE " + whereClause;
		retVal = getJdbcTemplate().update(deleteQuery, params.toArray());
		if (retVal == 0) {
			exceptionCode = getResultObject(Constants.ERRONEOUS_OPERATION);
			throw buildRuntimeCustomException(exceptionCode);
		} else {
			exceptionCode = getResultObject(Constants.SUCCESSFUL_OPERATION);
		}
		return exceptionCode;
	}

	public int getPendDataCnt(String baseTable, String whereClause, List<Object> params) {
		setServiceDefaults();
		params.add(intCurrentUserId);
		String checkQuery = "SELECT COUNT(1) FROM " + baseTable + " WHERE " + whereClause;
		Integer recordCount = getJdbcTemplate().queryForObject(checkQuery, Integer.class, params.toArray());
		return recordCount;
	}

	public int deleteAllRecords(TemplateScheduleVb vObject) {
		retVal = deletePendingRecord(vObject);
		retVal = doDeleteAppr(vObject);

		return Constants.SUCCESSFUL_OPERATION;

	}

	public int saveComments(TemplateScheduleVb vObject) {
		setServiceDefaults();
		String sql = " INSERT INTO RG_TEMPLATES_COMMENTS (TEMPLATE_ID, SUBMISSION_DATE, REPORTING_DATE, COUNTRY, "
				+ " LE_BOOK, COMMENTS, MAKER, VERIFIER, STATUS, RECORD_INDICATOR, DATE_LAST_MODIFIED, DATE_CREATION) "
				+ " VALUES ( ?,"+dateConvert+","+dateConvert+",?,?,?,?,?,?,?," + getDbFunction("SYSDATE") + "," + getDbFunction("SYSDATE") + ")";
		Object[] args = { vObject.getTemplateId(), vObject.getSubmissionDate(), vObject.getReportingDate(),
				vObject.getCountry(), vObject.getLeBook(), vObject.getComments(), intCurrentUserId, intCurrentUserId,
				vObject.getProcessStatus(),vObject.getRecordIndicator() };
		return getJdbcTemplate().update(sql, args);
	}
	public ArrayList<TemplateScheduleVb> getCommmentsLst(TemplateScheduleVb vObject){
		ArrayList<TemplateScheduleVb> collTemp = new ArrayList<>();
		try {
			String sql = " SELECT TEMPLATE_ID, SUBMISSION_DATE, REPORTING_DATE, STATUS_AT, RECORD_INDICATOR_AT, COUNTRY, LE_BOOK, COMMENTS, MAKER, "
					+ " VERIFIER, STATUS, RECORD_INDICATOR, "
					+ dbFunctionFormats("TAPPR.DATE_LAST_MODIFIED", "DATETIME_FORMAT", null)
					+ " DATE_LAST_MODIFIED,TAPPR.DATE_LAST_MODIFIED DATE_LAST_MODIFIED_1," + " "
					+ dbFunctionFormats("TAPPR.DATE_CREATION", "DATETIME_FORMAT", null) + " DATE_CREATION ,"
					+ " "+makerApprDesc +" , "+verifierApprDesc +" , "+recordIndicatorAtDesc.replaceAll("T1.", "TAPPR.")
					+ " FROM RG_TEMPLATES_COMMENTS TAPPR " + " where COUNTRY= ? AND "
					+ " LE_BOOK= ? AND TEMPLATE_ID= ? AND SUBMISSION_DATE= " + dateConvert + " AND REPORTING_DATE= "
					+ dateConvert +"ORDER BY DATE_LAST_MODIFIED_1 DESC ";
			Object[] args = { vObject.getCountry(), vObject.getLeBook(), vObject.getTemplateId(),vObject.getSubmissionDate(),vObject.getReportingDate()};
			collTemp = (ArrayList<TemplateScheduleVb>) getJdbcTemplate().query(sql, args,getCommentsMapper());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return collTemp;
	}
	protected RowMapper getCommentsMapper() {
		RowMapper mapper = new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				TemplateScheduleVb vObject = new TemplateScheduleVb();
				vObject.setCountry(rs.getString("COUNTRY"));
				vObject.setLeBook(rs.getString("LE_BOOK"));
				vObject.setTemplateId(rs.getString("TEMPLATE_ID"));
				vObject.setComments(rs.getString("COMMENTS"));
//				vObject.setStatus(rs.getInt("Status_NT"));
//				vObject.setStatus(rs.getInt("Status"));
				vObject.setRecordIndicator(rs.getInt("RECORD_INDICATOR"));
				vObject.setRecordIndicatorDesc(rs.getString("RECORD_INDICATOR_DESC"));
				vObject.setMaker(rs.getInt("MAKER"));
				vObject.setMakerName(rs.getString("MAKER_NAME"));
				vObject.setVerifier(rs.getInt("VERIFIER"));
				vObject.setVerifierName(rs.getString("VERIFIER_NAME"));
				vObject.setDateLastModified(rs.getString("DATE_LAST_MODIFIED"));
				vObject.setDateCreation(rs.getString("DATE_CREATION"));
				return vObject;
			}
		};
		return mapper;
	}
	public int insertIntoAuthToken(String token, Timestamp validTill) {
//
//	    StringBuilder columns = new StringBuilder("AUTH_TOKEN, VALID_TILL, STATUS_NT, STATUS");
//	    StringBuilder placeholders = new StringBuilder("?, "+dateTimeConvert+", ?, ?");
//
//	    List<Object> argsList = new ArrayList<>();
//	    argsList.add(token);
//	    argsList.add(validTill);
//	    argsList.add(1); // STATUS_NT
//	    argsList.add(1); // STATUS
//
//	    String sql = "INSERT INTO RG_AUTH_TOKEN (" + columns + ") VALUES (" + placeholders + ")";
//
//	    return getJdbcTemplate().update(sql, argsList.toArray());
		// ✅ Correct way
		StringBuilder columns = new StringBuilder("AUTH_TOKEN, VALID_TILL, STATUS_NT, STATUS");
		StringBuilder placeholders = new StringBuilder("?, ?, ?, ?"); // remove To_Date

		List<Object> argsList = new ArrayList<>();
		argsList.add(token);
		argsList.add(validTill); // must be java.sql.Timestamp
		argsList.add(1); // STATUS_NT
		argsList.add(1); // STATUS

		String sql = "INSERT INTO RG_AUTH_TOKEN (" + columns + ") VALUES (" + placeholders + ")";
		return getJdbcTemplate().update(sql, argsList.toArray());

	}
	public void markTokenAsExpired(String token) {
	    String updateSql = "UPDATE RG_AUTH_TOKEN SET STATUS = 9 WHERE AUTH_TOKEN = ?";
	    getJdbcTemplate().update(updateSql, token);
	}
	public Map<String, Object> fetchActiveAuthToken() {
	    String sql = "SELECT AUTH_TOKEN, VALID_TILL FROM RG_AUTH_TOKEN  WHERE STATUS = 1 ORDER BY VALID_TILL DESC  ";

	    try {
	        return getJdbcTemplate().queryForMap(sql);
	    } catch (Exception e) {
	        return null;
	    }
	}

	public TemplateScheduleVb fetchTemplateSchedule() {
	    String sql = " SELECT T1.COUNTRY, T1.LE_BOOK, T1.TEMPLATE_ID, " +
//	                 "T1.REPORTING_DATE, T1.SUBMISSION_DATE " +
	              dateFormat + "      (T1.SUBMISSION_DATE ," + formatdate
						+ ")SUBMISSION_DATE,"+
						  dateFormat + "      (T1.REPORTING_DATE ," + formatdate
							+ ")REPORTING_DATE " +
	                 " FROM rg_templates_header T1, Rg_Process_Controls T2 " +
	                 " WHERE T1.COUNTRY = T2.COUNTRY " +
	                 " AND T1.LE_BOOK = T2.LE_BOOK " +
	                 " AND T1.TEMPLATE_ID = T2.TEMPLATE_ID " +
	                 " AND T1.STATUS = T2.RG_PROCESS_STATUS " +
	                 " AND T1.STATUS IN ('SI')"
	                 + "AND T1.INTERNAL_STATUS  = 1";

	    List<TemplateScheduleVb> result = jdbcTemplate.query(sql, new RowMapper<TemplateScheduleVb>() {
	        @Override
	        public TemplateScheduleVb mapRow(ResultSet rs, int rowNum) throws SQLException {
	            TemplateScheduleVb vb = new TemplateScheduleVb();
	            vb.setCountry(rs.getString("COUNTRY"));
	            vb.setLeBook(rs.getString("LE_BOOK"));
	            vb.setTemplateId(rs.getString("TEMPLATE_ID"));
	            vb.setReportingDate(rs.getString("REPORTING_DATE"));
	            vb.setSubmissionDate(rs.getString("SUBMISSION_DATE"));
	            return vb;
	        }
	    });

	    return result.isEmpty() ? null : result.get(0);
	}
//	public String buildSelectColumns(List<TemplateMappingVb> mappings) {
//	    if (mappings == null || mappings.isEmpty()) {
//	        return "";
//	    }
//
//	    List<String> selectColumns = new ArrayList<>();
//	    // Use LinkedHashSet to preserve insertion order and do case-insensitive dedupe
//	    Set<String> seen = new LinkedHashSet<>();
//
//	    for (TemplateMappingVb m : mappings) {
//	        String src = m.getSourceColumn() == null ? "" : m.getSourceColumn().trim();
//	        String alias = m.getTargetColumn() == null ? "" : m.getTargetColumn().trim();
//
//	        if (src.isEmpty() || alias.isEmpty()) {
//	            continue;
//	        }
//
//	        String expr;
//
//	        // If already a TO_CHAR(...) or other explicit function starting with TO_CHAR, keep as-is but ensure alias
//	        if (src.toUpperCase().startsWith("TO_CHAR")) {
//	            expr = src + " AS " + alias;
//
//	        // Special-case when user indicated TIMESTAMP (or alias named Timestamp) -> create UTC timestamp string
//	        } else if ("TIMESTAMP".equalsIgnoreCase(src) || "TIMESTAMP".equalsIgnoreCase(alias)) {
//	            // use a safe alias name (avoid reserved words)
//	            String safeAlias = alias.replaceAll("\\s+", "_");
//	            expr = "TO_CHAR(SYSDATE, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS " + safeAlias;
//
//	        // Heuristic: if column name implies a date/time, wrap with TO_CHAR
//	        } else if (src.toUpperCase().contains("DATE")
//	                || src.toUpperCase().contains("DOB")
//	                || src.toUpperCase().contains("PERIOD")
//	                || src.toUpperCase().contains("TS")) {
//	            // assume column is in table T1 unless it already has a table qualifier or is an expression
//	            if (!src.contains("(") && !src.contains(".") ) {
//	                expr = "TO_CHAR(T1." + src + ",'RRRR-MM-DD') AS " + alias;
//	            } else {
//	                expr = "TO_CHAR(" + src + ",'RRRR-MM-DD') AS " + alias;
//	            }
//
//	        // Plain column name (no parentheses/functions) -> prefix with T1.
//	        } else if (!src.contains("(") && !src.contains(".")) {
//	            expr = "T1." + src + " AS " + alias;
//
//	        // Fallback: keep expression as-is and alias it
//	        } else {
//	            expr = src + " AS " + alias;
//	        }
//
//	        // Deduplicate based on a case-insensitive comparison of the final expression
//	        String dedupeKey = expr.toUpperCase(Locale.ROOT).replaceAll("\\s+", " ");
//	        if (!seen.contains(dedupeKey)) {
//	            seen.add(dedupeKey);
//	            selectColumns.add(expr);
//	        }
//	    }
//
//	    // Build a nicely formatted SELECT column fragment
//	    StringBuilder sb = new StringBuilder();
//	    for (int i = 0; i < selectColumns.size(); i++) {
//	        sb.append("    ").append(selectColumns.get(i));
//	        if (i < selectColumns.size() - 1) sb.append(",\n");
//	    }
//
//	    return sb.toString();
//	}



	    

	    public  String buildSelectColumns(List<TemplateMappingVb> mappings) {
	        if (mappings == null || mappings.isEmpty()) return "";

	        List<String> cols = new ArrayList<>();
	        Set<String> seen = new LinkedHashSet<>(); // preserves order
	        for (TemplateMappingVb m : mappings) {
	            String src = m.getSourceColumn() == null ? "" : m.getSourceColumn().trim();
	            String alias = m.getTargetColumn() == null ? "" : m.getTargetColumn().trim();
	            if (src.isEmpty() || alias.isEmpty()) continue;

	            String expr;
	            String srcUp = src.toUpperCase(Locale.ROOT);

	            if (srcUp.startsWith("TO_CHAR")) {
	                expr = src + " AS " + alias;
	            } else if ("TIMESTAMP".equalsIgnoreCase(src) || "TIMESTAMP".equalsIgnoreCase(alias)) {
	                String safeAlias = alias.replaceAll("\\s+", "_");
	                
	                if (databaseType.equalsIgnoreCase("ORACLE")) {
	                    expr = "TO_CHAR(SYSDATE,'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS " + safeAlias;
	                } else {
	                    expr = "LEFT(CONVERT(varchar(33), GETUTCDATE(), 126),19) + 'Z' AS " + safeAlias;
	                }
	            } else if (srcUp.contains("DATE") || srcUp.contains("DOB") || srcUp.contains("TS") || srcUp.contains("PERIOD")) {
	                String colRef = (!src.contains("(") && !src.contains(".")) ? "T1." + src : src;
	                if (databaseType.equalsIgnoreCase("ORACLE")) {
	                    expr = "TO_CHAR(" + colRef + ",'RRRR-MM-DD') AS " + alias;
	                } else {
	                    expr = "CONVERT(varchar(10)," + colRef + ",23) AS " + alias;
	                }
	            } else if (!src.contains("(") && !src.contains(".")) {
	                expr = "T1." + src + " AS " + alias;
	            } else {
	                expr = src + " AS " + alias;
	            }

	            String key = expr.toUpperCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
	            if (seen.add(key)) cols.add(expr);
	        }

	        return String.join(",\n    ", cols.isEmpty() ? Collections.emptyList() : cols).isEmpty()
	                ? ""
	                : "    " + String.join(",\n    ", cols);
	    }
	    public TemplateScheduleVb fetchTemplatemailSchedule(TemplateScheduleVb templateScheduleVb) {
			String sql = "  SELECT T1.TEMPLATE_ID,T1.TEMPLATE_NAME,T2.SUBMISSION_DATE,T2.REPORTING_DATE, "
					+ "  T2.MAKER,T2.SUBMITTER,V1.USER_NAME AS MAKER_NAME,V2.USER_NAME AS SUBMITTER_NAME, "
					+ "  V1.USER_EMAIL_ID AS MAKER_MAIL_ID,V2.USER_EMAIL_ID AS SUBMITTER_MAIL_ID, "
					+ "  A1.ALPHA_SUBTAB_DESCRIPTION  AS PROCESS_STATUS_DESC "
					+ "  FROM rg_templates_header  T1 JOIN rg_process_controls T2 " + "  ON T1.COUNTRY = T2.COUNTRY "
					+ "  AND T1.LE_BOOK = T2.LE_BOOK " + "  AND T1.TEMPLATE_ID = T2.TEMPLATE_ID "
					+ "  LEFT JOIN vision_users V1 ON V1.VISION_ID = T2.MAKER "
					+ "  LEFT JOIN vision_users V2 ON V2.VISION_ID = T2.SUBMITTER " + "  LEFT JOIN alpha_sub_tab A1 "
					+ "  ON A1.ALPHA_TAB = T1.STATUS_AT AND A1.ALPHA_SUB_TAB = T1.STATUS "
					+ "  WHERE T2.COUNTRY = ? " + "  AND T2.LE_BOOK = ? " + "  AND T2.TEMPLATE_ID = ? "
					+ "  AND T2.SUBMISSION_DATE =  " + dateConvert + "  AND T2.REPORTING_DATE =  " + dateConvert;
			Object[] args = { templateScheduleVb.getCountry(), templateScheduleVb.getLeBook(),
					templateScheduleVb.getTemplateId(), templateScheduleVb.getSubmissionDate(),
					templateScheduleVb.getReportingDate() };
			List<TemplateScheduleVb> result = jdbcTemplate.query(sql, new RowMapper<TemplateScheduleVb>() {
				@Override
				public TemplateScheduleVb mapRow(ResultSet rs, int rowNum) throws SQLException {
					TemplateScheduleVb vb = new TemplateScheduleVb();
					vb.setTemplateId(rs.getString("TEMPLATE_ID"));
					vb.setReportingDate(rs.getString("REPORTING_DATE"));
					vb.setSubmissionDate(rs.getString("SUBMISSION_DATE"));
					vb.setTemplateName(rs.getString("TEMPLATE_NAME"));
					vb.setMakerName(rs.getString("MAKER_NAME"));
					vb.setSubmitterName(rs.getString("SUBMITTER_NAME"));
					vb.setMakerMailId(rs.getString("MAKER_MAIL_ID"));
					vb.setSubmitterMailId(rs.getString("SUBMITTER_MAIL_ID"));
					vb.setProcessStatusDesc(rs.getString("PROCESS_STATUS_DESC"));
					return vb;
				}
			},args);

			return result.isEmpty() ? null : result.get(0);
		}

	    public List<TemplateScheduleVb> extractHistoryData(TemplateScheduleVb dObj, TemplateConfigVb vObject) {
			Vector<Object> params = new Vector<Object>();
			List<TemplateScheduleVb> collTemp =null;
			try {
				List<ColumnHeadersVb> lst = getColumns(vObject.getSourceTable().toUpperCase().trim()+"_HIS");
				StringJoiner joiner = new StringJoiner(",");
				if (lst != null && lst.size() > 0) {
					if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
						for (ColumnHeadersVb columnHeadersVb : lst) {
							if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
									|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
									|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
									&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0"))
								joiner.add(
										dbFunctionFormats("TAPPR." + columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5")
												+ " " + columnHeadersVb.getDbColumnName());
							else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
									|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
									|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
								joiner.add(dateFormat + "(TAPPR." + columnHeadersVb.getDbColumnName() + "," + formatdate
										+ ") " + columnHeadersVb.getDbColumnName());
							else
								joiner.add("TAPPR." + columnHeadersVb.getDbColumnName());
						}
					} else {
						for (ColumnHeadersVb columnHeadersVb : lst) {
//							if ("Y".equalsIgnoreCase(precisionFlag)) {
							if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
									|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
									|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
									&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0")
									&& "Y".equalsIgnoreCase(precisionFlag))
								joiner.add(dbFunctionFormats(columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5") + " "
										+ columnHeadersVb.getDbColumnName());
							else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
									|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
									|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
								joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + formatdate + ") "
										+ columnHeadersVb.getDbColumnName());
//							}
							else
								joiner.add(columnHeadersVb.getDbColumnName());
						}
					}

				}

				StringBuffer query = new StringBuffer("SELECT 'APPR' TAB, " + joiner + " FROM ");
//				String whereNotExists = getStringWhrNotExist(vObject.getSourceTable(), true);
				if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
					query = query.append(
							vObject.getSourceTable().toUpperCase().trim()+"_HIS" + " TAPPR," + vObject.getCbkFileName() + " ");
				} else {
					query = query.append(vObject.getSourceTable().toUpperCase().trim()+"_HIS" + " TAPPR");
				}
				if (ValidationUtil.isValid(dObj.getBasicFilterStr()) && !dObj.getBasicFilterStr().isEmpty()) {
					CommonUtils.addToQuery(dObj.getBasicFilterStr(), query);
				}
				if (vObject.getSourceTableFilter() != null && !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
					CommonUtils.addToQuery(vObject.getSourceTableFilter().replace("T1", "TAPPR"), query);
					if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
						query.append(" AND     TAPPR.COUNTRY = T3.COUNTRY    AND TAPPR.LE_BOOK = T3.LE_BOOK"
//								+ "       AND TAPPR.CUSTOMER_ID = T3.CUSTOMER_ID"
//								+ "       AND TAPPR.MESSAGE_REF_ID = T2.MESSAGE_REF_ID"
								);
					}
					StringBuffer newQuery = new StringBuffer("SELECT * FROM (" + query + ") TAPPR");
					query = newQuery;
				}
				if (ValidationUtil.isValid(dObj.getReportingDate())) {
					if ("MSSQL".equalsIgnoreCase(databaseType)) {
						CommonUtils.addToQuery("Convert(date, '" + dObj.getReportingDate() + "',113)", query);
					} else {
						CommonUtils.addToQuery("REPORTING_DATE = '" + dObj.getReportingDate() + "'", query);
					}
				}
				String orderBy = " ORDER BY ROW_ID,VERSION_NO DESC";
				String finalQuery ="SELECT ROW_NUMBER () OVER (ORDER BY ROW_ID, VERSION_NO DESC) NUM, ROWTEMP.* "
						+ "  FROM ("+query.toString()+") ROWTEMP";
				collTemp =getJdbcTemplate().query(finalQuery, getMapper());
				return collTemp;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	    
	    public ExceptionCode extractHistoryDataForExport(TemplateScheduleVb dObj, TemplateConfigVb vObject,String exportType,boolean isError) {
			Vector<Object> params = new Vector<Object>();
			List<TemplateScheduleVb> collTemp =null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
//				List<ColumnHeadersVb> lst = getColumns(vObject.getSourceTable().toUpperCase().trim()+"_HIS");
				List<ColumnHeadersVb> lst = getNeededColumnsForDownload(vObject.getSourceTable().toUpperCase().trim()+"_HIS");
				StringJoiner joiner = new StringJoiner(",");
				if (lst != null && lst.size() > 0) {
					if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
						for (ColumnHeadersVb columnHeadersVb : lst) {
							if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
									|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
									|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
									&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0"))
								joiner.add(
										dbFunctionFormats("TAPPR." + columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5")
												+ " " + columnHeadersVb.getDbColumnName());
							else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
									|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
									|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
								joiner.add(dateFormat + "(TAPPR." + columnHeadersVb.getDbColumnName() + "," + formatdate
										+ ") " + columnHeadersVb.getDbColumnName());
							else
								joiner.add("TAPPR." + columnHeadersVb.getDbColumnName());
						}
					} else {
						for (ColumnHeadersVb columnHeadersVb : lst) {
//							if ("Y".equalsIgnoreCase(precisionFlag)) {
							if ((columnHeadersVb.getColType().equalsIgnoreCase("NUMERIC")
									|| columnHeadersVb.getColType().equalsIgnoreCase("NUMBER")
									|| columnHeadersVb.getColType().equalsIgnoreCase("INT"))
									&& !columnHeadersVb.getDecimalCnt().equalsIgnoreCase("0")
									&& "Y".equalsIgnoreCase(precisionFlag))
								joiner.add(dbFunctionFormats(columnHeadersVb.getDbColumnName(), "NUM_FORMAT", "5") + " "
										+ columnHeadersVb.getDbColumnName());
							else if (columnHeadersVb.getColType().equalsIgnoreCase("DATE")
									|| columnHeadersVb.getColType().equalsIgnoreCase("DATETIME")
									|| columnHeadersVb.getColType().equalsIgnoreCase("TIMESTAMP"))
								joiner.add(dateFormat + "(" + columnHeadersVb.getDbColumnName() + "," + formatdate + ") "
										+ columnHeadersVb.getDbColumnName());
//							}
							else
								joiner.add(columnHeadersVb.getDbColumnName());
						}
					}

				}

				StringBuffer query = new StringBuffer("SELECT 'APPR' TAB, " + joiner + " FROM ");
//				String whereNotExists = getStringWhrNotExist(vObject.getSourceTable(), true);
				if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
					query = query.append(
							vObject.getSourceTable().toUpperCase().trim()+"_HIS" + " TAPPR," + vObject.getCbkFileName() + " ");
				} else {
					query = query.append(vObject.getSourceTable().toUpperCase().trim()+"_HIS" + " TAPPR");
				}
				if (ValidationUtil.isValid(dObj.getBasicFilterStr()) && !dObj.getBasicFilterStr().isEmpty()) {
					CommonUtils.addToQuery(dObj.getBasicFilterStr(), query);
				}
				if (vObject.getSourceTableFilter() != null && !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
					CommonUtils.addToQuery(vObject.getSourceTableFilter().replace("T1", "TAPPR"), query);
					if (cloud.equalsIgnoreCase("Y") && !vObject.getCategoryType().equalsIgnoreCase("RG")) {
						query.append(" AND     TAPPR.COUNTRY = T3.COUNTRY    AND TAPPR.LE_BOOK = T3.LE_BOOK"
//								+ "       AND TAPPR.CUSTOMER_ID = T3.CUSTOMER_ID"
//								+ "       AND TAPPR.MESSAGE_REF_ID = T2.MESSAGE_REF_ID"
								);
					}
					StringBuffer newQuery = new StringBuffer("SELECT * FROM (" + query + ") TAPPR");
					query = newQuery;
				}
				if (ValidationUtil.isValid(dObj.getReportingDate())) {
					if ("MSSQL".equalsIgnoreCase(databaseType)) {
						query.append(" WHERE REPORTING_DATE = Convert(date, '" + dObj.getReportingDate() + "',113)");
					} else {
						query.append(" WHERE REPORTING_DATE = '" + dObj.getReportingDate() + "'");
					}
				}
				String orderBy = " ORDER BY ROW_ID,VERSION_NO DESC";
//				return getQueryPopupResults(dObj, queryPend, query, whereNotExists, orderBy, params);
				String finalQuery ="SELECT ROW_NUMBER () OVER (ORDER BY ROW_ID, VERSION_NO DESC) NUM, ROWTEMP.* "
						+ "  FROM ("+query.toString()+") ROWTEMP";
//				collTemp =getJdbcTemplate().query(finalQuery, getMapper());
//				return collTemp;
				if ("CSV".equalsIgnoreCase(exportType)) {
					String tmpFilePath = System.getProperty("java.io.tmpdir");
					String csvFilePath = tmpFilePath + File.separator + vObject.getSourceTable() + ".csv";

					try (FileWriter fileWriter = new FileWriter(csvFilePath)) {
						// Execute the query and process the ResultSet
						getJdbcTemplate().query(finalQuery, new RowCallbackHandler() {
							@Override
							public void processRow(ResultSet rs) throws SQLException {
								try {
									writeResultSetToCsv(rs, fileWriter);
								} catch (IOException e) {
									throw new SQLException("Error writing to CSV file", e);
								}
							}
						});
					}

					exceptionCode.setOtherInfo(vObject.getSourceTable());
					exceptionCode.setResponse(tmpFilePath);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					return exceptionCode;
				} else if("XL".equalsIgnoreCase(exportType)) {
					String tempDir = System.getProperty("java.io.tmpdir");
					String fileSuffix = isError ? "_Errors" : "";
					String excelFilePath = tempDir + File.separator + dObj.getSourceTable()+"_HIS" + fileSuffix + ".xlsx";

					exceptionCode.setOtherInfo(dObj.getSourceTable()+"_HIS" +  fileSuffix);

					SXSSFWorkbook workbook = new SXSSFWorkbook();

					if (isError) {
						Sheet summarySheet = workbook.createSheet("Summary");

						String assetFolderUrl = commonDao.getAssetFolderUrl(servletContext);
						Map<Integer, XSSFCellStyle> styles = ExcelExportUtil.createStyles1(workbook, "4285F4");

						vObject.setMaker(intCurrentUserId);
						templateConfigWb.getScreenDao().fetchMakerVerifierNames(vObject);

						ReportsVb reports = new ReportsVb();
						reports.setApplicationTheme("8e8e8e");
						reports.setReportTitle(vObject.getTemplateDescription() + " - Errors");
						reports.setMakerName(vObject.getMakerName());

						ExcelExportUtil.createPromptsPage(reports, summarySheet, workbook, assetFolderUrl, styles, 0);
					}

					Sheet dataSheet = workbook.createSheet(dObj.getSourceTable().toUpperCase()+"_HIS");
					getJdbcTemplate().query(finalQuery.toString(), rs -> {
						try {
							writeResultSetToExcel(rs, dataSheet);
						} catch (IOException e) {
							throw new SQLException("Error writing to Excel file", e);
						}
					});


					try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
						workbook.write(fileOut);
					}
					workbook.close();

					exceptionCode.setResponse(tempDir);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				return exceptionCode;	
				}else {
					String tmpFilePath = System.getProperty("java.io.tmpdir");
					String xmlFilePath = tmpFilePath + File.separator + vObject.getSourceTable() + ".xml";

					StringBuilder xmlData = new StringBuilder();
					System.out.println("xml Query :"+finalQuery);
					getJdbcTemplate().query(finalQuery, new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							StringBuilder xmlBuilder = new StringBuilder();

							try {
								ResultSetMetaData metaData = rs.getMetaData();
								int columnCount = metaData.getColumnCount();
								Map<String, String> columnNameMap = new HashMap<>(); // Only stores column name mappings

								
								for (int i = 1; i <= columnCount; i++) {
								    String originalColumnName = metaData.getColumnLabel(i);
								    String normalizedKey = originalColumnName.replaceAll("_", "").toUpperCase();
								    columnNameMap.put(normalizedKey, originalColumnName); 
								}
								int row = rs.getRow();

								if (row == 1) {
									rootElementCrsBuilderxml(xmlBuilder);

									
									messageSpecCrsXmlBuilder(rs, xmlBuilder, columnNameMap);

									
									/* modified by gokulkumar */
									// CRS Body
									xmlBuilder.append("<crs:CrsBody>");
									xmlBuilder.append("<crs:ReportingFI>");

									if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
									    tagElement(xmlBuilder, "crs:ResCountryCode", rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
									}
									if (columnNameMap.containsKey("SENDINGCOMPANYIN") && columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
									    tagElementWithAttribute(xmlBuilder,
									        "crs:IN",
									        rs.getString(columnNameMap.get("SENDINGCOMPANYIN")),
									        "issuedBy",
									        rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE"))
									    );
									}
									if (columnNameMap.containsKey("ENTITYNAME")) {
									    tagElement(xmlBuilder, "crs:Name", rs.getString(columnNameMap.get("ENTITYNAME")));
									}
									
									
									
									/* modified by gokulkumar */
									// Address
									if (columnNameMap.containsKey("LEGALADDRESSTYPE")) {
									    addressCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
									}

									
									docSpecCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
									xmlBuilder.append("</crs:ReportingFI>");
									xmlBuilder.append("<crs:ReportingGroup>");
								}

								accountReportFooterCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
									

									/* modified by gokulkumar */
								// AccountHolder
								xmlBuilder.append("<crs:AccountHolder>");
								xmlBuilder.append("<crs:Individual>");
								// ResCountryCode
								if (columnNameMap.containsKey("RESCOUNTRYCODE")) {
								    tagElement(xmlBuilder, "crs:ResCountryCode", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));
								}
								// TIN with attribute "issuedBy"
								String taxIdNumber = columnNameMap.containsKey("TAXIDNUMBER") 
								        ? rs.getString(columnNameMap.get("TAXIDNUMBER")) : null;
								String resCountryCode = columnNameMap.containsKey("RESCOUNTRYCODE") 
								        ? rs.getString(columnNameMap.get("RESCOUNTRYCODE")) : null;

								if (taxIdNumber != null) {
								    tagElementWithAttribute(xmlBuilder, "crs:TIN", taxIdNumber, "issuedBy", resCountryCode != null ? resCountryCode : "");
								}
								// Name section
								xmlBuilder.append("<crs:Name>");
								if (columnNameMap.containsKey("FIRSTNAME")) {
								    tagElement(xmlBuilder, "crs:FirstName", rs.getString(columnNameMap.get("FIRSTNAME")));
								}

								if (columnNameMap.containsKey("LASTNAME")) {
								    tagElement(xmlBuilder, "crs:LastName", rs.getString(columnNameMap.get("LASTNAME")));
								}
								xmlBuilder.append("</crs:Name>");


								
								/* modified by gokulkumar */
								// address
								xmlBuilder.append("<crs:Address>");

								// CountryCode
								if (columnNameMap.containsKey("COUNTRYCODE")) {
								    tagElement(xmlBuilder, "cfc:CountryCode", rs.getString(columnNameMap.get("COUNTRYCODE")));
								}

								if (columnNameMap.containsKey("ADDRESSFREE")) {
								    String street = rs.getString(columnNameMap.get("ADDRESSFREE"));
								    tagElement(xmlBuilder, "cfc:AddressFree", street != null ? street : "");
								}

								
								
								// BirthInfo
								xmlBuilder.append("<crs:BirthInfo>");
								if (columnNameMap.containsKey("DATEOFBIRTH")) {
								    String birthDate = rs.getString(columnNameMap.get("DATEOFBIRTH"));
								    tagElement(xmlBuilder, "crs:BirthDate", birthDate != null ? birthDate : "");
								}
								if (columnNameMap.containsKey("CITY")) {
								    String city = rs.getString(columnNameMap.get("CITY"));
								    tagElement(xmlBuilder, "cfc:City", city != null ? city : "");
								}
						
								
								xmlBuilder.append("<crs:CountryInfo>");
								if (columnNameMap.containsKey("BIRTHCOUNTRYCODE")) {
								    tagElement(xmlBuilder, "cfc:CountryCode", rs.getString(columnNameMap.get("BIRTHCOUNTRYCODE")));
								}
								xmlBuilder.append("</crs:CountryInfo>");
								xmlBuilder.append("</crs:BirthInfo>");
								xmlBuilder.append("</crs:Individual>");
								xmlBuilder.append("</crs:AccountHolder>");
								// Account Balance
								String accountBalance = columnNameMap.containsKey("ACCOUNTBALANCE")
								        ? rs.getString(columnNameMap.get("ACCOUNTBALANCE")) : null;

								String acctCurrency = columnNameMap.containsKey("ACCTCURRENCY")
								        ? rs.getString(columnNameMap.get("ACCTCURRENCY")) : null;

								if (accountBalance != null) {
								    tagElementWithAttribute(
								        xmlBuilder,
								        "crs:AccountBalance",
								        accountBalance,
								        "currCode", acctCurrency != null ? acctCurrency : ""
								    );
								}

								

								xmlBuilder.append("</crs:AccountReport>");
								xmlData.append(xmlBuilder);

							} catch (Exception e) {
								e.printStackTrace();
								throw new SQLException("Error generating CRS XML", e);
							}
						}
					});
					// Append XML footer after processing all rows
					xmlData.append("</crs:ReportingGroup>");
					xmlData.append("</crs:CrsBody>" + "</crs:CRS_OECD>");

					// Now xmlData contains the complete XML document

//					System.out.println(xmlData.toString());
					String beautifiedXml = beautifyXML(xmlData.toString(),vObject.getCategoryType());

					// Write the beautified XML to a file
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(xmlFilePath))) {
						writer.write(beautifiedXml);
					}

					// Set the response details in the exception code
					exceptionCode.setOtherInfo(vObject.getSourceTable());
					exceptionCode.setResponse(tmpFilePath); // Return the path of the generated XML file
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					return exceptionCode;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return exceptionCode;
			}
		}
	    private String generateCrsXml(ResultSet rs,int cnt) throws SQLException {

	        StringBuilder xmlBuilder = new StringBuilder();
	        boolean isFirstRow = true;
	        ResultSetMetaData md = rs.getMetaData();
	        int columnCount = md.getColumnCount();
	        Map<String, String> columnNameMap = new HashMap<>();

	        while (rs.next()) {

	            // Build column map once per row
	        	columnNameMap.clear();
	            for (int i = 1; i <= columnCount; i++) {
	                String dbCol = md.getColumnLabel(i);
	                String key = dbCol.replaceAll("_", "").toUpperCase();
	                columnNameMap.put(key, dbCol);
	            }
            if(isFirstRow){
				// Start root element
            	rootElementCrsBuilderxml(xmlBuilder);

				messageSpecCrsXmlBuilder(rs, xmlBuilder, columnNameMap);

				// CRS Body
				xmlBuilder.append("<crs:CrsBody>");
				xmlBuilder.append("<crs:ReportingFI>");

				if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
				    tagElement(xmlBuilder, "crs:ResCountryCode", rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
				}
				if (columnNameMap.containsKey("SENDINGCOMPANYIN") && columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
				    tagElementWithAttribute(xmlBuilder,
				        "crs:IN",
				        rs.getString(columnNameMap.get("SENDINGCOMPANYIN")),
				        "issuedBy",
				        rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE"))
				    );
				}
				if (columnNameMap.containsKey("ENTITYNAME")) {
				    tagElement(xmlBuilder, "crs:Name", rs.getString(columnNameMap.get("ENTITYNAME")));
				}
				
				
				// Address
				if (columnNameMap.containsKey("LEGALADDRESSTYPE")) {
				    addressCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
				}

				// DocSpec
				docSpecCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
				xmlBuilder.append("</crs:ReportingFI>");
				xmlBuilder.append("<crs:ReportingGroup>");
				 isFirstRow = false;
			}
	            accountReportCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
	         // ---------------- FOOTER ----------------
				// AccountReport
				accountReportFooterCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
					

				
				
				// AccountHolder
				accountHolderCrsXmlBuilder(rs, xmlBuilder, columnNameMap);
				String accountBalance = columnNameMap.containsKey("ACCOUNTBALANCE")
				        ? rs.getString(columnNameMap.get("ACCOUNTBALANCE")) : null;

				String acctCurrency = columnNameMap.containsKey("ACCTCURRENCY")
				        ? rs.getString(columnNameMap.get("ACCTCURRENCY")) : null;

				if (accountBalance != null) {
				    tagElementWithAttribute(
				        xmlBuilder,
				        "crs:AccountBalance",
				        accountBalance,
				        "currCode", acctCurrency != null ? acctCurrency : ""
				    );
				}

				

				xmlBuilder.append("</crs:AccountReport>");
	        }
			xmlBuilder.append("</crs:ReportingGroup>");
			xmlBuilder.append("</crs:CrsBody>" + "</crs:CRS_OECD>");


	        return xmlBuilder.toString();
	    }

		private void accountHolderCrsXmlBuilder(ResultSet rs, StringBuilder xmlBuilder,
				Map<String, String> columnNameMap) throws SQLException {
			xmlBuilder.append("<crs:AccountHolder>");
			xmlBuilder.append("<crs:Individual>");
			// ResCountryCode
			if (columnNameMap.containsKey("RESCOUNTRYCODE")) {
			    tagElement(xmlBuilder, "crs:ResCountryCode", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));
			}
			// TIN with attribute "issuedBy"
			String taxIdNumber = columnNameMap.containsKey("TAXIDNUMBER") 
			        ? rs.getString(columnNameMap.get("TAXIDNUMBER")) : null;
			String resCountryCode = columnNameMap.containsKey("RESCOUNTRYCODE") 
			        ? rs.getString(columnNameMap.get("RESCOUNTRYCODE")) : null;

			if (taxIdNumber != null) {
			    tagElementWithAttribute(xmlBuilder, "crs:TIN", taxIdNumber, "issuedBy", resCountryCode != null ? resCountryCode : "");
			}
			// Name section
			xmlBuilder.append("<crs:Name>");
			if (columnNameMap.containsKey("FIRSTNAME")) {
			    tagElement(xmlBuilder, "crs:FirstName", rs.getString(columnNameMap.get("FIRSTNAME")));
			}

			if (columnNameMap.containsKey("LASTNAME")) {
			    tagElement(xmlBuilder, "crs:LastName", rs.getString(columnNameMap.get("LASTNAME")));
			}
			xmlBuilder.append("</crs:Name>");


			// address
			xmlBuilder.append("<crs:Address>");

			// CountryCode
			if (columnNameMap.containsKey("COUNTRYCODE")) {
			    tagElement(xmlBuilder, "cfc:CountryCode", rs.getString(columnNameMap.get("COUNTRYCODE")));
			}

			if (columnNameMap.containsKey("ADDRESSFREE")) {
			    String street = rs.getString(columnNameMap.get("ADDRESSFREE"));
			    tagElement(xmlBuilder, "cfc:AddressFree", street != null ? street : "");
			}
			xmlBuilder.append("</crs:Address>");

			
			// BirthInfo
			xmlBuilder.append("<crs:BirthInfo>");
			if (columnNameMap.containsKey("DATEOFBIRTH")) {
			    String birthDate = rs.getString(columnNameMap.get("DATEOFBIRTH"));
			    tagElement(xmlBuilder, "crs:BirthDate", birthDate != null ? birthDate : "");
			}
			if (columnNameMap.containsKey("CITY")) {
			    String city = rs.getString(columnNameMap.get("CITY"));
			    tagElement(xmlBuilder, "cfc:City", city != null ? city : "");
			}

			
			xmlBuilder.append("<crs:CountryInfo>");
			if (columnNameMap.containsKey("BIRTHCOUNTRYCODE")) {
			    tagElement(xmlBuilder, "cfc:CountryCode", rs.getString(columnNameMap.get("BIRTHCOUNTRYCODE")));
			}
			xmlBuilder.append("</crs:CountryInfo>");
			xmlBuilder.append("</crs:BirthInfo>");
			xmlBuilder.append("</crs:Individual>");
			xmlBuilder.append("</crs:AccountHolder>");
		}

		private void accountReportFooterCrsXmlBuilder(ResultSet rs, StringBuilder xmlBuilder,
				Map<String, String> columnNameMap) throws SQLException {
			xmlBuilder.append("<crs:AccountReport>");
			xmlBuilder.append("<crs:DocSpec>");
			
			if (columnNameMap.containsKey("DOCTYPEINDIC")) {
			    tagElement(xmlBuilder, "stf:DocTypeIndic", rs.getString(columnNameMap.get("DOCTYPEINDIC")));
			}
			
			
			if (columnNameMap.containsKey("DOCREFID")) {
			    tagElement(xmlBuilder, "stf:DocRefId", rs.getString(columnNameMap.get("DOCREFID"))+CommonUtils.generateRandom32());
			}
			xmlBuilder.append("</crs:DocSpec>");
 
			
			String accountNumber = columnNameMap.containsKey("ACCOUNTNUMBER")
				    ? rs.getString(columnNameMap.get("ACCOUNTNUMBER")) : null;

				String accountType = columnNameMap.containsKey("ACCOUNTTYPE")
				    ? rs.getString(columnNameMap.get("ACCOUNTTYPE")) : "";

				String accountStatus = columnNameMap.containsKey("ACCOUNTSTATUS")
				    ? rs.getString(columnNameMap.get("ACCOUNTSTATUS")) : null;

				if (accountNumber != null) {
					 tagElement(xmlBuilder, "crs:AccountNumber", accountNumber);
				}
		}

		private void accountReportCrsXmlBuilder(ResultSet rs, StringBuilder xmlBuilder,
				Map<String, String> columnNameMap) throws SQLException {
			xmlBuilder.append("<crs:AccountReport>");

			// DocSpec
			xmlBuilder.append("<crs:DocSpec>");

			if (columnNameMap.containsKey("DOCTYPEINDIC"))
			    tagElement(xmlBuilder, "stf:DocTypeIndic", rs.getString(columnNameMap.get("DOCTYPEINDIC")));

			if (columnNameMap.containsKey("DOCREFID"))
			    tagElement(xmlBuilder, "stf:DocRefId",
			        rs.getString(columnNameMap.get("DOCREFID")) + CommonUtils.generateRandom32());

			xmlBuilder.append("</crs:DocSpec>");

			// Account Number
			if (columnNameMap.containsKey("ACCOUNTNUMBER"))
			    tagElement(xmlBuilder, "crs:AccountNumber", rs.getString(columnNameMap.get("ACCOUNTNUMBER")));

			// Account Holder
			xmlBuilder.append("<crs:AccountHolder><crs:Individual>");

			if (columnNameMap.containsKey("RESCOUNTRYCODE"))
			    tagElement(xmlBuilder, "crs:ResCountryCode", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));

			if (columnNameMap.containsKey("TAXIDNUMBER"))
			    tagElementWithAttribute(xmlBuilder, "crs:TIN",
			        rs.getString(columnNameMap.get("TAXIDNUMBER")), "issuedBy",
			        rs.getString(columnNameMap.get("RESCOUNTRYCODE")));

			xmlBuilder.append("<crs:Name>");
			if (columnNameMap.containsKey("FIRSTNAME"))
			    tagElement(xmlBuilder, "crs:FirstName", rs.getString(columnNameMap.get("FIRSTNAME")));
			if (columnNameMap.containsKey("LASTNAME"))
			    tagElement(xmlBuilder, "crs:LastName", rs.getString(columnNameMap.get("LASTNAME")));
			xmlBuilder.append("</crs:Name>");

			xmlBuilder.append("</crs:Individual></crs:AccountHolder>");

			// Birth Info
			xmlBuilder.append("<crs:BirthInfo>");
			if (columnNameMap.containsKey("DATEOFBIRTH"))
			    tagElement(xmlBuilder, "crs:BirthDate", rs.getString(columnNameMap.get("DATEOFBIRTH")));
			xmlBuilder.append("</crs:BirthInfo>");

			// Account Balance
			if (columnNameMap.containsKey("ACCOUNTBALANCE"))
			    tagElementWithAttribute(xmlBuilder, "crs:AccountBalance",
			        rs.getString(columnNameMap.get("ACCOUNTBALANCE")),
			        "currCode", rs.getString(columnNameMap.get("ACCTCURRENCY")));

			xmlBuilder.append("</crs:AccountReport>");
		}

		private void docSpecCrsXmlBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<crs:DocSpec>");

			if (columnNameMap.containsKey("DOCTYPEINDIC")) {
			    tagElement(xmlBuilder, "stf:DocTypeIndic", rs.getString(columnNameMap.get("DOCTYPEINDIC")));
			}

			if (columnNameMap.containsKey("MESSAGEREFID")) {
			    tagElement(xmlBuilder, "stf:DocRefId", rs.getString(columnNameMap.get("MESSAGEREFID")));
			}

			xmlBuilder.append("</crs:DocSpec>");
		}

		private void addressCrsXmlBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<crs:Address legalAddressType=\"")
			          .append(rs.getString(columnNameMap.get("LEGALADDRESSTYPE")))
			          .append("\">");

			if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
			    tagElement(xmlBuilder, "cfc:CountryCode", rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
			}

			xmlBuilder.append("<cfc:AddressFix>");

			if (columnNameMap.containsKey("ENTITYSTREET")) {
			    tagElement(xmlBuilder, "cfc:Street", rs.getString(columnNameMap.get("ENTITYSTREET")));
			}

			if (columnNameMap.containsKey("ENTITYBUILDINGIDENTIFIER")) {
			    tagElement(xmlBuilder, "cfc:BuildingIdentifier", rs.getString(columnNameMap.get("ENTITYBUILDINGIDENTIFIER")));
			}

			if (columnNameMap.containsKey("ENTITYCITY")) {
			    tagElement(xmlBuilder, "cfc:City", rs.getString(columnNameMap.get("ENTITYCITY")));
			}

			xmlBuilder.append("</cfc:AddressFix>");
			xmlBuilder.append("</crs:Address>");
		}

		private void messageSpecCrsXmlBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<crs:MessageSpec>");
			if (columnNameMap.containsKey("SENDINGCOMPANYIN")) {
			    tagElement(xmlBuilder, "crs:SendingCompanyIN", rs.getString(columnNameMap.get("SENDINGCOMPANYIN")));
			}
			if (columnNameMap.containsKey("TRANSMITTINGCOUNTRY")) {
			    tagElement(xmlBuilder, "crs:TransmittingCountry", rs.getString(columnNameMap.get("TRANSMITTINGCOUNTRY")));
			}
			if (columnNameMap.containsKey("RECEIVINGCOUNTRY")) {
			    tagElement(xmlBuilder, "crs:ReceivingCountry", rs.getString(columnNameMap.get("RECEIVINGCOUNTRY")));
			}
			if (columnNameMap.containsKey("MESSAGETYPE")) {
			    tagElement(xmlBuilder, "crs:MessageType", rs.getString(columnNameMap.get("MESSAGETYPE")));
			}
			if (columnNameMap.containsKey("WARNING")) {
			    tagElement(xmlBuilder, "crs:Warning", rs.getString(columnNameMap.get("WARNING")));
			}
			if (columnNameMap.containsKey("CONTACT")) {
			    tagElement(xmlBuilder, "crs:Contact", rs.getString(columnNameMap.get("CONTACT")));
			}
			if (columnNameMap.containsKey("MESSAGEREFID")) {
			    tagElement(xmlBuilder, "crs:MessageRefId", rs.getString(columnNameMap.get("MESSAGEREFID"))+CommonUtils.generateRandom32());
			}
			if (columnNameMap.containsKey("MESSAGETYPEINDIC")) {
			    tagElement(xmlBuilder, "crs:MessageTypeIndic", rs.getString(columnNameMap.get("MESSAGETYPEINDIC")));
			}
			if (columnNameMap.containsKey("REPORTINGPERIOD")) {
			    tagElement(xmlBuilder, "crs:ReportingPeriod", rs.getString(columnNameMap.get("REPORTINGPERIOD")));
			}
			if (columnNameMap.containsKey("TIMESTAMP")) {
			    tagElement(xmlBuilder, "crs:Timestamp", rs.getString(columnNameMap.get("TIMESTAMP")));
			}

			xmlBuilder.append("</crs:MessageSpec>");
		}

		private void rootElementCrsBuilderxml(StringBuilder xmlBuilder) {
			xmlBuilder.append("<crs:CRS_OECD xmlns:crs=\"urn:oecd:ties:crs:v2\" ")
			.append("xmlns:cfc=\"urn:oecd:ties:commontypesfatcacrs:v2\" ")
			.append("xmlns:ftc=\"urn:oecd:ties:fatca:v1\" ")
			.append("xmlns:iso=\"urn:oecd:ties:isocrstypes:v1\" ")
			.append("xmlns:stf=\"urn:oecd:ties:crsstf:v5\" ")
			.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
			.append("version=\"2.0\" ")
			.append("xsi:schemaLocation=\"urn:oecd:ties:crs:v1 CrsXML_v2.0.xsd\">");
		}	    
	    
	    /**
	     * creating a fatca Xml
	     * @param rs
	     * @return
	     * @throws SQLException
	     */
	    private String generateFatcaXml(ResultSet rs,int cnt) throws SQLException {

	        StringBuilder xmlBuilder = new StringBuilder();
	        boolean isFirstRow = true;
	        ResultSetMetaData md = rs.getMetaData();
	        int columnCount = md.getColumnCount();
	        Map<String, String> columnNameMap = new HashMap<>();

			while (rs.next()) {
				// Build column map once per row
				columnNameMap.clear();
				for (int i = 1; i <= columnCount; i++) {
					String dbCol = md.getColumnLabel(i);
					String key = dbCol.replaceAll("_", "").toUpperCase();
					columnNameMap.put(key, dbCol);
				}
				if (isFirstRow) {

					// Start root element
					buildDefaultXmlBuilder(xmlBuilder);
					
					//Message Spec
					xmlMessageSpecBuilder(rs, xmlBuilder, columnNameMap);
					
					xmlBuilder.append("<ftc:FATCA>");
					xmlBuilder.append("<ftc:ReportingFI>");

					if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
						tagElement(xmlBuilder, "sfa:ResCountryCode",
								rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
					}
					if (columnNameMap.containsKey("ENTITYTIN")
							&& columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
						tagElementWithAttribute(xmlBuilder, "sfa:TIN",
								rs.getString(columnNameMap.get("ENTITYTIN")), "issuedBy",
								rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
					}
					if (columnNameMap.containsKey("ENTITYNAME")) {
						tagElement(xmlBuilder, "sfa:Name", rs.getString(columnNameMap.get("ENTITYNAME")));
					}

					// Address
					if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
						xmlAddressTagBuilder(rs, xmlBuilder, columnNameMap);
					}

					if (columnNameMap.containsKey("FILERCATEGORY")) {
						tagElement(xmlBuilder, "ftc:FilerCategory", rs.getString(columnNameMap.get("FILERCATEGORY")));
					}
					xmlBuilder.append("<ftc:DocSpec>");
					if (columnNameMap.containsKey("DOCTYPEINDIC")) {
						tagElement(xmlBuilder, "ftc:DocTypeIndic", rs.getString(columnNameMap.get("DOCTYPEINDIC")));
					}
					if (columnNameMap.containsKey("MESSAGEREFID")) {
						tagElement(xmlBuilder, "ftc:DocRefId", rs.getString(columnNameMap.get("MESSAGEREFID")));
					}

					xmlBuilder.append("</ftc:DocSpec>");
					xmlBuilder.append("</ftc:ReportingFI>");
					xmlBuilder.append("<ftc:ReportingGroup>");
					isFirstRow = false;
					if(cnt==0) {
						createDefaultStructure(rs, xmlBuilder, columnNameMap);
					}
				}
				// DocSpec
				if (cnt != 0) {
					xmlBuilder.append("<ftc:AccountReport>");
					xmldocSpecBuilder(rs, xmlBuilder, columnNameMap);
					// Account Holder Xml
					xmlAccountHolderBuilder(rs, xmlBuilder, columnNameMap);
					// ---------------- FOOTER ----------------
					// AccountReport
					xmlFooterAccountReportBuilder(rs, xmlBuilder, columnNameMap);
				}
			}
			xmlBuilder.append("</ftc:ReportingGroup>");
			xmlBuilder.append("</ftc:FATCA>");
			xmlBuilder.append("</ftc:FATCA_OECD>");
			xmlBuilder.append("</Object>");
			xmlBuilder.append("</Signature>");


	        return xmlBuilder.toString();
	    }

		private void createDefaultStructure(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap) {
			try {
				xmlBuilder.append("<ftc:NilReport>");
				xmlBuilder.append("<ftc:DocSpec>");

				if (columnNameMap.containsKey("MESSAGETYPE"))
					tagElement(xmlBuilder, "ftc:DocTypeIndic", rs.getString(columnNameMap.get("MESSAGETYPE")));

				if (columnNameMap.containsKey("MESSAGEREFID"))
					tagElement(xmlBuilder, "ftc:DocRefId",
							rs.getString(columnNameMap.get("MESSAGEREFID")) + CommonUtils.generateRandom32());

				xmlBuilder.append("</ftc:DocSpec>");
				xmlBuilder.append("<ftc:NoAccountToReport>");
				xmlBuilder.append("yes") ;
				xmlBuilder.append("</ftc:NoAccountToReport>");

				xmlBuilder.append("</ftc:NilReport>");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void xmlFooterAccountReportBuilder(ResultSet rs, StringBuilder xmlBuilder,
				Map<String, String> columnNameMap) throws SQLException {
			
			xmlBuilder.append("<ftc:AccountReport>");
			xmlBuilder.append("<ftc:DocSpec>");

			if (columnNameMap.containsKey("DOCTYPEINDIC")) {
				tagElement(xmlBuilder, "ftc:DocTypeIndic", rs.getString(columnNameMap.get("DOCTYPEINDIC")));
			}

			if (columnNameMap.containsKey("DOCREFID")) {
				tagElement(xmlBuilder, "ftc:DocRefId",
						rs.getString(columnNameMap.get("DOCREFID")) + CommonUtils.generateRandom32());
			}
			xmlBuilder.append("</ftc:DocSpec>");

			String accountNumber = columnNameMap.containsKey("ACCOUNTNUMBER")
					? rs.getString(columnNameMap.get("ACCOUNTNUMBER"))
					: null;

			String accountType = columnNameMap.containsKey("ACCOUNTTYPE")
					? rs.getString(columnNameMap.get("ACCOUNTTYPE"))
					: "";

			String accountStatus = columnNameMap.containsKey("ACCOUNTSTATUS")
					? rs.getString(columnNameMap.get("ACCOUNTSTATUS"))
					: null;

			if (accountNumber != null) {
				tagElement(xmlBuilder, "ftc:AccountNumber", accountNumber);
			}
			if (accountNumber != null) {
				tagElement(xmlBuilder, "ftc:AccountClosed", accountStatus);
			}


			// Account Holder
			xmlAccountHolderFooterBuilder(rs, xmlBuilder, columnNameMap);
			
			//Substantial Owner
			xmlSubstantialOwnerBuilder(rs, xmlBuilder, columnNameMap);

			// Account Balance
			String accountBalance = columnNameMap.containsKey("ACCOUNTBALANCE")
					? rs.getString(columnNameMap.get("ACCOUNTBALANCE"))
					: null;

			String acctCurrency = columnNameMap.containsKey("ACCTCURRENCY")
					? rs.getString(columnNameMap.get("ACCTCURRENCY"))
					: null;

			if (accountBalance != null) {
				tagElementWithAttribute(xmlBuilder, "ftc:AccountBalance", accountBalance, "currCode",
						acctCurrency != null ? acctCurrency : "");
			}
			
			//Payment Builder
			xmlPaymentBuilder(rs, xmlBuilder, columnNameMap);

			xmlBuilder.append("</ftc:AccountReport>");
		}

		private void xmlSubstantialOwnerBuilder(ResultSet rs, StringBuilder xmlBuilder,
				Map<String, String> columnNameMap) throws SQLException {
			xmlBuilder.append("<ftc:SubstantialOwner>");
			xmlBuilder.append("<ftc:Individual>");

			if (columnNameMap.containsKey("TIN"))
				tagElementWithAttribute(xmlBuilder, "sfa:TIN", rs.getString(columnNameMap.get("TIN")),
						"issuedBy", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));

			xmlBuilder.append("<sfa:Name>");
			if (columnNameMap.containsKey("CUSTOMERNAME"))
				tagElement(xmlBuilder, "sfa:FirstName", rs.getString(columnNameMap.get("CUSTOMERNAME")));
			if (columnNameMap.containsKey("MIDDLENAME"))
				tagElement(xmlBuilder, "sfa:MiddleName", rs.getString(columnNameMap.get("MIDDLENAME")));
			if (columnNameMap.containsKey("LASTNAME"))
				tagElement(xmlBuilder, "sfa:LastName", rs.getString(columnNameMap.get("LASTNAME")));
			xmlBuilder.append("</sfa:Name>");
			
			xmlBuilder.append("<sfa:Address>");

			// CountryCode
			if (columnNameMap.containsKey("COUNTRYCODE")) {
				tagElement(xmlBuilder, "sfa:CountryCode", rs.getString(columnNameMap.get("COUNTRYCODE")));
			}
			if (columnNameMap.containsKey("ADDRESSFREE")) {
				String street = rs.getString(columnNameMap.get("ADDRESSFREE"));
				tagElement(xmlBuilder, "sfa:AddressFree", street != null ? street : "");
			}
			xmlBuilder.append("</sfa:Address>");
			xmlBuilder.append("</ftc:Individual>");
			xmlBuilder.append("</ftc:SubstantialOwner>");
		}

		private void xmlAccountHolderFooterBuilder(ResultSet rs, StringBuilder xmlBuilder,
				Map<String, String> columnNameMap) throws SQLException {
			xmlBuilder.append("<ftc:AccountHolder>");
			xmlBuilder.append("<ftc:Individual>");
			// ResCountryCode
			if (columnNameMap.containsKey("RESCOUNTRYCODE")) {
				tagElement(xmlBuilder, "sfa:ResCountryCode", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));
			}
			// TIN with attribute "issuedBy"
			String taxIdNumber = columnNameMap.containsKey("TIN")
					? rs.getString(columnNameMap.get("TIN"))
					: null;
			String resCountryCode = columnNameMap.containsKey("RESCOUNTRYCODE")
					? rs.getString(columnNameMap.get("RESCOUNTRYCODE"))
					: null;

			if (taxIdNumber != null) {
				tagElementWithAttribute(xmlBuilder, "sfa:TIN", taxIdNumber, "issuedBy",
						resCountryCode != null ? resCountryCode : "");
			}
			// Name section
			xmlBuilder.append("<sfa:Name>");
			if (columnNameMap.containsKey("CUSTOMERNAME")) {
				tagElement(xmlBuilder, "sfa:FirstName", rs.getString(columnNameMap.get("CUSTOMERNAME")));
			}
			if (columnNameMap.containsKey("MIDDLENAME")) {
				tagElement(xmlBuilder, "sfa:MiddleName", rs.getString(columnNameMap.get("MIDDLENAME")));
			}
			if (columnNameMap.containsKey("LASTNAME")) {
				tagElement(xmlBuilder, "sfa:LastName", rs.getString(columnNameMap.get("LASTNAME")));
			}
			xmlBuilder.append("</sfa:Name>");

			// address

			// address
			xmlBuilder.append("<sfa:Address>");

			// CountryCode
			if (columnNameMap.containsKey("COUNTRYCODE")) {
				tagElement(xmlBuilder, "sfa:CountryCode", rs.getString(columnNameMap.get("COUNTRYCODE")));
			}
//			if (columnNameMap.containsKey("ADDRESSFREE")) {
//				String street = rs.getString(columnNameMap.get("ADDRESSFREE"));
//				tagElement(xmlBuilder, "sfa:AddressFree", street != null ? street : "");
//			}
			xmlBuilder.append("<sfa:AddressFix>");
			if (columnNameMap.containsKey("STREET")) {
				tagElement(xmlBuilder, "sfa:Street", rs.getString(columnNameMap.get("STREET")));
			}
			if (columnNameMap.containsKey("USZIPCODE")) {
				tagElement(xmlBuilder, "sfa:PostCode", rs.getString(columnNameMap.get("USZIPCODE")));
			}
			if (columnNameMap.containsKey("USCITY")) {
				tagElement(xmlBuilder, "sfa:City", rs.getString(columnNameMap.get("USCITY")));
			}
			if (columnNameMap.containsKey("COUNTRYSUBENTITY")) {
				tagElement(xmlBuilder, "sfa:CountrySubentity", rs.getString(columnNameMap.get("COUNTRYSUBENTITY")));
			}
			xmlBuilder.append("</sfa:AddressFix>");
			if (columnNameMap.containsKey("ADDRESSFREE")) {
				tagElement(xmlBuilder, "sfa:AddressFree", rs.getString(columnNameMap.get("ADDRESSFREE")));
			}
			xmlBuilder.append("</sfa:Address>");

			// BirthInfo
			if(columnNameMap.containsKey("DATEOFBIRTH") || columnNameMap.containsKey("BIRTHCOUNTRYCODE")) {
			xmlBuilder.append("<sfa:BirthInfo>");
			if (columnNameMap.containsKey("DATEOFBIRTH")) {
				String birthDate = rs.getString(columnNameMap.get("DATEOFBIRTH"));
				tagElement(xmlBuilder, "sfa:BirthDate", birthDate != null ? birthDate : "");
			}
			if (columnNameMap.containsKey("USCITY")) {
				String city = rs.getString(columnNameMap.get("USCITY"));
				tagElement(xmlBuilder, "sfa:City", city != null ? city : "");
			}

			xmlBuilder.append("<sfa:CountryInfo>");
			if (columnNameMap.containsKey("BIRTHCOUNTRYCODE")) {
				tagElement(xmlBuilder, "sfa:CountryCode", rs.getString(columnNameMap.get("BIRTHCOUNTRYCODE")));
			}
			xmlBuilder.append("</sfa:CountryInfo>");
			xmlBuilder.append("</sfa:BirthInfo>");
			}
			xmlBuilder.append("</ftc:Individual>");
			xmlBuilder.append("</ftc:AccountHolder>");
		}

		private void xmlPaymentBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<ftc:Payment>");
			if (columnNameMap.containsKey("PAYMENTTYPE"))
				tagElement(xmlBuilder, "ftc:Type", columnNameMap.get("PAYMENTTYPE"));

			String fdIntPaid = columnNameMap.containsKey("FDINTPAID") ? rs.getString(columnNameMap.get("FDINTPAID"))
					: null;

			String Currency = columnNameMap.containsKey("ACCTCURRENCY")
					? rs.getString(columnNameMap.get("ACCTCURRENCY"))
					: null;

			if (fdIntPaid != null) {
				tagElementWithAttribute(xmlBuilder, "ftc:PaymentAmnt", fdIntPaid, "currCode",
						Currency != null ? Currency : "");
			}

			xmlBuilder.append("</ftc:Payment>");
		}

		private void xmlAccountHolderBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			// Account Number
			if (columnNameMap.containsKey("ACCOUNTNUMBER"))
				tagElement(xmlBuilder, "ftc:AccountNumber", rs.getString(columnNameMap.get("ACCOUNTNUMBER")));

			// Account Holder
			xmlBuilder.append("<ftc:AccountHolder><ftc:Individual>");

			if (columnNameMap.containsKey("RESCOUNTRYCODE"))
				tagElement(xmlBuilder, "sfa:ResCountryCode", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));

			if (columnNameMap.containsKey("TIN"))
				tagElementWithAttribute(xmlBuilder, "sfa:TIN", rs.getString(columnNameMap.get("TIN")),
						"issuedBy", rs.getString(columnNameMap.get("RESCOUNTRYCODE")));

			xmlBuilder.append("<sfa:Name>");
			if (columnNameMap.containsKey("CUSTOMERNAME"))
				tagElement(xmlBuilder, "sfa:FirstName", rs.getString(columnNameMap.get("CUSTOMERNAME")));
			if (columnNameMap.containsKey("LASTNAME"))
				tagElement(xmlBuilder, "sfa:LastName", rs.getString(columnNameMap.get("LASTNAME")));
			xmlBuilder.append("</sfa:Name>");

			xmlBuilder.append("</ftc:Individual></ftc:AccountHolder>");

			// Birth Info
			xmlBuilder.append("<sfa:BirthInfo>");
			if (columnNameMap.containsKey("DATEOFBIRTH"))
				tagElement(xmlBuilder, "ftc:BirthDate", rs.getString(columnNameMap.get("DATEOFBIRTH")));
			xmlBuilder.append("</sfa:BirthInfo>");

			// Account Balance
			if (columnNameMap.containsKey("ACCOUNTBALANCE"))
				tagElement(xmlBuilder, "sfa:AccountBalance", rs.getString(columnNameMap.get("ACCOUNTBALANCE")));

			xmlBuilder.append("</ftc:AccountReport>");
		}

		private void xmldocSpecBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<ftc:DocSpec>");

			if (columnNameMap.containsKey("DOCTYPEINDIC"))
				tagElement(xmlBuilder, "ftc:DocTypeIndic", rs.getString(columnNameMap.get("DOCTYPEINDIC")));

			if (columnNameMap.containsKey("DOCREFID"))
				tagElement(xmlBuilder, "ftc:DocRefId",
						rs.getString(columnNameMap.get("DOCREFID")) + CommonUtils.generateRandom32());

			xmlBuilder.append("</ftc:DocSpec>");
		}

		private void xmlAddressTagBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<sfa:Address>");

			if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
				tagElement(xmlBuilder, "sfa:CountryCode",
						rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
			}

			xmlBuilder.append("<sfa:AddressFix>");

			if (columnNameMap.containsKey("ENTITYSTREET")) {
				tagElement(xmlBuilder, "sfa:Street", rs.getString(columnNameMap.get("ENTITYSTREET")));
			}

			if (columnNameMap.containsKey("ENTITYPOSTALCODE")) {
				tagElement(xmlBuilder, "sfa:PostCode",
						rs.getString(columnNameMap.get("ENTITYPOSTALCODE")));
			}

			if (columnNameMap.containsKey("ENTITYCITY")) {
				tagElement(xmlBuilder, "sfa:City", rs.getString(columnNameMap.get("ENTITYCITY")));
			}
			if (columnNameMap.containsKey("ENTITYCOUNTRYCODE")) {
				tagElement(xmlBuilder, "sfa:CountrySubentity", rs.getString(columnNameMap.get("ENTITYCOUNTRYCODE")));
			}
			
			xmlBuilder.append("</sfa:AddressFix>");
			if (columnNameMap.containsKey("ENTITYADDRESSFREE")) {
				tagElement(xmlBuilder, "sfa:AddressFree", rs.getString(columnNameMap.get("ENTITYADDRESSFREE")));
			}
			xmlBuilder.append("</sfa:Address>");
		}

		private void xmlMessageSpecBuilder(ResultSet rs, StringBuilder xmlBuilder, Map<String, String> columnNameMap)
				throws SQLException {
			xmlBuilder.append("<ftc:MessageSpec>");
			if (columnNameMap.containsKey("SENDINGCOMPANYIN")) {
				tagElement(xmlBuilder, "sfa:SendingCompanyIN",
						rs.getString(columnNameMap.get("SENDINGCOMPANYIN")));
			}
			if (columnNameMap.containsKey("TRANSMITTINGCOUNTRY")) {
				tagElement(xmlBuilder, "sfa:TransmittingCountry",
						rs.getString(columnNameMap.get("TRANSMITTINGCOUNTRY")));
			}
			if (columnNameMap.containsKey("RECEIVINGCOUNTRY")) {
				tagElement(xmlBuilder, "sfa:ReceivingCountry",
						rs.getString(columnNameMap.get("RECEIVINGCOUNTRY")));
			}
			if (columnNameMap.containsKey("MESSAGETYPE")) {
				tagElement(xmlBuilder, "sfa:MessageType", rs.getString(columnNameMap.get("MESSAGETYPE")));
			}
//			if (columnNameMap.containsKey("WARNING")) {
//				tagElement(xmlBuilder, "sfa:Warning", rs.getString(columnNameMap.get("WARNING")));
//			}
//			if (columnNameMap.containsKey("CONTACT")) {
//				tagElement(xmlBuilder, "sfa:Contact", rs.getString(columnNameMap.get("CONTACT")));
//			}
			if (columnNameMap.containsKey("MESSAGEREFID")) {
				tagElement(xmlBuilder, "sfa:MessageRefId",
						rs.getString(columnNameMap.get("MESSAGEREFID")) + CommonUtils.generateRandom32());
			}
//			if (columnNameMap.containsKey("MESSAGETYPEINDIC")) {
//				tagElement(xmlBuilder, "sfa:MessageTypeIndic",
//						rs.getString(columnNameMap.get("MESSAGETYPEINDIC")));
//			}
			if (columnNameMap.containsKey("REPORTINGPERIOD")) {
				tagElement(xmlBuilder, "sfa:ReportingPeriod",
						rs.getString(columnNameMap.get("REPORTINGPERIOD")));
			}
			if (columnNameMap.containsKey("TIMESTAMP")) {
				tagElement(xmlBuilder, "sfa:Timestamp", rs.getString(columnNameMap.get("TIMESTAMP")));
			}

			xmlBuilder.append("</ftc:MessageSpec>");
		}

		private void buildDefaultXmlBuilder(StringBuilder xmlBuilder) {
			xmlBuilder.append("<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo>")
					.append("<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />")
					.append("<SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\" />")
					.append("<Reference URI=\"#FATCA\">").append("<Transforms>")
					.append("<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\" />")
					.append("</Transforms>")
					.append("<DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\" />")
					.append("<DigestValue>cnuCEsWY4TwQTjv2z6JxZLpVHTKVly6j/CtaE48QfDc=</DigestValue>")
					.append("</Reference>").append("</SignedInfo>")
					.append("<SignatureValue>qZ544/xV/Z5LH36vKRYL3mo+ql7/PKqRlooXZUqUWfYH5aXxgE1zuCR7gVipCwhFYJYFJAFxMbVs1oQlTkWO0AwubpeJxZfK8JEEp5W9rzNrT3dpyGXqJmh1sEysWumqXRKNeF8+6ij99MY0Zzu4sg+UtZkb67WYZwpDEZFRehkd9MoyJS6Tk7gbdu5VVhBx5uRz22O2gQE/Nj1Fxkkz/Zs4C8tVY4E14nLXZjarjprAGecpTAezIOhDDkv2AHVMZkR14vgsJHeTTKbUDfHThbclMH6mTM7HjH0vdXn2zOQd+PJbt0JCaQmFWow+3L7ICgQGsE2nYum8LY2kOK+xPQ==</SignatureValue>")
					.append("<KeyInfo><X509Data><X509SubjectName>CN=fatca.ncbagroup.com, O=NCBA Bank Kenya PLC, L=Nairobi, C=KE</X509SubjectName><X509Certificate>MIIGzjCCBbagAwIBAgIQC/r5QgbUlK3AvK4hy2CDTTANBgkqhkiG9w0BAQsFADBZMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMTMwMQYDVQQDEypEaWdpQ2VydCBHbG9iYWwgRzIgVExTIFJTQSBTSEEyNTYgMjAyMCBDQTEwHhcNMjUwMzI3MDAwMDAwWhcNMjYwMzI3MjM1OTU5WjBbMQswCQYDVQQGEwJLRTEQMA4GA1UEBxMHTmFpcm9iaTEcMBoGA1UEChMTTkNCQSBCYW5rIEtlbnlhIFBMQzEcMBoGA1UEAxMTZmF0Y2EubmNiYWdyb3VwLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANMMKxlAPHLk2ENeilsdM7MopaTFT11wh9AMYDpSBx9KBb2Wuhr79Nq8rH40AQTAXm4TBuskqQxGdyy91G8DJkmfBuY2eTmVNDipWzqCV7j4Pyzw159WIvGdr2oH/3rdb5ItUOHAPCtFGEEbnEh+7KKZqnrrgwF/RlPyjqEazi2RCMoyuIU/Nyw6/PwKRS7wqCIsb1kwdYCRXRHqVarKkcmGbV7zo3FBzOculOB1omXFFCJY5V+yts20Bbc41voBrxCFNANuw10HDDxgTMpTb9Eu1NuJii5aKJRPHxnjEkvKfYLFzblvOzHujad5lKL6eX3ZZJfV4T/R5qaIwkxOLykCAwEAAaOCA44wggOKMB8GA1UdIwQYMBaAFHSFgMBmx9833s+9KTeqAx2+7c0XMB0GA1UdDgQWBBQhojq5sJhRsZSPWxptjuyytzX72zAeBgNVHREEFzAVghNmYXRjYS5uY2JhZ3JvdXAuY29tMD4GA1UdIAQ3MDUwMwYGZ4EMAQICMCkwJwYIKwYBBQUHAgEWG2h0dHA6Ly93d3cuZGlnaWNlcnQuY29tL0NQUzAOBgNVHQ8BAf8EBAMCBaAwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMIGfBgNVHR8EgZcwgZQwSKBGoESGQmh0dHA6Ly9jcmwzLmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydEdsb2JhbEcyVExTUlNBU0hBMjU2MjAyMENBMS0xLmNybDBIoEagRIZCaHR0cDovL2NybDQuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0R2xvYmFsRzJUTFNSU0FTSEEyNTYyMDIwQ0ExLTEuY3JsMIGHBggrBgEFBQcBAQR7MHkwJAYIKwYBBQUHMAGGGGh0dHA6Ly9vY3NwLmRpZ2ljZXJ0LmNvbTBRBggrBgEFBQcwAoZFaHR0cDovL2NhY2VydHMuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0R2xvYmFsRzJUTFNSU0FTSEEyNTYyMDIwQ0ExLTEuY3J0MAwGA1UdEwEB/wQCMAAwggF9BgorBgEEAdZ5AgQCBIIBbQSCAWkBZwB3AA5XlLzzrqk+MxssmQez95Dfm8I9cTIl3SGpJaxhxU4hAAABldbe0DwAAAQDAEgwRgIhAMQsh5ews5NzU4PWWs0Pg19f4dfgXUg/zaVjBGknE9emAiEAkajVsIYjEdnB0cQVFM2Vb3JYVWhFD3evPxcr56jQdaMAdQDLOPcViXyEoURfW8Hd+8lu8ppZzUcKaQWFsMsUwxRY5wAAAZXW3tBIAAAEAwBGMEQCIEoUni1NXR8jxTEtYDwgYQpNAeyVgz9E9uN78996MUijAiBiBRrWp+sXqdAMuxS63n3bH/gxs9aRVTpT2lT/RoQHDwB1AJaXZL9VWJet90OHaDcIQnfp8DrV9qTzNm5GpD8PyqnGAAABldbe0I0AAAQDAEYwRAIgJL0jxygIYHye43xqcqrAp+wVjVSXWyA9pwd+NlEM2xICIFQqe2GE2cYBM8fhNSWoEVb575Si7E4AMHxnKvrKodVXMA0GCSqGSIb3DQEBCwUAA4IBAQAdtEa1vhsRe+4NZNbkrpW7iChK1q5Dw3UHrcdin+z1jTObepVMbjCgxECW+SdRYa1+LatSQb4uUaLAR1gb25Y5ZqHJ8sWLz5EG8b7Hdhjh1Ml10bX6Sdsz0yt1s0UKlib1+IWICDLBywSaInbJVET0yZwWTLwpAR76Oc6j6uzIe3GNuxUAVo7KV+b6Zg8WreYjstOWl+naeMyS2/FiwptOy0SnHDzIHCaYWYsLJRrAx9gbCVZiYSIePqXiGDGei0lj22ztFReIpVf4/2zKT+Pc3F8+ZJJlp2L9cmDEeGm7XTw0s2mB/Hfa4a3jzGGmIcqBNgPASzerookGnRkmpz+P</X509Certificate></X509Data></KeyInfo>")
					.append("<Object Id=\"FATCA\">").append("<ftc:FATCA_OECD ")
					.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:oecd:ties:fatca:v2\" ")
					.append("xmlns:iso=\"urn:oecd:ties:isofatcatypes:v1\" ")
					.append("xmlns:ftc=\"urn:oecd:ties:fatca:v2\" ").append("xmlns:stf=\"urn:oecd:ties:stf:v4\" ")
					.append("xmlns:sfa=\"urn:oecd:ties:stffatcatypes:v2\" version=\"2.0\">");
		}	
		
}
