package com.gmoawirt.flightassistant;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

//Manage the Flight
//Create a new Flight
//Add a Plane to the Flight
//Set a Route
//Set the Flight Status
//Write the Log

public class FlightManager extends Service implements LocationListener, SensorEventListener {

	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		FlightManager getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return FlightManager.this;
		}
	}

	public static final String LOG_TAKEOFF = "Takeoff";
	public static final String LOG_LANDING = "Landing";
	public static final String LOG_TOUCH_AND_GO = "Touch and Go";

	private Sensor sensorPressure;
	private SensorManager sensorManager;

	// Kalman filter for smoothing the measured pressure.
	private KalmanFilter pressureFilter;
	// Time of the last measurement in seconds since boot; used to compute time
	// since last update so that the Kalman filter can estimate rate of change.
	private double lastMeasurementTime;

	private static final double KF_VAR_ACCEL = 0.0075; // Variance of pressure
														// acceleration noise
														// input.
	private static final double KF_VAR_MEASUREMENT = 0.05; // Variance of
															// pressure
															// measurement
															// noise.

	private static LocationManager locationManager;
	private StateManager stateManager;
	private LogManager logManager;

	private double longitude;
	private double latitude;
	private double altitude;
	private double groundspeed;
	private double pressure;

	private SharedPreferences sharedPref;
	private boolean useMockGPS;

	private boolean isRunning = false;

	public void onCreate() {
		super.onCreate();

		Log.i("FlightManager", "(OnCreate)");

		pressureFilter = new KalmanFilter(KF_VAR_ACCEL);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		useMockGPS = sharedPref.getBoolean("setting_mock_location", true);

	}

	public void prepareForTakeoff() {
		Log.i("FlightManager", "Setting state " + stateManager.STATE_INIT);
		stateManager.setState(stateManager.STATE_INIT);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i("FlightManager", "FlightManager Service created and running (onStartCommand)");

		isRunning = true;

		// //////////////////////////////////
		// FOR TESTING
		// Create a Plane
		PlaneManager.getInstance().addPlane(false, "C142", Double.parseDouble(sharedPref.getString("setting_groundspeed_takeoff", "100")),
				Double.parseDouble(sharedPref.getString("setting_delta_altitude_takeoff", "1")),
				Double.parseDouble(sharedPref.getString("setting_groundspeed_landing", "70d")),
				Double.parseDouble(sharedPref.getString("setting_delta_altitude_margin", "4d")));
		// PlaneManager.getInstance().addPlane(PositionAndSpeedData.getInstance(),
		// false, "EV97", 100d, 0.5d, 55d, 2.5d);
		PlaneManager.getInstance().setupPlane();

		// Create the Log Manager
		logManager = new LogManager(this);

		// Save the State Manager
		stateManager = StateManager.getInstance();
		stateManager.setLogManager(logManager);
		stateManager.setContext(this);

		isRunning = true;

		// Reset Kalman Filter
		pressureFilter.reset(1013.0912);
		lastMeasurementTime = SystemClock.elapsedRealtime() / 1000.;
		sensorManager.registerListener(this, sensorPressure, SensorManager.SENSOR_DELAY_GAME);

		// Subscribe to Position Listener
		PositionManager.setPositionListener(this, this, true, useMockGPS);

		prepareForTakeoff();
		
		NotificationHelper.updateNotification(this, NotificationHelper.STATUS_RUNNING);

		return Service.START_STICKY;
	}

	public void onDestroy() {
		if (isRunning) {
			isRunning = false;
			PositionManager.setPositionListener(this, this, false, useMockGPS);
		}
		NotificationHelper.cancelNotification(this);
		Log.i("FlightManager", "FlightManager Service being destroyed.");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("FlightManager", "(onBind)");

        NotificationHelper.updateNotification(this, NotificationHelper.STATUS_NOT_RUNNING);
		return mBinder;
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.i("FlightManager", "Location has changed");

		this.longitude = location.getLongitude();
		this.latitude = location.getLatitude();
		if (useMockGPS) {
			// altitude and groundspeed via Mock-GPS
			this.altitude = location.getAltitude();
			this.groundspeed = convertKnotsToKph(location.getSpeed());
		} else {
			// altitude via Barometer:
			double qnh = Double.parseDouble(sharedPref.getString("setting_qnh", "1013.25"));
			this.altitude = getElevation(pressureFilter.getXAbs(), qnh);
			
			// groundspeed without conversion
			this.groundspeed = location.getSpeed();
		}

		MainActivity.updateText(longitude, latitude, altitude, groundspeed);
		StateManager.getInstance().update(longitude, latitude, altitude, groundspeed);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public boolean getRunning() {
		return isRunning;
	}

	public static LocationManager getLocationManager() {
		return locationManager;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// Barometer Part
		final double curr_measurement_time = SystemClock.elapsedRealtime() / 1000.;
		final double dt = curr_measurement_time - lastMeasurementTime;
		pressureFilter.update(event.values[0], KF_VAR_MEASUREMENT, dt);
		lastMeasurementTime = curr_measurement_time;
	}

	private double getElevation(double qfe, double qnh) {
		double a = 0.1902612d;
		double b = 8.417168e-5d;

		double altitude = (Math.pow(qnh, a) - Math.pow(qfe, a)) / b;
		return altitude;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}	

	private double convertKnotsToKph(double knots) {
		return knots * 1.852d;
	}

}
