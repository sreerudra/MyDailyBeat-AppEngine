package com.verve.api;


public class VerveStandardUser {
	

	public String name, email, password, screenName, mobile, zipcode, birth_month;
	public long birth_year;
	
	public VerveStandardUser() {}
	public VerveStandardUser(String name, String email, String password,
			String screenName, String mobile, String zipcode,
			String birth_month, long birth_year) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.screenName = screenName;
		this.mobile = mobile;
		this.zipcode = zipcode;
		this.birth_month = birth_month;
		this.birth_year = birth_year;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getBirth_month() {
		return birth_month;
	}
	public void setBirth_month(String birth_month) {
		this.birth_month = birth_month;
	}
	public long getBirth_year() {
		return birth_year;
	}
	public void setBirth_year(long birth_year) {
		this.birth_year = birth_year;
	}

	
	

}
