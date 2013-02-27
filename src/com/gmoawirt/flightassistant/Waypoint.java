package com.gmoawirt.flightassistant;

public class Waypoint implements Comparable<Waypoint> {

	private long id;
	private String icao;
	private String name;
	private String description;
	private double latitude;
	private double longitude;
	private double altitude;
	private double distance;

	public Waypoint(Long id, String icao, String name, String description, double longitude, double latitude, double altitude) {

		this.id = id;
		this.icao = icao;
		this.name = name;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String icao) {
		this.icao = icao;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(Waypoint w) {
		if (this.getDistance() < w.getDistance())
			return -1;
		else if (this.getDistance() == w.getDistance())
			return 0;
		else
			return 1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

}
