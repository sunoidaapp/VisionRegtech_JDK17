package com.vision.dao;

import com.vision.vb.CommonVb;

public class CustomerManualColVb extends CommonVb {
	private String columnName;
	private Integer columnStatusNt =1;
	private Integer columnStatus =0 ;
	private String variableName="";
	private String customerId = "";
	private Integer custModStatusNt =1;
	private Integer custModStatus=0;
	private String ColumnValue = "";

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Integer getColumnStatusNt() {
		return columnStatusNt;
	}

	public void setColumnStatusNt(Integer columnStatusNt) {
		this.columnStatusNt = columnStatusNt;
	}

	public Integer getColumnStatus() {
		return columnStatus;
	}

	public void setColumnStatus(Integer columnStatus) {
		this.columnStatus = columnStatus;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Integer getCustModStatusNt() {
		return custModStatusNt;
	}

	public void setCustModStatusNt(Integer custModStatusNt) {
		this.custModStatusNt = custModStatusNt;
	}

	public Integer getCustModStatus() {
		return custModStatus;
	}

	public void setCustModStatus(Integer custModStatus) {
		this.custModStatus = custModStatus;
	}

	public String getColumnValue() {
		return ColumnValue;
	}

	public void setColumnValue(String columnValue) {
		ColumnValue = columnValue;
	}
}
