package com.gmoawirt.flightassistant;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class WaypointManager extends EntryManager {

	public static final String Waypoint_LANDING = "Landing";
	public static final String Waypoint_TAKEOFF = "Takeoff";
	public static final String Waypoint_TOUCH_AND_GO = "Touch and Go";

	// Database fields
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_AIRFIELD, MySQLiteHelper.COLUMN_ICAO, MySQLiteHelper.COLUMN_DESCRIPTION,
			MySQLiteHelper.COLUMN_LONGITUDE, MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_ALTITUDE };

	public WaypointManager(Context context) {
		super(context);
	}

	public void createWaypoint(String airfield, String icao, String description, double longitude, double latitude, double altitude) {
		Log.i(this.getClass().getName(), "Writing new Waypoint " + icao);

		// TODO check for correct input

		ContentValues values = createValues(airfield, icao, description, longitude, latitude, altitude);

		database.insert(MySQLiteHelper.TABLE_WAYPOINTS, null, values);

	}

	public void deleteWaypoint(Waypoint waypoint) {
		long id = waypoint.getId();
		Log.i(this.getClass().getName(), "Waypoint deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_WAYPOINTS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void deleteWaypointById(long id) {
		Log.i(this.getClass().getName(), "Waypoint deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_WAYPOINTS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public void editWaypoint(Long id, String airfield, String icao, String description, double longitude, double latitude, double altitude) {
		Log.i(this.getClass().getName(), "Waypoint edited with id: " + id);

		ContentValues values = createValues(airfield, icao, description, longitude, latitude, altitude);

		database.update(MySQLiteHelper.TABLE_WAYPOINTS, values, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public Waypoint getWaypoint(long id) {

		Log.i(this.getClass().getName(), "Query for id " + id + " with " + MySQLiteHelper.COLUMN_ID + " = " + id);

		Cursor cursor = database.query(MySQLiteHelper.TABLE_WAYPOINTS, allColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Waypoint waypoint = cursorToWaypoint(cursor);
		return waypoint;

	}

	public Waypoint getWaypointByColumn(String column, String value) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_WAYPOINTS, allColumns, column + " = '" + value + "'" , null, null, null, null);
		if (cursor.moveToFirst()) {
			Waypoint waypoint = cursorToWaypoint(cursor);
			return waypoint;
		} else {
			return null;
		}
	}

	public List<Waypoint> getAllWaypoints(String orderBy) {
		List<Waypoint> waypoints = new ArrayList<Waypoint>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_WAYPOINTS, allColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Waypoint waypoint = cursorToWaypoint(cursor);
			waypoints.add(waypoint);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return waypoints;
	}

	private Waypoint cursorToWaypoint(Cursor cursor) {
		return new Waypoint(cursor.getLong(0), cursor.getString(2), cursor.getString(1), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5),
				cursor.getDouble(6));
	}

	private ContentValues createValues(String airfield, String icao, String description, double longitude, double latitude, double altitude) {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_AIRFIELD, airfield);
		values.put(MySQLiteHelper.COLUMN_ICAO, icao);
		values.put(MySQLiteHelper.COLUMN_DESCRIPTION, description);
		values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);
		values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
		values.put(MySQLiteHelper.COLUMN_ALTITUDE, altitude);
		return values;

	}

}
