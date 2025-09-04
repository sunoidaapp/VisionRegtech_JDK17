package com.vision.vb;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class LeadEntryVb extends CommonVb{
	private String leadNo="";
	private int leadTypeAt=6011;
	private String leadType="";
	private String leadDate="";
	private String leadTypeDesc="";
	private String accountOfficer="";
	private String aoName="";
	private String customerName="";
	private int productBasketAt=6000;
	private String productBasket="";
	private String productBasketDesc="";
	private int schemeCodeAt=171;
	private String schemeCode="";
	private String schemeCodeDesc="";
	private String customerId="";
	private String segment = "";
	private String operationMode ="";
	private String accountType ="";
	
	private String dob="";
	private String emailId="";
	private String primaryContact="";
	private String secondaryContact="";
	private String postalAddress="";
	private String physicalAddress="";
	private String nationality="";
	private String countryCode="";
	private String region="";
	private String identityNo="";
	private String kraPinNo="";
	private String profession="";
	private String designation="";
	private String oraganisation="";
	private String referalSource="";
	private String incomeLevel="";
	
	private String shortTerm="";
	private String medTerm="";
	private String longTerm="";
	private int businessSectorAt=59;
	private String businessSector="";
	private String businessSectorDesc="";
	private int businessSubSectorAt=60;
	private String businessSubSector="";
	private String businessSubSectorDesc="";
	private String riskRating="";
	private String yearOperation="";
	private String employeeSize="";
	private String complianceRef="";
	private String balanceSheetSize="";
	private String annualTurnOver="";
	private String creditStanding="";
	private String debitStanding="";
	private String strategyInfo="";
	private String refDocName="";
	private String agewiseFileName="";
	private String accPlanFileName="";
	
	private MultipartFile refDocFile;
	private MultipartFile ageWiseFile;
	private MultipartFile accPlanFile;
	
	private int leadStatusAt=6010;
	private String leadStatus="";
	private String leadStatusDesc="";
	private String lostComment="";
	
	List<LeadBankInfoVb> accPlanlst = null;
	List<LeadCompanyInfoVb> companyInfolst = null;
	List<SmartSearchVb> smartSearchOpt = null;
	List<AlphaSubTabVb> uploadedFile = null;
	
	public String getLeadNo() {
		return leadNo;
	}
	public void setLeadNo(String leadNo) {
		this.leadNo = leadNo;
	}
	public int getLeadTypeAt() {
		return leadTypeAt;
	}
	public void setLeadTypeAt(int leadTypeAt) {
		this.leadTypeAt = leadTypeAt;
	}
	public String getLeadType() {
		return leadType;
	}
	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}
	public String getLeadDate() {
		return leadDate;
	}
	public void setLeadDate(String leadDate) {
		this.leadDate = leadDate;
	}
	public String getLeadTypeDesc() {
		return leadTypeDesc;
	}
	public void setLeadTypeDesc(String leadTypeDesc) {
		this.leadTypeDesc = leadTypeDesc;
	}
	public String getAccountOfficer() {
		return accountOfficer;
	}
	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}
	public String getAoName() {
		return aoName;
	}
	public void setAoName(String aoName) {
		this.aoName = aoName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getProductBasketAt() {
		return productBasketAt;
	}
	public void setProductBasketAt(int productBasketAt) {
		this.productBasketAt = productBasketAt;
	}
	public String getProductBasket() {
		return productBasket;
	}
	public void setProductBasket(String productBasket) {
		this.productBasket = productBasket;
	}
	public String getProductBasketDesc() {
		return productBasketDesc;
	}
	public void setProductBasketDesc(String productBasketDesc) {
		this.productBasketDesc = productBasketDesc;
	}
	public int getSchemeCodeAt() {
		return schemeCodeAt;
	}
	public void setSchemeCodeAt(int schemeCodeAt) {
		this.schemeCodeAt = schemeCodeAt;
	}
	public String getSchemeCode() {
		return schemeCode;
	}
	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}
	public String getSchemeCodeDesc() {
		return schemeCodeDesc;
	}
	public void setSchemeCodeDesc(String schemeCodeDesc) {
		this.schemeCodeDesc = schemeCodeDesc;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPrimaryContact() {
		return primaryContact;
	}
	public void setPrimaryContact(String primaryContact) {
		this.primaryContact = primaryContact;
	}
	public String getSecondaryContact() {
		return secondaryContact;
	}
	public void setSecondaryContact(String secondaryContact) {
		this.secondaryContact = secondaryContact;
	}
	public String getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	public String getPhysicalAddress() {
		return physicalAddress;
	}
	public void setPhysicalAddress(String physicalAddress) {
		this.physicalAddress = physicalAddress;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getIdentityNo() {
		return identityNo;
	}
	public void setIdentityNo(String identityNo) {
		this.identityNo = identityNo;
	}
	public String getKraPinNo() {
		return kraPinNo;
	}
	public void setKraPinNo(String kraPinNo) {
		this.kraPinNo = kraPinNo;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getOraganisation() {
		return oraganisation;
	}
	public void setOraganisation(String oraganisation) {
		this.oraganisation = oraganisation;
	}
	public String getReferalSource() {
		return referalSource;
	}
	public void setReferalSource(String referalSource) {
		this.referalSource = referalSource;
	}
	public String getIncomeLevel() {
		return incomeLevel;
	}
	public void setIncomeLevel(String incomeLevel) {
		this.incomeLevel = incomeLevel;
	}
	public String getShortTerm() {
		return shortTerm;
	}
	public void setShortTerm(String shortTerm) {
		this.shortTerm = shortTerm;
	}
	public String getMedTerm() {
		return medTerm;
	}
	public void setMedTerm(String medTerm) {
		this.medTerm = medTerm;
	}
	public String getLongTerm() {
		return longTerm;
	}
	public void setLongTerm(String longTerm) {
		this.longTerm = longTerm;
	}
	public int getBusinessSectorAt() {
		return businessSectorAt;
	}
	public void setBusinessSectorAt(int businessSectorAt) {
		this.businessSectorAt = businessSectorAt;
	}
	public String getBusinessSector() {
		return businessSector;
	}
	public void setBusinessSector(String businessSector) {
		this.businessSector = businessSector;
	}
	public String getBusinessSectorDesc() {
		return businessSectorDesc;
	}
	public void setBusinessSectorDesc(String businessSectorDesc) {
		this.businessSectorDesc = businessSectorDesc;
	}
	public int getBusinessSubSectorAt() {
		return businessSubSectorAt;
	}
	public void setBusinessSubSectorAt(int businessSubSectorAt) {
		this.businessSubSectorAt = businessSubSectorAt;
	}
	public String getBusinessSubSector() {
		return businessSubSector;
	}
	public void setBusinessSubSector(String businessSubSector) {
		this.businessSubSector = businessSubSector;
	}
	public String getBusinessSubSectorDesc() {
		return businessSubSectorDesc;
	}
	public void setBusinessSubSectorDesc(String businessSubSectorDesc) {
		this.businessSubSectorDesc = businessSubSectorDesc;
	}
	public String getRiskRating() {
		return riskRating;
	}
	public void setRiskRating(String riskRating) {
		this.riskRating = riskRating;
	}
	public String getYearOperation() {
		return yearOperation;
	}
	public void setYearOperation(String yearOperation) {
		this.yearOperation = yearOperation;
	}
	public String getEmployeeSize() {
		return employeeSize;
	}
	public void setEmployeeSize(String employeeSize) {
		this.employeeSize = employeeSize;
	}
	public String getComplianceRef() {
		return complianceRef;
	}
	public void setComplianceRef(String complianceRef) {
		this.complianceRef = complianceRef;
	}
	public String getBalanceSheetSize() {
		return balanceSheetSize;
	}
	public void setBalanceSheetSize(String balanceSheetSize) {
		this.balanceSheetSize = balanceSheetSize;
	}
	public String getAnnualTurnOver() {
		return annualTurnOver;
	}
	public void setAnnualTurnOver(String annualTurnOver) {
		this.annualTurnOver = annualTurnOver;
	}
	public String getCreditStanding() {
		return creditStanding;
	}
	public void setCreditStanding(String creditStanding) {
		this.creditStanding = creditStanding;
	}
	public String getDebitStanding() {
		return debitStanding;
	}
	public void setDebitStanding(String debitStanding) {
		this.debitStanding = debitStanding;
	}
	public String getStrategyInfo() {
		return strategyInfo;
	}
	public void setStrategyInfo(String strategyInfo) {
		this.strategyInfo = strategyInfo;
	}
	public String getRefDocName() {
		return refDocName;
	}
	public void setRefDocName(String refDocName) {
		this.refDocName = refDocName;
	}
	public String getAgewiseFileName() {
		return agewiseFileName;
	}
	public void setAgewiseFileName(String agewiseFileName) {
		this.agewiseFileName = agewiseFileName;
	}
	public String getAccPlanFileName() {
		return accPlanFileName;
	}
	public void setAccPlanFileName(String accPlanFileName) {
		this.accPlanFileName = accPlanFileName;
	}
	public MultipartFile getRefDocFile() {
		return refDocFile;
	}
	public void setRefDocFile(MultipartFile refDocFile) {
		this.refDocFile = refDocFile;
	}
	public MultipartFile getAgeWiseFile() {
		return ageWiseFile;
	}
	public void setAgeWiseFile(MultipartFile ageWiseFile) {
		this.ageWiseFile = ageWiseFile;
	}
	public MultipartFile getAccPlanFile() {
		return accPlanFile;
	}
	public void setAccPlanFile(MultipartFile accPlanFile) {
		this.accPlanFile = accPlanFile;
	}
	public int getLeadStatusAt() {
		return leadStatusAt;
	}
	public void setLeadStatusAt(int leadStatusAt) {
		this.leadStatusAt = leadStatusAt;
	}
	public String getLeadStatus() {
		return leadStatus;
	}
	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}
	public String getLeadStatusDesc() {
		return leadStatusDesc;
	}
	public void setLeadStatusDesc(String leadStatusDesc) {
		this.leadStatusDesc = leadStatusDesc;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public List<LeadBankInfoVb> getAccPlanlst() {
		return accPlanlst;
	}
	public void setAccPlanlst(List<LeadBankInfoVb> accPlanlst) {
		this.accPlanlst = accPlanlst;
	}
	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}
	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}
	public List<AlphaSubTabVb> getUploadedFile() {
		return uploadedFile;
	}
	public void setUploadedFile(List<AlphaSubTabVb> uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	public String getLostComment() {
		return lostComment;
	}
	public void setLostComment(String lostComment) {
		this.lostComment = lostComment;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getOperationMode() {
		return operationMode;
	}
	public void setOperationMode(String operationMode) {
		this.operationMode = operationMode;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public List<LeadCompanyInfoVb> getCompanyInfolst() {
		return companyInfolst;
	}
	public void setCompanyInfolst(List<LeadCompanyInfoVb> companyInfolst) {
		this.companyInfolst = companyInfolst;
	}
}
