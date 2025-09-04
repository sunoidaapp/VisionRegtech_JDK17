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

import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.vb.MenuVb;
import com.vision.vb.VisionDynamicHashVariablesVb;
import com.vision.wb.VisionDynamicHashVariablesWb;

@RestController
@RequestMapping("visionDynamicHashVariables")
//@Api(value = "visionDynamicHashVariables", description = "Template Config Header and Mapping")
public class VisionDynamicHashVariablesController {

	@Autowired
	VisionDynamicHashVariablesWb visionDynamicHashVariablesWb;
	


	/*-------------------------------------BRANCH RM TARGET SCREEN PAGE LOAD------------------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			MenuVb menuVb = new MenuVb();
			menuVb.setActionType("Clear");
			ArrayList arrayList = visionDynamicHashVariablesWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getAllQueryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryResults(@RequestBody VisionDynamicHashVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = visionDynamicHashVariablesWb.getAllQueryPopupResult(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*--------------get Query Results------------------*/
	@RequestMapping(path = "/getQueryDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All the details", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAllQueryDetails(@RequestBody VisionDynamicHashVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = visionDynamicHashVariablesWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------ADD Template config------------------------------------------*/
	@RequestMapping(path = "/addVisionDynamicHashVariables", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Product Line Config", notes = "Add Template Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> addVisionDynamicHashVariables(@RequestBody List<VisionDynamicHashVariablesVb> vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(new VisionDynamicHashVariablesVb());
			exceptionCode = visionDynamicHashVariablesWb.insertRecord(exceptionCode, vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Modify Template config------------------------------------------*/
	@RequestMapping(path = "/modifyVisionDynamicHashVariables", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Template Config", notes = "Modify Template Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyVisionDynamicHashVariables(@RequestBody List<VisionDynamicHashVariablesVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(vObjects.get(0));
			exceptionCode = visionDynamicHashVariablesWb.modifyRecord(exceptionCode, vObjects);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Delete Template config------------------------------------------*/
	@RequestMapping(path = "/deleteVisionDynamicHashVariables", method = RequestMethod.POST)
	//@ApiOperation(value = "delete Template Config", notes = "delete Template Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteVisionDynamicHashVariables(@RequestBody List<VisionDynamicHashVariablesVb> vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(new VisionDynamicHashVariablesVb());
			exceptionCode = visionDynamicHashVariablesWb.deleteRecord(exceptionCode, vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Reject Template config------------------------------------------*/
	@RequestMapping(path = "/rejectVisionDynamicHashVariables", method = RequestMethod.POST)
	//@ApiOperation(value = "reject Template Config", notes = "reject Template Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectVisionDynamicHashVariables(@RequestBody List<VisionDynamicHashVariablesVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObjects.get(0).setActionType("Reject");
			VisionDynamicHashVariablesVb visionDynamicHashVariablesVb =new VisionDynamicHashVariablesVb();
			visionDynamicHashVariablesVb=vObjects.get(0);
			exceptionCode = visionDynamicHashVariablesWb.bulkReject(vObjects,visionDynamicHashVariablesVb);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Approve Template config------------------------------------------*/
	@RequestMapping(path = "/approveVisionDynamicHashVariables", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve Template Config", notes = "reject Template Config", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveVisionDynamicHashVariables(@RequestBody List<VisionDynamicHashVariablesVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			VisionDynamicHashVariablesVb visionDynamicHashVariablesVb =new VisionDynamicHashVariablesVb();
			visionDynamicHashVariablesVb=vObjects.get(0);
			exceptionCode = visionDynamicHashVariablesWb.bulkApprove(vObjects,visionDynamicHashVariablesVb);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Review VisionDynamicHashVariables ------------------------------------------*/
	@RequestMapping(path = "/reviewVisionDynamicHashVariables", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Headers", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewVisionDynamicHashVariables(@RequestBody VisionDynamicHashVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = visionDynamicHashVariablesWb.reviewRecord1(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/testConnection", method = RequestMethod.POST)
	//@ApiOperation(value = "Test Connector", notes = "Test a connector", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> testConnector(@RequestBody VisionDynamicHashVariablesVb vObject) throws Exception {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setConnectionType("DB");
			String variableScript = visionDynamicHashVariablesWb.dynamicScriptCreation(vObject);
			vObject.setVariableScript(variableScript);
			ExceptionCode exceptionCode = CommonUtils.getConnectionFromScript(vObject.getVariableScript());
			if(exceptionCode.getErrorCode() ==Constants.SUCCESSFUL_OPERATION ) {
				exceptionCode.setResponse1(variableScript);
			}
			if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), "Connector - Test Connection - Successful",exceptionCode.getResponse(),
						exceptionCode.getResponse1(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (Exception e) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, e.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	/*------------------------------------ GET TAG LIST BASED ON SELECTED CONNECTOR TYPE -------------------------------*/
	@RequestMapping(path = "/getTagListDbCon", method = RequestMethod.POST)
	//@ApiOperation(value = "Get Tag list", notes = "Returns a tag list from Macrovar_tagging", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getTagListDbCon(@RequestBody VisionDynamicHashVariablesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
//			if(!ValidationUtil.isValid(vObject.getClientId()))
//				vObject.setClientId(clinetId);
			List<VisionDynamicHashVariablesVb> dbScriptPopList = visionDynamicHashVariablesWb.getDisplayTagList(vObject);
			if (dbScriptPopList != null && dbScriptPopList.size() > 0) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Display specific macro tag list", dbScriptPopList, vObject);
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "No Results Found", null);
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
}
