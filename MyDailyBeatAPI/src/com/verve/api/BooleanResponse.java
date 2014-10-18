package com.verve.api;

public class BooleanResponse {
	
	String response;
	String message;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public static BooleanResponse createResponse(boolean r) {
		return BooleanResponse.createResponse(r, "");
	}
	
	public static BooleanResponse createResponse(boolean r, String message) {
		BooleanResponse b = new BooleanResponse();
		b.response = (r) ? "Operation succeeded" : "Operation Failed";
		b.message = message;
		return b;
	}
	
	public BooleanResponse() {
		response = "Operation Failed";
		message = "";
	}

}
