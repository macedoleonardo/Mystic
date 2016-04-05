/**
 * 
 */
package com.mystic.jobs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.mystic.db.dao.IMockerDAO;
import com.mystic.db.domain.Mock;
import com.mystic.db.utils.MysticConfiguration;

/**
 * @author lpdmacedo
 * 
 */

@Data
@Lazy(false)
@Component("mysticJob")
public class MysticJob {

	private long initTime = 0;
	private long endTime = 0;

	private List<String> mocks = new ArrayList<String>();

	@Autowired
	private MysticConfiguration configuration;

	@Autowired
	private IMockerDAO mockerDAO;

	private static final Logger LOG = Logger.getLogger(MysticJob.class.getName());

	public void save(final Mock mock) {
		new Thread(new Runnable() {
			public void run() {
				mockerDAO.save(mock);
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				saveInsertSQL(mock);
			}
		}).start();
	}

	/**
	 * Add New Entry in SQL File
	 * 
	 * @param next
	 */
	private void saveInsertSQL(final Mock mock) {
		String query = "INSERT INTO MOCK VALUES ('{0}', '{1}', '{2}');";
		query = query.replace("{0}", mock.getKey());
		query = query.replace("{1}", mock.getValue());
		query = query.replace("{2}", mock.getContentType());

		try {
			if (!existMock(query)) {
				FileUtils.writeStringToFile(configuration.getData(), "\n", StandardCharsets.UTF_8, true);
				FileUtils.writeStringToFile(configuration.getData(), query.replaceAll("\\s*[\\r\\n]+\\s*", "").trim(), StandardCharsets.UTF_8, true);
				mocks.add(query);
				LOG.log(Level.INFO, "[MysticJob][query : " + query.replace("\n", "") + "]"); 
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "[MysticJob] Failed To Write New Insert SQL");
		}
	}

	private boolean existMock(String query) {
		return mocks.contains(query);
	}
}
