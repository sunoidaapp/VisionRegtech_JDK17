package com.vision.vb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RgFatcaRuleDetailsVb extends CommonVb {

	private int visionSbuAt = 3;
	private String visionSbu = "";
	private String visionSbuDesc = "";
	private String ruleId = "";
	private String columnName = "";
	private int ruleTypeAt = 857;
	private String ruleType = "";
	private String ruleTypeDesc = "";
	private int ruleSequence = 0;
	private String ruleAlphaFlag = "N";
	private int ruleAlphaAt = 854;
	private String ruleAlpha = "NA";
	private String ruleAlphaDesc = "";
	private String ruleNumFlag = "N";
	private int ruleNumAt = 854;
	private String ruleNum = "NA";
	private String ruleNumDesc = "";
	private int ruleLenMin = 0;
	private int ruleLenMax = 0;
	private int ruleStatusNt = 1;
	private int ruleStatus = 0;
	private String ruleDescription = "";
	private String ruleAlphaChar = "";
	private String lookupTableName = "";
	private String lookupCondition = "";
	private String valuePattFlag = "";
	private String ruleNumChar = "";
	private String lookupColumn = "";
	private String valuePattValues = "";

	private String filterCondition = "";

	private String columnValueBy = "";

	private String fatcaFlag = "";
	private String fatcaOverrideFlag = "";
	private String finalFlag = "";
	private int priority = 0;
	private String customerId = "";
	private String customerName = "";
	private String tableName = "";
	private boolean count = false;
	List<SmartSearchVb> smartSearchOpt = null;

	private Map<String, Object> dynamicFields = new HashMap<>();

	public Map<String, Object> getDynamicFields() {
		return dynamicFields;
	}

	public void setDynamicField(String key, Object value) {
		this.dynamicFields.put(key, value);
	}

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

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setRuleTypeAt(int ruleTypeAt) {
		this.ruleTypeAt = ruleTypeAt;
	}

	public int getRuleTypeAt() {
		return ruleTypeAt;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleDescription(String ruleDescription) {
		this.ruleDescription = ruleDescription;
	}

	public String getRuleDescription() {
		return ruleDescription;
	}

	public void setRuleSequence(int ruleSequence) {
		this.ruleSequence = ruleSequence;
	}

	public int getRuleSequence() {
		return ruleSequence;
	}

	public void setLookupTableName(String lookupTableName) {
		this.lookupTableName = lookupTableName;
	}

	public String getLookupTableName() {
		return lookupTableName;
	}

	public void setLookupColumn(String lookupColumn) {
		this.lookupColumn = lookupColumn;
	}

	public String getLookupColumn() {
		return lookupColumn;
	}

	public void setLookupCondition(String lookupCondition) {
		this.lookupCondition = lookupCondition;
	}

	public String getLookupCondition() {
		return lookupCondition;
	}

	public void setValuePattFlag(String valuePattFlag) {
		this.valuePattFlag = valuePattFlag;
	}

	public String getValuePattFlag() {
		return valuePattFlag;
	}

	public void setValuePattValues(String valuePattValues) {
		this.valuePattValues = valuePattValues;
	}

	public String getValuePattValues() {
		return valuePattValues;
	}

	public void setRuleAlphaFlag(String ruleAlphaFlag) {
		this.ruleAlphaFlag = ruleAlphaFlag;
	}

	public String getRuleAlphaFlag() {
		return ruleAlphaFlag;
	}

	public void setRuleAlphaAt(int ruleAlphaAt) {
		this.ruleAlphaAt = ruleAlphaAt;
	}

	public int getRuleAlphaAt() {
		return ruleAlphaAt;
	}

	public void setRuleAlpha(String ruleAlpha) {
		this.ruleAlpha = ruleAlpha;
	}

	public String getRuleAlpha() {
		return ruleAlpha;
	}

	public void setRuleAlphaChar(String ruleAlphaChar) {
		this.ruleAlphaChar = ruleAlphaChar;
	}

	public String getRuleAlphaChar() {
		return ruleAlphaChar;
	}

	public void setRuleNumFlag(String ruleNumFlag) {
		this.ruleNumFlag = ruleNumFlag;
	}

	public String getRuleNumFlag() {
		return ruleNumFlag;
	}

	public void setRuleNumAt(int ruleNumAt) {
		this.ruleNumAt = ruleNumAt;
	}

	public int getRuleNumAt() {
		return ruleNumAt;
	}

	public void setRuleNum(String ruleNum) {
		this.ruleNum = ruleNum;
	}

	public String getRuleNum() {
		return ruleNum;
	}

	public void setRuleNumChar(String ruleNumChar) {
		this.ruleNumChar = ruleNumChar;
	}

	public String getRuleNumChar() {
		return ruleNumChar;
	}

	public void setRuleLenMin(int ruleLenMin) {
		this.ruleLenMin = ruleLenMin;
	}

	public int getRuleLenMin() {
		return ruleLenMin;
	}

	public void setRuleLenMax(int ruleLenMax) {
		this.ruleLenMax = ruleLenMax;
	}

	public int getRuleLenMax() {
		return ruleLenMax;
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

	public String getRuleTypeDesc() {
		return ruleTypeDesc;
	}

	public void setRuleTypeDesc(String ruleTypeDesc) {
		this.ruleTypeDesc = ruleTypeDesc;
	}

	public String getRuleAlphaDesc() {
		return ruleAlphaDesc;
	}

	public void setRuleAlphaDesc(String ruleAlphaDesc) {
		this.ruleAlphaDesc = ruleAlphaDesc;
	}

	public String getRuleNumDesc() {
		return ruleNumDesc;
	}

	public void setRuleNumDesc(String ruleNumDesc) {
		this.ruleNumDesc = ruleNumDesc;
	}

	public String getColumnValueBy() {
		return columnValueBy;
	}

	public void setColumnValueBy(String columnValueBy) {
		this.columnValueBy = columnValueBy;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getFatcaFlag() {
		return fatcaFlag;
	}

	public void setFatcaFlag(String fatcaFlag) {
		this.fatcaFlag = fatcaFlag;
	}

	public String getFatcaOverrideFlag() {
		return fatcaOverrideFlag;
	}

	public void setFatcaOverrideFlag(String fatcaOverrideFlag) {
		this.fatcaOverrideFlag = fatcaOverrideFlag;
	}

	public String getFinalFlag() {
		return finalFlag;
	}

	public void setFinalFlag(String finalFlag) {
		this.finalFlag = finalFlag;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public boolean isCount() {
		return count;
	}

	public void setCount(boolean count) {
		this.count = count;
	}

}