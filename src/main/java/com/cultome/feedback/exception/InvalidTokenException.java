package com.cultome.feedback.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/** 
 * InvalidTokenException.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	31/08/2015
 */
public class InvalidTokenException extends WebApplicationException {

	private static final long serialVersionUID = 7475606061378832920L;
	
	public InvalidTokenException(String message) {
		super(Response.status(Status.FORBIDDEN).entity("{\"message\": \"" + message + "\"}").build());
	}
}
