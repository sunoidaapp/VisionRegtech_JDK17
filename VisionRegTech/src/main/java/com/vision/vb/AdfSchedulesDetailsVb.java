package com.vision.vb;

public class AdfSchedulesDetailsVb extends CommonVb {

	private static final long serialVersionUID = 1L;
	private String build = "";
	private String buildNumber = "";
	private String subBuildNumber = "";
	private String bsdSequence = "";
	private String associatedBuild = "";
	private String buildModule = "";
	private int runItAt = 0;
	private String runIt = "";
	private String submitterId = "";
	private int programTypeAt = 0;
	private String programType = "";
	private int moduleStatusAt = 0;
	private String moduleStatus = "";
	private String debugMode = "";
	private String startTime = "";
	private String endTime = "";
	private String scheduledDate = "";
	private String duration = "";
	private String progressPercent = "0";
	private String programDescription = "";
	private String expandFlag = "N";
	private boolean run = false;
	private boolean debug = false;
	
	private String adfNumber = "";
	private String subAdfNumber = "";
	private String processSequence = "";
	private String templateName = "";
	private int acquisitionStatusAt = 1084;
	private String acquisitionStatus = "";
	private String generalDescription = "";
	private String node = "";
	//private int recordsFetchedCount = 0;
	private String recordsFetchedCount = "";
	private String nextProcessTime = "";
	private String dateLastExtraction = "";
	
	private String adfMetadata = "";
	
	private String extIterationCount = "";
	private String mandatoryFlag = "";
	private String connectivityType = "";
	private String connectivityDetails = "";
	private String databaseType = "";
	private String databaseConnectivityDetails = "";
	private String alert1Timeslot  = "";
	private String alert1EmailIds = "";
	private String alert1MobileNo = "";
	private String alert1SendFlag = "";
	private String alert2Timeslot  = "";
	private String alert2EmailIds = "";
	private String alert2MobileNo = "";
	private String alert2SendFlag = "";
	private String alert3Timeslot  = "";
	private String alert3EmailIds = "";
	private String alert3MobileNo = "";
	private String alert3SendFlag = "";
	private String adfSuccessfulEmailIds = "";
	private String adfSuccessfulEmailFlag = "";
	private String adfFailedEmailIds = "";
	private String adfFailedEmailFlag = "";
	private String autoauthTime = "";
	private String authId = "";
	private String authTime = "";
	private String authStatus = "";
	
	// for audit 
	
