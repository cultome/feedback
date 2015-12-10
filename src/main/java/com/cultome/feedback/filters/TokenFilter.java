package com.cultome.feedback.filters;

import java.util.List;

import javax.ws.rs.core.PathSegment;

import com.cultome.feedback.exception.InvalidTokenException;
import com.cultome.feedback.manager.AdminManager;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/** 
 * TokenFilter.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	31/08/2015
 */
public class TokenFilter implements ContainerRequestFilter {

	@Override
	public ContainerRequest filter(ContainerRequest req) {
		if(!isAdminContext(req)){
			return req;
		}
		
		String token = req.getHeaderValue("token");
		
		System.out.println("[*] Token: " + token);
		
		if(isLogin(req)){
			return req;
		}
		
		if(token == null){
			throw new InvalidTokenException("Invalid request!");
		}
		
		AdminManager dao = new AdminManager();
		System.out.println("[*] Validating token [" + token + "]");
		if(dao.isValidToken(token)){
			System.out.println("[*] Is a valid token! [" + token + "]");
			return req;
		}
		
		System.out.println("[*] Invalidating token [" + token + "]");
		throw new InvalidTokenException("Invalid token!");
	}

	private boolean isAdminContext(ContainerRequest req) {
		String rootPath = getRootPath(req);
		if(rootPath != null && "admin".equals(rootPath)){
			return true;
		}
		return false;
	}

	private boolean isLogin(ContainerRequest req) {
		if(isAdminContext(req) && "POST".equals(req.getMethod())){
			System.out.println("[*] Login request!");
			return true;
		}
		return false;
	}

	private String getRootPath(ContainerRequest req) {
		List<PathSegment> pathSegments = req.getPathSegments(true);
		if(pathSegments == null || pathSegments.size() < 1){
			return null;
		}
		return pathSegments.get(0).getPath();
	}


}
