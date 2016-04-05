/**
 * 
 */
package com.mystic.server.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mystic.db.domain.Mock;
import com.mystic.mocker.Mocker;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * @author Leo
 * 
 */

@Scope("prototype")
@Component("serverContext")
public class ServerContext implements HttpHandler {

	private static final String ROOT_PATH = "/";
	Mock response = null; 
	
	private static final Logger logger = Logger.getLogger(ServerContext.class.getName());

	@Autowired
	Mocker mapper;

	public void handle(final HttpExchange exchange) {

		final String uri = exchange.getRequestURI().toString();

		switch (uri) {
		case ROOT_PATH:
			break;
		default:
			new Thread(new Runnable() {
				public void run() {
					response = mapper.getMock(uri, exchange.getRequestHeaders());
					final Headers headers = exchange.getResponseHeaders();
					headers.set("Content-Type", String.format(response.getContentType() + "; charset=%s", StandardCharsets.UTF_8));
					final byte[] rawResponseBody = response.getValue().getBytes(StandardCharsets.UTF_8);
					try {
						exchange.sendResponseHeaders(200, rawResponseBody.length);
						exchange.getResponseBody().write(rawResponseBody);
					} catch (IOException e) {
						logger.log(Level.SEVERE, "[ServerContext] Failed To Get Exchange"); 
					}
					exchange.close();
				}
			}).start();
			break;
		}
	}
}
