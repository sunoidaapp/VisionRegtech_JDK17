package com.vision.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.ADFTemplateAuthVb;
import com.vision.vb.RsAccessVb;
import com.vision.wb.ADFTemplateAuthWb;
import com.vision.wb.VisionUploadWb;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("adfTemplateAuth")
//@Api(value="adfTemplateAuth" , description="This is ADF Template Authentication")
public class AdfTemplateAuthController {
	@Autowired
	private ADFTemplateAuthWb adfTemplateAuthWb;
	@Autowired
	private VisionUploadWb visionUploadWb;
	
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageLoad(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			System.out.println("PL Start : "+(new Date()).toString());
			List list = adfTemplateAuthWb.getPageLoadValues();
			System.out.println("PL En : "+(new Date()).toString());
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", list);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/queryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryResults(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			List<ADFTemplateAuthVb> queryList = adfTemplateAuthWb.getQueryPopupResults(vObject);
			if(queryList == null ) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	
	@RequestMapping(path = "/queryDetailsList", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryDetailsList(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			ExceptionCode exceptionCode = adfTemplateAuthWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
		catch(Exception rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
	@RequestMapping(path = "/getComments", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Get the comments the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getComments(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			List<ADFTemplateAuthVb> queryList = adfTemplateAuthWb.getAdfTemplateAuthDao().getCommentResults(vObject);
			if(queryList == null ) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
	@RequestMapping(path = "/getCommentsDrilldown", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Get the drilldown the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getCommentsDrilldown(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			List<ADFTemplateAuthVb> queryList = adfTemplateAuthWb.getAdfTemplateAuthDao().getCommentDrilldownResults(vObject);
			if(queryList == null ) {
				exceptionCode.setErrorCode(Constants.NO_RECORDS_FOUND);
			}else {
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			exceptionCode.setOtherInfo(vObject);
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/verification", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ADF Template Authentication", notes = "Verification ADF Template Authentication", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> verification(@RequestBody ADFTemplateAuthVb adfTemplateAuthVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		   List<ADFTemplateAuthVb> adfTemplateAuthList = adfTemplateAuthVb.getChilds();
			exceptionCode.setOtherInfo(adfTemplateAuthVb);
			ExceptionCode exceptionCode1 = adfTemplateAuthWb.modifyRecord(exceptionCode, adfTemplateAuthList);
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getResponse(),exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getResponse(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(path = "/resubmission", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ADF Template Authentication", notes = "Resubmission ADF Template Authentication", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> resubmission(@RequestBody ADFTemplateAuthVb adfTemplateAuthVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		   List<ADFTemplateAuthVb> adfTemplateAuthList = adfTemplateAuthVb.getChilds();
	//		exceptionCode.setOtherInfo(adfTemplateAuthVb);
			ExceptionCode exceptionCode1 = new ExceptionCode();
			ADFTemplateAuthVb temp = adfTemplateAuthVb.getChilds().get(0);
			temp.setEmailNote(adfTemplateAuthVb.getEmailNote());
			exceptionCode.setOtherInfo(temp);
			if(!"".equalsIgnoreCase(adfTemplateAuthList.get(0).getEmailID()) && !"null".equalsIgnoreCase(adfTemplateAuthList.get(0).getEmailID())){
				exceptionCode1 = adfTemplateAuthWb.resubmissionRecord(exceptionCode, adfTemplateAuthList);
			}else{
				exceptionCode1.setResponse(adfTemplateAuthList);
				exceptionCode1.setOtherInfo(adfTemplateAuthVb);
				exceptionCode1.setErrorMsg("Sorry - No email id has been maintened");
			}
			if(exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getResponse(),exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getResponse(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(path = "/rejection", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ADF Template Authentication", notes = "Rejection ADF Template Authentication", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejection(@RequestBody ADFTemplateAuthVb adfTemplateAuthVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		   List<ADFTemplateAuthVb> adfTemplateAuthList = adfTemplateAuthVb.getChilds();
			exceptionCode.setOtherInfo(adfTemplateAuthVb);
			ExceptionCode exceptionCode1 = adfTemplateAuthWb.rejectRecord(exceptionCode, adfTemplateAuthList);
			if(exceptionCode1.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getResponse(),exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getResponse(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/getProcessFrequency", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Get the Proces Frequenceyfrom the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getProcessFrequency(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			List<RsAccessVb> queryList = adfTemplateAuthWb.getAdfTemplateAuthDao().getProcessFrequency(vObject, "");
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	
	
	@RequestMapping(path = "/getBusinessDate", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Get the Business Date from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getBusinessDate(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			List<RsAccessVb> queryList = adfTemplateAuthWb.getAdfTemplateAuthDao().getBusnessDate(vObject, "BusinessDate");
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	
	
	@RequestMapping(path = "/getFeedDate", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "Get the Feed Date from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getFeedDate(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			List<RsAccessVb> queryList = adfTemplateAuthWb.getAdfTemplateAuthDao().getFeedDate(vObject, "FeedDate");
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	
	
	@RequestMapping(path = "/getMajorBuild", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Template Authentication",notes = "ADF Template Authentication Major changes",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getMajorBuild(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			List<ADFTemplateAuthVb> queryList = adfTemplateAuthWb.getAdfTemplateAuthDao().getMajorBuild(vObject, "MajorBuild");
			exceptionCode.setResponse(queryList);
			exceptionCode.setOtherInfo(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Major Bui", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}

	@RequestMapping(path = "/reviewDownload", method = RequestMethod.POST)
	//@ApiOperation(value = "Download Template File For Review",notes = "File uploaded in e-upload to be downloaded here",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewDownload(@RequestBody ADFTemplateAuthVb vObject, HttpServletRequest request, HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = adfTemplateAuthWb.downloadExcelFileFromSFTP(vObject.getFileName());
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", exceptionCode.getOtherInfo());
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found",null), HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				if(exceptionCode.getErrorCode()==Constants.ERRONEOUS_OPERATION) {
					return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),null), HttpStatus.EXPECTATION_FAILED);
				}else {
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}
			}
		} catch (Exception rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	@RequestMapping(path = "/filterAdfTemplateAuth", method = RequestMethod.POST)
	//@ApiOperation(value = "Filter Adf Template Auth", notes = "Filter Adf Template Auth", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> filterAdfTemplateAuth(@RequestBody ADFTemplateAuthVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = adfTemplateAuthWb.filterAdfTemplateAuth(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getResponse(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
}