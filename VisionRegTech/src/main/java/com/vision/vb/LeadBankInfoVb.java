package com.vision.vb;

import java.io.Serializable;

public class LeadBankInfoVb implements Serializable {
	private String leadNo="";
	private String accBankName="";
	private String loc="";
	private String intRate="";
	private String concessions="";
	private String productName="";
	public String getLeadNo() {
		return leadNo;
	}
	public void setLeadNo(String leadNo) {
		this.leadNo = leadNo;
	}
	public String getAccBankName() {
		return accBankName;
	}
	public void setAccBankName(String accBankName) {
		this.accBankName = accBankName;
	}
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public String getIntRate() {
		return intRate;
	}
	public void setIntRate(String intRate) {
		this.intRate = intRate;
	}
	public String getConcessions() {
		return concessions;
	}
	public void setConcessions(String concessions) {
		this.concessions = concessions;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
}
