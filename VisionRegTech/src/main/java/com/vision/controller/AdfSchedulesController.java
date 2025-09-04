package com.vision.controller;

import java.io.IOException;
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
import com.vision.dao.AdfSchedulesDao;
import com.vision.download.ExportXlsServlet;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.AdfSchedulesDetailsVb;
import com.vision.vb.AdfSchedulesVb;
import com.vision.vb.FileInfoVb;
import com.vision.vb.PromptIdsVb;
import com.vision.vb.PromptTreeVb;
import com.vision.vb.ReportsWriterVb;
import com.vision.wb.AdfSchedulesDetailsWb;
import com.vision.wb.AdfSchedulesWb;
import com.vision.wb.ReportWriterWb;
import com.vision.wb.VisionUploadWb;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("adfSchedules")
//@Api(value = "adfSchedules", description = "This is ADF Schules")
public class AdfSchedulesController {
	public static String defaultCountry = "ZZ";
	public static String defaultLeBook = "999";
	@Autowired
	private AdfSchedulesWb adfSchedulesWb;
	@Autowired
	private AdfSchedulesDetailsWb adfSchedulesDetailsWb;
	@Autowired
	private AdfSchedulesDao adfSchedulesDao;
	@Autowired
	private ReportWriterWb reportWriterWb;
	@Autowired
	private VisionUploadWb visionUploadWb;
	@Autowired
	ExportXlsServlet exportXlsServlet;

	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageLoadValues() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			AdfSchedulesVb adfSchedulesVb = new AdfSchedulesVb();
			ArrayList arrayList = adfSchedulesWb.getPageLoadValues(adfSchedulesVb);
			String autoRefreshVal = adfSchedulesWb.getCommonDao()
					.findVisionVariableValue("ADF_SCHEDULES_AUTO_REFRESH_INTERVAL");
			if (!ValidationUtil.isValid(autoRefreshVal))
				autoRefreshVal = "10";
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList,
					null, autoRefreshVal, null, null);
