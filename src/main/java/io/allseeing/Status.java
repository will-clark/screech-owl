package io.allseeing;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.allseeing.Collector.State;

public class Status {

	private static final Logger log = LogManager.getLogger(Status.class);
	
	private Collector collector;
	private long collectionId;
	private State state;
	private String errorMessage;
	private Long startTime;
	private Long finishTime;
	private Long updateTime;
	private Long nextTime;
	private Long duration;
	
	public Status() {}
	
	public Status(Collector collector) {
		this.collector = collector;
	}
	
	public Status(Status status) {
		this.collector = status.collector;
		this.collectionId = status.collectionId;
		this.state = status.state;
		this.errorMessage = status.errorMessage;
		this.startTime = status.startTime;
		this.finishTime = status.finishTime;
		this.updateTime = status.updateTime;
		this.nextTime = status.nextTime;
		this.duration = status.duration;
	}
	
	public Collector getCollector() {
		return collector;
	}
	
	public void setCollector(Collector collector) {
		this.collector = collector;
	}
	
	public long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(long collectionId) {
		this.collectionId = collectionId;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public Long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	
	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Long getFinishTime() {
		return finishTime;
	}
	
	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}
	
	public Long getNextTime() {
		return nextTime;
	}

	public void setNextTime(Long nextTime) {
		this.nextTime = nextTime;
	}

	public void calcDuration() {
		this.duration = Math.max(0, (finishTime != null ? finishTime : System.currentTimeMillis()) - (startTime != null ? startTime : -1));
	}
	
	public Long getDuration() {
		return duration;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Status=[");		
		sb.append("collector=").append(collector.getKey()).append(",");
		sb.append("collectionId=").append(collectionId).append(",");
		sb.append("state=").append(state).append(",");
		sb.append("errorMessage=").append(errorMessage).append(",");		
		sb.append("startTime=").append(startTime != null ? new Date(startTime) : null).append(",");
		sb.append("updateTime=").append(updateTime != null ? new Date(updateTime) : null).append(",");
		sb.append("finishTime=").append(finishTime != null ? new Date(finishTime) : null).append(",");
		sb.append("nextTime=").append(nextTime != null ? new Date(nextTime) : null).append(",");
		sb.append("duration=").append(duration);			
		sb.append("]");
		return sb.toString();
	}
	
	public void monitor() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				setUpdateTime(System.currentTimeMillis());
				
				while (state == State.RUNNING || state == State.HUNG) {
					calcDuration();
					
					if (duration > collector.hungThreshold()) state = State.HUNG;
					
					if (getUpdateTime() + 60 * 1000 < System.currentTimeMillis()) {
						setUpdateTime(System.currentTimeMillis());
						save();
					}
					
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		}, collector.getKey()+".monitor");
		
		thread.start();
	}
	
	public void save() {
		History history = new History(this);
		history.save();
	}
}
