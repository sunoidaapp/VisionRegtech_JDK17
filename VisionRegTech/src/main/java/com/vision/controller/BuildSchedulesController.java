package com.vision.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.BuildSchedulesVb;
import com.vision.vb.FileInfoVb;
import com.vision.wb.BuildSchedulesDetailsWb;
import com.vision.wb.BuildSchedulesWb;

@RestController
@RequestMapping("buildSchedules")
//@Api(value="buildSchedules" , description="This is ADF Schules")
public class BuildSchedulesController {
	public static String defaultCountry="ZZ";
	public static String defaultLeBook="999";
	@Autowired
	private BuildSchedulesWb buildSchedulesWb;
	@Autowired
	private BuildSchedulesDetailsWb buildSchedulesDetailsWb;

	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageLoadValues(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			BuildSchedulesVb vObject = new BuildSchedulesVb();
			ArrayList arrayList = buildSchedulesWb.getPageLoadValues(vObject);
			String autoRefreshVal =  buildSchedulesWb.getCommonDao().findVisionVariableValue("BUILD_SCHEDULES_AUTO_REFRESH_INTERVAL");
			if(!ValidationUtil.isValid(autoRefreshVal))
				autoRefreshVal = "10";
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList, null, autoRefreshVal, null, null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/queryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryResults(@RequestBody BuildSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			if(ValidationUtil.isValid(vObject.getBuildName())) {
				vObject.setBuild(vObject.getBuildName().toUpperCase());
			}
			ExceptionCode exceptionCode = buildSchedulesWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo(), exceptionCode.getResponse1(), exceptionCode.getResponse2(), exceptionCode.getResponse3());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/buildScheduleDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules",notes = "ADF Schules Details",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> buildScheduleDetails(@RequestBody BuildSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			ExceptionCode  exceptionCode= buildSchedulesWb.getBuildScheduleDetails(vObject);			
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/majorBuildChange", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules",notes = "ADF Schules Major changes",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> majorBuildChange(@RequestBody BuildSchedulesVb buildSchedulesVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		List<BuildSchedulesVb> BuildBuinessDate = new ArrayList<BuildSchedulesVb>();
		try{
			buildSchedulesVb.setActionType("Query");
			String operation = buildSchedulesVb.getScreenName();
			if(ValidationUtil.isValid(buildSchedulesVb.getCountry()))
				buildSchedulesVb.setCountry(buildSchedulesVb.getCountry().toUpperCase());
			else
				buildSchedulesVb.setCountry(defaultCountry);
			if(ValidationUtil.isValid(buildSchedulesVb.getLeBook().toUpperCase()))
				buildSchedulesVb.setLeBook(buildSchedulesVb.getLeBook().toUpperCase());
			else
				buildSchedulesVb.setLeBook(defaultLeBook);
			ExceptionCode  exceptionCode= buildSchedulesDetailsWb.getQueryList(buildSchedulesVb, operation);
			
//			String businessFeedDate = getBuildSchedulesDetailsWb().getDateForBusinessFeedDate(buildSchedulesVb.getCountry(),buildSchedulesVb.getLeBook(),buildSchedulesVb.getBuild());
//			session.setAttribute("VisionBusinessFeedDate", businessFeedDate);
			
			
			BuildBuinessDate = buildSchedulesDetailsWb.getBuildSchedulesDao().getDateForBusinessFeedDate(buildSchedulesVb.getCountry(),
					buildSchedulesVb.getLeBook(),buildSchedulesVb.getBuild());
			exceptionCode.setResponse1(BuildBuinessDate);
			
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Major Build", exceptionCode.getResponse(),exceptionCode.getOtherInfo(), exceptionCode.getResponse1(), exceptionCode.getResponse2(), exceptionCode.getResponse3());
			
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	
	@RequestMapping(path = "/buildSchedulesAddMod", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ADF Schules", notes = "Add ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> insert(@RequestBody BuildSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			if(vObject.getSubmitterId() == 0) {
				vObject.setSubmitterId(SessionContextHolder.getContext().getVisionId());
			}
			exceptionCode = buildSchedulesWb.insertBuildSchedules(vObject, vObject.getBuildControls(), vObject.getScreenName());
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/terminateBuild", method = RequestMethod.POST)
	//@ApiOperation(value = "Terminate ADF Schules", notes = "Terminate ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> terminateBuild(@RequestBody BuildSchedulesVb buildSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<BuildSchedulesVb> buildSchedulesList = buildSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(buildSchedulesVb);
			ExceptionCode exceptionCode1 = buildSchedulesWb.terminateBuild(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(buildSchedulesVb);
			if(exceptionCode1.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}	
	@RequestMapping(path = "/deleteBuild", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete ADF Schules", notes = "Delete ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteBuild(@RequestBody BuildSchedulesVb buildSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<BuildSchedulesVb> buildSchedulesList = buildSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(buildSchedulesVb);
			//ExceptionCode exceptionCode1 = getAdfSchedulesWb().deleteRecord(exceptionCode, buildSchedulesList);
			ExceptionCode exceptionCode1 = buildSchedulesWb.deleteRecord(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(buildSchedulesVb);
			if(exceptionCode1.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/reInitiateBuild", method = RequestMethod.POST)
	//@ApiOperation(value = "ReInitate ADF Schules", notes = "ReInitate ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reInitiate(@RequestBody BuildSchedulesVb buildSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<BuildSchedulesVb> buildSchedulesList = buildSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(buildSchedulesVb);
			ExceptionCode exceptionCode1 = buildSchedulesWb.reInitiate(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(buildSchedulesVb);
			if(exceptionCode1.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/startBuildCron", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> startBuildCron(@RequestBody BuildSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = buildSchedulesWb.startCron(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
	@RequestMapping(path = "/transferBuild", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> transferBuild(@RequestBody BuildSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			List<BuildSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			String updateNode=adfSchedulesVb.getStartCronNode();
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = buildSchedulesWb.transfer(exceptionCode, buildSchedulesList, updateNode);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if(exceptionCode1.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(), exceptionCode1.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
			
	//@ApiOperation(value = "Get All ADF Schules",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> listFilesFromFtpBuildSchedule(@RequestBody FileInfoVb fileInfoVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			String node = fileInfoVb.getName();
			String groupBy = "Date";
			String errLogStakeHolder="";
			if("T".equalsIgnoreCase(fileInfoVb.getGroupBy())){
				groupBy = "Table";
			}else if("B".equalsIgnoreCase(fileInfoVb.getGroupBy())){
				groupBy = "Build";
			}
/*			if(ValidationUtil.isValid(request.getParameter("stakeholder"))){
				errLogStakeHolder =request.getParameter("stakeholder");
				errLogStakeHolder =getAdfSchedulesDetailsWb().getAdfSchedulesDao().removeDescLeBook(errLogStakeHolder);
			}
			*/
			String currentNodeName=fileInfoVb.getName();
			exceptionCode = buildSchedulesWb.listFilesFromFtpServer(fileInfoVb.getDownloadType(), groupBy, currentNodeName);
			exceptionCode.setOtherInfo(fileInfoVb);
			/*arrayList.add(fileInfoVb);
			arrayList.add(exceptionCode);
			arrayList.add(node);
			*/
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
}
