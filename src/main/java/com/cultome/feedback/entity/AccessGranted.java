package com.cultome.feedback.entity;

import java.io.Serializable;

/** 
 * AccessGranted.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	25/08/2015
 */
public class AccessGranted implements Serializable {

	private static final long serialVersionUID = 7647861444082666968L;

	private String token;
	private String username;

	public AccessGranted() {
	}

	public AccessGranted(String token, String username) {
		super();
		this.token = token;
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public String getUsername() {
		return username;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "AccessGranted [token=" + token + ", username=" + username + "]";
	}

}
