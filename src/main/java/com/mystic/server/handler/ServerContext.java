/**
 * 
 */
package com.mystic.server.handler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mystic.domain.Mock;
import com.mystic.mocker.Mocker;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.annotation.PostConstruct;

/**
 * @author Leo
 * 
 */

@Scope("singleton")
@Component("serverContext")
public class ServerContext implements HttpHandler {

	ExecutorService handlePool;
	private static final String ROOT_PATH = "/";
	Mock response = null; 
	
	private static final Logger LOG = Logger.getLogger(ServerContext.class.getName());

	@Autowired
	private Mocker mocker;

	@PostConstruct
	public void init() {
		handlePool = Executors.newFixedThreadPool(20);
	}

	public void handle(final HttpExchange exchange) {

		final String uri = exchange.getRequestURI().toString();

		switch (uri) {
		case ROOT_PATH:
			break;
		default:
			handlePool.execute(new Runnable() {
				@Override
				public void run() {
					response = mocker.getMock(uri, exchange.getRequestHeaders());
					final Headers headers = exchange.getResponseHeaders();
					headers.putAll(response.getHeader());
					final byte[] rawResponseBody = response.getBody().getBytes(StandardCharsets.UTF_8);
					try {
						exchange.sendResponseHeaders(response.getStatusCode().value(), rawResponseBody.length);
						exchange.getResponseBody().write(rawResponseBody);
						Writer out = new OutputStreamWriter(exchange.getResponseBody(), StandardCharsets.UTF_8);
						out.write(response.getBody());
					} catch (IOException e) {
						LOG.log(Level.SEVERE, "[ServerContext] Failed To Get Exchange");
					}
					exchange.close();
				}
			});
			break;
		}
	}
}
