package com.vision.vb;

public class EtlManagerDetailsVb extends CommonVb{
	private String extractionFrequency= "";
	private String extractionSequence= "";
	private int feedCategoryAt = 7037;
	private String feedCategory = "";
	private String feedId = "";
	private String debugMode= "";
	private String dependentFlag= "";
	private String dependentFeedId= "";
	
	public String getExtractionFrequency() {
		return extractionFrequency;
	}
	public void setExtractionFrequency(String extractionFrequency) {
		this.extractionFrequency = extractionFrequency;
	}
	public String getExtractionSequence() {
		return extractionSequence;
	}
	public void setExtractionSequence(String extractionSequence) {
		this.extractionSequence = extractionSequence;
	}
	public String getFeedCategory() {
		return feedCategory;
	}
	public void setFeedCategory(String feedCategory) {
		this.feedCategory = feedCategory;
	}
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	public String getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
	}
	public String getDependentFlag() {
		return dependentFlag;
	}
	public void setDependentFlag(String dependentFlag) {
		this.dependentFlag = dependentFlag;
	}
	public String getDependentFeedId() {
		return dependentFeedId;
	}
	public void setDependentFeedId(String dependentFeedId) {
		this.dependentFeedId = dependentFeedId;
	}
	public int getFeedCategoryAt() {
		return feedCategoryAt;
	}
	public void setFeedCategoryAt(int feedCategoryAt) {
		this.feedCategoryAt = feedCategoryAt;
	}
}
