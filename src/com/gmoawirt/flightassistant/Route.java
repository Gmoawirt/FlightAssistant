package com.gmoawirt.flightassistant;

import java.util.ArrayList;

public class Route {

	private String name;
	private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

	public Route(String name, Waypoint org, Waypoint dest) {
		setDeparture(org);
		setDestination(dest);
		setName(name);
	}

	public void setDeparture(Waypoint waypoint) {
		if (!waypoints.isEmpty()) {
			waypoints.set(0, waypoint);
		} else {
			waypoints.add(waypoint);
		}
	}

	public void setDestination(Waypoint waypoint) {
		waypoints.add(waypoints.size(), waypoint);
	}
	
	public Waypoint getDestination(){
		return waypoints.get(waypoints.size()-1);
	}
	
	public ArrayList<Waypoint> getWaypoints(){
		return waypoints;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
