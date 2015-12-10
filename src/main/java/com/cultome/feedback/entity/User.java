package com.cultome.feedback.entity;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/** 
 * User.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	31/08/2015
 */
@XmlRootElement
public class User implements Serializable {
	
	private static final long serialVersionUID = 429850267491786833L;
	
	private String username;
	private String token;
	private String password;
	private String salt;
	private Date expires;

	public User() {
	}

	public User(String username, String password, String salt, Date expires, String token) {
		this.username = username;
		this.password = password;
		this.salt = salt;
		this.expires = expires;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSalt() {
		return salt;
	}

	public Date getExpires() {
		return expires;
	}

	public String getToken() {
		return token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", expires=" + expires + ", token=" + token + "]";
	}

}
