package com.vision.vb;

public class TableColumnsVb extends CommonVb{
	
	private String tableName = "";
	private String colSeq = "";
	private String primaryKey="N";
	private String colName = "";
	private String colDesc = "";
	private int manFlagNt = 1013;
	private int manFlag = -1;
	private String valRuleId = "";
	private String macMap = "";
	private String hidMacMap = "";
	private String lookUpTable = "";
	private int lookUpDataLoadAt = 1088;
	private String lookUpDataLoad = "-1";
	private String defValue = "";
	private String inValValue = "";
	private int	tableColumnStatusNt  =  0;
	private int	tableColumnStatus = -1;
	private boolean newRec = false;
	private String ColumnList = "";
	private String errorCode ="";
	private String errorCodeDesc ="";
	private String errorCodeOverride ="N";
	private String recordStatusMappingFlag ="N";
	private String OverrideInvalidValue ="N";
	private String valRuleIdDesc ="";
	private String valueTrimFlag ="";
	private String effectiveDate ="";
	private int valueTrimAt = 1183;
	private String valueTrim = "-1";
	private String enableAudit = "N";
	
	public String getEnableAudit() {
		return enableAudit;
	}
	public void setEnableAudit(String enableAudit) {
		this.enableAudit = enableAudit;
	}
	public String getValueTrim() {
		return valueTrim;
	}
	public void setValueTrim(String valueTrim) {
		this.valueTrim = valueTrim;
	}
	public int getValueTrimAt() {
		return valueTrimAt;
	}
	public void setValueTrimAt(int valueTrimAt) {
		this.valueTrimAt = valueTrimAt;
	}
	public String getValueTrimFlag() {
		return valueTrimFlag;
	}
	public void setValueTrimFlag(String valueTrimFlag) {
		this.valueTrimFlag = valueTrimFlag;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getColDesc() {
		return colDesc;
	}
	public void setColDesc(String colDesc) {
		this.colDesc = colDesc;
	}
	public int getManFlagNt() {
		return manFlagNt;
	}
	public void setManFlagNt(int manFlagNt) {
		this.manFlagNt = manFlagNt;
	}
	public int getManFlag() {
		return manFlag;
	}
	public void setManFlag(int manFlag) {
		this.manFlag = manFlag;
	}
	public String getValRuleId() {
		return valRuleId;
	}
	public void setValRuleId(String valRuleId) {
		this.valRuleId = valRuleId;
	}
	public String getMacMap() {
		return macMap;
	}
	public void setMacMap(String macMap) {
		this.macMap = macMap;
	}
	public String getHidMacMap() {
		return hidMacMap;
	}
	public void setHidMacMap(String hidMacMap) {
		this.hidMacMap = hidMacMap;
	}
	public String getLookUpTable() {
		return lookUpTable;
	}
	public void setLookUpTable(String lookUpTable) {
		this.lookUpTable = lookUpTable;
	}
	public int getLookUpDataLoadAt() {
		return lookUpDataLoadAt;
	}
	public void setLookUpDataLoadAt(int lookUpDataLoadAt) {
		this.lookUpDataLoadAt = lookUpDataLoadAt;
	}
	public String getLookUpDataLoad() {
		return lookUpDataLoad;
	}
	public void setLookUpDataLoad(String lookUpDataLoad) {
		this.lookUpDataLoad = lookUpDataLoad;
	}
	public String getDefValue() {
		return defValue;
	}
	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}
	public String getInValValue() {
		return inValValue;
	}
	public void setInValValue(String inValValue) {
		this.inValValue = inValValue;
	}
	public int getTableColumnStatusNt() {
		return tableColumnStatusNt;
	}
	public void setTableColumnStatusNt(int tableColumnStatusNt) {
		this.tableColumnStatusNt = tableColumnStatusNt;
	}
	public int getTableColumnStatus() {
		return tableColumnStatus;
	}
	public void setTableColumnStatus(int tableColumnStatus) {
		this.tableColumnStatus = tableColumnStatus;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColSeq() {
		return colSeq;
	}
	public void setColSeq(String colSeq) {
		this.colSeq = colSeq;
	}
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public boolean isNewRec() {
		return newRec;
	}
	public void setNewRec(boolean newRec) {
		this.newRec = newRec;
	}
	public String getColumnList() {
		return ColumnList;
	}
	public void setColumnList(String columnList) {
		ColumnList = columnList;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorCodeDesc() {
		return errorCodeDesc;
	}
	public void setErrorCodeDesc(String errorCodeDesc) {
		this.errorCodeDesc = errorCodeDesc;
	}
	public String getErrorCodeOverride() {
		return errorCodeOverride;
	}
	public void setErrorCodeOverride(String errorCodeOverride) {
		this.errorCodeOverride = errorCodeOverride;
	}
	public String getRecordStatusMappingFlag() {
		return recordStatusMappingFlag;
	}
	public void setRecordStatusMappingFlag(String recordStatusMappingFlag) {
		this.recordStatusMappingFlag = recordStatusMappingFlag;
	}
	public String getOverrideInvalidValue() {
		return OverrideInvalidValue;
	}
	public void setOverrideInvalidValue(String overrideInvalidValue) {
		OverrideInvalidValue = overrideInvalidValue;
	}
	public String getValRuleIdDesc() {
		return valRuleIdDesc;
	}
	public void setValRuleIdDesc(String valRuleIdDesc) {
		this.valRuleIdDesc = valRuleIdDesc;
	}
}
