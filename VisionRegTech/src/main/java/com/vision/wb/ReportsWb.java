package com.vision.wb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonApiDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportsDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.DeepCopy;
import com.vision.util.ExcelExportUtil;
import com.vision.util.PDFExportUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.PrdQueryConfig;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.RCReportFieldsVb;
import com.vision.vb.ReportFilterVb;
import com.vision.vb.ReportStgVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.ServletContext;

@Component
public class ReportsWb extends AbstractWorkerBean<ReportsVb> implements ServletContextAware {
	public ApplicationContext applicationContext;
	private ServletContext servletContext;
	@Autowired
	private ReportsDao reportsDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private PDFExportUtil pdfExportUtil;
	@Autowired
	CommonApiDao commonApiDao;
	@Value("${app.databaseType}")
	private String databaseType;
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}
	public static Logger logger = LoggerFactory.getLogger(ReportsWb.class);

	@Override
	protected void setAtNtValues(ReportsVb vObject) {

	}

	@Override
	protected void setVerifReqDeleteType(ReportsVb vObject) {
		vObject.setStaticDelete(false);
		vObject.setVerificationRequired(false);
	}

	@Override
	protected AbstractDao<ReportsVb> getScreenDao() {
		return reportsDao;
	}

	public ExceptionCode getReportList() {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			ArrayList<ReportsVb> reportCatlst = (ArrayList<ReportsVb>) reportsDao.findReportCategory(6016);
			ArrayList<ReportsVb> reportslstbyCategory = new ArrayList<ReportsVb>();
			ArrayList<ReportsVb> reportslst = new ArrayList<ReportsVb>();
			for (ReportsVb categoryVb : reportCatlst) {
				reportslstbyCategory = (ArrayList<ReportsVb>) reportsDao.getReportList(categoryVb.getReportCategory());
				categoryVb.setReportslst(reportslstbyCategory);
				reportslst.add(categoryVb);
			}
			exceptionCode.setResponse(reportslst);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode reportFilterProcess(String filterRefCode) {
		List<ReportFilterVb> filterlst = new ArrayList<ReportFilterVb>();
		ExceptionCode exceptionCode = new ExceptionCode();
		Connection conExt = null;
		try {
			List<ReportFilterVb> filterDetaillst = reportsDao.getReportFilterDetail(filterRefCode);
			if (filterDetaillst != null && filterDetaillst.size() > 0) {
				ReportFilterVb filterObj = filterDetaillst.get(0);
				String filterObjProp = ValidationUtil.isValid(filterObj.getFilterRefXml())
						? filterObj.getFilterRefXml().replaceAll("\n", "").replaceAll("\r", "")
						: "";
				String promptXml = CommonUtils.getValueForXmlTag(filterObjProp, "Prompts");
				int filterCnt = Integer.parseInt(CommonUtils.getValueForXmlTag(promptXml, "PromptCount"));
				for (int i = 1; i <= filterCnt; i++) {
					ReportFilterVb vObject = new ReportFilterVb();
					String refXml = CommonUtils.getValueForXmlTag(filterObjProp, "Prompt" + i);

					vObject.setFilterSeq(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "Sequence")));
					vObject.setFilterLabel(CommonUtils.getValueForXmlTag(refXml, "Label"));
					vObject.setFilterType(CommonUtils.getValueForXmlTag(refXml, "Type"));
					vObject.setFilterSourceId(CommonUtils.getValueForXmlTag(refXml, "SourceId"));
					vObject.setDependencyFlag(CommonUtils.getValueForXmlTag(refXml, "DependencyFlag"));
					vObject.setDependentPrompt(CommonUtils.getValueForXmlTag(refXml, "DependentPrompt"));
					vObject.setMultiWidth(CommonUtils.getValueForXmlTag(refXml, "MultiWidth"));
					vObject.setMultiSelect(CommonUtils.getValueForXmlTag(refXml, "MultiSelect"));
					vObject.setSpecificTab(CommonUtils.getValueForXmlTag(refXml, "SpecificTab")); // Only for Dashboards
					vObject.setDefaultValueId( CommonUtils.getValueForXmlTag(refXml, "DefaultValue"));
					vObject.setFilterRow(CommonUtils.getValueForXmlTag(refXml, "FilterRow"));
					vObject.setFilterDateFormat(CommonUtils.getValueForXmlTag(refXml, "DateFormat"));
					vObject.setFilterDateRestrict(CommonUtils.getValueForXmlTag(refXml, "DateRestrict"));
					//Added for Exception Filter - To Add Filter Str T1.Country
					vObject.setFilterDbString(CommonUtils.getValueForXmlTag(refXml, "ExceptionFilter"));
					vObject.setFilterValueType(CommonUtils.getValueForXmlTag(refXml, "FilterValueType"));
					vObject.setIsMandatory(CommonUtils.getValueForXmlTag(refXml, "isMandatory"));
					if(!ValidationUtil.isValid(vObject.getIsMandatory())) {
						vObject.setIsMandatory("N");
					}
					if (ValidationUtil.isValid(vObject.getDefaultValueId())) {
						//defaultValueSrc = replaceFilterHashVariables(vObject.getDefaultValueId(), vObject);
						//vObject.setFilterDefaultValue(reportsDao.getReportFilterValue(defaultValueSrc));
						LinkedHashMap<String,String> filterDefaultValueMap = new LinkedHashMap<String,String>();
						exceptionCode = getReportFilterSourceValue(vObject);
						LinkedHashMap<String,String> filterMap = (LinkedHashMap<String,String>) exceptionCode.getResponse();
						if (filterMap != null) {
							for (Map.Entry<String, String> entry : filterMap.entrySet()) {
								String key = entry.getKey();
								if (key.contains("@")) {
									key = key.replace("@", "");
									filterDefaultValueMap.put(key, entry.getValue());
								}

							}
							vObject.setFilterDefaultValue(filterDefaultValueMap);
						}
					}
					if (!ValidationUtil.isValid(vObject.getDependencyFlag())) {
						vObject.setDependencyFlag("N");
					}
					/*
					 * if ("N".equalsIgnoreCase(vObject.getDependencyFlag()) &&
					 * ValidationUtil.isValid(vObject.getFilterSourceId())) {
					 * vObject.setFilterSourceVal(getFilterSourceValue(vObject)); }
					 */
					filterlst.add(vObject);
				}
			}
			exceptionCode.setResponse(filterlst);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			return exceptionCode;
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
	}

	public LinkedHashMap<String, String> getFilterSourceValue(ReportFilterVb vObject) {
		LinkedHashMap<String, String> filterSourceVal = new LinkedHashMap<String, String>();
		try {
			List<PrdQueryConfig> sourceQuerylst = reportsDao.getSqlQuery(vObject.getFilterSourceId());
			if (sourceQuerylst != null && sourceQuerylst.size() > 0) {
				PrdQueryConfig sourceQueryDet = sourceQuerylst.get(0);
				if ("QUERY".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					sourceQueryDet.setQueryProc(replaceFilterHashVariables(sourceQueryDet.getQueryProc(), vObject));
					filterSourceVal = reportsDao.getReportFilterValue(sourceQueryDet.getQueryProc());
				}else if ("PROCEDURE".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					filterSourceVal = reportsDao.getComboPromptData(vObject,sourceQueryDet.getQueryProc());
					
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return filterSourceVal;
	}

	public String replaceFilterHashVariables(String query, ReportFilterVb vObject) {
		query = query.replaceAll("#VISION_ID#", "" + SessionContextHolder.getContext().getVisionId());
		
		query = query.replaceAll("#TEMPLATE_ID#",
				ValidationUtil.isValid(vObject.getTemplateId()) ? vObject.getTemplateId() : "''");
		query = query.replaceAll("#SOURCE_TABLE#",
				ValidationUtil.isValid(vObject.getSourceTable()) ? vObject.getSourceTable() : "''");
		if(ValidationUtil.isValid(vObject.getFilterDbString()) && !vObject.getFilterDbString().contains("DATE")) {
		query = query.replaceAll("#COLUMN_ID#",
				ValidationUtil.isValid(vObject.getFilterDbString()) ? vObject.getFilterDbString() : "''");
		}else {
		String val = commonDao.getDbFunction("DATEFUNC")+"("+vObject.getFilterDbString()+",'"+commonDao.getDbFunction("DATEFORMAT")+"')";
		query = query.replaceAll("#COLUMN_ID#",
				val);
		}
		query = query.replaceAll("#SOURCE_TYPE#",
				ValidationUtil.isValid(vObject.getSourceType()) ? vObject.getSourceType() : "''");
		query = query.replaceAll("#PROMPT_VALUE_1#",
				ValidationUtil.isValid(vObject.getFilter1Val()) ? vObject.getFilter1Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_2#",
				ValidationUtil.isValid(vObject.getFilter2Val()) ? vObject.getFilter2Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_3#",
				ValidationUtil.isValid(vObject.getFilter3Val()) ? vObject.getFilter3Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_4#",
				ValidationUtil.isValid(vObject.getFilter4Val()) ? vObject.getFilter4Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_5#",
				ValidationUtil.isValid(vObject.getFilter5Val()) ? vObject.getFilter5Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_6#",
				ValidationUtil.isValid(vObject.getFilter6Val()) ? vObject.getFilter6Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_7#",
				ValidationUtil.isValid(vObject.getFilter7Val()) ? vObject.getFilter7Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_8#",
				ValidationUtil.isValid(vObject.getFilter8Val()) ? vObject.getFilter8Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_9#",
				ValidationUtil.isValid(vObject.getFilter9Val()) ? vObject.getFilter9Val() : "''");
		query = query.replaceAll("#PROMPT_VALUE_10#",
				ValidationUtil.isValid(vObject.getFilter10Val()) ? vObject.getFilter10Val() : "''");
		return query;
	}

	/*
	 * public ExceptionCode getResultData(ReportsVb vObject, Boolean exportFlag) {
	 * ExceptionCode exceptionCode = new ExceptionCode(); int gridReportTotalRow =
	 * 0; List datalst = new ArrayList(); try { if
	 * ("G".equalsIgnoreCase(vObject.getObjectType())) { exceptionCode =
	 * reportsDao.extractReportData(vObject); if (exceptionCode.getErrorCode() ==
	 * Constants.SUCCESSFUL_OPERATION) { datalst = (ArrayList)
	 * exceptionCode.getResponse(); vObject.setGridDataSet(datalst); // Filter the
	 * Data which doensnot contains Format Type FT List<Map<String, Object>>
	 * finalDatalst = (List<Map<String, Object>>) datalst.stream() .filter(hashmap
	 * -> ((HashMap<String, String>) hashmap).containsKey("FORMAT_TYPE"))
	 * .filter(hashmap -> !((HashMap<String, String>) hashmap).get("FORMAT_TYPE")
	 * .equalsIgnoreCase("S")) .collect(Collectors.toList()); if (finalDatalst !=
	 * null && !finalDatalst.isEmpty()) vObject.setGridDataSet(finalDatalst);
	 * 
	 * gridReportTotalRow = (int) exceptionCode.getRequest();
	 * vObject.setTotalRows(gridReportTotalRow); if (vObject.getCurrentPage() == 1)
	 * { List<ReportsVb> sumStringLst = new ArrayList<>(); StringJoiner sumString =
	 * new StringJoiner(","); vObject.getColumnHeaderslst().forEach(colHeadersVb ->
	 * { if (!"T".equalsIgnoreCase(colHeadersVb.getColType()) &&
	 * (colHeadersVb.getColspan() == 0 || colHeadersVb.getColspan() == 1) &&
	 * "Y".equalsIgnoreCase(colHeadersVb.getSumFlag())) { sumString.add("SUM(" +
	 * colHeadersVb.getDbColumnName() + ") " + colHeadersVb.getDbColumnName()); }
	 * }); ExceptionCode exceptionCode1 = new ExceptionCode(); String query = null;
	 * if (sumString.length() > 0) { query = "SELECT " + sumString.toString() +
	 * ",'S' FORMAT_TYPE FROM (" + vObject.getFinalExeQuery() + ") TOT ";
	 * exceptionCode1 = commonApiDao.getCommonResultDataQuery(query); if
	 * (exceptionCode1.getResponse() != null) { sumStringLst = (List<ReportsVb>)
	 * exceptionCode1.getResponse(); vObject.setTotal(sumStringLst); } } else { //
	 * Filter the Data which contains Format Type FT List<Map<String, Object>>
	 * totallst = (List<Map<String, Object>>) datalst.stream() .filter(hashmap ->
	 * ((HashMap<String, String>) hashmap).containsKey("FORMAT_TYPE"))
	 * .filter(hashmap -> ((HashMap<String, String>) hashmap).get("FORMAT_TYPE")
	 * .equalsIgnoreCase("S")) .collect(Collectors.toList());
	 * vObject.setTotal(totallst); } } } else { exceptionCode.setResponse(vObject);
	 * return exceptionCode; } } else if
	 * ("C".equalsIgnoreCase(vObject.getObjectType())) { exceptionCode =
	 * reportsDao.getChartReportData(vObject, vObject.getFinalExeQuery()); if
	 * (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
	 * vObject.setChartData(exceptionCode.getResponse().toString()); } }
	 * exceptionCode.setResponse(vObject);
	 * exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	 * exceptionCode.setErrorMsg("Success"); } catch (Exception e) {
	 * exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	 * exceptionCode.setErrorMsg(e.getMessage()); return exceptionCode; } finally {
	 * 
	 * } return exceptionCode; }
	 */
	
	public ExceptionCode getReportDetails(ReportsVb vObject) throws SQLException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ExceptionCode exceptionCodeProc = new ExceptionCode();
		PrdQueryConfig prdQueryConfig = new PrdQueryConfig();
		List<PrdQueryConfig> sqlQueryList = new ArrayList<PrdQueryConfig>();
		DeepCopy<ReportsVb> clonedObj = new DeepCopy<ReportsVb>();
		PromptTreeVb promptTreeVb = new PromptTreeVb();
		String reportType  = vObject.getReportType();
		try {
			ReportsVb subReportsVb = new ReportsVb();
			List<ReportsVb> reportDatalst = new ArrayList<ReportsVb>();
			if(!ValidationUtil.isValid(vObject.getObjectType())){
				vObject.setObjectType("G");
			}
			subReportsVb = clonedObj.copy(vObject);
			if (ValidationUtil.isValid(vObject.getSubReportId())) {
				if (ValidationUtil.isValid(subReportsVb.getDdFlag())
						&& "Y".equalsIgnoreCase(subReportsVb.getDdFlag())) {
					reportDatalst = reportsDao.getSubReportDetail(vObject);
					if (reportDatalst != null && reportDatalst.size() > 0) {
						subReportsVb = reportDatalst.get(0);
						subReportsVb.setNextLevel(reportsDao.getIntReportNextLevel(subReportsVb));
					}
				}
			}  else {
				reportDatalst = reportsDao.getSubReportDetail(vObject);
				if (reportDatalst != null && reportDatalst.size() > 0) {
					subReportsVb = reportDatalst.get(0);
					if (ValidationUtil.isValid(subReportsVb.getDdFlag())
							&& "Y".equalsIgnoreCase(subReportsVb.getDdFlag())) {
						subReportsVb.setNextLevel(reportsDao.getNextLevel(subReportsVb));
					}
				} else {
					exceptionCode.setOtherInfo(vObject);
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg(
							"Report Levels Not Maintained for the ReportId[" + vObject.getReportId() + "] !!");
					return exceptionCode;
				}
			}
			sqlQueryList = reportsDao.getSqlQuery(subReportsVb.getDataRefId());
			if (sqlQueryList != null && sqlQueryList.size() > 0) {
				prdQueryConfig = sqlQueryList.get(0);
			} else {
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Query not maintained for the Data Ref Id[" + subReportsVb.getDataRefId() + "]");
				return exceptionCode;
			}
			ArrayList<ColumnHeadersVb> colHeaders = new ArrayList<ColumnHeadersVb>();
			List<ColumnHeadersVb> columnHeadersXmllst = reportsDao.getReportColumns(subReportsVb);
			String columnHeaderXml = "";
			if((columnHeadersXmllst== null || columnHeadersXmllst.isEmpty())) {
				if("QUERY".equalsIgnoreCase(prdQueryConfig.getDataRefType())) {
					exceptionCode.setOtherInfo(vObject);
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Column Headers Not Maintained for the ReportId[" + subReportsVb.getReportId()+ "] and Sub Report Id[" + subReportsVb.getSubReportId() + "] !!");
					return exceptionCode;
				}
			}else {
				columnHeaderXml = columnHeadersXmllst.get(0).getColumnXml();
				
			}
			subReportsVb.setReportTitle(CommonUtils.getValueForXmlTag(columnHeaderXml, "OBJECT_CAPTION"));
			if ("G".equalsIgnoreCase(vObject.getObjectType())) {
				colHeaders = getColumnHeaders(columnHeaderXml);
				subReportsVb.setColumnHeaderslst(colHeaders);
			} else if("C".equalsIgnoreCase(vObject.getObjectType())) {
				subReportsVb.setColHeaderXml(columnHeaderXml);
			}
			String finalExeQuery = "";
			String maxRecords = commonDao.findVisionVariableValue("PRD_REPORT_MAXFETCH");
			subReportsVb.setMaxRecords(Integer.parseInt(ValidationUtil.isValid(maxRecords) ? maxRecords : "5000"));
			// prdQueryConfig.setQueryProc(prdQueryConfig.getQueryProc().toUpperCase());
			String queryTmp = prdQueryConfig.getQueryProc();
			queryTmp = queryTmp.toUpperCase();
			if ("QUERY".equalsIgnoreCase(prdQueryConfig.getDataRefType())) {
				if ("G".equalsIgnoreCase(vObject.getObjectType())) {
					if (queryTmp.contains("ORDER BY")) {
						String orderBy = queryTmp.substring(queryTmp.indexOf("ORDER BY"), queryTmp.length());
						prdQueryConfig
								.setQueryProc(prdQueryConfig.getQueryProc().substring(0, queryTmp.indexOf("ORDER BY")));
						prdQueryConfig.setSortField(orderBy);
					}
					//prdQueryConfig.setQueryProc(searchQuery);
					if (!ValidationUtil.isValid(prdQueryConfig.getSortField())) {
						exceptionCode.setOtherInfo(vObject);
						exceptionCode.setErrorMsg(
								"ORDER BY is mandatory in Query for [" + subReportsVb.getDataRefId() + "] !!");
						return exceptionCode;
					}
				}
				finalExeQuery = replacePromptVariables(prdQueryConfig.getQueryProc(), vObject);
			} else if ("PROCEDURE".equalsIgnoreCase(prdQueryConfig.getDataRefType())) {
				vObject.setSubReportId(subReportsVb.getSubReportId());
				if(vObject.getPromptTree() == null) {
					exceptionCodeProc = reportsDao.callProcforReportData(vObject, prdQueryConfig.getQueryProc());
				}else {
					exceptionCodeProc.setResponse(vObject.getPromptTree());
					exceptionCodeProc.setErrorCode(Constants.STATUS_ZERO);
				}
				if (exceptionCodeProc.getErrorCode() == Constants.STATUS_ZERO) {
					promptTreeVb = (PromptTreeVb)exceptionCodeProc.getResponse();
					if (ValidationUtil.isValid(promptTreeVb.getTableName())) {
						if("REPORTS_STG".equalsIgnoreCase(promptTreeVb.getTableName().toUpperCase())) {
							finalExeQuery = "SELECT * FROM " + promptTreeVb.getTableName() + " WHERE SESSION_ID='"+promptTreeVb.getSessionId()+"' AND REPORT_ID='"+promptTreeVb.getReportId()+"' ";
						}else {
							finalExeQuery = "SELECT * FROM " + promptTreeVb.getTableName() + " ";
						}
						prdQueryConfig.setSortField("ORDER BY SORT_FIELD");
						if(ValidationUtil.isValid(promptTreeVb.getColumnHeaderTable())) {
							colHeaders = (ArrayList<ColumnHeadersVb>)reportsDao.getColumnHeaderFromTable(promptTreeVb);
							if(colHeaders != null && !colHeaders.isEmpty()) {
								subReportsVb.setColumnHeaderslst(colHeaders);
							}
						}
					if(!"CB".equalsIgnoreCase(reportType))	{
						if(subReportsVb.getColumnHeaderslst() == null || subReportsVb.getColumnHeaderslst().isEmpty()) {
							exceptionCode.setOtherInfo(vObject);
							exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
							exceptionCode.setErrorMsg("Column Headers Not Maintained for the ReportId[" + subReportsVb.getReportId()+ "] and Sub Report Id[" + subReportsVb.getSubReportId() + "] !!");
							return exceptionCode;
						}
					}
						subReportsVb.setPromptTree(promptTreeVb);
					} else {
						exceptionCode.setOtherInfo(vObject);
						exceptionCode.setErrorMsg("Output Table not return from Procedure but the Procedure return Success Status");
						return exceptionCode;
					}
				}else {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg(exceptionCodeProc.getErrorMsg());
					exceptionCode.setOtherInfo(vObject);
					return exceptionCode;
				}
			}
			if (ValidationUtil.isValid(vObject.getScreenSortColumn())) {
				prdQueryConfig.setSortField(vObject.getScreenSortColumn());
			}
			if (vObject.getSmartSearchOpt() != null && vObject.getSmartSearchOpt().size() > 0) {
				finalExeQuery = "SELECT * FROM ( " + finalExeQuery + " ) TEMP WHERE ";
				for (int len = 0; len < vObject.getSmartSearchOpt().size(); len++) {
					SmartSearchVb data = vObject.getSmartSearchOpt().get(len);
					String searchVal = CommonUtils.criteriaBasedVal(data.getCriteria(),data.getValue());
					if (len > 0) 
						finalExeQuery = finalExeQuery + " AND ";
					if("MSSQL".equalsIgnoreCase(databaseType)) {
						if("GREATER".equalsIgnoreCase(data.getCriteria()) || "GREATEREQUALS".equalsIgnoreCase(data.getCriteria())
								|| "LESSER".equalsIgnoreCase(data.getCriteria()) || "LESSEREQUALS".equalsIgnoreCase(data.getCriteria()))
							finalExeQuery = finalExeQuery + "(" + data.getObject() + ") "+searchVal;
						else
							finalExeQuery = finalExeQuery + " UPPER(" + data.getObject() + ") "+searchVal;
					}else {
						finalExeQuery = finalExeQuery + " UPPER(" + data.getObject() + ") "+searchVal;
					}
				}
			}
			subReportsVb.setFinalExeQuery(finalExeQuery);
			subReportsVb.setSortField(prdQueryConfig.getSortField());
			subReportsVb.setObjectType(vObject.getObjectType());
			subReportsVb.setPromptLabel(vObject.getPromptLabel());
			subReportsVb.setDrillDownLabel(vObject.getDrillDownLabel());
			subReportsVb.setCurrentPage(vObject.getCurrentPage());
			exceptionCode.setResponse(subReportsVb);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Success");
			return exceptionCode;
		} catch (Exception ex) {
			logger.error("Exception while getting Report Data[" + vObject.getReportId() + "]SubSeq["+ vObject.getNextLevel() + "]...!!");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(ex.getMessage());
			return exceptionCode;
		}
	}
	public String replacePromptVariables(String reportQuery, ReportsVb promptsVb) {
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_1#", promptsVb.getPromptValue1());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_2#", promptsVb.getPromptValue2());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_3#", promptsVb.getPromptValue3());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_4#", promptsVb.getPromptValue4());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_5#", promptsVb.getPromptValue5());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_6#", promptsVb.getPromptValue6());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_7#", promptsVb.getPromptValue7());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_8#", promptsVb.getPromptValue8());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_9#", promptsVb.getPromptValue9());
		reportQuery = reportQuery.replaceAll("#PROMPT_VALUE_10#", promptsVb.getPromptValue10());
		reportQuery = reportQuery.replaceAll("#DDKEY1#", promptsVb.getDrillDownKey1());
		reportQuery = reportQuery.replaceAll("#DDKEY2#", promptsVb.getDrillDownKey2());
		reportQuery = reportQuery.replaceAll("#DDKEY3#", promptsVb.getDrillDownKey3());
		reportQuery = reportQuery.replaceAll("#DDKEY4#", promptsVb.getDrillDownKey4());
		reportQuery = reportQuery.replaceAll("#DDKEY5#", promptsVb.getDrillDownKey5());
		reportQuery = reportQuery.replaceAll("#DDKEY6#", promptsVb.getDrillDownKey6());
		reportQuery = reportQuery.replaceAll("#DDKEY7#", promptsVb.getDrillDownKey7());
		reportQuery = reportQuery.replaceAll("#DDKEY8#", promptsVb.getDrillDownKey8());
		reportQuery = reportQuery.replaceAll("#DDKEY9#", promptsVb.getDrillDownKey9());
		reportQuery = reportQuery.replaceAll("#DDKEY10#", promptsVb.getDrillDownKey10());
		reportQuery = reportQuery.replaceAll("#DDKEY1D#", promptsVb.getDrillDownKey1d());
		reportQuery = reportQuery.replaceAll("#VISION_ID#", "" + SessionContextHolder.getContext().getVisionId());
		if(promptsVb.getReportId().contains("MPR") && ValidationUtil.isValid(promptsVb.getPromptValue6())) {
			reportQuery = reportQuery.replaceAll("#PYM#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"PYM"));
			reportQuery = reportQuery.replaceAll("#NYM#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"NYM"));
			reportQuery = reportQuery.replaceAll("#PM#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"PM"));
			reportQuery = reportQuery.replaceAll("#NM#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"NM"));
			reportQuery = reportQuery.replaceAll("#CM#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"CM"));
			reportQuery = reportQuery.replaceAll("#CY#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"CY"));
			reportQuery = reportQuery.replaceAll("#PY#", reportsDao.getDateFormat(promptsVb.getPromptValue6(),"PY"));
		}
		return reportQuery;
	}
	
	public ExceptionCode getIntReportsDetail(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<ReportsVb> reportsLst = new ArrayList<>();
		try {
			reportsLst = reportsDao.getReportsDetail(vObject);
			if(reportsLst!= null && !reportsLst.isEmpty()) {
				reportsLst.forEach(reportDet  -> {
					reportDet.setNextLevel(reportsDao.getNextLevel(reportDet));
					List<ColumnHeadersVb> colHeadersLst =  new ArrayList<ColumnHeadersVb>();
					colHeadersLst = reportsDao.getReportColumns(reportDet);
					if(colHeadersLst != null && colHeadersLst.size()>0) {
						reportDet.setReportTitle(CommonUtils.getValueForXmlTag(colHeadersLst.get(0).getColumnXml(), "OBJECT_CAPTION"));
						reportDet.setColumnHeaderslst(colHeadersLst);
					}
				});
			}else {
				exceptionCode.setErrorMsg("Report Details not found!!");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(reportsLst);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}


	public ArrayList<ColumnHeadersVb> getColumnHeaders(String colHeadersXml) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ArrayList<ColumnHeadersVb> colHeaders = new ArrayList<ColumnHeadersVb>();
		try {
			if (ValidationUtil.isValid(colHeadersXml)) {
				colHeadersXml = ValidationUtil.isValid(colHeadersXml)?colHeadersXml.replaceAll("\n", "").replaceAll("\r", ""): "";
				String colDetXml = CommonUtils.getValueForXmlTag(colHeadersXml, "COLUMNS");
				int colCount = 0 ;
				colCount = Integer.parseInt(CommonUtils.getValueForXmlTag(colDetXml, "COLUMN_COUNT"));
				for (int i = 1; i <= colCount; i++) {
					ColumnHeadersVb colHeadersVb = new ColumnHeadersVb();
					String refXml = CommonUtils.getValueForXmlTag(colDetXml, "COLUMN" + i);
					if (!ValidationUtil.isValid(refXml))
						continue;
					if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "LABLE_ROW_NUM")))
						colHeadersVb.setLabelRowNum(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "LABLE_ROW_NUM")));

					if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "LABLE_COL_NUM")))
						colHeadersVb.setLabelColNum(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "LABLE_COL_NUM")));

					colHeadersVb.setCaption(CommonUtils.getValueForXmlTag(refXml, "CAPTION"));
					colHeadersVb.setColType(CommonUtils.getValueForXmlTag(refXml, "COL_TYPE"));

					if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "ROW_SPAN")))
						colHeadersVb.setRowspan(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "ROW_SPAN")));

					if (ValidationUtil.isValid(CommonUtils.getValueForXmlTag(refXml, "COL_SPAN")))
						colHeadersVb.setColspan(Integer.parseInt(CommonUtils.getValueForXmlTag(refXml, "COL_SPAN")));

					colHeadersVb.setDbColumnName(CommonUtils.getValueForXmlTag(refXml, "SOURCE_COLUMN"));
					String drillDownLabel = CommonUtils.getValueForXmlTag(refXml, "DRILLDOWN_LABEL_FLAG");
					if (ValidationUtil.isValid(drillDownLabel) && "Y".equalsIgnoreCase(drillDownLabel)) {
						colHeadersVb.setDrillDownLabel(true);
					} else {
						colHeadersVb.setDrillDownLabel(false);
					}
					colHeadersVb.setScaling(CommonUtils.getValueForXmlTag(refXml, "SCALING"));
					colHeadersVb.setDecimalCnt(CommonUtils.getValueForXmlTag(refXml, "DECIMALCNT"));
					colHeadersVb.setColumnWidth(CommonUtils.getValueForXmlTag(refXml, "COLUMN_WIDTH"));
					colHeadersVb.setGroupingFlag(CommonUtils.getValueForXmlTag(refXml, "GROUPING_FLAG"));
					/*String groupingFlag = CommonUtils.getValueForXmlTag(refXml, "GROUPING_FLAG");
					if (ValidationUtil.isValid(groupingFlag) && "Y".equalsIgnoreCase(groupingFlag)) {
						colHeadersVb.setGroupingFlag(true);
					} else {
						colHeadersVb.setGroupingFlag(false);
					}*/
					String sumFlag = CommonUtils.getValueForXmlTag(refXml, "SUM_FLAG");
					if (ValidationUtil.isValid(sumFlag) && "N".equalsIgnoreCase(sumFlag)) {
						colHeadersVb.setSumFlag(sumFlag);
					} else {
						colHeadersVb.setSumFlag("Y");
					}
					colHeaders.add(colHeadersVb);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return colHeaders;
	}
