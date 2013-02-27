package com.gmoawirt.flightassistant;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class ProviderManager {
	
	private static LocationManager locationManager;

	public static void enableProvider(boolean mockStatus, LocationListener location, Activity activity) {

		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		if (mockStatus) {

			if (locationManager.getProvider("testProvider") == null) {
				locationManager.addTestProvider("testProvider", false, true, false, false, true, true, false, 0, 5);
			}
			locationManager.setTestProviderEnabled("testProvider", true);

			locationManager.requestLocationUpdates("testProvider", 1000, 0, location);

			// locationManager.setTestProviderStatus("testProvider",
			// LocationProvider.AVAILABLE, null, System.currentTimeMillis());

		} else {

			locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 0, location);

		}

	}
	
	public static LocationManager getLocationManager(){
		return locationManager;
	}

}
