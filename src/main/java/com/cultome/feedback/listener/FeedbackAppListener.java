package com.cultome.feedback.listener;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.cultome.feedback.dao.AdminDao;
import com.cultome.feedback.dao.PollsDao;
import com.cultome.feedback.manager.AdminManager;

/** 
 * FeedbackAppListener.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	21/07/2015
 */
public class FeedbackAppListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent ctx) {
	}

	@Override
	public void contextInitialized(ServletContextEvent ctx) {
		try {
			new PollsDao().setupPollTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			new AdminDao().setupAdminTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			new AdminManager().createUser("admin", "admin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
