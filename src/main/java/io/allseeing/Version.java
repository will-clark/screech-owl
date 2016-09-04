package io.allseeing;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Version {

	private static final Logger log = LogManager.getLogger(Version.class);
	
	private static Version instance;
	private static Object mutex = new Object();
	
	private static final String PROPERTIES_FILE = "version.properties";
	
	private String version;
	private String build;
	
	public static Version getInstance() {
		if (instance == null) {
			synchronized (mutex) {
				if (instance == null) {
					instance = new Version();
				}
			}
		}
		
		return instance;
	}
	
	private Version() {
		Properties properties = new Properties();
		try (InputStream is = this.getClass().getResourceAsStream(PROPERTIES_FILE)) {
			properties.load(is);						
			this.version = properties.getProperty("VERSION");
			this.build = properties.getProperty("BUILD");
		}
		catch (IOException e) {
			log.fatal("Error reading the '" + PROPERTIES_FILE + "' file", e);
			System.exit(1);
		}
	}	
	
	public String toString() {
		return version + "." + build;
	}
	
}
