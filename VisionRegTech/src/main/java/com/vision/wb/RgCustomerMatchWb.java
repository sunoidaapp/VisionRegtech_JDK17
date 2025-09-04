package com.vision.wb;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonApiDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportsDao;
import com.vision.dao.RgBuildProcessDao;
import com.vision.dao.RgCustomerMatchDao;
import com.vision.exception.ExceptionCode;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ExcelExportUtil;
import com.vision.util.ValidationUtil;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.RgCustomerMatchVb;
import com.vision.vb.RgSourceTableMappings;
import com.vision.vb.SmartSearchVb;
import com.vision.vb.VisionUsersVb;

import jakarta.servlet.ServletContext;

@Component
public class RgCustomerMatchWb extends AbstractDynaWorkerBean<RgCustomerMatchVb> implements ServletContextAware {
	private ServletContext servletContext;

	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	@Autowired
	RgCustomerMatchDao rgCustomerMatchDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	RgBuildProcessDao rgBuildProcessDao;

	@Autowired
	CommonApiDao commonApiDao;

	@Autowired
	private ReportsDao reportsDao;

	@Override
	protected AbstractDao<RgCustomerMatchVb> getScreenDao() {
		return rgCustomerMatchDao;
	}

	@Override
	protected void setAtNtValues(RgCustomerMatchVb vObject) {
		vObject.setCustRelationStatusNt(207);
	}

	@Override
	protected void setVerifReqDeleteType(RgCustomerMatchVb vObject) {
		vObject.setStaticDelete(false);
		vObject.setVerificationRequired(false);
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {
			/*
			 * collTemp =
			 * commonDao.findVerificationRequiredAndStaticDelete("RG_CUSTOMER_MATCH");
			 * arrListLocal.add(collTemp);
			 */
			/*
			 * ArrayList<AlphaSubTabVb> countryLst =
			 * (ArrayList<AlphaSubTabVb>)commonDao.getCountry(); ArrayList ctryLeBookLst =
			 * new ArrayList(); for(AlphaSubTabVb ctryLeBook : countryLst) {
			 * ArrayList<AlphaSubTabVb> leBookLst =
			 * (ArrayList<AlphaSubTabVb>)commonDao.getLeBook(ctryLeBook.getAlphaSubTab());
			 * ctryLeBook.setChildren(leBookLst); ctryLeBookLst.add(ctryLeBook); }
			 */
//			ArrayList<AlphaSubTabVb> ctryLeBookLst = (ArrayList<AlphaSubTabVb>)rgCustomerMatchDao.getCountryLeBook();
//			arrListLocal.add(ctryLeBookLst);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			collTemp = getNumSubTabDao().findActiveNumSubTabsByNumTab(207);
			arrListLocal.add(collTemp);
			String country = "";
			String leBook = "";
			VisionUsersVb visionUsers = SessionContextHolder.getContext();
			if ("Y".equalsIgnoreCase(visionUsers.getUpdateRestriction())) {
				if (ValidationUtil.isValid(visionUsers.getCountry())) {
					country = visionUsers.getCountry();
				}
				if (ValidationUtil.isValid(visionUsers.getLeBook())) {
					leBook = visionUsers.getLeBook();
				}
			} else {
				country = commonDao.findVisionVariableValue("DEFAULT_COUNTRY");
				leBook = commonDao.findVisionVariableValue("DEFAULT_LE_BOOK");
			}
			String countryLeBook = country + "-" + leBook;
			arrListLocal.add(countryLeBook);
			collTemp = getAlphaSubTabDao().findActiveAlphaSubTabsByAlphaTab(810);
			arrListLocal.add(collTemp);
			return arrListLocal;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in getting the Page load values.", e);
			return null;
		}
	}

