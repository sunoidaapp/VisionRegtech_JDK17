package com.vision.vb;

import java.util.List;

public class RsAccessVb extends CommonVb {
	private static final long serialVersionUID = 1L;
	private String reportCategory = "-1";
	private int reportCategoryAt = 0;
	private String reportCategoryDesc = "";
	private String subCategory = "NA";
	private int subCategoryAt = 0;
	private String reportId = "";
	private int raStatusNt = 0;
	private int raStatus = -1;
	private int	userGroupAt = 0;//USER_GROUP_AT
	private String userGroup = "";//USER_GROUP
	private int	userProfileAt =  0;//USER_PROFILE_AT
	private String userProfile = "";//USER_PROFILE
	
	private String subCategoryDesc = "";
	private String reportIdDesc = "";
	private List<RsAccessVb> children;
	private int displayOrder = 1;
	
	public String getReportCategory() {
		return reportCategory;
	}
	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}
	public int getReportCategoryAt() {
		return reportCategoryAt;
	}
	public void setReportCategoryAt(int reportCategoryAt) {
		this.reportCategoryAt = reportCategoryAt;
	}
	public String getReportCategoryDesc() {
		return reportCategoryDesc;
	}
	public void setReportCategoryDesc(String reportCategoryDesc) {
		this.reportCategoryDesc = reportCategoryDesc;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public int getSubCategoryAt() {
		return subCategoryAt;
	}
	public void setSubCategoryAt(int subCategoryAt) {
		this.subCategoryAt = subCategoryAt;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public int getRaStatusNt() {
		return raStatusNt;
	}
	public void setRaStatusNt(int raStatusNt) {
		this.raStatusNt = raStatusNt;
	}
	public int getRaStatus() {
		return raStatus;
	}
	public void setRaStatus(int raStatus) {
		this.raStatus = raStatus;
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
	public String getSubCategoryDesc() {
		return subCategoryDesc;
	}
	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}
	public String getReportIdDesc() {
		return reportIdDesc;
	}
	public void setReportIdDesc(String reportIdDesc) {
		this.reportIdDesc = reportIdDesc;
	}
	public List<RsAccessVb> getChildren() {
		return children;
	}
	public void setChildren(List<RsAccessVb> children) {
		this.children = children;
	}
	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
}
