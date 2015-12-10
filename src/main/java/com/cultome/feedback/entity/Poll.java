package com.cultome.feedback.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlRootElement;

import com.cultome.feedback.exception.InvalidScriptException;

/** 
 * Poll.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
@XmlRootElement
public class Poll implements Serializable {

	private static final long serialVersionUID = 29922231467955406L;

	private Integer id;
	private String script;
	private Date updated;
	private Date sent;

	// Calculados
	private String title;
	private Question[] questions;
	private String[] contacts;
	private Answer[] answers;
	private String emailSubject;
	private String emailSign;
	private String emailContent;
	private String emailTitle;

	public Poll() {
	}

	public Poll(String script) throws InvalidScriptException {
		this.script = script;
		fill(script, this);
	}

	public Poll(Integer id, String script, Date updated, Date sent) throws InvalidScriptException {
		this(script);
		this.id = id;
		this.updated = updated;
		this.sent = sent;
	}

	public void fill(String script, Poll poll) throws InvalidScriptException {
		Poll scriptPoll = parse(script, true);
		poll.setTitle(scriptPoll.getTitle());
		poll.setQuestions(scriptPoll.getQuestions());
		poll.setContacts(scriptPoll.getContacts());
		poll.setEmailContent(scriptPoll.getEmailContent());
		poll.setEmailSign(scriptPoll.getEmailSign());
		poll.setEmailSubject(scriptPoll.getEmailSubject());
		poll.setEmailTitle(scriptPoll.getEmailTitle());
	}

	public Poll parse(String script, boolean throwErrors) throws InvalidScriptException {
		if (script == null || script.isEmpty()) {
			throw new InvalidScriptException("Script is empty!");
		}

		Poll poll = new Poll();
		Matcher matcher;
		List<Question> questions = new ArrayList<Question>();
		List<String> contacts = new ArrayList<String>();
		List<Configuration> configs = new ArrayList<Configuration>();
		List<Option> options = new ArrayList<Option>();
		Question currentQuestion = null;
		String[] lines = script.split("\n");
		String line;

		for (int i = 0; i < lines.length; i++) {
			line = lines[i];
			
			if (line.matches("^[\\s]*\\{[\\s]*[Qq]uestion.*")) {
				if (currentQuestion != null) {
					currentQuestion.setConfig(configs.toArray(new Configuration[] {}));
					currentQuestion.setOptions(options.toArray(new Option[] {}));

					questions.add(currentQuestion);

					configs = new ArrayList<Configuration>();
					options = new ArrayList<Option>();
				}

				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Qq]uestion[\\s]*([^}]*)\\}[\\s]*(.*)$").matcher(line);
				if (!matcher.find()) {
					throw new InvalidScriptException("Invalid Question directive [" + line + "]");
				}

				currentQuestion = new Question(matcher.group(1), matcher.group(2));

			} else if (line.matches("^[\\s]*\\{[\\s]*[Ss]end[Tt]o.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Ss]end[Tt]o[\\s]*(.+)\\}.*?$").matcher(line);
				if (!matcher.find()) {
					throw new InvalidScriptException("Invalid SendTo directive [" + line + "]");
				}

				String emails = matcher.group(1);
				String[] split = emails.split(",");
				for (String email : split) {
					contacts.add(email.trim());
				}

			} else if (line.matches("^[\\s]*\\{[\\s]*[Cc]onfig.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Cc]onfig[\\s]*([\\S]*)[\\s]*=>[\\s]*([^}]+)[\\s]*\\}.*$").matcher(line);
				if (!matcher.find()) {
					throw new InvalidScriptException("Invalid Config directive [" + line + "]");
				}

				configs.add(new Configuration(matcher.group(1), matcher.group(2)));

			} else if (line.matches("^[\\s]*(\\(.*?\\)|\\[.*?\\]|[_]+|\\->).*")) {
				if(currentQuestion == null){
					throw new InvalidScriptException("Before options you need to define a question. Add one with '{Question} Question text?'");
				}
				matcher = Pattern.compile("^[\\s]*(\\(.*?\\)|\\[.*?\\]|[_]+|\\->)[\\s]*(.*)$").matcher(line);
				if (!matcher.find()) {
					throw new InvalidScriptException("Invalid Option directive [" + line + "]");
				}

				String inputType = matcher.group(1);
				if (inputType.matches("\\([\\s]*\\)")) {
					currentQuestion.setOptionsType("radio");
				} else if (inputType.matches("\\[[\\s]*\\]")) {
					currentQuestion.setOptionsType("checkbox");
				} else if (inputType.matches("[_]+")) {
					currentQuestion.setOptionsType("textarea");
				} else if (inputType.matches("->")) {
					currentQuestion.setOptionsType("combo");
				}
				options.add(new Option(matcher.group(2)));
				
			} else if (line.matches("^[\\s]*\\{[\\s]*[Tt]itle.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Tt]itle[\\s]*([^}]*)\\}[\\s]*(.*)$").matcher(line);
				if (matcher.find()) {
					poll.setTitle(matcher.group(2));
				}
				
			} else if (line.matches("^[\\s]*\\{[\\s]*[Ee]mail[Ss]ubject.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Ee]mail[Ss]ubject[\\s]*([^}]*)\\}[\\s]*(.*)$").matcher(line);
				if (matcher.find()) {
					poll.setEmailSubject(matcher.group(2));
				}
				
			} else if (line.matches("^[\\s]*\\{[\\s]*[Ee]mail[Cc]ontent.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Ee]mail[Cc]ontent[\\s]*([^}]*)\\}[\\s]*(.*)$").matcher(line);
				if (matcher.find()) {
					poll.setEmailContent(matcher.group(2));
				}
				
				while(i+1 < lines.length && !lines[i+1].matches("^[\\s]*\\{.*")){
					i++;
					if("".equals(poll.getEmailContent())){
						poll.setEmailContent(lines[i].trim());
					} else {
						poll.setEmailContent(poll.getEmailContent() + "\n" + lines[i].trim());
					}
				}
				
			} else if (line.matches("^[\\s]*\\{[\\s]*[Ee]mail[Ss]ign.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Ee]mail[Ss]ign[\\s]*([^}]*)\\}[\\s]*(.*)$").matcher(line);
				if (matcher.find()) {
					poll.setEmailSign(matcher.group(2));
				}
				
			} else if (line.matches("^[\\s]*\\{[\\s]*[Ee]mail[Tt]itle.*")) {
				matcher = Pattern.compile("^[\\s]*\\{[\\s]*[Ee]mail[Tt]itle[\\s]*([^}]*)\\}[\\s]*(.*)$").matcher(line);
				if (matcher.find()) {
					poll.setEmailTitle(matcher.group(2));
				}
				
			}
		}

		if (currentQuestion == null) {
			throw new InvalidScriptException("You need to add a few questions. Add one with '{Question} Question text?'");
		}
		currentQuestion.setConfig(configs.toArray(new Configuration[] {}));
		currentQuestion.setOptions(options.toArray(new Option[] {}));
		questions.add(currentQuestion);

		if (contacts.isEmpty() && throwErrors) {
			throw new InvalidScriptException("You should declare some contacts for this poll to be send. Add them with '{SendTo contact1@example.com, contact2@example.com}'");
		}

		if ((poll.getTitle() == null || poll.getTitle().isEmpty()) && throwErrors) {
			throw new InvalidScriptException("You should put a title for this poll. Add one with '{Title} This is my Poll name'");
		}

		int i = 1;
		for (Question q : questions) {
			if ((q.getQuestion() == null || q.getQuestion().isEmpty()) && throwErrors) {
				throw new InvalidScriptException("The question #" + i + " dont have a question text. Add it with '{Question} This is my question text'.");
			}

			if ((q.getOptions() == null || q.getOptions().length == 0) && throwErrors) {
				throw new InvalidScriptException("The question #" + i + " dont have options. Add it with '()|[]|__ This is my option text.'.");
			}
		}

		poll.setContacts(contacts.toArray(new String[] {}));
		poll.setQuestions(questions.toArray(new Question[] {}));

		return poll;
	}

	public Integer getId() {
		return id;
	}

	public String getScript() {
		return script;
	}

	public String getTitle() {
		return title;
	}

	public Question[] getQuestions() {
		return questions;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setQuestions(Question[] questions) {
		this.questions = questions;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Date getSent() {
		return sent;
	}

	public void setSent(Date sent) {
		this.sent = sent;
	}

	public String[] getContacts() {
		return contacts;
	}

	public void setContacts(String[] contacts) {
		this.contacts = contacts;
	}

	public Answer[] getAnswers() {
		return answers;
	}

	public void setAnswers(Answer[] answers) {
		this.answers = answers;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public String getEmailSign() {
		return emailSign;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public void setEmailSign(String emailSign) {
		this.emailSign = emailSign;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public String getEmailTitle() {
		return emailTitle;
	}

	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}
	
	@Override
	public String toString() {
		return "Poll [id=" + id + ", script=" + script + ", updated=" + updated + ", sent=" + sent + "]";
	}
}
