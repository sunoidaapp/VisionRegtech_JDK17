package com.vision.vb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope("prototype")
public class VisionUsersVb extends CommonVb {

	private static final long serialVersionUID = -6687470125464332861L;
	
	private int	visionId =  0;//VISION_ID - Key Field
	private String userName = "";//USER_NAME
	private String userLoginId = "";//USER_LOGIN_ID - Key Field
	private String userEmailId = "";//USER_EMAIL_ID
	private String lastActivityDate = "";//LAST_ACTIVITY_DATE
	private int	userGroupAt = 1;//USER_GROUP_AT
	private String userGroup = "";//USER_GROUP
	private int	userProfileAt =  2;//USER_PROFILE_AT
	private String userProfile = "";//USER_PROFILE
	private String updateRestriction = "";//UPDATE_RESTRICTION
	private String legalVehicle = "";//LEGAL_VEHICLE
	private String legalVehicleDesc = "";//LEGAL_VEHICLE_DESC
	private String country = "";//COUNTRY
	private String leBook = "";//LE_BOOK
	private String regionProvince = "";//REGION_PROVINCE
	private String businessGroup = "";//BUSINESS_GROUP
	private String productSuperGroup = "";//PRODUCT_SUPER_GROUP
	private String oucAttribute = "";//OUC_ATTRIBUTE
	private int sbuCodeAt = 3;//SBU_CODE_AT
	private String sbuCode = "";//SBU_CODE
	private String productAttribute = "";//PRODUCT_ATTRIBUTE
	private String accountOfficer = "";//ACCOUNT_OFFICER
	private int	userStatusNt = 1;//USER_STATUS_NT
	private int	userStatus = -1;//USER_STATUS
	private String	userStatusDesc = "";
	private String userStatusDate = "";//USER_STATUS_DATE
	private String lastSuccessfulLoginDate = "";//Non DB Field
	private String lastUnsuccessfulLoginDate = "";
	private String lastUnsuccessfulLoginAttempts = "";
	private String restrictedAo = "";
	private String autoUpdateRestriction = "";
	private String clientName = "";
	private String gcidAccess = "-1";
	private String logInPassWord = "";
	private String passwordChanged = "";
	private String userGrpProfile = "";
	private String fileNmae = "";
	private boolean proilePictureChange = false;
	
	private String staffId = "";
	private String staffName = "";
	private String stfcountry = "";//COUNTRY
	private String stfleBook = "";

	HashMap<String,String> businessDateMap = new HashMap<String,String>();
	private int	applicationAccessAt = 8000;
	private String applicationAccess = "";
	private String ldapUser = "N";
	private String sessionTimeOut = "";
    private String raSoc = "";
    private String otherAttr = "";
    private String rSessionId = "";
	private String bSessionId = "";
    private String password = "";
    private int lastPwdResetCount;
    List<SmartSearchVb> smartSearchOpt = null;
    
    private String clebTransline ="";
	private String clebBusinessline = "";
	private String clebTrasnBusline = "";
	private String activeDirectoryFlag = "Y";
	private String defaultCountry = "";
	private String defaultCountryDesc = "";
	private String defaultLebookDesc = "";
	private String defaultLeBook = "";

	private String passwordChangeDate = "";
	private String nextPasswordModifyDate = "";
	private String password1 = "";
	private String password2 = "";
	private String password3 = "";
	private int changeCount;
	private String passwordResetFlag = "N";
	private int	passwordStatusNt = 0;//PASSWORD_STATUS_NT
	private int	passwordStatus = 0;//PASSWORD_STATUS
	private String currentPassword = "";
	private String newPassword = "";
	private String confirmPassword = "";
	
	private String enableWidgets = "N";

	private String	loggedinflag = "N";
	
	private String leBookDesc = "";//LE_BOOk_DESC
	private String pwdResetTime = "";
	private String passwordResetURL = "";
	private String errorMessage = "";
	private String status = "";
	private String emailStatus = "";
	private String homeDashboard = "";
	private List<UserRestrictionVb> restrictionList = null;
	private Map<String, List> restrictionMap = null;
	
	private String legalVehicleCleb = "";
	private String remoteHostName = "";
	private String loginStatus = "";
	private String comments = "";
	private String ipAddress = "";
	private String macAddress = "";
	
	private String language = "";
	private String appTheme = "";
	private String reportSliderTheme = "";

	private String userNameForUnlock = "";

	private String visionToken = "";
	private String troubleShootFor = "";
	
