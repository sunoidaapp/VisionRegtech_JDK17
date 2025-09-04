package com.vision.vb;

public class DataAcquisitionDynamicVb extends CommonVb{

	private String templateName = "";
	private String templateDesc = "";
	private String feedStgName = "";
	private String filePattern = "";
	private String excelFilePattern = "";
	private String excelTemplateId = "";
	private int acquisitionProcessTypeAt = 1079;
	private String acquisitionProcessType = "";
	private int connectivityTypeAt = 1081;
	private String connectivityType = "";
	private String connectivityDetails = "";
	private int databaseTypeAt = 1082;
	private String databaseType = "";
	private String databaseConnectivityDetails = "";
	private String serverFolderDetails = "";
	private int sourceScriptTypeAt = 1083;
	private String sourceScriptType = "";
	private String sourceServerScript="";
	private int targetScriptTypeAt = 1083;
	private String targetScriptType = "";
	private String targetServerScript;
	private int readinessScriptTypeAt = 1083;
	private String readinessScriptType = "";
	private String acquisitionReadinessScripts;
	private int preactivityScriptTypeAt = 1083;
	private String preactivityScriptType = "";
	private String preactivityScripts;
	private String integrityScriptName = "";
	private int frequencyProcessAt = 1077;
	private String frequencyProcess = "";
	private String processSequence ="";
	private String adfStartTime = "";
	private String adfEndTime = "";
	private String accessPermission = "N";
	private String dateLastExtraction = "";
	private String debugMode = "N";
	private int dataAcqStatusNt = 1;
	private int dataAcqStatus = -1;
	private String mandatoryFlag = "";
	private int mandatoryFlagAt = 1083;
	private int integrityScriptTypeAt = 1083;
	private String integrityScriptType = "";
	
	private String popConnectivityType="";
	private String popDbConnectivityType="";
	private String popDbLink="";
	private String popDbVarName="";
	private String popDbName="";
	private String popDbInstance="";
	private String popDbIp="";
	private String adfStartDay = "";
	private String adfEndDay = "";

	private int templateControlStatusAt = 206;
	private String templateControlStatus = "C";
	private String subAdfNumber = "";
	
	private String macroName = "";
	private String macroType ="";
	private String tagName ="";
	private String displayName ="";
	
	
	private String serverip="";
	private String serverport="";
	private String serverpwd="";
	private String serveruser="";
	private String dbinstance="";
	private String dbuser="";
	private String user="";
	private String pwd="";
	private String sid="";
	private String dbip="";
	private String dbname="";
	private String dbport="";
	private String serverhostname="";
	private String dbpwd="";
	private String servicename="";
	private String connectionString="";
	private String tagValue="";
	private String varName="";
	private String varType="";
	private String splCharReplace ="N";
	private boolean isAuditDbPop = false;
	private String DbPopOperation ="";
	private String ConnetPopOperation ="";
	
	private String masked ="";
	private String encryption ="";
	private String majorBuild ="";
	private int rttNextIntervalTypeAt = 2032;
	private String rttNextIntervalType   = "-1";
	private String rttEveryXMins = "";
	private String buildRunFlag = "";
	
