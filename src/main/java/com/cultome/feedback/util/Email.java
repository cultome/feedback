package com.cultome.feedback.util;

import java.util.Set;

/** 
 * Email.java
 *
 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	Aug 26, 2014
 */
public class Email {
	
	private String from;
	private Set<String> recipients;
	private String subject; 
	private String body;
	
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public Set<String> getRecipients() {
		return recipients;
	}
	public void setRecipients(Set<String> recipients) {
		this.recipients = recipients;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
		
}
