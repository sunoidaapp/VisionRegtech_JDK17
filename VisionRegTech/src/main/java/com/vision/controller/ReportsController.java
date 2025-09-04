package com.vision.controller;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.authentication.SessionContextHolder;
import com.vision.dao.ReportsDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.ReportFilterVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.VisionUsersVb;
import com.vision.wb.ReportsWb;
import com.vision.wb.VisionUploadWb;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("reports")
//@Api(value="reports" , description="Reports")
public class ReportsController {
	@Autowired
	ReportsWb reportsWb;
	@Autowired
	ReportsDao reportsDao;
	@Autowired
	VisionUploadWb visionUploadWb;
	/*-------------------------------------GET REPORT LIST BY GROUP------------------------------------------*/
	@RequestMapping(path = "/getReportMaster", method = RequestMethod.GET)
	//@ApiOperation(value = "Get Report List",notes = "Get List of Report on Group wise",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getReportMasterList(){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = reportsWb.getReportList();
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.METHOD_FAILURE);
		}	
	}
	/*-------------------------------------GET REPORT FILTERS------------------------------------------*/
	@RequestMapping(path = "/getReportFilter", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Report Filter",notes = "Get Filter for both Reports and Dashboard",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getReportFilter(@RequestBody ReportsVb vObject){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = reportsWb.reportFilterProcess(vObject.getFilterRefCode());
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.METHOD_FAILURE);
		}	
	}
	/*-------------------------------------GET CHILD DEPENDENT FILTER------------------------------------------*/
	@RequestMapping(path = "/getChildDependentFilter", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Dependency Filter Value",notes = "Get the value for Dependent filter",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getReportDependentFilter(@RequestBody ReportFilterVb vObject){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			LinkedHashMap<String,String> filterSourceVal = reportsWb.getFilterSourceValue(vObject);
			exceptionCode.setResponse(filterSourceVal);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.METHOD_FAILURE);
		}	
	}
	/*------------------------------------FETCHING REPORTS RESULTS-------------------------------*/
	@RequestMapping(path = "/getReportData", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Report Data",notes = "Get the Report Data for the specified Report Id",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getSubReportResultData(@RequestBody ReportsVb vObject) throws SQLException {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = reportsWb.getReportDetails(vObject);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (ValidationUtil.isValid(exceptionCode.getResponse())) {
					ReportsVb subReportsVb = (ReportsVb) exceptionCode.getResponse();
					exceptionCode =  reportsWb.getReportsDao().getResultData(subReportsVb,false);
				}
			}
			exceptionCode.setOtherInfo(vObject);
			reportsDao.insertReportsAudit(vObject, exceptionCode.getErrorMsg());
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*------------------------------------FETCHING REPORTS RESULTS-------------------------------*/
	@RequestMapping(path = "/getInteractiveReportsDetail", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Interactive Reports List",notes = "Get the list of Interactive Reports for the report id ",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getInteractiveReportsData(@RequestBody ReportsVb vObject) throws SQLException {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = reportsWb.getIntReportsDetail(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*------------------------------------MDM REPORTS EXCEL EXPORT-------------------------------*/
	@RequestMapping(path = "/reportExcelExport", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Excel Report",notes = "Export Report Data to Excel",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> excelExportReport(@RequestBody ReportsVb vObject,HttpServletRequest request,HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			VisionUsersVb visionUsersVb =SessionContextHolder.getContext();

			int currentUserId = visionUsersVb.getVisionId();
			ExceptionCode exceptionCode1 = reportsWb.exportToXls(vObject, currentUserId,"0");
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", ""+exceptionCode1.getOtherInfo()+".xlsx");
				request.setAttribute("filePath", exceptionCode1.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				jsonExceptionCode = new JSONExceptionCode(exceptionCode1.getErrorCode(),exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	/*------------------------------------ REPORTS PDF EXPORT-------------------------------*/
	@RequestMapping(path = "/reportPdfExport", method = RequestMethod.POST)
	//@ApiOperation(value = "Get PDF Report",notes = "Export Report Data to PDF",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pdfExportReport(@RequestBody ReportsVb vObject,HttpServletRequest request,HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			VisionUsersVb visionUsersVb =SessionContextHolder.getContext();

			int currentUserId = visionUsersVb.getVisionId();
			ExceptionCode exceptionCode1 = reportsWb.exportToPdf(currentUserId,vObject);
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "pdf");
				request.setAttribute("fileName", ""+exceptionCode1.getOtherInfo()+".pdf");
				request.setAttribute("filePath", exceptionCode1.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				jsonExceptionCode = new JSONExceptionCode(exceptionCode1.getErrorCode(),exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*------------------------------------REPORTS EXCEL EXPORT-------------------------------*/
	@RequestMapping(path = "/MultiExcel", method = RequestMethod.POST)
	//@ApiOperation(value = "Multi Excel",notes = "Export Report Data to Excel",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> exportMultiPanelExcel(@RequestBody ReportsVb vObject,HttpServletRequest request,HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode1 = reportsWb.exportMultiExcel(vObject);
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", ""+exceptionCode1.getOtherInfo()+".xlsx");
				request.setAttribute("filePath", exceptionCode1.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				jsonExceptionCode = new JSONExceptionCode(exceptionCode1.getErrorCode(),exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	/*------------------------------------ REPORTS PDF EXPORT-------------------------------*/
	@RequestMapping(path = "/MultiPdf", method = RequestMethod.POST)
	//@ApiOperation(value = "Get PDF Report",notes = "Export Report Data to PDF",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> exportMultiPanelpdf(@RequestBody ReportsVb vObject,HttpServletRequest request,HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode1 = reportsWb.exportMultiPdf(vObject);
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "pdf");
				request.setAttribute("fileName", ""+exceptionCode1.getOtherInfo()+".pdf");
				request.setAttribute("filePath", exceptionCode1.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				jsonExceptionCode = new JSONExceptionCode(exceptionCode1.getErrorCode(),exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*-------------------------------------GET TREE FILTER------------------------------------------*/
	@RequestMapping(path = "/getTreePromptData", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Tree Prompt Data",notes = "Get the value for Tree Prompt",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getTreePromptData(@RequestBody ReportFilterVb vObject){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			List<PromptTreeVb> promptTreelst = reportsWb.getTreePromptData(vObject);
			exceptionCode.setResponse(promptTreelst);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.METHOD_FAILURE);
		}	
	}
	/*------------------------------------MDM CB REPORT EXPORT-------------------------------*/
	@RequestMapping(path = "/getCBreport", method = RequestMethod.POST)
	//@ApiOperation(value = "Get CB Report Data",notes = "get CB Report Data",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getCBKReport(@RequestBody ReportsVb vObject,HttpServletRequest request,HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode1 = reportsWb.createCBReport(vObject);
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", exceptionCode1.getOtherInfo()+".xlsx");
				request.setAttribute("filePath", exceptionCode1.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				jsonExceptionCode = new JSONExceptionCode(exceptionCode1.getErrorCode(),exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	@RequestMapping(path = "/getReportChildDependentFilter", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Report Prompt Dependency Filter Value",notes = "Get the value for Dependent filter",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getDependentValueForReportPrompts(@RequestBody ReportFilterVb vObject){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			exceptionCode = reportsWb.getReportFilterSourceValue(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(),exceptionCode.getErrorMsg(), exceptionCode.getResponse());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.METHOD_FAILURE);
		}	
	}
}
