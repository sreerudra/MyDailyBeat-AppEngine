package com.verve.api;

import java.util.ArrayList;

public class Group {
	
	public String groupName;
	public String adminScreenName;
	public int id;
	public ArrayList<Post> posts;
	public String blobKey, servingURL;
	
	public static int ID_START = 1;

	public Group() {
	}

	public Group(String groupName, String adminScreenName) {
		id = ID_START++;
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
		posts = new ArrayList<Post>();
	}

	public Group(String groupName, String adminScreenName, int id, ArrayList<Post> posts, String blobKey, String servingURL) {
		this.groupName = groupName;
		this.adminScreenName = adminScreenName;
		this.id = id;
		this.posts = posts;
		this.blobKey = blobKey;
		this.servingURL = servingURL;
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

	public ArrayList<Post> getPosts() {
		return posts;
	}

	public void setPosts(ArrayList<Post> posts) {
		this.posts = posts;
	}

	public String getBlobKey() {
		return blobKey;
	}

	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}

	public String getServingURL() {
		return servingURL;
	}

	public void setServingURL(String servingURL) {
		this.servingURL = servingURL;
	}

}
