package com.gmoawirt.flightassistant;

import java.util.ArrayList;
import java.util.Collections;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

//Manage the Flight
//Create a new Flight
//Add a Plane to the Flight
//Set a Route
//Set the Flight Status
//Write the Log

public class FlightManager extends Service implements LocationListener {

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

	private static final int CACHE_CAPACITY = 5;
	public static final int NOTIFICATION_ID = 0;

	private static LocationManager locationManager;
	private StateManager stateManager;
	private LogManager logManager;
	private Plane plane;
	private Route route;
	private int planeId;

	private double longitude;
	private double latitude;
	private double altitude;
	private double groundspeed;
	private NotificationManager mNotificationManager;
	private SharedPreferences sharedPref;
	private boolean useMockGPS;

	private boolean isRunning = false;

	public void onCreate() {
		super.onCreate();

		Log.i("FlightManager", "(OnCreate)");
		
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
		PlaneManager.getInstance().addPlane(false, "C142", 100d, 0.5d, 70d, 4d);
		// PlaneManager.getInstance().addPlane(PositionAndSpeedData.getInstance(),
		// false, "EV97", 100d, 0.5d, 55d, 2.5d);
		PlaneManager.getInstance().setupPlane();

		// Create a Route
		// Waypoint org = new Waypoint("EDMF", 48.5111296d, 13.347400d,
		// 411.10356d);
		// Waypoint dest = new Waypoint("EDME", 48.3961129d, 12.722763d,
		// 410.17965d);

		// RouteManager.getInstance().addRoute("Fernflug", org, dest);
		// RouteManager.getInstance().setupRoute();

		// Load the Plane
		plane = PlaneManager.getInstance().getActivePlane();

		// Load the Route
		route = RouteManager.getInstance().getActiveRoute();

		// Create the Log Manager
		logManager = new LogManager(this);
		logManager.open();

		// Save the State Manager
		stateManager = StateManager.getInstance();
		stateManager.setLogManager(logManager);
		stateManager.setContext(this);

		isRunning = true;

		//Subscribe to Position Listener
		setPositionListener(this, this,true);

		prepareForTakeoff();

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle("FlightAssistant")
				.setContentText("Background service running.");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		// mId (NOTIFICATION_ID) allows you to update the notification later on.
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

		return Service.START_NOT_STICKY;
	}

	public void onDestroy() {
		if (isRunning) {
			isRunning = false;
			logManager.close();
			setPositionListener(this, this, false);
		}
		Log.i("FlightManager", "FlightManager Service being destroyed.");
	}

	public void setPositionListener(LocationListener lm, Context context, boolean enable) {

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		if (enable) {
			
			Log.i("FlightManager", "useMockGPS = " + useMockGPS);
			if (useMockGPS) {
				locationManager.removeTestProvider("testProvider");
				locationManager.addTestProvider("testProvider", false, true, false, false, true, true, false, 0, 5);
				locationManager.setTestProviderEnabled("testProvider", true);
				locationManager.requestLocationUpdates("testProvider", 1000, 0, lm);

			} else {
				locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 0, lm);
			}
		} else {
			locationManager.removeUpdates(lm);
			Log.i("FlightManager", "Removing Position Updates");
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("FlightManager", "(onBind)");

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(android.R.drawable.stat_notify_error)
				.setContentTitle("FlightAssistant").setContentText("Background service NOT running.");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		// mId (NOTIFICATION_ID) allows you to update the notification later on.
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

		return mBinder;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.i("FlightManager", "Location has changed");

		this.longitude = location.getLongitude();
		this.latitude = location.getLatitude();
		this.altitude = location.getAltitude();
		this.groundspeed = location.getSpeed();

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

	public double getDistanceToDestination() {

		return GPSHelper.gps2m(this.latitude, this.longitude, RouteManager.getInstance().getActiveRoute().getDestination().getLatitude(), RouteManager
				.getInstance().getActiveRoute().getDestination().getLongitude());
	}

	public static LocationManager getLocationManager() {
		return locationManager;
	}

}
