package com.verve.api;

public class WritePostRequestObject {
	public String postText;
	public String blobKey, servingURL;
	public String userScreenName;
	public long when;
	public int id;
	public WritePostRequestObject() {
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPostText() {
		return postText;
	}
	public void setPostText(String postText) {
		this.postText = postText;
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
	public String getUserScreenName() {
		return userScreenName;
	}
	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}
	public long getWhen() {
		return when;
	}
	public void setWhen(long when) {
		this.when = when;
	}

}
