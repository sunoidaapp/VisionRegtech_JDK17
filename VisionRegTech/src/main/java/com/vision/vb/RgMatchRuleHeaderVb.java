package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class RgMatchRuleHeaderVb extends CommonVb{

	private int approachTypeAt = 0 ;
	private String approachType = "";
	private String approachTypeDesc = "";
	private String ruleId = "" ;
	private String ruleDescription = "";
	private int rulePriority = 0 ;
	private String forceMatch = "Y";
	private int threshold = 0 ;
	private int ruleStatusNt = 0 ;
	private int ruleStatus = 0 ;
	private String ruleStatusDesc = "";
	List<RgMatchRuleDetailVb> matchRuleDetailLst =  new ArrayList<>();
	List<SmartSearchVb> smartSearchOpt = null;
	
	public void setApproachTypeAt(int approachTypeAt) {
		this.approachTypeAt = approachTypeAt; 
	}

	public int getApproachTypeAt() {
		return approachTypeAt; 
	}
	public void setApproachType(String approachType) {
		this.approachType = approachType; 
	}

	public String getApproachType() {
		return approachType; 
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
	public void setRulePriority(int rulePriority) {
		this.rulePriority = rulePriority; 
	}

	public int getRulePriority() {
		return rulePriority; 
	}
	public void setForceMatch(String forceMatch) {
		this.forceMatch = forceMatch; 
	}

	public String getForceMatch() {
		return forceMatch; 
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold; 
	}

	public int getThreshold() {
		return threshold; 
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

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getApproachTypeDesc() {
		return approachTypeDesc;
	}

	public void setApproachTypeDesc(String approachTypeDesc) {
		this.approachTypeDesc = approachTypeDesc;
	}

	public String getRuleStatusDesc() {
		return ruleStatusDesc;
	}

	public void setRuleStatusDesc(String ruleStatusDesc) {
		this.ruleStatusDesc = ruleStatusDesc;
	}

	public List<RgMatchRuleDetailVb> getMatchRuleDetailLst() {
		return matchRuleDetailLst;
	}

	public void setMatchRuleDetailLst(List<RgMatchRuleDetailVb> matchRuleDetailLst) {
		this.matchRuleDetailLst = matchRuleDetailLst;
	}
}