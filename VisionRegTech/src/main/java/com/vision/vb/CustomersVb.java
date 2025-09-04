/**
 * 
 */
package com.vision.vb;

public class CustomersVb extends CommonVb {
	private String customerOpenDate = "";
	private String customerId = "";
	private String customerName = "";
	private String customerAcronym = "";
	private String visionOuc = "";
	private String visionOucDesc = "";

	private int visionSbuAt = 0;
	private String visionSbu = "";
	private String visionSbuDesc = "";
	private String globalCustomerId = "";
	private String naicsCode = "99999";
	private String naicsCodeDesc = "";
	private int cbOrgCodeAt = 0;
	private String cbOrgCode = "";
	private int cbEconomicActCodeAt = 0;
	private String cbEconomicActCode = "";
	private String cbDomicile = "";
	private String cbNationality = "";
	private String cbResidence = "";

	private String cbDomicileDesc = "";
	private String cbNationalityDesc = "";
	private String cbResidenceDesc = "";

	private String cbMajorPartyIndicator = "";
	private int customerIdTypeAt = 0;
	private String customerIdType = "";
	private String idDetails = "";
	private String primaryCidDesc = "";
	private String primaryCid = "";
	private String parentCidDesc = "";
	private String parentCid = "";
	private String ultimateParent = "";
	private String ultimateParentDesc = "";
	private String customerHierarchy1 = "";
	private String customerHierarchy1Desc = "";
	private String customerHierarchy2 = "";
	private String customerHierarchy2Desc = "";
	private String customerHierarchy3 = "";
	private String customerHierarchy3Desc = "";
	private String accountOfficer = "";
	private String accountOfficerDesc = "";
	private int creditClassificationAt = 0;
	private String creditClassification = "";
	private int externalRiskRatingAt = 0;
	private String externalRiskRating = "";
	private int obligorRiskRatingAt = 0;
	private String obligorRiskRating = "";
	private int relatedPartyAt = 0;
	private String relatedParty = "";
	private int customerTieringAt = 0;
	private String customerTiering = "";
	private int salesAcquisitionChannelAt = 0;
	private String salesAcquisitionChannel = "";
	private String marketingCampaign = "";
	private String crosssaleReferId = "";
	private String customerIndicator = "";
	private int customerIndicatorAt = 0;
	private String numOfAccounts = "";
	private String customerAttribute1 = "";
	private String customerAttribute2 = "";
	private String customerAttribute3 = "";

	private String customerAttribute1Desc = "";
	private String customerAttribute2Desc = "";
	private String customerAttribute3Desc = "";

	private int customerStatusNt = 0;
	private int customerStatus = -1;
	private int customerSexAT = 0;
	private String customerSex = "";
	private String commAddress1 = "";
	private String commAddress2 = "";
	private String commCityAt = "";
	private String commCity = "";
	private String commStateAt = "";
	private String phoneNumber = "";
	private String commState = "";
	private String commPinCode = "";
	private String customerMask = "-1";
	private String crsFlag = "";
	private String fatcaFlag = "";
	private String crsOverRide = "";
	private String fatcaOverRide = "";
	private String crsOverRideDesc = "";
	private String fatcaOverRideDesc = "";

	private String dualNationality1 = "";
	private String dualNationality2 = "";
	private String dualNationality3 = "";
	private String dualNationality1Desc = "";
	private String dualNationality2Desc = "";
	private String dualNationality3Desc = "";
	public String getDualNationality1Desc() {
		return dualNationality1Desc;
	}

	public void setDualNationality1Desc(String dualNationality1Desc) {
		this.dualNationality1Desc = dualNationality1Desc;
	}

	public String getDualNationality2Desc() {
		return dualNationality2Desc;
	}

	public void setDualNationality2Desc(String dualNationality2Desc) {
		this.dualNationality2Desc = dualNationality2Desc;
	}

	public String getDualNationality3Desc() {
		return dualNationality3Desc;
	}

