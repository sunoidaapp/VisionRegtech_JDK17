package com.vision.controller;

import java.util.ArrayList;

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
import com.vision.vb.BusinessLineHeaderVb;
import com.vision.vb.MenuVb;
import com.vision.wb.BusinessLineConfigWb;
@RestController
@RequestMapping("businessLineConfig")
//@Api(value = "businessLineConfig", description = "Business Line Configuration and Details")
public class BusinessLineConfigController {
		@Autowired
		BusinessLineConfigWb businessLineConfigWb;
		
		/*-------------------------------------Business Line config SCREEN PAGE LOAD------------------------------------------*/
		@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
		//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> pageOnLoad(){
			JSONExceptionCode jsonExceptionCode  = null;
			try{
				MenuVb menuVb = new MenuVb();
				menuVb.setActionType("Clear");
				ArrayList arrayList = businessLineConfigWb.getPageLoadValues();
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}	
		}
		/*--------------get Query Results------------------*/
		@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody BusinessLineHeaderVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				ExceptionCode exceptionCode = businessLineConfigWb.getAllQueryPopupResult(vObject);
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*--------------get Query Results------------------*/
		@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
		//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody BusinessLineHeaderVb vObject){
			JSONExceptionCode jsonExceptionCode = null;
			try {
				vObject.setActionType("Query");
				ExceptionCode exceptionCode = businessLineConfigWb.getQueryResults(vObject);
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
			}catch(RuntimeCustomException rex){
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		}
		/*-------------------------------------ADD Business config------------------------------------------*/
		@RequestMapping(path = "/addBusinessLineConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Add Business Line Config", notes = "Add Business Line Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> addBusinessLineConfig(@RequestBody BusinessLineHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Add");
				exceptionCode = businessLineConfigWb.insertRecord(vObject);
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
		/*-------------------------------------Modify Business config------------------------------------------*/
		@RequestMapping(path = "/modifyBusinessLineConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Modify Business Line Config", notes = "Modify Business Line Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> modifyBusinessLineConfig(@RequestBody BusinessLineHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Modify");
				exceptionCode = businessLineConfigWb.modifyRecord(vObject);
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
		/*-------------------------------------Delete Business config------------------------------------------*/
		@RequestMapping(path = "/deleteBusinessLineConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "delete Business Line Config", notes = "delete Business Line Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> deleteBusinessLineConfig(@RequestBody BusinessLineHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Delete");
				exceptionCode = businessLineConfigWb.deleteRecord(vObject);
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
		/*-------------------------------------Reject Business config------------------------------------------*/
		@RequestMapping(path = "/rejectBusinessLineConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "reject Business Line Config", notes = "reject Business Line Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> rejectBusinessLineConfig(@RequestBody BusinessLineHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Reject");
				exceptionCode = businessLineConfigWb.reject(vObject);
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
		/*-------------------------------------Approve Business config------------------------------------------*/
		@RequestMapping(path = "/approveBusinessLineConfig", method = RequestMethod.POST)
		//@ApiOperation(value = "Approve Business Line Config", notes = "reject Business Line Config", response = ResponseEntity.class)
		public ResponseEntity<JSONExceptionCode> approveBusinessLineConfig(@RequestBody BusinessLineHeaderVb vObject) {
			JSONExceptionCode jsonExceptionCode = null;
			ExceptionCode exceptionCode = new ExceptionCode();
			try {
				vObject.setActionType("Approve");
				exceptionCode = businessLineConfigWb.approve(vObject);
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
}