package com.gmoawirt.flightassistant;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EntryManager {
	
	// Database fields
	protected SQLiteDatabase database;
	private MySQLiteHelper dbHelper;	
		
	public EntryManager(Context context) {
		dbHelper = new MySQLiteHelper(context);	
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();		
	}

	public void close() {
		dbHelper.close();
	}

}

