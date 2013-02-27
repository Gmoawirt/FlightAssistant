package com.gmoawirt.flightassistant;

public class Flightlog {
	private long id;
	private long timestamp;
	private String event;
	private String waypoint;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getWaypoint() {
		return waypoint;
	}

	public void setWaypoint(String waypoint) {
		this.waypoint = waypoint;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return waypoint;
	}
}