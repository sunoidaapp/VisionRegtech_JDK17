package com.vision.wb;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.authentication.SessionContextHolder;
import com.vision.dao.AbstractDao;
import com.vision.dao.CommonDao;
import com.vision.dao.ReportsDao;
import com.vision.dao.TemplateScheduleCronDao;
import com.vision.dao.TemplateScheduleDao;
import com.vision.download.ExportXlsServlet;
import com.vision.exception.ExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.CommonUtils;
import com.vision.util.Constants;
import com.vision.util.ValidationUtil;
import com.vision.util.ZipUtils;
import com.vision.vb.ColumnHeadersVb;
import com.vision.vb.ReportsVb;
import com.vision.vb.ReviewResultVb;
import com.vision.vb.TemplateConfigVb;
import com.vision.vb.TemplateErrorsVb;
import com.vision.vb.TemplateMappingVb;
import com.vision.vb.TemplateScheduleVb;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TemplateScheduleWb extends AbstractDynaWorkerBean<TemplateScheduleVb> {
	public static Logger logger = LoggerFactory.getLogger(TemplateScheduleWb.class);
	public static final long fixedRate = 30000;
	@Value("${schedule.rgBuild}")
	private String rgBuildFlag;

	@Value("${app.client.id}")
	private String clientID;

	@Value("${app.alient.sceret}")
	private String clientSecret;

	@Value("${app.client.scope}")
	private String scope;

	public String visionRegTechApijar = "VisionRegTechApi.jar";
	ResourceBundle rsb = CommonUtils.getResourceManger();

	@Autowired
	TemplateScheduleDao templateScheduleDao;

//	@Autowired
//	OAuth2Client auth2Client;
	@Autowired
	TemplateScheduleCronDao templateScheduleCronDao;

	@Autowired
	private CommonDao commonDao;

	@Autowired
	private ReportsDao reportsDao;

	@Autowired
	TemplateScheduleCron templateScheduleCron;

	@Autowired
	private ExportXlsServlet exportXlsServlet;

	@Override
	protected AbstractDao<TemplateScheduleVb> getScreenDao() {
		return templateScheduleDao;
	}

	@Override
	protected void setAtNtValues(TemplateScheduleVb vObject) {

	}

	private String uploadDir;
	private String uploadDirAd;
	private static final String SERVICE_NAME = "RegTech Upload";

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public String getUploadDirAd() {
		return uploadDirAd;
	}

	public void setUploadDirAd(String uploadDirAd) {
		this.uploadDirAd = uploadDirAd;
	}

	@Override
	protected void setVerifReqDeleteType(TemplateScheduleVb vObject) {
		vObject.setVerificationRequired(false);
		vObject.setStaticDelete(false);
	}

	public ArrayList getPageLoadValues() {
		List collTemp = null;
		ArrayList<Object> arrListLocal = new ArrayList<Object>();
		try {

			String refresh = commonDao.findVisionVariableValue("RG_LOG_AUTO_RFERESH");

			if (!ValidationUtil.isValid(refresh)) {
				refresh = "120";
			}
			arrListLocal.add(refresh);
			collTemp = getAlphaSubTabDao().findAlphaSubTabsByAlphaTabInternalStatus(9999);
			arrListLocal.add(collTemp);
			collTemp = getAlphaSubTabDao().findAlphaSubTabsByAlphaTabInternalStatus(5008);
			arrListLocal.add(collTemp);
			collTemp = commonDao.getLegalEntity();
			arrListLocal.add(collTemp);
			String xlRecordCount = commonDao.findVisionVariableValue("RG_XL_RECORD_COUNT");
			arrListLocal.add(xlRecordCount);
			return arrListLocal;
		} catch (Exception ex) {
			ex.printStackTrace();
			// logger.error("Exception in getting the Page load values.", ex);
			return null;
		}
	}

	public ExceptionCode getAcqPanel1(TemplateScheduleVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleDao.getPanelInfo1(queryPopupObj);
			if (exceptionCode.getResponse() != null) {
				exceptionCode.setOtherInfo(queryPopupObj);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			String autoRefreshVal = getCommonDao().findVisionVariableValue("RG_ACQ_REFRESH_TIME");
			if (!ValidationUtil.isValid(autoRefreshVal))
				autoRefreshVal = "10";
			exceptionCode.setResponse1(autoRefreshVal);
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Acq  Panel1 results.", ex);
		}
		return exceptionCode;
	}

	public ExceptionCode getAcqPanel2(TemplateScheduleVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleDao.getPanelInfo2(queryPopupObj);
			if (exceptionCode.getResponse() != null) {
				exceptionCode.setOtherInfo(queryPopupObj);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Acq -  Panel2 results.", ex);
		}
		return exceptionCode;
	}

	public ExceptionCode getAcqPanel3(TemplateScheduleVb queryPopupObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleDao.getPanelInfo3(queryPopupObj);
			if (exceptionCode.getResponse() != null) {
				ExceptionCode exceptionCode1 = new ExceptionCode();
				exceptionCode1 = templateScheduleDao.getSummaryDetailsforPanel3(queryPopupObj);
				if (exceptionCode1.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
					exceptionCode.setResponse1(exceptionCode1.getResponse());
				}
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Acq -  Panel3 results.", ex);
		}
		return exceptionCode;
	}

	public ExceptionCode downloadFile(String logFileName, TemplateScheduleVb vObject) throws IOException {
		ExceptionCode exceptionCode = new ExceptionCode();
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;
		try {
			exceptionCode = doValidate(vObject);
			if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
				return exceptionCode;
			}

			String filePath = commonDao.findVisionVariableValue("RG_SERV_LOGPATH");
			if (vObject.isReview()) {
				filePath = commonDao.findVisionVariableValue("RG_EXCES_LOGPATH");
			}
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			System.out.println("tmpFilePath -> " + tmpFilePath);
			String[] logFile = logFileName.split(",");
			File lfile = new File(tmpFilePath + File.separator + "logs.zip");
			if (lfile.exists()) {
				lfile.delete();
			}
			if (logFile.length > 1) {
				FileOutputStream fos = new FileOutputStream(tmpFilePath + File.separator + "logs.zip");
				ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
				for (int i = 0; i < logFile.length; i++) {
					FileInputStream fis = new FileInputStream(filePath + File.separator + logFile[i]);
					ZipEntry ze = new ZipEntry(logFile[i]);
					zipOut.putNextEntry(ze);
					byte[] tmp = new byte[4 * 1024];
					int size = 0;
					while ((size = fis.read(tmp)) != -1) {
						zipOut.write(tmp, 0, size);
					}
					fis.close();
				}
				zipOut.close();
				fos.close();
			} else {
				System.out.println("Log Path :" + filePath);
				File my_file = new File(filePath + File.separator + logFileName);
				System.out.println("Log Path :" + filePath + File.separator + logFileName);
				out = new ByteArrayOutputStream();

				FileInputStream in = new FileInputStream(my_file);
				out = new ByteArrayOutputStream();

				String logFilePath = tmpFilePath + File.separator + logFileName;
//				Process chmod = Runtime.getRuntime().exec("chmod 777 " + logFilePath);
//				chmod.waitFor(); // Wait for the command to finish

				lfile = new File(tmpFilePath + File.separator + logFileName);
				System.out.println("File Permission Setting.........");
//				lfile.setReadable(true, false);

				System.out.println("Temp Path :" + tmpFilePath + File.separator + logFileName);
				if (lfile.exists()) {
					lfile.delete();
				}
				outputStream = new FileOutputStream(tmpFilePath + File.separator + logFileName);
				int length = logFileName.length();
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				while ((length = in.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
				out.writeTo(outputStream);
				outputStream.flush();
				outputStream.close();
				out.flush();
				out.close();
				in.close();

			}

			exceptionCode.setResponse(tmpFilePath);
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Log File Downloaded");
			return exceptionCode;
		} catch (Exception ex) {
			// logger.error("Download Errror : " + ex.getMessage());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			return exceptionCode;
		}
	}

	public HttpServletResponse setExportXlsServlet(HttpServletRequest request, HttpServletResponse response) {
		try {
			exportXlsServlet.doPost(request, response);
			return response;
		} catch (Exception e) {
			throw new RuntimeCustomException(e.getMessage());
		}
	}

	public ExceptionCode doValidate(TemplateScheduleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String operation = vObject.getActionType();
		String srtRestrion = commonDao.getRestrictionsByUsers("30", operation);
		if (!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation + " " + Constants.userRestrictionMsg);
			return exceptionCode;
		}
		return exceptionCode;
	}

	public ExceptionCode doValidate(List<TemplateScheduleVb> vObjects) {
		ExceptionCode exceptionCode = null;
		TemplateScheduleVb vObject = vObjects.get(0);
		String operation = vObject.getActionType();
		String srtRestrion = getCommonDao().getRestrictionsByUsers("30", operation);
		if (!"Y".equalsIgnoreCase(srtRestrion)) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(operation + " " + Constants.userRestrictionMsg);
			return exceptionCode;
		}

		return exceptionCode;

	}

	public void setUploadDownloadDirFromDB() {
		String uploadLogFilePath = getVisionVariableValue("RG_UPLOAD_PATH");
		if (uploadLogFilePath != null && !uploadLogFilePath.isEmpty()) {
			uploadDir = uploadLogFilePath;
		}
		String uploadDataFilePath = getVisionVariableValue("RG_UPLOAD_AD_PATH");
		if (uploadDataFilePath != null && !uploadDataFilePath.isEmpty()) {
			uploadDirAd = uploadDataFilePath;
		}
	}

	public ExceptionCode doUpload(String fileName, byte[] data) {
		ExceptionCode exceptionCode = null;
		try {
			setUploadDownloadDirFromDB();
			fileName = fileName.toUpperCase();
			String fileExtension = "xlsx";
			String uploadDir = "";
			uploadDir = getUploadDir();
			uploadDirAd = getUploadDirAd();
			fileName = fileName.toUpperCase();
			fileName = fileName.substring(0, fileName.lastIndexOf('.')) + "." + fileExtension + "";
			String tempName = fileName;
			// System.out.println("fileName:" + fileName);
			if ('/' != uploadDir.charAt(uploadDir.length() - 1)) {
				fileName = uploadDir + "\\" + fileName;
				// System.out.println("******fileName:" + fileName);
			} else {
				fileName = uploadDir + fileName;
				// System.out.println("#####fileName:" + fileName);
			}
			File lFile = new File(fileName);
			if (lFile.exists()) {
				moveFileTouploadAd(fileName, tempName);
				lFile.delete();
			}
			lFile.createNewFile();
			// System.out.println("fileName :" + fileName);
			FileOutputStream fout = new FileOutputStream(fileName);
			fout.write(data);
			fout.close();
		} catch (FileNotFoundException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		} catch (IOException e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		} catch (Exception e) {
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.FILE_UPLOAD_REMOTE_ERROR, "Upload", "");
			exceptionCode.setResponse(fileName);
			throw new RuntimeCustomException(exceptionCode);
		}
		exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
		exceptionCode.setOtherInfo(fileName);
		return exceptionCode;
	}

	public void moveFileTouploadAd(String fileName, String tempName) {
		setUploadDownloadDirFromDB();
		String uploadDirAd = "";
		uploadDirAd = getUploadDirAd();
		Path sourcePath = Paths.get(fileName);
		Path destiPath = Paths.get(uploadDirAd + "\\" + tempName);
		try {
			// Move the file
			Files.move(sourcePath, destiPath, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

//	public boolean isExcel(InputStream i) throws IOException {
//		return (POIFSFileSystem.hasPOIFSHeader(i) || POIXMLDocument.hasOOXMLHeader(i));
//	}
	public boolean isExcel(InputStream inputStream) throws IOException {
		// Ensure mark/reset capability
		if (!inputStream.markSupported()) {
			inputStream = new PushbackInputStream(inputStream, 8);
		}

		FileMagic fileMagic = FileMagic.valueOf(inputStream);
		return fileMagic == FileMagic.OLE2 || fileMagic == FileMagic.OOXML;
	}

	public ExceptionCode reviewDetails(TemplateScheduleVb vObject, String fileName) throws IOException {
		ExceptionCode exceptionCode = null;
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;

		exceptionCode = doValidate(vObject);
		if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
			return exceptionCode;
		}
		String tmpFilePath = System.getProperty("java.io.tmpdir");
		if ("XL".equalsIgnoreCase(vObject.getSourceType())) {
			setUploadDownloadDirFromDB();
			String uploadDir = "";
			uploadDir = getUploadDir();

			File my_file = new File(uploadDir + File.separator + fileName);
			out = new ByteArrayOutputStream();
			FileInputStream in = new FileInputStream(my_file);
			out = new ByteArrayOutputStream();
			File lfile = new File(tmpFilePath + File.separator + fileName);
			if (lfile.exists()) {
				lfile.delete();
			}
			outputStream = new FileOutputStream(tmpFilePath + File.separator + fileName);
			int length = fileName.length();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.writeTo(outputStream);
			outputStream.flush();
			outputStream.close();
			out.flush();
			out.close();
			in.close();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
			exceptionCode.setOtherInfo(fileName);
			exceptionCode.setResponse(tmpFilePath);
		} else if ("DB".equalsIgnoreCase(vObject.getSourceType())) {
			exceptionCode = templateScheduleDao.reviewDbData(vObject);
		}
		return exceptionCode;
	}

	public ExceptionCode getTemplatesAudit(TemplateScheduleVb templateScheduleVb) {
		ExceptionCode exceptionCode = null;
		try {
			TemplateScheduleVb tempSchedules = null;
			tempSchedules = templateScheduleDao.getTemplatesAudit(templateScheduleVb);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), Constants.SUCCESSFUL_OPERATION,
					"Query", "");
			exceptionCode.setOtherInfo(templateScheduleVb);
			exceptionCode.setResponse(tempSchedules);
		} catch (RuntimeCustomException rex) {
			logger.error(" Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			return exceptionCode;
		}
		return exceptionCode;
	}

	public ExceptionCode getTemplatesScheduleAudit(TemplateScheduleVb templateScheduleVb) {
		ExceptionCode exceptionCode = null;
		try {
			List<TemplateScheduleVb> tempSchedules = null;
			tempSchedules = templateScheduleDao.getTemplateSchedulesAudit(templateScheduleVb);
			exceptionCode = CommonUtils.getResultObject(getScreenDao().getServiceName(), Constants.SUCCESSFUL_OPERATION,
					"Query", "");
			exceptionCode.setOtherInfo(templateScheduleVb);
			exceptionCode.setResponse(tempSchedules);
		} catch (RuntimeCustomException rex) {
			logger.error(" Exception " + rex.getCode().getErrorMsg());
			exceptionCode = rex.getCode();
			return exceptionCode;
		}
		return exceptionCode;
	}

	public ExceptionCode exportReviewDetails(TemplateScheduleVb vObject, String type) throws IOException {
		ExceptionCode exceptionCode = null;

		exceptionCode = doValidate(vObject);
		if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
			return exceptionCode;
		}
		exceptionCode = templateScheduleDao.exportReviewData(vObject, type);
		return exceptionCode;
	}

	public ExceptionCode getTemplateScheduleDetails(TemplateScheduleVb templateScheduleVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			exceptionCode = templateScheduleDao.getTemplateScheduleDetails(templateScheduleVb);
			if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
				List<TemplateScheduleVb> collTemp = (List) exceptionCode.getResponse();
				if (!collTemp.isEmpty() && collTemp != null) {
					for (TemplateScheduleVb dObj : collTemp) {
						TemplateConfigVb templateConfigVb = new TemplateConfigVb();
						templateConfigVb.setCountry(dObj.getCountry());
						templateConfigVb.setLeBook(dObj.getLeBook());
						templateConfigVb.setTemplateId(dObj.getTemplateId());
						List<TemplateErrorsVb> errorLst = templateScheduleDao.getErrorList(templateConfigVb);
						String errorCnt = commonDao.findVisionVariableValue("CB_ERROR_COUNT");
						int errCnt = ValidationUtil.isValid(errorCnt) ? Integer.parseInt(errorCnt) : 100;
						if (errorLst.size() > errCnt)
							dObj.setErrorLst(errorLst.subList(0, errCnt));
						else
							dObj.setErrorLst(errorLst);
						List<TemplateScheduleVb> cmtLst = templateScheduleDao.getCommmentsLst(dObj);
						if (ValidationUtil.isValidList(cmtLst)) {
							dObj.setCmtLst(cmtLst);
						}
					}

				}
				if (exceptionCode.getResponse() != null) {
					exceptionCode.setOtherInfo(templateScheduleVb);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				}
			}
			return exceptionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Exception in getting the Acq -  Panel3 results.", ex);
		}
		return exceptionCode;
	}

	public ExceptionCode insertRecord(TemplateScheduleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String tableName = "";
		int retval = 0;
		try {
			if (vObject.getDataLst() != null && !vObject.getDataLst().isEmpty()) {
				List<TemplateConfigVb> collTemp = templateScheduleDao.getQueryResults(vObject);
				if (collTemp != null && !collTemp.isEmpty()) {
					tableName = collTemp.get(0).getSourceTable().trim() + "_PEND";
					String whereClause = "MAKER != ?";
					int recordCnt = templateScheduleDao.getPendDataCnt(tableName, whereClause, new ArrayList<>());
					if (recordCnt > 0) {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("This Template has modified by Other User ");
						return exceptionCode;
					}
					List<Map<String, Object>> dataList = (List<Map<String, Object>>) vObject.getDataLst();
					for (Map<String, Object> dataMap : dataList) {
						exceptionCode = templateScheduleDao.doInsertRecordForNonTrans(dataMap, tableName, vObject);
						if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							String auditTable = tableName.replaceAll("PEND", "AUDIT");
							exceptionCode = templateScheduleDao.doInsertAudit(dataMap, auditTable, vObject);
						}
					}
					if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
//						if("VE".equalsIgnoreCase(vObject.getProcessStatus())) {
						ExceptionCode valexceptionCode = templateScheduleDao.templateValidation(vObject);
						if (valexceptionCode != null
								&& valexceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							vObject.setProcessStatus("AP");
							vObject.setMaker(SessionContextHolder.getContext().getVisionId());
							retval = templateScheduleDao.updateTemplateHeaders(vObject, true);
//							}
						}
//						vObject.setProcessStatus("AP");
						if (retval != Constants.ERRONEOUS_OPERATION)
							exceptionCode.setOtherInfo(vObject.getProcessStatus());
						else
							exceptionCode.setOtherInfo("VE");
					}
				}

			}
		} 
		catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setOtherInfo(vObject);
			e.printStackTrace();
		}
		return exceptionCode;
	}

	public ExceptionCode reject(TemplateScheduleVb vObject) {
		ExceptionCode exceptionCode = null;
		if (!ValidationUtil.isValid(vObject.getComments())) {
			exceptionCode = new ExceptionCode();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(rsb.getString("commentismandatorytoreject"));
			return exceptionCode;
		}
		List<TemplateConfigVb> collTemp = templateScheduleDao.getQueryResults(vObject);
		if (collTemp != null && !collTemp.isEmpty()) {
			vObject.setSourceTable(collTemp.get(0).getSourceTable());
			exceptionCode = templateScheduleDao.doRejectForTransaction(vObject);
		}
		return exceptionCode;
	}

	public ExceptionCode deleteRecord(TemplateScheduleVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateConfigVb> collTemp = templateScheduleDao.getQueryResults(vObject);
		if (collTemp != null && !collTemp.isEmpty()) {
			vObject.setSourceTable(collTemp.get(0).getSourceTable());
			exceptionCode = templateScheduleDao.doDeleteRecord(vObject);
		}
		return exceptionCode;
	}

	public ExceptionCode bulkApprove(List<TemplateScheduleVb> vObjects, TemplateScheduleVb dObj) {
		ExceptionCode exceptionCode = new ExceptionCode();
		for (TemplateScheduleVb templateScheduleVb : vObjects) {
			List<TemplateConfigVb> collTemp = templateScheduleDao.getQueryResults(templateScheduleVb);
			if (collTemp != null && !collTemp.isEmpty()) {
				for (TemplateConfigVb templateConfigVb : collTemp) {
					templateScheduleVb.setSourceTable(templateConfigVb.getSourceTable());
					List<TemplateScheduleVb> scheduleData = templateScheduleDao.getQueryTableResults(templateScheduleVb,
							Constants.ERRONEOUS_OPERATION, false);
					if (scheduleData != null && scheduleData.size() > 0) {
						templateScheduleVb.setDataLst(scheduleData);
						templateScheduleVb.setSourceTable(templateConfigVb.getSourceTable());
						templateScheduleVb.setActionType(dObj.getActionType());
						exceptionCode = templateScheduleDao.doApproveRecord(templateScheduleVb, true);
					} else {
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setErrorMsg("No Records Found");
					}
				}
			}
		}
		if (exceptionCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg("");
		}
		return exceptionCode;
	}

	public ExceptionCode reviewRecordNew(TemplateScheduleVb vObject) {
		try {
			ExceptionCode exceptionCode = new ExceptionCode();
			List collTemp = null;
			List<ReviewResultVb> list = null;
			ArrayList<ColumnHeadersVb> colHeaders = null;
			String columnHeaderXml = "";
			if (vObject.isChecked()) {
				String finalQuery = templateScheduleDao.getQueryResultsReview(vObject);
				exceptionCode = templateScheduleDao.getCommonResultDataQuery(finalQuery);
			} else {
				List<TemplateConfigVb> tabLst = templateScheduleDao.getQueryResults(vObject);
				ReportsVb reportsVb = new ReportsVb();
				reportsVb.setReportId(vObject.getTemplateId());
				reportsVb.setSubReportId(vObject.getTemplateId());
				List<ColumnHeadersVb> columnHeadersXmllst = reportsDao.getReportColumns(reportsVb);
				if (tabLst != null && !tabLst.isEmpty()) {
					for (TemplateConfigVb templateConfigVb : tabLst) {
						vObject.setSourceTable(templateConfigVb.getSourceTable());
						collTemp = templateScheduleDao.reviewAll(vObject);
					}
				}
				if (columnHeadersXmllst != null && columnHeadersXmllst.size() > 0) {
					columnHeaderXml = columnHeadersXmllst.get(0).getColumnXml();
					colHeaders = templateScheduleDao.getColumnHeaders(columnHeaderXml);
					vObject.setColumnHeaderLst(colHeaders);
				}
				vObject.setDataLst(collTemp);

				exceptionCode.setResponse(vObject);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			}
			return exceptionCode;
		} catch (Exception ex) {
			return null;
		}
	}

	protected List<ReviewResultVb> transformToReviewResults(TemplateScheduleVb approvedCollection,
			TemplateScheduleVb pendingCollection) {
		ArrayList<ReviewResultVb> lResult = new ArrayList<ReviewResultVb>();
		try {
			if (pendingCollection != null && approvedCollection != null) {
				List<Map<String, Object>> apprMap = (List<Map<String, Object>>) approvedCollection.getDataLst();
				List<Map<String, Object>> pendMap = (List<Map<String, Object>>) pendingCollection.getDataLst();
				for (Map<String, Object> approvedData : apprMap) {
					for (Map<String, Object> pendingData : pendMap) {
						for (String key : approvedData.keySet()) {
							if (pendingData.containsKey(key)) {
								ReviewResultVb ldata = new ReviewResultVb(CommonUtils.convertToReadableFormat(key),
										pendingData.get(key).toString(), approvedData.get(key).toString(),
										(!pendingData.get(key).toString().equals(approvedData.get(key).toString())));
								lResult.add(ldata);
							}
						}
					}

				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lResult;
	}

	public ExceptionCode Validate(List<TemplateScheduleVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String Status = "";
		try {
			for (TemplateScheduleVb templateScheduleVb : vObjects) {
				exceptionCode = templateScheduleDao.templateValidation(templateScheduleVb);

			}
		} catch (RuntimeCustomException rex) {
			logger.error("Insert Exception " + rex.getCode().getErrorMsg());
			logger.error(((vObjects == null) ? "vObject is Null" : vObjects.toString()));
			exceptionCode = rex.getCode();
		}
		return exceptionCode;
	}

	public ExceptionCode Submit(List<TemplateScheduleVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		for (TemplateScheduleVb templateScheduleVb : vObjects) {
			if ("Y".equalsIgnoreCase(rgBuildFlag)) {

				if (ValidationUtil.isValid(templateScheduleVb.getCountry())
						&& ValidationUtil.isValid(templateScheduleVb.getLeBook())
						&& ValidationUtil.isValid(templateScheduleVb.getTemplateId())
						&& ValidationUtil.isValid(templateScheduleVb.getReportingDate())) {
					templateScheduleVb.setRecordIndicator(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
					templateScheduleCronDao.updateProcessControl("SI", templateScheduleVb, true);
					templateScheduleCronDao.updateProcessControlInternalStatus(1, templateScheduleVb);
					templateScheduleCronDao.updateTemplatesHeaderInternalStatus(1, templateScheduleVb);
					templateScheduleCronDao.updateTemplatesHeader("SI", templateScheduleVb);
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setErrorMsg("Submission In Progress");
				}
			} else {
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg("Submission Cron is Stopped");
			}
		}

		return exceptionCode;
	}

	public ExceptionCode ReInitiate(List<TemplateScheduleVb> vObjects) {
		ExceptionCode exceptionCode = new ExceptionCode();
		for (TemplateScheduleVb templateScheduleVb : vObjects) {
			if (ValidationUtil.isValid(templateScheduleVb.getCountry())
					&& ValidationUtil.isValid(templateScheduleVb.getLeBook())
					&& ValidationUtil.isValid(templateScheduleVb.getTemplateId())
					&& ValidationUtil.isValid(templateScheduleVb.getReportingDate())) {
				templateScheduleVb.setRecordIndicator(Constants.ATTEMPT_TO_MODIFY_UNEXISTING_RECORD);
				templateScheduleCronDao.updateProcessControl("VP", templateScheduleVb, true);
				templateScheduleCronDao.updateProcessControlInternalStatus(1, templateScheduleVb);
				templateScheduleCronDao.updateTemplatesHeaderInternalStatus(1, templateScheduleVb);
				templateScheduleCronDao.updateTemplatesHeader("VP", templateScheduleVb);
				exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
				exceptionCode.setErrorMsg("ReInitited Successfull");
				exceptionCode.setOtherInfo(templateScheduleVb.getProcessStatus());

			}
		}

		return exceptionCode;
	}

	public ExceptionCode submission(TemplateConfigVb templateConfigVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
//		OAuth2Client auth2Client = new OAuth2Client();
		templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Process Started", "Process Started");
//		ExceptionCode authenticationExCode = fetchAuthenticationCode(templateConfigVb);
		ExceptionCode authenticationExCode = getAuthenticationCode(templateConfigVb);
		if (authenticationExCode != null && authenticationExCode.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Fetching Authorization Token Failed ",
					"Fetching Authorization Token[" + authenticationExCode.getErrorMsg() + "] ");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Getting Authorization Token Failed ");
			return exceptionCode;
		}
		templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Fetched Authorization Token Succces ",
				"Fetched Authorization Token[" + authenticationExCode.getErrorMsg() + "] ");

		ExceptionCode exceptionCode1 = getDataForTheCBApi(templateConfigVb);
		if (exceptionCode1.getErrorCode() != Constants.SUCCESSFUL_OPERATION) {
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Process Errored",
					"Process Errored[" + exceptionCode1.getErrorMsg() + "]");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Process Errored");
			return exceptionCode;
		}
		String institutionCode = commonDao.findVisionVariableValue("INSTITUTION_CODE");
		String isFIleAttached = "N";
		String requestId = templateScheduleCronDao.getRequestId(templateConfigVb);
		requestId = ValidationUtil.isValid(requestId) ? requestId : "";
		templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Submission Started", "Submission Started ");

		ExceptionCode apiDataSendExCode = postData(institutionCode, requestId, isFIleAttached,
				templateConfigVb.getReportingDate(), exceptionCode1, authenticationExCode.getResponse());
		if (apiDataSendExCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Submission Success",
					apiDataSendExCode.getErrorMsg().trim());
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setErrorMsg("Submission Success");
		} else {
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Submission Errored",
					apiDataSendExCode.getErrorMsg().trim());
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Submission Errored");

		}
		templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Process Ended", "Process Ended");
