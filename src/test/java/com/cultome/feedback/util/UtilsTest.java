package com.cultome.feedback.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cultome.feedback.util.Utils;

/** 
 * UtilsTest.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	31/08/2015
 */
public class UtilsTest {

	@Test
	public final void testGetHash() throws Exception {
		String hash1 = Utils.getHash("Carlos");
		String hash2 = Utils.getHash("Carlos");
		String hash3 = Utils.getHash("carlos");
		assertEquals(hash1, hash2);
		assertNotEquals(hash1, hash3);
	}

	@Test
	public final void testGetRandomString() {
		assertEquals(1, Utils.getRandomString(1).length());
		assertEquals(2, Utils.getRandomString(2).length());
		assertEquals(5, Utils.getRandomString(5).length());
		assertEquals(10, Utils.getRandomString(10).length());
		assertEquals(40, Utils.getRandomString(40).length());
	}

}
