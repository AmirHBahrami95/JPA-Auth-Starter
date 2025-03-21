package com.amir.app.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name="usr_token")
public class UserToken {
	
	public static final int TOKEN_LENGTH=16;
	
	@Id
	private String usrUid;

	@MapsId	@OneToOne
  @JoinColumn(name = "usr_uid")
	private User usr;
	
	// TODO change to JWT later, so there's no need for a db + redis mess (or just no db mess)
	//  @GeneratedValue(strategy = GenerationType.UUID) // cannot use for non-id >:	
	private String token;
	
	@Version // Add this line for optimistic locking
  private Long version; // or Integer version;
	
	public UserToken() {}

	public User getUsr() {
		return usr;
	}

	public String getToken() {
		return token;
	}

	public void setUsr(User usr) {
		this.usr = usr;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsrUid() {
		return usrUid;
	}

	public void setUsrUid(String usrUid) {
		this.usrUid = usrUid;
	}
}
