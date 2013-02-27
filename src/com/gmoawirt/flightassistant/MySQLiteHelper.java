package com.gmoawirt.flightassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_LOGS = "logs";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	public static final String COLUMN_WAYPOINT = "waypoint";
	public static final String COLUMN_EVENT = "event";

	public static final String TABLE_WAYPOINTS = "waypoints";
	public static final String COLUMN_AIRFIELD = "airfield";
	public static final String COLUMN_ICAO = "icao";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_ALTITUDE = "altitude";

	private static final String DATABASE_NAME = "flight_assist.db";
	private static final int DATABASE_VERSION = 7;

	// Database creation sql statement
	private static final String DATABASE_CREATE_LOGS = "create table " + TABLE_LOGS + "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_TIMESTAMP
			+ " integer not null, " + COLUMN_WAYPOINT + " text not null, " + COLUMN_EVENT + " text not null);";
	
	private static final String DATABASE_CREATE_WAYPOINTS = "create table " + TABLE_WAYPOINTS + "("
			+ COLUMN_ID + " integer primary key autoincrement, " + COLUMN_ICAO + " text not null, " + COLUMN_AIRFIELD + " text not null, " + COLUMN_DESCRIPTION
			+ " text, " + COLUMN_LONGITUDE + " real not null, " + COLUMN_LATITUDE + " real not null, " + COLUMN_ALTITUDE + " real not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Called when database file is first created
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_LOGS);
		database.execSQL(DATABASE_CREATE_WAYPOINTS);
	}

	//Called when the version number increases
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYPOINTS);
		onCreate(db);
	}

}