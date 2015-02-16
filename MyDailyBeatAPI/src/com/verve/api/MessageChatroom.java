package com.verve.api;

import java.util.ArrayList;
import java.util.List;

public class MessageChatroom {
	
	public int CHATROOM_ID;
	public List<String> screenNames;
	public List<Long> messages;
	public static int CHATROOM_ID_START = 1;
	public static int MAX_MESSAGE_ID = 1;
	
	public List<String> getScreenNames() {
		return screenNames;
	}
	public void setScreenNames(List<String> screenNames) {
		this.screenNames = screenNames;
	}
	public List<Long> getMessages() {
		return messages;
	}
	public void setMessages(List<Long> messages) {
		this.messages = messages;
	}
	public MessageChatroom() {
	}
	
	public MessageChatroom(String screenName1, String screenName2) {
		screenNames = new ArrayList<String>();
		messages = new ArrayList<Long>();
		screenNames.add(screenName1);
		screenNames.add(screenName2);
		CHATROOM_ID = CHATROOM_ID_START++;
	}

}
