package com.verve.api;

public class Group {
	
	public String groupName;
	public String adminScreenName;
	public int id;
	
	public static int ID_START = 1;

	public Group() {
		id = ++ID_START;
	}

	public Group(String groupName, String adminScreenName) {
		this();
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
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
