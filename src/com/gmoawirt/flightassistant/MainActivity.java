package com.gmoawirt.flightassistant;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gmoawirt.flightassistant.FlightManager.LocalBinder;

public class MainActivity extends Activity implements LocationListener {

	private static TextView textViewLatitude;
	private static TextView textViewLongitude;
	private static TextView textViewAltitude;
	private static TextView textViewGroundspeed;
	private static TextView textViewState;
	private static TextView satelliteStatus;
	private static Button startFlight;
	private static Button endFlight;
	
	private Location location = null;

	private FlightManager flightManager;
	private SharedPreferences sharedPref;
	private boolean useMockGPS;

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			flightManager = binder.getService();
			Log.i("MainActivity", "Service has connected to Activity");
			flightManager.setPositionListener(MainActivity.this, MainActivity.this, true);
			setButtons(flightManager.getRunning());
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			Log.i("MainActivity", "(onServiceDisconnected)");
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		useMockGPS = sharedPref.getBoolean("setting_mock_location", true);

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		GpsSocketServer.getInstance();

		Log.i("MainActivity", "onCreate called");
		// Enable Position Provider
		ProviderManager.enableProvider(true, this, this);

		// Start the Flight Manager service
		Intent intent = new Intent(this, FlightManager.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		// //////////////////////////
		// UI
		// //////////////////////////
		startFlight = (Button) findViewById(R.id.button_start_flight);
		endFlight = (Button) findViewById(R.id.button_end_flight);
		textViewLatitude = (TextView) findViewById(R.id.latitude);
		textViewLongitude = (TextView) findViewById(R.id.longitude);
		textViewAltitude = (TextView) findViewById(R.id.altitude);
		textViewGroundspeed = (TextView) findViewById(R.id.groundspeed);
		textViewState = (TextView) findViewById(R.id.state);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("MainActivity", "(onResume)");
		Intent intent = new Intent(this, FlightManager.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unbindService(mConnection);
		Log.i("MainActivity", "(onPause)");
		flightManager.setPositionListener(this, this, false);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("MainActivity", "(onStart)");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
		Log.i(this.getClass().getName(), "Main Activity being destroyed");
		flightManager.setPositionListener(this, this, false);
		if (!flightManager.getRunning()) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(FlightManager.NOTIFICATION_ID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.edit_waypoints:
			Intent intent = new Intent(this, WaypointViewer.class);
			intent.putExtra("location", this.location);
			startActivity(intent);
			return true;
		case R.id.view_logs:
			startActivity(new Intent(this, LogViewer.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void startFlight(View view) {
		// Do something in response to button

		Log.i("MainActivity", "Starting Flight Button clicked");

		// Thread t = new Thread(new FlugplatzParser(this));
		// t.start();

		// Start Command the Flight Manager service
		Intent intent = new Intent(this, FlightManager.class);
		if (startService(intent) != null) {
			Toast.makeText(getApplicationContext(), "FlightManager Service started", Toast.LENGTH_LONG).show();
			setButtons(true);
		} else {
			Toast.makeText(getApplicationContext(), "FlightManager Service NOT started!", Toast.LENGTH_LONG).show();
		}

	}

	public void endFlight(View view) {
		// Do something in response to button

		Log.i("MainActivity", "End Flight Button clicked");

		// End the Flight Manager service
		Intent intent = new Intent(this, FlightManager.class);
		if (stopService(intent)) {
			Toast.makeText(getApplicationContext(), "FlightManager Service stopped", Toast.LENGTH_LONG).show();
			Log.i("MainActivity", "Service stopped");
			flightManager.setPositionListener(flightManager, flightManager, false);
			setButtons(false);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(FlightManager.NOTIFICATION_ID);

		} else {
			Toast.makeText(getApplicationContext(), "Service NOT stopped, maybe it's not running?", Toast.LENGTH_LONG).show();
			Log.i("MainActivity", "Service not stopped, not running?");
		}

	}

	public static void updateText(double longitude, double latitude, double altitude, double groundspeed) {
		textViewLongitude.setText(String.valueOf(longitude));
		textViewLatitude.setText(String.valueOf(latitude));
		textViewAltitude.setText(String.valueOf(altitude));
		textViewGroundspeed.setText(String.valueOf(groundspeed));
	}

	public static void updateState(String state) {
		textViewState.setText(state);
	}

	private void enableStartButton(boolean enable) {
		Button b = (Button) findViewById(R.id.button_start_flight);
		if (enable) {
			Log.i("MainActivity", "Enabling Button");
			b.setClickable(true);
			b.setText(R.string.start_button_enabled);
		} else {
			Log.i("MainActivity", "Disabling Button");
			b.setClickable(false);
			b.setText(R.string.start_button_disabled);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		
		this.location = location;

		satelliteStatus = (TextView) findViewById(R.id.satellite_status);		

		if (!useMockGPS) {
			//Real GPS
			if (location.getAccuracy() == 0.0f || location.getAccuracy() > 10f) {
				enableStartButton(false);
			} else {
				enableStartButton(true);
			}
			satelliteStatus.setText("Satellites: " + location.getExtras().get("satellites") + "/13, Accuracy " + location.getAccuracy());			
		} else {
			//Mock Provider enabled
			enableStartButton(true);
		}

		// String satellites = (String) location.getExtras().get("satellites");
		// satelliteStatus.setText(satellites + " satellites in use.");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		Log.i("Main Activity", "GPS status changed");

	}

	private void setButtons(boolean isRunning) {

		if (isRunning) {
			startFlight.setVisibility(View.GONE);
			endFlight.setVisibility(View.VISIBLE);
		} else {
			startFlight.setVisibility(View.VISIBLE);
			endFlight.setVisibility(View.GONE);
		}
	}

}
