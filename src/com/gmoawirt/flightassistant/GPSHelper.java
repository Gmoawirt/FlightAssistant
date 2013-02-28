package com.gmoawirt.flightassistant;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class GpsHelper {
	
	public static Waypoint getNearestWaypoint(double longitudeP, double latitudeP, Context context) {

		// Loop through all waypoints, compare each Waypoint to current position

		WaypointManager datasource = new WaypointManager(context);
		datasource.open();
		String orderBy = MySQLiteHelper.COLUMN_ID;
		ArrayList<Waypoint> waypoints = (ArrayList<Waypoint>) datasource.getAllWaypoints(orderBy);
		datasource.close();

		for (Waypoint waypoint : waypoints) {

			float[] distance = new float[3];					
			Location.distanceBetween(latitudeP, longitudeP, waypoint.getLatitude(), waypoint.getLongitude(), distance);
			waypoint.setDistance(distance[0]);			
			
		}		

		ArrayList<Waypoint> sortedWaypoints = new ArrayList<Waypoint>();
		sortedWaypoints.addAll(waypoints);
		
		Log.i("GPSHelper", "Nearest Waypoint ICAO " + Collections.min(sortedWaypoints).getIcao() + ", Distance = " + Collections.min(sortedWaypoints).getDistance());
		
		return Collections.min(sortedWaypoints);

	}
	
	public static ArrayList<Waypoint> getSortedByDistance(double longitudeP, double latitudeP, Context context) {

		// Loop through all waypoints, compare each Waypoint to current position

		WaypointManager datasource = new WaypointManager(context);
		datasource.open();
		String orderBy = MySQLiteHelper.COLUMN_ID;
		ArrayList<Waypoint> waypoints = (ArrayList<Waypoint>) datasource.getAllWaypoints(orderBy);
		datasource.close();

		for (Waypoint waypoint : waypoints) {

			float[] distance = new float[3];					
			Location.distanceBetween(latitudeP, longitudeP, waypoint.getLatitude(), waypoint.getLongitude(), distance);
			waypoint.setDistance(distance[0]);			
			
		}				
		//Log.i("GPSHelper", "Nearest Waypoint ICAO " + Collections.min(sortedWaypoints).getIcao() + ", Distance = " + Collections.min(sortedWaypoints).getDistance());
		
		Collections.sort(waypoints);
		return waypoints;
	}	
}
