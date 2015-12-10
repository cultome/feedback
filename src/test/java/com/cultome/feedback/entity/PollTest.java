package com.cultome.feedback.entity;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.cultome.feedback.entity.Poll;
import com.cultome.feedback.exception.InvalidScriptException;

/** 
 * PollTest.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	07/08/2015
 */
public class PollTest {
	
	private Poll p;
	
	@Before
	public void setUp() throws Exception {
		p = new Poll();
	}

	@Test
	public final void testQuestionOptionsScript() {
		String validScript = "{title} Titulo\n"
				+ "{Question} Pregunta?\n"
				+ "{Config axis => Te gusto el servicio?} \n"
				+ "{config order => ascending } \n"
				+ "-> option 1\n"
				+ "-> Option 2\n"
				+ "{SendTo contact1@example.com,contact2@example.com , contact3@example.com}\n";
		assertNull(validateScript(validScript, true));
	}
	
	@Test
	public final void testValidScript() {
		String validScript = "{title} Titulo\n"
				+ "{Question} Pregunta?\n"
				+ "-> option 1\n"
				+ "-> Option 2\n"
				+ "{SendTo contact1@example.com,contact2@example.com , contact3@example.com}\n";
		assertNull(validateScript(validScript, true));
	}
	
	@Test
	public final void testIncompleteScript() {
		assertEquals("Script is empty!", validateScript("", false));
		assertEquals("You need to add a few questions. Add one with '{Question} Question text?'", validateScript("{title} Titulo", false));
	}
	
	@Test
	public final void testScriptNoTitle() {
		String noTitleScript = "{Question} Pregunta?\n"
				+ "() option 1\n"
				+ "() Option 2\n"
				+ "{SendTo contact1@example.com,contact2@example.com , contact3@example.com}\n";
		assertEquals("You should put a title for this poll. Add one with '{Title} This is my Poll name'", validateScript(noTitleScript, false));
	}
	
	@Test
	public final void testScriptNoQuestions() {
		String noQuestionScript = "{Title} Titulo\n"
				+ "{SendTo contact1@example.com,contact2@example.com , contact3@example.com}\n";
		assertEquals("You need to add a few questions. Add one with '{Question} Question text?'", validateScript(noQuestionScript, false));
	}
	
	@Test
	public final void testScriptNoOptions() {
		String noOptionsScript = "{Title} Titulo\n"
				+ "{Question} Pregunta?\n"
				+ "{SendTo contact1@example.com,contact2@example.com , contact3@example.com}\n";
		assertEquals("The question #1 dont have options. Add it with '()|[]|__ This is my option text.'.", validateScript(noOptionsScript, false));
	}
	
	@Test
	public final void testScriptNoContacts() {
		String noContactsScript = "{title} Titulo\n"
				+ "{Question} Pregunta?\n"
				+ "() option 1\n"
				+ "() Option 2\n";
		assertEquals("You should declare some contacts for this poll to be send. Add them with '{SendTo contact1@example.com, contact2@example.com}'", validateScript(noContactsScript, false));
	}
	
	@Test
	public final void testEmailFields() {
		String emailScript = "{title} Titulo\n"
				+ "{Question} Pregunta?\n"
				+ "() option 1\n"
				+ "() Option 2\n"
				+ "{EmailSubject} Hola amigos\n"
				+ "{EmailTitle} Solo para la banda\n"
				+ "{EmailContent} Este es el contenido\n"
				+ "multinea de un corrreo\n"
				+ "<A la encuesta!>\n"
				+ "los saltos de linea deberan\n"
				+ "respectarse\n"
				+ "{EmailSign} El barto\n"
				+ "{SendTo contact1@example.com,contact2@example.com , contact3@example.com}\n";
		assertEquals(null, validateScript(emailScript, true));
	}

	private String validateScript(String script, boolean isValid) {
		try {
			p.parse(script, true);
			// response = true
			assertTrue(isValid);
			return null;
		} catch (InvalidScriptException e) {
			// response = false
			assertFalse(isValid);
			return e.getMessage();
		}
	}
}
