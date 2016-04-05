package com.mystic.db.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import lombok.Data;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mystic.db.dao.IMockerDAO;
import com.mystic.db.domain.Mock;
import com.mystic.jobs.MysticJob;

@Data
@Lazy(false)
@Scope("prototype")
@Component("mysticConfiguration")
public class MysticConfiguration {
	private File data;
	
	private static final Logger logger = Logger.getLogger(MysticConfiguration.class.getName());
	
	@Autowired
	private MysticJob job;
	
	@Autowired
	private IMockerDAO mockerDAO;
	
	@PostConstruct
	void init() {
		dataMockFileConfig();
		loadSQLData();
	}

	/**
	 * Load SQL Data When Launch The App
	 */
	private void loadSQLData() {
		try {
			List<String> mocks = FileUtils.readLines(data);
			Iterator<String> it = mocks.iterator();
			job.getMocks().addAll(mocks);
			
			while (it.hasNext()) {
				mockerDAO.save(parseInsert(it.next()));
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[MysticConfiguration][Failed Trying To Read File - %s]", data.getAbsoluteFile());
		}
	}

	/**
	 * Transform String SQL To Mock Object
	 * @param insert
	 * @return
	 */
	private Mock parseInsert(String insert) {
		String[] sqlArray = insert.split("'");
		Mock mock = new Mock();
		mock.setKey(sqlArray[1].trim());
		mock.setValue(sqlArray[3].trim());
		mock.setContentType(sqlArray[5].trim());
		return mock;
	}

	/**
	 * Configuration Path To SQL Data
	 */
	private void dataMockFileConfig() {
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		String SCRIPT_SQL = System.getProperty("mystic.data") ;
		String mock = "INSERT INTO MOCK VALUES (null, '/mystic', '{\"name\":\"Mystic Mock Server\"}', 'application/json')";
		
		if (SCRIPT_SQL == null) { 
			SCRIPT_SQL = TEMP_DIR + "data.sql";
			data = new File(SCRIPT_SQL);
			writeInsertData(mock);
		}else {
			data = new File(SCRIPT_SQL);
			if (!data.exists()) {
				data.mkdirs();
				data = new File(data.getAbsolutePath().concat("/data.sql"));
				writeInsertData(mock);
			}else {
				data = new File(data.getAbsolutePath().concat("/data.sql"));
			}
		}
	}

	/**
	 * Insert Mock To JDBC Read File Without Exception
	 * @param mock
	 */
	private void writeInsertData(String mock) {
		try {
			FileUtils.write(data, mock, StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "[MysticConfiguration][Failed Trying To Write File - %s]", data.getAbsoluteFile());
		} 
	}
}
