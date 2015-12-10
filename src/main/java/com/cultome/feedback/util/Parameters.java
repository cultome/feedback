package com.cultome.feedback.util;

/** 
 * Parameters.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	10/08/2015
 */
public class Parameters {

	public static String getFeedbackServicesUrl(){
		return getFeedbackBaseUrl() + "/r";
	}
	
	public static String getFeedbackBaseUrl(){
		return "http://localhost:8080";
	}
	
	public static String getSentFromEmail(){
		return "no-reply@example.com";
	}

	public static String getSMTPServer() {
		return "smtp.mail.example.com";
	}

	public static String getConnectionString() {
		return "jdbc:sqlite:feedback_server.db";
	}
	
}
