package com.vision.util;

import java.util.ArrayList;
import java.util.List;

public class SampleVb {
	int id = 0;
	int parentId = 0;
	List<SampleVb> childs = new ArrayList<>();
	public List<SampleVb> getChilds() {
		return childs;
	}
	public void setChilds(List<SampleVb> childs) {
		this.childs = childs;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
}
