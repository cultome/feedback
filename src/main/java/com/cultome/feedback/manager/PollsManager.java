package com.cultome.feedback.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.cultome.feedback.dao.PollsDao;
import com.cultome.feedback.entity.Answer;
import com.cultome.feedback.entity.Poll;
import com.cultome.feedback.exception.FeedbackException;
import com.cultome.feedback.exception.InvalidScriptException;
import com.cultome.feedback.util.Email;
import com.cultome.feedback.util.Parameters;
import com.cultome.feedback.util.SmtpSender;
import com.cultome.feedback.util.Utils;

/** 
 * PollsManager.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class PollsManager extends BaseManager {
	
	private PollsDao dao = new PollsDao();

	public Poll get(Integer pollId) throws SQLException {
		return dao.get(pollId);
	}

	public Poll create(String script) throws SQLException, InvalidScriptException {
		return dao.save(new Poll(script).getScript());
	}

	public Poll update(Integer pollId, String script) throws SQLException, InvalidScriptException {
		return dao.update(pollId, new Poll(script).getScript());
	}

	public List<Poll> list() throws SQLException {
		return dao.list();
	}

	public String saveResponse(String token, Map<String, List<String>> responses) throws SQLException, FeedbackException {
		if(dao.saveResponse(token, responses)){
			return "Answers saved! Thanks!";
		}
		
		throw new FeedbackException("A problem ocurred saving answers. Please try again.");
	}

	public String sendPoll(Integer pollId) throws SQLException, IOException, NoSuchAlgorithmException {
		Poll poll = get(pollId);
		String[] contacts = poll.getContacts();
		
		if(sendMailTo(contacts, poll)){
			dao.sendPoll(pollId);
			return "The poll was send to all the contacts!";
		}
		throw new RuntimeException("A problem ocurred when sending the mail to the contacts");
	}

	public Poll get(String token) throws SQLException {
		Object[] tokenIds = dao.getTokenIds(token);
		return get((Integer) tokenIds[1]);
	}

	public boolean deletePoll(Integer pollId) throws SQLException {
		return dao.deletePoll(pollId);
	}

	private boolean sendMailTo(String[] contacts, Poll poll) throws IOException, NoSuchAlgorithmException, SQLException {
		try {
			String token; 
			SmtpSender client = new SmtpSender(Parameters.getSMTPServer());
			
			Map<String, String> replacement = new HashMap<String, String>();
			replacement.put("emailTitle", poll.getEmailTitle());
			replacement.put("sign", poll.getEmailSign());
			replacement.put("content", poll.getEmailContent());
			
			for (String contact : contacts) {
				do{
					token = Utils.getUrlSafeHash(contact + "-" + poll.getId() + "-" + System.currentTimeMillis() + "-" + Utils.getRandomString(10));
				} while(!dao.createToken(contact, poll.getId(), token));
				replacement.put("url", Parameters.getFeedbackBaseUrl() + "/#?t=" + token);
				System.out.println("[*] Mail to [contact: " + contact + ", pollId: " + poll.getId() + ", token: " + token + "]");
				client.sendEmail(createEmail(contact, poll, replacement));
			}
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Email createEmail(String contact, Poll poll, Map<String, String> replacement) throws IOException {
		HashSet<String> recepient = new HashSet<String>();
		recepient.add(contact);
		
		Email email = new Email();
		email.setFrom(Parameters.getSentFromEmail());
		email.setSubject("[Feedback Request] " + (poll.getEmailSubject() == null ? poll.getTitle() : poll.getEmailSubject()));
		email.setRecipients(recepient);
		email.setBody(createEmailBody(replacement));
		return email;
	}

	private String createEmailBody(Map<String, String> replacement) throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/mail.html");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder b = new StringBuilder();
		
		while((line = br.readLine()) != null){
			b.append(replaceTemplate(line, replacement));
		}
		
		return b.toString();
	}

	private String replaceTemplate(String line, Map<String, String> replacement) {
		if(line.indexOf("{{emailTitle}}") >= 0){
			line = line.replaceFirst("\\{\\{emailTitle}}", replacement.get("emailTitle") != null ? replacement.get("emailTitle") : "In IT we appreciate your feedback");
		} else if(line.indexOf("{{sign}}") >= 0){
			line = line.replaceFirst("\\{\\{sign}}", replacement.get("sign") != null ? replacement.get("sign") : "LAC IT Department");
		} else if(line.indexOf("{{content}}") >= 0){
			String defaultContent = "You are receiving this mail because we provided you service recently and as part " + 
			"of our efforts to improve our service, we would like you to help us answering a brief poll about it.<br><br>" + 
			"You can access the poll in the following link<br><br>" + 
			"<<TAKE ME TO THE POLL!>><br><br>" +
			"We want to thank you in advance for your time.<br><br>" + 
			"Regards,<br><br>";
			
			String emailContent = replacement.get("content") != null ? replacement.get("content") : defaultContent;
			String contentWithButton = replaceButton(emailContent, replacement.get("url"));
			String withHtmlLines = contentWithButton.replaceAll("\n", "<br>");
			
			line = line.replaceFirst("\\{\\{content}}", withHtmlLines);
		}
		return line;
	}

	private String replaceButton(String emailContent, String url) {
		String text = "TAKE ME TO THE POLL!";
		
		int leftIdx = emailContent.indexOf("<<");
		if(leftIdx >= 0){
			int rightIdx = emailContent.indexOf(">>", leftIdx);
			text = emailContent.substring(leftIdx+2, rightIdx);	
		}
		String button = "<a href=\"" + url + "\"><span style=\"color: #0070C0\"><b>" + text + "</b></span></a>";
		
		if(leftIdx >= 0){
			emailContent = emailContent.replaceFirst("<<[^>]+>>", button);
		} else {
			emailContent += "<br><br>" + button + "<br>";
		}
		
		
		return emailContent;
	}

	public Poll getAnswers(Integer pollId) throws SQLException {
		Poll poll = get(pollId);
		List<Answer> answers = dao.getAnswers(pollId);
		poll.setAnswers(answers.toArray(new Answer[]{}));
		return poll;
	}

}
