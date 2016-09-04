package io.allseeing;

import java.lang.instrument.Instrumentation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnowOwlAgent {
	
	private static final Logger log = LogManager.getLogger(SnowOwlAgent.class);

	private static final String APPLICATION_NAME = "Snow Owl Agent";
	private static final Version APPLICATION_VERSION = Version.getInstance();
	private static final String APPLICATION_COPYRIGHT = "Copyright (C) 2016 by All Seeing IO";
	
	public static void run() {
		log.info("This application is being monitored by " + APPLICATION_NAME + " v" + APPLICATION_VERSION);
		log.info(APPLICATION_COPYRIGHT);
	}
	
	public static void premain(String args, Instrumentation instrumentation) {
		run();
	}
	
	public static void main(String... args) {
		run();
	}
	
}
