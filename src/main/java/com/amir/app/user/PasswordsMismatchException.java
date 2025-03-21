package com.amir.app.user;

public class PasswordsMismatchException extends Exception{
	
	public PasswordsMismatchException() {
		super("Passwords Mismatch");
	}
	
	public PasswordsMismatchException(String msg) {
		super(msg);
	}
	
	public PasswordsMismatchException(Exception e) {
		super(e);
	}
}
