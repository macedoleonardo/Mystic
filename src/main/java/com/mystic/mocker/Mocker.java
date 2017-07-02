/**
 * 
 */
package com.mystic.mocker;


import com.mystic.client.RestClient;
import com.mystic.utils.MysticConfiguration;
import com.mystic.utils.ZipUtils;
import com.sun.net.httpserver.Headers;
import lombok.Data;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.mystic.domain.Mock;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Leo
 *
 */

@Data
@Lazy(false)
@Scope("prototype")
@Component("mocker")
public class Mocker {
	private ResponseEntity<String> response;
	private ExecutorService saveService;

	private static final Logger LOG = Logger.getLogger(Mocker.class.getName());

	@Autowired private Mock mock;
	@Autowired private RestClient client;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private ZipUtils zipUtils;
	@Autowired MysticConfiguration configuration;
	@Resource(name = "mocks") private Map<String, Mock> mocks;

	@PostConstruct
	public void init() {
		saveService = Executors.newFixedThreadPool(20);
		configuration.setUpMocks();
	}
		
	/**
	 * Get Mock From Repository or Request New Mock
	 * @param uri
	 * @param header
	 * @return String
	 */
	public Mock getMock(String uri, Headers header) {
		return findByMockKey(uri, header);
	}

	private Mock findByMockKey(String uri, Headers header) {
		if(mocks.containsKey(uri)) {
			Mock data = mocks.get(uri);
			mock.setUri(uri);
			mock.setBody(data.getBody());
			mock.setHeader(data.getHeader());
			mock.setStatusCode(data.getStatusCode());
		}else {
			response = client.resquest(uri, header);
			mock.setUri(uri);
			setBody();
			setHeader();
			mock.setStatusCode(response.getStatusCode());
			mocks.put(uri, mock);
			saveMock(mock);
		}
		return mock;
	}

	private void setHeader() {
		Headers header = new Headers();
		if(response.getHeaders().getContentType() != null) {
			for (Map.Entry<?, ?> entry : response.getHeaders().entrySet()) {
				if(!entry.getKey().equals("Transfer-Encoding")) {
					header.set(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			mock.setHeader(header);
		}else {
			header.add("Content-Type", "application/json");
			mock.setHeader(header);
		}
	}

	private void setBody() {
		if(response.getHeaders().getContentLength() != 0) {
			mock.setBody(response.getBody().toString());
		}else {
			mock.setBody("{}");
		}
	}

	private void saveMock(final Mock mock) {
		saveService.execute(new Runnable() {
			@Override
			public void run() {
				String mockToSave = objectToJson(mock);
				zipUtils.writeMockIntoZipFile(mockToSave);
				LOG.log(Level.INFO, "[Mystic - Mocker][Mock : " + mockToSave + "]");
			}
		});
	}

	private String objectToJson(Mock mock) {
		String mockToSave = null;
		try {
			mockToSave = objectMapper.writeValueAsString(mock);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[Mystic - Mocker][Failed To Transform Object To JSON String]");
		}
		return mockToSave;
	}
}
