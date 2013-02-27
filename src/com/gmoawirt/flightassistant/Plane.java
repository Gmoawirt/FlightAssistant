package com.gmoawirt.flightassistant;

public class Plane {

	boolean airborne;
	private String name;

	private double takeoffGroundspeed; // in kph
	private double takeoffDeltaAltitude; // equals 1.0 m/s

	private double landingGroundspeed; // in kph
	private double landingDeltaMargin; // equals 0.1 m/s

	public Plane(boolean airborne, String name, double takeoffGroundspeed, double takeoffDeltaAltitude,
			double landingGroundspeed, double landingDeltaMargin) {

		this.airborne = airborne;
		this.name = name;
		this.takeoffDeltaAltitude = takeoffDeltaAltitude;
		this.takeoffGroundspeed = takeoffGroundspeed;
		this.landingDeltaMargin = landingDeltaMargin;
		this.landingGroundspeed = landingGroundspeed;

	}

	public double getTakeoffGroundspeed() {
		return takeoffGroundspeed;
	}

	public void setTakeoffGroundspeed(double takeoffGroundspeed) {
		this.takeoffGroundspeed = takeoffGroundspeed;
	}

	public double getTakeoffDeltaAltitude() {
		return takeoffDeltaAltitude;
	}

	public void setTakeoffDeltaAltitude(double takeoffDeltaAltitude) {
		this.takeoffDeltaAltitude = takeoffDeltaAltitude;
	}

	public double getLandingGroundspeed() {
		return landingGroundspeed;
	}

	public void setLandingGroundspeed(double landingGroundspeed) {
		this.landingGroundspeed = landingGroundspeed;
	}

	public double getLandingDeltaMargin() {
		return landingDeltaMargin;
	}

	public void setLandingDeltaMargin(double landingDeltaMargin) {
		this.landingDeltaMargin = landingDeltaMargin;
	}	
}
