package com.verve.api;

public class JoinGroupPostObject {
	
	public String groupName;
	public String screenName;
	public String password;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getScreenName() {
		return screenName;
	}
	public JoinGroupPostObject() {
	}
	public JoinGroupPostObject(String groupName, String screenName,
			String password) {
		this.groupName = groupName;
		this.screenName = screenName;
		this.password = password;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
