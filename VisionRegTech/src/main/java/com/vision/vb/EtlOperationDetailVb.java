package com.vision.vb;

public class EtlOperationDetailVb extends CommonVb{
	private String posBusDate = "";
	private String frequency = "";
	private String extractionName = "";
	private String EtlCategory = "";
	private String feedDescription = "";
	private String startTime = "";
	private String endTime = "";
	private String duration = "";
	private String etlStatus = "";
	private String executionType = "";
	private String logFile = "";
	
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getExtractionName() {
		return extractionName;
	}
	public void setExtractionName(String extractionName) {
		this.extractionName = extractionName;
	}
	public String getEtlCategory() {
		return EtlCategory;
	}
	public void setEtlCategory(String etlCategory) {
		EtlCategory = etlCategory;
	}
	public String getFeedDescription() {
		return feedDescription;
	}
	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
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
	public String getEtlStatus() {
		return etlStatus;
	}
	public void setEtlStatus(String etlStatus) {
		this.etlStatus = etlStatus;
	}
	public String getLogFile() {
		return logFile;
	}
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	public String getExecutionType() {
		return executionType;
	}
	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}
	public String getPosBusDate() {
		return posBusDate;
	}
	public void setPosBusDate(String posBusDate) {
		this.posBusDate = posBusDate;
	}
}
