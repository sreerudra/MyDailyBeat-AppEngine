package com.verve.api;

public class BooleanResponse {
	
	String response;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public static BooleanResponse createResponse(boolean r) {
		BooleanResponse b = new BooleanResponse();
		b.response = (r) ? "Operation succeeded" : "Operation Failed";
		return b;
	}
	
	public BooleanResponse() {
		response = "Operation Failed";
	}

}