//			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/queryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryResults(@RequestBody AdfSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			ExceptionCode exceptionCode = adfSchedulesWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
					exceptionCode.getResponse2(), exceptionCode.getResponse3());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/adfScheduleDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "ADF Schules Details", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> adfScheduleDetails(@RequestBody AdfSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			ExceptionCode exceptionCode = adfSchedulesWb.getBuildScheduleDetails(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/adfSchedulesAdd", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "ADF Schules Add Screen", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> adfSchedulesAdd(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			String operation = "Add";
			ExceptionCode exceptionCode = null;
			adfSchedulesVb.setActionType(operation);
			operation = adfSchedulesVb.getScreenName();
			String country = adfSchedulesVb.getCountry().toUpperCase();
			String leBook = adfSchedulesVb.getLeBook().toUpperCase();
			long cronCount = adfSchedulesVb.getCronCount();
			String cronName = adfSchedulesVb.getCronName();
			String cronStatus = adfSchedulesVb.getCronStatus();
			String acquisitionProcessType = adfSchedulesVb.getAcquisitionProcessType();
			int runningCount = adfSchedulesDao.getCountOfRunningTemplates(adfSchedulesVb.getAdfNumber());
			if (runningCount > 0) {
				exceptionCode = CommonUtils.getResultObject("AdfSchedules", Constants.WE_HAVE_ERROR_DESCRIPTION,
						operation,
						"EOD has been processed or ADF is in progress or ADF about to be initiated. Cannot modify/reinitiate adf ["
								+ adfSchedulesVb.getAdfNumber() + "]. Please refresh & check.");
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setOtherInfo(adfSchedulesVb);
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Add",
							exceptionCode.getResponse(), exceptionCode.getOtherInfo());
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}
			exceptionCode = adfSchedulesDetailsWb.getMajorBuildList(adfSchedulesVb, adfSchedulesVb, operation);
			adfSchedulesVb.setCountry(country);
			adfSchedulesVb.setLeBook(leBook);
			adfSchedulesVb.setCronCount(cronCount);
			adfSchedulesVb.setCronName(cronName);
			adfSchedulesVb.setCronStatus(cronStatus);
			adfSchedulesVb.setAcquisitionProcessType(acquisitionProcessType);

			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/majorAdfChange", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "ADF Schules Major changes", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> majorAdfChange(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			adfSchedulesVb.setActionType("Query");
			String operation = adfSchedulesVb.getScreenName();
			if (ValidationUtil.isValid(adfSchedulesVb.getCountry()))
				adfSchedulesVb.setCountry(adfSchedulesVb.getCountry().toUpperCase());
			else
				adfSchedulesVb.setCountry(defaultCountry);
			if (ValidationUtil.isValid(adfSchedulesVb.getLeBook().toUpperCase()))
				adfSchedulesVb.setLeBook(adfSchedulesVb.getLeBook().toUpperCase());
			else
				adfSchedulesVb.setLeBook(defaultLeBook);
			int runningCount = adfSchedulesDao.getCountOfRunningTemplates(adfSchedulesVb.getAdfNumber());
			if (runningCount > 0) {
				ExceptionCode exceptionCode = CommonUtils.getResultObject("AdfSchedules",
						Constants.WE_HAVE_ERROR_DESCRIPTION, operation,
						"EOD has been processed or ADF is in progress or ADF about to be initiated. Cannot modify/reinitiate adf ["
								+ adfSchedulesVb.getAdfNumber() + "]. Please refresh & check.");
				if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setOtherInfo(adfSchedulesVb);
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							exceptionCode.getErrorMsg(), exceptionCode.getResponse(), exceptionCode.getOtherInfo(),
							exceptionCode.getResponse1(), exceptionCode.getResponse2(), exceptionCode.getResponse3());
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}

			ExceptionCode exceptionCode = adfSchedulesDetailsWb.getQueryList(adfSchedulesVb, operation);
			String businessDate = adfSchedulesDetailsWb.getDateFromVisionBusinessDate(adfSchedulesVb);
			String VisionBusinessDate = businessDate;

			String businessFeedDate = adfSchedulesDetailsWb.getDateForBusinessFeedDate(adfSchedulesVb);

			exceptionCode.setResponse1(VisionBusinessDate);
			exceptionCode.setResponse2(businessFeedDate);

