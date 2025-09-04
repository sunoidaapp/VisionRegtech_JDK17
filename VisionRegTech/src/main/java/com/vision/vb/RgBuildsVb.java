package com.vision.vb;

import java.io.Serializable;
import java.util.List;

public class RgBuildsVb extends CommonVb implements Serializable {
	private int sequence = 0;
	private String year = "";
	private int dataSourceAt = 10 ;
	private String dataSource = "";
	private String dataSourceDesc = "";
	private int programSeq = 0;
	private String program = "";
	private String programDesc = "";
	private String dependentProgram = "";
	private String startTime = "";
	private String endTime= "";
	private String lastRunDate = "";
	private String runStatus = "";
	private String runStatusDesc = "";
	private String submitterId = "";
	private int statusCnt = 0;
	private String logFile = "";
	private String buildId = "";
	
	List<RgBuildsVb> rgBuildlst = null;
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getDataSourceAt() {
		return dataSourceAt;
	}
	public void setDataSourceAt(int dataSourceAt) {
		this.dataSourceAt = dataSourceAt;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getProgramDesc() {
		return programDesc;
	}
	public void setProgramDesc(String programDesc) {
		this.programDesc = programDesc;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getLastRunDate() {
		return lastRunDate;
	}
	public void setLastRunDate(String lastRunDate) {
		this.lastRunDate = lastRunDate;
	}
	public String getRunStatus() {
		return runStatus;
	}
	public void setRunStatus(String runStatus) {
		this.runStatus = runStatus;
	}
	public String getSubmitterId() {
		return submitterId;
	}
	public void setSubmitterId(String submitterId) {
		this.submitterId = submitterId;
	}
	public String getDataSourceDesc() {
		return dataSourceDesc;
	}
	public void setDataSourceDesc(String dataSourceDesc) {
		this.dataSourceDesc = dataSourceDesc;
	}
	public int getProgramSeq() {
		return programSeq;
	}
	public void setProgramSeq(int programSeq) {
		this.programSeq = programSeq;
	}
	public int getStatusCnt() {
		return statusCnt;
	}
	public void setStatusCnt(int statusCnt) {
		this.statusCnt = statusCnt;
	}
	public String getDependentProgram() {
		return dependentProgram;
	}
	public void setDependentProgram(String dependentProgram) {
		this.dependentProgram = dependentProgram;
	}
	public String getRunStatusDesc() {
		return runStatusDesc;
	}
	public void setRunStatusDesc(String runStatusDesc) {
		this.runStatusDesc = runStatusDesc;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public List<RgBuildsVb> getRgBuildlst() {
		return rgBuildlst;
	}
	public void setRgBuildlst(List<RgBuildsVb> rgBuildlst) {
		this.rgBuildlst = rgBuildlst;
	}
	public String getLogFile() {
		return logFile;
	}
	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}
	/*public String getPlanningStatus() {
		return planningStatus;
	}
	public void setPlanningStatus(String planningStatus) {
		this.planningStatus = planningStatus;
	}
	public String getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}*/
	public String getBuildId() {
		return buildId;
	}
	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}
}
