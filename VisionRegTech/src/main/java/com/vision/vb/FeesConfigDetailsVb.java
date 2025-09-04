package com.vision.vb;

import java.util.List;

public class FeesConfigDetailsVb extends CommonVb{
	private String transLineId = "";
	private String businessLineId = "";
	private int feeConfigTypeAt = 7029;
	private String feeConfigType="";
	private String FeeConfigCode = "";
	private String contractId = "";
	private String effectiveDate = "";
	private int productIdAt = 171;
	private String productId = "";
	private String tranCcy = "";
	private int feeCcyAt = 7030;
	private String feeCcy = "";
	private int interestBasisAt = 7031;
	private String interestBasis = "";
	private int feeBasisAt = 7032;
	private String feeBasis = "";
	private int feeTypeAt = 7033;
	private String feeType = "";
	private int tierTypeAt = 7034;
	private String tierType = "";
	private String feeAmt = "";
	private String feePercentage = "";
	private String minFee= "";
	private String maxFee= "";
	private String channel="";
	
	List<FeesConfigTierVb> feesTierlst = null;
	
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
	public int getProductIdAt() {
		return productIdAt;
	}
	public void setProductIdAt(int productIdAt) {
		this.productIdAt = productIdAt;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getTranCcy() {
		return tranCcy;
	}
	public void setTranCcy(String tranCcy) {
		this.tranCcy = tranCcy;
	}
	public int getFeeCcyAt() {
		return feeCcyAt;
	}
	public void setFeeCcyAt(int feeCcyAt) {
		this.feeCcyAt = feeCcyAt;
	}
	public String getFeeCcy() {
		return feeCcy;
	}
	public void setFeeCcy(String feeCcy) {
		this.feeCcy = feeCcy;
	}
	public int getInterestBasisAt() {
		return interestBasisAt;
	}
	public void setInterestBasisAt(int interestBasisAt) {
		this.interestBasisAt = interestBasisAt;
	}
	public String getInterestBasis() {
		return interestBasis;
	}
	public void setInterestBasis(String interestBasis) {
		this.interestBasis = interestBasis;
	}
	public int getFeeBasisAt() {
		return feeBasisAt;
	}
	public void setFeeBasisAt(int feeBasisAt) {
		this.feeBasisAt = feeBasisAt;
	}
	public String getFeeBasis() {
		return feeBasis;
	}
	public void setFeeBasis(String feeBasis) {
		this.feeBasis = feeBasis;
	}
	public int getFeeTypeAt() {
		return feeTypeAt;
	}
	public void setFeeTypeAt(int feeTypeAt) {
		this.feeTypeAt = feeTypeAt;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public int getTierTypeAt() {
		return tierTypeAt;
	}
	public void setTierTypeAt(int tierTypeAt) {
		this.tierTypeAt = tierTypeAt;
	}
	public String getTierType() {
		return tierType;
	}
	public void setTierType(String tierType) {
		this.tierType = tierType;
	}
	public String getFeeAmt() {
		return feeAmt;
	}
	public void setFeeAmt(String feeAmt) {
		this.feeAmt = feeAmt;
	}
	public String getFeePercentage() {
		return feePercentage;
	}
	public void setFeePercentage(String feePercentage) {
		this.feePercentage = feePercentage;
	}
	public String getMinFee() {
		return minFee;
	}
	public void setMinFee(String minFee) {
		this.minFee = minFee;
	}
	public String getMaxFee() {
		return maxFee;
	}
	public void setMaxFee(String maxFee) {
		this.maxFee = maxFee;
	}
	public List<FeesConfigTierVb> getFeesTierlst() {
		return feesTierlst;
	}
	public void setFeesTierlst(List<FeesConfigTierVb> feesTierlst) {
		this.feesTierlst = feesTierlst;
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
}
