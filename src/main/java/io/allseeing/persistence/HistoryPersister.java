package io.allseeing.persistence;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import io.allseeing.History;
import io.allseeing.Persistence;

public class HistoryPersister {
	
	private Persistence persistence;
	private Dao<History, Integer> dao;
	
	public HistoryPersister(Persistence persistence) throws SQLException {
		this.persistence = persistence;
		this.dao = this.persistence.createDao(History.class);
	}
	
	public void save(History history) throws SQLException {
		dao.create(history);
	}
	
}
