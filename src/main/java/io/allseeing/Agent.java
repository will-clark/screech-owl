package io.allseeing;

import java.lang.instrument.Instrumentation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.allseeing.collectors.CollectorStatusCollector;
import io.allseeing.collectors.HelloWorldCollector;

public class Agent {
	
	private static final Logger log = LogManager.getLogger(Agent.class);

	private static final String APPLICATION_NAME = "Screech Owl Agent";
	private static final Version APPLICATION_VERSION = Version.getInstance();
	private static final String APPLICATION_COPYRIGHT = "Copyright (C) 2016 by All Seeing IO";
	
	public static void run(boolean agentMode) {
		try {
			log.info("This application is being monitored by " + APPLICATION_NAME + " v" + APPLICATION_VERSION);
			log.info(APPLICATION_COPYRIGHT);
			
			Scheduler collectorScheduler = Scheduler.getInstance();
			collectorScheduler.setAgentMode(agentMode);
			collectorScheduler.register(new HelloWorldCollector("helloWorld"));
			collectorScheduler.register(new CollectorStatusCollector("collectorStatus"));
			collectorScheduler.start();
			
			for (Collector each : collectorScheduler.getList()) {
				log.trace(each.getKey()+", "+each.getStatus());
			}
		}
		catch (RuntimeException e) {
			log.fatal("unhandled runtime exeception", e);
			System.exit(1);
		}
	}
	
	public static void premain(String args, Instrumentation instrumentation) {
		run(true);
	}
	
	public static void main(String... args) {
		run(false);
	}
	
}
