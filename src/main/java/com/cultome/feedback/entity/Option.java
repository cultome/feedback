package com.cultome.feedback.entity;

import java.io.Serializable;

/** 
 * Option.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class Option implements Serializable {

	private static final long serialVersionUID = 2300765699024421617L;
	
	private int id;
	private String label;
	private int value;
	
	public Option() {
	}
	
	public Option(String label) {
		this.label = label;
	}

	public int getId() {
		return id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getValue() {
		return value;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Option [id=" + id + ", label=" + label + ", value=" + value + "]";
	}
}
