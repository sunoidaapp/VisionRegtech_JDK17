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

import com.vision.dao.RaProfilePrivilegesDao;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.vb.RaProfileVb;
import com.vision.wb.RaProfilePrivilegesWb;

@RestController
@RequestMapping("profileSetup")
//@Api(value="ProfileSetup" , description="This is to set the Privileges based on User Group/Profile")
public class RaProfilePrivilegesController {
	@Autowired
	private RaProfilePrivilegesWb raprofilePrivilegesWb;
	@Autowired
	RaProfilePrivilegesDao raProfilePrivilegesDao;
	
	/*-------------------------------------RA PROFILE SETUP SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> profilePrivilegesLoad(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ArrayList arrayList = raprofilePrivilegesWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}	
	}
	/*-------------------------------------GET ALL PROFILE DATA RECORDS------------------------------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All profile Data",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody RaProfileVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = raprofilePrivilegesWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}	
	}
	/*-------------------------------------RA PROFILE SETUP - ADD------------------------------------------*/
	@RequestMapping(path = "/addProfile", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Profile Privileges",notes = "Add Profile Proviliges",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> add(@RequestBody RaProfileVb profileData){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			profileData.setActionType("Add");
			exceptionCode = raprofilePrivilegesWb.insertRecord(profileData);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------RA PROFILE SETUP - MODIFY------------------------------------------*/
	@RequestMapping(path = "/modifyProfile", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Profile Privileges",notes = "Modify Profile Proviliges",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modify(@RequestBody RaProfileVb profileData){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			profileData.setActionType("Modify");
			exceptionCode = raprofilePrivilegesWb.modifyRecord(profileData);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------RA PROFILE SETUP - DELETE------------------------------------------*/
	@RequestMapping(path = "/deleteProfile", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete Profile Privileges",notes = "Delete Profile Proviliges",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> delete(@RequestBody RaProfileVb profileData){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			profileData.setActionType("Delete");
			exceptionCode = raprofilePrivilegesWb.deleteRecord(profileData);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------RA PROFILE SETUP - ADD------------------------------------------*/
	@RequestMapping(path = "/updateHomeDashboard", method = RequestMethod.POST)
	//@ApiOperation(value = "Update Home Dashboard",notes = "Update Home Dashboard",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> updateHomeDashboard(@RequestBody RaProfileVb profileData){
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			profileData.setActionType("Modify");
			exceptionCode = raprofilePrivilegesWb.userHomeDashboardUpdate(profileData);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
//	/*-------------------------------------RA PROFILE SETUP - GET DASHBOARDLIST------------------------------------------*/
//	@RequestMapping(path = "/getReportDashboardList", method = RequestMethod.POST)
//	//@ApiOperation(value = "Get Report Dashboard list",notes = "List of Dashboard/Reports for profile setup",response = ResponseEntity.class)
//	public ResponseEntity<JSONExceptionCode> getAllReportDashboards(@RequestParam String type){
//		JSONExceptionCode jsonExceptionCode  = null;
//		ExceptionCode exceptionCode = new ExceptionCode();
//		try {
//			
//			List<AlphaSubTabVb> reportDashboardlst = raProfilePrivilegesDao.getAllDashboardList(type);
//			exceptionCode.setResponse(reportDashboardlst);
//			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
//			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
//		}catch (RuntimeCustomException rex) {
//			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
//			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
//		}
//	}
	/*-------------------------------------Approve RA User Setup------------------------------------------*/
	@RequestMapping(path = "/approveProfileSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve RA User Setup", notes = "approve RA User Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveRaUserSetup(@RequestBody List<RaProfileVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Approve");
			exceptionCode = raprofilePrivilegesWb.bulkApprove(vObjects,vObjects.get(0));
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Reject RA User Setup------------------------------------------*/
	@RequestMapping(path = "/rejectProfileSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "reject Profile Setup", notes = "reject Profile Setup", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectRaUserSetup(@RequestBody List<RaProfileVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Reject");
			exceptionCode = raprofilePrivilegesWb.bulkReject(vObjects,vObjects.get(0));
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/reviewProfileSetup", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> review(@RequestBody RaProfileVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = raprofilePrivilegesWb.reviewRecordNew(vObject);
			String errorMesss = exceptionCode.getErrorMsg().replaceAll("Query ", "Review");
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), errorMesss,exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get the details", notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getQueryDetails(@RequestBody RaProfileVb vObject){
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = raprofilePrivilegesWb.getQueryDetails(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);	
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
}
