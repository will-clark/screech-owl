package io.allseeing;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Scheduler {

	private static final Logger log = LogManager.getLogger(Scheduler.class);
	
	private static Scheduler instance;
	private static Object mutex = new Object();
	
	private it.sauronsoftware.cron4j.Scheduler scheduler;
	private boolean agentMode = true;
	private boolean shutdownRequested = false;
	private List<Collector> list = new ArrayList<Collector>(0);
	
	public static Scheduler getInstance() {
		if (instance == null) {
			synchronized (mutex) {
				if (instance == null) {
					instance = new Scheduler();
				}
			}
		}
		return instance;
	}
	
	public void setAgentMode(boolean agentMode) {
		this.agentMode = agentMode;
	}
	
	public List<Collector> getList() {
		return list;
	}
	
	public void register(Collector collector) {
		scheduler.schedule(collector.getSchedule(), collector.getTask());
		list.add(collector);
	}
	
	private Scheduler() {
		scheduler = new it.sauronsoftware.cron4j.Scheduler();
		scheduler.setDaemon(true);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("shutting down the scheduler");

				shutdownRequested = true;
			}
		});		
	}
	
	public void start() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				scheduler.start();
				
				while (true) {
					if (shutdownRequested && scheduler.isStarted()) {
						scheduler.stop();
					}

					try {
						Thread.sleep(1000);
					}
					catch (Exception e) {
						// do nothing
					}
				}				
			}
		}, "Collector Scheduler");
		
		// if we attached as an agent; we want to shutdown whenever the main program does
		thread.setDaemon(agentMode);
		
		thread.start();
	}
	
}
