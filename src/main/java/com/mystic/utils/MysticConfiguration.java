package com.mystic.utils;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;

import lombok.Data;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.mystic.domain.Mock;

@Data
@Lazy(false)
@Scope("prototype")
@Component("mysticConfiguration")
public class MysticConfiguration {

	private static final Logger LOG = Logger.getLogger(MysticConfiguration.class.getName());

	@Autowired private ZipUtils zipUtils;
	@Resource(name = "mocks") private Map<String, Mock> mocks;

	/**
	 * Load Mock Data When Launch The Application
	 */
	public void setUpMocks() {
		LOG.log(Level.CONFIG, "[MysticConfiguration][Load Mock Data]");
		StringBuilder stringBuilder = zipUtils.readZipFile();

		if(stringBuilder.length() > 0) {
			String[] lines = stringBuilder.toString().split("\\n");
			for(String line : lines) {
				Mock mock = stringToObject(line);
				mocks.put(mock.getUri(), mock);
			}
		}
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
