package com.amir.app.user;

import java.util.Collection;
import java.util.List;
// import java.util.UUID;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="usr")
public class User implements UserDetails{
	
	public static User of(UserDto ud) {
		User u=new User();
		u.setUname(ud.getUname());
		u.setPassw(ud.getPassw());
		u.setLname(ud.getLname());
		u.setFname(ud.getFname());
		u.setEmail(ud.getEmail());
		u.setPhoneNo(ud.getPhoneNo());
		return u;
	}
	
	@Id	@GeneratedValue(strategy = GenerationType.UUID)	
	private UUID uid; // saved as a uuid string
	private String passw;
	
	@Nullable
	private String email;
	
	@Nullable
	private String phoneNo;
	
	@Column(unique=true)
	private String uname;
	private String lname;
	private String fname;
	
	@Enumerated(EnumType.STRING)
	private UserRole role=UserRole.USER;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority ga=new SimpleGrantedAuthority(role.getRole());
		return List.of(ga);
	}

	@Override
	public String getPassword() {
		return passw;
	}

	@Override
	public String getUsername() {
		return uid.toString();
	}
	
	public UserDto toDto() {
		UserDto u=new UserDto();
		u.setId(this.uid);
		u.setUname(this.uname);
		u.setPassw(null);
		u.setLname(this.lname);
		u.setFname(this.fname);
		u.setEmail(this.email);
		u.setPhoneNo(this.phoneNo);
		return u;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getUname() {
		return uname;
	}

	public String getLname() {
		return lname;
	}

	public String getFname() {
		return fname;
	}

	public String getRole() {
		return role.getRole();
	}

	public void setPassw(String passw) {
		this.passw = passw;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public String getPassw() {
		return passw;
	}

}
