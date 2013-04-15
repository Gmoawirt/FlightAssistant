package com.gmoawirt.flightassistant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

//Monitors the Flight, checking for State Changes

public class StateManager {

	public static final int STATE_INIT = 1;
	public static final int STATE_BEFORE_TAKEOFF = 2;
	public static final int STATE_TOUCH_AND_GO = 3;
	public static final int STATE_DEPARTURE = 4;
	public static final int STATE_CRUISE = 5;
	public static final int STATE_APPROACH = 6;
	public static final int STATE_BEFORE_LANDING = 7;

	private static final int CACHE_CAPACITY = 2;

	private LogManager logManager;
	private static final StateManager stateManagerInstance = new StateManager();
	private Plane plane;

	private double longitude;
	private double altitude;
	private double latitude;
	private double groundspeed;
	private double previousSpeed;

	private Context context;
	private int state;

	private LRUCache<Double, Double> deltaAltitudeCache;
	private LRUCache<Double, Double> deltaGroundspeedCache;

	private StateManager() {

		// deltaAltitudeCache = new LRUCache(CACHE_CAPACITY);
		// deltaGroundspeedCache = new LRUCache(CACHE_CAPACITY);

		deltaAltitudeCache = new LRUCache<Double, Double>(CACHE_CAPACITY);
		deltaGroundspeedCache = new LRUCache<Double, Double>(CACHE_CAPACITY);
		this.plane = PlaneManager.getInstance().getActivePlane();

	}

	public static StateManager getInstance() {
		return stateManagerInstance;
	}

	public void update(double longitude, double latitude, double altitude, double groundspeed) {

		Log.i("StateManager", "Updating current state");

		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.groundspeed = groundspeed;

		deltaAltitudeCache.put(Math.random(), altitude);
		deltaGroundspeedCache.put(Math.random(), groundspeed);

		MainActivity.updateAltitudeDelta(getDeltaAltitude());
		checkForStateChange();
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setLogManager(LogManager lm) {
		this.logManager = lm;
	}

	private void checkForStateChange() {

		Log.i("StateManager", "Checking for State Change");

		switch (this.state) {

		case STATE_INIT:
			checkForInit();
			break;

		case STATE_BEFORE_TAKEOFF:
			checkForTakeoff();
			break;

		case STATE_CRUISE:
			checkForApproach();
			break;

		case STATE_BEFORE_LANDING:
			checkForLanding();
			break;

		case STATE_TOUCH_AND_GO:
			checkForTouchAndGo();
			break;
		}

	}

	public double getDeltaAltitude() {

		double delta = 0d;
		double high = 0d;
		double low = 0d;
		int i = 1;

		for (Double d : deltaAltitudeCache.values()) {
			if (i == 1) {
				low = d;
			} else if (i == CACHE_CAPACITY) {
				high = d;
			}

			i++;
		}

		if (high != (double) CACHE_CAPACITY) {
			delta = high - low;
		} else {
			delta = 0;
		}

		return delta;

	}

	private double getDeltaGroundspeed() {

		double delta = 0d;
		double high = 0d;
		double low = 0d;

		for (int i = 0; i < deltaGroundspeedCache.size(); i++) {
			if (i == 0) {
				low = deltaGroundspeedCache.get(i);
			} else if (i == CACHE_CAPACITY) {
				high = deltaGroundspeedCache.get(i);
			}
		}

		if (high != (double) CACHE_CAPACITY) {
			delta = high - low;
		} else {
			delta = 0;
		}
		return delta;
	}

	private void checkForInit() {		
		//Check if airborne or not
		
		if (this.altitude > (GpsHelper.getNearestWaypoint(this.longitude, this.latitude, this.context).getAltitude() + 50d)) {
			//airborne
			Log.i(this.getClass().getName(), "Setting state to before landing");
			setState(STATE_BEFORE_LANDING);		
			Toast.makeText(context, "State set to before landing", Toast.LENGTH_LONG).show();
		} else {
			//not airborne
			Log.i(this.getClass().getName(), "Setting state to before takeoff");
			setState(STATE_BEFORE_TAKEOFF);
			Toast.makeText(context, "State set to before takeoff", Toast.LENGTH_LONG).show();
		}
	}

	private void checkForTakeoff() {

		// Check if plane is taking off
		// true if groundspeed > 100 km/h, state = beforeTakeoff, Delta Altitude
		// > 1,0 m/s

		if ((this.groundspeed > plane.getTakeoffGroundspeed()) && (state == STATE_BEFORE_TAKEOFF) && (getDeltaAltitude() > plane.getTakeoffDeltaAltitude())) {			
			
			String nearest = GpsHelper.getNearestWaypoint(this.longitude, this.latitude, this.context).getIcao();
			SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
			String time = timeFormat.format(new Date(System.currentTimeMillis()));
			
			Log.i(this.getClass().getName(), "Setting state to before landing");
			MainActivity.updateLastLog("Takeoff | " + nearest + " | " + time);	

			setState(STATE_BEFORE_LANDING);
			logManager.open();
			logManager.createLog(System.currentTimeMillis(), LogManager.LOG_TAKEOFF, nearest);
			logManager.close();
		}

	}

	private void checkForApproach() {

		// Check if plane is approaching destination
		// true if range to destination < 15 kilometers
		//
		Log.i(this.getClass().getName(), "Setting state to before landing");
		this.state = STATE_BEFORE_LANDING;

	}

	private void checkForLanding() {

		// Check if plane is landing
		// true if groundspeed < 70 km/h, state = beforeLanding, Altitude change
		// > -2,5d

		if ((this.groundspeed < plane.getLandingGroundspeed()) && (state == STATE_BEFORE_LANDING) && (getDeltaAltitude() > -(plane.getLandingDeltaMargin()))) {
			Log.i(this.getClass().getName(), "Setting state to checking touch and go");
			setState(STATE_TOUCH_AND_GO);
			previousSpeed = this.groundspeed;
		}

	}

	private void checkForTouchAndGo() {

		// Check if plane is going around
		// true if groundspeed > landing speed, state = checkForTouchAndGo
		
		String nearest = GpsHelper.getNearestWaypoint(this.longitude, this.latitude, this.context).getIcao();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
		String time = timeFormat.format(new Date(System.currentTimeMillis()));	

		if ((this.groundspeed > previousSpeed) && (state == STATE_TOUCH_AND_GO)) {
			
			logManager.open();
			Log.i(this.getClass().getName(), "Touch and Go!");		
			MainActivity.updateLastLog("Touch and Go | " + nearest + " | " + time);			
			
			logManager.createLog(System.currentTimeMillis(), LogManager.LOG_TOUCH_AND_GO,
					nearest);
			logManager.close();
		} else {
			logManager.open();
			Log.i(this.getClass().getName(), "Normal Landing");
			MainActivity.updateLastLog("Normal Landing | " + nearest + " | " + time);	
			
			logManager.createLog(System.currentTimeMillis(), LogManager.LOG_LANDING, nearest);
			logManager.close();
		}

		Log.i(this.getClass().getName(), "Setting state to before takeoff");
		setState(STATE_BEFORE_TAKEOFF);

	}

	public void setContext(Context context) {
		this.context = context;
	}

}
