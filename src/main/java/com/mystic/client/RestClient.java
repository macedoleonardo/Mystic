/**
 * 
 */
package com.mystic.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sun.net.httpserver.Headers;

import lombok.Data;

/**
 * @author Leo
 * @since 26-03-2016
 * Class In Charge Of Make Requests
 */

@Data
@Scope("prototype")
@Component("restClient")
public class RestClient {
	String endpoint;
	RestTemplate restTemplate;
	ResponseEntity<String> response = null;
	String body = "{\"message\":\"Resource {0} not found.\",\"error\":\"not_found\",\"status\":404,\"cause\":[]}";
	
	private static final Logger logger = Logger.getLogger(RestClient.class.getName());
	
	/**
	 * Initialize Rest template
	 */
	@PostConstruct
	void init() {
		endpoint = System.getProperty("mystic.endpoint");
		restTemplate = new RestTemplate();
	}
	
	/**
	 * Basic Rest Client 
	 * @param URI
	 * @param headers
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> resquest(String URI, Headers headers) {
		response = null;
		HttpHeaders header = new HttpHeaders();
	    HttpEntity<String> request = new HttpEntity<String>(header);
	    try {
	    	response = restTemplate.exchange(endpoint.concat(URI), HttpMethod.GET, request, String.class);
	    } catch(RestClientException ex) {
	    	logger.log(Level.SEVERE, "[RestClient][URI {" + URI + "}] Set Default Error Response");
	    } finally {
	    	if (response == null) {
	    		response = new ResponseEntity<String>(body.replace("{0}", URI), HttpStatus.OK);
			}
	    }
	    return response;
	}
}
