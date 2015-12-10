package com.cultome.feedback;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.cultome.feedback.dao.PollsDaoTest;
import com.cultome.feedback.entity.PollTest;
import com.cultome.feedback.manager.PollsManagerTest;

/** 
 * AllTests.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	07/08/2015
 */
@RunWith(Suite.class)
@SuiteClasses({
	PollsDaoTest.class,
	PollTest.class,
	PollsManagerTest.class
})
public class AllTests {

}
