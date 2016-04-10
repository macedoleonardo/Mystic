package com.mystic.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import lombok.Data;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mystic.domain.Mock;
import com.mystic.jobs.MysticJob;
import com.mystic.mapper.Mapper;

@Data
@Lazy(false)
@Scope("prototype")
@Component("mysticConfiguration")
public class MysticConfiguration {
	private HashMap<String, String[]> data;
	
	private static final Logger LOG = Logger.getLogger(MysticConfiguration.class.getName());

	@Autowired
	private MysticJob job;
	
	@Autowired
	private ZipUtils zipUtils;
	
	@Autowired
	private Mapper mapper;
	
	@PostConstruct
	void init() {
		data = new HashMap<String, String[]>();
		loadMockData();
	}

	/**
	 * Load Mock Data When Launch The Application
	 */
	private void loadMockData() {
		LOG.log(Level.CONFIG, "[MysticConfiguration][Load Mock Data]");
		
		Mock mock = null;
		StringBuilder stringBuilder = zipUtils.readZipFile();
		if(stringBuilder.length() > 0) {
			String[] lines = stringBuilder.toString().split("\\n");
			for(String line : lines) {
				if(line.contains("/sites/MLA/brands")) {
					System.out.println(line);
				}
				mock = stringToObject(line);
				data.put(mock.getKey(), new String[]{mock.getValue(), mock.getContentType()});
			}
		}else {
			data.put("/mystic", new String[]{"{\"name\":\"Mystic Mock Server\"}", "application/json"});
		}
		mapper.setMockToMap(data);
	}
	
	/**
	 * Parse JSON String To Object Mock
	 * @param line
	 * @return
	 */
	private Mock stringToObject(String line) {
		Mock mock = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			mock = objectMapper.readValue(line, Mock.class);
		} catch (JsonParseException e) {
			LOG.log(Level.SEVERE, "[MysticConfiguration][Load Mock Data][Parser Exception]");
		} catch (JsonMappingException e) {
			LOG.log(Level.SEVERE, "[MysticConfiguration][Load Mock Data][Mapping Exception]");
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[MysticConfiguration][Load Mock Data][Input/Output Exception]");
		}
		return mock;
	}
}