	public int getIntegrityScriptTypeAt() {
		return integrityScriptTypeAt;
	}
	public void setIntegrityScriptTypeAt(int integrityScriptTypeAt) {
		this.integrityScriptTypeAt = integrityScriptTypeAt;
	}
	public String getIntegrityScriptType() {
		return integrityScriptType;
	}
	public void setIntegrityScriptType(String integrityScriptType) {
		this.integrityScriptType = integrityScriptType;
	}
	
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getTemplateDesc() {
		return templateDesc;
	}
	public void setTemplateDesc(String templateDesc) {
		this.templateDesc = templateDesc;
	}
	public String getFeedStgName() {
		return feedStgName;
	}
	public void setFeedStgName(String feedStgName) {
		this.feedStgName = feedStgName;
	}
	public String getFilePattern() {
		return filePattern;
	}
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}
	public String getExcelFilePattern() {
		return excelFilePattern;
	}
	public void setExcelFilePattern(String excelFilePattern) {
		this.excelFilePattern = excelFilePattern;
	}
	public String getExcelTemplateId() {
		return excelTemplateId;
	}
	public void setExcelTemplateId(String excelTemplateId) {
		this.excelTemplateId = excelTemplateId;
	}
	public int getAcquisitionProcessTypeAt() {
		return acquisitionProcessTypeAt;
	}
	public void setAcquisitionProcessTypeAt(int acquisitionProcessTypeAt) {
		this.acquisitionProcessTypeAt = acquisitionProcessTypeAt;
	}
	public String getAcquisitionProcessType() {
		return acquisitionProcessType;
	}
	public void setAcquisitionProcessType(String acquisitionProcessType) {
		this.acquisitionProcessType = acquisitionProcessType;
	}
	public int getConnectivityTypeAt() {
		return connectivityTypeAt;
	}
	public void setConnectivityTypeAt(int connectivityTypeAt) {
		this.connectivityTypeAt = connectivityTypeAt;
	}
	public String getConnectivityType() {
		return connectivityType;
	}
	public void setConnectivityType(String connectivityType) {
		this.connectivityType = connectivityType;
	}
	public String getConnectivityDetails() {
		return connectivityDetails;
	}
	public void setConnectivityDetails(String connectivityDetails) {
		this.connectivityDetails = connectivityDetails;
	}
	public int getDatabaseTypeAt() {
		return databaseTypeAt;
	}
	public void setDatabaseTypeAt(int databaseTypeAt) {
		this.databaseTypeAt = databaseTypeAt;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	public String getDatabaseConnectivityDetails() {
		return databaseConnectivityDetails;
	}
	public void setDatabaseConnectivityDetails(String databaseConnectivityDetails) {
		this.databaseConnectivityDetails = databaseConnectivityDetails;
	}
	public String getServerFolderDetails() {
		return serverFolderDetails;
	}
	public void setServerFolderDetails(String serverFolderDetails) {
		this.serverFolderDetails = serverFolderDetails;
	}
	public int getSourceScriptTypeAt() {
		return sourceScriptTypeAt;
	}
	public void setSourceScriptTypeAt(int sourceScriptTypeAt) {
		this.sourceScriptTypeAt = sourceScriptTypeAt;
	}
	public String getSourceScriptType() {
		return sourceScriptType;
	}
	public void setSourceScriptType(String sourceScriptType) {
		this.sourceScriptType = sourceScriptType;
	}
	public String getSourceServerScript() {
		return sourceServerScript;
	}
	public void setSourceServerScript(String sourceServerScript) {
		this.sourceServerScript = sourceServerScript;
	}
	public int getTargetScriptTypeAt() {
		return targetScriptTypeAt;
	}
	public void setTargetScriptTypeAt(int targetScriptTypeAt) {
		this.targetScriptTypeAt = targetScriptTypeAt;
	}
	public String getTargetScriptType() {
		return targetScriptType;
	}
	public void setTargetScriptType(String targetScriptType) {
		this.targetScriptType = targetScriptType;
	}
	public String getTargetServerScript() {
		return targetServerScript;
	}
	public void setTargetServerScript(String targetServerScript) {
		this.targetServerScript = targetServerScript;
	}
	public int getReadinessScriptTypeAt() {
		return readinessScriptTypeAt;
	}
	public void setReadinessScriptTypeAt(int readinessScriptTypeAt) {
		this.readinessScriptTypeAt = readinessScriptTypeAt;
	}
	public String getReadinessScriptType() {
		return readinessScriptType;
	}
	public void setReadinessScriptType(String readinessScriptType) {
		this.readinessScriptType = readinessScriptType;
	}
	public String getAcquisitionReadinessScripts() {
		return acquisitionReadinessScripts;
	}
	public void setAcquisitionReadinessScripts(String acquisitionReadinessScripts) {
		this.acquisitionReadinessScripts = acquisitionReadinessScripts;
	}
	public int getPreactivityScriptTypeAt() {
		return preactivityScriptTypeAt;
	}
	public void setPreactivityScriptTypeAt(int preactivityScriptTypeAt) {
		this.preactivityScriptTypeAt = preactivityScriptTypeAt;
	}
	public String getPreactivityScriptType() {
		return preactivityScriptType;
	}
	public void setPreactivityScriptType(String preactivityScriptType) {
		this.preactivityScriptType = preactivityScriptType;
	}
	public String getPreactivityScripts() {
		return preactivityScripts;
	}
	public void setPreactivityScripts(String preactivityScripts) {
		this.preactivityScripts = preactivityScripts;
	}
	public String getIntegrityScriptName() {
		return integrityScriptName;
	}
	public void setIntegrityScriptName(String integrityScriptName) {
		this.integrityScriptName = integrityScriptName;
	}
	public int getFrequencyProcessAt() {
		return frequencyProcessAt;
	}
	public void setFrequencyProcessAt(int frequencyProcessAt) {
		this.frequencyProcessAt = frequencyProcessAt;
	}
	public String getFrequencyProcess() {
		return frequencyProcess;
	}
	public void setFrequencyProcess(String frequencyProcess) {
		this.frequencyProcess = frequencyProcess;
	}
	public String getProcessSequence() {
		return processSequence;
	}
	public void setProcessSequence(String processSequence) {
		this.processSequence = processSequence;
	}
	public String getAdfStartTime() {
		return adfStartTime;
	}
	public void setAdfStartTime(String adfStartTime) {
		this.adfStartTime = adfStartTime;
	}
	public String getAdfEndTime() {
		return adfEndTime;
	}
	public void setAdfEndTime(String adfEndTime) {
		this.adfEndTime = adfEndTime;
	}
	public String getAccessPermission() {
		return accessPermission;
	}
	public void setAccessPermission(String accessPermission) {
		this.accessPermission = accessPermission;
	}
	public String getDateLastExtraction() {
		return dateLastExtraction;
	}
	public void setDateLastExtraction(String dateLastExtraction) {
		this.dateLastExtraction = dateLastExtraction;
	}
	public String getDebugMode() {
		return debugMode;
	}
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
	}
	public int getDataAcqStatusNt() {
		return dataAcqStatusNt;
	}
	public void setDataAcqStatusNt(int dataAcqStatusNt) {
		this.dataAcqStatusNt = dataAcqStatusNt;
	}
	public int getDataAcqStatus() {
		return dataAcqStatus;
	}
	public void setDataAcqStatus(int dataAcqStatus) {
		this.dataAcqStatus = dataAcqStatus;
	}
	public String getMandatoryFlag() {
		return mandatoryFlag;
	}
	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
	public String getPopConnectivityType() {
		return popConnectivityType;
	}
	public void setPopConnectivityType(String popConnectivityType) {
		this.popConnectivityType = popConnectivityType;
	}
	public String getPopDbConnectivityType() {
		return popDbConnectivityType;
	}
	public void setPopDbConnectivityType(String popDbConnectivityType) {
		this.popDbConnectivityType = popDbConnectivityType;
	}
	public String getPopDbLink() {
		return popDbLink;
	}
	public void setPopDbLink(String popDbLink) {
		this.popDbLink = popDbLink;
	}
	public String getPopDbVarName() {
		return popDbVarName;
	}
	public void setPopDbVarName(String popDbVarName) {
		this.popDbVarName = popDbVarName;
	}
	public String getPopDbName() {
		return popDbName;
	}
	public void setPopDbName(String popDbName) {
		this.popDbName = popDbName;
	}
	public String getPopDbInstance() {
		return popDbInstance;
	}
	public void setPopDbInstance(String popDbInstance) {
		this.popDbInstance = popDbInstance;
	}
	public String getPopDbIp() {
		return popDbIp;
	}
	public void setPopDbIp(String popDbIp) {
		this.popDbIp = popDbIp;
	}
	public String getAdfStartDay() {
		return adfStartDay;
	}
	public void setAdfStartDay(String adfStartDay) {
		this.adfStartDay = adfStartDay;
	}
	public String getAdfEndDay() {
		return adfEndDay;
	}
	public void setAdfEndDay(String adfEndDay) {
		this.adfEndDay = adfEndDay;
	}
	public int getTemplateControlStatusAt() {
		return templateControlStatusAt;
	}
	public void setTemplateControlStatusAt(int templateControlStatusAt) {
		this.templateControlStatusAt = templateControlStatusAt;
	}
	public String getTemplateControlStatus() {
		return templateControlStatus;
	}
	public void setTemplateControlStatus(String templateControlStatus) {
		this.templateControlStatus = templateControlStatus;
	}
	public String getSubAdfNumber() {
		return subAdfNumber;
	}
	public void setSubAdfNumber(String subAdfNumber) {
		this.subAdfNumber = subAdfNumber;
	}
	public String getMacroName() {
		return macroName;
	}
	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}
	public String getMacroType() {
		return macroType;
	}
	public void setMacroType(String macroType) {
		this.macroType = macroType;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getServerport() {
		return serverport;
	}
	public void setServerport(String serverport) {
		this.serverport = serverport;
	}
	public String getServeruser() {
		return serveruser;
	}
	public void setServeruser(String serveruser) {
		this.serveruser = serveruser;
	}
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getServerpwd() {
		return serverpwd;
	}
	public void setServerpwd(String serverpwd) {
		this.serverpwd = serverpwd;
	}
	public String getDbinstance() {
		return dbinstance;
	}
	public void setDbinstance(String dbinstance) {
		this.dbinstance = dbinstance;
	}
	public String getDbuser() {
		return dbuser;
	}
	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}
	public String getDbip() {
		return dbip;
	}
	public void setDbip(String dbip) {
		this.dbip = dbip;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getDbport() {
		return dbport;
	}
	public void setDbport(String dbport) {
		this.dbport = dbport;
	}
	public String getServerhostname() {
		return serverhostname;
	}
	public void setServerhostname(String serverhostname) {
		this.serverhostname = serverhostname;
	}
	public String getDbpwd() {
		return dbpwd;
	}
	public void setDbpwd(String dbpwd) {
		this.dbpwd = dbpwd;
	}
	public String getServicename() {
		return servicename;
	}
	public void setServicename(String servicename) {
		this.servicename = servicename;
	}
	public String getConnectionString() {
		return connectionString;
	}
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	public String getTagValue() {
		return tagValue;
	}
	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getVarType() {
		return varType;
	}
	public void setVarType(String varType) {
		this.varType = varType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSplCharReplace() {
		return splCharReplace;
	}
	public void setSplCharReplace(String splCharReplace) {
		this.splCharReplace = splCharReplace;
	}
	public int getMandatoryFlagAt() {
		return mandatoryFlagAt;
	}
	public void setMandatoryFlagAt(int mandatoryFlagAt) {
		this.mandatoryFlagAt = mandatoryFlagAt;
	}
	public String getDbPopOperation() {
		return DbPopOperation;
	}
	public void setDbPopOperation(String dbPopOperation) {
		DbPopOperation = dbPopOperation;
	}
	public String getConnetPopOperation() {
		return ConnetPopOperation;
	}
	public void setConnetPopOperation(String connetPopOperation) {
		ConnetPopOperation = connetPopOperation;
	}
	public boolean isAuditDbPop() {
		return isAuditDbPop;
	}
	public void setAuditDbPop(boolean isAuditDbPop) {
		this.isAuditDbPop = isAuditDbPop;
	}
	public String getMasked() {
		return masked;
	}
	public void setMasked(String masked) {
		this.masked = masked;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public String getMajorBuild() {
		return majorBuild;
	}
	public void setMajorBuild(String majorBuild) {
		this.majorBuild = majorBuild;
	}
	public int getRttNextIntervalTypeAt() {
		return rttNextIntervalTypeAt;
	}
	public void setRttNextIntervalTypeAt(int rttNextIntervalTypeAt) {
		this.rttNextIntervalTypeAt = rttNextIntervalTypeAt;
	}
	public String getRttNextIntervalType() {
		return rttNextIntervalType;
	}
	public void setRttNextIntervalType(String rttNextIntervalType) {
		this.rttNextIntervalType = rttNextIntervalType;
	}
	public String getRttEveryXMins() {
		return rttEveryXMins;
	}
	public void setRttEveryXMins(String rttEveryXMins) {
		this.rttEveryXMins = rttEveryXMins;
	}
	public String getBuildRunFlag() {
		return buildRunFlag;
	}
	public void setBuildRunFlag(String buildRunFlag) {
		this.buildRunFlag = buildRunFlag;
	}

}