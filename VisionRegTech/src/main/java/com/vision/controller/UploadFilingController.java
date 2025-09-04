package com.vision.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vision.authentication.SessionContextHolder;
import com.vision.exception.ExceptionCode;
import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.vb.FileInfoVb;
import com.vision.vb.UploadFilingVb;
import com.vision.wb.UploadFilingWb;
import com.vision.wb.VisionUploadWb;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("nonAdfSchedules")
//@Api(value="nonAdfSchedules" , description="This is Upload Filling")
public class UploadFilingController {
	@Autowired
	private UploadFilingWb uploadFilingWb;
	
	@Autowired
	private VisionUploadWb visionUploadWb;
	@RequestMapping(path = "/pageLoadValues", method = RequestMethod.GET)
	//@ApiOperation(value = "Page Load Values",notes = "Load AT/NT Values on screen load",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> pageLoad(){
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			UploadFilingVb uploadFilingVb = new UploadFilingVb();
			System.out.println("PL Start : "+(new Date()).toString());
//			ArrayList arrayList = uploadFilingWb.getPageLoadValues(uploadFilingVb);
			List<UploadFilingVb> list = uploadFilingWb.getQueryPopupResults(uploadFilingVb);
			System.out.println("PL En : "+(new Date()).toString());
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Page Load Values", list);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/queryResults", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryResults(@RequestBody UploadFilingVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			List<UploadFilingVb> queryList = uploadFilingWb.getQueryPopupResults(vObject);
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
	//@ApiOperation(value = "Get All Upload Filling",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> queryDetailsList(@RequestBody UploadFilingVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			vObject.setActionType("Query");
			vObject.setVerificationRequired(false);
			vObject.setRecordIndicator(0);
			ExceptionCode exceptionCode = uploadFilingWb.getQueryResults(vObject);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	/*@RequestMapping(path = "/uploadFilesUploadFiling", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling Add Screen",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> uploadFilesUploadFiling(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			boolean isCount = false;
			String operation = "Add";
			ExceptionCode exceptionCode = new ExceptionCode();
			uploadFilingVb.setActionType(operation);
			int runningCount = uploadFilingWb.getUploadFilingDao().getCountOfRunningTemplates(uploadFilingVb.getAdfNumber(), 1);
			if(runningCount>0){
				exceptionCode.setErrorCode(Constants.STATUS_ZERO);
				uploadFilingVb.setMakerName("You cannot submit at this moment because templates have been processed.");
				isCount = true;
			}
			runningCount = 0;
			runningCount = uploadFilingWb.getUploadFilingDao().getCountOfRunningTemplates(uploadFilingVb.getAdfNumber(), 2);
			if(runningCount>0){
				exceptionCode.setErrorCode(Constants.STATUS_ZERO);
				uploadFilingVb.setMakerName("You cannot submit at this moment because templates are in progress.");
				isCount = true;
			}
			runningCount = 0;
			runningCount = uploadFilingWb.getUploadFilingDao().getCountOfRunningTemplates(uploadFilingVb.getAdfNumber(), 3);
			if(runningCount>0){
				exceptionCode.setErrorCode(Constants.STATUS_ZERO);
				uploadFilingVb.setMakerName("You cannot submit at this moment because templates are in queue to be processed.");
				isCount = true;
			}
			exceptionCode.setOtherInfo(uploadFilingVb);
			int errorCode = Constants.SUCCESSFUL_OPERATION;
			String errorMessage = "Query Results";
			if(isCount) {
				errorCode = exceptionCode.getErrorCode();
				errorMessage = uploadFilingVb.getMakerName();
			}
			
			jsonExceptionCode = new JSONExceptionCode(errorCode, errorMessage, exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	*/
	
	@RequestMapping(path = "/uploadFilesToFtp", method = RequestMethod.POST)
	//@ApiOperation(value = "Uploading Multipart files to Server",notes = "Upload files to Server",response = ResponseEntity.class)
//	public ResponseEntity<JSONExceptionCode> uploadFilesToFtp(@RequestParam("file") MultipartFile[] files){
	public ResponseEntity<JSONExceptionCode> uploadFilesToFtp(@RequestParam("adfNumber") String adfNumber, @RequestParam("file") MultipartFile[] files){
		ExceptionCode exceptionCode = null;
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			//String adfNumber =uploadFilingVb.getAdfNumber();
			 String node = uploadFilingWb.getNodeRequestByAdfNumber(adfNumber);
			 if(!ValidationUtil.isValid(node))
				   node = "U1";
			   
			//List<MultipartFile> files = files;//uploadFilingVb.getFiles();
		   if (files == null || files.length<=0) {
			   return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload file error",null), HttpStatus.EXPECTATION_FAILED);
		   }else {		  
			   for(MultipartFile file : files) {
					String extension = FilenameUtils.getExtension(file.getOriginalFilename());
					FileInfoVb fileInfoVb = new FileInfoVb();
					fileInfoVb.setName(file.getOriginalFilename());
					fileInfoVb.setData(file.getBytes());
					fileInfoVb.setExtension(extension);
					    
					    
				    String originalFileName = file.getOriginalFilename();//RWF01_MTH_GLCODES_30-SEP-2016.xlsx
				    String suffix = originalFileName.substring(originalFileName.indexOf(".")+1 , originalFileName.length());
					originalFileName = originalFileName.substring(0, originalFileName.indexOf("."));
					byte [] byteArr=file.getBytes();
					InputStream inputStream = new ByteArrayInputStream(byteArr);	
//					boolean isExlel = isExcel(inputStream);
					boolean isExlel = true;
					if(isExlel != true){
						   exceptionCode = CommonUtils.getResultObject("Upload Filling", Constants.WE_HAVE_ERROR_DESCRIPTION, "Upload", "Please upload a valid Excel file");
							jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Upload Filling", exceptionCode.getResponse(),exceptionCode.getOtherInfo(), exceptionCode.getResponse1(), exceptionCode.getResponse2(), exceptionCode.getResponse3());
							return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
					}
							
					int scheduleCount = uploadFilingWb.getUploadFilingDao().getADFBuildStatusWith(originalFileName);				
					if(scheduleCount > 0){
						   exceptionCode = CommonUtils.getResultObject("Upload Filling", Constants.WE_HAVE_ERROR_DESCRIPTION, "Upload", "File In Progress can not upload again");
							jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "Upload Filling", exceptionCode.getResponse(),exceptionCode.getOtherInfo(), exceptionCode.getResponse1(), exceptionCode.getResponse2(), exceptionCode.getResponse3());
							return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
					}
					
					long maxVersion = uploadFilingWb.getUploadFilingDao().getMaxVersionNo(originalFileName);
					System.out.println("File UPload : Version : "+maxVersion+" File Name "+originalFileName);
					
					uploadFilingWb.getUploadFilingDao().doUpdateVersionNo(originalFileName, maxVersion);
				    if(ValidationUtil.isValid(fileInfoVb.getName())){
				    	exceptionCode = uploadFilingWb.doUpload(fileInfoVb.getName(), fileInfoVb.getData(), maxVersion, node);
				    	if(exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION){
				    		jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, exceptionCode.getErrorMsg(), exceptionCode.getResponse(),exceptionCode.getOtherInfo());
				    		return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				    	}
				    }
				}
		  	}
		   return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "E-Upload Successful", exceptionCode.getOtherInfo()), HttpStatus.OK);
		}catch (Exception e) {
			RuntimeCustomException ex = (RuntimeCustomException)e;
			e.printStackTrace();
			return new ResponseEntity<JSONExceptionCode>(new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, ex.getMessage(),null), HttpStatus.EXPECTATION_FAILED);
		}
	}