	public ExceptionCode updateProcessStatus(List<RgCustomerMatchVb> vObjects, String operation) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			if (vObjects != null && !vObjects.isEmpty()) {
				for (RgCustomerMatchVb vObj : vObjects) {
					if ("APPROVE".equalsIgnoreCase(operation.toUpperCase()))
						vObj.setCustRelationStatus(1);
					else
						vObj.setCustRelationStatus(2);
					int retVal = rgCustomerMatchDao.updateProcessStatus(vObj);
					if (retVal == Constants.ERRONEOUS_OPERATION) {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("Error while Performing  " + operation + " Operation");
						return exceptionCode;
					}
				}
			}
			exceptionCode.setErrorMsg("Rg Customer Match -" + operation + "- Successful");
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;

	}

	public ExceptionCode downloadCustomerMatch(RgCustomerMatchVb dObj, int currentUserId) {
		ExceptionCode exceptionCode = new ExceptionCode();
		int rowNum = 0;
		StringBuffer strBufApprove = new StringBuffer("");

		try {
			strBufApprove = new StringBuffer("SELECT * FROM (SELECT  T1.SET_NUMBER,T1.COUNTRY, T1.LE_BOOK, T1.RULE_ID, "
					+ " T1.CUSTOMER_ID, T1.RELATION_TYPE, T1.RELATION_CUSTOMER_ID, "
//					+ " T1.ULTIMATE_PARENT, "
					+ " T1.CUST_RELATION_STATUS FROM RG_CUSTOMER_RELATIONSHIPS T1 WHERE " + " T1.COUNTRY = '"
					+ dObj.getCountry() + "'  AND T1.LE_BOOK = '" + dObj.getLeBook() + "' )  TAPPR ");
			if (dObj.getSmartSearchOpt() != null && dObj.getSmartSearchOpt().size() > 0) {
				int count = 1;
				for (SmartSearchVb data : dObj.getSmartSearchOpt()) {
					if (count == dObj.getSmartSearchOpt().size()) {
						data.setJoinType("");
					} else {
						if (!ValidationUtil.isValid(data.getJoinType()) && !("AND".equalsIgnoreCase(data.getJoinType())
								|| "OR".equalsIgnoreCase(data.getJoinType()))) {
							data.setJoinType("AND");
						}
					}
					String val = CommonUtils.criteriaBasedVal(data.getCriteria(), data.getValue());
					switch (data.getObject()) {
					case "country":
						CommonUtils.addToQuerySearch(" upper(TAPPR.COUNTRY) " + val, strBufApprove, data.getJoinType());
						break;

					case "leBook":
						CommonUtils.addToQuerySearch(" upper(TAPPR.LE_BOOK) " + val, strBufApprove, data.getJoinType());
						break;

					case "ruleId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.RULE_ID) " + val, strBufApprove, data.getJoinType());
						break;

					case "setNumber":
						CommonUtils.addToQuerySearch(" upper(TAPPR.SET_NUMBER) " + val, strBufApprove,
								data.getJoinType());
						break;

					case "customerId":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_ID) " + val, strBufApprove,
								data.getJoinType());
						break;

//					case "primaryCid":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.PRIMARY_CID) " + val, strBufApprove, data.getJoinType());
//						break;
//						
//					case "ultimateParent":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.ULTIMATE_PARENT) " + val, strBufApprove, data.getJoinType());
//						break;
//					
//					case "parentCid":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.PROP_PARENT_CID) " + val, strBufApprove, data.getJoinType());
//						break;
//						
//					case "curParentCid":
//						CommonUtils.addToQuerySearch(" upper(TAPPR.CUR_PARENT_CID) " + val, strBufApprove, data.getJoinType());
//						break;

					case "customerName":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUSTOMER_NAME) " + val, strBufApprove,
								data.getJoinType());
						break;

					case "status":
						CommonUtils.addToQuerySearch(" upper(TAPPR.CUST_RELATION_STATUS) " + val, strBufApprove,
								data.getJoinType());
						break;
					default:
					}
					count++;
				}
			}

			String orderBy = "ORDER BY TO_NUMBER(SET_NUMBER)";
			ReportsVb vObject = new ReportsVb();
			vObject.setObjectType("G");
			vObject.setFinalExeQuery(strBufApprove.toString());
			vObject.setDbConnection("DEFAULT");
			vObject.setReportTitle("Customer Match");
			List<HashMap<String, String>> dataLst = null;
			List<HashMap<String, String>> totalLst = null;
			vObject.setSortField(orderBy);
			vObject.setMaxRecords(1000);
			exceptionCode = reportsDao.getResultData(vObject, true);
			ReportsVb resultVb = new ReportsVb();
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
					totalLst = resultVb.getTotal();
				}
			}
			SXSSFWorkbook workBook = new SXSSFWorkbook(500);
			String sheetName = vObject.getReportTitle().trim();
			vObject.setReportTitle(sheetName);
			List<ColumnHeadersVb> colHeaderslst = vObject.getColumnHeaderslst();
			List<String> colTypes = new ArrayList<String>();
			Map<Integer, Integer> columnWidths = new HashMap<Integer, Integer>(colHeaderslst.size());
			for (int loopCnt = 0; loopCnt < colHeaderslst.size(); loopCnt++) {
				columnWidths.put(Integer.valueOf(loopCnt), Integer.valueOf(-1));
				ColumnHeadersVb colHVb = colHeaderslst.get(loopCnt);
				if (colHVb.getColspan() <= 1 && colHVb.getNumericColumnNo() != 99) {
					colTypes.add(colHVb.getColType());
				}
			}

			SXSSFSheet sheet = (SXSSFSheet) workBook.createSheet(vObject.getReportTitle());
			boolean createHeadersAndFooters = true;
			Map<Integer, XSSFCellStyle> styls = ExcelExportUtil.createStyles(workBook);
			int ctr = 1;
			int sheetCnt = 3;
			String assetFolderUrl = servletContext.getRealPath("/WEB-INF/classes/images");
			do {
				if ((rowNum + vObject.getMaxRecords()) > SpreadsheetVersion.EXCEL2007.getMaxRows()) {
					rowNum = 0;
					sheet = (SXSSFSheet) workBook.createSheet("" + sheetCnt);
					sheetCnt++;
					createHeadersAndFooters = true;
				}
				if (createHeadersAndFooters) {
					rowNum = ExcelExportUtil.writeHeadersRA(vObject, colHeaderslst, rowNum, sheet, styls, colTypes,
							columnWidths);
					sheet.createFreezePane(vObject.getFreezeColumn(), rowNum);
				}
				createHeadersAndFooters = false;
				// writing data into excel
				ctr++;
				rowNum = ExcelExportUtil.writeReportDataRA(workBook, vObject, colHeaderslst, dataLst, sheet, rowNum,
						styls, colTypes, columnWidths, false, assetFolderUrl);
				vObject.setCurrentPage(ctr);
				dataLst = new ArrayList();
				exceptionCode = reportsDao.getResultData(vObject, true);
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					resultVb = (ReportsVb) exceptionCode.getResponse();
					dataLst = resultVb.getGridDataSet();
				}
			} while (dataLst != null && !dataLst.isEmpty());
			int headerCnt = 0;
			headerCnt = colTypes.size();
			int noOfSheets = workBook.getNumberOfSheets();
			for (int a = 1; a < noOfSheets; a++) {
				sheet = (SXSSFSheet) workBook.getSheetAt(a);
				int loopCount = 0;
				for (loopCount = 0; loopCount < headerCnt; loopCount++) {
					sheet.setColumnWidth(loopCount, columnWidths.get(loopCount));
				}
			}
			String filePath = System.getProperty("java.io.tmpdir");
			if (!ValidationUtil.isValid(filePath)) {
				filePath = System.getenv("TMP");
			}
			if (ValidationUtil.isValid(filePath)) {
				filePath = filePath + File.separator;
			}
			File lFile = new File(
					filePath + ValidationUtil.encode(vObject.getReportTitle()) + "_" + currentUserId + ".xlsx");
			if (lFile.exists()) {
				lFile.delete();
			}
			lFile.createNewFile();
			FileOutputStream fileOS = new FileOutputStream(lFile);
			workBook.write(fileOS);
			fileOS.flush();
			fileOS.close();
			exceptionCode.setResponse(filePath);
			exceptionCode.setOtherInfo(vObject.getReportTitle() + "_" + currentUserId);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode getRgCustomerMatchDetails(RgCustomerMatchVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		StringJoiner fromTableJoiner = new StringJoiner(",");
		String filterStr = " WHERE 1=1 ";
		try {
			RgCustomerMatchVb rgCustomerMatchVb = new RgCustomerMatchVb();
			rgCustomerMatchVb = rgCustomerMatchDao.getCustomerMatchDetails(vObject);
			if (ValidationUtil.isValid(rgCustomerMatchVb)) {
				exceptionCode = rgCustomerMatchDao.getRuleDetail(rgCustomerMatchVb);
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					// ArrayList<RgCustomerMatchVb> ruleDetaillst = (ArrayList<RgCustomerMatchVb>)
					// exceptionCode.getResponse();
					// String tableNames =
					// rgCustomerMatchDao.getRgSourceTableMappings(vObject.getRuleId());
					exceptionCode = (ExceptionCode) rgCustomerMatchDao.getRgSourceTableMappings(vObject.getRuleId());
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						ArrayList<RgSourceTableMappings> sourceTableMappinglst = (ArrayList<RgSourceTableMappings>) exceptionCode
								.getResponse();
						if (sourceTableMappinglst != null && sourceTableMappinglst.size() > 0) {
							for (RgSourceTableMappings sourceTableVb : sourceTableMappinglst) {
								if (ValidationUtil.isValid(sourceTableVb.getGenericCondition()))
									filterStr = filterStr + " " + sourceTableVb.getGenericCondition();
								fromTableJoiner
										.add(sourceTableVb.getSourceTable() + " " + sourceTableVb.getTableAlias());
							}
						}
					}
					String mergedColumnNames = (String) rgCustomerMatchDao
							.getRgSourceColumnMappings(vObject.getRuleId());
					String columnWithAliasName = "";
					String columnNames = "";
					String[] colNamesArr = mergedColumnNames.split("!@#");
					if (colNamesArr != null && colNamesArr.length > 0) {
						columnWithAliasName = colNamesArr[0];
						columnNames = colNamesArr[1];
					}
					StringJoiner withQuery = new StringJoiner(",");
					for (String colName : columnNames.split(",")) {
						withQuery.add("S1." + colName + commonApiDao.getDbFunction("PIPELINE") + "'!@#'"
								+ commonApiDao.getDbFunction("PIPELINE") + "S2." + colName + " " + colName);
					}
					String selectionQuery = "";
					selectionQuery = "With s1 as ( SELECT " + columnWithAliasName + ",'CUST_DET' Customer_Det FROM "
							+ fromTableJoiner.toString() + filterStr + " AND T1.CUSTOMER_ID = '"
							+ vObject.getCustomerId() + "' ), S2 as ( ";
					selectionQuery = selectionQuery + "SELECT " + columnNames + ",'MATCH_DET' Customer_Det FROM "
							+ fromTableJoiner.toString() + filterStr + " AND T1.CUSTOMER_ID = '"
							+ vObject.getRelationCustomerId() + "' )";
					selectionQuery = selectionQuery + " SELECT " + withQuery.toString() + " from S1,S2";
					selectionQuery = selectionQuery.replace("#COUNTRY#", "'" + vObject.getCountry() + "'");
					selectionQuery = selectionQuery.replace("#LE_BOOK#", "'" + vObject.getLeBook() + "'");

					exceptionCode = commonApiDao.getCommonResultDataQuery(selectionQuery);
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
						ArrayList result = (ArrayList) exceptionCode.getResponse();
						if (result != null && result.size() > 0) {
							rgCustomerMatchVb.setCustomerMatchDetailsLst(result);

						}
					}
					exceptionCode.setResponse(rgCustomerMatchVb);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				}
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode updateProcessStatusForAll(RgCustomerMatchVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<RgCustomerMatchVb> collTemp = rgCustomerMatchDao.selectRecordsForBulkOperation(vObject);
			for (RgCustomerMatchVb rgCustomerMatchVb : collTemp) {
				rgCustomerMatchVb.setCountry(vObject.getCountry());
				rgCustomerMatchVb.setLeBook(vObject.getLeBook());
				rgCustomerMatchVb.setCustRelationStatus(vObject.getCustRelationStatus());
				int retVal = rgCustomerMatchDao.updateProcessStatus(rgCustomerMatchVb);
				if (retVal == Constants.ERRONEOUS_OPERATION) {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Error while Performing  " + vObject.getActionType() + " Operation");
					return exceptionCode;
				}
			}
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			return exceptionCode;
		}
		exceptionCode.setErrorMsg("Customer Match - " + vObject.getActionType() + " - Successful ");
		return exceptionCode;
	}

	public RgCustomerMatchDao getRgCustomerMatchDao() {
		return rgCustomerMatchDao;
	}

}
