package com.vision.vb;

public class RgSourceTableMappings {
	private String TableName = "";
	private String SourceTable = "";
	private String TableAlias = "";
	private String GenericCondition = "";
	private int TableStatusNt = 0;
	private int TableStatus = 0;
	private String columnName = "";
	
	public String getTableName() {
		return TableName;
	}
	public void setTableName(String tableName) {
		TableName = tableName;
	}
	public String getSourceTable() {
		return SourceTable;
	}
	public void setSourceTable(String sourceTable) {
		SourceTable = sourceTable;
	}
	public String getTableAlias() {
		return TableAlias;
	}
	public void setTableAlias(String tableAlias) {
		TableAlias = tableAlias;
	}
	public String getGenericCondition() {
		return GenericCondition;
	}
	public void setGenericCondition(String genericCondition) {
		GenericCondition = genericCondition;
	}
	public int getTableStatusNt() {
		return TableStatusNt;
	}
	public void setTableStatusNt(int tableStatusNt) {
		TableStatusNt = tableStatusNt;
	}
	public int getTableStatus() {
		return TableStatus;
	}
	public void setTableStatus(int tableStatus) {
		TableStatus = tableStatus;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
