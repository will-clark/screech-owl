package io.allseeing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.allseeing.Collector.State;
import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

public abstract class Collector {

	public enum State {
		SLEEPING, RUNNING, SUCCESS, ERROR, CRASHED, HUNG;
	}

	private static final Logger log = LogManager.getLogger(Collector.class);
	
	private String name;	
	private String instance;	
	private String key;
	private String schedule;
	
	private Status status;
	
	private Predictor predictor;
	private Task task;
	
	public abstract String defaultSchedule();	
	public abstract boolean collect();
	
	public long hungThreshold() { return 1000 * 60 * 10; }
	
	protected Collector() {}
	
	public Collector(String instance) {
		this(instance, null);
	}
	
	public Collector(String instance, String schedule) {
		this.name = this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().lastIndexOf("Collector"));
		this.instance = instance;
		this.key = this.name + "." + this.instance;				
		this.schedule = (schedule != null) ? schedule : defaultSchedule();
		this.predictor = new Predictor(this.schedule);
		
		status = new Status(this);
		status.setState(State.SLEEPING);
		status.setNextTime(predictor.nextMatchingTime());
		status.save();
		
		final Collector collector = this;
		
		this.task = new Task() {
			@Override
			public void execute(TaskExecutionContext context) throws RuntimeException {				
				// do not allow concurrent executions of the same collector instance
				if (status.getState() == State.RUNNING || status.getState() == State.HUNG) {
					return;
				}
				
				long collectionId = System.currentTimeMillis();
				
				status = new Status(collector);
				status.setCollectionId(collectionId);
				status.setState(State.RUNNING);
				status.setStartTime(System.currentTimeMillis());
				status.setNextTime(predictor.nextMatchingTime());
				status.save();
				
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							status.monitor();
							
							if (collect()) {
								status.setState(State.SUCCESS);
							}
							else {
								status.setState(State.ERROR);
							}
							
							status.setFinishTime(System.currentTimeMillis());
							status.calcDuration();
							status.setNextTime(predictor.nextMatchingTime());
							status.save();
						}
						catch (Exception e) {
							status.setState(State.CRASHED);
							status.setErrorMessage(e.getMessage());
							status.save();
						}						
					}
				}, getKey()+".collect");
				
				thread.start();
				
				try {
					thread.join();
				}
				catch (InterruptedException e) {
					// do nothing
				}
				
				thread = null;
								
				status = new Status(collector);
				status.setState(State.SLEEPING);
				status.setNextTime(predictor.nextMatchingTime());
				status.save();
			}	
		};
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getInstance() {
		return instance;
	}
	
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getSchedule() {
		return schedule;
	}
	
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	public Task getTask() {
		return task;
	}
	
	public Status getStatus() {
		return status;
	}
	
}
