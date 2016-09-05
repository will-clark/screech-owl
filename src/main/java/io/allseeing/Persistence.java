package io.allseeing;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class Persistence {

	private static final Logger log = LogManager.getLogger(Persistence.class);
	
	private final static String DATABASE_URL = "jdbc:h2:./agent";
	
	private ConnectionSource connectionSource = null;
	
	static {
		try {
	        Flyway flyway = new Flyway();
	        flyway.setDataSource(DATABASE_URL, null, null);
	        flyway.migrate();
		}
		catch (FlywayException e) {
			log.fatal("error migrating the database", e);
			System.exit(1);
		}
	}
	
	public Persistence() {
	}
	
	public void open() throws SQLException {
		connectionSource = new JdbcConnectionSource(DATABASE_URL);
	}
	
	public void close() {
		if (connectionSource != null) {
			try {
				connectionSource.close();
				connectionSource = null;
			}
			catch (IOException e) {
				// do nothing
			}
		}
	}
	
	public <D extends Dao<T, ?>, T> D createDao(Class<T> clazz) throws SQLException {
		if (connectionSource == null) throw new SQLException("Persistence open method must be called first!");
		
		return DaoManager.createDao(connectionSource, clazz);
	}
	
}
