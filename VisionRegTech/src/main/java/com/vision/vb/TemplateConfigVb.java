package com.vision.vb;

import java.util.List;

public class TemplateConfigVb extends CommonVb {

	public String templateId;
	public String templateDescription;
	public int processFrequencyAt = 100;
	public String processFrequency;
	public String processFrequencyDesc;
	public int typeOfSubmissionAt = 7602;
	public String typeOfSubmission;
	public int csvDelimiterAt = 7603;
	public String csvDelimiter;
	public int sourceTypeAt = 7604;
	public String sourceType;
	public int dbConnectivityTypeAt = 7605;
	public String dbConnectivityType;
	public String dbConnectivityDetails;
	public String sourceTable;
	public String sourceTableFilter;
	public String soureTableReadiness;
	public String uploadFileName;
	public int apiConnectivityTypeAt = 7606;
	public String apiConnectivityType;
	public String apiConnectivityDetails;
	public int authConnectivityTypeAt = 7607;
	public String authConnectivityType;
	public String authConnectivityDetails;
	public String mainAPIStructure;
	public int templateStatusNt = 1;
	public int templateStatus = -1;
	public String templateStatusDesc;
	private String variableName = "";

	private int connectivityTypeAt = 1081;
	private String connectivityType = "";
	private String serverConnectivityDetails = "";
	private int databaseTypeAt = 1082;
	private String databaseType = "";
	private String databaseConnectivityDetails = "";
	private String newFlag = "N";
	private String dbUrl = "";

	private String tagValue = "";
	private String tagName = "";
	private String encryption = "";
	private Object[] tagList;
	// private String sourceScriptType = "";
	private boolean isAuditDbPop = false;
	private String newDbDetailsFlag = "";
	private String connectivityOption = "";
	private String conType = "";
	private String conDetails = "";
	private String databaseConDet = "";
	private String connectionScript = "";

	private String macroName = "";
	private String macroType = "";
	private String displayName = "";
	private String masked = "";
	private String ftpVariableName = "";
	private String ftpConnectivityType = "";
	private String ftpServerIp = "";
	private String ftpServerHostName = "";
	private String ftpServerPort = "";
	private String ftpServerUserName = "";
	private String ftpServerPwd = "";
	private String ftpServerType = "";
	private String ftp = "";
	private String ftpPath = "";
	private String newConFlag = "";
	private String connectionType = "";
	public int sequenceNo;
	private String dbConnection = "";
	private String dataList = "";
	private String submissionTime = "";
	private String autoSubmit = "";
	private String cbkFileName = "";
	public int categoryTypeAt = 5008;
	public String categoryType;
	public String categoryTypeDesc;
	private String submissionDate;
	

	
	public String getCategoryTypeDesc() {
		return categoryTypeDesc;
	}

