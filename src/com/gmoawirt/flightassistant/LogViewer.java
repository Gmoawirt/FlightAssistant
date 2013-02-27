package com.gmoawirt.flightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class LogViewer extends ListActivity {
	private LogManager datasource;
	private String oldTime = "";
	private static LogAdapter adapter;
	private Fragment fragment = new ArrayListFragment();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logviewer);
		setTitle("Flightlogs");
		datasource = new LogManager(this);
		datasource.open();
		ArrayList<Flightlog> values = (ArrayList<Flightlog>) datasource.getAllLogs();
		datasource.close();

		final ActionBar actionBar = getActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				
				datasource.open();
				ArrayList<Flightlog> valuesForDate = (ArrayList<Flightlog>) datasource.getLogsFromDate((Long)tab.getTag());
				datasource.close();
				adapter = new LogAdapter(valuesForDate);				

				// Check if the fragment is already initialized
				if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
					// If not, instantiate and add it to the activity
					Bundle bundle = new Bundle();
					bundle.putString("log_day", tab.getText().toString());					
					fragment.setArguments(bundle);
					ft.add(android.R.id.content, fragment);					
					Log.i("LogViewer", "Adding Fragment");
				} else {
					// If it exists, simply attach it in order to show it
					((ArrayListFragment) getFragmentManager().findFragmentById(android.R.id.content)).update(tab.getText().toString());
					ft.attach(fragment);
					Log.i("LogViewer", "Attaching Fragment");
				}

			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}
		};

		for (Flightlog flightlog : values) {

			SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
			String time = timeFormat.format(new Date(flightlog.getTimestamp()));

			if (!oldTime.equals(time)) {
				// make new row
				Log.i("LogViewer", "Unique Date: " + time);
				actionBar.addTab(actionBar.newTab().setText(time).setTabListener(tabListener).setTag(flightlog.getId()));
			}

			oldTime = time;

		}

	}

	public static class ArrayListFragment extends ListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			Bundle bundle = getArguments();
			Log.i("LogViewer", "Bundle = " + bundle.getString("log_day"));
			setListAdapter(adapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.i("FragmentList", "Item clicked: " + id);
		}
		
		@Override
		public void onAttach(Activity activity){
		super.onAttach(activity);
		}
		
		public void update(String date) {
			
			Log.i("LogViewer", "Would update with data = " + date);
		}
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}