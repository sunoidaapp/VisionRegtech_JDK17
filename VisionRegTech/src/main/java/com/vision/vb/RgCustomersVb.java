package com.vision.vb;
import java.util.List;

public class RgCustomersVb extends CommonVb{
	private String cifId= "";
	private String customerId= "";
	private String customerName= "";
	private String cifTypeId= "";
	private String segmentId= "";
	private String parentCifId= "";
	private String parentCifName= "";
	private String age= "";
	private String dob= "";
	private String maritalStatus= "";
	private String sectorCodeDesc= "";
	private String subSectorCodeDesc= "";
	private String visionSbu= "";
	private String oucDesc= "";
	private String aoName= "";
	private String parentCid= "";
	private String passportNumber= "";
	private String idNumber= "";
	private String kraPinNumber= "";
	private String emailId= "";
	private String telephoneNo= "";
	private String physicalAddress= "";
	private String externalRiskRating= "";
	private String riskReviewDate= "";
	private String nonPerformingAssets = "";
	private String shareWallet= "";
	private String groupCustomerId = "";
	private String accountOfficer="";
	private Boolean isDeleteFlag = false;
	private Boolean isModifyFlag = false;
	private Boolean isAddFlag = false;
	private String debitCardFlag = "";
	private String creditCardFlag = "";
	private String primeMobiFlag = "";
	private String cashToBankFlag = "";
	private String primeNetFlag = "";
	private String primeAlertEmailId = "";
	private String custAverageAnnualCreditTurnover = "";
	private String custMonthlyNetRevenue = "";
	private String custLastFiveTxn="";
	private String custfxRevenue = "";
	private String custAverageProdCustHolding = "";
	private String custCrossell = "";
	private String custCrbRating = "";
	private String custDateOfLastCall = "";

	List<RgCustomersVb> cifIdlst = null;
	List<SmartSearchVb> smartSearchOpt = null;
	
	public String getCifId() {
		return cifId;
	}

