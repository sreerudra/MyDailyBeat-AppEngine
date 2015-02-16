package com.verve.api;

public class VerveMessage {
	
	public int MESSAGE_ID;
	public String screenName;
	public String messageText;
	public long dateTimeMillis;
	public static int MESSAGE_ID_START = 1;
	
	public int getMESSAGE_ID() {
		return MESSAGE_ID;
	}
	public void setMESSAGE_ID(int mESSAGE_ID) {
		MESSAGE_ID = mESSAGE_ID;
	}
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
	public VerveMessage(String screenName, String messageText, long dateTimeMillis) {
		this.screenName = screenName;
		this.messageText = messageText;
		this.dateTimeMillis = dateTimeMillis;
		this.MESSAGE_ID = VerveMessage.MESSAGE_ID_START++;
		MessageChatroom.MAX_MESSAGE_ID = VerveMessage.MESSAGE_ID_START;
	}
	public VerveMessage() {
		// TODO Auto-generated constructor stub
	}

}
