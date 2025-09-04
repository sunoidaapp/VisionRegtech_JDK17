package com.vision.vb;

import java.util.List;

public class MdmReportPromptsVb extends CommonVb{

	private static final long serialVersionUID = 1L;
	private String promptValue1 = "";
	private String promptValue2 = ""; 
	private String promptValue3 =  "";
	private String promptValue4 =  "";
	private String promptValue5 =  "";
	private String promptValue6 =  "";
	private String promptValue7 =  "";
	private String promptValue8 =  "";
	private String promptValue9 = "";
	private String promptValue10 = "";
	private String reportId = "";
	private String reportTitle = "";
	
	private Boolean subReportFlag = false;
	List<ColumnHeadersVb> columnHeaderslst = null;
	
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
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public Boolean getSubReportFlag() {
		return subReportFlag;
	}
	public void setSubReportFlag(Boolean subReportFlag) {
		this.subReportFlag = subReportFlag;
	}
	public List<ColumnHeadersVb> getColumnHeaderslst() {
		return columnHeaderslst;
	}
	public void setColumnHeaderslst(List<ColumnHeadersVb> columnHeaderslst) {
		this.columnHeaderslst = columnHeaderslst;
	}
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
}
