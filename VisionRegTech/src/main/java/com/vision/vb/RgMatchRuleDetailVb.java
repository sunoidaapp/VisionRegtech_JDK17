package com.vision.vb;

public class RgMatchRuleDetailVb extends CommonVb{

	private String ruleId = "" ;
	private int sequence = 0 ;
	private String tableName = "";
	private String columnName = "";
	private String operator = "";
	private float score = 0f ;
	private String filterCondition = "";
	private String conditionValue1 = "";
	private String conditionValue2 = "";
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public String getFilterCondition() {
		return filterCondition;
	}
	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}
	public String getConditionValue1() {
		return conditionValue1;
	}
	public void setConditionValue1(String conditionValue1) {
		this.conditionValue1 = conditionValue1;
	}
	public String getConditionValue2() {
		return conditionValue2;
	}
	public void setConditionValue2(String conditionValue2) {
		this.conditionValue2 = conditionValue2;
	}
}