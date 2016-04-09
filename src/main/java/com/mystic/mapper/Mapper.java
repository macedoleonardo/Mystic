package com.mystic.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mystic.client.RestClient;
import com.mystic.domain.Mock;
import com.mystic.jobs.MysticJob;
import com.sun.net.httpserver.Headers;

@Component("mapper")
public class Mapper {
	Mock mock;
	Map<String, String[]> mocks;
	private ResponseEntity<String> response;
	
	@Autowired
	private RestClient client;
	
	@Autowired
	private MysticJob job;
	
	private static final Logger LOG = Logger.getLogger(Mapper.class.getName());
	
	@PostConstruct
	void init() {
		mocks = new HashMap<String, String[]>();
		mock = new Mock();
	}

	public Mock findByMockKey(String uri, Headers header) {
		if(mocks.containsKey(uri)) {
			String[] data = mocks.get(uri);
			mock.setKey(uri);
			mock.setValue(data[0]);
			mock.setContentType(data[1]);
		}else {
			response = client.resquest(uri, header);
			mock.setKey(uri); 
			setBody();
			setContentType();
			mocks.put(uri, new String[]{mock.getContentType(), mock.getValue()});
			saveNewMock(mock);
		}
		
		if(!mock.getKey().equals("/favicon.ico")) {
			LOG.log(Level.INFO, "[Mapper][Mock : " + mock.getKey() + "]");
		}
		
		return mock;
	}
	
	public void setMockToMap(HashMap<String, String[]> data) {
		mocks.putAll(data); 
	}
	
	private void setContentType() {
		if(response.getHeaders().getContentType() != null) {
			mock.setContentType(response.getHeaders().getContentType().toString());
		}else {
			mock.setContentType("application/json");
		}
	}

	private void setBody() {
		if(response.getHeaders().getContentLength() != 0) {
			mock.setValue(response.getBody().toString()); 
		}else {
			mock.setValue(""); 
		}
	}
	
	private void saveNewMock(final Mock newMock) {
		new Thread(new Runnable() {
			public void run() {
				job.save(newMock);
			}
		}).start();
	}
}
