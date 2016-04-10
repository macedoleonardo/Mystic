/**
 * 
 */
package com.mystic.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import lombok.Data;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.mystic.domain.Mock;
import com.mystic.utils.MysticConfiguration;
import com.mystic.utils.ZipUtils;

/**
 * @author lpdmacedo
 * 
 */

@Data
@Lazy(false)
@Component("mysticJob")
public class MysticJob {

	private List<String> mocks;
	private ObjectMapper objectMapper;
	
	@Autowired
	private MysticConfiguration configuration;
	
	@Autowired
	private ZipUtils zipUtils;

	private static final Logger LOG = Logger.getLogger(MysticJob.class.getName());
	
	@PostConstruct
	void init() {
		mocks = new ArrayList<String>();
		objectMapper = new ObjectMapper();
	}

	public void save(final Mock mock) {
		new Thread(new Runnable() {
			public void run() {
				saveMock(mock);
			}
		}).start();
	}

	/**
	 * Add New Entry in SQL File
	 * 
	 * @param next
	 */
	private void saveMock(final Mock mock) {
		String query = objectToJson(mock);
		
		if (!existMock(query)) {
			zipUtils.writeMockIntoZipFile(query); 
			mocks.add(query);
			LOG.log(Level.INFO, "[MysticJob][query : " + query + "]"); 
		}
	}

	private String objectToJson(Mock mock) {
		String query = null;
		try {
			query = objectMapper.writeValueAsString(mock);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[MysticJob][Failed To Transform Object To JSON String]"); 
		}
		return query;
	}

	private boolean existMock(String query) {
		return mocks.contains(query);
	}
}
