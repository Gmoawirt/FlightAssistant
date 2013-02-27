package com.gmoawirt.flightassistant;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class WaypointDialogActivity extends Activity {

	private EditText editIcao;
	private EditText editAirfield;
	private EditText editLongitude;
	private EditText editLatitude;
	private EditText editAltitude;
	private EditText editDescription;

	private WaypointManager datasource;
	private long waypointId;
	private boolean isNew;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit_waypoint);

		isNew = getIntent().getBooleanExtra("isNew", false);

		if (isNew) {

			setTitle("Add new waypoint");

		} else {

			setTitle("Edit waypoint");

			waypointId = getIntent().getLongExtra("waypoint_id", 0);

			datasource = new WaypointManager(getBaseContext());
			datasource.open();
			Waypoint waypoint = datasource.getWaypoint(waypointId);
			datasource.close();

			editIcao = (EditText) findViewById(R.id.edittext_icao);
			editIcao.setText(waypoint.getIcao());
			editAirfield = (EditText) findViewById(R.id.edittext_airfield);
			editAirfield.setText(waypoint.getName());
			editLongitude = (EditText) findViewById(R.id.edittext_longitude);
			editLongitude.setText(String.valueOf(waypoint.getLongitude()));
			editLatitude = (EditText) findViewById(R.id.edittext_latitude);
			editLatitude.setText(String.valueOf(waypoint.getLatitude()));
			editAltitude = (EditText) findViewById(R.id.edittext_altitude);
			editAltitude.setText(String.valueOf(waypoint.getAltitude()));
			editDescription = (EditText) findViewById(R.id.edittext_description);
			editDescription.setText(Html.fromHtml(waypoint.getDescription()));

		}

	}

	private boolean checkValues(String icao, String airfield, String description, String longitude, String latitude, String altitude) {

		boolean isError = false;

		// check for ICAO-Code length
		if (icao.length() > 4 || icao.length() < 2) {
			isError = true;
			Toast.makeText(getApplicationContext(), R.string.wrong_icao, Toast.LENGTH_LONG).show();
		}

		// check for Airfield length
		if (airfield.length() > 50 || airfield.length() < 1) {
			isError = true;
			Toast.makeText(getApplicationContext(), R.string.wrong_airfield, Toast.LENGTH_LONG).show();
		}

		// check for Description length
		if (description.length() > 500) {
			isError = true;
			Toast.makeText(getApplicationContext(), R.string.wrong_description, Toast.LENGTH_LONG).show();
		}

		// check for longitude format
		if (longitude.length() > 20 || longitude.length() < 3 || !isNumeric(longitude)) {
			isError = true;
			Toast.makeText(getApplicationContext(), R.string.wrong_longitude, Toast.LENGTH_LONG).show();
		}

		// check for latitude format
		if (latitude.length() > 20 || latitude.length() < 3 || !isNumeric(latitude)) {
			isError = true;
			Toast.makeText(getApplicationContext(), R.string.wrong_latitude, Toast.LENGTH_LONG).show();
		}

		// check for altitude format
		if (altitude.length() > 20 || altitude.length() < 1 || !isNumeric(altitude)) {
			isError = true;
			Toast.makeText(getApplicationContext(), R.string.wrong_altitude, Toast.LENGTH_LONG).show();
		}

		if (isError) {
			return false;
		} else {
			return true;
		}

	}

	private boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional
												// '-' and decimal.
	}

	private boolean notInDb(String icao, WaypointManager datasource) {
		if (datasource.getWaypointByColumn(MySQLiteHelper.COLUMN_ICAO, icao) == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_delete_waypoint, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		// Clicked on Edit/New Waypoint
		case R.id.waypoint_save:

			String icao = ((EditText) findViewById(R.id.edittext_icao)).getText().toString();
			String airfield = ((EditText) findViewById(R.id.edittext_airfield)).getText().toString();
			String description = ((EditText) findViewById(R.id.edittext_description)).getText().toString();
			String longitude = ((EditText) findViewById(R.id.edittext_longitude)).getText().toString();
			String latitude = ((EditText) findViewById(R.id.edittext_latitude)).getText().toString();
			String altitude = ((EditText) findViewById(R.id.edittext_altitude)).getText().toString();
			icao = icao.toUpperCase(Locale.US);

			if (checkValues(icao, airfield, description, longitude, latitude, altitude)) {
				datasource = new WaypointManager(getBaseContext());
				datasource.open();
				if (isNew) {
					if (notInDb(icao, datasource)) {
						// Waypoint with same ICAO does not already exist
						datasource.createWaypoint(airfield, icao, description, Double.parseDouble(longitude), Double.parseDouble(latitude),
								Double.parseDouble(altitude));
						Toast.makeText(getApplicationContext(), R.string.waypoint_added, Toast.LENGTH_LONG).show();
						datasource.close();
						finish();
					} else {
						// Waypoint with same ICAO already exists
						Toast.makeText(getApplicationContext(), R.string.icao_already_exists, Toast.LENGTH_LONG).show();
						datasource.close();
					}
				} else {
					datasource.editWaypoint(waypointId, airfield, icao, description, Double.parseDouble(longitude), Double.parseDouble(latitude),
							Double.parseDouble(altitude));
					Toast.makeText(getApplicationContext(), R.string.waypoint_edited, Toast.LENGTH_LONG).show();
					datasource.close();
					finish();
				}

			}
			return true;
			// Clicked on Cancel
		case R.id.waypoint_cancel:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}