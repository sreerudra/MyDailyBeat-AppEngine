package com.verve.api;

public class MessagePostRequest {
	
	public String screenName;
	public String messageText;
	public long dateTimeMillis;
	public int chatID;
	
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	public long getDateTimeMillis() {
		return dateTimeMillis;
	}
	public void setDateTimeMillis(long dateTimeMillis) {
		this.dateTimeMillis = dateTimeMillis;
	}
	public int getChatID() {
		return chatID;
	}
	public void setChatID(int chatID) {
		this.chatID = chatID;
	}
	public MessagePostRequest() {
	}

}
