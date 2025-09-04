package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class RgCustomerMatchVb extends CommonVb {

	private String ruleId = "";
	private String setNumber = "";
	private String customerId = "";
//	private String propParentCid = "";
//	private String curParentCid = "";
//	private String ultimateParent = "";
//	private String matchCustomerId = "";
//	private String matchCustomerName = "";
//	private String primaryCid = "";
//	private String propParentCustName = "";
	private int custRelationStatusNt = 207;
	private int custRelationStatus = 0;
	private String custRelationStatusDesc = "";
	private String customerName = "";
	private String forceMatch = "";
	private String ruleDesc = "";
	private String relationType = "";
	private int relationTypeAt = 810;
	private String relationTypeDesc = "";
	private String relationCustomerId = "";
	private String relationCustomerName = "";
	List<RgCustomerMatchVb> customerMatchDetailsLst = new ArrayList<>();

	private int freezeColumn = 0;
	private String applicationTheme = "";
	private String reportTitle = "";
	private String screenGroupColumn = "";
	private String screenSortColumn = "";
	private String columnsToHide = "";
	List<ColumnHeadersVb> columnHeaderslst = null;

	private String promptValue1 = "";
	private String promptValue2 = "";
	private String promptValue3 = "";
	private String promptValue4 = "";

	private String promptValue5 = "";
	private String promptValue6 = "";
	private String promptValue7 = "";
	private String promptValue8 = "";
	private String promptValue9 = "";
	private String promptValue10 = "";
	List gridDataSet = null;

	public List getGridDataSet() {
		return gridDataSet;
	}

	public void setGridDataSet(List gridDataSet) {
		this.gridDataSet = gridDataSet;
	}

	public List getTotal() {
		return Total;
	}

	public void setTotal(List total) {
		Total = total;
	}

	List Total = null;

	public int getFreezeColumn() {
		return freezeColumn;
	}

	public void setFreezeColumn(int freezeColumn) {
		this.freezeColumn = freezeColumn;
	}

	public String getApplicationTheme() {
		return applicationTheme;
	}

	public void setApplicationTheme(String applicationTheme) {
		this.applicationTheme = applicationTheme;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getScreenGroupColumn() {
		return screenGroupColumn;
	}

	public void setScreenGroupColumn(String screenGroupColumn) {
		this.screenGroupColumn = screenGroupColumn;
	}

	public String getScreenSortColumn() {
		return screenSortColumn;
	}

	public void setScreenSortColumn(String screenSortColumn) {
		this.screenSortColumn = screenSortColumn;
	}

	public String getColumnsToHide() {
		return columnsToHide;
	}

	public void setColumnsToHide(String columnsToHide) {
		this.columnsToHide = columnsToHide;
	}

	public List<ColumnHeadersVb> getColumnHeaderslst() {
		return columnHeaderslst;
	}

	public void setColumnHeaderslst(List<ColumnHeadersVb> columnHeaderslst) {
		this.columnHeaderslst = columnHeaderslst;
	}

	private String description = "";

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	List<SmartSearchVb> smartSearchOpt = null;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getSetNumber() {
		return setNumber;
	}

	public void setSetNumber(String setNumber) {
		this.setNumber = setNumber;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getPromptValue1() {
		return promptValue1;
	}

	public void setPromptValue1(String promptValue1) {
		this.promptValue1 = promptValue1;
	}

	public String getPromptValue2() {
		return promptValue2;
	}

	public void setPromptValue2(String promptValue2) {
		this.promptValue2 = promptValue2;
	}

	public String getPromptValue3() {
		return promptValue3;
	}

	public void setPromptValue3(String promptValue3) {
		this.promptValue3 = promptValue3;
	}

	public String getPromptValue4() {
		return promptValue4;
	}

	public void setPromptValue4(String promptValue4) {
		this.promptValue4 = promptValue4;
	}

	public String getPromptValue5() {
		return promptValue5;
	}

	public void setPromptValue5(String promptValue5) {
		this.promptValue5 = promptValue5;
	}

	public String getPromptValue6() {
		return promptValue6;
	}

	public void setPromptValue6(String promptValue6) {
		this.promptValue6 = promptValue6;
	}

	public String getPromptValue7() {
		return promptValue7;
	}

	public void setPromptValue7(String promptValue7) {
		this.promptValue7 = promptValue7;
	}

	public String getPromptValue8() {
		return promptValue8;
	}

	public void setPromptValue8(String promptValue8) {
		this.promptValue8 = promptValue8;
	}

	public String getPromptValue9() {
		return promptValue9;
	}

	public void setPromptValue9(String promptValue9) {
		this.promptValue9 = promptValue9;
	}

	public String getPromptValue10() {
		return promptValue10;
	}

	public void setPromptValue10(String promptValue10) {
		this.promptValue10 = promptValue10;
	}

	public Object getMainModel() {
		return null;
	}

	public String getForceMatch() {
		return forceMatch;
	}

	public void setForceMatch(String forceMatch) {
		this.forceMatch = forceMatch;
	}

	public String getRuleDesc() {
		return ruleDesc;
	}

	public void setRuleDesc(String ruleDesc) {
		this.ruleDesc = ruleDesc;
	}

	public List<RgCustomerMatchVb> getCustomerMatchDetailsLst() {
		return customerMatchDetailsLst;
	}

	public void setCustomerMatchDetailsLst(List<RgCustomerMatchVb> customerMatchDetailsLst) {
		this.customerMatchDetailsLst = customerMatchDetailsLst;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getRelationCustomerId() {
		return relationCustomerId;
	}

	public void setRelationCustomerId(String relationCustomerId) {
		this.relationCustomerId = relationCustomerId;
	}

	public String getRelationCustomerName() {
		return relationCustomerName;
	}

	public void setRelationCustomerName(String relationCustomerName) {
		this.relationCustomerName = relationCustomerName;
	}

	public int getCustRelationStatusNt() {
		return custRelationStatusNt;
	}

	public void setCustRelationStatusNt(int custRelationStatusNt) {
		this.custRelationStatusNt = custRelationStatusNt;
	}

	public int getCustRelationStatus() {
		return custRelationStatus;
	}

	public void setCustRelationStatus(int custRelationStatus) {
		this.custRelationStatus = custRelationStatus;
	}

	public String getCustRelationStatusDesc() {
		return custRelationStatusDesc;
	}

	public void setCustRelationStatusDesc(String custRelationStatusDesc) {
		this.custRelationStatusDesc = custRelationStatusDesc;
	}

	public int getRelationTypeAt() {
		return relationTypeAt;
	}

	public void setRelationTypeAt(int relationTypeAt) {
		this.relationTypeAt = relationTypeAt;
	}

	public String getRelationTypeDesc() {
		return relationTypeDesc;
	}

	public void setRelationTypeDesc(String relationTypeDesc) {
		this.relationTypeDesc = relationTypeDesc;
	}

}
