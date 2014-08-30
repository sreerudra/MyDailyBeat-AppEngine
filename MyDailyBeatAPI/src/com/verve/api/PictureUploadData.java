package com.verve.api;

public class PictureUploadData {
	
	public String screenName, password, blobKey, servingURL;

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public PictureUploadData() {
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
