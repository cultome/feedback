package com.cultome.feedback.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.cultome.feedback.entity.Poll;
import com.cultome.feedback.entity.Script;
import com.cultome.feedback.manager.PollsManager;

/** 
 * Polls.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
@Path("/polls")
public class Polls {

	private final PollsManager manager = new PollsManager();
	
	private abstract class Logic {
		abstract Response execute() throws Exception;
		
		Response process(){
			try {
				return execute();
			} catch (Exception e) {
				e.printStackTrace();
				return Response.status(500).entity(responseMessage(e.getMessage())).build();
			}
		}
	}

	@GET
	@Path("/admin")
	@Produces({"application/json","text/xml"})
	public Response list() {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.list()");
				return Response.ok(manager.list().toArray(new Poll[] {})).build();
			}
		}.process();
	}

	@GET
	@Path("/admin/{pollId}")
	@Produces("application/json")
	public Response get(@PathParam("pollId") final Integer pollId) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.get(pollId: " + pollId + ")");
				return Response.ok(manager.get(pollId)).build();
			}
		}.process();
	}

	@POST
	@Path("/admin")
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(final Script script) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.create(script: " + script.getScript() + ")");
				return Response.ok(manager.create(script.getScript()).getId()).build();
			}
		}.process();
	}

	@POST
	@Path("/admin/{pollId}/update")
	@Consumes("application/json")
	@Produces("application/json")
	public Response update(@PathParam("pollId") final Integer pollId, final Script script) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.update(script: " + script.getScript() + ")");
				Poll poll = manager.update(pollId, script.getScript());
				return Response.ok(responseMessage("Poll #" + poll.getId() + " updated!")).build();
			}
		}.process();
	}

	@POST
	@Path("/admin/preview")
	@Consumes("application/json")
	@Produces("application/json")
	public Response preview(final Script script) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.preview(script: " + script.getScript() + ")");
				return Response.ok(new Poll().parse(script.getScript(), false)).build();
			}
		}.process();
	}
	
	@GET
	@Path("/admin/{pollId}/answers")
	@Produces("application/json")
	public Response getAnswers(@PathParam("pollId") final Integer pollId) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.getAnswers(pollId: " + pollId + ")");
				return Response.ok(manager.getAnswers(pollId)).build();
			}
		}.process();
	}

	@POST
	@Path("/admin/{pollId}/send")
	@Produces("application/json")
	public Response sendPoll(@PathParam("pollId") final Integer pollId) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.sendPoll(pollId: " + pollId + ")");
				return Response.ok(responseMessage(manager.sendPoll(pollId))).build();
			}
		}.process();
	}
	
	@Path("/admin/{pollId}")
	@DELETE
	@Produces("text/plain")
	public Response deletePoll(@PathParam("pollId") Integer pollId) {
		System.out.println("Admin.deletePoll(pollId: " + pollId + ")");
		try {
			return Response.ok().entity(String.valueOf(manager.deletePoll(pollId))).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	/*
	 * PUBLIC
	 */
	@GET
	@Path("/{token}")
	@Produces("application/json")
	public Response get(@PathParam("token") final String token) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.get(token: " + token + ")");
				return Response.ok(manager.get(token)).build();
			}
		}.process();
	}
	
	@POST
	@Path("/{token}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response respondPoll(@PathParam("token") final String token,final List<Map<String, String>> responses) {
		return new Logic() {
			@Override Response execute() throws Exception {
				System.out.println("Polls.respondPoll(token: " + token + ", responses: " + responses + ")");
				Map<String, List<String>> parsedResponses = extractResponses(responses);
				return Response.ok(responseMessage(manager.saveResponse(token, parsedResponses))).build();
			}
		}.process();
	}

	private Map<String, List<String>> extractResponses(List<Map<String, String>> responses) {
		Map<String, List<String>> parsedResponses = new HashMap<String, List<String>>();
		String name;
		String value;

		// [{name=q0, value=Carlos}, {name=q1, value=0}, {name=q2, value=0}, {name=q2, value=1}, {name=q3, value=0}]
		for (Map<String, String> r : responses) {
			name = r.get("name");
			value = r.get("value");

			if (parsedResponses.get(name) == null) {
				parsedResponses.put(name, new ArrayList<String>());
			}
			parsedResponses.get(name).add(value);
		}
		return parsedResponses;
	}
	
	private String responseMessage(String msg) {
		return "{\"response\": \"" + msg + "\"}";
	}
}
