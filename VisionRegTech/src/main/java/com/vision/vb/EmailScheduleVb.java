package com.vision.vb;

import java.util.Date;

public class EmailScheduleVb extends CommonVb {

	private String templateId = "";
	private String scheduleStartDate = "";
	private String scheduleEndDate = "";
	private int rssFrequencyAt;
	private String rssFrequency = "";
	private String emailTo = "";
	private String emailCc = "";
	private String emailBcc = "";
	private String emailSubject = "";
	private String emailHeader = "";
	private String emailFooter = "";
	private String emailBody = "";
	private String nextScheduleDate = "";
	private String prevScheduleDate = "";
	private String intervalXDays = "";
	private String intervalXHours = "";
	private String intervalXMins = "";
	private int rssStatusNt;
	private String rssStatus = "";
	private String reportingDate = "";
	private int mailTgCount = 0;
	private String esEmail = "";
	private Date mailTgDate;
	private String sheduleDate;
	private int mailEsCount = 0;
	private Date esMailTgDate;
	private String templateDesc = "";
	private String processFrequency = "";

	public String getTemplateDesc() {
		return templateDesc;
	}

	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}

	public String getProcessFrequency() {
		return processFrequency;
	}

	public void setProcessFrequency(String processFrequency) {
		this.processFrequency = processFrequency;
	}

	public int getMailEsCount() {
		return mailEsCount;
	}

	public void setMailEsCount(int mailEsCount) {
		this.mailEsCount = mailEsCount;
	}

	public String getEsEmail() {
		return esEmail;
	}

	public void setEsEmail(String esEmail) {
		this.esEmail = esEmail;
	}

	public int getMailTgCount() {
		return mailTgCount;
	}

	public void setMailTgCount(int mailTgCount) {
		this.mailTgCount = mailTgCount;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getScheduleStartDate() {
		return scheduleStartDate;
	}

	public void setScheduleStartDate(String scheduleStartDate) {
		this.scheduleStartDate = scheduleStartDate;
	}

	public String getScheduleEndDate() {
		return scheduleEndDate;
	}

	public void setScheduleEndDate(String scheduleEndDate) {
		this.scheduleEndDate = scheduleEndDate;
	}

	public int getRssFrequencyAt() {
		return rssFrequencyAt;
	}

	public void setRssFrequencyAt(int rssFrequencyAt) {
		this.rssFrequencyAt = rssFrequencyAt;
	}

	public String getRssFrequency() {
		return rssFrequency;
	}

	public void setRssFrequency(String rssFrequency) {
		this.rssFrequency = rssFrequency;
	}

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getEmailCc() {
		return emailCc;
	}

	public void setEmailCc(String emailCc) {
		this.emailCc = emailCc;
	}

	public String getEmailBcc() {
		return emailBcc;
	}

	public void setEmailBcc(String emailBcc) {
		this.emailBcc = emailBcc;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailHeader() {
		return emailHeader;
	}

	public void setEmailHeader(String emailHeader) {
		this.emailHeader = emailHeader;
	}

	public String getEmailFooter() {
		return emailFooter;
	}

	public void setEmailFooter(String emailFooter) {
		this.emailFooter = emailFooter;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getNextScheduleDate() {
		return nextScheduleDate;
	}

	public void setNextScheduleDate(String nextScheduleDate) {
		this.nextScheduleDate = nextScheduleDate;
	}

	public String getPrevScheduleDate() {
		return prevScheduleDate;
	}

	public void setPrevScheduleDate(String prevScheduleDate) {
		this.prevScheduleDate = prevScheduleDate;
	}

	public String getIntervalXDays() {
		return intervalXDays;
	}

	public void setIntervalXDays(String intervalXDays) {
		this.intervalXDays = intervalXDays;
	}

	public String getIntervalXHours() {
		return intervalXHours;
	}

	public void setIntervalXHours(String intervalXHours) {
		this.intervalXHours = intervalXHours;
	}

	public String getIntervalXMins() {
		return intervalXMins;
	}

	public void setIntervalXMins(String intervalXMins) {
		this.intervalXMins = intervalXMins;
	}

	public int getRssStatusNt() {
		return rssStatusNt;
	}

	public void setRssStatusNt(int rssStatusNt) {
		this.rssStatusNt = rssStatusNt;
	}

	public String getRssStatus() {
		return rssStatus;
	}

	public void setRssStatus(String rssStatus) {
		this.rssStatus = rssStatus;
	}

	public String getReportingDate() {
		return reportingDate;
	}

	public void setReportingDate(String reportingDate) {
		this.reportingDate = reportingDate;
	}

	public Date getMailTgDate() {
		return mailTgDate;
	}

	public void setMailTgDate(Date mailTgDate) {
		this.mailTgDate = mailTgDate;
	}

	public String getSheduleDate() {
		return sheduleDate;
	}

	public void setSheduleDate(String sheduleDate) {
		this.sheduleDate = sheduleDate;
	}

	public Date getEsMailTgDate() {
		return esMailTgDate;
	}

	public void setEsMailTgDate(Date esMailTgDate) {
		this.esMailTgDate = esMailTgDate;
	}

}
