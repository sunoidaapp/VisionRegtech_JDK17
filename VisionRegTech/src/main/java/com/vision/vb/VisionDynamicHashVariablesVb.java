
package com.vision.vb;

public class VisionDynamicHashVariablesVb extends CommonVb {
	private static final long serialVersionUID = 2073522091526813534L;
	private String	variableName = "";
	private int	variableTypeNt =  1056;                 
	private int	variableType =  -1;
	private String	variableTypeDesc =  "";
	private int	scriptTypeAt = 1083;                  
	private String	scriptType =  "";
	private String	scriptTypeDesc =  "";
	private String	variableScript=  "";
	private int	variableStatusNt =  0;
	private int	variableStatus =  -1;
	private String variableStatusDesc =  "";
	private int sortOrder =  -1;
	private String databaseType=  "";
	private String databaseIp= "";
	private String serviceName=  "";
	private String sid=  "";
	private String user=  "";
	private String pwd=  "";
	private String schema=  "";
	private String dbLink=  "";
	private String dbPort=  "";
	private String suffix=  "";
	private String prefix=  "";
	private String dbInstance=  "";
	private String dbName=  "";
	private String serverIP=  "";
	private String serverHostName=  "";
	private String serverUser=  "";
	private String serverPwd=  "";
	private String serverPort=  "";
	private String connectionType = "";
	
	
	private String tagName = "";
	private String tagValue = ""; 
	private String displayName = "";
	private String maskedFlag = "";
	private String encryption = "";
	private String macroName = ""; // MACROVAR_NAME
	private String mandatoryFlag = "N";
	private String connectorScripts = "";
	private int connectorTypeAt = 815;
	private String connectorType = "";
	private int sourceTypeAt =1803;
	private String sourceType = "";
	private String macroType = "";
	private boolean fieldDisable = false;
	private Object[] tagList;
	
	
	
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public int getVariableTypeNt() {
		return variableTypeNt;
	}
	public void setVariableTypeNt(int variableTypeNt) {
		this.variableTypeNt = variableTypeNt;
	}
	public int getVariableType() {
		return variableType;
	}
	public void setVariableType(int variableType) {
		this.variableType = variableType;
	}
	public int getScriptTypeAt() {
		return scriptTypeAt;
	}
	public void setScriptTypeAt(int scriptTypeAt) {
		this.scriptTypeAt = scriptTypeAt;
	}
	public String getScriptType() {
		return scriptType;
	}
	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}
	public int getVariableStatusNt() {
		return variableStatusNt;
	}
	public void setVariableStatusNt(int variableStatusNt) {
		this.variableStatusNt = variableStatusNt;
	}
	public int getVariableStatus() {
		return variableStatus;
	}
	public void setVariableStatus(int variableStatus) {
		this.variableStatus = variableStatus;
	}
	public String getVariableScript() {
		return variableScript;
	}
	public void setVariableScript(String variableScript) {
		this.variableScript = variableScript;
	}
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	public String getDatabaseType() {
		return databaseType;
	}
	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getDbLink() {
		return dbLink;
	}
	public void setDbLink(String dbLink) {
		this.dbLink = dbLink;
	}
	public String getDbPort() {
		return dbPort;
	}
	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}
	public String getDatabaseIp() {
		return databaseIp;
	}
	public void setDatabaseIp(String databaseIp) {
		this.databaseIp = databaseIp;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getDbInstance() {
		return dbInstance;
	}
	public void setDbInstance(String dbInstance) {
		this.dbInstance = dbInstance;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	public String getServerHostName() {
		return serverHostName;
	}
	public void setServerHostName(String serverHostName) {
		this.serverHostName = serverHostName;
	}
	public String getServerUser() {
		return serverUser;
	}
	public void setServerUser(String serverUser) {
		this.serverUser = serverUser;
	}
	public String getServerPwd() {
		return serverPwd;
	}
	public void setServerPwd(String serverPwd) {
		this.serverPwd = serverPwd;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public String getVariableTypeDesc() {
		return variableTypeDesc;
	}
	public void setVariableTypeDesc(String variableTypeDesc) {
		this.variableTypeDesc = variableTypeDesc;
	}
	public String getScriptTypeDesc() {
		return scriptTypeDesc;
	}
	public void setScriptTypeDesc(String scriptTypeDesc) {
		this.scriptTypeDesc = scriptTypeDesc;
	}
	public String getVariableStatusDesc() {
		return variableStatusDesc;
	}
	public void setVariableStatusDesc(String variableStatusDesc) {
		this.variableStatusDesc = variableStatusDesc;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagValue() {
		return tagValue;
	}
	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getMaskedFlag() {
		return maskedFlag;
	}
	public void setMaskedFlag(String maskedFlag) {
		this.maskedFlag = maskedFlag;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public String getMacroName() {
		return macroName;
	}
	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}
	public String getMandatoryFlag() {
		return mandatoryFlag;
	}
	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
	public String getConnectorScripts() {
		return connectorScripts;
	}
	public void setConnectorScripts(String connectorScripts) {
		this.connectorScripts = connectorScripts;
	}
	public int getConnectorTypeAt() {
		return connectorTypeAt;
	}
	public void setConnectorTypeAt(int connectorTypeAt) {
		this.connectorTypeAt = connectorTypeAt;
	}
	public String getConnectorType() {
		return connectorType;
	}
	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType;
	}
	public int getSourceTypeAt() {
		return sourceTypeAt;
	}
	public void setSourceTypeAt(int sourceTypeAt) {
		this.sourceTypeAt = sourceTypeAt;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getMacroType() {
		return macroType;
	}
	public void setMacroType(String macroType) {
		this.macroType = macroType;
	}
	public boolean isFieldDisable() {
		return fieldDisable;
	}
	public void setFieldDisable(boolean fieldDisable) {
		this.fieldDisable = fieldDisable;
	}
	public Object[] getTagList() {
		return tagList;
	}
	public void setTagList(Object[] tagList) {
		this.tagList = tagList;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	
}