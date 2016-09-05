package io.allseeing.collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.allseeing.Collector;

public class HelloWorldCollector extends Collector {

	private static final Logger log = LogManager.getLogger(HelloWorldCollector.class);
	
	public HelloWorldCollector(String instance) {
		super(instance);
	}

	@Override
	public String defaultSchedule() {
		return "* * * * *";
	}

	@Override
	public boolean collect() {
		log.debug("Hello World");
		
		return true;
	}

}
