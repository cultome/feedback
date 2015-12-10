package com.cultome.feedback.util;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** 
 * SmtpSender.java
 *
 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	Aug 26, 2014
 */
public class SmtpSender {
	
	private static final String format = "text/html; charset=UTF-8";

	private String host;

	/** Creates a new instance of SmtpSender */
	public SmtpSender(String host) {
		this.host = host;
	}

	public void sendEmail(String[] recipients, String subject, String message, String from) throws MessagingException {
		sendEmail(recipients, subject, message, from, format);
	}

	public void sendEmail(String[] recipients, String subject, String message, String from, String format) throws MessagingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional : You can also set your custom headers in the Email if you Want
		// msg.addHeader("MyHeaderName", "myHeaderValue");

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, format);
		Transport.send(msg);
	}

	public void sendEmail(String recipient, String subject, String message, String from) throws MessagingException {
		String[] recipients = new String[1];
		recipients[0] = recipient;
		sendEmail(recipients, subject, message, from);
	}

	public void sendEmail(Set<String> recipientSet, String subject, String message, String from, String format) throws MessagingException {
		String[] recipients = new String[recipientSet.size()];
		Iterator<String> it = recipientSet.iterator();
		int i = 0;
		while (it.hasNext()) {
			String recipient = it.next();
			recipients[i] = recipient;
			i++;
		}
		sendEmail(recipients, subject, message, from, format);
	}

	public void sendEmail(Set<String> recipientSet, String subject, String message, String from) throws MessagingException {
		sendEmail(recipientSet, subject, message, from, format);
	}

	public void sendEmail(Email email) throws MessagingException {
		Set<String> recipientSet = email.getRecipients();
		String subject = email.getSubject();
		String message = email.getBody();
		String from = email.getFrom();
		sendEmail(recipientSet, subject, message, from, format);
	}

}
