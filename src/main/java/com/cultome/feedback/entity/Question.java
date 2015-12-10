package com.cultome.feedback.entity;

import java.io.Serializable;
import java.util.Arrays;

/** 
 * Question.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class Question implements Serializable {

	private static final long serialVersionUID = 3637283777025382186L;
	
	private Integer id;
	private String name;
	private String question;
	private String optionsType;
	private Option[] options;
	private Configuration[] config;

	public Question() {
	}
	
	public Question(Integer id, String name, String question, String optionsType, Option[] options, Configuration[] config) {
		this.id = id;
		this.name = name;
		this.question = question;
		this.optionsType = optionsType;
		this.options = options;
		this.config = config;
	}
	
	public Question(String question, String optionsType, Option[] options, Configuration[] config) {
		this(null, null, question, optionsType, options, config);
	}

	public Question(String name, String question) {
		this.name = name;
		this.question = question;
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", name=" + name + ", question=" + question + ", optionsType=" + optionsType + ", options=" + Arrays.toString(options) + ", config=" + Arrays.toString(config) + "]";
	}

	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getOptionsType() {
		return optionsType;
	}

	public Option[] getOptions() {
		return options;
	}
	
	public Configuration[] getConfig() {
		return config;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	

	public void setName(String name) {
		this.name = name;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setOptionsType(String optionsType) {
		this.optionsType = optionsType;
	}
	
	public void setOptions(Option[] options) {
		this.options = options;
	}
	
	public void setConfig(Configuration[] config) {
		this.config = config;
	}
}
