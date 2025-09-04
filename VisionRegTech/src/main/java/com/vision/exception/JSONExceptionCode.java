package com.vision.exception;

public class JSONExceptionCode {
 
	private int status = 0;
	private String message = null;
	private Object response = null;
	private Object otherInfo = null;
	private Object response1 = null;
	private Object response2 = null;
	private Object response3 = null;
	
	public JSONExceptionCode() {}
	
	public JSONExceptionCode(int status,String message, Object response, Object otherInfo, Object response1, Object response2, Object response3) {
		this.status = status;
		this.message = message;
		this.response = response;
		this.otherInfo = otherInfo;
		this.response1  = response1;
		this.response2  = response2;
		this.response3  = response3;
	}
	public JSONExceptionCode(int status,String message, Object response, Object otherInfo) {
		this.status = status;
		this.message = message;
		this.response = response;
		this.otherInfo = otherInfo;
	}
	public JSONExceptionCode(int status,String message, Object response,Object response1, Object otherInfo) {
		this.status = status;
		this.message = message;
		this.response = response;
		this.response1  = response1;
		this.otherInfo = otherInfo;
	}
	
	public JSONExceptionCode(int status,String message, Object response) {
		this.status = status;
		this.message = message;
		this.response = response;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
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
		
		 
}