package com.cultome.feedback.manager;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.cultome.feedback.dao.AdminDao;
import com.cultome.feedback.entity.AccessGranted;
import com.cultome.feedback.entity.User;
import com.cultome.feedback.util.Utils;

/** 
 * AdminManager.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	25/08/2015
 */
public class AdminManager extends BaseManager {
	
	private AdminDao dao = new AdminDao();
	private static final int EXPIRATION_MINS = 60;
	
	public static void main(String[] args) throws Exception {
		new AdminManager().createUser("5056186", "soria");
	}

	public AccessGranted login(String username, String plainPassword) throws Exception {
		User user = dao.getUser(username);
		
		String hashPassword = hashPassword(plainPassword, user.getSalt());
		if(user.getPassword().equals(hashPassword)){
			String token = Utils.getUrlSafeHash(username + "_" + System.currentTimeMillis());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, EXPIRATION_MINS);
			dao.updateToken(token, cal.getTime(), username);
			return new AccessGranted(token, username);
		}
		throw new RuntimeException("Invalid login credentials!");
	}
	
	public User createUser(String username, String plainPassword) throws Exception{
		String salt = getSalt(username);
		String hashedPasswd = hashPassword(plainPassword, salt);
		return dao.createUser(username, hashedPasswd, salt);
	}
	
	public boolean isValidToken(String token) {
		try{
			User user = dao.getUserByToken(token);
			return new Date().compareTo(user.getExpires()) < 0;
		} catch(Exception e){
		}
		return false;
	}

	public User getUser(String username) throws SQLException {
		return dao.getUser(username);
	}
	
	private String hashPassword(String plainPassword, String salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return Utils.getHash(salt + "_" + plainPassword);
	}
	
	private String getSalt(String username) {
		String salt = username.length() + username.toLowerCase() + username.length();
		return salt;
	}
}
