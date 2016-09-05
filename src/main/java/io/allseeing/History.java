package io.allseeing;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.allseeing.Collector.State;
import io.allseeing.persistence.HistoryPersister;

@DatabaseTable(tableName = "HISTORY")
public class History {

	private static final Logger log = LogManager.getLogger(History.class);
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(columnName = "COLLECTION_ID")
	private long collectionId;
	
	@DatabaseField(canBeNull = false)
	private String collector;
	
	@DatabaseField(canBeNull = false)
	private String instance;
	
	@DatabaseField(canBeNull = false)
	private String key;
	
	@DatabaseField(canBeNull = false)
	private String schedule;
	
	@DatabaseField(canBeNull = false)
	private State state;
	
	@DatabaseField
	private String errorMessage;
	
	@DatabaseField		
	private Long startTime;
	
	@DatabaseField
	private Long finishTime;
	
	@DatabaseField
	private Long updateTime;
	
	@DatabaseField
	private Long nextTime;
	
	@DatabaseField
	private Long duration;
	
	public History() {}
	
	public History(Status status) {
		this.collector = status.getCollector().getName();
		this.instance = status.getCollector().getInstance();
		this.key = status.getCollector().getKey();
		this.schedule = status.getCollector().getSchedule();
		this.collectionId = status.getCollectionId();
		this.state = status.getState();
		this.errorMessage = status.getErrorMessage();
		this.startTime = status.getStartTime();
		this.finishTime = status.getFinishTime();
		this.updateTime = status.getUpdateTime();
		this.nextTime = status.getNextTime();
		this.duration = status.getDuration();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(long collectionId) {
		this.collectionId = collectionId;
	}
	
	public String getCollector() {
		return collector;
	}
	
	public void setCollector(String collector) {
		this.collector = collector;
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
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getFinishTime() {
		return finishTime;
	}
	
	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	
	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public void calcDuration() {
		this.duration = Math.max(0, (finishTime != null ? finishTime : System.currentTimeMillis()) - startTime);
	}
	
	public long getDuration() {
		return duration;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("History=[");		
		sb.append("collector=").append(collector).append(",");
		sb.append("instance=").append(instance).append(",");
		sb.append("key=").append(key).append(",");
		sb.append("schedule=").append(schedule).append(",");
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
	
	public void save() {
		Persistence persist = null;
		try {
			persist = new Persistence();
			persist.open();
			
			new HistoryPersister(persist).save(this);
		}
		catch (Exception e) {
			log.fatal("Unable to save collector history to the database", e);
			System.exit(1);
		}
		finally {
			if (persist != null) {
				persist.close();
			}
		}
	}
	
}