	public void setCifId(String cifId) {
		this.cifId = cifId;
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

	public String getCifTypeId() {
		return cifTypeId;
	}

	public void setCifTypeId(String cifTypeId) {
		this.cifTypeId = cifTypeId;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}

	public String getParentCifId() {
		return parentCifId;
	}

	public void setParentCifId(String parentCifId) {
		this.parentCifId = parentCifId;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getSectorCodeDesc() {
		return sectorCodeDesc;
	}

	public void setSectorCodeDesc(String sectorCodeDesc) {
		this.sectorCodeDesc = sectorCodeDesc;
	}

	public String getSubSectorCodeDesc() {
		return subSectorCodeDesc;
	}

	public void setSubSectorCodeDesc(String subSectorCodeDesc) {
		this.subSectorCodeDesc = subSectorCodeDesc;
	}

	public String getVisionSbu() {
		return visionSbu;
	}

	public void setVisionSbu(String visionSbu) {
		this.visionSbu = visionSbu;
	}

	public String getOucDesc() {
		return oucDesc;
	}

	public void setOucDesc(String oucDesc) {
		this.oucDesc = oucDesc;
	}

	public String getAoName() {
		return aoName;
	}

	public void setAoName(String aoName) {
		this.aoName = aoName;
	}

	public String getParentCid() {
		return parentCid;
	}

	public void setParentCid(String parentCid) {
		this.parentCid = parentCid;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getKraPinNumber() {
		return kraPinNumber;
	}

	public void setKraPinNumber(String kraPinNumber) {
		this.kraPinNumber = kraPinNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}

	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getPhysicalAddress() {
		return physicalAddress;
	}

	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}

	public String getExternalRiskRating() {
		return externalRiskRating;
	}

	public void setExternalRiskRating(String externalRiskRating) {
		this.externalRiskRating = externalRiskRating;
	}

	public String getRiskReviewDate() {
		return riskReviewDate;
	}

	public void setRiskReviewDate(String riskReviewDate) {
		this.riskReviewDate = riskReviewDate;
	}

	public String getShareWallet() {
		return shareWallet;
	}

	public void setShareWallet(String shareWallet) {
		this.shareWallet = shareWallet;
	}

	public List<RgCustomersVb> getChildCustomerslst() {
		return childCustomerslst;
	}

	public void setChildCustomerslst(List<RgCustomersVb> childCustomerslst) {
		this.childCustomerslst = childCustomerslst;
	}

	private List<RgCustomersVb> childCustomerslst = null;

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getParentCifName() {
		return parentCifName;
	}

	public void setParentCifName(String parentCifName) {
		this.parentCifName = parentCifName;
	}

	public String getAccountOfficer() {
		return accountOfficer;
	}

	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}

	public List<RgCustomersVb> getCifIdlst() {
		return cifIdlst;
	}

	public void setCifIdlst(List<RgCustomersVb> cifIdlst) {
		this.cifIdlst = cifIdlst;
	}

	public Boolean getIsDeleteFlag() {
		return isDeleteFlag;
	}

	public void setIsDeleteFlag(Boolean isDeleteFlag) {
		this.isDeleteFlag = isDeleteFlag;
	}

	public Boolean getIsModifyFlag() {
		return isModifyFlag;
	}

	public void setIsModifyFlag(Boolean isModifyFlag) {
		this.isModifyFlag = isModifyFlag;
	}

	public Boolean getIsAddFlag() {
		return isAddFlag;
	}

	public void setIsAddFlag(Boolean isAddFlag) {
		this.isAddFlag = isAddFlag;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}
	public String getGroupCustomerId() {
		return groupCustomerId;
	}

	public void setGroupCustomerId(String groupCustomerId) {
		this.groupCustomerId = groupCustomerId;
	}

	public String getDebitCardFlag() {
		return debitCardFlag;
	}

	public void setDebitCardFlag(String debitCardFlag) {
		this.debitCardFlag = debitCardFlag;
	}

	public String getCreditCardFlag() {
		return creditCardFlag;
	}

	public void setCreditCardFlag(String creditCardFlag) {
		this.creditCardFlag = creditCardFlag;
	}

	public String getPrimeMobiFlag() {
		return primeMobiFlag;
	}

	public void setPrimeMobiFlag(String primeMobiFlag) {
		this.primeMobiFlag = primeMobiFlag;
	}

	public String getCashToBankFlag() {
		return cashToBankFlag;
	}

	public void setCashToBankFlag(String cashToBankFlag) {
		this.cashToBankFlag = cashToBankFlag;
	}

	public String getPrimeNetFlag() {
		return primeNetFlag;
	}

	public void setPrimeNetFlag(String primeNetFlag) {
		this.primeNetFlag = primeNetFlag;
	}

	public String getPrimeAlertEmailId() {
		return primeAlertEmailId;
	}

	public void setPrimeAlertEmailId(String primeAlertEmailId) {
		this.primeAlertEmailId = primeAlertEmailId;
	}

	public String getCustAverageAnnualCreditTurnover() {
		return custAverageAnnualCreditTurnover;
	}

	public void setCustAverageAnnualCreditTurnover(String custAverageAnnualCreditTurnover) {
		this.custAverageAnnualCreditTurnover = custAverageAnnualCreditTurnover;
	}

	public String getCustMonthlyNetRevenue() {
		return custMonthlyNetRevenue;
	}

	public void setCustMonthlyNetRevenue(String custMonthlyNetRevenue) {
		this.custMonthlyNetRevenue = custMonthlyNetRevenue;
	}

	public String getCustLastFiveTxn() {
		return custLastFiveTxn;
	}

	public void setCustLastFiveTxn(String custLastFiveTxn) {
		this.custLastFiveTxn = custLastFiveTxn;
	}

	public String getCustfxRevenue() {
		return custfxRevenue;
	}

	public void setCustfxRevenue(String custfxRevenue) {
		this.custfxRevenue = custfxRevenue;
	}

	public String getCustAverageProdCustHolding() {
		return custAverageProdCustHolding;
	}

	public void setCustAverageProdCustHolding(String custAverageProdCustHolding) {
		this.custAverageProdCustHolding = custAverageProdCustHolding;
	}

	public String getCustCrossell() {
		return custCrossell;
	}

	public void setCustCrossell(String custCrossell) {
		this.custCrossell = custCrossell;
	}

	public String getCustCrbRating() {
		return custCrbRating;
	}

	public void setCustCrbRating(String custCrbRating) {
		this.custCrbRating = custCrbRating;
	}

	public String getCustDateOfLastCall() {
		return custDateOfLastCall;
	}

	public void setCustDateOfLastCall(String custDateOfLastCall) {
		this.custDateOfLastCall = custDateOfLastCall;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getNonPerformingAssets() {
		return nonPerformingAssets;
	}

	public void setNonPerformingAssets(String nonPerformingAssets) {
		this.nonPerformingAssets = nonPerformingAssets;
	}
}
