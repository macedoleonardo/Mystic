/**
 * 
 */
package com.mystic.mocker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mystic.client.RestClient;
import com.mystic.db.dao.IMockerDAO;
import com.mystic.db.domain.Mock;
import com.mystic.jobs.MysticJob;
import com.sun.net.httpserver.Headers;

/**
 * @author Leo
 *
 */

@Data
@Lazy(false)
@Scope("prototype")
@Component("mocker")
public class Mocker {
	
	@Autowired
	private RestClient client;
	
	@Autowired 
	private IMockerDAO mockerDAO;
	
	@Autowired
	private MysticJob job;
	
	private Mock mock;
	private Mock neymar;
	private ResponseEntity<String> response;
	private List<Mock> mocks;
	private List<String> listKeys;
	
	@PostConstruct
	void init() {
		mocks = new LinkedList<Mock>();
		listKeys = new ArrayList<String>();
	}
	
	/**
	 * Get Mock From Repository or Request New Mock
	 * @param uri
	 * @param header
	 * @return String
	 */
	public Mock getMock(String uri, Headers header) {
		mock = null;
		mock = mockerDAO.findByMockKey(uri);
		
		if(mock == null) { 
			neymar = new Mock();
			response = client.resquest(uri, header);
			neymar.setKey(uri); 
			
			if(response.getHeaders().getContentLength() != 0) {
				neymar.setValue(response.getBody().toString()); 
			}else {
				neymar.setValue(""); 
			}
			neymar.setContentType(response.getHeaders().getContentType().toString());
			saveNewMock(neymar);
			mocks.add(neymar);
			return neymar;
		} 
		return mock;
	}
 
	private void saveNewMock(final Mock newMock) {
		new Thread(new Runnable() {
			public void run() {
				job.save(newMock);
			}
		}).start();
	}
}