//	public boolean isExcel(InputStream i) throws IOException{
//	    return (POIFSFileSystem.hasPOIFSHeader(i) || POIXMLDocument.hasOOXMLHeader(i));
//	}
	public boolean isExcel(InputStream inputStream) throws IOException {
	    // Ensure mark/reset capability
	    if (!inputStream.markSupported()) {
	        inputStream = new PushbackInputStream(inputStream, 8);
	    }

	    FileMagic fileMagic = FileMagic.valueOf(inputStream);
	    return fileMagic == FileMagic.OLE2 || fileMagic == FileMagic.OOXML;
	}
/*	@RequestMapping(path = "/addUploadFilingUploadFiling", method = RequestMethod.POST)
	//@ApiOperation(value = "Add Upload Filling", notes = "Add Upload Filling", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> add(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
		   String node = uploadFilingWb.getNodeRequestByAdfNumber(uploadFilingVb.getAdfNumber());
		   if(!ValidationUtil.isValid(node))
			   node = "N3";
		   List<UploadFilingVb> uploadFillingList = uploadFilingVb.getChilds();
		   
			exceptionCode.setOtherInfo(uploadFilingVb);
			ExceptionCode exceptionCode1 = uploadFilingWb.insertRecord(exceptionCode, uploadFillingList, node);
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
	}*/
	@RequestMapping(path = "/getDependencyCheck", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling Add Screen",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getDependencyCheck(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			String operation = "Add";
			ExceptionCode exceptionCode = new ExceptionCode();
			uploadFilingVb.setActionType(operation);
			int depedentCount = uploadFilingWb.getUploadFilingDao().getTemplateDependencyCheckCount(uploadFilingVb);
			exceptionCode.setOtherInfo(uploadFilingVb);
			exceptionCode.setResponse(depedentCount);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/getProcessFrequencyNonADF", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Fetch all the existing records from the table",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getAcquisitionProcessType(@RequestBody UploadFilingVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			List<UploadFilingVb> queryList = uploadFilingWb.getUploadFilingDao().getProcessFrequency(vObject);
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
	@RequestMapping(path = "/getBusinessDateNonADF", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Fetch Vision Business Day",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getBusinessDate(@RequestBody UploadFilingVb vObject) {
		JSONExceptionCode jsonExceptionCode  = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try{
			vObject.setActionType("Query");
			List<UploadFilingVb> queryList = uploadFilingWb.getUploadFilingDao().getBusnessDate(vObject);
			exceptionCode.setResponse(queryList);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Query Results", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
	@RequestMapping(path = "/getMajorBuildNonADF", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling Major changes",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> getMajorBuildNonADF(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			List<UploadFilingVb> queryList = uploadFilingWb.getUploadFilingDao().getMajorBuild(uploadFilingVb);
			exceptionCode.setResponse(queryList);
			exceptionCode.setOtherInfo(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Major Bui", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}

	/*-------------------------------------Log File Download------------------------------------------*/
	@RequestMapping(path = "/downloadEUploadError", method = RequestMethod.POST)
	//@ApiOperation(value = "downloadEUploadError", notes = "Download Log", response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> downloadCallReport(@RequestBody UploadFilingVb uploadFilingVb,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONExceptionCode jsonExceptionCode = null;
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
//			exceptionCode = etlOperationManagerWb.downloadFile(logFileName);
			int viisonId = SessionContextHolder.getContext().getVisionId();
			uploadFilingVb.setExportData(uploadFilingVb.getTemplateName());
			exceptionCode = uploadFilingWb.listExportToXls(uploadFilingVb, viisonId);
			if(exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				request.setAttribute("fileExtension", "XLSX");
				request.setAttribute("fileName", exceptionCode.getRequest()+"_"+viisonId);
				request.setAttribute("filePath", exceptionCode.getResponse());
				visionUploadWb.setExportXlsServlet(request, response);
				if(response.getStatus() == 404) {
					jsonExceptionCode =	new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, "File not found", null);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
				}else{
					jsonExceptionCode =	new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Success", response);
					return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
				}
			}else{
				return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (RuntimeCustomException rex) {
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.EXPECTATION_FAILED);
		}
	}
	@RequestMapping(path = "/errorLogUploadFiling", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling error Log file",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> errorLogUploadFiling(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			//List<UploadFilingVb> queryList = uploadFilingWb.getUploadFilingDao().getMajorBuild(uploadFilingVb);
			List<UploadFilingVb> queryList = uploadFilingWb.getDrillDownCommentsPopup(uploadFilingVb);
			exceptionCode.setResponse(queryList);
			exceptionCode.setOtherInfo(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Major Bui", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}	
	
	@RequestMapping(path = "/acqUploadFilingPanel1", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling frequency based on stakeholder and country",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadFilingPanel1(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = uploadFilingWb.getAcqUploadFilingPanel1(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload", exceptionCode.getResponse(), exceptionCode.getOtherInfo(), exceptionCode.getResponse1(), null, null);
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	
	@RequestMapping(path = "/acqUploadFilingPanel2", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling build based on stakeholder,frequency and country",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadFilingPanel2(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = uploadFilingWb.getAcqUploadFilingPanel2(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
	@RequestMapping(path = "/acqUploadFilingPanel3", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling frequency based on stakeholder and country",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadFilingPanel3(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			exceptionCode = uploadFilingWb.getAcqUploadFilingPanel3(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}

	@RequestMapping(path = "/acqUploadErrorPop", method = RequestMethod.POST)
	//@ApiOperation(value = "Get All Upload Filling",notes = "Upload Filling frequency based on stakeholder and country",response = ResponseEntity.class)
	public ResponseEntity<JSONExceptionCode> acqUploadErrorPop(@RequestBody UploadFilingVb uploadFilingVb) {
		JSONExceptionCode jsonExceptionCode  = null;
		List collTemp = null;
		try{
			ExceptionCode exceptionCode = new ExceptionCode();
			uploadFilingVb.setExportData(uploadFilingVb.getTemplateName());
			List<UploadFilingVb> queryList = uploadFilingWb.getDrillDownCommentsPopup(uploadFilingVb);
			exceptionCode.setResponse(queryList);
			exceptionCode.setOtherInfo(uploadFilingVb);
			jsonExceptionCode = new JSONExceptionCode(Constants.SUCCESSFUL_OPERATION, "Acq - E Upload", exceptionCode.getResponse(),exceptionCode.getOtherInfo());
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}catch(RuntimeCustomException rex){
			jsonExceptionCode = new JSONExceptionCode(Constants.ERRONEOUS_OPERATION, rex.getMessage(), "");
			return new ResponseEntity<JSONExceptionCode>(jsonExceptionCode, HttpStatus.OK);
		}	
	}
}