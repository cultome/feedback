package com.cultome.feedback.entity;

import java.io.Serializable;
import java.util.Date;

/** 
 * Answer.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	07/08/2015
 */
public class Answer implements Serializable {

	private static final long serialVersionUID = -8609110126024754887L;

	private Integer questionIdx;
	private String answer;
	private Integer txId;
	private Date sent;
	private String email;

	public Answer() {
	}

	public Answer(Integer questionIdx, String answer, String email, Integer txId, Date sent) {
		super();
		this.questionIdx = questionIdx;
		this.answer = answer;
		this.email = email;
		this.txId = txId;
		this.sent = sent;
	}

	public Integer getQuestionIdx() {
		return questionIdx;
	}

	public String getAnswer() {
		return answer;
	}

	public Date getSent() {
		return sent;
	}

	public void setQuestionIdx(Integer questionIdx) {
		this.questionIdx = questionIdx;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public void setSent(Date sent) {
		this.sent = sent;
	}

	public Integer getTxId() {
		return txId;
	}

	public void setTxId(Integer txId) {
		this.txId = txId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Answer [questionIdx=" + questionIdx + ", answer=" + answer + ", email=" + email + ", txId=" + txId + ", sent=" + sent + "]";
	}

}