//			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Major Bui", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Major Build",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
					exceptionCode.getResponse2(), exceptionCode.getResponse3());

			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/adfSchedulesAddMod", method = RequestMethod.POST)
	//@ApiOperation(value = "Add ADF Schules", notes = "Add ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> insert(@RequestBody AdfSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			if (vObject.getSubmitterId() == 0) {
				vObject.setSubmitterId(SessionContextHolder.getContext().getVisionId());
			}
			if ("Modify".equalsIgnoreCase(vObject.getScreenName())) {
				vObject.setOperationFroPopUp("Modify");
			} else {
				vObject.setOperationFroPopUp("Add");
			}
			exceptionCode = adfSchedulesWb.insertBuildSchedules(vObject, vObject.getScreenName());
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/terminateAdf", method = RequestMethod.POST)
	//@ApiOperation(value = "Terminate ADF Schules", notes = "Terminate ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> terminateAdf(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = adfSchedulesWb.terminateBuild(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/deleteAdf", method = RequestMethod.POST)
	//@ApiOperation(value = "Delete ADF Schules", notes = "Delete ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> delete(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = adfSchedulesWb.deleteRecord(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/reInitiateAdf", method = RequestMethod.POST)
	//@ApiOperation(value = "ReInitate ADF Schules", notes = "ReInitate ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reInitiate(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = adfSchedulesWb.reInitiate(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/reInitiateAdfForParticularStatus", method = RequestMethod.POST)
	//@ApiOperation(value = "ReInitate Particular ADF Schules", notes = "ReInitate Particular ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reInitiateToParticularStatus(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			if (buildSchedulesList != null && !buildSchedulesList.isEmpty())
				for (AdfSchedulesVb vObje : buildSchedulesList) {
					if (vObje.isChecked()) {
						vObje.setUpdateAllAvailableTemplateStatus(adfSchedulesVb.getUpdateAllAvailableTemplateStatus());
						int runningCount = adfSchedulesDao.getCountOfRunningTemplates(vObje.getAdfNumber());
						if (runningCount > 0) {
							exceptionCode = CommonUtils.getResultObject("AdfSchedules",
									Constants.WE_HAVE_ERROR_DESCRIPTION, "Re-Initiate",
									"EOD has been processed or ADF is in progress or ADF about to be initiated. Cannot modify/reinitiate adf ["
											+ vObje.getAdfNumber() + "]. Please refresh & check.");
							if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
								exceptionCode.setOtherInfo(adfSchedulesVb);
								jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Re-Initiate",
										exceptionCode.getResponse(), exceptionCode.getOtherInfo());
								return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
							}
						}
					}
				}
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = adfSchedulesWb.reInitiateForParticularStatus(exceptionCode,
					buildSchedulesList, adfSchedulesVb.getReInitiateStatus());
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/reInitiateAdfForImmidiateStatus", method = RequestMethod.POST)
	//@ApiOperation(value = "ReInitate Immediate Status ADF Schules", notes = "ReInitate Immediate Status ADF Schules", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reInitiateToImmediateStatus(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			if (buildSchedulesList != null && !buildSchedulesList.isEmpty()) {
				for (AdfSchedulesVb vObje : buildSchedulesList) {
					if (vObje.isChecked()) {
						vObje.setUpdateAllAvailableTemplateStatus(adfSchedulesVb.getUpdateAllAvailableTemplateStatus());
						int runningCount = adfSchedulesDao.getCountOfRunningTemplates(vObje.getAdfNumber());
						if (runningCount > 0) {
							exceptionCode = CommonUtils.getResultObject("AdfSchedules",
									Constants.WE_HAVE_ERROR_DESCRIPTION, "Re-Initiate",
									"EOD has been processed or ADF is in progress or ADF about to be initiated. Cannot modify/reinitiate adf ["
											+ vObje.getAdfNumber() + "]. Please refresh & check.");
							if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
								exceptionCode.setOtherInfo(adfSchedulesVb);
								jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Re-Initiate",
										exceptionCode.getResponse(), exceptionCode.getOtherInfo());
								return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
							}
						}
					}
				}
			}
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = adfSchedulesWb.reInitiate(exceptionCode, buildSchedulesList);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/getAcquisitionProcessType", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAcquisitionProcessType(@RequestBody AdfSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			List<AdfSchedulesVb> queryList = adfSchedulesDao.getAcquisitionProcessTypeAndMajorBild(vObject,
					vObject.getRequestType());
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/getadfSchedulesDetailsAudit", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> adfSchedulesAudit(@RequestBody AdfSchedulesDetailsVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = adfSchedulesDetailsWb.adfSchedulesDetailsAudit(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/startAdfCron", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> startAdfCron(@RequestBody AdfSchedulesVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = adfSchedulesWb.startCron(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Start ADF Corn",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/transferAdf", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> transferAdf(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			String updateNode = adfSchedulesVb.getStartCronNode();
			exceptionCode.setOtherInfo(adfSchedulesVb);
			ExceptionCode exceptionCode1 = adfSchedulesWb.transfer(exceptionCode, buildSchedulesList, updateNode);
			exceptionCode1.setResponse(buildSchedulesList);
			exceptionCode1.setOtherInfo(adfSchedulesVb);
			if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode1.getErrorMsg(),
						exceptionCode1.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);

		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/listFilesFromFtpAdfSchedule", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ADF Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> listFilesFromFtpAdfSchedule(@RequestBody FileInfoVb fileInfoVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			String node = fileInfoVb.getName();
			String groupBy = "Date";
			String errLogStakeHolder = "";
			if ("T".equalsIgnoreCase(fileInfoVb.getGroupBy())) {
				groupBy = "Table";
			} else if ("B".equalsIgnoreCase(fileInfoVb.getGroupBy())) {
				groupBy = "Build";
			}
			/*
			 * if(ValidationUtil.isValid(request.getParameter("stakeholder"))){
			 * errLogStakeHolder =request.getParameter("stakeholder"); errLogStakeHolder
			 * =adfSchedulesDetailsWb.adfSchedulesDao.removeDescLeBook(errLogStakeHolder); }
			 */
			if (!ValidationUtil.isValid(fileInfoVb.getDownloadType())) {
				fileInfoVb.setDownloadType(2);
			}
			if (fileInfoVb.getDownloadType() == 1) {
				fileInfoVb.setDownloadType(2);
			}
			String currentNodeName = fileInfoVb.getName();
			exceptionCode = adfSchedulesWb.listFilesFromFtpServer(fileInfoVb.getDownloadType(), groupBy,
					currentNodeName, errLogStakeHolder);
			exceptionCode.setOtherInfo(fileInfoVb);
			/*
			 * arrayList.add(fileInfoVb); arrayList.add(exceptionCode); arrayList.add(node);
			 */
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);

		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/getAdfReportData", method = RequestMethod.POST)
	//@ApiOperation(value = "get Adf Report Data", notes = "get Adf Report Data", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAdfReportData(@RequestBody AdfSchedulesVb adfSchedulesVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {

			String Legalvehicle = "";
			String AdfBusinessDate = "";
			String AdfFeedDate = "";
			String ddPrompt = "";
			String ddpromptValue = "";

			ReportsWriterVb reportsWriterVb = new ReportsWriterVb();

			ArrayList<String> prompts = new ArrayList<>();
			if (ValidationUtil.isValid(adfSchedulesVb.getReportId())
					&& "ACQALERT11".equalsIgnoreCase(adfSchedulesVb.getReportId())) {
				reportsWriterVb.setParameter1(adfSchedulesVb.getPromptId1());
				reportsWriterVb.setParameter2(adfSchedulesVb.getPromptId2());
				reportsWriterVb.setParameter3(adfSchedulesVb.getPromptId3());
				reportsWriterVb.setParameter4(adfSchedulesVb.getPromptId4());
				reportsWriterVb.setParameter5(adfSchedulesVb.getPromptId5());
				reportsWriterVb.setPromptId1(adfSchedulesVb.getPromptString4());
			} else {
				reportsWriterVb.setLegalVehiclesFlag(adfSchedulesVb.getLeBook());
				reportsWriterVb.setParameter2(adfSchedulesVb.getFrequencyProcess());
				AdfBusinessDate = adfSchedulesVb.getBusinessDate();
				AdfFeedDate = adfSchedulesVb.getFeedDate();
				AdfBusinessDate = adfSchedulesDao.getAdfBusinessFeedDate(AdfBusinessDate);
				AdfFeedDate = adfSchedulesDao.getAdfBusinessFeedDate(AdfFeedDate);
				reportsWriterVb.setParameter3(AdfBusinessDate + "," + AdfFeedDate);
				Legalvehicle = adfSchedulesDao.getAdfLegalvehicle(reportsWriterVb);
				reportsWriterVb.setParameter1(Legalvehicle);
				prompts.add("Legal Vehicle" + "-" + adfSchedulesVb.getLeBook());
				prompts.add("Frequency " + "-" + reportsWriterVb.getCaption2());
				prompts.add("Adf Date" + "-" + "Business" + " " + AdfBusinessDate + " " + "Feed" + " " + AdfFeedDate);
			}
			if (ValidationUtil.isValid(reportsWriterVb.getReportId())
					&& "ACQALERT11".equalsIgnoreCase(reportsWriterVb.getReportId())) {
				prompts.add("Legal Vehicle" + "-" + adfSchedulesVb.getPromptId1());
				prompts.add("Frequency" + "-" + adfSchedulesVb.getPromptId2());
				prompts.add("Adf Date" + "-" + adfSchedulesVb.getPromptId3());
				ddPrompt = adfSchedulesVb.getPromptString4();
				ddpromptValue = adfSchedulesVb.getPromptId4();
				prompts.add(ddPrompt + "-" + ddpromptValue);
			}
			reportsWriterVb.setProcedure1(adfSchedulesVb.getProcedure1());
			reportsWriterVb.setReportId(adfSchedulesVb.getReportId());
			reportsWriterVb.setDrillDownId(adfSchedulesVb.getDrillDownId());

			exceptionCode = adfSchedulesWb.getAdfListReportData(reportsWriterVb);
			List<AdfSchedulesVb> buildSchedulesList = adfSchedulesVb.getChilds();
			exceptionCode.setOtherInfo(adfSchedulesVb);
			/*
			 * ExceptionCode exceptionCode1 = adfSchedulesWb.deleteRecord(exceptionCode,
			 * buildSchedulesList); exceptionCode1.setResponse(buildSchedulesList);
			 * exceptionCode1.setOtherInfo(adfSchedulesVb);
			 */
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getRequest());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/adfListReportXls", method = RequestMethod.POST)
	//@ApiOperation(value = "Download Adf List Report Xls", notes = "Download Adf List Report Xls", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAdfXlsListExport(@RequestBody AdfSchedulesVb adfSchedulesVb,
			HttpServletRequest request, HttpServletResponse response) {
		ExceptionCode exceptionCode = null;
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ReportsWriterVb reportsWriterVb = new ReportsWriterVb();
			String displayName = adfSchedulesVb.getDisplayName();
			reportsWriterVb.setReportCategory("ADF");
			reportsWriterVb.setReportCategoryDesc(displayName);
			reportsWriterVb.setReportId(adfSchedulesVb.getReportId());
			reportsWriterVb.setReportTitle(adfSchedulesVb.getReportTitle());
			reportsWriterVb.setScalingFactor(adfSchedulesVb.getScalingFactor());
			reportsWriterVb.setProcedure1(adfSchedulesVb.getProcedure1());
			reportsWriterVb.setDrillDownId(adfSchedulesVb.getDrillDownId());
			reportsWriterVb.setOrientation(adfSchedulesVb.getOrientation());

			List<PromptIdsVb> Adfprompts = genrateLiist(request);
			exceptionCode = reportWriterWb.listExportToXls(Adfprompts, reportsWriterVb);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", "" + exceptionCode.getOtherInfo() + ".xlsx");
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							"Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		} catch (Exception e) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, e.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/adfListReportCsv", method = RequestMethod.POST)
	//@ApiOperation(value = "Download Adf List Report Csv", notes = "Download Adf List Report Csv", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAdfCsvListExport(@RequestBody AdfSchedulesVb adfSchedulesVb,
			HttpServletRequest request, HttpServletResponse response) {
		ExceptionCode exceptionCode = null;
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ReportsWriterVb reportsWriterVb = new ReportsWriterVb();
			String displayName = request.getParameter("displayName");
			reportsWriterVb.setReportCategory("ADF");
			reportsWriterVb.setReportCategoryDesc(displayName);
			reportsWriterVb.setReportId(adfSchedulesVb.getReportId());
			reportsWriterVb.setReportTitle(adfSchedulesVb.getReportTitle());
			reportsWriterVb.setScalingFactor(adfSchedulesVb.getScalingFactor());
			reportsWriterVb.setProcedure1(adfSchedulesVb.getProcedure1());
			reportsWriterVb.setDrillDownId(adfSchedulesVb.getDrillDownId());
			reportsWriterVb.setOrientation(adfSchedulesVb.getOrientation());
			List<PromptIdsVb> prompts = genrateLiist(request);
			exceptionCode = reportWriterWb.exportListDataToCSV(reportsWriterVb, prompts,
					SessionContextHolder.getContext().getVisionId());
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "csv");
				request.setAttribute("fileName", "" + exceptionCode.getOtherInfo() + ".csv");
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							"Report is unable to Export.Contact System Admin!!", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
		} catch (Exception e) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, e.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	public List<PromptIdsVb> genrateLiist(HttpServletRequest request) {
		List<PromptIdsVb> list = new ArrayList<PromptIdsVb>();
		PromptIdsVb vObject;
		int recordCount = request.getParameter("htxtoffset") == null ? 0
				: Integer.parseInt(request.getParameter("htxtoffset"));
		for (int i = 1; i <= recordCount; i++) {
			vObject = new PromptIdsVb();
			if (request.getParameter("promptId" + i) != null)
				vObject.setPromptId(request.getParameter("promptId" + i));
			vObject.setPromptType(request.getParameter("promptType" + i));
			vObject.setPromptString(request.getParameter("promptString" + i));
			if (request.getParameter("promptId" + i + "selectedValue1field1") != null) {
				vObject.setSelectedValue1(new PromptTreeVb());
				vObject.getSelectedValue1().setField1(request.getParameter("promptId" + i + "selectedValue1field1"));
				if (request.getParameter("promptId" + i + "selectedValue1field2") != null) {
					vObject.getSelectedValue1()
							.setField2(request.getParameter("promptId" + i + "selectedValue1field2"));
				} else {
					vObject.getSelectedValue1().setField2(vObject.getSelectedValue1().getField1());
				}
			}
			if (request.getParameter("promptId" + i + "selectedValue2field1") != null) {
				vObject.setSelectedValue2(new PromptTreeVb());
				vObject.getSelectedValue2().setField1(request.getParameter("promptId" + i + "selectedValue2field1"));
				if (request.getParameter("promptId" + i + "selectedValue2field2") != null) {
					vObject.getSelectedValue2()
							.setField2(request.getParameter("promptId" + i + "selectedValue2field2"));
				} else {
					vObject.getSelectedValue2().setField2(vObject.getSelectedValue2().getField1());
				}
			}
			list.add(vObject);
		}
		return list;
	}

	@RequestMapping(path = "/downloadErrorLog", method = RequestMethod.POST)
	//@ApiOperation(value = "downloadErrorLog", notes = "Download EtlLog", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadErrorLog(@RequestBody FileInfoVb fileInfoVb,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
//			exceptionCode = etlOperationManagerWb.downloadFile(logFileName);
			exceptionCode = adfSchedulesWb.fileDownload(fileInfoVb.getDownloadType(), fileInfoVb, "", "");
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				String fileNames = fileInfoVb.getName();
				String fileName = "";
				String fileType = "";
				if (!(ValidationUtil.isValid(fileInfoVb.getName()) && fileInfoVb.getName().contains(","))) {
					String fileExt[] = fileInfoVb.getName().split("\\.");
					request.setAttribute("fileExtension", fileExt[1]);
					request.setAttribute("fileName", fileExt[0]);
				} else {
					fileName = fileNames ;
					request.setAttribute("fileExtension", "zip");
					request.setAttribute("fileName", "logs");
				}
//				visionUploadWb.setExportXlsServlet(request, response);
				exportXlsServlet.doPost(request, response);
				if (response.getStatus() == 404) {
					return new ResponseEntity<JSONExceptionCode>(
							new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null),
							HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				if (exceptionCode.getErrorCode() == Constants.ERRONEOUS_OPERATION) {
					return new ResponseEntity<JSONExceptionCode>(
							new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(), null),
							HttpStatus.EXPECTATION_FAILED);
				} else {
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

}