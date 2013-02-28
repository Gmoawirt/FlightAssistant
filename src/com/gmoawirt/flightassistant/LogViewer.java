package com.gmoawirt.flightassistant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

public class LogViewer extends ListActivity {
	private LogManager datasource;
	private String oldTime = "";
	private static LogAdapter adapter;
	private ArrayList<Flightlog> valuesForDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logviewer);
		setTitle("Flightlogs");

		// Get the database datasource
		datasource = new LogManager(this);
		datasource.open();
		// Get all Logs from Database
		ArrayList<Flightlog> values = (ArrayList<Flightlog>) datasource.getAllLogs();
		datasource.close();

		// Create the Action Bar
		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (Flightlog flightlog : values) {

			SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
			String time = timeFormat.format(new Date(flightlog.getTimestamp()));

			if (!oldTime.equals(time)) {
				// make new row
				Log.i("LogViewer", "Unique Date: " + time);
				actionBar.addTab(actionBar.newTab().setText(time).setTabListener(new MyTabListener<LogListFragment>(this, "artist",
		                LogListFragment.class)).setTag(flightlog.getTimestamp()));				
				
			}

			oldTime = time;

		}
		
		actionBar.setSelectedNavigationItem(actionBar.getNavigationItemCount()-1);

	}

	public static class MyTabListener<T extends Fragment> implements TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * * Constructor used each time a new tab is created. * * @param
		 * activity * The host Activity, used to instantiate the fragment * @param
		 * tag * The identifier tag for the fragment * @param clz * The
		 * fragment's Class, used to instantiate the fragment
		 */

		public MyTabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				Bundle bundle = new Bundle();
				bundle.putString("log_day", tab.getTag().toString());
				mFragment.setArguments(bundle);
				ft.add(android.R.id.content, mFragment, mTag);				
			} else {
				// If it exists, simply attach it in order to show it
				//ft.setCustomAnimations(android.R.animator.fade_in, R.animator.animationtest);
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				//ft.setCustomAnimations(android.R.animator.fade_in, R.animator.test);
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
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