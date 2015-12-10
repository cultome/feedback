package com.cultome.feedback.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.cultome.feedback.entity.User;

/** 
 * AdminDao.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	25/08/2015
 */
public class AdminDao extends BaseDao {
	
	private static final String INSERT_USER = "insert into USERS(USERNAME, PASSWORD, SALT, EXPIRES, TOKEN) values(~,~,~,datetime('now'), '')";
	private static final String GET_USER = "select USERNAME, PASSWORD, SALT, EXPIRES, TOKEN from USERS where username=~";
	private static final String GET_USERBY_TOKEN = "select USERNAME, PASSWORD, SALT, EXPIRES, TOKEN from USERS where token=~";
	private static final String UPDATE_TOKEN = "update USERS set token=~, EXPIRES=~ where username=~";
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override public User map(ResultSet rs) throws SQLException {
			try {
				User user = new User();
				user.setUsername(rs.getString(1));
				user.setPassword(rs.getString(2));
				user.setSalt(rs.getString(3));
				user.setExpires(dateFormatter.parse(rs.getString(4)));
				user.setToken(rs.getString(5));
				return user;
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
	};
	
	public User createUser(String username, String hashedPassword, String salt) throws Exception{
		update(INSERT_USER, username, hashedPassword, salt);
		return getUniqueUser(GET_USER, username);
	}

	public User getUser(String username) throws SQLException {
		return getUniqueUser(GET_USER, username);
	}

	public void updateToken(String token, Date date, String username) throws SQLException {
		update(UPDATE_TOKEN, token, date, username);		
	}
	
	public User getUserByToken(String token) throws SQLException {
		return getUniqueUser(GET_USERBY_TOKEN, token);
	}

	private User getUniqueUser(final String query, final Object... parameters) throws SQLException {
		List<User> users = query(query, userMapper, parameters);
		
		if(users.size() != 1){
			throw new SQLException("Not unique user! [" + users.size() + "]");
		}
		
		return users.get(0);
	}
}
