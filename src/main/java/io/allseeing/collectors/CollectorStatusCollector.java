package io.allseeing.collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.allseeing.Collector;
import io.allseeing.Scheduler;

public class CollectorStatusCollector extends Collector {

	private static final Logger log = LogManager.getLogger(CollectorStatusCollector.class);
	
	public CollectorStatusCollector(String instance) {
		super(instance);
	}

	@Override
	public String defaultSchedule() {
		return "* * * * *";
	}

	@Override
	public boolean collect() {
		Scheduler collectors = Scheduler.getInstance();
		for (Collector each : collectors.getList()) {
			log.debug(each.getKey()+", "+each.getStatus());
			
			try {
				Thread.sleep(1000 * 90);
			}
			catch (Exception e) {
				// do nothing
			}
		}
		
		return true;
	}
	
	

}
