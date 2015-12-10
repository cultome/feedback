package com.cultome.feedback.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cultome.feedback.dao.PollsDao;
import com.cultome.feedback.entity.Poll;
import com.cultome.feedback.manager.PollsManager;

/** 
 * PollsManagerTest.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class PollsManagerTest {
	
	private PollsManager manager;

	@Before
	public void setUp() throws Exception {
		manager = new PollsManager();
		PollsDao dao = new PollsDao();
		try{
			dao.dropPollTables();
			dao.setupPollTables();
			dao.save("{Title} Cuestionario de prueba\n{Question uno} Como te llamas?\n___\n{Question 2} Rango de edad?\n{Config optional => true}\n( ) 1 a 20\n( ) 21 a 40\n( ) 41 a 60\n{ Question} Frutas favoritas (escoge 2)\n{Config choose => 2}\n[] Platano\n[] Manzana\n[] Piña\n{Question} Usas Tweeter?\n{Config conditional => [uno, 2]}\n( ) Si\n( ) No\n{SendTo user@example.com}\n{EmailSubject} Hola");
			HashMap<String, List<String>> responses = new HashMap<String, List<String>>();
			responses.put("0", Arrays.asList("Carlos Soria"));
			responses.put("1", Arrays.asList("1-20"));
			responses.put("2", Arrays.asList("Platano", "Manzana"));
			dao.saveResponse("AAB", responses);
		} catch(Exception e){}
	}
	
	@Test
	public final void testDeletePoll() throws Exception {
		assertNotNull(new PollsManager().get(1));
		manager.deletePoll(1);
		
		try{
			assertNull(new PollsManager().get(1));
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
	}

	@Test
	public final void testGet() throws Exception {
		Poll poll = manager.get(1);
		assertNotNull(poll);
		assertNotNull(poll.getId());
		assertNotNull(poll.getScript());
		assertNotNull(poll.getTitle());
		assertNotNull(poll.getQuestions());
		assertFalse(poll.getQuestions().length == 0);
		System.out.println(poll);
	}
	
	@Test
	public final void testCreate() throws Exception {
		Poll poll = manager.create("{Title} Titulo\n{Question} Hola como estas?\n( ) Bien\n( ) Mal\n( ) Mao o menos\n{SendTo user@example.com}");
		assertNotNull(poll);
		assertNotNull(poll.getId());
		assertNotNull(poll.getScript());
		assertNotNull(poll.getTitle());
		assertNotNull(poll.getQuestions());
		assertFalse(poll.getQuestions().length == 0);
		System.out.println(poll);
	}
	
	@Test
	public final void testUpdate() throws Exception {
		Poll poll = manager.update(1, "{Title} Titulo\n{Question} Hola como estas?\n( ) Bien\n( ) Mal\n( ) Mao o menos\n{SendTo user@example.com}");
		assertNotNull(poll);
		assertNotNull(poll.getId());
		assertNotNull(poll.getScript());
		assertNotNull(poll.getTitle());
		assertNotNull(poll.getQuestions());
		assertFalse(poll.getQuestions().length == 0);
		System.out.println(poll);
	}
	
	@Test
	public final void testList() throws Exception {
		List<Poll> polls = manager.list();
		assertNotNull(polls);
		assertFalse(polls.isEmpty());
		for (Poll poll : polls) {
			assertNotNull(poll.getId());
			assertNotNull(poll.getScript());
			assertNotNull(poll.getTitle());
			assertNotNull(poll.getQuestions());
			assertFalse(poll.getQuestions().length == 0);
			System.out.println(poll);
		}
	}
	
	@Test
	public final void testSendPoll() throws Exception {
		assertNotNull(manager.sendPoll(1));
	}

}
