package com.verve.api;

import java.util.ArrayList;

public class CheckBoxPreference {
	
	public String screenName, password;
	public ArrayList<String> options;
	public ArrayList<Boolean> selected;

	public CheckBoxPreference() {
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<String> options) {
		this.options = options;
	}

	public ArrayList<Boolean> getSelected() {
		return selected;
	}

	public void setSelected(ArrayList<Boolean> selected) {
		this.selected = selected;
	}

}
