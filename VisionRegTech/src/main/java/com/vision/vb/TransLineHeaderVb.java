package com.vision.vb;

import java.util.List;

public class TransLineHeaderVb extends CommonVb{

	private String transLineId = "";
	private String transLineDescription = "";
	private String transLineTypeAT = null;
	private String transLineType = "";
	private String transLineTypeDesc = "";
	
	private int transLineSubTypeAT = 7024;
	private String transLineSubType = "";
	private String transLineSubTypeDesc = "";
	private int transLineGrpAT = 7021;
	private String transLineGrp = "";
	private String transLineGrpDesc = "";
	
	private int extractionFrequencyAT = 7015;
	private String extractionFrequency = "";
	private String extractionFrequencyDesc = "";
	private String extractionMonthDay = "";
	private String extractionMonthDayDesc = "";
	
	private int targetStgTableIdAT = 7023;
	private String targetStgTableId = "";
	private int transLineStatusNT = 1;
	private int transLineStatus = 0;
	private String transLineStatusDesc = "";
	
	private String businessVertical = "";
	private String channelId = "";
	
	private int departmentAT = 7041;
	private String department = "";
	
	List<TransLineGLVb> transLineGllst = null;
	List<SmartSearchVb> smartSearchOpt = null;

	public String getTransLineId() {
		return transLineId;
	}

	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}

	public String getTransLineDescription() {
		return transLineDescription;
	}

	public void setTransLineDescription(String transLineDescription) {
		this.transLineDescription = transLineDescription;
	}

	public String getTransLineType() {
		return transLineType;
	}

	public void setTransLineType(String transLineType) {
		this.transLineType = transLineType;
	}


	public String getExtractionFrequency() {
		return extractionFrequency;
	}

	public void setExtractionFrequency(String extractionFrequency) {
		this.extractionFrequency = extractionFrequency;
	}

	public String getExtractionMonthDay() {
		return extractionMonthDay;
	}

	public void setExtractionMonthDay(String extractionMonthDay) {
		this.extractionMonthDay = extractionMonthDay;
	}

	public String getTargetStgTableId() {
		return targetStgTableId;
	}

	public void setTargetStgTableId(String targetStgTableId) {
		this.targetStgTableId = targetStgTableId;
	}

	public int getTransLineStatusNT() {
		return transLineStatusNT;
	}

	public void setTransLineStatusNT(int transLineStatusNT) {
		this.transLineStatusNT = transLineStatusNT;
	}

	public int getTransLineStatus() {
		return transLineStatus;
	}

	public void setTransLineStatus(int transLineStatus) {
		this.transLineStatus = transLineStatus;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}


	public String getExtractionFrequencyDesc() {
		return extractionFrequencyDesc;
	}

	public void setExtractionFrequencyDesc(String extractionFrequencyDesc) {
		this.extractionFrequencyDesc = extractionFrequencyDesc;
	}

	public String getTransLineStatusDesc() {
		return transLineStatusDesc;
	}

	public void setTransLineStatusDesc(String transLineStatusDesc) {
		this.transLineStatusDesc = transLineStatusDesc;
	}

	public List<TransLineGLVb> getTransLineGllst() {
		return transLineGllst;
	}

	public void setTransLineGllst(List<TransLineGLVb> transLineGllst) {
		this.transLineGllst = transLineGllst;
	}

	public String getBusinessVertical() {
		return businessVertical;
	}

	public void setBusinessVertical(String businessVertical) {
		this.businessVertical = businessVertical;
	}

	public String getExtractionMonthDayDesc() {
		return extractionMonthDayDesc;
	}

	public void setExtractionMonthDayDesc(String extractionMonthDayDesc) {
		this.extractionMonthDayDesc = extractionMonthDayDesc;
	}

	public int getExtractionFrequencyAT() {
		return extractionFrequencyAT;
	}

	public void setExtractionFrequencyAT(int extractionFrequencyAT) {
		this.extractionFrequencyAT = extractionFrequencyAT;
	}

	public int getTargetStgTableIdAT() {
		return targetStgTableIdAT;
	}

	public void setTargetStgTableIdAT(int targetStgTableIdAT) {
		this.targetStgTableIdAT = targetStgTableIdAT;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}


	public String getTransLineTypeDesc() {
		return transLineTypeDesc;
	}

	public void setTransLineTypeDesc(String transLineTypeDesc) {
		this.transLineTypeDesc = transLineTypeDesc;
	}

	public int getDepartmentAT() {
		return departmentAT;
	}

	public void setDepartmentAT(int departmentAT) {
		this.departmentAT = departmentAT;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public int getTransLineSubTypeAT() {
		return transLineSubTypeAT;
	}

	public void setTransLineSubTypeAT(int transLineSubTypeAT) {
		this.transLineSubTypeAT = transLineSubTypeAT;
	}

	public String getTransLineSubType() {
		return transLineSubType;
	}

	public void setTransLineSubType(String transLineSubType) {
		this.transLineSubType = transLineSubType;
	}

	public String getTransLineSubTypeDesc() {
		return transLineSubTypeDesc;
	}

	public void setTransLineSubTypeDesc(String transLineSubTypeDesc) {
		this.transLineSubTypeDesc = transLineSubTypeDesc;
	}

	public int getTransLineGrpAT() {
		return transLineGrpAT;
	}

	public void setTransLineGrpAT(int transLineGrpAT) {
		this.transLineGrpAT = transLineGrpAT;
	}

	public String getTransLineGrp() {
		return transLineGrp;
	}

	public void setTransLineGrp(String transLineGrp) {
		this.transLineGrp = transLineGrp;
	}

	public String getTransLineGrpDesc() {
		return transLineGrpDesc;
	}

	public void setTransLineGrpDesc(String transLineGrpDesc) {
		this.transLineGrpDesc = transLineGrpDesc;
	}

	public String getTransLineTypeAT() {
		return transLineTypeAT;
	}

	public void setTransLineTypeAT(String transLineTypeAT) {
		this.transLineTypeAT = transLineTypeAT;
	}
}
