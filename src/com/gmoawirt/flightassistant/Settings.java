package com.gmoawirt.flightassistant;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
		setTitle("Settings");
	}
	
	private PreferenceFragment fragment = new PreferenceFragment() {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);
		}
	};
	
}
