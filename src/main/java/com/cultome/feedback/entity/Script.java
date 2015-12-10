package com.cultome.feedback.entity;

import java.io.Serializable;

/** 
 * Script.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	26/08/2015
 */
public class Script implements Serializable {

	private static final long serialVersionUID = 453415730500065878L;
	
	private String script;

	public Script() {
	}

	public Script(String script) {
		this.script = script;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public String toString() {
		return "Script [script=" + script + "]";
	}
}
