package com.verve.api;

public class FavoritesPostObject {
	
	public String screenName;
	public FlingProfile other;
	
	public FavoritesPostObject() {
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public FlingProfile getOther() {
		return other;
	}
	public void setOther(FlingProfile other) {
		this.other = other;
	}

}
