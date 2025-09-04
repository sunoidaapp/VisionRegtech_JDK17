package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UploadFilingVb extends CommonVb{
	
	private static final long serialVersionUID = 1L;
	private String fileName = "";   
	private String templateName = "";
	private String uploadDate = "";
	private String businessDate = "";
	private int filingStatusNt = 1011;
	private int filingStatus = -1;
	private String shareHolder = "";
	private Boolean checkUploadInterval = false; 
	private String sessionId;
	private String legalVehicle ="";
	
	private String templateFile = "";
	private String feedCategory = "";
	private String dateLastRun = "";
	private String feedStgName = "";
	private String acquisitionProcessType = "";
	private String sourceScriptType = "";
	private String sourceServerScripts = "";
	private String targetServerScripts = "";
	private String targetScriptType = "";
	private String frequencyProcess = "";
	private String processSequence = "";
	private String integrityScriptName = "";
	private String debugMode = "Y";
	private String preActivityScriptType = "";
	private String preActivityScript = "";
	private String program = "";
	private String feedDate = "";
	private String excelTemplateId = "";
	private long versionNo = 0;
	
	private String uploadSequence = "";
	private int uploadStatusNt = 10;
	private int uploadStatus = -1;
	private String visionTableUploadDate = "";
	private String acquisitionReadinessScripts = "" ;
	private String filePattern = "";
	private String defaultAcqProcessType = "";
	private String acquisitionStatus = "";
	private String integrityScriptType = "";
	private String yearMonth = "";
	private String scheduleStatus = "";
	private String adfNumber = "";
	private String exportData = "ALL";
	private String exportReportId = "ACQALERT02";
	private String eodInitiatedFlag = "";
	private String fileNode = "";
	private String	processFrequency = "";
	private String	description = "";
	private String comment ="";
	private String recordCount ="";
	private String timeElapsed ="";
	private String majorBuild ="";
	private String  UFProcessedFlag ="";
	private String scheduleStartTime="";
	private String ScheduleEndTime="";
	private String duration = "";
	List<MultipartFile> files = new ArrayList<MultipartFile>(0);
	List<UploadFilingVb> childs =new ArrayList<UploadFilingVb>(0);


	private String acquisitionStatusDesc = "";


	public String getVisionTableUploadDate() {
		return visionTableUploadDate;
	}
	public void setVisionTableUploadDate(String visionTableUploadDate) {
		this.visionTableUploadDate = visionTableUploadDate;
	}
	public String getUploadSequence() {
		return uploadSequence;
	}
	public void setUploadSequence(String uploadSequence) {
		this.uploadSequence = uploadSequence;
	}
	public int getUploadStatusNt() {
		return uploadStatusNt;
	}
	public void setUploadStatusNt(int uploadStatusNt) {
		this.uploadStatusNt = uploadStatusNt;
	}
	public int getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public int getFilingStatusNt() {
		return filingStatusNt;
	}
	public void setFilingStatusNt(int filingStatusNt) {
		this.filingStatusNt = filingStatusNt;
	}
	public int getFilingStatus() {
		return filingStatus;
	}
	public void setFilingStatus(int filingStatus) {
		this.filingStatus = filingStatus;
	}
	public Boolean getCheckUploadInterval() {
		return checkUploadInterval;
	}
	public void setCheckUploadInterval(Boolean checkUploadInterval) {
		this.checkUploadInterval = checkUploadInterval;
	}
	public String getShareHolder() {
		return shareHolder;
	}
	public void setShareHolder(String shareHolder) {
		this.shareHolder = shareHolder;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getFeedCategory() {
		return feedCategory;
	}
	public void setFeedCategory(String feedCategory) {
		this.feedCategory = feedCategory;
	}
	public String getDateLastRun() {
		return dateLastRun;
	}
	public void setDateLastRun(String dateLastRun) {
		this.dateLastRun = dateLastRun;
	}
	public String getFeedStgName() {
		return feedStgName;
	}
	public void setFeedStgName(String feedStgName) {
		this.feedStgName = feedStgName;
	}
	public String getAcquisitionProcessType() {
		return acquisitionProcessType;
	}
	public void setAcquisitionProcessType(String acquisitionProcessType) {
		this.acquisitionProcessType = acquisitionProcessType;
	}
	public String getSourceScriptType() {
		return sourceScriptType;
	}
	public void setSourceScriptType(String sourceScriptType) {
		this.sourceScriptType = sourceScriptType;
	}
	public String getSourceServerScripts() {
		return sourceServerScripts;
	}
	public void setSourceServerScripts(String sourceServerScripts) {
		this.sourceServerScripts = sourceServerScripts;
	}
	public String getTargetServerScripts() {
		return targetServerScripts;
	}
	public void setTargetServerScripts(String targetServerScripts) {
		this.targetServerScripts = targetServerScripts;
	}
	public String getTargetScriptType() {
		return targetScriptType;
	}
	public void setTargetScriptType(String targetScriptType) {
		this.targetScriptType = targetScriptType;
	}
	public String getFrequencyProcess() {
		return frequencyProcess;
	}
	public void setFrequencyProcess(String frequencyProcess) {
		this.frequencyProcess = frequencyProcess;
	}
	public String getProcessSequence() {
		return processSequence;
	}
	public void setProcessSequence(String processSequence) {
		this.processSequence = processSequence;
	}
	public String getIntegrityScriptName() {
		return integrityScriptName;
	}
	public void setIntegrityScriptName(String integrityScriptName) {
		this.integrityScriptName = integrityScriptName;
	}
	public String getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
	}
	public String getPreActivityScriptType() {
		return preActivityScriptType;
	}
	public void setPreActivityScriptType(String preActivityScriptType) {
		this.preActivityScriptType = preActivityScriptType;
	}
	public String getPreActivityScript() {
		return preActivityScript;
	}
	public void setPreActivityScript(String preActivityScript) {
		this.preActivityScript = preActivityScript;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getFeedDate() {
		return feedDate;
	}
	public void setFeedDate(String feedDate) {
		this.feedDate = feedDate;
	}
	public String getExcelTemplateId() {
		return excelTemplateId;
	}
	public void setExcelTemplateId(String excelTemplateId) {
		this.excelTemplateId = excelTemplateId;
	}
	public long getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(long versionNo) {
		this.versionNo = versionNo;
	}
	public String getTemplateFile() {
		return templateFile;
	}
	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}
	public String getAcquisitionReadinessScripts() {
		return acquisitionReadinessScripts;
	}
	public void setAcquisitionReadinessScripts(
			String acquisitionReadinessScripts) {
		this.acquisitionReadinessScripts = acquisitionReadinessScripts;
	}
	public String getFilePattern() {
		return filePattern;
	}
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}
	public String getDefaultAcqProcessType() {
		return defaultAcqProcessType;
	}
	public void setDefaultAcqProcessType(String defaultAcqProcessType) {
		this.defaultAcqProcessType = defaultAcqProcessType;
	}
	public String getAcquisitionStatus() {
		return acquisitionStatus;
	}
	public void setAcquisitionStatus(String acquisitionStatus) {
		this.acquisitionStatus = acquisitionStatus;
	}
	public String getIntegrityScriptType() {
		return integrityScriptType;
	}
	public void setIntegrityScriptType(String integrityScriptType) {
		this.integrityScriptType = integrityScriptType;
	}
	public String getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	public String getScheduleStatus() {
		return scheduleStatus;
	}
	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}
	public String getAdfNumber() {
		return adfNumber;
	}
	public void setAdfNumber(String adfNumber) {
		this.adfNumber = adfNumber;
	}
	public String getExportData() {
		return exportData;
	}
	public void setExportData(String exportData) {
		this.exportData = exportData;
	}
	public String getExportReportId() {
		return exportReportId;
	}
	public void setExportReportId(String exportReportId) {
		this.exportReportId = exportReportId;
	}
	public String getEodInitiatedFlag() {
		return eodInitiatedFlag;
	}
	public void setEodInitiatedFlag(String eodInitiatedFlag) {
		this.eodInitiatedFlag = eodInitiatedFlag;
	}
	public String getFileNode() {
		return fileNode;
	}
	public void setFileNode(String fileNode) {
		this.fileNode = fileNode;
	}
	public String getProcessFrequency() {
		return processFrequency;
	}
	public void setProcessFrequency(String processFrequency) {
		this.processFrequency = processFrequency;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLegalVehicle() {
		return legalVehicle;
	}
	public void setLegalVehicle(String legalVehicle) {
		this.legalVehicle = legalVehicle;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTimeElapsed() {
		return timeElapsed;
	}
	public void setTimeElapsed(String timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	public String getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}
	public String getMajorBuild() {
		return majorBuild;
	}
	public void setMajorBuild(String majorBuild) {
		this.majorBuild = majorBuild;
	}
	public String getUFProcessedFlag() {
		return UFProcessedFlag;
	}
	public void setUFProcessedFlag(String uFProcessedFlag) {
		UFProcessedFlag = uFProcessedFlag;
	}
	public List<MultipartFile> getFiles() {
		return files;
	}
	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
	public List<UploadFilingVb> getChilds() {
		return childs;
	}
	public void setChilds(List<UploadFilingVb> childs) {
		this.childs = childs;
	}
	public String getScheduleStartTime() {
		return scheduleStartTime;
	}
	public void setScheduleStartTime(String scheduleStartTime) {
		this.scheduleStartTime = scheduleStartTime;
	}
	public String getScheduleEndTime() {
		return ScheduleEndTime;
	}
	public void setScheduleEndTime(String scheduleEndTime) {
		ScheduleEndTime = scheduleEndTime;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getAcquisitionStatusDesc() {
		return acquisitionStatusDesc;
	}
	public void setAcquisitionStatusDesc(String acquisitionStatusDesc) {
		this.acquisitionStatusDesc = acquisitionStatusDesc;
	}
	
}