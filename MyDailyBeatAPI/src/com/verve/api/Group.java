package com.verve.api;


public class Group {

	public String groupName;
	public String adminScreenName;
	public int id;
	public String blobKey, servingURL;

	public static int ID_START = 0;

	public Group() {
	}

	public Group(String groupName, String adminScreenName) {
		id = ++ID_START;
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
	}

	public Group(String groupName, String adminScreenName, int id,
			String blobKey, String servingURL) {
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
		this.id = id;
		this.blobKey = blobKey;
		this.servingURL = servingURL;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getAdminScreenName() {
		return adminScreenName;
	}

	public void setAdminScreenName(String adminScreenName) {
		this.adminScreenName = adminScreenName;
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
