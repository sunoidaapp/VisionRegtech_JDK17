/**
 * 
 */
package com.vision.exception;

import org.springframework.stereotype.Component;

/**
 * @author Kiran-Kumar.Karra
 *
 */
/**
 * @author kiran.karra
 *
 */
@Component
public class ExceptionCode {
 
	private int errorCode = 0;
	private String errorMsg = "";
	private String errorSevr = "";
	private Object otherInfo = null;
	private Object request = null;
	private Object response = null;
	private String actionType = "";
	private Object response1 = null;
	private Object response2 = null;
	private Object response3 = null;
	
	private Object csvPath = null;
	private Object csvFileName = null;
	
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public Object getRequest() {
		return request;
	}
	public void setRequest(Object request) {
		this.request = request;
	}
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getErrorSevr() {
		return errorSevr;
	}
	public void setErrorSevr(String errorSevr) {
		this.errorSevr = errorSevr;
	}
	public Object getOtherInfo() {
		return otherInfo;
	}
	public void setOtherInfo(Object otherInfo) {
		this.otherInfo = otherInfo;
	}
	public Object getResponse1() {
		return response1;
	}
	public void setResponse1(Object response1) {
		this.response1 = response1;
	}
	public Object getResponse2() {
		return response2;
	}
	public void setResponse2(Object response2) {
		this.response2 = response2;
	}
	public Object getResponse3() {
		return response3;
	}
	public void setResponse3(Object response3) {
		this.response3 = response3;
	}
	public Object getCsvPath() {
		return csvPath;
	}
	public void setCsvPath(Object csvPath) {
		this.csvPath = csvPath;
	}
	public Object getCsvFileName() {
		return csvFileName;
	}
	public void setCsvFileName(Object csvFileName) {
		this.csvFileName = csvFileName;
	}
	
	
}
