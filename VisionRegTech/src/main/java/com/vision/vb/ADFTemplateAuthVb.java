package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class ADFTemplateAuthVb extends CommonVb{
	
	private static final long serialVersionUID = 2942783983913844700L;

	private String	templateName = "";
	//private int recordCount =0;
	private String recordCount ;
	private String	businessDate = "";
	private String startTime ="";
	private String endTime ="";
	private String timeCollapsed ="";
	private int	ADFTempAuthStatusAt = 1084;
	private String	ADFTempAuthStatus = "";
	private String	ADFTempAuthStatusDesc = "";
	private String comment ="";
	private String review ="";
	private String outputTab = "";
	private String status = "";
	private String errorMessage = "";
	private String legalVehicle = "";
	private long authId;
	private String authTime= "";
	private int adfNumber;
	private String feedStgName= "";
	private String feedcategory= "";
	private String feedDate= "";
	private int emailStatus = 0;
	private int authStatusNt = 1065;
	private int	authStatus = -1;
	private String	authStatusDesc = "";
	private String	templateNameDesc = "";
	private String	emailNote = "";
	private String	emailID = "";
	private String	query = "";
	private String	tempTable = "";
	private String	processFrequency = "";
	private String	description = "";
	private String eodInitiatedFlag = "";
	private String errorCode = "";
	private String errColumnName = "";
	private String errDescription = "";
	private String primaryKeyColumn = "";
	private String columnValue = "";
	private String errType = "";
	private String majorBuild = "";
	List<ADFTemplateAuthVb> childs =new ArrayList<ADFTemplateAuthVb>(0);
//	private String acquisitionProcessType = "-1";
	private String fileName = "";   

	public String getErrType() {
		return errType;
	}
	public void setErrType(String errType) {
		this.errType = errType;
	}
	private List<ADFTemplateAuthVb> paginationResults = new ArrayList<ADFTemplateAuthVb>(0);

	public List<ADFTemplateAuthVb> getPaginationResults() {
		return paginationResults;
	}
	public void setPaginationResults(List<ADFTemplateAuthVb> paginationResults) {
		this.paginationResults = paginationResults;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getADFTempAuthStatusDesc() {
		return ADFTempAuthStatusDesc;
	}
	public void setADFTempAuthStatusDesc(String aDFTempAuthStatusDesc) {
		ADFTempAuthStatusDesc = aDFTempAuthStatusDesc;
	}
	public String getEmailNote() {
		return emailNote;
	}
	public void setEmailNote(String emailNote) {
		this.emailNote = emailNote;
	}
	public String getTemplateNameDesc() {
		return templateNameDesc;
	}
	public void setTemplateNameDesc(String templateNameDesc) {
		this.templateNameDesc = templateNameDesc;
	}
	public int getEmailStatus() {
		return emailStatus;
	}
	public void setEmailStatus(int emailStatus) {
		this.emailStatus = emailStatus;
	}
	public int getAdfNumber() {
		return adfNumber;
	}
	public void setAdfNumber(int adfNumber) {
		this.adfNumber = adfNumber;
	}
	public String getFeedStgName() {
		return feedStgName;
	}
	public void setFeedStgName(String feedStgName) {
		this.feedStgName = feedStgName;
	}
	public String getFeedcategory() {
		return feedcategory;
	}
	public void setFeedcategory(String feedcategory) {
		this.feedcategory = feedcategory;
	}
	public String getFeedDate() {
		return feedDate;
	}
	public void setFeedDate(String feedDate) {
		this.feedDate = feedDate;
	}
	public long getAuthId() {
		return authId;
	}
	public void setAuthId(long authId) {
		this.authId = authId;
	}
	public String getAuthTime() {
		return authTime;
	}
	public void setAuthTime(String authTime) {
		this.authTime = authTime;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	/*public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}*/
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
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
	public String getTimeCollapsed() {
		return timeCollapsed;
	}
	public void setTimeCollapsed(String timeCollapsed) {
		this.timeCollapsed = timeCollapsed;
	}
	public int getADFTempAuthStatusAt() {
		return ADFTempAuthStatusAt;
	}
	public void setADFTempAuthStatusAt(int aDFTempAuthStatusAt) {
		ADFTempAuthStatusAt = aDFTempAuthStatusAt;
	}
	public String getADFTempAuthStatus() {
		return ADFTempAuthStatus;
	}
	public void setADFTempAuthStatus(String aDFTempAuthStatus) {
		ADFTempAuthStatus = aDFTempAuthStatus;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public String getOutputTab() {
		return outputTab;
	}
	public void setOutputTab(String outputTab) {
		this.outputTab = outputTab;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getLegalVehicle() {
		return legalVehicle;
	}
	public void setLegalVehicle(String legalVehicle) {
		this.legalVehicle = legalVehicle;
	}
	public String getTempTable() {
		return tempTable;
	}
	public void setTempTable(String tempTable) {
		this.tempTable = tempTable;
	}
	public String getProcessFrequency() {
		return processFrequency;
	}
	public void setProcessFrequency(String processFrequency) {
		this.processFrequency = processFrequency;
	}
	public int getAuthStatusNt() {
		return authStatusNt;
	}
	public void setAuthStatusNt(int authStatusNt) {
		this.authStatusNt = authStatusNt;
	}
	public int getAuthStatus() {
		return authStatus;
	}
	public void setAuthStatus(int authStatus) {
		this.authStatus = authStatus;
	}
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEodInitiatedFlag() {
		return eodInitiatedFlag;
	}
	public void setEodInitiatedFlag(String eodInitiatedFlag) {
		this.eodInitiatedFlag = eodInitiatedFlag;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrColumnName() {
		return errColumnName;
	}
	public void setErrColumnName(String errColumnName) {
		this.errColumnName = errColumnName;
	}
	public String getErrDescription() {
		return errDescription;
	}
	public void setErrDescription(String errDescription) {
		this.errDescription = errDescription;
	}
	public String getPrimaryKeyColumn() {
		return primaryKeyColumn;
	}
	public void setPrimaryKeyColumn(String primaryKeyColumn) {
		this.primaryKeyColumn = primaryKeyColumn;
	}
	public String getColumnValue() {
		return columnValue;
	}
	public void setColumnValue(String columnValue) {
		this.columnValue = columnValue;
	}
	public String getMajorBuild() {
		return majorBuild;
	}
	public void setMajorBuild(String majorBuild) {
		this.majorBuild = majorBuild;
	}
	/*public String getAcquisitionProcessType() {
		return acquisitionProcessType;
	}
	public void setAcquisitionProcessType(String acquisitionProcessType) {
		this.acquisitionProcessType = acquisitionProcessType;
	}*/
	public List<ADFTemplateAuthVb> getChilds() {
		return childs;
	}
	public void setChilds(List<ADFTemplateAuthVb> childs) {
		this.childs = childs;
	}
	public String getAuthStatusDesc() {
		return authStatusDesc;
	}
	public void setAuthStatusDesc(String authStatusDesc) {
		this.authStatusDesc = authStatusDesc;
	}
	public String getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