	public void setDualNationality3Desc(String dualNationality3Desc) {
		this.dualNationality3Desc = dualNationality3Desc;
	}

	private int cusExtrasStatus = -1;
	private int cusExtrasStatusNt = -1;

	public String getCrsOverRideDesc() {
		return crsOverRideDesc;
	}

	public void setCrsOverRideDesc(String crsOverRideDesc) {
		this.crsOverRideDesc = crsOverRideDesc;
	}

	public String getFatcaOverRideDesc() {
		return fatcaOverRideDesc;
	}

	public void setFatcaOverRideDesc(String fatcaOverRideDesc) {
		this.fatcaOverRideDesc = fatcaOverRideDesc;
	}

	public String getCrsOverRide() {
		return crsOverRide;
	}

	public void setCrsOverRide(String crsOverRide) {
		this.crsOverRide = crsOverRide;
	}

	public String getFatcaOverRide() {
		return fatcaOverRide;
	}

	public void setFatcaOverRide(String fatcaOverRide) {
		this.fatcaOverRide = fatcaOverRide;
	}

	public String getCustomerOpenDate() {
		return customerOpenDate;
	}

	public void setCustomerOpenDate(String customerOpenDate) {
		this.customerOpenDate = customerOpenDate;
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

	public String getCustomerAcronym() {
		return customerAcronym;
	}

	public void setCustomerAcronym(String customerAcronym) {
		this.customerAcronym = customerAcronym;
	}

	public String getVisionOuc() {
		return visionOuc;
	}

	public void setVisionOuc(String visionOuc) {
		this.visionOuc = visionOuc;
	}

	public int getVisionSbuAt() {
		return visionSbuAt;
	}

	public void setVisionSbuAt(int visionSbuAt) {
		this.visionSbuAt = visionSbuAt;
	}

	public String getVisionSbu() {
		return visionSbu;
	}

	public void setVisionSbu(String visionSbu) {
		this.visionSbu = visionSbu;
	}

	public String getGlobalCustomerId() {
		return globalCustomerId;
	}

	public void setGlobalCustomerId(String globalCustomerId) {
		this.globalCustomerId = globalCustomerId;
	}

	public String getNaicsCode() {
		return naicsCode;
	}

	public void setNaicsCode(String naicsCode) {
		this.naicsCode = naicsCode;
	}

	public int getCbOrgCodeAt() {
		return cbOrgCodeAt;
	}

	public void setCbOrgCodeAt(int cbOrgCodeAt) {
		this.cbOrgCodeAt = cbOrgCodeAt;
	}

	public String getCbOrgCode() {
		return cbOrgCode;
	}

	public void setCbOrgCode(String cbOrgCode) {
		this.cbOrgCode = cbOrgCode;
	}

	public int getCbEconomicActCodeAt() {
		return cbEconomicActCodeAt;
	}

	public void setCbEconomicActCodeAt(int cbEconomicActCodeAt) {
		this.cbEconomicActCodeAt = cbEconomicActCodeAt;
	}

	public String getCbEconomicActCode() {
		return cbEconomicActCode;
	}

	public void setCbEconomicActCode(String cbEconomicActCode) {
		this.cbEconomicActCode = cbEconomicActCode;
	}

	public String getCbDomicile() {
		return cbDomicile;
	}

	public void setCbDomicile(String cbDomicile) {
		this.cbDomicile = cbDomicile;
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

	public String getCbMajorPartyIndicator() {
		return cbMajorPartyIndicator;
	}

	public void setCbMajorPartyIndicator(String cbMajorPartyIndicator) {
		this.cbMajorPartyIndicator = cbMajorPartyIndicator;
	}

	public int getCustomerIdTypeAt() {
		return customerIdTypeAt;
	}

	public void setCustomerIdTypeAt(int customerIdTypeAt) {
		this.customerIdTypeAt = customerIdTypeAt;
	}

	public String getCustomerIdType() {
		return customerIdType;
	}

	public void setCustomerIdType(String customerIdType) {
		this.customerIdType = customerIdType;
	}

	public String getIdDetails() {
		return idDetails;
	}

	public void setIdDetails(String idDetails) {
		this.idDetails = idDetails;
	}

	public String getPrimaryCid() {
		return primaryCid;
	}

	public void setPrimaryCid(String primaryCid) {
		this.primaryCid = primaryCid;
	}

	public String getParentCid() {
		return parentCid;
	}

	public void setParentCid(String parentCid) {
		this.parentCid = parentCid;
	}

	public String getUltimateParent() {
		return ultimateParent;
	}

	public void setUltimateParent(String ultimateParent) {
		this.ultimateParent = ultimateParent;
	}

	public String getCustomerHierarchy1() {
		return customerHierarchy1;
	}

	public void setCustomerHierarchy1(String customerHierarchy1) {
		this.customerHierarchy1 = customerHierarchy1;
	}

	public String getCustomerHierarchy2() {
		return customerHierarchy2;
	}

	public void setCustomerHierarchy2(String customerHierarchy2) {
		this.customerHierarchy2 = customerHierarchy2;
	}

	public String getCustomerHierarchy3() {
		return customerHierarchy3;
	}

	public void setCustomerHierarchy3(String customerHierarchy3) {
		this.customerHierarchy3 = customerHierarchy3;
	}

	public String getAccountOfficer() {
		return accountOfficer;
	}

	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}

	public int getCreditClassificationAt() {
		return creditClassificationAt;
	}

	public void setCreditClassificationAt(int creditClassificationAt) {
		this.creditClassificationAt = creditClassificationAt;
	}

	public String getCreditClassification() {
		return creditClassification;
	}

	public void setCreditClassification(String creditClassification) {
		this.creditClassification = creditClassification;
	}

	public int getExternalRiskRatingAt() {
		return externalRiskRatingAt;
	}

	public void setExternalRiskRatingAt(int externalRiskRatingAt) {
		this.externalRiskRatingAt = externalRiskRatingAt;
	}

	public String getExternalRiskRating() {
		return externalRiskRating;
	}

	public void setExternalRiskRating(String externalRiskRating) {
		this.externalRiskRating = externalRiskRating;
	}

	public int getObligorRiskRatingAt() {
		return obligorRiskRatingAt;
	}

	public void setObligorRiskRatingAt(int obligorRiskRatingAt) {
		this.obligorRiskRatingAt = obligorRiskRatingAt;
	}

	public String getObligorRiskRating() {
		return obligorRiskRating;
	}

	public void setObligorRiskRating(String obligorRiskRating) {
		this.obligorRiskRating = obligorRiskRating;
	}

	public int getRelatedPartyAt() {
		return relatedPartyAt;
	}

	public void setRelatedPartyAt(int relatedPartyAt) {
		this.relatedPartyAt = relatedPartyAt;
	}

	public String getRelatedParty() {
		return relatedParty;
	}

	public void setRelatedParty(String relatedParty) {
		this.relatedParty = relatedParty;
	}

	public int getCustomerTieringAt() {
		return customerTieringAt;
	}

	public void setCustomerTieringAt(int customerTieringAt) {
		this.customerTieringAt = customerTieringAt;
	}

	public String getCustomerTiering() {
		return customerTiering;
	}

	public void setCustomerTiering(String customerTiering) {
		this.customerTiering = customerTiering;
	}

	public int getSalesAcquisitionChannelAt() {
		return salesAcquisitionChannelAt;
	}

	public void setSalesAcquisitionChannelAt(int salesAcquisitionChannelAt) {
		this.salesAcquisitionChannelAt = salesAcquisitionChannelAt;
	}

	public String getSalesAcquisitionChannel() {
		return salesAcquisitionChannel;
	}

	public void setSalesAcquisitionChannel(String salesAcquisitionChannel) {
		this.salesAcquisitionChannel = salesAcquisitionChannel;
	}

	public String getMarketingCampaign() {
		return marketingCampaign;
	}

	public void setMarketingCampaign(String marketingCampaign) {
		this.marketingCampaign = marketingCampaign;
	}

	public String getCrosssaleReferId() {
		return crosssaleReferId;
	}

	public void setCrosssaleReferId(String crosssaleReferId) {
		this.crosssaleReferId = crosssaleReferId;
	}

	public String getCustomerIndicator() {
		return customerIndicator;
	}

	public void setCustomerIndicator(String customerIndicator) {
		this.customerIndicator = customerIndicator;
	}

	public String getNumOfAccounts() {
		return numOfAccounts;
	}

	public void setNumOfAccounts(String numOfAccounts) {
		this.numOfAccounts = numOfAccounts;
	}

	public String getCustomerAttribute1() {
		return customerAttribute1;
	}

	public void setCustomerAttribute1(String customerAttribute1) {
		this.customerAttribute1 = customerAttribute1;
	}

	public String getCustomerAttribute2() {
		return customerAttribute2;
	}

	public void setCustomerAttribute2(String customerAttribute2) {
		this.customerAttribute2 = customerAttribute2;
	}

	public String getCustomerAttribute3() {
		return customerAttribute3;
	}

	public void setCustomerAttribute3(String customerAttribute3) {
		this.customerAttribute3 = customerAttribute3;
	}

	public int getCustomerStatusNt() {
		return customerStatusNt;
	}

	public void setCustomerStatusNt(int customerStatusNt) {
		this.customerStatusNt = customerStatusNt;
	}

	public int getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(int customerStatus) {
		this.customerStatus = customerStatus;
	}

	public int getCustomerSexAT() {
		return customerSexAT;
	}

	public void setCustomerSexAT(int customerSexAT) {
		this.customerSexAT = customerSexAT;
	}

	public String getCustomerSex() {
		return customerSex;
	}

	public void setCustomerSex(String customerSex) {
		this.customerSex = customerSex;
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

	public String getCommCityAt() {
		return commCityAt;
	}

	public void setCommCityAt(String commCityAt) {
		this.commCityAt = commCityAt;
	}

	public String getCommCity() {
		return commCity;
	}

	public void setCommCity(String commCity) {
		this.commCity = commCity;
	}

	public String getCommStateAt() {
		return commStateAt;
	}

	public void setCommStateAt(String commStateAt) {
		this.commStateAt = commStateAt;
	}

	public String getCommState() {
		return commState;
	}

	public void setCommState(String commState) {
		this.commState = commState;
	}

	public String getCommPinCode() {
		return commPinCode;
	}

	public void setCommPinCode(String commPinCode) {
		this.commPinCode = commPinCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getCustomerIndicatorAt() {
		return customerIndicatorAt;
	}

	public void setCustomerIndicatorAt(int customerIndicatorAt) {
		this.customerIndicatorAt = customerIndicatorAt;
	}

	public String getCustomerMask() {
		return customerMask;
	}

	public void setCustomerMask(String customerMask) {
		this.customerMask = customerMask;
	}

	public String getCrsFlag() {
		return crsFlag;
	}

	public void setCrsFlag(String crsFlag) {
		this.crsFlag = crsFlag;
	}

	public String getFatcaFlag() {
		return fatcaFlag;
	}

	public void setFatcaFlag(String fatcaFlag) {
		this.fatcaFlag = fatcaFlag;
	}

	public String getNaicsCodeDesc() {
		return naicsCodeDesc;
	}

	public void setNaicsCodeDesc(String naicsCodeDesc) {
		this.naicsCodeDesc = naicsCodeDesc;
	}

	public String getCbDomicileDesc() {
		return cbDomicileDesc;
	}

	public void setCbDomicileDesc(String cbDomicileDesc) {
		this.cbDomicileDesc = cbDomicileDesc;
	}

	public String getCbNationalityDesc() {
		return cbNationalityDesc;
	}

	public void setCbNationalityDesc(String cbNationalityDesc) {
		this.cbNationalityDesc = cbNationalityDesc;
	}

	public String getCbResidenceDesc() {
		return cbResidenceDesc;
	}

	public void setCbResidenceDesc(String cbResidenceDesc) {
		this.cbResidenceDesc = cbResidenceDesc;
	}

	public String getPrimaryCidDesc() {
		return primaryCidDesc;
	}

	public void setPrimaryCidDesc(String primaryCidDesc) {
		this.primaryCidDesc = primaryCidDesc;
	}

	public String getParentCidDesc() {
		return parentCidDesc;
	}

	public void setParentCidDesc(String parentCidDesc) {
		this.parentCidDesc = parentCidDesc;
	}

	public String getUltimateParentDesc() {
		return ultimateParentDesc;
	}

	public void setUltimateParentDesc(String ultimateParentDesc) {
		this.ultimateParentDesc = ultimateParentDesc;
	}

	public String getCustomerHierarchy1Desc() {
		return customerHierarchy1Desc;
	}

	public void setCustomerHierarchy1Desc(String customerHierarchy1Desc) {
		this.customerHierarchy1Desc = customerHierarchy1Desc;
	}

	public String getCustomerHierarchy2Desc() {
		return customerHierarchy2Desc;
	}

	public void setCustomerHierarchy2Desc(String customerHierarchy2Desc) {
		this.customerHierarchy2Desc = customerHierarchy2Desc;
	}

	public String getCustomerHierarchy3Desc() {
		return customerHierarchy3Desc;
	}

	public void setCustomerHierarchy3Desc(String customerHierarchy3Desc) {
		this.customerHierarchy3Desc = customerHierarchy3Desc;
	}

	public String getAccountOfficerDesc() {
		return accountOfficerDesc;
	}

	public void setAccountOfficerDesc(String accountOfficerDesc) {
		this.accountOfficerDesc = accountOfficerDesc;
	}

	public String getCustomerAttribute3Desc() {
		return customerAttribute3Desc;
	}

	public void setCustomerAttribute3Desc(String customerAttribute3Desc) {
		this.customerAttribute3Desc = customerAttribute3Desc;
	}

	public String getCustomerAttribute1Desc() {
		return customerAttribute1Desc;
	}

	public void setCustomerAttribute1Desc(String customerAttribute1Desc) {
		this.customerAttribute1Desc = customerAttribute1Desc;
	}

	public String getCustomerAttribute2Desc() {
		return customerAttribute2Desc;
	}

	public void setCustomerAttribute2Desc(String customerAttribute2Desc) {
		this.customerAttribute2Desc = customerAttribute2Desc;
	}

	public String getVisionOucDesc() {
		return visionOucDesc;
	}

	public void setVisionOucDesc(String visionOucDesc) {
		this.visionOucDesc = visionOucDesc;
	}

	public String getVisionSbuDesc() {
		return visionSbuDesc;
	}

	public void setVisionSbuDesc(String visionSbuDesc) {
		this.visionSbuDesc = visionSbuDesc;
	}

	public String getDualNationality1() {
		return dualNationality1;
	}

	public void setDualNationality1(String dualNationality1) {
		this.dualNationality1 = dualNationality1;
	}

	public String getDualNationality2() {
		return dualNationality2;
	}

	public void setDualNationality2(String dualNationality2) {
		this.dualNationality2 = dualNationality2;
	}

	public String getDualNationality3() {
		return dualNationality3;
	}

	public void setDualNationality3(String dualNationality3) {
		this.dualNationality3 = dualNationality3;
	}

	public int getCusExtrasStatus() {
		return cusExtrasStatus;
	}

	public void setCusExtrasStatus(int cusExtrasStatus) {
		this.cusExtrasStatus = cusExtrasStatus;
	}

	public int getCusExtrasStatusNt() {
		return cusExtrasStatusNt;
	}

	public void setCusExtrasStatusNt(int cusExtrasStatusNt) {
		this.cusExtrasStatusNt = cusExtrasStatusNt;
	}
	
}
