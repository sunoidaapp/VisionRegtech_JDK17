package com.vision.vb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RgCrsRuleDetailsVb extends CommonVb {

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
	private String valuePattValues =  "";
	
	private String filterCondition =  "";

	private String columnValueBy = "";
	
	private String fatcaFlag = "";
	private String fatcaOverrideFlag = "";
	private String finalFlag = "";
	private int priority = 0 ;
	private String customerId = "";
	private String customerName = "";
	private String tableName="";
	private boolean count = false;
	
	List<SmartSearchVb> smartSearchOpt = null;
	
	private Map<String, Object> dynamicFields = new HashMap<>();
	///
		///// --- Additional customer/master columns returned by SELECT ---
		private String cbNationality = "";
		private String cbResidence = "";
		private String commAddress1 = "";
		private String commAddress2 = "";
		private String commAddress3 = "";
		private String standingOrder = "";
		private String phoneNumber = "";
		private String phoneNumber02 = "";
		private String phoneNumber03 = "";
		private String phoneNumber04 = "";
		private String phoneNumber05 = "";
		private String phoneNumber06 = "";
		private String phoneNumber07 = "";
		private String ssn = "";
		private String idIssuingJurisdiction = "";
		private String powerOfAttorney = "";
		private String countryOfIncorporation = "";
		private String gbCountry = "";
		private String customerTin = "";
		private String accountOfficer = "";
		private String customerStatus = "";
		private String visionOuc = "";
		private String customerOpenDate = ""; // formatted as per SELECT
		private String subSegment = "";
		private String complianceStatus = "";
		private String jointAccount = "";
		private String placeOfBirth = "";
		private int versionNo = 0; // from SELECT alias "VERSION_NO"


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

	public String getCrsFlag() {
		return fatcaFlag;
	}

	public void setCrsFlag(String fatcaFlag) {
		this.fatcaFlag = fatcaFlag;
	}

	public String getCrsOverrideFlag() {
		return fatcaOverrideFlag;
	}

	public void setCrsOverrideFlag(String fatcaOverrideFlag) {
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public boolean isCount() {
		return count;
	}
	public void setCount(boolean count) {
		this.count = count;
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
	public String getCbNationality() {
		return cbNationality;
	}
	public void setCbNationality(String cbNationality) {
		this.cbNationality = cbNationality;
	}
	public String getCbResidence() {
		return cbResidence;
	}
	public void setCbResidence(String cbResidence) {
		this.cbResidence = cbResidence;
	}
	public String getCommAddress1() {
		return commAddress1;
	}
	public void setCommAddress1(String commAddress1) {
		this.commAddress1 = commAddress1;
	}
	public String getCommAddress2() {
		return commAddress2;
	}
	public void setCommAddress2(String commAddress2) {
		this.commAddress2 = commAddress2;
	}
	public String getCommAddress3() {
		return commAddress3;
	}
	public void setCommAddress3(String commAddress3) {
		this.commAddress3 = commAddress3;
	}
	public String getStandingOrder() {
		return standingOrder;
	}
	public void setStandingOrder(String standingOrder) {
		this.standingOrder = standingOrder;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneNumber02() {
		return phoneNumber02;
	}
	public void setPhoneNumber02(String phoneNumber02) {
		this.phoneNumber02 = phoneNumber02;
	}
	public String getPhoneNumber03() {
		return phoneNumber03;
	}
	public void setPhoneNumber03(String phoneNumber03) {
		this.phoneNumber03 = phoneNumber03;
	}
	public String getPhoneNumber04() {
		return phoneNumber04;
	}
	public void setPhoneNumber04(String phoneNumber04) {
		this.phoneNumber04 = phoneNumber04;
	}
	public String getPhoneNumber05() {
		return phoneNumber05;
	}
	public void setPhoneNumber05(String phoneNumber05) {
		this.phoneNumber05 = phoneNumber05;
	}
	public String getPhoneNumber06() {
		return phoneNumber06;
	}
	public void setPhoneNumber06(String phoneNumber06) {
		this.phoneNumber06 = phoneNumber06;
	}
	public String getPhoneNumber07() {
		return phoneNumber07;
	}
	public void setPhoneNumber07(String phoneNumber07) {
		this.phoneNumber07 = phoneNumber07;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
	public String getIdIssuingJurisdiction() {
		return idIssuingJurisdiction;
	}
	public void setIdIssuingJurisdiction(String idIssuingJurisdiction) {
		this.idIssuingJurisdiction = idIssuingJurisdiction;
	}
	public String getPowerOfAttorney() {
		return powerOfAttorney;
	}
	public void setPowerOfAttorney(String powerOfAttorney) {
		this.powerOfAttorney = powerOfAttorney;
	}
	public String getCountryOfIncorporation() {
		return countryOfIncorporation;
	}
	public void setCountryOfIncorporation(String countryOfIncorporation) {
		this.countryOfIncorporation = countryOfIncorporation;
	}
	public String getGbCountry() {
		return gbCountry;
	}
	public void setGbCountry(String gbCountry) {
		this.gbCountry = gbCountry;
	}
	public String getCustomerTin() {
		return customerTin;
	}
	public void setCustomerTin(String customerTin) {
		this.customerTin = customerTin;
	}
	public String getAccountOfficer() {
		return accountOfficer;
	}
	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}
	public String getCustomerStatus() {
		return customerStatus;
	}
	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}
	public String getVisionOuc() {
		return visionOuc;
	}
	public void setVisionOuc(String visionOuc) {
		this.visionOuc = visionOuc;
	}
	public String getCustomerOpenDate() {
		return customerOpenDate;
	}
	public void setCustomerOpenDate(String customerOpenDate) {
		this.customerOpenDate = customerOpenDate;
	}
	public String getSubSegment() {
		return subSegment;
	}
	public void setSubSegment(String subSegment) {
		this.subSegment = subSegment;
	}
	public String getComplianceStatus() {
		return complianceStatus;
	}
	public void setComplianceStatus(String complianceStatus) {
		this.complianceStatus = complianceStatus;
	}
	public String getJointAccount() {
		return jointAccount;
	}
	public void setJointAccount(String jointAccount) {
		this.jointAccount = jointAccount;
	}
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public int getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}
	public void setDynamicFields(Map<String, Object> dynamicFields) {
		this.dynamicFields = dynamicFields;
	}

}