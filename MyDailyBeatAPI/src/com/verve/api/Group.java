package com.verve.api;

public class Group {
	
	public String groupName;
	public String adminScreenName;
	public int id;
	
	public static int ID_START = 1;

	public Group() {
	}

	public Group(String groupName, String adminScreenName) {
		id = ID_START++;
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
	}

	public Group(String groupName, String adminScreenName, int id) {
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
		this.id = id;
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

}
