package com.cultome.feedback.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.cultome.feedback.entity.User;
import com.cultome.feedback.manager.AdminManager;
import com.cultome.feedback.util.Parameters;

/** 
 * Admin.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	25/08/2015
 */
@Path("/admin")
public class Admin {
	
	private AdminManager manager = new AdminManager();
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response login(User user) {
		System.out.println("Admin.login(username: " + user.getUsername() + ")");
		try {
			return Response.ok(manager.login(user.getUsername(), user.getPassword())).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@Path("/users")
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(User user) {
		System.out.println("Admin.create(username: " + user.getUsername() + ")");
		try {
			return Response.created(new URI(Parameters.getFeedbackServicesUrl() + "/admin/user/" + manager.createUser(user.getUsername(), user.getPassword()).getUsername())).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
	@Path("/user/{username}")
	@GET
	@Consumes("application/json")
	@Produces("application/json")
	public Response getUser(@PathParam("username") String username) {
		System.out.println("Admin.create(username: " + username + ")");
		try {
			return Response.ok().entity(manager.getUser(username)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(e.getMessage()).build();
		}
	}
	
}
