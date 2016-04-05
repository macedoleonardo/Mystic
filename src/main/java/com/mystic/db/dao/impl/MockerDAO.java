package com.mystic.db.dao.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mystic.db.dao.IMockerDAO;
import com.mystic.db.domain.Mock;
import com.mystic.db.utils.CustomHibernateDaoSupport;

@Scope("prototype")
@Component("mockerDAO")
public class MockerDAO extends CustomHibernateDaoSupport implements IMockerDAO {
	
	private static final Logger LOG = Logger.getLogger(MockerDAO.class.getName());

	/*
	 * (non-Javadoc)
	 * @see com.mosckito.dao.IMockerDAO#save(com.mosckito.domain.Mock)
	 */
	public void save(Mock mock) {
		getHibernateTemplate().save(mock);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mosckito.dao.IMockerDAO#update(com.mosckito.domain.Mock)
	 */
	public void update(Mock mock) {
		getHibernateTemplate().update(mock);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mosckito.dao.IMockerDAO#delete(com.mosckito.domain.Mock)
	 */
	public void delete(Mock mock) {
		getHibernateTemplate().delete(mock);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mosckito.dao.IMockerDAO#findByStockCode(java.lang.String)
	 */
	public Mock findByMockKey(String key) {
		LOG.log(Level.INFO, "[KEY][" + key + "]");
		@SuppressWarnings("unchecked")
		List<Mock> mocks = getHibernateTemplate().find("from Mock where key=?", key);
		return mocks.isEmpty() ? null : mocks.get(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.mosckito.dao.IMockerDAO#findByStockCode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Mock> findByAll(String key) {
		return getHibernateTemplate().find("from Mock");
	}
}
