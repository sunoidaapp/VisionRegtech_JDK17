package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class RgFatcaRuleVb extends CommonVb{

	private int visionSbuAt = 3 ;
	private String visionSbu = "";
	private String visionSbuDesc = "";
	private String ruleId = "";
	private String ruleDescription = "";
	private int priority = 0 ;
	private int ruleStatusNt = 1 ;
	private int ruleStatus = 0 ;
	private String runDate = "";
	private int ruleCount = 0;
	private String variable = "";//VARIABLE - Key Field
	private String value = "";
	
	List<RgFatcaRuleDetailsVb> ruleDetailsList = new ArrayList<RgFatcaRuleDetailsVb>();
	List<SmartSearchVb> smartSearchOpt = null;
	
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public void setVisionSbuAt(int visionSbuAt) {
		this.visionSbuAt = visionSbuAt; 
	}

	public int getVisionSbuAt() {
		return visionSbuAt; 
	}
	public void setVisionSbu(String visionSbu) {
		this.visionSbu = visionSbu; 
	}

	public String getVisionSbu() {
		return visionSbu; 
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId; 
	}

	public String getRuleId() {
		return ruleId; 
	}
	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription; 
	}

	public String getRuleDescription() {
		return ruleDescription; 
	}
	public void setPriority(int priority) {
		this.priority = priority; 
	}

	public int getPriority() {
		return priority; 
	}
	public void setRuleStatusNt(int ruleStatusNt) {
		this.ruleStatusNt = ruleStatusNt; 
	}

	public int getRuleStatusNt() {
		return ruleStatusNt; 
	}
	public void setRuleStatus(int ruleStatus) {
		this.ruleStatus = ruleStatus; 
	}

	public int getRuleStatus() {
		return ruleStatus; 
	}

	public String getVisionSbuDesc() {
		return visionSbuDesc;
	}

	public void setVisionSbuDesc(String visionSbuDesc) {
		this.visionSbuDesc = visionSbuDesc;
	}

	public List<RgFatcaRuleDetailsVb> getRuleDetailsList() {
		return ruleDetailsList;
	}

	public void setRuleDetailsList(List<RgFatcaRuleDetailsVb> ruleDetailsList) {
		this.ruleDetailsList = ruleDetailsList;
	}

	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public int getRuleCount() {
		return ruleCount;
	}

	public void setRuleCount(int ruleCount) {
		this.ruleCount = ruleCount;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}