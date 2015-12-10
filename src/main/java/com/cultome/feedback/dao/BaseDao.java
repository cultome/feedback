package com.cultome.feedback.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cultome.feedback.util.Parameters;

/** 
 * BaseDao.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	31/08/2015
 */
public class BaseDao {
	
	public interface DBLogic<F> {
		F execute(Connection c) throws SQLException;
	}
	
	public interface RowMapper<E> {
		E map(ResultSet rs) throws SQLException;
	}

	protected final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public void dropPollTables() throws SQLException {
		try {
			update("DROP TABLE POLLS");
		} catch (Exception e) {}
		
		try {
			update("DROP TABLE ANSWERS");
		} catch (Exception e) {}
		
		try {
			update("DROP TABLE TOKENS");
		} catch (Exception e) {}
	}
	
	public void dropAdminTables() throws SQLException {
		try {
			update("DROP TABLE USERS");
		} catch (Exception e) {}
	}
	
	public void setupAdminTables() throws SQLException {
		try{
			update("CREATE TABLE USERS (" +
					"USERNAME		TEXT PRIMARY KEY UNIQUE NOT NULL," +
					"PASSWORD		TEXT NOT NULL," +
					"SALT			TEXT NOT NULL," +
					"EXPIRES		DATETIME NOT NULL," +
					"TOKEN			TEXT NOT NULL)");
		} catch(Exception e){}
	}

	public void setupPollTables() throws SQLException {
		try{
			update("CREATE TABLE POLLS (" +
					"ID 		INTEGER PRIMARY KEY AUTOINCREMENT," + 
					"SCRIPT		TEXT	NOT NULL, " +
					"UPDATED	DATETIME, " + 
					"SENT		DATETIME)");
		} catch(Exception e){}

		try{
			update("CREATE TABLE ANSWERS (" +
					"QUESTION_IDX	INTEGER	NOT NULL," +
					"ANSWER			TEXT NOT NULL," +
					"TOKEN			TEXT NOT NULL," +
					"TX_ID			INTEGER NOT NULL," +
					"SENT			DATETIME NOT NULL)");
		} catch(Exception e){}
		
		try{
			update("CREATE TABLE TOKENS (" +
					"TOKEN			TEXT PRIMARY KEY NOT NULL," +
					"POLL_ID		INTEGER NOT NULL," +
					"EMAIL			TEXT	NOT NULL)");
		} catch(Exception e){}
	}
	
	protected <E> List<E> query(final String query, final RowMapper<E> rowMapper, final Object... parameters ) throws SQLException {
		return withConnection(new DBLogic<List<E>>() {
			@Override public List<E> execute(Connection c) throws SQLException {
				Statement stmt = c.createStatement();
				String preparedQuery = replaceQueryParams(query, parameters);
				ResultSet rs = stmt.executeQuery(preparedQuery);
				List<E> results = new ArrayList<E>();
				while(rs.next()){
					results.add(rowMapper.map(rs));
				}
				
				return results;
			}
		});
	}
	
	protected void update(final String sqlStatement, final Object...parameters) throws SQLException{
		withConnection(new DBLogic<Boolean>() {
			@Override public Boolean execute(Connection c) throws SQLException {
				Statement stmt = c.createStatement();
				String sqlPrepared = replaceQueryParams(sqlStatement, parameters);
				stmt.executeUpdate(sqlPrepared);
				stmt.close();
				return true;
			}
		});
	}

	private String replaceQueryParams(String query, Object[] parameters) {
		String preparedQuery = query;
		for (Object obj : parameters) {
			preparedQuery = preparedQuery.replaceFirst("~", getQueryParam(obj));
		}
		return preparedQuery;
	}

	private String getQueryParam(Object obj) {
		if(obj instanceof String){
			return "'" + obj.toString() + "'";
		} else if(obj instanceof Integer){
			return ((Integer) obj).toString();
		} else if(obj instanceof Long){
			return ((Long) obj).toString();
		} else if(obj instanceof Date){
			return "'" + dateFormatter.format((Date) obj) + ".000'";
		}
		return "NULL";
	}
	
	private <E> E withConnection(DBLogic<E> logic) throws SQLException{
		Connection c = null;
		try {
			c = getConnection();
			return logic.execute(c);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(c != null){
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection(Parameters.getConnectionString());
	}
}