	public void setCategoryTypeDesc(String categoryTypeDesc) {
		this.categoryTypeDesc = categoryTypeDesc;
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

	public String getCbkFileName() {
		return cbkFileName;
	}

	public void setCbkFileName(String cbkFileName) {
		this.cbkFileName = cbkFileName;
	}

	public String reportingDate;

	public String getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(String dbConnection) {
		this.dbConnection = dbConnection;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	List<TemplateMappingVb> mappinglst = null;
	List<SmartSearchVb> smartSearchOpt = null;

	public final Boolean getIsChildReview() {
		return isChildReview;
	}

	public final void setIsChildReview(Boolean isChildReview) {
		this.isChildReview = isChildReview;
	}
	private Boolean isChildReview = false;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateDescription() {
		return templateDescription;
	}

	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	public int getProcessFrequencyAt() {
		return processFrequencyAt;
	}

	public void setProcessFrequencyAt(int processFrequencyAt) {
		this.processFrequencyAt = processFrequencyAt;
	}

	public String getProcessFrequency() {
		return processFrequency;
	}

	public void setProcessFrequency(String processFrequency) {
		this.processFrequency = processFrequency;
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

	public int getCsvDelimiterAt() {
		return csvDelimiterAt;
	}

	public void setCsvDelimiterAt(int csvDelimiterAt) {
		this.csvDelimiterAt = csvDelimiterAt;
	}

	public String getCsvDelimiter() {
		return csvDelimiter;
	}

	public void setCsvDelimiter(String csvDelimiter) {
		this.csvDelimiter = csvDelimiter;
	}

	public int getSourceTypeAt() {
		return sourceTypeAt;
	}

	public void setSourceTypeAt(int sourceTypeAt) {
		this.sourceTypeAt = sourceTypeAt;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public int getDbConnectivityTypeAt() {
		return dbConnectivityTypeAt;
	}

	public void setDbConnectivityTypeAt(int dbConnectivityTypeAt) {
		this.dbConnectivityTypeAt = dbConnectivityTypeAt;
	}

	public String getDbConnectivityType() {
		return dbConnectivityType;
	}

	public void setDbConnectivityType(String dbConnectivityType) {
		this.dbConnectivityType = dbConnectivityType;
	}

	public String getDbConnectivityDetails() {
		return dbConnectivityDetails;
	}

	public void setDbConnectivityDetails(String dbConnectivityDetails) {
		this.dbConnectivityDetails = dbConnectivityDetails;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	public String getSourceTableFilter() {
		return sourceTableFilter;
	}

	public void setSourceTableFilter(String sourceTableFilter) {
		this.sourceTableFilter = sourceTableFilter;
	}

	public String getSoureTableReadiness() {
		return soureTableReadiness;
	}

	public void setSoureTableReadiness(String soureTableReadiness) {
		this.soureTableReadiness = soureTableReadiness;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public int getApiConnectivityTypeAt() {
		return apiConnectivityTypeAt;
	}

	public void setApiConnectivityTypeAt(int apiConnectivityTypeAt) {
		this.apiConnectivityTypeAt = apiConnectivityTypeAt;
	}

	public String getApiConnectivityType() {
		return apiConnectivityType;
	}

	public void setApiConnectivityType(String apiConnectivityType) {
		this.apiConnectivityType = apiConnectivityType;
	}

	public String getApiConnectivityDetails() {
		return apiConnectivityDetails;
	}

	public void setApiConnectivityDetails(String apiConnectivityDetails) {
		this.apiConnectivityDetails = apiConnectivityDetails;
	}

	public int getAuthConnectivityTypeAt() {
		return authConnectivityTypeAt;
	}

	public void setAuthConnectivityTypeAt(int authConnectivityTypeAt) {
		this.authConnectivityTypeAt = authConnectivityTypeAt;
	}

	public String getAuthConnectivityType() {
		return authConnectivityType;
	}

	public void setAuthConnectivityType(String authConnectivityType) {
		this.authConnectivityType = authConnectivityType;
	}

	public String getAuthConnectivityDetails() {
		return authConnectivityDetails;
	}

	public void setAuthConnectivityDetails(String authConnectivityDetails) {
		this.authConnectivityDetails = authConnectivityDetails;
	}

	public String getMainAPIStructure() {
		return mainAPIStructure;
	}

	public void setMainAPIStructure(String mainAPIStructure) {
		this.mainAPIStructure = mainAPIStructure;
	}

	public int getTemplateStatusNt() {
		return templateStatusNt;
	}

	public void setTemplateStatusNt(int templateStatusNt) {
		this.templateStatusNt = templateStatusNt;
	}

	public int getTemplateStatus() {
		return templateStatus;
	}

	public void setTemplateStatus(int templateStatus) {
		this.templateStatus = templateStatus;
	}

	public String getTemplateStatusDesc() {
		return templateStatusDesc;
	}

	public void setTemplateStatusDesc(String templateStatusDesc) {
		this.templateStatusDesc = templateStatusDesc;
	}

	public List<TemplateMappingVb> getMappinglst() {
		return mappinglst;
	}

	public void setMappinglst(List<TemplateMappingVb> mappinglst) {
		this.mappinglst = mappinglst;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public final String getVariableName() {
		return variableName;
	}

	public final void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public final int getConnectivityTypeAt() {
		return connectivityTypeAt;
	}

	public final void setConnectivityTypeAt(int connectivityTypeAt) {
		this.connectivityTypeAt = connectivityTypeAt;
	}

	public final String getConnectivityType() {
		return connectivityType;
	}

	public final void setConnectivityType(String connectivityType) {
		this.connectivityType = connectivityType;
	}

	public final String getServerConnectivityDetails() {
		return serverConnectivityDetails;
	}

	public final void setServerConnectivityDetails(String serverConnectivityDetails) {
		this.serverConnectivityDetails = serverConnectivityDetails;
	}

	public final int getDatabaseTypeAt() {
		return databaseTypeAt;
	}

	public final void setDatabaseTypeAt(int databaseTypeAt) {
		this.databaseTypeAt = databaseTypeAt;
	}

	public final String getDatabaseType() {
		return databaseType;
	}

	public final void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public final String getDatabaseConnectivityDetails() {
		return databaseConnectivityDetails;
	}

	public final void setDatabaseConnectivityDetails(String databaseConnectivityDetails) {
		this.databaseConnectivityDetails = databaseConnectivityDetails;
	}

	public final String getNewFlag() {
		return newFlag;
	}

	public final void setNewFlag(String newFlag) {
		this.newFlag = newFlag;
	}

	public final String getDbUrl() {
		return dbUrl;
	}

	public final void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public final String getTagValue() {
		return tagValue;
	}

	public final void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	public final String getTagName() {
		return tagName;
	}

	public final void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public final String getEncryption() {
		return encryption;
	}

	public final void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public final Object[] getTagList() {
		return tagList;
	}

	public final void setTagList(Object[] tagList) {
		this.tagList = tagList;
	}

	public final boolean isAuditDbPop() {
		return isAuditDbPop;
	}

	public final void setAuditDbPop(boolean isAuditDbPop) {
		this.isAuditDbPop = isAuditDbPop;
	}

	public final String getNewDbDetailsFlag() {
		return newDbDetailsFlag;
	}

	public final void setNewDbDetailsFlag(String newDbDetailsFlag) {
		this.newDbDetailsFlag = newDbDetailsFlag;
	}

	public final String getConnectivityOption() {
		return connectivityOption;
	}

	public final void setConnectivityOption(String connectivityOption) {
		this.connectivityOption = connectivityOption;
	}

	public final String getConType() {
		return conType;
	}

	public final void setConType(String conType) {
		this.conType = conType;
	}

	public final String getConDetails() {
		return conDetails;
	}

	public final void setConDetails(String conDetails) {
		this.conDetails = conDetails;
	}

	public final String getDatabaseConDet() {
		return databaseConDet;
	}

	public final void setDatabaseConDet(String databaseConDet) {
		this.databaseConDet = databaseConDet;
	}

	public final String getConnectionScript() {
		return connectionScript;
	}

	public final void setConnectionScript(String connectionScript) {
		this.connectionScript = connectionScript;
	}

	public final String getMacroName() {
		return macroName;
	}

	public final void setMacroName(String macroName) {
		this.macroName = macroName;
	}

	public final String getMacroType() {
		return macroType;
	}

	public final void setMacroType(String macroType) {
		this.macroType = macroType;
	}

	public final String getDisplayName() {
		return displayName;
	}

	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public final String getMasked() {
		return masked;
	}

	public final void setMasked(String masked) {
		this.masked = masked;
	}

	public final String getFtpVariableName() {
		return ftpVariableName;
	}

	public final void setFtpVariableName(String ftpVariableName) {
		this.ftpVariableName = ftpVariableName;
	}

	public final String getFtpConnectivityType() {
		return ftpConnectivityType;
	}

	public final void setFtpConnectivityType(String ftpConnectivityType) {
		this.ftpConnectivityType = ftpConnectivityType;
	}

	public final String getFtpServerIp() {
		return ftpServerIp;
	}

	public final void setFtpServerIp(String ftpServerIp) {
		this.ftpServerIp = ftpServerIp;
	}

	public final String getFtpServerHostName() {
		return ftpServerHostName;
	}

	public final void setFtpServerHostName(String ftpServerHostName) {
		this.ftpServerHostName = ftpServerHostName;
	}

	public final String getFtpServerPort() {
		return ftpServerPort;
	}

	public final void setFtpServerPort(String ftpServerPort) {
		this.ftpServerPort = ftpServerPort;
	}

	public final String getFtpServerUserName() {
		return ftpServerUserName;
	}

	public final void setFtpServerUserName(String ftpServerUserName) {
		this.ftpServerUserName = ftpServerUserName;
	}

	public final String getFtpServerPwd() {
		return ftpServerPwd;
	}

	public final void setFtpServerPwd(String ftpServerPwd) {
		this.ftpServerPwd = ftpServerPwd;
	}

	public final String getFtpServerType() {
		return ftpServerType;
	}

	public final void setFtpServerType(String ftpServerType) {
		this.ftpServerType = ftpServerType;
	}

	public final String getFtp() {
		return ftp;
	}

	public final void setFtp(String ftp) {
		this.ftp = ftp;
	}

	public final String getFtpPath() {
		return ftpPath;
	}

	public final void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	public final String getNewConFlag() {
		return newConFlag;
	}

	public final void setNewConFlag(String newConFlag) {
		this.newConFlag = newConFlag;
	}

	public final String getConnectionType() {
		return connectionType;
	}

	public final void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public String getProcessFrequencyDesc() {
		return processFrequencyDesc;
	}

	public void setProcessFrequencyDesc(String processFrequencyDesc) {
		this.processFrequencyDesc = processFrequencyDesc;
	}

	public String getReportingDate() {
		return reportingDate;
	}

	public void setReportingDate(String reportingDate) {
		this.reportingDate = reportingDate;
	}

	public String getDataList() {
		return dataList;
	}

	public void setDataList(String dataList) {
		this.dataList = dataList;
	}

	public String getSubmissionTime() {
		return submissionTime;
	}

	public void setSubmissionTime(String submissionTime) {
		this.submissionTime = submissionTime;
	}


	public String getAutoSubmit() {
		return autoSubmit;
	}

	public void setAutoSubmit(String autoSubmit) {
		this.autoSubmit = autoSubmit;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}

}