	private String application_Id = "";
	
	
	public String getStaffId() {
		return staffId;
	}

	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStfcountry() {
		return stfcountry;
	}

	public void setStfcountry(String stfcountry) {
		this.stfcountry = stfcountry;
	}

	public String getStfleBook() {
		return stfleBook;
	}

	public void setStfleBook(String stfleBook) {
		this.stfleBook = stfleBook;
	}

	public boolean isProilePictureChange() {
		return proilePictureChange;
	}

	public void setProilePictureChange(boolean proilePictureChange) {
		this.proilePictureChange = proilePictureChange;
	}

	public String getFileNmae() {
		return fileNmae;
	}

	public void setFileNmae(String fileNmae) {
		this.fileNmae = fileNmae;
	}
	private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
	public String getLogInPassWord() {
		return logInPassWord;
	}
	public void setLogInPassWord(String logInPassWord) {
		this.logInPassWord = logInPassWord;
	}
	public int getVisionId() {
		return visionId;
	}
	public void setVisionId(int maximId) {
		this.visionId = maximId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserLoginId() {
		return userLoginId;
	}
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}
	public String getUserEmailId() {
		return userEmailId;
	}
	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}
	public String getLastActivityDate() {
		return lastActivityDate;
	}
	public void setLastActivityDate(String lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}
	public int getUserGroupAt() {
		return userGroupAt;
	}
	public void setUserGroupAt(int userGroupAt) {
		this.userGroupAt = userGroupAt;
	}
	public String getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	public int getUserProfileAt() {
		return userProfileAt;
	}
	public void setUserProfileAt(int userProfileAt) {
		this.userProfileAt = userProfileAt;
	}
	public String getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}
	public String getUpdateRestriction() {
		return updateRestriction;
	}
	public void setUpdateRestriction(String updateRestriction) {
		this.updateRestriction = updateRestriction;
	}
	public String getLegalVehicle() {
		return legalVehicle;
	}
	public void setLegalVehicle(String legalVehicle) {
		this.legalVehicle = legalVehicle;
	}
	public String getLegalVehicleDesc() {
		return legalVehicleDesc;
	}
	public void setLegalVehicleDesc(String legalVehicleDesc) {
		this.legalVehicleDesc = legalVehicleDesc;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLeBook() {
		return leBook;
	}
	public void setLeBook(String leBook) {
		this.leBook = leBook;
	}
	public String getRegionProvince() {
		return regionProvince;
	}
	public void setRegionProvince(String regionProvince) {
		this.regionProvince = regionProvince;
	}
	public String getBusinessGroup() {
		return businessGroup;
	}
	public void setBusinessGroup(String businessGroup) {
		this.businessGroup = businessGroup;
	}
	public String getProductSuperGroup() {
		return productSuperGroup;
	}
	public void setProductSuperGroup(String productSuperGroup) {
		this.productSuperGroup = productSuperGroup;
	}
	public String getOucAttribute() {
		return oucAttribute;
	}
	public void setOucAttribute(String oucAttribute) {
		this.oucAttribute = oucAttribute;
	}
	public int getSbuCodeAt() {
		return sbuCodeAt;
	}
	public void setSbuCodeAt(int sbuCodeNt) {
		this.sbuCodeAt = sbuCodeNt;
	}
	public String getSbuCode() {
		return sbuCode;
	}
	public void setSbuCode(String sbuCode) {
		this.sbuCode = sbuCode;
	}
	public String getProductAttribute() {
		return productAttribute;
	}
	public void setProductAttribute(String productAttribute) {
		this.productAttribute = productAttribute;
	}
	public String getAccountOfficer() {
		return accountOfficer;
	}
	public void setAccountOfficer(String accountOfficer) {
		this.accountOfficer = accountOfficer;
	}
	public String getGcidAccess() {
		return gcidAccess;
	}
	public void setGcidAccess(String gcidAccess) {
		this.gcidAccess = gcidAccess;
	}
	public int getUserStatusNt() {
		return userStatusNt;
	}
	public void setUserStatusNt(int userStatusNt) {
		this.userStatusNt = userStatusNt;
	}
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}
	public String getUserStatusDate() {
		return userStatusDate;
	}
	public void setUserStatusDate(String userStatusDate) {
		this.userStatusDate = userStatusDate;
	}
	public String getLastSuccessfulLoginDate() {
		return lastSuccessfulLoginDate;
	}
	public void setLastSuccessfulLoginDate(String lastSuccessfulLoginDate) {
		this.lastSuccessfulLoginDate = lastSuccessfulLoginDate;
	}
	public String getLastUnsuccessfulLoginDate() {
		return lastUnsuccessfulLoginDate;
	}
	public void setLastUnsuccessfulLoginDate(String lastUnsuccessfulLoginDate) {
		this.lastUnsuccessfulLoginDate = lastUnsuccessfulLoginDate;
	}
	public String getLastUnsuccessfulLoginAttempts() {
		return lastUnsuccessfulLoginAttempts;
	}
	public void setLastUnsuccessfulLoginAttempts(
			String lastUnsuccessfulLoginAttempts) {
		this.lastUnsuccessfulLoginAttempts = lastUnsuccessfulLoginAttempts;
	}

	public String getEnableWidgets() {
		return enableWidgets;
	}

	public void setEnableWidgets(String enableWidgets) {
		this.enableWidgets = enableWidgets;
	}

	public String getLeBookDesc() {
		return leBookDesc;
	}

	public void setLeBookDesc(String leBookDesc) {
		this.leBookDesc = leBookDesc;
	}

	public String getPwdResetTime() {
		return pwdResetTime;
	}

	public void setPwdResetTime(String pwdResetTime) {
		this.pwdResetTime = pwdResetTime;
	}

	public String getPasswordResetURL() {
		return passwordResetURL;
	}

	public void setPasswordResetURL(String passwordResetURL) {
		this.passwordResetURL = passwordResetURL;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(String emailStatus) {
		this.emailStatus = emailStatus;
	}

	public List<UserRestrictionVb> getRestrictionList() {
		return restrictionList;
	}

	public void setRestrictionList(List<UserRestrictionVb> restrictionList) {
		this.restrictionList = restrictionList;
	}

	public String getLegalVehicleCleb() {
		return legalVehicleCleb;
	}

	public void setLegalVehicleCleb(String legalVehicleCleb) {
		this.legalVehicleCleb = legalVehicleCleb;
	}
	public Map<String, List> getRestrictionMap() {
		return restrictionMap;
	}

	public void setRestrictionMap(Map<String, List> restrictionMap) {
		this.restrictionMap = restrictionMap;
	}

	public String getRestrictedAo() {
		return restrictedAo;
	}

	public void setRestrictedAo(String restrictedAo) {
		this.restrictedAo = restrictedAo;
	}

	public String getHomeDashboard() {
		return homeDashboard;
	}

	public void setHomeDashboard(String homeDashboard) {
		this.homeDashboard = homeDashboard;
	}

	public String getAutoUpdateRestriction() {
		return autoUpdateRestriction;
	}

	public void setAutoUpdateRestriction(String autoUpdateRestriction) {
		this.autoUpdateRestriction = autoUpdateRestriction;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getUserGrpProfile() {
		return userGrpProfile;
	}

	public void setUserGrpProfile(String userGrpProfile) {
		this.userGrpProfile = userGrpProfile;
	}

	public String getRemoteHostName() {
		return remoteHostName;
	}

	public void setRemoteHostName(String remoteHostName) {
		this.remoteHostName = remoteHostName;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAppTheme() {
		return appTheme;
	}

	public void setAppTheme(String appTheme) {
		this.appTheme = appTheme;
	}

	public String getReportSliderTheme() {
		return reportSliderTheme;
	}

	public void setReportSliderTheme(String reportSliderTheme) {
		this.reportSliderTheme = reportSliderTheme;
	}

	public HashMap<String, String> getBusinessDateMap() {
		return businessDateMap;
	}

	public void setBusinessDateMap(HashMap<String, String> businessDateMap) {
		this.businessDateMap = businessDateMap;
	}

	public String getUserStatusDesc() {
		return userStatusDesc;
	}

	public void setUserStatusDesc(String userStatusDesc) {
		this.userStatusDesc = userStatusDesc;
	}

	public String getApplicationAccess() {
		return applicationAccess;
	}

	public void setApplicationAccess(String applicationAccess) {
		this.applicationAccess = applicationAccess;
	}

	public String getLdapUser() {
		return ldapUser;
	}

	public void setLdapUser(String ldapUser) {
		this.ldapUser = ldapUser;
	}

	public String getPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(String passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public String getSessionTimeOut() {
		return sessionTimeOut;
	}

	public void setSessionTimeOut(String sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}

	public String getRaSoc() {
		return raSoc;
	}

	public void setRaSoc(String raSoc) {
		this.raSoc = raSoc;
	}

	public String getOtherAttr() {
		return otherAttr;
	}

	public void setOtherAttr(String otherAttr) {
		this.otherAttr = otherAttr;
	}

	public String getrSessionId() {
		return rSessionId;
	}

	public void setrSessionId(String rSessionId) {
		this.rSessionId = rSessionId;
	}

	public String getbSessionId() {
		return bSessionId;
	}

	public void setbSessionId(String bSessionId) {
		this.bSessionId = bSessionId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordResetFlag() {
		return passwordResetFlag;
	}

	public void setPasswordResetFlag(String passwordResetFlag) {
		this.passwordResetFlag = passwordResetFlag;
	}

	public int getLastPwdResetCount() {
		return lastPwdResetCount;
	}

	public void setLastPwdResetCount(int lastPwdResetCount) {
		this.lastPwdResetCount = lastPwdResetCount;
	}

	public List<SmartSearchVb> getSmartSearchOpt() {
		return smartSearchOpt;
	}

	public void setSmartSearchOpt(List<SmartSearchVb> smartSearchOpt) {
		this.smartSearchOpt = smartSearchOpt;
	}

	public String getClebTransline() {
		return clebTransline;
	}

	public void setClebTransline(String clebTransline) {
		this.clebTransline = clebTransline;
	}

	public String getClebBusinessline() {
		return clebBusinessline;
	}

	public void setClebBusinessline(String clebBusinessline) {
		this.clebBusinessline = clebBusinessline;
	}

	public String getClebTrasnBusline() {
		return clebTrasnBusline;
	}

	public void setClebTrasnBusline(String clebTrasnBusline) {
		this.clebTrasnBusline = clebTrasnBusline;
	}

	public String getActiveDirectoryFlag() {
		return activeDirectoryFlag;
	}

	public void setActiveDirectoryFlag(String activeDirectoryFlag) {
		this.activeDirectoryFlag = activeDirectoryFlag;
	}

	public String getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(String defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public String getDefaultCountryDesc() {
		return defaultCountryDesc;
	}

	public void setDefaultCountryDesc(String defaultCountryDesc) {
		this.defaultCountryDesc = defaultCountryDesc;
	}

	public String getDefaultLebookDesc() {
		return defaultLebookDesc;
	}

	public void setDefaultLebookDesc(String defaultLebookDesc) {
		this.defaultLebookDesc = defaultLebookDesc;
	}

	public String getDefaultLeBook() {
		return defaultLeBook;
	}

	public void setDefaultLeBook(String defaultLeBook) {
		this.defaultLeBook = defaultLeBook;
	}

	public String getPasswordChangeDate() {
		return passwordChangeDate;
	}

	public void setPasswordChangeDate(String passwordChangeDate) {
		this.passwordChangeDate = passwordChangeDate;
	}

	public String getNextPasswordModifyDate() {
		return nextPasswordModifyDate;
	}

	public void setNextPasswordModifyDate(String nextPasswordModifyDate) {
		this.nextPasswordModifyDate = nextPasswordModifyDate;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getPassword3() {
		return password3;
	}

	public void setPassword3(String password3) {
		this.password3 = password3;
	}

	public int getChangeCount() {
		return changeCount;
	}

	public void setChangeCount(int changeCount) {
		this.changeCount = changeCount;
	}

	public int getPasswordStatusNt() {
		return passwordStatusNt;
	}

	public void setPasswordStatusNt(int passwordStatusNt) {
		this.passwordStatusNt = passwordStatusNt;
	}

	public int getPasswordStatus() {
		return passwordStatus;
	}

	public void setPasswordStatus(int passwordStatus) {
		this.passwordStatus = passwordStatus;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getLoggedinflag() {
		return loggedinflag;
	}

	public void setLoggedinflag(String loggedinflag) {
		this.loggedinflag = loggedinflag;
	}

	public String getUserNameForUnlock() {
		return userNameForUnlock;
	}

	public void setUserNameForUnlock(String userNameForUnlock) {
		this.userNameForUnlock = userNameForUnlock;
	}

	public String getVisionToken() {
		return visionToken;
	}

	public void setVisionToken(String visionToken) {
		this.visionToken = visionToken;
	}

	public String getTroubleShootFor() {
		return troubleShootFor;
	}

	public void setTroubleShootFor(String troubleShootFor) {
		this.troubleShootFor = troubleShootFor;
	}

	public int getApplicationAccessAt() {
		return applicationAccessAt;
	}

	public void setApplicationAccessAt(int applicationAccessAt) {
		this.applicationAccessAt = applicationAccessAt;
	}

	public String getApplication_Id() {
		return application_Id;
	}

	public void setApplication_Id(String application_Id) {
		this.application_Id = application_Id;
	}
	
}