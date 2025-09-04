package com.vision.vb;

public class VisionBusinessDayVb extends CommonVb{
	private String	businessDate = "";
	private String	dbBusinessDate = "";
	private String	businessYearMonth = "";
	private String	dbBusinessYearMonth = "";
	private String	updateBusinessDate = "";
	/*private int	dlyFrequencyCount = 0;
	private int	monthlyFrequencyCount = 0;*/
	private int visionBusinessDayStatusNt = 1;
	private int visionBusinessDayStatus = -1;
	private String	businessWeeklyDate = "";
	private String  businessQtrYearMonth = "";
	
	private String	dbBusinessWeeklyDate = "";
	private String  dbBusinessQtrYearMonth = "";
	
	private String updAllowFlagDly = "";
	private String updAllowFlagMth = "";
	private String updAllowFlagWky = "";
	private String updAllowFlagQtr = "";
	private String updAllowFlagHyr = "";
	private String updAllowFlagAnn = "";
	
	
	private String businessHalfYearMonth ="";
	private String dbBusinessHalfYearMonth ="";

	private String businessYear ="";
	
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public String getUpdateBusinessDate() {
		return updateBusinessDate;
	}
	public void setUpdateBusinessDate(String updateBusinessDate) {
		this.updateBusinessDate = updateBusinessDate;
	}
	public String getBusinessYearMonth() {
		return businessYearMonth;
	}
	public void setBusinessYearMonth(String businessYearMonth) {
		this.businessYearMonth = businessYearMonth;
	}
	/*public int getDlyFrequencyCount() {
		return dlyFrequencyCount;
	}
	public void setDlyFrequencyCount(int dlyFrequencyCount) {
		this.dlyFrequencyCount = dlyFrequencyCount;
	}
	public int getMonthlyFrequencyCount() {
		return monthlyFrequencyCount;
	}
	public void setMonthlyFrequencyCount(int monthlyFrequencyCount) {
		this.monthlyFrequencyCount = monthlyFrequencyCount;
	}*/
	public int getVisionBusinessDayStatusNt() {
		return visionBusinessDayStatusNt;
	}
	public void setVisionBusinessDayStatusNt(int visionBusinessDayStatusNt) {
		this.visionBusinessDayStatusNt = visionBusinessDayStatusNt;
	}
	public int getVisionBusinessDayStatus() {
		return visionBusinessDayStatus;
	}
	public void setVisionBusinessDayStatus(int visionBusinessDayStatus) {
		this.visionBusinessDayStatus = visionBusinessDayStatus;
	}
	public String getBusinessWeeklyDate() {
		return businessWeeklyDate;
	}
	public void setBusinessWeeklyDate(String businessWeeklyDate) {
		this.businessWeeklyDate = businessWeeklyDate;
	}
	public String getDbBusinessDate() {
		return dbBusinessDate;
	}
	public void setDbBusinessDate(String dbBusinessDate) {
		this.dbBusinessDate = dbBusinessDate;
	}
	public String getDbBusinessYearMonth() {
		return dbBusinessYearMonth;
	}
	public void setDbBusinessYearMonth(String dbBusinessYearMonth) {
		this.dbBusinessYearMonth = dbBusinessYearMonth;
	}
	public String getBusinessQtrYearMonth() {
		return businessQtrYearMonth;
	}
	public void setBusinessQtrYearMonth(String businessQtrYearMonth) {
		this.businessQtrYearMonth = businessQtrYearMonth;
	}
	public String getDbBusinessWeeklyDate() {
		return dbBusinessWeeklyDate;
	}
	public void setDbBusinessWeeklyDate(String dbBusinessWeeklyDate) {
		this.dbBusinessWeeklyDate = dbBusinessWeeklyDate;
	}
	public String getDbBusinessQtrYearMonth() {
		return dbBusinessQtrYearMonth;
	}
	public void setDbBusinessQtrYearMonth(String dbBusinessQtrYearMonth) {
		this.dbBusinessQtrYearMonth = dbBusinessQtrYearMonth;
	}
	public String getUpdAllowFlagDly() {
		return updAllowFlagDly;
	}
	public void setUpdAllowFlagDly(String updAllowFlagDly) {
		this.updAllowFlagDly = updAllowFlagDly;
	}
	public String getUpdAllowFlagMth() {
		return updAllowFlagMth;
	}
	public void setUpdAllowFlagMth(String updAllowFlagMth) {
		this.updAllowFlagMth = updAllowFlagMth;
	}
	public String getUpdAllowFlagWky() {
		return updAllowFlagWky;
	}
	public void setUpdAllowFlagWky(String updAllowFlagWky) {
		this.updAllowFlagWky = updAllowFlagWky;
	}
	public String getUpdAllowFlagQtr() {
		return updAllowFlagQtr;
	}
	public void setUpdAllowFlagQtr(String updAllowFlagQtr) {
		this.updAllowFlagQtr = updAllowFlagQtr;
	}
	public String getBusinessHalfYearMonth() {
		return businessHalfYearMonth;
	}
	public void setBusinessHalfYearMonth(String businessHalfYearMonth) {
		this.businessHalfYearMonth = businessHalfYearMonth;
	}
	public String getBusinessYear() {
		return businessYear;
	}
	public void setBusinessYear(String businessYear) {
		this.businessYear = businessYear;
	}
	public String getUpdAllowFlagHyr() {
		return updAllowFlagHyr;
	}
	public void setUpdAllowFlagHyr(String updAllowFlagHyr) {
		this.updAllowFlagHyr = updAllowFlagHyr;
	}
	public String getUpdAllowFlagAnn() {
		return updAllowFlagAnn;
	}
	public void setUpdAllowFlagAnn(String updAllowFlagAnn) {
		this.updAllowFlagAnn = updAllowFlagAnn;
	}
	public String getDbBusinessHalfYearMonth() {
		return dbBusinessHalfYearMonth;
	}
	public void setDbBusinessHalfYearMonth(String dbBusinessHalfYearMonth) {
		this.dbBusinessHalfYearMonth = dbBusinessHalfYearMonth;
	}
}
