package com.amir.app.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.amir.app.user.User;

/**
 * Simple Token Authentication. If user is null, the object is not authenticated, and 
 * is being used only to pass information to the Provider. @see TokenAuthenticaitonFilter.
 * it can also be used as a JWT Authentication (if you're desperate enough), but please use
 * 3rd party or spring's built-int support to leverage JWT and NOT this class! 
 * */
public class TokenAuthentication implements Authentication{
	
	private String token;
	private User user;
	private boolean authenticated;
	
	public TokenAuthentication(String token,User user) {
		this.token=token;
		this.user=user; // XXX this guy is null if u print it, but if u use it elsewhere it's ok! :|
		this.authenticated=false; // XXX DO NOT forget to setAuthenticated(true) in the provider
	}
	
	@Override	public Object getCredentials() {return token;}
	@Override	public String getName() {return user.getUsername();}
	@Override	public Collection<? extends GrantedAuthority> getAuthorities() {return user.getAuthorities();}
	@Override	public Object getDetails() {return user.toDto();}
	@Override	public Object getPrincipal() {return user;}
	@Override	public boolean isAuthenticated() {return authenticated;	}
	@Override	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {this.authenticated=isAuthenticated;}

}
