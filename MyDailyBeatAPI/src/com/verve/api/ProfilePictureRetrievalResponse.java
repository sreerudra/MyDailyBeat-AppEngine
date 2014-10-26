package com.verve.api;

public class ProfilePictureRetrievalResponse {
	
	public String screenName, blobKey, servingURL;

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public ProfilePictureRetrievalResponse() {
	}

	public String getBlobKey() {
		return blobKey;
	}

	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}

	public String getServingURL() {
		return servingURL;
	}

	public void setServingURL(String servingURL) {
		this.servingURL = servingURL;
	}
}
