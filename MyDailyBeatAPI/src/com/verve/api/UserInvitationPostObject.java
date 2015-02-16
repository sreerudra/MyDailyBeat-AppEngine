package com.verve.api;

public class UserInvitationPostObject {
	public String senderemail;
	public String recipientemail;
	public String inviteMessage;
	public int groupID;
	
	public String getInviteMessage() {
		return inviteMessage;
	}
	public String getSenderemail() {
		return senderemail;
	}
	public void setSenderemail(String senderemail) {
		this.senderemail = senderemail;
	}
	public String getRecipientemail() {
		return recipientemail;
	}
	public void setRecipientemail(String recipientemail) {
		this.recipientemail = recipientemail;
	}
	public void setInviteMessage(String inviteMessage) {
		this.inviteMessage = inviteMessage;
	}
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public UserInvitationPostObject() {
		// TODO Auto-generated constructor stub
	}

}
