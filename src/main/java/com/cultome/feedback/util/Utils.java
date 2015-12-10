package com.cultome.feedback.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.sun.jersey.core.util.Base64;

/** 
 * Utils.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	31/08/2015
 */
public class Utils {

	public static String getHash(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest instance = MessageDigest.getInstance("SHA-256");
		byte[] digest = instance.digest(value.getBytes());
		return new String(Base64.encode(digest));
	}

	public static String getRandomString(int lenght) {
		Random rnd = new Random(System.currentTimeMillis());
		String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < lenght; i++){
			b.append(alpha.charAt(rnd.nextInt(alpha.length())));
		}
		return b.toString();
	}

	public static String getUrlSafeHash(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String hashedPass = Utils.getHash(value);
		String urlSafeToken = URLEncoder.encode(hashedPass, "UTF-8");
		String noHtmlEntities = urlSafeToken.replaceAll("%[A-Z0-9]{2}", "0");
		return noHtmlEntities;
	}
}
