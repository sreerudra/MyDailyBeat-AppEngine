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
