package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class AdfControlsVb extends CommonVb{

	private static final long serialVersionUID = -7135158174947618107L;
	private String build = "";
	private String subBuildNumber = "";
	private String bcSequence =  "";
	private String buildModule = "";
	private int	runItAt =  0;
	private String runIt = "";
	private String lastStartDate = "";
	private String lastBuildDate = "";
	private int	buildControlsStatusAt  =  0;
	private String buildControlsStatus = "";
	private String submitterId = "";
	private String buildNumber = "";
	private String expandFlag = "";
	private String programDescription;
	private int buildLevel;
	private int programTypeAt =  201;
	private String programType = "";
	private String debug = "";
	private String fullBuild = "";
	private List<AdfControlsVb> children = new ArrayList<AdfControlsVb>(0);
	private AdfControlsVb parent = null;
	
	private String majorBuild="";
	private String subAdfNumber="";
	private String processSequence="";
	private String templateName="";
	
	private String  businessDate = "";
	private String  filePattern = "";
	private String  excelFilePattern = "";
	private String  excelTemplateId = "";
	private String  nextProcessTime = "";
	private int acquisitionProcessTypeAt = 1079;
	private String  acquisitionProcessType = "";
	private int connectivityTypeAt = 1081;
	private String  connectivityType = "";
	private String  connectivityDetails = "";
	private int databaseTypeAt = 1082;
	private String  databaseType = "";
	private String  databaseConnectivityDetails = "";
	private String  serverFolderDetails = "";
	private int sourceScriptTypeAt = 1083;
	private String  sourceScriptType = "";
	private String  sourceServerScripts = "";
	private int targetScriptTypeAt = 1083;
	private String  targetScriptType = "";
	private String  targetServerScripts = "";
	private int readinessScriptsTypeAt = 1083;
	private String  readinessScriptsType = "";
	private String  acquisitionReadinessScripts = "";
	private int preactivityScriptTypeAt = 1083;
	private String  preactivityScriptType = "";
	private String  preactivityScripts = "";
	private int frequencyProcessAt = 1077;
	private String  frequencyProcess = "";
	private String  startTime = "";
	private String  endTime = "";
	private String  accessPermission = "";
	private String  acquisitionReadinessFlag = "";
	private String  recordsFetchedCount = "";
	private String  dateLastExtraction = "";
	private String  debugMode = "";
	private int acquisitionStatusAt = 1084;
	private String  acquisitionStatus = "";
	private int integrityScriptTypeAt = 1083;
	private String  integrityScriptType = "";
	private String  integrityScriptName = "";
	private String  alert1Timeslot = "";
	private String  alert1EmailIds = "";
	private String  alert1MobileNo = "";
	private String  alert2Timeslot = "";
	private String  alert2EmailIds = "";
	private String  alert2MobileNo = "";
	private String  alert3Timeslot = "";
	private String  alert3EmailIds = "";
	private String  alert3MobileNo = "";
	private String  adfSuccessfulEmailIds = "";
	private String  adfFailedEmailIds = "";
	private String  autoauthTime = "";
	private String  acquProcesscontrolStatus = "";
	private String  node = "";
	private String  nodeRequest = "";
	private String  nodeOverride = "";
	private String  nodeRequestTime = "";
	private String  nodeOverrideTime = "";
	private long adfNumber = 0;
	private String cbEmailIds = "";
	private String feedDate = "";
	private int  mandatoryFlagAt =1201;
	private String  mandatoryFlag ="";
	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public String getSubBuildNumber() {
		return subBuildNumber;
	}
	public void setSubBuildNumber(String subBuildNumber) {
		this.subBuildNumber = subBuildNumber;
	}
	public String getBcSequence() {
		return bcSequence;
	}
	public void setBcSequence(String bcSequence) {
		this.bcSequence = bcSequence;
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
	public String getLastStartDate() {
		return lastStartDate;
	}
	public void setLastStartDate(String lastStartDate) {
		this.lastStartDate = lastStartDate;
	}
	public String getLastBuildDate() {
		return lastBuildDate;
	}
	public void setLastBuildDate(String lastBuildDate) {
		this.lastBuildDate = lastBuildDate;
	}
	public int getBuildControlsStatusAt() {
		return buildControlsStatusAt;
	}
	public void setBuildControlsStatusAt(int buildControlsStatusAt) {
		this.buildControlsStatusAt = buildControlsStatusAt;
	}
	public String getBuildControlsStatus() {
		return buildControlsStatus;
	}
	public void setBuildControlsStatus(String ontrolbuildControlsStatussStatus) {
		this.buildControlsStatus = ontrolbuildControlsStatussStatus;
	}
	public String getSubmitterId() {
		return submitterId;
	}
	public void setSubmitterId(String submitterId) {
		this.submitterId = submitterId;
	}
	public String getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	public String getExpandFlag() {
		return expandFlag;
	}
	public void setExpandFlag(String expandFlag) {
		this.expandFlag = expandFlag;
	}
	public String getProgramDescription() {
		return programDescription;
	}
	public void setProgramDescription(String programDescription) {
		this.programDescription = programDescription;
	}
	public int getBuildLevel() {
		return buildLevel;
	}
	public void setBuildLevel(int buildLevel) {
		this.buildLevel = buildLevel;
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
	public String getDebug() {
		return debug;
	}
	public void setDebug(String debug) {
		this.debug = debug;
	}
	public List<AdfControlsVb> getChildren() {
		return children;
	}
	public void setChildren(List<AdfControlsVb> children) {
		this.children = children;
	}
	public AdfControlsVb getParent() {
		return parent;
	}
	public void setParent(AdfControlsVb parent) {
		this.parent = parent;
	}
	public String getFullBuild() {
		return fullBuild;
	}
	public void setFullBuild(String fullBuild) {
		this.fullBuild = fullBuild;
	}
	public String getMajorBuild() {
		return majorBuild;
	}
	public void setMajorBuild(String majorBuild) {
		this.majorBuild = majorBuild;
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

	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public String getFilePattern() {
		return filePattern;
	}
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}
	public String getExcelFilePattern() {
		return excelFilePattern;
	}
	public void setExcelFilePattern(String excelFilePattern) {
		this.excelFilePattern = excelFilePattern;
	}
	public String getExcelTemplateId() {
		return excelTemplateId;
	}
	public void setExcelTemplateId(String excelTemplateId) {
		this.excelTemplateId = excelTemplateId;
	}
	public String getNextProcessTime() {
		return nextProcessTime;
	}
	public void setNextProcessTime(String nextProcessTime) {
		this.nextProcessTime = nextProcessTime;
	}
	public String getAcquisitionProcessType() {
		return acquisitionProcessType;
	}
	public void setAcquisitionProcessType(String acquisitionProcessType) {
		this.acquisitionProcessType = acquisitionProcessType;
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
	public String getServerFolderDetails() {
		return serverFolderDetails;
	}
	public void setServerFolderDetails(String serverFolderDetails) {
		this.serverFolderDetails = serverFolderDetails;
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
	public String getTargetScriptType() {
		return targetScriptType;
	}
	public void setTargetScriptType(String targetScriptType) {
		this.targetScriptType = targetScriptType;
	}
	public String getTargetServerScripts() {
		return targetServerScripts;
	}
	public void setTargetServerScripts(String targetServerScripts) {
		this.targetServerScripts = targetServerScripts;
	}
	public String getReadinessScriptsType() {
		return readinessScriptsType;
	}
	public void setReadinessScriptsType(String readinessScriptsType) {
		this.readinessScriptsType = readinessScriptsType;
	}
	public String getAcquisitionReadinessScripts() {
		return acquisitionReadinessScripts;
	}
	public void setAcquisitionReadinessScripts(String acquisitionReadinessScripts) {
		this.acquisitionReadinessScripts = acquisitionReadinessScripts;
	}
	public String getPreactivityScriptType() {
		return preactivityScriptType;
	}
	public void setPreactivityScriptType(String preactivityScriptType) {
		this.preactivityScriptType = preactivityScriptType;
	}
	public String getPreactivityScripts() {
		return preactivityScripts;
	}
	public void setPreactivityScripts(String preactivityScripts) {
		this.preactivityScripts = preactivityScripts;
	}
	public String getFrequencyProcess() {
		return frequencyProcess;
	}
	public void setFrequencyProcess(String frequencyProcess) {
		this.frequencyProcess = frequencyProcess;
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
	public String getAccessPermission() {
		return accessPermission;
	}
	public void setAccessPermission(String accessPermission) {
		this.accessPermission = accessPermission;
	}
	public String getAcquisitionReadinessFlag() {
		return acquisitionReadinessFlag;
	}
	public void setAcquisitionReadinessFlag(String acquisitionReadinessFlag) {
		this.acquisitionReadinessFlag = acquisitionReadinessFlag;
	}
	public String getRecordsFetchedCount() {
		return recordsFetchedCount;
	}
	public void setRecordsFetchedCount(String recordsFetchedCount) {
		this.recordsFetchedCount = recordsFetchedCount;
	}
	public String getDateLastExtraction() {
		return dateLastExtraction;
	}
	public void setDateLastExtraction(String dateLastExtraction) {
		this.dateLastExtraction = dateLastExtraction;
	}
	public String getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
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
	public String getIntegrityScriptName() {
		return integrityScriptName;
	}
	public void setIntegrityScriptName(String integrityScriptName) {
		this.integrityScriptName = integrityScriptName;
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
	public String getAdfSuccessfulEmailIds() {
		return adfSuccessfulEmailIds;
	}
	public void setAdfSuccessfulEmailIds(String adfSuccessfulEmailIds) {
		this.adfSuccessfulEmailIds = adfSuccessfulEmailIds;
	}
	public String getAdfFailedEmailIds() {
		return adfFailedEmailIds;
	}
	public void setAdfFailedEmailIds(String adfFailedEmailIds) {
		this.adfFailedEmailIds = adfFailedEmailIds;
	}
	public String getAutoauthTime() {
		return autoauthTime;
	}
	public void setAutoauthTime(String autoauthTime) {
		this.autoauthTime = autoauthTime;
	}
	public String getAcquProcesscontrolStatus() {
		return acquProcesscontrolStatus;
	}
	public void setAcquProcesscontrolStatus(String acquProcesscontrolStatus) {
		this.acquProcesscontrolStatus = acquProcesscontrolStatus;
	}
	public int getAcquisitionProcessTypeAt() {
		return acquisitionProcessTypeAt;
	}
	public void setAcquisitionProcessTypeAt(int acquisitionProcessTypeAt) {
		this.acquisitionProcessTypeAt = acquisitionProcessTypeAt;
	}
	public int getConnectivityTypeAt() {
		return connectivityTypeAt;
	}
	public void setConnectivityTypeAt(int connectivityTypeAt) {
		this.connectivityTypeAt = connectivityTypeAt;
	}
	public int getDatabaseTypeAt() {
		return databaseTypeAt;
	}
	public void setDatabaseTypeAt(int databaseTypeAt) {
		this.databaseTypeAt = databaseTypeAt;
	}
	public int getSourceScriptTypeAt() {
		return sourceScriptTypeAt;
	}
	public void setSourceScriptTypeAt(int sourceScriptTypeAt) {
		this.sourceScriptTypeAt = sourceScriptTypeAt;
	}
	public int getTargetScriptTypeAt() {
		return targetScriptTypeAt;
	}
	public void setTargetScriptTypeAt(int targetScriptTypeAt) {
		this.targetScriptTypeAt = targetScriptTypeAt;
	}
	public int getReadinessScriptsTypeAt() {
		return readinessScriptsTypeAt;
	}
	public void setReadinessScriptsTypeAt(int readinessScriptsTypeAt) {
		this.readinessScriptsTypeAt = readinessScriptsTypeAt;
	}
	public int getPreactivityScriptTypeAt() {
		return preactivityScriptTypeAt;
	}
	public void setPreactivityScriptTypeAt(int preactivityScriptTypeAt) {
		this.preactivityScriptTypeAt = preactivityScriptTypeAt;
	}
	public int getFrequencyProcessAt() {
		return frequencyProcessAt;
	}
	public void setFrequencyProcessAt(int frequencyProcessAt) {
		this.frequencyProcessAt = frequencyProcessAt;
	}
	public int getAcquisitionStatusAt() {
		return acquisitionStatusAt;
	}
	public void setAcquisitionStatusAt(int acquisitionStatusAt) {
		this.acquisitionStatusAt = acquisitionStatusAt;
	}
	public int getIntegrityScriptTypeAt() {
		return integrityScriptTypeAt;
	}
	public void setIntegrityScriptTypeAt(int integrityScriptTypeAt) {
		this.integrityScriptTypeAt = integrityScriptTypeAt;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getNodeRequest() {
		return nodeRequest;
	}
	public void setNodeRequest(String nodeRequest) {
		this.nodeRequest = nodeRequest;
	}
	public String getNodeOverride() {
		return nodeOverride;
	}
	public void setNodeOverride(String nodeOverride) {
		this.nodeOverride = nodeOverride;
	}
	public String getNodeRequestTime() {
		return nodeRequestTime;
	}
	public void setNodeRequestTime(String nodeRequestTime) {
		this.nodeRequestTime = nodeRequestTime;
	}
	public String getNodeOverrideTime() {
		return nodeOverrideTime;
	}
	public void setNodeOverrideTime(String nodeOverrideTime) {
		this.nodeOverrideTime = nodeOverrideTime;
	}
	public long getAdfNumber() {
		return adfNumber;
	}
	public void setAdfNumber(long adfNumber) {
		this.adfNumber = adfNumber;
	}
	public String getCbEmailIds() {
		return cbEmailIds;
	}
	public void setCbEmailIds(String cbEmailIds) {
		this.cbEmailIds = cbEmailIds;
	}
	public String getFeedDate() {
		return feedDate;
	}
	public void setFeedDate(String feedDate) {
		this.feedDate = feedDate;
	}
	public int getMandatoryFlagAt() {
		return mandatoryFlagAt;
	}
	public void setMandatoryFlagAt(int mandatoryFlagAt) {
		this.mandatoryFlagAt = mandatoryFlagAt;
	}
	public String getMandatoryFlag() {
		return mandatoryFlag;
	}
	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
}