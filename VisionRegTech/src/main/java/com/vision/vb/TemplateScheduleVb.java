package com.vision.vb;

import java.util.List;

public class TemplateScheduleVb extends CommonVb {

	private static final long serialVersionUID = 1L;
	private String templateId = "";
	private String templateName = "";
	private String reportingDate = "";
	private String displayReportingDate = "";
	private String processStatus = "";
	private String processStatusDesc = "";
	private String processId = "";
	private String processFrequency = "";
	private String processFrequencyDesc = "";
	private String startTime = "";
	private String endTime = "";
	private String duration = "";
	private String lastSucessfulDate = "";
	private String counter = "";
	private String scheduleStatus ="";
	private String logFile = "";
	public String sourceType;
	public String sourceTypeDesc;
	private String restartProcess = "";
	private String xlName="";
	private String businessDate= "";
	private String scheduleDate= "";
	private String query= "";
	

	private String basicFilterStr = "";
	private String sourceTable = "";
	private int version = 0;
	List dataLst  = null;
	List errorLst  = null;
	List cmtLst  = null;
	private String errorMessage = "";
	private String status = "";
	private int errorCount = 0;
	public int typeOfSubmissionAt = 7602;
	public String typeOfSubmission;
	public int categoryTypeAt = 5008;
	public String categoryType;
	
	
	//audit
	private String auditTrailSequenceId= "";
	private String dateTimeStamp= "";
	private String auditDescription= "";
	private String auditDescriptionDetail= "";
	private boolean errorFlag = false;
	
	//JSON
	private String jsonFile = "";
	private String requestNo ="";
	private String cbStatus ="";
	private String table="";
	private String comments = "";
	private long submitter ;
	private String submitterName = "";
	
	
	private String submissionDate = "";
	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public String getCbStatus() {
		return cbStatus;
	}

	public void setCbStatus(String cbStatus) {
		this.cbStatus = cbStatus;
	}

	public String getJsonFile() {
		return jsonFile;
	}

	public void setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	List<ColumnHeadersVb> columnHeaderLst = null;
	
	public String getProcessFrequencyDesc() {
		return processFrequencyDesc;
	}

	public void setProcessFrequencyDesc(String processFrequencyDesc) {
		this.processFrequencyDesc = processFrequencyDesc;
	}

	public String getLastSucessfulDate() {
		return lastSucessfulDate;
	}

	public void setLastSucessfulDate(String lastSucessfulDate) {
		this.lastSucessfulDate = lastSucessfulDate;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getReportingDate() {
		return reportingDate;
	}

	public void setReportingDate(String reportingDate) {
		this.reportingDate = reportingDate;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public String getProcessStatusDesc() {
		return processStatusDesc;
	}

	public void setProcessStatusDesc(String processStatusDesc) {
		this.processStatusDesc = processStatusDesc;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessFrequency() {
		return processFrequency;
	}

	public void setProcessFrequency(String processFrequency) {
		this.processFrequency = processFrequency;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDisplayReportingDate() {
		return displayReportingDate;
	}

	public void setDisplayReportingDate(String displayReportingDate) {
		this.displayReportingDate = displayReportingDate;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceTypeDesc() {
		return sourceTypeDesc;
	}

	public void setSourceTypeDesc(String sourceTypeDesc) {
		this.sourceTypeDesc = sourceTypeDesc;
	}

	public String getRestartProcess() {
		return restartProcess;
	}

	public void setRestartProcess(String restartProcess) {
		this.restartProcess = restartProcess;
	}

	public String getXlName() {
		return xlName;
	}

	public void setXlName(String xlName) {
		this.xlName = xlName;
	}

	public String getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	

	public List<ColumnHeadersVb> getColumnHeaderLst() {
		return columnHeaderLst;
	}

	public void setColumnHeaderLst(List<ColumnHeadersVb> columnHeaderLst) {
		this.columnHeaderLst = columnHeaderLst;
	}

	public List getDataLst() {
		return dataLst;
	}

	public void setDataLst(List dataLst) {
		this.dataLst = dataLst;
	}

	public String getBasicFilterStr() {
		return basicFilterStr;
	}

	public void setBasicFilterStr(String basicFilterStr) {
		this.basicFilterStr = basicFilterStr;
	}
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	public List getErrorLst() {
		return errorLst;
	}

	public void setErrorLst(List errorLst) {
		this.errorLst = errorLst;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTypeOfSubmissionAt() {
		return typeOfSubmissionAt;
	}

	public void setTypeOfSubmissionAt(int typeOfSubmissionAt) {
		this.typeOfSubmissionAt = typeOfSubmissionAt;
	}

	public String getTypeOfSubmission() {
		return typeOfSubmission;
	}

	public void setTypeOfSubmission(String typeOfSubmission) {
		this.typeOfSubmission = typeOfSubmission;
	}

	public String getAuditTrailSequenceId() {
		return auditTrailSequenceId;
	}

	public void setAuditTrailSequenceId(String auditTrailSequenceId) {
		this.auditTrailSequenceId = auditTrailSequenceId;
	}

	public String getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(String dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public String getAuditDescription() {
		return auditDescription;
	}

	public void setAuditDescription(String auditDescription) {
		this.auditDescription = auditDescription;
	}

	public String getAuditDescriptionDetail() {
		return auditDescriptionDetail;
	}

	public void setAuditDescriptionDetail(String auditDescriptionDetail) {
		this.auditDescriptionDetail = auditDescriptionDetail;
	}
	public int getCategoryTypeAt() {
		return categoryTypeAt;
	}

	public void setCategoryTypeAt(int categoryTypeAt) {
		this.categoryTypeAt = categoryTypeAt;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public boolean isErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(boolean errorFlag) {
		this.errorFlag = errorFlag;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List getCmtLst() {
		return cmtLst;
	}

	public void setCmtLst(List cmtLst) {
		this.cmtLst = cmtLst;
	}


	public String getSubmitterName() {
		return submitterName;
	}

	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}

	public long getSubmitter() {
		return submitter;
	}

	public void setSubmitter(long submitter) {
		this.submitter = submitter;
	}
}