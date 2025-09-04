package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class AdfSchedulesVb extends CommonVb {

	private static final long serialVersionUID = 1L;
	private String build = "";//Should be removed
	private String scheduledDate = "";
	private String buildNumber = "";//Should be removed
	private int parallelProcsCount = 0;
	private String notify = "";
	private String supportContact = "";
	private String supportContactNumber = "";
	private String seriousErrorCnt = "";
	private String warningErrorCnt = "";

	private int submitterId = 0;
	private String startTime = "";
	private String submitterName = "";

	private String endTime = "";
	private int	buildScheduleStatusAt = 206;
	private String buildScheduleStatus = "";
	private int recurringFrequencyAt = 213;
	private String recurringFrequency = "";
	private String expandFlag = "";
	private String duration = "";
	private String dbDateTime = "";
	private List<AdfSchedulesDetailsVb> adfSchedulesDetailsList = new ArrayList<AdfSchedulesDetailsVb>(0);
	private List<AdfControlsVb> adfControlsList = new ArrayList<AdfControlsVb>(0);
	private List<AdfSchedulesVb> childs = new ArrayList<AdfSchedulesVb>(0);
	private String  requestType = "";
	private FileInfoVb fileInfoVb;
	
	private String node = "";
	private String businessDate = "";
	private String feedDate = "";
	private long cronCount = 1;
	private String cronName = "";
	private String cronStatus = "";
	private String startCronNode = "";
	private String adfName = "";

	private String startCronHostName = "";
	private String stakeHolder = "";
	
	private String status = "";

	private String majorBuild = "";
	private String templateName = "";
	private String adfNumber = "";
	private int	adfScheduleStatusAt = 206;
	private String adfScheduleStatus = "";
	private String nodeOverride = "";
	private String nodeRequest = "";
	private String NodeOverrideTime = "";
	private String NodeRequestTime = "";
	private String operationFroPopUp="";
	private String eodModifyFlag="";
	private String reInitiateOperation = "";
	private String reInitiateStatus = "";
	private String acquisitionProcessType = "";
	private int acquisitionProcessTypeAT = 1079;
	private String updateAllAvailableTemplateStatus;
	private String adfType = "";
	private String adfCustomStatus = "";
	private String frequencyProcess = "";
	private int frequencyProcessAt = 1077;
	private int makerId = 0; 
	private String logStartTime ="";
	private String categoryType ="";
	
	private String caption1;
	private String caption2;
	private String caption3;
	private String reportId="";
	private String procedure1; 
	private String drillDownId;
	private String displayName;
	
	private String promptId1; 
	private String promptId2;
	private String promptId3;
	private String promptId4;
	private String promptId5;
	private String promptId6;
	
	private String promptString4;
	
	
	private String reportTitle;
	private String orientation;
	private String scalingFactor;
	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public String getScheduledDate() {
		return scheduledDate;
	}
	public void setScheduledDate(String scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	public String getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	public int getParallelProcsCount() {
		return parallelProcsCount;
	}
	public void setParallelProcsCount(int parallelProcsCount) {
		this.parallelProcsCount = parallelProcsCount;
	}
	public String getNotify() {
		return notify;
	}
	public void setNotify(String notify) {
		this.notify = notify;
	}
	public String getSupportContact() {
		return supportContact;
	}
	public void setSupportContact(String supportContact) {
		this.supportContact = supportContact;
	}
	public String getSupportContactNumber() {
		return supportContactNumber;
	}
	public void setSupportContactNumber(String supportContactNumber) {
		this.supportContactNumber = supportContactNumber;
	}
	public int getSubmitterId() {
		return submitterId;
	}
	public void setSubmitterId(int submitterId) {
		this.submitterId = submitterId;
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
	public int getBuildScheduleStatusAt() {
		return buildScheduleStatusAt;
	}
	public void setBuildScheduleStatusAt(int buildScheduleStatusAt) {
		this.buildScheduleStatusAt = buildScheduleStatusAt;
	}
	public String getBuildScheduleStatus() {
		return buildScheduleStatus;
	}
	public void setBuildScheduleStatus(String buildScheduleStatus) {
		this.buildScheduleStatus = buildScheduleStatus;
	}
	public int getRecurringFrequencyAt() {
		return recurringFrequencyAt;
	}
	public void setRecurringFrequencyAt(int recurringFrequencyAt) {
		this.recurringFrequencyAt = recurringFrequencyAt;
	}
	public String getRecurringFrequency() {
		return recurringFrequency;
	}
	public void setRecurringFrequency(String recurringFrequency) {
		this.recurringFrequency = recurringFrequency;
	}
	public String getExpandFlag() {
		return expandFlag;
	}
	public void setExpandFlag(String expandFlag) {
		this.expandFlag = expandFlag;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	public String getCronStatus() {
		return cronStatus;
	}
	public void setCronStatus(String cronStatus) {
		this.cronStatus = cronStatus;
	}
	/*public List<AdfSchedulesDetailsVb> getBuildSchedulesDetails() {
		return adfSchedulesDetailsVb;
	}
	public void setBuildSchedulesDetails(
			List<AdfSchedulesDetailsVb> adfSchedulesDetailsVb) {
		this.adfSchedulesDetailsVb = adfSchedulesDetailsVb;
	}*/
	public FileInfoVb getFileInfoVb() {
		return fileInfoVb;
	}
	public void setFileInfoVb(FileInfoVb fileInfoVb) {
		this.fileInfoVb = fileInfoVb;
	}
	public String getDbDateTime() {
		return dbDateTime;
	}
	public void setDbDateTime(String dbDateTime) {
		this.dbDateTime = dbDateTime;
	}
	public long getCronCount() {
		return cronCount;
	}
	public void setCronCount(long cronCount) {
		this.cronCount = cronCount;
	}
	public String getCronName() {
		return cronName;
	}
	public void setCronName(String cronName) {
		this.cronName = cronName;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
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
	public String getStartCronNode() {
		return startCronNode;
	}
	public void setStartCronNode(String startCronNode) {
		this.startCronNode = startCronNode;
	}
	public String getStartCronHostName() {
		return startCronHostName;
	}
	public void setStartCronHostName(String startCronHostName) {
		this.startCronHostName = startCronHostName;
	}
	public String getMajorBuild() {
		return majorBuild;
	}
	public void setMajorBuild(String majorBuild) {
		this.majorBuild = majorBuild;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getAdfNumber() {
		return adfNumber;
	}
	public void setAdfNumber(String adfNumber) {
		this.adfNumber = adfNumber;
	}
	public int getAdfScheduleStatusAt() {
		return adfScheduleStatusAt;
	}
	public void setAdfScheduleStatusAt(int adfScheduleStatusAt) {
		this.adfScheduleStatusAt = adfScheduleStatusAt;
	}
	public String getAdfScheduleStatus() {
		return adfScheduleStatus;
	}
	public void setAdfScheduleStatus(String adfScheduleStatus) {
		this.adfScheduleStatus = adfScheduleStatus;
	}
	public String getNodeOverrideTime() {
		return NodeOverrideTime;
	}
	public void setNodeOverrideTime(String nodeOverrideTime) {
		NodeOverrideTime = nodeOverrideTime;
	}
	public String getNodeRequestTime() {
		return NodeRequestTime;
	}
	public void setNodeRequestTime(String nodeRequestTime) {
		NodeRequestTime = nodeRequestTime;
	}
	public String getNodeOverride() {
		return nodeOverride;
	}
	public void setNodeOverride(String nodeOverride) {
		this.nodeOverride = nodeOverride;
	}
	public String getNodeRequest() {
		return nodeRequest;
	}
	public void setNodeRequest(String nodeRequest) {
		this.nodeRequest = nodeRequest;
	}
	public String getOperationFroPopUp() {
		return operationFroPopUp;
	}
	public void setOperationFroPopUp(String operationFroPopUp) {
		this.operationFroPopUp = operationFroPopUp;
	}
	public String getEodModifyFlag() {
		return eodModifyFlag;
	}
	public void setEodModifyFlag(String eodModifyFlag) {
		this.eodModifyFlag = eodModifyFlag;
	}
	public String getReInitiateOperation() {
		return reInitiateOperation;
	}
	public void setReInitiateOperation(String reInitiateOperation) {
		this.reInitiateOperation = reInitiateOperation;
	}
	public String getReInitiateStatus() {
		return reInitiateStatus;
	}
	public void setReInitiateStatus(String reInitiateStatus) {
		this.reInitiateStatus = reInitiateStatus;
	}
	public List<AdfSchedulesDetailsVb> getAdfSchedulesDetailsList() {
		return adfSchedulesDetailsList;
	}
	public void setAdfSchedulesDetailsList(
			List<AdfSchedulesDetailsVb> adfSchedulesDetailsList) {
		this.adfSchedulesDetailsList = adfSchedulesDetailsList;
	}
	public List<AdfControlsVb> getAdfControlsList() {
		return adfControlsList;
	}
	public void setAdfControlsList(List<AdfControlsVb> adfControlsList) {
		this.adfControlsList = adfControlsList;
	}
	public String getAcquisitionProcessType() {
		return acquisitionProcessType;
	}
	public void setAcquisitionProcessType(String acquisitionProcessType) {
		this.acquisitionProcessType = acquisitionProcessType;
	}
	public int getAcquisitionProcessTypeAT() {
		return acquisitionProcessTypeAT;
	}
	public void setAcquisitionProcessTypeAT(int acquisitionProcessTypeAT) {
		this.acquisitionProcessTypeAT = acquisitionProcessTypeAT;
	}
	public String getUpdateAllAvailableTemplateStatus() {
		return updateAllAvailableTemplateStatus;
	}
	public void setUpdateAllAvailableTemplateStatus(
			String updateAllAvailableTemplateStatus) {
		this.updateAllAvailableTemplateStatus = updateAllAvailableTemplateStatus;
	}
	public String getAdfType() {
		return adfType;
	}
	public void setAdfType(String adfType) {
		this.adfType = adfType;
	}
	public String getAdfCustomStatus() {
		return adfCustomStatus;
	}
	public void setAdfCustomStatus(String adfCustomStatus) {
		this.adfCustomStatus = adfCustomStatus;
	}
	public String getFrequencyProcess() {
		return frequencyProcess;
	}
	public void setFrequencyProcess(String frequencyProcess) {
		this.frequencyProcess = frequencyProcess;
	}
	public int getFrequencyProcessAt() {
		return frequencyProcessAt;
	}
	public void setFrequencyProcessAt(int frequencyProcessAt) {
		this.frequencyProcessAt = frequencyProcessAt;
	}
	public int getMakerId() {
		return makerId;
	}
	public void setMakerId(int makerId) {
		this.makerId = makerId;
	}
	public String getLogStartTime() {
		return logStartTime;
	}
	public void setLogStartTime(String logStartTime) {
		this.logStartTime = logStartTime;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	public List<AdfSchedulesVb> getChilds() {
		return childs;
	}
	public void setChilds(List<AdfSchedulesVb> childs) {
		this.childs = childs;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getStakeHolder() {
		return stakeHolder;
	}
	public void setStakeHolder(String stakeHolder) {
		this.stakeHolder = stakeHolder;
	}

	public String getAdfName() {
		return adfName;
	}
	public void setAdfName(String adfName) {
		this.adfName = adfName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubmitterName() {
		return submitterName;
	}
	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}
	public String getSeriousErrorCnt() {
		return seriousErrorCnt;
	}
	public void setSeriousErrorCnt(String seriousErrorCnt) {
		this.seriousErrorCnt = seriousErrorCnt;
	}
	public String getWarningErrorCnt() {
		return warningErrorCnt;
	}
	public void setWarningErrorCnt(String warningErrorCnt) {
		this.warningErrorCnt = warningErrorCnt;
	}
	public String getCaption1() {
		return caption1;
	}
	public void setCaption1(String caption1) {
		this.caption1 = caption1;
	}
	public String getCaption2() {
		return caption2;
	}
	public void setCaption2(String caption2) {
		this.caption2 = caption2;
	}
	public String getCaption3() {
		return caption3;
	}
	public void setCaption3(String caption3) {
		this.caption3 = caption3;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getProcedure1() {
		return procedure1;
	}
	public void setProcedure1(String procedure1) {
		this.procedure1 = procedure1;
	}
	public String getDrillDownId() {
		return drillDownId;
	}
	public void setDrillDownId(String drillDownId) {
		this.drillDownId = drillDownId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPromptId1() {
		return promptId1;
	}
	public void setPromptId1(String promptId1) {
		this.promptId1 = promptId1;
	}
	public String getPromptId2() {
		return promptId2;
	}
	public void setPromptId2(String promptId2) {
		this.promptId2 = promptId2;
	}
	public String getPromptId3() {
		return promptId3;
	}
	public void setPromptId3(String promptId3) {
		this.promptId3 = promptId3;
	}
	public String getPromptId4() {
		return promptId4;
	}
	public void setPromptId4(String promptId4) {
		this.promptId4 = promptId4;
	}
	public String getPromptId5() {
		return promptId5;
	}
	public void setPromptId5(String promptId5) {
		this.promptId5 = promptId5;
	}
	public String getPromptId6() {
		return promptId6;
	}
	public void setPromptId6(String promptId6) {
		this.promptId6 = promptId6;
	}
	public String getPromptString4() {
		return promptString4;
	}
	public void setPromptString4(String promptString4) {
		this.promptString4 = promptString4;
	}
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public String getScalingFactor() {
		return scalingFactor;
	}
	public void setScalingFactor(String scalingFactor) {
		this.scalingFactor = scalingFactor;
	}
	
}