public ExceptionCode exportToXls(ReportsVb vObject,int currentUserId,String workBookCnt){
	ExceptionCode exceptionCode = new ExceptionCode();
	ReportsVb reportsVb = new ReportsVb();
	int  rowNum = 5;
	try{
		String reportTitle = "";
		reportTitle = vObject.getReportTitle();
		String screenGroupColumn = vObject.getScreenGroupColumn();
		String screenSortColumn = vObject.getScreenSortColumn();
		String[] hiddenColumns = null;
		if(ValidationUtil.isValid(vObject.getColumnsToHide())) {
			hiddenColumns = vObject.getColumnsToHide().split("!@#");
		}
		exceptionCode = getReportDetails(vObject);
		if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
			if (ValidationUtil.isValid(exceptionCode.getResponse())) {
				vObject = (ReportsVb) exceptionCode.getResponse();
			}
		}else {
			return exceptionCode;
		}
		List<ColumnHeadersVb> colHeaderslst = vObject.getColumnHeaderslst();
		List<ColumnHeadersVb> updatedLst = vObject.getColumnHeaderslst();
		if(hiddenColumns != null) {
			for(int ctr=0;ctr < hiddenColumns.length;ctr++) {
				updatedLst = formColumnHeader(updatedLst, hiddenColumns[ctr]);
			}
		}
		if(updatedLst != null && updatedLst.size()>0) {
			int finalMaxRow = updatedLst.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
			for(ColumnHeadersVb colObj : updatedLst) {
				if(colObj.getRowspan() > finalMaxRow) {
					colObj.setRowspan(colObj.getRowspan() - finalMaxRow);
				}
			}
			colHeaderslst = updatedLst;
		}
			
		List<String> colTypes = new ArrayList<String>();
		Map<Integer,Integer> columnWidths = new HashMap<Integer,Integer>(colHeaderslst.size());
		List<ReportStgVb> reportsStgs = new ArrayList<ReportStgVb>();
		for(int loopCnt= 0; loopCnt < colHeaderslst.size(); loopCnt++){
			columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
			ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
			if(colHVb.getColspan() <= 1 && colHVb.getNumericColumnNo() != 99) {
				colTypes.add(colHVb.getColType());
			}
		}
		int headerCnt  = 0;
		
		String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
		//String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
		//assetFolderUrl = "E:\\RA_Image\\";
		vObject.setReportTitle(reportTitle);
		vObject.setMaker(currentUserId);
		if(ValidationUtil.isValid(screenGroupColumn))
			vObject.setScreenGroupColumn(screenGroupColumn);
		if(ValidationUtil.isValid(screenSortColumn))
			vObject.setScreenSortColumn(screenSortColumn);
		getScreenDao().fetchMakerVerifierNames(vObject);	
		//ExcelExportUtil.createPrompts(vObject, new ArrayList(), sheet, workBook, assetFolderUrl, styls, headerCnt);
		 boolean createHeadersAndFooters = true;
		//Excel Report Header
		///rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,columnWidths);
	    logger.info("Excel Export Data Extraction Begin["+vObject.getReportId()+":"+vObject.getSubReportId()+"]");
		exceptionCode = reportsDao.getResultData(vObject,true);
		logger.info("Excel Export Data Extraction End["+ vObject.getReportId()+":"+vObject.getSubReportId()+"]");
		List<HashMap<String, String>> dataLst = null;
		List<HashMap<String, String>> totalLst = null;
		ReportsVb resultVb = new ReportsVb();
		if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
			if (ValidationUtil.isValid(exceptionCode.getResponse())) {
				resultVb = (ReportsVb) exceptionCode.getResponse();
				dataLst = resultVb.getGridDataSet();
				totalLst = resultVb.getTotal();
			}
		}
		logger.info("Excel Export Data Write Begin["+ vObject.getReportId()+":"+vObject.getSubReportId()+"]");
		SXSSFWorkbook workBook = new SXSSFWorkbook(500);
		SXSSFSheet sheet =(SXSSFSheet) workBook.createSheet(vObject.getReportTitle());
		//SXSSFSheet sheet = (SXSSFSheet) workBook.getSheet(vObject.getReportTitle());
		Map<Integer, XSSFCellStyle>  styls = ExcelExportUtil.createStyles(workBook);
		ExcelExportUtil.createPrompts(vObject, sheet, workBook, assetFolderUrl, styls, headerCnt);
		
		int ctr = 1;
		int sheetCnt = 2;
		do {
				if ((rowNum + vObject.getMaxRecords()) > SpreadsheetVersion.EXCEL2007.getMaxRows()) {
					rowNum = 0;
					sheet = (SXSSFSheet) workBook.createSheet(""+sheetCnt);
					sheetCnt++;
					createHeadersAndFooters = true;
				}
				if(createHeadersAndFooters){
					rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,columnWidths);
					sheet.createFreezePane(0, rowNum);
				}
				createHeadersAndFooters = false;
				//writing data into excel
			ctr++;
			rowNum = ExcelExportUtil.writeReportDataRA(vObject, colHeaderslst, dataLst, sheet, rowNum, styls, colTypes, columnWidths,false);
			vObject.setCurrentPage(ctr);
			dataLst = new ArrayList();
			exceptionCode = reportsDao.getResultData(vObject, true);
			if (ValidationUtil.isValid(exceptionCode.getResponse())) {
				resultVb = (ReportsVb) exceptionCode.getResponse();
				dataLst = resultVb.getGridDataSet();
			}
		} while (dataLst != null && !dataLst.isEmpty());
		
		if(totalLst != null)
			rowNum = ExcelExportUtil.writeReportDataRA(vObject, colHeaderslst, totalLst, sheet, rowNum, styls, colTypes, columnWidths,true);
		
		
		headerCnt = colTypes.size();
		int noOfSheets = workBook.getNumberOfSheets();
		for(int a =0 ;a < noOfSheets;a++) {
			sheet = (SXSSFSheet) workBook.getSheetAt(a);
			int loopCount = 0;
			for(loopCount=0; loopCount<headerCnt;loopCount++){
				sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
			}
		}
		String filePath = System.getProperty("java.io.tmpdir");
		if(!ValidationUtil.isValid(filePath)){
			filePath = System.getenv("TMP");
		}
		if(ValidationUtil.isValid(filePath)){
			filePath = filePath + File.separator;
		}
		File lFile = new File(filePath+ValidationUtil.encode(reportTitle)+"_"+currentUserId+".xlsx");
		if(lFile.exists()){
			lFile.delete();
		}
		lFile.createNewFile();
		FileOutputStream fileOS = new FileOutputStream(lFile);
		workBook.write(fileOS);
		fileOS.flush();
		fileOS.close();
		logger.info("Excel Export Data Write End["+vObject.getReportId()+":"+vObject.getSubReportId()+"]");
		exceptionCode.setResponse(filePath);
		exceptionCode.setOtherInfo(vObject.getReportTitle()+"_"+currentUserId);
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
	}catch (Exception e) {
		e.printStackTrace();
		logger.error("Report Export Excel Exception at: " + vObject.getReportId()+" : "+vObject.getReportTitle());
		exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
	}
	return exceptionCode;
}
	public ExceptionCode exportToPdf(int currentUserId,ReportsVb reportsVb){
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String reportOrientation = reportsVb.getReportOrientation();
			String reportTitle = reportsVb.getReportTitle();
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			//String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
			String screenGroupColumn = reportsVb.getScreenGroupColumn();
			String screenSortColumn = reportsVb.getScreenSortColumn();
			String[] hiddenColumns = null;
			if(ValidationUtil.isValid(reportsVb.getColumnsToHide())) {
				hiddenColumns = reportsVb.getColumnsToHide().split("!@#");
			}
			reportsVb.setMaker(SessionContextHolder.getContext().getVisionId());
			exceptionCode = getReportDetails(reportsVb);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					reportsVb = (ReportsVb) exceptionCode.getResponse();
					//exceptionCode =  getResultData(subReportsVb);
				}
			}else {
				return exceptionCode;
			}
			List<ColumnHeadersVb> colHeaderslst = reportsVb.getColumnHeaderslst();
			ArrayList<ColumnHeadersVb> updatedLst = new ArrayList<ColumnHeadersVb>();
			if(hiddenColumns != null) {
				for(int ctr=0;ctr < hiddenColumns.length;ctr++) {
					updatedLst = formColumnHeader(colHeaderslst, hiddenColumns[ctr]);
					colHeaderslst = updatedLst;
				}
			}
			
			if(updatedLst != null && updatedLst.size()>0) {
				int finalMaxRow = updatedLst.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
				for(ColumnHeadersVb colObj : updatedLst) {
					if(colObj.getRowspan() > finalMaxRow) {
						colObj.setRowspan(colObj.getRowspan() - finalMaxRow);
					}
				}
				colHeaderslst = updatedLst;
				
			}
				
			
			List<String> colTypes = new ArrayList<String>();
			Map<Integer,Integer> columnWidths = new HashMap<Integer,Integer>(colHeaderslst.size());
			for(int loopCnt= 0; loopCnt < colHeaderslst.size(); loopCnt++){
				columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
				ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
				if(colHVb.getColspan() <= 1 && colHVb.getNumericColumnNo() != 99) {
					colTypes.add(colHVb.getColType());
				}
			}
			logger.info("Pdf Export Data Extraction Begin["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
			exceptionCode = reportsDao.getResultData(reportsVb, true);
			logger.info("Pdf Export Data Extraction End["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}
			logger.info("Pdf Export Data Write Begin["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
			/*if(!reportOrientation.equalsIgnoreCase(reportsVb.getReportOrientation())) {
				reportsVb.setPdfGrwthPercent(0);
			}*/
			reportsVb.setReportTitle(reportTitle);
			reportsVb.setMaker(currentUserId);
			reportsVb.setReportOrientation(reportOrientation);
			//Grouping on PDF
			String[] capGrpCols = null; 
			ArrayList<String> groupingCols = new ArrayList<String>();
			ArrayList<ColumnHeadersVb> columnHeadersFinallst = new ArrayList<ColumnHeadersVb>();
			colHeaderslst.forEach(colHeadersVb -> {
				if(colHeadersVb.getColspan() <= 1 && colHeadersVb.getNumericColumnNo() != 99) {
					columnHeadersFinallst.add(colHeadersVb);
				}
			});
			if(ValidationUtil.isValid(screenGroupColumn)) {
				reportsVb.setPdfGroupColumn(screenGroupColumn);
			}
			if(ValidationUtil.isValid(reportsVb.getPdfGroupColumn()))
				capGrpCols = reportsVb.getPdfGroupColumn().split("!@#");
			
			if(reportsVb.getTotalRows() <= reportsVb.getMaxRecords() && capGrpCols != null && capGrpCols.length > 0) {
				for(String grpStr : capGrpCols) {
					for(ColumnHeadersVb colHeader : columnHeadersFinallst) {
						if(grpStr.equalsIgnoreCase(colHeader.getCaption().toUpperCase())) {
							groupingCols.add(colHeader.getDbColumnName());
							break;
						}
					}
				}
			}
			final String[] grpColNames =  capGrpCols;
			Map<String, List < HashMap<String, String> >> groupingMap = new HashMap<String, List < HashMap<String, String> >>();
			if(reportsVb.getTotalRows() <= reportsVb.getMaxRecords() && (groupingCols != null && groupingCols.size() > 0)) {
				switch(groupingCols.size()) {
					case 1:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
								));
						break;
					case 2:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
								));
						break;
					case 3:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))
										+" >> "+m.get(groupingCols.get(2))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(2))
								));
						break;
					case 4:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))
										+" >> "+m.get(groupingCols.get(2))
										+" >> "+m.get(groupingCols.get(3))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(2))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(3))
								));
						break;
					case 5:
						groupingMap = dataLst.stream().collect(
								Collectors.groupingBy(
								m -> (m.get(groupingCols.get(0))
										+" >> "+m.get(groupingCols.get(1))
										+" >> "+m.get(groupingCols.get(2))
										+" >> "+m.get(groupingCols.get(3))
										+" >> "+m.get(groupingCols.get(4))) == null ? 
										"" :grpColNames[0]+": " + m.get(groupingCols.get(0))
										+" >> "+grpColNames[1]+": " +m.get(groupingCols.get(1))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(2))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(3))
										+" >> "+grpColNames[2]+": " +m.get(groupingCols.get(4))
								));
						break;
				}
				Map<String, List < HashMap<String, String> >> sortedMap = new TreeMap<String, List < HashMap<String, String> >>();
				if (ValidationUtil.isValid(screenSortColumn)) {
					if (screenSortColumn.contains(groupingCols.get(0))) {
						String value = screenSortColumn.substring(9, screenSortColumn.length()).toUpperCase();
						String[] col = value.split(",");
						for (int i = 0; i < col.length; i++) {
							if (col[i].contains(groupingCols.get(0))) {
								String val = col[i];
								if (val.contains("DESC")) {
									sortedMap = new TreeMap<String, List<HashMap<String, String>>>(Collections.reverseOrder());
									sortedMap.putAll(groupingMap);
								} else {
									sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
								}
							}
						}
					} else {
						sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
					}
				} else {
					sortedMap = new TreeMap<String, List<HashMap<String, String>>>(groupingMap);
				}
					
					exceptionCode = pdfExportUtil.exportToPdfRAWithGroup(colHeaderslst, dataLst, reportsVb, assetFolderUrl,colTypes,currentUserId,totalLst,sortedMap,columnHeadersFinallst);
			}else {
					exceptionCode = pdfExportUtil.exportToPdfRA(colHeaderslst, dataLst, reportsVb, assetFolderUrl,colTypes,currentUserId,totalLst,columnHeadersFinallst);
			}
			logger.info("Pdf Export Data Write End["+reportsVb.getReportId()+":"+reportsVb.getSubReportId()+"]");
		}catch(Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}
	public ExceptionCode exportMultiExcel(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
			ReportsVb reportVb = new ReportsVb();
			exceptionCode = getIntReportsDetail(vObject);
			List<ReportsVb> detailReportlst = (ArrayList<ReportsVb>)exceptionCode.getResponse();
			List<DataFetcher> threads = new ArrayList<DataFetcher>(detailReportlst.size());
			detailReportlst.forEach(reportsVb -> {
				reportsVb.setPromptValue1(vObject.getPromptValue1());
				reportsVb.setPromptValue2(vObject.getPromptValue2());
				reportsVb.setPromptValue3(vObject.getPromptValue3());
				reportsVb.setPromptValue4(vObject.getPromptValue4());
				reportsVb.setPromptValue5(vObject.getPromptValue5());
				reportsVb.setPromptValue6(vObject.getPromptValue6());
				reportsVb.setPromptValue7(vObject.getPromptValue7());
				reportsVb.setPromptValue8(vObject.getPromptValue8());
				reportsVb.setPromptValue9(vObject.getPromptValue9());
				reportsVb.setPromptValue10(vObject.getPromptValue10());
				reportsVb.getReportTitle();
				DataFetcher fetcher = new DataFetcher(reportsVb,resultMap);
				fetcher.setDaemon(true);
				fetcher.start();
				try {
						fetcher.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.add(fetcher);
				
			});
			for(DataFetcher df:threads){
				int count = 0;
				if(!df.dataFetched){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count++;
					if(count > 150){
						count = 0;
						logger.info("Data fetch in progress for the report :"+ df.toString());
						continue;
					}
				}
			}
			exceptionCode = exportMultiXls(vObject,resultMap);
		}catch(Exception e) {
			
		}
		return exceptionCode;
	}
	
	class DataFetcher extends Thread {
		boolean dataFetched = false;
		boolean errorOccured = false;
		String errorMsg = "";
		ExceptionCode exceptionCode;
		ReportsVb dObj = new ReportsVb();
		HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
		public DataFetcher(ReportsVb reportsVb,HashMap<String,ExceptionCode> resultMap){
			this.dObj = reportsVb;
			this.resultMap = resultMap;
		}
		public void run() {
			try{
				
				ExceptionCode exceptionCode = getReportDetails(dObj);
				if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					if (ValidationUtil.isValid(exceptionCode.getResponse())) {
						ReportsVb subReportsVb = (ReportsVb) exceptionCode.getResponse();
						exceptionCode = reportsDao.getResultData(subReportsVb, false);
						exceptionCode.setRequest(dObj.getReportTitle());
					}
				}
				resultMap.put(dObj.getCurrentLevel(), exceptionCode);
			}catch(RuntimeCustomException rex){
				dataFetched = true;
				errorOccured = true;
				exceptionCode = rex.getCode();
			}catch(Exception e){
				dataFetched = true;
				errorOccured = true;
				errorMsg = e.getMessage();
			}
		}
	}
	public ExceptionCode exportMultiXls(ReportsVb vObject,HashMap<String,ExceptionCode> resultMap){
		ExceptionCode exceptionCode = new ExceptionCode();
		ReportsVb reportsVb = new ReportsVb();
		try{
			VisionUsersVb visionUsersVb =SessionContextHolder.getContext();
			int currentUserId = visionUsersVb.getVisionId();
			SXSSFWorkbook workBook = new SXSSFWorkbook(500);
			SXSSFSheet sheet = null; 
			String filePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(filePath)){
				filePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(filePath)){
				filePath = filePath + File.separator;
			}
			File lFile = new File(filePath+ValidationUtil.encode(vObject.getReportTitle())+"_"+currentUserId+".xlsx");
			if(lFile.exists())
				lFile.delete();
			lFile.createNewFile();
			List<HashMap<String, String>> dataLst = new ArrayList<>();
			List<HashMap<String, String>> totalLst = new ArrayList<>();
			List<ColumnHeadersVb> colHeaderslst = new ArrayList<>();
			ReportsVb reportsStgs =  null;
			Map<String, ExceptionCode> sortedMap = resultMap.entrySet().stream()
		            .sorted(Map.Entry.comparingByKey())
		            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
		                    (oldValue, newValue) -> oldValue, HashMap::new));
			for (Map.Entry<String, ExceptionCode> entry : sortedMap.entrySet()) {
				ExceptionCode resultDataException = (ExceptionCode) entry.getValue();
					reportsStgs = (ReportsVb) resultDataException.getResponse();
					String reportTitle = (String)resultDataException.getRequest();
					vObject.setScreenName(reportTitle);
				if(reportsStgs != null)	{
					dataLst = reportsStgs.getGridDataSet();
					totalLst = reportsStgs.getTotal();
					colHeaderslst = ((ReportsVb) reportsStgs).getColumnHeaderslst();
				} else {
					dataLst = new ArrayList<>();
					totalLst = new ArrayList<>();
					colHeaderslst = new ArrayList<>();
				}
				workBook.createSheet(reportTitle);
				sheet = (SXSSFSheet) workBook.getSheet(reportTitle);
				List<String> colTypes = new ArrayList<String>();
				Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>(colHeaderslst.size());
				for (int loopCnt = 0; loopCnt < colHeaderslst.size(); loopCnt++) {
					columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
					ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
					if (colHVb.getColspan() <= 1) {
						colTypes.add(colHVb.getColType());
					}
				}
				int headerCnt = 0;
				logger.info(
						"Report Export Excel Starts at: " + vObject.getReportId() + " : " + vObject.getReportTitle());
				String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
				//String assetFolderUrl = commonDao.findVisionVariableValue("MDM_IMAGE_PATH");
				vObject.setMaker(currentUserId);
				getScreenDao().fetchMakerVerifierNames(vObject);
				boolean createHeadersAndFooters = true;
				Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workBook);
				ExcelExportUtil.createPrompts(vObject, sheet, workBook, assetFolderUrl, styls, headerCnt);
				int  rowNum = 5;
				rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,
						columnWidths);
				rowNum = ExcelExportUtil.writeReportDataRA(vObject, colHeaderslst, dataLst, sheet, rowNum, styls,
						colTypes, columnWidths, false);
				if (totalLst != null)
					rowNum = ExcelExportUtil.writeReportDataRA(vObject, colHeaderslst, totalLst, sheet, rowNum, styls,
							colTypes, columnWidths, true);

				headerCnt = colTypes.size();
				int loopCount = 0;
				for (loopCount = 0; loopCount < headerCnt; loopCount++) {
					sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
				}
			}
			FileOutputStream fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
			logger.info("Report Export Excel End at: " + vObject.getReportId()+" : "+vObject.getReportTitle());
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle()+"_"+currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Report Export Excel Exception at: " + vObject.getReportId()+" : "+vObject.getReportTitle());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}
		return exceptionCode;
	}
	public ExceptionCode exportMultiPdf(ReportsVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			HashMap<String,ExceptionCode> resultMap = new HashMap<String,ExceptionCode>();
			ReportsVb reportVb = new ReportsVb();
			exceptionCode = getIntReportsDetail(vObject);
			List<ReportsVb> detailReportlst = (ArrayList<ReportsVb>)exceptionCode.getResponse();
			detailReportlst.get(0).getReportTitle();
			List<DataFetcher> threads = new ArrayList<DataFetcher>(detailReportlst.size());
			detailReportlst.forEach(reportsVb -> {
				reportsVb.setPromptValue1(vObject.getPromptValue1());
				reportsVb.setPromptValue2(vObject.getPromptValue2());
				reportsVb.setPromptValue3(vObject.getPromptValue3());
				reportsVb.setPromptValue4(vObject.getPromptValue4());
				reportsVb.setPromptValue5(vObject.getPromptValue5());
				reportsVb.setPromptValue6(vObject.getPromptValue6());
				reportsVb.setPromptValue7(vObject.getPromptValue7());
				reportsVb.setPromptValue8(vObject.getPromptValue8());
				reportsVb.setPromptValue9(vObject.getPromptValue9());
				reportsVb.setPromptValue10(vObject.getPromptValue10());
				reportsVb.getReportTitle();
				DataFetcher fetcher = new DataFetcher(reportsVb,resultMap);
				fetcher.setDaemon(true);
				fetcher.start();
				try {
						fetcher.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.add(fetcher);
				
			});
			for(DataFetcher df:threads){
				int count = 0;
				if(!df.dataFetched){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					count++;
					if(count > 150){
						count = 0;
						logger.info("Data fetch in progress for the report :"+ df.toString());
						continue;
					}
				}
			}
			exceptionCode = pdfExportUtil.exportMultiReportPdf(vObject,resultMap,assetFolderUrl);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return exceptionCode;
	}
	public List<PromptTreeVb> getTreePromptData(ReportFilterVb vObject) {
		List<PromptTreeVb> promptTree = null;
		try {
			List<PrdQueryConfig> sourceQuerylst = reportsDao.getSqlQuery(vObject.getFilterSourceId());
			if (sourceQuerylst != null && sourceQuerylst.size() > 0) {
				PrdQueryConfig sourceQueryDet = sourceQuerylst.get(0);
				promptTree = reportsDao.getTreePromptData(vObject,sourceQueryDet.getQueryProc());
				promptTree = createPraentChildRelations(promptTree, vObject.getFilterString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return promptTree;
	}
	public List<PromptTreeVb> createPraentChildRelations(List<PromptTreeVb> promptTreeList, String filterString) {
		DeepCopy<PromptTreeVb> deepCopy = new DeepCopy<PromptTreeVb>();
		List<PromptTreeVb> lResult = new ArrayList<PromptTreeVb>(0);
		List<PromptTreeVb> promptTreeListCopy = new CopyOnWriteArrayList<PromptTreeVb>(deepCopy.copyCollection(promptTreeList));
		//Top Roots are added.
		for(PromptTreeVb promptVb:promptTreeListCopy){
			if(promptVb.getField1().equalsIgnoreCase(promptVb.getField3())){
				lResult.add(promptVb);
				promptTreeListCopy.remove(promptVb);
			}
		}
		//For each top node add all child's and to that child's add sub child's recursively.
		for(PromptTreeVb promptVb:lResult){
			addChilds(promptVb,promptTreeListCopy);
		}
		//Get the sub tree from the filter string if filter string is not null.
		if(ValidationUtil.isValid(filterString)){
			lResult = getSubTreeFrom(filterString, lResult);
		}
		//set the empty lists to null. this is required for UI to display the leaf nodes properly.
		nullifyEmptyList(lResult);
		return lResult;
	}
	private void addChilds(PromptTreeVb vObject, List<PromptTreeVb> promptTreeListCopy) {
		for(PromptTreeVb promptTreeVb:promptTreeListCopy){
			if(vObject.getField1().equalsIgnoreCase(promptTreeVb.getField3())){
				if(vObject.getChildren() == null){
					vObject.setChildren(new ArrayList<PromptTreeVb>(0));
				}
				vObject.getChildren().add(promptTreeVb);
				addChilds(promptTreeVb, promptTreeListCopy);
			}
		}
	}
	private List<PromptTreeVb> getSubTreeFrom(String filterString, List<PromptTreeVb> result) {
		List<PromptTreeVb> lResult = new ArrayList<PromptTreeVb>(0);
		for(PromptTreeVb promptTreeVb:result){
			if(promptTreeVb.getField1().equalsIgnoreCase(filterString)){
				lResult.add(promptTreeVb);
				return lResult;
			}else if(promptTreeVb.getChildren() != null){
				lResult = getSubTreeFrom(filterString, promptTreeVb.getChildren());
				if(lResult != null && !lResult.isEmpty()) return lResult;
			}
		}
		return lResult;
	}
	private void nullifyEmptyList(List<PromptTreeVb> lResult){
		for(PromptTreeVb promptTreeVb:lResult){
			if(promptTreeVb.getChildren() != null){
				nullifyEmptyList(promptTreeVb.getChildren());
			}
			if(promptTreeVb.getChildren() != null && promptTreeVb.getChildren().isEmpty()){
				promptTreeVb.setChildren(null);
			}
		}
	}
	public ExceptionCode createCBReport(ReportsVb reportsVb){
		ExceptionCode exceptionCode = null;
		FileOutputStream fileOS = null;
		File lfile =  null;
		File lfileRs = null;
		String fileNames = "";
		String tmpFileName = "";
		String filesNameslst[] = null;
		ReportsVb vObject = new ReportsVb();
		PromptTreeVb promptTree = null;
		String destFilePath = System.getProperty("java.io.tmpdir");
		if(!ValidationUtil.isValid(destFilePath))
			destFilePath = System.getenv("TMP");
		if(ValidationUtil.isValid(destFilePath))
			destFilePath = destFilePath + File.separator;
		try{
			exceptionCode = getReportDetails(reportsVb);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				vObject = (ReportsVb) exceptionCode.getResponse();
				if(ValidationUtil.isValid(vObject))
					promptTree = vObject.getPromptTree();
			}
			if(promptTree == null || "1".equalsIgnoreCase(promptTree.getStatus())){
				exceptionCode.setErrorMsg("No Records Found");
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
				//exceptionCode.setOtherInfo(reportsVb);
				return exceptionCode;
			}
			lfile = createTemplateFile(reportsVb);	
			lfileRs = lfile;
			OPCPackage pkg = OPCPackage.open( new FileInputStream(lfile.getAbsolutePath()));
			XSSFWorkbook workbook = new XSSFWorkbook(pkg);
			
			XSSFCellStyle cs = (XSSFCellStyle)workbook.createCellStyle();
			cs.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
//		    cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
			cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    XSSFFont font= workbook.createFont();
		    font.setColor(IndexedColors.WHITE.getIndex());
		    font.setBold(true);
		    cs.setFont(font);

		    long min = 0;
			long max = 1000;
			workbook.setForceFormulaRecalculation(true);
			List<RCReportFieldsVb> results = null;
			
			logger.info("Get CB Report Data Begin["+reportsVb.getReportId()+"]Sub Report Id["+reportsVb.getSubReportId()+"]");
			results = reportsDao.getCBKReportData(reportsVb,promptTree, min, max);
			logger.info("Get CB Report Data End["+reportsVb.getReportId()+"]Sub Report Id["+reportsVb.getSubReportId()+"]");
			
			String tabId = "";
			do{
				 for(RCReportFieldsVb result:results){
					String actualFile = lfile.getName();
					String fileName = actualFile.substring(0, actualFile.lastIndexOf("_"));
					if(ValidationUtil.isValid(result.getExcelFileName()) && !result.getExcelFileName().equalsIgnoreCase(fileName)){
						 System.out.println(result.getExcelFileName()+" : "+fileName);
						FileOutputStream fileOS1 = new FileOutputStream(lfile);
						workbook.write(fileOS1);
						lfile = new File(destFilePath+result.getExcelFileName()+"_"+SessionContextHolder.getContext().getVisionId()+".xlsx");
						String filePath = lfile.getAbsolutePath();
						filePath = filePath.substring(0, filePath.indexOf(result.getExcelFileName()));
						if(filePath.contains("temp"))
							filePath = filePath.substring(0, filePath.indexOf("temp"));
						if(!lfile.exists())
							ExcelExportUtil.createTemplateFile(lfile);
						
						if(!tmpFileName.contains(result.getExcelFileName())){
							if(!ValidationUtil.isValid(fileNames)){
								fileNames = lfile.toString();
							}else{
								fileNames = fileNames+"#"+lfile.toString();	
							}
							if(!ValidationUtil.isValid(tmpFileName)){
								tmpFileName = result.getExcelFileName();
							}else{
								tmpFileName = tmpFileName+"#"+result.getExcelFileName();
							}
						}
						pkg = OPCPackage.open( new FileInputStream(lfile.getAbsolutePath()));
						workbook = new XSSFWorkbook(pkg);
						cs = (XSSFCellStyle)workbook.createCellStyle();
						byte[] greenClr = {(byte) 0, (byte) 92, (byte) 140};
						XSSFColor greenXClor = new XSSFColor(greenClr);
						cs.setFillForegroundColor(greenXClor);
//					    cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
						cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					    font= workbook.createFont();
					    font.setColor(IndexedColors.WHITE.getIndex());
					    font.setBold(true);
					    cs.setFont(font);
					 }
					 int noOfsheets = workbook.getNumberOfSheets();
					 tabId = result.getTabelId();
					 if(Integer.parseInt(result.getTabelId()) > (noOfsheets-1)){
						workbook.createSheet(result.getTabelId());
					 }
					 Sheet sheet = workbook.getSheetAt(Integer.parseInt(result.getTabelId()));
					 Row row = null;
				     row = sheet.getRow(Integer.parseInt(result.getRowId())-1);
				     if(row == null){
				    	 row = sheet.createRow(Integer.parseInt(result.getRowId())-1);
				     }
				     Cell cell = row.getCell(Integer.parseInt(result.getColId()));
				     if(cell == null){
				    	cell = row.createCell(Integer.parseInt(result.getColId()));
				     }
				     if(cell == null || row == null){
						throw new RuntimeCustomException("Invalid Report data,Tab ID["+result.getTabelId()+"] Row Id["+Integer.parseInt(result.getRowId())+"], Col Id["+Integer.parseInt(result.getColId())+"] does not exists in template.");
					 }
					if(!ValidationUtil.isValid(result.getValue1())){
						
					}else if(("C".equalsIgnoreCase(result.getColType()) || "N".equalsIgnoreCase(result.getColType()))){
						cell.setCellValue(Double.parseDouble(result.getValue1()));
					}else if("F".equalsIgnoreCase(result.getColType())){
//						cell.setCellType(Cell.CELL_TYPE_FORMULA);
						cell.setCellType(CellType.FORMULA);
						cell.setCellFormula(result.getValue1());
					}else{
						cell.setCellValue(result.getValue1());
					}
					if(ValidationUtil.isValid(result.getSheetName())){
				    	workbook.setSheetName(Integer.parseInt(result.getTabelId()),result.getSheetName());
				    }
					if(ValidationUtil.isValid(result.getRowStyle())){
						/*byte[] greenClr = {(byte) 0, (byte) 92, (byte) 140};
						XSSFColor greenXClor = new XSSFColor(greenClr);*/
						if("FHT".equalsIgnoreCase(result.getRowStyle())){
							cell.setCellStyle(cs);
						}if("FHTF".equalsIgnoreCase(result.getRowStyle())){
							sheet.createFreezePane(0, Integer.parseInt(result.getRowId()));
							cell.setCellStyle(cs);
						}
					}
				}
				min = max;
				max += 1000;
				//System.out.println("min : "+min+" max : "+max);
				results = reportsDao.getCBKReportData(reportsVb,promptTree, min, max);
			}while(!results.isEmpty());
				fileOS = new FileOutputStream(lfile);
				workbook.write(fileOS);
			//add list of files to Zip
			String fileslst[] = fileNames.split("#");
			filesNameslst = tmpFileName.split("#");
			if(ValidationUtil.isValid(tmpFileName) && filesNameslst.length == 1){
				reportsVb.setTemplateId(filesNameslst[0]);
			}
			if(fileslst.length > 1){
				File f= new File(destFilePath+reportsVb.getReportTitle()+".zip");
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
				for(int a = 0;a < fileslst.length;a++){
					FileInputStream fis = new FileInputStream(fileslst[a]);
					File f1 = new File(fileslst[a]);
					String tmpfileName = filesNameslst[a]+".xlsx";
					ZipEntry e = new ZipEntry(""+tmpfileName);
					out.putNextEntry(e);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						out.write(bytes, 0, length);
					}
					fis.close();
					f1.delete();
				}
				out.closeEntry();
				out.close();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in CBK Report Generation", e);
			throw new RuntimeCustomException(e.getMessage());
		}finally{
			try{
				if(ValidationUtil.isValid(promptTree))
					reportsDao.callProcToCleanUpTables(promptTree);
				if(fileOS != null){
					fileOS.flush();
					fileOS.close();
					fileOS = null;
				}
			}catch (Exception ex){}
		}
		exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		//exceptionCode =  CommonUtils.getResultObject("", 1, "", "");
		String fileName = reportsVb.getTemplateId().substring(0, reportsVb.getTemplateId().indexOf(".xlsx"));
		exceptionCode.setOtherInfo(fileName+"_"+SessionContextHolder.getContext().getVisionId());
		exceptionCode.setResponse(destFilePath);
		if(filesNameslst != null && filesNameslst.length > 1){
			exceptionCode.setRequest(reportsVb.getReportTitle());
			if(lfileRs.exists()){
				lfileRs.delete();
			}
		}
		return exceptionCode;
	}
	public File createTemplateFile(ReportsVb reportsVb){
		File lfile =  null;
		FileChannel source = null;
		FileChannel destination = null;
		try {
			String fileName = reportsVb.getTemplateId();
			fileName = ValidationUtil.encode(fileName.substring(0, fileName.indexOf(".xlsx")));
			String destFilePath = System.getProperty("java.io.tmpdir");
			if(!ValidationUtil.isValid(destFilePath)){
				destFilePath = System.getenv("TMP");
			}
			if(ValidationUtil.isValid(destFilePath)){
				destFilePath = destFilePath + File.separator;
			}
			lfile = new File(destFilePath+fileName+"_"+SessionContextHolder.getContext().getVisionId()+".xlsx");
			String filePath = lfile.getAbsolutePath();
			filePath = filePath.substring(0, filePath.indexOf(fileName));
			if(filePath.contains("temp")){
				filePath = filePath.substring(0, filePath.indexOf("temp"));
			}
			if(lfile.exists()){
				lfile.delete();
			}
			String templateFilePath = commonDao.findVisionVariableValue("PRD_CB_TEMPLATE_PATH");
			File lSourceFile = new File(templateFilePath+reportsVb.getTemplateId());
			if(!lSourceFile.exists()){
				throw new RuntimeCustomException("Invalid Report Configuration, Invalid file name or file does not exists @ "+lSourceFile);
			}
			source = new RandomAccessFile(lSourceFile,"rw").getChannel();
			destination = new RandomAccessFile(lfile,"rw").getChannel();
			long position = 0;
			long count = source.size();
			source.transferTo(position, count, destination);
	     }catch(Exception e){
	    	 throw new RuntimeCustomException("Invalid Report Configuration, Invalid file name or file does not exists");
	     }finally {
	    	 if(source != null) {
	    		 try{source.close();}catch(Exception ex){}
	    	 }
	    	 if(destination != null) {
	    		 try{destination.close();}catch(Exception ex){}
	    	 }
	    	 logger.info("Template File Successfully Created");
	    }
		return lfile;
	}
	private ArrayList<ColumnHeadersVb> formColumnHeader(List<ColumnHeadersVb> orgColList,String hiddenColumn) {
		
		ArrayList<ColumnHeadersVb> updatedColList = new ArrayList<ColumnHeadersVb>();
		int maxHeaderRow = orgColList.stream().mapToInt(ColumnHeadersVb::getLabelRowNum).max().orElse(0);
		
		ColumnHeadersVb matchingObj = orgColList.stream().
				filter(p -> p.getDbColumnName().equalsIgnoreCase(hiddenColumn)).
				findAny().orElse(null);
		
		final int hiddenColNum = matchingObj.getLabelColNum();
		final int hiddenRowNum = matchingObj.getLabelRowNum();
		
		if(maxHeaderRow > 1) {
			int rowNum = hiddenRowNum-1;
			for(int rnum = rowNum;rowNum >= 1;rowNum--) {
				ColumnHeadersVb Obj = new ColumnHeadersVb();
				try {
					Obj = orgColList.stream().
							filter(p -> (p.getLabelRowNum()== rnum && p.getLabelColNum()<= hiddenColNum)).
							max(Comparator.comparingInt(ColumnHeadersVb::getLabelColNum)).
							get();
				}catch(Exception e) {
					
				}
				for(int i=0;i < orgColList.size();i++) {
					if(orgColList.get(i).equals(Obj)) {
						orgColList.get(i).setColspan(orgColList.get(i).getColspan()-1);
						orgColList.get(i).setNumericColumnNo(99);
						if(orgColList.get(i).getColspan() == 0) {
							orgColList.remove(i);
						}
					}
				}
			}
		}
		for (ColumnHeadersVb colHeaderVb : orgColList) {
			if(colHeaderVb.getLabelColNum() > hiddenColNum) {
				  colHeaderVb.setLabelColNum(colHeaderVb.getLabelColNum() - 1);
			}
			if(!colHeaderVb.equals(matchingObj))
				updatedColList.add(colHeaderVb);
			
		}
		return updatedColList;
	}
	public ExceptionCode getReportFilterSourceValue(ReportFilterVb vObject) {
		//LinkedHashMap<String, String> filterSourceVal = new LinkedHashMap<String, String>();
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if(!ValidationUtil.isValid(vObject.getDefaultValueId())) {
				vObject.setDefaultValueId(vObject.getFilterSourceId());
			}
			vObject.setDateCreation(String.valueOf(Math.abs(ThreadLocalRandom.current().nextInt())));
			List<PrdQueryConfig> sourceQuerylst = reportsDao.getSqlQuery(vObject.getDefaultValueId());
			if (sourceQuerylst != null && sourceQuerylst.size() > 0) {
				PrdQueryConfig sourceQueryDet = sourceQuerylst.get(0);
				if ("QUERY".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					sourceQueryDet.setQueryProc(replaceFilterHashVariables(sourceQueryDet.getQueryProc(), vObject));
					exceptionCode = reportsDao.getReportPromptsFilterValue(sourceQueryDet,null);
				}else if ("PROCEDURE".equalsIgnoreCase(sourceQueryDet.getDataRefType())) {
					if(sourceQueryDet.getQueryProc().contains("#")) {
						sourceQueryDet.setQueryProc(replaceFilterHashVariables(sourceQueryDet.getQueryProc(), vObject));
						exceptionCode = reportsDao.getFilterPromptWithHashVar(vObject,sourceQueryDet,"COMBO");
					}else {
						exceptionCode = reportsDao.getComboPromptData(vObject,sourceQueryDet);	
					}
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ReportsDao getReportsDao() {
		return reportsDao;
	}

	
}
