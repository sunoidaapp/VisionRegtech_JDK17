package com.vision.vb;

public class TemplateMappingVb extends CommonVb {
	public String templateId;
	public int sequenceNo;
	public String sourceColumn;
	public String targetColumn;
	public int mappingStatusNt =1;
	public int mappingStatus =-1;
	public String mappingStatusDesc;
	public String autoFilter = "N";

	public String getAutoFilter() {
		return autoFilter;
	}

	public void setAutoFilter(String autoFilter) {
		this.autoFilter = autoFilter;
	}

	public String getMappingStatusDesc() {
		return mappingStatusDesc;
	}

	public void setMappingStatusDesc(String mappingStatusDesc) {
		this.mappingStatusDesc = mappingStatusDesc;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public int getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(int sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getSourceColumn() {
		return sourceColumn;
	}

	public void setSourceColumn(String sourceColumn) {
		this.sourceColumn = sourceColumn;
	}

	public String getTargetColumn() {
		return targetColumn;
	}

	public void setTargetColumn(String targetColumn) {
		this.targetColumn = targetColumn;
	}

	public int getMappingStatusNt() {
		return mappingStatusNt;
	}

	public void setMappingStatusNt(int mappingStatusNt) {
		this.mappingStatusNt = mappingStatusNt;
	}

	public int getMappingStatus() {
		return mappingStatus;
	}

	public void setMappingStatus(int mappingStatus) {
		this.mappingStatus = mappingStatus;
	}

}
