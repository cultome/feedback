package com.cultome.feedback.test;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.cultome.feedback.listener.FeedbackAppListener;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

/** 
 * Launcher.java

 * @author		Carlos Soria <cultome@gmail.com>
 * @creation	17/07/2015
 */
public class Launcher {
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(8080).build();
	}

	public static final URI BASE_URI = getBaseURI();

	protected static HttpServer startServer() throws IOException {
		System.out.println("Starting grizzly...");
		ResourceConfig rc = new PackagesResourceConfig("com.cultome.feedback.resource");
		Map<String, Object> properties = rc.getProperties();
		properties.put("com.sun.jersey.api.json.POJOMappingFeature", true);
		properties.put("com.sun.jersey.spi.container.ContainerRequestFilters", "com.cultome.feedback.filters.TokenFilter");
		rc.setPropertiesAndFeatures(properties);
		return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
	}

	public static void main(String[] args) throws Exception {
		new FeedbackAppListener().contextInitialized(null);
		
		HttpServer httpServer = startServer();
		System.out.println("Server ready!");
		System.in.read();
		httpServer.stop();
	} 
}
