/**
 * 
 */
package com.mystic.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.mystic.server.handler.ServerContext;
import com.sun.net.httpserver.HttpServer;

/**
 * @author Leo
 * @since 25-03-2016
 * HTTP Mock Server
 */

@Data
@Component("mysticServer")
public class MysticServer {
	int port = 1209;
	HttpServer server;
	ExecutorService executor;
	
	@Autowired 
	private ServerContext serverContext;
	
	private static final Logger logger = Logger.getLogger(MysticServer.class.getName());
	
	/**
	 * Init Server
	 */
	@PostConstruct
	void init() {
		try {
			executor = Executors.newFixedThreadPool(50);
			String portNumber = System.getProperty("mystic.port");
			if(portNumber != null) {
				port = Integer.valueOf(portNumber);
			}
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", serverContext);
			server.setExecutor(executor);
		    server.start();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[MysticServer] [port {" + port + "}]", e);
		}
	}
}
