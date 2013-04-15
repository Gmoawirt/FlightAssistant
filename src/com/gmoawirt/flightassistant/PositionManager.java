package com.gmoawirt.flightassistant;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class PositionManager {
	
	private static LocationManager locationManager;
	public static final String PROVIDER_GPS = "gps";
	public static final String PROVIDER_MOCK = "mock";
	
	public static void setPositionListener(LocationListener lm, Context context, boolean enable, boolean useMockGPS) {

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

}
