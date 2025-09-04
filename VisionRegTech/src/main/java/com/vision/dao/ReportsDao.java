package com.vision.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Component;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.ChartUtils;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.Paginationhelper;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.PrdQueryConfig;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.RCReportFieldsVb;
import com.vision.vb.ReportFilterVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.VisionUsersVb;

@Component
public class ReportsDao extends AbstractDao<ReportsVb> {
	@Value("${app.productName}")
	private String productName;
	@Value("${app.client}")
	private String client;
	@Autowired
	CommonDao commonDao;
	@Autowired
	ChartUtils chartUtils;
	@Autowired
	CommonApiDao commonApiDao;

	public List<ReportsVb> getReportList(String reportGroup) throws DataAccessException {
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		String sql = "";
		try {
			if ("OLD".equalsIgnoreCase(client)) {
				sql = " Select T2.Report_ID,T2.REPORT_TITLE,T2.FILTER_FLAG,T2.FILTER_REF_CODE,T2.APPLY_USER_RESTRCT, "
						+ " (SELECT NVL(MIN(SUBREPORT_SEQ),0) FROM PRD_REPORT_DETAILS WHERE REPORT_ID = T2.REPORT_ID) NEXT_LEVEL,T2.GROUPING_FLAG, "
						+ " T2.REPORT_ORDER, T2.Report_Type_AT, T2.Report_Type, T2.Template_ID,"
						+ " (SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE = 'PRD_REPORT_MAXROW') MAX_PERPAGE,T2.SCALING_FACTOR, "
						+ "	(SELECT NVL(REPORT_ORIENTATION,'P') FROM PRD_REPORT_DETAILS PRD WHERE PRD.REPORT_ID= T2.REPORT_ID AND "
						+ " SUBREPORT_SEQ = (SELECT MIN(SUBREPORT_SEQ) FROM PRD_REPORT_DETAILS PR WHERE PR.REPORT_ID= T2.REPORT_ID) "
						+ ") REPORT_ORIENTATION " + " FROM PRD_REPORT_ACCESS T1,PRD_REPORT_MASTER T2 "
						+ " WHERE t1.REPORT_ID  = t2.REPORT_ID AND T1.PRODUCT_NAME = T2.APPLICATION_ID AND T1.USER_GROUP = '"
						+ visionUsersVb.getUserGroup() + "' AND T1.USER_PROFILE='" + visionUsersVb.getUserProfile()
						+ "' " + " AND T2.APPLICATION_ID = '" + productName
						+ "' AND T2.REPORT_GROUP = ? AND T2.STATUS = 0";
			} else if ("NEW".equalsIgnoreCase(client)) {
				String[] userGrpPfl = visionUsersVb.getUserGrpProfile().split("-");
				sql = " Select T2.Report_ID,T2.REPORT_TITLE,T2.FILTER_FLAG,T2.FILTER_REF_CODE,T2.APPLY_USER_RESTRCT, "
						+ " (SELECT NVL(MIN(SUBREPORT_SEQ),0) FROM PRD_REPORT_DETAILS WHERE REPORT_ID = T2.REPORT_ID) NEXT_LEVEL,T2.GROUPING_FLAG, "
						+ " T2.REPORT_ORDER, T2.Report_Type_AT, T2.Report_Type, T2.Template_ID,"
						+ " (SELECT VALUE FROM VISION_VARIABLES WHERE VARIABLE = 'PRD_REPORT_MAXROW') MAX_PERPAGE,T2.SCALING_FACTOR, "
						+ "	(SELECT NVL(REPORT_ORIENTATION,'P') FROM PRD_REPORT_DETAILS PRD WHERE PRD.REPORT_ID= T2.REPORT_ID AND "
						+ " SUBREPORT_SEQ = (SELECT MIN(SUBREPORT_SEQ) FROM PRD_REPORT_DETAILS PR WHERE PR.REPORT_ID= T2.REPORT_ID) "
						+ ") REPORT_ORIENTATION " + " FROM PRD_REPORT_ACCESS T1,PRD_REPORT_MASTER T2 "
						+ " WHERE t1.REPORT_ID  = t2.REPORT_ID AND T1.PRODUCT_NAME = T2.APPLICATION_ID AND T1.USER_GROUP = '"
						+ userGrpPfl[0] + "' AND T1.USER_PROFILE='" + userGrpPfl[1] + "' "
						+ " AND T2.APPLICATION_ID = '" + productName + "' AND T2.REPORT_GROUP = ? AND T2.STATUS = 0";
			}

			String orderBy = " ORDER BY REPORT_ORDER";
			sql = sql + orderBy;
			Object[] lParams = new Object[1];
			lParams[0] = reportGroup;
			return getJdbcTemplate().query(sql, lParams, getReportListMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting Report List...!!");
			return null;
		}

	}

	protected RowMapper getReportListMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReportsVb vObject = new ReportsVb();
				vObject.setReportId(rs.getString("REPORT_ID"));
				vObject.setReportTitle(rs.getString("REPORT_TITLE"));
				vObject.setFilterFlag(rs.getString("FILTER_FLAG"));
				vObject.setFilterRefCode(rs.getString("FILTER_REF_CODE"));
				vObject.setApplyUserRestrct(rs.getString("APPLY_USER_RESTRCT"));
				vObject.setCurrentLevel(rs.getString("NEXT_LEVEL").replaceAll(".0", ""));
				vObject.setNextLevel(rs.getString("NEXT_LEVEL").replaceAll(".0", ""));
				vObject.setGroupingFlag(rs.getString("GROUPING_FLAG"));
				vObject.setMaxRecords(rs.getInt("MAX_PERPAGE"));
				vObject.setReportTypeAT(rs.getInt("Report_Type_AT"));
				vObject.setReportType(rs.getString("Report_Type"));
				vObject.setTemplateId(rs.getString("Template_ID"));
				vObject.setScalingFactor(rs.getString("SCALING_FACTOR"));
				vObject.setReportOrientation(rs.getString("REPORT_ORIENTATION"));
				return vObject;
			}
		};
		return mapper;
	}

	public List<ReportFilterVb> getReportFilterDetail(String filterRefCode) {
		setServiceDefaults();
		List<ReportFilterVb> collTemp = null;
		try {
			String query = " SELECT FILTER_REF_CODE,FILTER_XML,USER_RESTRICTION_XML,USER_RESTRICT_FLAG FROM PRD_REPORT_FILTERS WHERE STATUS = 0 AND FILTER_REF_CODE = '"
					+ filterRefCode + "' ";
			collTemp = getJdbcTemplate().query(query, getReportFilterMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting Dashboard Detail...!!");
			return null;
		}
	}

	private RowMapper getReportFilterMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReportFilterVb vObject = new ReportFilterVb();
				vObject.setFilterRefCode(rs.getString("FILTER_REF_CODE"));
				vObject.setFilterRefXml(rs.getString("FILTER_XML"));
				vObject.setUserRestrictionXml(rs.getString("USER_RESTRICTION_XML"));
				vObject.setUserRestrictFlag(rs.getString("USER_RESTRICT_FLAG"));
				return vObject;
			}
		};
		return mapper;
	}

	public List<ReportsVb> findReportCategory(int pAlphaTab) throws DataAccessException {
		String sql = "SELECT ALPHA_SUB_TAB,ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE ALPHA_SUBTAB_STATUS = 0 AND ALPHA_TAB = ?  AND ALPHA_SUB_TAB !='Z' ORDER BY ALPHA_SUB_TAB";
		Object[] lParams = new Object[1];
		lParams[0] = pAlphaTab;
		return getJdbcTemplate().query(sql, lParams, getCategoryMapper());
	}

	private RowMapper getCategoryMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReportsVb vObject = new ReportsVb();
				vObject.setReportCategory(rs.getString("ALPHA_SUB_TAB"));
				vObject.setCategoryDesc(rs.getString("ALPHA_SUBTAB_DESCRIPTION"));
				return vObject;
			}
		};
		return mapper;
	}

	public ExceptionCode extractReportData(ReportsVb vObj,Connection conExt,Boolean exportFlag) {
		ArrayList datalst = new ArrayList();
		ExceptionCode exceptionCode = new ExceptionCode();
		int totalRows = 0;
		Statement stmt1 = null;
		String resultFetchTable = "";
		String sqlQuery = "";
		Paginationhelper<ReportsVb> paginationhelper = new Paginationhelper<ReportsVb>();
		String tmpTableOrg = String.valueOf("TMP"+System.currentTimeMillis())+vObj.getSubReportId();
		String tmpTableGrp = String.valueOf("TMPG"+System.currentTimeMillis())+vObj.getSubReportId();
		String finalTmpTable = "";
		Boolean tempTableAlreadyAvail = false;
		if(ValidationUtil.isValid(vObj.getActionType())){
			tempTableAlreadyAvail = true;
		}
								
		try {
			stmt1 = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
					    ResultSet.CONCUR_READ_ONLY);
			if(!tempTableAlreadyAvail) {
				
				sqlQuery = vObj.getFinalExeQuery();
				//totalRows = (int) vObj.getTotalRows();
				String createTabScript= "";
				createTabScript = "SELECT * FROM ("+sqlQuery+") TA1";
				
				Boolean formatTypeAvail = false;
				ResultSet rsFormatCnt = null;
				if("ORACLE".equalsIgnoreCase(databaseType)) {
					totalRows = stmt1.executeUpdate("CREATE TABLE "+tmpTableOrg+" AS ("+createTabScript+")");
					
					rsFormatCnt = stmt1.executeQuery("SELECT * FROM USER_TAB_COLS WHERE "
							+ "TABLE_NAME = '"+tmpTableOrg+"' AND COLUMN_NAME = 'FORMAT_TYPE' ");
					
					while(rsFormatCnt.next()) {
						formatTypeAvail = true;
					}
				}else if("MSSQL".equalsIgnoreCase(databaseType)) {
					totalRows = stmt1.executeUpdate("Select * into "+tmpTableOrg+" FROM ("+createTabScript+") A");
					
					rsFormatCnt = stmt1.executeQuery("SELECT * FROM INFORMATION_SCHEMA.columns WHERE "
							+ "TABLE_NAME = '"+tmpTableOrg+"' AND COLUMN_NAME = 'FORMAT_TYPE' ");
					
					while(rsFormatCnt.next()) {
						formatTypeAvail = true;
					}
				}
				rsFormatCnt.close();
				
				resultFetchTable = tmpTableOrg;
				String formatTypeCond = "";
				if(formatTypeAvail && "Y".equalsIgnoreCase(vObj.getApplyGrouping()))
					formatTypeCond = "WHERE FORMAT_TYPE NOT IN ('S','FT') ";
				else
					formatTypeCond= "";
				if ("Y".equalsIgnoreCase(vObj.getApplyGrouping())) {
					String showMeasures = "";
					if(ValidationUtil.isValid(vObj.getShowMeasures())) {
						showMeasures = ","+vObj.getShowMeasures();
					}else
						showMeasures = "";
						
					String query = "SELECT " + vObj.getShowDimensions() + showMeasures + " FROM "+tmpTableOrg+ " "+formatTypeCond+ " GROUP BY " + vObj.getShowDimensions();
					if("ORACLE".equalsIgnoreCase(databaseType)) {
						totalRows = stmt1.executeUpdate("CREATE TABLE "+tmpTableGrp+" AS ("+query+")");
					}else if("MSSQL".equalsIgnoreCase(databaseType)) {
						totalRows = stmt1.executeUpdate("Select * into "+tmpTableGrp+" FROM ("+query+") A");
					}
					resultFetchTable = tmpTableGrp;
				}
				if(totalRows == 0) {
					exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
					exceptionCode.setErrorMsg("No Records Found!!");
					return exceptionCode;
				}
				/*Report Suite having the functionality of Pagination for every 5000 rows
				(Rows per page in parameterized in Vision_Variable PRD_REPORT_MAXROW/PRD_REPORT_MAX_PERPAGE */
				if(exportFlag) {
					finalTmpTable = String.valueOf("TMPF"+System.currentTimeMillis())+vObj.getSubReportId(); 
					String sqlTempTable = ValidationUtil.convertQuery("SELECT * FROM "+resultFetchTable,vObj.getSortField());
					if("ORACLE".equalsIgnoreCase(databaseType)) {
						totalRows = stmt1.executeUpdate("CREATE TABLE "+finalTmpTable+" AS ("+sqlTempTable+")");
						sqlTempTable = finalTmpTable;
					}else if("MSSQL".equalsIgnoreCase(databaseType)) {
						totalRows = stmt1.executeUpdate("Select * into "+finalTmpTable+" FROM ("+sqlTempTable+") A");
						sqlTempTable = "Select * from "+finalTmpTable+" ";
					}
					sqlQuery = paginationhelper.reportFetchPage(sqlTempTable,vObj.getCurrentPage(), vObj.getMaxRecords(),totalRows);
					vObj.setActionType(finalTmpTable);
					vObj.setTotalRows(totalRows);
				}else {
					String sqlTempTable = "SELECT * FROM "+resultFetchTable+"";
					sqlQuery = paginationhelper.reportFetchPage(ValidationUtil.convertQuery(sqlTempTable, vObj.getSortField()),vObj.getCurrentPage(), vObj.getMaxRecords(),totalRows);
				}
				
			}else {
				if(exportFlag) {
					finalTmpTable = vObj.getActionType();
					String sqlTempTable = "Select * from "+finalTmpTable+" ";
					sqlQuery = paginationhelper.reportFetchPage(sqlTempTable,vObj.getCurrentPage(), vObj.getMaxRecords(),(int)vObj.getTotalRows());
					vObj.setActionType(finalTmpTable);
					vObj.setTotalRows(vObj.getTotalRows());
					float noOfPages = (float)vObj.getTotalRows()/(float)vObj.getMaxRecPerPage();
					int MaxPageNo = Math.round(noOfPages);
					if(vObj.getCurrentPage() > MaxPageNo)
						exportFlag = false;
				}
			}
			
			logger.info("Final Result Data Extraction start Report Id["+vObj.getReportId()+"]SubReport["+vObj.getSubReportId()+"]");
			ResultSet rsData = stmt1.executeQuery(sqlQuery);// Final Result Data Query Execution
			logger.info("Final Result Data Extraction End Report Id["+vObj.getReportId()+"]SubReport["+vObj.getSubReportId()+"]");
			
			ResultSetMetaData metaData = rsData.getMetaData();
			int colCount = metaData.getColumnCount();
			HashMap<String,String> columns = new HashMap<String,String>();
			ArrayList<String> collst =new ArrayList<>();
			ArrayList<ColumnHeadersVb> colHeadersDetLst = new ArrayList<>();
			String reportUnusedColumns = commonDao.findVisionVariableValue("PRD_REPORT_UNUSED_COLS");
			String reportcolTypes = commonDao.findVisionVariableValue("PRD_REPORT_COL_TYPES");
			Boolean columnHeaderFetch = false;
			int rowNum = 1;
			int ctr = 0;
			while(rsData.next()){
				columns = new HashMap<String,String>();
				for(int cn = 1;cn <= colCount;cn++) {
					ColumnHeadersVb colHeader = new ColumnHeadersVb();
					String columnName = metaData.getColumnName(cn);
					String colType = metaData.getColumnTypeName(cn);
					ctr++;
					if("DD_KEY_ID".equalsIgnoreCase(columnName.toUpperCase())) {
						columns.put("DDKEYID", rsData.getString(columnName));
					}else{
						columns.put(columnName.toUpperCase(), rsData.getString(columnName));
					}
					if(!columnHeaderFetch) {
						collst.add(columnName.toUpperCase());
						if(vObj.getColumnHeaderslst() == null || vObj.getColumnHeaderslst().isEmpty()) {
							if (ValidationUtil.isValid(reportUnusedColumns) 
									&& reportUnusedColumns.toUpperCase().contains(columnName.toUpperCase())) {
								ctr = ctr - 1;
								continue;
							}
							colHeader.setLabelColNum(ctr);
							colHeader.setLabelRowNum(1);
							//colHeader.setCaption(WordUtils.capitalizeFully(columnName.replaceAll("_", " ")));
							colHeader.setCaption(columnName);
							colHeader.setDbColumnName(columnName.toUpperCase());
							if(ValidationUtil.isValid(reportcolTypes) && reportcolTypes.toUpperCase().contains(colType.toUpperCase()))
								colHeader.setColType("T");
							else
								colHeader.setColType("N");
							colHeader.setRowspan(0);
							colHeader.setColspan(0);
							colHeader.setDrillDownLabel(false);
							colHeadersDetLst.add(colHeader);
						}
						
					}
				}
				columnHeaderFetch = true;
				if(!columnHeaderFetch) {
					StringJoiner missingColumns = new StringJoiner(",");
					vObj.getColumnHeaderslst().forEach(colHeadersDataVb -> {
						if(!collst.contains(colHeadersDataVb.getDbColumnName())) {
							missingColumns.add(colHeadersDataVb.getDbColumnName());
						}
					});
					if(ValidationUtil.isValid(missingColumns.toString())) {
						exceptionCode.setErrorMsg(missingColumns+"these Source columns are not maintained  in the Result set");
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						break;
					}
				}
				columns.put("INDEXING",""+rowNum);//This is only for front-end scroll purpose not used anywhere in report.
				datalst.add(columns);
				if(rsData.getRow() == vObj.getMaxRecords())
					break;
				
				columnHeaderFetch = true;
				rowNum++;
			}
			rsData.close();
			//HashMap<String,String> columns = (HashMap<String,String>)getJdbcTemplate().query(sqlQuery, mapper);
			if(totalRows != 0) {
				vObj.setTotalRows(totalRows);
				exceptionCode.setRequest((int)totalRows);
			}else {
				vObj.setTotalRows(vObj.getTotalRows());
				exceptionCode.setRequest((int)vObj.getTotalRows());
			}
				
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setResponse(datalst);
			exceptionCode.setOtherInfo(vObj);
			if(colHeadersDetLst !=null && colHeadersDetLst.size() > 0)
				exceptionCode.setResponse1(colHeadersDetLst);
		}catch(Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			if(!ValidationUtil.isValid(exceptionCode.getErrorMsg())) {
				exceptionCode.setErrorMsg(e.getMessage());
			}
			return exceptionCode;
		}finally {
			if(!exportFlag) {
				try {
					if("ORACLE".equalsIgnoreCase(databaseType)) {
						if(ValidationUtil.isValid(resultFetchTable))
							stmt1.executeUpdate("DROP TABLE "+resultFetchTable+" PURGE ");
						if(ValidationUtil.isValid(finalTmpTable))
							stmt1.executeUpdate("DROP TABLE "+finalTmpTable+" PURGE ");
						vObj.setActionType("");
					}else if("MSSQL".equalsIgnoreCase(databaseType)) {
						if(ValidationUtil.isValid(resultFetchTable))
							stmt1.executeUpdate("DROP TABLE "+resultFetchTable);
						if(ValidationUtil.isValid(finalTmpTable))
							stmt1.executeUpdate("DROP TABLE "+finalTmpTable);
						vObj.setActionType("");
					}
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return exceptionCode;
	}

	public List<ReportsVb> getSubReportDetail(ReportsVb dObj) {
		List<ReportsVb> collTemp = null;
		try {
			/*
			 * String query =
			 * "SELECT REPORT_ID,SUBREPORT_SEQ,SUB_REPORT_ID,DATA_REF_ID,COUNT_FETCH_FLAG,DD_FLAG, "
			 * + "REPORT_ORIENTATION,PDF_GROUP_COLUMNS,PARENT_SUBREPORT_ID,"+commonDao.
			 * getDbFunction("NVL")
			 * +"(PDF_WIDTH_GRPERCENT,0) PDF_WIDTH_GRPERCENT FROM PRD_REPORT_DETAILS WHERE SUBREPORT_SEQ = ? AND REPORT_ID = ? "
			 * + "AND STATUS = 0";
			 */

			String query = "SELECT REPORT_ID,SUBREPORT_SEQ,SUB_REPORT_ID,DATA_REF_ID,COUNT_FETCH_FLAG,DD_FLAG, "
					+ "REPORT_ORIENTATION,PDF_GROUP_COLUMNS,PARENT_SUBREPORT_ID,SORTING_ENABLE,SEARCH_ENABLE,NVL(PDF_WIDTH_GRPERCENT,0) PDF_WIDTH_GRPERCENT FROM PRD_REPORT_DETAILS WHERE SUBREPORT_SEQ = ? AND REPORT_ID = ? "
					+ "AND STATUS = 0";

			Object[] lParams = new Object[2];
			lParams[0] = dObj.getNextLevel();
			lParams[1] = dObj.getReportId();
			collTemp = getJdbcTemplate().query(query, lParams, getSubReportsMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting sub Report Details...!!" + ex.getMessage());
			return null;
		}
	}

	private RowMapper getSubReportsMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReportsVb reportsVb = new ReportsVb();
				reportsVb.setReportId(rs.getString("REPORT_ID"));
				reportsVb.setIntReportSeq(rs.getInt("SUBREPORT_SEQ"));
				reportsVb.setCurrentLevel(rs.getString("SUBREPORT_SEQ").replaceAll(".0", ""));
				reportsVb.setSubReportId(rs.getString("SUB_REPORT_ID"));
				reportsVb.setDataRefId(rs.getString("DATA_REF_ID"));
				reportsVb.setFetchFlag(rs.getString("COUNT_FETCH_FLAG"));
				reportsVb.setDdFlag(rs.getString("DD_FLAG"));
				reportsVb.setReportOrientation(
						ValidationUtil.isValid(rs.getString("REPORT_ORIENTATION")) ? rs.getString("REPORT_ORIENTATION")
								: "P");
				reportsVb.setPdfGroupColumn(rs.getString("PDF_GROUP_COLUMNS"));
				reportsVb.setParentSubReportID(rs.getString("PARENT_SUBREPORT_ID"));
				reportsVb.setSortFlag(rs.getString("SORTING_ENABLE"));
				reportsVb.setSearchFlag(rs.getString("SEARCH_ENABLE"));
				reportsVb.setPdfGrwthPercent(rs.getInt("PDF_WIDTH_GRPERCENT"));
				return reportsVb;
			}
		};
		return mapper;
	}

	public String getNextLevel(ReportsVb dObj) {
		List<ReportsVb> collTemp = null;
		String nextLevel = "";
		String query = "";
		try {
			query = " SELECT NVL(MIN(SUBREPORT_SEQ),0) FROM PRD_REPORT_DETAILS WHERE SUBREPORT_SEQ > ? "
					+ " AND REPORT_ID = ?  ";
			Object[] lParams = new Object[2];
			lParams[0] = dObj.getCurrentLevel();
			lParams[1] = dObj.getReportId();
			nextLevel = getJdbcTemplate().queryForObject(query, lParams, String.class);
			return nextLevel;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting Next Level Sequence...!!");
			return "0";
		}
	}

	public List<ColumnHeadersVb> getReportColumns(ReportsVb dObj) {
		List<ColumnHeadersVb> collTemp = null;
		try {
			String query = " Select REPORT_ID,SUB_REPORT_ID,COLUMN_XML from PRD_REPORT_COLUMN "
					+ " WHERE REPORT_ID = ? AND SUB_REPORT_ID = ?";

			Object[] lParams = new Object[2];
			lParams[0] = dObj.getReportId();
			lParams[1] = dObj.getSubReportId();
			collTemp = getJdbcTemplate().query(query, lParams, getReportColumnHeadersMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting Report Columns for the sub Report Id[" + dObj.getSubReportId() + "]");
			return null;
		}
	}

	private RowMapper getReportColumnHeadersMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ColumnHeadersVb columnHeadersVb = new ColumnHeadersVb();
				columnHeadersVb.setReportId(rs.getString("REPORT_ID"));
				columnHeadersVb.setSubReportId(rs.getString("SUB_REPORT_ID"));
				columnHeadersVb.setColumnXml(rs.getString("COLUMN_XML"));
				return columnHeadersVb;
			}
		};
		return mapper;
	}

	public List<PrdQueryConfig> getSqlQuery(String dataRefId) {
		List<PrdQueryConfig> collTemp = null;
		try {
			String query = "SELECT DATA_REF_ID, QUERY, DATA_REF_TYPE from PRD_QUERY_CONFIG "
					+ "WHERE DATA_REF_ID = ? AND STATUS = 0 AND APPLICATION_ID = '" + productName + "' ";

			Object[] lParams = new Object[1];
			lParams[0] = dataRefId;
			collTemp = getJdbcTemplate().query(query, lParams, getSqlQueryMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting the Query for the Data Ref Id[" + dataRefId + "]");
			return null;
		}
	}

	private RowMapper getSqlQueryMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
				prdQueryConfig.setDataRefId(rs.getString("DATA_REF_ID"));
				prdQueryConfig.setQueryProc(rs.getString("QUERY"));
				prdQueryConfig.setDataRefType(rs.getString("DATA_REF_TYPE"));
				return prdQueryConfig;
			}
		};
		return mapper;
	}

	public int insertReportsAudit(ReportsVb dObj, String auditMsg) {
		VisionUsersVb visionUsersVb = SessionContextHolder.getContext();
		String referenceId = getReferenceNumforAudit();
		String promptXml = convertToXml(dObj);
		if (!ValidationUtil.isValid(promptXml))
			promptXml = "";
		int retVal = 0;
		String query = "";
		try {
			query = "Insert Into PRD_AUDIT_REPORTS (REFERENCE_NO,REPORT_ID, PROMPT_XML,"
					+ "RUN_DATE ,AUDIT_MESSAGE,USER_LOGIN_ID,VISION_ID,IP_ADDRESS,	MAC_ADDRESS,HOST_NAME ) "
					+ "Values (?,?,?,sysdate,?,?,?,?,?,?)";
			Object[] args = { referenceId, dObj.getReportId(), promptXml, auditMsg, visionUsersVb.getUserLoginId(),
					visionUsersVb.getVisionId(), visionUsersVb.getIpAddress(), visionUsersVb.getMacAddress(),
					visionUsersVb.getRemoteHostName() };
			retVal = getJdbcTemplate().update(query, args);
			return retVal;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while inserting into the PRD_AUDIT_REPORTS...!!");
			return 0;
		}
	}

	public static String getReferenceNumforAudit() {
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
			e.printStackTrace();
			return "";
		}
	}

	public ExceptionCode callProcforReportData(ReportsVb vObject, String procedure) {
		setServiceDefaults();
		strCurrentOperation = "Query";
		strErrorDesc = "";
		Connection con = null;
		CallableStatement cs = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		PromptTreeVb promptTreeVb = new PromptTreeVb();
		try {
			if (!ValidationUtil.isValid(procedure)) {
				strErrorDesc = "Invalid Procedure in PRD_QUERY_CONFIG table for report Id[" + vObject.getReportId()
						+ "].";
				throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
			}
			String sessionId = String.valueOf(System.currentTimeMillis());
			con = getConnection();
			cs = con.prepareCall(
					"{call " + procedure + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?,?)}");
			cs.setString(1, String.valueOf(vObject.getReportId()));
			cs.setString(2, String.valueOf(vObject.getSubReportId()));
			cs.setString(3, String.valueOf(intCurrentUserId));
			cs.setString(4, sessionId);
			cs.setString(5, vObject.getScalingFactor());
			cs.setString(6, vObject.getPromptValue1().replaceAll("'", ""));
			cs.setString(7, vObject.getPromptValue2().replaceAll("'", ""));
			cs.setString(8, vObject.getPromptValue3().replaceAll("'", ""));
			cs.setString(9, vObject.getPromptValue4().replaceAll("'", ""));
			cs.setString(10, vObject.getPromptValue5().replaceAll("'", ""));
			cs.setString(11, vObject.getPromptValue6().replaceAll("'", ""));
			cs.setString(12, vObject.getPromptValue7().replaceAll("'", ""));
			cs.setString(13, vObject.getPromptValue8().replaceAll("'", ""));
			cs.setString(14, vObject.getPromptValue9().replaceAll("'", ""));
			cs.setString(15, vObject.getPromptValue10().replaceAll("'", ""));
			cs.setString(16, vObject.getDrillDownKey1().replaceAll("'", ""));
			cs.setString(17, vObject.getDrillDownKey2().replaceAll("'", ""));
			cs.setString(18, vObject.getDrillDownKey3().replaceAll("'", ""));
			cs.setString(19, vObject.getDrillDownKey4().replaceAll("'", ""));
			cs.setString(20, vObject.getDrillDownKey5().replaceAll("'", ""));
			cs.setString(21, vObject.getDrillDownKey6().replaceAll("'", ""));
			cs.setString(22, vObject.getDrillDownKey7().replaceAll("'", ""));
			cs.setString(23, vObject.getDrillDownKey8().replaceAll("'", ""));
			cs.setString(24, vObject.getDrillDownKey9().replaceAll("'", ""));
			cs.setString(25, vObject.getDrillDownKey10().replaceAll("'", ""));
			cs.registerOutParameter(26, java.sql.Types.VARCHAR); // Status
			cs.registerOutParameter(27, java.sql.Types.VARCHAR); // Error Message
			cs.registerOutParameter(28, java.sql.Types.VARCHAR); // Table Name
			cs.registerOutParameter(29, java.sql.Types.VARCHAR); // Column Headers
			cs.execute();
			exceptionCode.setErrorCode(cs.getInt(26));
			exceptionCode.setErrorMsg(cs.getString(27));
			promptTreeVb.setTableName(cs.getString(28));
			promptTreeVb.setColumnHeaderTable(cs.getString(29));
			promptTreeVb.setSessionId(sessionId);
			promptTreeVb.setReportId(vObject.getSubReportId());
			exceptionCode.setResponse(promptTreeVb);
			cs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			exceptionCode.setErrorMsg(ex.getMessage());
			exceptionCode.setErrorCode(Constants.PASSIVATE);
			return exceptionCode;
		} finally {
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
		return exceptionCode;
	}

	public void deleteTempTable(String tableName) {
		try {
			String query = " DROP TABLE  " + tableName + " PURGE ";
			getJdbcTemplate().update(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while dropping the temp table...!!");
		}
	}

	private String convertToXml(ReportsVb vObject) {
		String promptsXml = "";
		promptsXml = "<Prompts>";
		String promptvalue1 = "<Prompt1>" + vObject.getPromptValue1() + "</Prompt1>";
		promptsXml = promptsXml + promptvalue1;
		String promptvalue2 = "<Prompt2>" + vObject.getPromptValue2() + "</Prompt2>";
		promptsXml = promptsXml + promptvalue2;
		String promptvalue3 = "<Prompt3>" + vObject.getPromptValue3() + "</Prompt3>";
		promptsXml = promptsXml + promptvalue3;
		String promptvalue4 = "<Prompt4>" + vObject.getPromptValue4() + "</Prompt4>";
		promptsXml = promptsXml + promptvalue4;
		String promptvalue5 = "<Prompt5>" + vObject.getPromptValue5() + "</Prompt5>";
		promptsXml = promptsXml + promptvalue5;
		String promptvalue6 = "<Prompt6>" + vObject.getPromptValue6() + "</Prompt6>";
		promptsXml = promptsXml + promptvalue6;
		String promptvalue7 = "<Prompt7>" + vObject.getPromptValue7() + "</Prompt7>";
		promptsXml = promptsXml + promptvalue7;
		String promptvalue8 = "<Prompt8>" + vObject.getPromptValue8() + "</Prompt8>";
		promptsXml = promptsXml + promptvalue8;
		String promptvalue9 = "<Prompt9>" + vObject.getPromptValue9() + "</Prompt9>";
		promptsXml = promptsXml + promptvalue9;
		String promptvalue10 = "<Prompt10>" + vObject.getPromptValue10() + "</Prompt10>";
		promptsXml = promptsXml + promptvalue10;
		promptsXml = promptsXml + "</Prompts>";
		return promptsXml;
	}

	public List<ReportsVb> getReportsDetail(ReportsVb dObj) {
		List<ReportsVb> collTemp = null;
		try {
			String query = " SELECT * FROM (SELECT REPORT_ID,SUBREPORT_SEQ,SUB_REPORT_ID,DATA_REF_ID,COUNT_FETCH_FLAG,DD_FLAG,"
					+ " REPORT_ORIENTATION, PARENT_SUBREPORT_ID, OBJECT_TYPE_AT, OBJECT_TYPE,"
					+ " (SELECT ALPHA_SUBTAB_DESCRIPTION FROM ALPHA_SUB_TAB WHERE "
					+ " ALPHA_TAB = 7005 AND ALPHA_SUB_TAB = CHART_TYPE) CHART_TYPE, CHART_TYPE_AT, "
					+ " CASE WHEN SUB_REPORT_ID != PARENT_SUBREPORT_ID THEN "
					+ " (SELECT DD_FLAG FROM PRD_REPORT_DETAILS S2 WHERE T1.PARENT_SUBREPORT_ID = S2.SUB_REPORT_ID) "
					+ " ELSE 'N' END ISDRILLDOWN "
					+ " FROM PRD_REPORT_DETAILS T1 WHERE REPORT_ID = ? ) A1 WHERE A1.ISDRILLDOWN = 'N' "
					+ " ORDER BY A1.SUB_REPORT_ID";
			Object[] lParams = new Object[1];
			lParams[0] = dObj.getReportId();
			collTemp = getJdbcTemplate().query(query, lParams, getInteractiveReportsMapper());
			return collTemp;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting interactive report detail...!!");
			return null;
		}
	}

	private RowMapper getInteractiveReportsMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ReportsVb reportsVb = new ReportsVb();
				reportsVb.setReportId(rs.getString("REPORT_ID"));
				reportsVb.setIntReportSeq(rs.getInt("SUBREPORT_SEQ"));
				reportsVb.setSubReportId(rs.getString("SUB_REPORT_ID"));
				reportsVb.setDataRefId(rs.getString("DATA_REF_ID"));
				reportsVb.setFetchFlag(rs.getString("COUNT_FETCH_FLAG"));
				reportsVb.setDdFlag(rs.getString("DD_FLAG"));
				reportsVb.setReportOrientation(rs.getString("REPORT_ORIENTATION"));
				reportsVb.setParentSubReportID(rs.getString("PARENT_SUBREPORT_ID"));
				reportsVb.setObjectTypeAT(rs.getInt("OBJECT_TYPE_AT"));
				reportsVb.setObjectType(rs.getString("OBJECT_TYPE"));
				reportsVb.setChartType(rs.getString("CHART_TYPE"));
				reportsVb.setChartTypeAT(rs.getInt("CHART_TYPE_AT"));
				reportsVb.setCurrentLevel(rs.getString("SUBREPORT_SEQ").replaceAll(".0", ""));
				reportsVb.setNextLevel(rs.getString("SUBREPORT_SEQ").replaceAll(".0", ""));
				return reportsVb;
			}
		};
		return mapper;
	}

	public ExceptionCode getChartReportData(ReportsVb vObject,  String orginalQuery,Connection conExt) throws SQLException {
		ExceptionCode exceptionCode = new ExceptionCode();
		Statement stmt = null;
		List collTemp = new ArrayList();
		ResultSet rs = null;
		try {
			stmt = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(orginalQuery);
//			CachedRowSetImpl rsChild = new CachedRowSetImpl();
//			rsChild = new CachedRowSetImpl();
//			rsChild.populate(rs);
			CachedRowSet rsChild = RowSetProvider.newFactory().createCachedRowSet();
			rsChild.populate(rs);
			exceptionCode = chartUtils.getChartXML(vObject.getChartType(), vObject.getColHeaderXml(), rs, rsChild,
					vObject.getWidgetTheme());
			if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			}
			String chartResultXml = exceptionCode.getResponse().toString();
			if(ValidationUtil.isValid(chartResultXml)) {
				chartResultXml = replaceTagValues(chartResultXml);
			}
			exceptionCode.setResponse(chartResultXml);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		} catch (Exception e) {
			exceptionCode.setErrorMsg(e.getCause().getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			if(!ValidationUtil.isValid(exceptionCode.getErrorMsg())) {
				exceptionCode.setErrorMsg(e.getMessage());
			}
			e.printStackTrace();
			return exceptionCode;
		}
	}

	public String getIntReportNextLevel(ReportsVb dObj) {
		String nextLevel = "";
		String query = "";
		try {
			query = " SELECT NVL(MIN(SUBREPORT_SEQ),0) FROM PRD_REPORT_DETAILS WHERE SUBREPORT_SEQ > ? "
					+ " AND REPORT_ID = ?  ";

			Object[] lParams = new Object[2];
			lParams[0] = dObj.getCurrentLevel();
			lParams[1] = dObj.getReportId();
			// lParams[2] = dObj.getSubReportId();
			nextLevel = getJdbcTemplate().queryForObject(query, lParams, String.class);
			return nextLevel;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception while getting Next Level Sequence...!!");
			return "0";
		}
	}

	public String getDateFormat(String promptDate, String format) {
		setServiceDefaults();
		String query = "";
		if ("PYM".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),-1),'RRRRMM') from Dual";
		} else if ("NYM".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),+1),'RRRRMM') from Dual";
		} else if ("PM".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),-1),'MM') from Dual";
		} else if ("NM".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),+1),'MM') from Dual";
		} else if ("CM".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),0),'MM') from Dual";
		} else if ("CY".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),0),'RRRR') from Dual";
		} else if ("PY".equalsIgnoreCase(format)) {
			query = "Select TO_char(Add_Months(To_Date(" + promptDate + ",'RRRRMM'),-12),'RRRR') from Dual";
		}

		try {
			String i = getJdbcTemplate().queryForObject(query, null, String.class);
			return i;
		} catch (Exception ex) {
			return "";
		}
	}

	@Override
	protected void setServiceDefaults() {
		serviceName = "ReportsDao";
		serviceDesc = "ReportsDao";
		;
		tableName = "PRD_REPORTS";
		childTableName = "PRD_REPORTS";
		intCurrentUserId = SessionContextHolder.getContext().getVisionId();
	}

	public List<PromptTreeVb> getTreePromptData(ReportFilterVb vObject, String procSrc) {
		setServiceDefaults();
		String query = "SELECT FIELD_1, FIELD_2, FIELD_3, FIELD_4, PROMPT_ID FROM "
				+ "PROMPTS_STG WHERE VISION_ID = ? AND SESSION_ID= ? AND PROMPT_ID = ? ";
		String sessionId = String.valueOf(System.currentTimeMillis());
		CallableStatementCreator creator = new TreePromptCallableStatement(vObject, sessionId,
				String.valueOf(intCurrentUserId), procSrc);
		CallableStatementCallback callBack = (CallableStatementCallback) creator;
		PromptTreeVb result = (PromptTreeVb) getJdbcTemplate().execute(creator, callBack);
		if (result != null && "0".equalsIgnoreCase(result.getStatus())) {
			String[] params = new String[3];
			params[0] = String.valueOf(intCurrentUserId);
			params[1] = sessionId;
			params[2] = vObject.getFilterSourceId();
			vObject.setFilterString(result.getFilterString());
			if (ValidationUtil.isValid(result.getFilterString())) {
				query = query + " " + result.getFilterString();
			}
			List<PromptTreeVb> tempPromptsList = getJdbcTemplate().query(query, params, getPromptTreeMapper());
			// query = query.substring(query.indexOf("FROM "), query.indexOf("Order By")-1);
			/* query = "DELETE "+query; */
			query = "DELETE FROM PROMPTS_STG WHERE VISION_ID = ? AND SESSION_ID= ? AND PROMPT_ID = ? ";
			int cout = getJdbcTemplate().update(query, params);
			return tempPromptsList;
		} else if (result != null && "1".equalsIgnoreCase(result.getStatus())) {
			return new ArrayList<PromptTreeVb>(0);
		}
		throw new RuntimeCustomException(result.getErrorMessage());
	}

	private RowMapper getPromptTreeMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				PromptTreeVb promptTreeVb = new PromptTreeVb();
				promptTreeVb.setField1(rs.getString("FIELD_1"));
				promptTreeVb.setField2(rs.getString("FIELD_2"));
				promptTreeVb.setField3(rs.getString("FIELD_3"));
				promptTreeVb.setField4(rs.getString("FIELD_4"));
				promptTreeVb.setPromptId(rs.getString("PROMPT_ID"));
				return promptTreeVb;
			}
		};
		return mapper;
	}

	class TreePromptCallableStatement implements CallableStatementCreator, CallableStatementCallback {
		private ReportFilterVb vObject = null;
		private String currentTimeAsSessionId = null;
		private String visionId = null;
		private String procSrc = null;

		public TreePromptCallableStatement(ReportFilterVb vObject, String currentTimeAsSessionId, String visionId,
				String procSrc) {
			this.vObject = vObject;
			this.currentTimeAsSessionId = currentTimeAsSessionId;
			this.visionId = visionId;
			this.procSrc = procSrc;
		}

		public CallableStatement createCallableStatement(Connection connection) throws SQLException {
			CallableStatement cs = connection.prepareCall("{call " + procSrc + "}");
			cs.setString(1, visionId);
			cs.setString(2, currentTimeAsSessionId);
			cs.setString(3, vObject.getFilterSourceId());
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);// filterString
			cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Status
			cs.registerOutParameter(6, java.sql.Types.VARCHAR); // Error Message
			return cs;
		}

		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
			ResultSet rs = cs.executeQuery();
			PromptTreeVb promptTreeVb = new PromptTreeVb();
			promptTreeVb.setFilterString(cs.getString(4));
			promptTreeVb.setStatus(cs.getString(5));
			promptTreeVb.setErrorMessage(cs.getString(6));
			rs.close();
			return promptTreeVb;
		}
	}

	public List<ColumnHeadersVb> getColumnHeaderFromTable(PromptTreeVb promptTreeVb) {
		setServiceDefaults();
		int intKeyFieldsCount = 2;
		String query = new String(
				"SELECT REPORT_ID, SESSION_ID, LABEL_ROW_NUM, LABEL_COL_NUM, CAPTION, COLUMN_WIDTH, COL_TYPE, ROW_SPAN, COL_SPAN, NUMERIC_COLUMN_NO,UPPER(DB_COLUMN) DB_COLUMN, "
						+ "COLUMN_WIDTH,SUM_FLAG,DRILLDOWN_LABEL_FLAG,SCALING,DECIMAL_CNT,GROUPING_FLAG FROM "
						+ promptTreeVb.getColumnHeaderTable()
						+ " WHERE REPORT_ID = ? AND SESSION_ID=? ORDER BY LABEL_ROW_NUM,LABEL_COL_NUM");
		Object params[] = new Object[intKeyFieldsCount];
		params[0] = promptTreeVb.getReportId();
		params[1] = promptTreeVb.getSessionId();
		try {
			return getJdbcTemplate().query(query, params, getColumnHeadersTableMapper());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query));
			return null;
		}
	}

	private RowMapper getColumnHeadersTableMapper() {
		RowMapper mapper = new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				ColumnHeadersVb columnHeadersVb = new ColumnHeadersVb();
				columnHeadersVb.setReportId(rs.getString("REPORT_ID"));
				columnHeadersVb.setSessionId(rs.getString("SESSION_ID"));
				columnHeadersVb.setLabelRowNum(rs.getInt("LABEL_ROW_NUM"));
				columnHeadersVb.setLabelColNum(rs.getInt("LABEL_COL_NUM"));
				columnHeadersVb.setCaption(rs.getString("CAPTION"));
				columnHeadersVb.setColType(rs.getString("COL_TYPE"));
				columnHeadersVb.setRowSpanNum(rs.getInt("ROW_SPAN"));
				columnHeadersVb.setRowspan(rs.getInt("ROW_SPAN"));
				columnHeadersVb.setNumericColumnNo(rs.getInt("NUMERIC_COLUMN_NO"));
				columnHeadersVb.setColSpanNum(rs.getInt("COL_SPAN"));
				columnHeadersVb.setColspan(rs.getInt("COL_SPAN"));
				columnHeadersVb.setDbColumnName(rs.getString("DB_COLUMN"));
				if (!ValidationUtil.isValid(columnHeadersVb.getDbColumnName()))
					columnHeadersVb.setDbColumnName(columnHeadersVb.getCaption().toUpperCase());
				columnHeadersVb.setColumnWidth(rs.getString("COLUMN_WIDTH"));
				columnHeadersVb.setSumFlag(rs.getString("SUM_FLAG"));
				String drillDownLabel = rs.getString("DRILLDOWN_LABEL_FLAG");
				if (ValidationUtil.isValid(drillDownLabel) && "Y".equalsIgnoreCase(drillDownLabel)) {
					columnHeadersVb.setDrillDownLabel(true);
				} else {
					columnHeadersVb.setDrillDownLabel(false);
				}
				columnHeadersVb.setScaling(rs.getString("SCALING"));
				columnHeadersVb.setDecimalCnt(rs.getString("DECIMAL_CNT"));
				columnHeadersVb.setGroupingFlag(rs.getString("GROUPING_FLAG"));
				/*
				 * String groupingFlag = rs.getString("GROUPING_FLAG"); if
				 * (ValidationUtil.isValid(groupingFlag) && "Y".equalsIgnoreCase(groupingFlag))
				 * { columnHeadersVb.setGroupingFlag(true); } else {
				 * columnHeadersVb.setGroupingFlag(false); }
				 */
				return columnHeadersVb;
			}
		};
		return mapper;
	}

	public int deleteReportsStgData(PromptTreeVb vObject) {
		String query = "DELETE FROM REPORTS_STG WHERE REPORT_ID = ?  And SESSION_ID = ? ";
		Object args[] = { vObject.getReportId(), vObject.getSessionId() };
		return getJdbcTemplate().update(query, args);
	}

	public int deleteColumnHeadersData(PromptTreeVb vObject) {
		String query = "DELETE FROM COLUMN_HEADERS_STG WHERE REPORT_ID = ?  And SESSION_ID = ? ";
		Object args[] = { vObject.getReportId(), vObject.getSessionId() };
		return getJdbcTemplate().update(query, args);
	}

	public LinkedHashMap<String, String> getComboPromptData(ReportFilterVb vObject, String queryProc) {
		setServiceDefaults();
		ExceptionCode exceptionCode = new ExceptionCode();
		String query = "SELECT FIELD_1, FIELD_2, FIELD_3, FIELD_4, PROMPT_ID FROM PROMPTS_STG WHERE VISION_ID = ? AND SESSION_ID= ? AND PROMPT_ID = ? ORDER BY SORT_FIELD ";
		Connection con = null;
		strCurrentOperation = "Prompts";
		CallableStatement cs = null;
		try {
			String sessionId = String.valueOf(System.currentTimeMillis());
			con = getConnection();
			cs = con.prepareCall("{call " + queryProc + "}");
			int parameterCount = queryProc.split("\\?").length - 1;
			if (parameterCount > 5) {
				cs.setString(1, String.valueOf(intCurrentUserId));
				cs.setString(2, sessionId);
				cs.setString(3, vObject.getFilterSourceId());
				cs.setString(4, vObject.getFilter1Val());
				cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(6, java.sql.Types.VARCHAR); // Error Message
			} else {
				cs.setString(1, String.valueOf(intCurrentUserId));
				cs.setString(2, sessionId);
				cs.setString(3, vObject.getFilterSourceId());
				cs.registerOutParameter(4, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Error Message
			}
			ResultSet rs = cs.executeQuery();
			PromptTreeVb promptTreeVb = new PromptTreeVb();
			if (parameterCount > 5) {
				promptTreeVb.setStatus(cs.getString(5));
				promptTreeVb.setErrorMessage(cs.getString(6));
			} else {
				promptTreeVb.setStatus(cs.getString(4));
				promptTreeVb.setErrorMessage(cs.getString(5));
			}
			rs.close();
			if (promptTreeVb != null && "0".equalsIgnoreCase(promptTreeVb.getStatus())) {
				String[] params = new String[3];
				params[0] = String.valueOf(intCurrentUserId);
				params[1] = sessionId;
				params[2] = vObject.getFilterSourceId();
				if (ValidationUtil.isValid(vObject.getFilterString())) {
					query = query + " " + vObject.getFilterString();
				}
				String resultQuery = "";
				resultQuery = query;
				for (int i = 0; i < params.length; i++) {
					resultQuery = resultQuery.replaceFirst("\\?", "'?'");
					resultQuery = resultQuery.replaceFirst("\\?", params[i]);
				}
				LinkedHashMap<String, String> comboValueMap = getReportFilterValue(resultQuery);
				query = query.toUpperCase();
				if (query.indexOf("ORDER BY") > 0) {
					query = query.substring(query.indexOf("FROM "), query.indexOf("ORDER BY") - 1);
				} else {
					query = query.substring(query.indexOf("FROM "), query.length());
				}
				query = "DELETE " + query;
				int count = getJdbcTemplate().update(query, params);
				return comboValueMap;
			} else if (promptTreeVb != null && "1".equalsIgnoreCase(promptTreeVb.getStatus())) {
				return null;
			}
			throw new RuntimeCustomException(promptTreeVb.getErrorMessage());
		} catch (SQLException ex) {
			strErrorDesc = parseErrorMsg(new UncategorizedSQLException("", "", ex));
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		} catch (Exception ex) {
			ex.printStackTrace();
			strErrorDesc = ex.getMessage().trim();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		} finally {
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
	}

	public LinkedHashMap<String, String> getReportFilterValue(String sourceQuery) {
		ResultSetExtractor mapper = new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData metaData = rs.getMetaData();
				LinkedHashMap<String, String> filterMap = new LinkedHashMap<String, String>();
				while (rs.next()) {
					filterMap.put("@" + rs.getString(1), rs.getString(2));
				}
				return filterMap;
			}
		};
		return (LinkedHashMap<String, String>) getJdbcTemplate().query(sourceQuery, mapper);
	}

	public String getReportFilterDefaultValue(String sourceQuery) {
		ResultSetExtractor mapper = new ResultSetExtractor() {
			String defaultValue = "";

			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					defaultValue = rs.getString(1);
				}
				return defaultValue;
			}
		};
		return (String) getJdbcTemplate().query(sourceQuery, mapper);
	}

	public List<RCReportFieldsVb> getCBKReportData(ReportsVb reportsVb, PromptTreeVb prompts, long min, long max) {
		String query = "";
		try {
			query = "SELECT TAB_ID, ROW_ID, COL_ID, CELL_DATA, COL_TYPE, CREATE_NEW,SHEET_NAME,FORMAT_TYPE,FILE_NAME FROM TEMPLATES_STG WHERE SORT_FIELD <=? AND SORT_FIELD >? "
					+ "AND SESSION_ID=? ORDER BY TAB_ID,ROW_ID,COL_ID";
			ResultSetExtractor mapper = new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<RCReportFieldsVb> result = new ArrayList<RCReportFieldsVb>();
					while (rs.next()) {
						RCReportFieldsVb fieldsVb = new RCReportFieldsVb();
						fieldsVb.setTabelId(rs.getString("TAB_ID"));
						fieldsVb.setRowId(rs.getString("ROW_ID"));
						fieldsVb.setColId(rs.getString("COL_ID"));
						fieldsVb.setValue1(rs.getString("CELL_DATA"));
						fieldsVb.setColType(rs.getString("COL_TYPE"));
						fieldsVb.setSheetName(rs.getString("SHEET_NAME"));
						fieldsVb.setRowStyle(rs.getString("FORMAT_TYPE"));
						fieldsVb.setExcelFileName(rs.getString("FILE_NAME"));
						// M - Mandatory create new Line. Y -- Create new Line only if does not exists
						// N-- Do not create new Line.
						fieldsVb.setCreateNew("Y".equalsIgnoreCase(rs.getString("CREATE_NEW"))
								|| "M".equalsIgnoreCase(rs.getString("CREATE_NEW")) ? true : false);
						fieldsVb.setCreateNewRow(rs.getString("CREATE_NEW"));
						result.add(fieldsVb);
					}
					return result;
				}
			};
			Object[] promptValues = new Object[3];
			promptValues[0] = max;
			promptValues[1] = min;
			promptValues[2] = prompts.getSessionId();
			/*
			 * int retVal =InsetAuditTrialDataForReports(reportWriterVb, prompts);
			 * if(retVal!=Constants.SUCCESSFUL_OPERATION){
			 * logger.error("Error  inserting into rs_Schedule Audit"); }
			 */

			return (List<RCReportFieldsVb>) getJdbcTemplate().query(query, promptValues, mapper);
		} catch (BadSqlGrammarException ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query));
			strErrorDesc = parseErrorMsg(new UncategorizedSQLException("", "", ex.getSQLException()));
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(((query == null) ? "query is Null" : query));
			strErrorDesc = ex.getMessage();
			throw buildRuntimeCustomException(getResultObject(Constants.WE_HAVE_ERROR_DESCRIPTION));
		}
	}

	public void callProcToCleanUpTables(PromptTreeVb promptTree) {
		Connection con = null;
		CallableStatement cs = null;
		try {
			con = getConnection();
			cs = con.prepareCall("{call PR_RS_CLEANUP(?, ?, ?, ?, ?)}");
			cs.setString(1, String.valueOf(intCurrentUserId));// Report Id
			cs.setString(2, promptTree.getSessionId());// Group Report Id
			cs.setString(3, promptTree.getReportId());// Group Report Id
			cs.registerOutParameter(4, java.sql.Types.VARCHAR);// Chart type list
			cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Status
			ResultSet rs = cs.executeQuery();
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			JdbcUtils.closeStatement(cs);
			DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
	}

	
	public ExceptionCode getResultData(ReportsVb vObject,Boolean exportFlag) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int totalRecords = 0;
		List datalst = new ArrayList();
		Connection conExt =null;
		try {
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt,vObject.getDbConnection());
			if(exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection)exConnection.getResponse();
			}else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				exceptionCode.setResponse(exConnection.getResponse());
				return exceptionCode;
			}
			if ("G".equalsIgnoreCase(vObject.getObjectType())) {
				if(exportFlag) {
					exceptionCode = extractReportData(vObject,conExt,exportFlag);
				}else {
					exceptionCode = extractReportData(vObject,conExt);
				}
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					datalst = (ArrayList) exceptionCode.getResponse();
					vObject.setGridDataSet(datalst);
					if(exceptionCode.getResponse1() != null)
						vObject.setColumnHeaderslst((ArrayList<ColumnHeadersVb>)exceptionCode.getResponse1());
					//Filter the Data which doensnot contains Format Type S
					List datalstNew = new ArrayList<>();
					for (Map<String, Object> dataLstMap : (List<Map<String, Object>>) datalst) {
						if(dataLstMap.containsKey("FORMAT_TYPE")) {
							if(dataLstMap.get("FORMAT_TYPE") == null)
								dataLstMap.put("FORMAT_TYPE", "D");
						}
						datalstNew.add(dataLstMap);
					}
					List<Map<String, Object>> finalDatalst = (List<Map<String, Object>>)datalstNew.stream()
			                .filter(hashmap -> ((HashMap<String, String>) hashmap).containsKey("FORMAT_TYPE"))
			                .filter(hashmap -> !((HashMap<String, String>) hashmap).get("FORMAT_TYPE").equalsIgnoreCase("S"))
			                .collect(Collectors.toList());
					if(finalDatalst != null && !finalDatalst.isEmpty())
						vObject.setGridDataSet(finalDatalst);
					
					totalRecords = (int) exceptionCode.getRequest();
					vObject.setTotalRows(totalRecords);
					if(vObject.getCurrentPage() == 1) {
						List<Map<String, Object>> totallst = (List<Map<String, Object>>)datalst.stream()
					                .filter(hashmap -> ((HashMap<String, String>) hashmap).containsKey("FORMAT_TYPE"))
					                .filter(hashmap -> ((HashMap<String, String>) hashmap).get("FORMAT_TYPE").equalsIgnoreCase("S"))
					                .collect(Collectors.toList());
						vObject.setTotal(totallst);
						if(totallst == null || totallst.isEmpty()) {
							List<ReportsVb> sumStringLst = new ArrayList<>();
							StringJoiner sumString = new StringJoiner(",");
							vObject.getColumnHeaderslst().forEach(colHeadersVb -> {
								if(!"T".equalsIgnoreCase(colHeadersVb.getColType()) && (colHeadersVb.getColspan() == 0 || colHeadersVb.getColspan() == 1) 
											&& "Y".equalsIgnoreCase(colHeadersVb.getSumFlag())) {
									sumString.add("SUM("+colHeadersVb.getDbColumnName()+") " +colHeadersVb.getDbColumnName());
								}
							});
							ExceptionCode exceptionCode1 = new ExceptionCode();
							String query = null;
							if(sumString.length() > 0) {
								query = "SELECT "+sumString.toString()+",'S' FORMAT_TYPE FROM (" +vObject.getFinalExeQuery() + ") TOT ";
								exceptionCode1 = commonApiDao.getCommonResultDataQuery(query,conExt);
								if(exceptionCode1.getResponse() != null) {
									sumStringLst  =  (List<ReportsVb>) exceptionCode1.getResponse();
									vObject.setTotal(sumStringLst);
								}
							}
						}
					}
				}else {
					exceptionCode.setResponse(vObject);
					return exceptionCode;
				}
		}else if ("C".equalsIgnoreCase(vObject.getObjectType())) {
				exceptionCode = getChartReportData(vObject, vObject.getFinalExeQuery(),conExt);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				vObject.setChartData(exceptionCode.getResponse().toString());
			}
		}else if("T".equalsIgnoreCase(vObject.getObjectType()) || "SC".equalsIgnoreCase(vObject.getObjectType()) || "S".equalsIgnoreCase(vObject.getObjectType())) {
			exceptionCode = getTilesReportData(vObject, vObject.getFinalExeQuery(),conExt);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				vObject.setTileData(exceptionCode.getResponse().toString());
			}
		}
		exceptionCode.setResponse(vObject);
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		exceptionCode.setErrorMsg("Success");
		}catch(Exception e) {
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}finally {
			try {
				conExt.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return exceptionCode;
	}

	public ExceptionCode getReportPromptsFilterValue(PrdQueryConfig vObject, String query) {
		ExceptionCode exceptionCode = new ExceptionCode();
		LinkedHashMap<String, String> filterMap = new LinkedHashMap<String, String>();
		Connection conExt = null;
		Statement stmt1 = null;
		try {
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt, vObject.getDbConnectionName());
			if (exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection) exConnection.getResponse();
			} else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				exceptionCode.setResponse(exConnection.getResponse());
				return exceptionCode;
			}
			if (ValidationUtil.isValid(query))
				vObject.setQueryProc(query);
			stmt1 = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rsData = stmt1.executeQuery(vObject.getQueryProc());
			ResultSetMetaData metaData = rsData.getMetaData();
			int colCount = metaData.getColumnCount();
			while (rsData.next()) {
				if (colCount == 1)
					filterMap.put("@" + rsData.getString(1), rsData.getString(1));
				else
					filterMap.put("@" + rsData.getString(1), rsData.getString(2));
			}
			if (filterMap == null && filterMap.isEmpty()) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				return exceptionCode;
			}
			exceptionCode.setResponse(filterMap);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		} finally {
			try {
				if (stmt1 != null)
					stmt1.close();
				if (conExt != null)
					conExt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return exceptionCode;
		// return (LinkedHashMap<String, String>) getJdbcTemplate().query(sourceQuery,
		// mapper);
	}

	public ExceptionCode getFilterPromptWithHashVar(ReportFilterVb vObject, PrdQueryConfig prdQueryConfigVb,
			String type) {
		setServiceDefaults();
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection conExt = null;
		strCurrentOperation = "Prompts";
		CallableStatement cs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		List<PromptTreeVb> tempPromptsList = new ArrayList<PromptTreeVb>();
		try {
			String sessionId = String.valueOf(System.currentTimeMillis());
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt, prdQueryConfigVb.getDbConnectionName());
			if (exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection) exConnection.getResponse();
			} else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				exceptionCode.setResponse(exConnection.getResponse());
				return exceptionCode;
			}
			int parameterCount = prdQueryConfigVb.getQueryProc().split("\\?").length - 1;

			cs = conExt.prepareCall("{call " + prdQueryConfigVb.getQueryProc() + "}");
			if (parameterCount == 3) {
				cs.registerOutParameter(1, java.sql.Types.VARCHAR); // Filter String
				cs.registerOutParameter(2, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(3, java.sql.Types.VARCHAR); // Error Msg
			} else if (parameterCount == 2) {
				cs.registerOutParameter(1, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(2, java.sql.Types.VARCHAR); // Error Msg
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("There should be only 2 or 3 output parameter configured");
				return exceptionCode;
			}
			cs.execute();
			PromptTreeVb promptTreeVb = new PromptTreeVb();
			if (parameterCount == 3) {
				promptTreeVb.setFilterString(cs.getString(1));
				promptTreeVb.setStatus(cs.getString(2));
				promptTreeVb.setErrorMessage(cs.getString(3));
			} else if (parameterCount == 2) {
				promptTreeVb.setStatus(cs.getString(1));
				promptTreeVb.setErrorMessage(cs.getString(2));
			}

			cs.close();
			String resultQuery = "SELECT FIELD_1, FIELD_2, FIELD_3, FIELD_4, PROMPT_ID FROM PROMPTS_STG WHERE VISION_ID = '"
					+ intCurrentUserId + "' " + "AND SESSION_ID= '" + vObject.getDateCreation() + "' AND PROMPT_ID = '"
					+ vObject.getFilterSourceId() + "' ";

			if (promptTreeVb != null && "0".equalsIgnoreCase(promptTreeVb.getStatus())) {
				if ("TREE".equalsIgnoreCase(type)) {
					vObject.setFilterString(promptTreeVb.getFilterString());
					stmt1 = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					rs1 = stmt1.executeQuery(resultQuery);
					while (rs1.next()) {
						PromptTreeVb Obj = new PromptTreeVb();
						Obj.setField1(rs1.getString("FIELD_1"));
						Obj.setField2(rs1.getString("FIELD_2"));
						Obj.setField3(rs1.getString("FIELD_3"));
						Obj.setField4(rs1.getString("FIELD_4"));
						Obj.setPromptId(rs1.getString("PROMPT_ID"));
						tempPromptsList.add(Obj);
						exceptionCode.setResponse(tempPromptsList);
					}
				} else {
					resultQuery = resultQuery + " ORDER BY SORT_FIELD ";
					LinkedHashMap<String, String> comboValueMap = new LinkedHashMap<String, String>();
					exceptionCode = getReportPromptsFilterValue(prdQueryConfigVb, resultQuery);
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION
							&& exceptionCode.getResponse() != null)
						comboValueMap = (LinkedHashMap<String, String>) exceptionCode.getResponse();

					exceptionCode.setResponse(comboValueMap);
				}
				String deletionQuery = "DELETE FROM PROMPTS_STG WHERE VISION_ID = ? "
						+ "AND SESSION_ID= ? AND PROMPT_ID = ? ";
				Object args[] = { intCurrentUserId, vObject.getDateCreation(), vObject.getFilterSourceId() };
				int deletionCnt = getJdbcTemplate().update(deletionQuery, args);

				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			} else if (promptTreeVb != null && "1".equalsIgnoreCase(promptTreeVb.getStatus())) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(promptTreeVb.getErrorMessage());
				return exceptionCode;
			}
		} catch (Exception ex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		} finally {
			JdbcUtils.closeStatement(cs);
			try {
				if (stmt1 != null)
					stmt1.close();
				if (conExt != null)
					conExt.close();
				if (rs1 != null)
					rs1.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return exceptionCode;
	}

	public ExceptionCode getComboPromptData(ReportFilterVb vObject, PrdQueryConfig prdQueryConfigVb) {
		setServiceDefaults();
		ExceptionCode exceptionCode = new ExceptionCode();
		String query = "SELECT FIELD_1, FIELD_2, FIELD_3, FIELD_4, PROMPT_ID FROM PROMPTS_STG WHERE VISION_ID = ? AND SESSION_ID= ? AND PROMPT_ID = ? ORDER BY SORT_FIELD ";
		Connection conExt = null;
		strCurrentOperation = "Prompts";
		CallableStatement cs = null;
		Statement stmt1 = null;
		try {
			String sessionId = String.valueOf(System.currentTimeMillis());
			ExceptionCode exConnection = commonDao.getReqdConnection(conExt, prdQueryConfigVb.getDbConnectionName());
			if (exConnection.getErrorCode() != Constants.ERRONEOUS_OPERATION && exConnection.getResponse() != null) {
				conExt = (Connection) exConnection.getResponse();
			} else {
				exceptionCode.setErrorCode(exConnection.getErrorCode());
				exceptionCode.setErrorMsg(exConnection.getErrorMsg());
				exceptionCode.setResponse(exConnection.getResponse());
				return exceptionCode;
			}
			stmt1 = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			cs = conExt.prepareCall("{call " + prdQueryConfigVb.getQueryProc() + "}");
			int parameterCount = prdQueryConfigVb.getQueryProc().split("\\?").length - 1;
			String filterVal = "";
			// String filter1Val = vObject.getFilter1Val().replaceAll("'", "");

			if (ValidationUtil.isValid(vObject.getFilter1Val()))
				filterVal = vObject.getFilter1Val();
			if (ValidationUtil.isValid(vObject.getFilter2Val()))
				filterVal = vObject.getFilter2Val();
			if (ValidationUtil.isValid(vObject.getFilter3Val()))
				filterVal = vObject.getFilter3Val();
			if (ValidationUtil.isValid(vObject.getFilter4Val()))
				filterVal = vObject.getFilter4Val();
			if (ValidationUtil.isValid(vObject.getFilter5Val()))
				filterVal = vObject.getFilter5Val();
			if (ValidationUtil.isValid(vObject.getFilter6Val()))
				filterVal = vObject.getFilter6Val();
			if (ValidationUtil.isValid(vObject.getFilter7Val()))
				filterVal = vObject.getFilter7Val();
			if (ValidationUtil.isValid(vObject.getFilter8Val()))
				filterVal = vObject.getFilter8Val();
			if (ValidationUtil.isValid(vObject.getFilter9Val()))
				filterVal = vObject.getFilter9Val();
			if (ValidationUtil.isValid(vObject.getFilter10Val()))
				filterVal = vObject.getFilter10Val();

			filterVal = filterVal.replaceAll("'", "");
			// logger.info("filter1Val : "+filterVal);
			// logger.info("parameterCount : "+parameterCount);
			if (parameterCount != 7 && parameterCount > 6) {
				cs.setString(1, String.valueOf(intCurrentUserId));
				cs.setString(2, sessionId);
				cs.setString(3, vObject.getFilterSourceId());
				if (!ValidationUtil.isValid(filterVal)) {
					cs.setString(4, "");// Filter Condition
				} else {
					cs.setString(4, filterVal);// Filter Condition
				}
				cs.registerOutParameter(5, java.sql.Types.VARCHAR);// filterString
				cs.registerOutParameter(6, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(7, java.sql.Types.VARCHAR); // Category (T-Trigger error,V-Validation Error)
			} else if (parameterCount == 7) {
				cs.setString(1, String.valueOf(intCurrentUserId));
				cs.setString(2, sessionId);
				cs.setString(3, vObject.getFilterSourceId());
				if (!ValidationUtil.isValid(filterVal)) {
					cs.setString(4, "");// Filter Condition
					cs.setString(5, "");// Filter Condition
				} else {
					cs.setString(4, filterVal);// Filter Condition
					cs.setString(5, "");// Filter Condition
				}
				cs.registerOutParameter(6, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(7, java.sql.Types.VARCHAR); // Category (T-Trigger error,V-Validation Error)
			} else if (parameterCount == 6) {
				cs.setString(1, String.valueOf(intCurrentUserId));
				cs.setString(2, sessionId);
				cs.setString(3, vObject.getFilterSourceId());
				cs.setString(4, filterVal);
				cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(6, java.sql.Types.VARCHAR); // Error Message
			} else {
				cs.setString(1, String.valueOf(intCurrentUserId));
				cs.setString(2, sessionId);
				cs.setString(3, vObject.getFilterSourceId());
				cs.registerOutParameter(4, java.sql.Types.VARCHAR); // Status
				cs.registerOutParameter(5, java.sql.Types.VARCHAR); // Error Message
			}
			cs.execute();
			PromptTreeVb promptTreeVb = new PromptTreeVb();
			/*
			 * if(parameterCount > 5) { promptTreeVb.setStatus(cs.getString(5));
			 * promptTreeVb.setErrorMessage(cs.getString(6)); }else {
			 * promptTreeVb.setStatus(cs.getString(4));
			 * promptTreeVb.setErrorMessage(cs.getString(5)); }
			 */
			if (parameterCount != 7 && parameterCount > 6) {
				promptTreeVb.setFilterString(cs.getString(5));
				promptTreeVb.setStatus(cs.getString(6));
				promptTreeVb.setErrorMessage(cs.getString(7));
			} else if (parameterCount == 7) {
				promptTreeVb.setStatus(cs.getString(6));
				promptTreeVb.setErrorMessage(cs.getString(7));
			} else if (parameterCount == 6) {
				promptTreeVb.setStatus(cs.getString(5));
				promptTreeVb.setErrorMessage(cs.getString(6));
			} else {
				promptTreeVb.setStatus(cs.getString(4));
				promptTreeVb.setErrorMessage(cs.getString(5));
			}
			cs.close();
			if (promptTreeVb != null && "0".equalsIgnoreCase(promptTreeVb.getStatus())) {
				String[] params = new String[3];
				params[0] = String.valueOf(intCurrentUserId);
				params[1] = sessionId;
				params[2] = vObject.getFilterSourceId();
				/*
				 * if (ValidationUtil.isValid(vObject.getFilterString())) { query = query + " "
				 * + vObject.getFilterString(); }
				 */
				String resultQuery = "";
				resultQuery = query;
				for (int i = 0; i < params.length; i++) {
					resultQuery = resultQuery.replaceFirst("\\?", "'?'");
					resultQuery = resultQuery.replaceFirst("\\?", params[i]);
				}
				LinkedHashMap<String, String> comboValueMap = new LinkedHashMap<String, String>();
				exceptionCode = getReportPromptsFilterValue(prdQueryConfigVb, resultQuery);
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION
						&& exceptionCode.getResponse() != null)
					comboValueMap = (LinkedHashMap<String, String>) exceptionCode.getResponse();
				query = query.toUpperCase();
				if (query.indexOf("ORDER BY") > 0) {
					query = query.substring(query.indexOf("FROM "), query.indexOf("ORDER BY") - 1);
				} else {
					query = query.substring(query.indexOf("FROM "), query.length());
				}
				for (int i = 0; i < params.length; i++) {
					if (query.contains("?"))
						query = query.replaceFirst("\\?", "'" + params[i] + "'");
				}
				query = "DELETE " + query;
				stmt1.executeUpdate(query);
				// int count = getJdbcTemplate().update(query, params);

				exceptionCode.setResponse(comboValueMap);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			} else if (promptTreeVb != null && "1".equalsIgnoreCase(promptTreeVb.getStatus())) {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				return exceptionCode;
			}
			// throw new RuntimeCustomException(promptTreeVb.getErrorMessage());
		} catch (Exception ex) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		} finally {
			JdbcUtils.closeStatement(cs);
			try {
				conExt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// DataSourceUtils.releaseConnection(con, jdbcTemplate.getDataSource());
		}
		return exceptionCode;
	}
	
	private String replaceTagValues(String chartXml) {
		String chartReplaceXml = chartXml;
		chartReplaceXml=chartReplaceXml.replaceAll("categoryL1", "category");
		chartReplaceXml=chartReplaceXml.replaceAll("categoryL2", "category");
		chartReplaceXml=chartReplaceXml.replaceAll("categoryL3", "category");
		return chartReplaceXml;
	}
	
	public ExceptionCode getTilesReportData(ReportsVb vObject,String orginalQuery,Connection conExt) throws SQLException{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		setServiceDefaults();
		Statement stmt = null; 
		ResultSet rs = null;
		String resultData = "";
		DecimalFormat dfDec = new DecimalFormat("0.00");
		DecimalFormat dfNoDec = new DecimalFormat("0");
		List<PrdQueryConfig> sqlQueryList = new ArrayList<PrdQueryConfig>();
		PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
		ExceptionCode exceptionCode = new ExceptionCode();
		Statement stmt1 = null;
		try
		{	
			stmt1 = conExt.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
				    ResultSet.CONCUR_READ_ONLY);
			
			ResultSet rsData = stmt1.executeQuery(orginalQuery);
			ResultSetMetaData metaData = rsData.getMetaData();
			int colCount = metaData.getColumnCount();
			HashMap<String,String> columns = new HashMap<String,String>();
			Boolean dataAvail = false;
			while(rsData.next()){
				for(int cn = 1;cn <= colCount;cn++) {
					String columnName = metaData.getColumnName(cn);
					columns.put(columnName.toUpperCase(), rsData.getString(columnName));
				}
				dataAvail = true;
				break;
			}
			rsData.close();
			if(!dataAvail) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				exceptionCode.setErrorMsg("No Records Found");
				return exceptionCode;
			}
			String fieldProp = vObject.getColHeaderXml();
			fieldProp = ValidationUtil.isValid(fieldProp)?fieldProp.replaceAll("\n", "").replaceAll("\r", ""):"";
			for(int ctr = 1;ctr <= 5;ctr++) {
				String placeHolder = CommonUtils.getValueForXmlTag(fieldProp, "PLACEHOLDER"+ctr);
				String sourceCol = CommonUtils.getValueForXmlTag(placeHolder, "SOURCECOL");
				String dataType = CommonUtils.getValueForXmlTag(placeHolder, "DATA_TYPE");
				String numberFormat = CommonUtils.getValueForXmlTag(placeHolder, "NUMBERFORMAT");
				String scaling = CommonUtils.getValueForXmlTag(placeHolder, "SCALING");
				if(!ValidationUtil.isValid(placeHolder) || !ValidationUtil.isValid(sourceCol)) {
					continue;
				}
				if(ValidationUtil.isValid(sourceCol)) {
					String fieldValue = columns.get(sourceCol);
					if(!ValidationUtil.isValid(fieldValue))
						continue;
					/*Double val = 0.00;*/
					String prefix="";
					if(ValidationUtil.isValid(scaling) && "Y".equalsIgnoreCase(scaling) && ValidationUtil.isValid(fieldValue)) {
						Double dbValue = Math.abs(Double.parseDouble(fieldValue));
						if(dbValue > 1000000000) {
							dbValue = Double.parseDouble(fieldValue)/1000000000;
							prefix = "B";
						}else if(dbValue > 1000000) {
							dbValue = Double.parseDouble(fieldValue)/1000000;
							prefix = "M";
						}else if(dbValue > 10000) {
							dbValue = Double.parseDouble(fieldValue)/1000;
							prefix = "K";
						}
						String afterDecimalVal = String.valueOf(dbValue);
						if(!afterDecimalVal.contains("E")) {
							afterDecimalVal = afterDecimalVal.substring(afterDecimalVal.indexOf ( "." )+1);
							if(Double.parseDouble(afterDecimalVal) > 0)
								fieldValue = dfDec.format(dbValue)+" "+prefix;
							else
								fieldValue = dfNoDec.format(dbValue)+" "+prefix;
						}else {
							fieldValue = "0.00";
						}
					}
					if(ValidationUtil.isValid(fieldValue) && ValidationUtil.isValid(numberFormat) && "Y".equalsIgnoreCase(numberFormat) && !ValidationUtil.isValid(prefix)) {
						DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
						double tmpVal = Double.parseDouble(fieldValue);
						fieldValue = decimalFormat.format(tmpVal);
					}
					/*if(ValidationUtil.isValid(fieldValue))*/
					fieldProp = fieldProp.replaceAll(sourceCol, fieldValue);
				}
			}
			String drillDownKey = CommonUtils.getValueForXmlTag(fieldProp,"DRILLDOWN_KEY");
			if(ValidationUtil.isValid(drillDownKey) && "DD_KEY_ID".equalsIgnoreCase(drillDownKey)) {
				String value = columns.get(drillDownKey);
				if(ValidationUtil.isValid(value))
					fieldProp = fieldProp.replaceAll(drillDownKey, value);
				else
					fieldProp = fieldProp.replaceAll(drillDownKey, "");
			}
			int PRETTY_PRINT_INDENT_FACTOR = 4;
			JSONObject xmlJSONObj = XML.toJSONObject(fieldProp);
			resultData = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR).replaceAll("[\\n\\t ]", "");
			exceptionCode.setResponse(resultData);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}catch(Exception ex){
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
		}finally {
			if(stmt1 != null)
				stmt1.close();
			if(conExt != null)
				conExt.close();
		}
		return exceptionCode;
	}
	
	public ExceptionCode extractReportData(ReportsVb vObj,Connection conExt) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = extractReportData(vObj,conExt,false);
		}catch(Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}
}