	private String auditTrailSequenceId= "";
	private String dateTimeStamp= "";
	private String auditDescription= "";
	private String auditDescriptionDetail= "";
	private String businessDate= "";
	private String feedDate= "";
	private String query= "";

	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
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
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public String getFeedDate() {
		return feedDate;
	}
	public void setFeedDate(String feedDate) {
		this.feedDate = feedDate;
	}
	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public String getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	public String getSubBuildNumber() {
		return subBuildNumber;
	}
	public void setSubBuildNumber(String subBuildNumber) {
		this.subBuildNumber = subBuildNumber;
	}
	public String getBsdSequence() {
		return bsdSequence;
	}
	public void setBsdSequence(String bsdSequence) {
		this.bsdSequence = bsdSequence;
	}
	public String getAssociatedBuild() {
		return associatedBuild;
	}
	public void setAssociatedBuild(String associatedBuild) {
		this.associatedBuild = associatedBuild;
	}
	public String getBuildModule() {
		return buildModule;
	}
	public void setBuildModule(String buildModule) {
		this.buildModule = buildModule;
	}
	public int getRunItAt() {
		return runItAt;
	}
	public void setRunItAt(int runItAt) {
		this.runItAt = runItAt;
	}
	public String getRunIt() {
		return runIt;
	}
	public void setRunIt(String runIt) {
		this.runIt = runIt;
	}
	public String getSubmitterId() {
		return submitterId;
	}
	public void setSubmitterId(String submitterId) {
		this.submitterId = submitterId;
	}
	public int getProgramTypeAt() {
		return programTypeAt;
	}
	public void setProgramTypeAt(int programTypeAt) {
		this.programTypeAt = programTypeAt;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public int getModuleStatusAt() {
		return moduleStatusAt;
	}
	public void setModuleStatusAt(int moduleStatusAt) {
		this.moduleStatusAt = moduleStatusAt;
	}
	public String getModuleStatus() {
		return moduleStatus;
	}
	public void setModuleStatus(String moduleStatus) {
		this.moduleStatus = moduleStatus;
	}
	public String getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
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
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getProgressPercent() {
		return progressPercent;
	}
	public void setProgressPercent(String progressPercent) {
		this.progressPercent = progressPercent;
	}
	public String getProgramDescription() {
		return programDescription;
	}
	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}
	public String getExpandFlag() {
		return expandFlag;
	}
	public void setExpandFlag(String expandFlag) {
		this.expandFlag = expandFlag;
	}
	public boolean isRun() {
		return run;
	}
	public void setRun(boolean run) {
		this.run = run;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	public String getAdfNumber() {
		return adfNumber;
	}
	public void setAdfNumber(String adfNumber) {
		this.adfNumber = adfNumber;
	}
	public String getSubAdfNumber() {
		return subAdfNumber;
	}
	public void setSubAdfNumber(String subAdfNumber) {
		this.subAdfNumber = subAdfNumber;
	}
	public String getProcessSequence() {
		return processSequence;
	}
	public void setProcessSequence(String processSequence) {
		this.processSequence = processSequence;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public int getAcquisitionStatusAt() {
		return acquisitionStatusAt;
	}
	public void setAcquisitionStatusAt(int acquisitionStatusAt) {
		this.acquisitionStatusAt = acquisitionStatusAt;
	}
	public String getAcquisitionStatus() {
		return acquisitionStatus;
	}
	public void setAcquisitionStatus(String acquisitionStatus) {
		this.acquisitionStatus = acquisitionStatus;
	}
	public String getGeneralDescription() {
		return generalDescription;
	}
	public void setGeneralDescription(String generalDescription) {
		this.generalDescription = generalDescription;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	/*public int getRecordsFetchedCount() {
		return recordsFetchedCount;
	}
	public void setRecordsFetchedCount(int recordsFetchedCount) {
		this.recordsFetchedCount = recordsFetchedCount;
	}*/
	public String getRecordsFetchedCount() {
		return recordsFetchedCount;
	}
	public void setRecordsFetchedCount(String recordsFetchedCount) {
		this.recordsFetchedCount = recordsFetchedCount;
	}
	public String getNextProcessTime() {
		return nextProcessTime;
	}
	public void setNextProcessTime(String nextProcessTime) {
		this.nextProcessTime = nextProcessTime;
	}
	public String getDateLastExtraction() {
		return dateLastExtraction;
	}
	public void setDateLastExtraction(String dateLastExtraction) {
		this.dateLastExtraction = dateLastExtraction;
	}
	public String getAdfMetadata() {
		return adfMetadata;
	}
	public void setAdfMetadata(String adfMetadata) {
		this.adfMetadata = adfMetadata;
	}
	public String getExtIterationCount() {
		return extIterationCount;
	}
	public void setExtIterationCount(String extIterationCount) {
		this.extIterationCount = extIterationCount;
	}
	public String getMandatoryFlag() {
		return mandatoryFlag;
	}
	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
	public String getConnectivityType() {
		return connectivityType;
	}
	public void setConnectivityType(String connectivityType) {
		this.connectivityType = connectivityType;
	}
	public String getConnectivityDetails() {
		return connectivityDetails;
	}
	public void setConnectivityDetails(String connectivityDetails) {
		this.connectivityDetails = connectivityDetails;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getDatabaseConnectivityDetails() {
		return databaseConnectivityDetails;
	}
	public void setDatabaseConnectivityDetails(String databaseConnectivityDetails) {
		this.databaseConnectivityDetails = databaseConnectivityDetails;
	}
	public String getAlert1Timeslot() {
		return alert1Timeslot;
	}
	public void setAlert1Timeslot(String alert1Timeslot) {
		this.alert1Timeslot = alert1Timeslot;
	}
	public String getAlert1EmailIds() {
		return alert1EmailIds;
	}
	public void setAlert1EmailIds(String alert1EmailIds) {
		this.alert1EmailIds = alert1EmailIds;
	}
	public String getAlert1MobileNo() {
		return alert1MobileNo;
	}
	public void setAlert1MobileNo(String alert1MobileNo) {
		this.alert1MobileNo = alert1MobileNo;
	}
	public String getAlert1SendFlag() {
		return alert1SendFlag;
	}
	public void setAlert1SendFlag(String alert1SendFlag) {
		this.alert1SendFlag = alert1SendFlag;
	}
	public String getAlert2Timeslot() {
		return alert2Timeslot;
	}
	public void setAlert2Timeslot(String alert2Timeslot) {
		this.alert2Timeslot = alert2Timeslot;
	}
	public String getAlert2EmailIds() {
		return alert2EmailIds;
	}
	public void setAlert2EmailIds(String alert2EmailIds) {
		this.alert2EmailIds = alert2EmailIds;
	}
	public String getAlert2MobileNo() {
		return alert2MobileNo;
	}
	public void setAlert2MobileNo(String alert2MobileNo) {
		this.alert2MobileNo = alert2MobileNo;
	}
	public String getAlert2SendFlag() {
		return alert2SendFlag;
	}
	public void setAlert2SendFlag(String alert2SendFlag) {
		this.alert2SendFlag = alert2SendFlag;
	}
	public String getAlert3Timeslot() {
		return alert3Timeslot;
	}
	public void setAlert3Timeslot(String alert3Timeslot) {
		this.alert3Timeslot = alert3Timeslot;
	}
	public String getAlert3EmailIds() {
		return alert3EmailIds;
	}
	public void setAlert3EmailIds(String alert3EmailIds) {
		this.alert3EmailIds = alert3EmailIds;
	}
	public String getAlert3MobileNo() {
		return alert3MobileNo;
	}
	public void setAlert3MobileNo(String alert3MobileNo) {
		this.alert3MobileNo = alert3MobileNo;
	}
	public String getAlert3SendFlag() {
		return alert3SendFlag;
	}
	public void setAlert3SendFlag(String alert3SendFlag) {
		this.alert3SendFlag = alert3SendFlag;
	}
	public String getAdfSuccessfulEmailIds() {
		return adfSuccessfulEmailIds;
	}
	public void setAdfSuccessfulEmailIds(String adfSuccessfulEmailIds) {
		this.adfSuccessfulEmailIds = adfSuccessfulEmailIds;
	}
	public String getAdfSuccessfulEmailFlag() {
		return adfSuccessfulEmailFlag;
	}
	public void setAdfSuccessfulEmailFlag(String adfSuccessfulEmailFlag) {
		this.adfSuccessfulEmailFlag = adfSuccessfulEmailFlag;
	}
	public String getAdfFailedEmailIds() {
		return adfFailedEmailIds;
	}
	public void setAdfFailedEmailIds(String adfFailedEmailIds) {
		this.adfFailedEmailIds = adfFailedEmailIds;
	}
	public String getAdfFailedEmailFlag() {
		return adfFailedEmailFlag;
	}
	public void setAdfFailedEmailFlag(String adfFailedEmailFlag) {
		this.adfFailedEmailFlag = adfFailedEmailFlag;
	}
	public String getAutoauthTime() {
		return autoauthTime;
	}
	public void setAutoauthTime(String autoauthTime) {
		this.autoauthTime = autoauthTime;
	}
	public String getAuthId() {
		return authId;
	}
	public void setAuthId(String authId) {
		this.authId = authId;
	}
	public String getAuthTime() {
		return authTime;
	}
	public void setAuthTime(String authTime) {
		this.authTime = authTime;
	}
	public String getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(String authStatus) {
		this.authStatus = authStatus;
	}
}
