package com.verve.api;

public class GroupPictureUploadData {
	
	public int id;
	public String blobKey, servingURL;
	public GroupPictureUploadData() {
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
