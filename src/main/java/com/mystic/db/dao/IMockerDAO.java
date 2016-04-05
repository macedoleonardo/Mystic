package com.mystic.db.dao;

import java.util.List;

import com.mystic.db.domain.Mock;

/**
 * @author leonardo
 * Interface Mocker
 */
public interface IMockerDAO {
	
	void save(Mock mock);
		
	void update(Mock mock);
	
	void delete(Mock mock);
	
	Mock findByMockKey(String key);
	
	List<Mock> findByAll(String key);
}
