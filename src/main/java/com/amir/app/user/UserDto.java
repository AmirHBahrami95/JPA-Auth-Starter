package com.amir.app.user;

import java.util.UUID;

public class UserDto {
	
	private UUID id;
	private String uname;
	private String passw;
	private String fname;
	private String lname;
	private String email;
	private String phoneNo;
	
	public UserDto() {}

	public String getUname() {
		return uname;
	}

	public String getPassw() {
		return passw;
	}

	public String getLname() {
		return lname;
	}

	public String getFname() {
		return fname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public void setPassw(String passw) {
		this.passw = passw;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
}
