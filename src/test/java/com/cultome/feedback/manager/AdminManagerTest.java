package com.cultome.feedback.manager;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.cultome.feedback.dao.AdminDao;
import com.cultome.feedback.entity.AccessGranted;
import com.cultome.feedback.entity.User;
import com.cultome.feedback.manager.AdminManager;

/** 
 * AdminManagerTest.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	25/08/2015
 */
public class AdminManagerTest {
	
	private AdminManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new AdminManager();
		AdminDao dao = new AdminDao();
		try{
			dao.dropAdminTables();
			dao.setupAdminTables();
		} catch(Exception e){}
		
	}
	
	@Test
	public final void testLogin() throws Exception {
		manager.createUser("5056186", "soria");
		AccessGranted auth1 = manager.login("5056186", "soria");
		assertNotNull(auth1);
		assertNotNull(auth1.getToken());
		assertNotNull(auth1.getUsername());
		
		AccessGranted auth2 = manager.login("5056186", "soria");
		assertNotNull(auth2);
		assertNotNull(auth2.getToken());
		assertNotNull(auth2.getUsername());
		
		assertNotEquals(auth1.getToken(), auth2.getToken());
	}

	@Test
	public final void testFailedLogin() {
		try {
			manager.createUser("5056186", "soria");
		} catch (Exception e1) {
			fail(e1.getMessage());
		}
		
		try {
			manager.login("5056186", "wrong");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			manager.login("unknow", "soria");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			manager.login("unknow", "wrong");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	public final void testCreateUser() throws Exception {
		User u = manager.createUser("5056186", "soria");
		assertNotNull(u);
		assertNotNull(u.getExpires());
		assertNotNull(u.getPassword());
		assertNotNull(u.getSalt());
		assertNotNull(u.getToken());
		assertNotNull(u.getUsername());
	}

}
