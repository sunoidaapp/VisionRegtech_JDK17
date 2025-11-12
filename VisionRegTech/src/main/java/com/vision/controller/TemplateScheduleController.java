package com.vision.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.FileInfoVb;
import com.vision.vb.MenuVb;
import com.vision.vb.TemplateScheduleVb;
import com.vision.wb.TemplateScheduleWb;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("templateSchedule")
//@Api(value = "templateSchedule", description = "This is templateSchedule")
public class TemplateScheduleController {
	@Autowired
	private TemplateScheduleWb templateScheduleWb;

	/*-------------------------------------Page Load Values-------------------------------*/
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values", notes = "Load AT/NT Values on screen load", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageOnLoad() {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			MenuVb menuVb = new MenuVb();
			menuVb.setActionType("Clear");
			ArrayList arrayList = templateScheduleWb.getPageLoadValues();
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", arrayList);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/templateSchedulePanel1", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling", notes = "Acquistion panel1 based on stakeholder and country", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadFilingPanel1(@RequestBody TemplateScheduleVb templateScheduleVb) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = templateScheduleWb.getAcqPanel1(templateScheduleVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(), null,
					null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/templateSchedulePanel2", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling", notes = "templateScheduleVb panel2 based on stakeholder,frequency and country", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadFilingPanel2(@RequestBody TemplateScheduleVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = templateScheduleWb.getAcqPanel2(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/templateSchedulePanel3", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling", notes = "Acquistion panel3 based on stakeholder,frequency and reportingdate", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadFilingPanel3(@RequestBody TemplateScheduleVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = templateScheduleWb.getAcqPanel3(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload",
					exceptionCode.getResponse(), exceptionCode.getResponse1(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/downloadErrorLog", method = RequestMethod.POST)
	//@ApiOperation(value = "downloadErrorLog", notes = "Download ErrorLog", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadCallReport(@RequestParam("logFileName") String logFileName,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			TemplateScheduleVb vObject = new TemplateScheduleVb();
			vObject.setActionType("Download");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
			exceptionCode = templateScheduleWb.downloadFile(logFileName, vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (!logFileName.contains(",")) {
					String fileExt[] = logFileName.split("\\.");
					request.setAttribute("fileExtension", fileExt[1]);
					request.setAttribute("fileName", fileExt[0]);
				} else {
					request.setAttribute("fileExtension", "zip");
					request.setAttribute("fileName", "logs");
				}
				request.setAttribute("filePath", exceptionCode.getResponse());
				templateScheduleWb.setExportXlsServlet(request, response);
				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	/*-------------------------------------Upload Files Rest Service-------------------------------*/
	@RequestMapping(path = "/uploadFiles", method = RequestMethod.POST)
	//@ApiOperation(value = "Uploading Multipart files to Server", notes = "Upload files to Server", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> uploadFilesToFtp(@RequestParam("file") MultipartFile[] files) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = null;
		try {
			boolean isExlel;
			TemplateScheduleVb vObject = new TemplateScheduleVb();
			vObject.setActionType("upload");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
			if (files.length <= 0) {
				return new ResponseEntity<JSONExceptionCode>(
						new JSONExceptionCode(Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload file error", null),
						HttpStatus.EXPECTATION_FAILED);
			} else {
				for (MultipartFile uploadedFile : files) {
					byte[] byteArr = uploadedFile.getBytes();
					InputStream inputStream = new ByteArrayInputStream(byteArr);
					isExlel = templateScheduleWb.isExcel(inputStream);
					if (isExlel != true) {
						return new ResponseEntity<JSONExceptionCode>(
								new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Please upload a valid Excel file",
										null),
								HttpStatus.OK);
					}

					String extension = FilenameUtils.getExtension(uploadedFile.getOriginalFilename());
					FileInfoVb fileInfoVb = new FileInfoVb();
					fileInfoVb.setName(uploadedFile.getOriginalFilename());
					fileInfoVb.setData(uploadedFile.getBytes());
					fileInfoVb.setExtension(extension);
					if (ValidationUtil.isValid(fileInfoVb.getName())) {
						exceptionCode = templateScheduleWb.doUpload(fileInfoVb.getName(), fileInfoVb.getData());
						if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
							return new ResponseEntity<JSONExceptionCode>(
									new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Fail", null),
									HttpStatus.EXPECTATION_FAILED);
						}
					}
				}
			}
			return new ResponseEntity<JSONExceptionCode>(
					new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Vision Upload Successful", ""),
					HttpStatus.OK);
		} catch (Exception e) {
			RuntimeCustomException ex = (RuntimeCustomException) e;
			e.printStackTrace();
			return new ResponseEntity<JSONExceptionCode>(
					new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, ex.getMessage(), null),
					HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/modifytemplateScheduleUpload", method = RequestMethod.POST)
	//@ApiOperation(value = "Modify Acq Upload", notes = "Modify Acq Upload", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> modifyAcqusitionUpload(@RequestBody List<TemplateScheduleVb> vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode.setOtherInfo(vObject.get(0));
			vObject.get(0).setActionType("Modify");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = templateScheduleWb.modifyRecord(exceptionCode, vObject);
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

	@RequestMapping(path = "/templateScheduleReview", method = RequestMethod.POST)
	//@ApiOperation(value = "Review", notes = "Review", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> review(@RequestBody TemplateScheduleVb vObject, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			vObject.setMaxRecords(100);
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = templateScheduleWb.reviewDetails(vObject, vObject.getXlName());
			if ("XL".equalsIgnoreCase(vObject.getSourceType())) {
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					if (!vObject.getXlName().contains(",")) {
						String fileExt[] = vObject.getXlName().split("\\.");
						request.setAttribute("fileExtension", fileExt[1]);
						request.setAttribute("fileName", fileExt[0]);
					} else {
						request.setAttribute("fileExtension", "zip");
						request.setAttribute("fileName", "logs");
					}
					request.setAttribute("filePath", exceptionCode.getResponse());
					templateScheduleWb.setExportXlsServlet(request, response);
					if (response.getStatus() == 404) {
						jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found",
								null);
						return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
					} else {
						jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
						return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
					}
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}
			} else {
				if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION,
							exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),exceptionCode.getResponse2(), exceptionCode.getResponse3()
							);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							exceptionCode.getErrorMsg(), exceptionCode.getOtherInfo());
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}

			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/getTemplatesAudit", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All ACQ Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getTemplatesAudit(@RequestBody TemplateScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = templateScheduleWb.getTemplatesAudit(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/templateScheduleReviewExportToCsv", method = RequestMethod.POST)
	//@ApiOperation(value = "Review", notes = "Review", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewExportToCsv(@RequestBody TemplateScheduleVb vObject,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Download");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			String type = "CSV";
			exceptionCode = templateScheduleWb.exportReviewDetails(vObject, type);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "csv");
				request.setAttribute("fileName", exceptionCode.getOtherInfo() + ".csv");
				request.setAttribute("filePath", exceptionCode.getResponse());
				templateScheduleWb.setExportXlsServlet(request, response);

				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							"Review Data Unable to Export.Contact System Admin!!", null);
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
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/templateScheduleReviewExportToExcel", method = RequestMethod.POST)
	//@ApiOperation(value = "Review", notes = "Review", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewExportToExcel(@RequestBody TemplateScheduleVb vObject,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Download");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			String type = "XL";
			exceptionCode = templateScheduleWb.exportReviewDetails(vObject, type);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", exceptionCode.getOtherInfo() + ".xlsx");
				request.setAttribute("filePath", exceptionCode.getResponse());
				templateScheduleWb.setExportXlsServlet(request, response);

				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							"Review Data Unable to Export.Contact System Admin!!", null);
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
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
	@RequestMapping(path = "/templateScheduleReviewExportToXML", method = RequestMethod.POST)
	//@ApiOperation(value = "Review", notes = "Review", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewExportToXML(@RequestBody TemplateScheduleVb vObject,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Download");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			String type = "XML";
			exceptionCode = templateScheduleWb.exportReviewDetails(vObject, type);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xml");
				request.setAttribute("fileName", exceptionCode.getOtherInfo() );
				request.setAttribute("filePath", exceptionCode.getResponse());
				templateScheduleWb.setExportXlsServlet(request, response);

				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							"Review Data Unable to Export.Contact System Admin!!", null);
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
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/savetemplateScheduleData", method = RequestMethod.POST)
	//@ApiOperation(value = "Save Acq Data", notes = "Save Acq Data", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> saveReviewData(@RequestBody TemplateScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Add");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = templateScheduleWb.insertRecord(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getResponse(),
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

	@RequestMapping(path = "/templateScheduleDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling", notes = "Acquistion panel1 based on stakeholder and country", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> templateScheduleDetails(
			@RequestBody TemplateScheduleVb templateScheduleVb) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = templateScheduleWb.getTemplateScheduleDetails(templateScheduleVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
					exceptionCode.getResponse2(), null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	
	@RequestMapping(path = "/delete", method = RequestMethod.POST)
	//@ApiOperation(value = "Reject In Adjustments", notes = "Reject In Adjustments", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> deleteDetails(
			@RequestBody TemplateScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Delete");
			vObject.setMaxRecords(100);
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = templateScheduleWb.deleteRecord(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
					exceptionCode.getResponse2(), null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	@RequestMapping(path = "/templateApproveScheduleDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Approve In Adjustments", notes = "Approve In Adjustments", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> approveScheduleDetails(
			@RequestBody List <TemplateScheduleVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			TemplateScheduleVb templateScheduleVb = new TemplateScheduleVb();
			templateScheduleVb.setActionType("Approve");
			templateScheduleVb.setMaxRecords(100);
			exceptionCode.setOtherInfo(vObjects);
			exceptionCode = templateScheduleWb.doValidate(templateScheduleVb);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = templateScheduleWb.bulkApprove(vObjects,templateScheduleVb);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
					exceptionCode.getResponse2(), null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	@RequestMapping(path = "/templateReviewScheduleDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Review In Adjustments", notes = "Review In Adjustments", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> reviewTemplateSchedule(@RequestBody TemplateScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Query");
			exceptionCode = templateScheduleWb.reviewRecordNew(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	/*-------------------------------------Unlock Vision User------------------------------------------*/
	@RequestMapping(path = "/templateValidate", method = RequestMethod.POST)
	//@ApiOperation(value = "Validate  ", notes = "Validate", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> templateValidate(@RequestBody List<TemplateScheduleVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleWb.Validate(vObjects);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/templateSubmit", method = RequestMethod.POST)
	//@ApiOperation(value = "Validate  ", notes = "Validate", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> templateSubmit(@RequestBody List<TemplateScheduleVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleWb.Submit(vObjects);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(path = "/templateReInitiate", method = RequestMethod.POST)
	//@ApiOperation(value = "ReInitiate  ", notes = "ReInitiate", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> templateReInitiate(@RequestBody List<TemplateScheduleVb> vObjects) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleWb.ReInitiate(vObjects);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),exceptionCode.getOtherInfo());
			}
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/getTemplateScheduleAudit", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All submit Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> templateSchedulesAudit(@RequestBody TemplateScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = templateScheduleWb.getTemplatesScheduleAudit(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results",
					exceptionCode.getResponse(), exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	@RequestMapping(path = "/getTemplateScheduleAuditLog", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All submit Schules", notes = "Fetch all the existing records from the table", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getTemplateSchedulesAuditLog(@RequestBody TemplateScheduleVb vObject,HttpServletRequest request, HttpServletResponse response) {
		JSONExceptionCode jsonExceptionCode = null;
		try {
			vObject.setActionType("Query");
			ExceptionCode exceptionCode = templateScheduleWb.auditLogDownload(vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "xlsx");
				request.setAttribute("fileName", exceptionCode.getOtherInfo() + ".xlsx");
				request.setAttribute("filePath", exceptionCode.getResponse());
				templateScheduleWb.setExportXlsServlet(request, response);

				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION,
							"Unable to Export.Contact System Admin!!", null);
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
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
	@RequestMapping(path = "/downloadJsonLog", method = RequestMethod.POST)
	//@ApiOperation(value = "downloadJsonLog", notes = "download Json Log", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadJsonLog(@RequestParam("logFileName") String logFileName,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			TemplateScheduleVb vObject = new TemplateScheduleVb();
			vObject.setActionType("Download");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
			vObject.setReview(true);
			exceptionCode = templateScheduleWb.downloadFile(logFileName, vObject);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				if (!logFileName.contains(",")) {
					String fileExt[] = logFileName.split("\\.");
					request.setAttribute("fileExtension", fileExt[1]);
					request.setAttribute("fileName", fileExt[0]);
				} else {
					request.setAttribute("fileExtension", "zip");
					request.setAttribute("fileName", "logs");
				}

				request.setAttribute("filePath", exceptionCode.getResponse());
				templateScheduleWb.setExportXlsServlet(request, response);				
				
//				request.setAttribute("filePath", exceptionCode.getResponse());
//				templateScheduleWb.setExportXlsServlet(request, response);
				if (response.getStatus() == 404) {
					jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				} else {
					jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			} else {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
//	@RequestMapping(path = "/bulkReject", method = RequestMethod.POST)
//	//@ApiOperation(value = "Reject In Adjustments", notes = "Reject In Adjustments", response = ResponseEntity.class)
//	public ResponseEntity<JSONExceptionCode> bulkReject(@RequestBody TemplateScheduleVb vObject) {
//		JSONExceptionCode jsonExceptionCode = null;
//		ExceptionCode exceptionCode = new ExceptionCode();
//		try {
//			vObject.setActionType("Reject");
//			vObject.setMaxRecords(100);
//			exceptionCode = templateScheduleWb.doValidate(vObject);
//			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
//				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
//						exceptionCode.getOtherInfo());
//				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
//			}
//			exceptionCode = templateScheduleWb.bulkReject(vObject);
//			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(),
//					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
//					exceptionCode.getResponse2(), null);
//			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
//		} catch (RuntimeCustomException rex) {
//			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
//			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
//		}
//	}
	@RequestMapping(path = "/templateRejectScheduleDetails", method = RequestMethod.POST)
	//@ApiOperation(value = "Reject In Adjustments", notes = "Reject In Adjustments", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> rejectScheduleDetails(
			@RequestBody TemplateScheduleVb vObject) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			vObject.setActionType("Reject");
			exceptionCode = templateScheduleWb.doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, exceptionCode.getErrorMsg(),
						exceptionCode.getOtherInfo());
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
			}
			exceptionCode = templateScheduleWb.reject(vObject);
			jsonExceptionCode = new JSONExceptionCode(exceptionCode.getErrorCode(), exceptionCode.getErrorMsg(),
					exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(),
					exceptionCode.getResponse2(), null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}
	}
}