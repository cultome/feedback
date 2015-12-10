package com.cultome.feedback.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cultome.feedback.dao.PollsDao;
import com.cultome.feedback.entity.Answer;
import com.cultome.feedback.entity.Poll;
import com.cultome.feedback.exception.InvalidScriptException;

/** 
 * PollsDaoTest.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	07/08/2015
 */
public class PollsDaoTest {
	
	private PollsDao dao;

	@Before
	public void setUp() throws Exception {
		dao = new PollsDao();
		dao.dropPollTables();
		dao.setupPollTables();
		
		dao.save("{Title} Cuestionario de prueba\n{Question uno} Como te llamas?\n___\n{Question 2} Rango de edad?\n{Config optional => true}\n( ) 1 a 20\n( ) 21 a 40\n( ) 41 a 60\n{ Question} Frutas favoritas (escoge 2)\n{Config choose => 2}\n[] Platano\n[] Manzana\n[] Piña\n{Question} Usas Tweeter?\n{Config conditional => [uno, 2]}\n( ) Si\n( ) No\n{SendTo user@example.com}");
		dao.createToken("cultome@gmail.com", 1, "AAA");
	}

	@Test
	public final void testList() throws SQLException {
		List<Poll> l = dao.list();
		assertNotNull(l);
		assertFalse(l.isEmpty());
	}

	@Test
	public final void testSendPoll() throws Exception {
		assertNull(dao.get(1).getSent());
		dao.sendPoll(1);
		assertNotNull(dao.get(1).getSent());
	}

	@Test
	public final void testGet() throws Exception {
		Poll p = dao.get(1);
		assertNotNull(p);
		assertNotNull(p.getContacts());
		assertNotEquals(0, p.getContacts().length);
		assertNotNull(p.getQuestions());
		assertNotEquals(0, p.getQuestions().length);
		assertNotNull(p.getId());
		assertNotNull(p.getScript());
		assertNull(p.getSent());
		assertNotNull(p.getTitle());
		assertNotNull(p.getUpdated());
	}

	@Test
	public final void testSave() throws Exception{
		try {
			dao.get(2);
			assertTrue(false);
		} catch (SQLException e) {
			assertTrue(true);
		}
		
		dao.save("{Title} Cuestionario de prueba\n{Question uno} Como te llamas?\n___\n{Question 2} Rango de edad?\n{Config optional => true}\n( ) 1 a 20\n( ) 21 a 40\n( ) 41 a 60\n{ Question} Frutas favoritas (escoge 2)\n{Config choose => 2}\n[] Platano\n[] Manzana\n[] Piña\n{Question} Usas Tweeter?\n{Config conditional => [uno, 2]}\n( ) Si\n( ) No\n{SendTo user@example.com}");
		
		try {
			dao.get(2);
			assertTrue(true);
		} catch (SQLException e) {
			assertTrue(false);
		}
	}

	@Test
	public final void testUpdate() throws SQLException, InvalidScriptException {
		Poll poll = dao.get(1);
		String script = poll.getScript();
		Date updated = poll.getUpdated();
		
		dao.update(1, "{Title} Cuestionario de prueba Actualizado\n{Question uno} Como te llamas?\n___\n{Question 2} Rango de edad?\n{Config optional => true}\n( ) 1 a 20\n( ) 21 a 40\n( ) 41 a 60\n{ Question} Frutas favoritas (escoge 2)\n{Config choose => 2}\n[] Platano\n[] Manzana\n[] Piña\n{Question} Usas Tweeter?\n{Config conditional => [uno, 2]}\n( ) Si\n( ) No\n{SendTo user@example.com}");
		
		poll = dao.get(1);
		assertNotEquals(script, poll.getScript());
		assertNotEquals(updated,poll.getUpdated());
		
	}
	
	@Test
	public final void testGetAnswers() throws SQLException {
		testSaveResponse();
		List<Answer> answers = dao.getAnswers(1);
		assertNotNull(answers);
		assertFalse(answers.isEmpty());
		for (Answer answer : answers) {
			assertNotNull(answer.getAnswer());
			assertNotNull(answer.getEmail());
			assertNotNull(answer.getQuestionIdx());
			assertNotNull(answer.getSent());
			assertNotNull(answer.getTxId());
		}
	}

	@Test
	public final void testSaveResponse() throws SQLException {
		String token = "AAA";
		
		HashMap<String, List<String>> responses = new HashMap<String, List<String>>();
		responses.put("0", Arrays.asList("Carlos Soria"));
		responses.put("1", Arrays.asList("1-20"));
		responses.put("2", Arrays.asList("Platano", "Manzana"));
		
		dao.saveResponse(token, responses);
		
		List<Answer> r = dao.getAnswers(1);
		assertNotNull(r);
		assertFalse(r.isEmpty());
		
	}

}
