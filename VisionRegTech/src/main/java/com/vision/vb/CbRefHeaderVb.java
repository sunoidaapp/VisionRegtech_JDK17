package com.vision.vb;

import java.util.ArrayList;
import java.util.List;

public class CbRefHeaderVb extends CommonVb{

	// private String legalEntity = "";
	private String refCode = "";
	private String refDescription = "";
	private int refStatusNt = 1 ;
	private int refStatus = 0 ;
	
	private List<CbRefDetailsVb> cbRefDetailsList = new ArrayList<CbRefDetailsVb>();
	
	List<SmartSearchVb> smartSearchOpt = null;
	
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public void setRefCode(String refCode) {
		this.refCode = refCode; 
	}

	public String getRefCode() {
		return refCode; 
	}
	public void setRefDescription(String refDescription) {
		this.refDescription = refDescription; 
	}

	public String getRefDescription() {
		return refDescription; 
	}
	public void setRefStatusNt(int refStatusNt) {
		this.refStatusNt = refStatusNt; 
	}

	public int getRefStatusNt() {
		return refStatusNt; 
	}
	public void setRefStatus(int refStatus) {
		this.refStatus = refStatus; 
	}

	public int getRefStatus() {
		return refStatus; 
	}

//	public String getLegalEntity() {
//		return legalEntity;
//	}
//
//	public void setLegalEntity(String legalEntity) {
//		this.legalEntity = legalEntity;
//	}

	public List<CbRefDetailsVb> getCbRefDetailsList() {
		return cbRefDetailsList;
	}

	public void setCbRefDetailsList(List<CbRefDetailsVb> cbRefDetailsList) {
		this.cbRefDetailsList = cbRefDetailsList;
	}

}