package com.cultome.feedback.entity;

import java.io.Serializable;

/** 
 * Configuration.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class Configuration implements Serializable {

	private static final long serialVersionUID = 6952957607978827258L;
	
	private int id;
	private String name;
	private String value;
	
	public Configuration() {
	}

	public Configuration(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Configuration [name=" + name + ", value=" + value + "]";
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
