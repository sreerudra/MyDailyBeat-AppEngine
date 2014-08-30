package com.verve.api;

import java.util.ArrayList;

public class PrefsList {
	
	public ArrayList<Object> prefs;
	public String screenName, password;

	public PrefsList() {
	}

	public String getScreenName() {
		return screenName;
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

	public ArrayList<Object> getPrefs() {
		return prefs;
	}

	public void setPrefs(ArrayList<Object> prefs) {
		this.prefs = prefs;
	}

}
