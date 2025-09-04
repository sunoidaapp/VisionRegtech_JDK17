package com.vision.vb;

import java.util.List;

public class FeesConfigHeaderVb extends CommonVb{
	private String transLineId = "";
	private String transLineIdDesc= "";
	private String businessLineId = "";
	private String businessLineIdDesc = "";
	private int feeConfigTypeAt = 7029;
	private String feeConfigType="";
	private String feeConfigTypeDesc="";
	private String FeeConfigCode = "";
	private String FeeConfigCodeDesc = "";
	private String contractId = "";
	private String contractIdDesc = "";
	private String effectiveDate = "";
	private int businessLineStatusNt = 1;
	private int businessLineStatus = 0 ;
	private String businessLineStatusDesc = "";
	private String tranLineGrp = "";
	private String tranLineGrpDesc = "";
	private String tranLineType = "";
	
	List<SmartSearchVb> smartSearchOpt = null;
	List<FeesConfigDetailsVb> feesConfigDetaillst = null;

	public String getTransLineId() {
		return transLineId;
	}

	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}

	public String getBusinessLineId() {
		return businessLineId;
	}

	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}

	public int getFeeConfigTypeAt() {
		return feeConfigTypeAt;
	}

	public void setFeeConfigTypeAt(int feeConfigTypeAt) {
		this.feeConfigTypeAt = feeConfigTypeAt;
	}

	public String getFeeConfigType() {
		return feeConfigType;
	}

	public void setFeeConfigType(String feeConfigType) {
		this.feeConfigType = feeConfigType;
	}

	public String getFeeConfigCode() {
		return FeeConfigCode;
	}

	public void setFeeConfigCode(String feeConfigCode) {
		FeeConfigCode = feeConfigCode;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public int getBusinessLineStatusNt() {
		return businessLineStatusNt;
	}

	public void setBusinessLineStatusNt(int businessLineStatusNt) {
		this.businessLineStatusNt = businessLineStatusNt;
	}

	public int getBusinessLineStatus() {
		return businessLineStatus;
	}

	public void setBusinessLineStatus(int businessLineStatus) {
		this.businessLineStatus = businessLineStatus;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getTransLineIdDesc() {
		return transLineIdDesc;
	}

	public void setTransLineIdDesc(String transLineIdDesc) {
		this.transLineIdDesc = transLineIdDesc;
	}

	public String getBusinessLineIdDesc() {
		return businessLineIdDesc;
	}

	public void setBusinessLineIdDesc(String businessLineIdDesc) {
		this.businessLineIdDesc = businessLineIdDesc;
	}

	public String getFeeConfigTypeDesc() {
		return feeConfigTypeDesc;
	}

	public void setFeeConfigTypeDesc(String feeConfigTypeDesc) {
		this.feeConfigTypeDesc = feeConfigTypeDesc;
	}

	public String getFeeConfigCodeDesc() {
		return FeeConfigCodeDesc;
	}

	public void setFeeConfigCodeDesc(String feeConfigCodeDesc) {
		FeeConfigCodeDesc = feeConfigCodeDesc;
	}

	public String getContractIdDesc() {
		return contractIdDesc;
	}

	public void setContractIdDesc(String contractIdDesc) {
		this.contractIdDesc = contractIdDesc;
	}

	public String getBusinessLineStatusDesc() {
		return businessLineStatusDesc;
	}

	public void setBusinessLineStatusDesc(String businessLineStatusDesc) {
		this.businessLineStatusDesc = businessLineStatusDesc;
	}

	public List<FeesConfigDetailsVb> getFeesConfigDetaillst() {
		return feesConfigDetaillst;
	}

	public void setFeesConfigDetaillst(List<FeesConfigDetailsVb> feesConfigDetaillst) {
		this.feesConfigDetaillst = feesConfigDetaillst;
	}

	public String getTranLineGrp() {
		return tranLineGrp;
	}

	public void setTranLineGrp(String tranLineGrp) {
		this.tranLineGrp = tranLineGrp;
	}

	public String getTranLineGrpDesc() {
		return tranLineGrpDesc;
	}

	public void setTranLineGrpDesc(String tranLineGrpDesc) {
		this.tranLineGrpDesc = tranLineGrpDesc;
	}

	public String getTranLineType() {
		return tranLineType;
	}

	public void setTranLineType(String tranLineType) {
		this.tranLineType = tranLineType;
	}
}