//		System.out.println("Status : "+status);
//		System.exit(status);
		exceptionCode.setResponse1(apiDataSendExCode.getResponse1());
		exceptionCode.setResponse2(apiDataSendExCode.getResponse2());
		exceptionCode.setCsvPath(apiDataSendExCode.getCsvPath());
		return exceptionCode;
	}
	////////////////////////////////////

	public static final String DB = "DB";
	public static final String JSON = "JSON";
	public static final String CSV = "CSV";
	public static final String XML = "xml";
	public static final String ZIP = "zip";

	public ExceptionCode getDataForTheCBApi(TemplateConfigVb templateConfigVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		ExceptionCode exceptionCodeFinal = new ExceptionCode();
		try {
			if ("Y".equalsIgnoreCase(rgBuildFlag)) {

				String processStatus = "";
				templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Getting Template Schedule Detail ",
						"Getting Template Schedule Details");
				List<TemplateConfigVb> configList = templateScheduleCronDao.getTemplateConfigDetail(templateConfigVb);
				if (configList != null && configList.size() > 0) {
					TemplateConfigVb configVb = configList.get(0);
					insertAuditTrialData(templateConfigVb, "Getting Template Schedule Detail Success ",
							"Getting Template Schedule Details[" + configVb.getTemplateDescription() + "] Success");

					String extension = "";

					String excesPath = commonDao.findVisionVariableValue("RG_EXCES_LOGPATH");
					templateConfigVb.setTemplateDescription(configVb.getTemplateDescription());
					ExceptionCode exceptionCode2 = getQueryResults(templateConfigVb);

					if (DB.equalsIgnoreCase(configVb.getSourceType())
							&& JSON.equalsIgnoreCase(configVb.getTypeOfSubmission())) {

						if (exceptionCode2.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							ArrayList<TemplateConfigVb> lst = (ArrayList<TemplateConfigVb>) exceptionCode2
									.getResponse();
							templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
									"JSON Formattion for Template[" + configVb.getTemplateDescription() + "] Started",
									"JSON Formattion for Template[" + configVb.getTemplateDescription() + "] Started");
							lst.get(0).setReportingDate(templateConfigVb.getReportingDate());
							lst.get(0).setSubmissionDate(templateConfigVb.getSubmissionDate());
							exceptionCode = formJson(lst.get(0));
							if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
								insertAuditTrialData(templateConfigVb,
										"JSON Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Finished",
										"JSON Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Finished");
								String json = exceptionCode.getResponse().toString();
								exceptionCodeFinal.setErrorCode(Constants.SUCCESSFUL_OPERATION);
								exceptionCodeFinal.setResponse1(exceptionCode.getResponse1());
								exceptionCodeFinal.setResponse(json);
								exceptionCodeFinal.setRequest(configVb.getTypeOfSubmission());
								exceptionCodeFinal.setCsvPath(excesPath);
								exceptionCodeFinal.setCsvFileName(configVb.getTemplateId() + "_"
										+ templateConfigVb.getReportingDate().replaceAll("/", "_"));

								// ExceptionCode exceptionCode3 = templateScheduleCronDao.jsonFileWriter(json,
								// excesPath);
								exceptionCodeFinal.setOtherInfo(lst.get(0));
							}
						}
					}

					if (DB.equalsIgnoreCase(configVb.getSourceType())
							&& CSV.equalsIgnoreCase(configVb.getTypeOfSubmission())) {
						extension = ".csv";

						if (exceptionCode2.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							ArrayList<TemplateConfigVb> lst = (ArrayList<TemplateConfigVb>) exceptionCode2
									.getResponse();
							templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
									"CSV Formattion for Template[" + configVb.getTemplateDescription() + "] Started",
									"CSV Formattion for Template[" + configVb.getTemplateDescription() + "] Started");
							lst.get(0).setReportingDate(templateConfigVb.getReportingDate());
							exceptionCode = formCsv(lst.get(0));
							if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
								templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
										"CSV Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Finished",
										"CSV Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Finished");
								ArrayList<String> dataLst = (ArrayList<String>) exceptionCode.getResponse();
//								excesPath = excesPath + configVb.getTemplateId() + extension;
								String csvFilename = configVb.getTemplateId().trim() + "_"
										+ lst.get(0).getCbkFileName().trim() + "_"
										+ templateConfigVb.getReportingDate();
								excesPath = excesPath + csvFilename + extension;
								exceptionCode = templateScheduleCronDao.csvFileWriter(dataLst, excesPath, "|");
								exceptionCodeFinal.setErrorCode(Constants.SUCCESSFUL_OPERATION);
								exceptionCodeFinal.setCsvPath(excesPath);
								exceptionCodeFinal.setCsvFileName(csvFilename);
								exceptionCodeFinal.setRequest(configVb.getTypeOfSubmission());
								exceptionCodeFinal.setOtherInfo(lst.get(0));

							}
						}

					}
					if (DB.equalsIgnoreCase(configVb.getSourceType())
							&& ZIP.equalsIgnoreCase(configVb.getTypeOfSubmission())) {

						extension = ".csv";
						if (exceptionCode2.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
							ArrayList<TemplateConfigVb> lst = (ArrayList<TemplateConfigVb>) exceptionCode2
									.getResponse();
							templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
									"CSV Formattion for Template[" + configVb.getTemplateDescription() + "] Started",
									"CSV Formattion for Template[" + configVb.getTemplateDescription() + "] Started");
							lst.get(0).setReportingDate(templateConfigVb.getReportingDate());
							exceptionCode = formCsv(lst.get(0));
							if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
								templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
										"CSV Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Finished",
										"CSV Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Finished");
								ArrayList<String> dataLst = (ArrayList<String>) exceptionCode.getResponse();
								String csvPath = excesPath;
								excesPath = excesPath + lst.get(0).getCbkFileName() + extension;

								exceptionCode = templateScheduleCronDao.csvFileWriter(dataLst, excesPath,
										configVb.getCsvDelimiter());
								String zipFilePath = csvPath + lst.get(0).getCbkFileName() + ".zip"; // Use the same
																										// name for the
																										// ZIP file
								templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
										"Zip  Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Started",
										"Zip Formattion for Template[" + configVb.getTemplateDescription()
												+ "] Started");
								try (FileOutputStream fos = new FileOutputStream(zipFilePath);
										ZipOutputStream zipOut = new ZipOutputStream(fos)) {

									// Add the CSV file to the ZIP
									ZipUtils.addFileToZip(excesPath, zipOut);

									System.out.println("CSV file successfully zipped at: " + zipFilePath);
									templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
											"Zip Formattion for Template[" + configVb.getTemplateDescription()
													+ "] Finished",
											"Zip Formattion for Template[" + configVb.getTemplateDescription()
													+ "] Finished");
								} catch (Exception e) {
									templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
											"Zip Formattion for Template[" + configVb.getTemplateDescription()
													+ "] Failed",
											"Zip Formattion for Template[" + configVb.getTemplateDescription()
													+ "] Failed");
									e.printStackTrace();
								}
								exceptionCodeFinal.setErrorCode(Constants.SUCCESSFUL_OPERATION);
								exceptionCodeFinal.setCsvPath(zipFilePath);
								exceptionCodeFinal.setCsvFileName(configVb.getTemplateId() + "_"
										+ templateConfigVb.getReportingDate().replaceAll("/", "_"));
								exceptionCodeFinal.setRequest(configVb.getTypeOfSubmission());
								exceptionCodeFinal.setOtherInfo(lst.get(0));

							}
						}

					}

				} else {
					templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
							"Getting Template Schedule Detail Failed ", "Getting Template Schedule Details["
									+ templateConfigVb.getTemplateDescription() + "] Failed");
					exceptionCodeFinal.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCodeFinal.setErrorMsg("No data to process ");
				}
			}
		} catch (Exception ex) {
			exceptionCodeFinal.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCodeFinal.setErrorMsg(ex.getMessage());
		}
		return exceptionCodeFinal;
	}

	public ExceptionCode getQueryResults(TemplateConfigVb vObject) {
		int intStatus = 1;
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateMappingVb> templatMappinglst = new ArrayList<TemplateMappingVb>();
		vObject.setReview(false);
		insertAuditTrialData(vObject, "Getting Template Config Detail  ",
				"Getting Template Config Details for Template [" + vObject.getTemplateDescription() + "] ");
		List<TemplateConfigVb> collTemp = templateScheduleCronDao.getQueryResults(vObject);
		if (collTemp == null || collTemp.size() == 0) {
			insertAuditTrialData(vObject, "Getting Template Config Detail Failed ",
					"Getting Template Config Details for Template[" + vObject.getTemplateDescription() + "] Failed");
			exceptionCode = CommonUtils.getResultObject("", 16, "Query", "");
			exceptionCode.setOtherInfo(vObject);
			return exceptionCode;
		} else {
			insertAuditTrialData(vObject, "Getting Template Config Detail Success ",
					"Getting Template Config Details for Template[" + vObject.getTemplateDescription() + "] Success");
			TemplateMappingVb templateMappingVb = new TemplateMappingVb();
			templateMappingVb.setCountry(vObject.getCountry());
			templateMappingVb.setLeBook(vObject.getLeBook());
			templateMappingVb.setTemplateId(vObject.getTemplateId());
			insertAuditTrialData(vObject, "Getting Template Mapping Detail  ",
					"Getting Template Mapping Details for Template[" + vObject.getTemplateDescription() + "] ");
			templatMappinglst = templateScheduleCronDao.getQueryMappingList(templateMappingVb);

			// templatMappinglst = templateMappingDao.getQueryResults(vObject,intStatus);

			if (templatMappinglst != null && !templatMappinglst.isEmpty()) {
				insertAuditTrialData(vObject, "Getting Template Mapping Detail Success ",
						"Getting Template Mapping Details for Template[" + vObject.getTemplateDescription()
								+ "] Success");
				collTemp.get(0).setMappinglst(templatMappinglst);
				exceptionCode = CommonUtils.getResultObject("", Constants.SUCCESSFUL_OPERATION, "Query", "");
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setResponse(collTemp);
			} else {
				insertAuditTrialData(vObject, "Getting Template Mapping Detail Failed ",
						"Getting Template Mapping Details for Template[" + vObject.getTemplateDescription()
								+ "] Failed");
				exceptionCode = CommonUtils.getResultObject("", 16, "Query", "");
				exceptionCode.setOtherInfo(vObject);
				exceptionCode.setResponse(collTemp);
			}
//			System.out.println(collTemp.toString());
			return exceptionCode;
		}
	}

	public ExceptionCode formJson(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		PreparedStatement pstmt = null;
		Connection conn;
		boolean flag;
		ArrayList result = new ArrayList();
		JSONArray resultArray = new JSONArray();
		StringJoiner selectColumns = new StringJoiner(",");
		String json = vObject.getMainAPIStructure();
		ObjectMapper obj = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			String query = "SELECT ";
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				selectColumns.add(templateMappingVb.getSourceColumn());
			}
			query = query + selectColumns.toString() + " FROM " + vObject.getSourceTable();
			String versionNo = " WHERE VERSION_NO = (SELECT MAX(VERSION_NO ) FROM " + vObject.getSourceTable() + " )";
			query += versionNo;
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				query = query + " AND " + vObject.getSourceTableFilter();
			}
			insertAuditTrialData(vObject, "Fetching DB Script ", "Fetching DB Script ");
			String varScript = dynamicScriptCreation(vObject);
			if (ValidationUtil.isValid(varScript)) {
				insertAuditTrialData(vObject, "Fetching DB Script Success ", "Fetching DB Script Success ");
				insertAuditTrialData(vObject, "DB Connection Started", "DB Connection Started");
				conn = CommonUtils.getDBConnection(varScript);
				if (conn != null) {
					insertAuditTrialData(vObject, "DB Connection Success", "DB Connection Success");
					pstmt = conn.prepareStatement(query);
					ResultSet rs = pstmt.executeQuery();
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while (rs.next()) {
						JsonNode n1 = obj.readTree(json);
						LinkedHashMap<String, String> resultData = new LinkedHashMap<String, String>();
						if (n1.isObject()) {
							Iterator<String> on = n1.fieldNames();
							while (on.hasNext()) {
								for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
									dataPresent = true;
									for (int cn = 1; cn <= colCount; cn++) {
										String columnName = metaData.getColumnName(cn).toUpperCase();
										int columnType = metaData.getColumnType(cn);
										if (columnName
												.equalsIgnoreCase(templateMappingVb.getSourceColumn().toUpperCase())) {
											String targetCol = templateMappingVb.getTargetColumn().toUpperCase();
											String col = on.next().toUpperCase();
											if (col.equalsIgnoreCase(targetCol)) {
//												System.out.println(col+" : "+columnName);
												String columnValue;
												if (columnType == java.sql.Types.DATE
														|| columnType == java.sql.Types.TIMESTAMP) {
													Date date = rs.getDate(columnName);
													columnValue = date != null ? dateFormat.format(date) : "";
												} else {
													columnValue = rs.getString(columnName);
												}
												resultData.put(col.replaceAll("_", " "), columnValue);
												break;
											}
										}
									}
								}
							}
							result.add(resultData);
						}
//					
					}

					if (dataPresent) {
						ObjectMapper objectMapper = new ObjectMapper();
						String jsonArray = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
						exceptionCode.setResponse(jsonArray.toString());
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
						exceptionCode.setResponse1(result);
					} else {
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("No data found");
						insertAuditTrialData(vObject, "No Data to process ", "No Data to process ");
					}
				} else {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Unable to connect DB [" + getValue(varScript, "USER") + "]");
					insertAuditTrialData(vObject, "DB Connection Failed", "DB Connection Failed");
				}
			} else {
				insertAuditTrialData(vObject, "Fetching DB Script Failed ", "Fetching DB Script Failed ");
				exceptionCode.setErrorMsg("Script not maintained properly!! ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorSevr("N");
		}
		return exceptionCode;
	}

	public ExceptionCode formCsv(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		PreparedStatement pstmt = null;
		Connection conn;
		boolean flag;
		ArrayList result = new ArrayList();
		StringJoiner selectColumns = new StringJoiner(",");
		try {
			String query = "SELECT ";
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				selectColumns.add(templateMappingVb.getSourceColumn());
			}
			query = query + selectColumns.toString() + " FROM " + vObject.getSourceTable();
			String versionNo = " WHERE VERSION_NO = (SELECT MAX(VERSION_NO ) FROM " + vObject.getSourceTable() + " )";
			query += versionNo;
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				query = query + " AND " + vObject.getSourceTableFilter();
			}
			insertAuditTrialData(vObject, "Fetching DB Script ", "Fetching DB Script ");
			String varScript = dynamicScriptCreation(vObject);
			if (ValidationUtil.isValid(varScript)) {
				insertAuditTrialData(vObject, "Fetching DB Script Success ", "Fetching DB Script Success ");
				insertAuditTrialData(vObject, "DB Connection Started", "DB Connection Started");
				conn = CommonUtils.getDBConnection(varScript);
				if (conn != null) {
					pstmt = conn.prepareStatement(query);
					ResultSet rs = pstmt.executeQuery();
					ResultSetMetaData metaData = rs.getMetaData();
					int colCount = metaData.getColumnCount();
					Boolean dataPresent = false;
					while (rs.next()) {
						LinkedHashMap<String, String> resultData = new LinkedHashMap<String, String>();
						dataPresent = true;
						for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
							for (int cn = 1; cn <= colCount; cn++) {
								String columnName = templateMappingVb.getSourceColumn();
								if (columnName.equalsIgnoreCase(templateMappingVb.getSourceColumn())) {
									resultData.put(templateMappingVb.getTargetColumn().toUpperCase(),
											rs.getString(columnName));
									break;
								}
							}

						}

						result.add(resultData);
					}
					if (dataPresent) {
						exceptionCode.setResponse(result);
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					} else {

						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
						exceptionCode.setErrorMsg("No data found");
						insertAuditTrialData(vObject, "No Data to process ", "No Data to process ");

					}
				} else {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("Unable to connect DB [" + getValue(varScript, "USER") + "]");
					insertAuditTrialData(vObject, "DB Connection Failed", "DB Connection Failed");
				}
			} else {
				insertAuditTrialData(vObject, "Fetching DB Script Failed ", "Fetching DB Script Failed ");
				exceptionCode.setErrorMsg("Script not maintained properly!! ");
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorSevr("N");
		}
		return exceptionCode;
	}

	public String dynamicScriptCreation(TemplateConfigVb vObject) {
		JSONObject extractVbData = new JSONObject(vObject);
		StringBuilder scriptFormation = new StringBuilder();
		if (ValidationUtil.isValid(vObject.getTagList())) {
			JSONArray tagListArr = (JSONArray) extractVbData.getJSONArray("tagList");
			if (tagListArr.length() != 0) {
				if ("DB".equalsIgnoreCase(vObject.getConnectionType())) {
					scriptFormation.append("{DATABASE_TYPE:#CONSTANT$@!" + vObject.getDbConnectivityType() + "#}");
					// scriptFormation.append("{DB_URL:#CONSTANT$@!" + vObject.getDbUrl() + "#}");
				}

				for (int i = 0; i < tagListArr.length(); i++) {
					JSONObject jsonObj = tagListArr.getJSONObject(i);
					String ch = fixJSONObject(jsonObj);
					JSONObject jsonData = new JSONObject(ch);
					String tagName = jsonData.getString("TAG");
					String encryption = jsonData.getString("ENCRYPTION");
					String value = jsonData.getString("VALUE");
					if ("yes".equalsIgnoreCase(encryption)) {
						scriptFormation.append("{" + tagName + ":#ENCRYPT$@!" + value + "#}");
					} else {
						scriptFormation.append("{" + tagName + ":#CONSTANT$@!" + value + "#}");
					}
				}
			}

			return String.valueOf(scriptFormation);
		} else {
			return templateScheduleCronDao.getScript(vObject);
		}
	}

	public String fixJSONObject(JSONObject obj) {
		String jsonString = obj.toString();
		for (int i = 0; i < obj.names().length(); i++) {
			try {
				jsonString = jsonString.replace(obj.names().getString(i), obj.names().getString(i).toUpperCase());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonString;
	}

//	@Transactional(rollbackForClassName={"com.vision.exception.RuntimeCustomException"})
	public synchronized int insertAuditTrialData(TemplateConfigVb vObject, String description, String detailDesc) {
		int auditSequence = templateScheduleCronDao.getMaxSequence(vObject);
		auditSequence++;
		int intValue = templateScheduleCronDao.insertAuditTrailSubmitData(vObject, auditSequence, description,
				detailDesc);
		return intValue;
	}

	public String getValue(String source, String key) {
		try {
//				 Matcher regexMatcher = Pattern.compile(key+"(.*?\\$@!)(.*?)#\\}").matcher(source);
			Matcher regexMatcher = Pattern.compile("\\{" + key + ":#(.*?)\\$@!(.*?)\\#}").matcher(source);
			return regexMatcher.find() ? regexMatcher.group(2) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public ExceptionCode formXml(TemplateConfigVb vObject) {
		ExceptionCode exceptionCode = new ExceptionCode();
		PreparedStatement pstmt = null;
		Connection conn = null;
		StringBuilder xmlOutput = new StringBuilder();
		StringJoiner selectColumns = new StringJoiner(",");

		try {
			// Constructing the SQL query
			String query = "SELECT ";
			for (TemplateMappingVb templateMappingVb : vObject.getMappinglst()) {
				selectColumns.add(templateMappingVb.getSourceColumn());
			}
			query += selectColumns.toString() + " FROM " + vObject.getSourceTable();
			if (ValidationUtil.isValid(vObject.getSourceTableFilter())
					&& !"NA".equalsIgnoreCase(vObject.getSourceTableFilter())) {
				query += " WHERE " + vObject.getSourceTableFilter();
			}

			// Generate dynamic script and get the connection
			String varScript = dynamicScriptCreation(vObject);
			if (ValidationUtil.isValid(varScript)) {
				conn = CommonUtils.getDBConnection(varScript);
				if (conn != null) {
					pstmt = conn.prepareStatement(query);
					ResultSet rs = pstmt.executeQuery();
					ResultSetMetaData metaData = rs.getMetaData();
					int columnCount = metaData.getColumnCount();
					boolean dataPresent = false;

					// Start building the XML document
					xmlOutput.append("<Results>");

					while (rs.next()) {
						dataPresent = true;
						xmlOutput.append("<crs:AccountReport>");
						xmlOutput.append("<crs:ControllingPerson>");

						for (int i = 1; i <= columnCount; i++) {
							String columnName = metaData.getColumnName(i);
							String value = rs.getString(i);

							if (!ValidationUtil.isValid(value)) {
								value = ""; // Handle null or invalid values
							}

							// Transform the column name to XML tag format
							columnName = ValidationUtil.toTitleCase(columnName);
							columnName = columnName.replaceAll("_", "");

							// Create XML element
							String tagStart = "<crs:" + columnName + ">";
							String tagEnd = "</crs:" + columnName + ">";
							xmlOutput.append(tagStart).append(value).append(tagEnd);
						}

						xmlOutput.append("</crs:ControllingPerson>");
						xmlOutput.append("</crs:AccountReport>");
					}

					xmlOutput.append("</Results>");

					if (dataPresent) {
						exceptionCode.setResponse(xmlOutput.toString());
						exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					} else {
						exceptionCode.setErrorMsg("No data found.");
						exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					}
				}
			} else {
				exceptionCode.setErrorMsg("Script not maintained properly!!");
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			}
		} catch (Exception e) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
			exceptionCode.setErrorSevr("N");
		} finally {
			// Close resources
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return exceptionCode;
	}

	public ExceptionCode csvformation(TemplateConfigVb templateConfigVb, ExceptionCode exceptionCode) {
		String excesPath = commonDao.findVisionVariableValue("RG_EXCES_LOGPATH");
		String extension = ".csv";
		if (exceptionCode.getErrorCode() == Constants.SUCCESSFUL_OPERATION) {
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
					"CSV Formattion for Template[" + templateConfigVb.getTemplateDescription() + "] Finished",
					"CSV Formattion for Template[" + templateConfigVb.getTemplateDescription() + "] Started ");
			ArrayList<String> dataLst = (ArrayList<String>) exceptionCode.getResponse1();
			excesPath = excesPath + templateConfigVb.getDataList() + extension;
			exceptionCode = templateScheduleCronDao.csvFileWriter(dataLst, excesPath, "|");
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb,
					"CSV Formattion for Template[" + templateConfigVb.getTemplateDescription() + "] Finished",
					"CSV Formattion for Template[" + templateConfigVb.getTemplateDescription() + "] Finished");
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			exceptionCode.setCsvPath(excesPath);
			exceptionCode.setCsvFileName(templateConfigVb.getTemplateId());
			exceptionCode.setRequest(templateConfigVb.getTypeOfSubmission());
			exceptionCode.setOtherInfo(templateConfigVb);

		}
		return exceptionCode;
	}

	public ExceptionCode fetchAuthenticationCode(TemplateConfigVb templateConfigVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		try {
			templateScheduleCronDao.insertAuditTrialData(templateConfigVb, "Fetching Authorization Token ",
					"Fetching Authorization Token ");
			Map<String, Object> row = templateScheduleDao.fetchActiveAuthToken();
			boolean needNewToken = true;
			if (row != null) {
				Timestamp validTill = (Timestamp) row.get("VALID_TILL");
				String existingToken = (String) row.get("AUTH_TOKEN");
				Timestamp now = new Timestamp(System.currentTimeMillis());

				if (validTill != null && now.before(validTill)) {
					// Still valid
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					exceptionCode.setResponse(existingToken);
					needNewToken = false;
				} else {
					// Expired — mark STATUS = 9
					// expired → call separate method
					templateScheduleDao.markTokenAsExpired(existingToken);
				}
			}
			if (needNewToken) {
				// ✅ Call your token fetch method to get fresh token
				exceptionCode = getAuthenticationCode(templateConfigVb);
			}

		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

//	public ExceptionCode getAuthenticationCode(TemplateConfigVb templateConfigVb) {
//		ExceptionCode exceptionCode = new ExceptionCode();
//		String TOKEN_URL = templateScheduleDao.getAuthTokenURl(templateConfigVb);
//		if (!ValidationUtil.isValid(TOKEN_URL)) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg("Authentication Token URL is not valid/maintianed");
//		}
//
//		String GRANT_TYPE = "client_credentials"; // or another grant type
//
//		System.out.println("TOKEN_URL :" + TOKEN_URL);
//		System.out.println("CLIENT_ID :" + clientID);
//		System.out.println("CLIENT_SECRET :" + clientSecret);// your
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//			// Prepare form data
//			StringBuilder formData = new StringBuilder();
//			formData.append("client_id=").append(clientID).append("&client_secret=").append(clientSecret)
//					.append("&grant_type=").append(GRANT_TYPE).append("&scope=").append(scope);
//
//			// Set request body
//			StringEntity entity = new StringEntity(formData.toString());
//
//			httpPost.setEntity(entity);
//
////			 Execute request
//			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//				// Check response status code
//
//				int statusCode = response.getStatusLine().getStatusCode();
//				if (statusCode == 200) {
//					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//					// Parse JSON response
//					String responseBody = EntityUtils.toString(response.getEntity());
//					ObjectMapper objectMapper = new ObjectMapper();
//					Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
//					String access_token = (String) responseData.get("access_token");
//					exceptionCode.setResponse(access_token);
//					exceptionCode.setErrorMsg(responseBody);
//				} else {
//					System.out.println("Failed to get access token. Response code: " + statusCode);
//					String responseBody = EntityUtils.toString(response.getEntity());
//					System.out.println("Response body: " + responseBody);
//					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//					exceptionCode.setErrorMsg(responseBody);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//		return exceptionCode;
//	}
//	public ExceptionCode getAuthenticationCode(TemplateConfigVb templateConfigVb) {
//		ExceptionCode exceptionCode = new ExceptionCode();
//		String TOKEN_URL = templateScheduleDao.getAuthTokenURl(templateConfigVb);
//		if (!ValidationUtil.isValid(TOKEN_URL)) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg("Authentication Token URL is not valid/maintained");
//			return exceptionCode;
//		}
//
//		String GRANT_TYPE = "client_credentials"; // or your grant type
//
//		System.out.println("TOKEN_URL: " + TOKEN_URL);
//		System.out.println("CLIENT_ID: " + clientID);
//		System.out.println("CLIENT_SECRET: " + clientSecret);
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//			// Prepare form data
//			StringBuilder formData = new StringBuilder();
//			formData.append("client_id=").append(clientID).append("&client_secret=").append(clientSecret)
//					.append("&grant_type=").append(GRANT_TYPE).append("&scope=").append(scope);
//
//			// Set request body
//			StringEntity entity = new StringEntity(formData.toString());
//
//			httpPost.setEntity(entity);
//
////			 Execute request
//			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//				// Check response status code
//
//				int statusCode = response.getStatusLine().getStatusCode();
//				if (statusCode == 200) {
//					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//					// Parse JSON response
//					String responseBody = EntityUtils.toString(response.getEntity());
//					ObjectMapper objectMapper = new ObjectMapper();
//					Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
//					String access_token = (String) responseData.get("access_token");
//					exceptionCode.setResponse(access_token);
//					exceptionCode.setErrorMsg(responseBody);
//				} else {
//					System.out.println("Failed to get access token. Response code: " + statusCode);
//					String responseBody = EntityUtils.toString(response.getEntity());
//					System.out.println("Response body: " + responseBody);
//					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//					exceptionCode.setErrorMsg(responseBody);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//		return exceptionCode;
//	}
//	public ExceptionCode getAuthenticationCode(TemplateConfigVb templateConfigVb) {
//		ExceptionCode exceptionCode = new ExceptionCode();
//		String TOKEN_URL = templateScheduleDao.getAuthTokenURl(templateConfigVb);
//		if (!ValidationUtil.isValid(TOKEN_URL)) {
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg("Authentication Token URL is not valid/maintained");
//			return exceptionCode;
//		}
//
//		String GRANT_TYPE = "client_credentials"; // or your grant type
//
//		System.out.println("TOKEN_URL: " + TOKEN_URL);
//		System.out.println("CLIENT_ID: " + clientID);
//		System.out.println("CLIENT_SECRET: " + clientSecret);
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//
//			HttpPost httpPost = new HttpPost(TOKEN_URL);
//			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//			// Prepare form data
//			StringBuilder formData = new StringBuilder();
//			formData.append("client_id=").append(clientID).append("&client_secret=").append(clientSecret)
//					.append("&grant_type=").append(GRANT_TYPE).append("&scope=").append(scope);
//
//			// Set request body
//			StringEntity entity = new StringEntity(formData.toString());
//			httpPost.setEntity(entity);
//
//			// Execute request
//			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
////					int statusCode = response.getStatusLine().getStatusCode();
//					int statusCode = response.getCode();
//				String responseBody = EntityUtils.toString(response.getEntity());
//
//				if (statusCode == 200) {
//					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
//
//					// Parse JSON response
//					ObjectMapper objectMapper = new ObjectMapper();
//					Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
//					String access_token = (String) responseData.get("access_token");
//					exceptionCode.setResponse(access_token);
//					exceptionCode.setErrorMsg(responseBody);
//
//					// Extract exp & subtract 5 min
//					String[] parts = access_token.split("\\.");
//					if (parts.length < 2) {
//						throw new IllegalArgumentException("Invalid token format");
//					}
//
//					String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
//					Map<String, Object> claims = new ObjectMapper().readValue(payload, Map.class);
//					long expEpoch = ((Number) claims.get("exp")).longValue();
//
//					LocalDateTime expTime = Instant.ofEpochSecond(expEpoch).atZone(ZoneId.systemDefault())
//							.toLocalDateTime();
//					Timestamp dbTimestamp = Timestamp.valueOf(expTime);
//
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(dbTimestamp);
//					calendar.add(Calendar.MINUTE, -5);
//					Timestamp validTill = new Timestamp(calendar.getTimeInMillis());
//
//					System.out.println("Token EXP: " + dbTimestamp);
//					System.out.println("VALID_TILL (minus 5 min): " + validTill);
//
//					// Insert into RG_AUTH_TOKEN
//					templateScheduleDao.insertIntoAuthToken(access_token, validTill);
//
//				} else {
//					System.out.println("Failed to get access token. Response code: " + statusCode);
//					System.out.println("Response body: " + responseBody);
//					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//					exceptionCode.setErrorMsg(responseBody);
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//			exceptionCode.setErrorMsg(e.getMessage());
//		}
//
//		return exceptionCode;
//
//	}
	public ExceptionCode getAuthenticationCode(TemplateConfigVb templateConfigVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		String TOKEN_URL = templateScheduleDao.getAuthTokenURl(templateConfigVb);
		if (!ValidationUtil.isValid(TOKEN_URL)) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("Authentication Token URL is not valid/maintianed");
		}

		String GRANT_TYPE = "client_credentials"; // or another grant type

		System.out.println("TOKEN_URL :" + TOKEN_URL);
		System.out.println("CLIENT_ID :" + clientID);
		System.out.println("CLIENT_SECRET :" + clientSecret);// your

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			HttpPost httpPost = new HttpPost(TOKEN_URL);
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

			// Prepare form data
			StringBuilder formData = new StringBuilder();
			formData.append("client_id=").append(clientID).append("&client_secret=").append(clientSecret)
					.append("&grant_type=").append(GRANT_TYPE).append("&scope=").append(scope);

			// Set request body
			StringEntity entity = new StringEntity(formData.toString());

			httpPost.setEntity(entity);

//			 Execute request
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				// Check response status code

				int statusCode = response.getCode();
				if (statusCode == 200) {
					exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
					// Parse JSON response
					String responseBody = EntityUtils.toString(response.getEntity());
					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, Object> responseData = objectMapper.readValue(responseBody, Map.class);
					String access_token = (String) responseData.get("access_token");
					exceptionCode.setResponse(access_token);
					exceptionCode.setErrorMsg(responseBody);
				} else {
					System.out.println("Failed to get access token. Response code: " + statusCode);
					String responseBody = EntityUtils.toString(response.getEntity());
					System.out.println("Response body: " + responseBody);
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg(responseBody);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}
		return exceptionCode;
	}

	public ExceptionCode postData(String institutionCode, String requestId, String isFIleAttached, String reportingDate,
			ExceptionCode exceptionCodeNew, Object authToken) {
		TemplateConfigVb templateConfigVb = (TemplateConfigVb) exceptionCodeNew.getOtherInfo();

		String TOKEN_URL = templateConfigVb.getApiConnectivityDetails();
		String dataList = templateConfigVb.getDataList().trim();
		ExceptionCode exceptionCode = new ExceptionCode();

		String logPath = commonDao.findVisionVariableValue("RG_CBK_LOGPATH");
		String fileName = (String) exceptionCodeNew.getCsvFileName();
		String fileExtension = ".log"; //
		String responseBody = "";
		String requestNo = "";
		String cbStatus = "";
		String jsonFilePath = commonDao.findVisionVariableValue("RG_EXCES_LOGPATH");
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			// Create HttpPost request
			HttpPost httpPost = new HttpPost(TOKEN_URL);
			httpPost.setHeader("Authorization", "Bearer " + authToken);

			SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = inputFormat.parse(reportingDate);
			String formattedDate = date1 != null ? outputFormat.format(date1) : "";

			String boundary = "boundary123";
			HttpEntity multipartEntity = null;
			if (JSON.equalsIgnoreCase(templateConfigVb.getTypeOfSubmission())) {
				JSONArray datasArray = new JSONArray((String) exceptionCodeNew.getResponse());

				System.out.println(formattedDate);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("INSTITUTION_CODE", institutionCode);
				jsonObject.put("REQUEST_ID", requestId);
				jsonObject.put("IS_ATTACHED", isFIleAttached);
				jsonObject.put("REPORTING_DATE", formattedDate);
				jsonObject.put(dataList, datasArray);
				System.out.println(jsonObject.toString());
				// Convert the JSON object to StringBody

				File jsonFile = new File(jsonFilePath + File.separator + fileName + ".JSON");
				try (FileWriter fileWriter = new FileWriter(jsonFile)) {
					fileWriter.write(jsonObject.toString());
					System.out.println("JSON data written to file: " + jsonFilePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				long fileSizeInBytes = jsonFile.length();
				// if the file file size more than 10 mb
				String jsonSize = commonDao.findVisionVariableValue("CB_JSON_SIZE");
				int size = ValidationUtil.isValid(jsonSize) ? Integer.parseInt(jsonSize) : 100;
				if (fileSizeInBytes > size) {
					exceptionCode = csvformation(templateConfigVb, exceptionCodeNew);
					String filePath = (String) exceptionCode.getCsvPath();
					File csvFile = new File(filePath);

					if (csvFile.exists()) {
						// Attach the CSV file
						System.out.println(filePath);
						JSONObject jsonObject1 = new JSONObject();
						jsonObject1.put("INSTITUTION_CODE", institutionCode);
						jsonObject1.put("REQUEST_ID", requestId);
						jsonObject1.put("IS_ATTACHED", "Y");
						jsonObject1.put("REPORTING_DATE", formattedDate);
						jsonObject1.put(dataList, new JSONArray());
						logger.info("jsonObject1 :" +jsonObject1.toString());
						FileBody csvFileBody = new FileBody(csvFile, ContentType.DEFAULT_BINARY);
						StringBody jsonDataPart = new StringBody(jsonObject1.toString(), ContentType.APPLICATION_JSON);
						multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
								.addPart("data", jsonDataPart).addPart("file", csvFileBody) // CSV file part
								.build();
						exceptionCode.setCsvFileName(filePath);
						httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
					}
				} else {
//					httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
					httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
					StringBody jsonDataPart = new StringBody(jsonObject.toString(), ContentType.APPLICATION_JSON);
					multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
							.addPart("data", jsonDataPart).build();
				}
			} else if (ZIP.equalsIgnoreCase(templateConfigVb.getTypeOfSubmission())) {
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("INSTITUTION_CODE", institutionCode);
				jsonObject1.put("REQUEST_ID", requestId);
				jsonObject1.put("IS_ATTACHED", "Y");
				jsonObject1.put("REPORTING_DATE", formattedDate);
				jsonObject1.put(dataList, new JSONArray());
				 System.out.println(jsonObject1.toString());
				 logger.info("jsonObject1 :" +jsonObject1.toString());
				String zipFilePath = (String) exceptionCodeNew.getCsvPath();
				File zipFile = new File(zipFilePath);
				System.out.println("PAth" + zipFilePath);

				if (zipFile.exists()) {

					FileBody zipFileBody = new FileBody(zipFile, ContentType.APPLICATION_OCTET_STREAM);
					StringBody jsonDataPart = new StringBody(jsonObject1.toString(), ContentType.APPLICATION_JSON);
					System.out.println("zipfile Body" + zipFileBody.getFile().getAbsolutePath());
					multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
							.addPart("data", jsonDataPart).addPart("file", zipFileBody).build();
					System.out.println(jsonObject1.toString());
				}
			} else if (CSV.equalsIgnoreCase(templateConfigVb.getTypeOfSubmission())) {
				String filePath = (String) exceptionCodeNew.getCsvPath();
				File csvFile = new File(filePath);

				if (csvFile.exists()) {
					// Attach the CSV file
					System.out.println(filePath);
					JSONObject jsonObject1 = new JSONObject();
					jsonObject1.put("INSTITUTION_CODE", institutionCode);
					jsonObject1.put("REQUEST_ID", requestId);
					jsonObject1.put("IS_ATTACHED", "Y");
					jsonObject1.put("REPORTING_DATE", formattedDate);
					jsonObject1.put(dataList, new JSONArray());
					FileBody csvFileBody = new FileBody(csvFile, ContentType.APPLICATION_OCTET_STREAM);
					StringBody jsonDataPart = new StringBody(jsonObject1.toString(), ContentType.APPLICATION_JSON);
					multipartEntity = MultipartEntityBuilder.create().setBoundary(boundary)
							.addPart("data", jsonDataPart).addPart("file", csvFileBody) // CSV file part
							.build();
					exceptionCode.setCsvFileName(filePath);
					httpPost.setHeader("Content-Type", "multipart/mixed; boundary=boundary123");
				} else {
					exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
					exceptionCode.setErrorMsg("File Not Found");
				}

			}
			httpPost.setEntity(multipartEntity);
			

//			 Execute request
			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {

			    int statusCode = response.getCode();
			    responseBody = EntityUtils.toString(response.getEntity());

			    logger.info("HTTP Status Code: {}", statusCode);
			    logger.info("Response Body: {}", responseBody);
			    
			    logger.error("HTTP Status Code: {}", statusCode);
			    logger.error("Response Body: {}", responseBody);

			    if (statusCode == 200) {

			        exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
			        exceptionCode.setErrorMsg(responseBody);

			        if (responseBody != null && responseBody.trim().startsWith("{")) {
			            JSONObject jsonResponse = new JSONObject(responseBody);

			            requestNo = jsonResponse.optString("RequestNo", "");
			            cbStatus  = jsonResponse.optString("Status", "SUCCESS");
			        } else {
			            cbStatus = responseBody;
			        }

			    } else {

			        exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			        exceptionCode.setErrorMsg(responseBody);

			        if (responseBody != null && responseBody.trim().startsWith("{")) {
			            JSONObject jsonResponse = new JSONObject(responseBody);

			            requestNo = jsonResponse.optString("RequestNo", "");
			            cbStatus  = jsonResponse.optString("httpMessage", "FAILED");
			        } else {
			            cbStatus = responseBody;
			        }
			    }

			    exceptionCode.setResponse1(requestNo);
			    exceptionCode.setResponse2(cbStatus);
			}

			if (responseBody == null || responseBody.isEmpty()) {
				responseBody = "{\"Status:\"\"No response received or an error occurred\"}";
			}
			// Write the responseBody to a file
			File logFile = Paths.get(logPath, fileName + fileExtension).toFile();
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
				writer.write(responseBody);
				System.out.println("Response body written to file: " + logFile.getAbsolutePath());
				logger.info("Response body written to file: " + logFile.getAbsolutePath());
			} catch (IOException ioe) {
				ioe.printStackTrace();
				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
				exceptionCode.setErrorMsg(ioe.getMessage());
				System.out.println("Failed to write response body to file: " + logFile.getAbsolutePath());
				logger.info("Failed to write response body to file: " + logFile.getAbsolutePath());
			}
			if (ZIP.equalsIgnoreCase(templateConfigVb.getTypeOfSubmission())) {
				String datetimeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
				System.out.println(datetimeStamp);
				ZipUtils.renameFileWithExtension((String) exceptionCodeNew.getCsvPath(),
						templateConfigVb.getCbkFileName() + "_" + datetimeStamp);
			}

		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg(e.getMessage());
		}

		return exceptionCode;
	}

	public ExceptionCode auditLogDownload(TemplateScheduleVb templateScheduleVb) {
		ExceptionCode exceptionCode = new ExceptionCode();
		List<TemplateScheduleVb> tempSchedules = null;
		templateScheduleVb.setReview(true);
		tempSchedules = templateScheduleDao.getTemplateSchedulesAudit(templateScheduleVb);
		if (tempSchedules == null || tempSchedules.isEmpty()) {
//	            System.out.println("No data to export.");
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setErrorMsg("No data to export.");
			return exceptionCode;
		}
		exceptionCode = exportListToExcel(tempSchedules,
				templateScheduleVb.getTemplateId().trim() + "_" + templateScheduleVb.getReportingDate(),
				templateScheduleVb.getTemplateId().trim().toUpperCase());

		return exceptionCode;
	}

	public ExceptionCode exportListToExcel(List<TemplateScheduleVb> dataList, String fileName, String sheetName) {
		ExceptionCode exceptionCode = new ExceptionCode();

		if (dataList == null || dataList.isEmpty()) {
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
			exceptionCode.setOtherInfo("No data available to export.");
			return exceptionCode;
		}

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet(sheetName);
			String tmpFilePath = System.getProperty("java.io.tmpdir");
			String excelFilePath = tmpFilePath + File.separator + fileName + "_AUDIT_LOGS.xlsx";

			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("AUDIT_TRAIL_SEQUENCE_ID");
			header.createCell(1).setCellValue("DATETIME_STAMP");
			header.createCell(2).setCellValue("AUDIT_DESCRIPTION");
			header.createCell(3).setCellValue("AUDIT_DESCRIPTION_DETAIL");

			int rowNum = 1;
			for (TemplateScheduleVb item : dataList) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(item.getAuditTrailSequenceId());
				row.createCell(1).setCellValue(item.getDateTimeStamp().toString()); // Or format as needed
				row.createCell(2).setCellValue(item.getAuditDescription());
				row.createCell(3).setCellValue(item.getAuditDescriptionDetail());
			}

			for (int i = 0; i < 4; i++) {
				sheet.autoSizeColumn(i);
			}

			try (FileOutputStream out = new FileOutputStream(excelFilePath)) {
				workbook.write(out);
			}

			System.out.println("Excel exported successfully: " + excelFilePath);
			exceptionCode.setResponse(tmpFilePath);
			exceptionCode.setOtherInfo(fileName + "_AUDIT_LOGS");
			exceptionCode.setErrorCode(Constants.SUCCESSFUL_OPERATION);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
		}

		return exceptionCode;
	}

//	public ExceptionCode bulkReject(TemplateScheduleVb queryPopObj){
//		ExceptionCode exceptionCode  = null;
//		try{
//			setVerifReqDeleteType(queryPopObj);
//			List<TemplateConfigVb> collTemp = templateScheduleDao.getQueryResults(queryPopObj);
//			if (collTemp != null && !collTemp.isEmpty()) {
//				queryPopObj.setSourceTable(collTemp.get(0).getSourceTable());
//			}else {
//				exceptionCode= new ExceptionCode();
//				exceptionCode.setErrorCode(Constants.ERRONEOUS_OPERATION);
//				exceptionCode.setOtherInfo("No Source TAble found.");
//				return exceptionCode;
//			}
//			exceptionCode =  templateScheduleDao.doBulkReject(queryPopObj);
//			exceptionCode.setOtherInfo(queryPopObj);
//			return exceptionCode;
//		}catch(RuntimeCustomException rex){
//			logger.error("Bulk Reject Exception " + rex.getCode().getErrorMsg());
//			exceptionCode = rex.getCode();
//			exceptionCode.setOtherInfo(queryPopObj);
//			return exceptionCode;
//		}
//	}
//	public void alertMail(TemplateScheduleVb templateScheduleVb ) {
//		try {
//			// Fetch mail configuration from Vision variables
//			final String hostName = commonDao.findVisionVariableValue("RG_MAIL_HOST");
//			final String mailPort = commonDao.findVisionVariableValue("RG_MAIL_PORT");
//			final String authFlag = commonDao.findVisionVariableValue("RG_MAIL_SMTP_AUTH"); // "true" or "false"
//			final String username = commonDao.findVisionVariableValue("RG_MAIL_ID");
//			final String password = commonDao.findVisionVariableValue("RG_MAIL_PWD");
//			boolean useAuth = "true".equalsIgnoreCase(authFlag);
//			// Set mail properties
//			Properties props = new Properties();
//			props.put("mail.smtp.host", hostName);
//			props.put("mail.smtp.port", mailPort);
//			props.put("mail.smtp.starttls.enable", "true");
//			props.put("mail.smtp.auth", String.valueOf(useAuth));
//
//			Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
//				protected PasswordAuthentication getPasswordAuthentication() {
//					return new PasswordAuthentication(username, password);
//				}
//			});
//			
//			TemplateScheduleVb mailDetails  =templateScheduleDao.fetchTemplatemailSchedule(templateScheduleVb);
//			String data = emailData(mailDetails);
//			StringJoiner toMail = new StringJoiner(",");
//			toMail.add( ValidationUtil.isValid(mailDetails.getMakerMailId())? mailDetails.getMakerMailId() :"");
//			toMail.add( ValidationUtil.isValid(mailDetails.getSubmitterMailId())? mailDetails.getSubmitterMailId() :"");
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress(username));
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMail.toString()));
//			message.setSubject("RegTech - Submission Status");
//			String Header = "Dear Team, Good day. ";
//					String body = "Please find the templates attached below.";
//			String footer = "-----------------------------------------------------------\n"
//					+ "This is a system generated mail, Please do not reply.\n"
//					+ "-----------------------------------------------------------";
//			String fullContent = "<html><body>" + Header + "<br>" + body
//					+ "<br><br>" + data + "<br>" + footer + "</body></html>";
//
//			message.setContent(fullContent, "text/html");
//			Transport.send(message);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	public void alertMail(TemplateScheduleVb templateScheduleVb) {
	    try {
	        // Fetch mail configuration from Vision variables
	        final String hostName = commonDao.findVisionVariableValue("RG_MAIL_HOST");
	        final String mailPort = commonDao.findVisionVariableValue("RG_MAIL_PORT");
	        final String authFlag = commonDao.findVisionVariableValue("RG_MAIL_SMTP_AUTH"); // "true" or "false"
	        final String username = commonDao.findVisionVariableValue("RG_MAIL_ID");
	        final String password = commonDao.findVisionVariableValue("RG_MAIL_PWD");
	        final String tlsEnable = commonDao.findVisionVariableValue("RG_TLS_ENABLE");
	        
	        System.out.println("Host Name"+hostName);
	        System.out.println("Mail Port"+mailPort);
	        System.out.println("Auth Flag"+authFlag);
	        System.out.println("User Name"+username);
	        System.out.println("Password "+password);
	        
	        logger.info("Host Name"+hostName);
	        logger.info("Mail Port"+mailPort);
	        logger.info("Auth Flag"+authFlag);
	        logger.info("User Name"+username);
	        logger.info("Password "+password);
	        logger.info("tlsEnable "+tlsEnable);

	        boolean useAuth = Boolean.parseBoolean(authFlag);

	        // Mail properties
	        Properties props = new Properties();
	        props.put("mail.smtp.host", hostName);
	        props.put("mail.smtp.port", mailPort);
	        props.put("mail.smtp.starttls.enable", tlsEnable);
	        props.put("mail.smtp.auth", String.valueOf(useAuth));

	        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(username, password);
	            }
	        });

	        // Fetch mail details from DB
	        TemplateScheduleVb mailDetails = templateScheduleDao.fetchTemplatemailSchedule(templateScheduleVb);
	        if(mailDetails == null && !ValidationUtil.isValid(mailDetails)) {
	        	return;
	        }
	        String data = emailData(mailDetails);

	        // Collect recipients
	        List<String> recipients = new ArrayList<>();
	        if (ValidationUtil.isValid(mailDetails.getMakerMailId())) {
	        	System.out.println("Maker Mail "+mailDetails.getMakerMailId());
	        	logger.info("Maker Mail "+mailDetails.getMakerMailId());
	            recipients.add(mailDetails.getMakerMailId());
	        }
	        if (ValidationUtil.isValid(mailDetails.getSubmitterMailId())) {
	            recipients.add(mailDetails.getSubmitterMailId());
	            System.out.println("Submitter Mail "+mailDetails.getSubmitterMailId());
	            logger.info("Submitter Mail "+mailDetails.getSubmitterMailId());
	        }

	        if (recipients.isEmpty()) {
	            System.out.println("⚠ No valid recipients found. Skipping mail.");
	            logger.info("⚠ No valid recipients found. Skipping mail.");
	            return;
	        }

	        // Build message
	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(username));
	        message.setRecipients(Message.RecipientType.TO,
	                InternetAddress.parse(String.join(",", recipients)));
	        message.setSubject("RegTech - Submission Status");

	        StringBuilder content = new StringBuilder();
	        content.append("<html><body>")
	               .append("<p>Dear Team, Good day.</p>")
	               .append("<p>Please find the templates attached below.</p>")
	               .append("<br>").append(data).append("<br>")
	               .append("<hr>")
	               .append("<p style='color:gray;font-size:12px;'>")
	               .append("This is a system generated mail, Please do not reply.")
	               .append("</p>")
	               .append("</body></html>");

	        message.setContent(content.toString(), "text/html; charset=UTF-8");

	        Transport.send(message);
	        System.out.println("✅ Mail sent successfully to: " + recipients);
	        logger.info("✅ Mail sent successfully to: " + recipients);

	    } catch (Exception e) {
	        e.printStackTrace(); // replace with logger.error("Mail sending failed", e);
	    }
	}


	public String emailData(TemplateScheduleVb templateScheduleVb) {
	    StringBuilder html = new StringBuilder();

	    // Header wrapper
	    html.append("<html><body>")
	        .append("<table width='80%' cellspacing='0' cellpadding='0' align='center' ")
	        .append("style='font:normal 12px Arial,Helvetica,sans-serif;border:solid 4px #006599;border-top:none;'>")
	        .append("<tr><td valign='top'>")

	        // Title Bar
	        .append("<table width='100%' cellspacing='0' cellpadding='0' border='0'>")
	        .append("<tr>")
	        .append("<td height='41' style='font:normal 14px Arial,Helvetica,sans-serif;")
	        .append("color:#FFF;padding:4px;background-color:#006599;'>")
	        .append("<strong>GDI Submission Status Report</strong>")
	        .append("</td>")
	        .append("</tr></table>");

	    // Data Table
	    html.append("<table width='100%' cellspacing='0' cellpadding='0' border='0' ")
	        .append("style='font-size:12px;border:1px solid #dfe8f6;'>")

	        // Table Header Row
	        .append("<tr style='background-color:#1E8B75;color:white;text-align:center;'>")
	        .append("<th style='padding:5px;border-right:1px solid #FFF;'>TEMPLATE_ID</th>")
	        .append("<th style='padding:5px;border-right:1px solid #FFF;'>TEMPLATE_DESCRIPTION</th>")
	        .append("<th style='padding:5px;border-right:1px solid #FFF;'>SUBMISSION_DATE</th>")
	        .append("<th style='padding:5px;border-right:1px solid #FFF;'>REPORTING_DATE</th>")
	        .append("<th style='padding:5px;border-right:1px solid #FFF;'>PROCESS_STATUS</th>")
	        .append("</tr>");

	    // Data Row
	    html.append("<tr style='background-color:#FFF;font-size:11px;'>")
	        .append("<td style='padding:5px;border-right:1px solid #CCC;'>")
	        .append(nullSafe(templateScheduleVb.getTemplateId())).append("</td>")
	        .append("<td style='padding:5px;border-right:1px solid #CCC;'>")
	        .append(nullSafe(templateScheduleVb.getTemplateName())).append("</td>")
	        .append("<td style='padding:5px;border-right:1px solid #CCC;'>")
	        .append(nullSafe(templateScheduleVb.getSubmissionDate())).append("</td>")
	        .append("<td style='padding:5px;border-right:1px solid #CCC;'>")
	        .append(nullSafe(templateScheduleVb.getReportingDate())).append("</td>")
	        .append("<td style='padding:5px;border-right:1px solid #CCC;'>")
	        .append(nullSafe(templateScheduleVb.getProcessStatusDesc())).append("</td>")
	        .append("</tr>");

	    // Close
	    html.append("</table></td></tr></table></body></html>");

	    return html.toString();
	}

	// Helper to avoid null values showing as "null"
	private String nullSafe(Object value) {
	    return value != null ? value.toString() : "";
	}
	public ExceptionCode reviewCrsfatcaDetails(TemplateScheduleVb vObject, String fileName) throws IOException {
		ExceptionCode exceptionCode = null;
		ByteArrayOutputStream out = null;
		OutputStream outputStream = null;

		exceptionCode = doValidate(vObject);
		if (exceptionCode != null && exceptionCode.getErrorMsg() != "") {
			return exceptionCode;
		}
		String tmpFilePath = System.getProperty("java.io.tmpdir");
		if ("XL".equalsIgnoreCase(vObject.getSourceType())) {
			setUploadDownloadDirFromDB();
			String uploadDir = "";
			uploadDir = getUploadDir();

			File my_file = new File(uploadDir + File.separator + fileName);
			out = new ByteArrayOutputStream();
			FileInputStream in = new FileInputStream(my_file);
			out = new ByteArrayOutputStream();
			File lfile = new File(tmpFilePath + File.separator + fileName);
			if (lfile.exists()) {
				lfile.delete();
			}
			outputStream = new FileOutputStream(tmpFilePath + File.separator + fileName);
			int length = fileName.length();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.writeTo(outputStream);
			outputStream.flush();
			outputStream.close();
			out.flush();
			out.close();
			in.close();
			exceptionCode = CommonUtils.getResultObject(SERVICE_NAME, Constants.SUCCESSFUL_OPERATION, "Upload", "");
			exceptionCode.setOtherInfo(fileName);
			exceptionCode.setResponse(tmpFilePath);
		} else if ("DB".equalsIgnoreCase(vObject.getSourceType())) {
			setUploadDownloadDirFromDB();
			String uploadDir = "";
			uploadDir = getUploadDir();
			exceptionCode = templateScheduleDao.reviewDbData(vObject);
			TemplateScheduleVb templateSchedule = (TemplateScheduleVb)exceptionCode.getResponse();
			exceptionCode =templateScheduleDao.writeXlsData(uploadDir,templateSchedule.getColumnHeaderLst(), templateSchedule.getDataLst(),vObject.getTemplateName());
		}
		return exceptionCode;
	}
}
