package com.vision.vb;

public class TransLineSbuVb extends CommonVb{

	private String BusinessVerticalAT = null;
	private String transLineId = "";
	private String BusinessVertical = "";
	private int transLineSbuStatusNT = 1;
	private int transLineSbuStatus = 0;

	public String getTransLineId() {
		return transLineId;
	}

	public void setTransLineId(String transLineId) {
		this.transLineId = transLineId;
	}

	public String getBusinessVertical() {
		return BusinessVertical;
	}

	public void setBusinessVertical(String businessVertical) {
		BusinessVertical = businessVertical;
	}

	public int getTransLineSbuStatusNT() {
		return transLineSbuStatusNT;
	}

	public void setTransLineSbuStatusNT(int transLineSbuStatusNT) {
		this.transLineSbuStatusNT = transLineSbuStatusNT;
	}

	public int getTransLineSbuStatus() {
		return transLineSbuStatus;
	}

	public void setTransLineSbuStatus(int transLineSbuStatus) {
		this.transLineSbuStatus = transLineSbuStatus;
	}

	public String getBusinessVerticalAT() {
		return BusinessVerticalAT;
	}

	public void setBusinessVerticalAT(String businessVerticalAT) {
		BusinessVerticalAT = businessVerticalAT;
	}
}
