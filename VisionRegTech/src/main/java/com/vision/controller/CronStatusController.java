package com.vision.controller;

import java.util.ArrayList;
import java.util.Date;

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
import com.vision.vb.CronStatusVb;
import com.vision.vb.MenuVb;
import com.vision.wb.CronStatusWb;

@RestController
@RequestMapping("cronStatus")
//@Api(value = "cronStatus", description = "Crons Status")
public class CronStatusController{
	@Autowired
	CronStatusWb cronStatusWb;
	/*-------------------------------------Crons Status SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			MenuVb menuVb = new MenuVb();
			menuVb.setActionType("Clear");
			System.out.println("PL Start : "+(new Date()).toString());
			ArrayList arrayList = cronStatusWb.getPageLoadValues();
			System.out.println("PL End : "+(new Date()).toString());
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*------------------------------------Crons Status - FETCH HEADER RECORDS-------------------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All profile Data",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody CronStatusVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			System.out.println("Query Start : "+(new Date()).toString());
			ExceptionCode exceptionCode = cronStatusWb.getAllQueryPopupResult(vObject);
			System.out.println("Query Start : "+(new Date()).toString());
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*-------------------------------------Start Cron Status------------------------------------------*/
	@RequestMapping(path = "/startCronStatus", method = RequestMethod.POST)
	//@ApiOperation(value = "Start Crons Status", notes = "Add Crons Status", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> startCronStatus(@RequestBody CronStatusVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			exceptionCode = cronStatusWb.startSecuredCronALL(vObject);
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Cron Status - Start - Successful !! ", exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Cron Status - Start - "+exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	
	/*-------------------------------------Stop Cron Status------------------------------------------*/
	@RequestMapping(path = "/stopCronStatus", method = RequestMethod.POST)
	//@ApiOperation(value = "Stop Crons Status", notes = "Modify Crons Status Values", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> stopCronStatus(@RequestBody CronStatusVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = cronStatusWb.stopSecuredCronALL(vObject);
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Cron Status - Stop - Successful !! ", exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Cron Status - Stop - "+exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	/*-------------------------------------Terminate Cron ---------------------------------------------*/
	@RequestMapping(path = "/terminateCronStatus", method = RequestMethod.POST)
	//@ApiOperation(value = "Terminate Cron Status", notes = "Modify Crons Status Values", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> terminateCronStatus(@RequestBody CronStatusVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Modify");
			exceptionCode = cronStatusWb.terminateSecuredCronALL(vObject);
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION){
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Cron Status - Terminate - Successful !! ", exceptionCode.getOtherInfo());
			}else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Cron Status - Terminate - "+exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}	
}
