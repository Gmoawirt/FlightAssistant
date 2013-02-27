package com.gmoawirt.flightassistant;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class LogManager extends EntryManager {

	public static final String LOG_LANDING = "Landing";
	public static final String LOG_TAKEOFF = "Takeoff";
	public static final String LOG_TOUCH_AND_GO = "Touch and Go";

	// Database fields
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_EVENT, MySQLiteHelper.COLUMN_WAYPOINT };

	public LogManager(Context context) {
		super(context);
	}

	public void createLog(long timestamp, String event, String waypoint) {
		Log.i(this.getClass().getName(), "Writing new Log: " + event + " at " + waypoint);

		ContentValues values = new ContentValues();

		values.put(MySQLiteHelper.COLUMN_TIMESTAMP, timestamp);
		values.put(MySQLiteHelper.COLUMN_EVENT, event);
		values.put(MySQLiteHelper.COLUMN_WAYPOINT, waypoint);

		database.insert(MySQLiteHelper.TABLE_LOGS, null, values);

	}

	public void deleteLog(Flightlog log) {
		long id = log.getId();
		Log.i(this.getClass().getName(), "Log deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_LOGS, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Flightlog> getLogsFromDate(Long timestamp) {
		List<Flightlog> logs = new ArrayList<Flightlog>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGS, allColumns, "DATE(" + timestamp + ", 'unixepoch', 'localtime') = DATE("
				+ MySQLiteHelper.COLUMN_TIMESTAMP + ", 'unixepoch', 'localtime')", null, null, null, MySQLiteHelper.COLUMN_TIMESTAMP);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Flightlog log = cursorToLog(cursor);
			logs.add(log);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return logs;
	}

	public List<Flightlog> getAllLogs() {
		List<Flightlog> logs = new ArrayList<Flightlog>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGS, allColumns, null, null, null, null, MySQLiteHelper.COLUMN_TIMESTAMP);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Flightlog log = cursorToLog(cursor);
			logs.add(log);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return logs;
	}

	private Flightlog cursorToLog(Cursor cursor) {
		Flightlog log = new Flightlog();
		log.setId(cursor.getLong(0));
		log.setTimestamp(cursor.getLong(1));
		log.setEvent(cursor.getString(2));
		log.setWaypoint(cursor.getString(3));
		return log;
	}

}
